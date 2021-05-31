package net.minecraft.nbt;

import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.util.Arrays;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.List;

public class IntArrayTag extends CollectionTag<IntTag> {
    private int[] data;
    
    IntArrayTag() {
    }
    
    public IntArrayTag(final int[] arr) {
        this.data = arr;
    }
    
    public IntArrayTag(final List<Integer> list) {
        this(toArray(list));
    }
    
    private static int[] toArray(final List<Integer> list) {
        final int[] arr2 = new int[list.size()];
        for (int integer3 = 0; integer3 < list.size(); ++integer3) {
            final Integer integer4 = (Integer)list.get(integer3);
            arr2[integer3] = ((integer4 == null) ? 0 : integer4);
        }
        return arr2;
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.data.length);
        for (final int integer6 : this.data) {
            dataOutput.writeInt(integer6);
        }
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(192L);
        final int integer2 = dataInput.readInt();
        in.accountBits(32 * integer2);
        this.data = new int[integer2];
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            this.data[integer3] = dataInput.readInt();
        }
    }
    
    public byte getId() {
        return 11;
    }
    
    public String toString() {
        final StringBuilder stringBuilder2 = new StringBuilder("[I;");
        for (int integer3 = 0; integer3 < this.data.length; ++integer3) {
            if (integer3 != 0) {
                stringBuilder2.append(',');
            }
            stringBuilder2.append(this.data[integer3]);
        }
        return stringBuilder2.append(']').toString();
    }
    
    public IntArrayTag copy() {
        final int[] arr2 = new int[this.data.length];
        System.arraycopy(this.data, 0, arr2, 0, this.data.length);
        return new IntArrayTag(arr2);
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof IntArrayTag && Arrays.equals(this.data, ((IntArrayTag)object).data));
    }
    
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }
    
    public int[] getAsIntArray() {
        return this.data;
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        final Component jo4 = new TextComponent("I").withStyle(IntArrayTag.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        final Component jo5 = new TextComponent("[").append(jo4).append(";");
        for (int integer2 = 0; integer2 < this.data.length; ++integer2) {
            jo5.append(" ").append(new TextComponent(String.valueOf(this.data[integer2])).withStyle(IntArrayTag.SYNTAX_HIGHLIGHTING_NUMBER));
            if (integer2 != this.data.length - 1) {
                jo5.append(",");
            }
        }
        jo5.append("]");
        return jo5;
    }
    
    public int size() {
        return this.data.length;
    }
    
    public IntTag get(final int integer) {
        return new IntTag(this.data[integer]);
    }
    
    @Override
    public IntTag set(final int integer, final IntTag ij) {
        final int integer2 = this.data[integer];
        this.data[integer] = ij.getAsInt();
        return new IntTag(integer2);
    }
    
    @Override
    public void add(final int integer, final IntTag ij) {
        this.data = ArrayUtils.add(this.data, integer, ij.getAsInt());
    }
    
    @Override
    public boolean setTag(final int integer, final Tag iu) {
        if (iu instanceof NumericTag) {
            this.data[integer] = ((NumericTag)iu).getAsInt();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean addTag(final int integer, final Tag iu) {
        if (iu instanceof NumericTag) {
            this.data = ArrayUtils.add(this.data, integer, ((NumericTag)iu).getAsInt());
            return true;
        }
        return false;
    }
    
    @Override
    public IntTag remove(final int integer) {
        final int integer2 = this.data[integer];
        this.data = ArrayUtils.remove(this.data, integer);
        return new IntTag(integer2);
    }
    
    public void clear() {
        this.data = new int[0];
    }
}
