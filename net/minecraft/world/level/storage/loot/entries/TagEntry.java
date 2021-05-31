package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonParseException;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.Iterator;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;

public class TagEntry extends LootPoolSingletonContainer {
    private final Tag<Item> tag;
    private final boolean expand;
    
    private TagEntry(final Tag<Item> zg, final boolean boolean2, final int integer3, final int integer4, final LootItemCondition[] arr, final LootItemFunction[] arr) {
        super(integer3, integer4, arr, arr);
        this.tag = zg;
        this.expand = boolean2;
    }
    
    public void createItemStack(final Consumer<ItemStack> consumer, final LootContext coy) {
        this.tag.getValues().forEach(bce -> consumer.accept(new ItemStack(bce)));
    }
    
    private boolean expandTag(final LootContext coy, final Consumer<LootPoolEntry> consumer) {
        if (this.canRun(coy)) {
            for (final Item bce5 : this.tag.getValues()) {
                consumer.accept(new EntryBase() {
                    public void createItemStack(final Consumer<ItemStack> consumer, final LootContext coy) {
                        consumer.accept((Object)new ItemStack(bce5));
                    }
                });
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean expand(final LootContext coy, final Consumer<LootPoolEntry> consumer) {
        if (this.expand) {
            return this.expandTag(coy, consumer);
        }
        return super.expand(coy, consumer);
    }
    
    public static Builder<?> expandTag(final Tag<Item> zg) {
        return LootPoolSingletonContainer.simpleBuilder((integer2, integer3, arr, arr) -> new TagEntry(zg, true, integer2, integer3, arr, arr));
    }
    
    public static class Serializer extends LootPoolSingletonContainer.Serializer<TagEntry> {
        public Serializer() {
            super(new ResourceLocation("tag"), TagEntry.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final TagEntry cpu, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cpu, jsonSerializationContext);
            jsonObject.addProperty("name", cpu.tag.getId().toString());
            jsonObject.addProperty("expand", Boolean.valueOf(cpu.expand));
        }
        
        @Override
        protected TagEntry deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final int integer3, final int integer4, final LootItemCondition[] arr, final LootItemFunction[] arr) {
            final ResourceLocation qv8 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
            final Tag<Item> zg9 = ItemTags.getAllTags().getTag(qv8);
            if (zg9 == null) {
                throw new JsonParseException(new StringBuilder().append("Can't find tag: ").append(qv8).toString());
            }
            final boolean boolean10 = GsonHelper.getAsBoolean(jsonObject, "expand");
            return new TagEntry(zg9, boolean10, integer3, integer4, arr, arr, null);
        }
    }
}
