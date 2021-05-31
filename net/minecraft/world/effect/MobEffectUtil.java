package net.minecraft.world.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.StringUtil;
import net.minecraft.util.Mth;

public final class MobEffectUtil {
    public static String formatDuration(final MobEffectInstance aii, final float float2) {
        if (aii.isNoCounter()) {
            return "**:**";
        }
        final int integer3 = Mth.floor(aii.getDuration() * float2);
        return StringUtil.formatTickDuration(integer3);
    }
    
    public static boolean hasDigSpeed(final LivingEntity aix) {
        return aix.hasEffect(MobEffects.DIG_SPEED) || aix.hasEffect(MobEffects.CONDUIT_POWER);
    }
    
    public static int getDigSpeedAmplification(final LivingEntity aix) {
        int integer2 = 0;
        int integer3 = 0;
        if (aix.hasEffect(MobEffects.DIG_SPEED)) {
            integer2 = aix.getEffect(MobEffects.DIG_SPEED).getAmplifier();
        }
        if (aix.hasEffect(MobEffects.CONDUIT_POWER)) {
            integer3 = aix.getEffect(MobEffects.CONDUIT_POWER).getAmplifier();
        }
        return Math.max(integer2, integer3);
    }
    
    public static boolean hasWaterBreathing(final LivingEntity aix) {
        return aix.hasEffect(MobEffects.WATER_BREATHING) || aix.hasEffect(MobEffects.CONDUIT_POWER);
    }
}
