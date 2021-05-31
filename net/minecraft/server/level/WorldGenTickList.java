package net.minecraft.server.level;

import net.minecraft.world.level.TickNextTickData;
import java.util.stream.Stream;
import net.minecraft.world.level.TickPriority;
import net.minecraft.core.BlockPos;
import java.util.function.Function;
import net.minecraft.world.level.TickList;

public class WorldGenTickList<T> implements TickList<T> {
    private final Function<BlockPos, TickList<T>> index;
    
    public WorldGenTickList(final Function<BlockPos, TickList<T>> function) {
        this.index = function;
    }
    
    public boolean hasScheduledTick(final BlockPos ew, final T object) {
        return ((TickList)this.index.apply(ew)).hasScheduledTick(ew, object);
    }
    
    public void scheduleTick(final BlockPos ew, final T object, final int integer, final TickPriority bii) {
        ((TickList)this.index.apply(ew)).scheduleTick(ew, object, integer, bii);
    }
    
    public boolean willTickThisTick(final BlockPos ew, final T object) {
        return false;
    }
    
    public void addAll(final Stream<TickNextTickData<T>> stream) {
        stream.forEach(bih -> ((TickList)this.index.apply(bih.pos)).addAll(Stream.of(bih)));
    }
}
