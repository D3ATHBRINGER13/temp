package net.minecraft.world.level.chunk;

import java.util.Arrays;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReport;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.util.BitStorage;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Function;
import net.minecraft.core.IdMapper;

public class PalettedContainer<T> implements PaletteResize<T> {
    private final Palette<T> globalPalette;
    private final PaletteResize<T> dummyPaletteResize;
    private final IdMapper<T> registry;
    private final Function<CompoundTag, T> reader;
    private final Function<T, CompoundTag> writer;
    private final T defaultValue;
    protected BitStorage storage;
    private Palette<T> palette;
    private int bits;
    private final ReentrantLock lock;
    
    public void acquire() {
        if (this.lock.isLocked() && !this.lock.isHeldByCurrentThread()) {
            final String string2 = (String)Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map(thread -> thread.getName() + ": \n\tat " + (String)Arrays.stream((Object[])thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "))).collect(Collectors.joining("\n"));
            final CrashReport d3 = new CrashReport("Writing into PalettedContainer from multiple threads", (Throwable)new IllegalStateException());
            final CrashReportCategory e4 = d3.addCategory("Thread dumps");
            e4.setDetail("Thread dumps", string2);
            throw new ReportedException(d3);
        }
        this.lock.lock();
    }
    
    public void release() {
        this.lock.unlock();
    }
    
    public PalettedContainer(final Palette<T> bxy, final IdMapper<T> ff, final Function<CompoundTag, T> function3, final Function<T, CompoundTag> function4, final T object) {
        this.dummyPaletteResize = ((integer, object) -> 0);
        this.lock = new ReentrantLock();
        this.globalPalette = bxy;
        this.registry = ff;
        this.reader = function3;
        this.writer = function4;
        this.defaultValue = object;
        this.setBits(4);
    }
    
    private static int getIndex(final int integer1, final int integer2, final int integer3) {
        return integer2 << 8 | integer3 << 4 | integer1;
    }
    
    private void setBits(final int integer) {
        if (integer == this.bits) {
            return;
        }
        this.bits = integer;
        if (this.bits <= 4) {
            this.bits = 4;
            this.palette = new LinearPalette<T>(this.registry, this.bits, this, this.reader);
        }
        else if (this.bits < 9) {
            this.palette = new HashMapPalette<T>(this.registry, this.bits, this, this.reader, this.writer);
        }
        else {
            this.palette = this.globalPalette;
            this.bits = Mth.ceillog2(this.registry.size());
        }
        this.palette.idFor(this.defaultValue);
        this.storage = new BitStorage(this.bits, 4096);
    }
    
    public int onResize(final int integer, final T object) {
        this.acquire();
        final BitStorage zk4 = this.storage;
        final Palette<T> bxy5 = this.palette;
        this.setBits(integer);
        for (int integer2 = 0; integer2 < zk4.getSize(); ++integer2) {
            final T object2 = bxy5.valueFor(zk4.get(integer2));
            if (object2 != null) {
                this.set(integer2, object2);
            }
        }
        int integer2 = this.palette.idFor(object);
        this.release();
        return integer2;
    }
    
    public T getAndSet(final int integer1, final int integer2, final int integer3, final T object) {
        this.acquire();
        final T object2 = this.getAndSet(getIndex(integer1, integer2, integer3), object);
        this.release();
        return object2;
    }
    
    public T getAndSetUnchecked(final int integer1, final int integer2, final int integer3, final T object) {
        return this.getAndSet(getIndex(integer1, integer2, integer3), object);
    }
    
    protected T getAndSet(final int integer, final T object) {
        final int integer2 = this.palette.idFor(object);
        final int integer3 = this.storage.getAndSet(integer, integer2);
        final T object2 = this.palette.valueFor(integer3);
        return (object2 == null) ? this.defaultValue : object2;
    }
    
    protected void set(final int integer, final T object) {
        final int integer2 = this.palette.idFor(object);
        this.storage.set(integer, integer2);
    }
    
    public T get(final int integer1, final int integer2, final int integer3) {
        return this.get(getIndex(integer1, integer2, integer3));
    }
    
    protected T get(final int integer) {
        final T object3 = this.palette.valueFor(this.storage.get(integer));
        return (object3 == null) ? this.defaultValue : object3;
    }
    
    public void read(final FriendlyByteBuf je) {
        this.acquire();
        final int integer3 = je.readByte();
        if (this.bits != integer3) {
            this.setBits(integer3);
        }
        this.palette.read(je);
        je.readLongArray(this.storage.getRaw());
        this.release();
    }
    
    public void write(final FriendlyByteBuf je) {
        this.acquire();
        je.writeByte(this.bits);
        this.palette.write(je);
        je.writeLongArray(this.storage.getRaw());
        this.release();
    }
    
    public void read(final ListTag ik, final long[] arr) {
        this.acquire();
        final int integer4 = Math.max(4, Mth.ceillog2(ik.size()));
        if (integer4 != this.bits) {
            this.setBits(integer4);
        }
        this.palette.read(ik);
        final int integer5 = arr.length * 64 / 4096;
        if (this.palette == this.globalPalette) {
            final Palette<T> bxy6 = new HashMapPalette<T>(this.registry, integer4, this.dummyPaletteResize, this.reader, this.writer);
            bxy6.read(ik);
            final BitStorage zk7 = new BitStorage(integer4, 4096, arr);
            for (int integer6 = 0; integer6 < 4096; ++integer6) {
                this.storage.set(integer6, this.globalPalette.idFor(bxy6.valueFor(zk7.get(integer6))));
            }
        }
        else if (integer5 == this.bits) {
            System.arraycopy(arr, 0, this.storage.getRaw(), 0, arr.length);
        }
        else {
            final BitStorage zk8 = new BitStorage(integer5, 4096, arr);
            for (int integer7 = 0; integer7 < 4096; ++integer7) {
                this.storage.set(integer7, zk8.get(integer7));
            }
        }
        this.release();
    }
    
    public void write(final CompoundTag id, final String string2, final String string3) {
        this.acquire();
        final HashMapPalette<T> bxr5 = new HashMapPalette<T>(this.registry, this.bits, this.dummyPaletteResize, this.reader, this.writer);
        bxr5.idFor(this.defaultValue);
        final int[] arr6 = new int[4096];
        for (int integer7 = 0; integer7 < 4096; ++integer7) {
            arr6[integer7] = bxr5.idFor(this.get(integer7));
        }
        final ListTag ik7 = new ListTag();
        bxr5.write(ik7);
        id.put(string2, ik7);
        final int integer8 = Math.max(4, Mth.ceillog2(ik7.size()));
        final BitStorage zk9 = new BitStorage(integer8, 4096);
        for (int integer9 = 0; integer9 < arr6.length; ++integer9) {
            zk9.set(integer9, arr6[integer9]);
        }
        id.putLongArray(string3, zk9.getRaw());
        this.release();
    }
    
    public int getSerializedSize() {
        return 1 + this.palette.getSerializedSize() + FriendlyByteBuf.getVarIntSize(this.storage.getSize()) + this.storage.getRaw().length * 8;
    }
    
    public boolean maybeHas(final T object) {
        return this.palette.maybeHas(object);
    }
    
    public void count(final CountConsumer<T> a) {
        final Int2IntMap int2IntMap3 = (Int2IntMap)new Int2IntOpenHashMap();
        this.storage.getAll(integer -> int2IntMap3.put(integer, int2IntMap3.get(integer) + 1));
        int2IntMap3.int2IntEntrySet().forEach(entry -> a.accept(this.palette.valueFor(entry.getIntKey()), entry.getIntValue()));
    }
    
    @FunctionalInterface
    public interface CountConsumer<T> {
        void accept(final T object, final int integer);
    }
}
