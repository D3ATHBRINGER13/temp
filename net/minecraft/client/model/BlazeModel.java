package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class BlazeModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart[] upperBodyParts;
    private final ModelPart head;
    
    public BlazeModel() {
        this.upperBodyParts = new ModelPart[12];
        for (int integer2 = 0; integer2 < this.upperBodyParts.length; ++integer2) {
            (this.upperBodyParts[integer2] = new ModelPart(this, 0, 16)).addBox(0.0f, 0.0f, 0.0f, 2, 8, 2);
        }
        (this.head = new ModelPart(this, 0, 0)).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.head.render(float7);
        for (final ModelPart djv12 : this.upperBodyParts) {
            djv12.render(float7);
        }
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        float float8 = float4 * 3.1415927f * -0.1f;
        for (int integer10 = 0; integer10 < 4; ++integer10) {
            this.upperBodyParts[integer10].y = -2.0f + Mth.cos((integer10 * 2 + float4) * 0.25f);
            this.upperBodyParts[integer10].x = Mth.cos(float8) * 9.0f;
            this.upperBodyParts[integer10].z = Mth.sin(float8) * 9.0f;
            float8 += 1.5707964f;
        }
        float8 = 0.7853982f + float4 * 3.1415927f * 0.03f;
        for (int integer10 = 4; integer10 < 8; ++integer10) {
            this.upperBodyParts[integer10].y = 2.0f + Mth.cos((integer10 * 2 + float4) * 0.25f);
            this.upperBodyParts[integer10].x = Mth.cos(float8) * 7.0f;
            this.upperBodyParts[integer10].z = Mth.sin(float8) * 7.0f;
            float8 += 1.5707964f;
        }
        float8 = 0.47123894f + float4 * 3.1415927f * -0.05f;
        for (int integer10 = 8; integer10 < 12; ++integer10) {
            this.upperBodyParts[integer10].y = 11.0f + Mth.cos((integer10 * 1.5f + float4) * 0.5f);
            this.upperBodyParts[integer10].x = Mth.cos(float8) * 5.0f;
            this.upperBodyParts[integer10].z = Mth.sin(float8) * 5.0f;
            float8 += 1.5707964f;
        }
        this.head.yRot = float5 * 0.017453292f;
        this.head.xRot = float6 * 0.017453292f;
    }
}
