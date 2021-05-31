package net.minecraft.client.particle;

import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.Level;

public abstract class SingleQuadParticle extends Particle {
    protected float quadSize;
    
    protected SingleQuadParticle(final Level bhr, final double double2, final double double3, final double double4) {
        super(bhr, double2, double3, double4);
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }
    
    protected SingleQuadParticle(final Level bhr, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7) {
        super(bhr, double2, double3, double4, double5, double6, double7);
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }
    
    @Override
    public void render(final BufferBuilder cuw, final Camera cxq, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final float float9 = this.getQuadSize(float3);
        final float float10 = this.getU0();
        final float float11 = this.getU1();
        final float float12 = this.getV0();
        final float float13 = this.getV1();
        final float float14 = (float)(Mth.lerp(float3, this.xo, this.x) - SingleQuadParticle.xOff);
        final float float15 = (float)(Mth.lerp(float3, this.yo, this.y) - SingleQuadParticle.yOff);
        final float float16 = (float)(Mth.lerp(float3, this.zo, this.z) - SingleQuadParticle.zOff);
        final int integer18 = this.getLightColor(float3);
        final int integer19 = integer18 >> 16 & 0xFFFF;
        final int integer20 = integer18 & 0xFFFF;
        final Vec3[] arr21 = { new Vec3(-float4 * float9 - float7 * float9, -float5 * float9, -float6 * float9 - float8 * float9), new Vec3(-float4 * float9 + float7 * float9, float5 * float9, -float6 * float9 + float8 * float9), new Vec3(float4 * float9 + float7 * float9, float5 * float9, float6 * float9 + float8 * float9), new Vec3(float4 * float9 - float7 * float9, -float5 * float9, float6 * float9 - float8 * float9) };
        if (this.roll != 0.0f) {
            final float float17 = Mth.lerp(float3, this.oRoll, this.roll);
            final float float18 = Mth.cos(float17 * 0.5f);
            final float float19 = (float)(Mth.sin(float17 * 0.5f) * cxq.getLookVector().x);
            final float float20 = (float)(Mth.sin(float17 * 0.5f) * cxq.getLookVector().y);
            final float float21 = (float)(Mth.sin(float17 * 0.5f) * cxq.getLookVector().z);
            final Vec3 csi27 = new Vec3(float19, float20, float21);
            for (int integer21 = 0; integer21 < 4; ++integer21) {
                arr21[integer21] = csi27.scale(2.0 * arr21[integer21].dot(csi27)).add(arr21[integer21].scale(float18 * float18 - csi27.dot(csi27))).add(csi27.cross(arr21[integer21]).scale(2.0f * float18));
            }
        }
        cuw.vertex(float14 + arr21[0].x, float15 + arr21[0].y, float16 + arr21[0].z).uv(float11, float13).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(integer19, integer20).endVertex();
        cuw.vertex(float14 + arr21[1].x, float15 + arr21[1].y, float16 + arr21[1].z).uv(float11, float12).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(integer19, integer20).endVertex();
        cuw.vertex(float14 + arr21[2].x, float15 + arr21[2].y, float16 + arr21[2].z).uv(float10, float12).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(integer19, integer20).endVertex();
        cuw.vertex(float14 + arr21[3].x, float15 + arr21[3].y, float16 + arr21[3].z).uv(float10, float13).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(integer19, integer20).endVertex();
    }
    
    public float getQuadSize(final float float1) {
        return this.quadSize;
    }
    
    @Override
    public Particle scale(final float float1) {
        this.quadSize *= float1;
        return super.scale(float1);
    }
    
    protected abstract float getU0();
    
    protected abstract float getU1();
    
    protected abstract float getV0();
    
    protected abstract float getV1();
}
