package net.minecraft.client.resources;

import net.minecraft.world.level.GrassColor;
import java.io.IOException;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

public class GrassColorReloadListener extends SimplePreparableReloadListener<int[]> {
    private static final ResourceLocation LOCATION;
    
    @Override
    protected int[] prepare(final ResourceManager xi, final ProfilerFiller agn) {
        try {
            return LegacyStuffWrapper.getPixels(xi, GrassColorReloadListener.LOCATION);
        }
        catch (IOException iOException4) {
            throw new IllegalStateException("Failed to load grass color texture", (Throwable)iOException4);
        }
    }
    
    @Override
    protected void apply(final int[] arr, final ResourceManager xi, final ProfilerFiller agn) {
        GrassColor.init(arr);
    }
    
    static {
        LOCATION = new ResourceLocation("textures/colormap/grass.png");
    }
}
