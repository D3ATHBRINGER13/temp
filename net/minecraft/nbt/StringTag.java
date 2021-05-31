package net.minecraft.nbt;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.Objects;

public class StringTag implements Tag {
    private String data;
    
    public StringTag() {
        this("");
    }
    
    public StringTag(final String string) {
        Objects.requireNonNull(string, "Null string not allowed");
        this.data = string;
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.data);
    }
    
    public void load(final DataInput dataInput, final int integer, final NbtAccounter in) throws IOException {
        in.accountBits(288L);
        this.data = dataInput.readUTF();
        in.accountBits(16 * this.data.length());
    }
    
    public byte getId() {
        return 8;
    }
    
    public String toString() {
        return quoteAndEscape(this.data);
    }
    
    public StringTag copy() {
        return new StringTag(this.data);
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof StringTag && Objects.equals(this.data, ((StringTag)object).data));
    }
    
    public int hashCode() {
        return this.data.hashCode();
    }
    
    public String getAsString() {
        return this.data;
    }
    
    public Component getPrettyDisplay(final String string, final int integer) {
        final String string2 = quoteAndEscape(this.data);
        final String string3 = string2.substring(0, 1);
        final Component jo6 = new TextComponent(string2.substring(1, string2.length() - 1)).withStyle(StringTag.SYNTAX_HIGHLIGHTING_STRING);
        return new TextComponent(string3).append(jo6).append(string3);
    }
    
    public static String quoteAndEscape(final String string) {
        final StringBuilder stringBuilder2 = new StringBuilder(" ");
        char character3 = '\0';
        for (int integer4 = 0; integer4 < string.length(); ++integer4) {
            final char character4 = string.charAt(integer4);
            if (character4 == '\\') {
                stringBuilder2.append('\\');
            }
            else if (character4 == '\"' || character4 == '\'') {
                if (character3 == '\0') {
                    character3 = ((character4 == '\"') ? '\'' : '\"');
                }
                if (character3 == character4) {
                    stringBuilder2.append('\\');
                }
            }
            stringBuilder2.append(character4);
        }
        if (character3 == '\0') {
            character3 = '\"';
        }
        stringBuilder2.setCharAt(0, character3);
        stringBuilder2.append(character3);
        return stringBuilder2.toString();
    }
}
