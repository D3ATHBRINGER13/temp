package net.minecraft.nbt;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class IntTag extends NumericTag {
    private int data;
    
    IntTag() {
    }
    
    public IntTag(final int integer) {
        this.data = integer;
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.data);
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(96L);
        this.data = dataInput.readInt();
    }
    
    public byte getId() {
        return 3;
    }
    
    public String toString() {
        return String.valueOf(this.data);
    }
    
    public IntTag copy() {
        return new IntTag(this.data);
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof IntTag && this.data == ((IntTag)object).data);
    }
    
    public int hashCode() {
        return this.data;
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        return new TextComponent(String.valueOf(this.data)).withStyle(IntTag.SYNTAX_HIGHLIGHTING_NUMBER);
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
        return (short)(this.data & 0xFFFF);
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
        return (float)this.data;
    }
    
    @Override
    public Number getAsNumber() {
        return this.data;
    }
}
