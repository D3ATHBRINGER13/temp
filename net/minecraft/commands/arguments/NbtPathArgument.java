package net.minecraft.commands.arguments;

import org.apache.commons.lang3.mutable.MutableBoolean;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CollectionTag;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.Collections;
import java.util.Arrays;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.Message;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import com.mojang.brigadier.arguments.ArgumentType;

public class NbtPathArgument implements ArgumentType<NbtPath> {
    private static final Collection<String> EXAMPLES;
    public static final SimpleCommandExceptionType ERROR_INVALID_NODE;
    public static final DynamicCommandExceptionType ERROR_NOTHING_FOUND;
    
    public static NbtPathArgument nbtPath() {
        return new NbtPathArgument();
    }
    
    public static NbtPath getPath(final CommandContext<CommandSourceStack> commandContext, final String string) {
        return (NbtPath)commandContext.getArgument(string, (Class)NbtPath.class);
    }
    
    public NbtPath parse(final StringReader stringReader) throws CommandSyntaxException {
        final List<Node> list3 = (List<Node>)Lists.newArrayList();
        final int integer4 = stringReader.getCursor();
        final Object2IntMap<Node> object2IntMap5 = (Object2IntMap<Node>)new Object2IntOpenHashMap();
        boolean boolean6 = true;
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            final Node i7 = parseNode(stringReader, boolean6);
            list3.add(i7);
            object2IntMap5.put(i7, stringReader.getCursor() - integer4);
            boolean6 = false;
            if (stringReader.canRead()) {
                final char character8 = stringReader.peek();
                if (character8 == ' ' || character8 == '[' || character8 == '{') {
                    continue;
                }
                stringReader.expect('.');
            }
        }
        return new NbtPath(stringReader.getString().substring(integer4, stringReader.getCursor()), (Node[])list3.toArray((Object[])new Node[0]), object2IntMap5);
    }
    
    private static Node parseNode(final StringReader stringReader, final boolean boolean2) throws CommandSyntaxException {
        switch (stringReader.peek()) {
            case '{': {
                if (!boolean2) {
                    throw NbtPathArgument.ERROR_INVALID_NODE.createWithContext((ImmutableStringReader)stringReader);
                }
                final CompoundTag id3 = new TagParser(stringReader).readStruct();
                return new MatchRootObjectNode(id3);
            }
            case '[': {
                stringReader.skip();
                final int integer3 = stringReader.peek();
                if (integer3 == 123) {
                    final CompoundTag id4 = new TagParser(stringReader).readStruct();
                    stringReader.expect(']');
                    return new MatchElementNode(id4);
                }
                if (integer3 == 93) {
                    stringReader.skip();
                    return AllElementsNode.INSTANCE;
                }
                final int integer4 = stringReader.readInt();
                stringReader.expect(']');
                return new IndexedElementNode(integer4);
            }
            case '\"': {
                final String string3 = stringReader.readString();
                return readObjectNode(stringReader, string3);
            }
            default: {
                final String string3 = readUnquotedName(stringReader);
                return readObjectNode(stringReader, string3);
            }
        }
    }
    
    private static Node readObjectNode(final StringReader stringReader, final String string) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '{') {
            final CompoundTag id3 = new TagParser(stringReader).readStruct();
            return new MatchObjectNode(string, id3);
        }
        return new CompoundChildNode(string);
    }
    
    private static String readUnquotedName(final StringReader stringReader) throws CommandSyntaxException {
        final int integer2 = stringReader.getCursor();
        while (stringReader.canRead() && isAllowedInUnquotedName(stringReader.peek())) {
            stringReader.skip();
        }
        if (stringReader.getCursor() == integer2) {
            throw NbtPathArgument.ERROR_INVALID_NODE.createWithContext((ImmutableStringReader)stringReader);
        }
        return stringReader.getString().substring(integer2, stringReader.getCursor());
    }
    
    public Collection<String> getExamples() {
        return NbtPathArgument.EXAMPLES;
    }
    
    private static boolean isAllowedInUnquotedName(final char character) {
        return character != ' ' && character != '\"' && character != '[' && character != ']' && character != '.' && character != '{' && character != '}';
    }
    
    private static Predicate<Tag> createTagPredicate(final CompoundTag id) {
        return (Predicate<Tag>)(iu -> NbtUtils.compareNbt(id, iu, true));
    }
    
    static {
        EXAMPLES = (Collection)Arrays.asList((Object[])new String[] { "foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}" });
        ERROR_INVALID_NODE = new SimpleCommandExceptionType((Message)new TranslatableComponent("arguments.nbtpath.node.invalid", new Object[0]));
        ERROR_NOTHING_FOUND = new DynamicCommandExceptionType(object -> new TranslatableComponent("arguments.nbtpath.nothing_found", new Object[] { object }));
    }
    
    public static class NbtPath {
        private final String original;
        private final Object2IntMap<Node> nodeToOriginalPosition;
        private final Node[] nodes;
        
        public NbtPath(final String string, final Node[] arr, final Object2IntMap<Node> object2IntMap) {
            this.original = string;
            this.nodes = arr;
            this.nodeToOriginalPosition = object2IntMap;
        }
        
        public List<Tag> get(final Tag iu) throws CommandSyntaxException {
            List<Tag> list3 = (List<Tag>)Collections.singletonList(iu);
            for (final Node i7 : this.nodes) {
                list3 = i7.get(list3);
                if (list3.isEmpty()) {
                    throw this.createNotFoundException(i7);
                }
            }
            return list3;
        }
        
        public int countMatching(final Tag iu) {
            List<Tag> list3 = (List<Tag>)Collections.singletonList(iu);
            for (final Node i7 : this.nodes) {
                list3 = i7.get(list3);
                if (list3.isEmpty()) {
                    return 0;
                }
            }
            return list3.size();
        }
        
        private List<Tag> getOrCreateParents(final Tag iu) throws CommandSyntaxException {
            List<Tag> list3 = (List<Tag>)Collections.singletonList(iu);
            for (int integer4 = 0; integer4 < this.nodes.length - 1; ++integer4) {
                final Node i5 = this.nodes[integer4];
                final int integer5 = integer4 + 1;
                list3 = i5.getOrCreate(list3, (Supplier<Tag>)this.nodes[integer5]::createPreferredParentTag);
                if (list3.isEmpty()) {
                    throw this.createNotFoundException(i5);
                }
            }
            return list3;
        }
        
        public List<Tag> getOrCreate(final Tag iu, final Supplier<Tag> supplier) throws CommandSyntaxException {
            final List<Tag> list4 = this.getOrCreateParents(iu);
            final Node i5 = this.nodes[this.nodes.length - 1];
            return i5.getOrCreate(list4, supplier);
        }
        
        private static int apply(final List<Tag> list, final Function<Tag, Integer> function) {
            return (int)list.stream().map((Function)function).reduce(0, (integer1, integer2) -> integer1 + integer2);
        }
        
        public int set(final Tag iu, final Supplier<Tag> supplier) throws CommandSyntaxException {
            final List<Tag> list4 = this.getOrCreateParents(iu);
            final Node i5 = this.nodes[this.nodes.length - 1];
            return apply(list4, (Function<Tag, Integer>)(iu -> i5.setTag(iu, supplier)));
        }
        
        public int remove(final Tag iu) {
            List<Tag> list3 = (List<Tag>)Collections.singletonList(iu);
            for (int integer4 = 0; integer4 < this.nodes.length - 1; ++integer4) {
                list3 = this.nodes[integer4].get(list3);
            }
            final Node i4 = this.nodes[this.nodes.length - 1];
            return apply(list3, (Function<Tag, Integer>)i4::removeTag);
        }
        
        private CommandSyntaxException createNotFoundException(final Node i) {
            final int integer3 = this.nodeToOriginalPosition.getInt(i);
            return NbtPathArgument.ERROR_NOTHING_FOUND.create(this.original.substring(0, integer3));
        }
        
        public String toString() {
            return this.original;
        }
    }
    
    interface Node {
        void getTag(final Tag iu, final List<Tag> list);
        
        void getOrCreateTag(final Tag iu, final Supplier<Tag> supplier, final List<Tag> list);
        
        Tag createPreferredParentTag();
        
        int setTag(final Tag iu, final Supplier<Tag> supplier);
        
        int removeTag(final Tag iu);
        
        default List<Tag> get(final List<Tag> list) {
            return this.collect(list, (BiConsumer<Tag, List<Tag>>)this::getTag);
        }
        
        default List<Tag> getOrCreate(final List<Tag> list, final Supplier<Tag> supplier) {
            return this.collect(list, (BiConsumer<Tag, List<Tag>>)((iu, list) -> this.getOrCreateTag(iu, supplier, (List<Tag>)list)));
        }
        
        default List<Tag> collect(final List<Tag> list, final BiConsumer<Tag, List<Tag>> biConsumer) {
            final List<Tag> list2 = (List<Tag>)Lists.newArrayList();
            for (final Tag iu6 : list) {
                biConsumer.accept(iu6, list2);
            }
            return list2;
        }
    }
    
    static class CompoundChildNode implements Node {
        private final String name;
        
        public CompoundChildNode(final String string) {
            this.name = string;
        }
        
        public void getTag(final Tag iu, final List<Tag> list) {
            if (iu instanceof CompoundTag) {
                final Tag iu2 = ((CompoundTag)iu).get(this.name);
                if (iu2 != null) {
                    list.add(iu2);
                }
            }
        }
        
        public void getOrCreateTag(final Tag iu, final Supplier<Tag> supplier, final List<Tag> list) {
            if (iu instanceof CompoundTag) {
                final CompoundTag id5 = (CompoundTag)iu;
                Tag iu2;
                if (id5.contains(this.name)) {
                    iu2 = id5.get(this.name);
                }
                else {
                    iu2 = (Tag)supplier.get();
                    id5.put(this.name, iu2);
                }
                list.add(iu2);
            }
        }
        
        public Tag createPreferredParentTag() {
            return new CompoundTag();
        }
        
        public int setTag(final Tag iu, final Supplier<Tag> supplier) {
            if (iu instanceof CompoundTag) {
                final CompoundTag id4 = (CompoundTag)iu;
                final Tag iu2 = (Tag)supplier.get();
                final Tag iu3 = id4.put(this.name, iu2);
                if (!iu2.equals(iu3)) {
                    return 1;
                }
            }
            return 0;
        }
        
        public int removeTag(final Tag iu) {
            if (iu instanceof CompoundTag) {
                final CompoundTag id3 = (CompoundTag)iu;
                if (id3.contains(this.name)) {
                    id3.remove(this.name);
                    return 1;
                }
            }
            return 0;
        }
    }
    
    static class IndexedElementNode implements Node {
        private final int index;
        
        public IndexedElementNode(final int integer) {
            this.index = integer;
        }
        
        public void getTag(final Tag iu, final List<Tag> list) {
            if (iu instanceof CollectionTag) {
                final CollectionTag<?> ic4 = iu;
                final int integer5 = ic4.size();
                final int integer6 = (this.index < 0) ? (integer5 + this.index) : this.index;
                if (0 <= integer6 && integer6 < integer5) {
                    list.add(ic4.get(integer6));
                }
            }
        }
        
        public void getOrCreateTag(final Tag iu, final Supplier<Tag> supplier, final List<Tag> list) {
            this.getTag(iu, list);
        }
        
        public Tag createPreferredParentTag() {
            return new ListTag();
        }
        
        public int setTag(final Tag iu, final Supplier<Tag> supplier) {
            if (iu instanceof CollectionTag) {
                final CollectionTag<?> ic4 = iu;
                final int integer5 = ic4.size();
                final int integer6 = (this.index < 0) ? (integer5 + this.index) : this.index;
                if (0 <= integer6 && integer6 < integer5) {
                    final Tag iu2 = (Tag)ic4.get(integer6);
                    final Tag iu3 = (Tag)supplier.get();
                    if (!iu3.equals(iu2) && ic4.setTag(integer6, iu3)) {
                        return 1;
                    }
                }
            }
            return 0;
        }
        
        public int removeTag(final Tag iu) {
            if (iu instanceof CollectionTag) {
                final CollectionTag<?> ic3 = iu;
                final int integer4 = ic3.size();
                final int integer5 = (this.index < 0) ? (integer4 + this.index) : this.index;
                if (0 <= integer5 && integer5 < integer4) {
                    ic3.remove(integer5);
                    return 1;
                }
            }
            return 0;
        }
    }
    
    static class MatchElementNode implements Node {
        private final CompoundTag pattern;
        private final Predicate<Tag> predicate;
        
        public MatchElementNode(final CompoundTag id) {
            this.pattern = id;
            this.predicate = createTagPredicate(id);
        }
        
        public void getTag(final Tag iu, final List<Tag> list) {
            if (iu instanceof ListTag) {
                final ListTag ik4 = (ListTag)iu;
                ik4.stream().filter((Predicate)this.predicate).forEach(list::add);
            }
        }
        
        public void getOrCreateTag(final Tag iu, final Supplier<Tag> supplier, final List<Tag> list) {
            final MutableBoolean mutableBoolean5 = new MutableBoolean();
            if (iu instanceof ListTag) {
                final ListTag ik6 = (ListTag)iu;
                ik6.stream().filter((Predicate)this.predicate).forEach(iu -> {
                    list.add(iu);
                    mutableBoolean5.setTrue();
                });
                if (mutableBoolean5.isFalse()) {
                    final CompoundTag id7 = this.pattern.copy();
                    ik6.add(id7);
                    list.add(id7);
                }
            }
        }
        
        public Tag createPreferredParentTag() {
            return new ListTag();
        }
        
        public int setTag(final Tag iu, final Supplier<Tag> supplier) {
            int integer4 = 0;
            if (iu instanceof ListTag) {
                final ListTag ik5 = (ListTag)iu;
                final int integer5 = ik5.size();
                if (integer5 == 0) {
                    ik5.add(supplier.get());
                    ++integer4;
                }
                else {
                    for (int integer6 = 0; integer6 < integer5; ++integer6) {
                        final Tag iu2 = ik5.get(integer6);
                        if (this.predicate.test(iu2)) {
                            final Tag iu3 = (Tag)supplier.get();
                            if (!iu3.equals(iu2) && ik5.setTag(integer6, iu3)) {
                                ++integer4;
                            }
                        }
                    }
                }
            }
            return integer4;
        }
        
        public int removeTag(final Tag iu) {
            int integer3 = 0;
            if (iu instanceof ListTag) {
                final ListTag ik4 = (ListTag)iu;
                for (int integer4 = ik4.size() - 1; integer4 >= 0; --integer4) {
                    if (this.predicate.test(ik4.get(integer4))) {
                        ik4.remove(integer4);
                        ++integer3;
                    }
                }
            }
            return integer3;
        }
    }
    
    static class AllElementsNode implements Node {
        public static final AllElementsNode INSTANCE;
        
        private AllElementsNode() {
        }
        
        public void getTag(final Tag iu, final List<Tag> list) {
            if (iu instanceof CollectionTag) {
                list.addAll((Collection)iu);
            }
        }
        
        public void getOrCreateTag(final Tag iu, final Supplier<Tag> supplier, final List<Tag> list) {
            if (iu instanceof CollectionTag) {
                final CollectionTag<?> ic5 = iu;
                if (ic5.isEmpty()) {
                    final Tag iu2 = (Tag)supplier.get();
                    if (ic5.addTag(0, iu2)) {
                        list.add(iu2);
                    }
                }
                else {
                    list.addAll((Collection)ic5);
                }
            }
        }
        
        public Tag createPreferredParentTag() {
            return new ListTag();
        }
        
        public int setTag(final Tag iu, final Supplier<Tag> supplier) {
            if (!(iu instanceof CollectionTag)) {
                return 0;
            }
            final CollectionTag<?> ic4 = iu;
            final int integer5 = ic4.size();
            if (integer5 == 0) {
                ic4.addTag(0, (Tag)supplier.get());
                return 1;
            }
            final Tag iu2 = (Tag)supplier.get();
            final int integer6 = integer5 - (int)ic4.stream().filter(iu2::equals).count();
            if (integer6 == 0) {
                return 0;
            }
            ic4.clear();
            if (!ic4.addTag(0, iu2)) {
                return 0;
            }
            for (int integer7 = 1; integer7 < integer5; ++integer7) {
                ic4.addTag(integer7, (Tag)supplier.get());
            }
            return integer6;
        }
        
        public int removeTag(final Tag iu) {
            if (iu instanceof CollectionTag) {
                final CollectionTag<?> ic3 = iu;
                final int integer4 = ic3.size();
                if (integer4 > 0) {
                    ic3.clear();
                    return integer4;
                }
            }
            return 0;
        }
        
        static {
            INSTANCE = new AllElementsNode();
        }
    }
    
    static class MatchObjectNode implements Node {
        private final String name;
        private final CompoundTag pattern;
        private final Predicate<Tag> predicate;
        
        public MatchObjectNode(final String string, final CompoundTag id) {
            this.name = string;
            this.pattern = id;
            this.predicate = createTagPredicate(id);
        }
        
        public void getTag(final Tag iu, final List<Tag> list) {
            if (iu instanceof CompoundTag) {
                final Tag iu2 = ((CompoundTag)iu).get(this.name);
                if (this.predicate.test(iu2)) {
                    list.add(iu2);
                }
            }
        }
        
        public void getOrCreateTag(final Tag iu, final Supplier<Tag> supplier, final List<Tag> list) {
            if (iu instanceof CompoundTag) {
                final CompoundTag id5 = (CompoundTag)iu;
                Tag iu2 = id5.get(this.name);
                if (iu2 == null) {
                    iu2 = this.pattern.copy();
                    id5.put(this.name, iu2);
                    list.add(iu2);
                }
                else if (this.predicate.test(iu2)) {
                    list.add(iu2);
                }
            }
        }
        
        public Tag createPreferredParentTag() {
            return new CompoundTag();
        }
        
        public int setTag(final Tag iu, final Supplier<Tag> supplier) {
            if (iu instanceof CompoundTag) {
                final CompoundTag id4 = (CompoundTag)iu;
                final Tag iu2 = id4.get(this.name);
                if (this.predicate.test(iu2)) {
                    final Tag iu3 = (Tag)supplier.get();
                    if (!iu3.equals(iu2)) {
                        id4.put(this.name, iu3);
                        return 1;
                    }
                }
            }
            return 0;
        }
        
        public int removeTag(final Tag iu) {
            if (iu instanceof CompoundTag) {
                final CompoundTag id3 = (CompoundTag)iu;
                final Tag iu2 = id3.get(this.name);
                if (this.predicate.test(iu2)) {
                    id3.remove(this.name);
                    return 1;
                }
            }
            return 0;
        }
    }
    
    static class MatchRootObjectNode implements Node {
        private final Predicate<Tag> predicate;
        
        public MatchRootObjectNode(final CompoundTag id) {
            this.predicate = createTagPredicate(id);
        }
        
        public void getTag(final Tag iu, final List<Tag> list) {
            if (iu instanceof CompoundTag && this.predicate.test(iu)) {
                list.add(iu);
            }
        }
        
        public void getOrCreateTag(final Tag iu, final Supplier<Tag> supplier, final List<Tag> list) {
            this.getTag(iu, list);
        }
        
        public Tag createPreferredParentTag() {
            return new CompoundTag();
        }
        
        public int setTag(final Tag iu, final Supplier<Tag> supplier) {
            return 0;
        }
        
        public int removeTag(final Tag iu) {
            return 0;
        }
    }
}
