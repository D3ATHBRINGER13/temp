package net.minecraft.world.level.timers;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Maps;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Map;
import com.google.common.primitives.UnsignedLong;
import java.util.Queue;
import org.apache.logging.log4j.Logger;

public class TimerQueue<T> {
    private static final Logger LOGGER;
    private final TimerCallbacks<T> callbacksRegistry;
    private final Queue<Event<T>> queue;
    private UnsignedLong sequentialId;
    private final Map<String, Event<T>> events;
    
    private static <T> Comparator<Event<T>> createComparator() {
        return (Comparator<Event<T>>)((a1, a2) -> {
            final int integer3 = Long.compare(a1.triggerTime, a2.triggerTime);
            if (integer3 != 0) {
                return integer3;
            }
            return a1.sequentialId.compareTo(a2.sequentialId);
        });
    }
    
    public TimerQueue(final TimerCallbacks<T> cry) {
        this.queue = (Queue<Event<T>>)new PriorityQueue((Comparator)TimerQueue.createComparator());
        this.sequentialId = UnsignedLong.ZERO;
        this.events = (Map<String, Event<T>>)Maps.newHashMap();
        this.callbacksRegistry = cry;
    }
    
    public void tick(final T object, final long long2) {
        while (true) {
            final Event<T> a5 = (Event<T>)this.queue.peek();
            if (a5 == null || a5.triggerTime > long2) {
                break;
            }
            this.queue.remove();
            this.events.remove(a5.id);
            a5.callback.handle(object, this, long2);
        }
    }
    
    private void addEvent(final String string, final long long2, final TimerCallback<T> crx) {
        this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
        final Event<T> a6 = new Event<T>(long2, this.sequentialId, string, (TimerCallback)crx);
        this.events.put(string, a6);
        this.queue.add(a6);
    }
    
    public boolean schedule(final String string, final long long2, final TimerCallback<T> crx) {
        if (this.events.containsKey(string)) {
            return false;
        }
        this.addEvent(string, long2, crx);
        return true;
    }
    
    public void reschedule(final String string, final long long2, final TimerCallback<T> crx) {
        final Event<T> a6 = (Event<T>)this.events.remove(string);
        if (a6 != null) {
            this.queue.remove(a6);
        }
        this.addEvent(string, long2, crx);
    }
    
    private void loadEvent(final CompoundTag id) {
        final CompoundTag id2 = id.getCompound("Callback");
        final TimerCallback<T> crx4 = this.callbacksRegistry.deserialize(id2);
        if (crx4 != null) {
            final String string5 = id.getString("Name");
            final long long6 = id.getLong("TriggerTime");
            this.schedule(string5, long6, crx4);
        }
    }
    
    public void load(final ListTag ik) {
        this.queue.clear();
        this.events.clear();
        this.sequentialId = UnsignedLong.ZERO;
        if (ik.isEmpty()) {
            return;
        }
        if (ik.getElementType() != 10) {
            TimerQueue.LOGGER.warn(new StringBuilder().append("Invalid format of events: ").append(ik).toString());
            return;
        }
        for (final Tag iu4 : ik) {
            this.loadEvent((CompoundTag)iu4);
        }
    }
    
    private CompoundTag storeEvent(final Event<T> a) {
        final CompoundTag id3 = new CompoundTag();
        id3.putString("Name", a.id);
        id3.putLong("TriggerTime", a.triggerTime);
        id3.put("Callback", (Tag)this.callbacksRegistry.<TimerCallback<T>>serialize(a.callback));
        return id3;
    }
    
    public ListTag store() {
        final ListTag ik2 = new ListTag();
        this.queue.stream().sorted((Comparator)TimerQueue.createComparator()).map(this::storeEvent).forEach(ik2::add);
        return ik2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Event<T> {
        public final long triggerTime;
        public final UnsignedLong sequentialId;
        public final String id;
        public final TimerCallback<T> callback;
        
        private Event(final long long1, final UnsignedLong unsignedLong, final String string, final TimerCallback<T> crx) {
            this.triggerTime = long1;
            this.sequentialId = unsignedLong;
            this.id = string;
            this.callback = crx;
        }
    }
}
