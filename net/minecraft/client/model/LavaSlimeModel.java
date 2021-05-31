package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Slime;

public class LavaSlimeModel<T extends Slime> extends EntityModel<T> {
    private final ModelPart[] bodyCubes;
    private final ModelPart insideCube;
    
    public LavaSlimeModel() {
        this.bodyCubes = new ModelPart[8];
        for (int integer2 = 0; integer2 < this.bodyCubes.length; ++integer2) {
            int integer3 = 0;
            int integer4;
            if ((integer4 = integer2) == 2) {
                integer3 = 24;
                integer4 = 10;
            }
            else if (integer2 == 3) {
                integer3 = 24;
                integer4 = 19;
            }
            (this.bodyCubes[integer2] = new ModelPart(this, integer3, integer4)).addBox(-4.0f, (float)(16 + integer2), -4.0f, 8, 1, 8);
        }
        (this.insideCube = new ModelPart(this, 0, 16)).addBox(-2.0f, 18.0f, -2.0f, 4, 4, 4);
    }
    
    @Override
    public void prepareMobModel(final T ave, final float float2, final float float3, final float float4) {
        float float5 = Mth.lerp(float4, ave.oSquish, ave.squish);
        if (float5 < 0.0f) {
            float5 = 0.0f;
        }
        for (int integer7 = 0; integer7 < this.bodyCubes.length; ++integer7) {
            this.bodyCubes[integer7].y = -(4 - integer7) * float5 * 1.7f;
        }
    }
    
    @Override
    public void render(final T ave, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.setupAnim(ave, float2, float3, float4, float5, float6, float7);
        this.insideCube.render(float7);
        for (final ModelPart djv12 : this.bodyCubes) {
            djv12.render(float7);
        }
    }
}
