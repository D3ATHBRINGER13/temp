package net.minecraft.world.effect;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttackDamageMobEffect extends MobEffect {
    protected final double multiplier;
    
    protected AttackDamageMobEffect(final MobEffectCategory aih, final int integer, final double double3) {
        super(aih, integer);
        this.multiplier = double3;
    }
    
    @Override
    public double getAttributeModifierValue(final int integer, final AttributeModifier ajp) {
        return this.multiplier * (integer + 1);
    }
}
