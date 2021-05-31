package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class SplashParticle extends WaterDropParticle {
    private SplashParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        super(bhr, double2, double3, double4);
        this.gravity = 0.04f;
        if (double6 == 0.0 && (double5 != 0.0 || double7 != 0.0)) {
            this.xd = double5;
            this.yd = 0.1;
            this.zd = double7;
        }
    }
    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public Provider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final SplashParticle dlz16 = new SplashParticle(bhr, double3, double4, double5, double6, double7, double8, null);
            dlz16.pickSprite(this.sprite);
            return dlz16;
        }
    }
}
