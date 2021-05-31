package net.minecraft.world.effect;

import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.world.entity.LivingEntity;

public class AbsoptionMobEffect extends MobEffect {
    protected AbsoptionMobEffect(final MobEffectCategory aih, final int integer) {
        super(aih, integer);
    }
    
    @Override
    public void removeAttributeModifiers(final LivingEntity aix, final BaseAttributeMap ajr, final int integer) {
        aix.setAbsorptionAmount(aix.getAbsorptionAmount() - 4 * (integer + 1));
        super.removeAttributeModifiers(aix, ajr, integer);
    }
    
    @Override
    public void addAttributeModifiers(final LivingEntity aix, final BaseAttributeMap ajr, final int integer) {
        aix.setAbsorptionAmount(aix.getAbsorptionAmount() + 4 * (integer + 1));
        super.addAttributeModifiers(aix, ajr, integer);
    }
}
