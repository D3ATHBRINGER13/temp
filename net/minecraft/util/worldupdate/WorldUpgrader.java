package net.minecraft.util.worldupdate;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import java.util.regex.Matcher;
import net.minecraft.world.level.chunk.storage.RegionFile;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.nbt.CompoundTag;
import java.util.List;
import java.util.Iterator;
import net.minecraft.ReportedException;
import java.io.IOException;
import net.minecraft.SharedConstants;
import java.util.function.Supplier;
import net.minecraft.world.level.ChunkPos;
import java.util.ListIterator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.TranslatableComponent;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import net.minecraft.Util;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.DimensionDataStorage;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.dimension.DimensionType;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import java.io.File;
import net.minecraft.world.level.storage.LevelStorage;
import java.util.concurrent.ThreadFactory;
import org.apache.logging.log4j.Logger;

public class WorldUpgrader {
    private static final Logger LOGGER;
    private static final ThreadFactory THREAD_FACTORY;
    private final String levelName;
    private final boolean eraseCache;
    private final LevelStorage levelStorage;
    private final Thread thread;
    private final File pathToWorld;
    private volatile boolean running;
    private volatile boolean finished;
    private volatile float progress;
    private volatile int totalChunks;
    private volatile int converted;
    private volatile int skipped;
    private final Object2FloatMap<DimensionType> progressMap;
    private volatile Component status;
    private static final Pattern REGEX;
    private final DimensionDataStorage overworldDataStorage;
    
    public WorldUpgrader(final String string, final LevelStorageSource coq, final LevelData com, final boolean boolean4) {
        this.running = true;
        this.progressMap = (Object2FloatMap<DimensionType>)Object2FloatMaps.synchronize((Object2FloatMap)new Object2FloatOpenCustomHashMap((Hash.Strategy)Util.identityStrategy()));
        this.status = new TranslatableComponent("optimizeWorld.stage.counting", new Object[0]);
        this.levelName = com.getLevelName();
        this.eraseCache = boolean4;
        (this.levelStorage = coq.selectLevel(string, null)).saveLevelData(com);
        this.overworldDataStorage = new DimensionDataStorage(new File(DimensionType.OVERWORLD.getStorageFolder(this.levelStorage.getFolder()), "data"), this.levelStorage.getFixerUpper());
        this.pathToWorld = this.levelStorage.getFolder();
        (this.thread = WorldUpgrader.THREAD_FACTORY.newThread(this::work)).setUncaughtExceptionHandler((thread, throwable) -> {
            WorldUpgrader.LOGGER.error("Error upgrading world", throwable);
            this.status = new TranslatableComponent("optimizeWorld.stage.failed", new Object[0]);
        });
        this.thread.start();
    }
    
    public void cancel() {
        this.running = false;
        try {
            this.thread.join();
        }
        catch (InterruptedException ex) {}
    }
    
