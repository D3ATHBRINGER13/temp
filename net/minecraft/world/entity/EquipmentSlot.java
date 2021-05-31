package net.minecraft.world.entity;

public enum EquipmentSlot {
    MAINHAND(Type.HAND, 0, 0, "mainhand"), 
    OFFHAND(Type.HAND, 1, 5, "offhand"), 
    FEET(Type.ARMOR, 0, 1, "feet"), 
    LEGS(Type.ARMOR, 1, 2, "legs"), 
    CHEST(Type.ARMOR, 2, 3, "chest"), 
    HEAD(Type.ARMOR, 3, 4, "head");
    
    private final Type type;
    private final int index;
    private final int filterFlag;
    private final String name;
    
    private EquipmentSlot(final Type a, final int integer4, final int integer5, final String string6) {
        this.type = a;
        this.index = integer4;
        this.filterFlag = integer5;
        this.name = string6;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public int getFilterFlag() {
        return this.filterFlag;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static EquipmentSlot byName(final String string) {
        for (final EquipmentSlot ait5 : values()) {
            if (ait5.getName().equals(string)) {
                return ait5;
            }
        }
        throw new IllegalArgumentException("Invalid slot '" + string + "'");
    }
    
    public static EquipmentSlot byTypeAndIndex(final Type a, final int integer) {
        for (final EquipmentSlot ait6 : values()) {
            if (ait6.getType() == a && ait6.getIndex() == integer) {
                return ait6;
            }
        }
        throw new IllegalArgumentException(new StringBuilder().append("Invalid slot '").append(a).append("': ").append(integer).toString());
    }
    
    public enum Type {
        HAND, 
        ARMOR;
    }
}
