package net.minecraft.client.renderer.texture;

import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import com.google.common.collect.Lists;
import java.util.Arrays;
import net.minecraft.server.packs.resources.Resource;
import com.mojang.datafixers.util.Pair;
import com.mojang.blaze3d.platform.PngInfo;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import javax.annotation.Nullable;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;

public class TextureAtlasSprite {
    private final ResourceLocation name;
    protected final int width;
    protected final int height;
    protected NativeImage[] mainImage;
    @Nullable
    protected int[] framesX;
    @Nullable
    protected int[] framesY;
    protected NativeImage[] activeFrame;
    private AnimationMetadataSection metadata;
    protected int x;
    protected int y;
    private float u0;
    private float u1;
    private float v0;
    private float v1;
    protected int frame;
    protected int subFrame;
    private static final float[] POW22;
    
    protected TextureAtlasSprite(final ResourceLocation qv, final int integer2, final int integer3) {
        this.name = qv;
        this.width = integer2;
        this.height = integer3;
    }
    
    protected TextureAtlasSprite(final ResourceLocation qv, final PngInfo cuk, @Nullable final AnimationMetadataSection dyd) {
        this.name = qv;
        if (dyd != null) {
            final Pair<Integer, Integer> pair5 = getFrameSize(dyd.getFrameWidth(), dyd.getFrameHeight(), cuk.width, cuk.height);
            this.width = (int)pair5.getFirst();
            this.height = (int)pair5.getSecond();
            if (!isDivisionInteger(cuk.width, this.width) || !isDivisionInteger(cuk.height, this.height)) {
                throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", new Object[] { this.width, this.height, cuk.width, cuk.height }));
            }
        }
        else {
            this.width = cuk.width;
            this.height = cuk.height;
        }
        this.metadata = dyd;
    }
    
    private static Pair<Integer, Integer> getFrameSize(final int integer1, final int integer2, final int integer3, final int integer4) {
        if (integer1 != -1) {
            if (integer2 != -1) {
                return (Pair<Integer, Integer>)Pair.of(integer1, integer2);
            }
            return (Pair<Integer, Integer>)Pair.of(integer1, integer4);
        }
        else {
            if (integer2 != -1) {
                return (Pair<Integer, Integer>)Pair.of(integer3, integer2);
            }
            final int integer5 = Math.min(integer3, integer4);
            return (Pair<Integer, Integer>)Pair.of(integer5, integer5);
        }
    }
    
    private static boolean isDivisionInteger(final int integer1, final int integer2) {
        return integer1 / integer2 * integer2 == integer1;
    }
    
    private void generateMipLevels(final int integer) {
        final NativeImage[] arr3 = new NativeImage[integer + 1];
        arr3[0] = this.mainImage[0];
        if (integer > 0) {
            boolean boolean4 = false;
        Label_0092:
            for (int integer2 = 0; integer2 < this.mainImage[0].getWidth(); ++integer2) {
                for (int integer3 = 0; integer3 < this.mainImage[0].getHeight(); ++integer3) {
                    if (this.mainImage[0].getPixelRGBA(integer2, integer3) >> 24 == 0) {
                        boolean4 = true;
                        break Label_0092;
                    }
                }
            }
            for (int integer2 = 1; integer2 <= integer; ++integer2) {
                if (this.mainImage.length > integer2 && this.mainImage[integer2] != null) {
                    arr3[integer2] = this.mainImage[integer2];
                }
                else {
                    final NativeImage cuj6 = arr3[integer2 - 1];
                    final NativeImage cuj7 = new NativeImage(cuj6.getWidth() >> 1, cuj6.getHeight() >> 1, false);
                    final int integer4 = cuj7.getWidth();
                    final int integer5 = cuj7.getHeight();
                    for (int integer6 = 0; integer6 < integer4; ++integer6) {
                        for (int integer7 = 0; integer7 < integer5; ++integer7) {
                            cuj7.setPixelRGBA(integer6, integer7, alphaBlend(cuj6.getPixelRGBA(integer6 * 2 + 0, integer7 * 2 + 0), cuj6.getPixelRGBA(integer6 * 2 + 1, integer7 * 2 + 0), cuj6.getPixelRGBA(integer6 * 2 + 0, integer7 * 2 + 1), cuj6.getPixelRGBA(integer6 * 2 + 1, integer7 * 2 + 1), boolean4));
                        }
                    }
                    arr3[integer2] = cuj7;
                }
            }
            for (int integer2 = integer + 1; integer2 < this.mainImage.length; ++integer2) {
                if (this.mainImage[integer2] != null) {
                    this.mainImage[integer2].close();
                }
            }
        }
        this.mainImage = arr3;
    }
    
