package net.minecraft.world.level.storage;

import net.minecraft.CrashReportCategory;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.timers.TimerQueue;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameType;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public class DerivedLevelData extends LevelData {
    private final LevelData wrapped;
    
    public DerivedLevelData(final LevelData com) {
        this.wrapped = com;
    }
    
    @Override
    public CompoundTag createTag(@Nullable final CompoundTag id) {
        return this.wrapped.createTag(id);
    }
    
    @Override
    public long getSeed() {
        return this.wrapped.getSeed();
    }
    
    @Override
    public int getXSpawn() {
        return this.wrapped.getXSpawn();
    }
    
    @Override
    public int getYSpawn() {
        return this.wrapped.getYSpawn();
    }
    
    @Override
    public int getZSpawn() {
        return this.wrapped.getZSpawn();
    }
    
    @Override
    public long getGameTime() {
        return this.wrapped.getGameTime();
    }
    
    @Override
    public long getDayTime() {
        return this.wrapped.getDayTime();
    }
    
    @Override
    public CompoundTag getLoadedPlayerTag() {
        return this.wrapped.getLoadedPlayerTag();
    }
    
    @Override
    public String getLevelName() {
        return this.wrapped.getLevelName();
    }
    
    @Override
    public int getVersion() {
        return this.wrapped.getVersion();
    }
    
    @Override
    public long getLastPlayed() {
        return this.wrapped.getLastPlayed();
    }
    
    @Override
    public boolean isThundering() {
        return this.wrapped.isThundering();
    }
    
    @Override
    public int getThunderTime() {
        return this.wrapped.getThunderTime();
    }
    
    @Override
    public boolean isRaining() {
        return this.wrapped.isRaining();
    }
    
    @Override
    public int getRainTime() {
        return this.wrapped.getRainTime();
    }
    
    @Override
    public GameType getGameType() {
        return this.wrapped.getGameType();
    }
    
    @Override
    public void setXSpawn(final int integer) {
    }
    
    @Override
    public void setYSpawn(final int integer) {
    }
    
    @Override
    public void setZSpawn(final int integer) {
    }
    
    @Override
    public void setGameTime(final long long1) {
    }
    
    @Override
    public void setDayTime(final long long1) {
    }
    
    @Override
    public void setSpawn(final BlockPos ew) {
    }
    
    @Override
    public void setLevelName(final String string) {
    }
    
    @Override
    public void setVersion(final int integer) {
    }
    
    @Override
    public void setThundering(final boolean boolean1) {
    }
    
    @Override
    public void setThunderTime(final int integer) {
    }
    
    @Override
    public void setRaining(final boolean boolean1) {
    }
    
    @Override
    public void setRainTime(final int integer) {
    }
    
    @Override
    public boolean isGenerateMapFeatures() {
        return this.wrapped.isGenerateMapFeatures();
    }
    
    @Override
    public boolean isHardcore() {
        return this.wrapped.isHardcore();
    }
    
    @Override
    public LevelType getGeneratorType() {
        return this.wrapped.getGeneratorType();
    }
    
    @Override
    public void setGenerator(final LevelType bhy) {
    }
    
    @Override
    public boolean getAllowCommands() {
        return this.wrapped.getAllowCommands();
    }
    
    @Override
    public void setAllowCommands(final boolean boolean1) {
    }
    
    @Override
    public boolean isInitialized() {
        return this.wrapped.isInitialized();
    }
    
    @Override
    public void setInitialized(final boolean boolean1) {
    }
    
    @Override
    public GameRules getGameRules() {
        return this.wrapped.getGameRules();
    }
    
    @Override
    public Difficulty getDifficulty() {
        return this.wrapped.getDifficulty();
    }
    
    @Override
    public void setDifficulty(final Difficulty ahg) {
    }
    
    @Override
    public boolean isDifficultyLocked() {
        return this.wrapped.isDifficultyLocked();
    }
    
    @Override
    public void setDifficultyLocked(final boolean boolean1) {
    }
    
    @Override
    public TimerQueue<MinecraftServer> getScheduledEvents() {
        return this.wrapped.getScheduledEvents();
    }
    
    @Override
    public void setDimensionData(final DimensionType byn, final CompoundTag id) {
        this.wrapped.setDimensionData(byn, id);
    }
    
    @Override
    public CompoundTag getDimensionData(final DimensionType byn) {
        return this.wrapped.getDimensionData(byn);
    }
    
    @Override
    public void fillCrashReportCategory(final CrashReportCategory e) {
        e.setDetail("Derived", true);
        this.wrapped.fillCrashReportCategory(e);
    }
}
