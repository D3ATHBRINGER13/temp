package net.minecraft.client;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.resources.LegacyResourcePackAdapter;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.crafting.Recipe;
import java.util.concurrent.CompletionStage;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.level.dimension.NetherDimension;
import net.minecraft.client.gui.screens.WinScreen;
import com.mojang.authlib.GameProfile;
import com.google.common.collect.Multimap;
import java.nio.IntBuffer;
import java.nio.ByteOrder;
import net.minecraft.CrashReportDetail;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.multiplayer.ClientPacketListener;
import com.mojang.authlib.AuthenticationService;
import net.minecraft.client.gui.screens.ProgressScreen;
import java.net.SocketAddress;
import com.mojang.authlib.GameProfileRepository;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ProcessorChunkProgressListener;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.CrashReportCategory;
import net.minecraft.world.Difficulty;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.client.gui.screens.PauseScreen;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.text.DecimalFormatSymbols;
import java.text.DecimalFormat;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.gui.screens.MenuScreens;
import java.util.Locale;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import java.util.Date;
import java.text.SimpleDateFormat;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.core.NonNullList;
import net.minecraft.client.searchtree.ReloadableIdSearchTree;
import java.util.stream.Stream;
import java.util.function.Function;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.searchtree.ReloadableSearchTree;
import java.util.Iterator;
import java.io.InputStream;
import java.util.function.LongSupplier;
import java.util.concurrent.Executor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.texture.TickableTextureObject;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import java.util.stream.Collectors;
import net.minecraft.server.packs.repository.UnopenedPack;
import java.util.List;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import com.mojang.blaze3d.platform.GlDebug;
import java.io.IOException;
import net.minecraft.server.packs.PackType;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.ReportedException;
import net.minecraft.client.gui.screens.OutOfMemoryScreen;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.server.Bootstrap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.util.UUID;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.Pack;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import com.google.common.collect.Queues;
import net.minecraft.Util;
import net.minecraft.client.main.GameConfig;
import java.util.Queue;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.SkinManager;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.resources.UnopenedResourcePack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.profiling.GameProfiler;
import net.minecraft.network.Connection;
import net.minecraft.util.FrameTimer;
import net.minecraft.world.level.storage.LevelStorageSource;
import java.net.Proxy;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.gui.Gui;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.particle.ParticleEngine;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.world.Snooper;
import net.minecraft.CrashReport;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import com.mojang.datafixers.DataFixer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.multiplayer.ServerData;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import net.minecraft.util.Unit;
import java.util.concurrent.CompletableFuture;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;
import com.mojang.blaze3d.platform.WindowEventHandler;
import net.minecraft.world.SnooperPopulator;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;

public class Minecraft extends ReentrantBlockableEventLoop<Runnable> implements SnooperPopulator, WindowEventHandler, AutoCloseable {
    private static final Logger LOGGER;
    public static final boolean ON_OSX;
    public static final ResourceLocation DEFAULT_FONT;
    public static final ResourceLocation ALT_FONT;
    private static final CompletableFuture<Unit> RESOURCE_RELOAD_INITIAL_TASK;
    public static byte[] reserve;
    private static int MAX_SUPPORTED_TEXTURE_SIZE;
    private final File resourcePackDirectory;
    private final PropertyMap profileProperties;
    private final DisplayData displayData;
    private ServerData currentServer;
    private TextureManager textureManager;
    private static Minecraft instance;
    private final DataFixer fixerUpper;
    public MultiPlayerGameMode gameMode;
    private VirtualScreen virtualScreen;
    public Window window;
    private boolean hasCrashed;
    private CrashReport delayedCrash;
    private boolean connectedToRealms;
    private final Timer timer;
    private final Snooper snooper;
    public MultiPlayerLevel level;
    public LevelRenderer levelRenderer;
    private EntityRenderDispatcher entityRenderDispatcher;
    private ItemRenderer itemRenderer;
    private ItemInHandRenderer itemInHandRenderer;
    public LocalPlayer player;
    @Nullable
    public Entity cameraEntity;
    @Nullable
    public Entity crosshairPickEntity;
    public ParticleEngine particleEngine;
    private final SearchRegistry searchRegistry;
    private final User user;
    private boolean pause;
    private float pausePartialTick;
    public Font font;
    @Nullable
    public Screen screen;
    @Nullable
    public Overlay overlay;
    public GameRenderer gameRenderer;
    public DebugRenderer debugRenderer;
    protected int missTime;
    @Nullable
    private IntegratedServer singleplayerServer;
    private final AtomicReference<StoringChunkProgressListener> progressListener;
    public Gui gui;
    public boolean noRender;
    public HitResult hitResult;
    public Options options;
    private HotbarManager hotbarManager;
    public MouseHandler mouseHandler;
    public KeyboardHandler keyboardHandler;
    public final File gameDirectory;
    private final File assetsDirectory;
    private final String launchedVersion;
    private final String versionType;
    private final Proxy proxy;
    private LevelStorageSource levelSource;
    private static int fps;
    private int rightClickDelay;
    private String connectToIp;
    private int connectToPort;
    public final FrameTimer frameTimer;
    private long lastNanoTime;
    private final boolean is64bit;
    private final boolean demo;
    @Nullable
    private Connection pendingConnection;
    private boolean isLocalServer;
    private final GameProfiler profiler;
    private ReloadableResourceManager resourceManager;
    private final ClientPackSource clientPackSource;
    private final PackRepository<UnopenedResourcePack> resourcePackRepository;
    private LanguageManager languageManager;
    private BlockColors blockColors;
    private ItemColors itemColors;
    private RenderTarget mainRenderTarget;
    private TextureAtlas textureAtlas;
    private SoundManager soundManager;
    private MusicManager musicManager;
    private FontManager fontManager;
    private SplashManager splashManager;
    private final MinecraftSessionService minecraftSessionService;
    private SkinManager skinManager;
    private final Thread gameThread;
    private ModelManager modelManager;
    private BlockRenderDispatcher blockRenderer;
    private PaintingTextureManager paintingTextures;
    private MobEffectTextureManager mobEffectTextures;
    private final ToastComponent toast;
    private final Game game;
    private volatile boolean running;
    public String fpsString;
    public boolean smartCull;
    private long lastTime;
    private int frames;
    private final Tutorial tutorial;
    private boolean windowActive;
    private final Queue<Runnable> progressTasks;
    private CompletableFuture<Void> pendingReload;
    private String debugPath;
    
    public Minecraft(final GameConfig dgh) {
        super("Client");
        this.timer = new Timer(20.0f, 0L);
        this.snooper = new Snooper("client", (SnooperPopulator)this, Util.getMillis());
        this.searchRegistry = new SearchRegistry();
        this.progressListener = (AtomicReference<StoringChunkProgressListener>)new AtomicReference();
        this.frameTimer = new FrameTimer();
        this.lastNanoTime = Util.getNanos();
        this.profiler = new GameProfiler(() -> this.timer.ticks);
        this.gameThread = Thread.currentThread();
        this.game = new Game(this);
        this.running = true;
        this.fpsString = "";
        this.smartCull = true;
        this.progressTasks = (Queue<Runnable>)Queues.newConcurrentLinkedQueue();
        this.debugPath = "root";
        this.displayData = dgh.display;
        Minecraft.instance = this;
        this.gameDirectory = dgh.location.gameDirectory;
        this.assetsDirectory = dgh.location.assetDirectory;
        this.resourcePackDirectory = dgh.location.resourcePackDirectory;
        this.launchedVersion = dgh.game.launchVersion;
        this.versionType = dgh.game.versionType;
        this.profileProperties = dgh.user.profileProperties;
        this.clientPackSource = new ClientPackSource(new File(this.gameDirectory, "server-resource-packs"), dgh.location.getAssetIndex());
        Supplier supplier2;
        (this.resourcePackRepository = new PackRepository<UnopenedResourcePack>((string, boolean2, supplier, wl, wq, a) -> {
            if (wq.getPackFormat() < SharedConstants.getCurrentVersion().getPackVersion()) {
                supplier2 = (() -> new LegacyResourcePackAdapter((Pack)supplier.get(), LegacyResourcePackAdapter.V3));
            }
            else {
                supplier2 = supplier;
            }
            return new UnopenedResourcePack(string, boolean2, (Supplier<Pack>)supplier2, wl, wq, a);
        })).addSource(this.clientPackSource);
        this.resourcePackRepository.addSource(new FolderRepositorySource(this.resourcePackDirectory));
        this.proxy = ((dgh.user.proxy == null) ? Proxy.NO_PROXY : dgh.user.proxy);
        this.minecraftSessionService = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
        this.user = dgh.user.user;
        Minecraft.LOGGER.info("Setting user: {}", this.user.getName());
        Minecraft.LOGGER.debug("(Session ID is {})", this.user.getSessionId());
        this.demo = dgh.game.demo;
        this.is64bit = checkIs64Bit();
        this.singleplayerServer = null;
        if (dgh.server.hostname != null) {
            this.connectToIp = dgh.server.hostname;
            this.connectToPort = dgh.server.port;
        }
        Bootstrap.bootStrap();
        Bootstrap.validate();
        KeybindComponent.keyResolver = (Function<String, Supplier<String>>)KeyMapping::createNameSupplier;
        this.fixerUpper = DataFixers.getDataFixer();
        this.toast = new ToastComponent(this);
        this.tutorial = new Tutorial(this);
    }
    
