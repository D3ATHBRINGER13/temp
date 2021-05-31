package net.minecraft.world.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.Vec3i;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Collection;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import java.util.Iterator;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.ReportedException;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReport;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Comparator;
import com.google.common.collect.Sets;
import java.util.function.Consumer;
import java.util.List;
import java.util.Queue;
import net.minecraft.server.level.ServerLevel;
import java.util.TreeSet;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import java.util.function.Predicate;

public class ServerTickList<T> implements TickList<T> {
    protected final Predicate<T> ignore;
    private final Function<T, ResourceLocation> toId;
    private final Function<ResourceLocation, T> fromId;
    private final Set<TickNextTickData<T>> tickNextTickSet;
    private final TreeSet<TickNextTickData<T>> tickNextTickList;
    private final ServerLevel level;
    private final Queue<TickNextTickData<T>> currentlyTicking;
    private final List<TickNextTickData<T>> alreadyTicked;
    private final Consumer<TickNextTickData<T>> ticker;
    
    public ServerTickList(final ServerLevel vk, final Predicate<T> predicate, final Function<T, ResourceLocation> function3, final Function<ResourceLocation, T> function4, final Consumer<TickNextTickData<T>> consumer) {
        this.tickNextTickSet = (Set<TickNextTickData<T>>)Sets.newHashSet();
        this.tickNextTickList = (TreeSet<TickNextTickData<T>>)Sets.newTreeSet((Comparator)TickNextTickData.createTimeComparator());
        this.currentlyTicking = (Queue<TickNextTickData<T>>)Queues.newArrayDeque();
        this.alreadyTicked = (List<TickNextTickData<T>>)Lists.newArrayList();
        this.ignore = predicate;
        this.toId = function3;
        this.fromId = function4;
        this.level = vk;
        this.ticker = consumer;
    }
    
