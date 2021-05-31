package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.world.entity.animal.Chicken;

public class ChickenRenderer extends MobRenderer<Chicken, ChickenModel<Chicken>> {
    private static final ResourceLocation CHICKEN_LOCATION;
    
    public ChickenRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new ChickenModel(), 0.3f);
    }
    
    protected ResourceLocation getTextureLocation(final Chicken arc) {
        return ChickenRenderer.CHICKEN_LOCATION;
    }
    
    @Override
    protected float getBob(final Chicken arc, final float float2) {
        final float float3 = Mth.lerp(float2, arc.oFlap, arc.flap);
        final float float4 = Mth.lerp(float2, arc.oFlapSpeed, arc.flapSpeed);
        return (Mth.sin(float3) + 1.0f) * float4;
    }
    
    static {
        CHICKEN_LOCATION = new ResourceLocation("textures/entity/chicken.png");
    }
}
