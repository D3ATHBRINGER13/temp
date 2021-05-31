package net.minecraft.world.level.storage.loot.predicates;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;

public class LootItemRandomChanceCondition implements LootItemCondition {
    private final float probability;
    
    private LootItemRandomChanceCondition(final float float1) {
        this.probability = float1;
    }
    
    public boolean test(final LootContext coy) {
        return coy.getRandom().nextFloat() < this.probability;
    }
    
    public static Builder randomChance(final float float1) {
        return () -> new LootItemRandomChanceCondition(float1);
    }
    
    public static class Serializer extends LootItemCondition.Serializer<LootItemRandomChanceCondition> {
        protected Serializer() {
            super(new ResourceLocation("random_chance"), LootItemRandomChanceCondition.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final LootItemRandomChanceCondition cro, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", (Number)cro.probability);
        }
        
        @Override
        public LootItemRandomChanceCondition deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            return new LootItemRandomChanceCondition(GsonHelper.getAsFloat(jsonObject, "chance"), null);
        }
    }
}
