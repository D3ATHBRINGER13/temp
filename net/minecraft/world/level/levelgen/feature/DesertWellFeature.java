package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

public class DesertWellFeature extends Feature<NoneFeatureConfiguration> {
    private static final BlockStatePredicate IS_SAND;
    private final BlockState sandSlab;
    private final BlockState sandstone;
    private final BlockState water;
    
    public DesertWellFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
        this.sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
        this.sandstone = Blocks.SANDSTONE.defaultBlockState();
        this.water = Blocks.WATER.defaultBlockState();
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, BlockPos ew, final NoneFeatureConfiguration cdd) {
        for (ew = ew.above(); bhs.isEmptyBlock(ew) && ew.getY() > 2; ew = ew.below()) {}
        if (!DesertWellFeature.IS_SAND.test(bhs.getBlockState(ew))) {
            return false;
        }
        for (int integer7 = -2; integer7 <= 2; ++integer7) {
            for (int integer8 = -2; integer8 <= 2; ++integer8) {
                if (bhs.isEmptyBlock(ew.offset(integer7, -1, integer8)) && bhs.isEmptyBlock(ew.offset(integer7, -2, integer8))) {
                    return false;
                }
            }
        }
        for (int integer7 = -1; integer7 <= 0; ++integer7) {
            for (int integer8 = -2; integer8 <= 2; ++integer8) {
                for (int integer9 = -2; integer9 <= 2; ++integer9) {
                    bhs.setBlock(ew.offset(integer8, integer7, integer9), this.sandstone, 2);
                }
            }
        }
        bhs.setBlock(ew, this.water, 2);
        for (final Direction fb8 : Direction.Plane.HORIZONTAL) {
            bhs.setBlock(ew.relative(fb8), this.water, 2);
        }
        for (int integer7 = -2; integer7 <= 2; ++integer7) {
            for (int integer8 = -2; integer8 <= 2; ++integer8) {
                if (integer7 == -2 || integer7 == 2 || integer8 == -2 || integer8 == 2) {
                    bhs.setBlock(ew.offset(integer7, 1, integer8), this.sandstone, 2);
                }
            }
        }
        bhs.setBlock(ew.offset(2, 1, 0), this.sandSlab, 2);
        bhs.setBlock(ew.offset(-2, 1, 0), this.sandSlab, 2);
        bhs.setBlock(ew.offset(0, 1, 2), this.sandSlab, 2);
        bhs.setBlock(ew.offset(0, 1, -2), this.sandSlab, 2);
        for (int integer7 = -1; integer7 <= 1; ++integer7) {
            for (int integer8 = -1; integer8 <= 1; ++integer8) {
                if (integer7 == 0 && integer8 == 0) {
                    bhs.setBlock(ew.offset(integer7, 4, integer8), this.sandstone, 2);
                }
                else {
                    bhs.setBlock(ew.offset(integer7, 4, integer8), this.sandSlab, 2);
                }
            }
        }
        for (int integer7 = 1; integer7 <= 3; ++integer7) {
            bhs.setBlock(ew.offset(-1, integer7, -1), this.sandstone, 2);
            bhs.setBlock(ew.offset(-1, integer7, 1), this.sandstone, 2);
            bhs.setBlock(ew.offset(1, integer7, -1), this.sandstone, 2);
            bhs.setBlock(ew.offset(1, integer7, 1), this.sandstone, 2);
        }
        return true;
    }
    
    static {
        IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
    }
}
