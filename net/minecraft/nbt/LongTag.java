package net.minecraft.nbt;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class LongTag extends NumericTag {
    private long data;
    
    LongTag() {
    }
    
    public LongTag(final long long1) {
        this.data = long1;
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(this.data);
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(128L);
        this.data = dataInput.readLong();
    }
    
    public byte getId() {
        return 4;
    }
    
    public String toString() {
        return new StringBuilder().append(this.data).append("L").toString();
    }
    
    public LongTag copy() {
        return new LongTag(this.data);
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof LongTag && this.data == ((LongTag)object).data);
    }
    
    public int hashCode() {
        return (int)(this.data ^ this.data >>> 32);
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        final Component jo4 = new TextComponent("L").withStyle(LongTag.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new TextComponent(String.valueOf(this.data)).append(jo4).withStyle(LongTag.SYNTAX_HIGHLIGHTING_NUMBER);
    }
    
    @Override
    public long getAsLong() {
        return this.data;
    }
    
    @Override
    public int getAsInt() {
        return (int)(this.data & -1L);
    }
    
    @Override
    public short getAsShort() {
        return (short)(this.data & 0xFFFFL);
    }
    
    @Override
    public byte getAsByte() {
        return (byte)(this.data & 0xFFL);
    }
    
    @Override
    public double getAsDouble() {
        return (double)this.data;
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
