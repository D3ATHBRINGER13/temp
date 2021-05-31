package net.minecraft.world.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SnowballItem extends Item {
    public SnowballItem(final Properties a) {
        super(a);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        if (!awg.abilities.instabuild) {
            bcj5.shrink(1);
        }
        bhr.playSound(null, awg.x, awg.y, awg.z, SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (SnowballItem.random.nextFloat() * 0.4f + 0.8f));
        if (!bhr.isClientSide) {
            final Snowball awz6 = new Snowball(bhr, awg);
            awz6.setItem(bcj5);
            awz6.shootFromRotation(awg, awg.xRot, awg.yRot, 0.0f, 1.5f, 1.0f);
            bhr.addFreshEntity(awz6);
        }
        awg.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
}