    public void run() {
        this.running = true;
        try {
            this.init();
        }
        catch (Throwable throwable2) {
            final CrashReport d3 = CrashReport.forThrowable(throwable2, "Initializing game");
            d3.addCategory("Initialization");
            this.crash(this.fillReport(d3));
            return;
        }
        try {
            boolean boolean2 = false;
            while (this.running) {
                if (this.hasCrashed && this.delayedCrash != null) {
                    this.crash(this.delayedCrash);
                    return;
                }
                try {
                    this.runTick(!boolean2);
                }
                catch (OutOfMemoryError outOfMemoryError3) {
                    if (boolean2) {
                        throw outOfMemoryError3;
                    }
                    this.emergencySave();
                    this.setScreen(new OutOfMemoryScreen());
                    System.gc();
                    Minecraft.LOGGER.fatal("Out of memory", (Throwable)outOfMemoryError3);
                    boolean2 = true;
                }
            }
        }
        catch (ReportedException m2) {
            this.fillReport(m2.getReport());
            this.emergencySave();
            Minecraft.LOGGER.fatal("Reported exception thrown!", (Throwable)m2);
            this.crash(m2.getReport());
        }
        catch (Throwable throwable2) {
            final CrashReport d3 = this.fillReport(new CrashReport("Unexpected error", throwable2));
            Minecraft.LOGGER.fatal("Unreported exception thrown!", throwable2);
            this.emergencySave();
            this.crash(d3);
        }
        finally {
            this.destroy();
        }
    }
    
