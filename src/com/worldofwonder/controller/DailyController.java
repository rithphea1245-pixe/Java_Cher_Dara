package com.worldofwonder.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.worldofwonder.model.DailyChallenge;
import com.worldofwonder.model.UserStreak;
import com.worldofwonder.service.DailyChallengeService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DailyController implements HttpHandler {

    private final DailyChallengeService dailyService = new DailyChallengeService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 204, "");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if ("/api/daily".equals(path) && "GET".equalsIgnoreCase(method)) {
                handleGetDaily(exchange);
            } else if ("/api/daily/complete".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleComplete(exchange);
            } else if ("/api/streak".equals(path) && "GET".equalsIgnoreCase(method)) {
                handleGetStreak(exchange);
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

    private void handleGetDaily(HttpExchange exchange) throws IOException {
        DailyChallenge challenge = dailyService.getTodaysChallenge();
        String uid = getQueryParam(exchange, "userId");
        boolean completedToday = false;
        if (uid != null) {
            try {
                completedToday = dailyService.hasCompletedToday(Integer.parseInt(uid.trim()));
            } catch (NumberFormatException ignored) {}
        }

        String json = "{\"success\":true,\"data\":{"
                + "\"id\":" + challenge.getId()
                + ",\"challengeDate\":\"" + esc(challenge.getChallengeDate()) + "\""
                + ",\"gameType\":\"" + esc(challenge.getGameType()) + "\""
                + ",\"configJson\":" + challenge.getConfigJson()
                + ",\"bonusMultiplier\":" + challenge.getBonusMultiplier()
                + ",\"completedToday\":" + completedToday
                + "}}";
        sendJson(exchange, 200, json);
    }

    private void handleComplete(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        int userId = intVal(body, "userId");
        int basePoints = intVal(body, "basePoints");
        if (userId <= 0) throw new IllegalArgumentException("Missing userId");
        if (basePoints <= 0) basePoints = 10;

        int newTotal = dailyService.completeDaily(userId, basePoints);
        sendJson(exchange, 200, "{\"success\":true,\"totalPoints\":" + newTotal + "}");
    }

    private void handleGetStreak(HttpExchange exchange) throws IOException {
        String uid = getQueryParam(exchange, "userId");
        if (uid == null) throw new IllegalArgumentException("Missing userId");
        int userId = Integer.parseInt(uid.trim());

        UserStreak streak = dailyService.getStreak(userId);
        String json = "{\"success\":true,\"data\":{"
                + "\"currentStreak\":" + streak.getCurrentStreak()
                + ",\"longestStreak\":" + streak.getLongestStreak()
                + ",\"lastPlayDate\":\"" + esc(streak.getLastPlayDate()) + "\""
                + ",\"streakFreezes\":" + streak.getStreakFreezes()
                + "}}";
        sendJson(exchange, 200, json);
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

    private int intVal(String json, String key) {
        String v = strVal(json, key);
        if (v == null) return 0;
        try { return Integer.parseInt(v); } catch (NumberFormatException e) { return 0; }
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
