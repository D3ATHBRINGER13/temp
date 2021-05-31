package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class SlimeModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart cube;
    private final ModelPart eye0;
    private final ModelPart eye1;
    private final ModelPart mouth;
    
    public SlimeModel(final int integer) {
        if (integer > 0) {
            (this.cube = new ModelPart(this, 0, integer)).addBox(-3.0f, 17.0f, -3.0f, 6, 6, 6);
            (this.eye0 = new ModelPart(this, 32, 0)).addBox(-3.25f, 18.0f, -3.5f, 2, 2, 2);
            (this.eye1 = new ModelPart(this, 32, 4)).addBox(1.25f, 18.0f, -3.5f, 2, 2, 2);
            (this.mouth = new ModelPart(this, 32, 8)).addBox(0.0f, 21.0f, -3.5f, 1, 1, 1);
        }
        else {
            (this.cube = new ModelPart(this, 0, integer)).addBox(-4.0f, 16.0f, -4.0f, 8, 8, 8);
            this.eye0 = null;
            this.eye1 = null;
            this.mouth = null;
        }
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        GlStateManager.translatef(0.0f, 0.001f, 0.0f);
        this.cube.render(float7);
        if (this.eye0 != null) {
            this.eye0.render(float7);
            this.eye1.render(float7);
            this.mouth.render(float7);
        }
    }
}
