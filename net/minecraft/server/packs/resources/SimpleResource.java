package net.minecraft.server.packs.resources;

import java.util.concurrent.Executors;
import net.minecraft.DefaultUncaughtExceptionHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import net.minecraft.util.GsonHelper;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import java.io.InputStream;
import net.minecraft.resources.ResourceLocation;
import java.util.concurrent.Executor;
import org.apache.logging.log4j.Logger;

public class SimpleResource implements Resource {
    private static final Logger LOGGER;
    public static final Executor IO_EXECUTOR;
    private final String sourceName;
    private final ResourceLocation location;
    private final InputStream resourceStream;
    private final InputStream metadataStream;
    private boolean triedMetadata;
    private JsonObject metadata;
    
    public SimpleResource(final String string, final ResourceLocation qv, final InputStream inputStream3, @Nullable final InputStream inputStream4) {
        this.sourceName = string;
        this.location = qv;
        this.resourceStream = inputStream3;
        this.metadataStream = inputStream4;
    }
    
    public ResourceLocation getLocation() {
        return this.location;
    }
    
    public InputStream getInputStream() {
        return this.resourceStream;
    }
    
    public boolean hasMetadata() {
        return this.metadataStream != null;
    }
    
    @Nullable
    public <T> T getMetadata(final MetadataSectionSerializer<T> wp) {
        if (!this.hasMetadata()) {
            return null;
        }
        if (this.metadata == null && !this.triedMetadata) {
            this.triedMetadata = true;
            BufferedReader bufferedReader3 = null;
            try {
                bufferedReader3 = new BufferedReader((Reader)new InputStreamReader(this.metadataStream, StandardCharsets.UTF_8));
                this.metadata = GsonHelper.parse((Reader)bufferedReader3);
            }
            finally {
                IOUtils.closeQuietly((Reader)bufferedReader3);
            }
        }
        if (this.metadata == null) {
            return null;
        }
        final String string3 = wp.getMetadataSectionName();
        return this.metadata.has(string3) ? wp.fromJson(GsonHelper.getAsJsonObject(this.metadata, string3)) : null;
    }
    
    public String getSourceName() {
        return this.sourceName;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SimpleResource)) {
            return false;
        }
        final SimpleResource xo3 = (SimpleResource)object;
        Label_0054: {
            if (this.location != null) {
                if (this.location.equals(xo3.location)) {
                    break Label_0054;
                }
            }
            else if (xo3.location == null) {
                break Label_0054;
            }
            return false;
        }
        if (this.sourceName != null) {
            if (this.sourceName.equals(xo3.sourceName)) {
                return true;
            }
        }
        else if (xo3.sourceName == null) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        int integer2 = (this.sourceName != null) ? this.sourceName.hashCode() : 0;
        integer2 = 31 * integer2 + ((this.location != null) ? this.location.hashCode() : 0);
        return integer2;
    }
    
    public void close() throws IOException {
        this.resourceStream.close();
        if (this.metadataStream != null) {
            this.metadataStream.close();
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        IO_EXECUTOR = (Executor)Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Resource IO {0}").setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(SimpleResource.LOGGER)).build());
    }
}
