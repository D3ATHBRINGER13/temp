package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;
import java.util.Random;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;

public class RemoveBlockGoal extends MoveToBlockGoal {
    private final Block blockToRemove;
    private final Mob removerMob;
    private int ticksSinceReachedGoal;
    
    public RemoveBlockGoal(final Block bmv, final PathfinderMob aje, final double double3, final int integer) {
        super(aje, double3, 24, integer);
        this.blockToRemove = bmv;
        this.removerMob = aje;
    }
    
    @Override
    public boolean canUse() {
        if (!this.removerMob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        }
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        }
        if (this.tryFindBlock()) {
            this.nextStartTick = 20;
            return true;
        }
        this.nextStartTick = this.nextStartTick(this.mob);
        return false;
    }
    
    private boolean tryFindBlock() {
        return (this.blockPos != null && this.isValidTarget(this.mob.level, this.blockPos)) || this.findNearestBlock();
    }
    
    @Override
    public void stop() {
        super.stop();
        this.removerMob.fallDistance = 1.0f;
    }
    
    @Override
    public void start() {
        super.start();
        this.ticksSinceReachedGoal = 0;
    }
    
    public void playDestroyProgressSound(final LevelAccessor bhs, final BlockPos ew) {
    }
    
    public void playBreakSound(final Level bhr, final BlockPos ew) {
    }
    
    @Override
    public void tick() {
        super.tick();
        final Level bhr2 = this.removerMob.level;
        final BlockPos ew3 = new BlockPos(this.removerMob);
        final BlockPos ew4 = this.getPosWithBlock(ew3, bhr2);
        final Random random5 = this.removerMob.getRandom();
        if (this.isReachedTarget() && ew4 != null) {
            if (this.ticksSinceReachedGoal > 0) {
                final Vec3 csi6 = this.removerMob.getDeltaMovement();
                this.removerMob.setDeltaMovement(csi6.x, 0.3, csi6.z);
                if (!bhr2.isClientSide) {
                    final double double7 = 0.08;
                    ((ServerLevel)bhr2).<ItemParticleOption>sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.EGG)), ew4.getX() + 0.5, ew4.getY() + 0.7, ew4.getZ() + 0.5, 3, (random5.nextFloat() - 0.5) * 0.08, (random5.nextFloat() - 0.5) * 0.08, (random5.nextFloat() - 0.5) * 0.08, 0.15000000596046448);
                }
            }
            if (this.ticksSinceReachedGoal % 2 == 0) {
                final Vec3 csi6 = this.removerMob.getDeltaMovement();
                this.removerMob.setDeltaMovement(csi6.x, -0.3, csi6.z);
                if (this.ticksSinceReachedGoal % 6 == 0) {
                    this.playDestroyProgressSound(bhr2, this.blockPos);
                }
            }
            if (this.ticksSinceReachedGoal > 60) {
                bhr2.removeBlock(ew4, false);
                if (!bhr2.isClientSide) {
                    for (int integer6 = 0; integer6 < 20; ++integer6) {
                        final double double7 = random5.nextGaussian() * 0.02;
                        final double double8 = random5.nextGaussian() * 0.02;
                        final double double9 = random5.nextGaussian() * 0.02;
                        ((ServerLevel)bhr2).<SimpleParticleType>sendParticles(ParticleTypes.POOF, ew4.getX() + 0.5, ew4.getY(), ew4.getZ() + 0.5, 1, double7, double8, double9, 0.15000000596046448);
                    }
                    this.playBreakSound(bhr2, ew4);
                }
            }
            ++this.ticksSinceReachedGoal;
        }
    }
    
    @Nullable
    private BlockPos getPosWithBlock(final BlockPos ew, final BlockGetter bhb) {
        if (bhb.getBlockState(ew).getBlock() == this.blockToRemove) {
            return ew;
        }
        final BlockPos[] array;
        final BlockPos[] arr4 = array = new BlockPos[] { ew.below(), ew.west(), ew.east(), ew.north(), ew.south(), ew.below().below() };
        for (final BlockPos ew2 : array) {
            if (bhb.getBlockState(ew2).getBlock() == this.blockToRemove) {
                return ew2;
            }
        }
        return null;
    }
    
    @Override
    protected boolean isValidTarget(final LevelReader bhu, final BlockPos ew) {
        final ChunkAccess bxh4 = bhu.getChunk(ew.getX() >> 4, ew.getZ() >> 4, ChunkStatus.FULL, false);
        return bxh4 != null && bxh4.getBlockState(ew).getBlock() == this.blockToRemove && bxh4.getBlockState(ew.above()).isAir() && bxh4.getBlockState(ew.above(2)).isAir();
    }
}
