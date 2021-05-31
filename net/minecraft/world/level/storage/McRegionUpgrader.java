package net.minecraft.world.level.storage;

import org.apache.logging.log4j.LogManager;
import java.util.Collections;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.storage.OldChunkStorage;
import java.io.IOException;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import java.util.Iterator;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.biome.OverworldBiomeSourceSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSourceSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.biome.BiomeSourceType;
import java.util.Collection;
import net.minecraft.world.level.dimension.DimensionType;
import java.io.File;
import com.google.common.collect.Lists;
import net.minecraft.util.ProgressListener;
import com.mojang.datafixers.DataFixer;
import java.nio.file.Path;
import org.apache.logging.log4j.Logger;

public class McRegionUpgrader {
    private static final Logger LOGGER;
    
    static boolean convertLevel(final Path path, final DataFixer dataFixer, final String string, final ProgressListener zz) {
        zz.progressStagePercentage(0);
        final List<File> list5 = (List<File>)Lists.newArrayList();
        final List<File> list6 = (List<File>)Lists.newArrayList();
        final List<File> list7 = (List<File>)Lists.newArrayList();
        final File file8 = new File(path.toFile(), string);
        final File file9 = DimensionType.NETHER.getStorageFolder(file8);
        final File file10 = DimensionType.THE_END.getStorageFolder(file8);
        McRegionUpgrader.LOGGER.info("Scanning folders...");
        addRegionFiles(file8, (Collection<File>)list5);
        if (file9.exists()) {
            addRegionFiles(file9, (Collection<File>)list6);
        }
        if (file10.exists()) {
            addRegionFiles(file10, (Collection<File>)list7);
        }
        final int integer11 = list5.size() + list6.size() + list7.size();
        McRegionUpgrader.LOGGER.info("Total conversion count is {}", integer11);
        final LevelData com12 = LevelStorageSource.getDataTagFor(path, dataFixer, string);
        final BiomeSourceType<FixedBiomeSourceSettings, FixedBiomeSource> bis14 = BiomeSourceType.FIXED;
        final BiomeSourceType<OverworldBiomeSourceSettings, OverworldBiomeSource> bis15 = BiomeSourceType.VANILLA_LAYERED;
        BiomeSource biq13;
        if (com12 != null && com12.getGeneratorType() == LevelType.FLAT) {
            biq13 = bis14.create(bis14.createSettings().setBiome(Biomes.PLAINS));
        }
        else {
            biq13 = bis15.create(bis15.createSettings().setLevelData(com12).setGeneratorSettings(ChunkGeneratorType.SURFACE.createSettings()));
        }
        convertRegions(new File(file8, "region"), (Iterable<File>)list5, biq13, 0, integer11, zz);
        convertRegions(new File(file9, "region"), (Iterable<File>)list6, bis14.create(bis14.createSettings().setBiome(Biomes.NETHER)), list5.size(), integer11, zz);
        convertRegions(new File(file10, "region"), (Iterable<File>)list7, bis14.create(bis14.createSettings().setBiome(Biomes.THE_END)), list5.size() + list6.size(), integer11, zz);
        com12.setVersion(19133);
        if (com12.getGeneratorType() == LevelType.NORMAL_1_1) {
            com12.setGenerator(LevelType.NORMAL);
        }
        makeMcrLevelDatBackup(path, string);
        final LevelStorage coo16 = LevelStorageSource.selectLevel(path, dataFixer, string, null);
        coo16.saveLevelData(com12);
        return true;
    }
    
    private static void makeMcrLevelDatBackup(final Path path, final String string) {
        final File file3 = new File(path.toFile(), string);
        if (!file3.exists()) {
            McRegionUpgrader.LOGGER.warn("Unable to create level.dat_mcr backup");
            return;
        }
        final File file4 = new File(file3, "level.dat");
        if (!file4.exists()) {
            McRegionUpgrader.LOGGER.warn("Unable to create level.dat_mcr backup");
            return;
        }
        final File file5 = new File(file3, "level.dat_mcr");
        if (!file4.renameTo(file5)) {
            McRegionUpgrader.LOGGER.warn("Unable to create level.dat_mcr backup");
        }
    }
    
    private static void convertRegions(final File file, final Iterable<File> iterable, final BiomeSource biq, int integer4, final int integer5, final ProgressListener zz) {
        for (final File file2 : iterable) {
            convertRegion(file, file2, biq, integer4, integer5, zz);
            ++integer4;
            final int integer6 = (int)Math.round(100.0 * integer4 / integer5);
            zz.progressStagePercentage(integer6);
        }
    }
    
    private static void convertRegion(final File file1, final File file2, final BiomeSource biq, final int integer4, final int integer5, final ProgressListener zz) {
        final String string7 = file2.getName();
        try (final RegionFile byi8 = new RegionFile(file2);
             final RegionFile byi9 = new RegionFile(new File(file1, string7.substring(0, string7.length() - ".mcr".length()) + ".mca"))) {
            for (int integer6 = 0; integer6 < 32; ++integer6) {
                for (int integer7 = 0; integer7 < 32; ++integer7) {
                    final ChunkPos bhd14 = new ChunkPos(integer6, integer7);
                    if (byi8.hasChunk(bhd14) && !byi9.hasChunk(bhd14)) {
                        CompoundTag id15;
                        try (final DataInputStream dataInputStream16 = byi8.getChunkDataInputStream(bhd14)) {
                            if (dataInputStream16 == null) {
                                McRegionUpgrader.LOGGER.warn("Failed to fetch input stream for chunk {}", bhd14);
                                continue;
                            }
                            id15 = NbtIo.read(dataInputStream16);
                        }
                        catch (IOException iOException16) {
                            McRegionUpgrader.LOGGER.warn("Failed to read data for chunk {}", bhd14, iOException16);
                            continue;
                        }
                        final CompoundTag id16 = id15.getCompound("Level");
                        final OldChunkStorage.OldLevelChunk a17 = OldChunkStorage.load(id16);
                        final CompoundTag id17 = new CompoundTag();
                        final CompoundTag id18 = new CompoundTag();
                        id17.put("Level", (Tag)id18);
                        OldChunkStorage.convertToAnvilFormat(a17, id18, biq);
                        try (final DataOutputStream dataOutputStream20 = byi9.getChunkDataOutputStream(bhd14)) {
                            NbtIo.write(id17, (DataOutput)dataOutputStream20);
                        }
                    }
                }
                int integer7 = (int)Math.round(100.0 * (integer4 * 1024) / (integer5 * 1024));
                final int integer8 = (int)Math.round(100.0 * ((integer6 + 1) * 32 + integer4 * 1024) / (integer5 * 1024));
                if (integer8 > integer7) {
                    zz.progressStagePercentage(integer8);
                }
            }
        }
        catch (IOException iOException17) {
            McRegionUpgrader.LOGGER.error("Failed to upgrade region file {}", file2, iOException17);
        }
    }
    
    private static void addRegionFiles(final File file, final Collection<File> collection) {
        final File file2 = new File(file, "region");
        final File[] arr4 = file2.listFiles((file, string) -> string.endsWith(".mcr"));
        if (arr4 != null) {
            Collections.addAll((Collection)collection, (Object[])arr4);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
