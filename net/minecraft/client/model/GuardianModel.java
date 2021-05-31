package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Guardian;

public class GuardianModel extends EntityModel<Guardian> {
    private static final float[] SPIKE_X_ROT;
    private static final float[] SPIKE_Y_ROT;
    private static final float[] SPIKE_Z_ROT;
    private static final float[] SPIKE_X;
    private static final float[] SPIKE_Y;
    private static final float[] SPIKE_Z;
    private final ModelPart head;
    private final ModelPart eye;
    private final ModelPart[] spikeParts;
    private final ModelPart[] tailParts;
    
    public GuardianModel() {
        this.texWidth = 64;
        this.texHeight = 64;
        this.spikeParts = new ModelPart[12];
        this.head = new ModelPart(this);
        this.head.texOffs(0, 0).addBox(-6.0f, 10.0f, -8.0f, 12, 12, 16);
        this.head.texOffs(0, 28).addBox(-8.0f, 10.0f, -6.0f, 2, 12, 12);
        this.head.texOffs(0, 28).addBox(6.0f, 10.0f, -6.0f, 2, 12, 12, true);
        this.head.texOffs(16, 40).addBox(-6.0f, 8.0f, -6.0f, 12, 2, 12);
        this.head.texOffs(16, 40).addBox(-6.0f, 22.0f, -6.0f, 12, 2, 12);
        for (int integer2 = 0; integer2 < this.spikeParts.length; ++integer2) {
            (this.spikeParts[integer2] = new ModelPart(this, 0, 0)).addBox(-1.0f, -4.5f, -1.0f, 2, 9, 2);
            this.head.addChild(this.spikeParts[integer2]);
        }
        (this.eye = new ModelPart(this, 8, 0)).addBox(-1.0f, 15.0f, 0.0f, 2, 2, 1);
        this.head.addChild(this.eye);
        this.tailParts = new ModelPart[3];
        (this.tailParts[0] = new ModelPart(this, 40, 0)).addBox(-2.0f, 14.0f, 7.0f, 4, 4, 8);
        (this.tailParts[1] = new ModelPart(this, 0, 54)).addBox(0.0f, 14.0f, 0.0f, 3, 3, 7);
        this.tailParts[2] = new ModelPart(this);
        this.tailParts[2].texOffs(41, 32).addBox(0.0f, 14.0f, 0.0f, 2, 2, 6);
        this.tailParts[2].texOffs(25, 19).addBox(1.0f, 10.5f, 3.0f, 1, 9, 9);
        this.head.addChild(this.tailParts[0]);
        this.tailParts[0].addChild(this.tailParts[1]);
        this.tailParts[1].addChild(this.tailParts[2]);
    }
    
    @Override
    public void setupAnim(final Guardian auo, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(auo, float2, float3, float4, float5, float6, float7);
        this.head.render(float7);
    }
    
    @Override
    public void setupAnim(final Guardian auo, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        final float float8 = float4 - auo.tickCount;
        this.head.yRot = float5 * 0.017453292f;
        this.head.xRot = float6 * 0.017453292f;
        final float float9 = (1.0f - auo.getSpikesAnimation(float8)) * 0.55f;
        for (int integer11 = 0; integer11 < 12; ++integer11) {
            this.spikeParts[integer11].xRot = 3.1415927f * GuardianModel.SPIKE_X_ROT[integer11];
            this.spikeParts[integer11].yRot = 3.1415927f * GuardianModel.SPIKE_Y_ROT[integer11];
            this.spikeParts[integer11].zRot = 3.1415927f * GuardianModel.SPIKE_Z_ROT[integer11];
            this.spikeParts[integer11].x = GuardianModel.SPIKE_X[integer11] * (1.0f + Mth.cos(float4 * 1.5f + integer11) * 0.01f - float9);
            this.spikeParts[integer11].y = 16.0f + GuardianModel.SPIKE_Y[integer11] * (1.0f + Mth.cos(float4 * 1.5f + integer11) * 0.01f - float9);
            this.spikeParts[integer11].z = GuardianModel.SPIKE_Z[integer11] * (1.0f + Mth.cos(float4 * 1.5f + integer11) * 0.01f - float9);
        }
        this.eye.z = -8.25f;
        Entity aio11 = Minecraft.getInstance().getCameraEntity();
        if (auo.hasActiveAttackTarget()) {
            aio11 = auo.getActiveAttackTarget();
        }
        if (aio11 != null) {
            final Vec3 csi12 = aio11.getEyePosition(0.0f);
            final Vec3 csi13 = auo.getEyePosition(0.0f);
            final double double14 = csi12.y - csi13.y;
            if (double14 > 0.0) {
                this.eye.y = 0.0f;
            }
            else {
                this.eye.y = 1.0f;
            }
            Vec3 csi14 = auo.getViewVector(0.0f);
            csi14 = new Vec3(csi14.x, 0.0, csi14.z);
            final Vec3 csi15 = new Vec3(csi13.x - csi12.x, 0.0, csi13.z - csi12.z).normalize().yRot(1.5707964f);
            final double double15 = csi14.dot(csi15);
            this.eye.x = Mth.sqrt((float)Math.abs(double15)) * 2.0f * (float)Math.signum(double15);
        }
        this.eye.visible = true;
        final float float10 = auo.getTailAnimation(float8);
        this.tailParts[0].yRot = Mth.sin(float10) * 3.1415927f * 0.05f;
        this.tailParts[1].yRot = Mth.sin(float10) * 3.1415927f * 0.1f;
        this.tailParts[1].x = -1.5f;
        this.tailParts[1].y = 0.5f;
        this.tailParts[1].z = 14.0f;
        this.tailParts[2].yRot = Mth.sin(float10) * 3.1415927f * 0.15f;
        this.tailParts[2].x = 0.5f;
        this.tailParts[2].y = 0.5f;
        this.tailParts[2].z = 6.0f;
    }
    
    static {
        SPIKE_X_ROT = new float[] { 1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f };
        SPIKE_Y_ROT = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f };
        SPIKE_Z_ROT = new float[] { 0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f };
        SPIKE_X = new float[] { 0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f };
        SPIKE_Y = new float[] { -8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f };
        SPIKE_Z = new float[] { 8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f };
    }
}
