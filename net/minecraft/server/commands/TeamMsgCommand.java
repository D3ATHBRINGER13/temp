package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Iterator;
import net.minecraft.network.chat.Style;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class TeamMsgCommand {
    private static final SimpleCommandExceptionType ERROR_NOT_ON_TEAM;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        final LiteralCommandNode<CommandSourceStack> literalCommandNode2 = (LiteralCommandNode<CommandSourceStack>)commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("teammsg").then(Commands.argument("message", (com.mojang.brigadier.arguments.ArgumentType<Object>)MessageArgument.message()).executes(commandContext -> sendMessage((CommandSourceStack)commandContext.getSource(), MessageArgument.getMessage((CommandContext<CommandSourceStack>)commandContext, "message")))));
        commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("tm").redirect((CommandNode)literalCommandNode2));
    }
    
    private static int sendMessage(final CommandSourceStack cd, final Component jo) throws CommandSyntaxException {
        final Entity aio3 = cd.getEntityOrException();
        final PlayerTeam ctg4 = (PlayerTeam)aio3.getTeam();
        if (ctg4 == null) {
            throw TeamMsgCommand.ERROR_NOT_ON_TEAM.create();
        }
        final Consumer<Style> consumer5 = (Consumer<Style>)(jw -> jw.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.type.team.hover", new Object[0]))).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg ")));
        final Component jo2 = ctg4.getFormattedDisplayName().withStyle(consumer5);
        for (final Component jo3 : jo2.getSiblings()) {
            jo3.withStyle(consumer5);
        }
        final List<ServerPlayer> list7 = cd.getServer().getPlayerList().getPlayers();
        for (final ServerPlayer vl9 : list7) {
            if (vl9 == aio3) {
                vl9.sendMessage(new TranslatableComponent("chat.type.team.sent", new Object[] { jo2, cd.getDisplayName(), jo.deepCopy() }));
            }
            else {
                if (vl9.getTeam() != ctg4) {
                    continue;
                }
                vl9.sendMessage(new TranslatableComponent("chat.type.team.text", new Object[] { jo2, cd.getDisplayName(), jo.deepCopy() }));
            }
        }
        return list7.size();
    }
    
    static {
        ERROR_NOT_ON_TEAM = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.teammsg.failed.noteam", new Object[0]));
    }
}
