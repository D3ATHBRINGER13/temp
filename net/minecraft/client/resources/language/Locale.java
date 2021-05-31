package net.minecraft.client.resources.language;

import org.apache.logging.log4j.LogManager;
import java.util.IllegalFormatException;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonElement;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import net.minecraft.server.packs.resources.Resource;
import java.util.Iterator;
import java.io.FileNotFoundException;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
import net.minecraft.server.packs.resources.ResourceManager;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;

public class Locale {
    private static final Gson GSON;
    private static final Logger LOGGER;
    private static final Pattern UNSUPPORTED_FORMAT_PATTERN;
    protected final Map<String, String> storage;
    
    public Locale() {
        this.storage = (Map<String, String>)Maps.newHashMap();
    }
    
    public synchronized void loadFrom(final ResourceManager xi, final List<String> list) {
        this.storage.clear();
        for (final String string5 : list) {
            final String string6 = String.format("lang/%s.json", new Object[] { string5 });
            for (final String string7 : xi.getNamespaces()) {
                try {
                    final ResourceLocation qv9 = new ResourceLocation(string7, string6);
                    this.appendFrom(xi.getResources(qv9));
                }
                catch (FileNotFoundException ex) {}
                catch (Exception exception9) {
                    Locale.LOGGER.warn("Skipped language file: {}:{} ({})", string7, string6, exception9.toString());
                }
            }
        }
    }
    
    private void appendFrom(final List<Resource> list) {
        for (final Resource xh4 : list) {
            final InputStream inputStream5 = xh4.getInputStream();
            try {
                this.appendFrom(inputStream5);
            }
            finally {
                IOUtils.closeQuietly(inputStream5);
            }
        }
    }
    
    private void appendFrom(final InputStream inputStream) {
        final JsonElement jsonElement3 = (JsonElement)Locale.GSON.fromJson((Reader)new InputStreamReader(inputStream, StandardCharsets.UTF_8), (Class)JsonElement.class);
        final JsonObject jsonObject4 = GsonHelper.convertToJsonObject(jsonElement3, "strings");
        for (final Map.Entry<String, JsonElement> entry6 : jsonObject4.entrySet()) {
            final String string7 = Locale.UNSUPPORTED_FORMAT_PATTERN.matcher((CharSequence)GsonHelper.convertToString((JsonElement)entry6.getValue(), (String)entry6.getKey())).replaceAll("%$1s");
            this.storage.put(entry6.getKey(), string7);
        }
    }
    
    private String getOrDefault(final String string) {
        final String string2 = (String)this.storage.get(string);
        return (string2 == null) ? string : string2;
    }
    
    public String get(final String string, final Object[] arr) {
        final String string2 = this.getOrDefault(string);
        try {
            return String.format(string2, arr);
        }
        catch (IllegalFormatException illegalFormatException5) {
            return "Format error: " + string2;
        }
    }
    
    public boolean has(final String string) {
        return this.storage.containsKey(string);
    }
    
    static {
        GSON = new Gson();
        LOGGER = LogManager.getLogger();
        UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    }
}
