package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.server.packs.resources.Resource;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class LegacyStuffWrapper {
    @Deprecated
    public static int[] getPixels(final ResourceManager xi, final ResourceLocation qv) throws IOException {
        try (final Resource xh3 = xi.getResource(qv);
             final NativeImage cuj5 = NativeImage.read(xh3.getInputStream())) {
            return cuj5.makePixelArray();
        }
    }
}
