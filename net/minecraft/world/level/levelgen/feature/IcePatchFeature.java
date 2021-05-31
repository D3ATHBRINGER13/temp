package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;

public class IcePatchFeature extends Feature<FeatureRadius> {
    private final Block block;
    
    public IcePatchFeature(final Function<Dynamic<?>, ? extends FeatureRadius> function) {
        super(function);
        this.block = Blocks.PACKED_ICE;
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, BlockPos ew, final FeatureRadius cbp) {
        while (bhs.isEmptyBlock(ew) && ew.getY() > 2) {
            ew = ew.below();
        }
        if (bhs.getBlockState(ew).getBlock() != Blocks.SNOW_BLOCK) {
            return false;
        }
        final int integer7 = random.nextInt(cbp.radius) + 2;
        final int integer8 = 1;
        for (int integer9 = ew.getX() - integer7; integer9 <= ew.getX() + integer7; ++integer9) {
            for (int integer10 = ew.getZ() - integer7; integer10 <= ew.getZ() + integer7; ++integer10) {
                final int integer11 = integer9 - ew.getX();
                final int integer12 = integer10 - ew.getZ();
                if (integer11 * integer11 + integer12 * integer12 <= integer7 * integer7) {
                    for (int integer13 = ew.getY() - 1; integer13 <= ew.getY() + 1; ++integer13) {
                        final BlockPos ew2 = new BlockPos(integer9, integer13, integer10);
                        final Block bmv15 = bhs.getBlockState(ew2).getBlock();
                        if (Block.equalsDirt(bmv15) || bmv15 == Blocks.SNOW_BLOCK || bmv15 == Blocks.ICE) {
                            bhs.setBlock(ew2, this.block.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
        return true;
    }
}
