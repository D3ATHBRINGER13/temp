package net.minecraft.resources;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.mojang.brigadier.Message;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import org.apache.commons.lang3.StringUtils;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ResourceLocation implements Comparable<ResourceLocation> {
    private static final SimpleCommandExceptionType ERROR_INVALID;
    protected final String namespace;
    protected final String path;
    
    protected ResourceLocation(final String[] arr) {
        this.namespace = (StringUtils.isEmpty((CharSequence)arr[0]) ? "minecraft" : arr[0]);
        this.path = arr[1];
        if (!isValidNamespace(this.namespace)) {
            throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.path);
        }
        if (!isValidPath(this.path)) {
            throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.path);
        }
    }
    
    public ResourceLocation(final String string) {
        this(decompose(string, ':'));
    }
    
    public ResourceLocation(final String string1, final String string2) {
        this(new String[] { string1, string2 });
    }
    
    public static ResourceLocation of(final String string, final char character) {
        return new ResourceLocation(decompose(string, character));
    }
    
    @Nullable
    public static ResourceLocation tryParse(final String string) {
        try {
            return new ResourceLocation(string);
        }
        catch (ResourceLocationException n2) {
            return null;
        }
    }
    
    protected static String[] decompose(final String string, final char character) {
        final String[] arr3 = { "minecraft", string };
        final int integer4 = string.indexOf((int)character);
        if (integer4 >= 0) {
            arr3[1] = string.substring(integer4 + 1, string.length());
            if (integer4 >= 1) {
                arr3[0] = string.substring(0, integer4);
            }
        }
        return arr3;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    public String toString() {
        return this.namespace + ':' + this.path;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ResourceLocation) {
            final ResourceLocation qv3 = (ResourceLocation)object;
            return this.namespace.equals(qv3.namespace) && this.path.equals(qv3.path);
        }
        return false;
    }
    
    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }
    
    public int compareTo(final ResourceLocation qv) {
        int integer3 = this.path.compareTo(qv.path);
        if (integer3 == 0) {
            integer3 = this.namespace.compareTo(qv.namespace);
        }
        return integer3;
    }
    
    public static ResourceLocation read(final StringReader stringReader) throws CommandSyntaxException {
        final int integer2 = stringReader.getCursor();
        while (stringReader.canRead() && isAllowedInResourceLocation(stringReader.peek())) {
            stringReader.skip();
        }
        final String string3 = stringReader.getString().substring(integer2, stringReader.getCursor());
        try {
            return new ResourceLocation(string3);
        }
        catch (ResourceLocationException n4) {
            stringReader.setCursor(integer2);
            throw ResourceLocation.ERROR_INVALID.createWithContext((ImmutableStringReader)stringReader);
        }
    }
    
    public static boolean isAllowedInResourceLocation(final char character) {
        return (character >= '0' && character <= '9') || (character >= 'a' && character <= 'z') || character == '_' || character == ':' || character == '/' || character == '.' || character == '-';
    }
    
    private static boolean isValidPath(final String string) {
        return string.chars().allMatch(integer -> integer == 95 || integer == 45 || (integer >= 97 && integer <= 122) || (integer >= 48 && integer <= 57) || integer == 47 || integer == 46);
    }
    
    private static boolean isValidNamespace(final String string) {
        return string.chars().allMatch(integer -> integer == 95 || integer == 45 || (integer >= 97 && integer <= 122) || (integer >= 48 && integer <= 57) || integer == 46);
    }
    
    public static boolean isValidResourceLocation(final String string) {
        final String[] arr2 = decompose(string, ':');
        return isValidNamespace(StringUtils.isEmpty((CharSequence)arr2[0]) ? "minecraft" : arr2[0]) && isValidPath(arr2[1]);
    }
    
    static {
        ERROR_INVALID = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.id.invalid", new Object[0]));
    }
    
    public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
        public ResourceLocation deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new ResourceLocation(GsonHelper.convertToString(jsonElement, "location"));
        }
        
        public JsonElement serialize(final ResourceLocation qv, final Type type, final JsonSerializationContext jsonSerializationContext) {
            return (JsonElement)new JsonPrimitive(qv.toString());
        }
    }
}
