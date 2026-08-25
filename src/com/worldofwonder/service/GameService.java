package com.worldofwonder.service;

import com.worldofwonder.model.Level;
import com.worldofwonder.model.Question;
import com.worldofwonder.model.User;
import com.worldofwonder.model.WowLevel;
import com.worldofwonder.model.World;
import com.worldofwonder.repository.GameRepository;
import com.worldofwonder.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameService {

    private final GameRepository gameRepository = new GameRepository();
    private final UserRepository userRepository = new UserRepository();

    public List<World> getWorlds() {
        return gameRepository.getAllWorlds();
    }

    public List<Level> getLevels(int worldId) {
        return gameRepository.getLevelsByWorldId(worldId);
    }

    public List<Question> getQuestions(int levelId) {
        return gameRepository.getQuestionsByLevelId(levelId);
    }

    public List<WowLevel> getAllWowLevels() {
        return gameRepository.getAllWowLevels();
    }

    public List<WowLevel> getWowLevels(int worldId) {
        return gameRepository.getWowLevelsByWorldId(worldId);
    }

    public List<User> getLeaderboard() {
        return userRepository.getLeaderboard(10);
    }

    public int completePuzzle(int userId, int pointsEarned) {
        if (pointsEarned <= 0) {
            throw new IllegalArgumentException("Invalid points value");
        }
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        int newTotal = user.getTotalPoints() + pointsEarned;
        userRepository.updateTotalPoints(userId, newTotal);
        return newTotal;
    }

    public boolean validateAnswer(int questionId, String selectedAnswer) {
        if (selectedAnswer == null || selectedAnswer.trim().isEmpty()) {
            return false;
        }
        Question question = gameRepository.findQuestionById(questionId);
        if (question == null || question.getCorrectAnswer() == null) {
            return false;
        }
        return question.getCorrectAnswer().trim().equalsIgnoreCase(selectedAnswer.trim());
    }

    public Map<String, Object> submitAnswer(int userId, int questionId, String selectedAnswer) {
        if (selectedAnswer == null || selectedAnswer.trim().isEmpty()) {
            throw new IllegalArgumentException("selectedAnswer is required");
        }

        Question question = gameRepository.findQuestionById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("Question not found: " + questionId);
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        boolean correct = question.getCorrectAnswer() != null
                && question.getCorrectAnswer().trim().equalsIgnoreCase(selectedAnswer.trim());

        String correctAnswer = question.getCorrectAnswer();
        int pointsEarned = 0;

        if (correct) {
            Level level = gameRepository.findLevelById(question.getLevelId());
            if (level != null) {
                List<Question> allQuestions = gameRepository.getQuestionsByLevelId(question.getLevelId());
                int questionCount = allQuestions.size();
                pointsEarned = questionCount > 0 ? level.getPointReward() / questionCount : level.getPointReward();
            }

            int newTotal = user.getTotalPoints() + pointsEarned;
            userRepository.updateTotalPoints(userId, newTotal);
            user.setTotalPoints(newTotal);
        }

        int totalPoints = user.getTotalPoints();

        Map<String, Object> result = new HashMap<>();
        result.put("correct", correct);
        result.put("correctAnswer", correctAnswer);
        result.put("pointsEarned", pointsEarned);
        result.put("totalPoints", totalPoints);
        return result;
    }
}
