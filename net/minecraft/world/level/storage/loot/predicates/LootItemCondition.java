package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import java.util.function.Predicate;
import net.minecraft.world.level.storage.loot.LootContextUser;

@FunctionalInterface
public interface LootItemCondition extends LootContextUser, Predicate<LootContext> {
    @FunctionalInterface
    public interface Builder {
        LootItemCondition build();
        
        default Builder invert() {
            return InvertedLootItemCondition.invert(this);
        }
        
        default AlternativeLootItemCondition.Builder or(final Builder a) {
            return AlternativeLootItemCondition.alternative(this, a);
        }
    }
    
    public abstract static class Serializer<T extends LootItemCondition> {
        private final ResourceLocation name;
        private final Class<T> clazz;
        
        protected Serializer(final ResourceLocation qv, final Class<T> class2) {
            this.name = qv;
            this.clazz = class2;
        }
        
        public ResourceLocation getName() {
            return this.name;
        }
        
        public Class<T> getPredicateClass() {
            return this.clazz;
        }
        
        public abstract void serialize(final JsonObject jsonObject, final T crk, final JsonSerializationContext jsonSerializationContext);
        
        public abstract T deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext);
    }
}
