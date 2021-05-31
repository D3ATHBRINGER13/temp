package net.minecraft.client.color.block;

import net.minecraft.core.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockColor {
    int getColor(final BlockState bvt, @Nullable final BlockAndBiomeGetter bgz, @Nullable final BlockPos ew, final int integer);
}
