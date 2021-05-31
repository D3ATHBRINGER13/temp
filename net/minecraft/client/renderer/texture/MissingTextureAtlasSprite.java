package net.minecraft.client.renderer.texture;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.util.LazyLoadedValue;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class MissingTextureAtlasSprite extends TextureAtlasSprite {
    private static final ResourceLocation MISSING_TEXTURE_LOCATION;
    @Nullable
    private static DynamicTexture missingTexture;
    private static final LazyLoadedValue<NativeImage> MISSING_IMAGE_DATA;
    
    private MissingTextureAtlasSprite() {
        super(MissingTextureAtlasSprite.MISSING_TEXTURE_LOCATION, 16, 16);
        this.mainImage = new NativeImage[] { MissingTextureAtlasSprite.MISSING_IMAGE_DATA.get() };
    }
    
    public static MissingTextureAtlasSprite newInstance() {
        return new MissingTextureAtlasSprite();
    }
    
    public static ResourceLocation getLocation() {
        return MissingTextureAtlasSprite.MISSING_TEXTURE_LOCATION;
    }
    
    @Override
    public void wipeFrameData() {
        for (int integer2 = 1; integer2 < this.mainImage.length; ++integer2) {
            this.mainImage[integer2].close();
        }
        this.mainImage = new NativeImage[] { MissingTextureAtlasSprite.MISSING_IMAGE_DATA.get() };
    }
    
    public static DynamicTexture getTexture() {
        if (MissingTextureAtlasSprite.missingTexture == null) {
            MissingTextureAtlasSprite.missingTexture = new DynamicTexture(MissingTextureAtlasSprite.MISSING_IMAGE_DATA.get());
            Minecraft.getInstance().getTextureManager().register(MissingTextureAtlasSprite.MISSING_TEXTURE_LOCATION, MissingTextureAtlasSprite.missingTexture);
        }
        return MissingTextureAtlasSprite.missingTexture;
    }
    
    static {
        MISSING_TEXTURE_LOCATION = new ResourceLocation("missingno");
        MISSING_IMAGE_DATA = new LazyLoadedValue<NativeImage>((java.util.function.Supplier<NativeImage>)(() -> {
            final NativeImage cuj1 = new NativeImage(16, 16, false);
            final int integer2 = -16777216;
            final int integer3 = -524040;
            for (int integer4 = 0; integer4 < 16; ++integer4) {
                for (int integer5 = 0; integer5 < 16; ++integer5) {
                    if (integer4 < 8 ^ integer5 < 8) {
                        cuj1.setPixelRGBA(integer5, integer4, -524040);
                    }
                    else {
                        cuj1.setPixelRGBA(integer5, integer4, -16777216);
                    }
                }
            }
            cuj1.untrack();
            return cuj1;
        }));
    }
}
