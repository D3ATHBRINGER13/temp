package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ShulkerBulletModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart main;
    
    public ShulkerBulletModel() {
        this.texWidth = 64;
        this.texHeight = 32;
        this.main = new ModelPart(this);
        this.main.texOffs(0, 0).addBox(-4.0f, -4.0f, -1.0f, 8, 8, 2, 0.0f);
        this.main.texOffs(0, 10).addBox(-1.0f, -4.0f, -4.0f, 2, 8, 8, 0.0f);
        this.main.texOffs(20, 0).addBox(-4.0f, -1.0f, -4.0f, 8, 2, 8, 0.0f);
        this.main.setPos(0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.main.render(float7);
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.main.yRot = float5 * 0.017453292f;
        this.main.xRot = float6 * 0.017453292f;
    }
}
