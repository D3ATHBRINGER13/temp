package net.minecraft.nbt;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class ByteTag extends NumericTag {
    private byte data;
    
    ByteTag() {
    }
    
    public ByteTag(final byte byte1) {
        this.data = byte1;
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeByte((int)this.data);
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(72L);
        this.data = dataInput.readByte();
    }
    
    public byte getId() {
        return 1;
    }
    
    public String toString() {
        return new StringBuilder().append((int)this.data).append("b").toString();
    }
    
    public ByteTag copy() {
        return new ByteTag(this.data);
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof ByteTag && this.data == ((ByteTag)object).data);
    }
    
    public int hashCode() {
        return this.data;
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        final Component jo4 = new TextComponent("b").withStyle(ByteTag.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new TextComponent(String.valueOf((int)this.data)).append(jo4).withStyle(ByteTag.SYNTAX_HIGHLIGHTING_NUMBER);
    }
    
    @Override
    public long getAsLong() {
        return this.data;
    }
    
    @Override
    public int getAsInt() {
        return this.data;
    }
    
    @Override
    public short getAsShort() {
        return this.data;
    }
    
    @Override
    public byte getAsByte() {
        return this.data;
    }
    
    @Override
    public double getAsDouble() {
        return this.data;
    }
    
    @Override
    public float getAsFloat() {
        return this.data;
    }
    
    @Override
    public Number getAsNumber() {
        return this.data;
    }
}
