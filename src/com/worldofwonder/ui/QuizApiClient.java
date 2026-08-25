package com.worldofwonder.ui;

import com.worldofwonder.model.Level;
import com.worldofwonder.model.Question;
import com.worldofwonder.model.World;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class QuizApiClient {

    private final String baseUrl;
    private final HttpClient client;

    QuizApiClient() {
        this("http://localhost:8080");
    }

    QuizApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    boolean isAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/worlds"))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    List<World> getWorlds() throws IOException {
        return parseWorlds(get("/api/worlds"));
    }

    List<Level> getLevels(int worldId) throws IOException {
        return parseLevels(get("/api/levels?worldId=" + worldId));
    }

    List<Question> getQuestions(int levelId) throws IOException {
        return parseQuestions(get("/api/questions?levelId=" + levelId));
    }

    AnswerResult submitAnswer(int userId, int questionId, String selectedAnswer) throws IOException {
        String body = "{\"userId\":" + userId + ",\"questionId\":" + questionId
                + ",\"selectedAnswer\":\"" + escape(selectedAnswer) + "\"}";
        String json = post("/api/answers", body);
        Map<String, Object> root = Json.asObject(Json.parse(json));
        boolean success = Boolean.TRUE.equals(root.get("success"));
        boolean correct = Boolean.TRUE.equals(root.get("correct"));
        String correctAnswer = Json.str(root.get("correctAnswer"));
        int pointsEarned = Json.num(root.get("pointsEarned"));
        int totalPoints = Json.num(root.get("totalPoints"));
        return new AnswerResult(success, correct, correctAnswer, pointsEarned, totalPoints);
    }

    int completePuzzle(int userId, int points) throws IOException {
        String body = "{\"userId\":" + userId + ",\"points\":" + points + "}";
        String json = post("/api/puzzles/complete", body);
        Map<String, Object> root = Json.asObject(Json.parse(json));
        return Json.num(root.get("totalPoints"));
    }

    private String post(String path, String body) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(6))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new IOException("Server returned status " + response.statusCode());
            }
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    static class AnswerResult {
        final boolean success;
        final boolean correct;
        final String correctAnswer;
        final int pointsEarned;
        final int totalPoints;

        AnswerResult(boolean success, boolean correct, String correctAnswer, int pointsEarned, int totalPoints) {
            this.success = success;
            this.correct = correct;
            this.correctAnswer = correctAnswer;
            this.pointsEarned = pointsEarned;
            this.totalPoints = totalPoints;
        }
    }

    private String get(String path) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("Server returned status " + response.statusCode());
            }
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    private List<World> parseWorlds(String json) {
        Map<String, Object> root = Json.asObject(Json.parse(json));
        List<Object> data = Json.asArray(root.get("data"));
        List<World> worlds = new ArrayList<>();
        for (Object item : data) {
            Map<String, Object> m = Json.asObject(item);
            World world = new World();
            world.setId(Json.num(m.get("id")));
            world.setName(Json.str(m.get("name")));
            world.setDescription(Json.str(m.get("description")));
            worlds.add(world);
        }
        return worlds;
    }

    private List<Level> parseLevels(String json) {
        Map<String, Object> root = Json.asObject(Json.parse(json));
        List<Object> data = Json.asArray(root.get("data"));
        List<Level> levels = new ArrayList<>();
        for (Object item : data) {
            Map<String, Object> m = Json.asObject(item);
            Level level = new Level();
            level.setId(Json.num(m.get("id")));
            level.setWorldId(Json.num(m.get("worldId")));
            level.setName(Json.str(m.get("name")));
            level.setDifficulty(Json.str(m.get("difficulty")));
            level.setPointReward(Json.num(m.get("pointReward")));
            levels.add(level);
        }
        return levels;
    }

    private List<Question> parseQuestions(String json) {
        Map<String, Object> root = Json.asObject(Json.parse(json));
        List<Object> data = Json.asArray(root.get("data"));
        List<Question> questions = new ArrayList<>();
        for (Object item : data) {
            Map<String, Object> m = Json.asObject(item);
            Question question = new Question();
            question.setId(Json.num(m.get("id")));
            question.setLevelId(Json.num(m.get("levelId")));
            question.setQuestionText(Json.str(m.get("questionText")));
            question.setOptionA(Json.str(m.get("optionA")));
            question.setOptionB(Json.str(m.get("optionB")));
            question.setOptionC(Json.str(m.get("optionC")));
            question.setOptionD(Json.str(m.get("optionD")));
            question.setCorrectAnswer(Json.str(m.get("correctAnswer")));
            question.setHint(Json.str(m.get("hint")));
            questions.add(question);
        }
        return questions;
    }
}
