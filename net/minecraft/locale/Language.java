package net.minecraft.locale;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import com.google.gson.JsonObject;
import java.io.InputStream;
import com.google.gson.JsonParseException;
import java.io.IOException;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;

public class Language {
    private static final Logger LOGGER;
    private static final Pattern UNSUPPORTED_FORMAT_PATTERN;
    private static final Language SINGLETON;
    private final Map<String, String> storage;
    private long lastUpdateTime;
    
    public Language() {
        this.storage = (Map<String, String>)Maps.newHashMap();
        try (final InputStream inputStream2 = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json")) {
            final JsonElement jsonElement4 = (JsonElement)new Gson().fromJson((Reader)new InputStreamReader(inputStream2, StandardCharsets.UTF_8), (Class)JsonElement.class);
            final JsonObject jsonObject5 = GsonHelper.convertToJsonObject(jsonElement4, "strings");
            for (final Map.Entry<String, JsonElement> entry7 : jsonObject5.entrySet()) {
                final String string8 = Language.UNSUPPORTED_FORMAT_PATTERN.matcher((CharSequence)GsonHelper.convertToString((JsonElement)entry7.getValue(), (String)entry7.getKey())).replaceAll("%$1s");
                this.storage.put(entry7.getKey(), string8);
            }
            this.lastUpdateTime = Util.getMillis();
        }
        catch (IOException | JsonParseException ex2) {
            final Exception ex;
            final Exception exception2 = ex;
            Language.LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)exception2);
        }
    }
    
    public static Language getInstance() {
        return Language.SINGLETON;
    }
    
    public static synchronized void forceData(final Map<String, String> map) {
        Language.SINGLETON.storage.clear();
        Language.SINGLETON.storage.putAll((Map)map);
        Language.SINGLETON.lastUpdateTime = Util.getMillis();
    }
    
    public synchronized String getElement(final String string) {
        return this.getProperty(string);
    }
    
    private String getProperty(final String string) {
        final String string2 = (String)this.storage.get(string);
        return (string2 == null) ? string : string2;
    }
    
    public synchronized boolean exists(final String string) {
        return this.storage.containsKey(string);
    }
    
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
        SINGLETON = new Language();
    }
}
