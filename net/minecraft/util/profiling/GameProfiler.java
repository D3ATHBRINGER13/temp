package net.minecraft.util.profiling;

import net.minecraft.Util;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import java.util.function.Supplier;
import java.util.function.IntSupplier;
import org.apache.logging.log4j.Logger;

public class GameProfiler implements ProfilerFiller {
    private static final Logger LOGGER;
    private static final long MAXIMUM_TICK_TIME_NANOS;
    private final IntSupplier getTickTime;
    private final ProfilerImpl continuous;
    private final ProfilerImpl perTick;
    
    public GameProfiler(final IntSupplier intSupplier) {
        this.continuous = new ProfilerImpl();
        this.perTick = new ProfilerImpl();
        this.getTickTime = intSupplier;
    }
    
    public Profiler continuous() {
        return this.continuous;
    }
    
    public void startTick() {
        this.continuous.collector.startTick();
        this.perTick.collector.startTick();
    }
    
    public void endTick() {
        this.continuous.collector.endTick();
        this.perTick.collector.endTick();
    }
    
    public void push(final String string) {
        this.continuous.collector.push(string);
        this.perTick.collector.push(string);
    }
    
    public void push(final Supplier<String> supplier) {
        this.continuous.collector.push(supplier);
        this.perTick.collector.push(supplier);
    }
    
    public void pop() {
        this.continuous.collector.pop();
        this.perTick.collector.pop();
    }
    
    public void popPush(final String string) {
        this.continuous.collector.popPush(string);
        this.perTick.collector.popPush(string);
    }
    
    public void popPush(final Supplier<String> supplier) {
        this.continuous.collector.popPush(supplier);
        this.perTick.collector.popPush(supplier);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MAXIMUM_TICK_TIME_NANOS = Duration.ofMillis(300L).toNanos();
    }
    
    class ProfilerImpl implements Profiler {
        protected ProfileCollector collector;
        
        private ProfilerImpl() {
            this.collector = InactiveProfiler.INACTIVE;
        }
        
        public boolean isEnabled() {
            return this.collector != InactiveProfiler.INACTIVE;
        }
        
        public ProfileResults disable() {
            final ProfileResults agm2 = this.collector.getResults();
            this.collector = InactiveProfiler.INACTIVE;
            return agm2;
        }
        
        public ProfileResults getResults() {
            return this.collector.getResults();
        }
        
        public void enable() {
            if (this.collector == InactiveProfiler.INACTIVE) {
                this.collector = new ActiveProfiler(Util.getNanos(), GameProfiler.this.getTickTime);
            }
        }
    }
    
    public interface Profiler {
        boolean isEnabled();
        
        ProfileResults disable();
        
        ProfileResults getResults();
        
        void enable();
    }
}
