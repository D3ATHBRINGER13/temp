package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;

public class BellModel extends Model {
    private final ModelPart bellBody;
    private final ModelPart bellBase;
    
    public BellModel() {
        this.texWidth = 32;
        this.texHeight = 32;
        (this.bellBody = new ModelPart(this, 0, 0)).addBox(-3.0f, -6.0f, -3.0f, 6, 7, 6);
        this.bellBody.setPos(8.0f, 12.0f, 8.0f);
        (this.bellBase = new ModelPart(this, 0, 13)).addBox(4.0f, 4.0f, 4.0f, 8, 2, 8);
        this.bellBase.setPos(-8.0f, -12.0f, -8.0f);
        this.bellBody.addChild(this.bellBase);
    }
    
    public void render(final float float1, final float float2, final float float3) {
        this.bellBody.xRot = float1;
        this.bellBody.zRot = float2;
        this.bellBody.render(float3);
    }
}
