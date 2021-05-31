package net.minecraft.util.profiling;

import java.util.function.Supplier;

public class InactiveProfiler implements ProfileCollector {
    public static final InactiveProfiler INACTIVE;
    
    private InactiveProfiler() {
    }
    
    public void startTick() {
    }
    
    public void endTick() {
    }
    
    public void push(final String string) {
    }
    
    public void push(final Supplier<String> supplier) {
    }
    
    public void pop() {
    }
    
    public void popPush(final String string) {
    }
    
    public void popPush(final Supplier<String> supplier) {
    }
    
    public ProfileResults getResults() {
        return EmptyProfileResults.EMPTY;
    }
    
    static {
        INACTIVE = new InactiveProfiler();
    }
}
