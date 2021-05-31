package net.minecraft.nbt;

import net.minecraft.util.Mth;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class FloatTag extends NumericTag {
    private float data;
    
    FloatTag() {
    }
    
    public FloatTag(final float float1) {
        this.data = float1;
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeFloat(this.data);
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(96L);
        this.data = dataInput.readFloat();
    }
    
    public byte getId() {
        return 5;
    }
    
    public String toString() {
        return new StringBuilder().append(this.data).append("f").toString();
    }
    
    public FloatTag copy() {
        return new FloatTag(this.data);
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof FloatTag && this.data == ((FloatTag)object).data);
    }
    
    public int hashCode() {
        return Float.floatToIntBits(this.data);
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        final Component jo4 = new TextComponent("f").withStyle(FloatTag.SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new TextComponent(String.valueOf(this.data)).append(jo4).withStyle(FloatTag.SYNTAX_HIGHLIGHTING_NUMBER);
    }
    
    @Override
    public long getAsLong() {
        return (long)this.data;
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
        return this.data;
    }
    
    @Override
    public Number getAsNumber() {
        return this.data;
    }
}
