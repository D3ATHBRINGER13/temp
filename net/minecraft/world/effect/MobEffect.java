package net.minecraft.world.effect;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import java.util.Iterator;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import java.util.function.Supplier;
import java.util.UUID;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import java.util.Map;

public class MobEffect {
    private final Map<Attribute, AttributeModifier> attributeModifiers;
    private final MobEffectCategory category;
    private final int color;
    @Nullable
    private String descriptionId;
    
    @Nullable
    public static MobEffect byId(final int integer) {
        return Registry.MOB_EFFECT.byId(integer);
    }
    
    public static int getId(final MobEffect aig) {
        return Registry.MOB_EFFECT.getId(aig);
    }
    
    protected MobEffect(final MobEffectCategory aih, final int integer) {
        this.attributeModifiers = (Map<Attribute, AttributeModifier>)Maps.newHashMap();
        this.category = aih;
        this.color = integer;
    }
    
    public void applyEffectTick(final LivingEntity aix, final int integer) {
        if (this == MobEffects.REGENERATION) {
            if (aix.getHealth() < aix.getMaxHealth()) {
                aix.heal(1.0f);
            }
        }
        else if (this == MobEffects.POISON) {
            if (aix.getHealth() > 1.0f) {
                aix.hurt(DamageSource.MAGIC, 1.0f);
            }
        }
        else if (this == MobEffects.WITHER) {
            aix.hurt(DamageSource.WITHER, 1.0f);
        }
        else if (this == MobEffects.HUNGER && aix instanceof Player) {
            ((Player)aix).causeFoodExhaustion(0.005f * (integer + 1));
        }
        else if (this == MobEffects.SATURATION && aix instanceof Player) {
            if (!aix.level.isClientSide) {
                ((Player)aix).getFoodData().eat(integer + 1, 1.0f);
            }
        }
        else if ((this == MobEffects.HEAL && !aix.isInvertedHealAndHarm()) || (this == MobEffects.HARM && aix.isInvertedHealAndHarm())) {
            aix.heal((float)Math.max(4 << integer, 0));
        }
        else if ((this == MobEffects.HARM && !aix.isInvertedHealAndHarm()) || (this == MobEffects.HEAL && aix.isInvertedHealAndHarm())) {
            aix.hurt(DamageSource.MAGIC, (float)(6 << integer));
        }
    }
    
    public void applyInstantenousEffect(@Nullable final Entity aio1, @Nullable final Entity aio2, final LivingEntity aix, final int integer, final double double5) {
        if ((this == MobEffects.HEAL && !aix.isInvertedHealAndHarm()) || (this == MobEffects.HARM && aix.isInvertedHealAndHarm())) {
            final int integer2 = (int)(double5 * (4 << integer) + 0.5);
            aix.heal((float)integer2);
        }
        else if ((this == MobEffects.HARM && !aix.isInvertedHealAndHarm()) || (this == MobEffects.HEAL && aix.isInvertedHealAndHarm())) {
            final int integer2 = (int)(double5 * (6 << integer) + 0.5);
            if (aio1 == null) {
                aix.hurt(DamageSource.MAGIC, (float)integer2);
            }
            else {
                aix.hurt(DamageSource.indirectMagic(aio1, aio2), (float)integer2);
            }
        }
        else {
            this.applyEffectTick(aix, integer);
        }
    }
    
    public boolean isDurationEffectTick(final int integer1, final int integer2) {
        if (this == MobEffects.REGENERATION) {
            final int integer3 = 50 >> integer2;
            return integer3 <= 0 || integer1 % integer3 == 0;
        }
        if (this == MobEffects.POISON) {
            final int integer3 = 25 >> integer2;
            return integer3 <= 0 || integer1 % integer3 == 0;
        }
        if (this == MobEffects.WITHER) {
            final int integer3 = 40 >> integer2;
            return integer3 <= 0 || integer1 % integer3 == 0;
        }
        return this == MobEffects.HUNGER;
    }
    
    public boolean isInstantenous() {
        return false;
    }
    
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("effect", Registry.MOB_EFFECT.getKey(this));
        }
        return this.descriptionId;
    }
    
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }
    
    public Component getDisplayName() {
        return new TranslatableComponent(this.getDescriptionId(), new Object[0]);
    }
    
    public MobEffectCategory getCategory() {
        return this.category;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public MobEffect addAttributeModifier(final Attribute ajn, final String string, final double double3, final AttributeModifier.Operation a) {
        final AttributeModifier ajp7 = new AttributeModifier(UUID.fromString(string), (Supplier<String>)this::getDescriptionId, double3, a);
        this.attributeModifiers.put(ajn, ajp7);
        return this;
    }
    
    public Map<Attribute, AttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }
    
    public void removeAttributeModifiers(final LivingEntity aix, final BaseAttributeMap ajr, final int integer) {
        for (final Map.Entry<Attribute, AttributeModifier> entry6 : this.attributeModifiers.entrySet()) {
            final AttributeInstance ajo7 = ajr.getInstance((Attribute)entry6.getKey());
            if (ajo7 != null) {
                ajo7.removeModifier((AttributeModifier)entry6.getValue());
            }
        }
    }
    
    public void addAttributeModifiers(final LivingEntity aix, final BaseAttributeMap ajr, final int integer) {
        for (final Map.Entry<Attribute, AttributeModifier> entry6 : this.attributeModifiers.entrySet()) {
            final AttributeInstance ajo7 = ajr.getInstance((Attribute)entry6.getKey());
            if (ajo7 != null) {
                final AttributeModifier ajp8 = (AttributeModifier)entry6.getValue();
                ajo7.removeModifier(ajp8);
                ajo7.addModifier(new AttributeModifier(ajp8.getId(), this.getDescriptionId() + " " + integer, this.getAttributeModifierValue(integer, ajp8), ajp8.getOperation()));
            }
        }
    }
    
    public double getAttributeModifierValue(final int integer, final AttributeModifier ajp) {
        return ajp.getAmount() * (integer + 1);
    }
    
    public boolean isBeneficial() {
        return this.category == MobEffectCategory.BENEFICIAL;
    }
}
