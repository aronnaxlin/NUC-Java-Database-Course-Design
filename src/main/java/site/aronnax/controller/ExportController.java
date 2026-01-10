package site.aronnax.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.aronnax.dao.FeeDAO;
import site.aronnax.dao.PropertyDAO;
import site.aronnax.dao.UserDAO;
import site.aronnax.entity.Fee;
import site.aronnax.entity.User;
import site.aronnax.service.FeeService;
import site.aronnax.util.CSVExporter;

/**
 * CSV数据导出控制器
 * 提供用户、账单、欠费和房产数据的CSV格式导出功能。
 *
 * @author Aronnax (Li Linhan)
 */
@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final UserDAO userDAO;
    private final FeeDAO feeDAO;
    private final FeeService feeService;
    private final PropertyDAO propertyDAO;

    public ExportController(UserDAO userDAO, FeeDAO feeDAO, FeeService feeService, PropertyDAO propertyDAO) {
        this.userDAO = userDAO;
        this.feeDAO = feeDAO;
        this.feeService = feeService;
        this.propertyDAO = propertyDAO;
    }

    /**
     * 导出所有用户数据为CSV
     *
     * @return CSV文件下载响应
     */
    @GetMapping("/users")
    public ResponseEntity<byte[]> exportUsers() {
        try {
            // 获取所有用户数据
            List<User> users = userDAO.findAll();

            // 生成临时CSV文件
            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "users_export_" + System.currentTimeMillis() + ".csv";
            String filePath = tempDir + File.separator + fileName;

            // 导出到CSV
            boolean success = CSVExporter.exportOwners(users, filePath);
            if (!success) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("导出失败".getBytes());
            }

            // 读取文件内容
            File file = new File(filePath);
            byte[] fileContent = Files.readAllBytes(file.toPath());

            // 删除临时文件
            file.delete();

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "users_export.csv");
            headers.setCacheControl("no-cache");

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("导出失败：" + e.getMessage()).getBytes());
        }
    }

    /**
     * 导出所有账单数据为CSV
     *
     * @return CSV文件下载响应
     */
    @GetMapping("/fees")
    public ResponseEntity<byte[]> exportFees() {
        try {
            // 获取所有账单数据
            List<Fee> fees = feeDAO.findAll();

            // 生成临时CSV文件
            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "fees_export_" + System.currentTimeMillis() + ".csv";
            String filePath = tempDir + File.separator + fileName;

            // 导出到CSV
            boolean success = CSVExporter.exportFees(fees, filePath);
            if (!success) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("导出失败".getBytes());
            }

            // 读取文件内容
            File file = new File(filePath);
            byte[] fileContent = Files.readAllBytes(file.toPath());

            // 删除临时文件
            file.delete();

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "fees_export.csv");
            headers.setCacheControl("no-cache");

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("导出失败：" + e.getMessage()).getBytes());
        }
    }

    /**
     * 导出欠费名单为CSV
     *
     * @return CSV文件下载响应
     */
    @GetMapping("/arrears")
    public ResponseEntity<byte[]> exportArrears() {
        try {
            // 获取欠费名单数据
            List<Map<String, Object>> arrearsList = feeService.getArrearsList();

            // 生成临时CSV文件
            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "arrears_export_" + System.currentTimeMillis() + ".csv";
            String filePath = tempDir + File.separator + fileName;

            // 导出到CSV
            boolean success = CSVExporter.exportArrears(arrearsList, filePath);
            if (!success) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("导出失败".getBytes());
            }

            // 读取文件内容
            File file = new File(filePath);
            byte[] fileContent = Files.readAllBytes(file.toPath());

            // 删除临时文件
            file.delete();

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "arrears_export.csv");
            headers.setCacheControl("no-cache");

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("导出失败：" + e.getMessage()).getBytes());
        }
    }

    /**
     * 导出所有房产数据为CSV
     *
     * @return CSV文件下载响应
     */
    @GetMapping("/properties")
    public ResponseEntity<byte[]> exportProperties() {
        try {
            List<site.aronnax.entity.Property> properties = propertyDAO.findAll();
            List<Map<String, Object>> enrichedProperties = new java.util.ArrayList<>();
            for (site.aronnax.entity.Property property : properties) {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("pId", property.getpId());
                map.put("buildingNo", property.getBuildingNo());
                map.put("unitNo", property.getUnitNo());
                map.put("roomNo", property.getRoomNo());
                map.put("area", property.getArea());
                map.put("pStatus", property.getpStatus());
                map.put("userId", property.getUserId());
                if (property.getUserId() != null) {
                    User owner = userDAO.findById(property.getUserId());
                    map.put("ownerName", owner != null ? owner.getName() : "未知业主");
                } else {
                    map.put("ownerName", "-");
                }
                enrichedProperties.add(map);
            }

            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "properties_export_" + System.currentTimeMillis() + ".csv";
            String filePath = tempDir + File.separator + fileName;

            boolean success = CSVExporter.exportProperties(enrichedProperties, filePath);
            if (!success) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导出失败".getBytes());
            }

            File file = new File(filePath);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            file.delete();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "properties_export.csv");
            headers.setCacheControl("no-cache");

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("导出失败：" + e.getMessage()).getBytes());
        }
    }
}
