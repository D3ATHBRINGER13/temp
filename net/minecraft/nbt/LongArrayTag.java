package net.minecraft.nbt;

import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.util.Arrays;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.List;
import it.unimi.dsi.fastutil.longs.LongSet;

public class LongArrayTag extends CollectionTag<LongTag> {
    private long[] data;
    
    LongArrayTag() {
    }
    
    public LongArrayTag(final long[] arr) {
        this.data = arr;
    }
    
    public LongArrayTag(final LongSet longSet) {
        this.data = longSet.toLongArray();
    }
    
    public LongArrayTag(final List<Long> list) {
        this(toArray(list));
    }
    
    private static long[] toArray(final List<Long> list) {
        final long[] arr2 = new long[list.size()];
        for (int integer3 = 0; integer3 < list.size(); ++integer3) {
            final Long long4 = (Long)list.get(integer3);
            arr2[integer3] = ((long4 == null) ? 0L : long4);
        }
        return arr2;
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.data.length);
        for (final long long6 : this.data) {
            dataOutput.writeLong(long6);
        }
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(192L);
        final int integer2 = dataInput.readInt();
        in.accountBits(64 * integer2);
        this.data = new long[integer2];
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            this.data[integer3] = dataInput.readLong();
        }
    }
    
    public byte getId() {
        return 12;
    }
    
    public String toString() {
        final StringBuilder stringBuilder2 = new StringBuilder("[L;");
        for (int integer3 = 0; integer3 < this.data.length; ++integer3) {
            if (integer3 != 0) {
                stringBuilder2.append(',');
            }
            stringBuilder2.append(this.data[integer3]).append('L');
        }
        return stringBuilder2.append(']').toString();
    }
    
    public LongArrayTag copy() {
        final long[] arr2 = new long[this.data.length];
        System.arraycopy(this.data, 0, arr2, 0, this.data.length);
        return new LongArrayTag(arr2);
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof LongArrayTag && Arrays.equals(this.data, ((LongArrayTag)object).data));
    }
    
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        final Component jo4 = new TextComponent("L").withStyle(LongArrayTag.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        final Component jo5 = new TextComponent("[").append(jo4).append(";");
        for (int integer2 = 0; integer2 < this.data.length; ++integer2) {
            final Component jo6 = new TextComponent(String.valueOf(this.data[integer2])).withStyle(LongArrayTag.SYNTAX_HIGHLIGHTING_NUMBER);
            jo5.append(" ").append(jo6).append(jo4);
            if (integer2 != this.data.length - 1) {
                jo5.append(",");
            }
        }
        jo5.append("]");
        return jo5;
    }
    
    public long[] getAsLongArray() {
        return this.data;
    }
    
    public int size() {
        return this.data.length;
    }
    
    public LongTag get(final int integer) {
        return new LongTag(this.data[integer]);
    }
    
    @Override
    public LongTag set(final int integer, final LongTag im) {
        final long long4 = this.data[integer];
        this.data[integer] = im.getAsLong();
        return new LongTag(long4);
    }
    
    @Override
    public void add(final int integer, final LongTag im) {
        this.data = ArrayUtils.add(this.data, integer, im.getAsLong());
    }
    
    @Override
    public boolean setTag(final int integer, final Tag iu) {
        if (iu instanceof NumericTag) {
            this.data[integer] = ((NumericTag)iu).getAsLong();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean addTag(final int integer, final Tag iu) {
        if (iu instanceof NumericTag) {
            this.data = ArrayUtils.add(this.data, integer, ((NumericTag)iu).getAsLong());
            return true;
        }
        return false;
    }
    
    @Override
    public LongTag remove(final int integer) {
        final long long3 = this.data[integer];
        this.data = ArrayUtils.remove(this.data, integer);
        return new LongTag(long3);
    }
    
    public void clear() {
        this.data = new long[0];
    }
}
