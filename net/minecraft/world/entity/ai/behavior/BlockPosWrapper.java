package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;

public class BlockPosWrapper implements PositionWrapper {
    private final BlockPos pos;
    private final Vec3 lookAt;
    
    public BlockPosWrapper(final BlockPos ew) {
        this.pos = ew;
        this.lookAt = new Vec3(ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5);
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public Vec3 getLookAtPos() {
        return this.lookAt;
    }
    
    public boolean isVisible(final LivingEntity aix) {
        return true;
    }
    
    public String toString() {
        return new StringBuilder().append("BlockPosWrapper{pos=").append(this.pos).append(", lookAt=").append(this.lookAt).append('}').toString();
    }
}
