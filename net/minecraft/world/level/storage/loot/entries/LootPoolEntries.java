package net.minecraft.world.level.storage.loot.entries;

import org.apache.commons.lang3.ArrayUtils;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public class LootPoolEntries {
    private static final Map<ResourceLocation, LootPoolEntryContainer.Serializer<?>> ID_TO_SERIALIZER;
    private static final Map<Class<?>, LootPoolEntryContainer.Serializer<?>> CLASS_TO_SERIALIZER;
    
    private static void register(final LootPoolEntryContainer.Serializer<?> b) {
        LootPoolEntries.ID_TO_SERIALIZER.put(b.getName(), b);
        LootPoolEntries.CLASS_TO_SERIALIZER.put(b.getContainerClass(), b);
    }
    
    static {
        ID_TO_SERIALIZER = (Map)Maps.newHashMap();
        CLASS_TO_SERIALIZER = (Map)Maps.newHashMap();
        register(CompositeEntryBase.<AlternativesEntry>createSerializer(new ResourceLocation("alternatives"), AlternativesEntry.class, AlternativesEntry::new));
        register(CompositeEntryBase.<SequentialEntry>createSerializer(new ResourceLocation("sequence"), SequentialEntry.class, SequentialEntry::new));
        register(CompositeEntryBase.<EntryGroup>createSerializer(new ResourceLocation("group"), EntryGroup.class, EntryGroup::new));
        register(new EmptyLootItem.Serializer());
        register(new LootItem.Serializer());
        register(new LootTableReference.Serializer());
        register(new DynamicLoot.Serializer());
        register(new TagEntry.Serializer());
    }
    
    public static class Serializer implements JsonDeserializer<LootPoolEntryContainer>, JsonSerializer<LootPoolEntryContainer> {
        public LootPoolEntryContainer deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) {
            final JsonObject jsonObject5 = GsonHelper.convertToJsonObject(jsonElement, "entry");
            final ResourceLocation qv6 = new ResourceLocation(GsonHelper.getAsString(jsonObject5, "type"));
            final LootPoolEntryContainer.Serializer<?> b7 = LootPoolEntries.ID_TO_SERIALIZER.get(qv6);
            if (b7 == null) {
                throw new JsonParseException(new StringBuilder().append("Unknown item type: ").append(qv6).toString());
            }
            final LootItemCondition[] arr8 = GsonHelper.<LootItemCondition[]>getAsObject(jsonObject5, "conditions", new LootItemCondition[0], jsonDeserializationContext, (java.lang.Class<? extends LootItemCondition[]>)LootItemCondition[].class);
            return (LootPoolEntryContainer)b7.deserialize(jsonObject5, jsonDeserializationContext, arr8);
        }
        
        public JsonElement serialize(final LootPoolEntryContainer cpq, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject5 = new JsonObject();
            final LootPoolEntryContainer.Serializer<LootPoolEntryContainer> b6 = getSerializer(cpq.getClass());
            jsonObject5.addProperty("type", b6.getName().toString());
            if (!ArrayUtils.isEmpty((Object[])cpq.conditions)) {
                jsonObject5.add("conditions", jsonSerializationContext.serialize(cpq.conditions));
            }
            b6.serialize(jsonObject5, cpq, jsonSerializationContext);
            return (JsonElement)jsonObject5;
        }
        
        private static LootPoolEntryContainer.Serializer<LootPoolEntryContainer> getSerializer(final Class<?> class1) {
            final LootPoolEntryContainer.Serializer<?> b2 = LootPoolEntries.CLASS_TO_SERIALIZER.get(class1);
            if (b2 == null) {
                throw new JsonParseException(new StringBuilder().append("Unknown item type: ").append(class1).toString());
            }
            return (LootPoolEntryContainer.Serializer<LootPoolEntryContainer>)b2;
        }
    }
}
