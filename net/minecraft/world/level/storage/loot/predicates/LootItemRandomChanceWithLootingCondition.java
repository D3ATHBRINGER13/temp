package net.minecraft.world.level.storage.loot.predicates;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;

public class LootItemRandomChanceWithLootingCondition implements LootItemCondition {
    private final float percent;
    private final float lootingMultiplier;
    
    private LootItemRandomChanceWithLootingCondition(final float float1, final float float2) {
        this.percent = float1;
        this.lootingMultiplier = float2;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(LootContextParams.KILLER_ENTITY);
    }
    
    public boolean test(final LootContext coy) {
        final Entity aio3 = coy.<Entity>getParamOrNull(LootContextParams.KILLER_ENTITY);
        int integer4 = 0;
        if (aio3 instanceof LivingEntity) {
            integer4 = EnchantmentHelper.getMobLooting((LivingEntity)aio3);
        }
        return coy.getRandom().nextFloat() < this.percent + integer4 * this.lootingMultiplier;
    }
    
    public static Builder randomChanceAndLootingBoost(final float float1, final float float2) {
        return () -> new LootItemRandomChanceWithLootingCondition(float1, float2);
    }
    
    public static class Serializer extends LootItemCondition.Serializer<LootItemRandomChanceWithLootingCondition> {
        protected Serializer() {
            super(new ResourceLocation("random_chance_with_looting"), LootItemRandomChanceWithLootingCondition.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final LootItemRandomChanceWithLootingCondition crp, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", (Number)crp.percent);
            jsonObject.addProperty("looting_multiplier", (Number)crp.lootingMultiplier);
        }
        
        @Override
        public LootItemRandomChanceWithLootingCondition deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            return new LootItemRandomChanceWithLootingCondition(GsonHelper.getAsFloat(jsonObject, "chance"), GsonHelper.getAsFloat(jsonObject, "looting_multiplier"), null);
        }
    }
}
