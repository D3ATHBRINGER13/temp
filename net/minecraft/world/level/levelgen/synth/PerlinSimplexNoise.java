package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

public class PerlinSimplexNoise implements SurfaceNoise {
    private final SimplexNoise[] noiseLevels;
    private final int levels;
    
    public PerlinSimplexNoise(final Random random, final int integer) {
        this.levels = integer;
        this.noiseLevels = new SimplexNoise[integer];
        for (int integer2 = 0; integer2 < integer; ++integer2) {
            this.noiseLevels[integer2] = new SimplexNoise(random);
        }
    }
    
    public double getValue(final double double1, final double double2) {
        return this.getValue(double1, double2, false);
    }
    
    public double getValue(final double double1, final double double2, final boolean boolean3) {
        double double3 = 0.0;
        double double4 = 1.0;
        for (int integer11 = 0; integer11 < this.levels; ++integer11) {
            double3 += this.noiseLevels[integer11].getValue(double1 * double4 + (boolean3 ? this.noiseLevels[integer11].xo : 0.0), double2 * double4 + (boolean3 ? this.noiseLevels[integer11].yo : 0.0)) / double4;
            double4 /= 2.0;
        }
        return double3;
    }
    
    public double getSurfaceNoiseValue(final double double1, final double double2, final double double3, final double double4) {
        return this.getValue(double1, double2, true) * 0.55;
    }
}
