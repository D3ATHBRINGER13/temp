package net.minecraft.world.item;

import java.util.Iterator;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.ItemLike;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.network.chat.Component;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class EnchantedBookItem extends Item {
    public EnchantedBookItem(final Properties a) {
        super(a);
    }
    
    @Override
    public boolean isFoil(final ItemStack bcj) {
        return true;
    }
    
    @Override
    public boolean isEnchantable(final ItemStack bcj) {
        return false;
    }
    
    public static ListTag getEnchantments(final ItemStack bcj) {
        final CompoundTag id2 = bcj.getTag();
        if (id2 != null) {
            return id2.getList("StoredEnchantments", 10);
        }
        return new ListTag();
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        super.appendHoverText(bcj, bhr, list, bdr);
        ItemStack.appendEnchantmentNames(list, getEnchantments(bcj));
    }
    
    public static void addEnchantment(final ItemStack bcj, final EnchantmentInstance bfv) {
        final ListTag ik3 = getEnchantments(bcj);
        boolean boolean4 = true;
        final ResourceLocation qv5 = Registry.ENCHANTMENT.getKey(bfv.enchantment);
        for (int integer6 = 0; integer6 < ik3.size(); ++integer6) {
            final CompoundTag id7 = ik3.getCompound(integer6);
            final ResourceLocation qv6 = ResourceLocation.tryParse(id7.getString("id"));
            if (qv6 != null && qv6.equals(qv5)) {
                if (id7.getInt("lvl") < bfv.level) {
                    id7.putShort("lvl", (short)bfv.level);
                }
                boolean4 = false;
                break;
            }
        }
        if (boolean4) {
            final CompoundTag id8 = new CompoundTag();
            id8.putString("id", String.valueOf(qv5));
            id8.putShort("lvl", (short)bfv.level);
            ik3.add(id8);
        }
        bcj.getOrCreateTag().put("StoredEnchantments", (Tag)ik3);
    }
    
    public static ItemStack createForEnchantment(final EnchantmentInstance bfv) {
        final ItemStack bcj2 = new ItemStack(Items.ENCHANTED_BOOK);
        addEnchantment(bcj2, bfv);
        return bcj2;
    }
    
    @Override
    public void fillItemCategory(final CreativeModeTab bba, final NonNullList<ItemStack> fk) {
        if (bba == CreativeModeTab.TAB_SEARCH) {
            for (final Enchantment bfs5 : Registry.ENCHANTMENT) {
                if (bfs5.category != null) {
                    for (int integer6 = bfs5.getMinLevel(); integer6 <= bfs5.getMaxLevel(); ++integer6) {
                        fk.add(createForEnchantment(new EnchantmentInstance(bfs5, integer6)));
                    }
                }
            }
        }
        else if (bba.getEnchantmentCategories().length != 0) {
            for (final Enchantment bfs5 : Registry.ENCHANTMENT) {
                if (bba.hasEnchantmentCategory(bfs5.category)) {
                    fk.add(createForEnchantment(new EnchantmentInstance(bfs5, bfs5.getMaxLevel())));
                }
            }
        }
    }
}
