package net.minecraft.world.level.storage;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.GameType;

public class LevelSummary implements Comparable<LevelSummary> {
    private final String levelId;
    private final String levelName;
    private final long lastPlayed;
    private final long sizeOnDisk;
    private final boolean requiresConversion;
    private final GameType gameMode;
    private final boolean hardcore;
    private final boolean hasCheats;
    private final String worldVersionName;
    private final int worldVersion;
    private final boolean snapshot;
    private final LevelType generatorType;
    
    public LevelSummary(final LevelData com, final String string2, final String string3, final long long4, final boolean boolean5) {
        this.levelId = string2;
        this.levelName = string3;
        this.lastPlayed = com.getLastPlayed();
        this.sizeOnDisk = long4;
        this.gameMode = com.getGameType();
        this.requiresConversion = boolean5;
        this.hardcore = com.isHardcore();
        this.hasCheats = com.getAllowCommands();
        this.worldVersionName = com.getMinecraftVersionName();
        this.worldVersion = com.getMinecraftVersion();
        this.snapshot = com.isSnapshot();
        this.generatorType = com.getGeneratorType();
    }
    
    public String getLevelId() {
        return this.levelId;
    }
    
    public String getLevelName() {
        return this.levelName;
    }
    
    public long getSizeOnDisk() {
        return this.sizeOnDisk;
    }
    
    public boolean isRequiresConversion() {
        return this.requiresConversion;
    }
    
    public long getLastPlayed() {
        return this.lastPlayed;
    }
    
    public int compareTo(final LevelSummary cor) {
        if (this.lastPlayed < cor.lastPlayed) {
            return 1;
        }
        if (this.lastPlayed > cor.lastPlayed) {
            return -1;
        }
        return this.levelId.compareTo(cor.levelId);
    }
    
    public GameType getGameMode() {
        return this.gameMode;
    }
    
    public boolean isHardcore() {
        return this.hardcore;
    }
    
    public boolean hasCheats() {
        return this.hasCheats;
    }
    
    public Component getWorldVersionName() {
        if (StringUtil.isNullOrEmpty(this.worldVersionName)) {
            return new TranslatableComponent("selectWorld.versionUnknown", new Object[0]);
        }
        return new TextComponent(this.worldVersionName);
    }
    
    public boolean markVersionInList() {
        return this.askToOpenWorld() || (!SharedConstants.getCurrentVersion().isStable() && !this.snapshot) || this.shouldBackup() || this.isOldCustomizedWorld();
    }
    
    public boolean askToOpenWorld() {
        return this.worldVersion > SharedConstants.getCurrentVersion().getWorldVersion();
    }
    
    public boolean isOldCustomizedWorld() {
        return this.generatorType == LevelType.CUSTOMIZED && this.worldVersion < 1466;
    }
    
    public boolean shouldBackup() {
        return this.worldVersion < SharedConstants.getCurrentVersion().getWorldVersion();
    }
}
