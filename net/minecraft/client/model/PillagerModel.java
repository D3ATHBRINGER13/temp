package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.AbstractIllager;

public class PillagerModel<T extends AbstractIllager> extends IllagerModel<T> {
    public PillagerModel(final float float1, final float float2, final int integer3, final int integer4) {
        super(float1, float2, integer3, integer4);
    }
    
    @Override
    public void render(final T aua, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.render(aua, float2, float3, float4, float5, float6, float7);
        this.head.render(float7);
        this.body.render(float7);
        this.leftLeg.render(float7);
        this.rightLeg.render(float7);
        this.rightArm.render(float7);
        this.leftArm.render(float7);
    }
}
