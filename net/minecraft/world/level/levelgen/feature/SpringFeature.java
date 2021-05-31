package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class SpringFeature extends Feature<SpringConfiguration> {
    public SpringFeature(final Function<Dynamic<?>, ? extends SpringConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final SpringConfiguration ceq) {
        if (!Block.equalsStone(bhs.getBlockState(ew.above()).getBlock())) {
            return false;
        }
        if (!Block.equalsStone(bhs.getBlockState(ew.below()).getBlock())) {
            return false;
        }
        final BlockState bvt7 = bhs.getBlockState(ew);
        if (!bvt7.isAir() && !Block.equalsStone(bvt7.getBlock())) {
            return false;
        }
        int integer8 = 0;
        int integer9 = 0;
        if (Block.equalsStone(bhs.getBlockState(ew.west()).getBlock())) {
            ++integer9;
        }
        if (Block.equalsStone(bhs.getBlockState(ew.east()).getBlock())) {
            ++integer9;
        }
        if (Block.equalsStone(bhs.getBlockState(ew.north()).getBlock())) {
            ++integer9;
        }
        if (Block.equalsStone(bhs.getBlockState(ew.south()).getBlock())) {
            ++integer9;
        }
        int integer10 = 0;
        if (bhs.isEmptyBlock(ew.west())) {
            ++integer10;
        }
        if (bhs.isEmptyBlock(ew.east())) {
            ++integer10;
        }
        if (bhs.isEmptyBlock(ew.north())) {
            ++integer10;
        }
        if (bhs.isEmptyBlock(ew.south())) {
            ++integer10;
        }
        if (integer9 == 3 && integer10 == 1) {
            bhs.setBlock(ew, ceq.state.createLegacyBlock(), 2);
            bhs.getLiquidTicks().scheduleTick(ew, ceq.state.getType(), 0);
            ++integer8;
        }
        return integer8 > 0;
    }
}
