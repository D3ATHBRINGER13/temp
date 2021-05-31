package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import net.minecraft.core.Registry;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.item.Item;

public class LootItem extends LootPoolSingletonContainer {
    private final Item item;
    
    private LootItem(final Item bce, final int integer2, final int integer3, final LootItemCondition[] arr, final LootItemFunction[] arr) {
        super(integer2, integer3, arr, arr);
        this.item = bce;
    }
    
    public void createItemStack(final Consumer<ItemStack> consumer, final LootContext coy) {
        consumer.accept(new ItemStack(this.item));
    }
    
    public static Builder<?> lootTableItem(final ItemLike bhq) {
        return LootPoolSingletonContainer.simpleBuilder((integer2, integer3, arr, arr) -> new LootItem(bhq.asItem(), integer2, integer3, arr, arr));
    }
    
    public static class Serializer extends LootPoolSingletonContainer.Serializer<LootItem> {
        public Serializer() {
            super(new ResourceLocation("item"), LootItem.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final LootItem cpn, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cpn, jsonSerializationContext);
            final ResourceLocation qv5 = Registry.ITEM.getKey(cpn.item);
            if (qv5 == null) {
                throw new IllegalArgumentException(new StringBuilder().append("Can't serialize unknown item ").append(cpn.item).toString());
            }
            jsonObject.addProperty("name", qv5.toString());
        }
        
        @Override
        protected LootItem deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final int integer3, final int integer4, final LootItemCondition[] arr, final LootItemFunction[] arr) {
            final Item bce8 = GsonHelper.getAsItem(jsonObject, "name");
            return new LootItem(bce8, integer3, integer4, arr, arr, null);
        }
    }
}
