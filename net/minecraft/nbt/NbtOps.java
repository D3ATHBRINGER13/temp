package net.minecraft.nbt;

import java.util.Objects;
import java.util.List;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterators;
import java.util.stream.LongStream;
import java.util.Arrays;
import java.util.stream.IntStream;
import com.mojang.datafixers.DataFixUtils;
import java.nio.ByteBuffer;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.Optional;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.DynamicOps;

public class NbtOps implements DynamicOps<Tag> {
    public static final NbtOps INSTANCE;
    
    protected NbtOps() {
    }
    
    public Tag empty() {
        return new EndTag();
    }
    
    public Type<?> getType(final Tag iu) {
        switch (iu.getId()) {
            case 0: {
                return DSL.nilType();
            }
            case 1: {
                return DSL.byteType();
            }
            case 2: {
                return DSL.shortType();
            }
            case 3: {
                return DSL.intType();
            }
            case 4: {
                return DSL.longType();
            }
            case 5: {
                return DSL.floatType();
            }
            case 6: {
                return DSL.doubleType();
            }
            case 7: {
                return DSL.list(DSL.byteType());
            }
            case 8: {
                return DSL.string();
            }
            case 9: {
                return DSL.list(DSL.remainderType());
            }
            case 10: {
                return DSL.compoundList(DSL.remainderType(), DSL.remainderType());
            }
            case 11: {
                return DSL.list(DSL.intType());
            }
            case 12: {
                return DSL.list(DSL.longType());
            }
            default: {
                return DSL.remainderType();
            }
        }
    }
    
    public Optional<Number> getNumberValue(final Tag iu) {
        if (iu instanceof NumericTag) {
            return (Optional<Number>)Optional.of(((NumericTag)iu).getAsNumber());
        }
        return (Optional<Number>)Optional.empty();
    }
    
    public Tag createNumeric(final Number number) {
        return new DoubleTag(number.doubleValue());
    }
    
    public Tag createByte(final byte byte1) {
        return new ByteTag(byte1);
    }
    
    public Tag createShort(final short short1) {
        return new ShortTag(short1);
    }
    
    public Tag createInt(final int integer) {
        return new IntTag(integer);
    }
    
    public Tag createLong(final long long1) {
        return new LongTag(long1);
    }
    
    public Tag createFloat(final float float1) {
        return new FloatTag(float1);
    }
    
    public Tag createDouble(final double double1) {
        return new DoubleTag(double1);
    }
    
    public Optional<String> getStringValue(final Tag iu) {
        if (iu instanceof StringTag) {
            return (Optional<String>)Optional.of(iu.getAsString());
        }
        return (Optional<String>)Optional.empty();
    }
    
    public Tag createString(final String string) {
        return new StringTag(string);
    }
    
    public Tag mergeInto(final Tag iu1, final Tag iu2) {
        if (iu2 instanceof EndTag) {
            return iu1;
        }
        if (iu1 instanceof CompoundTag) {
            if (iu2 instanceof CompoundTag) {
                final CompoundTag id5 = new CompoundTag();
                final CompoundTag id6 = (CompoundTag)iu1;
                for (final String string8 : id6.getAllKeys()) {
                    id5.put(string8, id6.get(string8));
                }
                final CompoundTag id7 = (CompoundTag)iu2;
                for (final String string9 : id7.getAllKeys()) {
                    id5.put(string9, id7.get(string9));
                }
                return id5;
            }
            return iu1;
        }
        else {
            if (iu1 instanceof EndTag) {
                throw new IllegalArgumentException("mergeInto called with a null input.");
            }
            if (iu1 instanceof CollectionTag) {
                final CollectionTag<Tag> ic4 = new ListTag();
                final CollectionTag<?> ic5 = iu1;
                ic4.addAll((Collection)ic5);
                ic4.add(iu2);
                return ic4;
            }
            return iu1;
        }
    }
    
    public Tag mergeInto(final Tag iu1, final Tag iu2, final Tag iu3) {
        CompoundTag id5;
        if (iu1 instanceof EndTag) {
            id5 = new CompoundTag();
        }
        else {
            if (!(iu1 instanceof CompoundTag)) {
                return iu1;
            }
            final CompoundTag id6 = (CompoundTag)iu1;
            id5 = new CompoundTag();
            id6.getAllKeys().forEach(string -> id5.put(string, id6.get(string)));
        }
        id5.put(iu2.getAsString(), iu3);
        return id5;
    }
    
    public Tag merge(final Tag iu1, final Tag iu2) {
        if (iu1 instanceof EndTag) {
            return iu2;
        }
        if (iu2 instanceof EndTag) {
            return iu1;
        }
        if (iu1 instanceof CompoundTag && iu2 instanceof CompoundTag) {
            final CompoundTag id4 = (CompoundTag)iu1;
            final CompoundTag id5 = (CompoundTag)iu2;
            final CompoundTag id6 = new CompoundTag();
            id4.getAllKeys().forEach(string -> id6.put(string, id4.get(string)));
            id5.getAllKeys().forEach(string -> id6.put(string, id5.get(string)));
        }
        if (iu1 instanceof CollectionTag && iu2 instanceof CollectionTag) {
            final ListTag ik4 = new ListTag();
            ik4.addAll((Collection)iu1);
            ik4.addAll((Collection)iu2);
            return ik4;
        }
        throw new IllegalArgumentException(new StringBuilder().append("Could not merge ").append(iu1).append(" and ").append(iu2).toString());
    }
    
