package net.minecraft.world.level.dimension;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.LevelType;
import net.minecraft.core.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public abstract class Dimension {
    public static final float[] MOON_BRIGHTNESS_PER_PHASE;
    protected final Level level;
    private final DimensionType type;
    protected boolean ultraWarm;
    protected boolean hasCeiling;
    protected final float[] brightnessRamp;
    private final float[] sunriseCol;
    
    public Dimension(final Level bhr, final DimensionType byn) {
        this.brightnessRamp = new float[16];
        this.sunriseCol = new float[4];
        this.level = bhr;
        this.type = byn;
        this.updateLightRamp();
    }
    
    protected void updateLightRamp() {
        final float float2 = 0.0f;
        for (int integer3 = 0; integer3 <= 15; ++integer3) {
            final float float3 = 1.0f - integer3 / 15.0f;
            this.brightnessRamp[integer3] = (1.0f - float3) / (float3 * 3.0f + 1.0f) * 1.0f + 0.0f;
        }
    }
    
    public int getMoonPhase(final long long1) {
        return (int)(long1 / 24000L % 8L + 8L) % 8;
    }
    
    @Nullable
    public float[] getSunriseColor(final float float1, final float float2) {
        final float float3 = 0.4f;
        final float float4 = Mth.cos(float1 * 6.2831855f) - 0.0f;
        final float float5 = -0.0f;
        if (float4 >= -0.4f && float4 <= 0.4f) {
            final float float6 = (float4 + 0.0f) / 0.4f * 0.5f + 0.5f;
            float float7 = 1.0f - (1.0f - Mth.sin(float6 * 3.1415927f)) * 0.99f;
            float7 *= float7;
            this.sunriseCol[0] = float6 * 0.3f + 0.7f;
            this.sunriseCol[1] = float6 * float6 * 0.7f + 0.2f;
            this.sunriseCol[2] = float6 * float6 * 0.0f + 0.2f;
            this.sunriseCol[3] = float7;
            return this.sunriseCol;
        }
        return null;
    }
    
    public float getCloudHeight() {
        return 128.0f;
    }
    
    public boolean hasGround() {
        return true;
    }
    
    @Nullable
    public BlockPos getDimensionSpecificSpawn() {
        return null;
    }
    
    public double getClearColorScale() {
        if (this.level.getLevelData().getGeneratorType() == LevelType.FLAT) {
            return 1.0;
        }
        return 0.03125;
    }
    
    public boolean isUltraWarm() {
        return this.ultraWarm;
    }
    
    public boolean isHasSkyLight() {
        return this.type.hasSkyLight();
    }
    
    public boolean isHasCeiling() {
        return this.hasCeiling;
    }
    
    public float[] getBrightnessRamp() {
        return this.brightnessRamp;
    }
    
    public WorldBorder createWorldBorder() {
        return new WorldBorder();
    }
    
    public void saveData() {
    }
    
    public void tick() {
    }
    
    public abstract ChunkGenerator<?> createRandomLevelGenerator();
    
    @Nullable
    public abstract BlockPos getSpawnPosInChunk(final ChunkPos bhd, final boolean boolean2);
    
    @Nullable
    public abstract BlockPos getValidSpawnPosition(final int integer1, final int integer2, final boolean boolean3);
    
    public abstract float getTimeOfDay(final long long1, final float float2);
    
    public abstract boolean isNaturalDimension();
    
    public abstract Vec3 getFogColor(final float float1, final float float2);
    
    public abstract boolean mayRespawn();
    
    public abstract boolean isFoggyAt(final int integer1, final int integer2);
    
    public abstract DimensionType getType();
    
    static {
        MOON_BRIGHTNESS_PER_PHASE = new float[] { 1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f };
    }
}
