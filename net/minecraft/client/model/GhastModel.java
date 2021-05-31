package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import java.util.Random;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class GhastModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart[] tentacles;
    
    public GhastModel() {
        this.tentacles = new ModelPart[9];
        final int integer2 = -16;
        (this.body = new ModelPart(this, 0, 0)).addBox(-8.0f, -8.0f, -8.0f, 16, 16, 16);
        final ModelPart body = this.body;
        body.y += 8.0f;
        final Random random3 = new Random(1660L);
        for (int integer3 = 0; integer3 < this.tentacles.length; ++integer3) {
            this.tentacles[integer3] = new ModelPart(this, 0, 0);
            final float float5 = ((integer3 % 3 - integer3 / 3 % 2 * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            final float float6 = (integer3 / 3 / 2.0f * 2.0f - 1.0f) * 5.0f;
            final int integer4 = random3.nextInt(7) + 8;
            this.tentacles[integer3].addBox(-1.0f, 0.0f, -1.0f, 2, integer4, 2);
            this.tentacles[integer3].x = float5;
            this.tentacles[integer3].z = float6;
            this.tentacles[integer3].y = 15.0f;
        }
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        for (int integer9 = 0; integer9 < this.tentacles.length; ++integer9) {
            this.tentacles[integer9].xRot = 0.2f * Mth.sin(float4 * 0.3f + integer9) + 0.4f;
        }
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.6f, 0.0f);
        this.body.render(float7);
        for (final ModelPart djv12 : this.tentacles) {
            djv12.render(float7);
        }
        GlStateManager.popMatrix();
    }
}
