package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class CampfireSmokeParticle extends TextureSheetParticle {
    private CampfireSmokeParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7, final boolean boolean8) {
        super(bhr, double2, double3, double4);
        this.scale(3.0f);
        this.setSize(0.25f, 0.25f);
        if (boolean8) {
            this.lifetime = this.random.nextInt(50) + 280;
        }
        else {
            this.lifetime = this.random.nextInt(50) + 80;
        }
        this.gravity = 3.0E-6f;
        this.xd = double5;
        this.yd = double6 + this.random.nextFloat() / 500.0f;
        this.zd = double7;
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime || this.alpha <= 0.0f) {
            this.remove();
            return;
        }
        this.xd += this.random.nextFloat() / 5000.0f * (this.random.nextBoolean() ? 1 : -1);
        this.zd += this.random.nextFloat() / 5000.0f * (this.random.nextBoolean() ? 1 : -1);
        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);
        if (this.age >= this.lifetime - 60 && this.alpha > 0.01f) {
            this.alpha -= 0.015f;
        }
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    public static class CosyProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        
        public CosyProvider(final SpriteSet dma) {
            this.sprites = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final CampfireSmokeParticle dkt16 = new CampfireSmokeParticle(bhr, double3, double4, double5, double6, double7, double8, false, null);
            dkt16.setAlpha(0.9f);
            dkt16.pickSprite(this.sprites);
            return dkt16;
        }
    }
    
    public static class SignalProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        
        public SignalProvider(final SpriteSet dma) {
            this.sprites = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final CampfireSmokeParticle dkt16 = new CampfireSmokeParticle(bhr, double3, double4, double5, double6, double7, double8, true, null);
            dkt16.setAlpha(0.95f);
            dkt16.pickSprite(this.sprites);
            return dkt16;
        }
    }
}
