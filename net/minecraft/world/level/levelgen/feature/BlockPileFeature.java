package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public abstract class BlockPileFeature extends Feature<NoneFeatureConfiguration> {
    public BlockPileFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        if (ew.getY() < 5) {
            return false;
        }
        final int integer7 = 2 + random.nextInt(2);
        final int integer8 = 2 + random.nextInt(2);
        for (final BlockPos ew2 : BlockPos.betweenClosed(ew.offset(-integer7, 0, -integer8), ew.offset(integer7, 1, integer8))) {
            final int integer9 = ew.getX() - ew2.getX();
            final int integer10 = ew.getZ() - ew2.getZ();
            if (integer9 * integer9 + integer10 * integer10 <= random.nextFloat() * 10.0f - random.nextFloat() * 6.0f) {
                this.tryPlaceBlock(bhs, ew2, random);
            }
            else {
                if (random.nextFloat() >= 0.031) {
                    continue;
                }
                this.tryPlaceBlock(bhs, ew2, random);
            }
        }
        return true;
    }
    
    private boolean mayPlaceOn(final LevelAccessor bhs, final BlockPos ew, final Random random) {
        final BlockPos ew2 = ew.below();
        final BlockState bvt6 = bhs.getBlockState(ew2);
        if (bvt6.getBlock() == Blocks.GRASS_PATH) {
            return random.nextBoolean();
        }
        return bvt6.isFaceSturdy(bhs, ew2, Direction.UP);
    }
    
    private void tryPlaceBlock(final LevelAccessor bhs, final BlockPos ew, final Random random) {
        if (bhs.isEmptyBlock(ew) && this.mayPlaceOn(bhs, ew, random)) {
            bhs.setBlock(ew, this.getBlockState(bhs), 4);
        }
    }
    
    protected abstract BlockState getBlockState(final LevelAccessor bhs);
}
