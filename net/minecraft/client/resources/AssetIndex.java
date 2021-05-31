package net.minecraft.client.resources;

import org.apache.logging.log4j.LogManager;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import com.google.gson.JsonElement;
import java.util.Iterator;
import java.io.BufferedReader;
import org.apache.commons.io.IOUtils;
import java.io.FileNotFoundException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import java.io.Reader;
import net.minecraft.util.GsonHelper;
import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class AssetIndex {
    protected static final Logger LOGGER;
    private final Map<String, File> mapping;
    
    protected AssetIndex() {
        this.mapping = (Map<String, File>)Maps.newHashMap();
    }
    
    public AssetIndex(final File file, final String string) {
        this.mapping = (Map<String, File>)Maps.newHashMap();
        final File file2 = new File(file, "objects");
        final File file3 = new File(file, "indexes/" + string + ".json");
        BufferedReader bufferedReader6 = null;
        try {
            bufferedReader6 = Files.newReader(file3, StandardCharsets.UTF_8);
            final JsonObject jsonObject7 = GsonHelper.parse((Reader)bufferedReader6);
            final JsonObject jsonObject8 = GsonHelper.getAsJsonObject(jsonObject7, "objects", (JsonObject)null);
            if (jsonObject8 != null) {
                for (final Map.Entry<String, JsonElement> entry10 : jsonObject8.entrySet()) {
                    final JsonObject jsonObject9 = (JsonObject)entry10.getValue();
                    final String string2 = (String)entry10.getKey();
                    final String[] arr13 = string2.split("/", 2);
                    final String string3 = (arr13.length == 1) ? arr13[0] : (arr13[0] + ":" + arr13[1]);
                    final String string4 = GsonHelper.getAsString(jsonObject9, "hash");
                    final File file4 = new File(file2, string4.substring(0, 2) + "/" + string4);
                    this.mapping.put(string3, file4);
                }
            }
        }
        catch (JsonParseException jsonParseException7) {
            AssetIndex.LOGGER.error("Unable to parse resource index file: {}", file3);
        }
        catch (FileNotFoundException fileNotFoundException7) {
            AssetIndex.LOGGER.error("Can't find the resource index file: {}", file3);
        }
        finally {
            IOUtils.closeQuietly((Reader)bufferedReader6);
        }
    }
    
    @Nullable
    public File getFile(final ResourceLocation qv) {
        return this.getFile(qv.toString());
    }
    
    @Nullable
    public File getFile(final String string) {
        return (File)this.mapping.get(string);
    }
    
    public Collection<String> getFiles(final String string, final int integer, final Predicate<String> predicate) {
        return (Collection<String>)this.mapping.keySet().stream().filter(string -> !string.endsWith(".mcmeta")).map(ResourceLocation::new).map(ResourceLocation::getPath).filter(string2 -> string2.startsWith(string + "/")).filter((Predicate)predicate).collect(Collectors.toList());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
