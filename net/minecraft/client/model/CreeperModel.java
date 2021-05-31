package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class CreeperModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart head;
    private final ModelPart hair;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    
    public CreeperModel() {
        this(0.0f);
    }
    
    public CreeperModel(final float float1) {
        final int integer3 = 6;
        (this.head = new ModelPart(this, 0, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, float1);
        this.head.setPos(0.0f, 6.0f, 0.0f);
        (this.hair = new ModelPart(this, 32, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, float1 + 0.5f);
        this.hair.setPos(0.0f, 6.0f, 0.0f);
        (this.body = new ModelPart(this, 16, 16)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, float1);
        this.body.setPos(0.0f, 6.0f, 0.0f);
        (this.leg0 = new ModelPart(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, float1);
        this.leg0.setPos(-2.0f, 18.0f, 4.0f);
        (this.leg1 = new ModelPart(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, float1);
        this.leg1.setPos(2.0f, 18.0f, 4.0f);
        (this.leg2 = new ModelPart(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, float1);
        this.leg2.setPos(-2.0f, 18.0f, -4.0f);
        (this.leg3 = new ModelPart(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, float1);
        this.leg3.setPos(2.0f, 18.0f, -4.0f);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.head.render(float7);
        this.body.render(float7);
        this.leg0.render(float7);
        this.leg1.render(float7);
        this.leg2.render(float7);
        this.leg3.render(float7);
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.head.yRot = float5 * 0.017453292f;
        this.head.xRot = float6 * 0.017453292f;
        this.leg0.xRot = Mth.cos(float2 * 0.6662f) * 1.4f * float3;
        this.leg1.xRot = Mth.cos(float2 * 0.6662f + 3.1415927f) * 1.4f * float3;
        this.leg2.xRot = Mth.cos(float2 * 0.6662f + 3.1415927f) * 1.4f * float3;
        this.leg3.xRot = Mth.cos(float2 * 0.6662f) * 1.4f * float3;
    }
}
