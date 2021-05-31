package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;

public class BookModel extends Model {
    private final ModelPart leftLid;
    private final ModelPart rightLid;
    private final ModelPart leftPages;
    private final ModelPart rightPages;
    private final ModelPart flipPage1;
    private final ModelPart flipPage2;
    private final ModelPart seam;
    
    public BookModel() {
        this.leftLid = new ModelPart(this).texOffs(0, 0).addBox(-6.0f, -5.0f, 0.0f, 6, 10, 0);
        this.rightLid = new ModelPart(this).texOffs(16, 0).addBox(0.0f, -5.0f, 0.0f, 6, 10, 0);
        this.seam = new ModelPart(this).texOffs(12, 0).addBox(-1.0f, -5.0f, 0.0f, 2, 10, 0);
        this.leftPages = new ModelPart(this).texOffs(0, 10).addBox(0.0f, -4.0f, -0.99f, 5, 8, 1);
        this.rightPages = new ModelPart(this).texOffs(12, 10).addBox(0.0f, -4.0f, -0.01f, 5, 8, 1);
        this.flipPage1 = new ModelPart(this).texOffs(24, 10).addBox(0.0f, -4.0f, 0.0f, 5, 8, 0);
        this.flipPage2 = new ModelPart(this).texOffs(24, 10).addBox(0.0f, -4.0f, 0.0f, 5, 8, 0);
        this.leftLid.setPos(0.0f, 0.0f, -1.0f);
        this.rightLid.setPos(0.0f, 0.0f, 1.0f);
        this.seam.yRot = 1.5707964f;
    }
    
    public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
        this.setupAnim(float1, float2, float3, float4, float5, float6);
        this.leftLid.render(float6);
        this.rightLid.render(float6);
        this.seam.render(float6);
        this.leftPages.render(float6);
        this.rightPages.render(float6);
        this.flipPage1.render(float6);
        this.flipPage2.render(float6);
    }
    
    private void setupAnim(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
        final float float7 = (Mth.sin(float1 * 0.02f) * 0.1f + 1.25f) * float4;
        this.leftLid.yRot = 3.1415927f + float7;
        this.rightLid.yRot = -float7;
        this.leftPages.yRot = float7;
        this.rightPages.yRot = -float7;
        this.flipPage1.yRot = float7 - float7 * 2.0f * float2;
        this.flipPage2.yRot = float7 - float7 * 2.0f * float3;
        this.leftPages.x = Mth.sin(float7);
        this.rightPages.x = Mth.sin(float7);
        this.flipPage1.x = Mth.sin(float7);
        this.flipPage2.x = Mth.sin(float7);
    }
}
