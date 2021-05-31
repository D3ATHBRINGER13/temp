package net.minecraft.client.gui;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.util.Mth;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.client.renderer.texture.DynamicTexture;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class MapRenderer implements AutoCloseable {
    private static final ResourceLocation MAP_ICONS_LOCATION;
    private final TextureManager textureManager;
    private final Map<String, MapInstance> maps;
    
    public MapRenderer(final TextureManager dxc) {
        this.maps = (Map<String, MapInstance>)Maps.newHashMap();
        this.textureManager = dxc;
    }
    
    public void update(final MapItemSavedData coh) {
        this.getMapInstance(coh).updateTexture();
    }
    
    public void render(final MapItemSavedData coh, final boolean boolean2) {
        this.getMapInstance(coh).draw(boolean2);
    }
    
    private MapInstance getMapInstance(final MapItemSavedData coh) {
        MapInstance a3 = (MapInstance)this.maps.get(coh.getId());
        if (a3 == null) {
            a3 = new MapInstance(coh);
            this.maps.put(coh.getId(), a3);
        }
        return a3;
    }
    
    @Nullable
    public MapInstance getMapInstanceIfExists(final String string) {
        return (MapInstance)this.maps.get(string);
    }
    
    public void resetData() {
        for (final MapInstance a3 : this.maps.values()) {
            a3.close();
        }
        this.maps.clear();
    }
    
    @Nullable
    public MapItemSavedData getData(@Nullable final MapInstance a) {
        if (a != null) {
            return a.data;
        }
        return null;
    }
    
    public void close() {
        this.resetData();
    }
    
    static {
        MAP_ICONS_LOCATION = new ResourceLocation("textures/map/map_icons.png");
    }
    
    class MapInstance implements AutoCloseable {
        private final MapItemSavedData data;
        private final DynamicTexture texture;
        private final ResourceLocation location;
        
        private MapInstance(final MapItemSavedData coh) {
            this.data = coh;
            this.texture = new DynamicTexture(128, 128, true);
            this.location = MapRenderer.this.textureManager.register("map/" + coh.getId(), this.texture);
        }
        
        private void updateTexture() {
            for (int integer2 = 0; integer2 < 128; ++integer2) {
                for (int integer3 = 0; integer3 < 128; ++integer3) {
                    final int integer4 = integer3 + integer2 * 128;
                    final int integer5 = this.data.colors[integer4] & 0xFF;
                    if (integer5 / 4 == 0) {
                        this.texture.getPixels().setPixelRGBA(integer3, integer2, (integer4 + integer4 / 128 & 0x1) * 8 + 16 << 24);
                    }
                    else {
                        this.texture.getPixels().setPixelRGBA(integer3, integer2, MaterialColor.MATERIAL_COLORS[integer5 / 4].calculateRGBColor(integer5 & 0x3));
                    }
                }
            }
            this.texture.upload();
        }
        
        private void draw(final boolean boolean1) {
            final int integer3 = 0;
            final int integer4 = 0;
            final Tesselator cuz5 = Tesselator.getInstance();
            final BufferBuilder cuw6 = cuz5.getBuilder();
            final float float7 = 0.0f;
            MapRenderer.this.textureManager.bind(this.location);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            GlStateManager.disableAlphaTest();
            cuw6.begin(7, DefaultVertexFormat.POSITION_TEX);
            cuw6.vertex(0.0, 128.0, -0.009999999776482582).uv(0.0, 1.0).endVertex();
            cuw6.vertex(128.0, 128.0, -0.009999999776482582).uv(1.0, 1.0).endVertex();
            cuw6.vertex(128.0, 0.0, -0.009999999776482582).uv(1.0, 0.0).endVertex();
            cuw6.vertex(0.0, 0.0, -0.009999999776482582).uv(0.0, 0.0).endVertex();
            cuz5.end();
            GlStateManager.enableAlphaTest();
            GlStateManager.disableBlend();
            int integer5 = 0;
            for (final MapDecoration coe10 : this.data.decorations.values()) {
                if (boolean1 && !coe10.renderOnFrame()) {
                    continue;
                }
                MapRenderer.this.textureManager.bind(MapRenderer.MAP_ICONS_LOCATION);
                GlStateManager.pushMatrix();
                GlStateManager.translatef(0.0f + coe10.getX() / 2.0f + 64.0f, 0.0f + coe10.getY() / 2.0f + 64.0f, -0.02f);
                GlStateManager.rotatef(coe10.getRot() * 360 / 16.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.scalef(4.0f, 4.0f, 3.0f);
                GlStateManager.translatef(-0.125f, 0.125f, 0.0f);
                final byte byte11 = coe10.getImage();
                final float float8 = (byte11 % 16 + 0) / 16.0f;
                final float float9 = (byte11 / 16 + 0) / 16.0f;
                final float float10 = (byte11 % 16 + 1) / 16.0f;
                final float float11 = (byte11 / 16 + 1) / 16.0f;
                cuw6.begin(7, DefaultVertexFormat.POSITION_TEX);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                final float float12 = -0.001f;
                cuw6.vertex(-1.0, 1.0, integer5 * -0.001f).uv(float8, float9).endVertex();
                cuw6.vertex(1.0, 1.0, integer5 * -0.001f).uv(float10, float9).endVertex();
                cuw6.vertex(1.0, -1.0, integer5 * -0.001f).uv(float10, float11).endVertex();
                cuw6.vertex(-1.0, -1.0, integer5 * -0.001f).uv(float8, float11).endVertex();
                cuz5.end();
                GlStateManager.popMatrix();
                if (coe10.getName() != null) {
                    final Font cyu17 = Minecraft.getInstance().font;
                    final String string18 = coe10.getName().getColoredString();
                    final float float13 = (float)cyu17.width(string18);
                    final float float15 = 25.0f / float13;
                    final float float16 = 0.0f;
                    final float n = 6.0f;
                    cyu17.getClass();
                    final float float14 = Mth.clamp(float15, float16, n / 9.0f);
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef(0.0f + coe10.getX() / 2.0f + 64.0f - float13 * float14 / 2.0f, 0.0f + coe10.getY() / 2.0f + 64.0f + 4.0f, -0.025f);
                    GlStateManager.scalef(float14, float14, 1.0f);
                    final int integer6 = -1;
                    final int integer7 = -1;
                    final int integer8 = (int)float13;
                    cyu17.getClass();
                    GuiComponent.fill(integer6, integer7, integer8, 9 - 1, Integer.MIN_VALUE);
                    GlStateManager.translatef(0.0f, 0.0f, -0.1f);
                    cyu17.draw(string18, 0.0f, 0.0f, -1);
                    GlStateManager.popMatrix();
                }
                ++integer5;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 0.0f, -0.04f);
            GlStateManager.scalef(1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
        
        public void close() {
            this.texture.close();
        }
    }
}
