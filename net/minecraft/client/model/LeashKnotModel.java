package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class LeashKnotModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart knot;
    
    public LeashKnotModel() {
        this(0, 0, 32, 32);
    }
    
    public LeashKnotModel(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.texWidth = integer3;
        this.texHeight = integer4;
        (this.knot = new ModelPart(this, integer1, integer2)).addBox(-3.0f, -6.0f, -3.0f, 6, 8, 6, 0.0f);
        this.knot.setPos(0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.knot.render(float7);
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.knot.yRot = float5 * 0.017453292f;
        this.knot.xRot = float6 * 0.017453292f;
    }
}
