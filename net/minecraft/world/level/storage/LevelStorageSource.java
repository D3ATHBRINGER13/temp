package net.minecraft.world.level.storage;

import java.time.temporal.TemporalField;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.format.DateTimeFormatterBuilder;
import org.apache.logging.log4j.LogManager;
import java.nio.file.FileVisitor;
import java.util.zip.ZipEntry;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.Paths;
import java.util.zip.ZipOutputStream;
import java.io.BufferedOutputStream;
import java.nio.file.OpenOption;
import net.minecraft.FileUtil;
import java.time.LocalDateTime;
import java.io.OutputStream;
import java.io.FileOutputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import java.io.InputStream;
import net.minecraft.nbt.NbtIo;
import java.io.FileInputStream;
import net.minecraft.util.ProgressListener;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import java.io.File;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.List;
import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import com.mojang.datafixers.DataFixer;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.Logger;

public class LevelStorageSource {
    private static final Logger LOGGER;
    private static final DateTimeFormatter FORMATTER;
    private final Path baseDir;
    private final Path backupDir;
    private final DataFixer fixerUpper;
    
    public LevelStorageSource(final Path path1, final Path path2, final DataFixer dataFixer) {
        this.fixerUpper = dataFixer;
        try {
            Files.createDirectories(Files.exists(path1, new LinkOption[0]) ? path1.toRealPath(new LinkOption[0]) : path1, new FileAttribute[0]);
        }
        catch (IOException iOException5) {
            throw new RuntimeException((Throwable)iOException5);
        }
        this.baseDir = path1;
        this.backupDir = path2;
    }
    
    public String getName() {
        return "Anvil";
    }
    
    public List<LevelSummary> getLevelList() throws LevelStorageException {
        if (!Files.isDirectory(this.baseDir, new LinkOption[0])) {
            throw new LevelStorageException(new TranslatableComponent("selectWorld.load_folder_access", new Object[0]).getString());
        }
        final List<LevelSummary> list2 = (List<LevelSummary>)Lists.newArrayList();
        final File[] listFiles;
        final File[] arr3 = listFiles = this.baseDir.toFile().listFiles();
        for (final File file7 : listFiles) {
            if (file7.isDirectory()) {
                final String string8 = file7.getName();
                final LevelData com9 = this.getDataTagFor(string8);
                if (com9 != null && (com9.getVersion() == 19132 || com9.getVersion() == 19133)) {
                    final boolean boolean10 = com9.getVersion() != this.getStorageVersion();
                    String string9 = com9.getLevelName();
                    if (StringUtils.isEmpty((CharSequence)string9)) {
                        string9 = string8;
                    }
                    final long long12 = 0L;
                    list2.add(new LevelSummary(com9, string8, string9, 0L, boolean10));
                }
            }
        }
        return list2;
    }
    
    private int getStorageVersion() {
        return 19133;
    }
    
    public LevelStorage selectLevel(final String string, @Nullable final MinecraftServer minecraftServer) {
        return selectLevel(this.baseDir, this.fixerUpper, string, minecraftServer);
    }
    
    protected static LevelStorage selectLevel(final Path path, final DataFixer dataFixer, final String string, @Nullable final MinecraftServer minecraftServer) {
        return new LevelStorage(path.toFile(), string, minecraftServer, dataFixer);
    }
    
    public boolean requiresConversion(final String string) {
        final LevelData com3 = this.getDataTagFor(string);
        return com3 != null && com3.getVersion() != this.getStorageVersion();
    }
    
    public boolean convertLevel(final String string, final ProgressListener zz) {
        return McRegionUpgrader.convertLevel(this.baseDir, this.fixerUpper, string, zz);
    }
    
    @Nullable
    public LevelData getDataTagFor(final String string) {
        return getDataTagFor(this.baseDir, this.fixerUpper, string);
    }
    
    @Nullable
    protected static LevelData getDataTagFor(final Path path, final DataFixer dataFixer, final String string) {
        final File file4 = new File(path.toFile(), string);
        if (!file4.exists()) {
            return null;
        }
        File file5 = new File(file4, "level.dat");
        if (file5.exists()) {
            final LevelData com6 = getLevelData(file5, dataFixer);
            if (com6 != null) {
                return com6;
            }
        }
        file5 = new File(file4, "level.dat_old");
        if (file5.exists()) {
            return getLevelData(file5, dataFixer);
        }
        return null;
    }
    
