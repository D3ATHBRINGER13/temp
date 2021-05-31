package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class BarrierParticle extends TextureSheetParticle {
    private BarrierParticle(final Level bhr, final double double2, final double double3, final double double4, final ItemLike bhq) {
        super(bhr, double2, double3, double4);
        this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelShaper().getParticleIcon(bhq));
        this.gravity = 0.0f;
        this.lifetime = 80;
        this.hasPhysics = false;
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }
    
    @Override
    public float getQuadSize(final float float1) {
        return 0.5f;
    }
    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(final SimpleParticleType gi, final Level bhr, final double double3, final double double4, final double double5, final double double6, final double double7, final double double8) {
            return new BarrierParticle(bhr, double3, double4, double5, Blocks.BARRIER.asItem(), null);
        }
    }
}
