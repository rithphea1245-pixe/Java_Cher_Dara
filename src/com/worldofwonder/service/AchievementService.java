package com.worldofwonder.service;

import com.worldofwonder.model.Achievement;
import com.worldofwonder.model.UserStats;
import com.worldofwonder.repository.AchievementRepository;
import com.worldofwonder.repository.DailyChallengeRepository;
import com.worldofwonder.repository.StatsRepository;

import java.util.ArrayList;
import java.util.List;

public class AchievementService {

    private final AchievementRepository achievementRepo = new AchievementRepository();
    private final StatsRepository statsRepo = new StatsRepository();
    private final DailyChallengeRepository dailyRepo = new DailyChallengeRepository();

    public List<Achievement> getAllAchievements() {
        return achievementRepo.getAllAchievements();
    }

    public List<Achievement> getUserAchievements(int userId) {
        return achievementRepo.getUserAchievements(userId);
    }

    /**
     * Checks all achievements for the user and awards any newly-earned ones.
     * Returns a list of newly awarded achievements (for popup display).
     */
    public List<Achievement> checkAndAward(int userId) {
        List<Achievement> newlyEarned = new ArrayList<>();
        UserStats stats = statsRepo.getUserStats(userId);
        List<Achievement> all = achievementRepo.getAllAchievements();

        for (Achievement a : all) {
            if (achievementRepo.hasAchievement(userId, a.getId())) {
                continue;
            }
            if (isConditionMet(a, stats, userId)) {
                if (achievementRepo.awardAchievement(userId, a.getId())) {
                    newlyEarned.add(a);
                }
            }
        }
        return newlyEarned;
    }

    private boolean isConditionMet(Achievement a, UserStats stats, int userId) {
        String type = a.getConditionType();
        int target = a.getConditionValue();

        switch (type) {
            case "total_completed":
                return stats.getTotalGamesCompleted() >= target;
            case "total_points":
                return stats.getTotalPoints() >= target;
            case "total_played":
                return stats.getTotalGamesPlayed() >= target;
            case "total_stars":
                return stats.getTotalStars() >= target;
            case "streak":
                var streak = dailyRepo.getStreak(userId);
                return streak != null && streak.getCurrentStreak() >= target;
            case "longest_streak":
                var ls = dailyRepo.getStreak(userId);
                return ls != null && ls.getLongestStreak() >= target;
            case "quiz_correct":
                return stats.getQuizGames() >= target;
            case "wordsearch_words":
                return stats.getWordSearchGames() >= target;
            case "wow_completed":
                return stats.getWowGames() >= target;
            case "cups_completed":
                return stats.getCupsGames() >= target;
            case "all_modes":
                return stats.getQuizGames() > 0 && stats.getWordSearchGames() > 0
                        && stats.getCupsGames() > 0 && stats.getWowGames() > 0;
            case "achievement_count":
                return stats.getAchievementCount() >= target;
            default:
                return false;
        }
    }
}
