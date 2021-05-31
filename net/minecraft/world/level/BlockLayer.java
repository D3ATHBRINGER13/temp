package net.minecraft.world.level;

public enum BlockLayer {
    SOLID("Solid"), 
    CUTOUT_MIPPED("Mipped Cutout"), 
    CUTOUT("Cutout"), 
    TRANSLUCENT("Translucent");
    
    private final String name;
    
    private BlockLayer(final String string3) {
        this.name = string3;
    }
    
    public String toString() {
        return this.name;
    }
}
