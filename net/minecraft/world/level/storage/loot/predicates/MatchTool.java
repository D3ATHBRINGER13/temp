package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import net.minecraft.advancements.critereon.ItemPredicate;

public class MatchTool implements LootItemCondition {
    private final ItemPredicate predicate;
    
    public MatchTool(final ItemPredicate bc) {
        this.predicate = bc;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(LootContextParams.TOOL);
    }
    
    public boolean test(final LootContext coy) {
        final ItemStack bcj3 = coy.<ItemStack>getParamOrNull(LootContextParams.TOOL);
        return bcj3 != null && this.predicate.matches(bcj3);
    }
    
    public static Builder toolMatches(final ItemPredicate.Builder a) {
        return () -> new MatchTool(a.build());
    }
    
    public static class Serializer extends LootItemCondition.Serializer<MatchTool> {
        protected Serializer() {
            super(new ResourceLocation("match_tool"), MatchTool.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final MatchTool crq, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", crq.predicate.serializeToJson());
        }
        
        @Override
        public MatchTool deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final ItemPredicate bc4 = ItemPredicate.fromJson(jsonObject.get("predicate"));
            return new MatchTool(bc4);
        }
    }
}
