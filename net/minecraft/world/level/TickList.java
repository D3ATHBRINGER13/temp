package net.minecraft.world.level;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public interface TickList<T> {
    boolean hasScheduledTick(final BlockPos ew, final T object);
    
    default void scheduleTick(final BlockPos ew, final T object, final int integer) {
        this.scheduleTick(ew, object, integer, TickPriority.NORMAL);
    }
    
    void scheduleTick(final BlockPos ew, final T object, final int integer, final TickPriority bii);
    
    boolean willTickThisTick(final BlockPos ew, final T object);
    
    void addAll(final Stream<TickNextTickData<T>> stream);
}
