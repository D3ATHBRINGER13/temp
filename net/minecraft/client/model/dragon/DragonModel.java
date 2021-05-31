package net.minecraft.client.model.dragon;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.client.model.EntityModel;

public class DragonModel extends EntityModel<EnderDragon> {
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart jaw;
    private final ModelPart body;
    private final ModelPart rearLeg;
    private final ModelPart frontLeg;
    private final ModelPart rearLegTip;
    private final ModelPart frontLegTip;
    private final ModelPart rearFoot;
    private final ModelPart frontFoot;
    private final ModelPart wing;
    private final ModelPart wingTip;
    private float a;
    
    public DragonModel(final float float1) {
        this.texWidth = 256;
        this.texHeight = 256;
        final float float2 = -16.0f;
        (this.head = new ModelPart((Model)this, "head")).addBox("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, float1, 176, 44);
        this.head.addBox("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, float1, 112, 30);
        this.head.mirror = true;
        this.head.addBox("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, float1, 0, 0);
        this.head.addBox("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, float1, 112, 0);
        this.head.mirror = false;
        this.head.addBox("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, float1, 0, 0);
        this.head.addBox("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, float1, 112, 0);
        (this.jaw = new ModelPart((Model)this, "jaw")).setPos(0.0f, 4.0f, -8.0f);
        this.jaw.addBox("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, float1, 176, 65);
        this.head.addChild(this.jaw);
        (this.neck = new ModelPart((Model)this, "neck")).addBox("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, float1, 192, 104);
        this.neck.addBox("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, float1, 48, 0);
        (this.body = new ModelPart((Model)this, "body")).setPos(0.0f, 4.0f, 8.0f);
        this.body.addBox("body", -12.0f, 0.0f, -16.0f, 24, 24, 64, float1, 0, 0);
        this.body.addBox("scale", -1.0f, -6.0f, -10.0f, 2, 6, 12, float1, 220, 53);
        this.body.addBox("scale", -1.0f, -6.0f, 10.0f, 2, 6, 12, float1, 220, 53);
        this.body.addBox("scale", -1.0f, -6.0f, 30.0f, 2, 6, 12, float1, 220, 53);
        (this.wing = new ModelPart((Model)this, "wing")).setPos(-12.0f, 5.0f, 2.0f);
        this.wing.addBox("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8, float1, 112, 88);
        this.wing.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, float1, -56, 88);
        (this.wingTip = new ModelPart((Model)this, "wingtip")).setPos(-56.0f, 0.0f, 0.0f);
        this.wingTip.addBox("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4, float1, 112, 136);
        this.wingTip.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, float1, -56, 144);
        this.wing.addChild(this.wingTip);
        (this.frontLeg = new ModelPart((Model)this, "frontleg")).setPos(-12.0f, 20.0f, 2.0f);
        this.frontLeg.addBox("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, float1, 112, 104);
        (this.frontLegTip = new ModelPart((Model)this, "frontlegtip")).setPos(0.0f, 20.0f, -1.0f);
        this.frontLegTip.addBox("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, float1, 226, 138);
        this.frontLeg.addChild(this.frontLegTip);
        (this.frontFoot = new ModelPart((Model)this, "frontfoot")).setPos(0.0f, 23.0f, 0.0f);
        this.frontFoot.addBox("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, float1, 144, 104);
        this.frontLegTip.addChild(this.frontFoot);
        (this.rearLeg = new ModelPart((Model)this, "rearleg")).setPos(-16.0f, 16.0f, 42.0f);
        this.rearLeg.addBox("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, float1, 0, 0);
        (this.rearLegTip = new ModelPart((Model)this, "rearlegtip")).setPos(0.0f, 32.0f, -4.0f);
        this.rearLegTip.addBox("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, float1, 196, 0);
        this.rearLeg.addChild(this.rearLegTip);
        (this.rearFoot = new ModelPart((Model)this, "rearfoot")).setPos(0.0f, 31.0f, 4.0f);
        this.rearFoot.addBox("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, float1, 112, 0);
        this.rearLegTip.addChild(this.rearFoot);
    }
    
    @Override
    public void prepareMobModel(final EnderDragon asp, final float float2, final float float3, final float float4) {
        this.a = float4;
    }
    
    @Override
    public void render(final EnderDragon asp, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        GlStateManager.pushMatrix();
        final float float8 = Mth.lerp(this.a, asp.oFlapTime, asp.flapTime);
        this.jaw.xRot = (float)(Math.sin((double)(float8 * 6.2831855f)) + 1.0) * 0.2f;
        float float9 = (float)(Math.sin((double)(float8 * 6.2831855f - 1.0f)) + 1.0);
        float9 = (float9 * float9 + float9 * 2.0f) * 0.05f;
        GlStateManager.translatef(0.0f, float9 - 2.0f, -3.0f);
        GlStateManager.rotatef(float9 * 2.0f, 1.0f, 0.0f, 0.0f);
        float float10 = 0.0f;
        float float11 = 20.0f;
        float float12 = -12.0f;
        final float float13 = 1.5f;
        double[] arr15 = asp.getLatencyPos(6, this.a);
        final float float14 = this.rotWrap(asp.getLatencyPos(5, this.a)[0] - asp.getLatencyPos(10, this.a)[0]);
        final float float15 = this.rotWrap(asp.getLatencyPos(5, this.a)[0] + float14 / 2.0f);
        float float16 = float8 * 6.2831855f;
        for (int integer19 = 0; integer19 < 5; ++integer19) {
            final double[] arr16 = asp.getLatencyPos(5 - integer19, this.a);
            final float float17 = (float)Math.cos((double)(integer19 * 0.45f + float16)) * 0.15f;
            this.neck.yRot = this.rotWrap(arr16[0] - arr15[0]) * 0.017453292f * 1.5f;
            this.neck.xRot = float17 + asp.getHeadPartYOffset(integer19, arr15, arr16) * 0.017453292f * 1.5f * 5.0f;
            this.neck.zRot = -this.rotWrap(arr16[0] - float15) * 0.017453292f * 1.5f;
            this.neck.y = float11;
            this.neck.z = float12;
            this.neck.x = float10;
            float11 += (float)(Math.sin((double)this.neck.xRot) * 10.0);
            float12 -= (float)(Math.cos((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0);
            float10 -= (float)(Math.sin((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0);
            this.neck.render(float7);
        }
        this.head.y = float11;
        this.head.z = float12;
        this.head.x = float10;
        double[] arr17 = asp.getLatencyPos(0, this.a);
        this.head.yRot = this.rotWrap(arr17[0] - arr15[0]) * 0.017453292f;
        this.head.xRot = this.rotWrap(asp.getHeadPartYOffset(6, arr15, arr17)) * 0.017453292f * 1.5f * 5.0f;
        this.head.zRot = -this.rotWrap(arr17[0] - float15) * 0.017453292f;
        this.head.render(float7);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-float14 * 1.5f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translatef(0.0f, -1.0f, 0.0f);
        this.body.zRot = 0.0f;
        this.body.render(float7);
        for (int integer20 = 0; integer20 < 2; ++integer20) {
            GlStateManager.enableCull();
            final float float17 = float8 * 6.2831855f;
            this.wing.xRot = 0.125f - (float)Math.cos((double)float17) * 0.2f;
            this.wing.yRot = 0.25f;
            this.wing.zRot = (float)(Math.sin((double)float17) + 0.125) * 0.8f;
            this.wingTip.zRot = -(float)(Math.sin((double)(float17 + 2.0f)) + 0.5) * 0.75f;
            this.rearLeg.xRot = 1.0f + float9 * 0.1f;
            this.rearLegTip.xRot = 0.5f + float9 * 0.1f;
            this.rearFoot.xRot = 0.75f + float9 * 0.1f;
            this.frontLeg.xRot = 1.3f + float9 * 0.1f;
            this.frontLegTip.xRot = -0.5f - float9 * 0.1f;
            this.frontFoot.xRot = 0.75f + float9 * 0.1f;
            this.wing.render(float7);
            this.frontLeg.render(float7);
            this.rearLeg.render(float7);
            GlStateManager.scalef(-1.0f, 1.0f, 1.0f);
            if (integer20 == 0) {
                GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableCull();
        float float18 = -(float)Math.sin((double)(float8 * 6.2831855f)) * 0.0f;
        float16 = float8 * 6.2831855f;
        float11 = 10.0f;
        float12 = 60.0f;
        float10 = 0.0f;
        arr15 = asp.getLatencyPos(11, this.a);
        for (int integer21 = 0; integer21 < 12; ++integer21) {
            arr17 = asp.getLatencyPos(12 + integer21, this.a);
            float18 += (float)(Math.sin((double)(integer21 * 0.45f + float16)) * 0.05000000074505806);
            this.neck.yRot = (this.rotWrap(arr17[0] - arr15[0]) * 1.5f + 180.0f) * 0.017453292f;
            this.neck.xRot = float18 + (float)(arr17[1] - arr15[1]) * 0.017453292f * 1.5f * 5.0f;
            this.neck.zRot = this.rotWrap(arr17[0] - float15) * 0.017453292f * 1.5f;
            this.neck.y = float11;
            this.neck.z = float12;
            this.neck.x = float10;
            float11 += (float)(Math.sin((double)this.neck.xRot) * 10.0);
            float12 -= (float)(Math.cos((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0);
            float10 -= (float)(Math.sin((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0);
            this.neck.render(float7);
        }
        GlStateManager.popMatrix();
    }
    
    private float rotWrap(double double1) {
        while (double1 >= 180.0) {
            double1 -= 360.0;
        }
        while (double1 < -180.0) {
            double1 += 360.0;
        }
        return (float)double1;
    }
}
