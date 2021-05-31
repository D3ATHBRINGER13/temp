package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.MapItem;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.item.Items;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ItemFrame;

public class ItemFrameRenderer extends EntityRenderer<ItemFrame> {
    private static final ResourceLocation MAP_BACKGROUND_LOCATION;
    private static final ModelResourceLocation FRAME_LOCATION;
    private static final ModelResourceLocation MAP_FRAME_LOCATION;
    private final Minecraft minecraft;
    private final ItemRenderer itemRenderer;
    
    public ItemFrameRenderer(final EntityRenderDispatcher dsa, final ItemRenderer dsv) {
        super(dsa);
        this.minecraft = Minecraft.getInstance();
        this.itemRenderer = dsv;
    }
    
    @Override
    public void render(final ItemFrame atn, final double double2, final double double3, final double double4, final float float5, final float float6) {
        GlStateManager.pushMatrix();
        final BlockPos ew11 = atn.getPos();
        final double double5 = ew11.getX() - atn.x + double2;
        final double double6 = ew11.getY() - atn.y + double3;
        final double double7 = ew11.getZ() - atn.z + double4;
        GlStateManager.translated(double5 + 0.5, double6 + 0.5, double7 + 0.5);
        GlStateManager.rotatef(atn.xRot, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(180.0f - atn.yRot, 0.0f, 1.0f, 0.0f);
        this.entityRenderDispatcher.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
        final BlockRenderDispatcher dnw18 = this.minecraft.getBlockRenderer();
        final ModelManager dyt19 = dnw18.getBlockModelShaper().getModelManager();
        final ModelResourceLocation dyu20 = (atn.getItem().getItem() == Items.FILLED_MAP) ? ItemFrameRenderer.MAP_FRAME_LOCATION : ItemFrameRenderer.FRAME_LOCATION;
        GlStateManager.pushMatrix();
        GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(atn));
        }
        dnw18.getModelRenderer().renderModel(dyt19.getModel(dyu20), 1.0f, 1.0f, 1.0f, 1.0f);
        if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        if (atn.getItem().getItem() == Items.FILLED_MAP) {
            GlStateManager.pushLightingAttributes();
            Lighting.turnOn();
        }
        GlStateManager.translatef(0.0f, 0.0f, 0.4375f);
        this.drawItem(atn);
        if (atn.getItem().getItem() == Items.FILLED_MAP) {
            Lighting.turnOff();
            GlStateManager.popAttributes();
        }
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        this.renderName(atn, double2 + atn.getDirection().getStepX() * 0.3f, double3 - 0.25, double4 + atn.getDirection().getStepZ() * 0.3f);
    }
    
    @Nullable
    @Override
    protected ResourceLocation getTextureLocation(final ItemFrame atn) {
        return null;
    }
    
    private void drawItem(final ItemFrame atn) {
        final ItemStack bcj3 = atn.getItem();
        if (bcj3.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        final boolean boolean4 = bcj3.getItem() == Items.FILLED_MAP;
        final int integer5 = boolean4 ? (atn.getRotation() % 4 * 2) : atn.getRotation();
        GlStateManager.rotatef(integer5 * 360.0f / 8.0f, 0.0f, 0.0f, 1.0f);
        if (boolean4) {
            GlStateManager.disableLighting();
            this.entityRenderDispatcher.textureManager.bind(ItemFrameRenderer.MAP_BACKGROUND_LOCATION);
            GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
            final float float6 = 0.0078125f;
            GlStateManager.scalef(0.0078125f, 0.0078125f, 0.0078125f);
            GlStateManager.translatef(-64.0f, -64.0f, 0.0f);
            final MapItemSavedData coh7 = MapItem.getOrCreateSavedData(bcj3, atn.level);
            GlStateManager.translatef(0.0f, 0.0f, -1.0f);
            if (coh7 != null) {
                this.minecraft.gameRenderer.getMapRenderer().render(coh7, true);
            }
        }
        else {
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            this.itemRenderer.renderStatic(bcj3, ItemTransforms.TransformType.FIXED);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    protected void renderName(final ItemFrame atn, final double double2, final double double3, final double double4) {
        if (!Minecraft.renderNames() || atn.getItem().isEmpty() || !atn.getItem().hasCustomHoverName() || this.entityRenderDispatcher.crosshairPickEntity != atn) {
            return;
        }
        final double double5 = atn.distanceToSqr(this.entityRenderDispatcher.camera.getPosition());
        final float float11 = atn.isVisuallySneaking() ? 32.0f : 64.0f;
        if (double5 >= float11 * float11) {
            return;
        }
        final String string12 = atn.getItem().getHoverName().getColoredString();
        this.renderNameTag(atn, string12, double2, double3, double4, 64);
    }
    
    static {
        MAP_BACKGROUND_LOCATION = new ResourceLocation("textures/map/map_background.png");
        FRAME_LOCATION = new ModelResourceLocation("item_frame", "map=false");
        MAP_FRAME_LOCATION = new ModelResourceLocation("item_frame", "map=true");
    }
}
