package net.minecraft.util.profiling;

import org.apache.logging.log4j.LogManager;
import java.time.Duration;
import java.util.Map;
import net.minecraft.Util;
import org.apache.logging.log4j.util.Supplier;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import com.google.common.collect.Lists;
import java.util.function.IntSupplier;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class ActiveProfiler implements ProfileCollector {
    private static final long WARNING_TIME_NANOS;
    private static final Logger LOGGER;
    private final List<String> paths;
    private final LongList startTimes;
    private final Object2LongMap<String> times;
    private final Object2LongMap<String> counts;
    private final IntSupplier getTickTime;
    private final long startTimeNano;
    private final int startTimeTicks;
    private String path;
    private boolean started;
    
    public ActiveProfiler(final long long1, final IntSupplier intSupplier) {
        this.paths = (List<String>)Lists.newArrayList();
        this.startTimes = (LongList)new LongArrayList();
        this.times = (Object2LongMap<String>)new Object2LongOpenHashMap();
        this.counts = (Object2LongMap<String>)new Object2LongOpenHashMap();
        this.path = "";
        this.startTimeNano = long1;
        this.startTimeTicks = intSupplier.getAsInt();
        this.getTickTime = intSupplier;
    }
    
    public void startTick() {
        if (this.started) {
            ActiveProfiler.LOGGER.error("Profiler tick already started - missing endTick()?");
            return;
        }
        this.started = true;
        this.path = "";
        this.paths.clear();
        this.push("root");
    }
    
    public void endTick() {
        if (!this.started) {
            ActiveProfiler.LOGGER.error("Profiler tick already ended - missing startTick()?");
            return;
        }
        this.pop();
        this.started = false;
        if (!this.path.isEmpty()) {
            ActiveProfiler.LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", new Supplier[] { () -> ProfileResults.demanglePath(this.path) });
        }
    }
    
    public void push(final String string) {
        if (!this.started) {
            ActiveProfiler.LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", string);
            return;
        }
        if (!this.path.isEmpty()) {
            this.path += '\u001e';
        }
        this.path += string;
        this.paths.add(this.path);
        this.startTimes.add(Util.getNanos());
    }
    
    public void push(final java.util.function.Supplier<String> supplier) {
        this.push((String)supplier.get());
    }
    
    public void pop() {
        if (!this.started) {
            ActiveProfiler.LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
            return;
        }
        if (this.startTimes.isEmpty()) {
            ActiveProfiler.LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
            return;
        }
        final long long2 = Util.getNanos();
        final long long3 = this.startTimes.removeLong(this.startTimes.size() - 1);
        this.paths.remove(this.paths.size() - 1);
        final long long4 = long2 - long3;
        this.times.put(this.path, this.times.getLong(this.path) + long4);
        this.counts.put(this.path, this.counts.getLong(this.path) + 1L);
        if (long4 > ActiveProfiler.WARNING_TIME_NANOS) {
            ActiveProfiler.LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", new Supplier[] { () -> ProfileResults.demanglePath(this.path), () -> long4 / 1000000.0 });
        }
        this.path = (this.paths.isEmpty() ? "" : ((String)this.paths.get(this.paths.size() - 1)));
    }
    
    public void popPush(final String string) {
        this.pop();
        this.push(string);
    }
    
    public void popPush(final java.util.function.Supplier<String> supplier) {
        this.pop();
        this.push(supplier);
    }
    
    public ProfileResults getResults() {
        return new FilledProfileResults((Map<String, Long>)this.times, (Map<String, Long>)this.counts, this.startTimeNano, this.startTimeTicks, Util.getNanos(), this.getTickTime.getAsInt());
    }
    
    static {
        WARNING_TIME_NANOS = Duration.ofMillis(100L).toNanos();
        LOGGER = LogManager.getLogger();
    }
}
