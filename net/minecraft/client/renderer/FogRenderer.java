package net.minecraft.client.renderer;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.core.BlockPos;
import net.minecraft.Util;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.Level;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.tags.FluidTags;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.platform.MemoryTracker;
import net.minecraft.client.Minecraft;
import java.nio.FloatBuffer;

public class FogRenderer {
    private final FloatBuffer blackBuffer;
    private final FloatBuffer colorBuffer;
    private float fogRed;
    private float fogGreen;
    private float fogBlue;
    private float oldRed;
    private float oldGreen;
    private float oldBlue;
    private int targetBiomeFog;
    private int previousBiomeFog;
    private long biomeChangedTime;
    private final GameRenderer renderer;
    private final Minecraft minecraft;
    
    public FogRenderer(final GameRenderer dnc) {
        this.blackBuffer = MemoryTracker.createFloatBuffer(16);
        this.colorBuffer = MemoryTracker.createFloatBuffer(16);
        this.oldRed = -1.0f;
        this.oldGreen = -1.0f;
        this.oldBlue = -1.0f;
        this.targetBiomeFog = -1;
        this.previousBiomeFog = -1;
        this.biomeChangedTime = -1L;
        this.renderer = dnc;
        this.minecraft = dnc.getMinecraft();
        this.blackBuffer.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
    }
    
    public void setupClearColor(final Camera cxq, final float float2) {
        final Level bhr4 = this.minecraft.level;
        final FluidState clk5 = cxq.getFluidInCamera();
        if (clk5.is(FluidTags.WATER)) {
            this.setWaterFogColor(cxq, bhr4);
        }
        else if (clk5.is(FluidTags.LAVA)) {
            this.fogRed = 0.6f;
            this.fogGreen = 0.1f;
            this.fogBlue = 0.0f;
            this.biomeChangedTime = -1L;
        }
        else {
            this.setLandFogColor(cxq, bhr4, float2);
            this.biomeChangedTime = -1L;
        }
        double double6 = cxq.getPosition().y * bhr4.dimension.getClearColorScale();
        if (cxq.getEntity() instanceof LivingEntity && ((LivingEntity)cxq.getEntity()).hasEffect(MobEffects.BLINDNESS)) {
            final int integer8 = ((LivingEntity)cxq.getEntity()).getEffect(MobEffects.BLINDNESS).getDuration();
            if (integer8 < 20) {
                double6 *= 1.0f - integer8 / 20.0f;
            }
            else {
                double6 = 0.0;
            }
        }
        if (double6 < 1.0) {
            if (double6 < 0.0) {
                double6 = 0.0;
            }
            double6 *= double6;
            this.fogRed *= (float)double6;
            this.fogGreen *= (float)double6;
            this.fogBlue *= (float)double6;
        }
        if (this.renderer.getDarkenWorldAmount(float2) > 0.0f) {
            final float float3 = this.renderer.getDarkenWorldAmount(float2);
            this.fogRed = this.fogRed * (1.0f - float3) + this.fogRed * 0.7f * float3;
            this.fogGreen = this.fogGreen * (1.0f - float3) + this.fogGreen * 0.6f * float3;
            this.fogBlue = this.fogBlue * (1.0f - float3) + this.fogBlue * 0.6f * float3;
        }
        if (clk5.is(FluidTags.WATER)) {
            float float3 = 0.0f;
            if (cxq.getEntity() instanceof LocalPlayer) {
                final LocalPlayer dmp9 = (LocalPlayer)cxq.getEntity();
                float3 = dmp9.getWaterVision();
            }
            float float4 = 1.0f / this.fogRed;
            if (float4 > 1.0f / this.fogGreen) {
                float4 = 1.0f / this.fogGreen;
            }
            if (float4 > 1.0f / this.fogBlue) {
                float4 = 1.0f / this.fogBlue;
            }
            this.fogRed = this.fogRed * (1.0f - float3) + this.fogRed * float4 * float3;
            this.fogGreen = this.fogGreen * (1.0f - float3) + this.fogGreen * float4 * float3;
            this.fogBlue = this.fogBlue * (1.0f - float3) + this.fogBlue * float4 * float3;
        }
        else if (cxq.getEntity() instanceof LivingEntity && ((LivingEntity)cxq.getEntity()).hasEffect(MobEffects.NIGHT_VISION)) {
            final float float3 = this.renderer.getNightVisionScale((LivingEntity)cxq.getEntity(), float2);
            float float4 = 1.0f / this.fogRed;
            if (float4 > 1.0f / this.fogGreen) {
                float4 = 1.0f / this.fogGreen;
            }
            if (float4 > 1.0f / this.fogBlue) {
                float4 = 1.0f / this.fogBlue;
            }
            this.fogRed = this.fogRed * (1.0f - float3) + this.fogRed * float4 * float3;
            this.fogGreen = this.fogGreen * (1.0f - float3) + this.fogGreen * float4 * float3;
            this.fogBlue = this.fogBlue * (1.0f - float3) + this.fogBlue * float4 * float3;
        }
        GlStateManager.clearColor(this.fogRed, this.fogGreen, this.fogBlue, 0.0f);
    }
    
