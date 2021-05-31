package net.minecraft.world.level.levelgen.carver;

import net.minecraft.util.Mth;
import java.util.BitSet;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;

public class CaveWorldCarver extends WorldCarver<ProbabilityFeatureConfiguration> {
    public CaveWorldCarver(final Function<Dynamic<?>, ? extends ProbabilityFeatureConfiguration> function, final int integer) {
        super(function, integer);
    }
    
    @Override
    public boolean isStartChunk(final Random random, final int integer2, final int integer3, final ProbabilityFeatureConfiguration cdn) {
        return random.nextFloat() <= cdn.probability;
    }
    
    @Override
    public boolean carve(final ChunkAccess bxh, final Random random, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final BitSet bitSet, final ProbabilityFeatureConfiguration cdn) {
        final int integer8 = (this.getRange() * 2 - 1) * 16;
        for (int integer9 = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1), integer10 = 0; integer10 < integer9; ++integer10) {
            final double double14 = integer4 * 16 + random.nextInt(16);
            final double double15 = this.getCaveY(random);
            final double double16 = integer5 * 16 + random.nextInt(16);
            int integer11 = 1;
            if (random.nextInt(4) == 0) {
                final double double17 = 0.5;
                final float float23 = 1.0f + random.nextFloat() * 6.0f;
                this.genRoom(bxh, random.nextLong(), integer3, integer6, integer7, double14, double15, double16, float23, 0.5, bitSet);
                integer11 += random.nextInt(4);
            }
            for (int integer12 = 0; integer12 < integer11; ++integer12) {
                final float float24 = random.nextFloat() * 6.2831855f;
                final float float23 = (random.nextFloat() - 0.5f) / 4.0f;
                final float float25 = this.getThickness(random);
                final int integer13 = integer8 - random.nextInt(integer8 / 4);
                final int integer14 = 0;
                this.genTunnel(bxh, random.nextLong(), integer3, integer6, integer7, double14, double15, double16, float25, float24, float23, 0, integer13, this.getYScale(), bitSet);
            }
        }
        return true;
    }
    
    protected int getCaveBound() {
        return 15;
    }
    
    protected float getThickness(final Random random) {
        float float3 = random.nextFloat() * 2.0f + random.nextFloat();
        if (random.nextInt(10) == 0) {
            float3 *= random.nextFloat() * random.nextFloat() * 3.0f + 1.0f;
        }
        return float3;
    }
    
    protected double getYScale() {
        return 1.0;
    }
    
    protected int getCaveY(final Random random) {
        return random.nextInt(random.nextInt(120) + 8);
    }
    
    protected void genRoom(final ChunkAccess bxh, final long long2, final int integer3, final int integer4, final int integer5, final double double6, final double double7, final double double8, final float float9, final double double10, final BitSet bitSet) {
        final double double11 = 1.5 + Mth.sin(1.5707964f) * float9;
        final double double12 = double11 * double10;
        this.carveSphere(bxh, long2, integer3, integer4, integer5, double6 + 1.0, double7, double8, double11, double12, bitSet);
    }
    
    protected void genTunnel(final ChunkAccess bxh, final long long2, final int integer3, final int integer4, final int integer5, double double6, double double7, double double8, final float float9, float float10, float float11, final int integer12, final int integer13, final double double14, final BitSet bitSet) {
        final Random random22 = new Random(long2);
        final int integer14 = random22.nextInt(integer13 / 2) + integer13 / 4;
        final boolean boolean24 = random22.nextInt(6) == 0;
        float float12 = 0.0f;
        float float13 = 0.0f;
        for (int integer15 = integer12; integer15 < integer13; ++integer15) {
            final double double15 = 1.5 + Mth.sin(3.1415927f * integer15 / integer13) * float9;
            final double double16 = double15 * double14;
            final float float14 = Mth.cos(float11);
            double6 += Mth.cos(float10) * float14;
            double7 += Mth.sin(float11);
            double8 += Mth.sin(float10) * float14;
            float11 *= (boolean24 ? 0.92f : 0.7f);
            float11 += float13 * 0.1f;
            float10 += float12 * 0.1f;
            float13 *= 0.9f;
            float12 *= 0.75f;
            float13 += (random22.nextFloat() - random22.nextFloat()) * random22.nextFloat() * 2.0f;
            float12 += (random22.nextFloat() - random22.nextFloat()) * random22.nextFloat() * 4.0f;
            if (integer15 == integer14 && float9 > 1.0f) {
                this.genTunnel(bxh, random22.nextLong(), integer3, integer4, integer5, double6, double7, double8, random22.nextFloat() * 0.5f + 0.5f, float10 - 1.5707964f, float11 / 3.0f, integer15, integer13, 1.0, bitSet);
                this.genTunnel(bxh, random22.nextLong(), integer3, integer4, integer5, double6, double7, double8, random22.nextFloat() * 0.5f + 0.5f, float10 + 1.5707964f, float11 / 3.0f, integer15, integer13, 1.0, bitSet);
                return;
            }
            if (random22.nextInt(4) != 0) {
                if (!this.canReach(integer4, integer5, double6, double8, integer15, integer13, float9)) {
                    return;
                }
                this.carveSphere(bxh, long2, integer3, integer4, integer5, double6, double7, double8, double15, double16, bitSet);
            }
        }
    }
    
    @Override
    protected boolean skip(final double double1, final double double2, final double double3, final int integer) {
        return double2 <= -0.7 || double1 * double1 + double2 * double2 + double3 * double3 >= 1.0;
    }
}
