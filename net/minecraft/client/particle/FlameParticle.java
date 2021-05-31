package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class FlameParticle extends TextureSheetParticle {
    private FlameParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        super(bhr, double2, double3, double4, double5, double6, double7);
        this.xd = this.xd * 0.009999999776482582 + double5;
        this.yd = this.yd * 0.009999999776482582 + double6;
        this.zd = this.zd * 0.009999999776482582 + double7;
        this.x += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;
        this.y += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;
        this.z += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
    
    @Override
    public void move(final double double1, final double double2, final double double3) {
        this.setBoundingBox(this.getBoundingBox().move(double1, double2, double3));
        this.setLocationFromBoundingbox();
    }
    
    @Override
    public float getQuadSize(final float float1) {
        final float float2 = (this.age + float1) / this.lifetime;
        return this.quadSize * (1.0f - float2 * float2 * 0.5f);
    }
    
    public int getLightColor(final float float1) {
        float float2 = (this.age + float1) / this.lifetime;
        float2 = Mth.clamp(float2, 0.0f, 1.0f);
        final int integer4 = super.getLightColor(float1);
        int integer5 = integer4 & 0xFF;
        final int integer6 = integer4 >> 16 & 0xFF;
        integer5 += (int)(float2 * 15.0f * 16.0f);
        if (integer5 > 240) {
            integer5 = 240;
        }
        return integer5 | integer6 << 16;
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
        this.xd *= 0.9599999785423279;
        this.yd *= 0.9599999785423279;
        this.zd *= 0.9599999785423279;
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
            final FlameParticle dld16 = new FlameParticle(bhr, double3, double4, double5, double6, double7, double8, null);
            dld16.pickSprite(this.sprite);
            return dld16;
        }
    }
}
