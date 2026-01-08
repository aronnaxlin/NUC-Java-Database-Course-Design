package site.aronnax;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import site.aronnax.dao.UserDAO;
import site.aronnax.entity.Fee;
import site.aronnax.entity.User;
import site.aronnax.service.FeeService;
import site.aronnax.service.OwnerService;
import site.aronnax.service.UtilityCardService;
import site.aronnax.service.impl.FeeServiceImpl;
import site.aronnax.service.impl.OwnerServiceImpl;
import site.aronnax.service.impl.UtilityCardServiceImpl;
import site.aronnax.util.CSVExporter;

/**
 * Service Layer Test Program
 * Tests business logic including:
 * - Multi-dimensional search
 * - Batch fee creation
 * - Arrears checking and management
 * - Utility card top-up with arrears interception
 * - CSV export functionality
 *
 * @author Aronnax (Li Linhan)
 * @version 1.0
 */
public class ServiceTest {

    private static final OwnerService ownerService = new OwnerServiceImpl();
    private static final FeeService feeService = new FeeServiceImpl();
    private static final UtilityCardService cardService = new UtilityCardServiceImpl();
    private static final UserDAO userDAO = new UserDAO();

    public static void main(String[] args) {
        printHeader();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("请选择操作 (输入序号): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    testMultiDimensionalSearch(scanner);
                    break;
                case "2":
                    testBatchFeeCreation(scanner);
                    break;
                case "3":
                    testArrearsList();
                    break;
                case "4":
                    testArrearsInterception(scanner);
                    break;
                case "5":
                    testPayFee(scanner);
                    break;
                case "6":
                    testCSVExport();
                    break;
                case "7":
                    testUpdatePropertyOwner(scanner);
                    break;
                case "0":
                    running = false;
                    System.out.println("\n👋 感谢使用，再见！\n");
                    break;
                default:
                    System.out.println("❌ 无效选项，请重新输入\n");
            }
        }

