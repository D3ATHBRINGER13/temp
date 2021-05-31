package net.minecraft.server.commands.data;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.Message;
import net.minecraft.commands.arguments.CompoundTagArgument;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.util.Collections;
import net.minecraft.commands.arguments.NbtTagArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Mth;
import net.minecraft.nbt.NumericTag;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.nbt.CollectionTag;
import java.util.function.Supplier;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import java.util.function.BiConsumer;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import java.util.function.Function;
import java.util.List;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class DataCommands {
    private static final SimpleCommandExceptionType ERROR_MERGE_UNCHANGED;
    private static final DynamicCommandExceptionType ERROR_GET_NOT_NUMBER;
    private static final DynamicCommandExceptionType ERROR_GET_NON_EXISTENT;
    private static final SimpleCommandExceptionType ERROR_MULTIPLE_TAGS;
    private static final DynamicCommandExceptionType ERROR_EXPECTED_LIST;
    private static final DynamicCommandExceptionType ERROR_EXPECTED_OBJECT;
    private static final DynamicCommandExceptionType ERROR_INVALID_INDEX;
    public static final List<Function<String, DataProvider>> ALL_PROVIDERS;
    public static final List<DataProvider> TARGET_PROVIDERS;
    public static final List<DataProvider> SOURCE_PROVIDERS;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder2 = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("data").requires(cd -> cd.hasPermission(2));
        for (final DataProvider c4 : DataCommands.TARGET_PROVIDERS) {
            ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder2.then((ArgumentBuilder)c4.wrap(Commands.literal("merge"), (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)(argumentBuilder -> argumentBuilder.then(Commands.argument("nbt", (com.mojang.brigadier.arguments.ArgumentType<Object>)CompoundTagArgument.compoundTag()).executes(commandContext -> mergeData((CommandSourceStack)commandContext.getSource(), c4.access((CommandContext<CommandSourceStack>)commandContext), CompoundTagArgument.getCompoundTag((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "nbt")))))))).then((ArgumentBuilder)c4.wrap(Commands.literal("get"), (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)(argumentBuilder -> argumentBuilder.executes(commandContext -> getData((CommandSourceStack)commandContext.getSource(), c4.access((CommandContext<CommandSourceStack>)commandContext))).then(((RequiredArgumentBuilder)Commands.argument("path", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtPathArgument.nbtPath()).executes(commandContext -> getData((CommandSourceStack)commandContext.getSource(), c4.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path")))).then(Commands.argument("scale", (com.mojang.brigadier.arguments.ArgumentType<Object>)DoubleArgumentType.doubleArg()).executes(commandContext -> getNumeric((CommandSourceStack)commandContext.getSource(), c4.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path"), DoubleArgumentType.getDouble(commandContext, "scale"))))))))).then((ArgumentBuilder)c4.wrap(Commands.literal("remove"), (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)(argumentBuilder -> argumentBuilder.then(Commands.argument("path", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtPathArgument.nbtPath()).executes(commandContext -> removeData((CommandSourceStack)commandContext.getSource(), c4.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path")))))))).then((ArgumentBuilder)decorateModification((BiConsumer<ArgumentBuilder<CommandSourceStack, ?>, DataManipulatorDecorator>)((argumentBuilder, b) -> {
                final int integer5;
                final List<Tag> collection5;
                int integer6;
                final Iterator iterator;
                Tag iu8;
                CompoundTag id2;
                CompoundTag id3;
                final Iterator iterator2;
                Tag iu9;
                argumentBuilder.then(Commands.literal("insert").then(Commands.argument("index", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer()).then((ArgumentBuilder)b.create((commandContext, id, h, list) -> {
                    integer5 = IntegerArgumentType.getInteger(commandContext, "index");
                    return insertAtIndex(integer5, id, h, list);
                })))).then(Commands.literal("prepend").then((ArgumentBuilder)b.create((commandContext, id, h, list) -> insertAtIndex(0, id, h, list)))).then(Commands.literal("append").then((ArgumentBuilder)b.create((commandContext, id, h, list) -> insertAtIndex(-1, id, h, list)))).then(Commands.literal("set").then((ArgumentBuilder)b.create((commandContext, id, h, list) -> h.set(id, (Supplier<Tag>)(Tag)Iterables.getLast(list)::copy)))).then(Commands.literal("merge").then((ArgumentBuilder)b.create((commandContext, id, h, list) -> {
                    collection5 = h.getOrCreate(id, (Supplier<Tag>)CompoundTag::new);
                    integer6 = 0;
                    ((Collection)collection5).iterator();
                    while (iterator.hasNext()) {
                        iu8 = (Tag)iterator.next();
                        if (!(iu8 instanceof CompoundTag)) {
                            throw DataCommands.ERROR_EXPECTED_OBJECT.create(iu8);
                        }
                        else {
                            id2 = (CompoundTag)iu8;
                            id3 = id2.copy();
                            list.iterator();
                            while (iterator2.hasNext()) {
                                iu9 = (Tag)iterator2.next();
                                if (!(iu9 instanceof CompoundTag)) {
                                    throw DataCommands.ERROR_EXPECTED_OBJECT.create(iu9);
                                }
                                else {
                                    id2.merge((CompoundTag)iu9);
                                }
                            }
                            integer6 += (id3.equals(id2) ? 0 : 1);
                        }
                    }
                    return integer6;
                })));
            })));
        }
        commandDispatcher.register((LiteralArgumentBuilder)literalArgumentBuilder2);
    }
    
    private static int insertAtIndex(final int integer, final CompoundTag id, final NbtPathArgument.NbtPath h, final List<Tag> list) throws CommandSyntaxException {
        final Collection<Tag> collection5 = (Collection<Tag>)h.getOrCreate(id, (Supplier<Tag>)ListTag::new);
        int integer2 = 0;
        for (final Tag iu8 : collection5) {
            if (!(iu8 instanceof CollectionTag)) {
                throw DataCommands.ERROR_EXPECTED_LIST.create(iu8);
            }
            boolean boolean9 = false;
            final CollectionTag<?> ic10 = iu8;
            int integer3 = (integer < 0) ? (ic10.size() + integer + 1) : integer;
            for (final Tag iu9 : list) {
                try {
                    if (!ic10.addTag(integer3, iu9.copy())) {
                        continue;
                    }
                    ++integer3;
                    boolean9 = true;
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException14) {
                    throw DataCommands.ERROR_INVALID_INDEX.create(integer3);
                }
            }
            integer2 += (boolean9 ? 1 : 0);
        }
        return integer2;
    }
    
    private static ArgumentBuilder<CommandSourceStack, ?> decorateModification(final BiConsumer<ArgumentBuilder<CommandSourceStack, ?>, DataManipulatorDecorator> biConsumer) {
        final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder2 = Commands.literal("modify");
        for (final DataProvider c4 : DataCommands.TARGET_PROVIDERS) {
            c4.wrap(literalArgumentBuilder2, (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)(argumentBuilder -> {
                final ArgumentBuilder<CommandSourceStack, ?> argumentBuilder2 = Commands.argument("targetPath", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtPathArgument.nbtPath());
                for (final DataProvider c2 : DataCommands.SOURCE_PROVIDERS) {
                    final DataProvider dataProvider;
                    biConsumer.accept(argumentBuilder2, a -> dataProvider.wrap(Commands.literal("from"), (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)(argumentBuilder -> argumentBuilder.executes(commandContext -> {
                        final List<Tag> list5 = (List<Tag>)Collections.singletonList(dataProvider.access((CommandContext<CommandSourceStack>)commandContext).getData());
                        return manipulateData((CommandContext<CommandSourceStack>)commandContext, c4, a, list5);
                    }).then(Commands.argument("sourcePath", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtPathArgument.nbtPath()).executes(commandContext -> {
                        final DataAccessor ue5 = dataProvider.access((CommandContext<CommandSourceStack>)commandContext);
                        final NbtPathArgument.NbtPath h6 = NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "sourcePath");
                        final List<Tag> list7 = h6.get(ue5.getData());
                        return manipulateData((CommandContext<CommandSourceStack>)commandContext, c4, a, list7);
                    })))));
                }
                biConsumer.accept(argumentBuilder2, a -> (LiteralArgumentBuilder)Commands.literal("value").then(Commands.argument("value", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtTagArgument.nbtTag()).executes(commandContext -> {
                    final List<Tag> list4 = (List<Tag>)Collections.singletonList(NbtTagArgument.getNbtTag((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "value"));
                    return manipulateData((CommandContext<CommandSourceStack>)commandContext, c4, a, list4);
                })));
                return argumentBuilder.then((ArgumentBuilder)argumentBuilder2);
            }));
        }
        return literalArgumentBuilder2;
    }
    
    private static int manipulateData(final CommandContext<CommandSourceStack> commandContext, final DataProvider c, final DataManipulator a, final List<Tag> list) throws CommandSyntaxException {
        final DataAccessor ue5 = c.access(commandContext);
        final NbtPathArgument.NbtPath h6 = NbtPathArgument.getPath(commandContext, "targetPath");
        final CompoundTag id7 = ue5.getData();
        final int integer8 = a.modify(commandContext, id7, h6, list);
        if (integer8 == 0) {
            throw DataCommands.ERROR_MERGE_UNCHANGED.create();
        }
        ue5.setData(id7);
        ((CommandSourceStack)commandContext.getSource()).sendSuccess(ue5.getModifiedSuccess(), true);
        return integer8;
    }
    
    private static int removeData(final CommandSourceStack cd, final DataAccessor ue, final NbtPathArgument.NbtPath h) throws CommandSyntaxException {
        final CompoundTag id4 = ue.getData();
        final int integer5 = h.remove(id4);
        if (integer5 == 0) {
            throw DataCommands.ERROR_MERGE_UNCHANGED.create();
        }
        ue.setData(id4);
        cd.sendSuccess(ue.getModifiedSuccess(), true);
        return integer5;
    }
    
    private static Tag getSingleTag(final NbtPathArgument.NbtPath h, final DataAccessor ue) throws CommandSyntaxException {
        final Collection<Tag> collection3 = (Collection<Tag>)h.get(ue.getData());
        final Iterator<Tag> iterator4 = (Iterator<Tag>)collection3.iterator();
        final Tag iu5 = (Tag)iterator4.next();
        if (iterator4.hasNext()) {
            throw DataCommands.ERROR_MULTIPLE_TAGS.create();
        }
        return iu5;
    }
    
    private static int getData(final CommandSourceStack cd, final DataAccessor ue, final NbtPathArgument.NbtPath h) throws CommandSyntaxException {
        final Tag iu4 = getSingleTag(h, ue);
        int integer5;
        if (iu4 instanceof NumericTag) {
            integer5 = Mth.floor(((NumericTag)iu4).getAsDouble());
        }
        else if (iu4 instanceof CollectionTag) {
            integer5 = ((CollectionTag)iu4).size();
        }
        else if (iu4 instanceof CompoundTag) {
            integer5 = ((CompoundTag)iu4).size();
        }
        else {
            if (!(iu4 instanceof StringTag)) {
                throw DataCommands.ERROR_GET_NON_EXISTENT.create(h.toString());
            }
            integer5 = iu4.getAsString().length();
        }
        cd.sendSuccess(ue.getPrintSuccess(iu4), false);
        return integer5;
    }
    
    private static int getNumeric(final CommandSourceStack cd, final DataAccessor ue, final NbtPathArgument.NbtPath h, final double double4) throws CommandSyntaxException {
        final Tag iu6 = getSingleTag(h, ue);
        if (!(iu6 instanceof NumericTag)) {
            throw DataCommands.ERROR_GET_NOT_NUMBER.create(h.toString());
        }
        final int integer7 = Mth.floor(((NumericTag)iu6).getAsDouble() * double4);
        cd.sendSuccess(ue.getPrintSuccess(h, double4, integer7), false);
        return integer7;
    }
    
    private static int getData(final CommandSourceStack cd, final DataAccessor ue) throws CommandSyntaxException {
        cd.sendSuccess(ue.getPrintSuccess(ue.getData()), false);
        return 1;
    }
    
    private static int mergeData(final CommandSourceStack cd, final DataAccessor ue, final CompoundTag id) throws CommandSyntaxException {
        final CompoundTag id2 = ue.getData();
        final CompoundTag id3 = id2.copy().merge(id);
        if (id2.equals(id3)) {
            throw DataCommands.ERROR_MERGE_UNCHANGED.create();
        }
        ue.setData(id3);
        cd.sendSuccess(ue.getModifiedSuccess(), true);
        return 1;
    }
    
    static {
        ERROR_MERGE_UNCHANGED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.data.merge.failed", new Object[0]));
        ERROR_GET_NOT_NUMBER = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.data.get.invalid", new Object[] { object }));
        ERROR_GET_NON_EXISTENT = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.data.get.unknown", new Object[] { object }));
        ERROR_MULTIPLE_TAGS = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.data.get.multiple", new Object[0]));
        ERROR_EXPECTED_LIST = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.data.modify.expected_list", new Object[] { object }));
        ERROR_EXPECTED_OBJECT = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.data.modify.expected_object", new Object[] { object }));
        ERROR_INVALID_INDEX = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.data.modify.invalid_index", new Object[] { object }));
        ALL_PROVIDERS = (List)ImmutableList.of(EntityDataAccessor.PROVIDER, BlockDataAccessor.PROVIDER);
        TARGET_PROVIDERS = (List)DataCommands.ALL_PROVIDERS.stream().map(function -> (DataProvider)function.apply("target")).collect(ImmutableList.toImmutableList());
        SOURCE_PROVIDERS = (List)DataCommands.ALL_PROVIDERS.stream().map(function -> (DataProvider)function.apply("source")).collect(ImmutableList.toImmutableList());
    }
    
    public interface DataProvider {
        DataAccessor access(final CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException;
        
        ArgumentBuilder<CommandSourceStack, ?> wrap(final ArgumentBuilder<CommandSourceStack, ?> argumentBuilder, final Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> function);
    }
    
    interface DataManipulatorDecorator {
        ArgumentBuilder<CommandSourceStack, ?> create(final DataManipulator a);
    }
    
    interface DataManipulator {
        int modify(final CommandContext<CommandSourceStack> commandContext, final CompoundTag id, final NbtPathArgument.NbtPath h, final List<Tag> list) throws CommandSyntaxException;
    }
}
