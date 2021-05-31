package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.util.Iterator;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.Registry;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.Maps;
import java.util.Random;
import net.minecraft.world.item.SuspiciousStewItem;
import com.google.common.collect.Iterables;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.effect.MobEffect;
import java.util.Map;

public class SetStewEffectFunction extends LootItemConditionalFunction {
    private final Map<MobEffect, RandomValueBounds> effectDurationMap;
    
    private SetStewEffectFunction(final LootItemCondition[] arr, final Map<MobEffect, RandomValueBounds> map) {
        super(arr);
        this.effectDurationMap = (Map<MobEffect, RandomValueBounds>)ImmutableMap.copyOf((Map)map);
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        if (bcj.getItem() != Items.SUSPICIOUS_STEW || this.effectDurationMap.isEmpty()) {
            return bcj;
        }
        final Random random4 = coy.getRandom();
        final int integer5 = random4.nextInt(this.effectDurationMap.size());
        final Map.Entry<MobEffect, RandomValueBounds> entry6 = (Map.Entry<MobEffect, RandomValueBounds>)Iterables.get((Iterable)this.effectDurationMap.entrySet(), integer5);
        final MobEffect aig7 = (MobEffect)entry6.getKey();
        int integer6 = ((RandomValueBounds)entry6.getValue()).getInt(random4);
        if (!aig7.isInstantenous()) {
            integer6 *= 20;
        }
        SuspiciousStewItem.saveMobEffect(bcj, aig7, integer6);
        return bcj;
    }
    
    public static Builder stewEffect() {
        return new Builder();
    }
    
    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final Map<MobEffect, RandomValueBounds> effectDurationMap;
        
        public Builder() {
            this.effectDurationMap = (Map<MobEffect, RandomValueBounds>)Maps.newHashMap();
        }
        
        @Override
        protected Builder getThis() {
            return this;
        }
        
        public Builder withEffect(final MobEffect aig, final RandomValueBounds cpg) {
            this.effectDurationMap.put(aig, cpg);
            return this;
        }
        
        public LootItemFunction build() {
            return new SetStewEffectFunction(this.getConditions(), this.effectDurationMap, null);
        }
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<SetStewEffectFunction> {
        public Serializer() {
            super(new ResourceLocation("set_stew_effect"), SetStewEffectFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final SetStewEffectFunction cqs, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqs, jsonSerializationContext);
            if (!cqs.effectDurationMap.isEmpty()) {
                final JsonArray jsonArray5 = new JsonArray();
                for (final MobEffect aig7 : cqs.effectDurationMap.keySet()) {
                    final JsonObject jsonObject2 = new JsonObject();
                    final ResourceLocation qv9 = Registry.MOB_EFFECT.getKey(aig7);
                    if (qv9 == null) {
                        throw new IllegalArgumentException(new StringBuilder().append("Don't know how to serialize mob effect ").append(aig7).toString());
                    }
                    jsonObject2.add("type", (JsonElement)new JsonPrimitive(qv9.toString()));
                    jsonObject2.add("duration", jsonSerializationContext.serialize(cqs.effectDurationMap.get(aig7)));
                    jsonArray5.add((JsonElement)jsonObject2);
                }
                jsonObject.add("effects", (JsonElement)jsonArray5);
            }
        }
        
        @Override
        public SetStewEffectFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final Map<MobEffect, RandomValueBounds> map5 = (Map<MobEffect, RandomValueBounds>)Maps.newHashMap();
            if (jsonObject.has("effects")) {
                final JsonArray jsonArray6 = GsonHelper.getAsJsonArray(jsonObject, "effects");
                for (final JsonElement jsonElement8 : jsonArray6) {
                    final String string9 = GsonHelper.getAsString(jsonElement8.getAsJsonObject(), "type");
                    final MobEffect aig10 = (MobEffect)Registry.MOB_EFFECT.getOptional(new ResourceLocation(string9)).orElseThrow(() -> new JsonSyntaxException("Unknown mob effect '" + string9 + "'"));
                    final RandomValueBounds cpg11 = GsonHelper.<RandomValueBounds>getAsObject(jsonElement8.getAsJsonObject(), "duration", jsonDeserializationContext, (java.lang.Class<? extends RandomValueBounds>)RandomValueBounds.class);
                    map5.put(aig10, cpg11);
                }
            }
            return new SetStewEffectFunction(arr, map5, null);
        }
    }
}
