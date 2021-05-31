package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.entity.layers.CarriedBlockLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.world.entity.monster.EnderMan;

public class EndermanRenderer extends MobRenderer<EnderMan, EndermanModel<EnderMan>> {
    private static final ResourceLocation ENDERMAN_LOCATION;
    private final Random random;
    
    public EndermanRenderer(final EntityRenderDispatcher dsa) {
        super(dsa, new EndermanModel(0.0f), 0.5f);
        this.random = new Random();
        this.addLayer((RenderLayer<EnderMan, EndermanModel<EnderMan>>)new EnderEyesLayer((RenderLayerParent<LivingEntity, EndermanModel<LivingEntity>>)this));
        this.addLayer(new CarriedBlockLayer(this));
    }
    
    @Override
    public void render(final EnderMan aui, double double2, final double double3, double double4, final float float5, final float float6) {
        final BlockState bvt11 = aui.getCarriedBlock();
        final EndermanModel<EnderMan> dhf12 = ((LivingEntityRenderer<T, EndermanModel<EnderMan>>)this).getModel();
        dhf12.carrying = (bvt11 != null);
        dhf12.creepy = aui.isCreepy();
        if (aui.isCreepy()) {
            final double double5 = 0.02;
            double2 += this.random.nextGaussian() * 0.02;
            double4 += this.random.nextGaussian() * 0.02;
        }
        super.render(aui, double2, double3, double4, float5, float6);
    }
    
    protected ResourceLocation getTextureLocation(final EnderMan aui) {
        return EndermanRenderer.ENDERMAN_LOCATION;
    }
    
    static {
        ENDERMAN_LOCATION = new ResourceLocation("textures/entity/enderman/enderman.png");
    }
}
