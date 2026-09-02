package com.worldofwonder.model;

public class GameSession {

    private int id;
    private int userId;
    private String gameType;
    private int pointsEarned;
    private boolean completed;
    private int durationSeconds;
    private int starRating;
    private String playedAt;

    public GameSession() {
    }

    public GameSession(int id, int userId, String gameType, int pointsEarned,
                       boolean completed, int durationSeconds, int starRating, String playedAt) {
        this.id = id;
        this.userId = userId;
        this.gameType = gameType;
        this.pointsEarned = pointsEarned;
        this.completed = completed;
        this.durationSeconds = durationSeconds;
        this.starRating = starRating;
        this.playedAt = playedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getGameType() { return gameType; }
    public void setGameType(String gameType) { this.gameType = gameType; }

    public int getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public int getStarRating() { return starRating; }
    public void setStarRating(int starRating) { this.starRating = starRating; }

    public String getPlayedAt() { return playedAt; }
    public void setPlayedAt(String playedAt) { this.playedAt = playedAt; }
}
