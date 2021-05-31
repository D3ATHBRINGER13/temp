package net.minecraft.world.item;

import java.util.function.Predicate;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.AbstractArrow;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;

public class BowItem extends ProjectileWeaponItem {
    public BowItem(final Properties a) {
        super(a);
        this.addProperty(new ResourceLocation("pull"), (bcj, bhr, aix) -> {
            if (aix == null) {
                return 0.0f;
            }
            else if (aix.getUseItem().getItem() != Items.BOW) {
                return 0.0f;
            }
            else {
                return (bcj.getUseDuration() - aix.getUseItemRemainingTicks()) / 20.0f;
            }
        });
        this.addProperty(new ResourceLocation("pulling"), (bcj, bhr, aix) -> (aix != null && aix.isUsingItem() && aix.getUseItem() == bcj) ? 1.0f : 0.0f);
    }
    
    @Override
    public void releaseUsing(final ItemStack bcj, final Level bhr, final LivingEntity aix, final int integer) {
        if (!(aix instanceof Player)) {
            return;
        }
        final Player awg6 = (Player)aix;
        final boolean boolean7 = awg6.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bcj) > 0;
        ItemStack bcj2 = awg6.getProjectile(bcj);
        if (bcj2.isEmpty() && !boolean7) {
            return;
        }
        if (bcj2.isEmpty()) {
            bcj2 = new ItemStack(Items.ARROW);
        }
        final int integer2 = this.getUseDuration(bcj) - integer;
        final float float10 = getPowerForTime(integer2);
        if (float10 < 0.1) {
            return;
        }
        final boolean boolean8 = boolean7 && bcj2.getItem() == Items.ARROW;
        if (!bhr.isClientSide) {
            final ArrowItem bah12 = (ArrowItem)((bcj2.getItem() instanceof ArrowItem) ? bcj2.getItem() : Items.ARROW);
            final AbstractArrow awk13 = bah12.createArrow(bhr, bcj2, awg6);
            awk13.shootFromRotation(awg6, awg6.xRot, awg6.yRot, 0.0f, float10 * 3.0f, 1.0f);
            if (float10 == 1.0f) {
                awk13.setCritArrow(true);
            }
            final int integer3 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bcj);
            if (integer3 > 0) {
                awk13.setBaseDamage(awk13.getBaseDamage() + integer3 * 0.5 + 0.5);
            }
            final int integer4 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bcj);
            if (integer4 > 0) {
                awk13.setKnockback(integer4);
            }
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, bcj) > 0) {
                awk13.setSecondsOnFire(100);
            }
            bcj.<Player>hurtAndBreak(1, awg6, (java.util.function.Consumer<Player>)(awg2 -> awg2.broadcastBreakEvent(awg6.getUsedItemHand())));
            if (boolean8 || (awg6.abilities.instabuild && (bcj2.getItem() == Items.SPECTRAL_ARROW || bcj2.getItem() == Items.TIPPED_ARROW))) {
                awk13.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            bhr.addFreshEntity(awk13);
        }
        bhr.playSound(null, awg6.x, awg6.y, awg6.z, SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f / (BowItem.random.nextFloat() * 0.4f + 1.2f) + float10 * 0.5f);
        if (!boolean8 && !awg6.abilities.instabuild) {
            bcj2.shrink(1);
            if (bcj2.isEmpty()) {
                awg6.inventory.removeItem(bcj2);
            }
        }
        awg6.awardStat(Stats.ITEM_USED.get(this));
    }
    
    public static float getPowerForTime(final int integer) {
        float float2 = integer / 20.0f;
        float2 = (float2 * float2 + float2 * 2.0f) / 3.0f;
        if (float2 > 1.0f) {
            float2 = 1.0f;
        }
        return float2;
    }
    
    @Override
    public int getUseDuration(final ItemStack bcj) {
        return 72000;
    }
    
    @Override
    public UseAnim getUseAnimation(final ItemStack bcj) {
        return UseAnim.BOW;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        final boolean boolean6 = !awg.getProjectile(bcj5).isEmpty();
        if (awg.abilities.instabuild || boolean6) {
            awg.startUsingItem(ahi);
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
        }
        if (boolean6) {
            return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, bcj5);
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, bcj5);
    }
    
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return BowItem.ARROW_ONLY;
    }
}
