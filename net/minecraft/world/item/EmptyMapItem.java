package net.minecraft.world.item;

import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EmptyMapItem extends ComplexItem {
    public EmptyMapItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = MapItem.create(bhr, Mth.floor(awg.x), Mth.floor(awg.z), (byte)0, true, false);
        final ItemStack bcj6 = awg.getItemInHand(ahi);
        if (!awg.abilities.instabuild) {
            bcj6.shrink(1);
        }
        if (bcj6.isEmpty()) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
        }
        if (!awg.inventory.add(bcj5.copy())) {
            awg.drop(bcj5, false);
        }
        awg.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj6);
    }
}
