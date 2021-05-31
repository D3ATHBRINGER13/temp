package net.minecraft.world.level.block.state.properties;

import com.google.common.base.MoreObjects;

public abstract class AbstractProperty<T extends Comparable<T>> implements Property<T> {
    private final Class<T> clazz;
    private final String name;
    private Integer hashCode;
    
    protected AbstractProperty(final String string, final Class<T> class2) {
        this.clazz = class2;
        this.name = string;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class<T> getValueClass() {
        return this.clazz;
    }
    
    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof AbstractProperty) {
            final AbstractProperty<?> bwf3 = object;
            return this.clazz.equals(bwf3.clazz) && this.name.equals(bwf3.name);
        }
        return false;
    }
    
    public final int hashCode() {
        if (this.hashCode == null) {
            this.hashCode = this.generateHashCode();
        }
        return this.hashCode;
    }
    
    public int generateHashCode() {
        return 31 * this.clazz.hashCode() + this.name.hashCode();
    }
}
