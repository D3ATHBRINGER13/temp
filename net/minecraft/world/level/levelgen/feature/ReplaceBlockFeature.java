package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ReplaceBlockFeature extends Feature<ReplaceBlockConfiguration> {
    public ReplaceBlockFeature(final Function<Dynamic<?>, ? extends ReplaceBlockConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final ReplaceBlockConfiguration cdx) {
        if (bhs.getBlockState(ew).getBlock() == cdx.target.getBlock()) {
            bhs.setBlock(ew, cdx.state, 2);
        }
        return true;
    }
}
