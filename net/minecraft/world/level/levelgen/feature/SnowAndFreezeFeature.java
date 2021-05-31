package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SnowAndFreezeFeature extends Feature<NoneFeatureConfiguration> {
    public SnowAndFreezeFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final NoneFeatureConfiguration cdd) {
        final BlockPos.MutableBlockPos a7 = new BlockPos.MutableBlockPos();
        final BlockPos.MutableBlockPos a8 = new BlockPos.MutableBlockPos();
        for (int integer9 = 0; integer9 < 16; ++integer9) {
            for (int integer10 = 0; integer10 < 16; ++integer10) {
                final int integer11 = ew.getX() + integer9;
                final int integer12 = ew.getZ() + integer10;
                final int integer13 = bhs.getHeight(Heightmap.Types.MOTION_BLOCKING, integer11, integer12);
                a7.set(integer11, integer13, integer12);
                a8.set(a7).move(Direction.DOWN, 1);
                final Biome bio14 = bhs.getBiome(a7);
                if (bio14.shouldFreeze(bhs, a8, false)) {
                    bhs.setBlock(a8, Blocks.ICE.defaultBlockState(), 2);
                }
                if (bio14.shouldSnow(bhs, a7)) {
                    bhs.setBlock(a7, Blocks.SNOW.defaultBlockState(), 2);
                    final BlockState bvt15 = bhs.getBlockState(a8);
                    if (bvt15.<Comparable>hasProperty((Property<Comparable>)SnowyDirtBlock.SNOWY)) {
                        bhs.setBlock(a8, ((AbstractStateHolder<O, BlockState>)bvt15).<Comparable, Boolean>setValue((Property<Comparable>)SnowyDirtBlock.SNOWY, true), 2);
                    }
                }
            }
        }
        return true;
    }
}
