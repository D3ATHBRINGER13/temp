package net.minecraft.world.level;

import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;

public enum EmptyBlockGetter implements BlockGetter {
    INSTANCE;
    
    @Nullable
    public BlockEntity getBlockEntity(final BlockPos ew) {
        return null;
    }
    
    public BlockState getBlockState(final BlockPos ew) {
        return Blocks.AIR.defaultBlockState();
    }
    
    public FluidState getFluidState(final BlockPos ew) {
        return Fluids.EMPTY.defaultFluidState();
    }
}
