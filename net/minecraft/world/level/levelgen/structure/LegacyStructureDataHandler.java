package net.minecraft.world.level.levelgen.structure;

import java.util.function.Consumer;
import net.minecraft.Util;
import java.util.HashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.nbt.ListTag;
import java.util.function.Supplier;
import java.io.IOException;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.nbt.Tag;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.util.Locale;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.ChunkPos;
import java.util.Iterator;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.DimensionDataStorage;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Map;

public class LegacyStructureDataHandler {
    private static final Map<String, String> CURRENT_TO_LEGACY_MAP;
    private static final Map<String, String> LEGACY_TO_CURRENT_MAP;
    private final boolean hasLegacyData;
    private final Map<String, Long2ObjectMap<CompoundTag>> dataMap;
    private final Map<String, StructureFeatureIndexSavedData> indexMap;
    private final List<String> legacyKeys;
    private final List<String> currentKeys;
    
    public LegacyStructureDataHandler(@Nullable final DimensionDataStorage col, final List<String> list2, final List<String> list3) {
        this.dataMap = (Map<String, Long2ObjectMap<CompoundTag>>)Maps.newHashMap();
        this.indexMap = (Map<String, StructureFeatureIndexSavedData>)Maps.newHashMap();
        this.legacyKeys = list2;
        this.currentKeys = list3;
        this.populateCaches(col);
        boolean boolean5 = false;
        for (final String string7 : this.currentKeys) {
            boolean5 |= (this.dataMap.get(string7) != null);
        }
        this.hasLegacyData = boolean5;
    }
    
    public void removeIndex(final long long1) {
        for (final String string5 : this.legacyKeys) {
            final StructureFeatureIndexSavedData ciu6 = (StructureFeatureIndexSavedData)this.indexMap.get(string5);
            if (ciu6 != null && ciu6.hasUnhandledIndex(long1)) {
                ciu6.removeIndex(long1);
                ciu6.setDirty();
            }
        }
    }
    
    public CompoundTag updateFromLegacy(CompoundTag id) {
        final CompoundTag id2 = id.getCompound("Level");
        final ChunkPos bhd4 = new ChunkPos(id2.getInt("xPos"), id2.getInt("zPos"));
        if (this.isUnhandledStructureStart(bhd4.x, bhd4.z)) {
            id = this.updateStructureStart(id, bhd4);
        }
        final CompoundTag id3 = id2.getCompound("Structures");
        final CompoundTag id4 = id3.getCompound("References");
        for (final String string8 : this.currentKeys) {
            final StructureFeature<?> ceu9 = Feature.STRUCTURES_REGISTRY.get(string8.toLowerCase(Locale.ROOT));
            if (!id4.contains(string8, 12)) {
                if (ceu9 == null) {
                    continue;
                }
                final int integer10 = ceu9.getLookupRange();
                final LongList longList11 = (LongList)new LongArrayList();
                for (int integer11 = bhd4.x - integer10; integer11 <= bhd4.x + integer10; ++integer11) {
                    for (int integer12 = bhd4.z - integer10; integer12 <= bhd4.z + integer10; ++integer12) {
                        if (this.hasLegacyStart(integer11, integer12, string8)) {
                            longList11.add(ChunkPos.asLong(integer11, integer12));
                        }
                    }
                }
                id4.putLongArray(string8, (List<Long>)longList11);
            }
        }
        id3.put("References", (Tag)id4);
        id2.put("Structures", (Tag)id3);
        id.put("Level", (Tag)id2);
        return id;
    }
    
    private boolean hasLegacyStart(final int integer1, final int integer2, final String string) {
        return this.hasLegacyData && (this.dataMap.get(string) != null && ((StructureFeatureIndexSavedData)this.indexMap.get(LegacyStructureDataHandler.CURRENT_TO_LEGACY_MAP.get(string))).hasStartIndex(ChunkPos.asLong(integer1, integer2)));
    }
    
    private boolean isUnhandledStructureStart(final int integer1, final int integer2) {
        if (!this.hasLegacyData) {
            return false;
        }
        for (final String string5 : this.currentKeys) {
            if (this.dataMap.get(string5) != null && ((StructureFeatureIndexSavedData)this.indexMap.get(LegacyStructureDataHandler.CURRENT_TO_LEGACY_MAP.get(string5))).hasUnhandledIndex(ChunkPos.asLong(integer1, integer2))) {
                return true;
            }
        }
        return false;
    }
    
