package net.minecraft.world.level.levelgen.placement;

import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class LakeLavaPlacementDecorator extends FeatureDecorator<LakeChanceDecoratorConfig> {
    public LakeLavaPlacementDecorator(final Function<Dynamic<?>, ? extends LakeChanceDecoratorConfig> function) {
        super(function);
    }
    
    @Override
    public Stream<BlockPos> getPositions(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final LakeChanceDecoratorConfig chh, final BlockPos ew) {
        if (random.nextInt(chh.chance / 10) == 0) {
            final int integer7 = random.nextInt(16);
            final int integer8 = random.nextInt(random.nextInt(bxi.getGenDepth() - 8) + 8);
            final int integer9 = random.nextInt(16);
            if (integer8 < bhs.getSeaLevel() || random.nextInt(chh.chance / 8) == 0) {
                return (Stream<BlockPos>)Stream.of(ew.offset(integer7, integer8, integer9));
            }
        }
        return (Stream<BlockPos>)Stream.empty();
    }
}
