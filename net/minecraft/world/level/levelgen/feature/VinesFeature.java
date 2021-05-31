package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.core.Direction;

public class VinesFeature extends Feature<NoneFeatureConfiguration> {
    private static final Direction[] DIRECTIONS;
    
    public VinesFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        final BlockPos.MutableBlockPos a7 = new BlockPos.MutableBlockPos(ew);
        for (int integer8 = ew.getY(); integer8 < 256; ++integer8) {
            a7.set(ew);
            a7.move(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            a7.setY(integer8);
            if (bhs.isEmptyBlock(a7)) {
                for (final Direction fb12 : VinesFeature.DIRECTIONS) {
                    if (fb12 != Direction.DOWN) {
                        if (VineBlock.isAcceptableNeighbour(bhs, a7, fb12)) {
                            bhs.setBlock(a7, ((AbstractStateHolder<O, BlockState>)Blocks.VINE.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)VineBlock.getPropertyForFace(fb12), true), 2);
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    static {
        DIRECTIONS = Direction.values();
    }
}
