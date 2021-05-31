package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class SpitParticle extends ExplodeParticle {
    private SpitParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7, final SpriteSet dma) {
        super(bhr, double2, double3, double4, double5, double6, double7, dma);
        this.gravity = 0.5f;
    }
    
    @Override
    public void tick() {
        super.tick();
        this.yd -= 0.004 + 0.04 * this.gravity;
    }
    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        
        public Provider(final SpriteSet dma) {
            this.sprites = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            return new SpitParticle(bhr, double3, double4, double5, double6, double7, double8, this.sprites, null);
        }
    }
}
