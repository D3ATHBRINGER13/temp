package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class GrassFeature extends Feature<GrassConfiguration> {
    public GrassFeature(final Function<Dynamic<?>, ? extends GrassConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, BlockPos ew, final GrassConfiguration cbw) {
        for (BlockState bvt7 = bhs.getBlockState(ew); (bvt7.isAir() || bvt7.is(BlockTags.LEAVES)) && ew.getY() > 0; ew = ew.below(), bvt7 = bhs.getBlockState(ew)) {}
        int integer8 = 0;
        for (int integer9 = 0; integer9 < 128; ++integer9) {
            final BlockPos ew2 = ew.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (bhs.isEmptyBlock(ew2) && cbw.state.canSurvive(bhs, ew2)) {
                bhs.setBlock(ew2, cbw.state, 2);
                ++integer8;
            }
        }
        return integer8 > 0;
    }
}
