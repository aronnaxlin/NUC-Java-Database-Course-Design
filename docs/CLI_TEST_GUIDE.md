# Java 数据库连接测试指南

## 项目结构

```
NUC-Java-Database-Course-Design/
├── pom.xml                          # Maven 配置文件
├── src/
│   └── main/
│       ├── java/
│       │   └── site/aronnax/
│       │       ├── CLITest.java     # 命令行测试程序
│       │       └── util/
│       │           └── DBUtil.java  # 数据库工具类
│       └── resources/
│           └── db.properties        # 数据库配置文件
└── sql/
    ├── schema.sql                   # 数据库表结构
    └── data.sql                     # 测试数据
```

## 运行步骤

### 1. 配置数据库连接

编辑 `src/main/resources/db.properties`，修改数据库连接参数：

```properties
db.url=jdbc:mysql://localhost:3306/property_management?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
db.username=root
db.password=你的密码
```

### 2. 初始化数据库

在 MySQL 中执行以下命令：

```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/data.sql
```

### 3. 编译项目

```bash
mvn clean compile
```

### 4. 运行测试程序

```bash
mvn exec:java -Dexec.mainClass="site.aronnax.CLITest"
```

## 功能说明

CLI 测试程序提供以下功能：

1. **查询所有用户** - 显示系统中的所有用户信息
2. **查询所有房产** - 显示所有房产记录
3. **查询所有账单** - 显示费用缴纳情况
4. **执行自定义 SQL** - 手动输入 SELECT 语句进行查询

## 注意事项

- 确保 MySQL 服务已启动
- 确保数据库 `property_management` 已创建
- 密码中包含特殊字符时需要在 db.properties 中正确转义
