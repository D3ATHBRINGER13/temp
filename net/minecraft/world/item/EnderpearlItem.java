package net.minecraft.world.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EnderpearlItem extends Item {
    public EnderpearlItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        if (!awg.abilities.instabuild) {
            bcj5.shrink(1);
        }
        bhr.playSound(null, awg.x, awg.y, awg.z, SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (EnderpearlItem.random.nextFloat() * 0.4f + 0.8f));
        awg.getCooldowns().addCooldown(this, 20);
        if (!bhr.isClientSide) {
            final ThrownEnderpearl axe6 = new ThrownEnderpearl(bhr, awg);
            axe6.setItem(bcj5);
            axe6.shootFromRotation(awg, awg.xRot, awg.yRot, 0.0f, 1.5f, 1.0f);
            bhr.addFreshEntity(axe6);
        }
        awg.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
}
