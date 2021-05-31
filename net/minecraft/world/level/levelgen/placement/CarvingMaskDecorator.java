package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.BitSet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class CarvingMaskDecorator extends FeatureDecorator<DecoratorCarvingMaskConfig> {
    public CarvingMaskDecorator(final Function<Dynamic<?>, ? extends DecoratorCarvingMaskConfig> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final DecoratorCarvingMaskConfig cgt, final BlockPos ew) {
        final ChunkAccess bxh7 = bhs.getChunk(ew);
        final ChunkPos bhd8 = bxh7.getPos();
        final BitSet bitSet9 = bxh7.getCarvingMask(cgt.step);
        return (Stream<BlockPos>)IntStream.range(0, bitSet9.length()).filter(integer -> bitSet9.get(integer) && random.nextFloat() < cgt.probability).mapToObj(integer -> {
            final int integer2 = integer & 0xF;
            final int integer3 = integer >> 4 & 0xF;
            final int integer4 = integer >> 8;
            return new BlockPos(bhd8.getMinBlockX() + integer2, integer4, bhd8.getMinBlockZ() + integer3);
        });
    }
}
