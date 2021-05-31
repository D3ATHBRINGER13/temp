package net.minecraft.data.structures;

import org.apache.logging.log4j.LogManager;
import java.io.BufferedWriter;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.nbt.NbtIo;
import java.nio.file.OpenOption;
import java.io.IOException;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataGenerator;
import org.apache.logging.log4j.Logger;
import net.minecraft.data.DataProvider;

public class NbtToSnbt implements DataProvider {
    private static final Logger LOGGER;
    private final DataGenerator generator;
    
    public NbtToSnbt(final DataGenerator gk) {
        this.generator = gk;
    }
    
    public void run(final HashCache gm) throws IOException {
        final Path path3 = this.generator.getOutputFolder();
        for (final Path path4 : this.generator.getInputFolders()) {
            Files.walk(path4, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".nbt")).forEach(path3 -> this.convertStructure(path3, this.getName(path4, path3), path3));
        }
    }
    
    public String getName() {
        return "NBT to SNBT";
    }
    
    private String getName(final Path path1, final Path path2) {
        final String string4 = path1.relativize(path2).toString().replaceAll("\\\\", "/");
        return string4.substring(0, string4.length() - ".nbt".length());
    }
    
    private void convertStructure(final Path path1, final String string, final Path path3) {
        try {
            final CompoundTag id5 = NbtIo.readCompressed(Files.newInputStream(path1, new OpenOption[0]));
            final Component jo6 = id5.getPrettyDisplay("    ", 0);
            final String string2 = jo6.getString() + "\n";
            final Path path4 = path3.resolve(string + ".snbt");
            Files.createDirectories(path4.getParent(), new FileAttribute[0]);
            try (final BufferedWriter bufferedWriter9 = Files.newBufferedWriter(path4, new OpenOption[0])) {
                bufferedWriter9.write(string2);
            }
            NbtToSnbt.LOGGER.info("Converted {} from NBT to SNBT", string);
        }
        catch (IOException iOException5) {
            NbtToSnbt.LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", string, path1, iOException5);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
