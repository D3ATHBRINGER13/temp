package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.RandomValueBounds;

public class LootingEnchantFunction extends LootItemConditionalFunction {
    private final RandomValueBounds value;
    private final int limit;
    
    private LootingEnchantFunction(final LootItemCondition[] arr, final RandomValueBounds cpg, final int integer) {
        super(arr);
        this.value = cpg;
        this.limit = integer;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(LootContextParams.KILLER_ENTITY);
    }
    
    private boolean hasLimit() {
        return this.limit > 0;
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        final Entity aio4 = coy.<Entity>getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (aio4 instanceof LivingEntity) {
            final int integer5 = EnchantmentHelper.getMobLooting((LivingEntity)aio4);
            if (integer5 == 0) {
                return bcj;
            }
            final float float6 = integer5 * this.value.getFloat(coy.getRandom());
            bcj.grow(Math.round(float6));
            if (this.hasLimit() && bcj.getCount() > this.limit) {
                bcj.setCount(this.limit);
            }
        }
        return bcj;
    }
    
    public static Builder lootingMultiplier(final RandomValueBounds cpg) {
        return new Builder(cpg);
    }
    
    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final RandomValueBounds count;
        private int limit;
        
        public Builder(final RandomValueBounds cpg) {
            this.limit = 0;
            this.count = cpg;
        }
        
        @Override
        protected Builder getThis() {
            return this;
        }
        
        public Builder setLimit(final int integer) {
            this.limit = integer;
            return this;
        }
        
        public LootItemFunction build() {
            return new LootingEnchantFunction(this.getConditions(), this.count, this.limit, null);
        }
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<LootingEnchantFunction> {
        protected Serializer() {
            super(new ResourceLocation("looting_enchant"), LootingEnchantFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final LootingEnchantFunction cqj, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqj, jsonSerializationContext);
            jsonObject.add("count", jsonSerializationContext.serialize(cqj.value));
            if (cqj.hasLimit()) {
                jsonObject.add("limit", jsonSerializationContext.serialize(cqj.limit));
            }
        }
        
        @Override
        public LootingEnchantFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final int integer5 = GsonHelper.getAsInt(jsonObject, "limit", 0);
            return new LootingEnchantFunction(arr, GsonHelper.<RandomValueBounds>getAsObject(jsonObject, "count", jsonDeserializationContext, (java.lang.Class<? extends RandomValueBounds>)RandomValueBounds.class), integer5, null);
        }
    }
}
