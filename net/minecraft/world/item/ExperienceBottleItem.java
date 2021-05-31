package net.minecraft.world.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ExperienceBottleItem extends Item {
    public ExperienceBottleItem(final Properties a) {
        super(a);
    }
    
    @Override
    public boolean isFoil(final ItemStack bcj) {
        return true;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(final Level bhr, final Player awg, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        if (!awg.abilities.instabuild) {
            bcj5.shrink(1);
        }
        bhr.playSound(null, awg.x, awg.y, awg.z, SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (ExperienceBottleItem.random.nextFloat() * 0.4f + 0.8f));
        if (!bhr.isClientSide) {
            final ThrownExperienceBottle axf6 = new ThrownExperienceBottle(bhr, awg);
            axf6.setItem(bcj5);
            axf6.shootFromRotation(awg, awg.xRot, awg.yRot, -20.0f, 0.7f, 1.0f);
            bhr.addFreshEntity(axf6);
        }
        awg.awardStat(Stats.ITEM_USED.get(this));
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, bcj5);
    }
}
