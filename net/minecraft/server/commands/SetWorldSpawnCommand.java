package net.minecraft.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetSpawnPositionPacket;
import net.minecraft.core.BlockPos;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class SetWorldSpawnCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setworldspawn").requires(cd -> cd.hasPermission(2))).executes(commandContext -> setSpawn((CommandSourceStack)commandContext.getSource(), new BlockPos(((CommandSourceStack)commandContext.getSource()).getPosition())))).then(Commands.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).executes(commandContext -> setSpawn((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getOrLoadBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos")))));
    }
    
    private static int setSpawn(final CommandSourceStack cd, final BlockPos ew) {
        cd.getLevel().setSpawnPos(ew);
        cd.getServer().getPlayerList().broadcastAll(new ClientboundSetSpawnPositionPacket(ew));
        cd.sendSuccess(new TranslatableComponent("commands.setworldspawn.success", new Object[] { ew.getX(), ew.getY(), ew.getZ() }), true);
        return 1;
    }
}
