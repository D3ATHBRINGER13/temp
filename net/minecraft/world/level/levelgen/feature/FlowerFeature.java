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

public abstract class FlowerFeature extends Feature<NoneFeatureConfiguration> {
    public FlowerFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function, false);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        final BlockState bvt7 = this.getRandomFlower(random, ew);
        int integer8 = 0;
        for (int integer9 = 0; integer9 < 64; ++integer9) {
            final BlockPos ew2 = ew.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (bhs.isEmptyBlock(ew2) && ew2.getY() < 255 && bvt7.canSurvive(bhs, ew2)) {
                bhs.setBlock(ew2, bvt7, 2);
                ++integer8;
            }
        }
        return integer8 > 0;
    }
    
    public abstract BlockState getRandomFlower(final Random random, final BlockPos ew);
}
