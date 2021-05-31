package net.minecraft.world.entity;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public abstract class PathfinderMob extends Mob {
    protected PathfinderMob(final EntityType<? extends PathfinderMob> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public float getWalkTargetValue(final BlockPos ew) {
        return this.getWalkTargetValue(ew, this.level);
    }
    
    public float getWalkTargetValue(final BlockPos ew, final LevelReader bhu) {
        return 0.0f;
    }
    
    @Override
    public boolean checkSpawnRules(final LevelAccessor bhs, final MobSpawnType aja) {
        return this.getWalkTargetValue(new BlockPos(this.x, this.getBoundingBox().minY, this.z), bhs) >= 0.0f;
    }
    
    public boolean isPathFinding() {
        return !this.getNavigation().isDone();
    }
    
    @Override
    protected void tickLeash() {
        super.tickLeash();
        final Entity aio2 = this.getLeashHolder();
        if (aio2 != null && aio2.level == this.level) {
            this.restrictTo(new BlockPos(aio2), 5);
            final float float3 = this.distanceTo(aio2);
            if (this instanceof TamableAnimal && ((TamableAnimal)this).isSitting()) {
                if (float3 > 10.0f) {
                    this.dropLeash(true, true);
                }
                return;
            }
            this.onLeashDistance(float3);
            if (float3 > 10.0f) {
                this.dropLeash(true, true);
                this.goalSelector.disableControlFlag(Goal.Flag.MOVE);
            }
            else if (float3 > 6.0f) {
                final double double4 = (aio2.x - this.x) / float3;
                final double double5 = (aio2.y - this.y) / float3;
                final double double6 = (aio2.z - this.z) / float3;
                this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign(double4 * double4 * 0.4, double4), Math.copySign(double5 * double5 * 0.4, double5), Math.copySign(double6 * double6 * 0.4, double6)));
            }
            else {
                this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
                final float float4 = 2.0f;
                final Vec3 csi5 = new Vec3(aio2.x - this.x, aio2.y - this.y, aio2.z - this.z).normalize().scale(Math.max(float3 - 2.0f, 0.0f));
                this.getNavigation().moveTo(this.x + csi5.x, this.y + csi5.y, this.z + csi5.z, this.followLeashSpeed());
            }
        }
    }
    
    protected double followLeashSpeed() {
        return 1.0;
    }
    
    protected void onLeashDistance(final float float1) {
    }
}
