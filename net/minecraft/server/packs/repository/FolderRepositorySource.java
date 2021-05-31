package net.minecraft.server.packs.repository;

import net.minecraft.server.packs.FolderResourcePack;
import net.minecraft.server.packs.FileResourcePack;
import net.minecraft.server.packs.Pack;
import java.util.function.Supplier;
import java.util.Map;
import java.io.File;
import java.io.FileFilter;

public class FolderRepositorySource implements RepositorySource {
    private static final FileFilter RESOURCEPACK_FILTER;
    private final File folder;
    
    public FolderRepositorySource(final File file) {
        this.folder = file;
    }
    
    public <T extends UnopenedPack> void loadPacks(final Map<String, T> map, final UnopenedPack.UnopenedPackConstructor<T> b) {
        if (!this.folder.isDirectory()) {
            this.folder.mkdirs();
        }
        final File[] arr4 = this.folder.listFiles(FolderRepositorySource.RESOURCEPACK_FILTER);
        if (arr4 == null) {
            return;
        }
        for (final File file8 : arr4) {
            final String string9 = "file/" + file8.getName();
            final T xa10 = UnopenedPack.<T>create(string9, false, this.createSupplier(file8), b, UnopenedPack.Position.TOP);
            if (xa10 != null) {
                map.put(string9, xa10);
            }
        }
    }
    
    private Supplier<Pack> createSupplier(final File file) {
        if (file.isDirectory()) {
            return (Supplier<Pack>)(() -> new FolderResourcePack(file));
        }
        return (Supplier<Pack>)(() -> new FileResourcePack(file));
    }
    
    static {
        RESOURCEPACK_FILTER = (file -> {
            final boolean boolean2 = file.isFile() && file.getName().endsWith(".zip");
            final boolean boolean3 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
            return boolean2 || boolean3;
        });
    }
}
