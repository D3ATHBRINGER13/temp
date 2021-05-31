package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;

public class DynamicLoot extends LootPoolSingletonContainer {
    public static final ResourceLocation TYPE;
    private final ResourceLocation name;
    
    private DynamicLoot(final ResourceLocation qv, final int integer2, final int integer3, final LootItemCondition[] arr, final LootItemFunction[] arr) {
        super(integer2, integer3, arr, arr);
        this.name = qv;
    }
    
    public void createItemStack(final Consumer<ItemStack> consumer, final LootContext coy) {
        coy.addDynamicDrops(this.name, consumer);
    }
    
    public static Builder<?> dynamicEntry(final ResourceLocation qv) {
        return LootPoolSingletonContainer.simpleBuilder((integer2, integer3, arr, arr) -> new DynamicLoot(qv, integer2, integer3, arr, arr));
    }
    
    static {
        TYPE = new ResourceLocation("dynamic");
    }
    
    public static class Serializer extends LootPoolSingletonContainer.Serializer<DynamicLoot> {
        public Serializer() {
            super(new ResourceLocation("dynamic"), DynamicLoot.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final DynamicLoot cpk, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cpk, jsonSerializationContext);
            jsonObject.addProperty("name", cpk.name.toString());
        }
        
        @Override
        protected DynamicLoot deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final int integer3, final int integer4, final LootItemCondition[] arr, final LootItemFunction[] arr) {
            final ResourceLocation qv8 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
            return new DynamicLoot(qv8, integer3, integer4, arr, arr, null);
        }
    }
}
