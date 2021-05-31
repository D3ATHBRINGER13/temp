package net.minecraft.world.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.entity.player.Player;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class MilkBucketItem extends Item {
    public MilkBucketItem(final Properties a) {
        super(a);
    }
    
    @Override
    public ItemStack finishUsingItem(final ItemStack bcj, final Level bhr, final LivingEntity aix) {
        if (aix instanceof ServerPlayer) {
            final ServerPlayer vl5 = (ServerPlayer)aix;
            CriteriaTriggers.CONSUME_ITEM.trigger(vl5, bcj);
            vl5.awardStat(Stats.ITEM_USED.get(this));
        }
        if (aix instanceof Player && !((Player)aix).abilities.instabuild) {
            bcj.shrink(1);
        }
        if (!bhr.isClientSide) {
            aix.removeAllEffects();
        }
        if (bcj.isEmpty()) {
            return new ItemStack(Items.BUCKET);
        }
        return bcj;
    }
    
    @Override
    public int getUseDuration(final ItemStack bcj) {
        return 32;
    }
    
    @Override
    public UseAnim getUseAnimation(final ItemStack bcj) {
        return UseAnim.DRINK;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        awg.startUsingItem(ahi);
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, awg.getItemInHand(ahi));
    }
}
