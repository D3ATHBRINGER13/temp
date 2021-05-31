package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.model.EntityModel;
import java.util.function.Consumer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;

public abstract class AbstractArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    protected static final ResourceLocation ENCHANT_GLINT_LOCATION;
    protected final A innerModel;
    protected final A outerModel;
    private float alpha;
    private float red;
    private float green;
    private float blue;
    private boolean colorized;
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE;
    
    protected AbstractArmorLayer(final RenderLayerParent<T, M> dtr, final A dhp2, final A dhp3) {
        super(dtr);
        this.alpha = 1.0f;
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
        this.innerModel = dhp2;
        this.outerModel = dhp3;
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        this.renderArmorPiece(aix, float2, float3, float4, float5, float6, float7, float8, EquipmentSlot.CHEST);
        this.renderArmorPiece(aix, float2, float3, float4, float5, float6, float7, float8, EquipmentSlot.LEGS);
        this.renderArmorPiece(aix, float2, float3, float4, float5, float6, float7, float8, EquipmentSlot.FEET);
        this.renderArmorPiece(aix, float2, float3, float4, float5, float6, float7, float8, EquipmentSlot.HEAD);
    }
    
    @Override
    public boolean colorsOnDamage() {
        return false;
    }
    
    private void renderArmorPiece(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8, final EquipmentSlot ait) {
        final ItemStack bcj11 = aix.getItemBySlot(ait);
        if (!(bcj11.getItem() instanceof ArmorItem)) {
            return;
        }
        final ArmorItem bad12 = (ArmorItem)bcj11.getItem();
        if (bad12.getSlot() != ait) {
            return;
        }
        final A dhp13 = this.getArmorModel(ait);
        this.getParentModel().copyPropertiesTo(dhp13);
        dhp13.prepareMobModel(aix, float2, float3, float4);
        this.setPartVisibility(dhp13, ait);
        final boolean boolean14 = this.usesInnerModel(ait);
        this.bindTexture(this.getArmorLocation(bad12, boolean14));
        if (bad12 instanceof DyeableArmorItem) {
            final int integer15 = ((DyeableArmorItem)bad12).getColor(bcj11);
            final float float9 = (integer15 >> 16 & 0xFF) / 255.0f;
            final float float10 = (integer15 >> 8 & 0xFF) / 255.0f;
            final float float11 = (integer15 & 0xFF) / 255.0f;
            GlStateManager.color4f(this.red * float9, this.green * float10, this.blue * float11, this.alpha);
            dhp13.render(aix, float2, float3, float5, float6, float7, float8);
            this.bindTexture(this.getArmorLocation(bad12, boolean14, "overlay"));
        }
        GlStateManager.color4f(this.red, this.green, this.blue, this.alpha);
        dhp13.render(aix, float2, float3, float5, float6, float7, float8);
        if (!this.colorized && bcj11.isEnchanted()) {
            AbstractArmorLayer.<T>renderFoil((Consumer<ResourceLocation>)this::bindTexture, aix, dhp13, float2, float3, float4, float5, float6, float7, float8);
        }
    }
    
    public A getArmorModel(final EquipmentSlot ait) {
        return this.usesInnerModel(ait) ? this.innerModel : this.outerModel;
    }
    
    private boolean usesInnerModel(final EquipmentSlot ait) {
        return ait == EquipmentSlot.LEGS;
    }
    
    public static <T extends Entity> void renderFoil(final Consumer<ResourceLocation> consumer, final T aio, final EntityModel<T> dhh, final float float4, final float float5, final float float6, final float float7, final float float8, final float float9, final float float10) {
        final float float11 = aio.tickCount + float6;
        consumer.accept(AbstractArmorLayer.ENCHANT_GLINT_LOCATION);
        final GameRenderer dnc12 = Minecraft.getInstance().gameRenderer;
        dnc12.resetFogColor(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        final float float12 = 0.5f;
        GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        for (int integer14 = 0; integer14 < 2; ++integer14) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            final float float13 = 0.76f;
            GlStateManager.color4f(0.38f, 0.19f, 0.608f, 1.0f);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            final float float14 = 0.33333334f;
            GlStateManager.scalef(0.33333334f, 0.33333334f, 0.33333334f);
            GlStateManager.rotatef(30.0f - integer14 * 60.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.translatef(0.0f, float11 * (0.001f + integer14 * 0.003f) * 20.0f, 0.0f);
            GlStateManager.matrixMode(5888);
            dhh.render(aio, float4, float5, float7, float8, float9, float10);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        dnc12.resetFogColor(false);
    }
    
    private ResourceLocation getArmorLocation(final ArmorItem bad, final boolean boolean2) {
        return this.getArmorLocation(bad, boolean2, null);
    }
    
    private ResourceLocation getArmorLocation(final ArmorItem bad, final boolean boolean2, @Nullable final String string) {
        final String string2 = "textures/models/armor/" + bad.getMaterial().getName() + "_layer_" + (boolean2 ? 2 : 1) + ((string == null) ? "" : ("_" + string)) + ".png";
        return (ResourceLocation)AbstractArmorLayer.ARMOR_LOCATION_CACHE.computeIfAbsent(string2, ResourceLocation::new);
    }
    
    protected abstract void setPartVisibility(final A dhp, final EquipmentSlot ait);
    
    protected abstract void hideAllArmor(final A dhp);
    
    static {
        ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
        ARMOR_LOCATION_CACHE = (Map)Maps.newHashMap();
    }
}
