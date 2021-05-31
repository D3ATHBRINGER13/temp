package net.minecraft.server.packs;

import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.resources.ResourceLocation;
import java.io.File;
import org.apache.logging.log4j.Logger;

public abstract class AbstractResourcePack implements Pack {
    private static final Logger LOGGER;
    protected final File file;
    
    public AbstractResourcePack(final File file) {
        this.file = file;
    }
    
    private static String getPathFromLocation(final PackType wm, final ResourceLocation qv) {
        return String.format("%s/%s/%s", new Object[] { wm.getDirectory(), qv.getNamespace(), qv.getPath() });
    }
    
    protected static String getRelativePath(final File file1, final File file2) {
        return file1.toURI().relativize(file2.toURI()).getPath();
    }
    
    public InputStream getResource(final PackType wm, final ResourceLocation qv) throws IOException {
        return this.getResource(getPathFromLocation(wm, qv));
    }
    
    public boolean hasResource(final PackType wm, final ResourceLocation qv) {
        return this.hasResource(getPathFromLocation(wm, qv));
    }
    
    protected abstract InputStream getResource(final String string) throws IOException;
    
    public InputStream getRootResource(final String string) throws IOException {
        if (string.contains("/") || string.contains("\\")) {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
        return this.getResource(string);
    }
    
    protected abstract boolean hasResource(final String string);
    
    protected void logWarning(final String string) {
        AbstractResourcePack.LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", string, this.file);
    }
    
    @Nullable
    public <T> T getMetadataSection(final MetadataSectionSerializer<T> wp) throws IOException {
        try (final InputStream inputStream3 = this.getResource("pack.mcmeta")) {
            return AbstractResourcePack.<T>getMetadataFromStream(wp, inputStream3);
        }
    }
    
    @Nullable
    public static <T> T getMetadataFromStream(final MetadataSectionSerializer<T> wp, final InputStream inputStream) {
        JsonObject jsonObject3;
        try (final BufferedReader bufferedReader4 = new BufferedReader((Reader)new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            jsonObject3 = GsonHelper.parse((Reader)bufferedReader4);
        }
        catch (IOException | JsonParseException ex2) {
            final Exception ex;
            final Exception exception4 = ex;
            AbstractResourcePack.LOGGER.error("Couldn't load {} metadata", wp.getMetadataSectionName(), exception4);
            return null;
        }
        if (!jsonObject3.has(wp.getMetadataSectionName())) {
            return null;
        }
        try {
            return wp.fromJson(GsonHelper.getAsJsonObject(jsonObject3, wp.getMetadataSectionName()));
        }
        catch (JsonParseException jsonParseException4) {
            AbstractResourcePack.LOGGER.error("Couldn't load {} metadata", wp.getMetadataSectionName(), jsonParseException4);
            return null;
        }
    }
    
    public String getName() {
        return this.file.getName();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
