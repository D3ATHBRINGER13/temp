package net.minecraft.world.item;

import net.minecraft.nbt.ListTag;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.InteractionResult;

public class WritableBookItem extends Item {
    public WritableBookItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        final BlockPos ew4 = bdu.getClickedPos();
        final BlockState bvt5 = bhr3.getBlockState(ew4);
        if (bvt5.getBlock() == Blocks.LECTERN) {
            return LecternBlock.tryPlaceBook(bhr3, ew4, bvt5, bdu.getItemInHand()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        awg.openItemGui(bcj5, ahi);
        awg.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
    
    public static boolean makeSureTagIsValid(@Nullable final CompoundTag id) {
        if (id == null) {
            return false;
        }
        if (!id.contains("pages", 9)) {
            return false;
        }
        final ListTag ik2 = id.getList("pages", 8);
        for (int integer3 = 0; integer3 < ik2.size(); ++integer3) {
            final String string4 = ik2.getString(integer3);
            if (string4.length() > 32767) {
                return false;
            }
        }
        return true;
    }
}
