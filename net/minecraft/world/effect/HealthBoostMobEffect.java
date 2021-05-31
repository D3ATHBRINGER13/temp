package net.minecraft.world.effect;

import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.world.entity.LivingEntity;

public class HealthBoostMobEffect extends MobEffect {
    public HealthBoostMobEffect(final MobEffectCategory aih, final int integer) {
        super(aih, integer);
    }
    
    @Override
    public void removeAttributeModifiers(final LivingEntity aix, final BaseAttributeMap ajr, final int integer) {
        super.removeAttributeModifiers(aix, ajr, integer);
        if (aix.getHealth() > aix.getMaxHealth()) {
            aix.setHealth(aix.getMaxHealth());
        }
    }
}
