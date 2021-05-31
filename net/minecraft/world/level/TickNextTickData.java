package net.minecraft.world.level;

import java.util.Comparator;
import net.minecraft.core.BlockPos;

public class TickNextTickData<T> {
    private static long counter;
    private final T type;
    public final BlockPos pos;
    public final long delay;
    public final TickPriority priority;
    private final long c;
    
    public TickNextTickData(final BlockPos ew, final T object) {
        this(ew, object, 0L, TickPriority.NORMAL);
    }
    
    public TickNextTickData(final BlockPos ew, final T object, final long long3, final TickPriority bii) {
        this.c = TickNextTickData.counter++;
        this.pos = ew.immutable();
        this.type = object;
        this.delay = long3;
        this.priority = bii;
    }
    
    public boolean equals(final Object object) {
        if (object instanceof TickNextTickData) {
            final TickNextTickData<?> bih3 = object;
            return this.pos.equals(bih3.pos) && this.type == bih3.type;
        }
        return false;
    }
    
    public int hashCode() {
        return this.pos.hashCode();
    }
    
    public static <T> Comparator<TickNextTickData<T>> createTimeComparator() {
        return (Comparator<TickNextTickData<T>>)((bih1, bih2) -> {
            int integer3 = Long.compare(bih1.delay, bih2.delay);
            if (integer3 != 0) {
                return integer3;
            }
            integer3 = bih1.priority.compareTo((Enum)bih2.priority);
            if (integer3 != 0) {
                return integer3;
            }
            return Long.compare(bih1.c, bih2.c);
        });
    }
    
    public String toString() {
        return new StringBuilder().append(this.type).append(": ").append(this.pos).append(", ").append(this.delay).append(", ").append(this.priority).append(", ").append(this.c).toString();
    }
    
    public T getType() {
        return this.type;
    }
}
