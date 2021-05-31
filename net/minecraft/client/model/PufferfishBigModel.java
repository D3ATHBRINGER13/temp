package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class PufferfishBigModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart cube;
    private final ModelPart blueFin0;
    private final ModelPart blueFin1;
    private final ModelPart topFrontFin;
    private final ModelPart topMidFin;
    private final ModelPart topBackFin;
    private final ModelPart sideFrontFin0;
    private final ModelPart sideFrontFin1;
    private final ModelPart bottomFrontFin;
    private final ModelPart bottomBackFin;
    private final ModelPart bottomMidFin;
    private final ModelPart sideBackFin0;
    private final ModelPart sideBackFin1;
    
    public PufferfishBigModel() {
        this.texWidth = 32;
        this.texHeight = 32;
        final int integer2 = 22;
        (this.cube = new ModelPart(this, 0, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8);
        this.cube.setPos(0.0f, 22.0f, 0.0f);
        (this.blueFin0 = new ModelPart(this, 24, 0)).addBox(-2.0f, 0.0f, -1.0f, 2, 1, 2);
        this.blueFin0.setPos(-4.0f, 15.0f, -2.0f);
        (this.blueFin1 = new ModelPart(this, 24, 3)).addBox(0.0f, 0.0f, -1.0f, 2, 1, 2);
        this.blueFin1.setPos(4.0f, 15.0f, -2.0f);
        (this.topFrontFin = new ModelPart(this, 15, 17)).addBox(-4.0f, -1.0f, 0.0f, 8, 1, 0);
        this.topFrontFin.setPos(0.0f, 14.0f, -4.0f);
        this.topFrontFin.xRot = 0.7853982f;
        (this.topMidFin = new ModelPart(this, 14, 16)).addBox(-4.0f, -1.0f, 0.0f, 8, 1, 1);
        this.topMidFin.setPos(0.0f, 14.0f, 0.0f);
        (this.topBackFin = new ModelPart(this, 23, 18)).addBox(-4.0f, -1.0f, 0.0f, 8, 1, 0);
        this.topBackFin.setPos(0.0f, 14.0f, 4.0f);
        this.topBackFin.xRot = -0.7853982f;
        (this.sideFrontFin0 = new ModelPart(this, 5, 17)).addBox(-1.0f, -8.0f, 0.0f, 1, 8, 0);
        this.sideFrontFin0.setPos(-4.0f, 22.0f, -4.0f);
        this.sideFrontFin0.yRot = -0.7853982f;
        (this.sideFrontFin1 = new ModelPart(this, 1, 17)).addBox(0.0f, -8.0f, 0.0f, 1, 8, 0);
        this.sideFrontFin1.setPos(4.0f, 22.0f, -4.0f);
        this.sideFrontFin1.yRot = 0.7853982f;
        (this.bottomFrontFin = new ModelPart(this, 15, 20)).addBox(-4.0f, 0.0f, 0.0f, 8, 1, 0);
        this.bottomFrontFin.setPos(0.0f, 22.0f, -4.0f);
        this.bottomFrontFin.xRot = -0.7853982f;
        (this.bottomMidFin = new ModelPart(this, 15, 20)).addBox(-4.0f, 0.0f, 0.0f, 8, 1, 0);
        this.bottomMidFin.setPos(0.0f, 22.0f, 0.0f);
        (this.bottomBackFin = new ModelPart(this, 15, 20)).addBox(-4.0f, 0.0f, 0.0f, 8, 1, 0);
        this.bottomBackFin.setPos(0.0f, 22.0f, 4.0f);
        this.bottomBackFin.xRot = 0.7853982f;
        (this.sideBackFin0 = new ModelPart(this, 9, 17)).addBox(-1.0f, -8.0f, 0.0f, 1, 8, 0);
        this.sideBackFin0.setPos(-4.0f, 22.0f, 4.0f);
        this.sideBackFin0.yRot = 0.7853982f;
        (this.sideBackFin1 = new ModelPart(this, 9, 17)).addBox(0.0f, -8.0f, 0.0f, 1, 8, 0);
        this.sideBackFin1.setPos(4.0f, 22.0f, 4.0f);
        this.sideBackFin1.yRot = -0.7853982f;
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.cube.render(float7);
        this.blueFin0.render(float7);
        this.blueFin1.render(float7);
        this.topFrontFin.render(float7);
        this.topMidFin.render(float7);
        this.topBackFin.render(float7);
        this.sideFrontFin0.render(float7);
        this.sideFrontFin1.render(float7);
        this.bottomFrontFin.render(float7);
        this.bottomMidFin.render(float7);
        this.bottomBackFin.render(float7);
        this.sideBackFin0.render(float7);
        this.sideBackFin1.render(float7);
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.blueFin0.zRot = -0.2f + 0.4f * Mth.sin(float4 * 0.2f);
        this.blueFin1.zRot = 0.2f - 0.4f * Mth.sin(float4 * 0.2f);
    }
}
