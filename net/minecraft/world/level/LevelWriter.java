package net.minecraft.world.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public interface LevelWriter {
    boolean setBlock(final BlockPos ew, final BlockState bvt, final int integer);
    
    boolean removeBlock(final BlockPos ew, final boolean boolean2);
    
    boolean destroyBlock(final BlockPos ew, final boolean boolean2);
    
    default boolean addFreshEntity(final Entity aio) {
        return false;
    }
}
