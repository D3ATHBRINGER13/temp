package net.minecraft.world.level.storage;

import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.nbt.StringTag;
import net.minecraft.Util;
import net.minecraft.nbt.Tag;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.nbt.NbtOps;
import com.mojang.datafixers.types.JsonOps;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.nbt.ListTag;
import java.util.Iterator;
import net.minecraft.SharedConstants;
import net.minecraft.world.level.timers.TimerCallbacks;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.timers.TimerQueue;
import net.minecraft.world.level.GameRules;
import java.util.UUID;
import net.minecraft.world.level.dimension.DimensionType;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.level.GameType;
import com.mojang.datafixers.DataFixer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.Difficulty;

public class LevelData {
    private String minecraftVersionName;
    private int minecraftVersion;
    private boolean snapshot;
    public static final Difficulty DEFAULT_DIFFICULTY;
    private long seed;
    private LevelType generator;
    private CompoundTag generatorOptions;
    @Nullable
    private String legacyCustomOptions;
    private int xSpawn;
    private int ySpawn;
    private int zSpawn;
    private long gameTime;
    private long dayTime;
    private long lastPlayed;
    private long sizeOnDisk;
    @Nullable
    private final DataFixer fixerUpper;
    private final int playerDataVersion;
    private boolean upgradedPlayerTag;
    private CompoundTag loadedPlayerTag;
    private String levelName;
    private int version;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private GameType gameType;
    private boolean generateMapFeatures;
    private boolean hardcore;
    private boolean allowCommands;
    private boolean initialized;
    private Difficulty difficulty;
    private boolean difficultyLocked;
    private double borderX;
    private double borderZ;
    private double borderSize;
    private long borderSizeLerpTime;
    private double borderSizeLerpTarget;
    private double borderSafeZone;
    private double borderDamagePerBlock;
    private int borderWarningBlocks;
    private int borderWarningTime;
    private final Set<String> disabledDataPacks;
    private final Set<String> enabledDataPacks;
    private final Map<DimensionType, CompoundTag> dimensionData;
    private CompoundTag customBossEvents;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    private UUID wanderingTraderId;
    private final GameRules gameRules;
    private final TimerQueue<MinecraftServer> scheduledEvents;
    
    protected LevelData() {
        this.generator = LevelType.NORMAL;
        this.generatorOptions = new CompoundTag();
        this.borderSize = 6.0E7;
        this.borderSafeZone = 5.0;
        this.borderDamagePerBlock = 0.2;
        this.borderWarningBlocks = 5;
        this.borderWarningTime = 15;
        this.disabledDataPacks = (Set<String>)Sets.newHashSet();
        this.enabledDataPacks = (Set<String>)Sets.newLinkedHashSet();
        this.dimensionData = (Map<DimensionType, CompoundTag>)Maps.newIdentityHashMap();
        this.gameRules = new GameRules();
        this.scheduledEvents = new TimerQueue<MinecraftServer>(TimerCallbacks.SERVER_CALLBACKS);
        this.fixerUpper = null;
        this.playerDataVersion = SharedConstants.getCurrentVersion().getWorldVersion();
        this.setGeneratorOptions(new CompoundTag());
    }
    
