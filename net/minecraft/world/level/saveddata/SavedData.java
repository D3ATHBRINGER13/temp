package net.minecraft.world.level.saveddata;

import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import java.io.OutputStream;
import net.minecraft.nbt.NbtIo;
import java.io.FileOutputStream;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.Tag;
import java.io.File;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.Logger;

public abstract class SavedData {
    private static final Logger LOGGER;
    private final String id;
    private boolean dirty;
    
    public SavedData(final String string) {
        this.id = string;
    }
    
    public abstract void load(final CompoundTag id);
    
    public abstract CompoundTag save(final CompoundTag id);
    
    public void setDirty() {
        this.setDirty(true);
    }
    
    public void setDirty(final boolean boolean1) {
        this.dirty = boolean1;
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void save(final File file) {
        if (!this.isDirty()) {
            return;
        }
        final CompoundTag id3 = new CompoundTag();
        id3.put("data", (Tag)this.save(new CompoundTag()));
        id3.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        try (final FileOutputStream fileOutputStream4 = new FileOutputStream(file)) {
            NbtIo.writeCompressed(id3, (OutputStream)fileOutputStream4);
        }
        catch (IOException iOException4) {
            SavedData.LOGGER.error("Could not save data {}", this, iOException4);
        }
        this.setDirty(false);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
