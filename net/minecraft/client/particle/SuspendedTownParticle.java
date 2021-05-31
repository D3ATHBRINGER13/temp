package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class SuspendedTownParticle extends TextureSheetParticle {
    private SuspendedTownParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        super(bhr, double2, double3, double4, double5, double6, double7);
        final float float15 = this.random.nextFloat() * 0.1f + 0.2f;
        this.rCol = float15;
        this.gCol = float15;
        this.bCol = float15;
        this.setSize(0.02f, 0.02f);
        this.quadSize *= this.random.nextFloat() * 0.6f + 0.5f;
        this.xd *= 0.019999999552965164;
        this.yd *= 0.019999999552965164;
        this.zd *= 0.019999999552965164;
        this.lifetime = (int)(20.0 / (Math.random() * 0.8 + 0.2));
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
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
            return;
        }
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.99;
        this.yd *= 0.99;
        this.zd *= 0.99;
    }
    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public Provider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final SuspendedTownParticle dmd16 = new SuspendedTownParticle(bhr, double3, double4, double5, double6, double7, double8, null);
            dmd16.pickSprite(this.sprite);
            return dmd16;
        }
    }
    
    public static class HappyVillagerProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public HappyVillagerProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final SuspendedTownParticle dmd16 = new SuspendedTownParticle(bhr, double3, double4, double5, double6, double7, double8, null);
            dmd16.pickSprite(this.sprite);
            dmd16.setColor(1.0f, 1.0f, 1.0f);
            return dmd16;
        }
    }
    
    public static class ComposterFillProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public ComposterFillProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final SuspendedTownParticle dmd16 = new SuspendedTownParticle(bhr, double3, double4, double5, double6, double7, double8, null);
            dmd16.pickSprite(this.sprite);
            dmd16.setColor(1.0f, 1.0f, 1.0f);
            dmd16.setLifetime(3 + bhr.getRandom().nextInt(5));
            return dmd16;
        }
    }
    
    public static class DolphinSpeedProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public DolphinSpeedProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final SuspendedTownParticle dmd16 = new SuspendedTownParticle(bhr, double3, double4, double5, double6, double7, double8, null);
            dmd16.setColor(0.3f, 0.5f, 1.0f);
            dmd16.pickSprite(this.sprite);
            dmd16.setAlpha(1.0f - bhr.random.nextFloat() * 0.7f);
            dmd16.setLifetime(dmd16.getLifetime() / 2);
            return dmd16;
        }
    }
}
