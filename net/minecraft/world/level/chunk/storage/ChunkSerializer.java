package net.minecraft.world.level.chunk.storage;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.material.Fluids;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import net.minecraft.nbt.ShortTag;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIO;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.nbt.LongArrayTag;
import java.util.Map;
import java.util.Collection;
import net.minecraft.world.entity.Entity;
import java.util.Arrays;
import net.minecraft.nbt.Tag;
import net.minecraft.SharedConstants;
import java.util.Iterator;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import java.util.BitSet;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import java.util.Set;
import java.util.EnumSet;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.function.Consumer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import net.minecraft.world.level.ChunkTickList;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.Fluid;
import java.util.function.Predicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ProtoTickList;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import java.util.Objects;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
    private static final Logger LOGGER;
    
    public static ProtoChunk read(final ServerLevel vk, final StructureManager cjp, final PoiManager aqp, final ChunkPos bhd, final CompoundTag id) {
        final ChunkGenerator<?> bxi6 = vk.getChunkSource().getGenerator();
        final BiomeSource biq7 = bxi6.getBiomeSource();
        final CompoundTag id2 = id.getCompound("Level");
        final ChunkPos bhd2 = new ChunkPos(id2.getInt("xPos"), id2.getInt("zPos"));
        if (!Objects.equals(bhd, bhd2)) {
            ChunkSerializer.LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", bhd, bhd, bhd2);
        }
        final Biome[] arr10 = new Biome[256];
        final BlockPos.MutableBlockPos a11 = new BlockPos.MutableBlockPos();
        if (id2.contains("Biomes", 11)) {
            final int[] arr11 = id2.getIntArray("Biomes");
            for (int integer13 = 0; integer13 < arr11.length; ++integer13) {
                arr10[integer13] = Registry.BIOME.byId(arr11[integer13]);
                if (arr10[integer13] == null) {
                    arr10[integer13] = biq7.getBiome(a11.set((integer13 & 0xF) + bhd.getMinBlockX(), 0, (integer13 >> 4 & 0xF) + bhd.getMinBlockZ()));
                }
            }
        }
        else {
            for (int integer14 = 0; integer14 < arr10.length; ++integer14) {
                arr10[integer14] = biq7.getBiome(a11.set((integer14 & 0xF) + bhd.getMinBlockX(), 0, (integer14 >> 4 & 0xF) + bhd.getMinBlockZ()));
            }
        }
        final UpgradeData byd12 = id2.contains("UpgradeData", 10) ? new UpgradeData(id2.getCompound("UpgradeData")) : UpgradeData.EMPTY;
        final ProtoTickList<Block> byc13 = new ProtoTickList<Block>((java.util.function.Predicate<Block>)(bmv -> bmv == null || bmv.defaultBlockState().isAir()), bhd, id2.getList("ToBeTicked", 9));
        final ProtoTickList<Fluid> byc14 = new ProtoTickList<Fluid>((java.util.function.Predicate<Fluid>)(clj -> clj == null || clj == Fluids.EMPTY), bhd, id2.getList("LiquidsToBeTicked", 9));
        final boolean boolean15 = id2.getBoolean("isLightOn");
        final ListTag ik16 = id2.getList("Sections", 10);
        final int integer15 = 16;
        final LevelChunkSection[] arr12 = new LevelChunkSection[16];
        final boolean boolean16 = vk.getDimension().isHasSkyLight();
        final ChunkSource bxl20 = vk.getChunkSource();
        final LevelLightEngine clb21 = bxl20.getLightEngine();
        if (boolean15) {
            clb21.retainData(bhd, true);
        }
        for (int integer16 = 0; integer16 < ik16.size(); ++integer16) {
            final CompoundTag id3 = ik16.getCompound(integer16);
            final int integer17 = id3.getByte("Y");
            if (id3.contains("Palette", 9) && id3.contains("BlockStates", 12)) {
                final LevelChunkSection bxu25 = new LevelChunkSection(integer17 << 4);
                bxu25.getStates().read(id3.getList("Palette", 10), id3.getLongArray("BlockStates"));
                bxu25.recalcBlockCounts();
                if (!bxu25.isEmpty()) {
                    arr12[integer17] = bxu25;
                }
                aqp.checkConsistencyWithBlocks(bhd, bxu25);
            }
            if (boolean15) {
                if (id3.contains("BlockLight", 7)) {
                    clb21.queueSectionData(LightLayer.BLOCK, SectionPos.of(bhd, integer17), new DataLayer(id3.getByteArray("BlockLight")));
                }
                if (boolean16 && id3.contains("SkyLight", 7)) {
                    clb21.queueSectionData(LightLayer.SKY, SectionPos.of(bhd, integer17), new DataLayer(id3.getByteArray("SkyLight")));
                }
            }
        }
        final long long22 = id2.getLong("InhabitedTime");
        final ChunkStatus.ChunkType a12 = getChunkTypeFromTag(id);
        ChunkAccess bxh25;
        if (a12 == ChunkStatus.ChunkType.LEVELCHUNK) {
            TickList<Block> big26;
            if (id2.contains("TileTicks", 9)) {
                big26 = ChunkTickList.create(id2.getList("TileTicks", 10), (java.util.function.Function<Object, ResourceLocation>)Registry.BLOCK::getKey, (java.util.function.Function<ResourceLocation, Object>)Registry.BLOCK::get);
            }
            else {
                big26 = byc13;
            }
            TickList<Fluid> big27;
            if (id2.contains("LiquidTicks", 9)) {
                big27 = ChunkTickList.create(id2.getList("LiquidTicks", 10), (java.util.function.Function<Object, ResourceLocation>)Registry.FLUID::getKey, (java.util.function.Function<ResourceLocation, Object>)Registry.FLUID::get);
            }
            else {
                big27 = byc14;
            }
            bxh25 = new LevelChunk(vk.getLevel(), bhd, arr10, byd12, big26, big27, long22, arr12, (Consumer<LevelChunk>)(bxt -> postLoadChunk(id2, bxt)));
        }
        else {
            final ProtoChunk byb26 = (ProtoChunk)(bxh25 = new ProtoChunk(bhd, byd12, arr12, byc13, byc14));
            bxh25.setBiomes(arr10);
            bxh25.setInhabitedTime(long22);
            byb26.setStatus(ChunkStatus.byName(id2.getString("Status")));
            if (bxh25.getStatus().isOrAfter(ChunkStatus.FEATURES)) {
                byb26.setLightEngine(clb21);
            }
            if (!boolean15 && bxh25.getStatus().isOrAfter(ChunkStatus.LIGHT)) {
                for (final BlockPos ew28 : BlockPos.betweenClosed(bhd.getMinBlockX(), 0, bhd.getMinBlockZ(), bhd.getMaxBlockX(), 255, bhd.getMaxBlockZ())) {
                    if (bxh25.getBlockState(ew28).getLightEmission() != 0) {
                        byb26.addLight(ew28);
                    }
                }
            }
        }
        bxh25.setLightCorrect(boolean15);
        final CompoundTag id4 = id2.getCompound("Heightmaps");
        final EnumSet<Heightmap.Types> enumSet27 = (EnumSet<Heightmap.Types>)EnumSet.noneOf((Class)Heightmap.Types.class);
        for (final Heightmap.Types a13 : bxh25.getStatus().heightmapsAfter()) {
            final String string30 = a13.getSerializationKey();
            if (id4.contains(string30, 12)) {
                bxh25.setHeightmap(a13, id4.getLongArray(string30));
            }
            else {
                enumSet27.add(a13);
            }
        }
        Heightmap.primeHeightmaps(bxh25, (Set<Heightmap.Types>)enumSet27);
        final CompoundTag id5 = id2.getCompound("Structures");
        bxh25.setAllStarts(unpackStructureStart(bxi6, cjp, biq7, id5));
        bxh25.setAllReferences(unpackStructureReferences(id5));
        if (id2.getBoolean("shouldSave")) {
            bxh25.setUnsaved(true);
        }
        final ListTag ik17 = id2.getList("PostProcessing", 9);
        for (int integer18 = 0; integer18 < ik17.size(); ++integer18) {
            final ListTag ik18 = ik17.getList(integer18);
            for (int integer19 = 0; integer19 < ik18.size(); ++integer19) {
                bxh25.addPackedPostProcess(ik18.getShort(integer19), integer18);
            }
        }
        if (a12 == ChunkStatus.ChunkType.LEVELCHUNK) {
            return new ImposterProtoChunk((LevelChunk)bxh25);
        }
        final ProtoChunk byb27 = (ProtoChunk)bxh25;
        final ListTag ik18 = id2.getList("Entities", 10);
        for (int integer19 = 0; integer19 < ik18.size(); ++integer19) {
            byb27.addEntity(ik18.getCompound(integer19));
        }
        final ListTag ik19 = id2.getList("TileEntities", 10);
        for (int integer20 = 0; integer20 < ik19.size(); ++integer20) {
            final CompoundTag id6 = ik19.getCompound(integer20);
            bxh25.setBlockEntityNbt(id6);
        }
        final ListTag ik20 = id2.getList("Lights", 9);
        for (int integer21 = 0; integer21 < ik20.size(); ++integer21) {
            final ListTag ik21 = ik20.getList(integer21);
            for (int integer22 = 0; integer22 < ik21.size(); ++integer22) {
                byb27.addLight(ik21.getShort(integer22), integer21);
            }
        }
        final CompoundTag id6 = id2.getCompound("CarvingMasks");
        for (final String string31 : id6.getAllKeys()) {
            final GenerationStep.Carving a14 = GenerationStep.Carving.valueOf(string31);
            byb27.setCarvingMask(a14, BitSet.valueOf(id6.getByteArray(string31)));
        }
        return byb27;
    }
    
    public static CompoundTag write(final ServerLevel vk, final ChunkAccess bxh) {
        final ChunkPos bhd3 = bxh.getPos();
        final CompoundTag id4 = new CompoundTag();
        final CompoundTag id5 = new CompoundTag();
        id4.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        id4.put("Level", (Tag)id5);
        id5.putInt("xPos", bhd3.x);
        id5.putInt("zPos", bhd3.z);
        id5.putLong("LastUpdate", vk.getGameTime());
        id5.putLong("InhabitedTime", bxh.getInhabitedTime());
        id5.putString("Status", bxh.getStatus().getName());
        final UpgradeData byd6 = bxh.getUpgradeData();
        if (!byd6.isEmpty()) {
            id5.put("UpgradeData", (Tag)byd6.write());
        }
        final LevelChunkSection[] arr7 = bxh.getSections();
        final ListTag ik8 = new ListTag();
        final LevelLightEngine clb9 = vk.getChunkSource().getLightEngine();
        final boolean boolean10 = bxh.isLightCorrect();
        for (int integer11 = -1; integer11 < 17; ++integer11) {
            final int integer12 = integer11;
            final LevelChunkSection bxu13 = (LevelChunkSection)Arrays.stream((Object[])arr7).filter(bxu -> bxu != null && bxu.bottomBlockY() >> 4 == integer12).findFirst().orElse(LevelChunk.EMPTY_SECTION);
            final DataLayer bxn14 = clb9.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(bhd3, integer12));
            final DataLayer bxn15 = clb9.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(bhd3, integer12));
            if (bxu13 != LevelChunk.EMPTY_SECTION || bxn14 != null || bxn15 != null) {
                final CompoundTag id6 = new CompoundTag();
                id6.putByte("Y", (byte)(integer12 & 0xFF));
                if (bxu13 != LevelChunk.EMPTY_SECTION) {
                    bxu13.getStates().write(id6, "Palette", "BlockStates");
                }
                if (bxn14 != null && !bxn14.isEmpty()) {
                    id6.putByteArray("BlockLight", bxn14.getData());
                }
                if (bxn15 != null && !bxn15.isEmpty()) {
                    id6.putByteArray("SkyLight", bxn15.getData());
                }
                ik8.add(id6);
            }
        }
        id5.put("Sections", (Tag)ik8);
        if (boolean10) {
            id5.putBoolean("isLightOn", true);
        }
        final Biome[] arr8 = bxh.getBiomes();
        final int[] arr9 = (arr8 != null) ? new int[arr8.length] : new int[0];
        if (arr8 != null) {
            for (int integer13 = 0; integer13 < arr8.length; ++integer13) {
                arr9[integer13] = Registry.BIOME.getId(arr8[integer13]);
            }
        }
        id5.putIntArray("Biomes", arr9);
        final ListTag ik9 = new ListTag();
        for (final BlockPos ew15 : bxh.getBlockEntitiesPos()) {
            final CompoundTag id6 = bxh.getBlockEntityNbtForSaving(ew15);
            if (id6 != null) {
                ik9.add(id6);
            }
        }
        id5.put("TileEntities", (Tag)ik9);
        final ListTag ik10 = new ListTag();
        if (bxh.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
            final LevelChunk bxt15 = (LevelChunk)bxh;
            bxt15.setLastSaveHadEntities(false);
            for (int integer14 = 0; integer14 < bxt15.getEntitySections().length; ++integer14) {
                for (final Entity aio18 : bxt15.getEntitySections()[integer14]) {
                    final CompoundTag id7 = new CompoundTag();
                    if (aio18.save(id7)) {
                        bxt15.setLastSaveHadEntities(true);
                        ik10.add(id7);
                    }
                }
            }
        }
        else {
            final ProtoChunk byb15 = (ProtoChunk)bxh;
            ik10.addAll((Collection)byb15.getEntities());
            id5.put("Lights", (Tag)packOffsets(byb15.getPackedLights()));
            final CompoundTag id6 = new CompoundTag();
            for (final GenerationStep.Carving a20 : GenerationStep.Carving.values()) {
                id6.putByteArray(a20.toString(), bxh.getCarvingMask(a20).toByteArray());
            }
            id5.put("CarvingMasks", (Tag)id6);
        }
        id5.put("Entities", (Tag)ik10);
        final TickList<Block> big15 = bxh.getBlockTicks();
        if (big15 instanceof ProtoTickList) {
            id5.put("ToBeTicked", (Tag)((ProtoTickList)big15).save());
        }
        else if (big15 instanceof ChunkTickList) {
            id5.put("TileTicks", (Tag)((ChunkTickList)big15).save(vk.getGameTime()));
        }
        else {
            id5.put("TileTicks", (Tag)vk.getBlockTicks().save(bhd3));
        }
        final TickList<Fluid> big16 = bxh.getLiquidTicks();
        if (big16 instanceof ProtoTickList) {
            id5.put("LiquidsToBeTicked", (Tag)((ProtoTickList)big16).save());
        }
        else if (big16 instanceof ChunkTickList) {
            id5.put("LiquidTicks", (Tag)((ChunkTickList)big16).save(vk.getGameTime()));
        }
        else {
            id5.put("LiquidTicks", (Tag)vk.getLiquidTicks().save(bhd3));
        }
        id5.put("PostProcessing", (Tag)packOffsets(bxh.getPostProcessing()));
        final CompoundTag id8 = new CompoundTag();
        for (final Map.Entry<Heightmap.Types, Heightmap> entry19 : bxh.getHeightmaps()) {
            if (bxh.getStatus().heightmapsAfter().contains(entry19.getKey())) {
                id8.put(((Heightmap.Types)entry19.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)entry19.getValue()).getRawData()));
            }
        }
        id5.put("Heightmaps", (Tag)id8);
        id5.put("Structures", (Tag)packStructureData(bhd3, bxh.getAllStarts(), bxh.getAllReferences()));
        return id4;
    }
    
    public static ChunkStatus.ChunkType getChunkTypeFromTag(@Nullable final CompoundTag id) {
        if (id != null) {
            final ChunkStatus bxm2 = ChunkStatus.byName(id.getCompound("Level").getString("Status"));
            if (bxm2 != null) {
                return bxm2.getChunkType();
            }
        }
        return ChunkStatus.ChunkType.PROTOCHUNK;
    }
    
    private static void postLoadChunk(final CompoundTag id, final LevelChunk bxt) {
        final ListTag ik3 = id.getList("Entities", 10);
        final Level bhr4 = bxt.getLevel();
        for (int integer5 = 0; integer5 < ik3.size(); ++integer5) {
            final CompoundTag id2 = ik3.getCompound(integer5);
            EntityType.loadEntityRecursive(id2, bhr4, (Function<Entity, Entity>)(aio -> {
                bxt.addEntity(aio);
                return aio;
            }));
            bxt.setLastSaveHadEntities(true);
        }
        final ListTag ik4 = id.getList("TileEntities", 10);
        for (int integer6 = 0; integer6 < ik4.size(); ++integer6) {
            final CompoundTag id3 = ik4.getCompound(integer6);
            final boolean boolean8 = id3.getBoolean("keepPacked");
            if (boolean8) {
                bxt.setBlockEntityNbt(id3);
            }
            else {
                final BlockEntity btw9 = BlockEntity.loadStatic(id3);
                if (btw9 != null) {
                    bxt.addBlockEntity(btw9);
                }
            }
        }
    }
    
    private static CompoundTag packStructureData(final ChunkPos bhd, final Map<String, StructureStart> map2, final Map<String, LongSet> map3) {
        final CompoundTag id4 = new CompoundTag();
        final CompoundTag id5 = new CompoundTag();
        for (final Map.Entry<String, StructureStart> entry7 : map2.entrySet()) {
            id5.put((String)entry7.getKey(), ((StructureStart)entry7.getValue()).createTag(bhd.x, bhd.z));
        }
        id4.put("Starts", (Tag)id5);
        final CompoundTag id6 = new CompoundTag();
        for (final Map.Entry<String, LongSet> entry8 : map3.entrySet()) {
            id6.put((String)entry8.getKey(), new LongArrayTag((LongSet)entry8.getValue()));
        }
        id4.put("References", (Tag)id6);
        return id4;
    }
    
    private static Map<String, StructureStart> unpackStructureStart(final ChunkGenerator<?> bxi, final StructureManager cjp, final BiomeSource biq, final CompoundTag id) {
        final Map<String, StructureStart> map5 = (Map<String, StructureStart>)Maps.newHashMap();
        final CompoundTag id2 = id.getCompound("Starts");
        for (final String string8 : id2.getAllKeys()) {
            map5.put(string8, StructureFeatureIO.loadStaticStart(bxi, cjp, biq, id2.getCompound(string8)));
        }
        return map5;
    }
    
    private static Map<String, LongSet> unpackStructureReferences(final CompoundTag id) {
        final Map<String, LongSet> map2 = (Map<String, LongSet>)Maps.newHashMap();
        final CompoundTag id2 = id.getCompound("References");
        for (final String string5 : id2.getAllKeys()) {
            map2.put(string5, new LongOpenHashSet(id2.getLongArray(string5)));
        }
        return map2;
    }
    
    public static ListTag packOffsets(final ShortList[] arr) {
        final ListTag ik2 = new ListTag();
        for (final ShortList shortList6 : arr) {
            final ListTag ik3 = new ListTag();
            if (shortList6 != null) {
                for (final Short short9 : shortList6) {
                    ik3.add(new ShortTag(short9));
                }
            }
            ik2.add(ik3);
        }
        return ik2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
