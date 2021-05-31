package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class CentralSpikedFeature extends Feature<NoneFeatureConfiguration> {
    protected final BlockState blockState;
    
    public CentralSpikedFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function, final BlockState bvt) {
        super(function);
        this.blockState = bvt;
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        int integer7 = 0;
        for (int integer8 = 0; integer8 < 64; ++integer8) {
            final BlockPos ew2 = ew.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (bhs.isEmptyBlock(ew2) && bhs.getBlockState(ew2.below()).getBlock() == Blocks.GRASS_BLOCK) {
                bhs.setBlock(ew2, this.blockState, 2);
                ++integer7;
            }
        }
        return integer7 > 0;
    }
}
