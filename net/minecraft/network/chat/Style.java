package net.minecraft.network.chat;

import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;

public class Style {
    private Style parent;
    private ChatFormatting color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private String insertion;
    private static final Style ROOT;
    
    @Nullable
    public ChatFormatting getColor() {
        return (this.color == null) ? this.getParent().getColor() : this.color;
    }
    
    public boolean isBold() {
        return (this.bold == null) ? this.getParent().isBold() : this.bold;
    }
    
    public boolean isItalic() {
        return (this.italic == null) ? this.getParent().isItalic() : this.italic;
    }
    
    public boolean isStrikethrough() {
        return (this.strikethrough == null) ? this.getParent().isStrikethrough() : this.strikethrough;
    }
    
    public boolean isUnderlined() {
        return (this.underlined == null) ? this.getParent().isUnderlined() : this.underlined;
    }
    
    public boolean isObfuscated() {
        return (this.obfuscated == null) ? this.getParent().isObfuscated() : this.obfuscated;
    }
    
    public boolean isEmpty() {
        return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null && this.clickEvent == null && this.hoverEvent == null && this.insertion == null;
    }
    
    @Nullable
    public ClickEvent getClickEvent() {
        return (this.clickEvent == null) ? this.getParent().getClickEvent() : this.clickEvent;
    }
    
    @Nullable
    public HoverEvent getHoverEvent() {
        return (this.hoverEvent == null) ? this.getParent().getHoverEvent() : this.hoverEvent;
    }
    
    @Nullable
    public String getInsertion() {
        return (this.insertion == null) ? this.getParent().getInsertion() : this.insertion;
    }
    
    public Style setColor(final ChatFormatting c) {
        this.color = c;
        return this;
    }
    
    public Style setBold(final Boolean boolean1) {
        this.bold = boolean1;
        return this;
    }
    
    public Style setItalic(final Boolean boolean1) {
        this.italic = boolean1;
        return this;
    }
    
    public Style setStrikethrough(final Boolean boolean1) {
        this.strikethrough = boolean1;
        return this;
    }
    
    public Style setUnderlined(final Boolean boolean1) {
        this.underlined = boolean1;
        return this;
    }
    
    public Style setObfuscated(final Boolean boolean1) {
        this.obfuscated = boolean1;
        return this;
    }
    
    public Style setClickEvent(final ClickEvent jn) {
        this.clickEvent = jn;
        return this;
    }
    
    public Style setHoverEvent(final HoverEvent jr) {
        this.hoverEvent = jr;
        return this;
    }
    
    public Style setInsertion(final String string) {
        this.insertion = string;
        return this;
    }
    
    public Style inheritFrom(final Style jw) {
        this.parent = jw;
        return this;
    }
    
    public String getLegacyFormatCodes() {
        if (!this.isEmpty()) {
            final StringBuilder stringBuilder2 = new StringBuilder();
            if (this.getColor() != null) {
                stringBuilder2.append(this.getColor());
            }
            if (this.isBold()) {
                stringBuilder2.append(ChatFormatting.BOLD);
            }
            if (this.isItalic()) {
                stringBuilder2.append(ChatFormatting.ITALIC);
            }
            if (this.isUnderlined()) {
                stringBuilder2.append(ChatFormatting.UNDERLINE);
            }
            if (this.isObfuscated()) {
                stringBuilder2.append(ChatFormatting.OBFUSCATED);
            }
            if (this.isStrikethrough()) {
                stringBuilder2.append(ChatFormatting.STRIKETHROUGH);
            }
            return stringBuilder2.toString();
        }
        if (this.parent != null) {
            return this.parent.getLegacyFormatCodes();
        }
        return "";
    }
    
    private Style getParent() {
        return (this.parent == null) ? Style.ROOT : this.parent;
    }
    
