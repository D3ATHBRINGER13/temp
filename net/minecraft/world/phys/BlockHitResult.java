package net.minecraft.world.phys;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class BlockHitResult extends HitResult {
    private final Direction direction;
    private final BlockPos blockPos;
    private final boolean miss;
    private final boolean inside;
    
    public static BlockHitResult miss(final Vec3 csi, final Direction fb, final BlockPos ew) {
        return new BlockHitResult(true, csi, fb, ew, false);
    }
    
    public BlockHitResult(final Vec3 csi, final Direction fb, final BlockPos ew, final boolean boolean4) {
        this(false, csi, fb, ew, boolean4);
    }
    
    private BlockHitResult(final boolean boolean1, final Vec3 csi, final Direction fb, final BlockPos ew, final boolean boolean5) {
        super(csi);
        this.miss = boolean1;
        this.direction = fb;
        this.blockPos = ew;
        this.inside = boolean5;
    }
    
    public BlockHitResult withDirection(final Direction fb) {
        return new BlockHitResult(this.miss, this.location, fb, this.blockPos, this.inside);
    }
    
    public BlockPos getBlockPos() {
        return this.blockPos;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    @Override
    public Type getType() {
        return this.miss ? Type.MISS : Type.BLOCK;
    }
    
    public boolean isInside() {
        return this.inside;
    }
}
