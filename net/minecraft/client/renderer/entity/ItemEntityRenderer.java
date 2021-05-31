package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.Mth;
import net.minecraft.client.resources.model.BakedModel;
import java.util.Random;
import net.minecraft.world.entity.item.ItemEntity;

public class ItemEntityRenderer extends EntityRenderer<ItemEntity> {
    private final ItemRenderer itemRenderer;
    private final Random random;
    
    public ItemEntityRenderer(final EntityRenderDispatcher dsa, final ItemRenderer dsv) {
        super(dsa);
        this.random = new Random();
        this.itemRenderer = dsv;
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.75f;
    }
    
    private int setupBobbingItem(final ItemEntity atx, final double double2, final double double3, final double double4, final float float5, final BakedModel dyp) {
        final ItemStack bcj11 = atx.getItem();
        final Item bce12 = bcj11.getItem();
        if (bce12 == null) {
            return 0;
        }
        final boolean boolean13 = dyp.isGui3d();
        final int integer14 = this.getRenderAmount(bcj11);
        final float float6 = 0.25f;
        final float float7 = Mth.sin((atx.getAge() + float5) / 10.0f + atx.bobOffs) * 0.1f + 0.1f;
        final float float8 = dyp.getTransforms().getTransform(ItemTransforms.TransformType.GROUND).scale.y();
        GlStateManager.translatef((float)double2, (float)double3 + float7 + 0.25f * float8, (float)double4);
        if (boolean13 || this.entityRenderDispatcher.options != null) {
            final float float9 = ((atx.getAge() + float5) / 20.0f + atx.bobOffs) * 57.295776f;
            GlStateManager.rotatef(float9, 0.0f, 1.0f, 0.0f);
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        return integer14;
    }
    
    private int getRenderAmount(final ItemStack bcj) {
        int integer3 = 1;
        if (bcj.getCount() > 48) {
            integer3 = 5;
        }
        else if (bcj.getCount() > 32) {
            integer3 = 4;
        }
        else if (bcj.getCount() > 16) {
            integer3 = 3;
        }
        else if (bcj.getCount() > 1) {
            integer3 = 2;
        }
        return integer3;
    }
    
    @Override
    public void render(final ItemEntity atx, final double double2, final double double3, final double double4, final float float5, final float float6) {
        final ItemStack bcj11 = atx.getItem();
        final int integer12 = bcj11.isEmpty() ? 187 : (Item.getId(bcj11.getItem()) + bcj11.getDamageValue());
        this.random.setSeed((long)integer12);
        boolean boolean13 = false;
        if (this.bindTexture(atx)) {
            this.entityRenderDispatcher.textureManager.getTexture(this.getTextureLocation(atx)).pushFilter(false, false);
            boolean13 = true;
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableBlend();
        Lighting.turnOn();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        final BakedModel dyp14 = this.itemRenderer.getModel(bcj11, atx.level, null);
        final int integer13 = this.setupBobbingItem(atx, double2, double3, double4, float6, dyp14);
        final float float7 = dyp14.getTransforms().ground.scale.x();
        final float float8 = dyp14.getTransforms().ground.scale.y();
        final float float9 = dyp14.getTransforms().ground.scale.z();
        final boolean boolean14 = dyp14.isGui3d();
        if (!boolean14) {
            final float float10 = -0.0f * (integer13 - 1) * 0.5f * float7;
            final float float11 = -0.0f * (integer13 - 1) * 0.5f * float8;
            final float float12 = -0.09375f * (integer13 - 1) * 0.5f * float9;
            GlStateManager.translatef(float10, float11, float12);
        }
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(atx));
        }
        for (int integer14 = 0; integer14 < integer13; ++integer14) {
            if (boolean14) {
                GlStateManager.pushMatrix();
                if (integer14 > 0) {
                    final float float11 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    final float float12 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    final float float13 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    GlStateManager.translatef(float11, float12, float13);
                }
                dyp14.getTransforms().apply(ItemTransforms.TransformType.GROUND);
                this.itemRenderer.render(bcj11, dyp14);
                GlStateManager.popMatrix();
            }
            else {
                GlStateManager.pushMatrix();
                if (integer14 > 0) {
                    final float float11 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    final float float12 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    GlStateManager.translatef(float11, float12, 0.0f);
                }
                dyp14.getTransforms().apply(ItemTransforms.TransformType.GROUND);
                this.itemRenderer.render(bcj11, dyp14);
                GlStateManager.popMatrix();
                GlStateManager.translatef(0.0f * float7, 0.0f * float8, 0.09375f * float9);
            }
        }
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindTexture(atx);
        if (boolean13) {
            this.entityRenderDispatcher.textureManager.getTexture(this.getTextureLocation(atx)).popFilter();
        }
        super.render(atx, double2, double3, double4, float5, float6);
    }
    
    @Override
    protected ResourceLocation getTextureLocation(final ItemEntity atx) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
