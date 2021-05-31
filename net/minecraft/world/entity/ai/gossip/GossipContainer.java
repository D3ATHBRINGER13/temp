package net.minecraft.world.entity.ai.gossip;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.function.Predicate;
import java.util.Set;
import java.util.Arrays;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Stream;
import java.util.Iterator;
import com.google.common.collect.Maps;
import java.util.UUID;
import java.util.Map;

public class GossipContainer {
    private final Map<UUID, EntityGossips> gossips;
    
    public GossipContainer() {
        this.gossips = (Map<UUID, EntityGossips>)Maps.newHashMap();
    }
    
    public void decay() {
        final Iterator<EntityGossips> iterator2 = (Iterator<EntityGossips>)this.gossips.values().iterator();
        while (iterator2.hasNext()) {
            final EntityGossips a3 = (EntityGossips)iterator2.next();
            a3.decay();
            if (a3.isEmpty()) {
                iterator2.remove();
            }
        }
    }
    
    private Stream<GossipEntry> unpack() {
        return (Stream<GossipEntry>)this.gossips.entrySet().stream().flatMap(entry -> ((EntityGossips)entry.getValue()).unpack((UUID)entry.getKey()));
    }
    
    private Collection<GossipEntry> selectGossipsForTransfer(final Random random, final int integer) {
        final List<GossipEntry> list4 = (List<GossipEntry>)this.unpack().collect(Collectors.toList());
        if (list4.isEmpty()) {
            return (Collection<GossipEntry>)Collections.emptyList();
        }
        final int[] arr5 = new int[list4.size()];
        int integer2 = 0;
        for (int integer3 = 0; integer3 < list4.size(); ++integer3) {
            final GossipEntry b8 = (GossipEntry)list4.get(integer3);
            integer2 += Math.abs(b8.weightedValue());
            arr5[integer3] = integer2 - 1;
        }
        final Set<GossipEntry> set7 = (Set<GossipEntry>)Sets.newIdentityHashSet();
        for (int integer4 = 0; integer4 < integer; ++integer4) {
            final int integer5 = random.nextInt(integer2);
            final int integer6 = Arrays.binarySearch(arr5, integer5);
            set7.add(list4.get((integer6 < 0) ? (-integer6 - 1) : integer6));
        }
        return (Collection<GossipEntry>)set7;
    }
    
    private EntityGossips getOrCreate(final UUID uUID) {
        return (EntityGossips)this.gossips.computeIfAbsent(uUID, uUID -> new EntityGossips());
    }
    
    public void transferFrom(final GossipContainer apg, final Random random, final int integer) {
        final Collection<GossipEntry> collection5 = apg.selectGossipsForTransfer(random, integer);
        collection5.forEach(b -> {
            final int integer3 = b.value - b.type.decayPerTransfer;
            if (integer3 >= 2) {
                this.getOrCreate(b.target).entries.mergeInt(b.type, integer3, GossipContainer::mergeValuesForTransfer);
            }
        });
    }
    
    public int getReputation(final UUID uUID, final Predicate<GossipType> predicate) {
        final EntityGossips a4 = (EntityGossips)this.gossips.get(uUID);
        return (a4 != null) ? a4.weightedValue(predicate) : 0;
    }
    
    public void add(final UUID uUID, final GossipType aph, final int integer) {
        final EntityGossips a5 = this.getOrCreate(uUID);
        a5.entries.mergeInt(aph, integer, (integer2, integer3) -> this.mergeValuesForAddition(aph, integer2, integer3));
        a5.makeSureValueIsntTooLowOrTooHigh(aph);
        if (a5.isEmpty()) {
            this.gossips.remove(uUID);
        }
    }
    
    public <T> Dynamic<T> store(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createList(this.unpack().map(b -> b.store((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps)).map(Dynamic::getValue)));
    }
    
    public void update(final Dynamic<?> dynamic) {
        dynamic.asStream().map(GossipEntry::load).flatMap(Util::toStream).forEach(b -> this.getOrCreate(b.target).entries.put(b.type, b.value));
    }
    
    private static int mergeValuesForTransfer(final int integer1, final int integer2) {
        return Math.max(integer1, integer2);
    }
    
    private int mergeValuesForAddition(final GossipType aph, final int integer2, final int integer3) {
        final int integer4 = integer2 + integer3;
        return (integer4 > aph.max) ? Math.max(aph.max, integer2) : integer4;
    }
    
    static class GossipEntry {
        public final UUID target;
        public final GossipType type;
        public final int value;
        
        public GossipEntry(final UUID uUID, final GossipType aph, final int integer) {
            this.target = uUID;
            this.type = aph;
            this.value = integer;
        }
        
        public int weightedValue() {
            return this.value * this.type.weight;
        }
        
        public String toString() {
            return new StringBuilder().append("GossipEntry{target=").append(this.target).append(", type=").append(this.type).append(", value=").append(this.value).append('}').toString();
        }
        
        public <T> Dynamic<T> store(final DynamicOps<T> dynamicOps) {
            return Util.<T>writeUUID("Target", this.target, (com.mojang.datafixers.Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("Type"), dynamicOps.createString(this.type.id), dynamicOps.createString("Value"), dynamicOps.createInt(this.value)))));
        }
        
        public static Optional<GossipEntry> load(final Dynamic<?> dynamic) {
            return (Optional<GossipEntry>)dynamic.get("Type").asString().map(GossipType::byId).flatMap(aph -> Util.readUUID("Target", dynamic).flatMap(uUID -> dynamic.get("Value").asNumber().map(number -> new GossipEntry(uUID, aph, number.intValue()))));
        }
    }
    
    static class EntityGossips {
        private final Object2IntMap<GossipType> entries;
        
        private EntityGossips() {
            this.entries = (Object2IntMap<GossipType>)new Object2IntOpenHashMap();
        }
        
        public int weightedValue(final Predicate<GossipType> predicate) {
            return this.entries.object2IntEntrySet().stream().filter(entry -> predicate.test(entry.getKey())).mapToInt(entry -> entry.getIntValue() * ((GossipType)entry.getKey()).weight).sum();
        }
        
        public Stream<GossipEntry> unpack(final UUID uUID) {
            return (Stream<GossipEntry>)this.entries.object2IntEntrySet().stream().map(entry -> new GossipEntry(uUID, (GossipType)entry.getKey(), entry.getIntValue()));
        }
        
        public void decay() {
            final ObjectIterator<Object2IntMap.Entry<GossipType>> objectIterator2 = (ObjectIterator<Object2IntMap.Entry<GossipType>>)this.entries.object2IntEntrySet().iterator();
            while (objectIterator2.hasNext()) {
                final Object2IntMap.Entry<GossipType> entry3 = (Object2IntMap.Entry<GossipType>)objectIterator2.next();
                final int integer4 = entry3.getIntValue() - ((GossipType)entry3.getKey()).decayPerDay;
                if (integer4 < 2) {
                    objectIterator2.remove();
                }
                else {
                    entry3.setValue(integer4);
                }
            }
        }
        
        public boolean isEmpty() {
            return this.entries.isEmpty();
        }
        
        public void makeSureValueIsntTooLowOrTooHigh(final GossipType aph) {
            final int integer3 = this.entries.getInt(aph);
            if (integer3 > aph.max) {
                this.entries.put(aph, aph.max);
            }
            if (integer3 < 2) {
                this.remove(aph);
            }
        }
        
        public void remove(final GossipType aph) {
            this.entries.removeInt(aph);
        }
    }
}