    public LevelData(final CompoundTag id1, final DataFixer dataFixer, final int integer, @Nullable final CompoundTag id4) {
        this.generator = LevelType.NORMAL;
        this.generatorOptions = new CompoundTag();
        this.borderSize = 6.0E7;
        this.borderSafeZone = 5.0;
        this.borderDamagePerBlock = 0.2;
        this.borderWarningBlocks = 5;
        this.borderWarningTime = 15;
        this.disabledDataPacks = (Set<String>)Sets.newHashSet();
        this.enabledDataPacks = (Set<String>)Sets.newLinkedHashSet();
        this.dimensionData = (Map<DimensionType, CompoundTag>)Maps.newIdentityHashMap();
        this.gameRules = new GameRules();
        this.scheduledEvents = new TimerQueue<MinecraftServer>(TimerCallbacks.SERVER_CALLBACKS);
        this.fixerUpper = dataFixer;
        if (id1.contains("Version", 10)) {
            final CompoundTag id5 = id1.getCompound("Version");
            this.minecraftVersionName = id5.getString("Name");
            this.minecraftVersion = id5.getInt("Id");
            this.snapshot = id5.getBoolean("Snapshot");
        }
        this.seed = id1.getLong("RandomSeed");
        if (id1.contains("generatorName", 8)) {
            final String string6 = id1.getString("generatorName");
            this.generator = LevelType.getLevelType(string6);
            if (this.generator == null) {
                this.generator = LevelType.NORMAL;
            }
            else if (this.generator == LevelType.CUSTOMIZED) {
                this.legacyCustomOptions = id1.getString("generatorOptions");
            }
            else if (this.generator.hasReplacement()) {
                int integer2 = 0;
                if (id1.contains("generatorVersion", 99)) {
                    integer2 = id1.getInt("generatorVersion");
                }
                this.generator = this.generator.getReplacementForVersion(integer2);
            }
            this.setGeneratorOptions(id1.getCompound("generatorOptions"));
        }
        this.gameType = GameType.byId(id1.getInt("GameType"));
        if (id1.contains("legacy_custom_options", 8)) {
            this.legacyCustomOptions = id1.getString("legacy_custom_options");
        }
        if (id1.contains("MapFeatures", 99)) {
            this.generateMapFeatures = id1.getBoolean("MapFeatures");
        }
        else {
            this.generateMapFeatures = true;
        }
        this.xSpawn = id1.getInt("SpawnX");
        this.ySpawn = id1.getInt("SpawnY");
        this.zSpawn = id1.getInt("SpawnZ");
        this.gameTime = id1.getLong("Time");
        if (id1.contains("DayTime", 99)) {
            this.dayTime = id1.getLong("DayTime");
        }
        else {
            this.dayTime = this.gameTime;
        }
        this.lastPlayed = id1.getLong("LastPlayed");
        this.sizeOnDisk = id1.getLong("SizeOnDisk");
        this.levelName = id1.getString("LevelName");
        this.version = id1.getInt("version");
        this.clearWeatherTime = id1.getInt("clearWeatherTime");
        this.rainTime = id1.getInt("rainTime");
        this.raining = id1.getBoolean("raining");
        this.thunderTime = id1.getInt("thunderTime");
        this.thundering = id1.getBoolean("thundering");
        this.hardcore = id1.getBoolean("hardcore");
        if (id1.contains("initialized", 99)) {
            this.initialized = id1.getBoolean("initialized");
        }
        else {
            this.initialized = true;
        }
        if (id1.contains("allowCommands", 99)) {
            this.allowCommands = id1.getBoolean("allowCommands");
        }
        else {
            this.allowCommands = (this.gameType == GameType.CREATIVE);
        }
        this.playerDataVersion = integer;
        if (id4 != null) {
            this.loadedPlayerTag = id4;
        }
        if (id1.contains("GameRules", 10)) {
            this.gameRules.loadFromTag(id1.getCompound("GameRules"));
        }
        if (id1.contains("Difficulty", 99)) {
            this.difficulty = Difficulty.byId(id1.getByte("Difficulty"));
        }
        if (id1.contains("DifficultyLocked", 1)) {
            this.difficultyLocked = id1.getBoolean("DifficultyLocked");
        }
        if (id1.contains("BorderCenterX", 99)) {
            this.borderX = id1.getDouble("BorderCenterX");
        }
        if (id1.contains("BorderCenterZ", 99)) {
            this.borderZ = id1.getDouble("BorderCenterZ");
        }
        if (id1.contains("BorderSize", 99)) {
            this.borderSize = id1.getDouble("BorderSize");
        }
        if (id1.contains("BorderSizeLerpTime", 99)) {
            this.borderSizeLerpTime = id1.getLong("BorderSizeLerpTime");
        }
        if (id1.contains("BorderSizeLerpTarget", 99)) {
            this.borderSizeLerpTarget = id1.getDouble("BorderSizeLerpTarget");
        }
        if (id1.contains("BorderSafeZone", 99)) {
            this.borderSafeZone = id1.getDouble("BorderSafeZone");
        }
        if (id1.contains("BorderDamagePerBlock", 99)) {
            this.borderDamagePerBlock = id1.getDouble("BorderDamagePerBlock");
        }
        if (id1.contains("BorderWarningBlocks", 99)) {
            this.borderWarningBlocks = id1.getInt("BorderWarningBlocks");
        }
        if (id1.contains("BorderWarningTime", 99)) {
            this.borderWarningTime = id1.getInt("BorderWarningTime");
        }
        if (id1.contains("DimensionData", 10)) {
            final CompoundTag id5 = id1.getCompound("DimensionData");
            for (final String string7 : id5.getAllKeys()) {
                this.dimensionData.put(DimensionType.getById(Integer.parseInt(string7)), id5.getCompound(string7));
            }
        }
        if (id1.contains("DataPacks", 10)) {
            final CompoundTag id5 = id1.getCompound("DataPacks");
            final ListTag ik7 = id5.getList("Disabled", 8);
            for (int integer3 = 0; integer3 < ik7.size(); ++integer3) {
                this.disabledDataPacks.add(ik7.getString(integer3));
            }
            final ListTag ik8 = id5.getList("Enabled", 8);
            for (int integer4 = 0; integer4 < ik8.size(); ++integer4) {
                this.enabledDataPacks.add(ik8.getString(integer4));
            }
        }
        if (id1.contains("CustomBossEvents", 10)) {
            this.customBossEvents = id1.getCompound("CustomBossEvents");
        }
        if (id1.contains("ScheduledEvents", 9)) {
            this.scheduledEvents.load(id1.getList("ScheduledEvents", 10));
        }
        if (id1.contains("WanderingTraderSpawnDelay", 99)) {
            this.wanderingTraderSpawnDelay = id1.getInt("WanderingTraderSpawnDelay");
        }
        if (id1.contains("WanderingTraderSpawnChance", 99)) {
            this.wanderingTraderSpawnChance = id1.getInt("WanderingTraderSpawnChance");
        }
        if (id1.contains("WanderingTraderId", 8)) {
            this.wanderingTraderId = UUID.fromString(id1.getString("WanderingTraderId"));
        }
    }
    
