package net.minecraft.server.commands;

import net.minecraft.server.players.StoredUserList;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.level.ServerPlayer;
import java.util.Iterator;
import net.minecraft.server.players.UserBanList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.ComponentUtils;
import java.util.Date;
import net.minecraft.server.players.UserBanListEntry;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import net.minecraft.commands.arguments.MessageArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.GameProfileArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class BanPlayerCommands {
    private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ban").requires(cd -> cd.getServer().getPlayerList().getBans().isEnabled() && cd.hasPermission(3))).then(((RequiredArgumentBuilder)Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)GameProfileArgument.gameProfile()).executes(commandContext -> banPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)commandContext, "targets"), null))).then(Commands.argument("reason", (com.mojang.brigadier.arguments.ArgumentType<Object>)MessageArgument.message()).executes(commandContext -> banPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)commandContext, "targets"), MessageArgument.getMessage((CommandContext<CommandSourceStack>)commandContext, "reason"))))));
    }
    
    private static int banPlayers(final CommandSourceStack cd, final Collection<GameProfile> collection, @Nullable final Component jo) throws CommandSyntaxException {
        final UserBanList ya4 = cd.getServer().getPlayerList().getBans();
        int integer5 = 0;
        for (final GameProfile gameProfile7 : collection) {
            if (!ya4.isBanned(gameProfile7)) {
                final UserBanListEntry yb8 = new UserBanListEntry(gameProfile7, null, cd.getTextName(), null, (jo == null) ? null : jo.getString());
                ((StoredUserList<K, UserBanListEntry>)ya4).add(yb8);
                ++integer5;
                cd.sendSuccess(new TranslatableComponent("commands.ban.success", new Object[] { ComponentUtils.getDisplayName(gameProfile7), yb8.getReason() }), true);
                final ServerPlayer vl9 = cd.getServer().getPlayerList().getPlayer(gameProfile7.getId());
                if (vl9 == null) {
                    continue;
                }
                vl9.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.banned", new Object[0]));
            }
        }
        if (integer5 == 0) {
            throw BanPlayerCommands.ERROR_ALREADY_BANNED.create();
        }
        return integer5;
    }
    
    static {
        ERROR_ALREADY_BANNED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.ban.failed", new Object[0]));
    }
}
