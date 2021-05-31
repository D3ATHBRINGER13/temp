package net.minecraft.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class SayCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("say").requires(cd -> cd.hasPermission(2))).then(Commands.argument("message", (com.mojang.brigadier.arguments.ArgumentType<Object>)MessageArgument.message()).executes(commandContext -> {
            final Component jo2 = MessageArgument.getMessage((CommandContext<CommandSourceStack>)commandContext, "message");
            ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().broadcastMessage(new TranslatableComponent("chat.type.announcement", new Object[] { ((CommandSourceStack)commandContext.getSource()).getDisplayName(), jo2 }));
            return 1;
        })));
    }
}