    public LevelData(final LevelSettings bhv, final String string) {
        this.generator = LevelType.NORMAL;
        this.generatorOptions = new CompoundTag();
        this.borderSize = 6.0E7;
        this.borderSafeZone = 5.0;
        this.borderDamagePerBlock = 0.2;
        this.borderWarningBlocks = 5;
        this.borderWarningTime = 15;
        this.disabledDataPacks = (Set<String>)Sets.newHashSet();
        this.enabledDataPacks = (Set<String>)Sets.newLinkedHashSet();
        this.dimensionData = (Map<DimensionType, CompoundTag>)Maps.newIdentityHashMap();
        this.gameRules = new GameRules();
        this.scheduledEvents = new TimerQueue<MinecraftServer>(TimerCallbacks.SERVER_CALLBACKS);
        this.fixerUpper = null;
        this.playerDataVersion = SharedConstants.getCurrentVersion().getWorldVersion();
        this.setLevelSettings(bhv);
        this.levelName = string;
        this.difficulty = LevelData.DEFAULT_DIFFICULTY;
        this.initialized = false;
    }
    
    public void setLevelSettings(final LevelSettings bhv) {
        this.seed = bhv.getSeed();
        this.gameType = bhv.getGameType();
        this.generateMapFeatures = bhv.isGenerateMapFeatures();
        this.hardcore = bhv.isHardcore();
        this.generator = bhv.getLevelType();
        this.setGeneratorOptions((CompoundTag)Dynamic.convert((DynamicOps)JsonOps.INSTANCE, (DynamicOps)NbtOps.INSTANCE, bhv.getLevelTypeOptions()));
        this.allowCommands = bhv.getAllowCommands();
    }
    
    public CompoundTag createTag(@Nullable CompoundTag id) {
        this.updatePlayerTag();
        if (id == null) {
            id = this.loadedPlayerTag;
        }
        final CompoundTag id2 = new CompoundTag();
        this.setTagData(id2, id);
        return id2;
    }
    
