package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WaterlilyFeature extends Feature<NoneFeatureConfiguration> {
    public WaterlilyFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        BlockPos ew3;
        for (BlockPos ew2 = ew; ew2.getY() > 0; ew2 = ew3) {
            ew3 = ew2.below();
            if (!bhs.isEmptyBlock(ew3)) {
                break;
            }
        }
        for (int integer8 = 0; integer8 < 10; ++integer8) {
            final BlockPos ew4 = ew.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            final BlockState bvt10 = Blocks.LILY_PAD.defaultBlockState();
            if (bhs.isEmptyBlock(ew4) && bvt10.canSurvive(bhs, ew4)) {
                bhs.setBlock(ew4, bvt10, 2);
            }
        }
        return true;
    }
}
