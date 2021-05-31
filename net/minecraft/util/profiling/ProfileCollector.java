package net.minecraft.util.profiling;

import java.util.function.Supplier;

public interface ProfileCollector extends ProfilerFiller {
    void push(final String string);
    
    void push(final Supplier<String> supplier);
    
    void pop();
    
    void popPush(final String string);
    
    void popPush(final Supplier<String> supplier);
    
    ProfileResults getResults();
}
