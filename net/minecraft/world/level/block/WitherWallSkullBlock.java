package net.minecraft.world.level.block;

import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WitherWallSkullBlock extends WallSkullBlock {
    protected WitherWallSkullBlock(final Properties c) {
        super(SkullBlock.Types.WITHER_SKELETON, c);
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, @Nullable final LivingEntity aix, final ItemStack bcj) {
        Blocks.WITHER_SKELETON_SKULL.setPlacedBy(bhr, ew, bvt, aix, bcj);
    }
}
