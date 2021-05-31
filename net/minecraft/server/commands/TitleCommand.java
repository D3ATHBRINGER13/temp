package net.minecraft.server.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.ComponentUtils;
import java.util.Iterator;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitlesPacket;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.arguments.ComponentArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.EntityArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class TitleCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("title").requires(cd -> cd.hasPermission(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.players()).then(Commands.literal("clear").executes(commandContext -> clearTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"))))).then(Commands.literal("reset").executes(commandContext -> resetTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"))))).then(Commands.literal("title").then(Commands.argument("title", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgument.textComponent()).executes(commandContext -> showTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "title"), ClientboundSetTitlesPacket.Type.TITLE))))).then(Commands.literal("subtitle").then(Commands.argument("title", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgument.textComponent()).executes(commandContext -> showTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "title"), ClientboundSetTitlesPacket.Type.SUBTITLE))))).then(Commands.literal("actionbar").then(Commands.argument("title", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgument.textComponent()).executes(commandContext -> showTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "title"), ClientboundSetTitlesPacket.Type.ACTIONBAR))))).then(Commands.literal("times").then(Commands.argument("fadeIn", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).then(Commands.argument("stay", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).then(Commands.argument("fadeOut", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> setTimes((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "fadeIn"), IntegerArgumentType.getInteger(commandContext, "stay"), IntegerArgumentType.getInteger(commandContext, "fadeOut")))))))));
    }
    
    private static int clearTitle(final CommandSourceStack cd, final Collection<ServerPlayer> collection) {
        final ClientboundSetTitlesPacket nf3 = new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.CLEAR, null);
        for (final ServerPlayer vl5 : collection) {
            vl5.connection.send(nf3);
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.title.cleared.single", new Object[] { ((ServerPlayer)collection.iterator().next()).getDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.title.cleared.multiple", new Object[] { collection.size() }), true);
        }
        return collection.size();
    }
    
    private static int resetTitle(final CommandSourceStack cd, final Collection<ServerPlayer> collection) {
        final ClientboundSetTitlesPacket nf3 = new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.RESET, null);
        for (final ServerPlayer vl5 : collection) {
            vl5.connection.send(nf3);
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.title.reset.single", new Object[] { ((ServerPlayer)collection.iterator().next()).getDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.title.reset.multiple", new Object[] { collection.size() }), true);
        }
        return collection.size();
    }
    
    private static int showTitle(final CommandSourceStack cd, final Collection<ServerPlayer> collection, final Component jo, final ClientboundSetTitlesPacket.Type a) throws CommandSyntaxException {
        for (final ServerPlayer vl6 : collection) {
            vl6.connection.send(new ClientboundSetTitlesPacket(a, ComponentUtils.updateForEntity(cd, jo, vl6, 0)));
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.title.show." + a.name().toLowerCase(Locale.ROOT) + ".single", new Object[] { ((ServerPlayer)collection.iterator().next()).getDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.title.show." + a.name().toLowerCase(Locale.ROOT) + ".multiple", new Object[] { collection.size() }), true);
        }
        return collection.size();
    }
    
    private static int setTimes(final CommandSourceStack cd, final Collection<ServerPlayer> collection, final int integer3, final int integer4, final int integer5) {
        final ClientboundSetTitlesPacket nf6 = new ClientboundSetTitlesPacket(integer3, integer4, integer5);
        for (final ServerPlayer vl8 : collection) {
            vl8.connection.send(nf6);
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.title.times.single", new Object[] { ((ServerPlayer)collection.iterator().next()).getDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.title.times.multiple", new Object[] { collection.size() }), true);
        }
        return collection.size();
    }
}
