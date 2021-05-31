package net.minecraft.world.entity.ai.village.poi;

import org.apache.logging.log4j.LogManager;
import com.google.common.collect.Sets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import net.minecraft.core.SectionPos;
import org.apache.logging.log4j.util.Supplier;
import net.minecraft.core.BlockPos;
import java.util.stream.Stream;
import java.util.function.Predicate;
import com.mojang.datafixers.Dynamic;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Set;
import java.util.Map;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.Serializable;

public class PoiSection implements Serializable {
    private static final Logger LOGGER;
    private final Short2ObjectMap<PoiRecord> records;
    private final Map<PoiType, Set<PoiRecord>> byType;
    private final Runnable setDirty;
    private boolean isValid;
    
    public PoiSection(final Runnable runnable) {
        this.records = (Short2ObjectMap<PoiRecord>)new Short2ObjectOpenHashMap();
        this.byType = (Map<PoiType, Set<PoiRecord>>)Maps.newHashMap();
        this.setDirty = runnable;
        this.isValid = true;
    }
    
    public <T> PoiSection(final Runnable runnable, final Dynamic<T> dynamic) {
        this.records = (Short2ObjectMap<PoiRecord>)new Short2ObjectOpenHashMap();
        this.byType = (Map<PoiType, Set<PoiRecord>>)Maps.newHashMap();
        this.setDirty = runnable;
        try {
            this.isValid = dynamic.get("Valid").asBoolean(false);
            dynamic.get("Records").asStream().forEach(dynamic -> this.add(new PoiRecord((Dynamic<T>)dynamic, runnable)));
        }
        catch (Exception exception4) {
            PoiSection.LOGGER.error("Failed to load POI chunk", (Throwable)exception4);
            this.clear();
            this.isValid = false;
        }
    }
    
    public Stream<PoiRecord> getRecords(final Predicate<PoiType> predicate, final PoiManager.Occupancy b) {
        return (Stream<PoiRecord>)this.byType.entrySet().stream().filter(entry -> predicate.test(entry.getKey())).flatMap(entry -> ((Set)entry.getValue()).stream()).filter((Predicate)b.getTest());
    }
    
    public void add(final BlockPos ew, final PoiType aqs) {
        if (this.add(new PoiRecord(ew, aqs, this.setDirty))) {
            PoiSection.LOGGER.debug("Added POI of type {} @ {}", new Supplier[] { () -> aqs, () -> ew });
            this.setDirty.run();
        }
    }
    
    private boolean add(final PoiRecord aqq) {
        final BlockPos ew3 = aqq.getPos();
        final PoiType aqs4 = aqq.getPoiType();
        final short short5 = SectionPos.sectionRelativePos(ew3);
        final PoiRecord aqq2 = (PoiRecord)this.records.get(short5);
        if (aqq2 == null) {
            this.records.put(short5, aqq);
            ((Set)this.byType.computeIfAbsent(aqs4, aqs -> Sets.newHashSet())).add(aqq);
            return true;
        }
        if (aqs4.equals(aqq2.getPoiType())) {
            return false;
        }
        throw new IllegalStateException(new StringBuilder().append("POI data mismatch: already registered at ").append(ew3).toString());
    }
    
    public void remove(final BlockPos ew) {
        final PoiRecord aqq3 = (PoiRecord)this.records.remove(SectionPos.sectionRelativePos(ew));
        if (aqq3 == null) {
            PoiSection.LOGGER.error(new StringBuilder().append("POI data mismatch: never registered at ").append(ew).toString());
            return;
        }
        ((Set)this.byType.get(aqq3.getPoiType())).remove(aqq3);
        PoiSection.LOGGER.debug("Removed POI of type {} @ {}", new Supplier[] { aqq3::getPoiType, aqq3::getPos });
        this.setDirty.run();
    }
    
    public boolean release(final BlockPos ew) {
        final PoiRecord aqq3 = (PoiRecord)this.records.get(SectionPos.sectionRelativePos(ew));
        if (aqq3 == null) {
            throw new IllegalStateException(new StringBuilder().append("POI never registered at ").append(ew).toString());
        }
        final boolean boolean4 = aqq3.releaseTicket();
        this.setDirty.run();
        return boolean4;
    }
    
    public boolean exists(final BlockPos ew, final Predicate<PoiType> predicate) {
        final short short4 = SectionPos.sectionRelativePos(ew);
        final PoiRecord aqq5 = (PoiRecord)this.records.get(short4);
        return aqq5 != null && predicate.test(aqq5.getPoiType());
    }
    
    public Optional<PoiType> getType(final BlockPos ew) {
        final short short3 = SectionPos.sectionRelativePos(ew);
        final PoiRecord aqq4 = (PoiRecord)this.records.get(short3);
        return (Optional<PoiType>)((aqq4 != null) ? Optional.of(aqq4.getPoiType()) : Optional.empty());
    }
    
    public <T> T serialize(final DynamicOps<T> dynamicOps) {
        final T object3 = (T)dynamicOps.createList(this.records.values().stream().map(aqq -> aqq.serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps)));
        return (T)dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("Records"), object3, dynamicOps.createString("Valid"), dynamicOps.createBoolean(this.isValid)));
    }
    
    public void refresh(final Consumer<BiConsumer<BlockPos, PoiType>> consumer) {
        if (!this.isValid) {
            final Short2ObjectMap<PoiRecord> short2ObjectMap3 = (Short2ObjectMap<PoiRecord>)new Short2ObjectOpenHashMap((Short2ObjectMap)this.records);
            this.clear();
            consumer.accept(((ew, aqs) -> {
                final short short5 = SectionPos.sectionRelativePos(ew);
                final PoiRecord aqq6 = (PoiRecord)short2ObjectMap3.computeIfAbsent(short5, integer -> new PoiRecord(ew, aqs, this.setDirty));
                this.add(aqq6);
            }));
            this.isValid = true;
            this.setDirty.run();
        }
    }
    
    private void clear() {
        this.records.clear();
        this.byType.clear();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
