package com.mojang.blaze3d.platform;

import org.lwjgl.stb.STBIWriteCallback;
import java.util.EnumSet;
import java.util.Base64;
import org.lwjgl.stb.STBImageResize;
import java.nio.channels.WritableByteChannel;
import org.lwjgl.stb.STBIWriteCallbackI;
import org.lwjgl.stb.STBImageWrite;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Path;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.stb.STBTTFontinfo;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.IntBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import java.nio.ByteBuffer;
import org.apache.commons.io.IOUtils;
import java.nio.Buffer;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import org.lwjgl.system.MemoryUtil;
import java.nio.file.StandardOpenOption;
import java.util.Set;

public final class NativeImage implements AutoCloseable {
    private static final Set<StandardOpenOption> OPEN_OPTIONS;
    private final Format format;
    private final int width;
    private final int height;
    private final boolean useStbFree;
    private long pixels;
    private final int size;
    
    public NativeImage(final int integer1, final int integer2, final boolean boolean3) {
        this(Format.RGBA, integer1, integer2, boolean3);
    }
    
    public NativeImage(final Format a, final int integer2, final int integer3, final boolean boolean4) {
        this.format = a;
        this.width = integer2;
        this.height = integer3;
        this.size = integer2 * integer3 * a.components();
        this.useStbFree = false;
        if (boolean4) {
            this.pixels = MemoryUtil.nmemCalloc(1L, (long)this.size);
        }
        else {
            this.pixels = MemoryUtil.nmemAlloc((long)this.size);
        }
    }
    
    private NativeImage(final Format a, final int integer2, final int integer3, final boolean boolean4, final long long5) {
        this.format = a;
        this.width = integer2;
        this.height = integer3;
        this.useStbFree = boolean4;
        this.pixels = long5;
        this.size = integer2 * integer3 * a.components();
    }
    
    public String toString() {
        return new StringBuilder().append("NativeImage[").append(this.format).append(" ").append(this.width).append("x").append(this.height).append("@").append(this.pixels).append(this.useStbFree ? "S" : "N").append("]").toString();
    }
    
    public static NativeImage read(final InputStream inputStream) throws IOException {
        return read(Format.RGBA, inputStream);
    }
    
