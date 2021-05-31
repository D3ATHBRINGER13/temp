package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class FallingDustParticle extends TextureSheetParticle {
    private final float rotSpeed;
    private final SpriteSet sprites;
    
    private FallingDustParticle(final Level bhr, final double double2, final double double3, final double double4, final float float5, final float float6, final float float7, final SpriteSet dma) {
        super(bhr, double2, double3, double4);
        this.sprites = dma;
        this.rCol = float5;
        this.gCol = float6;
        this.bCol = float7;
        final float float8 = 0.9f;
        this.quadSize *= 0.67499995f;
        final int integer14 = (int)(32.0 / (Math.random() * 0.8 + 0.2));
        this.lifetime = (int)Math.max(integer14 * 0.9f, 1.0f);
        this.setSpriteFromAge(dma);
        this.rotSpeed = ((float)Math.random() - 0.5f) * 0.1f;
        this.roll = (float)Math.random() * 6.2831855f;
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
        this.oRoll = this.roll;
        this.roll += 3.1415927f * this.rotSpeed * 2.0f;
        if (this.onGround) {
            final float n = 0.0f;
            this.roll = n;
            this.oRoll = n;
        }
        this.move(this.xd, this.yd, this.zd);
        this.yd -= 0.003000000026077032;
        this.yd = Math.max(this.yd, -0.14000000059604645);
    }
    
    public static class Provider implements ParticleProvider<BlockParticleOption> {
        private final SpriteSet sprite;
        
        public Provider(final SpriteSet dma) {
            this.sprite = dma;
        }
        
        @Nullable
        public Particle createParticle(final BlockParticleOption gc, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            final BlockState bvt16 = gc.getState();
            if (!bvt16.isAir() && bvt16.getRenderShape() == RenderShape.INVISIBLE) {
                return null;
            }
            int integer17 = Minecraft.getInstance().getBlockColors().getColor(bvt16, bhr, new BlockPos(double3, double4, double5));
            if (bvt16.getBlock() instanceof FallingBlock) {
                integer17 = ((FallingBlock)bvt16.getBlock()).getDustColor(bvt16);
            }
            final float float18 = (integer17 >> 16 & 0xFF) / 255.0f;
            final float float19 = (integer17 >> 8 & 0xFF) / 255.0f;
            final float float20 = (integer17 & 0xFF) / 255.0f;
            return new FallingDustParticle(bhr, double3, double4, double5, float18, float19, float20, this.sprite, null);
        }
    }
}
