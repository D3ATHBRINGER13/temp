package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Arrays;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.advancements.critereon.MinMaxBounds;

public interface RangeArgument<T extends MinMaxBounds<?>> extends ArgumentType<T> {
    default Ints intRange() {
        return new Ints();
    }
    
    public static class Ints implements RangeArgument<MinMaxBounds.Ints> {
        private static final Collection<String> EXAMPLES;
        
        public static MinMaxBounds.Ints getRange(final CommandContext<CommandSourceStack> commandContext, final String string) {
            return (MinMaxBounds.Ints)commandContext.getArgument(string, (Class)MinMaxBounds.Ints.class);
        }
        
        public MinMaxBounds.Ints parse(final StringReader stringReader) throws CommandSyntaxException {
            return MinMaxBounds.Ints.fromReader(stringReader);
        }
        
        public Collection<String> getExamples() {
            return Ints.EXAMPLES;
        }
        
        static {
            EXAMPLES = (Collection)Arrays.asList((Object[])new String[] { "0..5", "0", "-5", "-100..", "..100" });
        }
        
        public static class Serializer extends RangeArgument.Serializer<Ints> {
            public Ints deserializeFromNetwork(final FriendlyByteBuf je) {
                return new Ints();
            }
        }
    }
    
    public static class Floats implements RangeArgument<MinMaxBounds.Floats> {
        private static final Collection<String> EXAMPLES;
        
        public MinMaxBounds.Floats parse(final StringReader stringReader) throws CommandSyntaxException {
            return MinMaxBounds.Floats.fromReader(stringReader);
        }
        
        public Collection<String> getExamples() {
            return Floats.EXAMPLES;
        }
        
        static {
            EXAMPLES = (Collection)Arrays.asList((Object[])new String[] { "0..5.2", "0", "-5.4", "-100.76..", "..100" });
        }
        
        public static class Serializer extends RangeArgument.Serializer<Floats> {
            public Floats deserializeFromNetwork(final FriendlyByteBuf je) {
                return new Floats();
            }
        }
    }
    
    public abstract static class Serializer<T extends RangeArgument<?>> implements ArgumentSerializer<T> {
        public void serializeToNetwork(final T cx, final FriendlyByteBuf je) {
        }
        
        public void serializeToJson(final T cx, final JsonObject jsonObject) {
        }
    }
}
