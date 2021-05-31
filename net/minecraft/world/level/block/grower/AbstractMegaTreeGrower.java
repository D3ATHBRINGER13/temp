package net.minecraft.world.level.block.grower;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.block.Blocks;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public abstract class AbstractMegaTreeGrower extends AbstractTreeGrower {
    @Override
    public boolean growTree(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final Random random) {
        for (int integer6 = 0; integer6 >= -1; --integer6) {
            for (int integer7 = 0; integer7 >= -1; --integer7) {
                if (isTwoByTwoSapling(bvt, bhs, ew, integer6, integer7)) {
                    return this.placeMega(bhs, ew, bvt, random, integer6, integer7);
                }
            }
        }
        return super.growTree(bhs, ew, bvt, random);
    }
    
    @Nullable
    protected abstract AbstractTreeFeature<NoneFeatureConfiguration> getMegaFeature(final Random random);
    
    public boolean placeMega(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final Random random, final int integer5, final int integer6) {
        final AbstractTreeFeature<NoneFeatureConfiguration> bzv8 = this.getMegaFeature(random);
        if (bzv8 == null) {
            return false;
        }
        final BlockState bvt2 = Blocks.AIR.defaultBlockState();
        bhs.setBlock(ew.offset(integer5, 0, integer6), bvt2, 4);
        bhs.setBlock(ew.offset(integer5 + 1, 0, integer6), bvt2, 4);
        bhs.setBlock(ew.offset(integer5, 0, integer6 + 1), bvt2, 4);
        bhs.setBlock(ew.offset(integer5 + 1, 0, integer6 + 1), bvt2, 4);
        if (bzv8.place(bhs, bhs.getChunkSource().getGenerator(), random, ew.offset(integer5, 0, integer6), FeatureConfiguration.NONE)) {
            return true;
        }
        bhs.setBlock(ew.offset(integer5, 0, integer6), bvt, 4);
        bhs.setBlock(ew.offset(integer5 + 1, 0, integer6), bvt, 4);
        bhs.setBlock(ew.offset(integer5, 0, integer6 + 1), bvt, 4);
        bhs.setBlock(ew.offset(integer5 + 1, 0, integer6 + 1), bvt, 4);
        return false;
    }
    
    public static boolean isTwoByTwoSapling(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final int integer4, final int integer5) {
        final Block bmv6 = bvt.getBlock();
        return bmv6 == bhb.getBlockState(ew.offset(integer4, 0, integer5)).getBlock() && bmv6 == bhb.getBlockState(ew.offset(integer4 + 1, 0, integer5)).getBlock() && bmv6 == bhb.getBlockState(ew.offset(integer4, 0, integer5 + 1)).getBlock() && bmv6 == bhb.getBlockState(ew.offset(integer4 + 1, 0, integer5 + 1)).getBlock();
    }
}
