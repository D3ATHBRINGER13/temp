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

public class SaveOffCommand {
    private static final SimpleCommandExceptionType ERROR_ALREADY_OFF;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-off").requires(cd -> cd.hasPermission(4))).executes(commandContext -> {
            final CommandSourceStack cd2 = (CommandSourceStack)commandContext.getSource();
            boolean boolean3 = false;
            for (final ServerLevel vk5 : cd2.getServer().getAllLevels()) {
                if (vk5 != null && !vk5.noSave) {
                    vk5.noSave = true;
                    boolean3 = true;
                }
            }
            if (!boolean3) {
                throw SaveOffCommand.ERROR_ALREADY_OFF.create();
            }
            cd2.sendSuccess(new TranslatableComponent("commands.save.disabled", new Object[0]), true);
            return 1;
        }));
    }
    
    static {
        ERROR_ALREADY_OFF = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.save.alreadyOff", new Object[0]));
    }
}
