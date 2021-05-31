package net.minecraft.advancements;

import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;

public interface CriterionTriggerInstance {
    ResourceLocation getCriterion();
    
    default JsonElement serializeToJson() {
        return (JsonElement)JsonNull.INSTANCE;
    }
}
