package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Cat;

public class CatModel<T extends Cat> extends OcelotModel<T> {
    private float lieDownAmount;
    private float lieDownAmountTail;
    private float relaxStateOneAmount;
    
    public CatModel(final float float1) {
        super(float1);
    }
    
    @Override
    public void prepareMobModel(final T arb, final float float2, final float float3, final float float4) {
        this.lieDownAmount = arb.getLieDownAmount(float4);
        this.lieDownAmountTail = arb.getLieDownAmountTail(float4);
        this.relaxStateOneAmount = arb.getRelaxStateOneAmount(float4);
        if (this.lieDownAmount <= 0.0f) {
            this.head.xRot = 0.0f;
            this.head.zRot = 0.0f;
            this.frontLegL.xRot = 0.0f;
            this.frontLegL.zRot = 0.0f;
            this.frontLegR.xRot = 0.0f;
            this.frontLegR.zRot = 0.0f;
            this.frontLegR.x = -1.2f;
            this.backLegL.xRot = 0.0f;
            this.backLegR.xRot = 0.0f;
            this.backLegR.zRot = 0.0f;
            this.backLegR.x = -1.1f;
            this.backLegR.y = 18.0f;
        }
        super.prepareMobModel(arb, float2, float3, float4);
        if (arb.isSitting()) {
            this.body.xRot = 0.7853982f;
            final ModelPart body = this.body;
            body.y -= 4.0f;
            final ModelPart body2 = this.body;
            body2.z += 5.0f;
            final ModelPart head = this.head;
            head.y -= 3.3f;
            final ModelPart head2 = this.head;
            ++head2.z;
            final ModelPart tail1 = this.tail1;
            tail1.y += 8.0f;
            final ModelPart tail2 = this.tail1;
            tail2.z -= 2.0f;
            final ModelPart tail3 = this.tail2;
            tail3.y += 2.0f;
            final ModelPart tail4 = this.tail2;
            tail4.z -= 0.8f;
            this.tail1.xRot = 1.7278761f;
            this.tail2.xRot = 2.670354f;
            this.frontLegL.xRot = -0.15707964f;
            this.frontLegL.y = 16.1f;
            this.frontLegL.z = -7.0f;
            this.frontLegR.xRot = -0.15707964f;
            this.frontLegR.y = 16.1f;
            this.frontLegR.z = -7.0f;
            this.backLegL.xRot = -1.5707964f;
            this.backLegL.y = 21.0f;
            this.backLegL.z = 1.0f;
            this.backLegR.xRot = -1.5707964f;
            this.backLegR.y = 21.0f;
            this.backLegR.z = 1.0f;
            this.state = 3;
        }
    }
    
    @Override
    public void setupAnim(final T arb, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(arb, float2, float3, float4, float5, float6, float7);
        if (this.lieDownAmount > 0.0f) {
            this.head.zRot = this.rotlerpRad(this.head.zRot, -1.2707963f, this.lieDownAmount);
            this.head.yRot = this.rotlerpRad(this.head.yRot, 1.2707963f, this.lieDownAmount);
            this.frontLegL.xRot = -1.2707963f;
            this.frontLegR.xRot = -0.47079635f;
            this.frontLegR.zRot = -0.2f;
            this.frontLegR.x = -0.2f;
            this.backLegL.xRot = -0.4f;
            this.backLegR.xRot = 0.5f;
            this.backLegR.zRot = -0.5f;
            this.backLegR.x = -0.3f;
            this.backLegR.y = 20.0f;
            this.tail1.xRot = this.rotlerpRad(this.tail1.xRot, 0.8f, this.lieDownAmountTail);
            this.tail2.xRot = this.rotlerpRad(this.tail2.xRot, -0.4f, this.lieDownAmountTail);
        }
        if (this.relaxStateOneAmount > 0.0f) {
            this.head.xRot = this.rotlerpRad(this.head.xRot, -0.58177644f, this.relaxStateOneAmount);
        }
    }
    
    protected float rotlerpRad(final float float1, final float float2, final float float3) {
        float float4;
        for (float4 = float2 - float1; float4 < -3.1415927f; float4 += 6.2831855f) {}
        while (float4 >= 3.1415927f) {
            float4 -= 6.2831855f;
        }
        return float1 + float3 * float4;
    }
}
