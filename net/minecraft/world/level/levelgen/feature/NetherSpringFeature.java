package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class NetherSpringFeature extends Feature<HellSpringConfiguration> {
    private static final BlockState NETHERRACK;
    
    public NetherSpringFeature(final Function<Dynamic<?>, ? extends HellSpringConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final HellSpringConfiguration ccb) {
        if (bhs.getBlockState(ew.above()) != NetherSpringFeature.NETHERRACK) {
            return false;
        }
        if (!bhs.getBlockState(ew).isAir() && bhs.getBlockState(ew) != NetherSpringFeature.NETHERRACK) {
            return false;
        }
        int integer7 = 0;
        if (bhs.getBlockState(ew.west()) == NetherSpringFeature.NETHERRACK) {
            ++integer7;
        }
        if (bhs.getBlockState(ew.east()) == NetherSpringFeature.NETHERRACK) {
            ++integer7;
        }
        if (bhs.getBlockState(ew.north()) == NetherSpringFeature.NETHERRACK) {
            ++integer7;
        }
        if (bhs.getBlockState(ew.south()) == NetherSpringFeature.NETHERRACK) {
            ++integer7;
        }
        if (bhs.getBlockState(ew.below()) == NetherSpringFeature.NETHERRACK) {
            ++integer7;
        }
        int integer8 = 0;
        if (bhs.isEmptyBlock(ew.west())) {
            ++integer8;
        }
        if (bhs.isEmptyBlock(ew.east())) {
            ++integer8;
        }
        if (bhs.isEmptyBlock(ew.north())) {
            ++integer8;
        }
        if (bhs.isEmptyBlock(ew.south())) {
            ++integer8;
        }
        if (bhs.isEmptyBlock(ew.below())) {
            ++integer8;
        }
        if ((!ccb.insideRock && integer7 == 4 && integer8 == 1) || integer7 == 5) {
            bhs.setBlock(ew, Blocks.LAVA.defaultBlockState(), 2);
            bhs.getLiquidTicks().scheduleTick(ew, Fluids.LAVA, 0);
        }
        return true;
    }
    
    static {
        NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
    }
}
