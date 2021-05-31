package net.minecraft.commands.arguments.item;

import java.util.Arrays;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.Message;
import java.util.Collections;
import com.mojang.datafixers.util.Either;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.tags.Tag;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.resources.ResourceLocation;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class FunctionArgument implements ArgumentType<Result> {
    private static final Collection<String> EXAMPLES;
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG;
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION;
    
    public static FunctionArgument functions() {
        return new FunctionArgument();
    }
    
    public Result parse(final StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '#') {
            stringReader.skip();
            final ResourceLocation qv3 = ResourceLocation.read(stringReader);
            return new Result() {
                public Collection<CommandFunction> create(final CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
                    final Tag<CommandFunction> zg3 = getFunctionTag(commandContext, qv3);
                    return zg3.getValues();
                }
                
                public Either<CommandFunction, Tag<CommandFunction>> unwrap(final CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
                    return (Either<CommandFunction, Tag<CommandFunction>>)Either.right(getFunctionTag(commandContext, qv3));
                }
            };
        }
        final ResourceLocation qv3 = ResourceLocation.read(stringReader);
        return new Result() {
            public Collection<CommandFunction> create(final CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
                return (Collection<CommandFunction>)Collections.singleton(getFunction(commandContext, qv3));
            }
            
            public Either<CommandFunction, Tag<CommandFunction>> unwrap(final CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
                return (Either<CommandFunction, Tag<CommandFunction>>)Either.left(getFunction(commandContext, qv3));
            }
        };
    }
    
    private static CommandFunction getFunction(final CommandContext<CommandSourceStack> commandContext, final ResourceLocation qv) throws CommandSyntaxException {
        return (CommandFunction)((CommandSourceStack)commandContext.getSource()).getServer().getFunctions().get(qv).orElseThrow(() -> FunctionArgument.ERROR_UNKNOWN_FUNCTION.create(qv.toString()));
    }
    
    private static Tag<CommandFunction> getFunctionTag(final CommandContext<CommandSourceStack> commandContext, final ResourceLocation qv) throws CommandSyntaxException {
        final Tag<CommandFunction> zg3 = ((CommandSourceStack)commandContext.getSource()).getServer().getFunctions().getTags().getTag(qv);
        if (zg3 == null) {
            throw FunctionArgument.ERROR_UNKNOWN_TAG.create(qv.toString());
        }
        return zg3;
    }
    
    public static Collection<CommandFunction> getFunctions(final CommandContext<CommandSourceStack> commandContext, final String string) throws CommandSyntaxException {
        return ((Result)commandContext.getArgument(string, (Class)Result.class)).create(commandContext);
    }
    
    public static Either<CommandFunction, Tag<CommandFunction>> getFunctionOrTag(final CommandContext<CommandSourceStack> commandContext, final String string) throws CommandSyntaxException {
        return ((Result)commandContext.getArgument(string, (Class)Result.class)).unwrap(commandContext);
    }
    
    public Collection<String> getExamples() {
        return FunctionArgument.EXAMPLES;
    }
    
    static {
        EXAMPLES = (Collection)Arrays.asList((Object[])new String[] { "foo", "foo:bar", "#foo" });
        ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType(object -> new TranslatableComponent("arguments.function.tag.unknown", new Object[] { object }));
        ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType(object -> new TranslatableComponent("arguments.function.unknown", new Object[] { object }));
    }
    
    public interface Result {
        Collection<CommandFunction> create(final CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException;
        
        Either<CommandFunction, Tag<CommandFunction>> unwrap(final CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException;
    }
}
