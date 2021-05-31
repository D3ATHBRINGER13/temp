package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class TropicalFishModelB<T extends Entity> extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart leftFin;
    private final ModelPart rightFin;
    private final ModelPart topFin;
    private final ModelPart bottomFin;
    
    public TropicalFishModelB() {
        this(0.0f);
    }
    
    public TropicalFishModelB(final float float1) {
        this.texWidth = 32;
        this.texHeight = 32;
        final int integer3 = 19;
        (this.body = new ModelPart(this, 0, 20)).addBox(-1.0f, -3.0f, -3.0f, 2, 6, 6, float1);
        this.body.setPos(0.0f, 19.0f, 0.0f);
        (this.tail = new ModelPart(this, 21, 16)).addBox(0.0f, -3.0f, 0.0f, 0, 6, 5, float1);
        this.tail.setPos(0.0f, 19.0f, 3.0f);
        (this.leftFin = new ModelPart(this, 2, 16)).addBox(-2.0f, 0.0f, 0.0f, 2, 2, 0, float1);
        this.leftFin.setPos(-1.0f, 20.0f, 0.0f);
        this.leftFin.yRot = 0.7853982f;
        (this.rightFin = new ModelPart(this, 2, 12)).addBox(0.0f, 0.0f, 0.0f, 2, 2, 0, float1);
        this.rightFin.setPos(1.0f, 20.0f, 0.0f);
        this.rightFin.yRot = -0.7853982f;
        (this.topFin = new ModelPart(this, 20, 11)).addBox(0.0f, -4.0f, 0.0f, 0, 4, 6, float1);
        this.topFin.setPos(0.0f, 16.0f, -3.0f);
        (this.bottomFin = new ModelPart(this, 20, 21)).addBox(0.0f, 0.0f, 0.0f, 0, 4, 6, float1);
        this.bottomFin.setPos(0.0f, 22.0f, -3.0f);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.body.render(float7);
        this.tail.render(float7);
        this.leftFin.render(float7);
        this.rightFin.render(float7);
        this.topFin.render(float7);
        this.bottomFin.render(float7);
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        float float8 = 1.0f;
        if (!aio.isInWater()) {
            float8 = 1.5f;
        }
        this.tail.yRot = -float8 * 0.45f * Mth.sin(0.6f * float4);
    }
}
