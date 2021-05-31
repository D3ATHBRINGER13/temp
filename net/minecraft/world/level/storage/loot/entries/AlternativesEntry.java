package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class AlternativesEntry extends CompositeEntryBase {
    AlternativesEntry(final LootPoolEntryContainer[] arr, final LootItemCondition[] arr) {
        super(arr, arr);
    }
    
    @Override
    protected ComposableEntryContainer compose(final ComposableEntryContainer[] arr) {
        switch (arr.length) {
            case 0: {
                return AlternativesEntry.ALWAYS_FALSE;
            }
            case 1: {
                return arr[0];
            }
            case 2: {
                return arr[0].or(arr[1]);
            }
            default: {
                final int length;
                int i = 0;
                ComposableEntryContainer cpi7;
                return (coy, consumer) -> {
                    length = arr.length;
                    while (i < length) {
                        cpi7 = arr[i];
                        if (cpi7.expand(coy, consumer)) {
                            return true;
                        }
                        else {
                            ++i;
                        }
                    }
                    return false;
                };
            }
        }
    }
    
    @Override
    public void validate(final LootTableProblemCollector cpc, final Function<ResourceLocation, LootTable> function, final Set<ResourceLocation> set, final LootContextParamSet cqx) {
        super.validate(cpc, function, set, cqx);
        for (int integer6 = 0; integer6 < this.children.length - 1; ++integer6) {
            if (ArrayUtils.isEmpty((Object[])this.children[integer6].conditions)) {
                cpc.reportProblem("Unreachable entry!");
            }
        }
    }
    
    public static Builder alternatives(final LootPoolEntryContainer.Builder<?>... arr) {
        return new Builder(arr);
    }
    
    public static class Builder extends LootPoolEntryContainer.Builder<Builder> {
        private final List<LootPoolEntryContainer> entries;
        
        public Builder(final LootPoolEntryContainer.Builder<?>... arr) {
            this.entries = (List<LootPoolEntryContainer>)Lists.newArrayList();
            for (final LootPoolEntryContainer.Builder<?> a6 : arr) {
                this.entries.add(a6.build());
            }
        }
        
        @Override
        protected Builder getThis() {
            return this;
        }
        
        @Override
        public Builder otherwise(final LootPoolEntryContainer.Builder<?> a) {
            this.entries.add(a.build());
            return this;
        }
        
        @Override
        public LootPoolEntryContainer build() {
            return new AlternativesEntry((LootPoolEntryContainer[])this.entries.toArray((Object[])new LootPoolEntryContainer[0]), this.getConditions());
        }
    }
}
