package net.minecraft.world.entity.ai.attributes;

import javax.annotation.Nullable;

public interface Attribute {
    String getName();
    
    double sanitizeValue(final double double1);
    
    double getDefaultValue();
    
    boolean isClientSyncable();
    
    @Nullable
    Attribute getParentAttribute();
}
