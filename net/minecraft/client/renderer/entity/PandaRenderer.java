package net.minecraft.client.renderer.entity;

import java.util.function.Consumer;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import java.util.EnumMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.PandaHoldsItemLayer;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.client.model.PandaModel;
import net.minecraft.world.entity.animal.Panda;

public class PandaRenderer extends MobRenderer<Panda, PandaModel<Panda>> {
    private static final Map<Panda.Gene, ResourceLocation> TEXTURES;
    
    public PandaRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new PandaModel(9, 0.0f), 0.9f);
        this.addLayer(new PandaHoldsItemLayer(this));
    }
    
    @Nullable
    protected ResourceLocation getTextureLocation(final Panda arl) {
        return (ResourceLocation)PandaRenderer.TEXTURES.getOrDefault(arl.getVariant(), PandaRenderer.TEXTURES.get(Panda.Gene.NORMAL));
    }
    
    @Override
    protected void setupRotations(final Panda arl, final float float2, final float float3, final float float4) {
        super.setupRotations(arl, float2, float3, float4);
        if (arl.rollCounter > 0) {
            final int integer6 = arl.rollCounter;
            final int integer7 = integer6 + 1;
            final float float5 = 7.0f;
            final float float6 = arl.isBaby() ? 0.3f : 0.8f;
            if (integer6 < 8) {
                final float float7 = 90 * integer6 / 7.0f;
                final float float8 = 90 * integer7 / 7.0f;
                final float float9 = this.getAngle(float7, float8, integer7, float4, 8.0f);
                GlStateManager.translatef(0.0f, (float6 + 0.2f) * (float9 / 90.0f), 0.0f);
                GlStateManager.rotatef(-float9, 1.0f, 0.0f, 0.0f);
            }
            else if (integer6 < 16) {
                final float float7 = (integer6 - 8.0f) / 7.0f;
                final float float8 = 90.0f + 90.0f * float7;
                final float float10 = 90.0f + 90.0f * (integer7 - 8.0f) / 7.0f;
                final float float9 = this.getAngle(float8, float10, integer7, float4, 16.0f);
                GlStateManager.translatef(0.0f, float6 + 0.2f + (float6 - 0.2f) * (float9 - 90.0f) / 90.0f, 0.0f);
                GlStateManager.rotatef(-float9, 1.0f, 0.0f, 0.0f);
            }
            else if (integer6 < 24.0f) {
                final float float7 = (integer6 - 16.0f) / 7.0f;
                final float float8 = 180.0f + 90.0f * float7;
                final float float10 = 180.0f + 90.0f * (integer7 - 16.0f) / 7.0f;
                final float float9 = this.getAngle(float8, float10, integer7, float4, 24.0f);
                GlStateManager.translatef(0.0f, float6 + float6 * (270.0f - float9) / 90.0f, 0.0f);
                GlStateManager.rotatef(-float9, 1.0f, 0.0f, 0.0f);
            }
            else if (integer6 < 32) {
                final float float7 = (integer6 - 24.0f) / 7.0f;
                final float float8 = 270.0f + 90.0f * float7;
                final float float10 = 270.0f + 90.0f * (integer7 - 24.0f) / 7.0f;
                final float float9 = this.getAngle(float8, float10, integer7, float4, 32.0f);
                GlStateManager.translatef(0.0f, float6 * ((360.0f - float9) / 90.0f), 0.0f);
                GlStateManager.rotatef(-float9, 1.0f, 0.0f, 0.0f);
            }
        }
        else {
            GlStateManager.rotatef(0.0f, 1.0f, 0.0f, 0.0f);
        }
        final float float11 = arl.getSitAmount(float4);
        if (float11 > 0.0f) {
            GlStateManager.translatef(0.0f, 0.8f * float11, 0.0f);
            GlStateManager.rotatef(Mth.lerp(float11, arl.xRot, arl.xRot + 90.0f), 1.0f, 0.0f, 0.0f);
            GlStateManager.translatef(0.0f, -1.0f * float11, 0.0f);
            if (arl.isScared()) {
                final float float12 = (float)(Math.cos(arl.tickCount * 1.25) * 3.141592653589793 * 0.05000000074505806);
                GlStateManager.rotatef(float12, 0.0f, 1.0f, 0.0f);
                if (arl.isBaby()) {
                    GlStateManager.translatef(0.0f, 0.8f, 0.55f);
                }
            }
        }
        final float float12 = arl.getLieOnBackAmount(float4);
        if (float12 > 0.0f) {
            final float float5 = arl.isBaby() ? 0.5f : 1.3f;
            GlStateManager.translatef(0.0f, float5 * float12, 0.0f);
            GlStateManager.rotatef(Mth.lerp(float12, arl.xRot, arl.xRot + 180.0f), 1.0f, 0.0f, 0.0f);
        }
    }
    
    private float getAngle(final float float1, final float float2, final int integer, final float float4, final float float5) {
        if (integer < float5) {
            return Mth.lerp(float4, float1, float2);
        }
        return float1;
    }
    
    static {
        TEXTURES = Util.<Map>make((Map)Maps.newEnumMap((Class)Panda.Gene.class), (java.util.function.Consumer<Map>)(enumMap -> {
            enumMap.put((Enum)Panda.Gene.NORMAL, new ResourceLocation("textures/entity/panda/panda.png"));
            enumMap.put((Enum)Panda.Gene.LAZY, new ResourceLocation("textures/entity/panda/lazy_panda.png"));
            enumMap.put((Enum)Panda.Gene.WORRIED, new ResourceLocation("textures/entity/panda/worried_panda.png"));
            enumMap.put((Enum)Panda.Gene.PLAYFUL, new ResourceLocation("textures/entity/panda/playful_panda.png"));
            enumMap.put((Enum)Panda.Gene.BROWN, new ResourceLocation("textures/entity/panda/brown_panda.png"));
            enumMap.put((Enum)Panda.Gene.WEAK, new ResourceLocation("textures/entity/panda/weak_panda.png"));
            enumMap.put((Enum)Panda.Gene.AGGRESSIVE, new ResourceLocation("textures/entity/panda/aggressive_panda.png"));
        }));
    }
}
