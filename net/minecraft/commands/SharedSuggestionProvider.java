package net.minecraft.commands;

import java.util.List;
import com.google.common.collect.Lists;
import java.util.function.Predicate;
import com.mojang.brigadier.Message;
import java.util.Locale;
import com.google.common.base.Strings;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import java.util.Collections;
import java.util.Collection;

public interface SharedSuggestionProvider {
    Collection<String> getOnlinePlayerNames();
    
    default Collection<String> getSelectedEntities() {
        return (Collection<String>)Collections.emptyList();
    }
    
    Collection<String> getAllTeams();
    
    Collection<ResourceLocation> getAvailableSoundEvents();
    
    Stream<ResourceLocation> getRecipeNames();
    
    CompletableFuture<Suggestions> customSuggestion(final CommandContext<SharedSuggestionProvider> commandContext, final SuggestionsBuilder suggestionsBuilder);
    
    default Collection<TextCoordinates> getRelevantCoordinates() {
        return (Collection<TextCoordinates>)Collections.singleton(TextCoordinates.DEFAULT_GLOBAL);
    }
    
    default Collection<TextCoordinates> getAbsoluteCoordinates() {
        return (Collection<TextCoordinates>)Collections.singleton(TextCoordinates.DEFAULT_GLOBAL);
    }
    
    boolean hasPermission(final int integer);
    
    default <T> void filterResources(final Iterable<T> iterable, final String string, final Function<T, ResourceLocation> function, final Consumer<T> consumer) {
        final boolean boolean5 = string.indexOf(58) > -1;
        for (final T object7 : iterable) {
            final ResourceLocation qv8 = (ResourceLocation)function.apply(object7);
            if (boolean5) {
                final String string2 = qv8.toString();
                if (!string2.startsWith(string)) {
                    continue;
                }
                consumer.accept(object7);
            }
            else {
                if (!qv8.getNamespace().startsWith(string) && (!qv8.getNamespace().equals("minecraft") || !qv8.getPath().startsWith(string))) {
                    continue;
                }
                consumer.accept(object7);
            }
        }
    }
    
    default <T> void filterResources(final Iterable<T> iterable, final String string2, final String string3, final Function<T, ResourceLocation> function, final Consumer<T> consumer) {
        if (string2.isEmpty()) {
            iterable.forEach((Consumer)consumer);
        }
        else {
            final String string4 = Strings.commonPrefix((CharSequence)string2, (CharSequence)string3);
            if (!string4.isEmpty()) {
                final String string5 = string2.substring(string4.length());
                SharedSuggestionProvider.<T>filterResources(iterable, string5, function, consumer);
            }
        }
    }
    
