package com.ifham.analyzer.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ifham.analyzer.dto.LoginRequest;
import com.ifham.analyzer.dto.RegisterRequest;
import com.ifham.analyzer.entity.User;
import com.ifham.analyzer.repository.UserRepository;
import com.ifham.analyzer.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            logger.info("Register request received for username: {}", request.getUsername());
            User user = userService.register(request);
            logger.info("User registered successfully: {}", user.getUsername());
            return ResponseEntity.ok("User registered successfully: " + user.getUsername());
        } catch (Exception e) {
            logger.warn("Registration failed for username {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        logger.info("Login attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername());

        if (user == null) {
            logger.warn("Login failed: invalid username {}", request.getUsername());
            return ResponseEntity.status(401).body("Invalid username or password!");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login failed: invalid password for username {}", request.getUsername());
            return ResponseEntity.status(401).body("Invalid username or password!");
        }

        logger.info("Login successful for username: {}", user.getUsername());

        // NO JWT, NO TOKEN – sirf simple success response
        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful",
                        "username", user.getUsername(),
                        "role", user.getRole()
                )
        );
    }
}
