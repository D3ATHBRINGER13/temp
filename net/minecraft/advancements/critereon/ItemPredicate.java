package net.minecraft.advancements.critereon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ItemLike;
import com.google.common.collect.Lists;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.tags.ItemTags;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonElement;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.Map;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import javax.annotation.Nullable;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;

public class ItemPredicate {
    public static final ItemPredicate ANY;
    @Nullable
    private final Tag<Item> tag;
    @Nullable
    private final Item item;
    private final MinMaxBounds.Ints count;
    private final MinMaxBounds.Ints durability;
    private final EnchantmentPredicate[] enchantments;
    @Nullable
    private final Potion potion;
    private final NbtPredicate nbt;
    
    public ItemPredicate() {
        this.tag = null;
        this.item = null;
        this.potion = null;
        this.count = MinMaxBounds.Ints.ANY;
        this.durability = MinMaxBounds.Ints.ANY;
        this.enchantments = new EnchantmentPredicate[0];
        this.nbt = NbtPredicate.ANY;
    }
    
    public ItemPredicate(@Nullable final Tag<Item> zg, @Nullable final Item bce, final MinMaxBounds.Ints d3, final MinMaxBounds.Ints d4, final EnchantmentPredicate[] arr, @Nullable final Potion bdy, final NbtPredicate bk) {
        this.tag = zg;
        this.item = bce;
        this.count = d3;
        this.durability = d4;
        this.enchantments = arr;
        this.potion = bdy;
        this.nbt = bk;
    }
    
    public boolean matches(final ItemStack bcj) {
        if (this == ItemPredicate.ANY) {
            return true;
        }
        if (this.tag != null && !this.tag.contains(bcj.getItem())) {
            return false;
        }
        if (this.item != null && bcj.getItem() != this.item) {
            return false;
        }
        if (!this.count.matches(bcj.getCount())) {
            return false;
        }
        if (!this.durability.isAny() && !bcj.isDamageableItem()) {
            return false;
        }
        if (!this.durability.matches(bcj.getMaxDamage() - bcj.getDamageValue())) {
            return false;
        }
        if (!this.nbt.matches(bcj)) {
            return false;
        }
        final Map<Enchantment, Integer> map3 = EnchantmentHelper.getEnchantments(bcj);
        for (int integer4 = 0; integer4 < this.enchantments.length; ++integer4) {
            if (!this.enchantments[integer4].containedIn(map3)) {
                return false;
            }
        }
        final Potion bdy4 = PotionUtils.getPotion(bcj);
        return this.potion == null || this.potion == bdy4;
    }
    
    public static ItemPredicate fromJson(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ItemPredicate.ANY;
        }
        final JsonObject jsonObject2 = GsonHelper.convertToJsonObject(jsonElement, "item");
        final MinMaxBounds.Ints d3 = MinMaxBounds.Ints.fromJson(jsonObject2.get("count"));
        final MinMaxBounds.Ints d4 = MinMaxBounds.Ints.fromJson(jsonObject2.get("durability"));
        if (jsonObject2.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        final NbtPredicate bk5 = NbtPredicate.fromJson(jsonObject2.get("nbt"));
        Item bce6 = null;
        if (jsonObject2.has("item")) {
            final ResourceLocation qv7 = new ResourceLocation(GsonHelper.getAsString(jsonObject2, "item"));
            bce6 = (Item)Registry.ITEM.getOptional(qv7).orElseThrow(() -> new JsonSyntaxException(new StringBuilder().append("Unknown item id '").append(qv7).append("'").toString()));
        }
        Tag<Item> zg7 = null;
        if (jsonObject2.has("tag")) {
            final ResourceLocation qv8 = new ResourceLocation(GsonHelper.getAsString(jsonObject2, "tag"));
            zg7 = ItemTags.getAllTags().getTag(qv8);
            if (zg7 == null) {
                throw new JsonSyntaxException(new StringBuilder().append("Unknown item tag '").append(qv8).append("'").toString());
            }
        }
        final EnchantmentPredicate[] arr8 = EnchantmentPredicate.fromJsonArray(jsonObject2.get("enchantments"));
        Potion bdy9 = null;
        if (jsonObject2.has("potion")) {
            final ResourceLocation qv9 = new ResourceLocation(GsonHelper.getAsString(jsonObject2, "potion"));
            bdy9 = (Potion)Registry.POTION.getOptional(qv9).orElseThrow(() -> new JsonSyntaxException(new StringBuilder().append("Unknown potion '").append(qv9).append("'").toString()));
        }
        return new ItemPredicate(zg7, bce6, d3, d4, arr8, bdy9, bk5);
    }
    
