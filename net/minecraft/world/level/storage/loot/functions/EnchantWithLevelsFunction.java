package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import net.minecraft.world.level.storage.loot.RandomIntGenerators;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.Random;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;

public class EnchantWithLevelsFunction extends LootItemConditionalFunction {
    private final RandomIntGenerator levels;
    private final boolean treasure;
    
    private EnchantWithLevelsFunction(final LootItemCondition[] arr, final RandomIntGenerator cpe, final boolean boolean3) {
        super(arr);
        this.levels = cpe;
        this.treasure = boolean3;
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        final Random random4 = coy.getRandom();
        return EnchantmentHelper.enchantItem(random4, bcj, this.levels.getInt(random4), this.treasure);
    }
    
    public static Builder enchantWithLevels(final RandomIntGenerator cpe) {
        return new Builder(cpe);
    }
    
    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final RandomIntGenerator levels;
        private boolean treasure;
        
        public Builder(final RandomIntGenerator cpe) {
            this.levels = cpe;
        }
        
        @Override
        protected Builder getThis() {
            return this;
        }
        
        public Builder allowTreasure() {
            this.treasure = true;
            return this;
        }
        
        public LootItemFunction build() {
            return new EnchantWithLevelsFunction(this.getConditions(), this.levels, this.treasure, null);
        }
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<EnchantWithLevelsFunction> {
        public Serializer() {
            super(new ResourceLocation("enchant_with_levels"), EnchantWithLevelsFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final EnchantWithLevelsFunction cqb, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqb, jsonSerializationContext);
            jsonObject.add("levels", RandomIntGenerators.serialize(cqb.levels, jsonSerializationContext));
            jsonObject.addProperty("treasure", Boolean.valueOf(cqb.treasure));
        }
        
        @Override
        public EnchantWithLevelsFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final RandomIntGenerator cpe5 = RandomIntGenerators.deserialize(jsonObject.get("levels"), jsonDeserializationContext);
            final boolean boolean6 = GsonHelper.getAsBoolean(jsonObject, "treasure", false);
            return new EnchantWithLevelsFunction(arr, cpe5, boolean6, null);
        }
    }
}
