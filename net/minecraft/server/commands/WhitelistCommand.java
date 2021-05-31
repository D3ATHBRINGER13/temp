package net.minecraft.server.commands;

import net.minecraft.server.players.StoredUserList;
import com.mojang.brigadier.Message;
import net.minecraft.server.level.ServerPlayer;
import java.util.stream.Stream;
import net.minecraft.commands.SharedSuggestionProvider;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.StoredUserEntry;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.players.UserWhiteListEntry;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class WhitelistCommand {
    private static final SimpleCommandExceptionType ERROR_ALREADY_ENABLED;
    private static final SimpleCommandExceptionType ERROR_ALREADY_DISABLED;
    private static final SimpleCommandExceptionType ERROR_ALREADY_WHITELISTED;
    private static final SimpleCommandExceptionType ERROR_NOT_WHITELISTED;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("whitelist").requires(cd -> cd.hasPermission(3))).then(Commands.literal("on").executes(commandContext -> enableWhitelist((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("off").executes(commandContext -> disableWhitelist((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("list").executes(commandContext -> showList((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("add").then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)GameProfileArgument.gameProfile()).suggests((commandContext, suggestionsBuilder) -> {
            final PlayerList xv3 = ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList();
            return SharedSuggestionProvider.suggest((Stream<String>)xv3.getPlayers().stream().filter(vl -> !xv3.getWhiteList().isWhiteListed(vl.getGameProfile())).map(vl -> vl.getGameProfile().getName()), suggestionsBuilder);
        }).executes(commandContext -> addPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)commandContext, "targets")))))).then(Commands.literal("remove").then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)GameProfileArgument.gameProfile()).suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getWhiteListNames(), suggestionsBuilder)).executes(commandContext -> removePlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)commandContext, "targets")))))).then(Commands.literal("reload").executes(commandContext -> reload((CommandSourceStack)commandContext.getSource()))));
    }
    
    private static int reload(final CommandSourceStack cd) {
        cd.getServer().getPlayerList().reloadWhiteList();
        cd.sendSuccess(new TranslatableComponent("commands.whitelist.reloaded", new Object[0]), true);
        cd.getServer().kickUnlistedPlayers(cd);
        return 1;
    }
    
    private static int addPlayers(final CommandSourceStack cd, final Collection<GameProfile> collection) throws CommandSyntaxException {
        final UserWhiteList yc3 = cd.getServer().getPlayerList().getWhiteList();
        int integer4 = 0;
        for (final GameProfile gameProfile6 : collection) {
            if (!yc3.isWhiteListed(gameProfile6)) {
                final UserWhiteListEntry yd7 = new UserWhiteListEntry(gameProfile6);
                ((StoredUserList<K, UserWhiteListEntry>)yc3).add(yd7);
                cd.sendSuccess(new TranslatableComponent("commands.whitelist.add.success", new Object[] { ComponentUtils.getDisplayName(gameProfile6) }), true);
                ++integer4;
            }
        }
        if (integer4 == 0) {
            throw WhitelistCommand.ERROR_ALREADY_WHITELISTED.create();
        }
        return integer4;
    }
    
    private static int removePlayers(final CommandSourceStack cd, final Collection<GameProfile> collection) throws CommandSyntaxException {
        final UserWhiteList yc3 = cd.getServer().getPlayerList().getWhiteList();
        int integer4 = 0;
        for (final GameProfile gameProfile6 : collection) {
            if (yc3.isWhiteListed(gameProfile6)) {
                final UserWhiteListEntry yd7 = new UserWhiteListEntry(gameProfile6);
                ((StoredUserList<GameProfile, V>)yc3).remove(yd7);
                cd.sendSuccess(new TranslatableComponent("commands.whitelist.remove.success", new Object[] { ComponentUtils.getDisplayName(gameProfile6) }), true);
                ++integer4;
            }
        }
        if (integer4 == 0) {
            throw WhitelistCommand.ERROR_NOT_WHITELISTED.create();
        }
        cd.getServer().kickUnlistedPlayers(cd);
        return integer4;
    }
    
    private static int enableWhitelist(final CommandSourceStack cd) throws CommandSyntaxException {
        final PlayerList xv2 = cd.getServer().getPlayerList();
        if (xv2.isUsingWhitelist()) {
            throw WhitelistCommand.ERROR_ALREADY_ENABLED.create();
        }
        xv2.setUsingWhiteList(true);
        cd.sendSuccess(new TranslatableComponent("commands.whitelist.enabled", new Object[0]), true);
        cd.getServer().kickUnlistedPlayers(cd);
        return 1;
    }
    
    private static int disableWhitelist(final CommandSourceStack cd) throws CommandSyntaxException {
        final PlayerList xv2 = cd.getServer().getPlayerList();
        if (!xv2.isUsingWhitelist()) {
            throw WhitelistCommand.ERROR_ALREADY_DISABLED.create();
        }
        xv2.setUsingWhiteList(false);
        cd.sendSuccess(new TranslatableComponent("commands.whitelist.disabled", new Object[0]), true);
        return 1;
    }
    
    private static int showList(final CommandSourceStack cd) {
        final String[] arr2 = cd.getServer().getPlayerList().getWhiteListNames();
        if (arr2.length == 0) {
            cd.sendSuccess(new TranslatableComponent("commands.whitelist.none", new Object[0]), false);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.whitelist.list", new Object[] { arr2.length, String.join(", ", (CharSequence[])arr2) }), false);
        }
        return arr2.length;
    }
    
    static {
        ERROR_ALREADY_ENABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.whitelist.alreadyOn", new Object[0]));
        ERROR_ALREADY_DISABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.whitelist.alreadyOff", new Object[0]));
        ERROR_ALREADY_WHITELISTED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.whitelist.add.failed", new Object[0]));
        ERROR_NOT_WHITELISTED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.whitelist.remove.failed", new Object[0]));
    }
}
