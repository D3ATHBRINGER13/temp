package net.minecraft.world.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ChorusFruitItem extends Item {
    public ChorusFruitItem(final Properties a) {
        super(a);
    }
    
    @Override
    public ItemStack finishUsingItem(final ItemStack bcj, final Level bhr, final LivingEntity aix) {
        final ItemStack bcj2 = super.finishUsingItem(bcj, bhr, aix);
        if (!bhr.isClientSide) {
            final double double6 = aix.x;
            final double double7 = aix.y;
            final double double8 = aix.z;
            for (int integer12 = 0; integer12 < 16; ++integer12) {
                final double double9 = aix.x + (aix.getRandom().nextDouble() - 0.5) * 16.0;
                final double double10 = Mth.clamp(aix.y + (aix.getRandom().nextInt(16) - 8), 0.0, bhr.getHeight() - 1);
                final double double11 = aix.z + (aix.getRandom().nextDouble() - 0.5) * 16.0;
                if (aix.isPassenger()) {
                    aix.stopRiding();
                }
                if (aix.randomTeleport(double9, double10, double11, true)) {
                    bhr.playSound(null, double6, double7, double8, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
                    aix.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);
                    break;
                }
            }
            if (aix instanceof Player) {
                ((Player)aix).getCooldowns().addCooldown(this, 20);
            }
        }
        return bcj2;
    }
}
