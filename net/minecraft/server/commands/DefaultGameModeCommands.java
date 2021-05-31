package net.minecraft.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class DefaultGameModeCommands {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder2 = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("defaultgamemode").requires(cd -> cd.hasPermission(2));
        for (final GameType bho6 : GameType.values()) {
            if (bho6 != GameType.NOT_SET) {
                literalArgumentBuilder2.then(Commands.literal(bho6.getName()).executes(commandContext -> setMode((CommandSourceStack)commandContext.getSource(), bho6)));
            }
        }
        commandDispatcher.register((LiteralArgumentBuilder)literalArgumentBuilder2);
    }
    
    private static int setMode(final CommandSourceStack cd, final GameType bho) {
        int integer3 = 0;
        final MinecraftServer minecraftServer4 = cd.getServer();
        minecraftServer4.setDefaultGameMode(bho);
        if (minecraftServer4.getForceGameType()) {
            for (final ServerPlayer vl6 : minecraftServer4.getPlayerList().getPlayers()) {
                if (vl6.gameMode.getGameModeForPlayer() != bho) {
                    vl6.setGameMode(bho);
                    ++integer3;
                }
            }
        }
        cd.sendSuccess(new TranslatableComponent("commands.defaultgamemode.success", new Object[] { bho.getDisplayName() }), true);
        return integer3;
    }
}
