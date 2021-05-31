package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerModel<T extends Shulker> extends EntityModel<T> {
    private final ModelPart base;
    private final ModelPart lid;
    private final ModelPart head;
    
    public ShulkerModel() {
        this.texHeight = 64;
        this.texWidth = 64;
        this.lid = new ModelPart(this);
        this.base = new ModelPart(this);
        this.head = new ModelPart(this);
        this.lid.texOffs(0, 0).addBox(-8.0f, -16.0f, -8.0f, 16, 12, 16);
        this.lid.setPos(0.0f, 24.0f, 0.0f);
        this.base.texOffs(0, 28).addBox(-8.0f, -8.0f, -8.0f, 16, 8, 16);
        this.base.setPos(0.0f, 24.0f, 0.0f);
        this.head.texOffs(0, 52).addBox(-3.0f, 0.0f, -3.0f, 6, 6, 6);
        this.head.setPos(0.0f, 12.0f, 0.0f);
    }
    
    @Override
    public void setupAnim(final T avb, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        final float float8 = float4 - avb.tickCount;
        final float float9 = (0.5f + avb.getClientPeekAmount(float8)) * 3.1415927f;
        final float float10 = -1.0f + Mth.sin(float9);
        float float11 = 0.0f;
        if (float9 > 3.1415927f) {
            float11 = Mth.sin(float4 * 0.1f) * 0.7f;
        }
        this.lid.setPos(0.0f, 16.0f + Mth.sin(float9) * 8.0f + float11, 0.0f);
        if (avb.getClientPeekAmount(float8) > 0.3f) {
            this.lid.yRot = float10 * float10 * float10 * float10 * 3.1415927f * 0.125f;
        }
        else {
            this.lid.yRot = 0.0f;
        }
        this.head.xRot = float6 * 0.017453292f;
        this.head.yRot = float5 * 0.017453292f;
    }
    
    @Override
    public void render(final T avb, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.base.render(float7);
        this.lid.render(float7);
    }
    
    public ModelPart getBase() {
        return this.base;
    }
    
    public ModelPart getLid() {
        return this.lid;
    }
    
    public ModelPart getHead() {
        return this.head;
    }
}
