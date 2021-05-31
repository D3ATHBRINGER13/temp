package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotRenderer extends MobRenderer<Parrot, ParrotModel> {
    public static final ResourceLocation[] PARROT_LOCATIONS;
    
    public ParrotRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new ParrotModel(), 0.3f);
    }
    
    protected ResourceLocation getTextureLocation(final Parrot arm) {
        return ParrotRenderer.PARROT_LOCATIONS[arm.getVariant()];
    }
    
    public float getBob(final Parrot arm, final float float2) {
        final float float3 = Mth.lerp(float2, arm.oFlap, arm.flap);
        final float float4 = Mth.lerp(float2, arm.oFlapSpeed, arm.flapSpeed);
        return (Mth.sin(float3) + 1.0f) * float4;
    }
    
    static {
        PARROT_LOCATIONS = new ResourceLocation[] { new ResourceLocation("textures/entity/parrot/parrot_red_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_green.png"), new ResourceLocation("textures/entity/parrot/parrot_yellow_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_grey.png") };
    }
}