    private void setLandFogColor(final Camera cxq, final Level bhr, final float float3) {
        float float4 = 0.25f + 0.75f * this.minecraft.options.renderDistance / 32.0f;
        float4 = 1.0f - (float)Math.pow((double)float4, 0.25);
        final Vec3 csi6 = bhr.getSkyColor(cxq.getBlockPosition(), float3);
        final float float5 = (float)csi6.x;
        final float float6 = (float)csi6.y;
        final float float7 = (float)csi6.z;
        final Vec3 csi7 = bhr.getFogColor(float3);
        this.fogRed = (float)csi7.x;
        this.fogGreen = (float)csi7.y;
        this.fogBlue = (float)csi7.z;
        if (this.minecraft.options.renderDistance >= 4) {
            final double double11 = (Mth.sin(bhr.getSunAngle(float3)) > 0.0f) ? -1.0 : 1.0;
            final Vec3 csi8 = new Vec3(double11, 0.0, 0.0);
            float float8 = (float)cxq.getLookVector().dot(csi8);
            if (float8 < 0.0f) {
                float8 = 0.0f;
            }
            if (float8 > 0.0f) {
                final float[] arr15 = bhr.dimension.getSunriseColor(bhr.getTimeOfDay(float3), float3);
                if (arr15 != null) {
                    float8 *= arr15[3];
                    this.fogRed = this.fogRed * (1.0f - float8) + arr15[0] * float8;
                    this.fogGreen = this.fogGreen * (1.0f - float8) + arr15[1] * float8;
                    this.fogBlue = this.fogBlue * (1.0f - float8) + arr15[2] * float8;
                }
            }
        }
        this.fogRed += (float5 - this.fogRed) * float4;
        this.fogGreen += (float6 - this.fogGreen) * float4;
        this.fogBlue += (float7 - this.fogBlue) * float4;
        final float float9 = bhr.getRainLevel(float3);
        if (float9 > 0.0f) {
            final float float10 = 1.0f - float9 * 0.5f;
            final float float11 = 1.0f - float9 * 0.4f;
            this.fogRed *= float10;
            this.fogGreen *= float10;
            this.fogBlue *= float11;
        }
        final float float10 = bhr.getThunderLevel(float3);
        if (float10 > 0.0f) {
            final float float11 = 1.0f - float10 * 0.5f;
            this.fogRed *= float11;
            this.fogGreen *= float11;
            this.fogBlue *= float11;
        }
    }
    
    private void setWaterFogColor(final Camera cxq, final LevelReader bhu) {
        final long long4 = Util.getMillis();
        final int integer6 = bhu.getBiome(new BlockPos(cxq.getPosition())).getWaterFogColor();
        if (this.biomeChangedTime < 0L) {
            this.targetBiomeFog = integer6;
            this.previousBiomeFog = integer6;
            this.biomeChangedTime = long4;
        }
        final int integer7 = this.targetBiomeFog >> 16 & 0xFF;
        final int integer8 = this.targetBiomeFog >> 8 & 0xFF;
        final int integer9 = this.targetBiomeFog & 0xFF;
        final int integer10 = this.previousBiomeFog >> 16 & 0xFF;
        final int integer11 = this.previousBiomeFog >> 8 & 0xFF;
        final int integer12 = this.previousBiomeFog & 0xFF;
        final float float13 = Mth.clamp((long4 - this.biomeChangedTime) / 5000.0f, 0.0f, 1.0f);
        final float float14 = Mth.lerp(float13, (float)integer10, (float)integer7);
        final float float15 = Mth.lerp(float13, (float)integer11, (float)integer8);
        final float float16 = Mth.lerp(float13, (float)integer12, (float)integer9);
        this.fogRed = float14 / 255.0f;
        this.fogGreen = float15 / 255.0f;
        this.fogBlue = float16 / 255.0f;
        if (this.targetBiomeFog != integer6) {
            this.targetBiomeFog = integer6;
            this.previousBiomeFog = (Mth.floor(float14) << 16 | Mth.floor(float15) << 8 | Mth.floor(float16));
            this.biomeChangedTime = long4;
        }
    }
    
