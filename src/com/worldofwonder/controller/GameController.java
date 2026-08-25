package com.worldofwonder.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.worldofwonder.model.Level;
import com.worldofwonder.model.Question;
import com.worldofwonder.model.User;
import com.worldofwonder.model.WowLevel;
import com.worldofwonder.model.World;
import com.worldofwonder.service.GameService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameController implements HttpHandler {

    private final GameService gameService = new GameService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // Handle CORS Preflight Requests
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 204, "");
            return;
        }

        if ("/api/answers".equals(path)) {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, error("Method not allowed"));
                return;
            }
            try {
                handleAnswer(exchange);
            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, error(e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, error("Internal server error"));
            }
            return;
        }

        if ("/api/puzzles/complete".equals(path)) {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, error("Method not allowed"));
                return;
            }
            try {
                handlePuzzleComplete(exchange);
            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, error(e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, error("Internal server error"));
            }
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, error("Method not allowed"));
            return;
        }

        try {
            switch (path) {
                case "/api/worlds":
                    handleWorlds(exchange);
                    break;
                case "/api/levels":
                    handleLevels(exchange);
                    break;
                case "/api/questions":
                    handleQuestions(exchange);
                    break;
                case "/api/puzzles":
                    handlePuzzles(exchange);
                    break;
                case "/api/leaderboard":
                    handleLeaderboard(exchange);
                    break;
                default:
                    sendJson(exchange, 404, error("Endpoint not found"));
            }
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, error("Internal server error"));
        }
    }

    private void handleAnswer(HttpExchange exchange) throws IOException {
        Map<String, String> body = readJsonBody(exchange);
        int userId = parseId(body.get("userId"), "userId");
        int questionId = parseId(body.get("questionId"), "questionId");
        String selectedAnswer = body.get("selectedAnswer");

        if (selectedAnswer == null || selectedAnswer.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing field: selectedAnswer");
        }

        Map<String, Object> result = gameService.submitAnswer(userId, questionId, selectedAnswer);
        boolean correct = (Boolean) result.get("correct");
        String correctAnswer = (String) result.get("correctAnswer");
        int pointsEarned = (Integer) result.get("pointsEarned");
        int totalPoints = (Integer) result.get("totalPoints");

        String json = "{\"success\":true,\"correct\":" + correct
                + ",\"correctAnswer\":\"" + escapeJsonString(correctAnswer)
                + "\",\"pointsEarned\":" + pointsEarned
                + ",\"totalPoints\":" + totalPoints + "}";
        sendJson(exchange, 200, json);
    }

    private void handlePuzzleComplete(HttpExchange exchange) throws IOException {
        Map<String, String> body = readJsonBody(exchange);
        int userId = parseId(body.get("userId"), "userId");
        int points = parseId(body.get("points"), "points");
        int totalPoints = gameService.completePuzzle(userId, points);
        sendJson(exchange, 200, "{\"success\":true,\"totalPoints\":" + totalPoints + "}");
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
        putIfPresent(values, raw, "userId");
        putIfPresent(values, raw, "questionId");
        putIfPresent(values, raw, "selectedAnswer");
        putIfPresent(values, raw, "points");
        return values;
    }

    private void putIfPresent(Map<String, String> values, String json, String key) {
        String value = extractJsonString(json, key);
        if (value != null) {
            values.put(key, value);
        }
    }

    private String extractJsonString(String json, String key) {
        if (json == null) {
            return null;
        }
        // Matches both quoted strings ("key": "val") and raw numbers ("key": 123)
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"?([^\",\\}\\]]+)\"?");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).trim().replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return null;
    }

    private void handleWorlds(HttpExchange exchange) throws IOException {
        List<World> worlds = gameService.getWorlds();
        sendJson(exchange, 200, "{\"success\":true,\"data\":" + toWorldsJson(worlds) + "}");
    }

    private void handleLevels(HttpExchange exchange) throws IOException {
        String worldIdParam = getQueryParam(exchange, "worldId");
        if (worldIdParam == null) {
            throw new IllegalArgumentException("Missing query parameter: worldId");
        }
        int worldId = parseId(worldIdParam, "worldId");
        List<Level> levels = gameService.getLevels(worldId);
        sendJson(exchange, 200, "{\"success\":true,\"data\":" + toLevelsJson(levels) + "}");
    }

    private void handleQuestions(HttpExchange exchange) throws IOException {
        String levelIdParam = getQueryParam(exchange, "levelId");
        if (levelIdParam == null) {
            throw new IllegalArgumentException("Missing query parameter: levelId");
        }
        int levelId = parseId(levelIdParam, "levelId");
        List<Question> questions = gameService.getQuestions(levelId);
        sendJson(exchange, 200, "{\"success\":true,\"data\":" + toQuestionsJson(questions) + "}");
    }

    private void handleLeaderboard(HttpExchange exchange) throws IOException {
        List<User> users = gameService.getLeaderboard();
        sendJson(exchange, 200, "{\"success\":true,\"data\":" + toLeaderboardJson(users) + "}");
    }

    private void handlePuzzles(HttpExchange exchange) throws IOException {
        String worldIdParam = getQueryParam(exchange, "worldId");
        List<WowLevel> puzzles;
        if (worldIdParam != null) {
            int worldId = parseId(worldIdParam, "worldId");
            puzzles = gameService.getWowLevels(worldId);
        } else {
            puzzles = gameService.getAllWowLevels();
        }
        sendJson(exchange, 200, "{\"success\":true,\"data\":" + toWowLevelsJson(puzzles) + "}");
    }

    private String toLeaderboardJson(List<User> users) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < users.size(); i++) {
            if (i > 0) sb.append(",");
            User user = users.get(i);
            sb.append("{\"id\":").append(user.getId())
              .append(",\"username\":\"").append(escapeJsonString(user.getUsername()))
              .append("\",\"totalPoints\":").append(user.getTotalPoints()).append("}");
        }
        return sb.append("]").toString();
    }

    private int parseId(String value, String paramName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing field/parameter: " + paramName);
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for parameter: " + paramName);
        }
    }

    private String getQueryParam(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            return null;
        }
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2 && key.equals(parts[0])) {
                return java.net.URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private String toWorldsJson(List<World> worlds) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < worlds.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toWorldJson(worlds.get(i)));
        }
        return sb.append("]").toString();
    }

    private String toWorldJson(World world) {
        return "{\"id\":" + world.getId() + ",\"name\":\"" + escapeJsonString(world.getName())
                + "\",\"description\":\"" + escapeJsonString(world.getDescription()) + "\"}";
    }

    private String toLevelsJson(List<Level> levels) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < levels.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toLevelJson(levels.get(i)));
        }
        return sb.append("]").toString();
    }

    private String toLevelJson(Level level) {
        return "{\"id\":" + level.getId() + ",\"worldId\":" + level.getWorldId()
                + ",\"name\":\"" + escapeJsonString(level.getName())
                + "\",\"difficulty\":\"" + escapeJsonString(level.getDifficulty())
                + "\",\"pointReward\":" + level.getPointReward() + "}";
    }

    private String toWowLevelsJson(List<WowLevel> levels) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < levels.size(); i++) {
            if (i > 0) sb.append(",");
            WowLevel level = levels.get(i);
            sb.append("{\"id\":").append(level.getId())
              .append(",\"worldId\":").append(level.getWorldId())
              .append(",\"name\":\"").append(escapeJsonString(level.getName())).append("\"")
              .append(",\"difficulty\":\"").append(escapeJsonString(level.getDifficulty())).append("\"")
              .append(",\"theme\":\"").append(escapeJsonString(level.getTheme())).append("\"")
              .append(",\"words\":\"").append(escapeJsonString(level.getWords())).append("\"")
              .append(",\"pointReward\":").append(level.getPointReward()).append("}");
        }
        return sb.append("]").toString();
    }

    private String toQuestionsJson(List<Question> questions) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < questions.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toQuestionJson(questions.get(i)));
        }
        return sb.append("]").toString();
    }

    private String toQuestionJson(Question question) {
        return "{\"id\":" + question.getId() + ",\"levelId\":" + question.getLevelId()
                + ",\"questionText\":\"" + escapeJsonString(question.getQuestionText())
                + "\",\"optionA\":\"" + escapeJsonString(question.getOptionA())
                + "\",\"optionB\":\"" + escapeJsonString(question.getOptionB())
                + "\",\"optionC\":\"" + escapeJsonString(question.getOptionC())
                + "\",\"optionD\":\"" + escapeJsonString(question.getOptionD())
                + "\",\"correctAnswer\":\"" + escapeJsonString(question.getCorrectAnswer())
                + "\",\"hint\":\"" + escapeJsonString(question.getHint()) + "\"}";
    }

    private String escapeJsonString(String value) {
        if (value == null) return "";
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
        exchange.sendResponseHeaders(statusCode, statusCode == 204 ? -1 : response.length);
        if (statusCode != 204) {
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(response);
            }
        }
    }
}