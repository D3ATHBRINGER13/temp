package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class ChestedHorseModel<T extends AbstractChestedHorse> extends HorseModel<T> {
    private final ModelPart boxL;
    private final ModelPart boxR;
    
    public ChestedHorseModel(final float float1) {
        super(float1);
        (this.boxL = new ModelPart(this, 26, 21)).addBox(-4.0f, 0.0f, -2.0f, 8, 8, 3);
        (this.boxR = new ModelPart(this, 26, 21)).addBox(-4.0f, 0.0f, -2.0f, 8, 8, 3);
        this.boxL.yRot = -1.5707964f;
        this.boxR.yRot = 1.5707964f;
        this.boxL.setPos(6.0f, -8.0f, 0.0f);
        this.boxR.setPos(-6.0f, -8.0f, 0.0f);
        this.body.addChild(this.boxL);
        this.body.addChild(this.boxR);
    }
    
    @Override
    protected void addEarModels(final ModelPart djv) {
        final ModelPart djv2 = new ModelPart(this, 0, 12);
        djv2.addBox(-1.0f, -7.0f, 0.0f, 2, 7, 1);
        djv2.setPos(1.25f, -10.0f, 4.0f);
        final ModelPart djv3 = new ModelPart(this, 0, 12);
        djv3.addBox(-1.0f, -7.0f, 0.0f, 2, 7, 1);
        djv3.setPos(-1.25f, -10.0f, 4.0f);
        djv2.xRot = 0.2617994f;
        djv2.zRot = 0.2617994f;
        djv3.xRot = 0.2617994f;
        djv3.zRot = -0.2617994f;
        djv.addChild(djv2);
        djv.addChild(djv3);
    }
    
    @Override
    public void render(final T asa, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        if (asa.hasChest()) {
            this.boxL.visible = true;
            this.boxR.visible = true;
        }
        else {
            this.boxL.visible = false;
            this.boxR.visible = false;
        }
        super.render(asa, float2, float3, float4, float5, float6, float7);
    }
}
