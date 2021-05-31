package net.minecraft.world.entity.ai.attributes;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.Collection;

public interface AttributeInstance {
    Attribute getAttribute();
    
    double getBaseValue();
    
    void setBaseValue(final double double1);
    
    Collection<AttributeModifier> getModifiers(final AttributeModifier.Operation a);
    
    Collection<AttributeModifier> getModifiers();
    
    boolean hasModifier(final AttributeModifier ajp);
    
    @Nullable
    AttributeModifier getModifier(final UUID uUID);
    
    void addModifier(final AttributeModifier ajp);
    
    void removeModifier(final AttributeModifier ajp);
    
    void removeModifier(final UUID uUID);
    
    void removeModifiers();
    
    double getValue();
}
