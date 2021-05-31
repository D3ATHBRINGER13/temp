package net.minecraft.world.level;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class EmptyTickList<T> implements TickList<T> {
    private static final EmptyTickList<Object> INSTANCE;
    
    public static <T> EmptyTickList<T> empty() {
        return (EmptyTickList<T>)EmptyTickList.INSTANCE;
    }
    
    public boolean hasScheduledTick(final BlockPos ew, final T object) {
        return false;
    }
    
    public void scheduleTick(final BlockPos ew, final T object, final int integer) {
    }
    
    public void scheduleTick(final BlockPos ew, final T object, final int integer, final TickPriority bii) {
    }
    
    public boolean willTickThisTick(final BlockPos ew, final T object) {
        return false;
    }
    
    public void addAll(final Stream<TickNextTickData<T>> stream) {
    }
    
    static {
        INSTANCE = new EmptyTickList<>();
    }
}
