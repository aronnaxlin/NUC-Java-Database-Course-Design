package site.aronnax;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import site.aronnax.util.DBUtil;

/**
 * 命令行测试程序
 * 用于测试数据库连接并执行简单的 SQL 查询
 *
 * @author Aronnax (Li Linhan)
 * @version 1.0
 */
public class CLITest {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  智慧物业管理系统 - 数据库连接测试");
        System.out.println("  Smart Property Management System");
        System.out.println("========================================");
        System.out.println();

        // 1. 测试数据库连接
        System.out.println("[Step 1] 测试数据库连接...");
        if (!DBUtil.testConnection()) {
            System.err.println("❌ 数据库连接失败，请检查配置文件和 MySQL 服务状态");
            return;
        }
        System.out.println("✅ 数据库连接成功！\n");

        // 2. 显示菜单并进入交互模式
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("请选择操作 (输入序号): ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1":
                    queryAllUsers();
                    break;
                case "2":
                    queryAllProperties();
                    break;
                case "3":
                    queryAllFees();
                    break;
                case "4":
                    executeCustomSQL();
                    break;
                case "0":
                    running = false;
                    System.out.println("👋 感谢使用，再见！");
                    break;
                default:
                    System.out.println("⚠️  无效选项，请重新输入\n");
            }
        }

        scanner.close();
    }

    /**
     * 打印主菜单
     */
    private static void printMenu() {
        System.out.println("========================================");
        System.out.println("  主菜单");
        System.out.println("========================================");
        System.out.println("1. 查询所有用户");
        System.out.println("2. 查询所有房产");
        System.out.println("3. 查询所有账单");
        System.out.println("4. 执行自定义 SQL");
        System.out.println("0. 退出程序");
        System.out.println("========================================");
    }

    /**
     * 查询所有用户信息
     */
    private static void queryAllUsers() {
        String sql = "SELECT user_id, user_name, user_type, name, phone FROM users";
        System.out.println("📋 查询所有用户信息...\n");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            System.out.println("用户ID | 登录账号       | 用户类型 | 真实姓名   | 联系电话");
            System.out.println("------------------------------------------------------------");

            int count = 0;
            while (rs.next()) {
                long userId = rs.getLong("user_id");
                String userName = rs.getString("user_name");
                String userType = rs.getString("user_type");
                String name = rs.getString("name");
                String phone = rs.getString("phone");

                System.out.printf("%-6d | %-14s | %-8s | %-10s | %s%n",
                        userId, userName, userType,
                        name != null ? name : "N/A",
                        phone != null ? phone : "N/A");
                count++;
            }

            System.out.println("------------------------------------------------------------");
            System.out.println("✅ 查询完成，共 " + count + " 条记录\n");

        } catch (SQLException e) {
            System.err.println("❌ 查询失败: " + e.getMessage() + "\n");
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * 查询所有房产信息
     */
    private static void queryAllProperties() {
        String sql = "SELECT p_id, building_no, unit_no, room_no, area, p_status FROM properties";
        System.out.println("🏢 查询所有房产信息...\n");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            System.out.println("房产ID | 楼栋 | 单元 | 房号 | 面积(㎡) | 状态");
            System.out.println("--------------------------------------------------");

            int count = 0;
            while (rs.next()) {
                long pId = rs.getLong("p_id");
                String building = rs.getString("building_no");
                String unit = rs.getString("unit_no");
                String room = rs.getString("room_no");
                double area = rs.getDouble("area");
                String status = rs.getString("p_status");

                System.out.printf("%-6d | %-4s | %-4s | %-4s | %-8.2f | %s%n",
                        pId, building, unit, room, area, status);
                count++;
            }

            System.out.println("--------------------------------------------------");
            System.out.println("✅ 查询完成，共 " + count + " 条记录\n");

        } catch (SQLException e) {
            System.err.println("❌ 查询失败: " + e.getMessage() + "\n");
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * 查询所有账单信息
     */
    private static void queryAllFees() {
        String sql = "SELECT f_id, p_id, fee_type, amount, is_paid FROM fees";
        System.out.println("💰 查询所有账单信息...\n");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            System.out.println("账单ID | 房产ID | 费用类型       | 金额     | 状态");
            System.out.println("------------------------------------------------------");

            int count = 0;
            while (rs.next()) {
                long fId = rs.getLong("f_id");
                long pId = rs.getLong("p_id");
                String feeType = rs.getString("fee_type");
                double amount = rs.getDouble("amount");
                int isPaid = rs.getInt("is_paid");

                System.out.printf("%-6d | %-6d | %-14s | %-8.2f | %s%n",
                        fId, pId, feeType, amount, isPaid == 1 ? "已缴" : "未缴");
                count++;
            }

            System.out.println("------------------------------------------------------");
            System.out.println("✅ 查询完成，共 " + count + " 条记录\n");

        } catch (SQLException e) {
            System.err.println("❌ 查询失败: " + e.getMessage() + "\n");
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * 执行自定义 SQL 语句
     */
    private static void executeCustomSQL() {
        System.out.println("🔧 执行自定义 SQL");
        System.out.println("提示: 请输入 SELECT 查询语句");
        System.out.print("SQL> ");

        String sql = scanner.nextLine().trim();

        if (sql.isEmpty()) {
            System.out.println("⚠️  SQL 语句不能为空\n");
            return;
        }

        // 简单校验：仅允许 SELECT 语句
        if (!sql.toUpperCase().startsWith("SELECT")) {
            System.out.println("⚠️  当前仅支持 SELECT 查询语句\n");
            return;
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            // 获取列信息
            int columnCount = rs.getMetaData().getColumnCount();

            // 打印表头
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rs.getMetaData().getColumnName(i));
                if (i < columnCount)
                    System.out.print(" | ");
            }
            System.out.println();
            System.out.println("-".repeat(60));

            // 打印数据
            int count = 0;
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i));
                    if (i < columnCount)
                        System.out.print(" | ");
                }
                System.out.println();
                count++;
            }

            System.out.println("-".repeat(60));
            System.out.println("✅ 查询完成，共 " + count + " 条记录\n");

        } catch (SQLException e) {
            System.err.println("❌ SQL 执行失败: " + e.getMessage() + "\n");
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * 关闭数据库资源
     */
    private static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.err.println("关闭资源时发生错误: " + e.getMessage());
        }
    }
}
