package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.PigModel;
import net.minecraft.world.entity.animal.Pig;

public class PigSaddleLayer extends RenderLayer<Pig, PigModel<Pig>> {
    private static final ResourceLocation SADDLE_LOCATION;
    private final PigModel<Pig> model;
    
    public PigSaddleLayer(final RenderLayerParent<Pig, PigModel<Pig>> dtr) {
        super(dtr);
        this.model = new PigModel<Pig>(0.5f);
    }
    
    @Override
    public void render(final Pig arn, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!arn.hasSaddle()) {
            return;
        }
        this.bindTexture(PigSaddleLayer.SADDLE_LOCATION);
        ((RenderLayer<T, PigModel<Pig>>)this).getParentModel().copyPropertiesTo(this.model);
        this.model.render(arn, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
    
    static {
        SADDLE_LOCATION = new ResourceLocation("textures/entity/pig/pig_saddle.png");
    }
}