    private static int alphaBlend(final int integer1, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        if (boolean5) {
            float float6 = 0.0f;
            float float7 = 0.0f;
            float float8 = 0.0f;
            float float9 = 0.0f;
            if (integer1 >> 24 != 0) {
                float6 += getPow22(integer1 >> 24);
                float7 += getPow22(integer1 >> 16);
                float8 += getPow22(integer1 >> 8);
                float9 += getPow22(integer1 >> 0);
            }
            if (integer2 >> 24 != 0) {
                float6 += getPow22(integer2 >> 24);
                float7 += getPow22(integer2 >> 16);
                float8 += getPow22(integer2 >> 8);
                float9 += getPow22(integer2 >> 0);
            }
            if (integer3 >> 24 != 0) {
                float6 += getPow22(integer3 >> 24);
                float7 += getPow22(integer3 >> 16);
                float8 += getPow22(integer3 >> 8);
                float9 += getPow22(integer3 >> 0);
            }
            if (integer4 >> 24 != 0) {
                float6 += getPow22(integer4 >> 24);
                float7 += getPow22(integer4 >> 16);
                float8 += getPow22(integer4 >> 8);
                float9 += getPow22(integer4 >> 0);
            }
            float6 /= 4.0f;
            float7 /= 4.0f;
            float8 /= 4.0f;
            float9 /= 4.0f;
            int integer5 = (int)(Math.pow((double)float6, 0.45454545454545453) * 255.0);
            final int integer6 = (int)(Math.pow((double)float7, 0.45454545454545453) * 255.0);
            final int integer7 = (int)(Math.pow((double)float8, 0.45454545454545453) * 255.0);
            final int integer8 = (int)(Math.pow((double)float9, 0.45454545454545453) * 255.0);
            if (integer5 < 96) {
                integer5 = 0;
            }
            return integer5 << 24 | integer6 << 16 | integer7 << 8 | integer8;
        }
        final int integer9 = gammaBlend(integer1, integer2, integer3, integer4, 24);
        final int integer10 = gammaBlend(integer1, integer2, integer3, integer4, 16);
        final int integer11 = gammaBlend(integer1, integer2, integer3, integer4, 8);
        final int integer12 = gammaBlend(integer1, integer2, integer3, integer4, 0);
        return integer9 << 24 | integer10 << 16 | integer11 << 8 | integer12;
    }
    
    private static int gammaBlend(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        final float float6 = getPow22(integer1 >> integer5);
        final float float7 = getPow22(integer2 >> integer5);
        final float float8 = getPow22(integer3 >> integer5);
        final float float9 = getPow22(integer4 >> integer5);
        final float float10 = (float)Math.pow((float6 + float7 + float8 + float9) * 0.25, 0.45454545454545453);
        return (int)(float10 * 255.0);
    }
    
    private static float getPow22(final int integer) {
        return TextureAtlasSprite.POW22[integer & 0xFF];
    }
    
    private void upload(final int integer) {
        int integer2 = 0;
        int integer3 = 0;
        if (this.framesX != null) {
            integer2 = this.framesX[integer] * this.width;
            integer3 = this.framesY[integer] * this.height;
        }
        this.upload(integer2, integer3, this.mainImage);
    }
    
    private void upload(final int integer1, final int integer2, final NativeImage[] arr) {
        for (int integer3 = 0; integer3 < this.mainImage.length; ++integer3) {
            arr[integer3].upload(integer3, this.x >> integer3, this.y >> integer3, integer1 >> integer3, integer2 >> integer3, this.width >> integer3, this.height >> integer3, this.mainImage.length > 1);
        }
    }
    
