package net.minecraft.client.renderer.texture;

import org.apache.logging.log4j.LogManager;
import net.minecraft.server.packs.resources.Resource;
import java.io.IOException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.DyeColor;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class LayeredColorMaskTexture extends AbstractTexture {
    private static final Logger LOGGER;
    private final ResourceLocation baseLayerResource;
    private final List<String> layerMaskPaths;
    private final List<DyeColor> layerColors;
    
    public LayeredColorMaskTexture(final ResourceLocation qv, final List<String> list2, final List<DyeColor> list3) {
        this.baseLayerResource = qv;
        this.layerMaskPaths = list2;
        this.layerColors = list3;
    }
    
    public void load(final ResourceManager xi) throws IOException {
        try (final Resource xh3 = xi.getResource(this.baseLayerResource);
             final NativeImage cuj5 = NativeImage.read(xh3.getInputStream());
             final NativeImage cuj6 = new NativeImage(cuj5.getWidth(), cuj5.getHeight(), false)) {
            cuj6.copyFrom(cuj5);
            for (int integer9 = 0; integer9 < 17 && integer9 < this.layerMaskPaths.size() && integer9 < this.layerColors.size(); ++integer9) {
                final String string10 = (String)this.layerMaskPaths.get(integer9);
                if (string10 != null) {
                    try (final Resource xh4 = xi.getResource(new ResourceLocation(string10));
                         final NativeImage cuj7 = NativeImage.read(xh4.getInputStream())) {
                        final int integer10 = ((DyeColor)this.layerColors.get(integer9)).getTextureDiffuseColorBGR();
                        if (cuj7.getWidth() != cuj6.getWidth() || cuj7.getHeight() != cuj6.getHeight()) {
                            continue;
                        }
                        for (int integer11 = 0; integer11 < cuj7.getHeight(); ++integer11) {
                            for (int integer12 = 0; integer12 < cuj7.getWidth(); ++integer12) {
                                final int integer13 = cuj7.getPixelRGBA(integer12, integer11);
                                if ((integer13 & 0xFF000000) != 0x0) {
                                    final int integer14 = (integer13 & 0xFF) << 24 & 0xFF000000;
                                    final int integer15 = cuj5.getPixelRGBA(integer12, integer11);
                                    final int integer16 = Mth.colorMultiply(integer15, integer10) & 0xFFFFFF;
                                    cuj6.blendPixel(integer12, integer11, integer14 | integer16);
                                }
                            }
                        }
                    }
                }
            }
            TextureUtil.prepareImage(this.getId(), cuj6.getWidth(), cuj6.getHeight());
            GlStateManager.pixelTransfer(3357, Float.MAX_VALUE);
            cuj6.upload(0, 0, 0, false);
            GlStateManager.pixelTransfer(3357, 0.0f);
        }
        catch (IOException iOException3) {
            LayeredColorMaskTexture.LOGGER.error("Couldn't load layered color mask image", (Throwable)iOException3);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
