package net.minecraft.world.level;

import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.stream.Stream;
import java.util.Set;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.Dimension;

public interface LevelAccessor extends EntityGetter, LevelReader, LevelSimulatedRW {
    long getSeed();
    
    default float getMoonBrightness() {
        return Dimension.MOON_BRIGHTNESS_PER_PHASE[this.getDimension().getMoonPhase(this.getLevelData().getDayTime())];
    }
    
    default float getTimeOfDay(final float float1) {
        return this.getDimension().getTimeOfDay(this.getLevelData().getDayTime(), float1);
    }
    
    default int getMoonPhase() {
        return this.getDimension().getMoonPhase(this.getLevelData().getDayTime());
    }
    
    TickList<Block> getBlockTicks();
    
    TickList<Fluid> getLiquidTicks();
    
    Level getLevel();
    
    LevelData getLevelData();
    
    DifficultyInstance getCurrentDifficultyAt(final BlockPos ew);
    
    default Difficulty getDifficulty() {
        return this.getLevelData().getDifficulty();
    }
    
    ChunkSource getChunkSource();
    
    default boolean hasChunk(final int integer1, final int integer2) {
        return this.getChunkSource().hasChunk(integer1, integer2);
    }
    
    Random getRandom();
    
    void blockUpdated(final BlockPos ew, final Block bmv);
    
    BlockPos getSharedSpawnPos();
    
    void playSound(@Nullable final Player awg, final BlockPos ew, final SoundEvent yo, final SoundSource yq, final float float5, final float float6);
    
    void addParticle(final ParticleOptions gf, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7);
    
    void levelEvent(@Nullable final Player awg, final int integer2, final BlockPos ew, final int integer4);
    
    default void levelEvent(final int integer1, final BlockPos ew, final int integer3) {
        this.levelEvent(null, integer1, ew, integer3);
    }
    
    default Stream<VoxelShape> getEntityCollisions(@Nullable final Entity aio, final AABB csc, final Set<Entity> set) {
        return super.getEntityCollisions(aio, csc, set);
    }
    
    default boolean isUnobstructed(@Nullable final Entity aio, final VoxelShape ctc) {
        return super.isUnobstructed(aio, ctc);
    }
}
