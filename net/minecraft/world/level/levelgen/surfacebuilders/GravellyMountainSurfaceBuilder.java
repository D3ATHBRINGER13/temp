package net.minecraft.world.level.levelgen.surfacebuilders;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class GravellyMountainSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    public GravellyMountainSurfaceBuilder(final Function<Dynamic<?>, ? extends SurfaceBuilderBaseConfiguration> function) {
        super(function);
    }
    
    @Override
    public void apply(final Random random, final ChunkAccess bxh, final Biome bio, final int integer4, final int integer5, final int integer6, final double double7, final BlockState bvt8, final BlockState bvt9, final int integer10, final long long11, final SurfaceBuilderBaseConfiguration cki) {
        if (double7 < -1.0 || double7 > 2.0) {
            SurfaceBuilder.DEFAULT.apply(random, bxh, bio, integer4, integer5, integer6, double7, bvt8, bvt9, integer10, long11, SurfaceBuilder.CONFIG_GRAVEL);
        }
        else if (double7 > 1.0) {
            SurfaceBuilder.DEFAULT.apply(random, bxh, bio, integer4, integer5, integer6, double7, bvt8, bvt9, integer10, long11, SurfaceBuilder.CONFIG_STONE);
        }
        else {
            SurfaceBuilder.DEFAULT.apply(random, bxh, bio, integer4, integer5, integer6, double7, bvt8, bvt9, integer10, long11, SurfaceBuilder.CONFIG_GRASS);
        }
    }
}
