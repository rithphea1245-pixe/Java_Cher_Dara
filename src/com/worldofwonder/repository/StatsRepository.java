package com.worldofwonder.repository;

import com.worldofwonder.database.DatabaseConnection;
import com.worldofwonder.model.GameSession;
import com.worldofwonder.model.UserStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsRepository {

    public boolean recordSession(GameSession session) {
        String sql = "INSERT INTO game_sessions (user_id, game_type, points_earned, completed, "
                + "duration_seconds, star_rating) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, session.getUserId());
            stmt.setString(2, session.getGameType());
            stmt.setInt(3, session.getPointsEarned());
            stmt.setBoolean(4, session.isCompleted());
            stmt.setInt(5, session.getDurationSeconds());
            stmt.setInt(6, session.getStarRating());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserStats getUserStats(int userId) {
        UserStats stats = new UserStats();
        stats.setUserId(userId);

        String sql = "SELECT "
                + "COUNT(*) AS total_played, "
                + "COUNT(*) FILTER (WHERE completed = TRUE) AS total_completed, "
                + "COALESCE(SUM(star_rating), 0) AS total_stars, "
                + "COUNT(*) FILTER (WHERE game_type = 'quiz') AS quiz_count, "
                + "COUNT(*) FILTER (WHERE game_type = 'wordsearch') AS ws_count, "
                + "COUNT(*) FILTER (WHERE game_type = 'cups') AS cups_count, "
                + "COUNT(*) FILTER (WHERE game_type = 'words') AS wow_count "
                + "FROM game_sessions WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalGamesPlayed(rs.getInt("total_played"));
                    stats.setTotalGamesCompleted(rs.getInt("total_completed"));
                    stats.setTotalStars(rs.getInt("total_stars"));
                    stats.setQuizGames(rs.getInt("quiz_count"));
                    stats.setWordSearchGames(rs.getInt("ws_count"));
                    stats.setCupsGames(rs.getInt("cups_count"));
                    stats.setWowGames(rs.getInt("wow_count"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Determine favorite game type
        int max = Math.max(Math.max(stats.getQuizGames(), stats.getWordSearchGames()),
                Math.max(stats.getCupsGames(), stats.getWowGames()));
        if (max == 0) {
            stats.setFavoriteGameType("none");
        } else if (max == stats.getQuizGames()) {
            stats.setFavoriteGameType("quiz");
        } else if (max == stats.getWordSearchGames()) {
            stats.setFavoriteGameType("wordsearch");
        } else if (max == stats.getCupsGames()) {
            stats.setFavoriteGameType("cups");
        } else {
            stats.setFavoriteGameType("words");
        }

        // Get total points and username from users table
        String userSql = "SELECT username, total_points FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(userSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setUsername(rs.getString("username"));
                    stats.setTotalPoints(rs.getInt("total_points"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Get achievement count
        String achSql = "SELECT COUNT(*) AS cnt FROM user_achievements WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(achSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setAchievementCount(rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            // Table might not exist yet, ignore
        }

        return stats;
    }
}
