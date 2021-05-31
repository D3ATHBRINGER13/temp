package net.minecraft.server.dedicated;

import org.apache.logging.log4j.LogManager;
import com.mojang.authlib.GameProfile;
import java.util.function.UnaryOperator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.Snooper;
import java.util.function.BooleanSupplier;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import net.minecraft.world.Difficulty;
import com.google.common.base.Strings;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import net.minecraft.world.level.GameRules;
import java.util.Locale;
import com.google.gson.JsonElement;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.LevelType;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Crypt;
import java.net.InetAddress;
import net.minecraft.SharedConstants;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.DefaultUncaughtExceptionHandler;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.commands.Commands;
import java.net.Proxy;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.players.GameProfileCache;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.world.level.GameType;
import net.minecraft.server.rcon.thread.RconThread;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import net.minecraft.server.ConsoleInput;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.MinecraftServer;

public class DedicatedServer extends MinecraftServer implements ServerInterface {
    private static final Logger LOGGER;
    private static final Pattern SHA1;
    private final List<ConsoleInput> consoleInput;
    private QueryThreadGs4 queryThreadGs4;
    private final RconConsoleSource rconConsoleSource;
    private RconThread rconThread;
    private final DedicatedServerSettings settings;
    private GameType gameType;
    @Nullable
    private MinecraftServerGui gui;
    
