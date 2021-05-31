package net.minecraft.client.resources;

import java.util.stream.Collectors;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPack;

public class DefaultClientResourcePack extends VanillaPack {
    private final AssetIndex assetIndex;
    
    public DefaultClientResourcePack(final AssetIndex dxi) {
        super("minecraft", "realms");
        this.assetIndex = dxi;
    }
    
    @Nullable
    @Override
    protected InputStream getResourceAsStream(final PackType wm, final ResourceLocation qv) {
        if (wm == PackType.CLIENT_RESOURCES) {
            final File file4 = this.assetIndex.getFile(qv);
            if (file4 != null && file4.exists()) {
                try {
                    return (InputStream)new FileInputStream(file4);
                }
                catch (FileNotFoundException ex) {}
            }
        }
        return super.getResourceAsStream(wm, qv);
    }
    
    @Override
    public boolean hasResource(final PackType wm, final ResourceLocation qv) {
        if (wm == PackType.CLIENT_RESOURCES) {
            final File file4 = this.assetIndex.getFile(qv);
            if (file4 != null && file4.exists()) {
                return true;
            }
        }
        return super.hasResource(wm, qv);
    }
    
    @Nullable
    @Override
    protected InputStream getResourceAsStream(final String string) {
        final File file3 = this.assetIndex.getFile(string);
        if (file3 != null && file3.exists()) {
            try {
                return (InputStream)new FileInputStream(file3);
            }
            catch (FileNotFoundException ex) {}
        }
        return super.getResourceAsStream(string);
    }
    
    @Override
    public Collection<ResourceLocation> getResources(final PackType wm, final String string, final int integer, final Predicate<String> predicate) {
        final Collection<ResourceLocation> collection6 = super.getResources(wm, string, integer, predicate);
        collection6.addAll((Collection)this.assetIndex.getFiles(string, integer, predicate).stream().map(ResourceLocation::new).collect(Collectors.toList()));
        return collection6;
    }
}