    public JsonElement serializeToJson() {
        if (this == ItemPredicate.ANY) {
            return (JsonElement)JsonNull.INSTANCE;
        }
        final JsonObject jsonObject2 = new JsonObject();
        if (this.item != null) {
            jsonObject2.addProperty("item", Registry.ITEM.getKey(this.item).toString());
        }
        if (this.tag != null) {
            jsonObject2.addProperty("tag", this.tag.getId().toString());
        }
        jsonObject2.add("count", this.count.serializeToJson());
        jsonObject2.add("durability", this.durability.serializeToJson());
        jsonObject2.add("nbt", this.nbt.serializeToJson());
        if (this.enchantments.length > 0) {
            final JsonArray jsonArray3 = new JsonArray();
            for (final EnchantmentPredicate aq7 : this.enchantments) {
                jsonArray3.add(aq7.serializeToJson());
            }
            jsonObject2.add("enchantments", (JsonElement)jsonArray3);
        }
        if (this.potion != null) {
            jsonObject2.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
        }
        return (JsonElement)jsonObject2;
    }
    
    public static ItemPredicate[] fromJsonArray(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return new ItemPredicate[0];
        }
        final JsonArray jsonArray2 = GsonHelper.convertToJsonArray(jsonElement, "items");
        final ItemPredicate[] arr3 = new ItemPredicate[jsonArray2.size()];
        for (int integer4 = 0; integer4 < arr3.length; ++integer4) {
            arr3[integer4] = fromJson(jsonArray2.get(integer4));
        }
        return arr3;
    }
    
    static {
        ANY = new ItemPredicate();
    }
    
    public static class Builder {
        private final List<EnchantmentPredicate> enchantments;
        @Nullable
        private Item item;
        @Nullable
        private Tag<Item> tag;
        private MinMaxBounds.Ints count;
        private MinMaxBounds.Ints durability;
        @Nullable
        private Potion potion;
        private NbtPredicate nbt;
        
        private Builder() {
            this.enchantments = (List<EnchantmentPredicate>)Lists.newArrayList();
            this.count = MinMaxBounds.Ints.ANY;
            this.durability = MinMaxBounds.Ints.ANY;
            this.nbt = NbtPredicate.ANY;
        }
        
        public static Builder item() {
            return new Builder();
        }
        
        public Builder of(final ItemLike bhq) {
            this.item = bhq.asItem();
            return this;
        }
        
        public Builder of(final Tag<Item> zg) {
            this.tag = zg;
            return this;
        }
        
        public Builder withCount(final MinMaxBounds.Ints d) {
            this.count = d;
            return this;
        }
        
        public Builder hasNbt(final CompoundTag id) {
            this.nbt = new NbtPredicate(id);
            return this;
        }
        
        public Builder hasEnchantment(final EnchantmentPredicate aq) {
            this.enchantments.add(aq);
            return this;
        }
        
        public ItemPredicate build() {
            return new ItemPredicate(this.tag, this.item, this.count, this.durability, (EnchantmentPredicate[])this.enchantments.toArray((Object[])new EnchantmentPredicate[0]), this.potion, this.nbt);
        }
    }
}
