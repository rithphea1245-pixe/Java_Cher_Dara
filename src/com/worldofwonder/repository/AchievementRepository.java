package com.worldofwonder.repository;

import com.worldofwonder.database.DatabaseConnection;
import com.worldofwonder.model.Achievement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AchievementRepository {

    public List<Achievement> getAllAchievements() {
        List<Achievement> list = new ArrayList<>();
        String sql = "SELECT id, key, name, description, icon, condition_type, condition_value "
                + "FROM achievements ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapAchievement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Achievement> getUserAchievements(int userId) {
        List<Achievement> list = new ArrayList<>();
        String sql = "SELECT a.id, a.key, a.name, a.description, a.icon, a.condition_type, a.condition_value "
                + "FROM achievements a JOIN user_achievements ua ON a.id = ua.achievement_id "
                + "WHERE ua.user_id = ? ORDER BY ua.earned_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAchievement(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean hasAchievement(int userId, int achievementId) {
        String sql = "SELECT 1 FROM user_achievements WHERE user_id = ? AND achievement_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, achievementId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean awardAchievement(int userId, int achievementId) {
        if (hasAchievement(userId, achievementId)) {
            return false;
        }
        String sql = "INSERT INTO user_achievements (user_id, achievement_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, achievementId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Achievement findByKey(String key) {
        String sql = "SELECT id, key, name, description, icon, condition_type, condition_value "
                + "FROM achievements WHERE key = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAchievement(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Achievement mapAchievement(ResultSet rs) throws SQLException {
        Achievement a = new Achievement();
        a.setId(rs.getInt("id"));
        a.setKey(rs.getString("key"));
        a.setName(rs.getString("name"));
        a.setDescription(rs.getString("description"));
        a.setIcon(rs.getString("icon"));
        a.setConditionType(rs.getString("condition_type"));
        a.setConditionValue(rs.getInt("condition_value"));
        return a;
    }
}
