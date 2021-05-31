package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class SnowGolemModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart piece1;
    private final ModelPart piece2;
    private final ModelPart head;
    private final ModelPart arm1;
    private final ModelPart arm2;
    
    public SnowGolemModel() {
        final float float2 = 4.0f;
        final float float3 = 0.0f;
        (this.head = new ModelPart(this, 0, 0).setTexSize(64, 64)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, -0.5f);
        this.head.setPos(0.0f, 4.0f, 0.0f);
        (this.arm1 = new ModelPart(this, 32, 0).setTexSize(64, 64)).addBox(-1.0f, 0.0f, -1.0f, 12, 2, 2, -0.5f);
        this.arm1.setPos(0.0f, 6.0f, 0.0f);
        (this.arm2 = new ModelPart(this, 32, 0).setTexSize(64, 64)).addBox(-1.0f, 0.0f, -1.0f, 12, 2, 2, -0.5f);
        this.arm2.setPos(0.0f, 6.0f, 0.0f);
        (this.piece1 = new ModelPart(this, 0, 16).setTexSize(64, 64)).addBox(-5.0f, -10.0f, -5.0f, 10, 10, 10, -0.5f);
        this.piece1.setPos(0.0f, 13.0f, 0.0f);
        (this.piece2 = new ModelPart(this, 0, 36).setTexSize(64, 64)).addBox(-6.0f, -12.0f, -6.0f, 12, 12, 12, -0.5f);
        this.piece2.setPos(0.0f, 24.0f, 0.0f);
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.head.yRot = float5 * 0.017453292f;
        this.head.xRot = float6 * 0.017453292f;
        this.piece1.yRot = float5 * 0.017453292f * 0.25f;
        final float float8 = Mth.sin(this.piece1.yRot);
        final float float9 = Mth.cos(this.piece1.yRot);
        this.arm1.zRot = 1.0f;
        this.arm2.zRot = -1.0f;
        this.arm1.yRot = 0.0f + this.piece1.yRot;
        this.arm2.yRot = 3.1415927f + this.piece1.yRot;
        this.arm1.x = float9 * 5.0f;
        this.arm1.z = -float8 * 5.0f;
        this.arm2.x = -float9 * 5.0f;
        this.arm2.z = float8 * 5.0f;
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.piece1.render(float7);
        this.piece2.render(float7);
        this.head.render(float7);
        this.arm1.render(float7);
        this.arm2.render(float7);
    }
    
    public ModelPart getHead() {
        return this.head;
    }
}
