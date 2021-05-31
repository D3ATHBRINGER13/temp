package net.minecraft.server.packs.resources;

import org.apache.logging.log4j.LogManager;
import java.io.InputStream;
import java.util.Iterator;
import java.io.IOException;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;
import com.google.common.collect.Maps;
import net.minecraft.util.profiling.ProfilerFiller;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public abstract class SimpleJsonResourceReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonObject>> {
    private static final Logger LOGGER;
    private static final int PATH_SUFFIX_LENGTH;
    private final Gson gson;
    private final String directory;
    
    public SimpleJsonResourceReloadListener(final Gson gson, final String string) {
        this.gson = gson;
        this.directory = string;
    }
    
    @Override
    protected Map<ResourceLocation, JsonObject> prepare(final ResourceManager xi, final ProfilerFiller agn) {
        final Map<ResourceLocation, JsonObject> map4 = (Map<ResourceLocation, JsonObject>)Maps.newHashMap();
        final int integer5 = this.directory.length() + 1;
        for (final ResourceLocation qv7 : xi.listResources(this.directory, (Predicate<String>)(string -> string.endsWith(".json")))) {
            final String string8 = qv7.getPath();
            final ResourceLocation qv8 = new ResourceLocation(qv7.getNamespace(), string8.substring(integer5, string8.length() - SimpleJsonResourceReloadListener.PATH_SUFFIX_LENGTH));
            try (final Resource xh10 = xi.getResource(qv7);
                 final InputStream inputStream12 = xh10.getInputStream();
                 final Reader reader14 = (Reader)new BufferedReader((Reader)new InputStreamReader(inputStream12, StandardCharsets.UTF_8))) {
                final JsonObject jsonObject16 = GsonHelper.<JsonObject>fromJson(this.gson, reader14, JsonObject.class);
                if (jsonObject16 != null) {
                    final JsonObject jsonObject17 = (JsonObject)map4.put(qv8, jsonObject16);
                    if (jsonObject17 != null) {
                        throw new IllegalStateException(new StringBuilder().append("Duplicate data file ignored with ID ").append(qv8).toString());
                    }
                }
                else {
                    SimpleJsonResourceReloadListener.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", qv8, qv7);
                }
            }
            catch (JsonParseException | IllegalArgumentException | IOException ex2) {
                final Exception ex;
                final Exception exception10 = ex;
                SimpleJsonResourceReloadListener.LOGGER.error("Couldn't parse data file {} from {}", qv8, qv7, exception10);
            }
        }
        return map4;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        PATH_SUFFIX_LENGTH = ".json".length();
    }
}
