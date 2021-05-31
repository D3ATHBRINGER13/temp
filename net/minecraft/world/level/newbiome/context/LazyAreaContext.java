package net.minecraft.world.level.newbiome.context;

import net.minecraft.world.level.newbiome.area.Area;
import java.util.Random;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.level.newbiome.area.LazyArea;

public class LazyAreaContext implements BigContext<LazyArea> {
    private final Long2IntLinkedOpenHashMap cache;
    private final int maxCache;
    protected long seedMixup;
    protected ImprovedNoise biomeNoise;
    private long seed;
    private long rval;
    
    public LazyAreaContext(final int integer, final long long2, final long long3) {
        this.seedMixup = long3;
        this.seedMixup *= this.seedMixup * 6364136223846793005L + 1442695040888963407L;
        this.seedMixup += long3;
        this.seedMixup *= this.seedMixup * 6364136223846793005L + 1442695040888963407L;
        this.seedMixup += long3;
        this.seedMixup *= this.seedMixup * 6364136223846793005L + 1442695040888963407L;
        this.seedMixup += long3;
        (this.cache = new Long2IntLinkedOpenHashMap(16, 0.25f)).defaultReturnValue(Integer.MIN_VALUE);
        this.maxCache = integer;
        this.init(long2);
    }
    
    public LazyArea createResult(final PixelTransformer cnj) {
        return new LazyArea(this.cache, this.maxCache, cnj);
    }
    
    public LazyArea createResult(final PixelTransformer cnj, final LazyArea clv) {
        return new LazyArea(this.cache, Math.min(1024, clv.getMaxCache() * 4), cnj);
    }
    
    public LazyArea createResult(final PixelTransformer cnj, final LazyArea clv2, final LazyArea clv3) {
        return new LazyArea(this.cache, Math.min(1024, Math.max(clv2.getMaxCache(), clv3.getMaxCache()) * 4), cnj);
    }
    
    public void init(final long long1) {
        this.seed = long1;
        this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
        this.seed += this.seedMixup;
        this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
        this.seed += this.seedMixup;
        this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
        this.seed += this.seedMixup;
        this.biomeNoise = new ImprovedNoise(new Random(long1));
    }
    
    public void initRandom(final long long1, final long long2) {
        this.rval = this.seed;
        this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
        this.rval += long1;
        this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
        this.rval += long2;
        this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
        this.rval += long1;
        this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
        this.rval += long2;
    }
    
    public int nextRandom(final int integer) {
        int integer2 = (int)((this.rval >> 24) % integer);
        if (integer2 < 0) {
            integer2 += integer;
        }
        this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
        this.rval += this.seed;
        return integer2;
    }
    
    public ImprovedNoise getBiomeNoise() {
        return this.biomeNoise;
    }
}
