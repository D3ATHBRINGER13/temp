package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class CodModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart topFin;
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart sideFin0;
    private final ModelPart sideFin1;
    private final ModelPart tailFin;
    
    public CodModel() {
        this.texWidth = 32;
        this.texHeight = 32;
        final int integer2 = 22;
        (this.body = new ModelPart(this, 0, 0)).addBox(-1.0f, -2.0f, 0.0f, 2, 4, 7);
        this.body.setPos(0.0f, 22.0f, 0.0f);
        (this.head = new ModelPart(this, 11, 0)).addBox(-1.0f, -2.0f, -3.0f, 2, 4, 3);
        this.head.setPos(0.0f, 22.0f, 0.0f);
        (this.nose = new ModelPart(this, 0, 0)).addBox(-1.0f, -2.0f, -1.0f, 2, 3, 1);
        this.nose.setPos(0.0f, 22.0f, -3.0f);
        (this.sideFin0 = new ModelPart(this, 22, 1)).addBox(-2.0f, 0.0f, -1.0f, 2, 0, 2);
        this.sideFin0.setPos(-1.0f, 23.0f, 0.0f);
        this.sideFin0.zRot = -0.7853982f;
        (this.sideFin1 = new ModelPart(this, 22, 4)).addBox(0.0f, 0.0f, -1.0f, 2, 0, 2);
        this.sideFin1.setPos(1.0f, 23.0f, 0.0f);
        this.sideFin1.zRot = 0.7853982f;
        (this.tailFin = new ModelPart(this, 22, 3)).addBox(0.0f, -2.0f, 0.0f, 0, 4, 4);
        this.tailFin.setPos(0.0f, 22.0f, 7.0f);
        (this.topFin = new ModelPart(this, 20, -6)).addBox(0.0f, -1.0f, -1.0f, 0, 1, 6);
        this.topFin.setPos(0.0f, 20.0f, 0.0f);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.body.render(float7);
        this.head.render(float7);
        this.nose.render(float7);
        this.sideFin0.render(float7);
        this.sideFin1.render(float7);
        this.tailFin.render(float7);
        this.topFin.render(float7);
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        float float8 = 1.0f;
        if (!aio.isInWater()) {
            float8 = 1.5f;
        }
        this.tailFin.yRot = -float8 * 0.45f * Mth.sin(0.6f * float4);
    }
}
