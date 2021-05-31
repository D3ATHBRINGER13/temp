package net.minecraft.world.level.dimension.end;

import net.minecraft.nbt.Tag;
import java.util.Random;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSourceSettings;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.levelgen.TheEndGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.Dimension;

public class TheEndDimension extends Dimension {
    public static final BlockPos END_SPAWN_POINT;
    private final EndDragonFight dragonFight;
    
    public TheEndDimension(final Level bhr, final DimensionType byn) {
        super(bhr, byn);
        final CompoundTag id4 = bhr.getLevelData().getDimensionData(DimensionType.THE_END);
        this.dragonFight = ((bhr instanceof ServerLevel) ? new EndDragonFight((ServerLevel)bhr, id4.getCompound("DragonFight")) : null);
    }
    
    @Override
    public ChunkGenerator<?> createRandomLevelGenerator() {
        final TheEndGeneratorSettings bzi2 = ChunkGeneratorType.FLOATING_ISLANDS.createSettings();
        bzi2.setDefaultBlock(Blocks.END_STONE.defaultBlockState());
        bzi2.setDefaultFluid(Blocks.AIR.defaultBlockState());
        bzi2.setSpawnPosition(this.getDimensionSpecificSpawn());
        return ChunkGeneratorType.FLOATING_ISLANDS.create(this.level, BiomeSourceType.THE_END.create(BiomeSourceType.THE_END.createSettings().setSeed(this.level.getSeed())), bzi2);
    }
    
    @Override
    public float getTimeOfDay(final long long1, final float float2) {
        return 0.0f;
    }
    
    @Nullable
    @Override
    public float[] getSunriseColor(final float float1, final float float2) {
        return null;
    }
    
    @Override
    public Vec3 getFogColor(final float float1, final float float2) {
        final int integer4 = 10518688;
        float float3 = Mth.cos(float1 * 6.2831855f) * 2.0f + 0.5f;
        float3 = Mth.clamp(float3, 0.0f, 1.0f);
        float float4 = 0.627451f;
        float float5 = 0.5019608f;
        float float6 = 0.627451f;
        float4 *= float3 * 0.0f + 0.15f;
        float5 *= float3 * 0.0f + 0.15f;
        float6 *= float3 * 0.0f + 0.15f;
        return new Vec3(float4, float5, float6);
    }
    
    @Override
    public boolean hasGround() {
        return false;
    }
    
    @Override
    public boolean mayRespawn() {
        return false;
    }
    
    @Override
    public boolean isNaturalDimension() {
        return false;
    }
    
    @Override
    public float getCloudHeight() {
        return 8.0f;
    }
    
    @Nullable
    @Override
    public BlockPos getSpawnPosInChunk(final ChunkPos bhd, final boolean boolean2) {
        final Random random4 = new Random(this.level.getSeed());
        final BlockPos ew5 = new BlockPos(bhd.getMinBlockX() + random4.nextInt(15), 0, bhd.getMaxBlockZ() + random4.nextInt(15));
        return this.level.getTopBlockState(ew5).getMaterial().blocksMotion() ? ew5 : null;
    }
    
    @Override
    public BlockPos getDimensionSpecificSpawn() {
        return TheEndDimension.END_SPAWN_POINT;
    }
    
    @Nullable
    @Override
    public BlockPos getValidSpawnPosition(final int integer1, final int integer2, final boolean boolean3) {
        return this.getSpawnPosInChunk(new ChunkPos(integer1 >> 4, integer2 >> 4), boolean3);
    }
    
    @Override
    public boolean isFoggyAt(final int integer1, final int integer2) {
        return false;
    }
    
    @Override
    public DimensionType getType() {
        return DimensionType.THE_END;
    }
    
    @Override
    public void saveData() {
        final CompoundTag id2 = new CompoundTag();
        if (this.dragonFight != null) {
            id2.put("DragonFight", (Tag)this.dragonFight.saveData());
        }
        this.level.getLevelData().setDimensionData(DimensionType.THE_END, id2);
    }
    
    @Override
    public void tick() {
        if (this.dragonFight != null) {
            this.dragonFight.tick();
        }
    }
    
    @Nullable
    public EndDragonFight getDragonFight() {
        return this.dragonFight;
    }
    
    static {
        END_SPAWN_POINT = new BlockPos(100, 50, 0);
    }
}
