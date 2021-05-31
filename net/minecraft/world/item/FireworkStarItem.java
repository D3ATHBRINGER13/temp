package net.minecraft.world.item;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;

public class FireworkStarItem extends Item {
    public FireworkStarItem(final Properties a) {
        super(a);
    }
    
    @Override
    public void appendHoverText(final ItemStack bcj, @Nullable final Level bhr, final List<Component> list, final TooltipFlag bdr) {
        final CompoundTag id6 = bcj.getTagElement("Explosion");
        if (id6 != null) {
            appendHoverText(id6, list);
        }
    }
    
    public static void appendHoverText(final CompoundTag id, final List<Component> list) {
        final FireworkRocketItem.Shape a3 = FireworkRocketItem.Shape.byId(id.getByte("Type"));
        list.add(new TranslatableComponent("item.minecraft.firework_star.shape." + a3.getName(), new Object[0]).withStyle(ChatFormatting.GRAY));
        final int[] arr4 = id.getIntArray("Colors");
        if (arr4.length > 0) {
            list.add(appendColors(new TextComponent("").withStyle(ChatFormatting.GRAY), arr4));
        }
        final int[] arr5 = id.getIntArray("FadeColors");
        if (arr5.length > 0) {
            list.add(appendColors(new TranslatableComponent("item.minecraft.firework_star.fade_to", new Object[0]).append(" ").withStyle(ChatFormatting.GRAY), arr5));
        }
        if (id.getBoolean("Trail")) {
            list.add(new TranslatableComponent("item.minecraft.firework_star.trail", new Object[0]).withStyle(ChatFormatting.GRAY));
        }
        if (id.getBoolean("Flicker")) {
            list.add(new TranslatableComponent("item.minecraft.firework_star.flicker", new Object[0]).withStyle(ChatFormatting.GRAY));
        }
    }
    
    private static Component appendColors(final Component jo, final int[] arr) {
        for (int integer3 = 0; integer3 < arr.length; ++integer3) {
            if (integer3 > 0) {
                jo.append(", ");
            }
            jo.append(getColorName(arr[integer3]));
        }
        return jo;
    }
    
    private static Component getColorName(final int integer) {
        final DyeColor bbg2 = DyeColor.byFireworkColor(integer);
        if (bbg2 == null) {
            return new TranslatableComponent("item.minecraft.firework_star.custom_color", new Object[0]);
        }
        return new TranslatableComponent("item.minecraft.firework_star." + bbg2.getName(), new Object[0]);
    }
}
