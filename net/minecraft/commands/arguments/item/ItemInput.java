package net.minecraft.commands.arguments.item;

import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ItemLike;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import net.minecraft.world.item.ItemStack;
import java.util.function.Predicate;

public class ItemInput implements Predicate<ItemStack> {
    private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG;
    private final Item item;
    @Nullable
    private final CompoundTag tag;
    
    public ItemInput(final Item bce, @Nullable final CompoundTag id) {
        this.item = bce;
        this.tag = id;
    }
    
    public Item getItem() {
        return this.item;
    }
    
    public boolean test(final ItemStack bcj) {
        return bcj.getItem() == this.item && NbtUtils.compareNbt(this.tag, bcj.getTag(), true);
    }
    
    public ItemStack createItemStack(final int integer, final boolean boolean2) throws CommandSyntaxException {
        final ItemStack bcj4 = new ItemStack(this.item, integer);
        if (this.tag != null) {
            bcj4.setTag(this.tag);
        }
        if (boolean2 && integer > bcj4.getMaxStackSize()) {
            throw ItemInput.ERROR_STACK_TOO_BIG.create(Registry.ITEM.getKey(this.item), bcj4.getMaxStackSize());
        }
        return bcj4;
    }
    
    public String serialize() {
        final StringBuilder stringBuilder2 = new StringBuilder(Registry.ITEM.getId(this.item));
        if (this.tag != null) {
            stringBuilder2.append(this.tag);
        }
        return stringBuilder2.toString();
    }
    
    static {
        ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableComponent("arguments.item.overstacked", new Object[] { object1, object2 }));
    }
}
