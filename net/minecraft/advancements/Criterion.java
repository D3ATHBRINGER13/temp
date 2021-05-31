package net.minecraft.advancements;

import javax.annotation.Nullable;
import java.util.Iterator;
import com.google.gson.JsonElement;
import com.google.common.collect.Maps;
import java.util.Map;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

public class Criterion {
    private final CriterionTriggerInstance trigger;
    
    public Criterion(final CriterionTriggerInstance y) {
        this.trigger = y;
    }
    
    public Criterion() {
        this.trigger = null;
    }
    
    public void serializeToNetwork(final FriendlyByteBuf je) {
    }
    
    public static Criterion criterionFromJson(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
        final ResourceLocation qv3 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "trigger"));
        final CriterionTrigger<?> x4 = CriteriaTriggers.getCriterion(qv3);
        if (x4 == null) {
            throw new JsonSyntaxException(new StringBuilder().append("Invalid criterion trigger: ").append(qv3).toString());
        }
        final CriterionTriggerInstance y5 = (CriterionTriggerInstance)x4.createInstance(GsonHelper.getAsJsonObject(jsonObject, "conditions", new JsonObject()), jsonDeserializationContext);
        return new Criterion(y5);
    }
    
    public static Criterion criterionFromNetwork(final FriendlyByteBuf je) {
        return new Criterion();
    }
    
    public static Map<String, Criterion> criteriaFromJson(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
        final Map<String, Criterion> map3 = (Map<String, Criterion>)Maps.newHashMap();
        for (final Map.Entry<String, JsonElement> entry5 : jsonObject.entrySet()) {
            map3.put(entry5.getKey(), criterionFromJson(GsonHelper.convertToJsonObject((JsonElement)entry5.getValue(), "criterion"), jsonDeserializationContext));
        }
        return map3;
    }
    
    public static Map<String, Criterion> criteriaFromNetwork(final FriendlyByteBuf je) {
        final Map<String, Criterion> map2 = (Map<String, Criterion>)Maps.newHashMap();
        for (int integer3 = je.readVarInt(), integer4 = 0; integer4 < integer3; ++integer4) {
            map2.put(je.readUtf(32767), criterionFromNetwork(je));
        }
        return map2;
    }
    
    public static void serializeToNetwork(final Map<String, Criterion> map, final FriendlyByteBuf je) {
        je.writeVarInt(map.size());
        for (final Map.Entry<String, Criterion> entry4 : map.entrySet()) {
            je.writeUtf((String)entry4.getKey());
            ((Criterion)entry4.getValue()).serializeToNetwork(je);
        }
    }
    
    @Nullable
    public CriterionTriggerInstance getTrigger() {
        return this.trigger;
    }
    
    public JsonElement serializeToJson() {
        final JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("trigger", this.trigger.getCriterion().toString());
        jsonObject2.add("conditions", this.trigger.serializeToJson());
        return (JsonElement)jsonObject2;
    }
}
