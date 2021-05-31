package net.minecraft.commands.arguments;

import java.util.stream.Collectors;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.Message;
import net.minecraft.commands.CommandSourceStack;
import java.util.stream.Stream;
import net.minecraft.commands.SharedSuggestionProvider;
import com.google.common.collect.Streams;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import net.minecraft.world.level.dimension.DimensionType;
import com.mojang.brigadier.arguments.ArgumentType;

public class DimensionTypeArgument implements ArgumentType<DimensionType> {
    private static final Collection<String> EXAMPLES;
    public static final DynamicCommandExceptionType ERROR_INVALID_VALUE;
    
    public DimensionType parse(final StringReader stringReader) throws CommandSyntaxException {
        final ResourceLocation qv3 = ResourceLocation.read(stringReader);
        return (DimensionType)Registry.DIMENSION_TYPE.getOptional(qv3).orElseThrow(() -> DimensionTypeArgument.ERROR_INVALID_VALUE.create(qv3));
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> commandContext, final SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource((Stream<ResourceLocation>)Streams.stream((Iterable)DimensionType.getAllTypes()).map(DimensionType::getName), suggestionsBuilder);
    }
    
    public Collection<String> getExamples() {
        return DimensionTypeArgument.EXAMPLES;
    }
    
    public static DimensionTypeArgument dimension() {
        return new DimensionTypeArgument();
    }
    
    public static DimensionType getDimension(final CommandContext<CommandSourceStack> commandContext, final String string) {
        return (DimensionType)commandContext.getArgument(string, (Class)DimensionType.class);
    }
    
    static {
        EXAMPLES = (Collection)Stream.of((Object[])new DimensionType[] { DimensionType.OVERWORLD, DimensionType.NETHER }).map(byn -> DimensionType.getName(byn).toString()).collect(Collectors.toList());
        ERROR_INVALID_VALUE = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.dimension.invalid", new Object[] { object }));
    }
}
