package net.minecraft.nbt;

import net.minecraft.util.Mth;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class DoubleTag extends NumericTag {
    private double data;
    
    DoubleTag() {
    }
    
    public DoubleTag(final double double1) {
        this.data = double1;
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(this.data);
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(128L);
        this.data = dataInput.readDouble();
    }
    
    public byte getId() {
        return 6;
    }
    
    public String toString() {
        return new StringBuilder().append(this.data).append("d").toString();
    }
    
    public DoubleTag copy() {
        return new DoubleTag(this.data);
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof DoubleTag && this.data == ((DoubleTag)object).data);
    }
    
    public int hashCode() {
        final long long2 = Double.doubleToLongBits(this.data);
        return (int)(long2 ^ long2 >>> 32);
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        final Component jo4 = new TextComponent("d").withStyle(DoubleTag.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new TextComponent(String.valueOf(this.data)).append(jo4).withStyle(DoubleTag.SYNTAX_HIGHLIGHTING_NUMBER);
    }
    
    @Override
    public long getAsLong() {
        return (long)Math.floor(this.data);
    }
    
    @Override
    public int getAsInt() {
        return Mth.floor(this.data);
    }
    
    @Override
    public short getAsShort() {
        return (short)(Mth.floor(this.data) & 0xFFFF);
    }
    
    @Override
    public byte getAsByte() {
        return (byte)(Mth.floor(this.data) & 0xFF);
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
