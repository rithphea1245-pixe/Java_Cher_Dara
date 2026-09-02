package com.worldofwonder.model;

public class Achievement {

    private int id;
    private String key;
    private String name;
    private String description;
    private String icon;
    private String conditionType;
    private int conditionValue;

    public Achievement() {
    }

    public Achievement(int id, String key, String name, String description,
                       String icon, String conditionType, int conditionValue) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.conditionType = conditionType;
        this.conditionValue = conditionValue;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getConditionType() { return conditionType; }
    public void setConditionType(String conditionType) { this.conditionType = conditionType; }

    public int getConditionValue() { return conditionValue; }
    public void setConditionValue(int conditionValue) { this.conditionValue = conditionValue; }
}
