package net.minecraft.world.level.storage.loot.predicates;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.LootContext;
import java.util.function.Predicate;

public class AlternativeLootItemCondition implements LootItemCondition {
    private final LootItemCondition[] terms;
    private final Predicate<LootContext> composedPredicate;
    
    private AlternativeLootItemCondition(final LootItemCondition[] arr) {
        this.terms = arr;
        this.composedPredicate = LootItemConditions.<LootContext>orConditions((java.util.function.Predicate<LootContext>[])arr);
    }
    
    public final boolean test(final LootContext coy) {
        return this.composedPredicate.test(coy);
    }
    
    public void validate(final LootTableProblemCollector cpc, final Function<ResourceLocation, LootTable> function, final Set<ResourceLocation> set, final LootContextParamSet cqx) {
        super.validate(cpc, function, set, cqx);
        for (int integer6 = 0; integer6 < this.terms.length; ++integer6) {
            this.terms[integer6].validate(cpc.forChild(new StringBuilder().append(".term[").append(integer6).append("]").toString()), function, set, cqx);
        }
    }
    
    public static Builder alternative(final LootItemCondition.Builder... arr) {
        return new Builder(arr);
    }
    
    public static class Builder implements LootItemCondition.Builder {
        private final List<LootItemCondition> terms;
        
        public Builder(final LootItemCondition.Builder... arr) {
            this.terms = (List<LootItemCondition>)Lists.newArrayList();
            for (final LootItemCondition.Builder a6 : arr) {
                this.terms.add(a6.build());
            }
        }
        
        public Builder or(final LootItemCondition.Builder a) {
            this.terms.add(a.build());
            return this;
        }
        
        public LootItemCondition build() {
            return new AlternativeLootItemCondition((LootItemCondition[])this.terms.toArray((Object[])new LootItemCondition[0]), null);
        }
    }
    
    public static class Serializer extends LootItemCondition.Serializer<AlternativeLootItemCondition> {
        public Serializer() {
            super(new ResourceLocation("alternative"), AlternativeLootItemCondition.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final AlternativeLootItemCondition crb, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("terms", jsonSerializationContext.serialize(crb.terms));
        }
        
        @Override
        public AlternativeLootItemCondition deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final LootItemCondition[] arr4 = GsonHelper.<LootItemCondition[]>getAsObject(jsonObject, "terms", jsonDeserializationContext, (java.lang.Class<? extends LootItemCondition[]>)LootItemCondition[].class);
            return new AlternativeLootItemCondition(arr4, null);
        }
    }
}
