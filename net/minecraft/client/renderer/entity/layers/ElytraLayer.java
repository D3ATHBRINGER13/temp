package net.minecraft.client.renderer.entity.layers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;

public class ElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation WINGS_LOCATION;
    private final ElytraModel<T> elytraModel;
    
    public ElytraLayer(final RenderLayerParent<T, M> dtr) {
        super(dtr);
        this.elytraModel = new ElytraModel<T>();
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack bcj10 = aix.getItemBySlot(EquipmentSlot.CHEST);
        if (bcj10.getItem() != Items.ELYTRA) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if (aix instanceof AbstractClientPlayer) {
            final AbstractClientPlayer dmm11 = (AbstractClientPlayer)aix;
            if (dmm11.isElytraLoaded() && dmm11.getElytraTextureLocation() != null) {
                this.bindTexture(dmm11.getElytraTextureLocation());
            }
            else if (dmm11.isCapeLoaded() && dmm11.getCloakTextureLocation() != null && dmm11.isModelPartShown(PlayerModelPart.CAPE)) {
                this.bindTexture(dmm11.getCloakTextureLocation());
            }
            else {
                this.bindTexture(ElytraLayer.WINGS_LOCATION);
            }
        }
        else {
            this.bindTexture(ElytraLayer.WINGS_LOCATION);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 0.125f);
        this.elytraModel.setupAnim(aix, float2, float3, float5, float6, float7, float8);
        this.elytraModel.setupAnim(aix, float2, float3, float5, float6, float7, float8);
        if (bcj10.isEnchanted()) {
            AbstractArmorLayer.<T>renderFoil((Consumer<ResourceLocation>)this::bindTexture, aix, this.elytraModel, float2, float3, float4, float5, float6, float7, float8);
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
    
    static {
        WINGS_LOCATION = new ResourceLocation("textures/entity/elytra.png");
    }
}
