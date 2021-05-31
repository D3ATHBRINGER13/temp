package net.minecraft.client.gui.font.providers;

import net.minecraft.client.Minecraft;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import com.mojang.blaze3d.font.RawGlyph;
import net.minecraft.server.packs.resources.Resource;
import java.util.Arrays;
import java.io.IOException;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.Logger;
import com.mojang.blaze3d.font.GlyphProvider;

public class LegacyUnicodeBitmapsProvider implements GlyphProvider {
    private static final Logger LOGGER;
    private final ResourceManager resourceManager;
    private final byte[] sizes;
    private final String texturePattern;
    private final Map<ResourceLocation, NativeImage> textures;
    
    public LegacyUnicodeBitmapsProvider(final ResourceManager xi, final byte[] arr, final String string) {
        this.textures = (Map<ResourceLocation, NativeImage>)Maps.newHashMap();
        this.resourceManager = xi;
        this.sizes = arr;
        this.texturePattern = string;
        for (int integer5 = 0; integer5 < 256; ++integer5) {
            final char character6 = (char)(integer5 * 256);
            final ResourceLocation qv7 = this.getSheetLocation(character6);
            try (final Resource xh8 = this.resourceManager.getResource(qv7);
                 final NativeImage cuj10 = NativeImage.read(NativeImage.Format.RGBA, xh8.getInputStream())) {
                if (cuj10.getWidth() == 256 && cuj10.getHeight() == 256) {
                    for (int integer6 = 0; integer6 < 256; ++integer6) {
                        final byte byte13 = arr[character6 + integer6];
                        if (byte13 != 0 && getLeft(byte13) > getRight(byte13)) {
                            arr[character6 + integer6] = 0;
                        }
                    }
                    continue;
                }
            }
            catch (IOException ex) {}
            Arrays.fill(arr, (int)character6, character6 + '\u0100', (byte)0);
        }
    }
    
    public void close() {
        this.textures.values().forEach(NativeImage::close);
    }
    
    private ResourceLocation getSheetLocation(final char character) {
        final ResourceLocation qv3 = new ResourceLocation(String.format(this.texturePattern, new Object[] { String.format("%02x", new Object[] { character / '\u0100' }) }));
        return new ResourceLocation(qv3.getNamespace(), "textures/" + qv3.getPath());
    }
    
    @Nullable
    public RawGlyph getGlyph(final char character) {
        final byte byte3 = this.sizes[character];
        if (byte3 != 0) {
            final NativeImage cuj4 = (NativeImage)this.textures.computeIfAbsent(this.getSheetLocation(character), this::loadTexture);
            if (cuj4 != null) {
                final int integer5 = getLeft(byte3);
                return new Glyph(character % '\u0010' * 16 + integer5, (character & '\u00ff') / 16 * 16, getRight(byte3) - integer5, 16, cuj4);
            }
        }
        return null;
    }
    
    @Nullable
    private NativeImage loadTexture(final ResourceLocation qv) {
        try (final Resource xh3 = this.resourceManager.getResource(qv)) {
            return NativeImage.read(NativeImage.Format.RGBA, xh3.getInputStream());
        }
        catch (IOException iOException3) {
            LegacyUnicodeBitmapsProvider.LOGGER.error("Couldn't load texture {}", qv, iOException3);
            return null;
        }
    }
    
    private static int getLeft(final byte byte1) {
        return byte1 >> 4 & 0xF;
    }
    
    private static int getRight(final byte byte1) {
        return (byte1 & 0xF) + 1;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Builder implements GlyphProviderBuilder {
        private final ResourceLocation metadata;
        private final String texturePattern;
        
        public Builder(final ResourceLocation qv, final String string) {
            this.metadata = qv;
            this.texturePattern = string;
        }
        
        public static GlyphProviderBuilder fromJson(final JsonObject jsonObject) {
            return new Builder(new ResourceLocation(GsonHelper.getAsString(jsonObject, "sizes")), GsonHelper.getAsString(jsonObject, "template"));
        }
        
        @Nullable
        public GlyphProvider create(final ResourceManager xi) {
            try (final Resource xh3 = Minecraft.getInstance().getResourceManager().getResource(this.metadata)) {
                final byte[] arr5 = new byte[65536];
                xh3.getInputStream().read(arr5);
                return new LegacyUnicodeBitmapsProvider(xi, arr5, this.texturePattern);
            }
            catch (IOException iOException3) {
                LegacyUnicodeBitmapsProvider.LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", this.metadata);
                return null;
            }
        }
    }
    
    static class Glyph implements RawGlyph {
        private final int width;
        private final int height;
        private final int sourceX;
        private final int sourceY;
        private final NativeImage source;
        
        private Glyph(final int integer1, final int integer2, final int integer3, final int integer4, final NativeImage cuj) {
            this.width = integer3;
            this.height = integer4;
            this.sourceX = integer1;
            this.sourceY = integer2;
            this.source = cuj;
        }
        
        public float getOversample() {
            return 2.0f;
        }
        
        public int getPixelWidth() {
            return this.width;
        }
        
        public int getPixelHeight() {
            return this.height;
        }
        
        public float getAdvance() {
            return (float)(this.width / 2 + 1);
        }
        
        public void upload(final int integer1, final int integer2) {
            this.source.upload(0, integer1, integer2, this.sourceX, this.sourceY, this.width, this.height, false);
        }
        
        public boolean isColored() {
            return this.source.format().components() > 1;
        }
        
        public float getShadowOffset() {
            return 0.5f;
        }
        
        public float getBoldOffset() {
            return 0.5f;
        }
    }
}
