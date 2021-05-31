package net.minecraft.world.level.dimension;

import net.minecraft.world.level.border.WorldBorder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSourceSettings;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.levelgen.NetherGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class NetherDimension extends Dimension {
    public NetherDimension(final Level bhr, final DimensionType byn) {
        super(bhr, byn);
        this.ultraWarm = true;
        this.hasCeiling = true;
    }
    
    @Override
    public Vec3 getFogColor(final float float1, final float float2) {
        return new Vec3(0.20000000298023224, 0.029999999329447746, 0.029999999329447746);
    }
    
    @Override
    protected void updateLightRamp() {
        final float float2 = 0.1f;
        for (int integer3 = 0; integer3 <= 15; ++integer3) {
            final float float3 = 1.0f - integer3 / 15.0f;
            this.brightnessRamp[integer3] = (1.0f - float3) / (float3 * 3.0f + 1.0f) * 0.9f + 0.1f;
        }
    }
    
    @Override
    public ChunkGenerator<?> createRandomLevelGenerator() {
        final NetherGeneratorSettings bzb2 = ChunkGeneratorType.CAVES.createSettings();
        bzb2.setDefaultBlock(Blocks.NETHERRACK.defaultBlockState());
        bzb2.setDefaultFluid(Blocks.LAVA.defaultBlockState());
        return ChunkGeneratorType.CAVES.create(this.level, BiomeSourceType.FIXED.create(BiomeSourceType.FIXED.createSettings().setBiome(Biomes.NETHER)), bzb2);
    }
    
    @Override
    public boolean isNaturalDimension() {
        return false;
    }
    
    @Nullable
    @Override
    public BlockPos getSpawnPosInChunk(final ChunkPos bhd, final boolean boolean2) {
        return null;
    }
    
    @Nullable
    @Override
    public BlockPos getValidSpawnPosition(final int integer1, final int integer2, final boolean boolean3) {
        return null;
    }
    
    @Override
    public float getTimeOfDay(final long long1, final float float2) {
        return 0.5f;
    }
    
    @Override
    public boolean mayRespawn() {
        return false;
    }
    
    @Override
    public boolean isFoggyAt(final int integer1, final int integer2) {
        return true;
    }
    
    @Override
    public WorldBorder createWorldBorder() {
        return new WorldBorder() {
            @Override
            public double getCenterX() {
                return super.getCenterX() / 8.0;
            }
            
            @Override
            public double getCenterZ() {
                return super.getCenterZ() / 8.0;
            }
        };
    }
    
    @Override
    public DimensionType getType() {
        return DimensionType.NETHER;
    }
}
