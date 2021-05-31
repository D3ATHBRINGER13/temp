package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.stream.IntStream;
import net.minecraft.world.level.biome.Biome;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class TopSolidHeightMapNoiseBasedDecorator extends FeatureDecorator<DecoratorNoiseCountFactor> {
    public TopSolidHeightMapNoiseBasedDecorator(final Function<Dynamic<?>, ? extends DecoratorNoiseCountFactor> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final DecoratorNoiseCountFactor cgy, final BlockPos ew) {
        final double double7 = Biome.BIOME_INFO_NOISE.getValue(ew.getX() / cgy.noiseFactor, ew.getZ() / cgy.noiseFactor);
        final int integer9 = (int)Math.ceil((double7 + cgy.noiseOffset) * cgy.noiseToCountRatio);
        return (Stream<BlockPos>)IntStream.range(0, integer9).mapToObj(integer -> {
            final int integer2 = random.nextInt(16);
            final int integer3 = random.nextInt(16);
            final int integer4 = bhs.getHeight(cgy.heightmap, ew.getX() + integer2, ew.getZ() + integer3);
            return new BlockPos(ew.getX() + integer2, integer4, ew.getZ() + integer3);
        });
    }
}
