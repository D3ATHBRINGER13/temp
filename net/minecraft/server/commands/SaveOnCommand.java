package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class SaveOnCommand {
    private static final SimpleCommandExceptionType ERROR_ALREADY_ON;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-on").requires(cd -> cd.hasPermission(4))).executes(commandContext -> {
            final CommandSourceStack cd2 = (CommandSourceStack)commandContext.getSource();
            boolean boolean3 = false;
            for (final ServerLevel vk5 : cd2.getServer().getAllLevels()) {
                if (vk5 != null && vk5.noSave) {
                    vk5.noSave = false;
                    boolean3 = true;
                }
            }
            if (!boolean3) {
                throw SaveOnCommand.ERROR_ALREADY_ON.create();
            }
            cd2.sendSuccess(new TranslatableComponent("commands.save.enabled", new Object[0]), true);
            return 1;
        }));
    }
    
    static {
        ERROR_ALREADY_ON = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.save.alreadyOn", new Object[0]));
    }
}
