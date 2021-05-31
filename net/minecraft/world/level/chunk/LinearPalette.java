package net.minecraft.world.level.chunk;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Function;
import net.minecraft.core.IdMapper;

public class LinearPalette<T> implements Palette<T> {
    private final IdMapper<T> registry;
    private final T[] values;
    private final PaletteResize<T> resizeHandler;
    private final Function<CompoundTag, T> reader;
    private final int bits;
    private int size;
    
    public LinearPalette(final IdMapper<T> ff, final int integer, final PaletteResize<T> bxz, final Function<CompoundTag, T> function) {
        this.registry = ff;
        this.values = (T[])new Object[1 << integer];
        this.bits = integer;
        this.resizeHandler = bxz;
        this.reader = function;
    }
    
    public int idFor(final T object) {
        for (int integer3 = 0; integer3 < this.size; ++integer3) {
            if (this.values[integer3] == object) {
                return integer3;
            }
        }
        int integer3 = this.size;
        if (integer3 < this.values.length) {
            this.values[integer3] = object;
            ++this.size;
            return integer3;
        }
        return this.resizeHandler.onResize(this.bits + 1, object);
    }
    
    public boolean maybeHas(final T object) {
        return ArrayUtils.contains((Object[])this.values, object);
    }
    
    @Nullable
    public T valueFor(final int integer) {
        if (integer >= 0 && integer < this.size) {
            return this.values[integer];
        }
        return null;
    }
    
    public void read(final FriendlyByteBuf je) {
        this.size = je.readVarInt();
        for (int integer3 = 0; integer3 < this.size; ++integer3) {
            this.values[integer3] = this.registry.byId(je.readVarInt());
        }
    }
    
    public void write(final FriendlyByteBuf je) {
        je.writeVarInt(this.size);
        for (int integer3 = 0; integer3 < this.size; ++integer3) {
            je.writeVarInt(this.registry.getId(this.values[integer3]));
        }
    }
    
    public int getSerializedSize() {
        int integer2 = FriendlyByteBuf.getVarIntSize(this.getSize());
        for (int integer3 = 0; integer3 < this.getSize(); ++integer3) {
            integer2 += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values[integer3]));
        }
        return integer2;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void read(final ListTag ik) {
        for (int integer3 = 0; integer3 < ik.size(); ++integer3) {
            this.values[integer3] = (T)this.reader.apply(ik.getCompound(integer3));
        }
        this.size = ik.size();
    }
}
