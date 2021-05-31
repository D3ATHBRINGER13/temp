package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import net.minecraft.core.Registry;
import java.lang.management.ThreadMXBean;
import java.util.Comparator;
import java.lang.management.ThreadInfo;
import java.lang.management.ManagementFactory;
import com.google.common.base.Splitter;
import java.io.Writer;
import java.nio.file.OpenOption;
import net.minecraft.resources.ResourceLocation;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.packs.Pack;
import java.util.function.Function;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.CrashReportDetail;
import java.nio.file.Path;
import joptsimple.OptionSet;
import net.minecraft.DefaultUncaughtExceptionHandler;
import java.awt.GraphicsEnvironment;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.util.datafix.DataFixers;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import java.nio.file.Paths;
import joptsimple.OptionSpec;
import joptsimple.OptionParser;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import java.util.Collections;
import java.util.Arrays;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.authlib.GameProfile;
import java.util.function.BooleanSupplier;
import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;
import io.netty.buffer.ByteBuf;
import java.util.Base64;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import io.netty.buffer.ByteBufOutputStream;
import org.apache.commons.lang3.Validate;
import javax.imageio.ImageIO;
import io.netty.buffer.Unpooled;
import java.util.Date;
import java.text.SimpleDateFormat;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.LevelConflictException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import java.util.Collection;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import java.util.Iterator;
import net.minecraft.server.level.DerivedServerLevel;
import net.minecraft.ReportedException;
import net.minecraft.CrashReport;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.storage.LevelStorage;
import com.google.gson.JsonElement;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.util.Mth;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.util.ProgressListener;
import net.minecraft.network.chat.TranslatableComponent;
import java.io.IOException;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SaveDataDirtyRunnable;
import net.minecraft.world.scores.Scoreboard;
import java.util.function.Supplier;
import net.minecraft.world.scores.ScoreboardSaveData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.server.packs.PackType;
import java.util.function.Consumer;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.Util;
import java.util.concurrent.Executor;
import net.minecraft.util.FrameTimer;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.commands.Commands;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.UnopenedPack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.players.GameProfileCache;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.network.chat.Component;
import java.security.KeyPair;
import javax.annotation.Nullable;
import java.net.Proxy;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import java.util.Map;
import com.mojang.datafixers.DataFixer;
import java.util.Random;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.util.profiling.GameProfiler;
import java.util.List;
import net.minecraft.world.Snooper;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.util.Unit;
import java.util.concurrent.CompletableFuture;
import java.io.File;
import org.apache.logging.log4j.Logger;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.SnooperPopulator;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;

public abstract class MinecraftServer extends ReentrantBlockableEventLoop<TickTask> implements SnooperPopulator, CommandSource, AutoCloseable, Runnable {
    private static final Logger LOGGER;
    public static final File USERID_CACHE_FILE;
    private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK;
    public static final LevelSettings DEMO_SETTINGS;
    private final LevelStorageSource storageSource;
    private final Snooper snooper;
    private final File universe;
    private final List<Runnable> tickables;
    private final GameProfiler profiler;
    private final ServerConnectionListener connection;
    protected final ChunkProgressListenerFactory progressListenerFactory;
    private final ServerStatus status;
    private final Random random;
    private final DataFixer fixerUpper;
    private String localIp;
    private int port;
    private final Map<DimensionType, ServerLevel> levels;
    private PlayerList playerList;
    private volatile boolean running;
    private boolean stopped;
    private int tickCount;
    protected final Proxy proxy;
    private boolean onlineMode;
    private boolean preventProxyConnections;
    private boolean animals;
    private boolean npcs;
    private boolean pvp;
    private boolean allowFlight;
    @Nullable
    private String motd;
    private int maxBuildHeight;
    private int playerIdleTimeout;
    public final long[] tickTimes;
    @Nullable
    private KeyPair keyPair;
    @Nullable
    private String singleplayerName;
    private final String levelIdName;
    @Nullable
    private String levelName;
    private boolean isDemo;
    private boolean levelHasStartingBonusChest;
    private String resourcePack;
    private String resourcePackHash;
    private volatile boolean isReady;
    private long lastOverloadWarning;
    @Nullable
    private Component startupState;
    private boolean delayProfilerStart;
    private boolean forceGameType;
    @Nullable
    private final YggdrasilAuthenticationService authenticationService;
    private final MinecraftSessionService sessionService;
    private final GameProfileRepository profileRepository;
    private final GameProfileCache profileCache;
    private long lastServerStatus;
    protected final Thread serverThread;
    private long nextTickTime;
    private long delayedTasksMaxNextTickTime;
    private boolean mayHaveDelayedTasks;
    private boolean hasWorldScreenshot;
    private final ReloadableResourceManager resources;
    private final PackRepository<UnopenedPack> packRepository;
    @Nullable
    private FolderRepositorySource folderPackSource;
    private final Commands commands;
    private final RecipeManager recipes;
    private final TagManager tags;
    private final ServerScoreboard scoreboard;
    private final CustomBossEvents customBossEvents;
    private final LootTables lootTables;
    private final ServerAdvancementManager advancements;
    private final ServerFunctionManager functions;
    private final FrameTimer frameTimer;
    private boolean enforceWhitelist;
    private boolean forceUpgrade;
    private boolean eraseCache;
    private float averageTickTime;
    private final Executor executor;
    @Nullable
    private String serverId;
    
    public MinecraftServer(final File file, final Proxy proxy, final DataFixer dataFixer, final Commands ce, final YggdrasilAuthenticationService yggdrasilAuthenticationService, final MinecraftSessionService minecraftSessionService, final GameProfileRepository gameProfileRepository, final GameProfileCache xr, final ChunkProgressListenerFactory vu, final String string) {
        super("Server");
        this.snooper = new Snooper("server", (SnooperPopulator)this, Util.getMillis());
        this.tickables = (List<Runnable>)Lists.newArrayList();
        this.profiler = new GameProfiler(this::getTickCount);
        this.status = new ServerStatus();
        this.random = new Random();
        this.port = -1;
        this.levels = (Map<DimensionType, ServerLevel>)Maps.newIdentityHashMap();
        this.running = true;
        this.tickTimes = new long[100];
        this.resourcePack = "";
        this.resourcePackHash = "";
        this.serverThread = Util.<Thread>make(new Thread((Runnable)this, "Server thread"), (java.util.function.Consumer<Thread>)(thread -> thread.setUncaughtExceptionHandler((thread, throwable) -> MinecraftServer.LOGGER.error(throwable))));
        this.nextTickTime = Util.getMillis();
        this.resources = new SimpleReloadableResourceManager(PackType.SERVER_DATA, this.serverThread);
        this.packRepository = new PackRepository<UnopenedPack>(UnopenedPack::new);
        this.recipes = new RecipeManager();
        this.tags = new TagManager();
        this.scoreboard = new ServerScoreboard(this);
        this.customBossEvents = new CustomBossEvents(this);
        this.lootTables = new LootTables();
        this.advancements = new ServerAdvancementManager();
        this.functions = new ServerFunctionManager(this);
        this.frameTimer = new FrameTimer();
        this.proxy = proxy;
        this.commands = ce;
        this.authenticationService = yggdrasilAuthenticationService;
        this.sessionService = minecraftSessionService;
        this.profileRepository = gameProfileRepository;
        this.profileCache = xr;
        this.universe = file;
        this.connection = new ServerConnectionListener(this);
        this.progressListenerFactory = vu;
        this.storageSource = new LevelStorageSource(file.toPath(), file.toPath().resolve("../backups"), dataFixer);
        this.fixerUpper = dataFixer;
        this.resources.registerReloadListener(this.tags);
        this.resources.registerReloadListener(this.recipes);
        this.resources.registerReloadListener(this.lootTables);
        this.resources.registerReloadListener(this.functions);
        this.resources.registerReloadListener(this.advancements);
        this.executor = Util.backgroundExecutor();
        this.levelIdName = string;
    }
    
