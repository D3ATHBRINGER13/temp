package net.minecraft.world.entity.ai.memory;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.behavior.BlockPosWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.PositionWrapper;

public class WalkTarget {
    private final PositionWrapper target;
    private final float speed;
    private final int closeEnoughDist;
    
    public WalkTarget(final BlockPos ew, final float float2, final int integer) {
        this(new BlockPosWrapper(ew), float2, integer);
    }
    
    public WalkTarget(final Vec3 csi, final float float2, final int integer) {
        this(new BlockPosWrapper(new BlockPos(csi)), float2, integer);
    }
    
    public WalkTarget(final PositionWrapper akw, final float float2, final int integer) {
        this.target = akw;
        this.speed = float2;
        this.closeEnoughDist = integer;
    }
    
    public PositionWrapper getTarget() {
        return this.target;
    }
    
    public float getSpeed() {
        return this.speed;
    }
    
    public int getCloseEnoughDist() {
        return this.closeEnoughDist;
    }
}
