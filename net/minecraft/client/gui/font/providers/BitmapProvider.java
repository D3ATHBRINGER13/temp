package net.minecraft.client.gui.font.providers;

import net.minecraft.server.packs.resources.Resource;
import java.io.IOException;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.server.packs.resources.ResourceManager;
import com.google.gson.JsonArray;
import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import com.mojang.blaze3d.font.RawGlyph;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import com.mojang.blaze3d.platform.NativeImage;
import org.apache.logging.log4j.Logger;
import com.mojang.blaze3d.font.GlyphProvider;

public class BitmapProvider implements GlyphProvider {
    private static final Logger LOGGER;
    private final NativeImage image;
    private final Char2ObjectMap<Glyph> glyphs;
    
    public BitmapProvider(final NativeImage cuj, final Char2ObjectMap<Glyph> char2ObjectMap) {
        this.image = cuj;
        this.glyphs = char2ObjectMap;
    }
    
    public void close() {
        this.image.close();
    }
    
    @Nullable
    public RawGlyph getGlyph(final char character) {
        return (RawGlyph)this.glyphs.get(character);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Builder implements GlyphProviderBuilder {
        private final ResourceLocation texture;
        private final List<String> chars;
        private final int height;
        private final int ascent;
        
        public Builder(final ResourceLocation qv, final int integer2, final int integer3, final List<String> list) {
            this.texture = new ResourceLocation(qv.getNamespace(), "textures/" + qv.getPath());
            this.chars = list;
            this.height = integer2;
            this.ascent = integer3;
        }
        
        public static Builder fromJson(final JsonObject jsonObject) {
            final int integer2 = GsonHelper.getAsInt(jsonObject, "height", 8);
            final int integer3 = GsonHelper.getAsInt(jsonObject, "ascent");
            if (integer3 > integer2) {
                throw new JsonParseException(new StringBuilder().append("Ascent ").append(integer3).append(" higher than height ").append(integer2).toString());
            }
            final List<String> list4 = (List<String>)Lists.newArrayList();
            final JsonArray jsonArray5 = GsonHelper.getAsJsonArray(jsonObject, "chars");
            for (int integer4 = 0; integer4 < jsonArray5.size(); ++integer4) {
                final String string7 = GsonHelper.convertToString(jsonArray5.get(integer4), new StringBuilder().append("chars[").append(integer4).append("]").toString());
                if (integer4 > 0) {
                    final int integer5 = string7.length();
                    final int integer6 = ((String)list4.get(0)).length();
                    if (integer5 != integer6) {
                        throw new JsonParseException(new StringBuilder().append("Elements of chars have to be the same length (found: ").append(integer5).append(", expected: ").append(integer6).append("), pad with space or \\u0000").toString());
                    }
                }
                list4.add(string7);
            }
            if (list4.isEmpty() || ((String)list4.get(0)).isEmpty()) {
                throw new JsonParseException("Expected to find data in chars, found none.");
            }
            return new Builder(new ResourceLocation(GsonHelper.getAsString(jsonObject, "file")), integer2, integer3, list4);
        }
        
        @Nullable
        public GlyphProvider create(final ResourceManager xi) {
            try (final Resource xh3 = xi.getResource(this.texture)) {
                final NativeImage cuj5 = NativeImage.read(NativeImage.Format.RGBA, xh3.getInputStream());
                final int integer6 = cuj5.getWidth();
                final int integer7 = cuj5.getHeight();
                final int integer8 = integer6 / ((String)this.chars.get(0)).length();
                final int integer9 = integer7 / this.chars.size();
                final float float10 = this.height / (float)integer9;
                final Char2ObjectMap<Glyph> char2ObjectMap11 = (Char2ObjectMap<Glyph>)new Char2ObjectOpenHashMap();
                for (int integer10 = 0; integer10 < this.chars.size(); ++integer10) {
                    final String string13 = (String)this.chars.get(integer10);
                    for (int integer11 = 0; integer11 < string13.length(); ++integer11) {
                        final char character15 = string13.charAt(integer11);
                        if (character15 != '\0') {
                            if (character15 != ' ') {
                                final int integer12 = this.getActualGlyphWidth(cuj5, integer8, integer9, integer11, integer10);
                                final Glyph b17 = (Glyph)char2ObjectMap11.put(character15, new Glyph(float10, cuj5, integer11 * integer8, integer10 * integer9, integer8, integer9, (int)(0.5 + integer12 * float10) + 1, this.ascent));
                                if (b17 != null) {
                                    BitmapProvider.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString((int)character15), this.texture);
                                }
                            }
                        }
                    }
                }
                return new BitmapProvider(cuj5, char2ObjectMap11);
            }
            catch (IOException iOException3) {
                throw new RuntimeException(iOException3.getMessage());
            }
        }
        
        private int getActualGlyphWidth(final NativeImage cuj, final int integer2, final int integer3, final int integer4, final int integer5) {
            int integer6;
            for (integer6 = integer2 - 1; integer6 >= 0; --integer6) {
                final int integer7 = integer4 * integer2 + integer6;
                for (int integer8 = 0; integer8 < integer3; ++integer8) {
                    final int integer9 = integer5 * integer3 + integer8;
                    if (cuj.getLuminanceOrAlpha(integer7, integer9) != 0) {
                        return integer6 + 1;
                    }
                }
            }
            return integer6 + 1;
        }
    }
    
    static final class Glyph implements RawGlyph {
        private final float scale;
        private final NativeImage image;
        private final int offsetX;
        private final int offsetY;
        private final int width;
        private final int height;
        private final int advance;
        private final int ascent;
        
        private Glyph(final float float1, final NativeImage cuj, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
            this.scale = float1;
            this.image = cuj;
            this.offsetX = integer3;
            this.offsetY = integer4;
            this.width = integer5;
            this.height = integer6;
            this.advance = integer7;
            this.ascent = integer8;
        }
        
        public float getOversample() {
            return 1.0f / this.scale;
        }
        
        public int getPixelWidth() {
            return this.width;
        }
        
        public int getPixelHeight() {
            return this.height;
        }
        
        public float getAdvance() {
            return (float)this.advance;
        }
        
        public float getBearingY() {
            return super.getBearingY() + 7.0f - this.ascent;
        }
        
        public void upload(final int integer1, final int integer2) {
            this.image.upload(0, integer1, integer2, this.offsetX, this.offsetY, this.width, this.height, false);
        }
        
        public boolean isColored() {
            return this.image.format().components() > 1;
        }
    }
}
