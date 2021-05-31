package net.minecraft.server.commands;

import java.util.Collections;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.Collection;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.level.GameType;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class GameModeCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder2 = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("gamemode").requires(cd -> cd.hasPermission(2));
        for (final GameType bho6 : GameType.values()) {
            if (bho6 != GameType.NOT_SET) {
                literalArgumentBuilder2.then(((LiteralArgumentBuilder)Commands.literal(bho6.getName()).executes(commandContext -> setMode((CommandContext<CommandSourceStack>)commandContext, (Collection<ServerPlayer>)Collections.singleton(((CommandSourceStack)commandContext.getSource()).getPlayerOrException()), bho6))).then(Commands.argument("target", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.players()).executes(commandContext -> setMode((CommandContext<CommandSourceStack>)commandContext, EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "target"), bho6))));
            }
        }
        commandDispatcher.register((LiteralArgumentBuilder)literalArgumentBuilder2);
    }
    
    private static void logGamemodeChange(final CommandSourceStack cd, final ServerPlayer vl, final GameType bho) {
        final Component jo4 = new TranslatableComponent("gameMode." + bho.getName(), new Object[0]);
        if (cd.getEntity() == vl) {
            cd.sendSuccess(new TranslatableComponent("commands.gamemode.success.self", new Object[] { jo4 }), true);
        }
        else {
            if (cd.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
                vl.sendMessage(new TranslatableComponent("gameMode.changed", new Object[] { jo4 }));
            }
            cd.sendSuccess(new TranslatableComponent("commands.gamemode.success.other", new Object[] { vl.getDisplayName(), jo4 }), true);
        }
    }
    
    private static int setMode(final CommandContext<CommandSourceStack> commandContext, final Collection<ServerPlayer> collection, final GameType bho) {
        int integer4 = 0;
        for (final ServerPlayer vl6 : collection) {
            if (vl6.gameMode.getGameModeForPlayer() != bho) {
                vl6.setGameMode(bho);
                logGamemodeChange((CommandSourceStack)commandContext.getSource(), vl6, bho);
                ++integer4;
            }
        }
        return integer4;
    }
}
