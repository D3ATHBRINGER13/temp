package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.HashMultimap;
import java.util.function.Supplier;
import com.google.common.collect.Multimap;

public class LootTableProblemCollector {
    private final Multimap<String, String> problems;
    private final Supplier<String> context;
    private String contextCache;
    
    public LootTableProblemCollector() {
        this((Multimap<String, String>)HashMultimap.create(), (Supplier<String>)(() -> ""));
    }
    
    public LootTableProblemCollector(final Multimap<String, String> multimap, final Supplier<String> supplier) {
        this.problems = multimap;
        this.context = supplier;
    }
    
    private String getContext() {
        if (this.contextCache == null) {
            this.contextCache = (String)this.context.get();
        }
        return this.contextCache;
    }
    
    public void reportProblem(final String string) {
        this.problems.put(this.getContext(), string);
    }
    
    public LootTableProblemCollector forChild(final String string) {
        return new LootTableProblemCollector(this.problems, (Supplier<String>)(() -> this.getContext() + string));
    }
    
    public Multimap<String, String> getProblems() {
        return (Multimap<String, String>)ImmutableMultimap.copyOf((Multimap)this.problems);
    }
}
