package net.minecraft.world.level.storage.loot.parameters;

import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class LootContextParamSet {
    private final Set<LootContextParam<?>> required;
    private final Set<LootContextParam<?>> all;
    
    private LootContextParamSet(final Set<LootContextParam<?>> set1, final Set<LootContextParam<?>> set2) {
        this.required = (Set<LootContextParam<?>>)ImmutableSet.copyOf((Collection)set1);
        this.all = (Set<LootContextParam<?>>)ImmutableSet.copyOf((Collection)Sets.union((Set)set1, (Set)set2));
    }
    
    public Set<LootContextParam<?>> getRequired() {
        return this.required;
    }
    
    public Set<LootContextParam<?>> getAllowed() {
        return this.all;
    }
    
    public String toString() {
        return "[" + Joiner.on(", ").join(this.all.stream().map(cqw -> new StringBuilder().append(this.required.contains(cqw) ? "!" : "").append(cqw.getName()).toString()).iterator()) + "]";
    }
    
    public void validateUser(final LootTableProblemCollector cpc, final LootContextUser coz) {
        final Set<LootContextParam<?>> set4 = coz.getReferencedContextParams();
        final Set<LootContextParam<?>> set5 = (Set<LootContextParam<?>>)Sets.difference((Set)set4, (Set)this.all);
        if (!set5.isEmpty()) {
            cpc.reportProblem(new StringBuilder().append("Parameters ").append(set5).append(" are not provided in this context").toString());
        }
    }
    
    public static class Builder {
        private final Set<LootContextParam<?>> required;
        private final Set<LootContextParam<?>> optional;
        
        public Builder() {
            this.required = (Set<LootContextParam<?>>)Sets.newIdentityHashSet();
            this.optional = (Set<LootContextParam<?>>)Sets.newIdentityHashSet();
        }
        
        public Builder required(final LootContextParam<?> cqw) {
            if (this.optional.contains(cqw)) {
                throw new IllegalArgumentException(new StringBuilder().append("Parameter ").append(cqw.getName()).append(" is already optional").toString());
            }
            this.required.add(cqw);
            return this;
        }
        
        public Builder optional(final LootContextParam<?> cqw) {
            if (this.required.contains(cqw)) {
                throw new IllegalArgumentException(new StringBuilder().append("Parameter ").append(cqw.getName()).append(" is already required").toString());
            }
            this.optional.add(cqw);
            return this;
        }
        
        public LootContextParamSet build() {
            return new LootContextParamSet(this.required, this.optional, null);
        }
    }
}
