package com.worldofwonder.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.worldofwonder.service.AuthService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AdminController implements HttpHandler {

    private final AuthService authService = new AuthService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 204, "");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (!"DELETE".equalsIgnoreCase(method)) {
            sendJson(exchange, 405, error("Method not allowed"));
            return;
        }

        try {
            if (!path.startsWith("/api/users/")) {
                sendJson(exchange, 404, error("Endpoint not found"));
                return;
            }

            String idSegment = path.substring("/api/users/".length());
            int userId;
            try {
                userId = Integer.parseInt(idSegment.trim());
            } catch (NumberFormatException e) {
                sendJson(exchange, 400, error("Invalid user id"));
                return;
            }

            String adminToken = extractBearerToken(exchange);
            var admin = authService.resolveTokenUser(adminToken);
            if (admin == null || !admin.isAdmin()) {
                sendJson(exchange, 403, error("Admin privileges required"));
                return;
            }

            if (admin.getId() == userId) {
                sendJson(exchange, 400, error("You cannot delete your own account"));
                return;
            }

            if (!authService.deleteUser(userId)) {
                sendJson(exchange, 404, error("User not found"));
                return;
            }

            sendJson(exchange, 200, "{\"success\":true,\"message\":\"User deleted\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, error("Internal server error"));
        }
    }

    private String extractBearerToken(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Authorization");
        if (header == null || header.isEmpty()) {
            return null;
        }
        return header.startsWith("Bearer ") ? header.substring("Bearer ".length()).trim() : null;
    }

    private String error(String message) {
        return "{\"success\":false,\"message\":\"" + escapeJsonString(message) + "\"}";
    }

    private String escapeJsonString(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(statusCode, statusCode == 204 ? -1 : response.length);
        if (statusCode != 204) {
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(response);
                output.flush();
            }
        }
    }
}
