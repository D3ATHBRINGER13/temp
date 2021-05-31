package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;

public class SpinAttackEffectLayer<T extends LivingEntity> extends RenderLayer<T, PlayerModel<T>> {
    public static final ResourceLocation TEXTURE;
    private final SpinAttackModel model;
    
    public SpinAttackEffectLayer(final RenderLayerParent<T, PlayerModel<T>> dtr) {
        super(dtr);
        this.model = new SpinAttackModel();
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!aix.isAutoSpinAttack()) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(SpinAttackEffectLayer.TEXTURE);
        for (int integer10 = 0; integer10 < 3; ++integer10) {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(float5 * -(45 + integer10 * 5), 0.0f, 1.0f, 0.0f);
            final float float9 = 0.75f * integer10;
            GlStateManager.scalef(float9, float9, float9);
            GlStateManager.translatef(0.0f, -0.2f + 0.6f * integer10, 0.0f);
            this.model.render(float2, float3, float5, float6, float7, float8);
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
    
    static {
        TEXTURE = new ResourceLocation("textures/entity/trident_riptide.png");
    }
    
    static class SpinAttackModel extends Model {
        private final ModelPart box;
        
        public SpinAttackModel() {
            this.texWidth = 64;
            this.texHeight = 64;
            (this.box = new ModelPart(this, 0, 0)).addBox(-8.0f, -16.0f, -8.0f, 16, 32, 16);
        }
        
        public void render(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
            this.box.render(float6);
        }
    }
}
