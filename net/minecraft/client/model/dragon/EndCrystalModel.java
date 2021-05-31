package net.minecraft.client.model.dragon;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;

public class EndCrystalModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart cube;
    private final ModelPart glass;
    private final ModelPart base;
    
    public EndCrystalModel(final float float1, final boolean boolean2) {
        this.glass = new ModelPart((Model)this, "glass");
        this.glass.texOffs(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        this.cube = new ModelPart((Model)this, "cube");
        this.cube.texOffs(32, 0).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        if (boolean2) {
            this.base = new ModelPart((Model)this, "base");
            this.base.texOffs(0, 16).addBox(-6.0f, 0.0f, -6.0f, 12, 4, 12);
        }
        else {
            this.base = null;
        }
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        GlStateManager.pushMatrix();
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        GlStateManager.translatef(0.0f, -0.5f, 0.0f);
        if (this.base != null) {
            this.base.render(float7);
        }
        GlStateManager.rotatef(float3, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.8f + float4, 0.0f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        this.glass.render(float7);
        final float float8 = 0.875f;
        GlStateManager.scalef(0.875f, 0.875f, 0.875f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        GlStateManager.rotatef(float3, 0.0f, 1.0f, 0.0f);
        this.glass.render(float7);
        GlStateManager.scalef(0.875f, 0.875f, 0.875f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        GlStateManager.rotatef(float3, 0.0f, 1.0f, 0.0f);
        this.cube.render(float7);
        GlStateManager.popMatrix();
    }
}
