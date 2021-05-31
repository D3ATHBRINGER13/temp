package net.minecraft.server.commands;

import net.minecraft.server.players.StoredUserList;
import net.minecraft.server.players.PlayerList;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.players.BanListEntry;
import java.util.Collection;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class BanListCommands {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("banlist").requires(cd -> (cd.getServer().getPlayerList().getBans().isEnabled() || cd.getServer().getPlayerList().getIpBans().isEnabled()) && cd.hasPermission(3))).executes(commandContext -> {
            final PlayerList xv2 = ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList();
            return showList((CommandSourceStack)commandContext.getSource(), Lists.newArrayList(Iterables.concat((Iterable)xv2.getBans().getEntries(), (Iterable)xv2.getIpBans().getEntries())));
        })).then(Commands.literal("ips").executes(commandContext -> showList((CommandSourceStack)commandContext.getSource(), (((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getIpBans()).getEntries())))).then(Commands.literal("players").executes(commandContext -> showList((CommandSourceStack)commandContext.getSource(), (((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getBans()).getEntries()))));
    }
    
    private static int showList(final CommandSourceStack cd, final Collection<? extends BanListEntry<?>> collection) {
        if (collection.isEmpty()) {
            cd.sendSuccess(new TranslatableComponent("commands.banlist.none", new Object[0]), false);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.banlist.list", new Object[] { collection.size() }), false);
            for (final BanListEntry<?> xq4 : collection) {
                cd.sendSuccess(new TranslatableComponent("commands.banlist.entry", new Object[] { xq4.getDisplayName(), xq4.getSource(), xq4.getReason() }), false);
            }
        }
        return collection.size();
    }
}
