package net.minecraft.network.chat;

import java.util.function.Supplier;
import net.minecraft.Util;
import com.google.gson.TypeAdapterFactory;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import javax.annotation.Nullable;
import com.google.gson.JsonPrimitive;
import java.util.Map;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import java.lang.reflect.Field;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.List;
import net.minecraft.ChatFormatting;
import java.util.Iterator;
import com.mojang.brigadier.Message;

public interface Component extends Message, Iterable<Component> {
    Component setStyle(final Style jw);
    
    Style getStyle();
    
    default Component append(final String string) {
        return this.append(new TextComponent(string));
    }
    
    Component append(final Component jo);
    
    String getContents();
    
    default String getString() {
        final StringBuilder stringBuilder2 = new StringBuilder();
        this.stream().forEach(jo -> stringBuilder2.append(jo.getContents()));
        return stringBuilder2.toString();
    }
    
    default String getString(final int integer) {
        final StringBuilder stringBuilder3 = new StringBuilder();
        final Iterator<Component> iterator4 = (Iterator<Component>)this.stream().iterator();
        while (iterator4.hasNext()) {
            final int integer2 = integer - stringBuilder3.length();
            if (integer2 <= 0) {
                break;
            }
            final String string6 = ((Component)iterator4.next()).getContents();
            stringBuilder3.append((string6.length() <= integer2) ? string6 : string6.substring(0, integer2));
        }
        return stringBuilder3.toString();
    }
    
    default String getColoredString() {
        final StringBuilder stringBuilder2 = new StringBuilder();
        String string3 = "";
        for (final Component jo5 : this.stream()) {
            final String string4 = jo5.getContents();
            if (!string4.isEmpty()) {
                final String string5 = jo5.getStyle().getLegacyFormatCodes();
                if (!string5.equals(string3)) {
                    if (!string3.isEmpty()) {
                        stringBuilder2.append(ChatFormatting.RESET);
                    }
                    stringBuilder2.append(string5);
                    string3 = string5;
                }
                stringBuilder2.append(string4);
            }
        }
        if (!string3.isEmpty()) {
            stringBuilder2.append(ChatFormatting.RESET);
        }
        return stringBuilder2.toString();
    }
    
    List<Component> getSiblings();
    
    Stream<Component> stream();
    
    default Stream<Component> flatStream() {
        return (Stream<Component>)this.stream().map(Component::flattenStyle);
    }
    
    default Iterator<Component> iterator() {
        return (Iterator<Component>)this.flatStream().iterator();
    }
    
    Component copy();
    
    default Component deepCopy() {
        final Component jo2 = this.copy();
        jo2.setStyle(this.getStyle().copy());
        for (final Component jo3 : this.getSiblings()) {
            jo2.append(jo3.deepCopy());
        }
        return jo2;
    }
    
    default Component withStyle(final Consumer<Style> consumer) {
        consumer.accept(this.getStyle());
        return this;
    }
    
    default Component withStyle(final ChatFormatting... arr) {
        for (final ChatFormatting c6 : arr) {
            this.withStyle(c6);
        }
        return this;
    }
    
    default Component withStyle(final ChatFormatting c) {
        final Style jw3 = this.getStyle();
        if (c.isColor()) {
            jw3.setColor(c);
        }
        if (c.isFormat()) {
            switch (c) {
                case OBFUSCATED: {
                    jw3.setObfuscated(true);
                    break;
                }
                case BOLD: {
                    jw3.setBold(true);
                    break;
                }
                case STRIKETHROUGH: {
                    jw3.setStrikethrough(true);
                    break;
                }
                case UNDERLINE: {
                    jw3.setUnderlined(true);
                    break;
                }
                case ITALIC: {
                    jw3.setItalic(true);
                    break;
                }
            }
        }
        return this;
    }
    
    default Component flattenStyle(final Component jo) {
        final Component jo2 = jo.copy();
        jo2.setStyle(jo.getStyle().flatCopy());
        return jo2;
    }
    
