package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class BlockBlobFeature extends Feature<BlockBlobConfiguration> {
    public BlockBlobFeature(final Function<Dynamic<?>, ? extends BlockBlobConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, BlockPos ew, final BlockBlobConfiguration bzz) {
        while (ew.getY() > 3) {
            if (!bhs.isEmptyBlock(ew.below())) {
                final Block bmv7 = bhs.getBlockState(ew.below()).getBlock();
                if (bmv7 == Blocks.GRASS_BLOCK || Block.equalsDirt(bmv7)) {
                    break;
                }
                if (Block.equalsStone(bmv7)) {
                    break;
                }
            }
            ew = ew.below();
        }
        if (ew.getY() <= 3) {
            return false;
        }
        for (int integer7 = bzz.startRadius, integer8 = 0; integer7 >= 0 && integer8 < 3; ++integer8) {
            final int integer9 = integer7 + random.nextInt(2);
            final int integer10 = integer7 + random.nextInt(2);
            final int integer11 = integer7 + random.nextInt(2);
            final float float12 = (integer9 + integer10 + integer11) * 0.333f + 0.5f;
            for (final BlockPos ew2 : BlockPos.betweenClosed(ew.offset(-integer9, -integer10, -integer11), ew.offset(integer9, integer10, integer11))) {
                if (ew2.distSqr(ew) <= float12 * float12) {
                    bhs.setBlock(ew2, bzz.state, 4);
                }
            }
            ew = ew.offset(-(integer7 + 1) + random.nextInt(2 + integer7 * 2), 0 - random.nextInt(2), -(integer7 + 1) + random.nextInt(2 + integer7 * 2));
        }
        return true;
    }
}
