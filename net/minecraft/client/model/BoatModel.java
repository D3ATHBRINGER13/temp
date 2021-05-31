package net.minecraft.client.model;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatModel extends EntityModel<Boat> {
    private final ModelPart[] cubes;
    private final ModelPart[] paddles;
    private final ModelPart waterPatch;
    
    public BoatModel() {
        this.cubes = new ModelPart[5];
        this.paddles = new ModelPart[2];
        this.cubes[0] = new ModelPart(this, 0, 0).setTexSize(128, 64);
        this.cubes[1] = new ModelPart(this, 0, 19).setTexSize(128, 64);
        this.cubes[2] = new ModelPart(this, 0, 27).setTexSize(128, 64);
        this.cubes[3] = new ModelPart(this, 0, 35).setTexSize(128, 64);
        this.cubes[4] = new ModelPart(this, 0, 43).setTexSize(128, 64);
        final int integer2 = 32;
        final int integer3 = 6;
        final int integer4 = 20;
        final int integer5 = 4;
        final int integer6 = 28;
        this.cubes[0].addBox(-14.0f, -9.0f, -3.0f, 28, 16, 3, 0.0f);
        this.cubes[0].setPos(0.0f, 3.0f, 1.0f);
        this.cubes[1].addBox(-13.0f, -7.0f, -1.0f, 18, 6, 2, 0.0f);
        this.cubes[1].setPos(-15.0f, 4.0f, 4.0f);
        this.cubes[2].addBox(-8.0f, -7.0f, -1.0f, 16, 6, 2, 0.0f);
        this.cubes[2].setPos(15.0f, 4.0f, 0.0f);
        this.cubes[3].addBox(-14.0f, -7.0f, -1.0f, 28, 6, 2, 0.0f);
        this.cubes[3].setPos(0.0f, 4.0f, -9.0f);
        this.cubes[4].addBox(-14.0f, -7.0f, -1.0f, 28, 6, 2, 0.0f);
        this.cubes[4].setPos(0.0f, 4.0f, 9.0f);
        this.cubes[0].xRot = 1.5707964f;
        this.cubes[1].yRot = 4.712389f;
        this.cubes[2].yRot = 1.5707964f;
        this.cubes[3].yRot = 3.1415927f;
        (this.paddles[0] = this.makePaddle(true)).setPos(3.0f, -5.0f, 9.0f);
        (this.paddles[1] = this.makePaddle(false)).setPos(3.0f, -5.0f, -9.0f);
        this.paddles[1].yRot = 3.1415927f;
        this.paddles[0].zRot = 0.19634955f;
        this.paddles[1].zRot = 0.19634955f;
        (this.waterPatch = new ModelPart(this, 0, 0).setTexSize(128, 64)).addBox(-14.0f, -9.0f, -3.0f, 28, 16, 3, 0.0f);
        this.waterPatch.setPos(0.0f, -3.0f, 1.0f);
        this.waterPatch.xRot = 1.5707964f;
    }
    
    @Override
    public void render(final Boat axw, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        this.setupAnim(axw, float2, float3, float4, float5, float6, float7);
        for (int integer9 = 0; integer9 < 5; ++integer9) {
            this.cubes[integer9].render(float7);
        }
        this.animatePaddle(axw, 0, float7, float2);
        this.animatePaddle(axw, 1, float7, float2);
    }
    
    public void renderSecondPass(final Entity aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.colorMask(false, false, false, false);
        this.waterPatch.render(float7);
        GlStateManager.colorMask(true, true, true, true);
    }
    
    protected ModelPart makePaddle(final boolean boolean1) {
        final ModelPart djv3 = new ModelPart(this, 62, boolean1 ? 0 : 20).setTexSize(128, 64);
        final int integer4 = 20;
        final int integer5 = 7;
        final int integer6 = 6;
        final float float7 = -5.0f;
        djv3.addBox(-1.0f, 0.0f, -5.0f, 2, 2, 18);
        djv3.addBox(boolean1 ? -1.001f : 0.001f, -3.0f, 8.0f, 1, 6, 7);
        return djv3;
    }
    
    protected void animatePaddle(final Boat axw, final int integer, final float float3, final float float4) {
        final float float5 = axw.getRowingTime(integer, float4);
        final ModelPart djv7 = this.paddles[integer];
        djv7.xRot = (float)Mth.clampedLerp(-1.0471975803375244, -0.2617993950843811, (Mth.sin(-float5) + 1.0f) / 2.0f);
        djv7.yRot = (float)Mth.clampedLerp(-0.7853981852531433, 0.7853981852531433, (Mth.sin(-float5 + 1.0f) + 1.0f) / 2.0f);
        if (integer == 1) {
            djv7.yRot = 3.1415927f - djv7.yRot;
        }
        djv7.render(float3);
    }
}
