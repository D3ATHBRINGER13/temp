package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum RailShape implements StringRepresentable {
    NORTH_SOUTH(0, "north_south"), 
    EAST_WEST(1, "east_west"), 
    ASCENDING_EAST(2, "ascending_east"), 
    ASCENDING_WEST(3, "ascending_west"), 
    ASCENDING_NORTH(4, "ascending_north"), 
    ASCENDING_SOUTH(5, "ascending_south"), 
    SOUTH_EAST(6, "south_east"), 
    SOUTH_WEST(7, "south_west"), 
    NORTH_WEST(8, "north_west"), 
    NORTH_EAST(9, "north_east");
    
    private final int data;
    private final String name;
    
    private RailShape(final int integer3, final String string4) {
        this.data = integer3;
        this.name = string4;
    }
    
    public int getData() {
        return this.data;
    }
    
    public String toString() {
        return this.name;
    }
    
    public boolean isAscending() {
        return this == RailShape.ASCENDING_NORTH || this == RailShape.ASCENDING_EAST || this == RailShape.ASCENDING_SOUTH || this == RailShape.ASCENDING_WEST;
    }
    
    public String getSerializedName() {
        return this.name;
    }
}
