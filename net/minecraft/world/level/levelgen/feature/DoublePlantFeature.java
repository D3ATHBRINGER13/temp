package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DoublePlantFeature extends Feature<DoublePlantConfiguration> {
    public DoublePlantFeature(final Function<Dynamic<?>, ? extends DoublePlantConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final DoublePlantConfiguration cbg) {
        boolean boolean7 = false;
        for (int integer8 = 0; integer8 < 64; ++integer8) {
            final BlockPos ew2 = ew.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (bhs.isEmptyBlock(ew2) && ew2.getY() < 254 && cbg.state.canSurvive(bhs, ew2)) {
                ((DoublePlantBlock)cbg.state.getBlock()).placeAt(bhs, ew2, 2);
                boolean7 = true;
            }
        }
        return boolean7;
    }
}
