package com.worldofwonder.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.worldofwonder.model.Achievement;
import com.worldofwonder.service.AchievementService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AchievementController implements HttpHandler {

    private final AchievementService achievementService = new AchievementService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 204, "");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if ("/api/achievements".equals(path) && "GET".equalsIgnoreCase(method)) {
                handleGetAll(exchange);
            } else if ("/api/achievements/user".equals(path) && "GET".equalsIgnoreCase(method)) {
                handleGetUserAchievements(exchange);
            } else if ("/api/achievements/check".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleCheck(exchange);
            } else {
                sendJson(exchange, 404, error("Endpoint not found"));
            }
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, error("Internal server error"));
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Achievement> all = achievementService.getAllAchievements();
        sendJson(exchange, 200, "{\"success\":true,\"data\":" + toJson(all) + "}");
    }

    private void handleGetUserAchievements(HttpExchange exchange) throws IOException {
        String uid = getQueryParam(exchange, "userId");
        if (uid == null) throw new IllegalArgumentException("Missing userId");
        int userId = Integer.parseInt(uid.trim());
        List<Achievement> earned = achievementService.getUserAchievements(userId);
        sendJson(exchange, 200, "{\"success\":true,\"data\":" + toJson(earned) + "}");
    }

    private void handleCheck(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        String uid = strVal(body, "userId");
        if (uid == null) throw new IllegalArgumentException("Missing userId");
        int userId = Integer.parseInt(uid.trim());

        List<Achievement> newlyEarned = achievementService.checkAndAward(userId);
        sendJson(exchange, 200, "{\"success\":true,\"newlyEarned\":" + toJson(newlyEarned) + "}");
    }

    private String toJson(List<Achievement> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            Achievement a = list.get(i);
            sb.append("{\"id\":").append(a.getId())
                    .append(",\"key\":\"").append(esc(a.getKey()))
                    .append("\",\"name\":\"").append(esc(a.getName()))
                    .append("\",\"description\":\"").append(esc(a.getDescription()))
                    .append("\",\"icon\":\"").append(esc(a.getIcon()))
                    .append("\",\"conditionType\":\"").append(esc(a.getConditionType()))
                    .append("\",\"conditionValue\":").append(a.getConditionValue())
                    .append("}");
        }
        return sb.append("]").toString();
    }

    private String getQueryParam(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2 && key.equals(parts[0]))
                return java.net.URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
        }
        return null;
    }

    private String readBody(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private String strVal(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"?([^\",\\}\\]]+)\"?");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1).trim() : null;
    }

    private String esc(String v) {
        if (v == null) return "";
        return v.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String error(String msg) {
        return "{\"success\":false,\"message\":\"" + esc(msg) + "\"}";
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(status, status == 204 ? -1 : resp.length);
        if (status != 204) {
            try (OutputStream out = exchange.getResponseBody()) { out.write(resp); }
        }
    }
}
