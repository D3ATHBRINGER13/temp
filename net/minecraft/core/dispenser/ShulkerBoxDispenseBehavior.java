package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.DirectionalPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockSource;

public class ShulkerBoxDispenseBehavior extends OptionalDispenseItemBehavior {
    @Override
    protected ItemStack execute(final BlockSource ex, final ItemStack bcj) {
        this.success = false;
        final Item bce4 = bcj.getItem();
        if (bce4 instanceof BlockItem) {
            final Direction fb5 = ex.getBlockState().<Direction>getValue((Property<Direction>)DispenserBlock.FACING);
            final BlockPos ew6 = ex.getPos().relative(fb5);
            final Direction fb6 = ex.getLevel().isEmptyBlock(ew6.below()) ? fb5 : Direction.UP;
            this.success = (((BlockItem)bce4).place(new DirectionalPlaceContext(ex.getLevel(), ew6, fb5, bcj, fb6)) == InteractionResult.SUCCESS);
        }
        return bcj;
    }
}
