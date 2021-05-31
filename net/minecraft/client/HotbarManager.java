package net.minecraft.client;

import org.apache.logging.log4j.LogManager;
import net.minecraft.nbt.Tag;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.nbt.NbtIo;
import net.minecraft.client.player.inventory.Hotbar;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import org.apache.logging.log4j.Logger;

public class HotbarManager {
    private static final Logger LOGGER;
    private final File optionsFile;
    private final DataFixer fixerUpper;
    private final Hotbar[] hotbars;
    private boolean loaded;
    
    public HotbarManager(final File file, final DataFixer dataFixer) {
        this.hotbars = new Hotbar[9];
        this.optionsFile = new File(file, "hotbar.nbt");
        this.fixerUpper = dataFixer;
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.hotbars[integer4] = new Hotbar();
        }
    }
    
    private void load() {
        try {
            CompoundTag id2 = NbtIo.read(this.optionsFile);
            if (id2 == null) {
                return;
            }
            if (!id2.contains("DataVersion", 99)) {
                id2.putInt("DataVersion", 1343);
            }
            id2 = NbtUtils.update(this.fixerUpper, DataFixTypes.HOTBAR, id2, id2.getInt("DataVersion"));
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                this.hotbars[integer3].fromTag(id2.getList(String.valueOf(integer3), 10));
            }
        }
        catch (Exception exception2) {
            HotbarManager.LOGGER.error("Failed to load creative mode options", (Throwable)exception2);
        }
    }
    
    public void save() {
        try {
            final CompoundTag id2 = new CompoundTag();
            id2.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                id2.put(String.valueOf(integer3), this.get(integer3).createTag());
            }
            NbtIo.write(id2, this.optionsFile);
        }
        catch (Exception exception2) {
            HotbarManager.LOGGER.error("Failed to save creative mode options", (Throwable)exception2);
        }
    }
    
    public Hotbar get(final int integer) {
        if (!this.loaded) {
            this.load();
            this.loaded = true;
        }
        return this.hotbars[integer];
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
