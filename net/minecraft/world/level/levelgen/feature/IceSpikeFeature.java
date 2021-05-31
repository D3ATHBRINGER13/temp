package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Block;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class IceSpikeFeature extends Feature<NoneFeatureConfiguration> {
    public IceSpikeFeature(final Function<Dynamic<?>, ? extends NoneFeatureConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, BlockPos ew, final NoneFeatureConfiguration cdd) {
        while (bhs.isEmptyBlock(ew) && ew.getY() > 2) {
            ew = ew.below();
        }
        if (bhs.getBlockState(ew).getBlock() != Blocks.SNOW_BLOCK) {
            return false;
        }
        ew = ew.above(random.nextInt(4));
        final int integer7 = random.nextInt(4) + 7;
        final int integer8 = integer7 / 4 + random.nextInt(2);
        if (integer8 > 1 && random.nextInt(60) == 0) {
            ew = ew.above(10 + random.nextInt(30));
        }
        for (int integer9 = 0; integer9 < integer7; ++integer9) {
            final float float10 = (1.0f - integer9 / (float)integer7) * integer8;
            for (int integer10 = Mth.ceil(float10), integer11 = -integer10; integer11 <= integer10; ++integer11) {
                final float float11 = Mth.abs(integer11) - 0.25f;
                for (int integer12 = -integer10; integer12 <= integer10; ++integer12) {
                    final float float12 = Mth.abs(integer12) - 0.25f;
                    if ((integer11 == 0 && integer12 == 0) || float11 * float11 + float12 * float12 <= float10 * float10) {
                        if ((integer11 != -integer10 && integer11 != integer10 && integer12 != -integer10 && integer12 != integer10) || random.nextFloat() <= 0.75f) {
                            BlockState bvt16 = bhs.getBlockState(ew.offset(integer11, integer9, integer12));
                            Block bmv17 = bvt16.getBlock();
                            if (bvt16.isAir() || Block.equalsDirt(bmv17) || bmv17 == Blocks.SNOW_BLOCK || bmv17 == Blocks.ICE) {
                                this.setBlock(bhs, ew.offset(integer11, integer9, integer12), Blocks.PACKED_ICE.defaultBlockState());
                            }
                            if (integer9 != 0 && integer10 > 1) {
                                bvt16 = bhs.getBlockState(ew.offset(integer11, -integer9, integer12));
                                bmv17 = bvt16.getBlock();
                                if (bvt16.isAir() || Block.equalsDirt(bmv17) || bmv17 == Blocks.SNOW_BLOCK || bmv17 == Blocks.ICE) {
                                    this.setBlock(bhs, ew.offset(integer11, -integer9, integer12), Blocks.PACKED_ICE.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
        int integer9 = integer8 - 1;
        if (integer9 < 0) {
            integer9 = 0;
        }
        else if (integer9 > 1) {
            integer9 = 1;
        }
        for (int integer13 = -integer9; integer13 <= integer9; ++integer13) {
            for (int integer10 = -integer9; integer10 <= integer9; ++integer10) {
                BlockPos ew2 = ew.offset(integer13, -1, integer10);
                int integer14 = 50;
                if (Math.abs(integer13) == 1 && Math.abs(integer10) == 1) {
                    integer14 = random.nextInt(5);
                }
                while (ew2.getY() > 50) {
                    final BlockState bvt17 = bhs.getBlockState(ew2);
                    final Block bmv18 = bvt17.getBlock();
                    if (!bvt17.isAir() && !Block.equalsDirt(bmv18) && bmv18 != Blocks.SNOW_BLOCK && bmv18 != Blocks.ICE && bmv18 != Blocks.PACKED_ICE) {
                        break;
                    }
                    this.setBlock(bhs, ew2, Blocks.PACKED_ICE.defaultBlockState());
                    ew2 = ew2.below();
                    if (--integer14 > 0) {
                        continue;
                    }
                    ew2 = ew2.below(random.nextInt(5) + 1);
                    integer14 = random.nextInt(5);
                }
            }
        }
        return true;
    }
}
