package net.minecraft.commands.arguments.item;

import net.minecraft.nbt.NbtUtils;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import java.util.Arrays;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.world.item.ItemStack;
import java.util.function.Predicate;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class ItemPredicateArgument implements ArgumentType<Result> {
    private static final Collection<String> EXAMPLES;
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG;
    
    public static ItemPredicateArgument itemPredicate() {
        return new ItemPredicateArgument();
    }
    
    public Result parse(final StringReader stringReader) throws CommandSyntaxException {
        final ItemParser dy3 = new ItemParser(stringReader, true).parse();
        if (dy3.getItem() != null) {
            final ItemPredicate a4 = new ItemPredicate(dy3.getItem(), dy3.getNbt());
            return commandContext -> a4;
        }
        final ResourceLocation qv4 = dy3.getTag();
        final ResourceLocation qv5;
        final Tag<Object> zg4;
        final ItemParser itemParser;
        return commandContext -> {
            zg4 = (Tag<Object>)((CommandSourceStack)commandContext.getSource()).getServer().getTags().getItems().getTag(qv5);
            if (zg4 == null) {
                throw ItemPredicateArgument.ERROR_UNKNOWN_TAG.create(qv5.toString());
            }
            else {
                return (Predicate<ItemStack>)new TagPredicate((Tag<Item>)zg4, itemParser.getNbt());
            }
        };
    }
    
    public static Predicate<ItemStack> getItemPredicate(final CommandContext<CommandSourceStack> commandContext, final String string) throws CommandSyntaxException {
        return ((Result)commandContext.getArgument(string, (Class)Result.class)).create(commandContext);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> commandContext, final SuggestionsBuilder suggestionsBuilder) {
        final StringReader stringReader4 = new StringReader(suggestionsBuilder.getInput());
        stringReader4.setCursor(suggestionsBuilder.getStart());
        final ItemParser dy5 = new ItemParser(stringReader4, true);
        try {
            dy5.parse();
        }
        catch (CommandSyntaxException ex) {}
        return dy5.fillSuggestions(suggestionsBuilder);
    }
    
    public Collection<String> getExamples() {
        return ItemPredicateArgument.EXAMPLES;
    }
    
    static {
        EXAMPLES = (Collection)Arrays.asList((Object[])new String[] { "stick", "minecraft:stick", "#stick", "#stick{foo=bar}" });
        ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType(object -> new TranslatableComponent("arguments.item.tag.unknown", new Object[] { object }));
    }
    
    static class ItemPredicate implements Predicate<ItemStack> {
        private final Item item;
        @Nullable
        private final CompoundTag nbt;
        
        public ItemPredicate(final Item bce, @Nullable final CompoundTag id) {
            this.item = bce;
            this.nbt = id;
        }
        
        public boolean test(final ItemStack bcj) {
            return bcj.getItem() == this.item && NbtUtils.compareNbt(this.nbt, bcj.getTag(), true);
        }
    }
    
    static class TagPredicate implements Predicate<ItemStack> {
        private final Tag<Item> tag;
        @Nullable
        private final CompoundTag nbt;
        
        public TagPredicate(final Tag<Item> zg, @Nullable final CompoundTag id) {
            this.tag = zg;
            this.nbt = id;
        }
        
        public boolean test(final ItemStack bcj) {
            return this.tag.contains(bcj.getItem()) && NbtUtils.compareNbt(this.nbt, bcj.getTag(), true);
        }
    }
    
    public interface Result {
        Predicate<ItemStack> create(final CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException;
    }
}
