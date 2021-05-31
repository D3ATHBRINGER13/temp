package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class WitchModel<T extends Entity> extends VillagerModel<T> {
    private boolean holdingItem;
    private final ModelPart mole;
    
    public WitchModel(final float float1) {
        super(float1, 64, 128);
        (this.mole = new ModelPart(this).setTexSize(64, 128)).setPos(0.0f, -2.0f, 0.0f);
        this.mole.texOffs(0, 0).addBox(0.0f, 3.0f, -6.75f, 1, 1, 1, -0.25f);
        this.nose.addChild(this.mole);
        this.head.removeChild(this.hat);
        (this.hat = new ModelPart(this).setTexSize(64, 128)).setPos(-5.0f, -10.03125f, -5.0f);
        this.hat.texOffs(0, 64).addBox(0.0f, 0.0f, 0.0f, 10, 2, 10);
        this.head.addChild(this.hat);
        final ModelPart djv3 = new ModelPart(this).setTexSize(64, 128);
        djv3.setPos(1.75f, -4.0f, 2.0f);
        djv3.texOffs(0, 76).addBox(0.0f, 0.0f, 0.0f, 7, 4, 7);
        djv3.xRot = -0.05235988f;
        djv3.zRot = 0.02617994f;
        this.hat.addChild(djv3);
        final ModelPart djv4 = new ModelPart(this).setTexSize(64, 128);
        djv4.setPos(1.75f, -4.0f, 2.0f);
        djv4.texOffs(0, 87).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4);
        djv4.xRot = -0.10471976f;
        djv4.zRot = 0.05235988f;
        djv3.addChild(djv4);
        final ModelPart djv5 = new ModelPart(this).setTexSize(64, 128);
        djv5.setPos(1.75f, -2.0f, 2.0f);
        djv5.texOffs(0, 95).addBox(0.0f, 0.0f, 0.0f, 1, 2, 1, 0.25f);
        djv5.xRot = -0.20943952f;
        djv5.zRot = 0.10471976f;
        djv4.addChild(djv5);
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        super.setupAnim(aio, float2, float3, float4, float5, float6, float7);
        this.nose.translateX = 0.0f;
        this.nose.translateY = 0.0f;
        this.nose.translateZ = 0.0f;
        final float float8 = 0.01f * (aio.getId() % 10);
        this.nose.xRot = Mth.sin(aio.tickCount * float8) * 4.5f * 0.017453292f;
        this.nose.yRot = 0.0f;
        this.nose.zRot = Mth.cos(aio.tickCount * float8) * 2.5f * 0.017453292f;
        if (this.holdingItem) {
            this.nose.xRot = -0.9f;
            this.nose.translateZ = -0.09375f;
            this.nose.translateY = 0.1875f;
        }
    }
    
    public ModelPart getNose() {
        return this.nose;
    }
    
    public void setHoldingItem(final boolean boolean1) {
        this.holdingItem = boolean1;
    }
}