    public static class Serializer implements JsonDeserializer<Component>, JsonSerializer<Component> {
        private static final Gson GSON;
        private static final Field JSON_READER_POS;
        private static final Field JSON_READER_LINESTART;
        
        public Component deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                return new TextComponent(jsonElement.getAsString());
            }
            if (jsonElement.isJsonObject()) {
                final JsonObject jsonObject5 = jsonElement.getAsJsonObject();
                Component jo6;
                if (jsonObject5.has("text")) {
                    jo6 = new TextComponent(GsonHelper.getAsString(jsonObject5, "text"));
                }
                else if (jsonObject5.has("translate")) {
                    final String string7 = GsonHelper.getAsString(jsonObject5, "translate");
                    if (jsonObject5.has("with")) {
                        final JsonArray jsonArray8 = GsonHelper.getAsJsonArray(jsonObject5, "with");
                        final Object[] arr9 = new Object[jsonArray8.size()];
                        for (int integer10 = 0; integer10 < arr9.length; ++integer10) {
                            arr9[integer10] = this.deserialize(jsonArray8.get(integer10), type, jsonDeserializationContext);
                            if (arr9[integer10] instanceof TextComponent) {
                                final TextComponent jx11 = (TextComponent)arr9[integer10];
                                if (jx11.getStyle().isEmpty() && jx11.getSiblings().isEmpty()) {
                                    arr9[integer10] = jx11.getText();
                                }
                            }
                        }
                        jo6 = new TranslatableComponent(string7, arr9);
                    }
                    else {
                        jo6 = new TranslatableComponent(string7, new Object[0]);
                    }
                }
                else if (jsonObject5.has("score")) {
                    final JsonObject jsonObject6 = GsonHelper.getAsJsonObject(jsonObject5, "score");
                    if (!jsonObject6.has("name") || !jsonObject6.has("objective")) {
                        throw new JsonParseException("A score component needs a least a name and an objective");
                    }
                    jo6 = new ScoreComponent(GsonHelper.getAsString(jsonObject6, "name"), GsonHelper.getAsString(jsonObject6, "objective"));
                    if (jsonObject6.has("value")) {
                        ((ScoreComponent)jo6).setValue(GsonHelper.getAsString(jsonObject6, "value"));
                    }
                }
                else if (jsonObject5.has("selector")) {
                    jo6 = new SelectorComponent(GsonHelper.getAsString(jsonObject5, "selector"));
                }
                else if (jsonObject5.has("keybind")) {
                    jo6 = new KeybindComponent(GsonHelper.getAsString(jsonObject5, "keybind"));
                }
                else {
                    if (!jsonObject5.has("nbt")) {
                        throw new JsonParseException(new StringBuilder().append("Don't know how to turn ").append(jsonElement).append(" into a Component").toString());
                    }
                    final String string7 = GsonHelper.getAsString(jsonObject5, "nbt");
                    final boolean boolean8 = GsonHelper.getAsBoolean(jsonObject5, "interpret", false);
                    if (jsonObject5.has("block")) {
                        jo6 = new NbtComponent.BlockNbtComponent(string7, boolean8, GsonHelper.getAsString(jsonObject5, "block"));
                    }
                    else {
                        if (!jsonObject5.has("entity")) {
                            throw new JsonParseException(new StringBuilder().append("Don't know how to turn ").append(jsonElement).append(" into a Component").toString());
                        }
                        jo6 = new NbtComponent.EntityNbtComponent(string7, boolean8, GsonHelper.getAsString(jsonObject5, "entity"));
                    }
                }
                if (jsonObject5.has("extra")) {
                    final JsonArray jsonArray9 = GsonHelper.getAsJsonArray(jsonObject5, "extra");
                    if (jsonArray9.size() <= 0) {
                        throw new JsonParseException("Unexpected empty array of components");
                    }
                    for (int integer11 = 0; integer11 < jsonArray9.size(); ++integer11) {
                        jo6.append(this.deserialize(jsonArray9.get(integer11), type, jsonDeserializationContext));
                    }
                }
                jo6.setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, (Type)Style.class));
                return jo6;
            }
            if (jsonElement.isJsonArray()) {
                final JsonArray jsonArray10 = jsonElement.getAsJsonArray();
                Component jo6 = null;
                for (final JsonElement jsonElement2 : jsonArray10) {
                    final Component jo7 = this.deserialize(jsonElement2, (Type)jsonElement2.getClass(), jsonDeserializationContext);
                    if (jo6 == null) {
                        jo6 = jo7;
                    }
                    else {
                        jo6.append(jo7);
                    }
                }
                return jo6;
            }
            throw new JsonParseException(new StringBuilder().append("Don't know how to turn ").append(jsonElement).append(" into a Component").toString());
        }
        
        private void serializeStyle(final Style jw, final JsonObject jsonObject, final JsonSerializationContext jsonSerializationContext) {
            final JsonElement jsonElement5 = jsonSerializationContext.serialize(jw);
            if (jsonElement5.isJsonObject()) {
                final JsonObject jsonObject2 = (JsonObject)jsonElement5;
                for (final Map.Entry<String, JsonElement> entry8 : jsonObject2.entrySet()) {
                    jsonObject.add((String)entry8.getKey(), (JsonElement)entry8.getValue());
                }
            }
        }
        
        public JsonElement serialize(final Component jo, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject5 = new JsonObject();
            if (!jo.getStyle().isEmpty()) {
                this.serializeStyle(jo.getStyle(), jsonObject5, jsonSerializationContext);
            }
            if (!jo.getSiblings().isEmpty()) {
                final JsonArray jsonArray6 = new JsonArray();
                for (final Component jo2 : jo.getSiblings()) {
                    jsonArray6.add(this.serialize(jo2, (Type)jo2.getClass(), jsonSerializationContext));
                }
                jsonObject5.add("extra", (JsonElement)jsonArray6);
            }
            if (jo instanceof TextComponent) {
                jsonObject5.addProperty("text", ((TextComponent)jo).getText());
            }
            else if (jo instanceof TranslatableComponent) {
                final TranslatableComponent jy6 = (TranslatableComponent)jo;
                jsonObject5.addProperty("translate", jy6.getKey());
                if (jy6.getArgs() != null && jy6.getArgs().length > 0) {
                    final JsonArray jsonArray7 = new JsonArray();
                    for (final Object object11 : jy6.getArgs()) {
                        if (object11 instanceof Component) {
                            jsonArray7.add(this.serialize((Component)object11, (Type)object11.getClass(), jsonSerializationContext));
                        }
                        else {
                            jsonArray7.add((JsonElement)new JsonPrimitive(String.valueOf(object11)));
                        }
                    }
                    jsonObject5.add("with", (JsonElement)jsonArray7);
                }
            }
            else if (jo instanceof ScoreComponent) {
                final ScoreComponent ju6 = (ScoreComponent)jo;
                final JsonObject jsonObject6 = new JsonObject();
                jsonObject6.addProperty("name", ju6.getName());
                jsonObject6.addProperty("objective", ju6.getObjective());
                jsonObject6.addProperty("value", ju6.getContents());
                jsonObject5.add("score", (JsonElement)jsonObject6);
            }
            else if (jo instanceof SelectorComponent) {
                final SelectorComponent jv6 = (SelectorComponent)jo;
                jsonObject5.addProperty("selector", jv6.getPattern());
            }
            else if (jo instanceof KeybindComponent) {
                final KeybindComponent js6 = (KeybindComponent)jo;
                jsonObject5.addProperty("keybind", js6.getName());
            }
            else {
                if (!(jo instanceof NbtComponent)) {
                    throw new IllegalArgumentException(new StringBuilder().append("Don't know how to serialize ").append(jo).append(" as a Component").toString());
                }
                final NbtComponent jt6 = (NbtComponent)jo;
                jsonObject5.addProperty("nbt", jt6.getNbtPath());
                jsonObject5.addProperty("interpret", Boolean.valueOf(jt6.isInterpreting()));
                if (jo instanceof NbtComponent.BlockNbtComponent) {
                    final NbtComponent.BlockNbtComponent a7 = (NbtComponent.BlockNbtComponent)jo;
                    jsonObject5.addProperty("block", a7.getPos());
                }
                else {
                    if (!(jo instanceof NbtComponent.EntityNbtComponent)) {
                        throw new IllegalArgumentException(new StringBuilder().append("Don't know how to serialize ").append(jo).append(" as a Component").toString());
                    }
                    final NbtComponent.EntityNbtComponent b7 = (NbtComponent.EntityNbtComponent)jo;
                    jsonObject5.addProperty("entity", b7.getSelector());
                }
            }
            return (JsonElement)jsonObject5;
        }
        
        public static String toJson(final Component jo) {
            return Serializer.GSON.toJson(jo);
        }
        
        public static JsonElement toJsonTree(final Component jo) {
            return Serializer.GSON.toJsonTree(jo);
        }
        
        @Nullable
        public static Component fromJson(final String string) {
            return GsonHelper.<Component>fromJson(Serializer.GSON, string, Component.class, false);
        }
        
        @Nullable
        public static Component fromJson(final JsonElement jsonElement) {
            return (Component)Serializer.GSON.fromJson(jsonElement, (Class)Component.class);
        }
        
        @Nullable
        public static Component fromJsonLenient(final String string) {
            return GsonHelper.<Component>fromJson(Serializer.GSON, string, Component.class, true);
        }
        
        public static Component fromJson(final StringReader stringReader) {
            try {
                final JsonReader jsonReader2 = new JsonReader((Reader)new java.io.StringReader(stringReader.getRemaining()));
                jsonReader2.setLenient(false);
                final Component jo3 = (Component)Serializer.GSON.getAdapter((Class)Component.class).read(jsonReader2);
                stringReader.setCursor(stringReader.getCursor() + getPos(jsonReader2));
                return jo3;
            }
            catch (IOException iOException2) {
                throw new JsonParseException((Throwable)iOException2);
            }
        }
        
        private static int getPos(final JsonReader jsonReader) {
            try {
                return Serializer.JSON_READER_POS.getInt(jsonReader) - Serializer.JSON_READER_LINESTART.getInt(jsonReader) + 1;
            }
            catch (IllegalAccessException illegalAccessException2) {
                throw new IllegalStateException("Couldn't read position of JsonReader", (Throwable)illegalAccessException2);
            }
        }
        
        static {
            GSON = Util.<Gson>make((java.util.function.Supplier<Gson>)(() -> {
                final GsonBuilder gsonBuilder1 = new GsonBuilder();
                gsonBuilder1.disableHtmlEscaping();
                gsonBuilder1.registerTypeHierarchyAdapter((Class)Component.class, new Serializer());
                gsonBuilder1.registerTypeHierarchyAdapter((Class)Style.class, new Style.Serializer());
                gsonBuilder1.registerTypeAdapterFactory((TypeAdapterFactory)new LowerCaseEnumTypeAdapterFactory());
                return gsonBuilder1.create();
            }));
            JSON_READER_POS = Util.<Field>make((java.util.function.Supplier<Field>)(() -> {
                try {
                    final JsonReader jsonReader = new JsonReader((Reader)new java.io.StringReader(""));
                    final Field field1 = JsonReader.class.getDeclaredField("pos");
                    field1.setAccessible(true);
                    return field1;
                }
                catch (NoSuchFieldException noSuchFieldException1) {
                    throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", (Throwable)noSuchFieldException1);
                }
            }));
            JSON_READER_LINESTART = Util.<Field>make((java.util.function.Supplier<Field>)(() -> {
                try {
                    final JsonReader jsonReader = new JsonReader((Reader)new java.io.StringReader(""));
                    final Field field1 = JsonReader.class.getDeclaredField("lineStart");
                    field1.setAccessible(true);
                    return field1;
                }
                catch (NoSuchFieldException noSuchFieldException1) {
                    throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", (Throwable)noSuchFieldException1);
                }
            }));
        }
    }
}
