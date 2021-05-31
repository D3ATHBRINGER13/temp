package net.minecraft.world.level.levelgen.carver;

import net.minecraft.util.Mth;
import java.util.BitSet;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;

public class CanyonWorldCarver extends WorldCarver<ProbabilityFeatureConfiguration> {
    private final float[] rs;
    
    public CanyonWorldCarver(final Function<Dynamic<?>, ? extends ProbabilityFeatureConfiguration> function) {
        super(function, 256);
        this.rs = new float[1024];
    }
    
    @Override
    public boolean isStartChunk(final Random random, final int integer2, final int integer3, final ProbabilityFeatureConfiguration cdn) {
        return random.nextFloat() <= cdn.probability;
    }
    
    @Override
    public boolean carve(final ChunkAccess bxh, final Random random, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final BitSet bitSet, final ProbabilityFeatureConfiguration cdn) {
        final int integer8 = (this.getRange() * 2 - 1) * 16;
        final double double12 = integer4 * 16 + random.nextInt(16);
        final double double13 = random.nextInt(random.nextInt(40) + 8) + 20;
        final double double14 = integer5 * 16 + random.nextInt(16);
        final float float18 = random.nextFloat() * 6.2831855f;
        final float float19 = (random.nextFloat() - 0.5f) * 2.0f / 8.0f;
        final double double15 = 3.0;
        final float float20 = (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f;
        final int integer9 = integer8 - random.nextInt(integer8 / 4);
        final int integer10 = 0;
        this.genCanyon(bxh, random.nextLong(), integer3, integer6, integer7, double12, double13, double14, float20, float18, float19, 0, integer9, 3.0, bitSet);
        return true;
    }
    
    private void genCanyon(final ChunkAccess bxh, final long long2, final int integer3, final int integer4, final int integer5, double double6, double double7, double double8, final float float9, float float10, float float11, final int integer12, final int integer13, final double double14, final BitSet bitSet) {
        final Random random22 = new Random(long2);
        float float12 = 1.0f;
        for (int integer14 = 0; integer14 < 256; ++integer14) {
            if (integer14 == 0 || random22.nextInt(3) == 0) {
                float12 = 1.0f + random22.nextFloat() * random22.nextFloat();
            }
            this.rs[integer14] = float12 * float12;
        }
        float float13 = 0.0f;
        float float14 = 0.0f;
        for (int integer15 = integer12; integer15 < integer13; ++integer15) {
            double double15 = 1.5 + Mth.sin(integer15 * 3.1415927f / integer13) * float9;
            double double16 = double15 * double14;
            double15 *= random22.nextFloat() * 0.25 + 0.75;
            double16 *= random22.nextFloat() * 0.25 + 0.75;
            final float float15 = Mth.cos(float11);
            final float float16 = Mth.sin(float11);
            double6 += Mth.cos(float10) * float15;
            double7 += float16;
            double8 += Mth.sin(float10) * float15;
            float11 *= 0.7f;
            float11 += float14 * 0.05f;
            float10 += float13 * 0.05f;
            float14 *= 0.8f;
            float13 *= 0.5f;
            float14 += (random22.nextFloat() - random22.nextFloat()) * random22.nextFloat() * 2.0f;
            float13 += (random22.nextFloat() - random22.nextFloat()) * random22.nextFloat() * 4.0f;
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
        return (double1 * double1 + double3 * double3) * this.rs[integer - 1] + double2 * double2 / 6.0 >= 1.0;
    }
}
