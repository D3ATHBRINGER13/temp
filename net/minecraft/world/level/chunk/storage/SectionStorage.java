package net.minecraft.world.level.chunk.storage;

import org.apache.logging.log4j.LogManager;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import com.google.common.collect.Maps;
import net.minecraft.nbt.Tag;
import com.mojang.datafixers.OptionalDynamic;
import net.minecraft.SharedConstants;
import java.io.IOException;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.SectionPos;
import java.util.function.BooleanSupplier;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.io.File;
import net.minecraft.util.datafix.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.util.function.Function;
import com.mojang.datafixers.Dynamic;
import java.util.function.BiFunction;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.Optional;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.Serializable;

public class SectionStorage<R extends Serializable> extends RegionFileStorage {
    private static final Logger LOGGER;
    private final Long2ObjectMap<Optional<R>> storage;
    private final LongLinkedOpenHashSet dirty;
    private final BiFunction<Runnable, Dynamic<?>, R> deserializer;
    private final Function<Runnable, R> factory;
    private final DataFixer fixerUpper;
    private final DataFixTypes type;
    
    public SectionStorage(final File file, final BiFunction<Runnable, Dynamic<?>, R> biFunction, final Function<Runnable, R> function, final DataFixer dataFixer, final DataFixTypes aaj) {
        super(file);
        this.storage = (Long2ObjectMap<Optional<R>>)new Long2ObjectOpenHashMap();
        this.dirty = new LongLinkedOpenHashSet();
        this.deserializer = biFunction;
        this.factory = function;
        this.fixerUpper = dataFixer;
        this.type = aaj;
    }
    
    protected void tick(final BooleanSupplier booleanSupplier) {
        while (!this.dirty.isEmpty() && booleanSupplier.getAsBoolean()) {
            final ChunkPos bhd3 = SectionPos.of(this.dirty.firstLong()).chunk();
            this.writeColumn(bhd3);
        }
    }
    
    @Nullable
    protected Optional<R> get(final long long1) {
        return (Optional<R>)this.storage.get(long1);
    }
    
    protected Optional<R> getOrLoad(final long long1) {
        final SectionPos fp4 = SectionPos.of(long1);
        if (this.outsideStoredRange(fp4)) {
            return (Optional<R>)Optional.empty();
        }
        Optional<R> optional5 = this.get(long1);
        if (optional5 != null) {
            return optional5;
        }
        this.readColumn(fp4.chunk());
        optional5 = this.get(long1);
        if (optional5 == null) {
            throw new IllegalStateException();
        }
        return optional5;
    }
    
    protected boolean outsideStoredRange(final SectionPos fp) {
        return Level.isOutsideBuildHeight(SectionPos.sectionToBlockCoord(fp.y()));
    }
    
    protected R getOrCreate(final long long1) {
        final Optional<R> optional4 = this.getOrLoad(long1);
        if (optional4.isPresent()) {
            return (R)optional4.get();
        }
        final R aab5 = (R)this.factory.apply((() -> this.setDirty(long1)));
        this.storage.put(long1, Optional.of((Object)aab5));
        return aab5;
    }
    
    private void readColumn(final ChunkPos bhd) {
        this.<CompoundTag>readColumn(bhd, (com.mojang.datafixers.types.DynamicOps<CompoundTag>)NbtOps.INSTANCE, this.tryRead(bhd));
    }
    
    @Nullable
    private CompoundTag tryRead(final ChunkPos bhd) {
        try {
            return this.read(bhd);
        }
        catch (IOException iOException3) {
            SectionStorage.LOGGER.error("Error reading chunk {} data from disk", bhd, iOException3);
            return null;
        }
    }
    
    private <T> void readColumn(final ChunkPos bhd, final DynamicOps<T> dynamicOps, @Nullable final T object) {
        if (object == null) {
            for (int integer5 = 0; integer5 < 16; ++integer5) {
                this.storage.put(SectionPos.of(bhd, integer5).asLong(), Optional.empty());
            }
        }
        else {
            final Dynamic<T> dynamic5 = (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, object);
            final int integer6 = getVersion(dynamic5);
            final int integer7 = SharedConstants.getCurrentVersion().getWorldVersion();
            final boolean boolean8 = integer6 != integer7;
            final Dynamic<T> dynamic6 = (Dynamic<T>)this.fixerUpper.update(this.type.getType(), (Dynamic)dynamic5, integer6, integer7);
            final OptionalDynamic<T> optionalDynamic10 = (OptionalDynamic<T>)dynamic6.get("Sections");
            for (int integer8 = 0; integer8 < 16; ++integer8) {
                final long long12 = SectionPos.of(bhd, integer8).asLong();
                final Optional<R> optional14 = (Optional<R>)optionalDynamic10.get(Integer.toString(integer8)).get().map(dynamic -> (Serializable)this.deserializer.apply((() -> this.setDirty(long12)), dynamic));
                this.storage.put(long12, optional14);
                optional14.ifPresent(aab -> {
                    this.onSectionLoad(long12);
                    if (boolean8) {
                        this.setDirty(long12);
                    }
                });
            }
        }
    }
    
    private void writeColumn(final ChunkPos bhd) {
        final Dynamic<Tag> dynamic3 = this.<Tag>writeColumn(bhd, (com.mojang.datafixers.types.DynamicOps<Tag>)NbtOps.INSTANCE);
        final Tag iu4 = (Tag)dynamic3.getValue();
        if (iu4 instanceof CompoundTag) {
            try {
                this.write(bhd, (CompoundTag)iu4);
            }
            catch (IOException iOException5) {
                SectionStorage.LOGGER.error("Error writing data to disk", (Throwable)iOException5);
            }
        }
        else {
            SectionStorage.LOGGER.error("Expected compound tag, got {}", iu4);
        }
    }
    
    private <T> Dynamic<T> writeColumn(final ChunkPos bhd, final DynamicOps<T> dynamicOps) {
        final Map<T, T> map4 = (Map<T, T>)Maps.newHashMap();
        for (int integer5 = 0; integer5 < 16; ++integer5) {
            final long long6 = SectionPos.of(bhd, integer5).asLong();
            this.dirty.remove(long6);
            final Optional<R> optional8 = (Optional<R>)this.storage.get(long6);
            if (optional8 != null) {
                if (optional8.isPresent()) {
                    map4.put(dynamicOps.createString(Integer.toString(integer5)), ((Serializable)optional8.get()).<T>serialize(dynamicOps));
                }
            }
        }
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("Sections"), dynamicOps.createMap((Map)map4), dynamicOps.createString("DataVersion"), dynamicOps.createInt(SharedConstants.getCurrentVersion().getWorldVersion()))));
    }
    
    protected void onSectionLoad(final long long1) {
    }
    
    protected void setDirty(final long long1) {
        final Optional<R> optional4 = (Optional<R>)this.storage.get(long1);
        if (optional4 == null || !optional4.isPresent()) {
            SectionStorage.LOGGER.warn("No data for position: {}", SectionPos.of(long1));
            return;
        }
        this.dirty.add(long1);
    }
    
    private static int getVersion(final Dynamic<?> dynamic) {
        return ((Number)dynamic.get("DataVersion").asNumber().orElse(1945)).intValue();
    }
    
    public void flush(final ChunkPos bhd) {
        if (!this.dirty.isEmpty()) {
            for (int integer3 = 0; integer3 < 16; ++integer3) {
                final long long4 = SectionPos.of(bhd, integer3).asLong();
                if (this.dirty.contains(long4)) {
                    this.writeColumn(bhd);
                    return;
                }
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
