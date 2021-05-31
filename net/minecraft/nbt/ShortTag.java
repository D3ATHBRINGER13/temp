package net.minecraft.nbt;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class ShortTag extends NumericTag {
    private short data;
    
    public ShortTag() {
    }
    
    public ShortTag(final short short1) {
        this.data = short1;
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeShort((int)this.data);
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(80L);
        this.data = dataInput.readShort();
    }
    
    public byte getId() {
        return 2;
    }
    
    public String toString() {
        return new StringBuilder().append((int)this.data).append("s").toString();
    }
    
    public ShortTag copy() {
        return new ShortTag(this.data);
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof ShortTag && this.data == ((ShortTag)object).data);
    }
    
    public int hashCode() {
        return this.data;
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        final Component jo4 = new TextComponent("s").withStyle(ShortTag.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new TextComponent(String.valueOf((int)this.data)).append(jo4).withStyle(ShortTag.SYNTAX_HIGHLIGHTING_NUMBER);
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
        return (byte)(this.data & 0xFF);
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
