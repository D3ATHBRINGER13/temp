package net.minecraft.client.renderer.entity;

import net.minecraft.util.Mth;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;

public class DolphinModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart tailFin;
    
    public DolphinModel() {
        this.texWidth = 64;
        this.texHeight = 64;
        final float float2 = 18.0f;
        final float float3 = -8.0f;
        (this.body = new ModelPart(this, 22, 0)).addBox(-4.0f, -7.0f, 0.0f, 8, 7, 13);
        this.body.setPos(0.0f, 22.0f, -5.0f);
        final ModelPart djv4 = new ModelPart(this, 51, 0);
        djv4.addBox(-0.5f, 0.0f, 8.0f, 1, 4, 5);
        djv4.xRot = 1.0471976f;
        this.body.addChild(djv4);
        final ModelPart djv5 = new ModelPart(this, 48, 20);
        djv5.mirror = true;
        djv5.addBox(-0.5f, -4.0f, 0.0f, 1, 4, 7);
        djv5.setPos(2.0f, -2.0f, 4.0f);
        djv5.xRot = 1.0471976f;
        djv5.zRot = 2.0943952f;
        this.body.addChild(djv5);
        final ModelPart djv6 = new ModelPart(this, 48, 20);
        djv6.addBox(-0.5f, -4.0f, 0.0f, 1, 4, 7);
        djv6.setPos(-2.0f, -2.0f, 4.0f);
        djv6.xRot = 1.0471976f;
        djv6.zRot = -2.0943952f;
        this.body.addChild(djv6);
        (this.tail = new ModelPart(this, 0, 19)).addBox(-2.0f, -2.5f, 0.0f, 4, 5, 11);
        this.tail.setPos(0.0f, -2.5f, 11.0f);
        this.tail.xRot = -0.10471976f;
        this.body.addChild(this.tail);
        (this.tailFin = new ModelPart(this, 19, 20)).addBox(-5.0f, -0.5f, 0.0f, 10, 1, 6);
        this.tailFin.setPos(0.0f, 0.0f, 9.0f);
        this.tailFin.xRot = 0.0f;
        this.tail.addChild(this.tailFin);
        (this.head = new ModelPart(this, 0, 0)).addBox(-4.0f, -3.0f, -3.0f, 8, 7, 6);
        this.head.setPos(0.0f, -4.0f, -3.0f);
        final ModelPart djv7 = new ModelPart(this, 0, 13);
        djv7.addBox(-1.0f, 2.0f, -7.0f, 2, 2, 4);
        this.head.addChild(djv7);
        this.body.addChild(this.head);
    }
    
    @Override
    public void render(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.body.render(float7);
    }
    
    @Override
    public void setupAnim(final T aio, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.body.xRot = float6 * 0.017453292f;
        this.body.yRot = float5 * 0.017453292f;
        if (Entity.getHorizontalDistanceSqr(aio.getDeltaMovement()) > 1.0E-7) {
            final ModelPart body = this.body;
            body.xRot += -0.05f + -0.05f * Mth.cos(float4 * 0.3f);
            this.tail.xRot = -0.1f * Mth.cos(float4 * 0.3f);
            this.tailFin.xRot = -0.2f * Mth.cos(float4 * 0.3f);
        }
    }
}
