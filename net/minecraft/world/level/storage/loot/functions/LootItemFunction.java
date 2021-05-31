package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import java.util.function.BiFunction;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface LootItemFunction extends LootContextUser, BiFunction<ItemStack, LootContext, ItemStack> {
    default Consumer<ItemStack> decorate(final BiFunction<ItemStack, LootContext, ItemStack> biFunction, final Consumer<ItemStack> consumer, final LootContext coy) {
        return (Consumer<ItemStack>)(bcj -> consumer.accept(biFunction.apply(bcj, coy)));
    }
    
    public abstract static class Serializer<T extends LootItemFunction> {
        private final ResourceLocation name;
        private final Class<T> clazz;
        
        protected Serializer(final ResourceLocation qv, final Class<T> class2) {
            this.name = qv;
            this.clazz = class2;
        }
        
        public ResourceLocation getName() {
            return this.name;
        }
        
        public Class<T> getFunctionClass() {
            return this.clazz;
        }
        
        public abstract void serialize(final JsonObject jsonObject, final T cqh, final JsonSerializationContext jsonSerializationContext);
        
        public abstract T deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext);
    }
    
    public interface Builder {
        LootItemFunction build();
    }
}
