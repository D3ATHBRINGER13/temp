package net.minecraft.world.entity.ai.attributes;

import javax.annotation.Nullable;

public abstract class BaseAttribute implements Attribute {
    private final Attribute parent;
    private final String name;
    private final double defaultValue;
    private boolean syncable;
    
    protected BaseAttribute(@Nullable final Attribute ajn, final String string, final double double3) {
        this.parent = ajn;
        this.name = string;
        this.defaultValue = double3;
        if (string == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public double getDefaultValue() {
        return this.defaultValue;
    }
    
    public boolean isClientSyncable() {
        return this.syncable;
    }
    
    public BaseAttribute setSyncable(final boolean boolean1) {
        this.syncable = boolean1;
        return this;
    }
    
    @Nullable
    public Attribute getParentAttribute() {
        return this.parent;
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    public boolean equals(final Object object) {
        return object instanceof Attribute && this.name.equals(((Attribute)object).getName());
    }
}
