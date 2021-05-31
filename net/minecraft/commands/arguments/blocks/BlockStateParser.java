package net.minecraft.commands.arguments.blocks;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.Message;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Optional;
import net.minecraft.nbt.TagParser;
import com.mojang.brigadier.ImmutableStringReader;
import net.minecraft.core.Registry;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.tags.Tag;
import net.minecraft.tags.BlockTags;
import java.util.Iterator;
import java.util.Locale;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Map;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.function.Function;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class BlockStateParser {
    public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED;
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_BLOCK;
    public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_PROPERTY;
    public static final Dynamic2CommandExceptionType ERROR_DUPLICATE_PROPERTY;
    public static final Dynamic3CommandExceptionType ERROR_INVALID_VALUE;
    public static final Dynamic2CommandExceptionType ERROR_EXPECTED_VALUE;
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_PROPERTIES;
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING;
    private final StringReader reader;
    private final boolean forTesting;
    private final Map<Property<?>, Comparable<?>> properties;
    private final Map<String, String> vagueProperties;
    private ResourceLocation id;
    private StateDefinition<Block, BlockState> definition;
    private BlockState state;
    @Nullable
    private CompoundTag nbt;
    private ResourceLocation tag;
    private int tagCursor;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions;
    
    public BlockStateParser(final StringReader stringReader, final boolean boolean2) {
        this.properties = (Map<Property<?>, Comparable<?>>)Maps.newHashMap();
        this.vagueProperties = (Map<String, String>)Maps.newHashMap();
        this.id = new ResourceLocation("");
        this.tag = new ResourceLocation("");
        this.suggestions = BlockStateParser.SUGGEST_NOTHING;
        this.reader = stringReader;
        this.forTesting = boolean2;
    }
    
    public Map<Property<?>, Comparable<?>> getProperties() {
        return this.properties;
    }
    
    @Nullable
    public BlockState getState() {
        return this.state;
    }
    
    @Nullable
    public CompoundTag getNbt() {
        return this.nbt;
    }
    
    @Nullable
    public ResourceLocation getTag() {
        return this.tag;
    }
    
    public BlockStateParser parse(final boolean boolean1) throws CommandSyntaxException {
        this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestBlockIdOrTag;
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
            this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestOpenVaguePropertiesOrNbt;
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.readVagueProperties();
                this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestOpenNbt;
            }
        }
        else {
            this.readBlock();
            this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestOpenPropertiesOrNbt;
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.readProperties();
                this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestOpenNbt;
            }
        }
        if (boolean1 && this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = BlockStateParser.SUGGEST_NOTHING;
            this.readNbt();
        }
        return this;
    }
    
    private CompletableFuture<Suggestions> suggestPropertyNameOrEnd(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }
        return this.suggestPropertyName(suggestionsBuilder);
    }
    
    private CompletableFuture<Suggestions> suggestVaguePropertyNameOrEnd(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }
        return this.suggestVaguePropertyName(suggestionsBuilder);
    }
    
    private CompletableFuture<Suggestions> suggestPropertyName(final SuggestionsBuilder suggestionsBuilder) {
        final String string3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (final Property<?> bww5 : this.state.getProperties()) {
            if (!this.properties.containsKey(bww5) && bww5.getName().startsWith(string3)) {
                suggestionsBuilder.suggest(bww5.getName() + '=');
            }
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestVaguePropertyName(final SuggestionsBuilder suggestionsBuilder) {
        final String string3 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        if (this.tag != null && !this.tag.getPath().isEmpty()) {
            final Tag<Block> zg4 = BlockTags.getAllTags().getTag(this.tag);
            if (zg4 != null) {
                for (final Block bmv6 : zg4.getValues()) {
                    for (final Property<?> bww8 : bmv6.getStateDefinition().getProperties()) {
                        if (!this.vagueProperties.containsKey(bww8.getName()) && bww8.getName().startsWith(string3)) {
                            suggestionsBuilder.suggest(bww8.getName() + '=');
                        }
                    }
                }
            }
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestOpenNbt(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty() && this.hasBlockEntity()) {
            suggestionsBuilder.suggest(String.valueOf('{'));
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private boolean hasBlockEntity() {
        if (this.state != null) {
            return this.state.getBlock().isEntityBlock();
        }
        if (this.tag != null) {
            final Tag<Block> zg2 = BlockTags.getAllTags().getTag(this.tag);
            if (zg2 != null) {
                for (final Block bmv4 : zg2.getValues()) {
                    if (bmv4.isEntityBlock()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private CompletableFuture<Suggestions> suggestEquals(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf('='));
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestNextPropertyOrEnd(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }
        if (suggestionsBuilder.getRemaining().isEmpty() && this.properties.size() < this.state.getProperties().size()) {
            suggestionsBuilder.suggest(String.valueOf(','));
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private static <T extends Comparable<T>> SuggestionsBuilder addSuggestions(final SuggestionsBuilder suggestionsBuilder, final Property<T> bww) {
        for (final T comparable4 : bww.getPossibleValues()) {
            if (comparable4 instanceof Integer) {
                suggestionsBuilder.suggest((int)comparable4);
            }
            else {
                suggestionsBuilder.suggest(bww.getName(comparable4));
            }
        }
        return suggestionsBuilder;
    }
    
    private CompletableFuture<Suggestions> suggestVaguePropertyValue(final SuggestionsBuilder suggestionsBuilder, final String string) {
        boolean boolean4 = false;
        if (this.tag != null && !this.tag.getPath().isEmpty()) {
            final Tag<Block> zg5 = BlockTags.getAllTags().getTag(this.tag);
            if (zg5 != null) {
                for (final Block bmv7 : zg5.getValues()) {
                    final Property<?> bww8 = bmv7.getStateDefinition().getProperty(string);
                    if (bww8 != null) {
                        BlockStateParser.addSuggestions(suggestionsBuilder, bww8);
                    }
                    if (!boolean4) {
                        for (final Property<?> bww9 : bmv7.getStateDefinition().getProperties()) {
                            if (!this.vagueProperties.containsKey(bww9.getName())) {
                                boolean4 = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (boolean4) {
            suggestionsBuilder.suggest(String.valueOf(','));
        }
        suggestionsBuilder.suggest(String.valueOf(']'));
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestOpenVaguePropertiesOrNbt(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            final Tag<Block> zg3 = BlockTags.getAllTags().getTag(this.tag);
            if (zg3 != null) {
                boolean boolean4 = false;
                boolean boolean5 = false;
                for (final Block bmv7 : zg3.getValues()) {
                    boolean4 |= !bmv7.getStateDefinition().getProperties().isEmpty();
                    boolean5 |= bmv7.isEntityBlock();
                    if (boolean4 && boolean5) {
                        break;
                    }
                }
                if (boolean4) {
                    suggestionsBuilder.suggest(String.valueOf('['));
                }
                if (boolean5) {
                    suggestionsBuilder.suggest(String.valueOf('{'));
                }
            }
        }
        return this.suggestTag(suggestionsBuilder);
    }
    
    private CompletableFuture<Suggestions> suggestOpenPropertiesOrNbt(final SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            if (!this.state.getBlock().getStateDefinition().getProperties().isEmpty()) {
                suggestionsBuilder.suggest(String.valueOf('['));
            }
            if (this.state.getBlock().isEntityBlock()) {
                suggestionsBuilder.suggest(String.valueOf('{'));
            }
        }
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    private CompletableFuture<Suggestions> suggestTag(final SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource((Iterable<ResourceLocation>)BlockTags.getAllTags().getAvailableTags(), suggestionsBuilder.createOffset(this.tagCursor).add(suggestionsBuilder));
    }
    
    private CompletableFuture<Suggestions> suggestBlockIdOrTag(final SuggestionsBuilder suggestionsBuilder) {
        if (this.forTesting) {
            SharedSuggestionProvider.suggestResource((Iterable<ResourceLocation>)BlockTags.getAllTags().getAvailableTags(), suggestionsBuilder, String.valueOf('#'));
        }
        SharedSuggestionProvider.suggestResource((Iterable<ResourceLocation>)Registry.BLOCK.keySet(), suggestionsBuilder);
        return (CompletableFuture<Suggestions>)suggestionsBuilder.buildFuture();
    }
    
    public void readBlock() throws CommandSyntaxException {
        final int integer2 = this.reader.getCursor();
        this.id = ResourceLocation.read(this.reader);
        final Block bmv3 = (Block)Registry.BLOCK.getOptional(this.id).orElseThrow(() -> {
            this.reader.setCursor(integer2);
            return BlockStateParser.ERROR_UNKNOWN_BLOCK.createWithContext((ImmutableStringReader)this.reader, this.id.toString());
        });
        this.definition = bmv3.getStateDefinition();
        this.state = bmv3.defaultBlockState();
    }
    
    public void readTag() throws CommandSyntaxException {
        if (!this.forTesting) {
            throw BlockStateParser.ERROR_NO_TAGS_ALLOWED.create();
        }
        this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestTag;
        this.reader.expect('#');
        this.tagCursor = this.reader.getCursor();
        this.tag = ResourceLocation.read(this.reader);
    }
    
    public void readProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestPropertyNameOrEnd;
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            final int integer2 = this.reader.getCursor();
            final String string3 = this.reader.readString();
            final Property<?> bww4 = this.definition.getProperty(string3);
            if (bww4 == null) {
                this.reader.setCursor(integer2);
                throw BlockStateParser.ERROR_UNKNOWN_PROPERTY.createWithContext((ImmutableStringReader)this.reader, this.id.toString(), string3);
            }
            if (this.properties.containsKey(bww4)) {
                this.reader.setCursor(integer2);
                throw BlockStateParser.ERROR_DUPLICATE_PROPERTY.createWithContext((ImmutableStringReader)this.reader, this.id.toString(), string3);
            }
            this.reader.skipWhitespace();
            this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestEquals;
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                throw BlockStateParser.ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader, this.id.toString(), string3);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)(suggestionsBuilder -> BlockStateParser.<Comparable>addSuggestions(suggestionsBuilder, bww4).buildFuture());
            final int integer3 = this.reader.getCursor();
            this.setValue(bww4, this.reader.readString(), integer3);
            this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestNextPropertyOrEnd;
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
                continue;
            }
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestPropertyName;
            }
            else {
                if (this.reader.peek() == ']') {
                    break;
                }
                throw BlockStateParser.ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext((ImmutableStringReader)this.reader);
            }
        }
        if (this.reader.canRead()) {
            this.reader.skip();
            return;
        }
        throw BlockStateParser.ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext((ImmutableStringReader)this.reader);
    }
    
    public void readVagueProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestVaguePropertyNameOrEnd;
        int integer2 = -1;
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            final int integer3 = this.reader.getCursor();
            final String string4 = this.reader.readString();
            if (this.vagueProperties.containsKey(string4)) {
                this.reader.setCursor(integer3);
                throw BlockStateParser.ERROR_DUPLICATE_PROPERTY.createWithContext((ImmutableStringReader)this.reader, this.id.toString(), string4);
            }
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                this.reader.setCursor(integer3);
                throw BlockStateParser.ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader, this.id.toString(), string4);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)(suggestionsBuilder -> this.suggestVaguePropertyValue(suggestionsBuilder, string4));
            integer2 = this.reader.getCursor();
            final String string5 = this.reader.readString();
            this.vagueProperties.put(string4, string5);
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
                continue;
            }
            integer2 = -1;
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestions = (Function<SuggestionsBuilder, CompletableFuture<Suggestions>>)this::suggestVaguePropertyName;
            }
            else {
                if (this.reader.peek() == ']') {
                    break;
                }
                throw BlockStateParser.ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext((ImmutableStringReader)this.reader);
            }
        }
        if (this.reader.canRead()) {
            this.reader.skip();
            return;
        }
        if (integer2 >= 0) {
            this.reader.setCursor(integer2);
        }
        throw BlockStateParser.ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext((ImmutableStringReader)this.reader);
    }
    
    public void readNbt() throws CommandSyntaxException {
        this.nbt = new TagParser(this.reader).readStruct();
    }
    
    private <T extends Comparable<T>> void setValue(final Property<T> bww, final String string, final int integer) throws CommandSyntaxException {
        final Optional<T> optional5 = bww.getValue(string);
        if (optional5.isPresent()) {
            this.state = ((AbstractStateHolder<O, BlockState>)this.state).<T, Comparable>setValue(bww, optional5.get());
            this.properties.put(bww, optional5.get());
            return;
        }
        this.reader.setCursor(integer);
        throw BlockStateParser.ERROR_INVALID_VALUE.createWithContext((ImmutableStringReader)this.reader, this.id.toString(), bww.getName(), string);
    }
    
    public static String serialize(final BlockState bvt) {
        final StringBuilder stringBuilder2 = new StringBuilder(Registry.BLOCK.getKey(bvt.getBlock()).toString());
        if (!bvt.getProperties().isEmpty()) {
            stringBuilder2.append('[');
            boolean boolean3 = false;
            for (final Map.Entry<Property<?>, Comparable<?>> entry5 : bvt.getValues().entrySet()) {
                if (boolean3) {
                    stringBuilder2.append(',');
                }
                BlockStateParser.<Comparable>appendProperty(stringBuilder2, (Property<Comparable>)entry5.getKey(), entry5.getValue());
                boolean3 = true;
            }
            stringBuilder2.append(']');
        }
        return stringBuilder2.toString();
    }
    
    private static <T extends Comparable<T>> void appendProperty(final StringBuilder stringBuilder, final Property<T> bww, final Comparable<?> comparable) {
        stringBuilder.append(bww.getName());
        stringBuilder.append('=');
        stringBuilder.append(bww.getName((T)comparable));
    }
    
    public CompletableFuture<Suggestions> fillSuggestions(final SuggestionsBuilder suggestionsBuilder) {
        return (CompletableFuture<Suggestions>)this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()));
    }
    
    public Map<String, String> getVagueProperties() {
        return this.vagueProperties;
    }
    
    static {
        ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.block.tag.disallowed", new Object[0]));
        ERROR_UNKNOWN_BLOCK = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.block.id.invalid", new Object[] { object }));
        ERROR_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableComponent("argument.block.property.unknown", new Object[] { object1, object2 }));
        ERROR_DUPLICATE_PROPERTY = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableComponent("argument.block.property.duplicate", new Object[] { object2, object1 }));
        ERROR_INVALID_VALUE = new Dynamic3CommandExceptionType((object1, object2, object3) -> new TranslatableComponent("argument.block.property.invalid", new Object[] { object1, object3, object2 }));
        ERROR_EXPECTED_VALUE = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableComponent("argument.block.property.novalue", new Object[] { object1, object2 }));
        ERROR_EXPECTED_END_OF_PROPERTIES = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.block.property.unclosed", new Object[0]));
        SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    }
}
