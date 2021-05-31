package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class TotemParticle extends SimpleAnimatedParticle {
    private TotemParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7, final SpriteSet dma) {
        super(bhr, double2, double3, double4, dma, -0.05f);
        this.xd = double5;
        this.yd = double6;
        this.zd = double7;
        this.quadSize *= 0.75f;
        this.lifetime = 60 + this.random.nextInt(12);
        this.setSpriteFromAge(dma);
        if (this.random.nextInt(4) == 0) {
            this.setColor(0.6f + this.random.nextFloat() * 0.2f, 0.6f + this.random.nextFloat() * 0.3f, this.random.nextFloat() * 0.2f);
        }
        else {
            this.setColor(0.1f + this.random.nextFloat() * 0.2f, 0.4f + this.random.nextFloat() * 0.3f, this.random.nextFloat() * 0.2f);
        }
        this.setBaseAirFriction(0.6f);
    }
    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        
        public Provider(final SpriteSet dma) {
            this.sprites = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            return new TotemParticle(bhr, double3, double4, double5, double6, double7, double8, this.sprites, null);
        }
    }
}
