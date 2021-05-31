package net.minecraft.nbt;

import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.util.Arrays;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.List;

public class ByteArrayTag extends CollectionTag<ByteTag> {
    private byte[] data;
    
    ByteArrayTag() {
    }
    
    public ByteArrayTag(final byte[] arr) {
        this.data = arr;
    }
    
    public ByteArrayTag(final List<Byte> list) {
        this(toArray(list));
    }
    
    private static byte[] toArray(final List<Byte> list) {
        final byte[] arr2 = new byte[list.size()];
        for (int integer3 = 0; integer3 < list.size(); ++integer3) {
            final Byte byte4 = (Byte)list.get(integer3);
            arr2[integer3] = (byte)((byte4 == null) ? 0 : ((byte)byte4));
        }
        return arr2;
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.data.length);
        dataOutput.write(this.data);
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(192L);
        final int integer2 = dataInput.readInt();
        in.accountBits(8 * integer2);
        dataInput.readFully(this.data = new byte[integer2]);
    }
    
    public byte getId() {
        return 7;
    }
    
    public String toString() {
        final StringBuilder stringBuilder2 = new StringBuilder("[B;");
        for (int integer3 = 0; integer3 < this.data.length; ++integer3) {
            if (integer3 != 0) {
                stringBuilder2.append(',');
            }
            stringBuilder2.append((int)this.data[integer3]).append('B');
        }
        return stringBuilder2.append(']').toString();
    }
    
    public Tag copy() {
        final byte[] arr2 = new byte[this.data.length];
        System.arraycopy(this.data, 0, arr2, 0, this.data.length);
        return new ByteArrayTag(arr2);
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof ByteArrayTag && Arrays.equals(this.data, ((ByteArrayTag)object).data));
    }
    
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        final Component jo4 = new TextComponent("B").withStyle(ByteArrayTag.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        final Component jo5 = new TextComponent("[").append(jo4).append(";");
        for (int integer2 = 0; integer2 < this.data.length; ++integer2) {
            final Component jo6 = new TextComponent(String.valueOf((int)this.data[integer2])).withStyle(ByteArrayTag.SYNTAX_HIGHLIGHTING_NUMBER);
            jo5.append(" ").append(jo6).append(jo4);
            if (integer2 != this.data.length - 1) {
                jo5.append(",");
            }
        }
        jo5.append("]");
        return jo5;
    }
    
    public byte[] getAsByteArray() {
        return this.data;
    }
    
    public int size() {
        return this.data.length;
    }
    
    public ByteTag get(final int integer) {
        return new ByteTag(this.data[integer]);
    }
    
    @Override
    public ByteTag set(final int integer, final ByteTag ib) {
        final byte byte4 = this.data[integer];
        this.data[integer] = ib.getAsByte();
        return new ByteTag(byte4);
    }
    
    @Override
    public void add(final int integer, final ByteTag ib) {
        this.data = ArrayUtils.add(this.data, integer, ib.getAsByte());
    }
    
    @Override
    public boolean setTag(final int integer, final Tag iu) {
        if (iu instanceof NumericTag) {
            this.data[integer] = ((NumericTag)iu).getAsByte();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean addTag(final int integer, final Tag iu) {
        if (iu instanceof NumericTag) {
            this.data = ArrayUtils.add(this.data, integer, ((NumericTag)iu).getAsByte());
            return true;
        }
        return false;
    }
    
    @Override
    public ByteTag remove(final int integer) {
        final byte byte3 = this.data[integer];
        this.data = ArrayUtils.remove(this.data, integer);
        return new ByteTag(byte3);
    }
    
    public void clear() {
        this.data = new byte[0];
    }
}
