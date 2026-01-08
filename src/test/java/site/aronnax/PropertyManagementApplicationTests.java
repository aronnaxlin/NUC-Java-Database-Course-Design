package site.aronnax;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class PropertyManagementApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void checkUsers() {
        System.out.println("---------- DB CHECK START ----------");
        List<Map<String, Object>> users = jdbcTemplate
                .queryForList("SELECT user_id, user_name, password, user_type FROM users");

        if (users.isEmpty()) {
            System.out.println("No users found! Creating admin...");
            jdbcTemplate.update("INSERT INTO users (user_name, password, user_type, name) VALUES (?, ?, ?, ?)",
                    "admin", "123456", "ADMIN", "Administrator");
            System.out.println("Admin created: admin / 123456");
        } else {
            for (Map<String, Object> user : users) {
                System.out.println("User: " + user.get("user_name") + " | Pwd: " + user.get("password") + " | Type: "
                        + user.get("user_type"));
            }
        }
        System.out.println("---------- DB CHECK END ----------");
    }
}
