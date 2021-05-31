package net.minecraft.realms;

import net.minecraft.world.level.storage.LevelSummary;

public class RealmsLevelSummary implements Comparable<RealmsLevelSummary> {
    private final LevelSummary levelSummary;
    
    public RealmsLevelSummary(final LevelSummary cor) {
        this.levelSummary = cor;
    }
    
    public int getGameMode() {
        return this.levelSummary.getGameMode().getId();
    }
    
    public String getLevelId() {
        return this.levelSummary.getLevelId();
    }
    
    public boolean hasCheats() {
        return this.levelSummary.hasCheats();
    }
    
    public boolean isHardcore() {
        return this.levelSummary.isHardcore();
    }
    
    public boolean isRequiresConversion() {
        return this.levelSummary.isRequiresConversion();
    }
    
    public String getLevelName() {
        return this.levelSummary.getLevelName();
    }
    
    public long getLastPlayed() {
        return this.levelSummary.getLastPlayed();
    }
    
    public int compareTo(final LevelSummary cor) {
        return this.levelSummary.compareTo(cor);
    }
    
    public long getSizeOnDisk() {
        return this.levelSummary.getSizeOnDisk();
    }
    
    public int compareTo(final RealmsLevelSummary realmsLevelSummary) {
        if (this.levelSummary.getLastPlayed() < realmsLevelSummary.getLastPlayed()) {
            return 1;
        }
        if (this.levelSummary.getLastPlayed() > realmsLevelSummary.getLastPlayed()) {
            return -1;
        }
        return this.levelSummary.getLevelId().compareTo(realmsLevelSummary.getLevelId());
    }
}
