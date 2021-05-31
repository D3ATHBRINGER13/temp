package net.minecraft.world.level.storage;

import org.apache.logging.log4j.LogManager;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelConflictException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;
import net.minecraft.nbt.NbtIo;
import java.io.FileOutputStream;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.Util;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import com.mojang.datafixers.DataFixer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import java.io.File;
import org.apache.logging.log4j.Logger;

public class LevelStorage implements PlayerIO {
    private static final Logger LOGGER;
    private final File worldDir;
    private final File playerDir;
    private final long sessionId;
    private final String levelId;
    private final StructureManager structureManager;
    protected final DataFixer fixerUpper;
    
    public LevelStorage(final File file, final String string, @Nullable final MinecraftServer minecraftServer, final DataFixer dataFixer) {
        this.sessionId = Util.getMillis();
        this.fixerUpper = dataFixer;
        (this.worldDir = new File(file, string)).mkdirs();
        this.playerDir = new File(this.worldDir, "playerdata");
        this.levelId = string;
        if (minecraftServer != null) {
            this.playerDir.mkdirs();
            this.structureManager = new StructureManager(minecraftServer, this.worldDir, dataFixer);
        }
        else {
            this.structureManager = null;
        }
        this.initiateSession();
    }
    
    public void saveLevelData(final LevelData com, @Nullable final CompoundTag id) {
        com.setVersion(19133);
        final CompoundTag id2 = com.createTag(id);
        final CompoundTag id3 = new CompoundTag();
        id3.put("Data", (Tag)id2);
        try {
            final File file6 = new File(this.worldDir, "level.dat_new");
            final File file7 = new File(this.worldDir, "level.dat_old");
            final File file8 = new File(this.worldDir, "level.dat");
            NbtIo.writeCompressed(id3, (OutputStream)new FileOutputStream(file6));
            if (file7.exists()) {
                file7.delete();
            }
            file8.renameTo(file7);
            if (file8.exists()) {
                file8.delete();
            }
            file6.renameTo(file8);
            if (file6.exists()) {
                file6.delete();
            }
        }
        catch (Exception exception6) {
            exception6.printStackTrace();
        }
    }
    
    private void initiateSession() {
        try {
            final File file2 = new File(this.worldDir, "session.lock");
            final DataOutputStream dataOutputStream3 = new DataOutputStream((OutputStream)new FileOutputStream(file2));
            try {
                dataOutputStream3.writeLong(this.sessionId);
            }
            finally {
                dataOutputStream3.close();
            }
        }
        catch (IOException iOException2) {
            iOException2.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }
    
    public File getFolder() {
        return this.worldDir;
    }
    
    public void checkSession() throws LevelConflictException {
        try {
            final File file2 = new File(this.worldDir, "session.lock");
            final DataInputStream dataInputStream3 = new DataInputStream((InputStream)new FileInputStream(file2));
            try {
                if (dataInputStream3.readLong() != this.sessionId) {
                    throw new LevelConflictException("The save is being accessed from another location, aborting");
                }
            }
            finally {
                dataInputStream3.close();
            }
        }
        catch (IOException iOException2) {
            throw new LevelConflictException("Failed to check session lock, aborting");
        }
    }
    
    @Nullable
    public LevelData prepareLevel() {
        File file2 = new File(this.worldDir, "level.dat");
        if (file2.exists()) {
            final LevelData com3 = LevelStorageSource.getLevelData(file2, this.fixerUpper);
            if (com3 != null) {
                return com3;
            }
        }
        file2 = new File(this.worldDir, "level.dat_old");
        if (file2.exists()) {
            return LevelStorageSource.getLevelData(file2, this.fixerUpper);
        }
        return null;
    }
    
    public void saveLevelData(final LevelData com) {
        this.saveLevelData(com, null);
    }
    
    public void save(final Player awg) {
        try {
            final CompoundTag id3 = awg.saveWithoutId(new CompoundTag());
            final File file4 = new File(this.playerDir, awg.getStringUUID() + ".dat.tmp");
            final File file5 = new File(this.playerDir, awg.getStringUUID() + ".dat");
            NbtIo.writeCompressed(id3, (OutputStream)new FileOutputStream(file4));
            if (file5.exists()) {
                file5.delete();
            }
            file4.renameTo(file5);
        }
        catch (Exception exception3) {
            LevelStorage.LOGGER.warn("Failed to save player data for {}", awg.getName().getString());
        }
    }
    
    @Nullable
    public CompoundTag load(final Player awg) {
        CompoundTag id3 = null;
        try {
            final File file4 = new File(this.playerDir, awg.getStringUUID() + ".dat");
            if (file4.exists() && file4.isFile()) {
                id3 = NbtIo.readCompressed((InputStream)new FileInputStream(file4));
            }
        }
        catch (Exception exception4) {
            LevelStorage.LOGGER.warn("Failed to load player data for {}", awg.getName().getString());
        }
        if (id3 != null) {
            final int integer4 = id3.contains("DataVersion", 3) ? id3.getInt("DataVersion") : -1;
            awg.load(NbtUtils.update(this.fixerUpper, DataFixTypes.PLAYER, id3, integer4));
        }
        return id3;
    }
    
    public String[] getSeenPlayers() {
        String[] arr2 = this.playerDir.list();
        if (arr2 == null) {
            arr2 = new String[0];
        }
        for (int integer3 = 0; integer3 < arr2.length; ++integer3) {
            if (arr2[integer3].endsWith(".dat")) {
                arr2[integer3] = arr2[integer3].substring(0, arr2[integer3].length() - 4);
            }
        }
        return arr2;
    }
    
    public StructureManager getStructureManager() {
        return this.structureManager;
    }
    
    public DataFixer getFixerUpper() {
        return this.fixerUpper;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
