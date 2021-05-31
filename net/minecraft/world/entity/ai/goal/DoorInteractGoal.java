package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

public abstract class DoorInteractGoal extends Goal {
    protected Mob mob;
    protected BlockPos doorPos;
    protected boolean hasDoor;
    private boolean passed;
    private float doorOpenDirX;
    private float doorOpenDirZ;
    
    public DoorInteractGoal(final Mob aiy) {
        this.doorPos = BlockPos.ZERO;
        this.mob = aiy;
        if (!(aiy.getNavigation() instanceof GroundPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }
    
    protected boolean isOpen() {
        if (!this.hasDoor) {
            return false;
        }
        final BlockState bvt2 = this.mob.level.getBlockState(this.doorPos);
        if (!(bvt2.getBlock() instanceof DoorBlock)) {
            return this.hasDoor = false;
        }
        return bvt2.<Boolean>getValue((Property<Boolean>)DoorBlock.OPEN);
    }
    
    protected void setOpen(final boolean boolean1) {
        if (this.hasDoor) {
            final BlockState bvt3 = this.mob.level.getBlockState(this.doorPos);
            if (bvt3.getBlock() instanceof DoorBlock) {
                ((DoorBlock)bvt3.getBlock()).setOpen(this.mob.level, this.doorPos, boolean1);
            }
        }
    }
    
    @Override
    public boolean canUse() {
        if (!this.mob.horizontalCollision) {
            return false;
        }
        final GroundPathNavigation apo2 = (GroundPathNavigation)this.mob.getNavigation();
        final Path cnr3 = apo2.getPath();
        if (cnr3 == null || cnr3.isDone() || !apo2.canOpenDoors()) {
            return false;
        }
        for (int integer4 = 0; integer4 < Math.min(cnr3.getIndex() + 2, cnr3.getSize()); ++integer4) {
            final Node cnp5 = cnr3.get(integer4);
            this.doorPos = new BlockPos(cnp5.x, cnp5.y + 1, cnp5.z);
            if (this.mob.distanceToSqr(this.doorPos.getX(), this.mob.y, this.doorPos.getZ()) <= 2.25) {
                this.hasDoor = isDoor(this.mob.level, this.doorPos);
                if (this.hasDoor) {
                    return true;
                }
            }
        }
        this.doorPos = new BlockPos(this.mob).above();
        return this.hasDoor = isDoor(this.mob.level, this.doorPos);
    }
    
    @Override
    public boolean canContinueToUse() {
        return !this.passed;
    }
    
    @Override
    public void start() {
        this.passed = false;
        this.doorOpenDirX = (float)(this.doorPos.getX() + 0.5f - this.mob.x);
        this.doorOpenDirZ = (float)(this.doorPos.getZ() + 0.5f - this.mob.z);
    }
    
    @Override
    public void tick() {
        final float float2 = (float)(this.doorPos.getX() + 0.5f - this.mob.x);
        final float float3 = (float)(this.doorPos.getZ() + 0.5f - this.mob.z);
        final float float4 = this.doorOpenDirX * float2 + this.doorOpenDirZ * float3;
        if (float4 < 0.0f) {
            this.passed = true;
        }
    }
    
    public static boolean isDoor(final Level bhr, final BlockPos ew) {
        final BlockState bvt3 = bhr.getBlockState(ew);
        return bvt3.getBlock() instanceof DoorBlock && bvt3.getMaterial() == Material.WOOD;
    }
}
