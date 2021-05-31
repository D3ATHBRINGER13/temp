package net.minecraft.world.level;

import net.minecraft.world.level.storage.LevelData;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

public final class LevelSettings {
    private final long seed;
    private final GameType gameType;
    private final boolean generateMapFeatures;
    private final boolean hardcore;
    private final LevelType levelType;
    private boolean allowCommands;
    private boolean startingBonusItems;
    private JsonElement levelTypeOptions;
    
    public LevelSettings(final long long1, final GameType bho, final boolean boolean3, final boolean boolean4, final LevelType bhy) {
        this.levelTypeOptions = (JsonElement)new JsonObject();
        this.seed = long1;
        this.gameType = bho;
        this.generateMapFeatures = boolean3;
        this.hardcore = boolean4;
        this.levelType = bhy;
    }
    
    public LevelSettings(final LevelData com) {
        this(com.getSeed(), com.getGameType(), com.isGenerateMapFeatures(), com.isHardcore(), com.getGeneratorType());
    }
    
    public LevelSettings enableStartingBonusItems() {
        this.startingBonusItems = true;
        return this;
    }
    
    public LevelSettings enableSinglePlayerCommands() {
        this.allowCommands = true;
        return this;
    }
    
    public LevelSettings setLevelTypeOptions(final JsonElement jsonElement) {
        this.levelTypeOptions = jsonElement;
        return this;
    }
    
    public boolean hasStartingBonusItems() {
        return this.startingBonusItems;
    }
    
    public long getSeed() {
        return this.seed;
    }
    
    public GameType getGameType() {
        return this.gameType;
    }
    
    public boolean isHardcore() {
        return this.hardcore;
    }
    
    public boolean isGenerateMapFeatures() {
        return this.generateMapFeatures;
    }
    
    public LevelType getLevelType() {
        return this.levelType;
    }
    
    public boolean getAllowCommands() {
        return this.allowCommands;
    }
    
    public JsonElement getLevelTypeOptions() {
        return this.levelTypeOptions;
    }
}