    private void setTagData(final CompoundTag id1, final CompoundTag id2) {
        final CompoundTag id3 = new CompoundTag();
        id3.putString("Name", SharedConstants.getCurrentVersion().getName());
        id3.putInt("Id", SharedConstants.getCurrentVersion().getWorldVersion());
        id3.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
        id1.put("Version", (Tag)id3);
        id1.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        id1.putLong("RandomSeed", this.seed);
        id1.putString("generatorName", this.generator.getSerialization());
        id1.putInt("generatorVersion", this.generator.getVersion());
        if (!this.generatorOptions.isEmpty()) {
            id1.put("generatorOptions", (Tag)this.generatorOptions);
        }
        if (this.legacyCustomOptions != null) {
            id1.putString("legacy_custom_options", this.legacyCustomOptions);
        }
        id1.putInt("GameType", this.gameType.getId());
        id1.putBoolean("MapFeatures", this.generateMapFeatures);
        id1.putInt("SpawnX", this.xSpawn);
        id1.putInt("SpawnY", this.ySpawn);
        id1.putInt("SpawnZ", this.zSpawn);
        id1.putLong("Time", this.gameTime);
        id1.putLong("DayTime", this.dayTime);
        id1.putLong("SizeOnDisk", this.sizeOnDisk);
        id1.putLong("LastPlayed", Util.getEpochMillis());
        id1.putString("LevelName", this.levelName);
        id1.putInt("version", this.version);
        id1.putInt("clearWeatherTime", this.clearWeatherTime);
        id1.putInt("rainTime", this.rainTime);
        id1.putBoolean("raining", this.raining);
        id1.putInt("thunderTime", this.thunderTime);
        id1.putBoolean("thundering", this.thundering);
        id1.putBoolean("hardcore", this.hardcore);
        id1.putBoolean("allowCommands", this.allowCommands);
        id1.putBoolean("initialized", this.initialized);
        id1.putDouble("BorderCenterX", this.borderX);
        id1.putDouble("BorderCenterZ", this.borderZ);
        id1.putDouble("BorderSize", this.borderSize);
        id1.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
        id1.putDouble("BorderSafeZone", this.borderSafeZone);
        id1.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
        id1.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
        id1.putDouble("BorderWarningBlocks", (double)this.borderWarningBlocks);
        id1.putDouble("BorderWarningTime", (double)this.borderWarningTime);
        if (this.difficulty != null) {
            id1.putByte("Difficulty", (byte)this.difficulty.getId());
        }
        id1.putBoolean("DifficultyLocked", this.difficultyLocked);
        id1.put("GameRules", (Tag)this.gameRules.createTag());
        final CompoundTag id4 = new CompoundTag();
        for (final Map.Entry<DimensionType, CompoundTag> entry7 : this.dimensionData.entrySet()) {
            id4.put(String.valueOf(((DimensionType)entry7.getKey()).getId()), (Tag)entry7.getValue());
        }
        id1.put("DimensionData", (Tag)id4);
        if (id2 != null) {
            id1.put("Player", (Tag)id2);
        }
        final CompoundTag id5 = new CompoundTag();
        final ListTag ik7 = new ListTag();
        for (final String string9 : this.enabledDataPacks) {
            ik7.add(new StringTag(string9));
        }
        id5.put("Enabled", (Tag)ik7);
        final ListTag ik8 = new ListTag();
        for (final String string10 : this.disabledDataPacks) {
            ik8.add(new StringTag(string10));
        }
        id5.put("Disabled", (Tag)ik8);
        id1.put("DataPacks", (Tag)id5);
        if (this.customBossEvents != null) {
            id1.put("CustomBossEvents", (Tag)this.customBossEvents);
        }
        id1.put("ScheduledEvents", (Tag)this.scheduledEvents.store());
        id1.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        id1.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        if (this.wanderingTraderId != null) {
            id1.putString("WanderingTraderId", this.wanderingTraderId.toString());
        }
    }
    
    public long getSeed() {
        return this.seed;
    }
    
    public int getXSpawn() {
        return this.xSpawn;
    }
    
    public int getYSpawn() {
        return this.ySpawn;
    }
    
    public int getZSpawn() {
        return this.zSpawn;
    }
    
    public long getGameTime() {
        return this.gameTime;
    }
    
    public long getDayTime() {
        return this.dayTime;
    }
    
    private void updatePlayerTag() {
        if (this.upgradedPlayerTag || this.loadedPlayerTag == null) {
            return;
        }
        if (this.playerDataVersion < SharedConstants.getCurrentVersion().getWorldVersion()) {
            if (this.fixerUpper == null) {
                throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
            }
            this.loadedPlayerTag = NbtUtils.update(this.fixerUpper, DataFixTypes.PLAYER, this.loadedPlayerTag, this.playerDataVersion);
        }
        this.upgradedPlayerTag = true;
    }
    
