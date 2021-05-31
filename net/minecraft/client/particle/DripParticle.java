package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class DripParticle extends TextureSheetParticle {
    private final Fluid type;
    
    private DripParticle(final Level bhr, final double double2, final double double3, final double double4, final Fluid clj) {
        super(bhr, double2, double3, double4);
        this.setSize(0.01f, 0.01f);
        this.gravity = 0.06f;
        this.type = clj;
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
    
    public int getLightColor(final float float1) {
        if (this.type.is(FluidTags.LAVA)) {
            return 240;
        }
        return super.getLightColor(float1);
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.preMoveUpdate();
        if (this.removed) {
            return;
        }
        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.postMoveUpdate();
        if (this.removed) {
            return;
        }
        this.xd *= 0.9800000190734863;
        this.yd *= 0.9800000190734863;
        this.zd *= 0.9800000190734863;
        final BlockPos ew2 = new BlockPos(this.x, this.y, this.z);
        final FluidState clk3 = this.level.getFluidState(ew2);
        if (clk3.getType() == this.type && this.y < ew2.getY() + clk3.getHeight(this.level, ew2)) {
            this.remove();
        }
    }
    
    protected void preMoveUpdate() {
        if (this.lifetime-- <= 0) {
            this.remove();
        }
    }
    
    protected void postMoveUpdate() {
    }
    
    static class DripHangParticle extends DripParticle {
        private final ParticleOptions fallingParticle;
        
        private DripHangParticle(final Level bhr, final double double2, final double double3, final double double4, final Fluid clj, final ParticleOptions gf) {
            super(bhr, double2, double3, double4, clj, null);
            this.fallingParticle = gf;
            this.gravity *= 0.02f;
            this.lifetime = 40;
        }
        
        @Override
        protected void preMoveUpdate() {
            if (this.lifetime-- <= 0) {
                this.remove();
                this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }
        }
        
        @Override
        protected void postMoveUpdate() {
            this.xd *= 0.02;
            this.yd *= 0.02;
            this.zd *= 0.02;
        }
    }
    
    static class CoolingDripHangParticle extends DripHangParticle {
        private CoolingDripHangParticle(final Level bhr, final double double2, final double double3, final double double4, final Fluid clj, final ParticleOptions gf) {
            super(bhr, double2, double3, double4, clj, gf);
        }
        
        @Override
        protected void preMoveUpdate() {
            this.rCol = 1.0f;
            this.gCol = 16.0f / (40 - this.lifetime + 16);
            this.bCol = 4.0f / (40 - this.lifetime + 8);
            super.preMoveUpdate();
        }
    }
    
    static class DripFallParticle extends DripParticle {
        private final ParticleOptions landParticle;
        
        private DripFallParticle(final Level bhr, final double double2, final double double3, final double double4, final Fluid clj, final ParticleOptions gf) {
            super(bhr, double2, double3, double4, clj, null);
            this.landParticle = gf;
            this.lifetime = (int)(64.0 / (Math.random() * 0.8 + 0.2));
        }
        
        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }
        }
    }
    
    static class DripLandParticle extends DripParticle {
        private DripLandParticle(final Level bhr, final double double2, final double double3, final double double4, final Fluid clj) {
            super(bhr, double2, double3, double4, clj, null);
            this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        }
    }
    
    public static class WaterHangProvider implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;
        
        public WaterHangProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final DripParticle dkw16 = new DripHangParticle(bhr, double3, double4, double5, (Fluid)Fluids.WATER, (ParticleOptions)ParticleTypes.FALLING_WATER);
            dkw16.setColor(0.2f, 0.3f, 1.0f);
            dkw16.pickSprite(this.sprite);
            return dkw16;
        }
    }
    
    public static class WaterFallProvider implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;
        
        public WaterFallProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final DripParticle dkw16 = new DripFallParticle(bhr, double3, double4, double5, (Fluid)Fluids.WATER, (ParticleOptions)ParticleTypes.SPLASH);
            dkw16.setColor(0.2f, 0.3f, 1.0f);
            dkw16.pickSprite(this.sprite);
            return dkw16;
        }
    }
    
    public static class LavaHangProvider implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;
        
        public LavaHangProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final CoolingDripHangParticle a16 = new CoolingDripHangParticle(bhr, double3, double4, double5, (Fluid)Fluids.LAVA, (ParticleOptions)ParticleTypes.FALLING_LAVA);
            a16.pickSprite(this.sprite);
            return a16;
        }
    }
    
    public static class LavaFallProvider implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;
        
        public LavaFallProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final DripParticle dkw16 = new DripFallParticle(bhr, double3, double4, double5, (Fluid)Fluids.LAVA, (ParticleOptions)ParticleTypes.LANDING_LAVA);
            dkw16.setColor(1.0f, 0.2857143f, 0.083333336f);
            dkw16.pickSprite(this.sprite);
            return dkw16;
        }
    }
    
    public static class LavaLandProvider implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;
        
        public LavaLandProvider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final DripParticle dkw16 = new DripLandParticle(bhr, double3, double4, double5, (Fluid)Fluids.LAVA);
            dkw16.setColor(1.0f, 0.2857143f, 0.083333336f);
            dkw16.pickSprite(this.sprite);
            return dkw16;
        }
    }
}