        scanner.close();
    }

    /**
     * Test 1: Multi-dimensional search
     */
    private static void testMultiDimensionalSearch(Scanner scanner) {
        System.out.println("\n🔍 多维度搜索测试");
        System.out.println("========================================");
        System.out.print("请输入搜索关键词 (姓名/电话): ");
        String keyword = scanner.nextLine().trim();

        List<Map<String, Object>> results = ownerService.searchOwners(keyword);

        if (results.isEmpty()) {
            System.out.println("❌ 未找到匹配的业主信息");
        } else {
            System.out.println("\n✅ 找到 " + results.size() + " 条记录：\n");
            System.out.printf("%-10s %-15s %-15s %-10s %-10s %-10s%n",
                    "业主姓名", "联系电话", "房产ID", "楼栋", "单元", "房号");
            System.out.println("------------------------------------------------------------");

            for (Map<String, Object> info : results) {
                System.out.printf("%-10s %-15s %-10s %-10s %-10s %-10s%n",
                        info.get("name"),
                        info.get("phone"),
                        info.get("property_id"),
                        info.get("building_no"),
                        info.get("unit_no"),
                        info.get("room_no"));
            }
        }
        System.out.println();
    }

    /**
     * Test 2: Batch fee creation
     */
    private static void testBatchFeeCreation(Scanner scanner) {
        System.out.println("\n💰 批量计费测试");
        System.out.println("========================================");
        System.out.print("请输入房产ID列表 (用逗号分隔，如: 1,2,3): ");
        String idsInput = scanner.nextLine().trim();

        System.out.print("请输入费用类型 (如: PROPERTY_FEE, HEATING_FEE): ");
        String feeType = scanner.nextLine().trim();

        System.out.print("请输入金额: ");
        Double amount = Double.parseDouble(scanner.nextLine().trim());

        // Parse property IDs
        List<Long> propertyIds = Arrays.stream(idsInput.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();

        int count = feeService.batchCreateFees(propertyIds, feeType, amount);

        System.out.println("\n✅ 批量计费完成，成功创建 " + count + " 条账单");
        System.out.println();
    }

    /**
     * Test 3: Arrears list
     */
    private static void testArrearsList() {
        System.out.println("\n📋 欠费名单查询");
        System.out.println("========================================");

        List<Map<String, Object>> arrearsList = feeService.getArrearsList();

        if (arrearsList.isEmpty()) {
            System.out.println("✅ 暂无欠费记录");
        } else {
            System.out.println("\n⚠️  欠费总数: " + arrearsList.size() + " 条\n");
            System.out.printf("%-10s %-10s %-10s %-15s %-15s %-20s %-10s%n",
                    "账单ID", "房产ID", "房号", "业主姓名", "联系电话", "费用类型", "金额");
            System.out.println("--------------------------------------------------------------------------------");

            for (Map<String, Object> arrears : arrearsList) {
                String roomNo = arrears.get("building_no") + "-" +
                        arrears.get("unit_no") + "-" +
                        arrears.get("room_no");

                System.out.printf("%-10s %-10s %-10s %-15s %-15s %-20s %-10.2f%n",
                        arrears.get("fee_id"),
                        arrears.get("property_id"),
                        roomNo,
                        arrears.get("owner_name"),
                        arrears.get("owner_phone"),
                        arrears.get("fee_type"),
                        arrears.get("amount"));
            }
        }
        System.out.println();
    }

    /**
     * Test 4: Arrears interception (critical business logic)
     */
    private static void testArrearsInterception(Scanner scanner) {
        System.out.println("\n🚫 欠费硬拦截测试 (水电卡充值)");
        System.out.println("========================================");
        System.out.print("请输入水电卡ID: ");
        Long cardId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("请输入充值金额: ");
        Double amount = Double.parseDouble(scanner.nextLine().trim());

        try {
            boolean success = cardService.topUp(cardId, amount);
            if (success) {
                System.out.println("✅ 充值成功！");
            } else {
                System.out.println("❌ 充值失败");
            }
        } catch (IllegalStateException e) {
            System.out.println("🚫 欠费拦截生效: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Test 5: Pay fee
     */
    private static void testPayFee(Scanner scanner) {
        System.out.println("\n💳 缴费测试");
        System.out.println("========================================");
        System.out.print("请输入要缴费的账单ID: ");
        Long feeId = Long.parseLong(scanner.nextLine().trim());

        boolean success = feeService.payFee(feeId);
        if (success) {
            System.out.println("✅ 缴费成功");
        } else {
            System.out.println("❌ 缴费失败，账单不存在");
        }
        System.out.println();
    }

    /**
     * Test 6: CSV export
     */
    private static void testCSVExport() {
        System.out.println("\n📊 CSV导出测试");
        System.out.println("========================================");

        // Export owners
        List<User> owners = userDAO.findAll();
        CSVExporter.exportOwners(owners, "owners_export.csv");

        // Export unpaid fees
        List<Fee> unpaidFees = feeService.getUnpaidFees();
        CSVExporter.exportFees(unpaidFees, "unpaid_fees_export.csv");

        // Export arrears list
        List<Map<String, Object>> arrearsList = feeService.getArrearsList();
        CSVExporter.exportArrears(arrearsList, "arrears_list_export.csv");

        System.out.println("\n✅ 所有导出完成！");
        System.out.println();
    }

    /**
     * Test 7: Update property owner
     */
    private static void testUpdatePropertyOwner(Scanner scanner) {
        System.out.println("\n🏠 产权变更测试");
        System.out.println("========================================");
        System.out.print("请输入房产ID: ");
        Long propertyId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("请输入新业主ID: ");
        Long newOwnerId = Long.parseLong(scanner.nextLine().trim());

        boolean success = ownerService.updatePropertyOwner(propertyId, newOwnerId);
        if (success) {
            System.out.println("✅ 产权变更成功");
        } else {
            System.out.println("❌ 产权变更失败");
        }
        System.out.println();
    }

    private static void printHeader() {
        System.out.println("========================================");
        System.out.println("  智慧物业管理系统 - 业务逻辑测试");
        System.out.println("  Service Layer Test Program");
        System.out.println("========================================\n");
    }

    private static void printMenu() {
        System.out.println("========================================");
        System.out.println("  主菜单");
        System.out.println("========================================");
        System.out.println("1. 多维度搜索业主");
        System.out.println("2. 批量创建账单");
        System.out.println("3. 查询欠费名单");
        System.out.println("4. 测试欠费硬拦截 (水电卡充值)");
        System.out.println("5. 缴纳费用");
        System.out.println("6. CSV数据导出");
        System.out.println("7. 产权变更");
        System.out.println("0. 退出程序");
        System.out.println("========================================");
    }
}
