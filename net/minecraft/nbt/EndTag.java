package net.minecraft.nbt;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;

public class EndTag implements Tag {
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(64L);
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
    }
    
    public byte getId() {
        return 0;
    }
    
    public String toString() {
        return "END";
    }
    
    public EndTag copy() {
        return new EndTag();
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        return new TextComponent("");
    }
    
    public boolean equals(final Object object) {
        return object instanceof EndTag;
    }
    
    public int hashCode() {
        return this.getId();
    }
}
