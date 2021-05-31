package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.Mth;
import java.util.Random;

public class PerlinNoise implements SurfaceNoise {
    private final ImprovedNoise[] noiseLevels;
    
    public PerlinNoise(final Random random, final int integer) {
        this.noiseLevels = new ImprovedNoise[integer];
        for (int integer2 = 0; integer2 < integer; ++integer2) {
            this.noiseLevels[integer2] = new ImprovedNoise(random);
        }
    }
    
    public double getValue(final double double1, final double double2, final double double3) {
        return this.getValue(double1, double2, double3, 0.0, 0.0, false);
    }
    
    public double getValue(final double double1, final double double2, final double double3, final double double4, final double double5, final boolean boolean6) {
        double double6 = 0.0;
        double double7 = 1.0;
        for (final ImprovedNoise ckn20 : this.noiseLevels) {
            double6 += ckn20.noise(wrap(double1 * double7), boolean6 ? (-ckn20.yo) : wrap(double2 * double7), wrap(double3 * double7), double4 * double7, double5 * double7) / double7;
            double7 /= 2.0;
        }
        return double6;
    }
    
    public ImprovedNoise getOctaveNoise(final int integer) {
        return this.noiseLevels[integer];
    }
    
    public static double wrap(final double double1) {
        return double1 - Mth.lfloor(double1 / 3.3554432E7 + 0.5) * 3.3554432E7;
    }
    
    public double getSurfaceNoiseValue(final double double1, final double double2, final double double3, final double double4) {
        return this.getValue(double1, double2, 0.0, double3, double4, false);
    }
}
