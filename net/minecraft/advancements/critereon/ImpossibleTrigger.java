package net.minecraft.advancements.critereon;

import net.minecraft.advancements.CriterionTriggerInstance;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;

public class ImpossibleTrigger implements CriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID;
    
    public ResourceLocation getId() {
        return ImpossibleTrigger.ID;
    }
    
    public void addPlayerListener(final PlayerAdvancements re, final Listener<TriggerInstance> a) {
    }
    
    public void removePlayerListener(final PlayerAdvancements re, final Listener<TriggerInstance> a) {
    }
    
    public void removePlayerListeners(final PlayerAdvancements re) {
    }
    
    public TriggerInstance createInstance(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
        return new TriggerInstance();
    }
    
    static {
        ID = new ResourceLocation("impossible");
    }
    
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance() {
            super(ImpossibleTrigger.ID);
        }
    }
}