    private void readScoreboard(final DimensionDataStorage col) {
        final ScoreboardSaveData ctj3 = col.<ScoreboardSaveData>computeIfAbsent((java.util.function.Supplier<ScoreboardSaveData>)ScoreboardSaveData::new, "scoreboard");
        ctj3.setScoreboard(this.getScoreboard());
        this.getScoreboard().addDirtyListener((Runnable)new SaveDataDirtyRunnable(ctj3));
    }
    
    protected abstract boolean initServer() throws IOException;
    
    protected void ensureLevelConversion(final String string) {
        if (this.getStorageSource().requiresConversion(string)) {
            MinecraftServer.LOGGER.info("Converting map!");
            this.setServerStartupState(new TranslatableComponent("menu.convertingLevel", new Object[0]));
            this.getStorageSource().convertLevel(string, new ProgressListener() {
                private long timeStamp = Util.getMillis();
                
                public void progressStartNoAbort(final Component jo) {
                }
                
                public void progressStart(final Component jo) {
                }
                
                public void progressStagePercentage(final int integer) {
                    if (Util.getMillis() - this.timeStamp >= 1000L) {
                        this.timeStamp = Util.getMillis();
                        MinecraftServer.LOGGER.info("Converting... {}%", integer);
                    }
                }
                
                public void stop() {
                }
                
                public void progressStage(final Component jo) {
                }
            });
        }
        if (this.forceUpgrade) {
            MinecraftServer.LOGGER.info("Forcing world upgrade!");
            final LevelData com3 = this.getStorageSource().getDataTagFor(this.getLevelIdName());
            if (com3 != null) {
                final WorldUpgrader agx4 = new WorldUpgrader(this.getLevelIdName(), this.getStorageSource(), com3, this.eraseCache);
                Component jo5 = null;
                while (!agx4.isFinished()) {
                    final Component jo6 = agx4.getStatus();
                    if (jo5 != jo6) {
                        jo5 = jo6;
                        MinecraftServer.LOGGER.info(agx4.getStatus().getString());
                    }
                    final int integer7 = agx4.getTotalChunks();
                    if (integer7 > 0) {
                        final int integer8 = agx4.getConverted() + agx4.getSkipped();
                        MinecraftServer.LOGGER.info("{}% completed ({} / {} chunks)...", Mth.floor(integer8 / (float)integer7 * 100.0f), integer8, integer7);
                    }
                    if (this.isStopped()) {
                        agx4.cancel();
                    }
                    else {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException ex) {}
                    }
                }
            }
        }
    }
    
    protected synchronized void setServerStartupState(final Component jo) {
        this.startupState = jo;
    }
    
    protected void loadLevel(final String string1, final String string2, final long long3, final LevelType bhy, final JsonElement jsonElement) {
        this.ensureLevelConversion(string1);
        this.setServerStartupState(new TranslatableComponent("menu.loadingLevel", new Object[0]));
        final LevelStorage coo8 = this.getStorageSource().selectLevel(string1, this);
        this.detectBundledResources(this.getLevelIdName(), coo8);
        LevelData com10 = coo8.prepareLevel();
        LevelSettings bhv9;
        if (com10 == null) {
            if (this.isDemo()) {
                bhv9 = MinecraftServer.DEMO_SETTINGS;
            }
            else {
                bhv9 = new LevelSettings(long3, this.getDefaultGameType(), this.canGenerateStructures(), this.isHardcore(), bhy);
                bhv9.setLevelTypeOptions(jsonElement);
                if (this.levelHasStartingBonusChest) {
                    bhv9.enableStartingBonusItems();
                }
            }
            com10 = new LevelData(bhv9, string2);
        }
        else {
            com10.setLevelName(string2);
            bhv9 = new LevelSettings(com10);
        }
        this.loadDataPacks(coo8.getFolder(), com10);
        final ChunkProgressListener vt11 = this.progressListenerFactory.create(11);
        this.createLevels(coo8, com10, bhv9, vt11);
        this.setDifficulty(this.getDefaultDifficulty(), true);
        this.prepareLevels(vt11);
    }
    
    protected void createLevels(final LevelStorage coo, final LevelData com, final LevelSettings bhv, final ChunkProgressListener vt) {
        if (this.isDemo()) {
            com.setLevelSettings(MinecraftServer.DEMO_SETTINGS);
        }
        final ServerLevel vk6 = new ServerLevel(this, this.executor, coo, com, DimensionType.OVERWORLD, this.profiler, vt);
        this.levels.put(DimensionType.OVERWORLD, vk6);
        this.readScoreboard(vk6.getDataStorage());
        vk6.getWorldBorder().readBorderData(com);
        final ServerLevel vk7 = this.getLevel(DimensionType.OVERWORLD);
        if (!com.isInitialized()) {
            try {
                vk7.setInitialSpawn(bhv);
                if (com.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES) {
                    this.setupDebugLevel(com);
                }
                com.setInitialized(true);
            }
            catch (Throwable throwable8) {
                final CrashReport d9 = CrashReport.forThrowable(throwable8, "Exception initializing level");
                try {
                    vk7.fillReportDetails(d9);
                }
                catch (Throwable t) {}
                throw new ReportedException(d9);
            }
            com.setInitialized(true);
        }
        this.getPlayerList().setLevel(vk7);
        if (com.getCustomBossEvents() != null) {
            this.getCustomBossEvents().load(com.getCustomBossEvents());
        }
        for (final DimensionType byn9 : DimensionType.getAllTypes()) {
            if (byn9 == DimensionType.OVERWORLD) {
                continue;
            }
            this.levels.put(byn9, new DerivedServerLevel(vk7, this, this.executor, coo, byn9, this.profiler, vt));
        }
    }
    
    private void setupDebugLevel(final LevelData com) {
        com.setGenerateMapFeatures(false);
        com.setAllowCommands(true);
        com.setRaining(false);
        com.setThundering(false);
        com.setClearWeatherTime(1000000000);
        com.setDayTime(6000L);
        com.setGameType(GameType.SPECTATOR);
        com.setHardcore(false);
        com.setDifficulty(Difficulty.PEACEFUL);
        com.setDifficultyLocked(true);
        com.getGameRules().<GameRules.BooleanValue>getRule(GameRules.RULE_DAYLIGHT).set(false, this);
    }
    
    protected void loadDataPacks(final File file, final LevelData com) {
        this.packRepository.addSource(new ServerPacksSource());
        this.folderPackSource = new FolderRepositorySource(new File(file, "datapacks"));
        this.packRepository.addSource(this.folderPackSource);
        this.packRepository.reload();
        final List<UnopenedPack> list4 = (List<UnopenedPack>)Lists.newArrayList();
        for (final String string6 : com.getEnabledDataPacks()) {
            final UnopenedPack xa7 = this.packRepository.getPack(string6);
            if (xa7 != null) {
                list4.add(xa7);
            }
            else {
                MinecraftServer.LOGGER.warn("Missing data pack {}", string6);
            }
        }
        this.packRepository.setSelected((java.util.Collection<UnopenedPack>)list4);
        this.updateSelectedPacks(com);
    }
    
    protected void prepareLevels(final ChunkProgressListener vt) {
        this.setServerStartupState(new TranslatableComponent("menu.generatingTerrain", new Object[0]));
        final ServerLevel vk3 = this.getLevel(DimensionType.OVERWORLD);
        MinecraftServer.LOGGER.info(new StringBuilder().append("Preparing start region for dimension ").append(DimensionType.getName(vk3.dimension.getType())).toString());
        final BlockPos ew4 = vk3.getSharedSpawnPos();
        vt.updateSpawnPos(new ChunkPos(ew4));
        final ServerChunkCache vi5 = vk3.getChunkSource();
        vi5.getLightEngine().setTaskPerBatch(500);
        this.nextTickTime = Util.getMillis();
        vi5.<Unit>addRegionTicket(TicketType.START, new ChunkPos(ew4), 11, Unit.INSTANCE);
        while (vi5.getTickingGenerated() != 441) {
            this.nextTickTime = Util.getMillis() + 10L;
            this.waitUntilNextTick();
        }
        this.nextTickTime = Util.getMillis() + 10L;
        this.waitUntilNextTick();
        for (final DimensionType byn7 : DimensionType.getAllTypes()) {
            final ForcedChunksSavedData bhm8 = this.getLevel(byn7).getDataStorage().<ForcedChunksSavedData>get((java.util.function.Supplier<ForcedChunksSavedData>)ForcedChunksSavedData::new, "chunks");
            if (bhm8 != null) {
                final ServerLevel vk4 = this.getLevel(byn7);
                final LongIterator longIterator10 = bhm8.getChunks().iterator();
                while (longIterator10.hasNext()) {
                    final long long11 = longIterator10.nextLong();
                    final ChunkPos bhd13 = new ChunkPos(long11);
                    vk4.getChunkSource().updateChunkForced(bhd13, true);
                }
            }
        }
        this.nextTickTime = Util.getMillis() + 10L;
        this.waitUntilNextTick();
        vt.stop();
        vi5.getLightEngine().setTaskPerBatch(5);
    }
    
    protected void detectBundledResources(final String string, final LevelStorage coo) {
        final File file4 = new File(coo.getFolder(), "resources.zip");
        if (file4.isFile()) {
            try {
                this.setResourcePack("level://" + URLEncoder.encode(string, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException5) {
                MinecraftServer.LOGGER.warn("Something went wrong url encoding {}", string);
            }
        }
    }
    
    public abstract boolean canGenerateStructures();
    
    public abstract GameType getDefaultGameType();
    
    public abstract Difficulty getDefaultDifficulty();
    
    public abstract boolean isHardcore();
    
    public abstract int getOperatorUserPermissionLevel();
    
    public abstract int getFunctionCompilationLevel();
    
    public abstract boolean shouldRconBroadcast();
    
    public boolean saveAllChunks(final boolean boolean1, final boolean boolean2, final boolean boolean3) {
        boolean boolean4 = false;
        for (final ServerLevel vk7 : this.getAllLevels()) {
            if (!boolean1) {
                MinecraftServer.LOGGER.info("Saving chunks for level '{}'/{}", vk7.getLevelData().getLevelName(), DimensionType.getName(vk7.dimension.getType()));
            }
            try {
                vk7.save(null, boolean2, vk7.noSave && !boolean3);
            }
            catch (LevelConflictException bht8) {
                MinecraftServer.LOGGER.warn(bht8.getMessage());
            }
            boolean4 = true;
        }
        final ServerLevel vk8 = this.getLevel(DimensionType.OVERWORLD);
        final LevelData com7 = vk8.getLevelData();
        vk8.getWorldBorder().saveWorldBorderData(com7);
        com7.setCustomBossEvents(this.getCustomBossEvents().save());
        vk8.getLevelStorage().saveLevelData(com7, this.getPlayerList().getSingleplayerData());
        return boolean4;
    }
    
    public void close() {
        this.stopServer();
    }
    
    protected void stopServer() {
        MinecraftServer.LOGGER.info("Stopping server");
        if (this.getConnection() != null) {
            this.getConnection().stop();
        }
        if (this.playerList != null) {
            MinecraftServer.LOGGER.info("Saving players");
            this.playerList.saveAll();
            this.playerList.removeAll();
        }
        MinecraftServer.LOGGER.info("Saving worlds");
        for (final ServerLevel vk3 : this.getAllLevels()) {
            if (vk3 != null) {
                vk3.noSave = false;
            }
        }
        this.saveAllChunks(false, true, false);
        for (final ServerLevel vk3 : this.getAllLevels()) {
            if (vk3 != null) {
                try {
                    vk3.close();
                }
                catch (IOException iOException4) {
                    MinecraftServer.LOGGER.error("Exception closing the level", (Throwable)iOException4);
                }
            }
        }
        if (this.snooper.isStarted()) {
            this.snooper.interrupt();
        }
    }
    
    public String getLocalIp() {
        return this.localIp;
    }
    
    public void setLocalIp(final String string) {
        this.localIp = string;
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    public void halt(final boolean boolean1) {
        this.running = false;
        if (boolean1) {
            try {
                this.serverThread.join();
            }
            catch (InterruptedException interruptedException3) {
                MinecraftServer.LOGGER.error("Error while shutting down", (Throwable)interruptedException3);
            }
        }
    }
    
    public void run() {
        try {
            if (this.initServer()) {
                this.nextTickTime = Util.getMillis();
                this.status.setDescription(new TextComponent(this.motd));
                this.status.setVersion(new ServerStatus.Version(SharedConstants.getCurrentVersion().getName(), SharedConstants.getCurrentVersion().getProtocolVersion()));
                this.updateStatusIcon(this.status);
                while (this.running) {
                    final long long2 = Util.getMillis() - this.nextTickTime;
                    if (long2 > 2000L && this.nextTickTime - this.lastOverloadWarning >= 15000L) {
                        final long long3 = long2 / 50L;
                        MinecraftServer.LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", long2, long3);
                        this.nextTickTime += long3 * 50L;
                        this.lastOverloadWarning = this.nextTickTime;
                    }
                    this.nextTickTime += 50L;
                    if (this.delayProfilerStart) {
                        this.delayProfilerStart = false;
                        this.profiler.continuous().enable();
                    }
                    this.profiler.startTick();
                    this.profiler.push("tick");
                    this.tickServer(this::haveTime);
                    this.profiler.popPush("nextTickWait");
                    this.mayHaveDelayedTasks = true;
                    this.delayedTasksMaxNextTickTime = Math.max(Util.getMillis() + 50L, this.nextTickTime);
                    this.waitUntilNextTick();
                    this.profiler.pop();
                    this.profiler.endTick();
                    this.isReady = true;
                }
            }
            else {
                this.onServerCrash(null);
            }
        }
        catch (Throwable throwable2) {
            MinecraftServer.LOGGER.error("Encountered an unexpected exception", throwable2);
            CrashReport d3;
            if (throwable2 instanceof ReportedException) {
                d3 = this.fillReport(((ReportedException)throwable2).getReport());
            }
            else {
                d3 = this.fillReport(new CrashReport("Exception in server tick loop", throwable2));
            }
            final File file4 = new File(new File(this.getServerDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
            if (d3.saveToFile(file4)) {
                MinecraftServer.LOGGER.error("This crash report has been saved to: {}", file4.getAbsolutePath());
            }
            else {
                MinecraftServer.LOGGER.error("We were unable to save this crash report to disk.");
            }
            this.onServerCrash(d3);
            try {
                this.stopped = true;
                this.stopServer();
            }
            catch (Throwable throwable2) {
                MinecraftServer.LOGGER.error("Exception stopping the server", throwable2);
            }
            finally {
                this.onServerExit();
            }
        }
        finally {
            try {
                this.stopped = true;
                this.stopServer();
            }
            catch (Throwable throwable3) {
                MinecraftServer.LOGGER.error("Exception stopping the server", throwable3);
                this.onServerExit();
            }
            finally {
                this.onServerExit();
            }
        }
    }
    
    private boolean haveTime() {
        return this.runningTask() || Util.getMillis() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTime : this.nextTickTime);
    }
    
    protected void waitUntilNextTick() {
        this.runAllTasks();
        this.managedBlock(() -> !this.haveTime());
    }
    
    protected TickTask wrapRunnable(final Runnable runnable) {
        return new TickTask(this.tickCount, runnable);
    }
    
    protected boolean shouldRun(final TickTask rk) {
        return rk.getTick() + 3 < this.tickCount || this.haveTime();
    }
    
    public boolean pollTask() {
        final boolean boolean2 = this.pollTaskInternal();
        return this.mayHaveDelayedTasks = boolean2;
    }
    
    private boolean pollTaskInternal() {
        if (super.pollTask()) {
            return true;
        }
        if (this.haveTime()) {
            for (final ServerLevel vk3 : this.getAllLevels()) {
                if (vk3.getChunkSource().pollTask()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void updateStatusIcon(final ServerStatus qf) {
        File file3 = this.getFile("server-icon.png");
        if (!file3.exists()) {
            file3 = this.getStorageSource().getFile(this.getLevelIdName(), "icon.png");
        }
        if (file3.isFile()) {
            final ByteBuf byteBuf4 = Unpooled.buffer();
            try {
                final BufferedImage bufferedImage5 = ImageIO.read(file3);
                Validate.validState(bufferedImage5.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(bufferedImage5.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                ImageIO.write((RenderedImage)bufferedImage5, "PNG", (OutputStream)new ByteBufOutputStream(byteBuf4));
                final ByteBuffer byteBuffer6 = Base64.getEncoder().encode(byteBuf4.nioBuffer());
                qf.setFavicon(new StringBuilder().append("data:image/png;base64,").append(StandardCharsets.UTF_8.decode(byteBuffer6)).toString());
            }
            catch (Exception exception5) {
                MinecraftServer.LOGGER.error("Couldn't load server icon", (Throwable)exception5);
            }
            finally {
                byteBuf4.release();
            }
        }
    }
    
    public boolean hasWorldScreenshot() {
        return this.hasWorldScreenshot = (this.hasWorldScreenshot || this.getWorldScreenshotFile().isFile());
    }
    
    public File getWorldScreenshotFile() {
        return this.getStorageSource().getFile(this.getLevelIdName(), "icon.png");
    }
    
    public File getServerDirectory() {
        return new File(".");
    }
    
    protected void onServerCrash(final CrashReport d) {
    }
    
    protected void onServerExit() {
    }
    
    protected void tickServer(final BooleanSupplier booleanSupplier) {
        final long long3 = Util.getNanos();
        ++this.tickCount;
        this.tickChildren(booleanSupplier);
        if (long3 - this.lastServerStatus >= 5000000000L) {
            this.lastServerStatus = long3;
            this.status.setPlayers(new ServerStatus.Players(this.getMaxPlayers(), this.getPlayerCount()));
            final GameProfile[] arr5 = new GameProfile[Math.min(this.getPlayerCount(), 12)];
            final int integer6 = Mth.nextInt(this.random, 0, this.getPlayerCount() - arr5.length);
            for (int integer7 = 0; integer7 < arr5.length; ++integer7) {
                arr5[integer7] = ((ServerPlayer)this.playerList.getPlayers().get(integer6 + integer7)).getGameProfile();
            }
            Collections.shuffle(Arrays.asList((Object[])arr5));
            this.status.getPlayers().setSample(arr5);
        }
        if (this.tickCount % 6000 == 0) {
            MinecraftServer.LOGGER.debug("Autosave started");
            this.profiler.push("save");
            this.playerList.saveAll();
            this.saveAllChunks(true, false, false);
            this.profiler.pop();
            MinecraftServer.LOGGER.debug("Autosave finished");
        }
        this.profiler.push("snooper");
        if (!this.snooper.isStarted() && this.tickCount > 100) {
            this.snooper.start();
        }
        if (this.tickCount % 6000 == 0) {
            this.snooper.prepare();
        }
        this.profiler.pop();
        this.profiler.push("tallying");
        final long[] tickTimes = this.tickTimes;
        final int n = this.tickCount % 100;
        final long n2 = Util.getNanos() - long3;
        tickTimes[n] = n2;
        final long long4 = n2;
        this.averageTickTime = this.averageTickTime * 0.8f + long4 / 1000000.0f * 0.19999999f;
        final long long5 = Util.getNanos();
        this.frameTimer.logFrameDuration(long5 - long3);
        this.profiler.pop();
    }
    
    protected void tickChildren(final BooleanSupplier booleanSupplier) {
        this.profiler.push("commandFunctions");
        this.getFunctions().tick();
        this.profiler.popPush("levels");
        for (final ServerLevel vk4 : this.getAllLevels()) {
            if (vk4.dimension.getType() == DimensionType.OVERWORLD || this.isNetherEnabled()) {
                this.profiler.push((Supplier<String>)(() -> vk4.getLevelData().getLevelName() + " " + Registry.DIMENSION_TYPE.getKey(vk4.dimension.getType())));
                if (this.tickCount % 20 == 0) {
                    this.profiler.push("timeSync");
                    this.playerList.broadcastAll(new ClientboundSetTimePacket(vk4.getGameTime(), vk4.getDayTime(), vk4.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)), vk4.dimension.getType());
                    this.profiler.pop();
                }
                this.profiler.push("tick");
                try {
                    vk4.tick(booleanSupplier);
                }
                catch (Throwable throwable5) {
                    final CrashReport d6 = CrashReport.forThrowable(throwable5, "Exception ticking world");
                    vk4.fillReportDetails(d6);
                    throw new ReportedException(d6);
                }
                this.profiler.pop();
                this.profiler.pop();
            }
        }
        this.profiler.popPush("connection");
        this.getConnection().tick();
        this.profiler.popPush("players");
        this.playerList.tick();
        this.profiler.popPush("server gui refresh");
        for (int integer3 = 0; integer3 < this.tickables.size(); ++integer3) {
            ((Runnable)this.tickables.get(integer3)).run();
        }
        this.profiler.pop();
    }
    
    public boolean isNetherEnabled() {
        return true;
    }
    
    public void addTickable(final Runnable runnable) {
        this.tickables.add(runnable);
    }
    
    public static void main(final String[] arr) {
        final OptionParser optionParser2 = new OptionParser();
        final OptionSpec<Void> optionSpec3 = (OptionSpec<Void>)optionParser2.accepts("nogui");
        final OptionSpec<Void> optionSpec4 = (OptionSpec<Void>)optionParser2.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
        final OptionSpec<Void> optionSpec5 = (OptionSpec<Void>)optionParser2.accepts("demo");
        final OptionSpec<Void> optionSpec6 = (OptionSpec<Void>)optionParser2.accepts("bonusChest");
        final OptionSpec<Void> optionSpec7 = (OptionSpec<Void>)optionParser2.accepts("forceUpgrade");
        final OptionSpec<Void> optionSpec8 = (OptionSpec<Void>)optionParser2.accepts("eraseCache");
        final OptionSpec<Void> optionSpec9 = (OptionSpec<Void>)optionParser2.accepts("help").forHelp();
        final OptionSpec<String> optionSpec10 = (OptionSpec<String>)optionParser2.accepts("singleplayer").withRequiredArg();
        final OptionSpec<String> optionSpec11 = (OptionSpec<String>)optionParser2.accepts("universe").withRequiredArg().defaultsTo(".", (Object[])new String[0]);
        final OptionSpec<String> optionSpec12 = (OptionSpec<String>)optionParser2.accepts("world").withRequiredArg();
        final OptionSpec<Integer> optionSpec13 = (OptionSpec<Integer>)optionParser2.accepts("port").withRequiredArg().ofType((Class)Integer.class).defaultsTo((-1), (Object[])new Integer[0]);
        final OptionSpec<String> optionSpec14 = (OptionSpec<String>)optionParser2.accepts("serverId").withRequiredArg();
        final OptionSpec<String> optionSpec15 = (OptionSpec<String>)optionParser2.nonOptions();
        try {
            final OptionSet optionSet16 = optionParser2.parse(arr);
            if (optionSet16.has((OptionSpec)optionSpec9)) {
                optionParser2.printHelpOn((OutputStream)System.err);
                return;
            }
            final Path path17 = Paths.get("server.properties", new String[0]);
            final DedicatedServerSettings um18 = new DedicatedServerSettings(path17);
            um18.forceSave();
            final Path path18 = Paths.get("eula.txt", new String[0]);
            final Eula rc20 = new Eula(path18);
            if (optionSet16.has((OptionSpec)optionSpec4)) {
                MinecraftServer.LOGGER.info("Initialized '" + path17.toAbsolutePath().toString() + "' and '" + path18.toAbsolutePath().toString() + "'");
                return;
            }
            if (!rc20.hasAgreedToEULA()) {
                MinecraftServer.LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
                return;
            }
            Bootstrap.bootStrap();
            Bootstrap.validate();
            final String string21 = (String)optionSet16.valueOf((OptionSpec)optionSpec11);
            final YggdrasilAuthenticationService yggdrasilAuthenticationService22 = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
            final MinecraftSessionService minecraftSessionService23 = yggdrasilAuthenticationService22.createMinecraftSessionService();
            final GameProfileRepository gameProfileRepository24 = yggdrasilAuthenticationService22.createProfileRepository();
            final GameProfileCache xr25 = new GameProfileCache(gameProfileRepository24, new File(string21, MinecraftServer.USERID_CACHE_FILE.getName()));
            final String string22 = (String)Optional.ofNullable(optionSet16.valueOf((OptionSpec)optionSpec12)).orElse(um18.getProperties().levelName);
            final DedicatedServer uk27 = new DedicatedServer(new File(string21), um18, DataFixers.getDataFixer(), yggdrasilAuthenticationService22, minecraftSessionService23, gameProfileRepository24, xr25, LoggerChunkProgressListener::new, string22);
            uk27.setSingleplayerName((String)optionSet16.valueOf((OptionSpec)optionSpec10));
            uk27.setPort((int)optionSet16.valueOf((OptionSpec)optionSpec13));
            uk27.setDemo(optionSet16.has((OptionSpec)optionSpec5));
            uk27.setBonusChest(optionSet16.has((OptionSpec)optionSpec6));
            uk27.forceUpgrade(optionSet16.has((OptionSpec)optionSpec7));
            uk27.eraseCache(optionSet16.has((OptionSpec)optionSpec8));
            uk27.setId((String)optionSet16.valueOf((OptionSpec)optionSpec14));
            final boolean boolean28 = !optionSet16.has((OptionSpec)optionSpec3) && !optionSet16.valuesOf((OptionSpec)optionSpec15).contains("nogui");
            if (boolean28 && !GraphicsEnvironment.isHeadless()) {
                uk27.showGui();
            }
            uk27.forkAndRun();
            final Thread thread29 = new Thread("Server Shutdown Thread") {
                public void run() {
                    uk27.halt(true);
                }
            };
            thread29.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(MinecraftServer.LOGGER));
            Runtime.getRuntime().addShutdownHook(thread29);
        }
        catch (Exception exception16) {
            MinecraftServer.LOGGER.fatal("Failed to start the minecraft server", (Throwable)exception16);
        }
    }
    
    protected void setId(final String string) {
        this.serverId = string;
    }
    
    protected void forceUpgrade(final boolean boolean1) {
        this.forceUpgrade = boolean1;
    }
    
    protected void eraseCache(final boolean boolean1) {
        this.eraseCache = boolean1;
    }
    
    public void forkAndRun() {
        this.serverThread.start();
    }
    
    public boolean isShutdown() {
        return !this.serverThread.isAlive();
    }
    
    public File getFile(final String string) {
        return new File(this.getServerDirectory(), string);
    }
    
    public void info(final String string) {
        MinecraftServer.LOGGER.info(string);
    }
    
    public void warn(final String string) {
        MinecraftServer.LOGGER.warn(string);
    }
    
    public ServerLevel getLevel(final DimensionType byn) {
        return (ServerLevel)this.levels.get(byn);
    }
    
    public Iterable<ServerLevel> getAllLevels() {
        return (Iterable<ServerLevel>)this.levels.values();
    }
    
    public String getServerVersion() {
        return SharedConstants.getCurrentVersion().getName();
    }
    
    public int getPlayerCount() {
        return this.playerList.getPlayerCount();
    }
    
    public int getMaxPlayers() {
        return this.playerList.getMaxPlayers();
    }
    
    public String[] getPlayerNames() {
        return this.playerList.getPlayerNamesArray();
    }
    
    public boolean isDebugging() {
        return false;
    }
    
    public void error(final String string) {
        MinecraftServer.LOGGER.error(string);
    }
    
    public void debug(final String string) {
        if (this.isDebugging()) {
            MinecraftServer.LOGGER.info(string);
        }
    }
    
    public String getServerModName() {
        return "vanilla";
    }
    
    public CrashReport fillReport(final CrashReport d) {
        if (this.playerList != null) {
            d.getSystemDetails().setDetail("Player Count", (CrashReportDetail<String>)(() -> new StringBuilder().append(this.playerList.getPlayerCount()).append(" / ").append(this.playerList.getMaxPlayers()).append("; ").append(this.playerList.getPlayers()).toString()));
        }
        d.getSystemDetails().setDetail("Data Packs", (CrashReportDetail<String>)(() -> {
            final StringBuilder stringBuilder2 = new StringBuilder();
            for (final UnopenedPack xa4 : this.packRepository.getSelected()) {
                if (stringBuilder2.length() > 0) {
                    stringBuilder2.append(", ");
                }
                stringBuilder2.append(xa4.getId());
                if (!xa4.getCompatibility().isCompatible()) {
                    stringBuilder2.append(" (incompatible)");
                }
            }
            return stringBuilder2.toString();
        }));
        if (this.serverId != null) {
            d.getSystemDetails().setDetail("Server Id", (CrashReportDetail<String>)(() -> this.serverId));
        }
        return d;
    }
    
    public boolean isInitialized() {
        return this.universe != null;
    }
    
    @Override
    public void sendMessage(final Component jo) {
        MinecraftServer.LOGGER.info(jo.getString());
    }
    
    public KeyPair getKeyPair() {
        return this.keyPair;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int integer) {
        this.port = integer;
    }
    
    public String getSingleplayerName() {
        return this.singleplayerName;
    }
    
    public void setSingleplayerName(final String string) {
        this.singleplayerName = string;
    }
    
    public boolean isSingleplayer() {
        return this.singleplayerName != null;
    }
    
    public String getLevelIdName() {
        return this.levelIdName;
    }
    
    public void setLevelName(final String string) {
        this.levelName = string;
    }
    
    public String getLevelName() {
        return this.levelName;
    }
    
    public void setKeyPair(final KeyPair keyPair) {
        this.keyPair = keyPair;
    }
    
    public void setDifficulty(final Difficulty ahg, final boolean boolean2) {
        for (final ServerLevel vk5 : this.getAllLevels()) {
            final LevelData com6 = vk5.getLevelData();
            if (!boolean2 && com6.isDifficultyLocked()) {
                continue;
            }
            if (com6.isHardcore()) {
                com6.setDifficulty(Difficulty.HARD);
                vk5.setSpawnSettings(true, true);
            }
            else if (this.isSingleplayer()) {
                com6.setDifficulty(ahg);
                vk5.setSpawnSettings(vk5.getDifficulty() != Difficulty.PEACEFUL, true);
            }
            else {
                com6.setDifficulty(ahg);
                vk5.setSpawnSettings(this.getSpawnMonsters(), this.animals);
            }
        }
        this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
    }
    
    public void setDifficultyLocked(final boolean boolean1) {
        for (final ServerLevel vk4 : this.getAllLevels()) {
            final LevelData com5 = vk4.getLevelData();
            com5.setDifficultyLocked(boolean1);
        }
        this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
    }
    
    private void sendDifficultyUpdate(final ServerPlayer vl) {
        final LevelData com3 = vl.getLevel().getLevelData();
        vl.connection.send(new ClientboundChangeDifficultyPacket(com3.getDifficulty(), com3.isDifficultyLocked()));
    }
    
    protected boolean getSpawnMonsters() {
        return true;
    }
    
    public boolean isDemo() {
        return this.isDemo;
    }
    
    public void setDemo(final boolean boolean1) {
        this.isDemo = boolean1;
    }
    
    public void setBonusChest(final boolean boolean1) {
        this.levelHasStartingBonusChest = boolean1;
    }
    
    public LevelStorageSource getStorageSource() {
        return this.storageSource;
    }
    
    public String getResourcePack() {
        return this.resourcePack;
    }
    
    public String getResourcePackHash() {
        return this.resourcePackHash;
    }
    
    public void setResourcePack(final String string1, final String string2) {
        this.resourcePack = string1;
        this.resourcePackHash = string2;
    }
    
    @Override
    public void populateSnooper(final Snooper ahq) {
        ahq.setDynamicData("whitelist_enabled", false);
        ahq.setDynamicData("whitelist_count", 0);
        if (this.playerList != null) {
            ahq.setDynamicData("players_current", this.getPlayerCount());
            ahq.setDynamicData("players_max", this.getMaxPlayers());
            ahq.setDynamicData("players_seen", this.getLevel(DimensionType.OVERWORLD).getLevelStorage().getSeenPlayers().length);
        }
        ahq.setDynamicData("uses_auth", this.onlineMode);
        ahq.setDynamicData("gui_state", this.hasGui() ? "enabled" : "disabled");
        ahq.setDynamicData("run_time", ((Util.getMillis() - ahq.getStartupTime()) / 60L * 1000L));
        ahq.setDynamicData("avg_tick_ms", (int)(Mth.average(this.tickTimes) * 1.0E-6));
        int integer3 = 0;
        for (final ServerLevel vk5 : this.getAllLevels()) {
            if (vk5 != null) {
                final LevelData com6 = vk5.getLevelData();
                ahq.setDynamicData(new StringBuilder().append("world[").append(integer3).append("][dimension]").toString(), vk5.dimension.getType());
                ahq.setDynamicData(new StringBuilder().append("world[").append(integer3).append("][mode]").toString(), com6.getGameType());
                ahq.setDynamicData(new StringBuilder().append("world[").append(integer3).append("][difficulty]").toString(), vk5.getDifficulty());
                ahq.setDynamicData(new StringBuilder().append("world[").append(integer3).append("][hardcore]").toString(), com6.isHardcore());
                ahq.setDynamicData(new StringBuilder().append("world[").append(integer3).append("][generator_name]").toString(), com6.getGeneratorType().getName());
                ahq.setDynamicData(new StringBuilder().append("world[").append(integer3).append("][generator_version]").toString(), com6.getGeneratorType().getVersion());
                ahq.setDynamicData(new StringBuilder().append("world[").append(integer3).append("][height]").toString(), this.maxBuildHeight);
                ahq.setDynamicData(new StringBuilder().append("world[").append(integer3).append("][chunks_loaded]").toString(), vk5.getChunkSource().getLoadedChunksCount());
                ++integer3;
            }
        }
        ahq.setDynamicData("worlds", integer3);
    }
    
    public abstract boolean isDedicatedServer();
    
    public boolean usesAuthentication() {
        return this.onlineMode;
    }
    
    public void setUsesAuthentication(final boolean boolean1) {
        this.onlineMode = boolean1;
    }
    
    public boolean getPreventProxyConnections() {
        return this.preventProxyConnections;
    }
    
    public void setPreventProxyConnections(final boolean boolean1) {
        this.preventProxyConnections = boolean1;
    }
    
    public boolean isAnimals() {
        return this.animals;
    }
    
    public void setAnimals(final boolean boolean1) {
        this.animals = boolean1;
    }
    
    public boolean isNpcsEnabled() {
        return this.npcs;
    }
    
    public abstract boolean isEpollEnabled();
    
    public void setNpcsEnabled(final boolean boolean1) {
        this.npcs = boolean1;
    }
    
    public boolean isPvpAllowed() {
        return this.pvp;
    }
    
    public void setPvpAllowed(final boolean boolean1) {
        this.pvp = boolean1;
    }
    
    public boolean isFlightAllowed() {
        return this.allowFlight;
    }
    
    public void setFlightAllowed(final boolean boolean1) {
        this.allowFlight = boolean1;
    }
    
    public abstract boolean isCommandBlockEnabled();
    
    public String getMotd() {
        return this.motd;
    }
    
    public void setMotd(final String string) {
        this.motd = string;
    }
    
    public int getMaxBuildHeight() {
        return this.maxBuildHeight;
    }
    
    public void setMaxBuildHeight(final int integer) {
        this.maxBuildHeight = integer;
    }
    
    public boolean isStopped() {
        return this.stopped;
    }
    
    public PlayerList getPlayerList() {
        return this.playerList;
    }
    
    public void setPlayerList(final PlayerList xv) {
        this.playerList = xv;
    }
    
    public abstract boolean isPublished();
    
    public void setDefaultGameMode(final GameType bho) {
        for (final ServerLevel vk4 : this.getAllLevels()) {
            vk4.getLevelData().setGameType(bho);
        }
    }
    
    @Nullable
    public ServerConnectionListener getConnection() {
        return this.connection;
    }
    
    public boolean isReady() {
        return this.isReady;
    }
    
    public boolean hasGui() {
        return false;
    }
    
    public abstract boolean publishServer(final GameType bho, final boolean boolean2, final int integer);
    
    public int getTickCount() {
        return this.tickCount;
    }
    
    public void delayStartProfiler() {
        this.delayProfilerStart = true;
    }
    
    public Snooper getSnooper() {
        return this.snooper;
    }
    
    public int getSpawnProtectionRadius() {
        return 16;
    }
    
    public boolean isUnderSpawnProtection(final Level bhr, final BlockPos ew, final Player awg) {
        return false;
    }
    
    public void setForceGameType(final boolean boolean1) {
        this.forceGameType = boolean1;
    }
    
    public boolean getForceGameType() {
        return this.forceGameType;
    }
    
    public int getPlayerIdleTimeout() {
        return this.playerIdleTimeout;
    }
    
    public void setPlayerIdleTimeout(final int integer) {
        this.playerIdleTimeout = integer;
    }
    
    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }
    
    public GameProfileRepository getProfileRepository() {
        return this.profileRepository;
    }
    
    public GameProfileCache getProfileCache() {
        return this.profileCache;
    }
    
    public ServerStatus getStatus() {
        return this.status;
    }
    
    public void invalidateStatus() {
        this.lastServerStatus = 0L;
    }
    
    public int getAbsoluteMaxWorldSize() {
        return 29999984;
    }
    
    public boolean scheduleExecutables() {
        return super.scheduleExecutables() && !this.isStopped();
    }
    
    public Thread getRunningThread() {
        return this.serverThread;
    }
    
    public int getCompressionThreshold() {
        return 256;
    }
    
    public long getNextTickTime() {
        return this.nextTickTime;
    }
    
    public DataFixer getFixerUpper() {
        return this.fixerUpper;
    }
    
    public int getSpawnRadius(@Nullable final ServerLevel vk) {
        if (vk != null) {
            return vk.getGameRules().getInt(GameRules.RULE_SPAWN_RADIUS);
        }
        return 10;
    }
    
    public ServerAdvancementManager getAdvancements() {
        return this.advancements;
    }
    
    public ServerFunctionManager getFunctions() {
        return this.functions;
    }
    
    public void reloadResources() {
        if (!this.isSameThread()) {
            this.execute(this::reloadResources);
            return;
        }
        this.getPlayerList().saveAll();
        this.packRepository.reload();
        this.updateSelectedPacks(this.getLevel(DimensionType.OVERWORLD).getLevelData());
        this.getPlayerList().reloadResources();
    }
    
    private void updateSelectedPacks(final LevelData com) {
        final List<UnopenedPack> list3 = (List<UnopenedPack>)Lists.newArrayList((Iterable)this.packRepository.getSelected());
        for (final UnopenedPack xa5 : this.packRepository.getAvailable()) {
            if (!com.getDisabledDataPacks().contains(xa5.getId()) && !list3.contains(xa5)) {
                MinecraftServer.LOGGER.info("Found new data pack {}, loading it automatically", xa5.getId());
                xa5.getDefaultPosition().<UnopenedPack, UnopenedPack>insert(list3, xa5, (java.util.function.Function<UnopenedPack, UnopenedPack>)(xa -> xa), false);
            }
        }
        this.packRepository.setSelected((java.util.Collection<UnopenedPack>)list3);
        final List<Pack> list4 = (List<Pack>)Lists.newArrayList();
        this.packRepository.getSelected().forEach(xa -> list4.add(xa.open()));
        final CompletableFuture<Unit> completableFuture5 = this.resources.reload(this.executor, (Executor)this, list4, MinecraftServer.DATA_RELOAD_INITIAL_TASK);
        this.managedBlock(completableFuture5::isDone);
        try {
            completableFuture5.get();
        }
        catch (Exception exception6) {
            MinecraftServer.LOGGER.error("Failed to reload data packs", (Throwable)exception6);
        }
        com.getEnabledDataPacks().clear();
        com.getDisabledDataPacks().clear();
        this.packRepository.getSelected().forEach(xa -> com.getEnabledDataPacks().add(xa.getId()));
        this.packRepository.getAvailable().forEach(xa -> {
            if (!this.packRepository.getSelected().contains(xa)) {
                com.getDisabledDataPacks().add(xa.getId());
            }
        });
    }
    
    public void kickUnlistedPlayers(final CommandSourceStack cd) {
        if (!this.isEnforceWhitelist()) {
            return;
        }
        final PlayerList xv3 = cd.getServer().getPlayerList();
        final UserWhiteList yc4 = xv3.getWhiteList();
        if (!yc4.isEnabled()) {
            return;
        }
        final List<ServerPlayer> list5 = (List<ServerPlayer>)Lists.newArrayList((Iterable)xv3.getPlayers());
        for (final ServerPlayer vl7 : list5) {
            if (!yc4.isWhiteListed(vl7.getGameProfile())) {
                vl7.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.not_whitelisted", new Object[0]));
            }
        }
    }
    
    public ReloadableResourceManager getResources() {
        return this.resources;
    }
    
    public PackRepository<UnopenedPack> getPackRepository() {
        return this.packRepository;
    }
    
    public Commands getCommands() {
        return this.commands;
    }
    
    public CommandSourceStack createCommandSourceStack() {
        return new CommandSourceStack((CommandSource)this, (this.getLevel(DimensionType.OVERWORLD) == null) ? Vec3.ZERO : new Vec3(this.getLevel(DimensionType.OVERWORLD).getSharedSpawnPos()), Vec2.ZERO, this.getLevel(DimensionType.OVERWORLD), 4, "Server", (Component)new TextComponent("Server"), this, (Entity)null);
    }
    
    @Override
    public boolean acceptsSuccess() {
        return true;
    }
    
    @Override
    public boolean acceptsFailure() {
        return true;
    }
    
    public RecipeManager getRecipeManager() {
        return this.recipes;
    }
    
    public TagManager getTags() {
        return this.tags;
    }
    
    public ServerScoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public LootTables getLootTables() {
        return this.lootTables;
    }
    
    public GameRules getGameRules() {
        return this.getLevel(DimensionType.OVERWORLD).getGameRules();
    }
    
    public CustomBossEvents getCustomBossEvents() {
        return this.customBossEvents;
    }
    
    public boolean isEnforceWhitelist() {
        return this.enforceWhitelist;
    }
    
    public void setEnforceWhitelist(final boolean boolean1) {
        this.enforceWhitelist = boolean1;
    }
    
    public float getAverageTickTime() {
        return this.averageTickTime;
    }
    
    public int getProfilePermissions(final GameProfile gameProfile) {
        if (!this.getPlayerList().isOp(gameProfile)) {
            return 0;
        }
        final ServerOpListEntry xx3 = this.getPlayerList().getOps().get(gameProfile);
        if (xx3 != null) {
            return xx3.getLevel();
        }
        if (this.isSingleplayerOwner(gameProfile)) {
            return 4;
        }
        if (this.isSingleplayer()) {
            return this.getPlayerList().isAllowCheatsForAllPlayers() ? 4 : 0;
        }
        return this.getOperatorUserPermissionLevel();
    }
    
    public FrameTimer getFrameTimer() {
        return this.frameTimer;
    }
    
    public GameProfiler getProfiler() {
        return this.profiler;
    }
    
    public Executor getBackgroundTaskExecutor() {
        return this.executor;
    }
    
    public abstract boolean isSingleplayerOwner(final GameProfile gameProfile);
    
    public void saveDebugReport(final Path path) throws IOException {
        final Path path2 = path.resolve("levels");
        for (final Map.Entry<DimensionType, ServerLevel> entry5 : this.levels.entrySet()) {
            final ResourceLocation qv6 = DimensionType.getName((DimensionType)entry5.getKey());
            final Path path3 = path2.resolve(qv6.getNamespace()).resolve(qv6.getPath());
            Files.createDirectories(path3, new FileAttribute[0]);
            ((ServerLevel)entry5.getValue()).saveDebugReport(path3);
        }
        this.dumpGameRules(path.resolve("gamerules.txt"));
        this.dumpClasspath(path.resolve("classpath.txt"));
        this.dumpCrashCategory(path.resolve("example_crash.txt"));
        this.dumpMiscStats(path.resolve("stats.txt"));
        this.dumpThreads(path.resolve("threads.txt"));
    }
    
    private void dumpMiscStats(final Path path) throws IOException {
        try (final Writer writer3 = (Writer)Files.newBufferedWriter(path, new OpenOption[0])) {
            writer3.write(String.format("pending_tasks: %d\n", new Object[] { this.getPendingTasksCount() }));
            writer3.write(String.format("average_tick_time: %f\n", new Object[] { this.getAverageTickTime() }));
            writer3.write(String.format("tick_times: %s\n", new Object[] { Arrays.toString(this.tickTimes) }));
            writer3.write(String.format("queue: %s\n", new Object[] { Util.backgroundExecutor() }));
        }
    }
    
    private void dumpCrashCategory(final Path path) throws IOException {
        final CrashReport d3 = new CrashReport("Server dump", (Throwable)new Exception("dummy"));
        this.fillReport(d3);
        try (final Writer writer4 = (Writer)Files.newBufferedWriter(path, new OpenOption[0])) {
            writer4.write(d3.getFriendlyReport());
        }
    }
    
    private void dumpGameRules(final Path path) throws IOException {
        try (final Writer writer3 = (Writer)Files.newBufferedWriter(path, new OpenOption[0])) {
            final List<String> list5 = (List<String>)Lists.newArrayList();
            final GameRules bhn6 = this.getGameRules();
            GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
                public <T extends GameRules.Value<T>> void visit(final GameRules.Key<T> d, final GameRules.Type<T> e) {
                    list5.add(String.format("%s=%s\n", new Object[] { d.getId(), bhn6.<T>getRule(d).toString() }));
                }
            });
            for (final String string8 : list5) {
                writer3.write(string8);
            }
        }
    }
    
    private void dumpClasspath(final Path path) throws IOException {
        try (final Writer writer3 = (Writer)Files.newBufferedWriter(path, new OpenOption[0])) {
            final String string5 = System.getProperty("java.class.path");
            final String string6 = System.getProperty("path.separator");
            for (final String string7 : Splitter.on(string6).split((CharSequence)string5)) {
                writer3.write(string7);
                writer3.write("\n");
            }
        }
    }
    
    private void dumpThreads(final Path path) throws IOException {
        final ThreadMXBean threadMXBean3 = ManagementFactory.getThreadMXBean();
        final ThreadInfo[] arr4 = threadMXBean3.dumpAllThreads(true, true);
        Arrays.sort((Object[])arr4, Comparator.comparing(ThreadInfo::getThreadName));
        try (final Writer writer5 = (Writer)Files.newBufferedWriter(path, new OpenOption[0])) {
            for (final ThreadInfo threadInfo10 : arr4) {
                writer5.write(threadInfo10.toString());
                writer5.write(10);
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        USERID_CACHE_FILE = new File("usercache.json");
        DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
        DEMO_SETTINGS = new LevelSettings("North Carolina".hashCode(), GameType.SURVIVAL, true, false, LevelType.NORMAL).enableStartingBonusItems();
    }
}