    private CompoundTag updateStructureStart(final CompoundTag id, final ChunkPos bhd) {
        final CompoundTag id2 = id.getCompound("Level");
        final CompoundTag id3 = id2.getCompound("Structures");
        final CompoundTag id4 = id3.getCompound("Starts");
        for (final String string8 : this.currentKeys) {
            final Long2ObjectMap<CompoundTag> long2ObjectMap9 = (Long2ObjectMap<CompoundTag>)this.dataMap.get(string8);
            if (long2ObjectMap9 == null) {
                continue;
            }
            final long long10 = bhd.toLong();
            if (!((StructureFeatureIndexSavedData)this.indexMap.get(LegacyStructureDataHandler.CURRENT_TO_LEGACY_MAP.get(string8))).hasUnhandledIndex(long10)) {
                continue;
            }
            final CompoundTag id5 = (CompoundTag)long2ObjectMap9.get(long10);
            if (id5 == null) {
                continue;
            }
            id4.put(string8, id5);
        }
        id3.put("Starts", (Tag)id4);
        id2.put("Structures", (Tag)id3);
        id.put("Level", (Tag)id2);
        return id;
    }
    
    private void populateCaches(@Nullable final DimensionDataStorage col) {
        if (col == null) {
            return;
        }
        for (final String string4 : this.legacyKeys) {
            CompoundTag id5 = new CompoundTag();
            try {
                id5 = col.readTagFromDisk(string4, 1493).getCompound("data").getCompound("Features");
                if (id5.isEmpty()) {
                    continue;
                }
            }
            catch (IOException ex) {}
            for (final String string5 : id5.getAllKeys()) {
                final CompoundTag id6 = id5.getCompound(string5);
                final long long9 = ChunkPos.asLong(id6.getInt("ChunkX"), id6.getInt("ChunkZ"));
                final ListTag ik11 = id6.getList("Children", 10);
                if (!ik11.isEmpty()) {
                    final String string6 = ik11.getCompound(0).getString("id");
                    final String string7 = (String)LegacyStructureDataHandler.LEGACY_TO_CURRENT_MAP.get(string6);
                    if (string7 != null) {
                        id6.putString("id", string7);
                    }
                }
                final String string6 = id6.getString("id");
                ((Long2ObjectMap)this.dataMap.computeIfAbsent(string6, string -> new Long2ObjectOpenHashMap())).put(long9, id6);
            }
            final String string8 = string4 + "_index";
            final StructureFeatureIndexSavedData ciu7 = col.<StructureFeatureIndexSavedData>computeIfAbsent((java.util.function.Supplier<StructureFeatureIndexSavedData>)(() -> new StructureFeatureIndexSavedData(string8)), string8);
            if (ciu7.getAll().isEmpty()) {
                final StructureFeatureIndexSavedData ciu8 = new StructureFeatureIndexSavedData(string8);
                this.indexMap.put(string4, ciu8);
                for (final String string9 : id5.getAllKeys()) {
                    final CompoundTag id7 = id5.getCompound(string9);
                    ciu8.addIndex(ChunkPos.asLong(id7.getInt("ChunkX"), id7.getInt("ChunkZ")));
                }
                ciu8.setDirty();
            }
            else {
                this.indexMap.put(string4, ciu7);
            }
        }
    }
    
    public static LegacyStructureDataHandler getLegacyStructureHandler(final DimensionType byn, @Nullable final DimensionDataStorage col) {
        if (byn == DimensionType.OVERWORLD) {
            return new LegacyStructureDataHandler(col, (List<String>)ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), (List<String>)ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"));
        }
        if (byn == DimensionType.NETHER) {
            final List<String> list3 = (List<String>)ImmutableList.of("Fortress");
            return new LegacyStructureDataHandler(col, list3, list3);
        }
        if (byn == DimensionType.THE_END) {
            final List<String> list3 = (List<String>)ImmutableList.of("EndCity");
            return new LegacyStructureDataHandler(col, list3, list3);
        }
        throw new RuntimeException(String.format("Unknown dimension type : %s", new Object[] { byn }));
    }
    
    static {
        CURRENT_TO_LEGACY_MAP = Util.<Map>make((Map)Maps.newHashMap(), (java.util.function.Consumer<Map>)(hashMap -> {
            hashMap.put("Village", "Village");
            hashMap.put("Mineshaft", "Mineshaft");
            hashMap.put("Mansion", "Mansion");
            hashMap.put("Igloo", "Temple");
            hashMap.put("Desert_Pyramid", "Temple");
            hashMap.put("Jungle_Pyramid", "Temple");
            hashMap.put("Swamp_Hut", "Temple");
            hashMap.put("Stronghold", "Stronghold");
            hashMap.put("Monument", "Monument");
            hashMap.put("Fortress", "Fortress");
            hashMap.put("EndCity", "EndCity");
        }));
        LEGACY_TO_CURRENT_MAP = Util.<Map>make((Map)Maps.newHashMap(), (java.util.function.Consumer<Map>)(hashMap -> {
            hashMap.put("Iglu", "Igloo");
            hashMap.put("TeDP", "Desert_Pyramid");
            hashMap.put("TeJP", "Jungle_Pyramid");
            hashMap.put("TeSH", "Swamp_Hut");
        }));
    }
}