    public String toString() {
        return new StringBuilder().append("Style{hasParent=").append(this.parent != null).append(", color=").append(this.color).append(", bold=").append(this.bold).append(", italic=").append(this.italic).append(", underlined=").append(this.underlined).append(", obfuscated=").append(this.obfuscated).append(", clickEvent=").append(this.getClickEvent()).append(", hoverEvent=").append(this.getHoverEvent()).append(", insertion=").append(this.getInsertion()).append('}').toString();
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Style) {
            final Style jw3 = (Style)object;
            if (this.isBold() == jw3.isBold() && this.getColor() == jw3.getColor() && this.isItalic() == jw3.isItalic() && this.isObfuscated() == jw3.isObfuscated() && this.isStrikethrough() == jw3.isStrikethrough() && this.isUnderlined() == jw3.isUnderlined()) {
                if (this.getClickEvent() != null) {
                    if (!this.getClickEvent().equals(jw3.getClickEvent())) {
                        return false;
                    }
                }
                else if (jw3.getClickEvent() != null) {
                    return false;
                }
                if (this.getHoverEvent() != null) {
                    if (!this.getHoverEvent().equals(jw3.getHoverEvent())) {
                        return false;
                    }
                }
                else if (jw3.getHoverEvent() != null) {
                    return false;
                }
                if ((this.getInsertion() == null) ? (jw3.getInsertion() == null) : this.getInsertion().equals(jw3.getInsertion())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public int hashCode() {
        return Objects.hash(new Object[] { this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion });
    }
    
    public Style copy() {
        final Style jw2 = new Style();
        jw2.bold = this.bold;
        jw2.italic = this.italic;
        jw2.strikethrough = this.strikethrough;
        jw2.underlined = this.underlined;
        jw2.obfuscated = this.obfuscated;
        jw2.color = this.color;
        jw2.clickEvent = this.clickEvent;
        jw2.hoverEvent = this.hoverEvent;
        jw2.parent = this.parent;
        jw2.insertion = this.insertion;
        return jw2;
    }
    
    public Style flatCopy() {
        final Style jw2 = new Style();
        jw2.setBold(this.isBold());
        jw2.setItalic(this.isItalic());
        jw2.setStrikethrough(this.isStrikethrough());
        jw2.setUnderlined(this.isUnderlined());
        jw2.setObfuscated(this.isObfuscated());
        jw2.setColor(this.getColor());
        jw2.setClickEvent(this.getClickEvent());
        jw2.setHoverEvent(this.getHoverEvent());
        jw2.setInsertion(this.getInsertion());
        return jw2;
    }
    
    static {
        ROOT = new Style() {
            @Nullable
            @Override
            public ChatFormatting getColor() {
                return null;
            }
            
            @Override
            public boolean isBold() {
                return false;
            }
            
            @Override
            public boolean isItalic() {
                return false;
            }
            
            @Override
            public boolean isStrikethrough() {
                return false;
            }
            
            @Override
            public boolean isUnderlined() {
                return false;
            }
            
            @Override
            public boolean isObfuscated() {
                return false;
            }
            
            @Nullable
            @Override
            public ClickEvent getClickEvent() {
                return null;
            }
            
            @Nullable
            @Override
            public HoverEvent getHoverEvent() {
                return null;
            }
            
            @Nullable
            @Override
            public String getInsertion() {
                return null;
            }
            
            @Override
            public Style setColor(final ChatFormatting c) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setBold(final Boolean boolean1) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setItalic(final Boolean boolean1) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setStrikethrough(final Boolean boolean1) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setUnderlined(final Boolean boolean1) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setObfuscated(final Boolean boolean1) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setClickEvent(final ClickEvent jn) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style setHoverEvent(final HoverEvent jr) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Style inheritFrom(final Style jw) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public String toString() {
                return "Style.ROOT";
            }
            
            @Override
            public Style copy() {
                return this;
            }
            
            @Override
            public Style flatCopy() {
                return this;
            }
            
            @Override
            public String getLegacyFormatCodes() {
                return "";
            }
        };
    }
    
    public static class Serializer implements JsonDeserializer<Style>, JsonSerializer<Style> {
        @Nullable
        public Style deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonObject()) {
                return null;
            }
            final Style jw5 = new Style();
            final JsonObject jsonObject6 = jsonElement.getAsJsonObject();
            if (jsonObject6 == null) {
                return null;
            }
            if (jsonObject6.has("bold")) {
                jw5.bold = jsonObject6.get("bold").getAsBoolean();
            }
            if (jsonObject6.has("italic")) {
                jw5.italic = jsonObject6.get("italic").getAsBoolean();
            }
            if (jsonObject6.has("underlined")) {
                jw5.underlined = jsonObject6.get("underlined").getAsBoolean();
            }
            if (jsonObject6.has("strikethrough")) {
                jw5.strikethrough = jsonObject6.get("strikethrough").getAsBoolean();
            }
            if (jsonObject6.has("obfuscated")) {
                jw5.obfuscated = jsonObject6.get("obfuscated").getAsBoolean();
            }
            if (jsonObject6.has("color")) {
                jw5.color = (ChatFormatting)jsonDeserializationContext.deserialize(jsonObject6.get("color"), (Type)ChatFormatting.class);
            }
            if (jsonObject6.has("insertion")) {
                jw5.insertion = jsonObject6.get("insertion").getAsString();
            }
            if (jsonObject6.has("clickEvent")) {
                final JsonObject jsonObject7 = GsonHelper.getAsJsonObject(jsonObject6, "clickEvent");
                final String string8 = GsonHelper.getAsString(jsonObject7, "action", (String)null);
                final ClickEvent.Action a9 = (string8 == null) ? null : ClickEvent.Action.getByName(string8);
                final String string9 = GsonHelper.getAsString(jsonObject7, "value", (String)null);
                if (a9 != null && string9 != null && a9.isAllowedFromServer()) {
                    jw5.clickEvent = new ClickEvent(a9, string9);
                }
            }
            if (jsonObject6.has("hoverEvent")) {
                final JsonObject jsonObject7 = GsonHelper.getAsJsonObject(jsonObject6, "hoverEvent");
                final String string8 = GsonHelper.getAsString(jsonObject7, "action", (String)null);
                final HoverEvent.Action a10 = (string8 == null) ? null : HoverEvent.Action.getByName(string8);
                final Component jo10 = (Component)jsonDeserializationContext.deserialize(jsonObject7.get("value"), (Type)Component.class);
                if (a10 != null && jo10 != null && a10.isAllowedFromServer()) {
                    jw5.hoverEvent = new HoverEvent(a10, jo10);
                }
            }
            return jw5;
        }
        
        @Nullable
        public JsonElement serialize(final Style jw, final Type type, final JsonSerializationContext jsonSerializationContext) {
            if (jw.isEmpty()) {
                return null;
            }
            final JsonObject jsonObject5 = new JsonObject();
            if (jw.bold != null) {
                jsonObject5.addProperty("bold", jw.bold);
            }
            if (jw.italic != null) {
                jsonObject5.addProperty("italic", jw.italic);
            }
            if (jw.underlined != null) {
                jsonObject5.addProperty("underlined", jw.underlined);
            }
            if (jw.strikethrough != null) {
                jsonObject5.addProperty("strikethrough", jw.strikethrough);
            }
            if (jw.obfuscated != null) {
                jsonObject5.addProperty("obfuscated", jw.obfuscated);
            }
            if (jw.color != null) {
                jsonObject5.add("color", jsonSerializationContext.serialize(jw.color));
            }
            if (jw.insertion != null) {
                jsonObject5.add("insertion", jsonSerializationContext.serialize(jw.insertion));
            }
            if (jw.clickEvent != null) {
                final JsonObject jsonObject6 = new JsonObject();
                jsonObject6.addProperty("action", jw.clickEvent.getAction().getName());
                jsonObject6.addProperty("value", jw.clickEvent.getValue());
                jsonObject5.add("clickEvent", (JsonElement)jsonObject6);
            }
            if (jw.hoverEvent != null) {
                final JsonObject jsonObject6 = new JsonObject();
                jsonObject6.addProperty("action", jw.hoverEvent.getAction().getName());
                jsonObject6.add("value", jsonSerializationContext.serialize(jw.hoverEvent.getValue()));
                jsonObject5.add("hoverEvent", (JsonElement)jsonObject6);
            }
            return (JsonElement)jsonObject5;
        }
    }
}
