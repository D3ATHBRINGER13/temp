package net.minecraft.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class KillCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kill").requires(cd -> cd.hasPermission(2))).then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.entities()).executes(commandContext -> kill((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets")))));
    }
    
    private static int kill(final CommandSourceStack cd, final Collection<? extends Entity> collection) {
        for (final Entity aio4 : collection) {
            aio4.kill();
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.kill.success.single", new Object[] { ((Entity)collection.iterator().next()).getDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.kill.success.multiple", new Object[] { collection.size() }), true);
        }
        return collection.size();
    }
}
