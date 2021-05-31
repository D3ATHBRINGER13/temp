package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import net.minecraft.world.item.ItemPropertyFunction;
import java.util.Iterator;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class ItemOverride {
    private final ResourceLocation model;
    private final Map<ResourceLocation, Float> predicates;
    
    public ItemOverride(final ResourceLocation qv, final Map<ResourceLocation, Float> map) {
        this.model = qv;
        this.predicates = map;
    }
    
    public ResourceLocation getModel() {
        return this.model;
    }
    
    boolean test(final ItemStack bcj, @Nullable final Level bhr, @Nullable final LivingEntity aix) {
        final Item bce5 = bcj.getItem();
        for (final Map.Entry<ResourceLocation, Float> entry7 : this.predicates.entrySet()) {
            final ItemPropertyFunction bci8 = bce5.getProperty((ResourceLocation)entry7.getKey());
            if (bci8 == null || bci8.call(bcj, bhr, aix) < (float)entry7.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    public static class Deserializer implements JsonDeserializer<ItemOverride> {
        protected Deserializer() {
        }
        
        public ItemOverride deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject5 = jsonElement.getAsJsonObject();
            final ResourceLocation qv6 = new ResourceLocation(GsonHelper.getAsString(jsonObject5, "model"));
            final Map<ResourceLocation, Float> map7 = this.getPredicates(jsonObject5);
            return new ItemOverride(qv6, map7);
        }
        
        protected Map<ResourceLocation, Float> getPredicates(final JsonObject jsonObject) {
            final Map<ResourceLocation, Float> map3 = (Map<ResourceLocation, Float>)Maps.newLinkedHashMap();
            final JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "predicate");
            for (final Map.Entry<String, JsonElement> entry6 : jsonObject2.entrySet()) {
                map3.put(new ResourceLocation((String)entry6.getKey()), GsonHelper.convertToFloat((JsonElement)entry6.getValue(), (String)entry6.getKey()));
            }
            return map3;
        }
    }
}