    public CompoundTag getLoadedPlayerTag() {
        this.updatePlayerTag();
        return this.loadedPlayerTag;
    }
    
    public void setXSpawn(final int integer) {
        this.xSpawn = integer;
    }
    
    public void setYSpawn(final int integer) {
        this.ySpawn = integer;
    }
    
    public void setZSpawn(final int integer) {
        this.zSpawn = integer;
    }
    
    public void setGameTime(final long long1) {
        this.gameTime = long1;
    }
    
    public void setDayTime(final long long1) {
        this.dayTime = long1;
    }
    
    public void setSpawn(final BlockPos ew) {
        this.xSpawn = ew.getX();
        this.ySpawn = ew.getY();
        this.zSpawn = ew.getZ();
    }
    
    public String getLevelName() {
        return this.levelName;
    }
    
    public void setLevelName(final String string) {
        this.levelName = string;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int integer) {
        this.version = integer;
    }
    
    public long getLastPlayed() {
        return this.lastPlayed;
    }
    
    public int getClearWeatherTime() {
        return this.clearWeatherTime;
    }
    
    public void setClearWeatherTime(final int integer) {
        this.clearWeatherTime = integer;
    }
    
    public boolean isThundering() {
        return this.thundering;
    }
    
    public void setThundering(final boolean boolean1) {
        this.thundering = boolean1;
    }
    
    public int getThunderTime() {
        return this.thunderTime;
    }
    
    public void setThunderTime(final int integer) {
        this.thunderTime = integer;
    }
    
    public boolean isRaining() {
        return this.raining;
    }
    
    public void setRaining(final boolean boolean1) {
        this.raining = boolean1;
    }
    
    public int getRainTime() {
        return this.rainTime;
    }
    
    public void setRainTime(final int integer) {
        this.rainTime = integer;
    }
    
    public GameType getGameType() {
        return this.gameType;
    }
    
    public boolean isGenerateMapFeatures() {
        return this.generateMapFeatures;
    }
    
    public void setGenerateMapFeatures(final boolean boolean1) {
        this.generateMapFeatures = boolean1;
    }
    
    public void setGameType(final GameType bho) {
        this.gameType = bho;
    }
    
    public boolean isHardcore() {
        return this.hardcore;
    }
    
    public void setHardcore(final boolean boolean1) {
        this.hardcore = boolean1;
    }
    
    public LevelType getGeneratorType() {
        return this.generator;
    }
    
    public void setGenerator(final LevelType bhy) {
        this.generator = bhy;
    }
    
    public CompoundTag getGeneratorOptions() {
        return this.generatorOptions;
    }
    
    public void setGeneratorOptions(final CompoundTag id) {
        this.generatorOptions = id;
    }
    
    public boolean getAllowCommands() {
        return this.allowCommands;
    }
    
    public void setAllowCommands(final boolean boolean1) {
        this.allowCommands = boolean1;
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }
    
    public void setInitialized(final boolean boolean1) {
        this.initialized = boolean1;
    }
    
    public GameRules getGameRules() {
        return this.gameRules;
    }
    
    public double getBorderX() {
        return this.borderX;
    }
    
    public double getBorderZ() {
        return this.borderZ;
    }
    
    public double getBorderSize() {
        return this.borderSize;
    }
    
    public void setBorderSize(final double double1) {
        this.borderSize = double1;
    }
    
    public long getBorderSizeLerpTime() {
        return this.borderSizeLerpTime;
    }
    
    public void setBorderSizeLerpTime(final long long1) {
        this.borderSizeLerpTime = long1;
    }
    
    public double getBorderSizeLerpTarget() {
        return this.borderSizeLerpTarget;
    }
    
    public void setBorderSizeLerpTarget(final double double1) {
        this.borderSizeLerpTarget = double1;
    }
    
    public void setBorderZ(final double double1) {
        this.borderZ = double1;
    }
    
    public void setBorderX(final double double1) {
        this.borderX = double1;
    }
    
    public double getBorderSafeZone() {
        return this.borderSafeZone;
    }
    
    public void setBorderSafeZone(final double double1) {
        this.borderSafeZone = double1;
    }
    
    public double getBorderDamagePerBlock() {
        return this.borderDamagePerBlock;
    }
    
    public void setBorderDamagePerBlock(final double double1) {
        this.borderDamagePerBlock = double1;
    }
    
