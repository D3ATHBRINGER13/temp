package net.minecraft.server.commands;

import java.util.Collections;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.EntityArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class SetSpawnCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawnpoint").requires(cd -> cd.hasPermission(2))).executes(commandContext -> setSpawn((CommandSourceStack)commandContext.getSource(), (Collection<ServerPlayer>)Collections.singleton(((CommandSourceStack)commandContext.getSource()).getPlayerOrException()), new BlockPos(((CommandSourceStack)commandContext.getSource()).getPosition())))).then(((RequiredArgumentBuilder)Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.players()).executes(commandContext -> setSpawn((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), new BlockPos(((CommandSourceStack)commandContext.getSource()).getPosition())))).then(Commands.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).executes(commandContext -> setSpawn((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), BlockPosArgument.getOrLoadBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"))))));
    }
    
    private static int setSpawn(final CommandSourceStack cd, final Collection<ServerPlayer> collection, final BlockPos ew) {
        for (final ServerPlayer vl5 : collection) {
            vl5.setRespawnPosition(ew, true);
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.spawnpoint.success.single", new Object[] { ew.getX(), ew.getY(), ew.getZ(), ((ServerPlayer)collection.iterator().next()).getDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.spawnpoint.success.multiple", new Object[] { ew.getX(), ew.getY(), ew.getZ(), collection.size() }), true);
        }
        return collection.size();
    }
}
