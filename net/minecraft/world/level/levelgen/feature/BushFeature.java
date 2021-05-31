package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class BushFeature extends Feature<BushConfiguration> {
    public BushFeature(final Function<Dynamic<?>, ? extends BushConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final BushConfiguration cag) {
        int integer7 = 0;
        final BlockState bvt8 = cag.state;
        for (int integer8 = 0; integer8 < 64; ++integer8) {
            final BlockPos ew2 = ew.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (bhs.isEmptyBlock(ew2) && (!bhs.getDimension().isHasCeiling() || ew2.getY() < 255) && bvt8.canSurvive(bhs, ew2)) {
                bhs.setBlock(ew2, bvt8, 2);
                ++integer7;
            }
        }
        return integer7 > 0;
    }
}
