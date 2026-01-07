package site.aronnax;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import site.aronnax.dao.FeeDAO;
import site.aronnax.dao.PropertyDAO;
import site.aronnax.dao.UserDAO;
import site.aronnax.dao.UtilityCardDAO;
import site.aronnax.entity.Fee;
import site.aronnax.entity.Property;
import site.aronnax.entity.User;
import site.aronnax.entity.UtilityCard;

/**
 * CRUD 测试程序
 * 命令行交互式界面，用于测试 Entity 和 DAO 层的增删改查功能
 *
 * @author Aronnax (Li Linhan)
 * @version 1.0
 */
public class CRUDTest {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDAO userDAO = new UserDAO();
    private static final PropertyDAO propertyDAO = new PropertyDAO();
    private static final FeeDAO feeDAO = new FeeDAO();
    private static final UtilityCardDAO cardDAO = new UtilityCardDAO();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  智慧物业管理系统 - CRUD 测试");
        System.out.println("  Entity & DAO Layer Test");
        System.out.println("========================================\n");

        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("请选择操作 (输入序号): ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1":
                    userMenu();
                    break;
                case "2":
                    propertyMenu();
                    break;
                case "3":
                    feeMenu();
                    break;
                case "4":
                    utilityCardMenu();
                    break;
                case "0":
                    running = false;
                    System.out.println("👋 测试结束，再见！");
                    break;
                default:
                    System.out.println("⚠️  无效选项，请重新输入\n");
            }
        }

        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("========================================");
        System.out.println("  主菜单 - 选择要测试的模块");
        System.out.println("========================================");
        System.out.println("1. 用户管理 (User CRUD)");
        System.out.println("2. 房产管理 (Property CRUD)");
        System.out.println("3. 账单管理 (Fee CRUD)");
        System.out.println("4. 水电卡管理 (UtilityCard CRUD)");
        System.out.println("0. 退出程序");
        System.out.println("========================================");
    }

    // ==================== User CRUD ====================

    private static void userMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n【用户管理】");
            System.out.println("1. 查询所有用户  2. 根据ID查询  3. 新增用户");
            System.out.println("4. 更新用户      5. 删除用户    0. 返回主菜单");
            System.out.print("选择: ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1":
                    queryAllUsers();
                    break;
                case "2":
                    queryUserById();
                    break;
                case "3":
                    insertUser();
                    break;
                case "4":
                    updateUser();
                    break;
                case "5":
                    deleteUser();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("无效选项\n");
            }
        }
    }

    private static void queryAllUsers() {
        System.out.println("📋 查询所有用户...");
        List<User> users = userDAO.findAll();
        if (users.isEmpty()) {
            System.out.println("暂无用户数据\n");
        } else {
            users.forEach(System.out::println);
            System.out.println("共 " + users.size() + " 条记录\n");
        }
    }

    private static void queryUserById() {
        System.out.print("请输入用户ID: ");
        Long userId = Long.parseLong(scanner.nextLine().trim());

        User user = userDAO.findById(userId);
        if (user != null) {
            System.out.println("✅ 查询成功: " + user + "\n");
        } else {
            System.out.println("❌ 未找到该用户\n");
        }
    }

    private static void insertUser() {
        System.out.println("【新增用户】");
        System.out.print("用户名: ");
        String userName = scanner.nextLine().trim();
        System.out.print("密码: ");
        String password = scanner.nextLine().trim();
        System.out.print("用户类型 (ADMIN/OWNER): ");
        String userType = scanner.nextLine().trim();
        System.out.print("真实姓名: ");
        String name = scanner.nextLine().trim();
        System.out.print("性别 (Male/Female): ");
        String gender = scanner.nextLine().trim();
        System.out.print("电话: ");
        String phone = scanner.nextLine().trim();

        User user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        user.setUserType(userType);
        user.setName(name);
        user.setGender(gender);
        user.setPhone(phone);

        Long id = userDAO.insert(user);
        if (id != null) {
            System.out.println("✅ 插入成功，生成ID: " + id + "\n");
        } else {
            System.out.println("❌ 插入失败\n");
        }
    }

    private static void updateUser() {
        System.out.print("请输入要更新的用户ID: ");
        Long userId = Long.parseLong(scanner.nextLine().trim());

        User user = userDAO.findById(userId);
        if (user == null) {
            System.out.println("❌ 用户不存在\n");
            return;
        }

        System.out.println("当前用户: " + user);
        System.out.print("新用户名 (回车跳过): ");
        String userName = scanner.nextLine().trim();
        if (!userName.isEmpty())
            user.setUserName(userName);

        System.out.print("新密码 (回车跳过): ");
        String password = scanner.nextLine().trim();
        if (!password.isEmpty())
            user.setPassword(password);

        boolean success = userDAO.update(user);
        System.out.println(success ? "✅ 更新成功\n" : "❌ 更新失败\n");
    }

    private static void deleteUser() {
        System.out.print("请输入要删除的用户ID: ");
        Long userId = Long.parseLong(scanner.nextLine().trim());

        boolean success = userDAO.deleteById(userId);
        System.out.println(success ? "✅ 删除成功\n" : "❌ 删除失败\n");
    }

    // ==================== Property CRUD ====================

    private static void propertyMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n【房产管理】");
            System.out.println("1. 查询所有房产  2. 根据ID查询  3. 新增房产");
            System.out.println("4. 更新房产      5. 删除房产    0. 返回主菜单");
            System.out.print("选择: ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1":
                    queryAllProperties();
                    break;
                case "2":
                    queryPropertyById();
                    break;
                case "3":
                    insertProperty();
                    break;
                case "4":
                    updateProperty();
                    break;
                case "5":
                    deleteProperty();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("无效选项\n");
            }
        }
    }

    private static void queryAllProperties() {
        System.out.println("🏢 查询所有房产...");
        List<Property> properties = propertyDAO.findAll();
        if (properties.isEmpty()) {
            System.out.println("暂无房产数据\n");
        } else {
            properties.forEach(System.out::println);
            System.out.println("共 " + properties.size() + " 条记录\n");
        }
    }

    private static void queryPropertyById() {
        System.out.print("请输入房产ID: ");
        Long pId = Long.parseLong(scanner.nextLine().trim());

        Property property = propertyDAO.findById(pId);
        if (property != null) {
            System.out.println("✅ 查询成功: " + property + "\n");
        } else {
            System.out.println("❌ 未找到该房产\n");
        }
    }

    private static void insertProperty() {
        System.out.println("【新增房产】");
        System.out.print("楼栋号: ");
        String building = scanner.nextLine().trim();
        System.out.print("单元号: ");
        String unit = scanner.nextLine().trim();
        System.out.print("房间号: ");
        String room = scanner.nextLine().trim();
        System.out.print("面积: ");
        Double area = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("状态 (SOLD/UNSOLD/RENTED): ");
        String status = scanner.nextLine().trim();
        System.out.print("业主ID (可选，回车跳过): ");
        String userIdStr = scanner.nextLine().trim();

        Property property = new Property();
        property.setBuildingNo(building);
        property.setUnitNo(unit);
        property.setRoomNo(room);
        property.setArea(area);
        property.setpStatus(status);
        if (!userIdStr.isEmpty()) {
            property.setUserId(Long.parseLong(userIdStr));
        }

        Long id = propertyDAO.insert(property);
        if (id != null) {
            System.out.println("✅ 插入成功，生成ID: " + id + "\n");
        } else {
            System.out.println("❌ 插入失败\n");
        }
    }

    private static void updateProperty() {
        System.out.print("请输入要更新的房产ID: ");
        Long pId = Long.parseLong(scanner.nextLine().trim());

        Property property = propertyDAO.findById(pId);
        if (property == null) {
            System.out.println("❌ 房产不存在\n");
            return;
        }

        System.out.println("当前房产: " + property);
        System.out.print("新状态 (SOLD/UNSOLD/RENTED，回车跳过): ");
        String status = scanner.nextLine().trim();
        if (!status.isEmpty())
            property.setpStatus(status);

        boolean success = propertyDAO.update(property);
        System.out.println(success ? "✅ 更新成功\n" : "❌ 更新失败\n");
    }

    private static void deleteProperty() {
        System.out.print("请输入要删除的房产ID: ");
        Long pId = Long.parseLong(scanner.nextLine().trim());

        boolean success = propertyDAO.deleteById(pId);
        System.out.println(success ? "✅ 删除成功\n" : "❌ 删除失败\n");
    }

    // ==================== Fee CRUD ====================

    private static void feeMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n【账单管理】");
            System.out.println("1. 查询所有账单  2. 根据ID查询  3. 新增账单");
            System.out.println("4. 更新账单      5. 删除账单    0. 返回主菜单");
            System.out.print("选择: ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1":
                    queryAllFees();
                    break;
                case "2":
                    queryFeeById();
                    break;
                case "3":
                    insertFee();
                    break;
                case "4":
                    updateFee();
                    break;
                case "5":
                    deleteFee();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("无效选项\n");
            }
        }
    }

    private static void queryAllFees() {
        System.out.println("💰 查询所有账单...");
        List<Fee> fees = feeDAO.findAll();
        if (fees.isEmpty()) {
            System.out.println("暂无账单数据\n");
        } else {
            fees.forEach(System.out::println);
            System.out.println("共 " + fees.size() + " 条记录\n");
        }
    }

    private static void queryFeeById() {
        System.out.print("请输入账单ID: ");
        Long fId = Long.parseLong(scanner.nextLine().trim());

        Fee fee = feeDAO.findById(fId);
        if (fee != null) {
            System.out.println("✅ 查询成功: " + fee + "\n");
        } else {
            System.out.println("❌ 未找到该账单\n");
        }
    }

    private static void insertFee() {
        System.out.println("【新增账单】");
        System.out.print("房产ID: ");
        Long pId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("费用类型 (PROPERTY_FEE/HEATING_FEE): ");
        String feeType = scanner.nextLine().trim();
        System.out.print("金额: ");
        Double amount = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("是否已缴 (0-未缴/1-已缴): ");
        Integer isPaid = Integer.parseInt(scanner.nextLine().trim());

        Fee fee = new Fee();
        fee.setpId(pId);
        fee.setFeeType(feeType);
        fee.setAmount(amount);
        fee.setIsPaid(isPaid);
        if (isPaid == 1) {
            fee.setPayDate(LocalDateTime.now());
        }

        Long id = feeDAO.insert(fee);
        if (id != null) {
            System.out.println("✅ 插入成功，生成ID: " + id + "\n");
        } else {
            System.out.println("❌ 插入失败\n");
        }
    }

    private static void updateFee() {
        System.out.print("请输入要更新的账单ID: ");
        Long fId = Long.parseLong(scanner.nextLine().trim());

        Fee fee = feeDAO.findById(fId);
        if (fee == null) {
            System.out.println("❌ 账单不存在\n");
            return;
        }

        System.out.println("当前账单: " + fee);
        System.out.print("标记为已缴? (y/n): ");
        String pay = scanner.nextLine().trim();
        if (pay.equalsIgnoreCase("y")) {
            fee.setIsPaid(1);
            fee.setPayDate(LocalDateTime.now());
        }

        boolean success = feeDAO.update(fee);
        System.out.println(success ? "✅ 更新成功\n" : "❌ 更新失败\n");
    }

    private static void deleteFee() {
        System.out.print("请输入要删除的账单ID: ");
        Long fId = Long.parseLong(scanner.nextLine().trim());

        boolean success = feeDAO.deleteById(fId);
        System.out.println(success ? "✅ 删除成功\n" : "❌ 删除失败\n");
    }

    // ==================== UtilityCard CRUD ====================

    private static void utilityCardMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n【水电卡管理】");
            System.out.println("1. 查询所有水电卡  2. 根据ID查询  3. 新增水电卡");
            System.out.println("4. 更新水电卡      5. 删除水电卡  0. 返回主菜单");
            System.out.print("选择: ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1":
                    queryAllCards();
                    break;
                case "2":
                    queryCardById();
                    break;
                case "3":
                    insertCard();
                    break;
                case "4":
                    updateCard();
                    break;
                case "5":
                    deleteCard();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("无效选项\n");
            }
        }
    }

    private static void queryAllCards() {
        System.out.println("💳 查询所有水电卡...");
        List<UtilityCard> cards = cardDAO.findAll();
        if (cards.isEmpty()) {
            System.out.println("暂无水电卡数据\n");
        } else {
            cards.forEach(System.out::println);
            System.out.println("共 " + cards.size() + " 条记录\n");
        }
    }

    private static void queryCardById() {
        System.out.print("请输入水电卡ID: ");
        Long cardId = Long.parseLong(scanner.nextLine().trim());

        UtilityCard card = cardDAO.findById(cardId);
        if (card != null) {
            System.out.println("✅ 查询成功: " + card + "\n");
        } else {
            System.out.println("❌ 未找到该水电卡\n");
        }
    }

    private static void insertCard() {
        System.out.println("【新增水电卡】");
        System.out.print("房产ID: ");
        Long pId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("卡类型 (WATER/ELECTRICITY): ");
        String cardType = scanner.nextLine().trim();
        System.out.print("余额: ");
        Double balance = Double.parseDouble(scanner.nextLine().trim());

        UtilityCard card = new UtilityCard();
        card.setpId(pId);
        card.setCardType(cardType);
        card.setBalance(balance);
        card.setLastTopup(LocalDateTime.now());

        Long id = cardDAO.insert(card);
        if (id != null) {
            System.out.println("✅ 插入成功，生成ID: " + id + "\n");
        } else {
            System.out.println("❌ 插入失败\n");
        }
    }

    private static void updateCard() {
        System.out.print("请输入要更新的水电卡ID: ");
        Long cardId = Long.parseLong(scanner.nextLine().trim());

        UtilityCard card = cardDAO.findById(cardId);
        if (card == null) {
            System.out.println("❌ 水电卡不存在\n");
            return;
        }

        System.out.println("当前水电卡: " + card);
        System.out.print("充值金额: ");
        Double topup = Double.parseDouble(scanner.nextLine().trim());

        card.setBalance(card.getBalance() + topup);
        card.setLastTopup(LocalDateTime.now());

        boolean success = cardDAO.update(card);
        System.out.println(success ? "✅ 充值成功，当前余额: " + card.getBalance() + "\n" : "❌ 充值失败\n");
    }

    private static void deleteCard() {
        System.out.print("请输入要删除的水电卡ID: ");
        Long cardId = Long.parseLong(scanner.nextLine().trim());

        boolean success = cardDAO.deleteById(cardId);
        System.out.println(success ? "✅ 删除成功\n" : "❌ 删除失败\n");
    }
}
