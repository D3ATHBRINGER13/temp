package net.minecraft.data.structures;

import org.apache.logging.log4j.LogManager;
import java.io.BufferedReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.LinkOption;
import java.util.Objects;
import java.io.OutputStream;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import net.minecraft.data.HashCache;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.data.DataGenerator;
import org.apache.logging.log4j.Logger;
import net.minecraft.data.DataProvider;

public class SnbtToNbt implements DataProvider {
    private static final Logger LOGGER;
    private final DataGenerator generator;
    private final List<Filter> filters;
    
    public SnbtToNbt(final DataGenerator gk) {
        this.filters = (List<Filter>)Lists.newArrayList();
        this.generator = gk;
    }
    
    public SnbtToNbt addFilter(final Filter a) {
        this.filters.add(a);
        return this;
    }
    
    private CompoundTag applyFilters(final String string, final CompoundTag id) {
        CompoundTag id2 = id;
        for (final Filter a6 : this.filters) {
            id2 = a6.apply(string, id2);
        }
        return id2;
    }
    
    public void run(final HashCache gm) throws IOException {
        final Path path3 = this.generator.getOutputFolder();
        for (final Path path4 : this.generator.getInputFolders()) {
            Files.walk(path4, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".snbt")).forEach(path4 -> this.convertStructure(gm, path4, this.getName(path4, path4), path3));
        }
    }
    
    public String getName() {
        return "SNBT -> NBT";
    }
    
    private String getName(final Path path1, final Path path2) {
        final String string4 = path1.relativize(path2).toString().replaceAll("\\\\", "/");
        return string4.substring(0, string4.length() - ".snbt".length());
    }
    
    private void convertStructure(final HashCache gm, final Path path2, final String string, final Path path4) {
        try {
            final Path path5 = path4.resolve(string + ".nbt");
            try (final BufferedReader bufferedReader7 = Files.newBufferedReader(path2)) {
                final String string2 = IOUtils.toString((Reader)bufferedReader7);
                final ByteArrayOutputStream byteArrayOutputStream10 = new ByteArrayOutputStream();
                NbtIo.writeCompressed(this.applyFilters(string, TagParser.parseTag(string2)), (OutputStream)byteArrayOutputStream10);
                final String string3 = SnbtToNbt.SHA1.hashBytes(byteArrayOutputStream10.toByteArray()).toString();
                if (!Objects.equals(gm.getHash(path5), string3) || !Files.exists(path5, new LinkOption[0])) {
                    Files.createDirectories(path5.getParent(), new FileAttribute[0]);
                    try (final OutputStream outputStream12 = Files.newOutputStream(path5, new OpenOption[0])) {
                        outputStream12.write(byteArrayOutputStream10.toByteArray());
                    }
                }
                gm.putNew(path5, string3);
            }
        }
        catch (CommandSyntaxException commandSyntaxException6) {
            SnbtToNbt.LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", string, path2, commandSyntaxException6);
        }
        catch (IOException iOException6) {
            SnbtToNbt.LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", string, path2, iOException6);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    @FunctionalInterface
    public interface Filter {
        CompoundTag apply(final String string, final CompoundTag id);
    }
}
