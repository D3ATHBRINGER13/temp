package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
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
import net.minecraft.world.level.block.DeadBushBlock;

public class DeadBushFeature extends Feature<NoneFeatureConfiguration> {
    private static final DeadBushBlock DEAD_BUSH;
    
    public DeadBushFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, BlockPos ew, final NoneFeatureConfiguration cdd) {
        for (BlockState bvt7 = bhs.getBlockState(ew); (bvt7.isAir() || bvt7.is(BlockTags.LEAVES)) && ew.getY() > 0; ew = ew.below(), bvt7 = bhs.getBlockState(ew)) {}
        final BlockState bvt8 = DeadBushFeature.DEAD_BUSH.defaultBlockState();
        for (int integer9 = 0; integer9 < 4; ++integer9) {
            final BlockPos ew2 = ew.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (bhs.isEmptyBlock(ew2) && bvt8.canSurvive(bhs, ew2)) {
                bhs.setBlock(ew2, bvt8, 2);
            }
        }
        return true;
    }
    
    static {
        DEAD_BUSH = (DeadBushBlock)Blocks.DEAD_BUSH;
    }
}
