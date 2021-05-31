package net.minecraft.server.packs;

import net.minecraft.resources.ResourceLocation;
import java.util.Collection;
import java.util.function.Predicate;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import java.util.List;
import java.util.Enumeration;
import java.util.Locale;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.zip.ZipFile;
import com.google.common.base.Splitter;

public class FileResourcePack extends AbstractResourcePack {
    public static final Splitter SPLITTER;
    private ZipFile zipFile;
    
    public FileResourcePack(final File file) {
        super(file);
    }
    
    private ZipFile getOrCreateZipFile() throws IOException {
        if (this.zipFile == null) {
            this.zipFile = new ZipFile(this.file);
        }
        return this.zipFile;
    }
    
    @Override
    protected InputStream getResource(final String string) throws IOException {
        final ZipFile zipFile3 = this.getOrCreateZipFile();
        final ZipEntry zipEntry4 = zipFile3.getEntry(string);
        if (zipEntry4 == null) {
            throw new ResourcePackFileNotFoundException(this.file, string);
        }
        return zipFile3.getInputStream(zipEntry4);
    }
    
    public boolean hasResource(final String string) {
        try {
            return this.getOrCreateZipFile().getEntry(string) != null;
        }
        catch (IOException iOException3) {
            return false;
        }
    }
    
    public Set<String> getNamespaces(final PackType wm) {
        ZipFile zipFile3;
        try {
            zipFile3 = this.getOrCreateZipFile();
        }
        catch (IOException iOException4) {
            return (Set<String>)Collections.emptySet();
        }
        final Enumeration<? extends ZipEntry> enumeration4 = zipFile3.entries();
        final Set<String> set5 = (Set<String>)Sets.newHashSet();
        while (enumeration4.hasMoreElements()) {
            final ZipEntry zipEntry6 = (ZipEntry)enumeration4.nextElement();
            final String string7 = zipEntry6.getName();
            if (string7.startsWith(wm.getDirectory() + "/")) {
                final List<String> list8 = (List<String>)Lists.newArrayList(FileResourcePack.SPLITTER.split((CharSequence)string7));
                if (list8.size() <= 1) {
                    continue;
                }
                final String string8 = (String)list8.get(1);
                if (string8.equals(string8.toLowerCase(Locale.ROOT))) {
                    set5.add(string8);
                }
                else {
                    this.logWarning(string8);
                }
            }
        }
        return set5;
    }
    
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
    
    public void close() {
        if (this.zipFile != null) {
            IOUtils.closeQuietly((Closeable)this.zipFile);
            this.zipFile = null;
        }
    }
    
    public Collection<ResourceLocation> getResources(final PackType wm, final String string, final int integer, final Predicate<String> predicate) {
        ZipFile zipFile6;
        try {
            zipFile6 = this.getOrCreateZipFile();
        }
        catch (IOException iOException7) {
            return (Collection<ResourceLocation>)Collections.emptySet();
        }
        final Enumeration<? extends ZipEntry> enumeration7 = zipFile6.entries();
        final List<ResourceLocation> list8 = (List<ResourceLocation>)Lists.newArrayList();
        final String string2 = wm.getDirectory() + "/";
        while (enumeration7.hasMoreElements()) {
            final ZipEntry zipEntry10 = (ZipEntry)enumeration7.nextElement();
            if (!zipEntry10.isDirectory()) {
                if (!zipEntry10.getName().startsWith(string2)) {
                    continue;
                }
                final String string3 = zipEntry10.getName().substring(string2.length());
                if (string3.endsWith(".mcmeta")) {
                    continue;
                }
                final int integer2 = string3.indexOf(47);
                if (integer2 < 0) {
                    continue;
                }
                final String string4 = string3.substring(integer2 + 1);
                if (!string4.startsWith(string + "/")) {
                    continue;
                }
                final String[] arr14 = string4.substring(string.length() + 2).split("/");
                if (arr14.length < integer + 1 || !predicate.test(string4)) {
                    continue;
                }
                final String string5 = string3.substring(0, integer2);
                list8.add(new ResourceLocation(string5, string4));
            }
        }
        return (Collection<ResourceLocation>)list8;
    }
    
    static {
        SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
    }
}
