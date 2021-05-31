package net.minecraft.client.renderer.texture;

import org.apache.logging.log4j.LogManager;
import net.minecraft.server.packs.resources.Resource;
import java.util.Iterator;
import java.io.IOException;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture {
    private static final Logger LOGGER;
    public final List<String> layerPaths;
    
    public LayeredTexture(final String... arr) {
        this.layerPaths = (List<String>)Lists.newArrayList((Object[])arr);
        if (this.layerPaths.isEmpty()) {
            throw new IllegalStateException("Layered texture with no layers.");
        }
    }
    
    public void load(final ResourceManager xi) throws IOException {
        final Iterator<String> iterator3 = (Iterator<String>)this.layerPaths.iterator();
        final String string4 = (String)iterator3.next();
        try (final Resource xh5 = xi.getResource(new ResourceLocation(string4));
             final NativeImage cuj7 = NativeImage.read(xh5.getInputStream())) {
            while (iterator3.hasNext()) {
                final String string5 = (String)iterator3.next();
                if (string5 == null) {
                    continue;
                }
                try (final Resource xh6 = xi.getResource(new ResourceLocation(string5));
                     final NativeImage cuj8 = NativeImage.read(xh6.getInputStream())) {
                    for (int integer14 = 0; integer14 < cuj8.getHeight(); ++integer14) {
                        for (int integer15 = 0; integer15 < cuj8.getWidth(); ++integer15) {
                            cuj7.blendPixel(integer15, integer14, cuj8.getPixelRGBA(integer15, integer14));
                        }
                    }
                }
            }
            TextureUtil.prepareImage(this.getId(), cuj7.getWidth(), cuj7.getHeight());
            cuj7.upload(0, 0, 0, false);
        }
        catch (IOException iOException5) {
            LayeredTexture.LOGGER.error("Couldn't load layered image", (Throwable)iOException5);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
