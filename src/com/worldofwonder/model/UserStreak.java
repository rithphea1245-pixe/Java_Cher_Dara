package com.worldofwonder.model;

public class UserStreak {

    private int id;
    private int userId;
    private int currentStreak;
    private int longestStreak;
    private String lastPlayDate;
    private int streakFreezes;

    public UserStreak() {
    }

    public UserStreak(int id, int userId, int currentStreak, int longestStreak,
                      String lastPlayDate, int streakFreezes) {
        this.id = id;
        this.userId = userId;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastPlayDate = lastPlayDate;
        this.streakFreezes = streakFreezes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public String getLastPlayDate() { return lastPlayDate; }
    public void setLastPlayDate(String lastPlayDate) { this.lastPlayDate = lastPlayDate; }

    public int getStreakFreezes() { return streakFreezes; }
    public void setStreakFreezes(int streakFreezes) { this.streakFreezes = streakFreezes; }
}
