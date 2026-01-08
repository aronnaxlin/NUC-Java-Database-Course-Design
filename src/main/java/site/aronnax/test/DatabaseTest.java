package site.aronnax.test;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DatabaseTest {
    public static void main(String[] args) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(
                "jdbc:mysql://crystallove.me:3306/property_management?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8");
        dataSource.setUsername("propertyAdmin");
        dataSource.setPassword("realAronnaxlin917-");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        try {
            System.out.println("Checking users table...");
            List<Map<String, Object>> users = jdbcTemplate
                    .queryForList("SELECT user_id, user_name, password, user_type FROM users");

            if (users.isEmpty()) {
                System.out.println("No users found!");
                // Create admin user
                System.out.println("Creating default admin user...");
                jdbcTemplate.update("INSERT INTO users (user_name, password, user_type, name) VALUES (?, ?, ?, ?)",
                        "admin", "123456", "ADMIN", "Administrator");
                System.out.println("Admin user created.");
            } else {
                for (Map<String, Object> user : users) {
                    System.out.println("User: " + user.get("user_name") + ", Type: " + user.get("user_type") + ", Pwd: "
                            + user.get("password"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
