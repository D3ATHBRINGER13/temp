package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class LlamaSpitModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart main;
    
    public LlamaSpitModel() {
        this(0.0f);
    }
    
    public LlamaSpitModel(final float float1) {
        this.main = new ModelPart(this);
        final int integer3 = 2;
        this.main.texOffs(0, 0).addBox(-4.0f, 0.0f, 0.0f, 2, 2, 2, float1);
        this.main.texOffs(0, 0).addBox(0.0f, -4.0f, 0.0f, 2, 2, 2, float1);
        this.main.texOffs(0, 0).addBox(0.0f, 0.0f, -4.0f, 2, 2, 2, float1);
        this.main.texOffs(0, 0).addBox(0.0f, 0.0f, 0.0f, 2, 2, 2, float1);
        this.main.texOffs(0, 0).addBox(2.0f, 0.0f, 0.0f, 2, 2, 2, float1);
        this.main.texOffs(0, 0).addBox(0.0f, 2.0f, 0.0f, 2, 2, 2, float1);
        this.main.texOffs(0, 0).addBox(0.0f, 0.0f, 2.0f, 2, 2, 2, float1);
        this.main.setPos(0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.main.render(float7);
    }
}