    public void tick() {
        int integer2 = this.tickNextTickList.size();
        if (integer2 != this.tickNextTickSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        if (integer2 > 65536) {
            integer2 = 65536;
        }
        final ServerChunkCache vi3 = this.level.getChunkSource();
        final Iterator<TickNextTickData<T>> iterator4 = (Iterator<TickNextTickData<T>>)this.tickNextTickList.iterator();
        this.level.getProfiler().push("cleaning");
        while (integer2 > 0 && iterator4.hasNext()) {
            final TickNextTickData<T> bih5 = (TickNextTickData<T>)iterator4.next();
            if (bih5.delay > this.level.getGameTime()) {
                break;
            }
            if (!vi3.isTickingChunk(bih5.pos)) {
                continue;
            }
            iterator4.remove();
            this.tickNextTickSet.remove(bih5);
            this.currentlyTicking.add(bih5);
            --integer2;
        }
        this.level.getProfiler().popPush("ticking");
        TickNextTickData<T> bih5;
        while ((bih5 = (TickNextTickData<T>)this.currentlyTicking.poll()) != null) {
            if (vi3.isTickingChunk(bih5.pos)) {
                try {
                    this.alreadyTicked.add(bih5);
                    this.ticker.accept(bih5);
                    continue;
                }
                catch (Throwable throwable6) {
                    final CrashReport d7 = CrashReport.forThrowable(throwable6, "Exception while ticking");
                    final CrashReportCategory e8 = d7.addCategory("Block being ticked");
                    CrashReportCategory.populateBlockDetails(e8, bih5.pos, null);
                    throw new ReportedException(d7);
                }
            }
            this.scheduleTick(bih5.pos, bih5.getType(), 0);
        }
        this.level.getProfiler().pop();
        this.alreadyTicked.clear();
        this.currentlyTicking.clear();
    }
    
    public boolean willTickThisTick(final BlockPos ew, final T object) {
        return this.currentlyTicking.contains(new TickNextTickData(ew, object));
    }
    
    public void addAll(final Stream<TickNextTickData<T>> stream) {
        stream.forEach(this::addTickData);
    }
    
    public List<TickNextTickData<T>> fetchTicksInChunk(final ChunkPos bhd, final boolean boolean2, final boolean boolean3) {
        final int integer5 = (bhd.x << 4) - 2;
        final int integer6 = integer5 + 16 + 2;
        final int integer7 = (bhd.z << 4) - 2;
        final int integer8 = integer7 + 16 + 2;
        return this.fetchTicksInArea(new BoundingBox(integer5, 0, integer7, integer6, 256, integer8), boolean2, boolean3);
    }
    
    public List<TickNextTickData<T>> fetchTicksInArea(final BoundingBox cic, final boolean boolean2, final boolean boolean3) {
        List<TickNextTickData<T>> list5 = this.fetchTicksInArea(null, (Collection<TickNextTickData<T>>)this.tickNextTickList, cic, boolean2);
        if (boolean2 && list5 != null) {
            this.tickNextTickSet.removeAll((Collection)list5);
        }
        list5 = this.fetchTicksInArea(list5, (Collection<TickNextTickData<T>>)this.currentlyTicking, cic, boolean2);
        if (!boolean3) {
            list5 = this.fetchTicksInArea(list5, (Collection<TickNextTickData<T>>)this.alreadyTicked, cic, boolean2);
        }
        return (List<TickNextTickData<T>>)((list5 == null) ? Collections.emptyList() : list5);
    }
    
    @Nullable
    private List<TickNextTickData<T>> fetchTicksInArea(@Nullable List<TickNextTickData<T>> list, final Collection<TickNextTickData<T>> collection, final BoundingBox cic, final boolean boolean4) {
        final Iterator<TickNextTickData<T>> iterator6 = (Iterator<TickNextTickData<T>>)collection.iterator();
        while (iterator6.hasNext()) {
            final TickNextTickData<T> bih7 = (TickNextTickData<T>)iterator6.next();
            final BlockPos ew8 = bih7.pos;
            if (ew8.getX() >= cic.x0 && ew8.getX() < cic.x1 && ew8.getZ() >= cic.z0 && ew8.getZ() < cic.z1) {
                if (boolean4) {
                    iterator6.remove();
                }
                if (list == null) {
                    list = (List<TickNextTickData<T>>)Lists.newArrayList();
                }
                list.add(bih7);
            }
        }
        return list;
    }
    
    public void copy(final BoundingBox cic, final BlockPos ew) {
        final List<TickNextTickData<T>> list4 = this.fetchTicksInArea(cic, false, false);
        for (final TickNextTickData<T> bih6 : list4) {
            if (cic.isInside(bih6.pos)) {
                final BlockPos ew2 = bih6.pos.offset(ew);
                final T object8 = bih6.getType();
                this.addTickData(new TickNextTickData<T>(ew2, object8, bih6.delay, bih6.priority));
            }
        }
    }
    
    public ListTag save(final ChunkPos bhd) {
        final List<TickNextTickData<T>> list3 = this.fetchTicksInChunk(bhd, false, true);
        return ServerTickList.<T>saveTickList(this.toId, (java.lang.Iterable<TickNextTickData<T>>)list3, this.level.getGameTime());
    }
    
    public static <T> ListTag saveTickList(final Function<T, ResourceLocation> function, final Iterable<TickNextTickData<T>> iterable, final long long3) {
        final ListTag ik5 = new ListTag();
        for (final TickNextTickData<T> bih7 : iterable) {
            final CompoundTag id8 = new CompoundTag();
            id8.putString("i", ((ResourceLocation)function.apply(bih7.getType())).toString());
            id8.putInt("x", bih7.pos.getX());
            id8.putInt("y", bih7.pos.getY());
            id8.putInt("z", bih7.pos.getZ());
            id8.putInt("t", (int)(bih7.delay - long3));
            id8.putInt("p", bih7.priority.getValue());
            ik5.add(id8);
        }
        return ik5;
    }
    
    public boolean hasScheduledTick(final BlockPos ew, final T object) {
        return this.tickNextTickSet.contains(new TickNextTickData(ew, object));
    }
    
    public void scheduleTick(final BlockPos ew, final T object, final int integer, final TickPriority bii) {
        if (!this.ignore.test(object)) {
            this.addTickData(new TickNextTickData<T>(ew, object, integer + this.level.getGameTime(), bii));
        }
    }
    
    private void addTickData(final TickNextTickData<T> bih) {
        if (!this.tickNextTickSet.contains(bih)) {
            this.tickNextTickSet.add(bih);
            this.tickNextTickList.add(bih);
        }
    }
    
    public int size() {
        return this.tickNextTickSet.size();
    }
}
