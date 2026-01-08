package site.aronnax.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import site.aronnax.common.Result;
import site.aronnax.dao.UserDAO;
import site.aronnax.dto.LoginRequest;
import site.aronnax.entity.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDAO userDAO;

    public AuthController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        User user = userDAO.findByUserNameAndPassword(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            session.setAttribute("user", user);
            return Result.success(user);
        }
        return Result.error("用户名或密码错误 | Invalid Credentials");
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        return Result.success("Logged out successfully");
    }
}