    public DedicatedServer(final File file, final DedicatedServerSettings um, final DataFixer dataFixer, final YggdrasilAuthenticationService yggdrasilAuthenticationService, final MinecraftSessionService minecraftSessionService, final GameProfileRepository gameProfileRepository, final GameProfileCache xr, final ChunkProgressListenerFactory vu, final String string) {
        super(file, Proxy.NO_PROXY, dataFixer, new Commands(true), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, xr, vu, string);
        this.consoleInput = (List<ConsoleInput>)Collections.synchronizedList((List)Lists.newArrayList());
        this.settings = um;
        this.rconConsoleSource = new RconConsoleSource(this);
        new Thread("Server Infinisleeper") {
            {
                this.setDaemon(true);
                this.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(DedicatedServer.LOGGER));
                this.start();
            }
            
            public void run() {
                while (true) {
                    try {
                        while (true) {
                            Thread.sleep(2147483647L);
                        }
                    }
                    catch (InterruptedException ex) {
                        continue;
                    }
                    break;
                }
            }
        };
    }
    
    public boolean initServer() throws IOException {
        final Thread thread2 = new Thread("Server console handler") {
            public void run() {
                final BufferedReader bufferedReader2 = new BufferedReader((Reader)new InputStreamReader(System.in, StandardCharsets.UTF_8));
                try {
                    String string3;
                    while (!DedicatedServer.this.isStopped() && DedicatedServer.this.isRunning() && (string3 = bufferedReader2.readLine()) != null) {
                        DedicatedServer.this.handleConsoleInput(string3, DedicatedServer.this.createCommandSourceStack());
                    }
                }
                catch (IOException iOException4) {
                    DedicatedServer.LOGGER.error("Exception handling console input", (Throwable)iOException4);
                }
            }
        };
        thread2.setDaemon(true);
        thread2.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(DedicatedServer.LOGGER));
        thread2.start();
        DedicatedServer.LOGGER.info("Starting minecraft server version " + SharedConstants.getCurrentVersion().getName());
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            DedicatedServer.LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }
        DedicatedServer.LOGGER.info("Loading properties");
        final DedicatedServerProperties ul3 = this.settings.getProperties();
        if (this.isSingleplayer()) {
            this.setLocalIp("127.0.0.1");
        }
        else {
            this.setUsesAuthentication(ul3.onlineMode);
            this.setPreventProxyConnections(ul3.preventProxyConnections);
            this.setLocalIp(ul3.serverIp);
        }
        this.setAnimals(ul3.spawnAnimals);
        this.setNpcsEnabled(ul3.spawnNpcs);
        this.setPvpAllowed(ul3.pvp);
        this.setFlightAllowed(ul3.allowFlight);
        this.setResourcePack(ul3.resourcePack, this.getPackHash());
        this.setMotd(ul3.motd);
        this.setForceGameType(ul3.forceGameMode);
        super.setPlayerIdleTimeout(ul3.playerIdleTimeout.get());
        this.setEnforceWhitelist(ul3.enforceWhitelist);
        this.gameType = ul3.gamemode;
        DedicatedServer.LOGGER.info("Default game type: {}", this.gameType);
        InetAddress inetAddress4 = null;
        if (!this.getLocalIp().isEmpty()) {
            inetAddress4 = InetAddress.getByName(this.getLocalIp());
        }
        if (this.getPort() < 0) {
            this.setPort(ul3.serverPort);
        }
        DedicatedServer.LOGGER.info("Generating keypair");
        this.setKeyPair(Crypt.generateKeyPair());
        DedicatedServer.LOGGER.info("Starting Minecraft server on {}:{}", this.getLocalIp().isEmpty() ? "*" : this.getLocalIp(), this.getPort());
        try {
            this.getConnection().startTcpServerListener(inetAddress4, this.getPort());
        }
        catch (IOException iOException5) {
            DedicatedServer.LOGGER.warn("**** FAILED TO BIND TO PORT!");
            DedicatedServer.LOGGER.warn("The exception was: {}", iOException5.toString());
            DedicatedServer.LOGGER.warn("Perhaps a server is already running on that port?");
            return false;
        }
        if (!this.usesAuthentication()) {
            DedicatedServer.LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            DedicatedServer.LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
            DedicatedServer.LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            DedicatedServer.LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }
        if (this.convertOldUsers()) {
            this.getProfileCache().save();
        }
        if (!OldUsersConverter.serverReadyAfterUserconversion(this)) {
            return false;
        }
        this.setPlayerList(new DedicatedPlayerList(this));
        final long long5 = Util.getNanos();
        final String string7 = ul3.levelSeed;
        final String string8 = ul3.generatorSettings;
        long long6 = new Random().nextLong();
        if (!string7.isEmpty()) {
            try {
                final long long7 = Long.parseLong(string7);
                if (long7 != 0L) {
                    long6 = long7;
                }
            }
            catch (NumberFormatException numberFormatException11) {
                long6 = string7.hashCode();
            }
        }
        final LevelType bhy11 = ul3.levelType;
        this.setMaxBuildHeight(ul3.maxBuildHeight);
        SkullBlockEntity.setProfileCache(this.getProfileCache());
        SkullBlockEntity.setSessionService(this.getSessionService());
        GameProfileCache.setUsesAuthentication(this.usesAuthentication());
        DedicatedServer.LOGGER.info("Preparing level \"{}\"", this.getLevelIdName());
        JsonObject jsonObject12 = new JsonObject();
        if (bhy11 == LevelType.FLAT) {
            jsonObject12.addProperty("flat_world_options", string8);
        }
        else if (!string8.isEmpty()) {
            jsonObject12 = GsonHelper.parse(string8);
        }
        this.loadLevel(this.getLevelIdName(), this.getLevelIdName(), long6, bhy11, (JsonElement)jsonObject12);
        final long long8 = Util.getNanos() - long5;
        final String string9 = String.format(Locale.ROOT, "%.3fs", new Object[] { long8 / 1.0E9 });
        DedicatedServer.LOGGER.info("Done ({})! For help, type \"help\"", string9);
        if (ul3.announcePlayerAchievements != null) {
            this.getGameRules().<GameRules.BooleanValue>getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(ul3.announcePlayerAchievements, this);
        }
        if (ul3.enableQuery) {
            DedicatedServer.LOGGER.info("Starting GS4 status listener");
            (this.queryThreadGs4 = new QueryThreadGs4(this)).start();
        }
        if (ul3.enableRcon) {
            DedicatedServer.LOGGER.info("Starting remote control listener");
            (this.rconThread = new RconThread(this)).start();
        }
        if (this.getMaxTickLength() > 0L) {
            final Thread thread3 = new Thread((Runnable)new ServerWatchdog(this));
            thread3.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandlerWithName(DedicatedServer.LOGGER));
            thread3.setName("Server Watchdog");
            thread3.setDaemon(true);
            thread3.start();
        }
        Items.AIR.fillItemCategory(CreativeModeTab.TAB_SEARCH, NonNullList.<ItemStack>create());
        return true;
    }
    
    public String getPackHash() {
        final DedicatedServerProperties ul2 = this.settings.getProperties();
        String string3;
        if (!ul2.resourcePackSha1.isEmpty()) {
            string3 = ul2.resourcePackSha1;
            if (!Strings.isNullOrEmpty(ul2.resourcePackHash)) {
                DedicatedServer.LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
        }
        else if (!Strings.isNullOrEmpty(ul2.resourcePackHash)) {
            DedicatedServer.LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            string3 = ul2.resourcePackHash;
        }
        else {
            string3 = "";
        }
        if (!string3.isEmpty() && !DedicatedServer.SHA1.matcher((CharSequence)string3).matches()) {
            DedicatedServer.LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
        }
        if (!ul2.resourcePack.isEmpty() && string3.isEmpty()) {
            DedicatedServer.LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        }
        return string3;
    }
    
    @Override
    public void setDefaultGameMode(final GameType bho) {
        super.setDefaultGameMode(bho);
        this.gameType = bho;
    }
    
    @Override
    public DedicatedServerProperties getProperties() {
        return this.settings.getProperties();
    }
    
    @Override
    public boolean canGenerateStructures() {
        return this.getProperties().generateStructures;
    }
    
    @Override
    public GameType getDefaultGameType() {
        return this.gameType;
    }
    
    @Override
    public Difficulty getDefaultDifficulty() {
        return this.getProperties().difficulty;
    }
    
    @Override
    public boolean isHardcore() {
        return this.getProperties().hardcore;
    }
    
    @Override
    public CrashReport fillReport(CrashReport d) {
        d = super.fillReport(d);
        d.getSystemDetails().setDetail("Is Modded", (CrashReportDetail<String>)(() -> {
            final String string2 = this.getServerModName();
            if (!"vanilla".equals(string2)) {
                return "Definitely; Server brand changed to '" + string2 + "'";
            }
            return "Unknown (can't tell)";
        }));
        d.getSystemDetails().setDetail("Type", (CrashReportDetail<String>)(() -> "Dedicated Server (map_server.txt)"));
        return d;
    }
    
    public void onServerExit() {
        if (this.gui != null) {
            this.gui.close();
        }
        if (this.rconThread != null) {
            this.rconThread.stop();
        }
        if (this.queryThreadGs4 != null) {
            this.queryThreadGs4.stop();
        }
    }
    
    public void tickChildren(final BooleanSupplier booleanSupplier) {
        super.tickChildren(booleanSupplier);
        this.handleConsoleInputs();
    }
    
    @Override
    public boolean isNetherEnabled() {
        return this.getProperties().allowNether;
    }
    
    public boolean getSpawnMonsters() {
        return this.getProperties().spawnMonsters;
    }
    
    @Override
    public void populateSnooper(final Snooper ahq) {
        ahq.setDynamicData("whitelist_enabled", this.getPlayerList().isUsingWhitelist());
        ahq.setDynamicData("whitelist_count", this.getPlayerList().getWhiteListNames().length);
        super.populateSnooper(ahq);
    }
    
    public void handleConsoleInput(final String string, final CommandSourceStack cd) {
        this.consoleInput.add(new ConsoleInput(string, cd));
    }
    
    public void handleConsoleInputs() {
        while (!this.consoleInput.isEmpty()) {
            final ConsoleInput qz2 = (ConsoleInput)this.consoleInput.remove(0);
            this.getCommands().performCommand(qz2.source, qz2.msg);
        }
    }
    
    @Override
    public boolean isDedicatedServer() {
        return true;
    }
    
    @Override
    public boolean isEpollEnabled() {
        return this.getProperties().useNativeTransport;
    }
    
    @Override
    public DedicatedPlayerList getPlayerList() {
        return (DedicatedPlayerList)super.getPlayerList();
    }
    
    @Override
    public boolean isPublished() {
        return true;
    }
    
    @Override
    public String getServerIp() {
        return this.getLocalIp();
    }
    
    @Override
    public int getServerPort() {
        return this.getPort();
    }
    
    @Override
    public String getServerName() {
        return this.getMotd();
    }
    
    public void showGui() {
        if (this.gui == null) {
            this.gui = MinecraftServerGui.showFrameFor(this);
        }
    }
    
    @Override
    public boolean hasGui() {
        return this.gui != null;
    }
    
    @Override
    public boolean publishServer(final GameType bho, final boolean boolean2, final int integer) {
        return false;
    }
    
    @Override
    public boolean isCommandBlockEnabled() {
        return this.getProperties().enableCommandBlock;
    }
    
    @Override
    public int getSpawnProtectionRadius() {
        return this.getProperties().spawnProtection;
    }
    
    @Override
    public boolean isUnderSpawnProtection(final Level bhr, final BlockPos ew, final Player awg) {
        if (bhr.dimension.getType() != DimensionType.OVERWORLD) {
            return false;
        }
        if (this.getPlayerList().getOps().isEmpty()) {
            return false;
        }
        if (this.getPlayerList().isOp(awg.getGameProfile())) {
            return false;
        }
        if (this.getSpawnProtectionRadius() <= 0) {
            return false;
        }
        final BlockPos ew2 = bhr.getSharedSpawnPos();
        final int integer6 = Mth.abs(ew.getX() - ew2.getX());
        final int integer7 = Mth.abs(ew.getZ() - ew2.getZ());
        final int integer8 = Math.max(integer6, integer7);
        return integer8 <= this.getSpawnProtectionRadius();
    }
    
    @Override
    public int getOperatorUserPermissionLevel() {
        return this.getProperties().opPermissionLevel;
    }
    
    @Override
    public int getFunctionCompilationLevel() {
        return this.getProperties().functionPermissionLevel;
    }
    
    @Override
    public void setPlayerIdleTimeout(final int integer) {
        super.setPlayerIdleTimeout(integer);
        this.settings.update((UnaryOperator<DedicatedServerProperties>)(ul -> ul.playerIdleTimeout.update(integer)));
    }
    
    @Override
    public boolean shouldRconBroadcast() {
        return this.getProperties().broadcastRconToOps;
    }
    
    @Override
    public boolean shouldInformAdmins() {
        return this.getProperties().broadcastConsoleToOps;
    }
    
    @Override
    public int getAbsoluteMaxWorldSize() {
        return this.getProperties().maxWorldSize;
    }
    
    @Override
    public int getCompressionThreshold() {
        return this.getProperties().networkCompressionThreshold;
    }
    
    protected boolean convertOldUsers() {
        boolean boolean3 = false;
        for (int integer2 = 0; !boolean3 && integer2 <= 2; boolean3 = OldUsersConverter.convertUserBanlist(this), ++integer2) {
            if (integer2 > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.waitForRetry();
            }
        }
        boolean boolean4 = false;
        for (int integer2 = 0; !boolean4 && integer2 <= 2; boolean4 = OldUsersConverter.convertIpBanlist(this), ++integer2) {
            if (integer2 > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.waitForRetry();
            }
        }
        boolean boolean5 = false;
        for (int integer2 = 0; !boolean5 && integer2 <= 2; boolean5 = OldUsersConverter.convertOpsList(this), ++integer2) {
            if (integer2 > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.waitForRetry();
            }
        }
        boolean boolean6 = false;
        for (int integer2 = 0; !boolean6 && integer2 <= 2; boolean6 = OldUsersConverter.convertWhiteList(this), ++integer2) {
            if (integer2 > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.waitForRetry();
            }
        }
        boolean boolean7 = false;
        for (int integer2 = 0; !boolean7 && integer2 <= 2; boolean7 = OldUsersConverter.convertPlayers(this), ++integer2) {
            if (integer2 > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.waitForRetry();
            }
        }
        return boolean3 || boolean4 || boolean5 || boolean6 || boolean7;
    }
    
    private void waitForRetry() {
        try {
            Thread.sleep(5000L);
        }
        catch (InterruptedException interruptedException2) {}
    }
    
    public long getMaxTickLength() {
        return this.getProperties().maxTickTime;
    }
    
    @Override
    public String getPluginNames() {
        return "";
    }
    
    @Override
    public String runCommand(final String string) {
        this.rconConsoleSource.prepareForCommand();
        this.executeBlocking(() -> this.getCommands().performCommand(this.rconConsoleSource.createCommandSourceStack(), string));
        return this.rconConsoleSource.getCommandResponse();
    }
    
    public void storeUsingWhiteList(final boolean boolean1) {
        this.settings.update((UnaryOperator<DedicatedServerProperties>)(ul -> ul.whiteList.update(boolean1)));
    }
    
    public void stopServer() {
        super.stopServer();
        Util.shutdownBackgroundExecutor();
    }
    
    @Override
    public boolean isSingleplayerOwner(final GameProfile gameProfile) {
        return false;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
    }
}
