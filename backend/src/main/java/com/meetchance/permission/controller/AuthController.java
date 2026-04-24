package com.meetchance.permission.controller;

import com.meetchance.permission.common.Result;
import com.meetchance.permission.dto.LoginDTO;
import com.meetchance.permission.dto.RegisterDTO;
import com.meetchance.permission.entity.User;
import com.meetchance.permission.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterDTO dto) {
        User user = authService.register(dto);
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        Map<String, Object> result = authService.login(dto);
        User user = (User) result.get("user");
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(result);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestAttribute("userId") Long userId) {
        authService.logout(userId);
        return Result.success();
    }

    @GetMapping("/user-info")
    public Result<User> getUserInfo(@RequestAttribute("userId") Long userId) {
        User user = authService.getCurrentUser(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
}
