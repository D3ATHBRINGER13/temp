package net.minecraft.commands.synchronization;

import com.mojang.brigadier.suggestion.Suggestions;
import com.google.common.collect.Maps;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.Util;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.Message;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.Registry;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public class SuggestionProviders {
    private static final Map<ResourceLocation, SuggestionProvider<SharedSuggestionProvider>> PROVIDERS_BY_NAME;
    private static final ResourceLocation DEFAULT_NAME;
    public static final SuggestionProvider<SharedSuggestionProvider> ASK_SERVER;
    public static final SuggestionProvider<CommandSourceStack> ALL_RECIPES;
    public static final SuggestionProvider<CommandSourceStack> AVAILABLE_SOUNDS;
    public static final SuggestionProvider<CommandSourceStack> SUMMONABLE_ENTITIES;
    
    public static <S extends SharedSuggestionProvider> SuggestionProvider<S> register(final ResourceLocation qv, final SuggestionProvider<SharedSuggestionProvider> suggestionProvider) {
        if (SuggestionProviders.PROVIDERS_BY_NAME.containsKey(qv)) {
            throw new IllegalArgumentException(new StringBuilder().append("A command suggestion provider is already registered with the name ").append(qv).toString());
        }
        SuggestionProviders.PROVIDERS_BY_NAME.put(qv, suggestionProvider);
        return (SuggestionProvider<S>)new Wrapper(qv, suggestionProvider);
    }
    
    public static SuggestionProvider<SharedSuggestionProvider> getProvider(final ResourceLocation qv) {
        return (SuggestionProvider<SharedSuggestionProvider>)SuggestionProviders.PROVIDERS_BY_NAME.getOrDefault(qv, SuggestionProviders.ASK_SERVER);
    }
    
    public static ResourceLocation getName(final SuggestionProvider<SharedSuggestionProvider> suggestionProvider) {
        if (suggestionProvider instanceof Wrapper) {
            return ((Wrapper)suggestionProvider).name;
        }
        return SuggestionProviders.DEFAULT_NAME;
    }
    
    public static SuggestionProvider<SharedSuggestionProvider> safelySwap(final SuggestionProvider<SharedSuggestionProvider> suggestionProvider) {
        if (suggestionProvider instanceof Wrapper) {
            return suggestionProvider;
        }
        return SuggestionProviders.ASK_SERVER;
    }
    
    static {
        PROVIDERS_BY_NAME = (Map)Maps.newHashMap();
        DEFAULT_NAME = new ResourceLocation("ask_server");
        ASK_SERVER = SuggestionProviders.<SharedSuggestionProvider>register(SuggestionProviders.DEFAULT_NAME, (SuggestionProvider<SharedSuggestionProvider>)((commandContext, suggestionsBuilder) -> ((SharedSuggestionProvider)commandContext.getSource()).customSuggestion((CommandContext<SharedSuggestionProvider>)commandContext, suggestionsBuilder)));
        ALL_RECIPES = SuggestionProviders.<CommandSourceStack>register(new ResourceLocation("all_recipes"), (SuggestionProvider<SharedSuggestionProvider>)((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggestResource(((SharedSuggestionProvider)commandContext.getSource()).getRecipeNames(), suggestionsBuilder)));
        AVAILABLE_SOUNDS = SuggestionProviders.<CommandSourceStack>register(new ResourceLocation("available_sounds"), (SuggestionProvider<SharedSuggestionProvider>)((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggestResource((Iterable<ResourceLocation>)((SharedSuggestionProvider)commandContext.getSource()).getAvailableSoundEvents(), suggestionsBuilder)));
        SUMMONABLE_ENTITIES = SuggestionProviders.<CommandSourceStack>register(new ResourceLocation("summonable_entities"), (SuggestionProvider<SharedSuggestionProvider>)((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggestResource((java.util.stream.Stream<Object>)Registry.ENTITY_TYPE.stream().filter(EntityType::canSummon), suggestionsBuilder, (java.util.function.Function<Object, ResourceLocation>)EntityType::getKey, (java.util.function.Function<Object, Message>)(ais -> new TranslatableComponent(Util.makeDescriptionId("entity", EntityType.getKey(ais)), new Object[0])))));
    }
    
    public static class Wrapper implements SuggestionProvider<SharedSuggestionProvider> {
        private final SuggestionProvider<SharedSuggestionProvider> delegate;
        private final ResourceLocation name;
        
        public Wrapper(final ResourceLocation qv, final SuggestionProvider<SharedSuggestionProvider> suggestionProvider) {
            this.delegate = suggestionProvider;
            this.name = qv;
        }
        
        public CompletableFuture<Suggestions> getSuggestions(final CommandContext<SharedSuggestionProvider> commandContext, final SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
            return (CompletableFuture<Suggestions>)this.delegate.getSuggestions((CommandContext)commandContext, suggestionsBuilder);
        }
    }
}
