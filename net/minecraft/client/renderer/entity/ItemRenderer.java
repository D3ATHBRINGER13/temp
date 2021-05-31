package net.minecraft.client.renderer.entity;

import com.google.common.collect.Sets;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.Font;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import java.util.List;
import net.minecraft.core.Vec3i;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.Util;
import net.minecraft.client.renderer.EntityBlockRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import java.util.Random;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.resources.model.BakedModel;
import java.util.Iterator;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.world.item.Item;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ItemRenderer implements ResourceManagerReloadListener {
    public static final ResourceLocation ENCHANT_GLINT_LOCATION;
    private static final Set<Item> IGNORED;
    public float blitOffset;
    private final ItemModelShaper itemModelShaper;
    private final TextureManager textureManager;
    private final ItemColors itemColors;
    
    public ItemRenderer(final TextureManager dxc, final ModelManager dyt, final ItemColors cys) {
        this.textureManager = dxc;
        this.itemModelShaper = new ItemModelShaper(dyt);
        for (final Item bce6 : Registry.ITEM) {
            if (!ItemRenderer.IGNORED.contains(bce6)) {
                this.itemModelShaper.register(bce6, new ModelResourceLocation(Registry.ITEM.getKey(bce6), "inventory"));
            }
        }
        this.itemColors = cys;
    }
    
    public ItemModelShaper getItemModelShaper() {
        return this.itemModelShaper;
    }
    
    private void renderModelLists(final BakedModel dyp, final ItemStack bcj) {
        this.renderModelLists(dyp, -1, bcj);
    }
    
    private void renderModelLists(final BakedModel dyp, final int integer) {
        this.renderModelLists(dyp, integer, ItemStack.EMPTY);
    }
    
    private void renderModelLists(final BakedModel dyp, final int integer, final ItemStack bcj) {
        final Tesselator cuz5 = Tesselator.getInstance();
        final BufferBuilder cuw6 = cuz5.getBuilder();
        cuw6.begin(7, DefaultVertexFormat.BLOCK_NORMALS);
        final Random random7 = new Random();
        final long long8 = 42L;
        for (final Direction fb13 : Direction.values()) {
            random7.setSeed(42L);
            this.renderQuadList(cuw6, dyp.getQuads(null, fb13, random7), integer, bcj);
        }
        random7.setSeed(42L);
        this.renderQuadList(cuw6, dyp.getQuads(null, null, random7), integer, bcj);
        cuz5.end();
    }
    
    public void render(final ItemStack bcj, final BakedModel dyp) {
        if (bcj.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        if (dyp.isCustomRenderer()) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableRescaleNormal();
            EntityBlockRenderer.instance.renderByItem(bcj);
        }
        else {
            this.renderModelLists(dyp, bcj);
            if (bcj.hasFoil()) {
                renderFoilLayer(this.textureManager, () -> this.renderModelLists(dyp, -8372020), 8);
            }
        }
        GlStateManager.popMatrix();
    }
    
    public static void renderFoilLayer(final TextureManager dxc, final Runnable runnable, final int integer) {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        dxc.bind(ItemRenderer.ENCHANT_GLINT_LOCATION);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scalef((float)integer, (float)integer, (float)integer);
        final float float4 = Util.getMillis() % 3000L / 3000.0f / integer;
        GlStateManager.translatef(float4, 0.0f, 0.0f);
        GlStateManager.rotatef(-50.0f, 0.0f, 0.0f, 1.0f);
        runnable.run();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scalef((float)integer, (float)integer, (float)integer);
        final float float5 = Util.getMillis() % 4873L / 4873.0f / integer;
        GlStateManager.translatef(-float5, 0.0f, 0.0f);
        GlStateManager.rotatef(10.0f, 0.0f, 0.0f, 1.0f);
        runnable.run();
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        dxc.bind(TextureAtlas.LOCATION_BLOCKS);
    }
    
    private void applyNormal(final BufferBuilder cuw, final BakedQuad dnz) {
        final Vec3i fs4 = dnz.getDirection().getNormal();
        cuw.postNormal((float)fs4.getX(), (float)fs4.getY(), (float)fs4.getZ());
    }
    
    private void putQuadData(final BufferBuilder cuw, final BakedQuad dnz, final int integer) {
        cuw.putBulkData(dnz.getVertices());
        cuw.fixupQuadColor(integer);
        this.applyNormal(cuw, dnz);
    }
    
    private void renderQuadList(final BufferBuilder cuw, final List<BakedQuad> list, final int integer, final ItemStack bcj) {
        final boolean boolean6 = integer == -1 && !bcj.isEmpty();
        for (int integer2 = 0, integer3 = list.size(); integer2 < integer3; ++integer2) {
            final BakedQuad dnz9 = (BakedQuad)list.get(integer2);
            int integer4 = integer;
            if (boolean6 && dnz9.isTinted()) {
                integer4 = this.itemColors.getColor(bcj, dnz9.getTintIndex());
                integer4 |= 0xFF000000;
            }
            this.putQuadData(cuw, dnz9, integer4);
        }
    }
    
    public boolean isGui3d(final ItemStack bcj) {
        final BakedModel dyp3 = this.itemModelShaper.getItemModel(bcj);
        return dyp3 != null && dyp3.isGui3d();
    }
    
    public void renderStatic(final ItemStack bcj, final ItemTransforms.TransformType b) {
        if (bcj.isEmpty()) {
            return;
        }
        final BakedModel dyp4 = this.getModel(bcj);
        this.renderStatic(bcj, dyp4, b, false);
    }
    
    public BakedModel getModel(final ItemStack bcj, @Nullable final Level bhr, @Nullable final LivingEntity aix) {
        final BakedModel dyp5 = this.itemModelShaper.getItemModel(bcj);
        final Item bce6 = bcj.getItem();
        if (!bce6.hasProperties()) {
            return dyp5;
        }
        return this.resolveOverrides(dyp5, bcj, bhr, aix);
    }
    
    public BakedModel getInHandModel(final ItemStack bcj, final Level bhr, final LivingEntity aix) {
        final Item bce6 = bcj.getItem();
        BakedModel dyp5;
        if (bce6 == Items.TRIDENT) {
            dyp5 = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
        }
        else {
            dyp5 = this.itemModelShaper.getItemModel(bcj);
        }
        if (!bce6.hasProperties()) {
            return dyp5;
        }
        return this.resolveOverrides(dyp5, bcj, bhr, aix);
    }
    
    public BakedModel getModel(final ItemStack bcj) {
        return this.getModel(bcj, null, null);
    }
    
    private BakedModel resolveOverrides(final BakedModel dyp, final ItemStack bcj, @Nullable final Level bhr, @Nullable final LivingEntity aix) {
        final BakedModel dyp2 = dyp.getOverrides().resolve(dyp, bcj, bhr, aix);
        return (dyp2 == null) ? this.itemModelShaper.getModelManager().getMissingModel() : dyp2;
    }
    
    public void renderWithMobState(final ItemStack bcj, final LivingEntity aix, final ItemTransforms.TransformType b, final boolean boolean4) {
        if (bcj.isEmpty() || aix == null) {
            return;
        }
        final BakedModel dyp6 = this.getInHandModel(bcj, aix.level, aix);
        this.renderStatic(bcj, dyp6, b, boolean4);
    }
    
    protected void renderStatic(final ItemStack bcj, final BakedModel dyp, final ItemTransforms.TransformType b, final boolean boolean4) {
        if (bcj.isEmpty()) {
            return;
        }
        this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
        this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).pushFilter(false, false);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        final ItemTransforms dom6 = dyp.getTransforms();
        ItemTransforms.apply(dom6.getTransform(b), boolean4);
        if (this.needsFlip(dom6.getTransform(b))) {
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        }
        this.render(bcj, dyp);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
        this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).popFilter();
    }
    
    private boolean needsFlip(final ItemTransform dol) {
        return dol.scale.x() < 0.0f ^ dol.scale.y() < 0.0f ^ dol.scale.z() < 0.0f;
    }
    
    public void renderGuiItem(final ItemStack bcj, final int integer2, final int integer3) {
        this.renderGuiItem(bcj, integer2, integer3, this.getModel(bcj));
    }
    
    protected void renderGuiItem(final ItemStack bcj, final int integer2, final int integer3, final BakedModel dyp) {
        GlStateManager.pushMatrix();
        this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
        this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).pushFilter(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.setupGuiItem(integer2, integer3, dyp.isGui3d());
        dyp.getTransforms().apply(ItemTransforms.TransformType.GUI);
        this.render(bcj, dyp);
        GlStateManager.disableAlphaTest();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
        this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).popFilter();
    }
    
    private void setupGuiItem(final int integer1, final int integer2, final boolean boolean3) {
        GlStateManager.translatef((float)integer1, (float)integer2, 100.0f + this.blitOffset);
        GlStateManager.translatef(8.0f, 8.0f, 0.0f);
        GlStateManager.scalef(1.0f, -1.0f, 1.0f);
        GlStateManager.scalef(16.0f, 16.0f, 16.0f);
        if (boolean3) {
            GlStateManager.enableLighting();
        }
        else {
            GlStateManager.disableLighting();
        }
    }
    
    public void renderAndDecorateItem(final ItemStack bcj, final int integer2, final int integer3) {
        this.renderAndDecorateItem(Minecraft.getInstance().player, bcj, integer2, integer3);
    }
    
    public void renderAndDecorateItem(@Nullable final LivingEntity aix, final ItemStack bcj, final int integer3, final int integer4) {
        if (bcj.isEmpty()) {
            return;
        }
        this.blitOffset += 50.0f;
        try {
            this.renderGuiItem(bcj, integer3, integer4, this.getModel(bcj, null, aix));
        }
        catch (Throwable throwable6) {
            final CrashReport d7 = CrashReport.forThrowable(throwable6, "Rendering item");
            final CrashReportCategory e8 = d7.addCategory("Item being rendered");
            e8.setDetail("Item Type", (CrashReportDetail<String>)(() -> String.valueOf(bcj.getItem())));
            e8.setDetail("Item Damage", (CrashReportDetail<String>)(() -> String.valueOf(bcj.getDamageValue())));
            e8.setDetail("Item NBT", (CrashReportDetail<String>)(() -> String.valueOf(bcj.getTag())));
            e8.setDetail("Item Foil", (CrashReportDetail<String>)(() -> String.valueOf(bcj.hasFoil())));
            throw new ReportedException(d7);
        }
        this.blitOffset -= 50.0f;
    }
    
    public void renderGuiItemDecorations(final Font cyu, final ItemStack bcj, final int integer3, final int integer4) {
        this.renderGuiItemDecorations(cyu, bcj, integer3, integer4, null);
    }
    
    public void renderGuiItemDecorations(final Font cyu, final ItemStack bcj, final int integer3, final int integer4, @Nullable final String string) {
        if (bcj.isEmpty()) {
            return;
        }
        if (bcj.getCount() != 1 || string != null) {
            final String string2 = (string == null) ? String.valueOf(bcj.getCount()) : string;
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableBlend();
            cyu.drawShadow(string2, (float)(integer3 + 19 - 2 - cyu.width(string2)), (float)(integer4 + 6 + 3), 16777215);
            GlStateManager.enableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
        }
        if (bcj.isDamaged()) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            final Tesselator cuz7 = Tesselator.getInstance();
            final BufferBuilder cuw8 = cuz7.getBuilder();
            final float float9 = (float)bcj.getDamageValue();
            final float float10 = (float)bcj.getMaxDamage();
            final float float11 = Math.max(0.0f, (float10 - float9) / float10);
            final int integer5 = Math.round(13.0f - float9 * 13.0f / float10);
            final int integer6 = Mth.hsvToRgb(float11 / 3.0f, 1.0f, 1.0f);
            this.fillRect(cuw8, integer3 + 2, integer4 + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect(cuw8, integer3 + 2, integer4 + 13, integer5, 1, integer6 >> 16 & 0xFF, integer6 >> 8 & 0xFF, integer6 & 0xFF, 255);
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableTexture();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
        }
        final LocalPlayer dmp7 = Minecraft.getInstance().player;
        final float float12 = (dmp7 == null) ? 0.0f : dmp7.getCooldowns().getCooldownPercent(bcj.getItem(), Minecraft.getInstance().getFrameTime());
        if (float12 > 0.0f) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            final Tesselator cuz8 = Tesselator.getInstance();
            final BufferBuilder cuw9 = cuz8.getBuilder();
            this.fillRect(cuw9, integer3, integer4 + Mth.floor(16.0f * (1.0f - float12)), 16, Mth.ceil(16.0f * float12), 255, 255, 255, 127);
            GlStateManager.enableTexture();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
        }
    }
    
    private void fillRect(final BufferBuilder cuw, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9) {
        cuw.begin(7, DefaultVertexFormat.POSITION_COLOR);
        cuw.vertex(integer2 + 0, integer3 + 0, 0.0).color(integer6, integer7, integer8, integer9).endVertex();
        cuw.vertex(integer2 + 0, integer3 + integer5, 0.0).color(integer6, integer7, integer8, integer9).endVertex();
        cuw.vertex(integer2 + integer4, integer3 + integer5, 0.0).color(integer6, integer7, integer8, integer9).endVertex();
        cuw.vertex(integer2 + integer4, integer3 + 0, 0.0).color(integer6, integer7, integer8, integer9).endVertex();
        Tesselator.getInstance().end();
    }
    
    public void onResourceManagerReload(final ResourceManager xi) {
        this.itemModelShaper.rebuildCache();
    }
    
    static {
        ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
        IGNORED = (Set)Sets.newHashSet((Object[])new Item[] { Items.AIR });
    }
}
