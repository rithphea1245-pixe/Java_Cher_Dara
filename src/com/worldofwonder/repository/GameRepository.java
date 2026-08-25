package com.worldofwonder.repository;

import com.worldofwonder.database.DatabaseConnection;
import com.worldofwonder.model.Level;
import com.worldofwonder.model.Question;
import com.worldofwonder.model.WowLevel;
import com.worldofwonder.model.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameRepository {

    public List<World> getAllWorlds() {
        List<World> worlds = new ArrayList<>();
        String sql = "SELECT id, name, description FROM worlds ORDER BY id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                World world = new World();
                world.setId(resultSet.getInt("id"));
                world.setName(resultSet.getString("name"));
                world.setDescription(resultSet.getString("description"));
                worlds.add(world);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return worlds;
    }

    public List<Level> getLevelsByWorldId(int worldId) {
        List<Level> levels = new ArrayList<>();
        String sql = "SELECT id, world_id, name, difficulty, point_reward FROM levels WHERE world_id = ? ORDER BY id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, worldId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Level level = new Level();
                    level.setId(resultSet.getInt("id"));
                    level.setWorldId(resultSet.getInt("world_id"));
                    level.setName(resultSet.getString("name"));
                    level.setDifficulty(resultSet.getString("difficulty"));
                    level.setPointReward(resultSet.getInt("point_reward"));
                    levels.add(level);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return levels;
    }

    public List<Question> getQuestionsByLevelId(int levelId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT id, level_id, question_text, option_a, option_b, option_c, option_d, "
                + "correct_answer, hint FROM questions WHERE level_id = ? ORDER BY id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, levelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    questions.add(mapQuestion(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public Level findLevelById(int levelId) {
        String sql = "SELECT id, world_id, name, difficulty, point_reward FROM levels WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, levelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Level level = new Level();
                    level.setId(resultSet.getInt("id"));
                    level.setWorldId(resultSet.getInt("world_id"));
                    level.setName(resultSet.getString("name"));
                    level.setDifficulty(resultSet.getString("difficulty"));
                    level.setPointReward(resultSet.getInt("point_reward"));
                    return level;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Question findQuestionById(int questionId) {
        String sql = "SELECT id, level_id, question_text, option_a, option_b, option_c, option_d, "
                + "correct_answer, hint FROM questions WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, questionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapQuestion(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Question mapQuestion(ResultSet resultSet) throws SQLException {
        Question question = new Question();
        question.setId(resultSet.getInt("id"));
        question.setLevelId(resultSet.getInt("level_id"));
        question.setQuestionText(resultSet.getString("question_text"));
        question.setOptionA(resultSet.getString("option_a"));
        question.setOptionB(resultSet.getString("option_b"));
        question.setOptionC(resultSet.getString("option_c"));
        question.setOptionD(resultSet.getString("option_d"));
        question.setCorrectAnswer(resultSet.getString("correct_answer"));
        question.setHint(resultSet.getString("hint"));
        return question;
    }

    public List<WowLevel> getAllWowLevels() {
        List<WowLevel> levels = new ArrayList<>();
        String sql = "SELECT id, world_id, name, difficulty, theme, words, point_reward FROM wow_levels ORDER BY id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                levels.add(mapWowLevel(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return levels;
    }

    public List<WowLevel> getWowLevelsByWorldId(int worldId) {
        List<WowLevel> levels = new ArrayList<>();
        String sql = "SELECT id, world_id, name, difficulty, theme, words, point_reward FROM wow_levels WHERE world_id = ? ORDER BY id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, worldId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    levels.add(mapWowLevel(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return levels;
    }

    public WowLevel findWowLevelById(int wowLevelId) {
        String sql = "SELECT id, world_id, name, difficulty, theme, words, point_reward FROM wow_levels WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, wowLevelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapWowLevel(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private WowLevel mapWowLevel(ResultSet resultSet) throws SQLException {
        WowLevel level = new WowLevel();
        level.setId(resultSet.getInt("id"));
        level.setWorldId(resultSet.getInt("world_id"));
        level.setName(resultSet.getString("name"));
        level.setDifficulty(resultSet.getString("difficulty"));
        level.setTheme(resultSet.getString("theme"));
        level.setWords(resultSet.getString("words"));
        level.setPointReward(resultSet.getInt("point_reward"));
        return level;
    }
}
