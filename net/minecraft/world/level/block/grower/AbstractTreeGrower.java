package net.minecraft.world.level.block.grower;

import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import java.util.Random;

public abstract class AbstractTreeGrower {
    @Nullable
    protected abstract AbstractTreeFeature<NoneFeatureConfiguration> getFeature(final Random random);
    
    public boolean growTree(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final Random random) {
        final AbstractTreeFeature<NoneFeatureConfiguration> bzv6 = this.getFeature(random);
        if (bzv6 == null) {
            return false;
        }
        bhs.setBlock(ew, Blocks.AIR.defaultBlockState(), 4);
        if (bzv6.place(bhs, bhs.getChunkSource().getGenerator(), random, ew, FeatureConfiguration.NONE)) {
            return true;
        }
        bhs.setBlock(ew, bvt, 4);
        return false;
    }
}
