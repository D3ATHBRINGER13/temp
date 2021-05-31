package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BubbleColumnUpParticle extends TextureSheetParticle {
    private BubbleColumnUpParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        super(bhr, double2, double3, double4);
        this.setSize(0.02f, 0.02f);
        this.quadSize *= this.random.nextFloat() * 0.6f + 0.2f;
        this.xd = double5 * 0.20000000298023224 + (Math.random() * 2.0 - 1.0) * 0.019999999552965164;
        this.yd = double6 * 0.20000000298023224 + (Math.random() * 2.0 - 1.0) * 0.019999999552965164;
        this.zd = double7 * 0.20000000298023224 + (Math.random() * 2.0 - 1.0) * 0.019999999552965164;
        this.lifetime = (int)(40.0 / (Math.random() * 0.8 + 0.2));
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.yd += 0.005;
        if (this.lifetime-- <= 0) {
            this.remove();
            return;
        }
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.8500000238418579;
        this.yd *= 0.8500000238418579;
        this.zd *= 0.8500000238418579;
        if (!this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).is(FluidTags.WATER)) {
            this.remove();
        }
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        
        public Provider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final BubbleColumnUpParticle dkq16 = new BubbleColumnUpParticle(bhr, double3, double4, double5, double6, double7, double8, null);
            dkq16.pickSprite(this.sprite);
            return dkq16;
        }
    }
}
