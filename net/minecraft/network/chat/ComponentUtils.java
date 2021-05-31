package net.minecraft.network.chat;

import com.mojang.brigadier.Message;
import net.minecraft.ChatFormatting;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.function.Function;
import java.util.Collection;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.world.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;

public class ComponentUtils {
    public static Component mergeStyles(final Component jo, final Style jw) {
        if (jw.isEmpty()) {
            return jo;
        }
        if (jo.getStyle().isEmpty()) {
            return jo.setStyle(jw.copy());
        }
        return new TextComponent("").append(jo).setStyle(jw.copy());
    }
    
    public static Component updateForEntity(@Nullable final CommandSourceStack cd, final Component jo, @Nullable final Entity aio, int integer) throws CommandSyntaxException {
        if (integer > 100) {
            return jo;
        }
        ++integer;
        final Component jo2 = (jo instanceof ContextAwareComponent) ? ((ContextAwareComponent)jo).resolve(cd, aio, integer) : jo.copy();
        for (final Component jo3 : jo.getSiblings()) {
            jo2.append(updateForEntity(cd, jo3, aio, integer));
        }
        return mergeStyles(jo2, jo.getStyle());
    }
    
    public static Component getDisplayName(final GameProfile gameProfile) {
        if (gameProfile.getName() != null) {
            return new TextComponent(gameProfile.getName());
        }
        if (gameProfile.getId() != null) {
            return new TextComponent(gameProfile.getId().toString());
        }
        return new TextComponent("(unknown)");
    }
    
    public static Component formatList(final Collection<String> collection) {
        return ComponentUtils.<String>formatAndSortList(collection, (java.util.function.Function<String, Component>)(string -> new TextComponent(string).withStyle(ChatFormatting.GREEN)));
    }
    
    public static <T extends Comparable<T>> Component formatAndSortList(final Collection<T> collection, final Function<T, Component> function) {
        if (collection.isEmpty()) {
            return new TextComponent("");
        }
        if (collection.size() == 1) {
            return (Component)function.apply(collection.iterator().next());
        }
        final List<T> list3 = (List<T>)Lists.newArrayList((Iterable)collection);
        list3.sort(Comparable::compareTo);
        return ComponentUtils.formatList((java.util.Collection<Object>)collection, (java.util.function.Function<Object, Component>)function);
    }
    
    public static <T> Component formatList(final Collection<T> collection, final Function<T, Component> function) {
        if (collection.isEmpty()) {
            return new TextComponent("");
        }
        if (collection.size() == 1) {
            return (Component)function.apply(collection.iterator().next());
        }
        final Component jo3 = new TextComponent("");
        boolean boolean4 = true;
        for (final T object6 : collection) {
            if (!boolean4) {
                jo3.append(new TextComponent(", ").withStyle(ChatFormatting.GRAY));
            }
            jo3.append((Component)function.apply(object6));
            boolean4 = false;
        }
        return jo3;
    }
    
    public static Component wrapInSquareBrackets(final Component jo) {
        return new TextComponent("[").append(jo).append("]");
    }
    
    public static Component fromMessage(final Message message) {
        if (message instanceof Component) {
            return (Component)message;
        }
        return new TextComponent(message.getString());
    }
}
