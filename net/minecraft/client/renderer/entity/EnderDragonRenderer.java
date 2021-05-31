package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.EnderDragonDeathLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.EnderDragonEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.dragon.DragonModel;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class EnderDragonRenderer extends MobRenderer<EnderDragon, DragonModel> {
    public static final ResourceLocation CRYSTAL_BEAM_LOCATION;
    private static final ResourceLocation DRAGON_EXPLODING_LOCATION;
    private static final ResourceLocation DRAGON_LOCATION;
    
    public EnderDragonRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new DragonModel(0.0f), 0.5f);
        this.addLayer(new EnderDragonEyesLayer(this));
        this.addLayer(new EnderDragonDeathLayer(this));
    }
    
    @Override
    protected void setupRotations(final EnderDragon asp, final float float2, final float float3, final float float4) {
        final float float5 = (float)asp.getLatencyPos(7, float4)[0];
        final float float6 = (float)(asp.getLatencyPos(5, float4)[1] - asp.getLatencyPos(10, float4)[1]);
        GlStateManager.rotatef(-float5, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(float6 * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.0f, 1.0f);
        if (asp.deathTime > 0) {
            float float7 = (asp.deathTime + float4 - 1.0f) / 20.0f * 1.6f;
            float7 = Mth.sqrt(float7);
            if (float7 > 1.0f) {
                float7 = 1.0f;
            }
            GlStateManager.rotatef(float7 * ((LivingEntityRenderer<EnderDragon, M>)this).getFlipDegrees(asp), 0.0f, 0.0f, 1.0f);
        }
    }
    
    @Override
    protected void renderModel(final EnderDragon asp, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        if (asp.dragonDeathTime > 0) {
            final float float8 = asp.dragonDeathTime / 200.0f;
            GlStateManager.depthFunc(515);
            GlStateManager.enableAlphaTest();
            GlStateManager.alphaFunc(516, float8);
            this.bindTexture(EnderDragonRenderer.DRAGON_EXPLODING_LOCATION);
            ((DragonModel)this.model).render(asp, float2, float3, float4, float5, float6, float7);
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.depthFunc(514);
        }
        this.bindTexture((T)asp);
        ((DragonModel)this.model).render(asp, float2, float3, float4, float5, float6, float7);
        if (asp.hurtTime > 0) {
            GlStateManager.depthFunc(514);
            GlStateManager.disableTexture();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color4f(1.0f, 0.0f, 0.0f, 0.5f);
            ((DragonModel)this.model).render(asp, float2, float3, float4, float5, float6, float7);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
            GlStateManager.depthFunc(515);
        }
    }
    
    @Override
    public void render(final EnderDragon asp, final double double2, final double double3, final double double4, final float float5, final float float6) {
        super.render(asp, double2, double3, double4, float5, float6);
        if (asp.nearestCrystal != null) {
            this.bindTexture(EnderDragonRenderer.CRYSTAL_BEAM_LOCATION);
            float float7 = Mth.sin((asp.nearestCrystal.tickCount + float6) * 0.2f) / 2.0f + 0.5f;
            float7 = (float7 * float7 + float7) * 0.2f;
            renderCrystalBeams(double2, double3, double4, float6, Mth.lerp(1.0f - float6, asp.x, asp.xo), Mth.lerp(1.0f - float6, asp.y, asp.yo), Mth.lerp(1.0f - float6, asp.z, asp.zo), asp.tickCount, asp.nearestCrystal.x, float7 + asp.nearestCrystal.y, asp.nearestCrystal.z);
        }
    }
    
    public static void renderCrystalBeams(final double double1, final double double2, final double double3, final float float4, final double double5, final double double6, final double double7, final int integer, final double double9, final double double10, final double double11) {
        final float float5 = (float)(double9 - double5);
        final float float6 = (float)(double10 - 1.0 - double6);
        final float float7 = (float)(double11 - double7);
        final float float8 = Mth.sqrt(float5 * float5 + float7 * float7);
        final float float9 = Mth.sqrt(float5 * float5 + float6 * float6 + float7 * float7);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double1, (float)double2 + 2.0f, (float)double3);
        GlStateManager.rotatef((float)(-Math.atan2((double)float7, (double)float5)) * 57.295776f - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef((float)(-Math.atan2((double)float8, (double)float6)) * 57.295776f - 90.0f, 1.0f, 0.0f, 0.0f);
        final Tesselator cuz26 = Tesselator.getInstance();
        final BufferBuilder cuw27 = cuz26.getBuilder();
        Lighting.turnOff();
        GlStateManager.disableCull();
        GlStateManager.shadeModel(7425);
        final float float10 = 0.0f - (integer + float4) * 0.01f;
        final float float11 = Mth.sqrt(float5 * float5 + float6 * float6 + float7 * float7) / 32.0f - (integer + float4) * 0.01f;
        cuw27.begin(5, DefaultVertexFormat.POSITION_TEX_COLOR);
        final int integer2 = 8;
        for (int integer3 = 0; integer3 <= 8; ++integer3) {
            final float float12 = Mth.sin(integer3 % 8 * 6.2831855f / 8.0f) * 0.75f;
            final float float13 = Mth.cos(integer3 % 8 * 6.2831855f / 8.0f) * 0.75f;
            final float float14 = integer3 % 8 / 8.0f;
            cuw27.vertex(float12 * 0.2f, float13 * 0.2f, 0.0).uv(float14, float10).color(0, 0, 0, 255).endVertex();
            cuw27.vertex(float12, float13, float9).uv(float14, float11).color(255, 255, 255, 255).endVertex();
        }
        cuz26.end();
        GlStateManager.enableCull();
        GlStateManager.shadeModel(7424);
        Lighting.turnOn();
        GlStateManager.popMatrix();
    }
    
    protected ResourceLocation getTextureLocation(final EnderDragon asp) {
        return EnderDragonRenderer.DRAGON_LOCATION;
    }
    
    static {
        CRYSTAL_BEAM_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal_beam.png");
        DRAGON_EXPLODING_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
        DRAGON_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    }
}