    private void init() {
        this.options = new Options(this, this.gameDirectory);
        this.hotbarManager = new HotbarManager(this.gameDirectory, this.fixerUpper);
        this.startTimerHackThread();
        Minecraft.LOGGER.info("LWJGL Version: {}", GLX.getLWJGLVersion());
        DisplayData cuc2 = this.displayData;
        if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
            cuc2 = new DisplayData(this.options.overrideWidth, this.options.overrideHeight, cuc2.fullscreenWidth, cuc2.fullscreenHeight, cuc2.isFullscreen);
        }
        final LongSupplier longSupplier3 = GLX.initGlfw();
        if (longSupplier3 != null) {
            Util.timeSource = longSupplier3;
        }
        this.virtualScreen = new VirtualScreen(this);
        this.window = this.virtualScreen.newWindow(cuc2, this.options.fullscreenVideoModeString, "Minecraft " + SharedConstants.getCurrentVersion().getName());
        this.setWindowActive(true);
        try {
            final InputStream inputStream4 = this.getClientPackSource().getVanillaPack().getResource(PackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_16x16.png"));
            final InputStream inputStream5 = this.getClientPackSource().getVanillaPack().getResource(PackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_32x32.png"));
            this.window.setIcon(inputStream4, inputStream5);
        }
        catch (IOException iOException4) {
            Minecraft.LOGGER.error("Couldn't set icon", (Throwable)iOException4);
        }
        this.window.setFramerateLimit(this.options.framerateLimit);
        (this.mouseHandler = new MouseHandler(this)).setup(this.window.getWindow());
        (this.keyboardHandler = new KeyboardHandler(this)).setup(this.window.getWindow());
        GLX.init();
        GlDebug.enableDebugCallback(this.options.glDebugVerbosity, false);
        (this.mainRenderTarget = new RenderTarget(this.window.getWidth(), this.window.getHeight(), true, Minecraft.ON_OSX)).setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.resourceManager = new SimpleReloadableResourceManager(PackType.CLIENT_RESOURCES, this.gameThread);
        this.options.loadResourcePacks(this.resourcePackRepository);
        this.resourcePackRepository.reload();
        final List<Pack> list4 = (List<Pack>)this.resourcePackRepository.getSelected().stream().map(UnopenedPack::open).collect(Collectors.toList());
        for (final Pack wl6 : list4) {
            this.resourceManager.add(wl6);
        }
        this.languageManager = new LanguageManager(this.options.languageCode);
        this.resourceManager.registerReloadListener(this.languageManager);
        this.languageManager.reload(list4);
        this.textureManager = new TextureManager(this.resourceManager);
        this.resourceManager.registerReloadListener(this.textureManager);
        this.resizeDisplay();
        this.skinManager = new SkinManager(this.textureManager, new File(this.assetsDirectory, "skins"), this.minecraftSessionService);
        this.levelSource = new LevelStorageSource(this.gameDirectory.toPath().resolve("saves"), this.gameDirectory.toPath().resolve("backups"), this.fixerUpper);
        this.soundManager = new SoundManager(this.resourceManager, this.options);
        this.resourceManager.registerReloadListener(this.soundManager);
        this.splashManager = new SplashManager(this.user);
        this.resourceManager.registerReloadListener(this.splashManager);
        this.musicManager = new MusicManager(this);
        this.fontManager = new FontManager(this.textureManager, this.isEnforceUnicode());
        this.resourceManager.registerReloadListener(this.fontManager.getReloadListener());
        this.font = this.fontManager.get(Minecraft.DEFAULT_FONT);
        if (this.options.languageCode != null) {
            this.font.setBidirectional(this.languageManager.isBidirectional());
        }
        this.resourceManager.registerReloadListener(new GrassColorReloadListener());
        this.resourceManager.registerReloadListener(new FoliageColorReloadListener());
        this.window.setGlErrorSection("Startup");
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0);
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        this.window.setGlErrorSection("Post startup");
        (this.textureAtlas = new TextureAtlas("textures")).setMaxMipLevel(this.options.mipmapLevels);
        this.textureManager.register(TextureAtlas.LOCATION_BLOCKS, this.textureAtlas);
        this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
        this.textureAtlas.setFilter(false, this.options.mipmapLevels > 0);
        this.blockColors = BlockColors.createDefault();
        this.itemColors = ItemColors.createDefault(this.blockColors);
        this.modelManager = new ModelManager(this.textureAtlas, this.blockColors);
        this.resourceManager.registerReloadListener(this.modelManager);
        this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager, this.itemColors);
        this.entityRenderDispatcher = new EntityRenderDispatcher(this.textureManager, this.itemRenderer, this.resourceManager);
        this.itemInHandRenderer = new ItemInHandRenderer(this);
        this.resourceManager.registerReloadListener(this.itemRenderer);
        this.gameRenderer = new GameRenderer(this, this.resourceManager);
        this.resourceManager.registerReloadListener(this.gameRenderer);
        this.blockRenderer = new BlockRenderDispatcher(this.modelManager.getBlockModelShaper(), this.blockColors);
        this.resourceManager.registerReloadListener(this.blockRenderer);
        this.levelRenderer = new LevelRenderer(this);
        this.resourceManager.registerReloadListener(this.levelRenderer);
        this.createSearchTrees();
        this.resourceManager.registerReloadListener(this.searchRegistry);
        GlStateManager.viewport(0, 0, this.window.getWidth(), this.window.getHeight());
        this.particleEngine = new ParticleEngine(this.level, this.textureManager);
        this.resourceManager.registerReloadListener(this.particleEngine);
        this.paintingTextures = new PaintingTextureManager(this.textureManager);
        this.resourceManager.registerReloadListener(this.paintingTextures);
        this.mobEffectTextures = new MobEffectTextureManager(this.textureManager);
        this.resourceManager.registerReloadListener(this.mobEffectTextures);
        this.gui = new Gui(this);
        this.debugRenderer = new DebugRenderer(this);
        GLX.setGlfwErrorCallback(this::onFullscreenError);
        if (this.options.fullscreen && !this.window.isFullscreen()) {
            this.window.toggleFullScreen();
            this.options.fullscreen = this.window.isFullscreen();
        }
        this.window.updateVsync(this.options.enableVsync);
        this.window.updateRawMouseInput(this.options.rawMouseInput);
        this.window.setDefaultGlErrorCallback();
        if (this.connectToIp != null) {
            this.setScreen(new ConnectScreen(new TitleScreen(), this, this.connectToIp, this.connectToPort));
        }
        else {
            this.setScreen(new TitleScreen(true));
        }
        LoadingOverlay.registerTextures(this);
        this.setOverlay(new LoadingOverlay(this, this.resourceManager.createQueuedReload(Util.backgroundExecutor(), (Executor)this, Minecraft.RESOURCE_RELOAD_INITIAL_TASK), () -> {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                this.selfTest();
            }
        }, false));
    }
    
    private void createSearchTrees() {
        final ReloadableSearchTree<ItemStack> dzw2 = new ReloadableSearchTree<ItemStack>((java.util.function.Function<ItemStack, Stream<String>>)(bcj -> bcj.getTooltipLines(null, TooltipFlag.Default.NORMAL).stream().map(jo -> ChatFormatting.stripFormatting(jo.getString()).trim()).filter(string -> !string.isEmpty())), (java.util.function.Function<ItemStack, Stream<ResourceLocation>>)(bcj -> Stream.of(Registry.ITEM.getKey(bcj.getItem()))));
        final ReloadableIdSearchTree<ItemStack> dzv3 = new ReloadableIdSearchTree<ItemStack>((java.util.function.Function<ItemStack, Stream<ResourceLocation>>)(bcj -> ItemTags.getAllTags().getMatchingTags(bcj.getItem()).stream()));
        final NonNullList<ItemStack> fk4 = NonNullList.<ItemStack>create();
        for (final Item bce6 : Registry.ITEM) {
            bce6.fillItemCategory(CreativeModeTab.TAB_SEARCH, fk4);
        }
        fk4.forEach(bcj -> {
            dzw2.add(bcj);
            dzv3.add(bcj);
        });
        final ReloadableSearchTree<RecipeCollection> dzw3 = new ReloadableSearchTree<RecipeCollection>((java.util.function.Function<RecipeCollection, Stream<String>>)(dfc -> dfc.getRecipes().stream().flatMap(ber -> ber.getResultItem().getTooltipLines(null, TooltipFlag.Default.NORMAL).stream()).map(jo -> ChatFormatting.stripFormatting(jo.getString()).trim()).filter(string -> !string.isEmpty())), (java.util.function.Function<RecipeCollection, Stream<ResourceLocation>>)(dfc -> dfc.getRecipes().stream().map(ber -> Registry.ITEM.getKey(ber.getResultItem().getItem()))));
        this.searchRegistry.<ItemStack>register(SearchRegistry.CREATIVE_NAMES, dzw2);
        this.searchRegistry.<ItemStack>register(SearchRegistry.CREATIVE_TAGS, dzv3);
        this.searchRegistry.<RecipeCollection>register(SearchRegistry.RECIPE_COLLECTIONS, dzw3);
    }
    
    private void onFullscreenError(final int integer, final long long2) {
        this.options.enableVsync = false;
        this.options.save();
    }
    
    private static boolean checkIs64Bit() {
        final String[] array;
        final String[] arr1 = array = new String[] { "sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch" };
        for (final String string5 : array) {
            final String string6 = System.getProperty(string5);
            if (string6 != null && string6.contains("64")) {
                return true;
            }
        }
        return false;
    }
    
    public RenderTarget getMainRenderTarget() {
        return this.mainRenderTarget;
    }
    
    public String getLaunchedVersion() {
        return this.launchedVersion;
    }
    
    public String getVersionType() {
        return this.versionType;
    }
    
    private void startTimerHackThread() {
        final Thread thread2 = new Thread("Timer hack thread") {
            public void run() {
                while (Minecraft.this.running) {
                    try {
                        Thread.sleep(2147483647L);
                    }
                    catch (InterruptedException ex) {}
                }
            }
        };
        thread2.setDaemon(true);
        thread2.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(Minecraft.LOGGER));
        thread2.start();
    }
    
    public void delayCrash(final CrashReport d) {
        this.hasCrashed = true;
        this.delayedCrash = d;
    }
    
    public void crash(final CrashReport d) {
        final File file3 = new File(getInstance().gameDirectory, "crash-reports");
        final File file4 = new File(file3, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
        Bootstrap.realStdoutPrintln(d.getFriendlyReport());
        if (d.getSaveFile() != null) {
            Bootstrap.realStdoutPrintln(new StringBuilder().append("#@!@# Game crashed! Crash report saved to: #@!@# ").append(d.getSaveFile()).toString());
            System.exit(-1);
        }
        else if (d.saveToFile(file4)) {
            Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + file4.getAbsolutePath());
            System.exit(-1);
        }
        else {
            Bootstrap.realStdoutPrintln("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }
    
    public boolean isEnforceUnicode() {
        return this.options.forceUnicodeFont;
    }
    
    public CompletableFuture<Void> reloadResourcePacks() {
        if (this.pendingReload != null) {
            return this.pendingReload;
        }
        final CompletableFuture<Void> completableFuture2 = (CompletableFuture<Void>)new CompletableFuture();
        if (this.overlay instanceof LoadingOverlay) {
            return this.pendingReload = completableFuture2;
        }
        this.resourcePackRepository.reload();
        final List<Pack> list3 = (List<Pack>)this.resourcePackRepository.getSelected().stream().map(UnopenedPack::open).collect(Collectors.toList());
        this.setOverlay(new LoadingOverlay(this, this.resourceManager.createFullReload(Util.backgroundExecutor(), (Executor)this, Minecraft.RESOURCE_RELOAD_INITIAL_TASK, list3), () -> {
            this.languageManager.reload(list3);
            if (this.levelRenderer != null) {
                this.levelRenderer.allChanged();
            }
            completableFuture2.complete(null);
        }, true));
        return completableFuture2;
    }
    
    private void selfTest() {
        boolean boolean2 = false;
        final BlockModelShaper dnv3 = this.getBlockRenderer().getBlockModelShaper();
        final BakedModel dyp4 = dnv3.getModelManager().getMissingModel();
        for (final Block bmv6 : Registry.BLOCK) {
            for (final BlockState bvt8 : bmv6.getStateDefinition().getPossibleStates()) {
                if (bvt8.getRenderShape() == RenderShape.MODEL) {
                    final BakedModel dyp5 = dnv3.getBlockModel(bvt8);
                    if (dyp5 != dyp4) {
                        continue;
                    }
                    Minecraft.LOGGER.debug("Missing model for: {}", bvt8);
                    boolean2 = true;
                }
            }
        }
        final TextureAtlasSprite dxb5 = dyp4.getParticleIcon();
        for (final Block bmv7 : Registry.BLOCK) {
            for (final BlockState bvt9 : bmv7.getStateDefinition().getPossibleStates()) {
                final TextureAtlasSprite dxb6 = dnv3.getParticleIcon(bvt9);
                if (!bvt9.isAir() && dxb6 == dxb5) {
                    Minecraft.LOGGER.debug("Missing particle icon for: {}", bvt9);
                    boolean2 = true;
                }
            }
        }
        final NonNullList<ItemStack> fk6 = NonNullList.<ItemStack>create();
        for (final Item bce8 : Registry.ITEM) {
            fk6.clear();
            bce8.fillItemCategory(CreativeModeTab.TAB_SEARCH, fk6);
            for (final ItemStack bcj10 : fk6) {
                final String string11 = bcj10.getDescriptionId();
                final String string12 = new TranslatableComponent(string11, new Object[0]).getString();
                if (string12.toLowerCase(Locale.ROOT).equals(bce8.getDescriptionId())) {
                    Minecraft.LOGGER.debug("Missing translation for: {} {} {}", bcj10, string11, bcj10.getItem());
                }
            }
        }
        boolean2 |= MenuScreens.selfTest();
        if (boolean2) {
            throw new IllegalStateException("Your game data is foobar, fix the errors above!");
        }
    }
    
    public LevelStorageSource getLevelSource() {
        return this.levelSource;
    }
    
    public void setScreen(@Nullable Screen dcl) {
        if (this.screen != null) {
            this.screen.removed();
        }
        if (dcl == null && this.level == null) {
            dcl = new TitleScreen();
        }
        else if (dcl == null && this.player.getHealth() <= 0.0f) {
            dcl = new DeathScreen(null, this.level.getLevelData().isHardcore());
        }
        if (dcl instanceof TitleScreen || dcl instanceof JoinMultiplayerScreen) {
            this.options.renderDebug = false;
            this.gui.getChat().clearMessages(true);
        }
        if ((this.screen = dcl) != null) {
            this.mouseHandler.releaseMouse();
            KeyMapping.releaseAll();
            dcl.init(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
            this.noRender = false;
            NarratorChatListener.INSTANCE.sayNow(dcl.getNarrationMessage());
        }
        else {
            this.soundManager.resume();
            this.mouseHandler.grabMouse();
        }
    }
    
    public void setOverlay(@Nullable final Overlay dcg) {
        this.overlay = dcg;
    }
    
    public void destroy() {
        try {
            Minecraft.LOGGER.info("Stopping!");
            NarratorChatListener.INSTANCE.destroy();
            try {
                if (this.level != null) {
                    this.level.disconnect();
                }
                this.clearLevel();
            }
            catch (Throwable t) {}
            if (this.screen != null) {
                this.screen.removed();
            }
            this.close();
        }
        finally {
            Util.timeSource = System::nanoTime;
            if (!this.hasCrashed) {
                System.exit(0);
            }
        }
    }
    
    public void close() {
        try {
            this.textureAtlas.clearTextureData();
            this.font.close();
            this.fontManager.close();
            this.gameRenderer.close();
            this.levelRenderer.close();
            this.soundManager.destroy();
            this.resourcePackRepository.close();
            this.particleEngine.close();
            this.mobEffectTextures.close();
            this.paintingTextures.close();
            Util.shutdownBackgroundExecutor();
        }
        finally {
            this.virtualScreen.close();
            this.window.close();
        }
    }
    
    private void runTick(final boolean boolean1) {
        this.window.setGlErrorSection("Pre render");
        final long long3 = Util.getNanos();
        this.profiler.startTick();
        if (GLX.shouldClose(this.window)) {
            this.stop();
        }
        if (this.pendingReload != null && !(this.overlay instanceof LoadingOverlay)) {
            final CompletableFuture<Void> completableFuture5 = this.pendingReload;
            this.pendingReload = null;
            this.reloadResourcePacks().thenRun(() -> completableFuture5.complete(null));
        }
        Runnable runnable5;
        while ((runnable5 = (Runnable)this.progressTasks.poll()) != null) {
            runnable5.run();
        }
        if (boolean1) {
            this.timer.advanceTime(Util.getMillis());
            this.profiler.push("scheduledExecutables");
            this.runAllTasks();
            this.profiler.pop();
        }
        final long long4 = Util.getNanos();
        this.profiler.push("tick");
        if (boolean1) {
            for (int integer8 = 0; integer8 < Math.min(10, this.timer.ticks); ++integer8) {
                this.tick();
            }
        }
        this.mouseHandler.turnPlayer();
        this.window.setGlErrorSection("Render");
        GLX.pollEvents();
        final long long5 = Util.getNanos() - long4;
        this.profiler.popPush("sound");
        this.soundManager.updateSource(this.gameRenderer.getMainCamera());
        this.profiler.pop();
        this.profiler.push("render");
        GlStateManager.pushMatrix();
        GlStateManager.clear(16640, Minecraft.ON_OSX);
        this.mainRenderTarget.bindWrite(true);
        this.profiler.push("display");
        GlStateManager.enableTexture();
        this.profiler.pop();
        if (!this.noRender) {
            this.profiler.popPush("gameRenderer");
            this.gameRenderer.render(this.pause ? this.pausePartialTick : this.timer.partialTick, long3, boolean1);
            this.profiler.popPush("toasts");
            this.toast.render();
            this.profiler.pop();
        }
        this.profiler.endTick();
        if (this.options.renderDebug && this.options.renderDebugCharts && !this.options.hideGui) {
            this.profiler.continuous().enable();
            this.renderFpsMeter();
        }
        else {
            this.profiler.continuous().disable();
        }
        this.mainRenderTarget.unbindWrite();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.mainRenderTarget.blitToScreen(this.window.getWidth(), this.window.getHeight());
        GlStateManager.popMatrix();
        this.profiler.startTick();
        this.updateDisplay(true);
        Thread.yield();
        this.window.setGlErrorSection("Post render");
        ++this.frames;
        final boolean boolean2 = this.hasSingleplayerServer() && ((this.screen != null && this.screen.isPauseScreen()) || (this.overlay != null && this.overlay.isPauseScreen())) && !this.singleplayerServer.isPublished();
        if (this.pause != boolean2) {
            if (this.pause) {
                this.pausePartialTick = this.timer.partialTick;
            }
            else {
                this.timer.partialTick = this.pausePartialTick;
            }
            this.pause = boolean2;
        }
        final long long6 = Util.getNanos();
        this.frameTimer.logFrameDuration(long6 - this.lastNanoTime);
        this.lastNanoTime = long6;
        while (Util.getMillis() >= this.lastTime + 1000L) {
            Minecraft.fps = this.frames;
            this.fpsString = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", new Object[] { Minecraft.fps, RenderChunk.updateCounter, (RenderChunk.updateCounter == 1) ? "" : "s", (this.options.framerateLimit == Option.FRAMERATE_LIMIT.getMaxValue()) ? "inf" : Integer.valueOf(this.options.framerateLimit), this.options.enableVsync ? " vsync" : "", this.options.fancyGraphics ? "" : " fast", (this.options.renderClouds == CloudStatus.OFF) ? "" : ((this.options.renderClouds == CloudStatus.FAST) ? " fast-clouds" : " fancy-clouds"), GLX.useVbo() ? " vbo" : "" });
            RenderChunk.updateCounter = 0;
            this.lastTime += 1000L;
            this.frames = 0;
            this.snooper.prepare();
            if (!this.snooper.isStarted()) {
                this.snooper.start();
            }
        }
        this.profiler.endTick();
    }
    
    @Override
    public void updateDisplay(final boolean boolean1) {
        this.profiler.push("display_update");
        this.window.updateDisplay(this.options.fullscreen);
        this.profiler.pop();
        if (boolean1 && this.isFramerateLimited()) {
            this.profiler.push("fpslimit_wait");
            this.window.limitDisplayFPS();
            this.profiler.pop();
        }
    }
    
    @Override
    public void resizeDisplay() {
        final int integer2 = this.window.calculateScale(this.options.guiScale, this.isEnforceUnicode());
        this.window.setGuiScale(integer2);
        if (this.screen != null) {
            this.screen.resize(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
        }
        final RenderTarget ctz3 = this.getMainRenderTarget();
        if (ctz3 != null) {
            ctz3.resize(this.window.getWidth(), this.window.getHeight(), Minecraft.ON_OSX);
        }
        if (this.gameRenderer != null) {
            this.gameRenderer.resize(this.window.getWidth(), this.window.getHeight());
        }
        if (this.mouseHandler != null) {
            this.mouseHandler.setIgnoreFirstMove();
        }
    }
    
    private int getFramerateLimit() {
        if (this.level == null && (this.screen != null || this.overlay != null)) {
            return 60;
        }
        return this.window.getFramerateLimit();
    }
    
    private boolean isFramerateLimited() {
        return this.getFramerateLimit() < Option.FRAMERATE_LIMIT.getMaxValue();
    }
    
    public void emergencySave() {
        try {
            Minecraft.reserve = new byte[0];
            this.levelRenderer.clear();
        }
        catch (Throwable t) {}
        try {
            System.gc();
            if (this.hasSingleplayerServer()) {
                this.singleplayerServer.halt(true);
            }
            this.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel", new Object[0])));
        }
        catch (Throwable t2) {}
        System.gc();
    }
    
    void debugFpsMeterKeyPress(int integer) {
        final ProfileResults agm3 = this.profiler.continuous().getResults();
        final List<ResultField> list4 = agm3.getTimes(this.debugPath);
        if (list4.isEmpty()) {
            return;
        }
        final ResultField ago5 = (ResultField)list4.remove(0);
        if (integer == 0) {
            if (!ago5.name.isEmpty()) {
                final int integer2 = this.debugPath.lastIndexOf(46);
                if (integer2 >= 0) {
                    this.debugPath = this.debugPath.substring(0, integer2);
                }
            }
        }
        else if (--integer < list4.size() && !"unspecified".equals(((ResultField)list4.get(integer)).name)) {
            if (!this.debugPath.isEmpty()) {
                this.debugPath += ".";
            }
            this.debugPath += ((ResultField)list4.get(integer)).name;
        }
    }
    
    private void renderFpsMeter() {
        if (!this.profiler.continuous().isEnabled()) {
            return;
        }
        final ProfileResults agm2 = this.profiler.continuous().getResults();
        final List<ResultField> list3 = agm2.getTimes(this.debugPath);
        final ResultField ago4 = (ResultField)list3.remove(0);
        GlStateManager.clear(256, Minecraft.ON_OSX);
        GlStateManager.matrixMode(5889);
        GlStateManager.enableColorMaterial();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, this.window.getWidth(), this.window.getHeight(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0f, 0.0f, -2000.0f);
        GlStateManager.lineWidth(1.0f);
        GlStateManager.disableTexture();
        final Tesselator cuz5 = Tesselator.getInstance();
        final BufferBuilder cuw6 = cuz5.getBuilder();
        final int integer7 = 160;
        final int integer8 = this.window.getWidth() - 160 - 10;
        final int integer9 = this.window.getHeight() - 320;
        GlStateManager.enableBlend();
        cuw6.begin(7, DefaultVertexFormat.POSITION_COLOR);
        cuw6.vertex(integer8 - 176.0f, integer9 - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).endVertex();
        cuw6.vertex(integer8 - 176.0f, integer9 + 320, 0.0).color(200, 0, 0, 0).endVertex();
        cuw6.vertex(integer8 + 176.0f, integer9 + 320, 0.0).color(200, 0, 0, 0).endVertex();
        cuw6.vertex(integer8 + 176.0f, integer9 - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).endVertex();
        cuz5.end();
        GlStateManager.disableBlend();
        double double10 = 0.0;
        for (int integer10 = 0; integer10 < list3.size(); ++integer10) {
            final ResultField ago5 = (ResultField)list3.get(integer10);
            final int integer11 = Mth.floor(ago5.percentage / 4.0) + 1;
            cuw6.begin(6, DefaultVertexFormat.POSITION_COLOR);
            final int integer12 = ago5.getColor();
            final int integer13 = integer12 >> 16 & 0xFF;
            final int integer14 = integer12 >> 8 & 0xFF;
            final int integer15 = integer12 & 0xFF;
            cuw6.vertex(integer8, integer9, 0.0).color(integer13, integer14, integer15, 255).endVertex();
            for (int integer16 = integer11; integer16 >= 0; --integer16) {
                final float float20 = (float)((double10 + ago5.percentage * integer16 / integer11) * 6.2831854820251465 / 100.0);
                final float float21 = Mth.sin(float20) * 160.0f;
                final float float22 = Mth.cos(float20) * 160.0f * 0.5f;
                cuw6.vertex(integer8 + float21, integer9 - float22, 0.0).color(integer13, integer14, integer15, 255).endVertex();
            }
            cuz5.end();
            cuw6.begin(5, DefaultVertexFormat.POSITION_COLOR);
            for (int integer16 = integer11; integer16 >= 0; --integer16) {
                final float float20 = (float)((double10 + ago5.percentage * integer16 / integer11) * 6.2831854820251465 / 100.0);
                final float float21 = Mth.sin(float20) * 160.0f;
                final float float22 = Mth.cos(float20) * 160.0f * 0.5f;
                cuw6.vertex(integer8 + float21, integer9 - float22, 0.0).color(integer13 >> 1, integer14 >> 1, integer15 >> 1, 255).endVertex();
                cuw6.vertex(integer8 + float21, integer9 - float22 + 10.0f, 0.0).color(integer13 >> 1, integer14 >> 1, integer15 >> 1, 255).endVertex();
            }
            cuz5.end();
            double10 += ago5.percentage;
        }
        final DecimalFormat decimalFormat12 = new DecimalFormat("##0.00");
        decimalFormat12.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        GlStateManager.enableTexture();
        String string13 = "";
        if (!"unspecified".equals(ago4.name)) {
            string13 += "[0] ";
        }
        if (ago4.name.isEmpty()) {
            string13 += "ROOT ";
        }
        else {
            string13 = string13 + ago4.name + ' ';
        }
        final int integer11 = 16777215;
        this.font.drawShadow(string13, (float)(integer8 - 160), (float)(integer9 - 80 - 16), 16777215);
        string13 = decimalFormat12.format(ago4.globalPercentage) + "%";
        this.font.drawShadow(string13, (float)(integer8 + 160 - this.font.width(string13)), (float)(integer9 - 80 - 16), 16777215);
        for (int integer17 = 0; integer17 < list3.size(); ++integer17) {
            final ResultField ago6 = (ResultField)list3.get(integer17);
            final StringBuilder stringBuilder15 = new StringBuilder();
            if ("unspecified".equals(ago6.name)) {
                stringBuilder15.append("[?] ");
            }
            else {
                stringBuilder15.append("[").append(integer17 + 1).append("] ");
            }
            String string14 = stringBuilder15.append(ago6.name).toString();
            this.font.drawShadow(string14, (float)(integer8 - 160), (float)(integer9 + 80 + integer17 * 8 + 20), ago6.getColor());
            string14 = decimalFormat12.format(ago6.percentage) + "%";
            this.font.drawShadow(string14, (float)(integer8 + 160 - 50 - this.font.width(string14)), (float)(integer9 + 80 + integer17 * 8 + 20), ago6.getColor());
            string14 = decimalFormat12.format(ago6.globalPercentage) + "%";
            this.font.drawShadow(string14, (float)(integer8 + 160 - this.font.width(string14)), (float)(integer9 + 80 + integer17 * 8 + 20), ago6.getColor());
        }
    }
    
    public void stop() {
        this.running = false;
    }
    
    public void pauseGame(final boolean boolean1) {
        if (this.screen != null) {
            return;
        }
        final boolean boolean2 = this.hasSingleplayerServer() && !this.singleplayerServer.isPublished();
        if (boolean2) {
            this.setScreen(new PauseScreen(!boolean1));
            this.soundManager.pause();
        }
        else {
            this.setScreen(new PauseScreen(true));
        }
    }
    
    private void continueAttack(final boolean boolean1) {
        if (!boolean1) {
            this.missTime = 0;
        }
        if (this.missTime > 0 || this.player.isUsingItem()) {
            return;
        }
        if (boolean1 && this.hitResult != null && this.hitResult.getType() == HitResult.Type.BLOCK) {
            final BlockHitResult csd3 = (BlockHitResult)this.hitResult;
            final BlockPos ew4 = csd3.getBlockPos();
            if (!this.level.getBlockState(ew4).isAir()) {
                final Direction fb5 = csd3.getDirection();
                if (this.gameMode.continueDestroyBlock(ew4, fb5)) {
                    this.particleEngine.crack(ew4, fb5);
                    this.player.swing(InteractionHand.MAIN_HAND);
                }
            }
            return;
        }
        this.gameMode.stopDestroyBlock();
    }
    
    private void startAttack() {
        if (this.missTime > 0) {
            return;
        }
        if (this.hitResult == null) {
            Minecraft.LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.gameMode.hasMissTime()) {
                this.missTime = 10;
            }
            return;
        }
        if (this.player.isHandsBusy()) {
            return;
        }
        switch (this.hitResult.getType()) {
            case ENTITY: {
                this.gameMode.attack(this.player, ((EntityHitResult)this.hitResult).getEntity());
                break;
            }
            case BLOCK: {
                final BlockHitResult csd2 = (BlockHitResult)this.hitResult;
                final BlockPos ew3 = csd2.getBlockPos();
                if (!this.level.getBlockState(ew3).isAir()) {
                    this.gameMode.startDestroyBlock(ew3, csd2.getDirection());
                    break;
                }
            }
            case MISS: {
                if (this.gameMode.hasMissTime()) {
                    this.missTime = 10;
                }
                this.player.resetAttackStrengthTicker();
                break;
            }
        }
        this.player.swing(InteractionHand.MAIN_HAND);
    }
    
    private void startUseItem() {
        if (this.gameMode.isDestroying()) {
            return;
        }
        this.rightClickDelay = 4;
        if (this.player.isHandsBusy()) {
            return;
        }
        if (this.hitResult == null) {
            Minecraft.LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
        }
        for (final InteractionHand ahi5 : InteractionHand.values()) {
            final ItemStack bcj6 = this.player.getItemInHand(ahi5);
            if (this.hitResult != null) {
                switch (this.hitResult.getType()) {
                    case ENTITY: {
                        final EntityHitResult cse7 = (EntityHitResult)this.hitResult;
                        final Entity aio8 = cse7.getEntity();
                        if (this.gameMode.interactAt(this.player, aio8, cse7, ahi5) == InteractionResult.SUCCESS) {
                            return;
                        }
                        if (this.gameMode.interact(this.player, aio8, ahi5) == InteractionResult.SUCCESS) {
                            return;
                        }
                        break;
                    }
                    case BLOCK: {
                        final BlockHitResult csd9 = (BlockHitResult)this.hitResult;
                        final int integer10 = bcj6.getCount();
                        final InteractionResult ahj11 = this.gameMode.useItemOn(this.player, this.level, ahi5, csd9);
                        if (ahj11 == InteractionResult.SUCCESS) {
                            this.player.swing(ahi5);
                            if (!bcj6.isEmpty() && (bcj6.getCount() != integer10 || this.gameMode.hasInfiniteItems())) {
                                this.gameRenderer.itemInHandRenderer.itemUsed(ahi5);
                            }
                            return;
                        }
                        if (ahj11 == InteractionResult.FAIL) {
                            return;
                        }
                        break;
                    }
                }
            }
            if (!bcj6.isEmpty() && this.gameMode.useItem(this.player, this.level, ahi5) == InteractionResult.SUCCESS) {
                this.gameRenderer.itemInHandRenderer.itemUsed(ahi5);
                return;
            }
        }
    }
    
    public MusicManager getMusicManager() {
        return this.musicManager;
    }
    
    public void tick() {
        if (this.rightClickDelay > 0) {
            --this.rightClickDelay;
        }
        this.profiler.push("gui");
        if (!this.pause) {
            this.gui.tick();
        }
        this.profiler.pop();
        this.gameRenderer.pick(1.0f);
        this.tutorial.onLookAt(this.level, this.hitResult);
        this.profiler.push("gameMode");
        if (!this.pause && this.level != null) {
            this.gameMode.tick();
        }
        this.profiler.popPush("textures");
        if (this.level != null) {
            this.textureManager.tick();
        }
        if (this.screen == null && this.player != null) {
            if (this.player.getHealth() <= 0.0f && !(this.screen instanceof DeathScreen)) {
                this.setScreen(null);
            }
            else if (this.player.isSleeping() && this.level != null) {
                this.setScreen(new InBedChatScreen());
            }
        }
        else if (this.screen != null && this.screen instanceof InBedChatScreen && !this.player.isSleeping()) {
            this.setScreen(null);
        }
        if (this.screen != null) {
            this.missTime = 10000;
        }
        if (this.screen != null) {
            Screen.wrapScreenError(() -> this.screen.tick(), "Ticking screen", this.screen.getClass().getCanonicalName());
        }
        if (!this.options.renderDebug) {
            this.gui.clearCache();
        }
        if (this.overlay == null && (this.screen == null || this.screen.passEvents)) {
            this.profiler.popPush("GLFW events");
            GLX.pollEvents();
            this.handleKeybinds();
            if (this.missTime > 0) {
                --this.missTime;
            }
        }
        if (this.level != null) {
            this.profiler.popPush("gameRenderer");
            if (!this.pause) {
                this.gameRenderer.tick();
            }
            this.profiler.popPush("levelRenderer");
            if (!this.pause) {
                this.levelRenderer.tick();
            }
            this.profiler.popPush("level");
            if (!this.pause) {
                if (this.level.getSkyFlashTime() > 0) {
                    this.level.setSkyFlashTime(this.level.getSkyFlashTime() - 1);
                }
                this.level.tickEntities();
            }
        }
        else if (this.gameRenderer.postEffectActive()) {
            this.gameRenderer.shutdownEffect();
        }
        if (!this.pause) {
            this.musicManager.tick();
        }
        this.soundManager.tick(this.pause);
        if (this.level != null) {
            if (!this.pause) {
                this.level.setSpawnSettings(this.level.getDifficulty() != Difficulty.PEACEFUL, true);
                this.tutorial.tick();
                try {
                    this.level.tick(() -> true);
                }
                catch (Throwable throwable2) {
                    final CrashReport d3 = CrashReport.forThrowable(throwable2, "Exception in world tick");
                    if (this.level == null) {
                        final CrashReportCategory e4 = d3.addCategory("Affected level");
                        e4.setDetail("Problem", "Level is null!");
                    }
                    else {
                        this.level.fillReportDetails(d3);
                    }
                    throw new ReportedException(d3);
                }
            }
            this.profiler.popPush("animateTick");
            if (!this.pause && this.level != null) {
                this.level.animateTick(Mth.floor(this.player.x), Mth.floor(this.player.y), Mth.floor(this.player.z));
            }
            this.profiler.popPush("particles");
            if (!this.pause) {
                this.particleEngine.tick();
            }
        }
        else if (this.pendingConnection != null) {
            this.profiler.popPush("pendingConnection");
            this.pendingConnection.tick();
        }
        this.profiler.popPush("keyboard");
        this.keyboardHandler.tick();
        this.profiler.pop();
    }
    
    private void handleKeybinds() {
        while (this.options.keyTogglePerspective.consumeClick()) {
            final Options options = this.options;
            ++options.thirdPersonView;
            if (this.options.thirdPersonView > 2) {
                this.options.thirdPersonView = 0;
            }
            if (this.options.thirdPersonView == 0) {
                this.gameRenderer.checkEntityPostEffect(this.getCameraEntity());
            }
            else if (this.options.thirdPersonView == 1) {
                this.gameRenderer.checkEntityPostEffect(null);
            }
            this.levelRenderer.needsUpdate();
        }
        while (this.options.keySmoothCamera.consumeClick()) {
            this.options.smoothCamera = !this.options.smoothCamera;
        }
        for (int integer2 = 0; integer2 < 9; ++integer2) {
            final boolean boolean3 = this.options.keySaveHotbarActivator.isDown();
            final boolean boolean4 = this.options.keyLoadHotbarActivator.isDown();
            if (this.options.keyHotbarSlots[integer2].consumeClick()) {
                if (this.player.isSpectator()) {
                    this.gui.getSpectatorGui().onHotbarSelected(integer2);
                }
                else if (this.player.isCreative() && this.screen == null && (boolean4 || boolean3)) {
                    CreativeModeInventoryScreen.handleHotbarLoadOrSave(this, integer2, boolean4, boolean3);
                }
                else {
                    this.player.inventory.selected = integer2;
                }
            }
        }
        while (this.options.keyInventory.consumeClick()) {
            if (this.gameMode.isServerControlledInventory()) {
                this.player.sendOpenInventory();
            }
            else {
                this.tutorial.onOpenInventory();
                this.setScreen(new InventoryScreen(this.player));
            }
        }
        while (this.options.keyAdvancements.consumeClick()) {
            this.setScreen(new AdvancementsScreen(this.player.connection.getAdvancements()));
        }
        while (this.options.keySwapHands.consumeClick()) {
            if (!this.player.isSpectator()) {
                this.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_HELD_ITEMS, BlockPos.ZERO, Direction.DOWN));
            }
        }
        while (this.options.keyDrop.consumeClick()) {
            if (!this.player.isSpectator()) {
                this.player.drop(Screen.hasControlDown());
            }
        }
        final boolean boolean5 = this.options.chatVisibility != ChatVisiblity.HIDDEN;
        if (boolean5) {
            while (this.options.keyChat.consumeClick()) {
                this.setScreen(new ChatScreen(""));
            }
            if (this.screen == null && this.overlay == null && this.options.keyCommand.consumeClick()) {
                this.setScreen(new ChatScreen("/"));
            }
        }
        if (this.player.isUsingItem()) {
            if (!this.options.keyUse.isDown()) {
                this.gameMode.releaseUsingItem(this.player);
            }
            while (this.options.keyAttack.consumeClick()) {}
            while (this.options.keyUse.consumeClick()) {}
            while (this.options.keyPickItem.consumeClick()) {}
        }
        else {
            while (this.options.keyAttack.consumeClick()) {
                this.startAttack();
            }
            while (this.options.keyUse.consumeClick()) {
                this.startUseItem();
            }
            while (this.options.keyPickItem.consumeClick()) {
                this.pickBlock();
            }
        }
        if (this.options.keyUse.isDown() && this.rightClickDelay == 0 && !this.player.isUsingItem()) {
            this.startUseItem();
        }
        this.continueAttack(this.screen == null && this.options.keyAttack.isDown() && this.mouseHandler.isMouseGrabbed());
    }
    
    public void selectLevel(final String string1, final String string2, @Nullable LevelSettings bhv) {
        this.clearLevel();
        final LevelStorage coo5 = this.levelSource.selectLevel(string1, null);
        LevelData com6 = coo5.prepareLevel();
        if (com6 == null && bhv != null) {
            com6 = new LevelData(bhv, string1);
            coo5.saveLevelData(com6);
        }
        if (bhv == null) {
            bhv = new LevelSettings(com6);
        }
        this.progressListener.set(null);
        try {
            final YggdrasilAuthenticationService yggdrasilAuthenticationService7 = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
            final MinecraftSessionService minecraftSessionService8 = yggdrasilAuthenticationService7.createMinecraftSessionService();
            final GameProfileRepository gameProfileRepository9 = yggdrasilAuthenticationService7.createProfileRepository();
            final GameProfileCache xr10 = new GameProfileCache(gameProfileRepository9, new File(this.gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
            SkullBlockEntity.setProfileCache(xr10);
            SkullBlockEntity.setSessionService(minecraftSessionService8);
            GameProfileCache.setUsesAuthentication(false);
            final StoringChunkProgressListener vx3;
            (this.singleplayerServer = new IntegratedServer(this, string1, string2, bhv, yggdrasilAuthenticationService7, minecraftSessionService8, gameProfileRepository9, xr10, integer -> {
                vx3 = new StoringChunkProgressListener(integer + 0);
                vx3.start();
                this.progressListener.set(vx3);
                return new ProcessorChunkProgressListener(vx3, this.progressTasks::add);
            })).forkAndRun();
            this.isLocalServer = true;
        }
        catch (Throwable throwable7) {
            final CrashReport d8 = CrashReport.forThrowable(throwable7, "Starting integrated server");
            final CrashReportCategory e9 = d8.addCategory("Starting integrated server");
            e9.setDetail("Level ID", string1);
            e9.setDetail("Level Name", string2);
            throw new ReportedException(d8);
        }
        while (this.progressListener.get() == null) {
            Thread.yield();
        }
        final LevelLoadingScreen dca7 = new LevelLoadingScreen((StoringChunkProgressListener)this.progressListener.get());
        this.setScreen(dca7);
        while (!this.singleplayerServer.isReady()) {
            dca7.tick();
            this.runTick(false);
            try {
                Thread.sleep(16L);
            }
            catch (InterruptedException ex) {}
            if (this.hasCrashed && this.delayedCrash != null) {
                this.crash(this.delayedCrash);
                return;
            }
        }
        final SocketAddress socketAddress8 = this.singleplayerServer.getConnection().startMemoryChannel();
        final Connection jc9 = Connection.connectToLocalServer(socketAddress8);
        jc9.setListener(new ClientHandshakePacketListenerImpl(jc9, this, null, (Consumer<Component>)(jo -> {})));
        jc9.send(new ClientIntentionPacket(socketAddress8.toString(), 0, ConnectionProtocol.LOGIN));
        jc9.send(new ServerboundHelloPacket(this.getUser().getGameProfile()));
        this.pendingConnection = jc9;
    }
    
    public void setLevel(final MultiPlayerLevel dkf) {
        final ProgressScreen dcj3 = new ProgressScreen();
        dcj3.progressStartNoAbort(new TranslatableComponent("connect.joining", new Object[0]));
        this.updateScreenAndTick(dcj3);
        this.updateLevelInEngines(this.level = dkf);
        if (!this.isLocalServer) {
            final AuthenticationService authenticationService4 = (AuthenticationService)new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
            final MinecraftSessionService minecraftSessionService5 = authenticationService4.createMinecraftSessionService();
            final GameProfileRepository gameProfileRepository6 = authenticationService4.createProfileRepository();
            final GameProfileCache xr7 = new GameProfileCache(gameProfileRepository6, new File(this.gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
            SkullBlockEntity.setProfileCache(xr7);
            SkullBlockEntity.setSessionService(minecraftSessionService5);
            GameProfileCache.setUsesAuthentication(false);
        }
    }
    
    public void clearLevel() {
        this.clearLevel(new ProgressScreen());
    }
    
    public void clearLevel(final Screen dcl) {
        final ClientPacketListener dkc3 = this.getConnection();
        if (dkc3 != null) {
            this.dropAllTasks();
            dkc3.cleanup();
        }
        final IntegratedServer eac4 = this.singleplayerServer;
        this.singleplayerServer = null;
        this.gameRenderer.resetData();
        this.gameMode = null;
        NarratorChatListener.INSTANCE.clear();
        this.updateScreenAndTick(dcl);
        if (this.level != null) {
            if (eac4 != null) {
                while (!eac4.isShutdown()) {
                    this.runTick(false);
                }
            }
            this.clientPackSource.clearServerPack();
            this.gui.onDisconnected();
            this.setCurrentServer(null);
            this.isLocalServer = false;
            this.game.onLeaveGameSession();
        }
        this.updateLevelInEngines(this.level = null);
        this.player = null;
    }
    
    private void updateScreenAndTick(final Screen dcl) {
        this.musicManager.stopPlaying();
        this.soundManager.stop();
        this.cameraEntity = null;
        this.pendingConnection = null;
        this.setScreen(dcl);
        this.runTick(false);
    }
    
    private void updateLevelInEngines(@Nullable final MultiPlayerLevel dkf) {
        if (this.levelRenderer != null) {
            this.levelRenderer.setLevel(dkf);
        }
        if (this.particleEngine != null) {
            this.particleEngine.setLevel(dkf);
        }
        BlockEntityRenderDispatcher.instance.setLevel(dkf);
    }
    
    public final boolean isDemo() {
        return this.demo;
    }
    
    @Nullable
    public ClientPacketListener getConnection() {
        return (this.player == null) ? null : this.player.connection;
    }
    
    public static boolean renderNames() {
        return Minecraft.instance == null || !Minecraft.instance.options.hideGui;
    }
    
    public static boolean useFancyGraphics() {
        return Minecraft.instance != null && Minecraft.instance.options.fancyGraphics;
    }
    
    public static boolean useAmbientOcclusion() {
        return Minecraft.instance != null && Minecraft.instance.options.ambientOcclusion != AmbientOcclusionStatus.OFF;
    }
    
    private void pickBlock() {
        if (this.hitResult == null || this.hitResult.getType() == HitResult.Type.MISS) {
            return;
        }
        final boolean boolean2 = this.player.abilities.instabuild;
        BlockEntity btw3 = null;
        final HitResult.Type a5 = this.hitResult.getType();
        ItemStack bcj4;
        if (a5 == HitResult.Type.BLOCK) {
            final BlockPos ew6 = ((BlockHitResult)this.hitResult).getBlockPos();
            final BlockState bvt7 = this.level.getBlockState(ew6);
            final Block bmv8 = bvt7.getBlock();
            if (bvt7.isAir()) {
                return;
            }
            bcj4 = bmv8.getCloneItemStack(this.level, ew6, bvt7);
            if (bcj4.isEmpty()) {
                return;
            }
            if (boolean2 && Screen.hasControlDown() && bmv8.isEntityBlock()) {
                btw3 = this.level.getBlockEntity(ew6);
            }
        }
        else {
            if (a5 != HitResult.Type.ENTITY || !boolean2) {
                return;
            }
            final Entity aio6 = ((EntityHitResult)this.hitResult).getEntity();
            if (aio6 instanceof Painting) {
                bcj4 = new ItemStack(Items.PAINTING);
            }
            else if (aio6 instanceof LeashFenceKnotEntity) {
                bcj4 = new ItemStack(Items.LEAD);
            }
            else if (aio6 instanceof ItemFrame) {
                final ItemFrame atn7 = (ItemFrame)aio6;
                final ItemStack bcj5 = atn7.getItem();
                if (bcj5.isEmpty()) {
                    bcj4 = new ItemStack(Items.ITEM_FRAME);
                }
                else {
                    bcj4 = bcj5.copy();
                }
            }
            else if (aio6 instanceof AbstractMinecart) {
                final AbstractMinecart axu7 = (AbstractMinecart)aio6;
                Item bce8 = null;
                switch (axu7.getMinecartType()) {
                    case FURNACE: {
                        bce8 = Items.FURNACE_MINECART;
                        break;
                    }
                    case CHEST: {
                        bce8 = Items.CHEST_MINECART;
                        break;
                    }
                    case TNT: {
                        bce8 = Items.TNT_MINECART;
                        break;
                    }
                    case HOPPER: {
                        bce8 = Items.HOPPER_MINECART;
                        break;
                    }
                    case COMMAND_BLOCK: {
                        bce8 = Items.COMMAND_BLOCK_MINECART;
                        break;
                    }
                    default: {
                        bce8 = Items.MINECART;
                        break;
                    }
                }
                bcj4 = new ItemStack(bce8);
            }
            else if (aio6 instanceof Boat) {
                bcj4 = new ItemStack(((Boat)aio6).getDropItem());
            }
            else if (aio6 instanceof ArmorStand) {
                bcj4 = new ItemStack(Items.ARMOR_STAND);
            }
            else if (aio6 instanceof EndCrystal) {
                bcj4 = new ItemStack(Items.END_CRYSTAL);
            }
            else {
                final SpawnEggItem bdh7 = SpawnEggItem.byId(aio6.getType());
                if (bdh7 == null) {
                    return;
                }
                bcj4 = new ItemStack(bdh7);
            }
        }
        if (bcj4.isEmpty()) {
            String string6 = "";
            if (a5 == HitResult.Type.BLOCK) {
                string6 = Registry.BLOCK.getKey(this.level.getBlockState(((BlockHitResult)this.hitResult).getBlockPos()).getBlock()).toString();
            }
            else if (a5 == HitResult.Type.ENTITY) {
                string6 = Registry.ENTITY_TYPE.getKey(((EntityHitResult)this.hitResult).getEntity().getType()).toString();
            }
            Minecraft.LOGGER.warn("Picking on: [{}] {} gave null item", a5, string6);
            return;
        }
        final Inventory awf6 = this.player.inventory;
        if (btw3 != null) {
            this.addCustomNbtData(bcj4, btw3);
        }
        final int integer7 = awf6.findSlotMatchingItem(bcj4);
        if (boolean2) {
            awf6.setPickedItem(bcj4);
            this.gameMode.handleCreativeModeItemAdd(this.player.getItemInHand(InteractionHand.MAIN_HAND), 36 + awf6.selected);
        }
        else if (integer7 != -1) {
            if (Inventory.isHotbarSlot(integer7)) {
                awf6.selected = integer7;
            }
            else {
                this.gameMode.handlePickItem(integer7);
            }
        }
    }
    
    private ItemStack addCustomNbtData(final ItemStack bcj, final BlockEntity btw) {
        final CompoundTag id4 = btw.save(new CompoundTag());
        if (bcj.getItem() instanceof PlayerHeadItem && id4.contains("Owner")) {
            final CompoundTag id5 = id4.getCompound("Owner");
            bcj.getOrCreateTag().put("SkullOwner", (Tag)id5);
            return bcj;
        }
        bcj.addTagElement("BlockEntityTag", (Tag)id4);
        final CompoundTag id5 = new CompoundTag();
        final ListTag ik6 = new ListTag();
        ik6.add(new StringTag("\"(+NBT)\""));
        id5.put("Lore", (Tag)ik6);
        bcj.addTagElement("display", (Tag)id5);
        return bcj;
    }
    
    public CrashReport fillReport(final CrashReport d) {
        final CrashReportCategory e3 = d.getSystemDetails();
        e3.setDetail("Launched Version", (CrashReportDetail<String>)(() -> this.launchedVersion));
        e3.setDetail("LWJGL", (CrashReportDetail<String>)GLX::getLWJGLVersion);
        e3.setDetail("OpenGL", (CrashReportDetail<String>)GLX::getOpenGLVersionString);
        e3.setDetail("GL Caps", (CrashReportDetail<String>)GLX::getCapsString);
        e3.setDetail("Using VBOs", (CrashReportDetail<String>)(() -> "Yes"));
        e3.setDetail("Is Modded", (CrashReportDetail<String>)(() -> {
            final String string1 = ClientBrandRetriever.getClientModName();
            if (!"vanilla".equals(string1)) {
                return "Definitely; Client brand changed to '" + string1 + "'";
            }
            if (Minecraft.class.getSigners() == null) {
                return "Very likely; Jar signature invalidated";
            }
            return "Probably not. Jar signature remains and client brand is untouched.";
        }));
        e3.setDetail("Type", "Client (map_client.txt)");
        e3.setDetail("Resource Packs", (CrashReportDetail<String>)(() -> {
            final StringBuilder stringBuilder2 = new StringBuilder();
            for (final String string4 : this.options.resourcePacks) {
                if (stringBuilder2.length() > 0) {
                    stringBuilder2.append(", ");
                }
                stringBuilder2.append(string4);
                if (this.options.incompatibleResourcePacks.contains(string4)) {
                    stringBuilder2.append(" (incompatible)");
                }
            }
            return stringBuilder2.toString();
        }));
        e3.setDetail("Current Language", (CrashReportDetail<String>)(() -> this.languageManager.getSelected().toString()));
        e3.setDetail("CPU", (CrashReportDetail<String>)GLX::getCpuInfo);
        if (this.level != null) {
            this.level.fillReportDetails(d);
        }
        return d;
    }
    
    public static Minecraft getInstance() {
        return Minecraft.instance;
    }
    
    public CompletableFuture<Void> delayTextureReload() {
        return (CompletableFuture<Void>)this.submit((java.util.function.Supplier<Object>)this::reloadResourcePacks).thenCompose(completableFuture -> completableFuture);
    }
    
    @Override
    public void populateSnooper(final Snooper ahq) {
        ahq.setDynamicData("fps", Minecraft.fps);
        ahq.setDynamicData("vsync_enabled", this.options.enableVsync);
        final int integer3 = GLX.getRefreshRate(this.window);
        ahq.setDynamicData("display_frequency", integer3);
        ahq.setDynamicData("display_type", this.window.isFullscreen() ? "fullscreen" : "windowed");
        ahq.setDynamicData("run_time", ((Util.getMillis() - ahq.getStartupTime()) / 60L * 1000L));
        ahq.setDynamicData("current_action", this.getCurrentSnooperAction());
        ahq.setDynamicData("language", (this.options.languageCode == null) ? "en_us" : this.options.languageCode);
        final String string4 = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) ? "little" : "big";
        ahq.setDynamicData("endianness", string4);
        ahq.setDynamicData("subtitles", this.options.showSubtitles);
        ahq.setDynamicData("touch", this.options.touchscreen ? "touch" : "mouse");
        int integer4 = 0;
        for (final UnopenedResourcePack dxw7 : this.resourcePackRepository.getSelected()) {
            if (!dxw7.isRequired() && !dxw7.isFixedPosition()) {
                ahq.setDynamicData(new StringBuilder().append("resource_pack[").append(integer4++).append("]").toString(), dxw7.getId());
            }
        }
        ahq.setDynamicData("resource_packs", integer4);
        if (this.singleplayerServer != null && this.singleplayerServer.getSnooper() != null) {
            ahq.setDynamicData("snooper_partner", this.singleplayerServer.getSnooper().getToken());
        }
    }
    
    private String getCurrentSnooperAction() {
        if (this.singleplayerServer != null) {
            if (this.singleplayerServer.isPublished()) {
                return "hosting_lan";
            }
            return "singleplayer";
        }
        else {
            if (this.currentServer == null) {
                return "out_of_game";
            }
            if (this.currentServer.isLan()) {
                return "playing_lan";
            }
            return "multiplayer";
        }
    }
    
    public static int maxSupportedTextureSize() {
        if (Minecraft.MAX_SUPPORTED_TEXTURE_SIZE == -1) {
            for (int integer1 = 16384; integer1 > 0; integer1 >>= 1) {
                GlStateManager.texImage2D(32868, 0, 6408, integer1, integer1, 0, 6408, 5121, null);
                final int integer2 = GlStateManager.getTexLevelParameter(32868, 0, 4096);
                if (integer2 != 0) {
                    return Minecraft.MAX_SUPPORTED_TEXTURE_SIZE = integer1;
                }
            }
            Minecraft.MAX_SUPPORTED_TEXTURE_SIZE = Mth.clamp(GlStateManager.getInteger(3379), 1024, 16384);
            Minecraft.LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", Minecraft.MAX_SUPPORTED_TEXTURE_SIZE);
        }
        return Minecraft.MAX_SUPPORTED_TEXTURE_SIZE;
    }
    
    public void setCurrentServer(final ServerData dki) {
        this.currentServer = dki;
    }
    
    @Nullable
    public ServerData getCurrentServer() {
        return this.currentServer;
    }
    
    public boolean isLocalServer() {
        return this.isLocalServer;
    }
    
    public boolean hasSingleplayerServer() {
        return this.isLocalServer && this.singleplayerServer != null;
    }
    
    @Nullable
    public IntegratedServer getSingleplayerServer() {
        return this.singleplayerServer;
    }
    
    public Snooper getSnooper() {
        return this.snooper;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public PropertyMap getProfileProperties() {
        if (this.profileProperties.isEmpty()) {
            final GameProfile gameProfile2 = this.getMinecraftSessionService().fillProfileProperties(this.user.getGameProfile(), false);
            this.profileProperties.putAll((Multimap)gameProfile2.getProperties());
        }
        return this.profileProperties;
    }
    
    public Proxy getProxy() {
        return this.proxy;
    }
    
    public TextureManager getTextureManager() {
        return this.textureManager;
    }
    
    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }
    
    public PackRepository<UnopenedResourcePack> getResourcePackRepository() {
        return this.resourcePackRepository;
    }
    
    public ClientPackSource getClientPackSource() {
        return this.clientPackSource;
    }
    
    public File getResourcePackDirectory() {
        return this.resourcePackDirectory;
    }
    
    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }
    
    public TextureAtlas getTextureAtlas() {
        return this.textureAtlas;
    }
    
    public boolean is64Bit() {
        return this.is64bit;
    }
    
    public boolean isPaused() {
        return this.pause;
    }
    
    public SoundManager getSoundManager() {
        return this.soundManager;
    }
    
    public MusicManager.Music getSituationalMusic() {
        if (this.screen instanceof WinScreen) {
            return MusicManager.Music.CREDITS;
        }
        if (this.player == null) {
            return MusicManager.Music.MENU;
        }
        if (this.player.level.dimension instanceof NetherDimension) {
            return MusicManager.Music.NETHER;
        }
        if (this.player.level.dimension instanceof TheEndDimension) {
            if (this.gui.getBossOverlay().shouldPlayMusic()) {
                return MusicManager.Music.END_BOSS;
            }
            return MusicManager.Music.END;
        }
        else {
            final Biome.BiomeCategory b2 = this.player.level.getBiome(new BlockPos(this.player)).getBiomeCategory();
            if (this.musicManager.isPlayingMusic(MusicManager.Music.UNDER_WATER) || (this.player.isUnderWater() && !this.musicManager.isPlayingMusic(MusicManager.Music.GAME) && (b2 == Biome.BiomeCategory.OCEAN || b2 == Biome.BiomeCategory.RIVER))) {
                return MusicManager.Music.UNDER_WATER;
            }
            if (this.player.abilities.instabuild && this.player.abilities.mayfly) {
                return MusicManager.Music.CREATIVE;
            }
            return MusicManager.Music.GAME;
        }
    }
    
    public MinecraftSessionService getMinecraftSessionService() {
        return this.minecraftSessionService;
    }
    
    public SkinManager getSkinManager() {
        return this.skinManager;
    }
    
    @Nullable
    public Entity getCameraEntity() {
        return this.cameraEntity;
    }
    
    public void setCameraEntity(final Entity aio) {
        this.cameraEntity = aio;
        this.gameRenderer.checkEntityPostEffect(aio);
    }
    
    protected Thread getRunningThread() {
        return this.gameThread;
    }
    
    protected Runnable wrapRunnable(final Runnable runnable) {
        return runnable;
    }
    
    protected boolean shouldRun(final Runnable runnable) {
        return true;
    }
    
    public BlockRenderDispatcher getBlockRenderer() {
        return this.blockRenderer;
    }
    
    public EntityRenderDispatcher getEntityRenderDispatcher() {
        return this.entityRenderDispatcher;
    }
    
    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }
    
    public ItemInHandRenderer getItemInHandRenderer() {
        return this.itemInHandRenderer;
    }
    
    public <T> MutableSearchTree<T> getSearchTree(final SearchRegistry.Key<T> a) {
        return this.searchRegistry.<T>getTree(a);
    }
    
    public static int getAverageFps() {
        return Minecraft.fps;
    }
    
    public FrameTimer getFrameTimer() {
        return this.frameTimer;
    }
    
    public boolean isConnectedToRealms() {
        return this.connectedToRealms;
    }
    
    public void setConnectedToRealms(final boolean boolean1) {
        this.connectedToRealms = boolean1;
    }
    
    public DataFixer getFixerUpper() {
        return this.fixerUpper;
    }
    
    public float getFrameTime() {
        return this.timer.partialTick;
    }
    
    public float getDeltaFrameTime() {
        return this.timer.tickDelta;
    }
    
    public BlockColors getBlockColors() {
        return this.blockColors;
    }
    
    public boolean showOnlyReducedInfo() {
        return (this.player != null && this.player.isReducedDebugInfo()) || this.options.reducedDebugInfo;
    }
    
    public ToastComponent getToasts() {
        return this.toast;
    }
    
    public Tutorial getTutorial() {
        return this.tutorial;
    }
    
    public boolean isWindowActive() {
        return this.windowActive;
    }
    
    public HotbarManager getHotbarManager() {
        return this.hotbarManager;
    }
    
    public ModelManager getModelManager() {
        return this.modelManager;
    }
    
    public FontManager getFontManager() {
        return this.fontManager;
    }
    
    public PaintingTextureManager getPaintingTextures() {
        return this.paintingTextures;
    }
    
    public MobEffectTextureManager getMobEffectTextures() {
        return this.mobEffectTextures;
    }
    
    @Override
    public void setWindowActive(final boolean boolean1) {
        this.windowActive = boolean1;
    }
    
    public ProfilerFiller getProfiler() {
        return this.profiler;
    }
    
    public Game getGame() {
        return this.game;
    }
    
    public SplashManager getSplashManager() {
        return this.splashManager;
    }
    
    @Nullable
    public Overlay getOverlay() {
        return this.overlay;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        ON_OSX = (Util.getPlatform() == Util.OS.OSX);
        DEFAULT_FONT = new ResourceLocation("default");
        ALT_FONT = new ResourceLocation("alt");
        RESOURCE_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
        Minecraft.reserve = new byte[10485760];
        Minecraft.MAX_SUPPORTED_TEXTURE_SIZE = -1;
    }
}
