package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import java.util.function.Consumer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;

public class FishingRodItem extends Item {
    public FishingRodItem(final Properties a) {
        super(a);
        boolean boolean4;
        boolean boolean5;
        this.addProperty(new ResourceLocation("cast"), (bcj, bhr, aix) -> {
            if (aix == null) {
                return 0.0f;
            }
            else {
                boolean4 = (aix.getMainHandItem() == bcj);
                boolean5 = (aix.getOffhandItem() == bcj);
                if (aix.getMainHandItem().getItem() instanceof FishingRodItem) {
                    boolean5 = false;
                }
                return ((boolean4 || boolean5) && aix instanceof Player && aix.fishing != null) ? 1.0f : 0.0f;
            }
        });
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        if (awg.fishing != null) {
            if (!bhr.isClientSide) {
                final int integer6 = awg.fishing.retrieve(bcj5);
                bcj5.<Player>hurtAndBreak(integer6, awg, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(ahi)));
            }
            awg.swing(ahi);
            bhr.playSound(null, awg.x, awg.y, awg.z, SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0f, 0.4f / (FishingRodItem.random.nextFloat() * 0.4f + 0.8f));
        }
        else {
            bhr.playSound(null, awg.x, awg.y, awg.z, SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (FishingRodItem.random.nextFloat() * 0.4f + 0.8f));
            if (!bhr.isClientSide) {
                final int integer6 = EnchantmentHelper.getFishingSpeedBonus(bcj5);
                final int integer7 = EnchantmentHelper.getFishingLuckBonus(bcj5);
                bhr.addFreshEntity(new FishingHook(awg, bhr, integer7, integer6));
            }
            awg.swing(ahi);
            awg.awardStat(Stats.ITEM_USED.get(this));
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
    
    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}
