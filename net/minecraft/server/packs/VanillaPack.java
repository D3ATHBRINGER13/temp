package net.minecraft.server.packs;

import java.util.function.Consumer;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import java.nio.file.FileSystemNotFoundException;
import java.util.Collections;
import java.nio.file.FileSystems;
import java.util.HashMap;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import java.io.File;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.nio.file.FileVisitOption;
import com.google.common.collect.Lists;
import java.net.URI;
import java.util.Enumeration;
import java.nio.file.NoSuchFileException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.net.URL;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.function.Predicate;
import java.io.FileNotFoundException;
import net.minecraft.resources.ResourceLocation;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.io.InputStream;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.nio.file.FileSystem;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import java.nio.file.Path;

public class VanillaPack implements Pack {
    public static Path generatedDir;
    private static final Logger LOGGER;
    public static Class<?> clientObject;
    private static final Map<PackType, FileSystem> JAR_FILESYSTEM_BY_TYPE;
    public final Set<String> namespaces;
    
    public VanillaPack(final String... arr) {
        this.namespaces = (Set<String>)ImmutableSet.copyOf((Object[])arr);
    }
    
    public InputStream getRootResource(final String string) throws IOException {
        if (string.contains("/") || string.contains("\\")) {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
        if (VanillaPack.generatedDir != null) {
            final Path path3 = VanillaPack.generatedDir.resolve(string);
            if (Files.exists(path3, new LinkOption[0])) {
                return Files.newInputStream(path3, new OpenOption[0]);
            }
        }
        return this.getResourceAsStream(string);
    }
    
    public InputStream getResource(final PackType wm, final ResourceLocation qv) throws IOException {
        final InputStream inputStream4 = this.getResourceAsStream(wm, qv);
        if (inputStream4 != null) {
            return inputStream4;
        }
        throw new FileNotFoundException(qv.getPath());
    }
    
    public Collection<ResourceLocation> getResources(final PackType wm, final String string, final int integer, final Predicate<String> predicate) {
        final Set<ResourceLocation> set6 = (Set<ResourceLocation>)Sets.newHashSet();
        if (VanillaPack.generatedDir != null) {
            try {
                set6.addAll((Collection)this.getResources(integer, "minecraft", VanillaPack.generatedDir.resolve(wm.getDirectory()).resolve("minecraft"), string, predicate));
            }
            catch (IOException ex2) {}
            if (wm == PackType.CLIENT_RESOURCES) {
                Enumeration<URL> enumeration7 = null;
                try {
                    enumeration7 = (Enumeration<URL>)VanillaPack.clientObject.getClassLoader().getResources(wm.getDirectory() + "/minecraft");
                }
                catch (IOException ex3) {}
                while (enumeration7 != null && enumeration7.hasMoreElements()) {
                    try {
                        final URI uRI8 = ((URL)enumeration7.nextElement()).toURI();
                        if (!"file".equals(uRI8.getScheme())) {
                            continue;
                        }
                        set6.addAll((Collection)this.getResources(integer, "minecraft", Paths.get(uRI8), string, predicate));
                    }
                    catch (URISyntaxException | IOException ex4) {}
                }
            }
        }
        try {
            final URL uRL7 = VanillaPack.class.getResource("/" + wm.getDirectory() + "/.mcassetsroot");
            if (uRL7 == null) {
                VanillaPack.LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
                return (Collection<ResourceLocation>)set6;
            }
            final URI uRI8 = uRL7.toURI();
            if ("file".equals(uRI8.getScheme())) {
                final URL uRL8 = new URL(uRL7.toString().substring(0, uRL7.toString().length() - ".mcassetsroot".length()) + "minecraft");
                if (uRL8 == null) {
                    return (Collection<ResourceLocation>)set6;
                }
                final Path path10 = Paths.get(uRL8.toURI());
                set6.addAll((Collection)this.getResources(integer, "minecraft", path10, string, predicate));
            }
            else if ("jar".equals(uRI8.getScheme())) {
                final Path path11 = ((FileSystem)VanillaPack.JAR_FILESYSTEM_BY_TYPE.get(wm)).getPath("/" + wm.getDirectory() + "/minecraft", new String[0]);
                set6.addAll((Collection)this.getResources(integer, "minecraft", path11, string, predicate));
            }
            else {
                VanillaPack.LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", uRI8);
            }
        }
        catch (FileNotFoundException | NoSuchFileException ex5) {}
        catch (URISyntaxException | IOException ex6) {
            final Exception ex;
            final Exception exception7 = ex;
            VanillaPack.LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)exception7);
        }
        return (Collection<ResourceLocation>)set6;
    }
    
    private Collection<ResourceLocation> getResources(final int integer, final String string2, final Path path, final String string4, final Predicate<String> predicate) throws IOException {
        final List<ResourceLocation> list7 = (List<ResourceLocation>)Lists.newArrayList();
        for (final Path path2 : Files.walk(path.resolve(string4), integer, new FileVisitOption[0])) {
            if (!path2.endsWith(".mcmeta") && Files.isRegularFile(path2, new LinkOption[0]) && predicate.test(path2.getFileName().toString())) {
                list7.add(new ResourceLocation(string2, path.relativize(path2).toString().replaceAll("\\\\", "/")));
            }
        }
        return (Collection<ResourceLocation>)list7;
    }
    
    @Nullable
    protected InputStream getResourceAsStream(final PackType wm, final ResourceLocation qv) {
        final String string4 = createPath(wm, qv);
        if (VanillaPack.generatedDir != null) {
            final Path path5 = VanillaPack.generatedDir.resolve(wm.getDirectory() + "/" + qv.getNamespace() + "/" + qv.getPath());
            if (Files.exists(path5, new LinkOption[0])) {
                try {
                    return Files.newInputStream(path5, new OpenOption[0]);
                }
                catch (IOException ex) {}
            }
        }
        try {
            final URL uRL5 = VanillaPack.class.getResource(string4);
            if (isResourceUrlValid(string4, uRL5)) {
                return uRL5.openStream();
            }
        }
        catch (IOException iOException5) {
            return VanillaPack.class.getResourceAsStream(string4);
        }
        return null;
    }
    
    private static String createPath(final PackType wm, final ResourceLocation qv) {
        return "/" + wm.getDirectory() + "/" + qv.getNamespace() + "/" + qv.getPath();
    }
    
    private static boolean isResourceUrlValid(final String string, @Nullable final URL uRL) throws IOException {
        return uRL != null && (uRL.getProtocol().equals("jar") || FolderResourcePack.validatePath(new File(uRL.getFile()), string));
    }
    
    @Nullable
    protected InputStream getResourceAsStream(final String string) {
        return VanillaPack.class.getResourceAsStream("/" + string);
    }
    
    public boolean hasResource(final PackType wm, final ResourceLocation qv) {
        final String string4 = createPath(wm, qv);
        if (VanillaPack.generatedDir != null) {
            final Path path5 = VanillaPack.generatedDir.resolve(wm.getDirectory() + "/" + qv.getNamespace() + "/" + qv.getPath());
            if (Files.exists(path5, new LinkOption[0])) {
                return true;
            }
        }
        try {
            final URL uRL5 = VanillaPack.class.getResource(string4);
            return isResourceUrlValid(string4, uRL5);
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    public Set<String> getNamespaces(final PackType wm) {
        return this.namespaces;
    }
    
    @Nullable
    public <T> T getMetadataSection(final MetadataSectionSerializer<T> wp) throws IOException {
        try (final InputStream inputStream3 = this.getRootResource("pack.mcmeta")) {
            return AbstractResourcePack.<T>getMetadataFromStream(wp, inputStream3);
        }
        catch (RuntimeException | FileNotFoundException ex2) {
            final Exception ex;
            final Exception exception3 = ex;
            return null;
        }
    }
    
    public String getName() {
        return "Default";
    }
    
    public void close() {
    }
    
    static {
        LOGGER = LogManager.getLogger();
        JAR_FILESYSTEM_BY_TYPE = Util.<Map>make((Map)Maps.newHashMap(), (java.util.function.Consumer<Map>)(hashMap -> {
            synchronized (VanillaPack.class) {
                for (final PackType wm6 : PackType.values()) {
                    final URL uRL7 = VanillaPack.class.getResource("/" + wm6.getDirectory() + "/.mcassetsroot");
                    try {
                        final URI uRI8 = uRL7.toURI();
                        if ("jar".equals(uRI8.getScheme())) {
                            FileSystem fileSystem9;
                            try {
                                fileSystem9 = FileSystems.getFileSystem(uRI8);
                            }
                            catch (FileSystemNotFoundException fileSystemNotFoundException10) {
                                fileSystem9 = FileSystems.newFileSystem(uRI8, Collections.emptyMap());
                            }
                            hashMap.put(wm6, fileSystem9);
                        }
                    }
                    catch (URISyntaxException | IOException ex2) {
                        final Exception ex;
                        final Exception exception8 = ex;
                        VanillaPack.LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)exception8);
                    }
                }
            }
        }));
    }
}
