package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.TextComponent;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

public class ComponentRenderUtils {
    public static String stripColor(final String string, final boolean boolean2) {
        if (boolean2 || Minecraft.getInstance().options.chatColors) {
            return string;
        }
        return ChatFormatting.stripFormatting(string);
    }
    
    public static List<Component> wrapComponents(final Component jo, final int integer, final Font cyu, final boolean boolean4, final boolean boolean5) {
        int integer2 = 0;
        Component jo2 = new TextComponent("");
        final List<Component> list8 = (List<Component>)Lists.newArrayList();
        final List<Component> list9 = (List<Component>)Lists.newArrayList((Iterable)jo);
        for (int integer3 = 0; integer3 < list9.size(); ++integer3) {
            final Component jo3 = (Component)list9.get(integer3);
            String string12 = jo3.getContents();
            boolean boolean6 = false;
            if (string12.contains("\n")) {
                final int integer4 = string12.indexOf(10);
                final String string13 = string12.substring(integer4 + 1);
                string12 = string12.substring(0, integer4 + 1);
                final Component jo4 = new TextComponent(string13).setStyle(jo3.getStyle().copy());
                list9.add(integer3 + 1, jo4);
                boolean6 = true;
            }
            String string14 = stripColor(jo3.getStyle().getLegacyFormatCodes() + string12, boolean5);
            final String string13 = string14.endsWith("\n") ? string14.substring(0, string14.length() - 1) : string14;
            int integer5 = cyu.width(string13);
            Component jo5 = new TextComponent(string13).setStyle(jo3.getStyle().copy());
            if (integer2 + integer5 > integer) {
                String string15 = cyu.substrByWidth(string14, integer - integer2, false);
                String string16 = (string15.length() < string14.length()) ? string14.substring(string15.length()) : null;
                if (string16 != null && !string16.isEmpty()) {
                    int integer6 = (string16.charAt(0) != ' ') ? string15.lastIndexOf(32) : string15.length();
                    if (integer6 >= 0 && cyu.width(string14.substring(0, integer6)) > 0) {
                        string15 = string14.substring(0, integer6);
                        if (boolean4) {
                            ++integer6;
                        }
                        string16 = string14.substring(integer6);
                    }
                    else if (integer2 > 0 && !string14.contains(" ")) {
                        string15 = "";
                        string16 = string14;
                    }
                    final Component jo6 = new TextComponent(string16).setStyle(jo3.getStyle().copy());
                    list9.add(integer3 + 1, jo6);
                }
                string14 = string15;
                integer5 = cyu.width(string14);
                jo5 = new TextComponent(string14);
                jo5.setStyle(jo3.getStyle().copy());
                boolean6 = true;
            }
            if (integer2 + integer5 <= integer) {
                integer2 += integer5;
                jo2.append(jo5);
            }
            else {
                boolean6 = true;
            }
            if (boolean6) {
                list8.add(jo2);
                integer2 = 0;
                jo2 = new TextComponent("");
            }
        }
        list8.add(jo2);
        return list8;
    }
}
