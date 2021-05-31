package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.common.collect.Maps;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public class LootItemConditions {
    private static final Map<ResourceLocation, LootItemCondition.Serializer<?>> CONDITIONS_BY_NAME;
    private static final Map<Class<? extends LootItemCondition>, LootItemCondition.Serializer<?>> CONDITIONS_BY_CLASS;
    
    public static <T extends LootItemCondition> void register(final LootItemCondition.Serializer<? extends T> b) {
        final ResourceLocation qv2 = b.getName();
        final Class<T> class3 = (Class<T>)b.getPredicateClass();
        if (LootItemConditions.CONDITIONS_BY_NAME.containsKey(qv2)) {
            throw new IllegalArgumentException(new StringBuilder().append("Can't re-register item condition name ").append(qv2).toString());
        }
        if (LootItemConditions.CONDITIONS_BY_CLASS.containsKey(class3)) {
            throw new IllegalArgumentException("Can't re-register item condition class " + class3.getName());
        }
        LootItemConditions.CONDITIONS_BY_NAME.put(qv2, b);
        LootItemConditions.CONDITIONS_BY_CLASS.put(class3, b);
    }
    
    public static LootItemCondition.Serializer<?> getSerializer(final ResourceLocation qv) {
        final LootItemCondition.Serializer<?> b2 = LootItemConditions.CONDITIONS_BY_NAME.get(qv);
        if (b2 == null) {
            throw new IllegalArgumentException(new StringBuilder().append("Unknown loot item condition '").append(qv).append("'").toString());
        }
        return b2;
    }
    
    public static <T extends LootItemCondition> LootItemCondition.Serializer<T> getSerializer(final T crk) {
        final LootItemCondition.Serializer<T> b2 = (LootItemCondition.Serializer<T>)LootItemConditions.CONDITIONS_BY_CLASS.get(crk.getClass());
        if (b2 == null) {
            throw new IllegalArgumentException(new StringBuilder().append("Unknown loot item condition ").append(crk).toString());
        }
        return b2;
    }
    
    public static <T> Predicate<T> andConditions(final Predicate<T>[] arr) {
        switch (arr.length) {
            case 0: {
                return (Predicate<T>)(object -> true);
            }
            case 1: {
                return arr[0];
            }
            case 2: {
                return (Predicate<T>)arr[0].and((Predicate)arr[1]);
            }
            default: {
                return (Predicate<T>)(object -> {
                    for (final Predicate<Object> predicate6 : arr) {
                        if (!predicate6.test(object)) {
                            return false;
                        }
                    }
                    return true;
                });
            }
        }
    }
    
    public static <T> Predicate<T> orConditions(final Predicate<T>[] arr) {
        switch (arr.length) {
            case 0: {
                return (Predicate<T>)(object -> false);
            }
            case 1: {
                return arr[0];
            }
            case 2: {
                return (Predicate<T>)arr[0].or((Predicate)arr[1]);
            }
            default: {
                return (Predicate<T>)(object -> {
                    for (final Predicate<Object> predicate6 : arr) {
                        if (predicate6.test(object)) {
                            return true;
                        }
                    }
                    return false;
                });
            }
        }
    }
    
    static {
        CONDITIONS_BY_NAME = (Map)Maps.newHashMap();
        CONDITIONS_BY_CLASS = (Map)Maps.newHashMap();
        LootItemConditions.<LootItemCondition>register(new InvertedLootItemCondition.Serializer());
        LootItemConditions.<LootItemCondition>register(new AlternativeLootItemCondition.Serializer());
        LootItemConditions.<LootItemCondition>register(new LootItemRandomChanceCondition.Serializer());
        LootItemConditions.<LootItemCondition>register(new LootItemRandomChanceWithLootingCondition.Serializer());
        LootItemConditions.<LootItemCondition>register(new LootItemEntityPropertyCondition.Serializer());
        LootItemConditions.<LootItemCondition>register(new LootItemKilledByPlayerCondition.Serializer());
        LootItemConditions.<LootItemCondition>register(new EntityHasScoreCondition.Serializer());
        LootItemConditions.<LootItemCondition>register(new LootItemBlockStatePropertyCondition.Serializer());
        LootItemConditions.<LootItemCondition>register(new MatchTool.Serializer());
        LootItemConditions.<LootItemCondition>register(new BonusLevelTableCondition.Serializer());
        LootItemConditions.<LootItemCondition>register(new ExplosionCondition.Serializer());
        LootItemConditions.<LootItemCondition>register(new DamageSourceCondition.Serializer());
        LootItemConditions.<LootItemCondition>register(new LocationCheck.Serializer());
        LootItemConditions.<LootItemCondition>register(new WeatherCheck.Serializer());
    }
    
    public static class Serializer implements JsonDeserializer<LootItemCondition>, JsonSerializer<LootItemCondition> {
        public LootItemCondition deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject5 = GsonHelper.convertToJsonObject(jsonElement, "condition");
            final ResourceLocation qv6 = new ResourceLocation(GsonHelper.getAsString(jsonObject5, "condition"));
            LootItemCondition.Serializer<?> b7;
            try {
                b7 = LootItemConditions.getSerializer(qv6);
            }
            catch (IllegalArgumentException illegalArgumentException8) {
                throw new JsonSyntaxException(new StringBuilder().append("Unknown condition '").append(qv6).append("'").toString());
            }
            return (LootItemCondition)b7.deserialize(jsonObject5, jsonDeserializationContext);
        }
        
        public JsonElement serialize(final LootItemCondition crk, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final LootItemCondition.Serializer<LootItemCondition> b5 = LootItemConditions.<LootItemCondition>getSerializer(crk);
            final JsonObject jsonObject6 = new JsonObject();
            jsonObject6.addProperty("condition", b5.getName().toString());
            b5.serialize(jsonObject6, crk, jsonSerializationContext);
            return (JsonElement)jsonObject6;
        }
    }
}
