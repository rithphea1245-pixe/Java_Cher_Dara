package com.worldofwonder.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.worldofwonder.model.User;
import com.worldofwonder.service.AuthService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthController implements HttpHandler {

    private final AuthService authService = new AuthService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // Handle CORS preflight options request
        if ("OPTIONS".equalsIgnoreCase(method)) {
            sendJson(exchange, 204, "");
            return;
        }

        if (!"POST".equalsIgnoreCase(method)) {
            sendJson(exchange, 405, error("Method not allowed"));
            return;
        }

        try {
            switch (path) {
                case "/api/auth/register":
                case "/api/auth/register/":
                    handleRegister(exchange);
                    break;
                case "/api/auth/login":
                case "/api/auth/login/":
                    handleLogin(exchange);
                    break;
                default:
                    sendJson(exchange, 404, error("Endpoint not found"));
            }
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, error("Internal server error: " + e.getMessage()));
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        Map<String, String> body = readJsonBody(exchange);

        User user = new User();
        user.setUsername(body.get("username"));
        user.setEmail(body.get("email"));
        user.setPassword(body.get("password"));

        User registered = authService.register(user);

        String json = "{\"success\":true,\"message\":\"Registration successful\",\"user\":"
                + toUserJson(registered) + "}";
        sendJson(exchange, 201, json);
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        Map<String, String> body = readJsonBody(exchange);

        Map<String, Object> result = authService.login(body.get("username"), body.get("password"));
        User user = (User) result.get("user");
        String token = (String) result.get("token");

        String json = "{\"success\":true,\"message\":\"Login successful\",\"token\":\""
                + escapeJsonString(token) + "\",\"user\":" + toUserJson(user) + "}";
        sendJson(exchange, 200, json);
    }

    private Map<String, String> readJsonBody(HttpExchange exchange) throws IOException {
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }

        String raw = buffer.toString();
        Map<String, String> values = new HashMap<>();
        
        values.put("username", extractJsonString(raw, "username"));
        values.put("email", extractJsonString(raw, "email"));
        values.put("password", extractJsonString(raw, "password"));

        return values;
    }

    private String extractJsonString(String json, String key) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return unescapeJsonString(matcher.group(1));
        }
        return null;
    }

    private String unescapeJsonString(String value) {
        if (value == null) return null;
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private String toUserJson(User user) {
        if (user == null) return "null";
        return "{\"id\":" + user.getId() + ",\"username\":\"" + escapeJsonString(user.getUsername())
                + "\",\"email\":\"" + escapeJsonString(user.getEmail())
                + "\",\"totalPoints\":" + user.getTotalPoints()
                + ",\"isAdmin\":" + user.isAdmin() + "}";
    }

    private String escapeJsonString(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String error(String message) {
        return "{\"success\":false,\"message\":\"" + escapeJsonString(message) + "\"}";
    }

    private void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if (statusCode == 204 || response.length == 0) {
            exchange.sendResponseHeaders(statusCode, -1);
        } else {
            exchange.sendResponseHeaders(statusCode, response.length);
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(response);
                output.flush();
            }
        }
    }
}