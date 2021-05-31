package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.LevelReader;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class TaigaGrassFeature extends Feature<NoneFeatureConfiguration> {
    public TaigaGrassFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    public BlockState getState(final Random random) {
        return (random.nextInt(5) > 0) ? Blocks.FERN.defaultBlockState() : Blocks.GRASS.defaultBlockState();
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, BlockPos ew, final NoneFeatureConfiguration cdd) {
        final BlockState bvt7 = this.getState(random);
        for (BlockState bvt8 = bhs.getBlockState(ew); (bvt8.isAir() || bvt8.is(BlockTags.LEAVES)) && ew.getY() > 0; ew = ew.below(), bvt8 = bhs.getBlockState(ew)) {}
        int integer9 = 0;
        for (int integer10 = 0; integer10 < 128; ++integer10) {
            final BlockPos ew2 = ew.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (bhs.isEmptyBlock(ew2) && bvt7.canSurvive(bhs, ew2)) {
                bhs.setBlock(ew2, bvt7, 2);
                ++integer9;
            }
        }
        return integer9 > 0;
    }
}
