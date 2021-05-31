package net.minecraft.server.packs.resources;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import net.minecraft.resources.ResourceLocation;
import java.util.Collections;
import java.util.Set;
import com.google.common.collect.Lists;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.Pack;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements ResourceManager {
    private static final Logger LOGGER;
    protected final List<Pack> fallbacks;
    private final PackType type;
    
    public FallbackResourceManager(final PackType wm) {
        this.fallbacks = (List<Pack>)Lists.newArrayList();
        this.type = wm;
    }
    
    public void add(final Pack wl) {
        this.fallbacks.add(wl);
    }
    
    public Set<String> getNamespaces() {
        return (Set<String>)Collections.emptySet();
    }
    
    public Resource getResource(final ResourceLocation qv) throws IOException {
        this.validateLocation(qv);
        Pack wl3 = null;
        final ResourceLocation qv2 = getMetadataLocation(qv);
        for (int integer5 = this.fallbacks.size() - 1; integer5 >= 0; --integer5) {
            final Pack wl4 = (Pack)this.fallbacks.get(integer5);
            if (wl3 == null && wl4.hasResource(this.type, qv2)) {
                wl3 = wl4;
            }
            if (wl4.hasResource(this.type, qv)) {
                InputStream inputStream7 = null;
                if (wl3 != null) {
                    inputStream7 = this.getWrappedResource(qv2, wl3);
                }
                return new SimpleResource(wl4.getName(), qv, this.getWrappedResource(qv, wl4), inputStream7);
            }
        }
        throw new FileNotFoundException(qv.toString());
    }
    
    public boolean hasResource(final ResourceLocation qv) {
        if (!this.isValidLocation(qv)) {
            return false;
        }
        for (int integer3 = this.fallbacks.size() - 1; integer3 >= 0; --integer3) {
            final Pack wl4 = (Pack)this.fallbacks.get(integer3);
            if (wl4.hasResource(this.type, qv)) {
                return true;
            }
        }
        return false;
    }
    
    protected InputStream getWrappedResource(final ResourceLocation qv, final Pack wl) throws IOException {
        final InputStream inputStream4 = wl.getResource(this.type, qv);
        return FallbackResourceManager.LOGGER.isDebugEnabled() ? new LeakedResourceWarningInputStream(inputStream4, qv, wl.getName()) : inputStream4;
    }
    
    private void validateLocation(final ResourceLocation qv) throws IOException {
        if (!this.isValidLocation(qv)) {
            throw new IOException(new StringBuilder().append("Invalid relative path to resource: ").append(qv).toString());
        }
    }
    
    private boolean isValidLocation(final ResourceLocation qv) {
        return !qv.getPath().contains("..");
    }
    
    public List<Resource> getResources(final ResourceLocation qv) throws IOException {
        this.validateLocation(qv);
        final List<Resource> list3 = (List<Resource>)Lists.newArrayList();
        final ResourceLocation qv2 = getMetadataLocation(qv);
        for (final Pack wl6 : this.fallbacks) {
            if (wl6.hasResource(this.type, qv)) {
                final InputStream inputStream7 = wl6.hasResource(this.type, qv2) ? this.getWrappedResource(qv2, wl6) : null;
                list3.add(new SimpleResource(wl6.getName(), qv, this.getWrappedResource(qv, wl6), inputStream7));
            }
        }
        if (list3.isEmpty()) {
            throw new FileNotFoundException(qv.toString());
        }
        return list3;
    }
    
    public Collection<ResourceLocation> listResources(final String string, final Predicate<String> predicate) {
        final List<ResourceLocation> list4 = (List<ResourceLocation>)Lists.newArrayList();
        for (final Pack wl6 : this.fallbacks) {
            list4.addAll((Collection)wl6.getResources(this.type, string, Integer.MAX_VALUE, predicate));
        }
        Collections.sort((List)list4);
        return (Collection<ResourceLocation>)list4;
    }
    
    static ResourceLocation getMetadataLocation(final ResourceLocation qv) {
        return new ResourceLocation(qv.getNamespace(), qv.getPath() + ".mcmeta");
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    static class LeakedResourceWarningInputStream extends InputStream {
        private final InputStream wrapped;
        private final String message;
        private boolean closed;
        
        public LeakedResourceWarningInputStream(final InputStream inputStream, final ResourceLocation qv, final String string) {
            this.wrapped = inputStream;
            final ByteArrayOutputStream byteArrayOutputStream5 = new ByteArrayOutputStream();
            new Exception().printStackTrace(new PrintStream((OutputStream)byteArrayOutputStream5));
            this.message = new StringBuilder().append("Leaked resource: '").append(qv).append("' loaded from pack: '").append(string).append("'\n").append(byteArrayOutputStream5).toString();
        }
        
        public void close() throws IOException {
            this.wrapped.close();
            this.closed = true;
        }
        
        protected void finalize() throws Throwable {
            if (!this.closed) {
                FallbackResourceManager.LOGGER.warn(this.message);
            }
            super.finalize();
        }
        
        public int read() throws IOException {
            return this.wrapped.read();
        }
    }
}
