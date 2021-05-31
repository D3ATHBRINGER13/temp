package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossModel<T extends WitherBoss> extends EntityModel<T> {
    private final ModelPart[] upperBodyParts;
    private final ModelPart[] heads;
    
    public WitherBossModel(final float float1) {
        this.texWidth = 64;
        this.texHeight = 64;
        this.upperBodyParts = new ModelPart[3];
        (this.upperBodyParts[0] = new ModelPart(this, 0, 16)).addBox(-10.0f, 3.9f, -0.5f, 20, 3, 3, float1);
        (this.upperBodyParts[1] = new ModelPart(this).setTexSize(this.texWidth, this.texHeight)).setPos(-2.0f, 6.9f, -0.5f);
        this.upperBodyParts[1].texOffs(0, 22).addBox(0.0f, 0.0f, 0.0f, 3, 10, 3, float1);
        this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0f, 1.5f, 0.5f, 11, 2, 2, float1);
        this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0f, 4.0f, 0.5f, 11, 2, 2, float1);
        this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0f, 6.5f, 0.5f, 11, 2, 2, float1);
        (this.upperBodyParts[2] = new ModelPart(this, 12, 22)).addBox(0.0f, 0.0f, 0.0f, 3, 6, 3, float1);
        this.heads = new ModelPart[3];
        (this.heads[0] = new ModelPart(this, 0, 0)).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8, float1);
        (this.heads[1] = new ModelPart(this, 32, 0)).addBox(-4.0f, -4.0f, -4.0f, 6, 6, 6, float1);
        this.heads[1].x = -8.0f;
        this.heads[1].y = 4.0f;
        (this.heads[2] = new ModelPart(this, 32, 0)).addBox(-4.0f, -4.0f, -4.0f, 6, 6, 6, float1);
        this.heads[2].x = 10.0f;
        this.heads[2].y = 4.0f;
    }
    
    @Override
    public void render(final T atj, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.render(atj, float2, float3, float4, float5, float6, float7);
        for (final ModelPart djv12 : this.heads) {
            djv12.render(float7);
        }
        for (final ModelPart djv12 : this.upperBodyParts) {
            djv12.render(float7);
        }
    }
    
    @Override
    public void render(final T atj, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        final float float8 = Mth.cos(float4 * 0.1f);
        this.upperBodyParts[1].xRot = (0.065f + 0.05f * float8) * 3.1415927f;
        this.upperBodyParts[2].setPos(-2.0f, 6.9f + Mth.cos(this.upperBodyParts[1].xRot) * 10.0f, -0.5f + Mth.sin(this.upperBodyParts[1].xRot) * 10.0f);
        this.upperBodyParts[2].xRot = (0.265f + 0.1f * float8) * 3.1415927f;
        this.heads[0].yRot = float5 * 0.017453292f;
        this.heads[0].xRot = float6 * 0.017453292f;
    }
    
    @Override
    public void prepareMobModel(final T atj, final float float2, final float float3, final float float4) {
        for (int integer6 = 1; integer6 < 3; ++integer6) {
            this.heads[integer6].yRot = (atj.getHeadYRot(integer6 - 1) - atj.yBodyRot) * 0.017453292f;
            this.heads[integer6].xRot = atj.getHeadXRot(integer6 - 1) * 0.017453292f;
        }
    }
}
