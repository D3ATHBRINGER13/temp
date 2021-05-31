package net.minecraft.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.resources.ResourceLocation;

public interface CriterionTrigger<T extends CriterionTriggerInstance> {
    ResourceLocation getId();
    
    void addPlayerListener(final PlayerAdvancements re, final Listener<T> a);
    
    void removePlayerListener(final PlayerAdvancements re, final Listener<T> a);
    
    void removePlayerListeners(final PlayerAdvancements re);
    
    T createInstance(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext);
    
    public static class Listener<T extends CriterionTriggerInstance> {
        private final T trigger;
        private final Advancement advancement;
        private final String criterion;
        
        public Listener(final T y, final Advancement q, final String string) {
            this.trigger = y;
            this.advancement = q;
            this.criterion = string;
        }
        
        public T getTriggerInstance() {
            return this.trigger;
        }
        
        public void run(final PlayerAdvancements re) {
            re.award(this.advancement, this.criterion);
        }
        
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            final Listener<?> a3 = object;
            return this.trigger.equals(a3.trigger) && this.advancement.equals(a3.advancement) && this.criterion.equals(a3.criterion);
        }
        
        public int hashCode() {
            int integer2 = this.trigger.hashCode();
            integer2 = 31 * integer2 + this.advancement.hashCode();
            integer2 = 31 * integer2 + this.criterion.hashCode();
            return integer2;
        }
    }
}
