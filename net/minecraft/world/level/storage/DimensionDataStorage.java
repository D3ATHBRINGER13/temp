package net.minecraft.world.level.storage;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import java.io.DataInputStream;
import net.minecraft.nbt.NbtIo;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.FileInputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.SharedConstants;
import javax.annotation.Nullable;
import java.util.function.Supplier;
import com.google.common.collect.Maps;
import java.io.File;
import com.mojang.datafixers.DataFixer;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class DimensionDataStorage {
    private static final Logger LOGGER;
    private final Map<String, SavedData> cache;
    private final DataFixer fixerUpper;
    private final File dataFolder;
    
    public DimensionDataStorage(final File file, final DataFixer dataFixer) {
        this.cache = (Map<String, SavedData>)Maps.newHashMap();
        this.fixerUpper = dataFixer;
        this.dataFolder = file;
    }
    
    private File getDataFile(final String string) {
        return new File(this.dataFolder, string + ".dat");
    }
    
    public <T extends SavedData> T computeIfAbsent(final Supplier<T> supplier, final String string) {
        final T coc4 = (T)this.<SavedData>get((java.util.function.Supplier<SavedData>)supplier, string);
        if (coc4 != null) {
            return coc4;
        }
        final T coc5 = (T)supplier.get();
        this.set(coc5);
        return coc5;
    }
    
    @Nullable
    public <T extends SavedData> T get(final Supplier<T> supplier, final String string) {
        SavedData coc4 = (SavedData)this.cache.get(string);
        if (coc4 == null && !this.cache.containsKey(string)) {
            coc4 = this.<SavedData>readSavedData((java.util.function.Supplier<SavedData>)supplier, string);
            this.cache.put(string, coc4);
        }
        return (T)coc4;
    }
    
    @Nullable
    private <T extends SavedData> T readSavedData(final Supplier<T> supplier, final String string) {
        try {
            final File file4 = this.getDataFile(string);
            if (file4.exists()) {
                final T coc5 = (T)supplier.get();
                final CompoundTag id6 = this.readTagFromDisk(string, SharedConstants.getCurrentVersion().getWorldVersion());
                coc5.load(id6.getCompound("data"));
                return coc5;
            }
        }
        catch (Exception exception4) {
            DimensionDataStorage.LOGGER.error("Error loading saved data: {}", string, exception4);
        }
        return null;
    }
    
    public void set(final SavedData coc) {
        this.cache.put(coc.getId(), coc);
    }
    
    public CompoundTag readTagFromDisk(final String string, final int integer) throws IOException {
        final File file4 = this.getDataFile(string);
        try (final PushbackInputStream pushbackInputStream5 = new PushbackInputStream((InputStream)new FileInputStream(file4), 2)) {
            CompoundTag id7;
            if (this.isGzip(pushbackInputStream5)) {
                id7 = NbtIo.readCompressed((InputStream)pushbackInputStream5);
            }
            else {
                try (final DataInputStream dataInputStream8 = new DataInputStream((InputStream)pushbackInputStream5)) {
                    id7 = NbtIo.read(dataInputStream8);
                }
            }
            final int integer2 = id7.contains("DataVersion", 99) ? id7.getInt("DataVersion") : 1343;
            return NbtUtils.update(this.fixerUpper, DataFixTypes.SAVED_DATA, id7, integer2, integer);
        }
    }
    
    private boolean isGzip(final PushbackInputStream pushbackInputStream) throws IOException {
        final byte[] arr3 = new byte[2];
        boolean boolean4 = false;
        final int integer5 = pushbackInputStream.read(arr3, 0, 2);
        if (integer5 == 2) {
            final int integer6 = (arr3[1] & 0xFF) << 8 | (arr3[0] & 0xFF);
            if (integer6 == 35615) {
                boolean4 = true;
            }
        }
        if (integer5 != 0) {
            pushbackInputStream.unread(arr3, 0, integer5);
        }
        return boolean4;
    }
    
    public void save() {
        for (final SavedData coc3 : this.cache.values()) {
            if (coc3 != null) {
                coc3.save(this.getDataFile(coc3.getId()));
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
