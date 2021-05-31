package net.minecraft.world.level.levelgen.carver;

import java.util.BitSet;
import net.minecraft.world.level.chunk.ChunkAccess;
import java.util.Random;

public class ConfiguredWorldCarver<WC extends CarverConfiguration> {
    public final WorldCarver<WC> worldCarver;
    public final WC config;
    
    public ConfiguredWorldCarver(final WorldCarver<WC> bzt, final WC bzm) {
        this.worldCarver = bzt;
        this.config = bzm;
    }
    
    public boolean isStartChunk(final Random random, final int integer2, final int integer3) {
        return this.worldCarver.isStartChunk(random, integer2, integer3, this.config);
    }
    
    public boolean carve(final ChunkAccess bxh, final Random random, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final BitSet bitSet) {
        return this.worldCarver.carve(bxh, random, integer3, integer4, integer5, integer6, integer7, bitSet, this.config);
    }
}
