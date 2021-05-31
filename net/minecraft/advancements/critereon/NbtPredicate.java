package net.minecraft.advancements.critereon;

import net.minecraft.world.entity.player.Player;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public class NbtPredicate {
    public static final NbtPredicate ANY;
    @Nullable
    private final CompoundTag tag;
    
    public NbtPredicate(@Nullable final CompoundTag id) {
        this.tag = id;
    }
    
    public boolean matches(final ItemStack bcj) {
        return this == NbtPredicate.ANY || this.matches(bcj.getTag());
    }
    
    public boolean matches(final Entity aio) {
        return this == NbtPredicate.ANY || this.matches(getEntityTagToCompare(aio));
    }
    
    public boolean matches(@Nullable final Tag iu) {
        if (iu == null) {
            return this == NbtPredicate.ANY;
        }
        return this.tag == null || NbtUtils.compareNbt(this.tag, iu, true);
    }
    
    public JsonElement serializeToJson() {
        if (this == NbtPredicate.ANY || this.tag == null) {
            return (JsonElement)JsonNull.INSTANCE;
        }
        return (JsonElement)new JsonPrimitive(this.tag.toString());
    }
    
    public static NbtPredicate fromJson(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return NbtPredicate.ANY;
        }
        CompoundTag id2;
        try {
            id2 = TagParser.parseTag(GsonHelper.convertToString(jsonElement, "nbt"));
        }
        catch (CommandSyntaxException commandSyntaxException3) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException3.getMessage());
        }
        return new NbtPredicate(id2);
    }
    
    public static CompoundTag getEntityTagToCompare(final Entity aio) {
        final CompoundTag id2 = aio.saveWithoutId(new CompoundTag());
        if (aio instanceof Player) {
            final ItemStack bcj3 = ((Player)aio).inventory.getSelected();
            if (!bcj3.isEmpty()) {
                id2.put("SelectedItem", (Tag)bcj3.save(new CompoundTag()));
            }
        }
        return id2;
    }
    
    static {
        ANY = new NbtPredicate(null);
    }
}
