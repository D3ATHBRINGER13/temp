package net.minecraft.client.resources;

import java.nio.file.LinkOption;
import java.util.stream.Stream;
import java.nio.file.Path;
import java.util.Collections;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.FileVisitOption;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import java.io.File;

public class DirectAssetIndex extends AssetIndex {
    private final File assetsDirectory;
    
    public DirectAssetIndex(final File file) {
        this.assetsDirectory = file;
    }
    
    @Override
    public File getFile(final ResourceLocation qv) {
        return new File(this.assetsDirectory, qv.toString().replace(':', '/'));
    }
    
    @Override
    public File getFile(final String string) {
        return new File(this.assetsDirectory, string);
    }
    
    @Override
    public Collection<String> getFiles(final String string, final int integer, final Predicate<String> predicate) {
        final Path path5 = this.assetsDirectory.toPath().resolve("minecraft/");
        try (final Stream<Path> stream6 = (Stream<Path>)Files.walk(path5.resolve(string), integer, new FileVisitOption[0])) {
            return (Collection<String>)stream6.filter(path -> Files.isRegularFile(path, new LinkOption[0])).filter(path -> !path.endsWith(".mcmeta")).map(path5::relativize).map(Object::toString).map(string -> string.replaceAll("\\\\", "/")).filter((Predicate)predicate).collect(Collectors.toList());
        }
        catch (NoSuchFileException ex) {}
        catch (IOException iOException6) {
            DirectAssetIndex.LOGGER.warn("Unable to getFiles on {}", string, iOException6);
        }
        return (Collection<String>)Collections.emptyList();
    }
}
