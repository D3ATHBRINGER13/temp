package net.minecraft.commands;

import java.util.Optional;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.resources.ResourceLocation;

public class CommandFunction {
    private final Entry[] entries;
    private final ResourceLocation id;
    
    public CommandFunction(final ResourceLocation qv, final Entry[] arr) {
        this.id = qv;
        this.entries = arr;
    }
    
    public ResourceLocation getId() {
        return this.id;
    }
    
    public Entry[] getEntries() {
        return this.entries;
    }
    
    public static CommandFunction fromLines(final ResourceLocation qv, final ServerFunctionManager rh, final List<String> list) {
        final List<Entry> list2 = (List<Entry>)Lists.newArrayListWithCapacity(list.size());
        for (int integer5 = 0; integer5 < list.size(); ++integer5) {
            final int integer6 = integer5 + 1;
            final String string7 = ((String)list.get(integer5)).trim();
            final StringReader stringReader8 = new StringReader(string7);
            if (stringReader8.canRead()) {
                if (stringReader8.peek() != '#') {
                    if (stringReader8.peek() == '/') {
                        stringReader8.skip();
                        if (stringReader8.peek() == '/') {
                            throw new IllegalArgumentException("Unknown or invalid command '" + string7 + "' on line " + integer6 + " (if you intended to make a comment, use '#' not '//')");
                        }
                        final String string8 = stringReader8.readUnquotedString();
                        throw new IllegalArgumentException("Unknown or invalid command '" + string7 + "' on line " + integer6 + " (did you mean '" + string8 + "'? Do not use a preceding forwards slash.)");
                    }
                    else {
                        try {
                            final ParseResults<CommandSourceStack> parseResults9 = (ParseResults<CommandSourceStack>)rh.getServer().getCommands().getDispatcher().parse(stringReader8, rh.getCompilationContext());
                            if (parseResults9.getReader().canRead()) {
                                if (parseResults9.getExceptions().size() == 1) {
                                    throw (CommandSyntaxException)parseResults9.getExceptions().values().iterator().next();
                                }
                                if (parseResults9.getContext().getRange().isEmpty()) {
                                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults9.getReader());
                                }
                                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseResults9.getReader());
                            }
                            else {
                                list2.add(new CommandEntry(parseResults9));
                            }
                        }
                        catch (CommandSyntaxException commandSyntaxException9) {
                            throw new IllegalArgumentException(new StringBuilder().append("Whilst parsing command on line ").append(integer6).append(": ").append(commandSyntaxException9.getMessage()).toString());
                        }
                    }
                }
            }
        }
        return new CommandFunction(qv, (Entry[])list2.toArray((Object[])new Entry[0]));
    }
    
    public static class CommandEntry implements Entry {
        private final ParseResults<CommandSourceStack> parse;
        
        public CommandEntry(final ParseResults<CommandSourceStack> parseResults) {
            this.parse = parseResults;
        }
        
        public void execute(final ServerFunctionManager rh, final CommandSourceStack cd, final ArrayDeque<ServerFunctionManager.QueuedCommand> arrayDeque, final int integer) throws CommandSyntaxException {
            rh.getDispatcher().execute(new ParseResults(this.parse.getContext().withSource(cd), this.parse.getReader(), this.parse.getExceptions()));
        }
        
        public String toString() {
            return this.parse.getReader().getString();
        }
    }
    
    public static class FunctionEntry implements Entry {
        private final CacheableFunction function;
        
        public FunctionEntry(final CommandFunction ca) {
            this.function = new CacheableFunction(ca);
        }
        
        public void execute(final ServerFunctionManager rh, final CommandSourceStack cd, final ArrayDeque<ServerFunctionManager.QueuedCommand> arrayDeque, final int integer) {
            this.function.get(rh).ifPresent(ca -> {
                final Entry[] arr6 = ca.getEntries();
                final int integer2 = integer - arrayDeque.size();
                final int integer3 = Math.min(arr6.length, integer2);
                for (int integer4 = integer3 - 1; integer4 >= 0; --integer4) {
                    arrayDeque.addFirst(new ServerFunctionManager.QueuedCommand(rh, cd, arr6[integer4]));
                }
            });
        }
        
        public String toString() {
            return new StringBuilder().append("function ").append(this.function.getId()).toString();
        }
    }
    
    public static class CacheableFunction {
        public static final CacheableFunction NONE;
        @Nullable
        private final ResourceLocation id;
        private boolean resolved;
        private Optional<CommandFunction> function;
        
        public CacheableFunction(@Nullable final ResourceLocation qv) {
            this.function = (Optional<CommandFunction>)Optional.empty();
            this.id = qv;
        }
        
        public CacheableFunction(final CommandFunction ca) {
            this.function = (Optional<CommandFunction>)Optional.empty();
            this.resolved = true;
            this.id = null;
            this.function = (Optional<CommandFunction>)Optional.of(ca);
        }
        
        public Optional<CommandFunction> get(final ServerFunctionManager rh) {
            if (!this.resolved) {
                if (this.id != null) {
                    this.function = rh.get(this.id);
                }
                this.resolved = true;
            }
            return this.function;
        }
        
        @Nullable
        public ResourceLocation getId() {
            return (ResourceLocation)this.function.map(ca -> ca.id).orElse(this.id);
        }
        
        static {
            NONE = new CacheableFunction((ResourceLocation)null);
        }
    }
    
    public interface Entry {
        void execute(final ServerFunctionManager rh, final CommandSourceStack cd, final ArrayDeque<ServerFunctionManager.QueuedCommand> arrayDeque, final int integer) throws CommandSyntaxException;
    }
}
