package com.worldofwonder.model;

public class UserStats {

    private int userId;
    private String username;
    private int totalGamesPlayed;
    private int totalGamesCompleted;
    private int totalPoints;
    private int totalStars;
    private int currentStreak;
    private int longestStreak;
    private String favoriteGameType;
    private int quizGames;
    private int wordSearchGames;
    private int cupsGames;
    private int wowGames;
    private int achievementCount;

    public UserStats() {
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getTotalGamesPlayed() { return totalGamesPlayed; }
    public void setTotalGamesPlayed(int totalGamesPlayed) { this.totalGamesPlayed = totalGamesPlayed; }

    public int getTotalGamesCompleted() { return totalGamesCompleted; }
    public void setTotalGamesCompleted(int totalGamesCompleted) { this.totalGamesCompleted = totalGamesCompleted; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public int getTotalStars() { return totalStars; }
    public void setTotalStars(int totalStars) { this.totalStars = totalStars; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public String getFavoriteGameType() { return favoriteGameType; }
    public void setFavoriteGameType(String favoriteGameType) { this.favoriteGameType = favoriteGameType; }

    public int getQuizGames() { return quizGames; }
    public void setQuizGames(int quizGames) { this.quizGames = quizGames; }

    public int getWordSearchGames() { return wordSearchGames; }
    public void setWordSearchGames(int wordSearchGames) { this.wordSearchGames = wordSearchGames; }

    public int getCupsGames() { return cupsGames; }
    public void setCupsGames(int cupsGames) { this.cupsGames = cupsGames; }

    public int getWowGames() { return wowGames; }
    public void setWowGames(int wowGames) { this.wowGames = wowGames; }

    public int getAchievementCount() { return achievementCount; }
    public void setAchievementCount(int achievementCount) { this.achievementCount = achievementCount; }

    public double getWinRate() {
        return totalGamesPlayed > 0
                ? Math.round((totalGamesCompleted * 100.0 / totalGamesPlayed) * 10.0) / 10.0
                : 0.0;
    }
}
