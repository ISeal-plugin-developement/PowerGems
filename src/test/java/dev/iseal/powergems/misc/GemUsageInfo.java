package dev.iseal.powergems.misc;

public class GemUsageInfo {

    private String gemName;
    private int level;

    public GemUsageInfo(String gemName, int level) {
        this.gemName = gemName;
        this.level = level;
    }

    public String getName() {
        return gemName;
    }

    public int getLevel() {
        return level;
    }
}
