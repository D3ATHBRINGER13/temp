package net.minecraft.client.particle;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;

public class TrackingEmitter extends NoRenderParticle {
    private final Entity entity;
    private int life;
    private final int lifeTime;
    private final ParticleOptions particleType;
    
    public TrackingEmitter(final Level bhr, final Entity aio, final ParticleOptions gf) {
        this(bhr, aio, gf, 3);
    }
    
    public TrackingEmitter(final Level bhr, final Entity aio, final ParticleOptions gf, final int integer) {
        this(bhr, aio, gf, integer, aio.getDeltaMovement());
    }
    
    private TrackingEmitter(final Level bhr, final Entity aio, final ParticleOptions gf, final int integer, final Vec3 csi) {
        super(bhr, aio.x, aio.getBoundingBox().minY + aio.getBbHeight() / 2.0f, aio.z, csi.x, csi.y, csi.z);
        this.entity = aio;
        this.lifeTime = integer;
        this.particleType = gf;
        this.tick();
    }
    
    @Override
    public void tick() {
        for (int integer2 = 0; integer2 < 16; ++integer2) {
            final double double3 = this.random.nextFloat() * 2.0f - 1.0f;
            final double double4 = this.random.nextFloat() * 2.0f - 1.0f;
            final double double5 = this.random.nextFloat() * 2.0f - 1.0f;
            if (double3 * double3 + double4 * double4 + double5 * double5 <= 1.0) {
                final double double6 = this.entity.x + double3 * this.entity.getBbWidth() / 4.0;
                final double double7 = this.entity.getBoundingBox().minY + this.entity.getBbHeight() / 2.0f + double4 * this.entity.getBbHeight() / 4.0;
                final double double8 = this.entity.z + double5 * this.entity.getBbWidth() / 4.0;
                this.level.addParticle(this.particleType, false, double6, double7, double8, double3, double4 + 0.2, double5);
            }
        }
        ++this.life;
        if (this.life >= this.lifeTime) {
            this.remove();
        }
    }
}
