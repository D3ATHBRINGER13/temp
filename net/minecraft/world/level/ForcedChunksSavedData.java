package net.minecraft.world.level;

import net.minecraft.nbt.CompoundTag;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.saveddata.SavedData;

public class ForcedChunksSavedData extends SavedData {
    private LongSet chunks;
    
    public ForcedChunksSavedData() {
        super("chunks");
        this.chunks = (LongSet)new LongOpenHashSet();
    }
    
    @Override
    public void load(final CompoundTag id) {
        this.chunks = (LongSet)new LongOpenHashSet(id.getLongArray("Forced"));
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        id.putLongArray("Forced", this.chunks.toLongArray());
        return id;
    }
    
    public LongSet getChunks() {
        return this.chunks;
    }
}
