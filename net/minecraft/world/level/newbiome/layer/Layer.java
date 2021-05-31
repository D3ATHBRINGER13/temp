package net.minecraft.world.level.newbiome.layer;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.LazyArea;
import org.apache.logging.log4j.Logger;

public class Layer {
    private static final Logger LOGGER;
    private final LazyArea area;
    
    public Layer(final AreaFactory<LazyArea> clu) {
        this.area = clu.make();
    }
    
    public Biome[] getArea(final int integer1, final int integer2, final int integer3, final int integer4) {
        final Biome[] arr6 = new Biome[integer3 * integer4];
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            for (int integer6 = 0; integer6 < integer3; ++integer6) {
                final int integer7 = this.area.get(integer1 + integer6, integer2 + integer5);
                final Biome bio10 = this.getBiome(integer7);
                arr6[integer6 + integer5 * integer3] = bio10;
            }
        }
        return arr6;
    }
    
    private Biome getBiome(final int integer) {
        final Biome bio3 = Registry.BIOME.byId(integer);
        if (bio3 != null) {
            return bio3;
        }
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw new IllegalStateException(new StringBuilder().append("Unknown biome id: ").append(integer).toString());
        }
        Layer.LOGGER.warn("Unknown biome id: ", integer);
        return Biomes.DEFAULT;
    }
    
    public Biome get(final int integer1, final int integer2) {
        return this.getBiome(this.area.get(integer1, integer2));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
