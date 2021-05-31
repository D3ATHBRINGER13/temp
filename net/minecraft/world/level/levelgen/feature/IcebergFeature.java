package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class IcebergFeature extends Feature<IcebergConfiguration> {
    public IcebergFeature(final Function<Dynamic<?>, ? extends IcebergConfiguration> function) {
        super(function);
    }
    
    @Override
    public boolean place(final LevelAccessor bhs, final ChunkGenerator<? extends ChunkGeneratorSettings> bxi, final Random random, BlockPos ew, final IcebergConfiguration cci) {
        ew = new BlockPos(ew.getX(), bhs.getSeaLevel(), ew.getZ());
        final boolean boolean7 = random.nextDouble() > 0.7;
        final BlockState bvt8 = cci.state;
        final double double9 = random.nextDouble() * 2.0 * 3.141592653589793;
        final int integer11 = 11 - random.nextInt(5);
        final int integer12 = 3 + random.nextInt(3);
        final boolean boolean8 = random.nextDouble() > 0.7;
        final int integer13 = 11;
        int integer14 = boolean8 ? (random.nextInt(6) + 6) : (random.nextInt(15) + 3);
        if (!boolean8 && random.nextDouble() > 0.9) {
            integer14 += random.nextInt(19) + 7;
        }
        final int integer15 = Math.min(integer14 + random.nextInt(11), 18);
        final int integer16 = Math.min(integer14 + random.nextInt(7) - random.nextInt(5), 11);
        final int integer17 = boolean8 ? integer11 : 11;
        for (int integer18 = -integer17; integer18 < integer17; ++integer18) {
            for (int integer19 = -integer17; integer19 < integer17; ++integer19) {
                for (int integer20 = 0; integer20 < integer14; ++integer20) {
                    final int integer21 = boolean8 ? this.heightDependentRadiusEllipse(integer20, integer14, integer16) : this.heightDependentRadiusRound(random, integer20, integer14, integer16);
                    if (boolean8 || integer18 < integer21) {
                        this.generateIcebergBlock(bhs, random, ew, integer14, integer18, integer20, integer19, integer21, integer17, boolean8, integer12, double9, boolean7, bvt8);
                    }
                }
            }
        }
        this.smooth(bhs, ew, integer16, integer14, boolean8, integer11);
        for (int integer18 = -integer17; integer18 < integer17; ++integer18) {
            for (int integer19 = -integer17; integer19 < integer17; ++integer19) {
                for (int integer20 = -1; integer20 > -integer15; --integer20) {
                    final int integer21 = boolean8 ? Mth.ceil(integer17 * (1.0f - (float)Math.pow((double)integer20, 2.0) / (integer15 * 8.0f))) : integer17;
                    final int integer22 = this.heightDependentRadiusSteep(random, -integer20, integer15, integer16);
                    if (integer18 < integer22) {
                        this.generateIcebergBlock(bhs, random, ew, integer15, integer18, integer20, integer19, integer22, integer21, boolean8, integer12, double9, boolean7, bvt8);
                    }
                }
            }
        }
        final boolean boolean9 = boolean8 ? (random.nextDouble() > 0.1) : (random.nextDouble() > 0.7);
        if (boolean9) {
            this.generateCutOut(random, bhs, integer16, integer14, ew, boolean8, integer11, double9, integer12);
        }
        return true;
    }
    
    private void generateCutOut(final Random random, final LevelAccessor bhs, final int integer3, final int integer4, final BlockPos ew, final boolean boolean6, final int integer7, final double double8, final int integer9) {
        final int integer10 = random.nextBoolean() ? -1 : 1;
        final int integer11 = random.nextBoolean() ? -1 : 1;
        int integer12 = random.nextInt(Math.max(integer3 / 2 - 2, 1));
        if (random.nextBoolean()) {
            integer12 = integer3 / 2 + 1 - random.nextInt(Math.max(integer3 - integer3 / 2 - 1, 1));
        }
        int integer13 = random.nextInt(Math.max(integer3 / 2 - 2, 1));
        if (random.nextBoolean()) {
            integer13 = integer3 / 2 + 1 - random.nextInt(Math.max(integer3 - integer3 / 2 - 1, 1));
        }
        if (boolean6) {
            integer13 = (integer12 = random.nextInt(Math.max(integer7 - 5, 1)));
        }
        final BlockPos ew2 = new BlockPos(integer10 * integer12, 0, integer11 * integer13);
        final double double9 = boolean6 ? (double8 + 1.5707963267948966) : (random.nextDouble() * 2.0 * 3.141592653589793);
        for (int integer14 = 0; integer14 < integer4 - 3; ++integer14) {
            final int integer15 = this.heightDependentRadiusRound(random, integer14, integer4, integer3);
            this.carve(integer15, integer14, ew, bhs, false, double9, ew2, integer7, integer9);
        }
        for (int integer14 = -1; integer14 > -integer4 + random.nextInt(5); --integer14) {
            final int integer15 = this.heightDependentRadiusSteep(random, -integer14, integer4, integer3);
            this.carve(integer15, integer14, ew, bhs, true, double9, ew2, integer7, integer9);
        }
    }
    
    private void carve(final int integer1, final int integer2, final BlockPos ew3, final LevelAccessor bhs, final boolean boolean5, final double double6, final BlockPos ew7, final int integer8, final int integer9) {
        final int integer10 = integer1 + 1 + integer8 / 3;
        final int integer11 = Math.min(integer1 - 3, 3) + integer9 / 2 - 1;
        for (int integer12 = -integer10; integer12 < integer10; ++integer12) {
            for (int integer13 = -integer10; integer13 < integer10; ++integer13) {
                final double double7 = this.signedDistanceEllipse(integer12, integer13, ew7, integer10, integer11, double6);
                if (double7 < 0.0) {
                    final BlockPos ew8 = ew3.offset(integer12, integer2, integer13);
                    final Block bmv19 = bhs.getBlockState(ew8).getBlock();
                    if (this.isIcebergBlock(bmv19) || bmv19 == Blocks.SNOW_BLOCK) {
                        if (boolean5) {
                            this.setBlock(bhs, ew8, Blocks.WATER.defaultBlockState());
                        }
                        else {
                            this.setBlock(bhs, ew8, Blocks.AIR.defaultBlockState());
                            this.removeFloatingSnowLayer(bhs, ew8);
                        }
                    }
                }
            }
        }
    }
    
    private void removeFloatingSnowLayer(final LevelAccessor bhs, final BlockPos ew) {
        if (bhs.getBlockState(ew.above()).getBlock() == Blocks.SNOW) {
            this.setBlock(bhs, ew.above(), Blocks.AIR.defaultBlockState());
        }
    }
    
    private void generateIcebergBlock(final LevelAccessor bhs, final Random random, final BlockPos ew, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9, final boolean boolean10, final int integer11, final double double12, final boolean boolean13, final BlockState bvt) {
        final double double13 = boolean10 ? this.signedDistanceEllipse(integer5, integer7, BlockPos.ZERO, integer9, this.getEllipseC(integer6, integer4, integer11), double12) : this.signedDistanceCircle(integer5, integer7, BlockPos.ZERO, integer8, random);
        if (double13 < 0.0) {
            final BlockPos ew2 = ew.offset(integer5, integer6, integer7);
            final double double14 = boolean10 ? -0.5 : (-6 - random.nextInt(3));
            if (double13 > double14 && random.nextDouble() > 0.9) {
                return;
            }
            this.setIcebergBlock(ew2, bhs, random, integer4 - integer6, integer4, boolean10, boolean13, bvt);
        }
    }
    
    private void setIcebergBlock(final BlockPos ew, final LevelAccessor bhs, final Random random, final int integer4, final int integer5, final boolean boolean6, final boolean boolean7, final BlockState bvt) {
        final BlockState bvt2 = bhs.getBlockState(ew);
        final Block bmv11 = bvt2.getBlock();
        if (bvt2.getMaterial() == Material.AIR || bmv11 == Blocks.SNOW_BLOCK || bmv11 == Blocks.ICE || bmv11 == Blocks.WATER) {
            final boolean boolean8 = !boolean6 || random.nextDouble() > 0.05;
            final int integer6 = boolean6 ? 3 : 2;
            if (boolean7 && bmv11 != Blocks.WATER && integer4 <= random.nextInt(Math.max(1, integer5 / integer6)) + integer5 * 0.6 && boolean8) {
                this.setBlock(bhs, ew, Blocks.SNOW_BLOCK.defaultBlockState());
            }
            else {
                this.setBlock(bhs, ew, bvt);
            }
        }
    }
    
    private int getEllipseC(final int integer1, final int integer2, final int integer3) {
        int integer4 = integer3;
        if (integer1 > 0 && integer2 - integer1 <= 3) {
            integer4 -= 4 - (integer2 - integer1);
        }
        return integer4;
    }
    
    private double signedDistanceCircle(final int integer1, final int integer2, final BlockPos ew, final int integer4, final Random random) {
        final float float7 = 10.0f * Mth.clamp(random.nextFloat(), 0.2f, 0.8f) / integer4;
        return float7 + Math.pow((double)(integer1 - ew.getX()), 2.0) + Math.pow((double)(integer2 - ew.getZ()), 2.0) - Math.pow((double)integer4, 2.0);
    }
    
    private double signedDistanceEllipse(final int integer1, final int integer2, final BlockPos ew, final int integer4, final int integer5, final double double6) {
        return Math.pow(((integer1 - ew.getX()) * Math.cos(double6) - (integer2 - ew.getZ()) * Math.sin(double6)) / integer4, 2.0) + Math.pow(((integer1 - ew.getX()) * Math.sin(double6) + (integer2 - ew.getZ()) * Math.cos(double6)) / integer5, 2.0) - 1.0;
    }
    
    private int heightDependentRadiusRound(final Random random, final int integer2, final int integer3, final int integer4) {
        final float float6 = 3.5f - random.nextFloat();
        float float7 = (1.0f - (float)Math.pow((double)integer2, 2.0) / (integer3 * float6)) * integer4;
        if (integer3 > 15 + random.nextInt(5)) {
            final int integer5 = (integer2 < 3 + random.nextInt(6)) ? (integer2 / 2) : integer2;
            float7 = (1.0f - integer5 / (integer3 * float6 * 0.4f)) * integer4;
        }
        return Mth.ceil(float7 / 2.0f);
    }
    
    private int heightDependentRadiusEllipse(final int integer1, final int integer2, final int integer3) {
        final float float5 = 1.0f;
        final float float6 = (1.0f - (float)Math.pow((double)integer1, 2.0) / (integer2 * 1.0f)) * integer3;
        return Mth.ceil(float6 / 2.0f);
    }
    
    private int heightDependentRadiusSteep(final Random random, final int integer2, final int integer3, final int integer4) {
        final float float6 = 1.0f + random.nextFloat() / 2.0f;
        final float float7 = (1.0f - integer2 / (integer3 * float6)) * integer4;
        return Mth.ceil(float7 / 2.0f);
    }
    
    private boolean isIcebergBlock(final Block bmv) {
        return bmv == Blocks.PACKED_ICE || bmv == Blocks.SNOW_BLOCK || bmv == Blocks.BLUE_ICE;
    }
    
    private boolean belowIsAir(final BlockGetter bhb, final BlockPos ew) {
        return bhb.getBlockState(ew.below()).getMaterial() == Material.AIR;
    }
    
    private void smooth(final LevelAccessor bhs, final BlockPos ew, final int integer3, final int integer4, final boolean boolean5, final int integer6) {
        for (int integer7 = boolean5 ? integer6 : (integer3 / 2), integer8 = -integer7; integer8 <= integer7; ++integer8) {
            for (int integer9 = -integer7; integer9 <= integer7; ++integer9) {
                for (int integer10 = 0; integer10 <= integer4; ++integer10) {
                    final BlockPos ew2 = ew.offset(integer8, integer10, integer9);
                    final Block bmv13 = bhs.getBlockState(ew2).getBlock();
                    if (this.isIcebergBlock(bmv13) || bmv13 == Blocks.SNOW) {
                        if (this.belowIsAir(bhs, ew2)) {
                            this.setBlock(bhs, ew2, Blocks.AIR.defaultBlockState());
                            this.setBlock(bhs, ew2.above(), Blocks.AIR.defaultBlockState());
                        }
                        else if (this.isIcebergBlock(bmv13)) {
                            final Block[] arr14 = { bhs.getBlockState(ew2.west()).getBlock(), bhs.getBlockState(ew2.east()).getBlock(), bhs.getBlockState(ew2.north()).getBlock(), bhs.getBlockState(ew2.south()).getBlock() };
                            int integer11 = 0;
                            for (final Block bmv14 : arr14) {
                                if (!this.isIcebergBlock(bmv14)) {
                                    ++integer11;
                                }
                            }
                            if (integer11 >= 3) {
                                this.setBlock(bhs, ew2, Blocks.AIR.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
    }
}
