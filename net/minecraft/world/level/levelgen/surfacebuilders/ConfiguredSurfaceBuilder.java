package net.minecraft.world.level.levelgen.surfacebuilders;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;

public class ConfiguredSurfaceBuilder<SC extends SurfaceBuilderConfiguration> {
    public final SurfaceBuilder<SC> surfaceBuilder;
    public final SC config;
    
    public ConfiguredSurfaceBuilder(final SurfaceBuilder<SC> ckh, final SC ckj) {
        this.surfaceBuilder = ckh;
        this.config = ckj;
    }
    
    public void apply(final Random random, final ChunkAccess bxh, final Biome bio, final int integer4, final int integer5, final int integer6, final double double7, final BlockState bvt8, final BlockState bvt9, final int integer10, final long long11) {
        this.surfaceBuilder.apply(random, bxh, bio, integer4, integer5, integer6, double7, bvt8, bvt9, integer10, long11, this.config);
    }
    
    public void initNoise(final long long1) {
        this.surfaceBuilder.initNoise(long1);
    }
    
    public SC getSurfaceBuilderConfiguration() {
        return this.config;
    }
}
