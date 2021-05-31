package net.minecraft.nbt;

import com.google.common.base.Strings;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.util.Objects;
import java.io.DataInput;
import java.io.IOException;
import java.util.Iterator;
import java.io.DataOutput;
import com.google.common.collect.Lists;
import java.util.List;

public class ListTag extends CollectionTag<Tag> {
    private List<Tag> list;
    private byte type;
    
    public ListTag() {
        this.list = (List<Tag>)Lists.newArrayList();
        this.type = 0;
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        if (this.list.isEmpty()) {
            this.type = 0;
        }
        else {
            this.type = ((Tag)this.list.get(0)).getId();
        }
        dataOutput.writeByte((int)this.type);
        dataOutput.writeInt(this.list.size());
        for (final Tag iu4 : this.list) {
            iu4.write(dataOutput);
        }
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(296L);
        if (integer > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        this.type = dataInput.readByte();
        final int integer2 = dataInput.readInt();
        if (this.type == 0 && integer2 > 0) {
            throw new RuntimeException("Missing type on ListTag");
        }
        in.accountBits(32L * integer2);
        this.list = (List<Tag>)Lists.newArrayListWithCapacity(integer2);
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            final Tag iu7 = Tag.newTag(this.type);
            iu7.load(dataInput, integer + 1, in);
            this.list.add(iu7);
        }
    }
    
    public byte getId() {
        return 9;
    }
    
    public String toString() {
        final StringBuilder stringBuilder2 = new StringBuilder("[");
        for (int integer3 = 0; integer3 < this.list.size(); ++integer3) {
            if (integer3 != 0) {
                stringBuilder2.append(',');
            }
            stringBuilder2.append(this.list.get(integer3));
        }
        return stringBuilder2.append(']').toString();
    }
    
    private void updateTypeAfterRemove() {
        if (this.list.isEmpty()) {
            this.type = 0;
        }
    }
    
    @Override
    public Tag remove(final int integer) {
        final Tag iu3 = (Tag)this.list.remove(integer);
        this.updateTypeAfterRemove();
        return iu3;
    }
    
    public boolean isEmpty() {
        return this.list.isEmpty();
    }
    
    public CompoundTag getCompound(final int integer) {
        if (integer >= 0 && integer < this.list.size()) {
            final Tag iu3 = (Tag)this.list.get(integer);
            if (iu3.getId() == 10) {
                return (CompoundTag)iu3;
            }
        }
        return new CompoundTag();
    }
    
    public ListTag getList(final int integer) {
        if (integer >= 0 && integer < this.list.size()) {
            final Tag iu3 = (Tag)this.list.get(integer);
            if (iu3.getId() == 9) {
                return (ListTag)iu3;
            }
        }
        return new ListTag();
    }
    
    public short getShort(final int integer) {
        if (integer >= 0 && integer < this.list.size()) {
            final Tag iu3 = (Tag)this.list.get(integer);
            if (iu3.getId() == 2) {
                return ((ShortTag)iu3).getAsShort();
            }
        }
        return 0;
    }
    
    public int getInt(final int integer) {
        if (integer >= 0 && integer < this.list.size()) {
            final Tag iu3 = (Tag)this.list.get(integer);
            if (iu3.getId() == 3) {
                return ((IntTag)iu3).getAsInt();
            }
        }
        return 0;
    }
    
    public int[] getIntArray(final int integer) {
        if (integer >= 0 && integer < this.list.size()) {
            final Tag iu3 = (Tag)this.list.get(integer);
            if (iu3.getId() == 11) {
                return ((IntArrayTag)iu3).getAsIntArray();
            }
        }
        return new int[0];
    }
    
    public double getDouble(final int integer) {
        if (integer >= 0 && integer < this.list.size()) {
            final Tag iu3 = (Tag)this.list.get(integer);
            if (iu3.getId() == 6) {
                return ((DoubleTag)iu3).getAsDouble();
            }
        }
        return 0.0;
    }
    
    public float getFloat(final int integer) {
        if (integer >= 0 && integer < this.list.size()) {
            final Tag iu3 = (Tag)this.list.get(integer);
            if (iu3.getId() == 5) {
                return ((FloatTag)iu3).getAsFloat();
            }
        }
        return 0.0f;
    }
    
    public String getString(final int integer) {
        if (integer < 0 || integer >= this.list.size()) {
            return "";
        }
        final Tag iu3 = (Tag)this.list.get(integer);
        if (iu3.getId() == 8) {
            return iu3.getAsString();
        }
        return iu3.toString();
    }
    
    public int size() {
        return this.list.size();
    }
    
    public Tag get(final int integer) {
        return (Tag)this.list.get(integer);
    }
    
    @Override
    public Tag set(final int integer, final Tag iu) {
        final Tag iu2 = this.get(integer);
        if (!this.setTag(integer, iu)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", new Object[] { iu.getId(), this.type }));
        }
        return iu2;
    }
    
    @Override
    public void add(final int integer, final Tag iu) {
        if (!this.addTag(integer, iu)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", new Object[] { iu.getId(), this.type }));
        }
    }
    
    @Override
    public boolean setTag(final int integer, final Tag iu) {
        if (this.updateType(iu)) {
            this.list.set(integer, iu);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean addTag(final int integer, final Tag iu) {
        if (this.updateType(iu)) {
            this.list.add(integer, iu);
            return true;
        }
        return false;
    }
    
    private boolean updateType(final Tag iu) {
        if (iu.getId() == 0) {
            return false;
        }
        if (this.type == 0) {
            this.type = iu.getId();
            return true;
        }
        return this.type == iu.getId();
    }
    
    public ListTag copy() {
        final ListTag ik2 = new ListTag();
        ik2.type = this.type;
        for (final Tag iu4 : this.list) {
            final Tag iu5 = iu4.copy();
            ik2.list.add(iu5);
        }
        return ik2;
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof ListTag && Objects.equals(this.list, ((ListTag)object).list));
    }
    
    public int hashCode() {
        return this.list.hashCode();
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        if (this.isEmpty()) {
            return new TextComponent("[]");
        }
        final Component jo4 = new TextComponent("[");
        if (!string.isEmpty()) {
            jo4.append("\n");
        }
        for (int integer2 = 0; integer2 < this.list.size(); ++integer2) {
            final Component jo5 = new TextComponent(Strings.repeat(string, integer + 1));
            jo5.append(((Tag)this.list.get(integer2)).getPrettyDisplay(string, integer + 1));
            if (integer2 != this.list.size() - 1) {
                jo5.append(String.valueOf(',')).append(string.isEmpty() ? " " : "\n");
            }
            jo4.append(jo5);
        }
        if (!string.isEmpty()) {
            jo4.append("\n").append(Strings.repeat(string, integer));
        }
        jo4.append("]");
        return jo4;
    }
    
    public int getElementType() {
        return this.type;
    }
    
    public void clear() {
        this.list.clear();
        this.type = 0;
    }
}