    public void init(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.x = integer3;
        this.y = integer4;
        this.u0 = integer3 / (float)integer1;
        this.u1 = (integer3 + this.width) / (float)integer1;
        this.v0 = integer4 / (float)integer2;
        this.v1 = (integer4 + this.height) / (float)integer2;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public float getU0() {
        return this.u0;
    }
    
    public float getU1() {
        return this.u1;
    }
    
    public float getU(final double double1) {
        final float float4 = this.u1 - this.u0;
        return this.u0 + float4 * (float)double1 / 16.0f;
    }
    
    public float getUOffset(final float float1) {
        final float float2 = this.u1 - this.u0;
        return (float1 - this.u0) / float2 * 16.0f;
    }
    
    public float getV0() {
        return this.v0;
    }
    
    public float getV1() {
        return this.v1;
    }
    
    public float getV(final double double1) {
        final float float4 = this.v1 - this.v0;
        return this.v0 + float4 * (float)double1 / 16.0f;
    }
    
    public float getVOffset(final float float1) {
        final float float2 = this.v1 - this.v0;
        return (float1 - this.v0) / float2 * 16.0f;
    }
    
    public ResourceLocation getName() {
        return this.name;
    }
    
    public void cycleFrames() {
        ++this.subFrame;
        if (this.subFrame >= this.metadata.getFrameTime(this.frame)) {
            final int integer2 = this.metadata.getFrameIndex(this.frame);
            final int integer3 = (this.metadata.getFrameCount() == 0) ? this.getFrameCount() : this.metadata.getFrameCount();
            this.frame = (this.frame + 1) % integer3;
            this.subFrame = 0;
            final int integer4 = this.metadata.getFrameIndex(this.frame);
            if (integer2 != integer4 && integer4 >= 0 && integer4 < this.getFrameCount()) {
                this.upload(integer4);
            }
        }
        else if (this.metadata.isInterpolatedFrames()) {
            this.uploadInterpolatedFrame();
        }
    }
    
    private void uploadInterpolatedFrame() {
        final double double2 = 1.0 - this.subFrame / (double)this.metadata.getFrameTime(this.frame);
        final int integer4 = this.metadata.getFrameIndex(this.frame);
        final int integer5 = (this.metadata.getFrameCount() == 0) ? this.getFrameCount() : this.metadata.getFrameCount();
        final int integer6 = this.metadata.getFrameIndex((this.frame + 1) % integer5);
        if (integer4 != integer6 && integer6 >= 0 && integer6 < this.getFrameCount()) {
            if (this.activeFrame == null || this.activeFrame.length != this.mainImage.length) {
                if (this.activeFrame != null) {
                    for (final NativeImage cuj10 : this.activeFrame) {
                        if (cuj10 != null) {
                            cuj10.close();
                        }
                    }
                }
                this.activeFrame = new NativeImage[this.mainImage.length];
            }
            for (int integer7 = 0; integer7 < this.mainImage.length; ++integer7) {
                final int integer8 = this.width >> integer7;
                final int integer9 = this.height >> integer7;
                if (this.activeFrame[integer7] == null) {
                    this.activeFrame[integer7] = new NativeImage(integer8, integer9, false);
                }
                for (int integer10 = 0; integer10 < integer9; ++integer10) {
                    for (int integer11 = 0; integer11 < integer8; ++integer11) {
                        final int integer12 = this.getPixel(integer4, integer7, integer11, integer10);
                        final int integer13 = this.getPixel(integer6, integer7, integer11, integer10);
                        final int integer14 = this.mix(double2, integer12 >> 16 & 0xFF, integer13 >> 16 & 0xFF);
                        final int integer15 = this.mix(double2, integer12 >> 8 & 0xFF, integer13 >> 8 & 0xFF);
                        final int integer16 = this.mix(double2, integer12 & 0xFF, integer13 & 0xFF);
                        this.activeFrame[integer7].setPixelRGBA(integer11, integer10, (integer12 & 0xFF000000) | integer14 << 16 | integer15 << 8 | integer16);
                    }
                }
            }
            this.upload(0, 0, this.activeFrame);
        }
    }
    
    private int mix(final double double1, final int integer2, final int integer3) {
        return (int)(double1 * integer2 + (1.0 - double1) * integer3);
    }
    
    public int getFrameCount() {
        return (this.framesX == null) ? 0 : this.framesX.length;
    }
    
    public void loadData(final Resource xh, final int integer) throws IOException {
        final NativeImage cuj4 = NativeImage.read(xh.getInputStream());
        (this.mainImage = new NativeImage[integer])[0] = cuj4;
        int integer2;
        if (this.metadata != null && this.metadata.getFrameWidth() != -1) {
            integer2 = cuj4.getWidth() / this.metadata.getFrameWidth();
        }
        else {
            integer2 = cuj4.getWidth() / this.width;
        }
        int integer3;
        if (this.metadata != null && this.metadata.getFrameHeight() != -1) {
            integer3 = cuj4.getHeight() / this.metadata.getFrameHeight();
        }
        else {
            integer3 = cuj4.getHeight() / this.height;
        }
        if (this.metadata != null && this.metadata.getFrameCount() > 0) {
            final int integer4 = (int)this.metadata.getUniqueFrameIndices().stream().max(Integer::compareTo).get() + 1;
            this.framesX = new int[integer4];
            this.framesY = new int[integer4];
            Arrays.fill(this.framesX, -1);
            Arrays.fill(this.framesY, -1);
            for (final int integer5 : this.metadata.getUniqueFrameIndices()) {
                if (integer5 >= integer2 * integer3) {
                    throw new RuntimeException(new StringBuilder().append("invalid frameindex ").append(integer5).toString());
                }
                final int integer6 = integer5 / integer2;
                final int integer7 = integer5 % integer2;
                this.framesX[integer5] = integer7;
                this.framesY[integer5] = integer6;
            }
        }
        else {
            final List<AnimationFrame> list7 = (List<AnimationFrame>)Lists.newArrayList();
            final int integer8 = integer2 * integer3;
            this.framesX = new int[integer8];
            this.framesY = new int[integer8];
            for (int integer5 = 0; integer5 < integer3; ++integer5) {
                for (int integer6 = 0; integer6 < integer2; ++integer6) {
                    final int integer7 = integer5 * integer2 + integer6;
                    this.framesX[integer7] = integer6;
                    this.framesY[integer7] = integer5;
                    list7.add(new AnimationFrame(integer7, -1));
                }
            }
            int integer5 = 1;
            boolean boolean10 = false;
            if (this.metadata != null) {
                integer5 = this.metadata.getDefaultFrameTime();
                boolean10 = this.metadata.isInterpolatedFrames();
            }
            this.metadata = new AnimationMetadataSection(list7, this.width, this.height, integer5, boolean10);
        }
    }
    
    public void applyMipmapping(final int integer) {
        try {
            this.generateMipLevels(integer);
        }
        catch (Throwable throwable3) {
            final CrashReport d4 = CrashReport.forThrowable(throwable3, "Generating mipmaps for frame");
            final CrashReportCategory e5 = d4.addCategory("Frame being iterated");
            e5.setDetail("Frame sizes", (CrashReportDetail<String>)(() -> {
                final StringBuilder stringBuilder2 = new StringBuilder();
                for (final NativeImage cuj6 : this.mainImage) {
                    if (stringBuilder2.length() > 0) {
                        stringBuilder2.append(", ");
                    }
                    stringBuilder2.append((cuj6 == null) ? "null" : new StringBuilder().append(cuj6.getWidth()).append("x").append(cuj6.getHeight()).toString());
                }
                return stringBuilder2.toString();
            }));
            throw new ReportedException(d4);
        }
    }
    
    public void wipeFrameData() {
        if (this.mainImage != null) {
            for (final NativeImage cuj5 : this.mainImage) {
                if (cuj5 != null) {
                    cuj5.close();
                }
            }
        }
        this.mainImage = null;
        if (this.activeFrame != null) {
            for (final NativeImage cuj5 : this.activeFrame) {
                if (cuj5 != null) {
                    cuj5.close();
                }
            }
        }
        this.activeFrame = null;
    }
    
    public boolean isAnimation() {
        return this.metadata != null && this.metadata.getFrameCount() > 1;
    }
    
    public String toString() {
        final int integer2 = (this.framesX == null) ? 0 : this.framesX.length;
        return new StringBuilder().append("TextureAtlasSprite{name='").append(this.name).append('\'').append(", frameCount=").append(integer2).append(", x=").append(this.x).append(", y=").append(this.y).append(", height=").append(this.height).append(", width=").append(this.width).append(", u0=").append(this.u0).append(", u1=").append(this.u1).append(", v0=").append(this.v0).append(", v1=").append(this.v1).append('}').toString();
    }
    
    private int getPixel(final int integer1, final int integer2, final int integer3, final int integer4) {
        return this.mainImage[integer2].getPixelRGBA(integer3 + (this.framesX[integer1] * this.width >> integer2), integer4 + (this.framesY[integer1] * this.height >> integer2));
    }
    
    public boolean isTransparent(final int integer1, final int integer2, final int integer3) {
        return (this.mainImage[0].getPixelRGBA(integer2 + this.framesX[integer1] * this.width, integer3 + this.framesY[integer1] * this.height) >> 24 & 0xFF) == 0x0;
    }
    
    public void uploadFirstFrame() {
        this.upload(0);
    }
    
    static {
        POW22 = Util.<float[]>make(new float[256], (java.util.function.Consumer<float[]>)(arr -> {
            for (int integer2 = 0; integer2 < arr.length; ++integer2) {
                arr[integer2] = (float)Math.pow((double)(integer2 / 255.0f), 2.2);
            }
        }));
    }
}
