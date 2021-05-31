package com.mojang.blaze3d.font;

import com.mojang.blaze3d.platform.NativeImage;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.stb.STBTruetype;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import org.lwjgl.stb.STBTTFontinfo;
import org.apache.logging.log4j.Logger;

public class TrueTypeGlyphProvider implements GlyphProvider {
    private static final Logger LOGGER;
    private final STBTTFontinfo font;
    private final float oversample;
    private final CharSet skip;
    private final float shiftX;
    private final float shiftY;
    private final float pointScale;
    private final float ascent;
    
    public TrueTypeGlyphProvider(final STBTTFontinfo sTBTTFontinfo, final float float2, final float float3, final float float4, final float float5, final String string) {
        this.skip = (CharSet)new CharArraySet();
        this.font = sTBTTFontinfo;
        this.oversample = float3;
        string.chars().forEach(integer -> this.skip.add((char)(integer & 0xFFFF)));
        this.shiftX = float4 * float3;
        this.shiftY = float5 * float3;
        this.pointScale = STBTruetype.stbtt_ScaleForPixelHeight(sTBTTFontinfo, float2 * float3);
        try (final MemoryStack memoryStack8 = MemoryStack.stackPush()) {
            final IntBuffer intBuffer10 = memoryStack8.mallocInt(1);
            final IntBuffer intBuffer11 = memoryStack8.mallocInt(1);
            final IntBuffer intBuffer12 = memoryStack8.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics(sTBTTFontinfo, intBuffer10, intBuffer11, intBuffer12);
            this.ascent = intBuffer10.get(0) * this.pointScale;
        }
    }
    
    @Nullable
    public Glyph getGlyph(final char character) {
        if (this.skip.contains(character)) {
            return null;
        }
        try (final MemoryStack memoryStack3 = MemoryStack.stackPush()) {
            final IntBuffer intBuffer5 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer6 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer7 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer8 = memoryStack3.mallocInt(1);
            final int integer9 = STBTruetype.stbtt_FindGlyphIndex(this.font, (int)character);
            if (integer9 == 0) {
                return null;
            }
            STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(this.font, integer9, this.pointScale, this.pointScale, this.shiftX, this.shiftY, intBuffer5, intBuffer6, intBuffer7, intBuffer8);
            final int integer10 = intBuffer7.get(0) - intBuffer5.get(0);
            final int integer11 = intBuffer8.get(0) - intBuffer6.get(0);
            if (integer10 == 0 || integer11 == 0) {
                return null;
            }
            final IntBuffer intBuffer9 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer10 = memoryStack3.mallocInt(1);
            STBTruetype.stbtt_GetGlyphHMetrics(this.font, integer9, intBuffer9, intBuffer10);
            return new Glyph(intBuffer5.get(0), intBuffer7.get(0), -intBuffer6.get(0), -intBuffer8.get(0), intBuffer9.get(0) * this.pointScale, intBuffer10.get(0) * this.pointScale, integer9);
        }
    }
    
    public static STBTTFontinfo getStbttFontinfo(final ByteBuffer byteBuffer) throws IOException {
        final STBTTFontinfo sTBTTFontinfo2 = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(sTBTTFontinfo2, byteBuffer)) {
            throw new IOException("Invalid ttf");
        }
        return sTBTTFontinfo2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    class Glyph implements RawGlyph {
        private final int width;
        private final int height;
        private final float bearingX;
        private final float bearingY;
        private final float advance;
        private final int index;
        
        private Glyph(final int integer2, final int integer3, final int integer4, final int integer5, final float float6, final float float7, final int integer8) {
            this.width = integer3 - integer2;
            this.height = integer4 - integer5;
            this.advance = float6 / TrueTypeGlyphProvider.this.oversample;
            this.bearingX = (float7 + integer2 + TrueTypeGlyphProvider.this.shiftX) / TrueTypeGlyphProvider.this.oversample;
            this.bearingY = (TrueTypeGlyphProvider.this.ascent - integer4 + TrueTypeGlyphProvider.this.shiftY) / TrueTypeGlyphProvider.this.oversample;
            this.index = integer8;
        }
        
        public int getPixelWidth() {
            return this.width;
        }
        
        public int getPixelHeight() {
            return this.height;
        }
        
        public float getOversample() {
            return TrueTypeGlyphProvider.this.oversample;
        }
        
        public float getAdvance() {
            return this.advance;
        }
        
        public float getBearingX() {
            return this.bearingX;
        }
        
        public float getBearingY() {
            return this.bearingY;
        }
        
        public void upload(final int integer1, final int integer2) {
            try (final NativeImage cuj4 = new NativeImage(NativeImage.Format.LUMINANCE, this.width, this.height, false)) {
                cuj4.copyFromFont(TrueTypeGlyphProvider.this.font, this.index, this.width, this.height, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.shiftX, TrueTypeGlyphProvider.this.shiftY, 0, 0);
                cuj4.upload(0, integer1, integer2, 0, 0, this.width, this.height, false);
            }
        }
        
        public boolean isColored() {
            return false;
        }
    }
}
