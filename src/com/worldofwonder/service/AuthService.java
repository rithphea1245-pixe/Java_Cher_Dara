package com.worldofwonder.service;

import com.worldofwonder.model.User;
import com.worldofwonder.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();

    public User register(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim());
        user.setTotalPoints(0);

        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (!userRepository.createUser(user)) {
            throw new IllegalStateException("Failed to register user");
        }
        return user;
    }

    public Map<String, Object> login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password are required");
        }

        String trimmedUser = username.trim();
        // Hardcoded admin login bypass check: Username "admin" and password "hengheng168"
        if ("admin".equalsIgnoreCase(trimmedUser) && "hengheng168".equals(password)) {
            User adminUser = null;
            try {
                adminUser = userRepository.findByUsername("admin");
            } catch (Exception ignored) {
            }
            if (adminUser == null) {
                adminUser = new User(1, "admin", "admin@worldofwonder.com", "hengheng168", 100);
                adminUser.setAdmin(true);
            } else {
                adminUser.setAdmin(true);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("user", adminUser);
            result.put("token", generateToken(adminUser));
            return result;
        }

        User user = userRepository.findByUsername(trimmedUser);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", generateToken(user));
        return result;
    }

    private String generateToken(User user) {
        String raw = user.getId() + ":" + user.getUsername() + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public User resolveTokenUser(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            String raw = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = raw.split(":", 3);
            if (parts.length < 2) {
                return null;
            }
            int userId = Integer.parseInt(parts[0]);
            String username = parts[1];
            if ("admin".equalsIgnoreCase(username)) {
                User admin = null;
                try {
                    admin = userRepository.findById(userId);
                } catch (Exception ignored) {}
                if (admin == null) {
                    admin = new User(userId, "admin", "admin@worldofwonder.com", "hengheng168", 100);
                    admin.setAdmin(true);
                }
                return admin;
            }
            User user = userRepository.findById(userId);
            if (user == null || !user.getUsername().equals(username)) {
                return null;
            }
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean deleteUser(int userId) {
        return userRepository.deleteById(userId);
    }
}
