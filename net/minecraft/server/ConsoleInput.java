package net.minecraft.server;

import net.minecraft.commands.CommandSourceStack;

public class ConsoleInput {
    public final String msg;
    public final CommandSourceStack source;
    
    public ConsoleInput(final String string, final CommandSourceStack cd) {
        this.msg = string;
        this.source = cd;
    }
}
