package com.ifham.analyzer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ifham.analyzer.dto.RegisterRequest;
import com.ifham.analyzer.entity.User;
import com.ifham.analyzer.repository.UserRepository;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User register(RegisterRequest request) {
        logger.debug("Checking if username already exists: {}", request.getUsername());

        if (repo.findByUsername(request.getUsername()) != null) {
            logger.warn("Username already exists: {}", request.getUsername());
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole("USER");

        User saved = repo.save(user);
        logger.info("User saved in DB with id {} and username {}", saved.getId(), saved.getUsername());

        return saved;
    }
}
