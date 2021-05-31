package net.minecraft.client.renderer;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class LightTexture implements AutoCloseable {
    private final DynamicTexture lightTexture;
    private final NativeImage lightPixels;
    private final ResourceLocation lightTextureLocation;
    private boolean updateLightTexture;
    private float blockLightRed;
    private float blockLightRedTotal;
    private final GameRenderer renderer;
    private final Minecraft minecraft;
    
    public LightTexture(final GameRenderer dnc) {
        this.renderer = dnc;
        this.minecraft = dnc.getMinecraft();
        this.lightTexture = new DynamicTexture(16, 16, false);
        this.lightTextureLocation = this.minecraft.getTextureManager().register("light_map", this.lightTexture);
        this.lightPixels = this.lightTexture.getPixels();
    }
    
    public void close() {
        this.lightTexture.close();
    }
    
    public void tick() {
        this.blockLightRedTotal += (float)((Math.random() - Math.random()) * Math.random() * Math.random());
        this.blockLightRedTotal *= (float)0.9;
        this.blockLightRed += this.blockLightRedTotal - this.blockLightRed;
        this.updateLightTexture = true;
    }
    
    public void turnOffLightLayer() {
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    public void turnOnLightLayer() {
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        final float float2 = 0.00390625f;
        GlStateManager.scalef(0.00390625f, 0.00390625f, 0.00390625f);
        GlStateManager.translatef(8.0f, 8.0f, 8.0f);
        GlStateManager.matrixMode(5888);
        this.minecraft.getTextureManager().bind(this.lightTextureLocation);
        GlStateManager.texParameter(3553, 10241, 9729);
        GlStateManager.texParameter(3553, 10240, 9729);
        GlStateManager.texParameter(3553, 10242, 10496);
        GlStateManager.texParameter(3553, 10243, 10496);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    public void updateLightTexture(final float float1) {
        if (!this.updateLightTexture) {
            return;
        }
        this.minecraft.getProfiler().push("lightTex");
        final Level bhr3 = this.minecraft.level;
        if (bhr3 == null) {
            return;
        }
        final float float2 = bhr3.getSkyDarken(1.0f);
        final float float3 = float2 * 0.95f + 0.05f;
        final float float4 = this.minecraft.player.getWaterVision();
        float float5;
        if (this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION)) {
            float5 = this.renderer.getNightVisionScale(this.minecraft.player, float1);
        }
        else if (float4 > 0.0f && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER)) {
            float5 = float4;
        }
        else {
            float5 = 0.0f;
        }
        for (int integer8 = 0; integer8 < 16; ++integer8) {
            for (int integer9 = 0; integer9 < 16; ++integer9) {
                float float6 = bhr3.dimension.getBrightnessRamp()[integer8] * float3;
                final float float7 = bhr3.dimension.getBrightnessRamp()[integer9] * (this.blockLightRed * 0.1f + 1.5f);
                if (bhr3.getSkyFlashTime() > 0) {
                    float6 = bhr3.dimension.getBrightnessRamp()[integer8];
                }
                final float float8 = float6 * (float2 * 0.65f + 0.35f);
                final float float9 = float6 * (float2 * 0.65f + 0.35f);
                final float float10 = float6;
                final float float11 = float7;
                final float float12 = float7 * ((float7 * 0.6f + 0.4f) * 0.6f + 0.4f);
                final float float13 = float7 * (float7 * float7 * 0.6f + 0.4f);
                float float14 = float8 + float11;
                float float15 = float9 + float12;
                float float16 = float10 + float13;
                float14 = float14 * 0.96f + 0.03f;
                float15 = float15 * 0.96f + 0.03f;
                float16 = float16 * 0.96f + 0.03f;
                if (this.renderer.getDarkenWorldAmount(float1) > 0.0f) {
                    final float float17 = this.renderer.getDarkenWorldAmount(float1);
                    float14 = float14 * (1.0f - float17) + float14 * 0.7f * float17;
                    float15 = float15 * (1.0f - float17) + float15 * 0.6f * float17;
                    float16 = float16 * (1.0f - float17) + float16 * 0.6f * float17;
                }
                if (bhr3.dimension.getType() == DimensionType.THE_END) {
                    float14 = 0.22f + float11 * 0.75f;
                    float15 = 0.28f + float12 * 0.75f;
                    float16 = 0.25f + float13 * 0.75f;
                }
                if (float5 > 0.0f) {
                    float float17 = 1.0f / float14;
                    if (float17 > 1.0f / float15) {
                        float17 = 1.0f / float15;
                    }
                    if (float17 > 1.0f / float16) {
                        float17 = 1.0f / float16;
                    }
                    float14 = float14 * (1.0f - float5) + float14 * float17 * float5;
                    float15 = float15 * (1.0f - float5) + float15 * float17 * float5;
                    float16 = float16 * (1.0f - float5) + float16 * float17 * float5;
                }
                if (float14 > 1.0f) {
                    float14 = 1.0f;
                }
                if (float15 > 1.0f) {
                    float15 = 1.0f;
                }
                if (float16 > 1.0f) {
                    float16 = 1.0f;
                }
                float float17 = (float)this.minecraft.options.gamma;
                float float18 = 1.0f - float14;
                float float19 = 1.0f - float15;
                float float20 = 1.0f - float16;
                float18 = 1.0f - float18 * float18 * float18 * float18;
                float19 = 1.0f - float19 * float19 * float19 * float19;
                float20 = 1.0f - float20 * float20 * float20 * float20;
                float14 = float14 * (1.0f - float17) + float18 * float17;
                float15 = float15 * (1.0f - float17) + float19 * float17;
                float16 = float16 * (1.0f - float17) + float20 * float17;
                float14 = float14 * 0.96f + 0.03f;
                float15 = float15 * 0.96f + 0.03f;
                float16 = float16 * 0.96f + 0.03f;
                if (float14 > 1.0f) {
                    float14 = 1.0f;
                }
                if (float15 > 1.0f) {
                    float15 = 1.0f;
                }
                if (float16 > 1.0f) {
                    float16 = 1.0f;
                }
                if (float14 < 0.0f) {
                    float14 = 0.0f;
                }
                if (float15 < 0.0f) {
                    float15 = 0.0f;
                }
                if (float16 < 0.0f) {
                    float16 = 0.0f;
                }
                final int integer10 = 255;
                final int integer11 = (int)(float14 * 255.0f);
                final int integer12 = (int)(float15 * 255.0f);
                final int integer13 = (int)(float16 * 255.0f);
                this.lightPixels.setPixelRGBA(integer9, integer8, 0xFF000000 | integer13 << 16 | integer12 << 8 | integer11);
            }
        }
        this.lightTexture.upload();
        this.updateLightTexture = false;
        this.minecraft.getProfiler().pop();
    }
}
