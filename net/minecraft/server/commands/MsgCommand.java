package net.minecraft.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.arguments.MessageArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class MsgCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        final LiteralCommandNode<CommandSourceStack> literalCommandNode2 = (LiteralCommandNode<CommandSourceStack>)commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("msg").then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.players()).then(Commands.argument("message", (com.mojang.brigadier.arguments.ArgumentType<Object>)MessageArgument.message()).executes(commandContext -> sendMessage((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), MessageArgument.getMessage((CommandContext<CommandSourceStack>)commandContext, "message"))))));
        commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("tell").redirect((CommandNode)literalCommandNode2));
        commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("w").redirect((CommandNode)literalCommandNode2));
    }
    
    private static int sendMessage(final CommandSourceStack cd, final Collection<ServerPlayer> collection, final Component jo) {
        for (final ServerPlayer vl5 : collection) {
            vl5.sendMessage(new TranslatableComponent("commands.message.display.incoming", new Object[] { cd.getDisplayName(), jo.deepCopy() }).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            cd.sendSuccess(new TranslatableComponent("commands.message.display.outgoing", new Object[] { vl5.getDisplayName(), jo.deepCopy() }).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), false);
        }
        return collection.size();
    }
}
