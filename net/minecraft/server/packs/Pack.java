package net.minecraft.server.packs;

import javax.annotation.Nullable;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import java.util.Set;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.Closeable;

public interface Pack extends Closeable {
    InputStream getRootResource(final String string) throws IOException;
    
    InputStream getResource(final PackType wm, final ResourceLocation qv) throws IOException;
    
    Collection<ResourceLocation> getResources(final PackType wm, final String string, final int integer, final Predicate<String> predicate);
    
    boolean hasResource(final PackType wm, final ResourceLocation qv);
    
    Set<String> getNamespaces(final PackType wm);
    
    @Nullable
     <T> T getMetadataSection(final MetadataSectionSerializer<T> wp) throws IOException;
    
    String getName();
}
