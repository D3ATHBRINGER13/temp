package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class EvokerFangsModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart base;
    private final ModelPart upperJaw;
    private final ModelPart lowerJaw;
    
    public EvokerFangsModel() {
        (this.base = new ModelPart(this, 0, 0)).setPos(-5.0f, 22.0f, -5.0f);
        this.base.addBox(0.0f, 0.0f, 0.0f, 10, 12, 10);
        (this.upperJaw = new ModelPart(this, 40, 0)).setPos(1.5f, 22.0f, -4.0f);
        this.upperJaw.addBox(0.0f, 0.0f, 0.0f, 4, 14, 8);
        (this.lowerJaw = new ModelPart(this, 40, 0)).setPos(-1.5f, 22.0f, 4.0f);
        this.lowerJaw.addBox(0.0f, 0.0f, 0.0f, 4, 14, 8);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        float float8 = float2 * 2.0f;
        if (float8 > 1.0f) {
            float8 = 1.0f;
        }
        float8 = 1.0f - float8 * float8 * float8;
        this.upperJaw.zRot = 3.1415927f - float8 * 0.35f * 3.1415927f;
        this.lowerJaw.zRot = 3.1415927f + float8 * 0.35f * 3.1415927f;
        this.lowerJaw.yRot = 3.1415927f;
        final float float9 = (float2 + Mth.sin(float2 * 2.7f)) * 0.6f * 12.0f;
        this.upperJaw.y = 24.0f - float9;
        this.lowerJaw.y = this.upperJaw.y;
        this.base.y = this.upperJaw.y;
        this.base.render(float7);
        this.upperJaw.render(float7);
        this.lowerJaw.render(float7);
    }
}
