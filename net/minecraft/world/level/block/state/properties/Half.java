package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum Half implements StringRepresentable {
    TOP("top"), 
    BOTTOM("bottom");
    
    private final String name;
    
    private Half(final String string3) {
        this.name = string3;
    }
    
    public String toString() {
        return this.name;
    }
    
    public String getSerializedName() {
        return this.name;
    }
}
