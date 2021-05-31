package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import java.util.Random;

public class SpellParticle extends TextureSheetParticle {
    private static final Random RANDOM;
    private final SpriteSet sprites;
    
    private SpellParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7, final SpriteSet dma) {
        super(bhr, double2, double3, double4, 0.5 - SpellParticle.RANDOM.nextDouble(), double6, 0.5 - SpellParticle.RANDOM.nextDouble());
        this.sprites = dma;
        this.yd *= 0.20000000298023224;
        if (double5 == 0.0 && double7 == 0.0) {
            this.xd *= 0.10000000149011612;
            this.zd *= 0.10000000149011612;
        }
        this.quadSize *= 0.75f;
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
        this.setSpriteFromAge(dma);
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
        this.setSpriteFromAge(this.sprites);
        this.yd += 0.004;
        this.move(this.xd, this.yd, this.zd);
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= 0.9599999785423279;
        this.yd *= 0.9599999785423279;
        this.zd *= 0.9599999785423279;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }
    
    static {
        RANDOM = new Random();
    }
    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public Provider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            return new SpellParticle(bhr, double3, double4, double5, double6, double7, double8, this.sprite, null);
        }
    }
    
    public static class MobProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public MobProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final Particle dln16 = new SpellParticle(bhr, double3, double4, double5, double6, double7, double8, this.sprite, null);
            dln16.setColor((float)double6, (float)double7, (float)double8);
            return dln16;
        }
    }
    
    public static class AmbientMobProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public AmbientMobProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final Particle dln16 = new SpellParticle(bhr, double3, double4, double5, double6, double7, double8, this.sprite, null);
            dln16.setAlpha(0.15f);
            dln16.setColor((float)double6, (float)double7, (float)double8);
            return dln16;
        }
    }
    
    public static class WitchProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public WitchProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final SpellParticle dlx16 = new SpellParticle(bhr, double3, double4, double5, double6, double7, double8, this.sprite, null);
            final float float17 = bhr.random.nextFloat() * 0.5f + 0.35f;
            dlx16.setColor(1.0f * float17, 0.0f * float17, 1.0f * float17);
            return dlx16;
        }
    }
    
    public static class InstantProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public InstantProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            return new SpellParticle(bhr, double3, double4, double5, double6, double7, double8, this.sprite, null);
        }
    }
}
