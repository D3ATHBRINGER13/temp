package net.minecraft.util.profiling;

import java.util.function.Supplier;

public interface ProfilerFiller {
    void startTick();
    
    void endTick();
    
    void push(final String string);
    
    void push(final Supplier<String> supplier);
    
    void pop();
    
    void popPush(final String string);
    
    void popPush(final Supplier<String> supplier);
}
