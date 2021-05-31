package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class FillLayerFeature extends Feature<LayerConfiguration> {
    public FillLayerFeature(final Function<Dynamic<?>, ? extends LayerConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final LayerConfiguration ccr) {
        final BlockPos.MutableBlockPos a7 = new BlockPos.MutableBlockPos();
        for (int integer8 = 0; integer8 < 16; ++integer8) {
            for (int integer9 = 0; integer9 < 16; ++integer9) {
                final int integer10 = ew.getX() + integer8;
                final int integer11 = ew.getZ() + integer9;
                final int integer12 = ccr.height;
                a7.set(integer10, integer12, integer11);
                if (bhs.getBlockState(a7).isAir()) {
                    bhs.setBlock(a7, ccr.state, 2);
                }
            }
        }
        return true;
    }
}