    @Nullable
    public static LevelData getLevelData(final File file, final DataFixer dataFixer) {
        try {
            final CompoundTag id3 = NbtIo.readCompressed((InputStream)new FileInputStream(file));
            final CompoundTag id4 = id3.getCompound("Data");
            final CompoundTag id5 = id4.contains("Player", 10) ? id4.getCompound("Player") : null;
            id4.remove("Player");
            final int integer6 = id4.contains("DataVersion", 99) ? id4.getInt("DataVersion") : -1;
            return new LevelData(NbtUtils.update(dataFixer, DataFixTypes.LEVEL, id4, integer6), dataFixer, integer6, id5);
        }
        catch (Exception exception3) {
            LevelStorageSource.LOGGER.error("Exception reading {}", file, exception3);
            return null;
        }
    }
    
    public void renameLevel(final String string1, final String string2) {
        final File file4 = new File(this.baseDir.toFile(), string1);
        if (!file4.exists()) {
            return;
        }
        final File file5 = new File(file4, "level.dat");
        if (file5.exists()) {
            try {
                final CompoundTag id6 = NbtIo.readCompressed((InputStream)new FileInputStream(file5));
                final CompoundTag id7 = id6.getCompound("Data");
                id7.putString("LevelName", string2);
                NbtIo.writeCompressed(id6, (OutputStream)new FileOutputStream(file5));
            }
            catch (Exception exception6) {
                exception6.printStackTrace();
            }
        }
    }
    
    public boolean isNewLevelIdAcceptable(final String string) {
        try {
            final Path path3 = this.baseDir.resolve(string);
            Files.createDirectory(path3, new FileAttribute[0]);
            Files.deleteIfExists(path3);
            return true;
        }
        catch (IOException iOException3) {
            return false;
        }
    }
    
    public boolean deleteLevel(final String string) {
        final File file3 = new File(this.baseDir.toFile(), string);
        if (!file3.exists()) {
            return true;
        }
        LevelStorageSource.LOGGER.info("Deleting level {}", string);
        for (int integer4 = 1; integer4 <= 5; ++integer4) {
            LevelStorageSource.LOGGER.info("Attempt {}...", integer4);
            if (deleteRecursive(file3.listFiles())) {
                break;
            }
            LevelStorageSource.LOGGER.warn("Unsuccessful in deleting contents.");
            if (integer4 < 5) {
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException ex) {}
            }
        }
        return file3.delete();
    }
    
    private static boolean deleteRecursive(final File[] arr) {
        for (final File file5 : arr) {
            LevelStorageSource.LOGGER.debug("Deleting {}", file5);
            if (file5.isDirectory() && !deleteRecursive(file5.listFiles())) {
                LevelStorageSource.LOGGER.warn("Couldn't delete directory {}", file5);
                return false;
            }
            if (!file5.delete()) {
                LevelStorageSource.LOGGER.warn("Couldn't delete file {}", file5);
                return false;
            }
        }
        return true;
    }
    
    public boolean levelExists(final String string) {
        return Files.isDirectory(this.baseDir.resolve(string), new LinkOption[0]);
    }
    
    public Path getBaseDir() {
        return this.baseDir;
    }
    
    public File getFile(final String string1, final String string2) {
        return this.baseDir.resolve(string1).resolve(string2).toFile();
    }
    
    private Path getLevelPath(final String string) {
        return this.baseDir.resolve(string);
    }
    
    public Path getBackupPath() {
        return this.backupDir;
    }
    
    public long makeWorldBackup(final String string) throws IOException {
        final Path path3 = this.getLevelPath(string);
        final String string2 = LocalDateTime.now().format(LevelStorageSource.FORMATTER) + "_" + string;
        final Path path4 = this.getBackupPath();
        try {
            Files.createDirectories(Files.exists(path4, new LinkOption[0]) ? path4.toRealPath(new LinkOption[0]) : path4, new FileAttribute[0]);
        }
        catch (IOException iOException6) {
            throw new RuntimeException((Throwable)iOException6);
        }
        final Path path5 = path4.resolve(FileUtil.findAvailableName(path4, string2, ".zip"));
        try (final ZipOutputStream zipOutputStream7 = new ZipOutputStream((OutputStream)new BufferedOutputStream(Files.newOutputStream(path5, new OpenOption[0])))) {
            final Path path6 = Paths.get(string, new String[0]);
            Files.walkFileTree(path3, (FileVisitor)new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(final Path path, final BasicFileAttributes basicFileAttributes) throws IOException {
                    final String string4 = path6.resolve(path3.relativize(path)).toString().replace('\\', '/');
                    final ZipEntry zipEntry5 = new ZipEntry(string4);
                    zipOutputStream7.putNextEntry(zipEntry5);
                    com.google.common.io.Files.asByteSource(path.toFile()).copyTo((OutputStream)zipOutputStream7);
                    zipOutputStream7.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return Files.size(path5);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        FORMATTER = new DateTimeFormatterBuilder().appendValue((TemporalField)ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue((TemporalField)ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue((TemporalField)ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue((TemporalField)ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue((TemporalField)ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue((TemporalField)ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    }
}
