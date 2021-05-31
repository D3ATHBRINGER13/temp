package net.minecraft.world.item;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.Registry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.ItemLike;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public class PotionItem extends Item {
    public PotionItem(final Properties a) {
        super(a);
    }
    
    @Override
    public ItemStack getDefaultInstance() {
        return PotionUtils.setPotion(super.getDefaultInstance(), Potions.WATER);
    }
    
    @Override
    public ItemStack finishUsingItem(final ItemStack bcj, final Level bhr, final LivingEntity aix) {
        final Player awg5 = (aix instanceof Player) ? ((Player)aix) : null;
        if (awg5 == null || !awg5.abilities.instabuild) {
            bcj.shrink(1);
        }
        if (awg5 instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)awg5, bcj);
        }
        if (!bhr.isClientSide) {
            final List<MobEffectInstance> list6 = PotionUtils.getMobEffects(bcj);
            for (final MobEffectInstance aii8 : list6) {
                if (aii8.getEffect().isInstantenous()) {
                    aii8.getEffect().applyInstantenousEffect(awg5, awg5, aix, aii8.getAmplifier(), 1.0);
                }
                else {
                    aix.addEffect(new MobEffectInstance(aii8));
                }
            }
        }
        if (awg5 != null) {
            awg5.awardStat(Stats.ITEM_USED.get(this));
        }
        if (awg5 == null || !awg5.abilities.instabuild) {
            if (bcj.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            if (awg5 != null) {
                awg5.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
            }
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
    
    @Override
    public String getDescriptionId(final ItemStack bcj) {
        return PotionUtils.getPotion(bcj).getName(this.getDescriptionId() + ".effect.");
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        PotionUtils.addPotionTooltip(bcj, list, 1.0f);
    }
    
    @Override
    public boolean isFoil(final ItemStack bcj) {
        return super.isFoil(bcj) || !PotionUtils.getMobEffects(bcj).isEmpty();
    }
    
    @Override
    public void fillItemCategory(final CreativeModeTab bba, final NonNullList<ItemStack> fk) {
        if (this.allowdedIn(bba)) {
            for (final Potion bdy5 : Registry.POTION) {
                if (bdy5 != Potions.EMPTY) {
                    fk.add(PotionUtils.setPotion(new ItemStack(this), bdy5));
                }
            }
        }
    }
}