    default CompletableFuture<Suggestions> suggestResource(final Iterable<ResourceLocation> iterable, final SuggestionsBuilder suggestionsBuilder, final String string) {
        final String string2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        SharedSuggestionProvider.<ResourceLocation>filterResources(iterable, string2, string, (java.util.function.Function<ResourceLocation, ResourceLocation>)(qv -> qv), (java.util.function.Consumer<ResourceLocation>)(qv -> suggestionsBuilder.suggest(string + qv)));
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    default CompletableFuture<Suggestions> suggestResource(final Iterable<ResourceLocation> iterable, final SuggestionsBuilder suggestionsBuilder) {
        final String string3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        SharedSuggestionProvider.<ResourceLocation>filterResources(iterable, string3, (java.util.function.Function<ResourceLocation, ResourceLocation>)(qv -> qv), (java.util.function.Consumer<ResourceLocation>)(qv -> suggestionsBuilder.suggest(qv.toString())));
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    default <T> CompletableFuture<Suggestions> suggestResource(final Iterable<T> iterable, final SuggestionsBuilder suggestionsBuilder, final Function<T, ResourceLocation> function3, final Function<T, Message> function4) {
        final String string5 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        SharedSuggestionProvider.<T>filterResources(iterable, string5, function3, (java.util.function.Consumer<T>)(object -> suggestionsBuilder.suggest(((ResourceLocation)function3.apply(object)).toString(), (Message)function4.apply(object))));
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    default CompletableFuture<Suggestions> suggestResource(final Stream<ResourceLocation> stream, final SuggestionsBuilder suggestionsBuilder) {
        return suggestResource((Iterable<ResourceLocation>)stream::iterator, suggestionsBuilder);
    }
    
    default <T> CompletableFuture<Suggestions> suggestResource(final Stream<T> stream, final SuggestionsBuilder suggestionsBuilder, final Function<T, ResourceLocation> function3, final Function<T, Message> function4) {
        return SharedSuggestionProvider.<T>suggestResource((java.lang.Iterable<T>)stream::iterator, suggestionsBuilder, function3, function4);
    }
    
    default CompletableFuture<Suggestions> suggestCoordinates(final String string, final Collection<TextCoordinates> collection, final SuggestionsBuilder suggestionsBuilder, final Predicate<String> predicate) {
        final List<String> list5 = (List<String>)Lists.newArrayList();
        if (Strings.isNullOrEmpty(string)) {
            for (final TextCoordinates a7 : collection) {
                final String string2 = a7.x + " " + a7.y + " " + a7.z;
                if (predicate.test(string2)) {
                    list5.add(a7.x);
                    list5.add((a7.x + " " + a7.y));
                    list5.add(string2);
                }
            }
        }
        else {
            final String[] arr6 = string.split(" ");
            if (arr6.length == 1) {
                for (final TextCoordinates a8 : collection) {
                    final String string3 = arr6[0] + " " + a8.y + " " + a8.z;
                    if (predicate.test(string3)) {
                        list5.add((arr6[0] + " " + a8.y));
                        list5.add(string3);
                    }
                }
            }
            else if (arr6.length == 2) {
                for (final TextCoordinates a8 : collection) {
                    final String string3 = arr6[0] + " " + arr6[1] + " " + a8.z;
                    if (predicate.test(string3)) {
                        list5.add(string3);
                    }
                }
            }
        }
        return suggest((Iterable<String>)list5, suggestionsBuilder);
    }
    
    default CompletableFuture<Suggestions> suggest2DCoordinates(final String string, final Collection<TextCoordinates> collection, final SuggestionsBuilder suggestionsBuilder, final Predicate<String> predicate) {
        final List<String> list5 = (List<String>)Lists.newArrayList();
        if (Strings.isNullOrEmpty(string)) {
            for (final TextCoordinates a7 : collection) {
                final String string2 = a7.x + " " + a7.z;
                if (predicate.test(string2)) {
                    list5.add(a7.x);
                    list5.add(string2);
                }
            }
        }
        else {
            final String[] arr6 = string.split(" ");
            if (arr6.length == 1) {
                for (final TextCoordinates a8 : collection) {
                    final String string3 = arr6[0] + " " + a8.z;
                    if (predicate.test(string3)) {
                        list5.add(string3);
                    }
                }
            }
        }
        return suggest((Iterable<String>)list5, suggestionsBuilder);
    }
    
    default CompletableFuture<Suggestions> suggest(final Iterable<String> iterable, final SuggestionsBuilder suggestionsBuilder) {
        final String string3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (final String string4 : iterable) {
            if (string4.toLowerCase(Locale.ROOT).startsWith(string3)) {
                suggestionsBuilder.suggest(string4);
            }
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    default CompletableFuture<Suggestions> suggest(final Stream<String> stream, final SuggestionsBuilder suggestionsBuilder) {
        final String string3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        stream.filter(string2 -> string2.toLowerCase(Locale.ROOT).startsWith(string3)).forEach(suggestionsBuilder::suggest);
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    default CompletableFuture<Suggestions> suggest(final String[] arr, final SuggestionsBuilder suggestionsBuilder) {
        final String string3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (final String string4 : arr) {
            if (string4.toLowerCase(Locale.ROOT).startsWith(string3)) {
                suggestionsBuilder.suggest(string4);
            }
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    public static class TextCoordinates {
        public static final TextCoordinates DEFAULT_LOCAL;
        public static final TextCoordinates DEFAULT_GLOBAL;
        public final String x;
        public final String y;
        public final String z;
        
        public TextCoordinates(final String string1, final String string2, final String string3) {
            this.x = string1;
            this.y = string2;
            this.z = string3;
        }
        
        static {
            DEFAULT_LOCAL = new TextCoordinates("^", "^", "^");
            DEFAULT_GLOBAL = new TextCoordinates("~", "~", "~");
        }
    }
}