    public static NativeImage read(@Nullable final Format a, final InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer3 = null;
        try {
            byteBuffer3 = TextureUtil.readResource(inputStream);
            byteBuffer3.rewind();
            return read(a, byteBuffer3);
        }
        finally {
            MemoryUtil.memFree((Buffer)byteBuffer3);
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    public static NativeImage read(final ByteBuffer byteBuffer) throws IOException {
        return read(Format.RGBA, byteBuffer);
    }
    
    public static NativeImage read(@Nullable final Format a, final ByteBuffer byteBuffer) throws IOException {
        if (a != null && !a.supportedByStb()) {
            throw new UnsupportedOperationException(new StringBuilder().append("Don't know how to read format ").append(a).toString());
        }
        if (MemoryUtil.memAddress(byteBuffer) == 0L) {
            throw new IllegalArgumentException("Invalid buffer");
        }
        try (final MemoryStack memoryStack3 = MemoryStack.stackPush()) {
            final IntBuffer intBuffer5 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer6 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer7 = memoryStack3.mallocInt(1);
            final ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer5, intBuffer6, intBuffer7, (a == null) ? 0 : a.components);
            if (byteBuffer2 == null) {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }
            return new NativeImage((a == null) ? getStbFormat(intBuffer7.get(0)) : a, intBuffer5.get(0), intBuffer6.get(0), true, MemoryUtil.memAddress(byteBuffer2));
        }
    }
    
    private static void setClamp(final boolean boolean1) {
        if (boolean1) {
            GlStateManager.texParameter(3553, 10242, 10496);
            GlStateManager.texParameter(3553, 10243, 10496);
        }
        else {
            GlStateManager.texParameter(3553, 10242, 10497);
            GlStateManager.texParameter(3553, 10243, 10497);
        }
    }
    
    private static void setFilter(final boolean boolean1, final boolean boolean2) {
        if (boolean1) {
            GlStateManager.texParameter(3553, 10241, boolean2 ? 9987 : 9729);
            GlStateManager.texParameter(3553, 10240, 9729);
        }
        else {
            GlStateManager.texParameter(3553, 10241, boolean2 ? 9986 : 9728);
            GlStateManager.texParameter(3553, 10240, 9728);
        }
    }
    
    private void checkAllocated() {
        if (this.pixels == 0L) {
            throw new IllegalStateException("Image is not allocated.");
        }
    }
    
    public void close() {
        if (this.pixels != 0L) {
            if (this.useStbFree) {
                STBImage.nstbi_image_free(this.pixels);
            }
            else {
                MemoryUtil.nmemFree(this.pixels);
            }
        }
        this.pixels = 0L;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public Format format() {
        return this.format;
    }
    
    public int getPixelRGBA(final int integer1, final int integer2) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[] { this.format }));
        }
        if (integer1 > this.width || integer2 > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[] { integer1, integer2, this.width, this.height }));
        }
        this.checkAllocated();
        return MemoryUtil.memIntBuffer(this.pixels, this.size).get(integer1 + integer2 * this.width);
    }
    
    public void setPixelRGBA(final int integer1, final int integer2, final int integer3) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[] { this.format }));
        }
        if (integer1 > this.width || integer2 > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[] { integer1, integer2, this.width, this.height }));
        }
        this.checkAllocated();
        MemoryUtil.memIntBuffer(this.pixels, this.size).put(integer1 + integer2 * this.width, integer3);
    }
    
    public byte getLuminanceOrAlpha(final int integer1, final int integer2) {
        if (!this.format.hasLuminanceOrAlpha()) {
            throw new IllegalArgumentException(String.format("no luminance or alpha in %s", new Object[] { this.format }));
        }
        if (integer1 > this.width || integer2 > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", new Object[] { integer1, integer2, this.width, this.height }));
        }
        return MemoryUtil.memByteBuffer(this.pixels, this.size).get((integer1 + integer2 * this.width) * this.format.components() + this.format.luminanceOrAlphaOffset() / 8);
    }
    
    public void blendPixel(final int integer1, final int integer2, final int integer3) {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
        }
        final int integer4 = this.getPixelRGBA(integer1, integer2);
        final float float6 = (integer3 >> 24 & 0xFF) / 255.0f;
        final float float7 = (integer3 >> 16 & 0xFF) / 255.0f;
        final float float8 = (integer3 >> 8 & 0xFF) / 255.0f;
        final float float9 = (integer3 >> 0 & 0xFF) / 255.0f;
        final float float10 = (integer4 >> 24 & 0xFF) / 255.0f;
        final float float11 = (integer4 >> 16 & 0xFF) / 255.0f;
        final float float12 = (integer4 >> 8 & 0xFF) / 255.0f;
        final float float13 = (integer4 >> 0 & 0xFF) / 255.0f;
        final float float14 = float6;
        final float float15 = 1.0f - float6;
        float float16 = float6 * float14 + float10 * float15;
        float float17 = float7 * float14 + float11 * float15;
        float float18 = float8 * float14 + float12 * float15;
        float float19 = float9 * float14 + float13 * float15;
        if (float16 > 1.0f) {
            float16 = 1.0f;
        }
        if (float17 > 1.0f) {
            float17 = 1.0f;
        }
        if (float18 > 1.0f) {
            float18 = 1.0f;
        }
        if (float19 > 1.0f) {
            float19 = 1.0f;
        }
        final int integer5 = (int)(float16 * 255.0f);
        final int integer6 = (int)(float17 * 255.0f);
        final int integer7 = (int)(float18 * 255.0f);
        final int integer8 = (int)(float19 * 255.0f);
        this.setPixelRGBA(integer1, integer2, integer5 << 24 | integer6 << 16 | integer7 << 8 | integer8 << 0);
    }
    
    @Deprecated
    public int[] makePixelArray() {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        }
        this.checkAllocated();
        final int[] arr2 = new int[this.getWidth() * this.getHeight()];
        for (int integer3 = 0; integer3 < this.getHeight(); ++integer3) {
            for (int integer4 = 0; integer4 < this.getWidth(); ++integer4) {
                final int integer5 = this.getPixelRGBA(integer4, integer3);
                final int integer6 = integer5 >> 24 & 0xFF;
                final int integer7 = integer5 >> 16 & 0xFF;
                final int integer8 = integer5 >> 8 & 0xFF;
                final int integer9 = integer5 >> 0 & 0xFF;
                final int integer10 = integer6 << 24 | integer9 << 16 | integer8 << 8 | integer7;
                arr2[integer4 + integer3 * this.getWidth()] = integer10;
            }
        }
        return arr2;
    }
    
    public void upload(final int integer1, final int integer2, final int integer3, final boolean boolean4) {
        this.upload(integer1, integer2, integer3, 0, 0, this.width, this.height, boolean4);
    }
    
    public void upload(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8) {
        this.upload(integer1, integer2, integer3, integer4, integer5, integer6, integer7, false, false, boolean8);
    }
    
    public void upload(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final boolean boolean9, final boolean boolean10) {
        this.checkAllocated();
        setFilter(boolean8, boolean10);
        setClamp(boolean9);
        if (integer6 == this.getWidth()) {
            GlStateManager.pixelStore(3314, 0);
        }
        else {
            GlStateManager.pixelStore(3314, this.getWidth());
        }
        GlStateManager.pixelStore(3316, integer4);
        GlStateManager.pixelStore(3315, integer5);
        this.format.setUnpackPixelStoreState();
        GlStateManager.texSubImage2D(3553, integer1, integer2, integer3, integer6, integer7, this.format.glFormat(), 5121, this.pixels);
    }
    
    public void downloadTexture(final int integer, final boolean boolean2) {
        this.checkAllocated();
        this.format.setPackPixelStoreState();
        GlStateManager.getTexImage(3553, integer, this.format.glFormat(), 5121, this.pixels);
        if (boolean2 && this.format.hasAlpha()) {
            for (int integer2 = 0; integer2 < this.getHeight(); ++integer2) {
                for (int integer3 = 0; integer3 < this.getWidth(); ++integer3) {
                    this.setPixelRGBA(integer3, integer2, this.getPixelRGBA(integer3, integer2) | 255 << this.format.alphaOffset());
                }
            }
        }
    }
    
    public void downloadFrameBuffer(final boolean boolean1) {
        this.checkAllocated();
        this.format.setPackPixelStoreState();
        if (boolean1) {
            GlStateManager.pixelTransfer(3357, Float.MAX_VALUE);
        }
        GlStateManager.readPixels(0, 0, this.width, this.height, this.format.glFormat(), 5121, this.pixels);
        if (boolean1) {
            GlStateManager.pixelTransfer(3357, 0.0f);
        }
    }
    
    public void writeToFile(final String string) throws IOException {
        this.writeToFile(FileSystems.getDefault().getPath(string, new String[0]));
    }
    
    public void writeToFile(final File file) throws IOException {
        this.writeToFile(file.toPath());
    }
    
    public void copyFromFont(final STBTTFontinfo sTBTTFontinfo, final int integer2, final int integer3, final int integer4, final float float5, final float float6, final float float7, final float float8, final int integer9, final int integer10) {
        if (integer9 < 0 || integer9 + integer3 > this.getWidth() || integer10 < 0 || integer10 + integer4 > this.getHeight()) {
            throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", new Object[] { integer9, integer10, integer3, integer4, this.getWidth(), this.getHeight() }));
        }
        if (this.format.components() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        }
        STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(sTBTTFontinfo.address(), this.pixels + integer9 + integer10 * this.getWidth(), integer3, integer4, this.getWidth(), float5, float6, float7, float8, integer2);
    }
    
    public void writeToFile(final Path path) throws IOException {
        if (!this.format.supportedByStb()) {
            throw new UnsupportedOperationException(new StringBuilder().append("Don't know how to write format ").append(this.format).toString());
        }
        this.checkAllocated();
        try (final WritableByteChannel writableByteChannel3 = (WritableByteChannel)Files.newByteChannel(path, (Set)NativeImage.OPEN_OPTIONS, new FileAttribute[0])) {
            final WriteCallback c5 = new WriteCallback(writableByteChannel3);
            try {
                if (!STBImageWrite.stbi_write_png_to_func((STBIWriteCallbackI)c5, 0L, this.getWidth(), this.getHeight(), this.format.components(), MemoryUtil.memByteBuffer(this.pixels, this.size), 0)) {
                    throw new IOException(new StringBuilder().append("Could not write image to the PNG file \"").append(path.toAbsolutePath()).append("\": ").append(STBImage.stbi_failure_reason()).toString());
                }
            }
            finally {
                c5.free();
            }
            c5.throwIfException();
        }
    }
    
    public void copyFrom(final NativeImage cuj) {
        if (cuj.format() != this.format) {
            throw new UnsupportedOperationException("Image formats don't match.");
        }
        final int integer3 = this.format.components();
        this.checkAllocated();
        cuj.checkAllocated();
        if (this.width == cuj.width) {
            MemoryUtil.memCopy(cuj.pixels, this.pixels, (long)Math.min(this.size, cuj.size));
        }
        else {
            final int integer4 = Math.min(this.getWidth(), cuj.getWidth());
            for (int integer5 = Math.min(this.getHeight(), cuj.getHeight()), integer6 = 0; integer6 < integer5; ++integer6) {
                final int integer7 = integer6 * cuj.getWidth() * integer3;
                final int integer8 = integer6 * this.getWidth() * integer3;
                MemoryUtil.memCopy(cuj.pixels + integer7, this.pixels + integer8, (long)integer4);
            }
        }
    }
    
    public void fillRect(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        for (int integer6 = integer2; integer6 < integer2 + integer4; ++integer6) {
            for (int integer7 = integer1; integer7 < integer1 + integer3; ++integer7) {
                this.setPixelRGBA(integer7, integer6, integer5);
            }
        }
    }
    
    public void copyRect(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final boolean boolean7, final boolean boolean8) {
        for (int integer7 = 0; integer7 < integer6; ++integer7) {
            for (int integer8 = 0; integer8 < integer5; ++integer8) {
                final int integer9 = boolean7 ? (integer5 - 1 - integer8) : integer8;
                final int integer10 = boolean8 ? (integer6 - 1 - integer7) : integer7;
                final int integer11 = this.getPixelRGBA(integer1 + integer8, integer2 + integer7);
                this.setPixelRGBA(integer1 + integer3 + integer9, integer2 + integer4 + integer10, integer11);
            }
        }
    }
    
    public void flipY() {
        this.checkAllocated();
        try (final MemoryStack memoryStack2 = MemoryStack.stackPush()) {
            final int integer4 = this.format.components();
            final int integer5 = this.getWidth() * integer4;
            final long long6 = memoryStack2.nmalloc(integer5);
            for (int integer6 = 0; integer6 < this.getHeight() / 2; ++integer6) {
                final int integer7 = integer6 * this.getWidth() * integer4;
                final int integer8 = (this.getHeight() - 1 - integer6) * this.getWidth() * integer4;
                MemoryUtil.memCopy(this.pixels + integer7, long6, (long)integer5);
                MemoryUtil.memCopy(this.pixels + integer8, this.pixels + integer7, (long)integer5);
                MemoryUtil.memCopy(long6, this.pixels + integer8, (long)integer5);
            }
        }
    }
    
    public void resizeSubRectTo(final int integer1, final int integer2, final int integer3, final int integer4, final NativeImage cuj) {
        this.checkAllocated();
        if (cuj.format() != this.format) {
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        }
        final int integer5 = this.format.components();
        STBImageResize.nstbir_resize_uint8(this.pixels + (integer1 + integer2 * this.getWidth()) * integer5, integer3, integer4, this.getWidth() * integer5, cuj.pixels, cuj.getWidth(), cuj.getHeight(), 0, integer5);
    }
    
    public void untrack() {
        DebugMemoryUntracker.untrack(this.pixels);
    }
    
    public static NativeImage fromBase64(final String string) throws IOException {
        try (final MemoryStack memoryStack2 = MemoryStack.stackPush()) {
            final ByteBuffer byteBuffer4 = memoryStack2.UTF8((CharSequence)string.replaceAll("\n", ""), false);
            final ByteBuffer byteBuffer5 = Base64.getDecoder().decode(byteBuffer4);
            final ByteBuffer byteBuffer6 = memoryStack2.malloc(byteBuffer5.remaining());
            byteBuffer6.put(byteBuffer5);
            byteBuffer6.rewind();
            return read(byteBuffer6);
        }
    }
    
    static {
        OPEN_OPTIONS = (Set)EnumSet.of((Enum)StandardOpenOption.WRITE, (Enum)StandardOpenOption.CREATE, (Enum)StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    static class WriteCallback extends STBIWriteCallback {
        private final WritableByteChannel output;
        private IOException exception;
        
        private WriteCallback(final WritableByteChannel writableByteChannel) {
            this.output = writableByteChannel;
        }
        
        public void invoke(final long long1, final long long2, final int integer) {
            final ByteBuffer byteBuffer7 = getData(long2, integer);
            try {
                this.output.write(byteBuffer7);
            }
            catch (IOException iOException8) {
                this.exception = iOException8;
            }
        }
        
        public void throwIfException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
    
    public enum InternalGlFormat {
        RGBA(6408), 
        RGB(6407), 
        LUMINANCE_ALPHA(6410), 
        LUMINANCE(6409), 
        INTENSITY(32841);
        
        private final int glFormat;
        
        private InternalGlFormat(final int integer3) {
            this.glFormat = integer3;
        }
        
        public int glFormat() {
            return this.glFormat;
        }
    }
    
    public enum Format {
        RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true), 
        RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true), 
        LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true), 
        LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);
        
        private final int components;
        private final int glFormat;
        private final boolean hasRed;
        private final boolean hasGreen;
        private final boolean hasBlue;
        private final boolean hasLuminance;
        private final boolean hasAlpha;
        private final int redOffset;
        private final int greenOffset;
        private final int blueOffset;
        private final int luminanceOffset;
        private final int alphaOffset;
        private final boolean supportedByStb;
        
        private Format(final int integer3, final int integer4, final boolean boolean5, final boolean boolean6, final boolean boolean7, final boolean boolean8, final boolean boolean9, final int integer10, final int integer11, final int integer12, final int integer13, final int integer14, final boolean boolean15) {
            this.components = integer3;
            this.glFormat = integer4;
            this.hasRed = boolean5;
            this.hasGreen = boolean6;
            this.hasBlue = boolean7;
            this.hasLuminance = boolean8;
            this.hasAlpha = boolean9;
            this.redOffset = integer10;
            this.greenOffset = integer11;
            this.blueOffset = integer12;
            this.luminanceOffset = integer13;
            this.alphaOffset = integer14;
            this.supportedByStb = boolean15;
        }
        
        public int components() {
            return this.components;
        }
        
        public void setPackPixelStoreState() {
            GlStateManager.pixelStore(3333, this.components());
        }
        
        public void setUnpackPixelStoreState() {
            GlStateManager.pixelStore(3317, this.components());
        }
        
        public int glFormat() {
            return this.glFormat;
        }
        
        public boolean hasAlpha() {
            return this.hasAlpha;
        }
        
        public int alphaOffset() {
            return this.alphaOffset;
        }
        
        public boolean hasLuminanceOrAlpha() {
            return this.hasLuminance || this.hasAlpha;
        }
        
        public int luminanceOrAlphaOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.alphaOffset;
        }
        
        public boolean supportedByStb() {
            return this.supportedByStb;
        }
        
        private static Format getStbFormat(final int integer) {
            switch (integer) {
                case 1: {
                    return Format.LUMINANCE;
                }
                case 2: {
                    return Format.LUMINANCE_ALPHA;
                }
                case 3: {
                    return Format.RGB;
                }
                default: {
                    return Format.RGBA;
                }
            }
        }
    }
}
