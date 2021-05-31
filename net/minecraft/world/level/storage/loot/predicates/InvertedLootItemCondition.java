package net.minecraft.world.level.storage.loot.predicates;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootContext;

public class InvertedLootItemCondition implements LootItemCondition {
    private final LootItemCondition term;
    
    private InvertedLootItemCondition(final LootItemCondition crk) {
        this.term = crk;
    }
    
    public final boolean test(final LootContext coy) {
        return !this.term.test(coy);
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.term.getReferencedContextParams();
    }
    
    public void validate(final LootTableProblemCollector cpc, final Function<ResourceLocation, LootTable> function, final Set<ResourceLocation> set, final LootContextParamSet cqx) {
        super.validate(cpc, function, set, cqx);
        this.term.validate(cpc, function, set, cqx);
    }
    
    public static Builder invert(final Builder a) {
        final InvertedLootItemCondition crh2 = new InvertedLootItemCondition(a.build());
        return () -> crh2;
    }
    
    public static class Serializer extends LootItemCondition.Serializer<InvertedLootItemCondition> {
        public Serializer() {
            super(new ResourceLocation("inverted"), InvertedLootItemCondition.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final InvertedLootItemCondition crh, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("term", jsonSerializationContext.serialize(crh.term));
        }
        
        @Override
        public InvertedLootItemCondition deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final LootItemCondition crk4 = GsonHelper.<LootItemCondition>getAsObject(jsonObject, "term", jsonDeserializationContext, (java.lang.Class<? extends LootItemCondition>)LootItemCondition.class);
            return new InvertedLootItemCondition(crk4, null);
        }
    }
}
