package net.minecraft.world.item.crafting;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Collection;
import net.minecraft.tags.ItemTags;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import java.util.stream.StreamSupport;
import com.google.gson.JsonSyntaxException;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.ItemLike;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Comparator;
import it.unimi.dsi.fastutil.ints.IntComparators;
import net.minecraft.world.entity.player.StackedContents;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.ItemStack;
import java.util.function.Predicate;

public final class Ingredient implements Predicate<ItemStack> {
    private static final Predicate<? super Value> NON_ALL_EMPTY;
    public static final Ingredient EMPTY;
    private final Value[] values;
    private ItemStack[] itemStacks;
    private IntList stackingIds;
    
    private Ingredient(final Stream<? extends Value> stream) {
        this.values = (Value[])stream.filter((Predicate)Ingredient.NON_ALL_EMPTY).toArray(Value[]::new);
    }
    
    public ItemStack[] getItems() {
        this.dissolve();
        return this.itemStacks;
    }
    
    private void dissolve() {
        if (this.itemStacks == null) {
            this.itemStacks = (ItemStack[])Arrays.stream((Object[])this.values).flatMap(c -> c.getItems().stream()).distinct().toArray(ItemStack[]::new);
        }
    }
    
    public boolean test(@Nullable final ItemStack bcj) {
        if (bcj == null) {
            return false;
        }
        if (this.values.length == 0) {
            return bcj.isEmpty();
        }
        this.dissolve();
        for (final ItemStack bcj2 : this.itemStacks) {
            if (bcj2.getItem() == bcj.getItem()) {
                return true;
            }
        }
        return false;
    }
    
    public IntList getStackingIds() {
        if (this.stackingIds == null) {
            this.dissolve();
            this.stackingIds = (IntList)new IntArrayList(this.itemStacks.length);
            for (final ItemStack bcj5 : this.itemStacks) {
                this.stackingIds.add(StackedContents.getStackingIndex(bcj5));
            }
            this.stackingIds.sort((Comparator)IntComparators.NATURAL_COMPARATOR);
        }
        return this.stackingIds;
    }
    
    public void toNetwork(final FriendlyByteBuf je) {
        this.dissolve();
        je.writeVarInt(this.itemStacks.length);
        for (int integer3 = 0; integer3 < this.itemStacks.length; ++integer3) {
            je.writeItem(this.itemStacks[integer3]);
        }
    }
    
    public JsonElement toJson() {
        if (this.values.length == 1) {
            return (JsonElement)this.values[0].serialize();
        }
        final JsonArray jsonArray2 = new JsonArray();
        for (final Value c6 : this.values) {
            jsonArray2.add((JsonElement)c6.serialize());
        }
        return (JsonElement)jsonArray2;
    }
    
    public boolean isEmpty() {
        return this.values.length == 0 && (this.itemStacks == null || this.itemStacks.length == 0) && (this.stackingIds == null || this.stackingIds.isEmpty());
    }
    
    private static Ingredient fromValues(final Stream<? extends Value> stream) {
        final Ingredient beo2 = new Ingredient(stream);
        return (beo2.values.length == 0) ? Ingredient.EMPTY : beo2;
    }
    
    public static Ingredient of(final ItemLike... arr) {
        return fromValues(Arrays.stream((Object[])arr).map(bhq -> new ItemValue(new ItemStack(bhq))));
    }
    
    public static Ingredient of(final ItemStack... arr) {
        return fromValues(Arrays.stream((Object[])arr).map(bcj -> new ItemValue(bcj)));
    }
    
    public static Ingredient of(final Tag<Item> zg) {
        return fromValues(Stream.of(new TagValue((Tag)zg)));
    }
    
    public static Ingredient fromNetwork(final FriendlyByteBuf je) {
        final int integer2 = je.readVarInt();
        return fromValues(Stream.generate(() -> new ItemValue(je.readItem())).limit((long)integer2));
    }
    
    public static Ingredient fromJson(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new JsonSyntaxException("Item cannot be null");
        }
        if (jsonElement.isJsonObject()) {
            return fromValues(Stream.of(valueFromJson(jsonElement.getAsJsonObject())));
        }
        if (!jsonElement.isJsonArray()) {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
        }
        final JsonArray jsonArray2 = jsonElement.getAsJsonArray();
        if (jsonArray2.size() == 0) {
            throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
        }
        return fromValues(StreamSupport.stream(jsonArray2.spliterator(), false).map(jsonElement -> valueFromJson(GsonHelper.convertToJsonObject(jsonElement, "item"))));
    }
    
    public static Value valueFromJson(final JsonObject jsonObject) {
        if (jsonObject.has("item") && jsonObject.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        }
        if (jsonObject.has("item")) {
            final ResourceLocation qv2 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "item"));
            final Item bce3 = (Item)Registry.ITEM.getOptional(qv2).orElseThrow(() -> new JsonSyntaxException(new StringBuilder().append("Unknown item '").append(qv2).append("'").toString()));
            return new ItemValue(new ItemStack(bce3));
        }
        if (!jsonObject.has("tag")) {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
        final ResourceLocation qv2 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "tag"));
        final Tag<Item> zg3 = ItemTags.getAllTags().getTag(qv2);
        if (zg3 == null) {
            throw new JsonSyntaxException(new StringBuilder().append("Unknown item tag '").append(qv2).append("'").toString());
        }
        return new TagValue((Tag)zg3);
    }
    
    static {
        NON_ALL_EMPTY = (c -> !c.getItems().stream().allMatch(ItemStack::isEmpty));
        EMPTY = new Ingredient(Stream.empty());
    }
    
    static class ItemValue implements Value {
        private final ItemStack item;
        
        private ItemValue(final ItemStack bcj) {
            this.item = bcj;
        }
        
        public Collection<ItemStack> getItems() {
            return (Collection<ItemStack>)Collections.singleton(this.item);
        }
        
        public JsonObject serialize() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("item", Registry.ITEM.getKey(this.item.getItem()).toString());
            return jsonObject2;
        }
    }
    
    static class TagValue implements Value {
        private final Tag<Item> tag;
        
        private TagValue(final Tag<Item> zg) {
            this.tag = zg;
        }
        
        public Collection<ItemStack> getItems() {
            final List<ItemStack> list2 = (List<ItemStack>)Lists.newArrayList();
            for (final Item bce4 : this.tag.getValues()) {
                list2.add(new ItemStack(bce4));
            }
            return (Collection<ItemStack>)list2;
        }
        
        public JsonObject serialize() {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("tag", this.tag.getId().toString());
            return jsonObject2;
        }
    }
    
    interface Value {
        Collection<ItemStack> getItems();
        
        JsonObject serialize();
    }
}
