package net.minecraft.stats;

import net.minecraft.world.entity.player.Player;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class StatsCounter {
    protected final Object2IntMap<Stat<?>> stats;
    
    public StatsCounter() {
        (this.stats = (Object2IntMap<Stat<?>>)Object2IntMaps.synchronize((Object2IntMap)new Object2IntOpenHashMap())).defaultReturnValue(0);
    }
    
    public void increment(final Player awg, final Stat<?> yv, final int integer) {
        this.setValue(awg, yv, this.getValue(yv) + integer);
    }
    
    public void setValue(final Player awg, final Stat<?> yv, final int integer) {
        this.stats.put(yv, integer);
    }
    
    public <T> int getValue(final StatType<T> yx, final T object) {
        return yx.contains(object) ? this.getValue(yx.get(object)) : 0;
    }
    
    public int getValue(final Stat<?> yv) {
        return this.stats.getInt(yv);
    }
}
