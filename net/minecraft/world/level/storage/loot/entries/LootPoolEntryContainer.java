package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.LootContext;
import java.util.function.Predicate;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class LootPoolEntryContainer implements ComposableEntryContainer {
    protected final LootItemCondition[] conditions;
    private final Predicate<LootContext> compositeCondition;
    
    protected LootPoolEntryContainer(final LootItemCondition[] arr) {
        this.conditions = arr;
        this.compositeCondition = LootItemConditions.<LootContext>andConditions((java.util.function.Predicate<LootContext>[])arr);
    }
    
    public void validate(final LootTableProblemCollector cpc, final Function<ResourceLocation, LootTable> function, final Set<ResourceLocation> set, final LootContextParamSet cqx) {
        for (int integer6 = 0; integer6 < this.conditions.length; ++integer6) {
            this.conditions[integer6].validate(cpc.forChild(new StringBuilder().append(".condition[").append(integer6).append("]").toString()), function, set, cqx);
        }
    }
    
    protected final boolean canRun(final LootContext coy) {
        return this.compositeCondition.test(coy);
    }
    
    public abstract static class Builder<T extends Builder<T>> implements ConditionUserBuilder<T> {
        private final List<LootItemCondition> conditions;
        
        public Builder() {
            this.conditions = (List<LootItemCondition>)Lists.newArrayList();
        }
        
        protected abstract T getThis();
        
        public T when(final LootItemCondition.Builder a) {
            this.conditions.add(a.build());
            return this.getThis();
        }
        
        public final T unwrap() {
            return this.getThis();
        }
        
        protected LootItemCondition[] getConditions() {
            return (LootItemCondition[])this.conditions.toArray((Object[])new LootItemCondition[0]);
        }
        
        public AlternativesEntry.Builder otherwise(final Builder<?> a) {
            return new AlternativesEntry.Builder(new Builder[] { this, a });
        }
        
        public abstract LootPoolEntryContainer build();
    }
    
    public abstract static class Serializer<T extends LootPoolEntryContainer> {
        private final ResourceLocation name;
        private final Class<T> clazz;
        
        protected Serializer(final ResourceLocation qv, final Class<T> class2) {
            this.name = qv;
            this.clazz = class2;
        }
        
        public ResourceLocation getName() {
            return this.name;
        }
        
        public Class<T> getContainerClass() {
            return this.clazz;
        }
        
        public abstract void serialize(final JsonObject jsonObject, final T cpq, final JsonSerializationContext jsonSerializationContext);
        
        public abstract T deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr);
    }
}