    public void setupFog(final Camera cxq, final int integer) {
        this.resetFogColor(false);
        GlStateManager.normal3f(0.0f, -1.0f, 0.0f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final FluidState clk4 = cxq.getFluidInCamera();
        if (cxq.getEntity() instanceof LivingEntity && ((LivingEntity)cxq.getEntity()).hasEffect(MobEffects.BLINDNESS)) {
            float float5 = 5.0f;
            final int integer2 = ((LivingEntity)cxq.getEntity()).getEffect(MobEffects.BLINDNESS).getDuration();
            if (integer2 < 20) {
                float5 = Mth.lerp(1.0f - integer2 / 20.0f, 5.0f, this.renderer.getRenderDistance());
            }
            GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
            if (integer == -1) {
                GlStateManager.fogStart(0.0f);
                GlStateManager.fogEnd(float5 * 0.8f);
            }
            else {
                GlStateManager.fogStart(float5 * 0.25f);
                GlStateManager.fogEnd(float5);
            }
            GLX.setupNvFogDistance();
        }
        else if (clk4.is(FluidTags.WATER)) {
            GlStateManager.fogMode(GlStateManager.FogMode.EXP2);
            if (cxq.getEntity() instanceof LivingEntity) {
                if (cxq.getEntity() instanceof LocalPlayer) {
                    final LocalPlayer dmp5 = (LocalPlayer)cxq.getEntity();
                    float float6 = 0.05f - dmp5.getWaterVision() * dmp5.getWaterVision() * 0.03f;
                    final Biome bio7 = dmp5.level.getBiome(new BlockPos(dmp5));
                    if (bio7 == Biomes.SWAMP || bio7 == Biomes.SWAMP_HILLS) {
                        float6 += 0.005f;
                    }
                    GlStateManager.fogDensity(float6);
                }
                else {
                    GlStateManager.fogDensity(0.05f);
                }
            }
            else {
                GlStateManager.fogDensity(0.1f);
            }
        }
        else if (clk4.is(FluidTags.LAVA)) {
            GlStateManager.fogMode(GlStateManager.FogMode.EXP);
            GlStateManager.fogDensity(2.0f);
        }
        else {
            final float float5 = this.renderer.getRenderDistance();
            GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
            if (integer == -1) {
                GlStateManager.fogStart(0.0f);
                GlStateManager.fogEnd(float5);
            }
            else {
                GlStateManager.fogStart(float5 * 0.75f);
                GlStateManager.fogEnd(float5);
            }
            GLX.setupNvFogDistance();
            if (this.minecraft.level.dimension.isFoggyAt(Mth.floor(cxq.getPosition().x), Mth.floor(cxq.getPosition().z)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog()) {
                GlStateManager.fogStart(float5 * 0.05f);
                GlStateManager.fogEnd(Math.min(float5, 192.0f) * 0.5f);
            }
        }
        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }
    
    public void resetFogColor(final boolean boolean1) {
        if (boolean1) {
            GlStateManager.fog(2918, this.blackBuffer);
        }
        else {
            GlStateManager.fog(2918, this.updateColorBuffer());
        }
    }
    
    private FloatBuffer updateColorBuffer() {
        if (this.oldRed != this.fogRed || this.oldGreen != this.fogGreen || this.oldBlue != this.fogBlue) {
            this.colorBuffer.clear();
            this.colorBuffer.put(this.fogRed).put(this.fogGreen).put(this.fogBlue).put(1.0f);
            this.colorBuffer.flip();
            this.oldRed = this.fogRed;
            this.oldGreen = this.fogGreen;
            this.oldBlue = this.fogBlue;
        }
        return this.colorBuffer;
    }
}
