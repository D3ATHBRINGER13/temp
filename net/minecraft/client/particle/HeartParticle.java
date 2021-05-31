package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class HeartParticle extends TextureSheetParticle {
    private HeartParticle(final Level bhr, final double double2, final double double3, final double double4) {
        super(bhr, double2, double3, double4, 0.0, 0.0, 0.0);
        this.xd *= 0.009999999776482582;
        this.yd *= 0.009999999776482582;
        this.zd *= 0.009999999776482582;
        this.yd += 0.1;
        this.quadSize *= 1.5f;
        this.lifetime = 16;
        this.hasPhysics = false;
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
    
    @Override
    public float getQuadSize(final float float1) {
        return this.quadSize * Mth.clamp((this.age + float1) / this.lifetime * 32.0f, 0.0f, 1.0f);
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.move(this.xd, this.yd, this.zd);
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= 0.8600000143051147;
        this.yd *= 0.8600000143051147;
        this.zd *= 0.8600000143051147;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }
    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public Provider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final HeartParticle dle16 = new HeartParticle(bhr, double3, double4, double5, null);
            dle16.pickSprite(this.sprite);
            return dle16;
        }
    }
    
    public static class AngryVillagerProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public AngryVillagerProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final HeartParticle dle16 = new HeartParticle(bhr, double3, double4 + 0.5, double5, null);
            dle16.pickSprite(this.sprite);
            dle16.setColor(1.0f, 1.0f, 1.0f);
            return dle16;
        }
    }
}
