package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class LargeSmokeParticle extends SmokeParticle {
    protected LargeSmokeParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7, final SpriteSet dma) {
        super(bhr, double2, double3, double4, double5, double6, double7, 2.5f, dma);
    }
    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        
        public Provider(final SpriteSet dma) {
            this.sprites = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            return new LargeSmokeParticle(bhr, double3, double4, double5, double6, double7, double8, this.sprites);
        }
    }
}
