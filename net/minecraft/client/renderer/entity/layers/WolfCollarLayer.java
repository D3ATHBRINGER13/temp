package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.WolfModel;
import net.minecraft.world.entity.animal.Wolf;

public class WolfCollarLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {
    private static final ResourceLocation WOLF_COLLAR_LOCATION;
    
    public WolfCollarLayer(final RenderLayerParent<Wolf, WolfModel<Wolf>> dtr) {
        super(dtr);
    }
    
    @Override
    public void render(final Wolf arz, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!arz.isTame() || arz.isInvisible()) {
            return;
        }
        this.bindTexture(WolfCollarLayer.WOLF_COLLAR_LOCATION);
        final float[] arr10 = arz.getCollarColor().getTextureDiffuseColors();
        GlStateManager.color3f(arr10[0], arr10[1], arr10[2]);
        ((RenderLayer<T, WolfModel<Wolf>>)this).getParentModel().setupAnim(arz, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return true;
    }
    
    static {
        WOLF_COLLAR_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_collar.png");
    }
}
