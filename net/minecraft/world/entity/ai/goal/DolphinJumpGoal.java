package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Dolphin;

public class DolphinJumpGoal extends JumpGoal {
    private static final int[] STEPS_TO_CHECK;
    private final Dolphin dolphin;
    private final int interval;
    private boolean breached;
    
    public DolphinJumpGoal(final Dolphin arf, final int integer) {
        this.dolphin = arf;
        this.interval = integer;
    }
    
    @Override
    public boolean canUse() {
        if (this.dolphin.getRandom().nextInt(this.interval) != 0) {
            return false;
        }
        final Direction fb2 = this.dolphin.getMotionDirection();
        final int integer3 = fb2.getStepX();
        final int integer4 = fb2.getStepZ();
        final BlockPos ew5 = new BlockPos(this.dolphin);
        for (final int integer5 : DolphinJumpGoal.STEPS_TO_CHECK) {
            if (!this.waterIsClear(ew5, integer3, integer4, integer5) || !this.surfaceIsClear(ew5, integer3, integer4, integer5)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean waterIsClear(final BlockPos ew, final int integer2, final int integer3, final int integer4) {
        final BlockPos ew2 = ew.offset(integer2 * integer4, 0, integer3 * integer4);
        return this.dolphin.level.getFluidState(ew2).is(FluidTags.WATER) && !this.dolphin.level.getBlockState(ew2).getMaterial().blocksMotion();
    }
    
    private boolean surfaceIsClear(final BlockPos ew, final int integer2, final int integer3, final int integer4) {
        return this.dolphin.level.getBlockState(ew.offset(integer2 * integer4, 1, integer3 * integer4)).isAir() && this.dolphin.level.getBlockState(ew.offset(integer2 * integer4, 2, integer3 * integer4)).isAir();
    }
    
    @Override
    public boolean canContinueToUse() {
        final double double2 = this.dolphin.getDeltaMovement().y;
        return (double2 * double2 >= 0.029999999329447746 || this.dolphin.xRot == 0.0f || Math.abs(this.dolphin.xRot) >= 10.0f || !this.dolphin.isInWater()) && !this.dolphin.onGround;
    }
    
    @Override
    public boolean isInterruptable() {
        return false;
    }
    
    @Override
    public void start() {
        final Direction fb2 = this.dolphin.getMotionDirection();
        this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(fb2.getStepX() * 0.6, 0.7, fb2.getStepZ() * 0.6));
        this.dolphin.getNavigation().stop();
    }
    
    @Override
    public void stop() {
        this.dolphin.xRot = 0.0f;
    }
    
    @Override
    public void tick() {
        final boolean boolean2 = this.breached;
        if (!boolean2) {
            final FluidState clk3 = this.dolphin.level.getFluidState(new BlockPos(this.dolphin));
            this.breached = clk3.is(FluidTags.WATER);
        }
        if (this.breached && !boolean2) {
            this.dolphin.playSound(SoundEvents.DOLPHIN_JUMP, 1.0f, 1.0f);
        }
        final Vec3 csi3 = this.dolphin.getDeltaMovement();
        if (csi3.y * csi3.y < 0.029999999329447746 && this.dolphin.xRot != 0.0f) {
            this.dolphin.xRot = this.rotlerp(this.dolphin.xRot, 0.0f, 0.2f);
        }
        else {
            final double double4 = Math.sqrt(Entity.getHorizontalDistanceSqr(csi3));
            final double double5 = Math.signum(-csi3.y) * Math.acos(double4 / csi3.length()) * 57.2957763671875;
            this.dolphin.xRot = (float)double5;
        }
    }
    
    static {
        STEPS_TO_CHECK = new int[] { 0, 1, 4, 5, 6, 7 };
    }
}
