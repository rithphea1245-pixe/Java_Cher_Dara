package com.worldofwonder.service;

import com.worldofwonder.model.DailyChallenge;
import com.worldofwonder.model.UserStreak;
import com.worldofwonder.repository.DailyChallengeRepository;
import com.worldofwonder.repository.UserRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class DailyChallengeService {

    private static final String[] GAME_TYPES = {"quiz", "wordsearch", "cups", "words"};

    private final DailyChallengeRepository dailyRepo = new DailyChallengeRepository();
    private final UserRepository userRepo = new UserRepository();

    /**
     * Gets today's challenge, generating one if it doesn't exist yet.
     */
    public DailyChallenge getTodaysChallenge() {
        String today = LocalDate.now().toString();
        DailyChallenge existing = dailyRepo.findByDate(today);
        if (existing != null) {
            return existing;
        }
        // Generate deterministic daily challenge using date as seed
        DailyChallenge challenge = generateChallenge(today);
        dailyRepo.createChallenge(challenge);
        return dailyRepo.findByDate(today);
    }

    /**
     * Marks today's challenge as completed for the user and updates streak.
     */
    public int completeDaily(int userId, int basePoints) {
        String today = LocalDate.now().toString();
        if (dailyRepo.hasCompletedToday(userId, today)) {
            return 0; // Already completed
        }

        DailyChallenge challenge = getTodaysChallenge();
        int bonus = basePoints * (challenge != null ? challenge.getBonusMultiplier() : 2);
        dailyRepo.recordCompletion(userId, today, bonus);
        updateStreak(userId, today);

        // Update user total points
        var user = userRepo.findById(userId);
        if (user != null) {
            int newTotal = user.getTotalPoints() + bonus;
            userRepo.updateTotalPoints(userId, newTotal);
            return newTotal;
        }
        return bonus;
    }

    public UserStreak getStreak(int userId) {
        UserStreak streak = dailyRepo.getStreak(userId);
        if (streak == null) {
            streak = new UserStreak();
            streak.setUserId(userId);
            streak.setCurrentStreak(0);
            streak.setLongestStreak(0);
            streak.setStreakFreezes(1); // Start with 1 free freeze
        }
        return streak;
    }

    public boolean hasCompletedToday(int userId) {
        return dailyRepo.hasCompletedToday(userId, LocalDate.now().toString());
    }

    private void updateStreak(int userId, String todayStr) {
        LocalDate today = LocalDate.parse(todayStr);
        UserStreak streak = dailyRepo.getStreak(userId);

        if (streak == null) {
            streak = new UserStreak();
            streak.setUserId(userId);
            streak.setCurrentStreak(1);
            streak.setLongestStreak(1);
            streak.setLastPlayDate(todayStr);
            streak.setStreakFreezes(1);
            dailyRepo.upsertStreak(streak);
            return;
        }

        if (todayStr.equals(streak.getLastPlayDate())) {
            return; // Already played today
        }

        LocalDate lastPlay = streak.getLastPlayDate() != null
                ? LocalDate.parse(streak.getLastPlayDate()) : null;

        if (lastPlay == null) {
            streak.setCurrentStreak(1);
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastPlay, today);
            if (daysBetween == 1) {
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
            } else if (daysBetween == 2 && streak.getStreakFreezes() > 0) {
                // Use streak freeze
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                streak.setStreakFreezes(streak.getStreakFreezes() - 1);
            } else {
                streak.setCurrentStreak(1);
            }
        }

        if (streak.getCurrentStreak() > streak.getLongestStreak()) {
            streak.setLongestStreak(streak.getCurrentStreak());
        }
        streak.setLastPlayDate(todayStr);
        dailyRepo.upsertStreak(streak);
    }

    private DailyChallenge generateChallenge(String dateStr) {
        // Use date string hash as seed for reproducible daily puzzles
        Random rnd = new Random(dateStr.hashCode());
        String gameType = GAME_TYPES[rnd.nextInt(GAME_TYPES.length)];

        String config;
        switch (gameType) {
            case "quiz":
                int worldId = rnd.nextInt(6) + 1;
                config = "{\"worldId\":" + worldId + ",\"questionCount\":5}";
                break;
            case "wordsearch":
                String[] diffs = {"EASY", "MEDIUM", "HARD"};
                config = "{\"difficulty\":\"" + diffs[rnd.nextInt(3)]
                        + "\",\"theme\":" + rnd.nextInt(6) + "}";
                break;
            case "cups":
                int colors = rnd.nextInt(3) + 4; // 4-6 colors
                config = "{\"colorCount\":" + colors + ",\"capacity\":4}";
                break;
            case "words":
            default:
                int puzzleIdx = rnd.nextInt(8);
                config = "{\"puzzleIndex\":" + puzzleIdx + "}";
                break;
        }

        DailyChallenge challenge = new DailyChallenge();
        challenge.setChallengeDate(dateStr);
        challenge.setGameType(gameType);
        challenge.setConfigJson(config);
        challenge.setBonusMultiplier(2);
        return challenge;
    }
}
