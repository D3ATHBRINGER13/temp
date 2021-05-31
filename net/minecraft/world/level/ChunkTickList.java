package net.minecraft.world.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import com.google.common.collect.Sets;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import java.util.Set;

public class ChunkTickList<T> implements TickList<T> {
    private final Set<TickNextTickData<T>> ticks;
    private final Function<T, ResourceLocation> toId;
    
    public ChunkTickList(final Function<T, ResourceLocation> function, final List<TickNextTickData<T>> list) {
        this(function, (Set)Sets.newHashSet((Iterable)list));
    }
    
    private ChunkTickList(final Function<T, ResourceLocation> function, final Set<TickNextTickData<T>> set) {
        this.ticks = set;
        this.toId = function;
    }
    
    public boolean hasScheduledTick(final BlockPos ew, final T object) {
        return false;
    }
    
    public void scheduleTick(final BlockPos ew, final T object, final int integer, final TickPriority bii) {
        this.ticks.add(new TickNextTickData(ew, object, integer, bii));
    }
    
    public boolean willTickThisTick(final BlockPos ew, final T object) {
        return false;
    }
    
    public void addAll(final Stream<TickNextTickData<T>> stream) {
        stream.forEach(this.ticks::add);
    }
    
    public Stream<TickNextTickData<T>> ticks() {
        return (Stream<TickNextTickData<T>>)this.ticks.stream();
    }
    
    public ListTag save(final long long1) {
        return ServerTickList.<T>saveTickList(this.toId, (java.lang.Iterable<TickNextTickData<T>>)this.ticks, long1);
    }
    
    public static <T> ChunkTickList<T> create(final ListTag ik, final Function<T, ResourceLocation> function2, final Function<ResourceLocation, T> function3) {
        final Set<TickNextTickData<T>> set4 = (Set<TickNextTickData<T>>)Sets.newHashSet();
        for (int integer5 = 0; integer5 < ik.size(); ++integer5) {
            final CompoundTag id6 = ik.getCompound(integer5);
            final T object7 = (T)function3.apply(new ResourceLocation(id6.getString("i")));
            if (object7 != null) {
                set4.add(new TickNextTickData(new BlockPos(id6.getInt("x"), id6.getInt("y"), id6.getInt("z")), object7, id6.getInt("t"), TickPriority.byValue(id6.getInt("p"))));
            }
        }
        return new ChunkTickList<T>(function2, set4);
    }
}
