package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DiskReplaceFeature extends Feature<DiskConfiguration> {
    public DiskReplaceFeature(final Function<Dynamic<?>, ? extends DiskConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, final BlockPos ew, final DiskConfiguration cbe) {
        if (!bhs.getFluidState(ew).is(FluidTags.WATER)) {
            return false;
        }
        int integer7 = 0;
        for (int integer8 = random.nextInt(cbe.radius - 2) + 2, integer9 = ew.getX() - integer8; integer9 <= ew.getX() + integer8; ++integer9) {
            for (int integer10 = ew.getZ() - integer8; integer10 <= ew.getZ() + integer8; ++integer10) {
                final int integer11 = integer9 - ew.getX();
                final int integer12 = integer10 - ew.getZ();
                if (integer11 * integer11 + integer12 * integer12 <= integer8 * integer8) {
                    for (int integer13 = ew.getY() - cbe.ySize; integer13 <= ew.getY() + cbe.ySize; ++integer13) {
                        final BlockPos ew2 = new BlockPos(integer9, integer13, integer10);
                        final BlockState bvt15 = bhs.getBlockState(ew2);
                        for (final BlockState bvt16 : cbe.targets) {
                            if (bvt16.getBlock() == bvt15.getBlock()) {
                                bhs.setBlock(ew2, cbe.state, 2);
                                ++integer7;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return integer7 > 0;
    }
}
