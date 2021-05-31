package net.minecraft.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;

public class CommandRuntimeException extends RuntimeException {
    private final Component message;
    
    public CommandRuntimeException(final Component jo) {
        super(jo.getContents(), (Throwable)null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
        this.message = jo;
    }
    
    public Component getComponent() {
        return this.message;
    }
}
