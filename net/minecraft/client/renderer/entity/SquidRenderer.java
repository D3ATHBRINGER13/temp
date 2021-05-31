package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.SquidModel;
import net.minecraft.world.entity.animal.Squid;

public class SquidRenderer extends MobRenderer<Squid, SquidModel<Squid>> {
    private static final ResourceLocation SQUID_LOCATION;
    
    public SquidRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new SquidModel(), 0.7f);
    }
    
    protected ResourceLocation getTextureLocation(final Squid arv) {
        return SquidRenderer.SQUID_LOCATION;
    }
    
    @Override
    protected void setupRotations(final Squid arv, final float float2, final float float3, final float float4) {
        final float float5 = Mth.lerp(float4, arv.xBodyRotO, arv.xBodyRot);
        final float float6 = Mth.lerp(float4, arv.zBodyRotO, arv.zBodyRot);
        GlStateManager.translatef(0.0f, 0.5f, 0.0f);
        GlStateManager.rotatef(180.0f - float3, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(float5, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(float6, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, -1.2f, 0.0f);
    }
    
    @Override
    protected float getBob(final Squid arv, final float float2) {
        return Mth.lerp(float2, arv.oldTentacleAngle, arv.tentacleAngle);
    }
    
    static {
        SQUID_LOCATION = new ResourceLocation("textures/entity/squid.png");
    }
}
