package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.Level;

public class DustParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    
    private DustParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7, final DustParticleOptions gd, final SpriteSet dma) {
        super(bhr, double2, double3, double4, double5, double6, double7);
        this.sprites = dma;
        this.xd *= 0.10000000149011612;
        this.yd *= 0.10000000149011612;
        this.zd *= 0.10000000149011612;
        final float float17 = (float)Math.random() * 0.4f + 0.6f;
        this.rCol = ((float)(Math.random() * 0.20000000298023224) + 0.8f) * gd.getR() * float17;
        this.gCol = ((float)(Math.random() * 0.20000000298023224) + 0.8f) * gd.getG() * float17;
        this.bCol = ((float)(Math.random() * 0.20000000298023224) + 0.8f) * gd.getB() * float17;
        this.quadSize *= 0.75f * gd.getScale();
        final int integer18 = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.lifetime = (int)Math.max(integer18 * gd.getScale(), 1.0f);
        this.setSpriteFromAge(dma);
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
        this.setSpriteFromAge(this.sprites);
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
    
    public static class Provider implements ParticleProvider<DustParticleOptions> {
        private final SpriteSet sprites;
        
        public Provider(final SpriteSet dma) {
            this.sprites = dma;
        }
        
        public Particle createParticle(final DustParticleOptions gd, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            return new DustParticle(bhr, double3, double4, double5, double6, double7, double8, gd, this.sprites, null);
        }
    }
}
