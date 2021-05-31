package net.minecraft.world.level.chunk;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Function;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.core.IdMapper;

public class HashMapPalette<T> implements Palette<T> {
    private final IdMapper<T> registry;
    private final CrudeIncrementalIntIdentityHashBiMap<T> values;
    private final PaletteResize<T> resizeHandler;
    private final Function<CompoundTag, T> reader;
    private final Function<T, CompoundTag> writer;
    private final int bits;
    
    public HashMapPalette(final IdMapper<T> ff, final int integer, final PaletteResize<T> bxz, final Function<CompoundTag, T> function4, final Function<T, CompoundTag> function5) {
        this.registry = ff;
        this.bits = integer;
        this.resizeHandler = bxz;
        this.reader = function4;
        this.writer = function5;
        this.values = new CrudeIncrementalIntIdentityHashBiMap<T>(1 << integer);
    }
    
    public int idFor(final T object) {
        int integer3 = this.values.getId(object);
        if (integer3 == -1) {
            integer3 = this.values.add(object);
            if (integer3 >= 1 << this.bits) {
                integer3 = this.resizeHandler.onResize(this.bits + 1, object);
            }
        }
        return integer3;
    }
    
    public boolean maybeHas(final T object) {
        return this.values.getId(object) != -1;
    }
    
    @Nullable
    public T valueFor(final int integer) {
        return this.values.byId(integer);
    }
    
    public void read(final FriendlyByteBuf je) {
        this.values.clear();
        for (int integer3 = je.readVarInt(), integer4 = 0; integer4 < integer3; ++integer4) {
            this.values.add(this.registry.byId(je.readVarInt()));
        }
    }
    
    public void write(final FriendlyByteBuf je) {
        final int integer3 = this.getSize();
        je.writeVarInt(integer3);
        for (int integer4 = 0; integer4 < integer3; ++integer4) {
            je.writeVarInt(this.registry.getId(this.values.byId(integer4)));
        }
    }
    
    public int getSerializedSize() {
        int integer2 = FriendlyByteBuf.getVarIntSize(this.getSize());
        for (int integer3 = 0; integer3 < this.getSize(); ++integer3) {
            integer2 += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values.byId(integer3)));
        }
        return integer2;
    }
    
    public int getSize() {
        return this.values.size();
    }
    
    public void read(final ListTag ik) {
        this.values.clear();
        for (int integer3 = 0; integer3 < ik.size(); ++integer3) {
            this.values.add((T)this.reader.apply(ik.getCompound(integer3)));
        }
    }
    
    public void write(final ListTag ik) {
        for (int integer3 = 0; integer3 < this.getSize(); ++integer3) {
            ik.add(this.writer.apply(this.values.byId(integer3)));
        }
    }
}
