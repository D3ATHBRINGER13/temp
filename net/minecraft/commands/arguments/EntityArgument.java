package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import com.mojang.brigadier.Message;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.Arrays;
import com.google.common.collect.Iterables;
import java.util.function.Consumer;
import net.minecraft.commands.SharedSuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.ImmutableStringReader;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import com.mojang.brigadier.StringReader;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.commands.arguments.selector.EntitySelector;
import com.mojang.brigadier.arguments.ArgumentType;

public class EntityArgument implements ArgumentType<EntitySelector> {
    private static final Collection<String> EXAMPLES;
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY;
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER;
    public static final SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED;
    public static final SimpleCommandExceptionType NO_ENTITIES_FOUND;
    public static final SimpleCommandExceptionType NO_PLAYERS_FOUND;
    public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED;
    private final boolean single;
    private final boolean playersOnly;
    
    protected EntityArgument(final boolean boolean1, final boolean boolean2) {
        this.single = boolean1;
        this.playersOnly = boolean2;
    }
    
    public static EntityArgument entity() {
        return new EntityArgument(true, false);
    }
    
    public static Entity getEntity(final CommandContext<CommandSourceStack> commandContext, final String string) throws CommandSyntaxException {
        return ((EntitySelector)commandContext.getArgument(string, (Class)EntitySelector.class)).findSingleEntity((CommandSourceStack)commandContext.getSource());
    }
    
    public static EntityArgument entities() {
        return new EntityArgument(false, false);
    }
    
    public static Collection<? extends Entity> getEntities(final CommandContext<CommandSourceStack> commandContext, final String string) throws CommandSyntaxException {
        final Collection<? extends Entity> collection3 = getOptionalEntities(commandContext, string);
        if (collection3.isEmpty()) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
        }
        return collection3;
    }
    
    public static Collection<? extends Entity> getOptionalEntities(final CommandContext<CommandSourceStack> commandContext, final String string) throws CommandSyntaxException {
        return ((EntitySelector)commandContext.getArgument(string, (Class)EntitySelector.class)).findEntities((CommandSourceStack)commandContext.getSource());
    }
    
    public static Collection<ServerPlayer> getOptionalPlayers(final CommandContext<CommandSourceStack> commandContext, final String string) throws CommandSyntaxException {
        return (Collection<ServerPlayer>)((EntitySelector)commandContext.getArgument(string, (Class)EntitySelector.class)).findPlayers((CommandSourceStack)commandContext.getSource());
    }
    
    public static EntityArgument player() {
        return new EntityArgument(true, true);
    }
    
    public static ServerPlayer getPlayer(final CommandContext<CommandSourceStack> commandContext, final String string) throws CommandSyntaxException {
        return ((EntitySelector)commandContext.getArgument(string, (Class)EntitySelector.class)).findSinglePlayer((CommandSourceStack)commandContext.getSource());
    }
    
    public static EntityArgument players() {
        return new EntityArgument(false, true);
    }
    
    public static Collection<ServerPlayer> getPlayers(final CommandContext<CommandSourceStack> commandContext, final String string) throws CommandSyntaxException {
        final List<ServerPlayer> list3 = ((EntitySelector)commandContext.getArgument(string, (Class)EntitySelector.class)).findPlayers((CommandSourceStack)commandContext.getSource());
        if (list3.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
        }
        return (Collection<ServerPlayer>)list3;
    }
    
    public EntitySelector parse(final StringReader stringReader) throws CommandSyntaxException {
        final int integer3 = 0;
        final EntitySelectorParser ed4 = new EntitySelectorParser(stringReader);
        final EntitySelector ec5 = ed4.parse();
        if (ec5.getMaxResults() > 1 && this.single) {
            if (this.playersOnly) {
                stringReader.setCursor(0);
                throw EntityArgument.ERROR_NOT_SINGLE_PLAYER.createWithContext((ImmutableStringReader)stringReader);
            }
            stringReader.setCursor(0);
            throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.createWithContext((ImmutableStringReader)stringReader);
        }
        else {
            if (ec5.includesEntities() && this.playersOnly && !ec5.isSelfSelector()) {
                stringReader.setCursor(0);
                throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.createWithContext((ImmutableStringReader)stringReader);
            }
            return ec5;
        }
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> commandContext, final SuggestionsBuilder suggestionsBuilder) {
        if (commandContext.getSource() instanceof SharedSuggestionProvider) {
            final StringReader stringReader4 = new StringReader(suggestionsBuilder.getInput());
            stringReader4.setCursor(suggestionsBuilder.getStart());
            final SharedSuggestionProvider cf5 = (SharedSuggestionProvider)commandContext.getSource();
            final EntitySelectorParser ed6 = new EntitySelectorParser(stringReader4, cf5.hasPermission(2));
            try {
                ed6.parse();
            }
            catch (CommandSyntaxException ex) {}
            return ed6.fillSuggestions(suggestionsBuilder, (Consumer<SuggestionsBuilder>)(suggestionsBuilder -> {
                final Collection<String> collection4 = cf5.getOnlinePlayerNames();
                final Iterable<String> iterable5 = (Iterable<String>)(this.playersOnly ? collection4 : Iterables.concat((Iterable)collection4, (Iterable)cf5.getSelectedEntities()));
                SharedSuggestionProvider.suggest(iterable5, suggestionsBuilder);
            }));
        }
        return (CompletableFuture<Suggestions>)Suggestions.empty();
    }
    
    public Collection<String> getExamples() {
        return EntityArgument.EXAMPLES;
    }
    
    static {
        EXAMPLES = (Collection)Arrays.asList((Object[])new String[] { "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498" });
        ERROR_NOT_SINGLE_ENTITY = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.toomany", new Object[0]));
        ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.player.toomany", new Object[0]));
        ERROR_ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.player.entities", new Object[0]));
        NO_ENTITIES_FOUND = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.notfound.entity", new Object[0]));
        NO_PLAYERS_FOUND = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.notfound.player", new Object[0]));
        ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.selector.not_allowed", new Object[0]));
    }
    
    public static class Serializer implements ArgumentSerializer<EntityArgument> {
        public void serializeToNetwork(final EntityArgument cl, final FriendlyByteBuf je) {
            byte byte4 = 0;
            if (cl.single) {
                byte4 |= 0x1;
            }
            if (cl.playersOnly) {
                byte4 |= 0x2;
            }
            je.writeByte(byte4);
        }
        
        public EntityArgument deserializeFromNetwork(final FriendlyByteBuf je) {
            final byte byte3 = je.readByte();
            return new EntityArgument((byte3 & 0x1) != 0x0, (byte3 & 0x2) != 0x0);
        }
        
        public void serializeToJson(final EntityArgument cl, final JsonObject jsonObject) {
            jsonObject.addProperty("amount", cl.single ? "single" : "multiple");
            jsonObject.addProperty("type", cl.playersOnly ? "players" : "entities");
        }
    }
}
