package site.aronnax.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import site.aronnax.common.Result;
import site.aronnax.service.FeeService;
import site.aronnax.service.UtilityCardService;

/**
 * 费用管理控制器
 * 面向物业管理端，提供账单的精准派发、批量操作及欠费全局监控。
 *
 * @author Aronnax (Li Linhan)
 */
@RestController
@RequestMapping("/api/fee")
public class FeeController {

    private static final Logger logger = LoggerFactory.getLogger(FeeController.class);

    private final FeeService feeService;
    private final UtilityCardService utilityCardService;

    public FeeController(FeeService feeService, UtilityCardService utilityCardService) {
        this.feeService = feeService;
        this.utilityCardService = utilityCardService;
    }

    /**
     * 下发单笔费用单据
     * 适用于针对特定房产的临时收费（如特殊维护费）。
     *
     * @param propertyId 房产主键 ID
     * @param feeType    待生成的费用类型
     * @param amount     账单面额
     */
    @PostMapping("/create")
    public Result<String> createFee(@RequestParam("propertyId") Long propertyId,
            @RequestParam("feeType") String feeType,
            @RequestParam("amount") Double amount) {
        // 参数前置校验：确保数据有效性
        if (propertyId == null || propertyId <= 0) {
            return Result.error("无效的房产标识");
        }
        if (feeType == null || feeType.trim().isEmpty()) {
            return Result.error("费用类型未指定");
        }
        if (amount == null || amount <= 0) {
            return Result.error("计费金额必须大于零");
        }

        // 风控拦截：防止逻辑溢出或非法输入
        if (amount > 100000) {
            return Result.error("单张账单金额已超过系统上限（10万元）");
        }

        try {
            // 路由至业务层，执行入库逻辑
            feeService.createFee(propertyId, feeType, amount);
            return Result.success("费用下发成功");
        } catch (Exception e) {
            return Result.error("下发失败，请检查数据一致性：" + e.getMessage());
        }
    }

    /**
     * 批量创建功能
     * 典型场景：物业费年度统扣、取暖季统扣。
     *
     * @param params 映射列表，需包含 propertyIds, feeType 和 amount
     */
    @PostMapping("/batch-create")
    public Result<String> batchCreate(@RequestBody Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return Result.error("报文负载缺失");
        }

        // 提取待处理的房产 ID 集合
        @SuppressWarnings("unchecked")
        List<Long> propertyIds = (List<Long>) params.get("propertyIds");
        if (propertyIds == null || propertyIds.isEmpty()) {
            return Result.error("目标房产范围不能为空");
        }

        String feeType = (String) params.get("feeType");
        if (feeType == null || feeType.trim().isEmpty()) {
            return Result.error("费用分类未定义");
        }

        // 金额类型适应性解析（兼容 JSON 数值的不同表现形式）
        Double amount;
        try {
            Object amountObj = params.get("amount");
            if (amountObj instanceof Integer) {
                amount = ((Integer) amountObj).doubleValue();
            } else if (amountObj instanceof Double) {
                amount = (Double) amountObj;
            } else {
                return Result.error("计费额度格式解析异常");
            }
        } catch (Exception e) {
            return Result.error("金额数据类型错误");
        }

        if (amount <= 0) {
            return Result.error("计费值必须为正数");
        }

        try {
            // 调用业务层执行批量插入事务
            int count = feeService.batchCreateFees(propertyIds, feeType, amount);
            return Result.success("批量计费完成，已生成 " + count + " 个房产的应缴账单");
        } catch (Exception e) {
            return Result.error("批量执行失败，事务已回滚：" + e.getMessage());
        }
    }

    /**
     * 欠费清册接口（支持角色过滤）
     * - 管理员：查看所有欠费
     * - 业主：只查看自己名下房产的欠费
     */
    @GetMapping("/arrears")
    public Result<List<Map<String, Object>>> getArrearsList(HttpSession session) {
        try {
            String userType = (String) session.getAttribute("userType");
            Long userId = (Long) session.getAttribute("userId");

            // 验证session
            if (userType == null) {
                logger.warn("欠费列表访问被拒绝：session已失效");
                return Result.error("未登录或会话已过期");
            }

            List<Map<String, Object>> arrearsList;
            if ("ADMIN".equals(userType)) {
                arrearsList = feeService.getArrearsList();
            } else {
                if (userId == null) {
                    logger.warn("欠费列表访问被拒绝：userId为空");
                    return Result.error("用户ID无效");
                }
                arrearsList = feeService.getArrearsListByUserId(userId);
            }

            return Result.success(arrearsList != null ? arrearsList : List.of());
        } catch (Exception e) {
            logger.error("获取欠费列表失败", e);
            return Result.error("获取统计视图失败：" + e.getMessage());
        }
    }

    /**
     * 从水电卡扣费（仅业主可用）
     * 用于业主支付水费/电费账单
     */
    @PostMapping("/pay-from-card")
    public Result<String> payFeeFromCard(@RequestParam("feeId") Long feeId, HttpSession session) {
        String userType = (String) session.getAttribute("userType");

        // 只允许业主使用此接口
        if (!"OWNER".equals(userType)) {
            return Result.error("管理员不能使用水电卡代缴费用");
        }

        try {
            boolean success = utilityCardService.payFeeFromCard(feeId);
            return success ? Result.success("水电费缴纳成功") : Result.error("缴费失败");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("系统错误：" + e.getMessage());
        }
    }

}
