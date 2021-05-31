package net.minecraft.client.resources;

import net.minecraft.world.level.FoliageColor;
import java.io.IOException;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

public class FoliageColorReloadListener extends SimplePreparableReloadListener<int[]> {
    private static final ResourceLocation LOCATION;
    
    @Override
    protected int[] prepare(final ResourceManager xi, final ProfilerFiller agn) {
        try {
            return LegacyStuffWrapper.getPixels(xi, FoliageColorReloadListener.LOCATION);
        }
        catch (IOException iOException4) {
            throw new IllegalStateException("Failed to load foliage color texture", (Throwable)iOException4);
        }
    }
    
    @Override
    protected void apply(final int[] arr, final ResourceManager xi, final ProfilerFiller agn) {
        FoliageColor.init(arr);
    }
    
    static {
        LOCATION = new ResourceLocation("textures/colormap/foliage.png");
    }
}
