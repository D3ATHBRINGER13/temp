package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ChorusPlantFeature extends Feature<NoneFeatureConfiguration> {
    public ChorusPlantFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        if (bhs.isEmptyBlock(ew.above()) && bhs.getBlockState(ew).getBlock() == Blocks.END_STONE) {
            ChorusFlowerBlock.generatePlant(bhs, ew.above(), random, 8);
            return true;
        }
        return false;
    }
}
