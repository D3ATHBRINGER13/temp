package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class SaveAllCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-all").requires(cd -> cd.hasPermission(4))).executes(commandContext -> saveAll((CommandSourceStack)commandContext.getSource(), false))).then(Commands.literal("flush").executes(commandContext -> saveAll((CommandSourceStack)commandContext.getSource(), true))));
    }
    
    private static int saveAll(final CommandSourceStack cd, final boolean boolean2) throws CommandSyntaxException {
        cd.sendSuccess(new TranslatableComponent("commands.save.saving", new Object[0]), false);
        final MinecraftServer minecraftServer3 = cd.getServer();
        minecraftServer3.getPlayerList().saveAll();
        final boolean boolean3 = minecraftServer3.saveAllChunks(true, boolean2, true);
        if (!boolean3) {
            throw SaveAllCommand.ERROR_FAILED.create();
        }
        cd.sendSuccess(new TranslatableComponent("commands.save.success", new Object[0]), true);
        return 1;
    }
    
    static {
        ERROR_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.save.failed", new Object[0]));
    }
}
