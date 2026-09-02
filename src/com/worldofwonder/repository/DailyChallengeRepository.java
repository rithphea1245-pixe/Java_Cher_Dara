package com.worldofwonder.repository;

import com.worldofwonder.database.DatabaseConnection;
import com.worldofwonder.model.DailyChallenge;
import com.worldofwonder.model.UserStreak;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DailyChallengeRepository {

    // ── Daily Challenge ─────────────────────────────────────────────────

    public DailyChallenge findByDate(String dateStr) {
        String sql = "SELECT id, challenge_date, game_type, config_json, bonus_multiplier "
                + "FROM daily_challenges WHERE challenge_date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(dateStr));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapChallenge(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createChallenge(DailyChallenge challenge) {
        String sql = "INSERT INTO daily_challenges (challenge_date, game_type, config_json, bonus_multiplier) "
                + "VALUES (?, ?, ?, ?) ON CONFLICT (challenge_date) DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(challenge.getChallengeDate()));
            stmt.setString(2, challenge.getGameType());
            stmt.setString(3, challenge.getConfigJson());
            stmt.setInt(4, challenge.getBonusMultiplier());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasCompletedToday(int userId, String dateStr) {
        String sql = "SELECT 1 FROM daily_completions WHERE user_id = ? AND challenge_date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(dateStr));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean recordCompletion(int userId, String dateStr, int pointsEarned) {
        String sql = "INSERT INTO daily_completions (user_id, challenge_date, points_earned) "
                + "VALUES (?, ?, ?) ON CONFLICT (user_id, challenge_date) DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(dateStr));
            stmt.setInt(3, pointsEarned);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── User Streak ─────────────────────────────────────────────────────

    public UserStreak getStreak(int userId) {
        String sql = "SELECT id, user_id, current_streak, longest_streak, last_play_date, streak_freezes "
                + "FROM user_streaks WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserStreak s = new UserStreak();
                    s.setId(rs.getInt("id"));
                    s.setUserId(rs.getInt("user_id"));
                    s.setCurrentStreak(rs.getInt("current_streak"));
                    s.setLongestStreak(rs.getInt("longest_streak"));
                    Date d = rs.getDate("last_play_date");
                    s.setLastPlayDate(d != null ? d.toString() : null);
                    s.setStreakFreezes(rs.getInt("streak_freezes"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean upsertStreak(UserStreak streak) {
        String sql = "INSERT INTO user_streaks (user_id, current_streak, longest_streak, last_play_date, streak_freezes) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT (user_id) DO UPDATE SET "
                + "current_streak = EXCLUDED.current_streak, "
                + "longest_streak = EXCLUDED.longest_streak, "
                + "last_play_date = EXCLUDED.last_play_date, "
                + "streak_freezes = EXCLUDED.streak_freezes";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, streak.getUserId());
            stmt.setInt(2, streak.getCurrentStreak());
            stmt.setInt(3, streak.getLongestStreak());
            if (streak.getLastPlayDate() != null) {
                stmt.setDate(4, Date.valueOf(streak.getLastPlayDate()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            stmt.setInt(5, streak.getStreakFreezes());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getCompletedDaysCount(int userId) {
        String sql = "SELECT COUNT(DISTINCT challenge_date) AS cnt FROM daily_completions WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private DailyChallenge mapChallenge(ResultSet rs) throws SQLException {
        DailyChallenge c = new DailyChallenge();
        c.setId(rs.getInt("id"));
        c.setChallengeDate(rs.getDate("challenge_date").toString());
        c.setGameType(rs.getString("game_type"));
        c.setConfigJson(rs.getString("config_json"));
        c.setBonusMultiplier(rs.getInt("bonus_multiplier"));
        return c;
    }
}
