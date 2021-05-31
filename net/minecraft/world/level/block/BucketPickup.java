package net.minecraft.world.level.block;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public interface BucketPickup {
    Fluid takeLiquid(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt);
}
