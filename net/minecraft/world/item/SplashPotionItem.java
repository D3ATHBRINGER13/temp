package net.minecraft.world.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SplashPotionItem extends PotionItem {
    public SplashPotionItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        final ItemStack bcj6 = awg.abilities.instabuild ? bcj5.copy() : bcj5.split(1);
        bhr.playSound(null, awg.x, awg.y, awg.z, SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5f, 0.4f / (SplashPotionItem.random.nextFloat() * 0.4f + 0.8f));
        if (!bhr.isClientSide) {
            final ThrownPotion axg7 = new ThrownPotion(bhr, awg);
            axg7.setItem(bcj6);
            axg7.shootFromRotation(awg, awg.xRot, awg.yRot, -20.0f, 0.5f, 1.0f);
            bhr.addFreshEntity(axg7);
        }
        awg.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
}
