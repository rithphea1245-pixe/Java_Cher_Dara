package com.worldofwonder.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.worldofwonder.model.GameSession;
import com.worldofwonder.model.UserStats;
import com.worldofwonder.service.StatsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatsController implements HttpHandler {

    private final StatsService statsService = new StatsService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 204, "");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if ("/api/stats".equals(path) && "GET".equalsIgnoreCase(method)) {
                handleGetStats(exchange);
            } else if ("/api/sessions".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleRecordSession(exchange);
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

    private void handleGetStats(HttpExchange exchange) throws IOException {
        String userIdParam = getQueryParam(exchange, "userId");
        if (userIdParam == null) {
            throw new IllegalArgumentException("Missing query parameter: userId");
        }
        int userId = Integer.parseInt(userIdParam.trim());
        UserStats stats = statsService.getUserStats(userId);

        String json = "{\"success\":true,\"data\":{"
                + "\"userId\":" + stats.getUserId()
                + ",\"username\":\"" + esc(stats.getUsername()) + "\""
                + ",\"totalGamesPlayed\":" + stats.getTotalGamesPlayed()
                + ",\"totalGamesCompleted\":" + stats.getTotalGamesCompleted()
                + ",\"totalPoints\":" + stats.getTotalPoints()
                + ",\"totalStars\":" + stats.getTotalStars()
                + ",\"winRate\":" + stats.getWinRate()
                + ",\"favoriteGameType\":\"" + esc(stats.getFavoriteGameType()) + "\""
                + ",\"quizGames\":" + stats.getQuizGames()
                + ",\"wordSearchGames\":" + stats.getWordSearchGames()
                + ",\"cupsGames\":" + stats.getCupsGames()
                + ",\"wowGames\":" + stats.getWowGames()
                + ",\"achievementCount\":" + stats.getAchievementCount()
                + "}}";
        sendJson(exchange, 200, json);
    }

    private void handleRecordSession(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        GameSession session = new GameSession();
        session.setUserId(intVal(body, "userId"));
        session.setGameType(strVal(body, "gameType"));
        session.setPointsEarned(intVal(body, "pointsEarned"));
        session.setCompleted("true".equals(strVal(body, "completed")));
        session.setDurationSeconds(intVal(body, "durationSeconds"));
        session.setStarRating(intVal(body, "starRating"));

        boolean ok = statsService.recordSession(session);
        sendJson(exchange, ok ? 201 : 400,
                ok ? "{\"success\":true}" : error("Failed to record session"));
    }

    private String getQueryParam(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2 && key.equals(parts[0])) {
                return java.net.URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
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
