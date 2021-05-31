package net.minecraft.world.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SaddleItem extends Item {
    public SaddleItem(final Properties a) {
        super(a);
    }
    
    @Override
    public boolean interactEnemy(final ItemStack bcj, final Player awg, final LivingEntity aix, final InteractionHand ahi) {
        if (aix instanceof Pig) {
            final Pig arn6 = (Pig)aix;
            if (arn6.isAlive() && !arn6.hasSaddle() && !arn6.isBaby()) {
                arn6.setSaddle(true);
                arn6.level.playSound(awg, arn6.x, arn6.y, arn6.z, SoundEvents.PIG_SADDLE, SoundSource.NEUTRAL, 0.5f, 1.0f);
                bcj.shrink(1);
            }
            return true;
        }
        return false;
    }
}
