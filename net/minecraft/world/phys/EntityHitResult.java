package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public class EntityHitResult extends HitResult {
    private final Entity entity;
    
    public EntityHitResult(final Entity aio) {
        this(aio, new Vec3(aio.x, aio.y, aio.z));
    }
    
    public EntityHitResult(final Entity aio, final Vec3 csi) {
        super(csi);
        this.entity = aio;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    @Override
    public Type getType() {
        return Type.ENTITY;
    }
}
