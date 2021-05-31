package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ReedsFeature extends Feature<NoneFeatureConfiguration> {
    public ReedsFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        int integer7 = 0;
        for (int integer8 = 0; integer8 < 20; ++integer8) {
            final BlockPos ew2 = ew.offset(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            if (bhs.isEmptyBlock(ew2)) {
                final BlockPos ew3 = ew2.below();
                if (bhs.getFluidState(ew3.west()).is(FluidTags.WATER) || bhs.getFluidState(ew3.east()).is(FluidTags.WATER) || bhs.getFluidState(ew3.north()).is(FluidTags.WATER) || bhs.getFluidState(ew3.south()).is(FluidTags.WATER)) {
                    for (int integer9 = 2 + random.nextInt(random.nextInt(3) + 1), integer10 = 0; integer10 < integer9; ++integer10) {
                        if (Blocks.SUGAR_CANE.defaultBlockState().canSurvive(bhs, ew2)) {
                            bhs.setBlock(ew2.above(integer10), Blocks.SUGAR_CANE.defaultBlockState(), 2);
                            ++integer7;
                        }
                    }
                }
            }
        }
        return integer7 > 0;
    }
}
