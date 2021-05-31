package net.minecraft.server.packs.resources;

import net.minecraft.server.packs.Pack;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.List;
import java.io.IOException;
import net.minecraft.resources.ResourceLocation;
import java.util.Set;

public interface ResourceManager {
    Set<String> getNamespaces();
    
    Resource getResource(final ResourceLocation qv) throws IOException;
    
    boolean hasResource(final ResourceLocation qv);
    
    List<Resource> getResources(final ResourceLocation qv) throws IOException;
    
    Collection<ResourceLocation> listResources(final String string, final Predicate<String> predicate);
    
    void add(final Pack wl);
}
