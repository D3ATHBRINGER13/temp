package net.minecraft.world.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;

public abstract class FlyingMob extends Mob {
    protected FlyingMob(final EntityType<? extends FlyingMob> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    @Override
    public void causeFallDamage(final float float1, final float float2) {
    }
    
    @Override
    protected void checkFallDamage(final double double1, final boolean boolean2, final BlockState bvt, final BlockPos ew) {
    }
    
    @Override
    public void travel(final Vec3 csi) {
        if (this.isInWater()) {
            this.moveRelative(0.02f, csi);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.800000011920929));
        }
        else if (this.isInLava()) {
            this.moveRelative(0.02f, csi);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
        }
        else {
            float float3 = 0.91f;
            if (this.onGround) {
                float3 = this.level.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFriction() * 0.91f;
            }
            final float float4 = 0.16277137f / (float3 * float3 * float3);
            float3 = 0.91f;
            if (this.onGround) {
                float3 = this.level.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFriction() * 0.91f;
            }
            this.moveRelative(this.onGround ? (0.1f * float4) : 0.02f, csi);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(float3));
        }
        this.animationSpeedOld = this.animationSpeed;
        final double double3 = this.x - this.xo;
        final double double4 = this.z - this.zo;
        float float5 = Mth.sqrt(double3 * double3 + double4 * double4) * 4.0f;
        if (float5 > 1.0f) {
            float5 = 1.0f;
        }
        this.animationSpeed += (float5 - this.animationSpeed) * 0.4f;
        this.animationPosition += this.animationSpeed;
    }
    
    @Override
    public boolean onLadder() {
        return false;
    }
}
