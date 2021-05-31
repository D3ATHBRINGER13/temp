package net.minecraft.world.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BlockTags;
import java.util.function.Consumer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;

public class ShearsItem extends Item {
    public ShearsItem(final Properties a) {
        super(a);
    }
    
    @Override
    public boolean mineBlock(final ItemStack bcj, final Level bhr, final BlockState bvt, final BlockPos ew, final LivingEntity aix) {
        if (!bhr.isClientSide) {
            bcj.<LivingEntity>hurtAndBreak(1, aix, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
        }
        final Block bmv7 = bvt.getBlock();
        return bvt.is(BlockTags.LEAVES) || bmv7 == Blocks.COBWEB || bmv7 == Blocks.GRASS || bmv7 == Blocks.FERN || bmv7 == Blocks.DEAD_BUSH || bmv7 == Blocks.VINE || bmv7 == Blocks.TRIPWIRE || bmv7.is(BlockTags.WOOL) || super.mineBlock(bcj, bhr, bvt, ew, aix);
    }
    
    @Override
    public boolean canDestroySpecial(final BlockState bvt) {
        final Block bmv3 = bvt.getBlock();
        return bmv3 == Blocks.COBWEB || bmv3 == Blocks.REDSTONE_WIRE || bmv3 == Blocks.TRIPWIRE;
    }
    
    @Override
    public float getDestroySpeed(final ItemStack bcj, final BlockState bvt) {
        final Block bmv4 = bvt.getBlock();
        if (bmv4 == Blocks.COBWEB || bvt.is(BlockTags.LEAVES)) {
            return 15.0f;
        }
        if (bmv4.is(BlockTags.WOOL)) {
            return 5.0f;
        }
        return super.getDestroySpeed(bcj, bvt);
    }
}
