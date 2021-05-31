package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SimpleBlockFeature extends Feature<SimpleBlockConfiguration> {
    public SimpleBlockFeature(final Function<Dynamic<?>, ? extends SimpleBlockConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final SimpleBlockConfiguration ceg) {
        if (ceg.placeOn.contains(bhs.getBlockState(ew.below())) && ceg.placeIn.contains(bhs.getBlockState(ew)) && ceg.placeUnder.contains(bhs.getBlockState(ew.above()))) {
            bhs.setBlock(ew, ceg.toPlace, 2);
            return true;
        }
        return false;
    }
}
