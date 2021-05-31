package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class GlowstoneFeature extends Feature<NoneFeatureConfiguration> {
    public GlowstoneFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        if (!bhs.isEmptyBlock(ew)) {
            return false;
        }
        if (bhs.getBlockState(ew.above()).getBlock() != Blocks.NETHERRACK) {
            return false;
        }
        bhs.setBlock(ew, Blocks.GLOWSTONE.defaultBlockState(), 2);
        for (int integer7 = 0; integer7 < 1500; ++integer7) {
            final BlockPos ew2 = ew.offset(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
            if (bhs.getBlockState(ew2).isAir()) {
                int integer8 = 0;
                for (final Direction fb13 : Direction.values()) {
                    if (bhs.getBlockState(ew2.relative(fb13)).getBlock() == Blocks.GLOWSTONE) {
                        ++integer8;
                    }
                    if (integer8 > 1) {
                        break;
                    }
                }
                if (integer8 == 1) {
                    bhs.setBlock(ew2, Blocks.GLOWSTONE.defaultBlockState(), 2);
                }
            }
        }
        return true;
    }
}
