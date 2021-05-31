package net.minecraft.world.phys;

public abstract class HitResult {
    protected final Vec3 location;
    
    protected HitResult(final Vec3 csi) {
        this.location = csi;
    }
    
    public abstract Type getType();
    
    public Vec3 getLocation() {
        return this.location;
    }
    
    public enum Type {
        MISS, 
        BLOCK, 
        ENTITY;
    }
}