    private void work() {
        final File file2 = this.levelStorage.getFolder();
        this.totalChunks = 0;
        final ImmutableMap.Builder<DimensionType, ListIterator<ChunkPos>> builder3 = (ImmutableMap.Builder<DimensionType, ListIterator<ChunkPos>>)ImmutableMap.builder();
        for (final DimensionType byn5 : DimensionType.getAllTypes()) {
            final List<ChunkPos> list6 = this.getAllChunkPos(byn5);
            builder3.put(byn5, list6.listIterator());
            this.totalChunks += list6.size();
        }
        if (this.totalChunks == 0) {
            this.finished = true;
            return;
        }
        final float float4 = (float)this.totalChunks;
        final ImmutableMap<DimensionType, ListIterator<ChunkPos>> immutableMap5 = (ImmutableMap<DimensionType, ListIterator<ChunkPos>>)builder3.build();
        final ImmutableMap.Builder<DimensionType, ChunkStorage> builder4 = (ImmutableMap.Builder<DimensionType, ChunkStorage>)ImmutableMap.builder();
        for (final DimensionType byn6 : DimensionType.getAllTypes()) {
            final File file3 = byn6.getStorageFolder(file2);
            builder4.put(byn6, new ChunkStorage(new File(file3, "region"), this.levelStorage.getFixerUpper()));
        }
        final ImmutableMap<DimensionType, ChunkStorage> immutableMap6 = (ImmutableMap<DimensionType, ChunkStorage>)builder4.build();
        long long8 = Util.getMillis();
        this.status = new TranslatableComponent("optimizeWorld.stage.upgrading", new Object[0]);
        while (this.running) {
            boolean boolean10 = false;
            float float5 = 0.0f;
            for (final DimensionType byn7 : DimensionType.getAllTypes()) {
                final ListIterator<ChunkPos> listIterator14 = (ListIterator<ChunkPos>)immutableMap5.get(byn7);
                final ChunkStorage byg15 = (ChunkStorage)immutableMap6.get(byn7);
                if (listIterator14.hasNext()) {
                    final ChunkPos bhd16 = (ChunkPos)listIterator14.next();
                    boolean boolean11 = false;
                    try {
                        final CompoundTag id18 = byg15.read(bhd16);
                        if (id18 != null) {
                            final int integer19 = ChunkStorage.getVersion(id18);
                            final CompoundTag id19 = byg15.upgradeChunkTag(byn7, (Supplier<DimensionDataStorage>)(() -> this.overworldDataStorage), id18);
                            boolean boolean12 = integer19 < SharedConstants.getCurrentVersion().getWorldVersion();
                            if (this.eraseCache) {
                                final CompoundTag id20 = id19.getCompound("Level");
                                boolean12 = (boolean12 || id20.contains("Heightmaps"));
                                id20.remove("Heightmaps");
                                boolean12 = (boolean12 || id20.contains("isLightOn"));
                                id20.remove("isLightOn");
                            }
                            if (boolean12) {
                                byg15.write(bhd16, id19);
                                boolean11 = true;
                            }
                        }
                    }
                    catch (ReportedException m18) {
                        final Throwable throwable19 = m18.getCause();
                        if (!(throwable19 instanceof IOException)) {
                            throw m18;
                        }
                        WorldUpgrader.LOGGER.error("Error upgrading chunk {}", bhd16, throwable19);
                    }
                    catch (IOException iOException18) {
                        WorldUpgrader.LOGGER.error("Error upgrading chunk {}", bhd16, iOException18);
                    }
                    if (boolean11) {
                        ++this.converted;
                    }
                    else {
                        ++this.skipped;
                    }
                    boolean10 = true;
                }
                final float float6 = listIterator14.nextIndex() / float4;
                this.progressMap.put(byn7, float6);
                float5 += float6;
            }
            this.progress = float5;
            if (!boolean10) {
                this.running = false;
            }
        }
        this.status = new TranslatableComponent("optimizeWorld.stage.finished", new Object[0]);
        for (final ChunkStorage byg16 : immutableMap6.values()) {
            try {
                byg16.close();
            }
            catch (IOException iOException19) {
                WorldUpgrader.LOGGER.error("Error upgrading chunk", (Throwable)iOException19);
            }
        }
        this.overworldDataStorage.save();
        long8 = Util.getMillis() - long8;
        WorldUpgrader.LOGGER.info("World optimizaton finished after {} ms", long8);
        this.finished = true;
    }
    
    private List<ChunkPos> getAllChunkPos(final DimensionType byn) {
        final File file3 = byn.getStorageFolder(this.pathToWorld);
        final File file4 = new File(file3, "region");
        final File[] arr5 = file4.listFiles((file, string) -> string.endsWith(".mca"));
        if (arr5 == null) {
            return (List<ChunkPos>)ImmutableList.of();
        }
        final List<ChunkPos> list6 = (List<ChunkPos>)Lists.newArrayList();
        for (final File file5 : arr5) {
            final Matcher matcher11 = WorldUpgrader.REGEX.matcher((CharSequence)file5.getName());
            if (matcher11.matches()) {
                final int integer12 = Integer.parseInt(matcher11.group(1)) << 5;
                final int integer13 = Integer.parseInt(matcher11.group(2)) << 5;
                try (final RegionFile byi14 = new RegionFile(file5)) {
                    for (int integer14 = 0; integer14 < 32; ++integer14) {
                        for (int integer15 = 0; integer15 < 32; ++integer15) {
                            final ChunkPos bhd18 = new ChunkPos(integer14 + integer12, integer15 + integer13);
                            if (byi14.doesChunkExist(bhd18)) {
                                list6.add(bhd18);
                            }
                        }
                    }
                }
                catch (Throwable t4) {}
            }
        }
        return list6;
    }
    
    public boolean isFinished() {
        return this.finished;
    }
    
    public float dimensionProgress(final DimensionType byn) {
        return this.progressMap.getFloat(byn);
    }
    
    public float getProgress() {
        return this.progress;
    }
    
    public int getTotalChunks() {
        return this.totalChunks;
    }
    
    public int getConverted() {
        return this.converted;
    }
    
    public int getSkipped() {
        return this.skipped;
    }
    
    public Component getStatus() {
        return this.status;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
        REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    }
}