    public int getBorderWarningBlocks() {
        return this.borderWarningBlocks;
    }
    
    public int getBorderWarningTime() {
        return this.borderWarningTime;
    }
    
    public void setBorderWarningBlocks(final int integer) {
        this.borderWarningBlocks = integer;
    }
    
    public void setBorderWarningTime(final int integer) {
        this.borderWarningTime = integer;
    }
    
    public Difficulty getDifficulty() {
        return this.difficulty;
    }
    
    public void setDifficulty(final Difficulty ahg) {
        this.difficulty = ahg;
    }
    
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }
    
    public void setDifficultyLocked(final boolean boolean1) {
        this.difficultyLocked = boolean1;
    }
    
    public TimerQueue<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }
    
    public void fillCrashReportCategory(final CrashReportCategory e) {
        e.setDetail("Level name", (CrashReportDetail<String>)(() -> this.levelName));
        e.setDetail("Level seed", (CrashReportDetail<String>)(() -> String.valueOf(this.seed)));
        e.setDetail("Level generator", (CrashReportDetail<String>)(() -> String.format("ID %02d - %s, ver %d. Features enabled: %b", new Object[] { this.generator.getId(), this.generator.getName(), this.generator.getVersion(), this.generateMapFeatures })));
        e.setDetail("Level generator options", (CrashReportDetail<String>)(() -> this.generatorOptions.toString()));
        e.setDetail("Level spawn location", (CrashReportDetail<String>)(() -> CrashReportCategory.formatLocation(this.xSpawn, this.ySpawn, this.zSpawn)));
        e.setDetail("Level time", (CrashReportDetail<String>)(() -> String.format("%d game time, %d day time", new Object[] { this.gameTime, this.dayTime })));
        e.setDetail("Level storage version", (CrashReportDetail<String>)(() -> {
            String string2 = "Unknown?";
            try {
                switch (this.version) {
                    case 19133: {
                        string2 = "Anvil";
                        break;
                    }
                    case 19132: {
                        string2 = "McRegion";
                        break;
                    }
                }
            }
            catch (Throwable t) {}
            return String.format("0x%05X - %s", new Object[] { this.version, string2 });
        }));
        e.setDetail("Level weather", (CrashReportDetail<String>)(() -> String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", new Object[] { this.rainTime, this.raining, this.thunderTime, this.thundering })));
        e.setDetail("Level game mode", (CrashReportDetail<String>)(() -> String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", new Object[] { this.gameType.getName(), this.gameType.getId(), this.hardcore, this.allowCommands })));
    }
    
    public CompoundTag getDimensionData(final DimensionType byn) {
        final CompoundTag id3 = (CompoundTag)this.dimensionData.get(byn);
        if (id3 == null) {
            return new CompoundTag();
        }
        return id3;
    }
    
    public void setDimensionData(final DimensionType byn, final CompoundTag id) {
        this.dimensionData.put(byn, id);
    }
    
    public int getMinecraftVersion() {
        return this.minecraftVersion;
    }
    
    public boolean isSnapshot() {
        return this.snapshot;
    }
    
    public String getMinecraftVersionName() {
        return this.minecraftVersionName;
    }
    
    public Set<String> getDisabledDataPacks() {
        return this.disabledDataPacks;
    }
    
    public Set<String> getEnabledDataPacks() {
        return this.enabledDataPacks;
    }
    
    @Nullable
    public CompoundTag getCustomBossEvents() {
        return this.customBossEvents;
    }
    
    public void setCustomBossEvents(@Nullable final CompoundTag id) {
        this.customBossEvents = id;
    }
    
    public int getWanderingTraderSpawnDelay() {
        return this.wanderingTraderSpawnDelay;
    }
    
    public void setWanderingTraderSpawnDelay(final int integer) {
        this.wanderingTraderSpawnDelay = integer;
    }
    
    public int getWanderingTraderSpawnChance() {
        return this.wanderingTraderSpawnChance;
    }
    
    public void setWanderingTraderSpawnChance(final int integer) {
        this.wanderingTraderSpawnChance = integer;
    }
    
    public void setWanderingTraderId(final UUID uUID) {
        this.wanderingTraderId = uUID;
    }
    
    static {
        DEFAULT_DIFFICULTY = Difficulty.NORMAL;
    }
}