    public Optional<Map<Tag, Tag>> getMapValues(final Tag iu) {
        if (iu instanceof CompoundTag) {
            final CompoundTag id3 = (CompoundTag)iu;
            return (Optional<Map<Tag, Tag>>)Optional.of(id3.getAllKeys().stream().map(string -> Pair.of(this.createString(string), id3.get(string))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        }
        return (Optional<Map<Tag, Tag>>)Optional.empty();
    }
    
    public Tag createMap(final Map<Tag, Tag> map) {
        final CompoundTag id3 = new CompoundTag();
        for (final Map.Entry<Tag, Tag> entry5 : map.entrySet()) {
            id3.put(((Tag)entry5.getKey()).getAsString(), (Tag)entry5.getValue());
        }
        return id3;
    }
    
    public Optional<Stream<Tag>> getStream(final Tag iu) {
        if (iu instanceof CollectionTag) {
            return (Optional<Stream<Tag>>)Optional.of(((CollectionTag)iu).stream().map(iu -> iu));
        }
        return (Optional<Stream<Tag>>)Optional.empty();
    }
    
    public Optional<ByteBuffer> getByteBuffer(final Tag iu) {
        if (iu instanceof ByteArrayTag) {
            return (Optional<ByteBuffer>)Optional.of(ByteBuffer.wrap(((ByteArrayTag)iu).getAsByteArray()));
        }
        return (Optional<ByteBuffer>)super.getByteBuffer(iu);
    }
    
    public Tag createByteList(final ByteBuffer byteBuffer) {
        return new ByteArrayTag(DataFixUtils.toArray(byteBuffer));
    }
    
    public Optional<IntStream> getIntStream(final Tag iu) {
        if (iu instanceof IntArrayTag) {
            return (Optional<IntStream>)Optional.of(Arrays.stream(((IntArrayTag)iu).getAsIntArray()));
        }
        return (Optional<IntStream>)super.getIntStream(iu);
    }
    
    public Tag createIntList(final IntStream intStream) {
        return new IntArrayTag(intStream.toArray());
    }
    
    public Optional<LongStream> getLongStream(final Tag iu) {
        if (iu instanceof LongArrayTag) {
            return (Optional<LongStream>)Optional.of(Arrays.stream(((LongArrayTag)iu).getAsLongArray()));
        }
        return (Optional<LongStream>)super.getLongStream(iu);
    }
    
    public Tag createLongList(final LongStream longStream) {
        return new LongArrayTag(longStream.toArray());
    }
    
    public Tag createList(final Stream<Tag> stream) {
        final PeekingIterator<Tag> peekingIterator3 = (PeekingIterator<Tag>)Iterators.peekingIterator(stream.iterator());
        if (!peekingIterator3.hasNext()) {
            return new ListTag();
        }
        final Tag iu4 = (Tag)peekingIterator3.peek();
        if (iu4 instanceof ByteTag) {
            final List<Byte> list5 = (List<Byte>)Lists.newArrayList(Iterators.transform((Iterator)peekingIterator3, iu -> ((ByteTag)iu).getAsByte()));
            return new ByteArrayTag(list5);
        }
        if (iu4 instanceof IntTag) {
            final List<Integer> list6 = (List<Integer>)Lists.newArrayList(Iterators.transform((Iterator)peekingIterator3, iu -> ((IntTag)iu).getAsInt()));
            return new IntArrayTag(list6);
        }
        if (iu4 instanceof LongTag) {
            final List<Long> list7 = (List<Long>)Lists.newArrayList(Iterators.transform((Iterator)peekingIterator3, iu -> ((LongTag)iu).getAsLong()));
            return new LongArrayTag(list7);
        }
        final ListTag ik5 = new ListTag();
        while (peekingIterator3.hasNext()) {
            final Tag iu5 = (Tag)peekingIterator3.next();
            if (iu5 instanceof EndTag) {
                continue;
            }
            ik5.add(iu5);
        }
        return ik5;
    }
    
    public Tag remove(final Tag iu, final String string) {
        if (iu instanceof CompoundTag) {
            final CompoundTag id4 = (CompoundTag)iu;
            final CompoundTag id5 = new CompoundTag();
            id4.getAllKeys().stream().filter(string2 -> !Objects.equals(string2, string)).forEach(string -> id5.put(string, id4.get(string)));
            return id5;
        }
        return iu;
    }
    
    public String toString() {
        return "NBT";
    }
    
    static {
        INSTANCE = new NbtOps();
    }
}
