package net.minecraft.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class ReloadCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reload").requires(cd -> cd.hasPermission(2))).executes(commandContext -> {
            ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.reload.success", new Object[0]), true);
            ((CommandSourceStack)commandContext.getSource()).getServer().reloadResources();
            return 0;
        }));
    }
}
