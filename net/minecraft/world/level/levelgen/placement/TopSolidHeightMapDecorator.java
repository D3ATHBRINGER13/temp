package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;

public class TopSolidHeightMapDecorator extends FeatureDecorator<NoneDecoratorConfiguration> {
    public TopSolidHeightMapDecorator(final Function<Dynamic<?>, ? extends NoneDecoratorConfiguration> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final NoneDecoratorConfiguration cdc, final BlockPos ew) {
        final int integer7 = random.nextInt(16);
        final int integer8 = random.nextInt(16);
        final int integer9 = bhs.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, ew.getX() + integer7, ew.getZ() + integer8);
        return (Stream<BlockPos>)Stream.of(new BlockPos(ew.getX() + integer7, integer9, ew.getZ() + integer8));
    }
}
