package net.minecraft.world.level.chunk.storage;

import java.io.IOException;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.DimensionDataStorage;
import java.util.function.Supplier;
import net.minecraft.world.level.dimension.DimensionType;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.structure.LegacyStructureDataHandler;
import com.mojang.datafixers.DataFixer;

public class ChunkStorage extends RegionFileStorage {
    protected final DataFixer fixerUpper;
    @Nullable
    private LegacyStructureDataHandler legacyStructureHandler;
    
    public ChunkStorage(final File file, final DataFixer dataFixer) {
        super(file);
        this.fixerUpper = dataFixer;
    }
    
    public CompoundTag upgradeChunkTag(final DimensionType byn, final Supplier<DimensionDataStorage> supplier, CompoundTag id) {
        final int integer5 = getVersion(id);
        final int integer6 = 1493;
        if (integer5 < 1493) {
            id = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, id, integer5, 1493);
            if (id.getCompound("Level").getBoolean("hasLegacyStructureData")) {
                if (this.legacyStructureHandler == null) {
                    this.legacyStructureHandler = LegacyStructureDataHandler.getLegacyStructureHandler(byn, (DimensionDataStorage)supplier.get());
                }
                id = this.legacyStructureHandler.updateFromLegacy(id);
            }
        }
        id = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, id, Math.max(1493, integer5));
        if (integer5 < SharedConstants.getCurrentVersion().getWorldVersion()) {
            id.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        }
        return id;
    }
    
    public static int getVersion(final CompoundTag id) {
        return id.contains("DataVersion", 99) ? id.getInt("DataVersion") : -1;
    }
    
    public void write(final ChunkPos bhd, final CompoundTag id) throws IOException {
        super.write(bhd, id);
        if (this.legacyStructureHandler != null) {
            this.legacyStructureHandler.removeIndex(bhd.toLong());
        }
    }
}
