package net.minecraft.client.particle;

import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.Level;

public class NoRenderParticle extends Particle {
    protected NoRenderParticle(final Level bhr, final double double2, final double double3, final double double4) {
        super(bhr, double2, double3, double4);
    }
    
    protected NoRenderParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        super(bhr, double2, double3, double4, double5, double6, double7);
    }
    
    @Override
    public final void render(final BufferBuilder cuw, final Camera cxq, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.NO_RENDER;
    }
}
