package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class MelonFeature extends Feature<NoneFeatureConfiguration> {
    public MelonFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        for (int integer7 = 0; integer7 < 64; ++integer7) {
            final BlockPos ew2 = ew.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            final BlockState bvt9 = Blocks.MELON.defaultBlockState();
            if (bhs.getBlockState(ew2).getMaterial().isReplaceable() && bhs.getBlockState(ew2.below()).getBlock() == Blocks.GRASS_BLOCK) {
                bhs.setBlock(ew2, bvt9, 2);
            }
        }
        return true;
    }
}
