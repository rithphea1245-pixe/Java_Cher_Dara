package com.worldofwonder.model;

public class DailyChallenge {

    private int id;
    private String challengeDate;
    private String gameType;
    private String configJson;
    private int bonusMultiplier;

    public DailyChallenge() {
    }

    public DailyChallenge(int id, String challengeDate, String gameType,
                          String configJson, int bonusMultiplier) {
        this.id = id;
        this.challengeDate = challengeDate;
        this.gameType = gameType;
        this.configJson = configJson;
        this.bonusMultiplier = bonusMultiplier;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getChallengeDate() { return challengeDate; }
    public void setChallengeDate(String challengeDate) { this.challengeDate = challengeDate; }

    public String getGameType() { return gameType; }
    public void setGameType(String gameType) { this.gameType = gameType; }

    public String getConfigJson() { return configJson; }
    public void setConfigJson(String configJson) { this.configJson = configJson; }

    public int getBonusMultiplier() { return bonusMultiplier; }
    public void setBonusMultiplier(int bonusMultiplier) { this.bonusMultiplier = bonusMultiplier; }
}
