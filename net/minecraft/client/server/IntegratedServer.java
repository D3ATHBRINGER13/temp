package net.minecraft.client.server;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.ClientBrandRetriever;
import java.util.List;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import net.minecraft.server.level.ServerPlayer;
import java.net.InetAddress;
import net.minecraft.world.Snooper;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.util.profiling.GameProfiler;
import java.util.function.BooleanSupplier;
import java.io.IOException;
import net.minecraft.util.Crypt;
import net.minecraft.SharedConstants;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import com.google.gson.JsonElement;
import net.minecraft.world.level.LevelType;
import net.minecraft.server.players.PlayerList;
import net.minecraft.commands.Commands;
import java.io.File;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.players.GameProfileCache;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.util.UUID;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.MinecraftServer;

public class IntegratedServer extends MinecraftServer {
    private static final Logger LOGGER;
    private final Minecraft minecraft;
    private final LevelSettings settings;
    private boolean paused;
    private int publishedPort;
    private LanServerPinger lanPinger;
    private UUID uuid;
    
    public IntegratedServer(final Minecraft cyc, final String string2, final String string3, final LevelSettings bhv, final YggdrasilAuthenticationService yggdrasilAuthenticationService, final MinecraftSessionService minecraftSessionService, final GameProfileRepository gameProfileRepository, final GameProfileCache xr, final ChunkProgressListenerFactory vu) {
        super(new File(cyc.gameDirectory, "saves"), cyc.getProxy(), cyc.getFixerUpper(), new Commands(false), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, xr, vu, string2);
        this.publishedPort = -1;
        this.setSingleplayerName(cyc.getUser().getName());
        this.setLevelName(string3);
        this.setDemo(cyc.isDemo());
        this.setBonusChest(bhv.hasStartingBonusItems());
        this.setMaxBuildHeight(256);
        this.setPlayerList(new IntegratedPlayerList(this));
        this.minecraft = cyc;
        this.settings = (this.isDemo() ? MinecraftServer.DEMO_SETTINGS : bhv);
    }
    
    public void loadLevel(final String string1, final String string2, final long long3, final LevelType bhy, final JsonElement jsonElement) {
        this.ensureLevelConversion(string1);
        final LevelStorage coo8 = this.getStorageSource().selectLevel(string1, this);
        this.detectBundledResources(this.getLevelIdName(), coo8);
        LevelData com9 = coo8.prepareLevel();
        if (com9 == null) {
            com9 = new LevelData(this.settings, string2);
        }
        else {
            com9.setLevelName(string2);
        }
        this.loadDataPacks(coo8.getFolder(), com9);
        final ChunkProgressListener vt10 = this.progressListenerFactory.create(11);
        this.createLevels(coo8, com9, this.settings, vt10);
        if (this.getLevel(DimensionType.OVERWORLD).getLevelData().getDifficulty() == null) {
            this.setDifficulty(this.minecraft.options.difficulty, true);
        }
        this.prepareLevels(vt10);
    }
    
    public boolean initServer() throws IOException {
        IntegratedServer.LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getCurrentVersion().getName());
        this.setUsesAuthentication(true);
        this.setAnimals(true);
        this.setNpcsEnabled(true);
        this.setPvpAllowed(true);
        this.setFlightAllowed(true);
        IntegratedServer.LOGGER.info("Generating keypair");
        this.setKeyPair(Crypt.generateKeyPair());
        this.loadLevel(this.getLevelIdName(), this.getLevelName(), this.settings.getSeed(), this.settings.getLevelType(), this.settings.getLevelTypeOptions());
        this.setMotd(this.getSingleplayerName() + " - " + this.getLevel(DimensionType.OVERWORLD).getLevelData().getLevelName());
        return true;
    }
    
    public void tickServer(final BooleanSupplier booleanSupplier) {
        final boolean boolean3 = this.paused;
        this.paused = (Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isPaused());
        final GameProfiler agj4 = this.getProfiler();
        if (!boolean3 && this.paused) {
            agj4.push("autoSave");
            IntegratedServer.LOGGER.info("Saving and pausing game...");
            this.getPlayerList().saveAll();
            this.saveAllChunks(false, false, false);
            agj4.pop();
        }
        if (this.paused) {
            return;
        }
        super.tickServer(booleanSupplier);
        final int integer5 = Math.max(2, this.minecraft.options.renderDistance - 2);
        if (integer5 != this.getPlayerList().getViewDistance()) {
            IntegratedServer.LOGGER.info("Changing view distance to {}, from {}", integer5, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(integer5);
        }
    }
    
    @Override
    public boolean canGenerateStructures() {
        return false;
    }
    
    @Override
    public GameType getDefaultGameType() {
        return this.settings.getGameType();
    }
    
    @Override
    public Difficulty getDefaultDifficulty() {
        return this.minecraft.level.getLevelData().getDifficulty();
    }
    
    @Override
    public boolean isHardcore() {
        return this.settings.isHardcore();
    }
    
    @Override
    public boolean shouldRconBroadcast() {
        return true;
    }
    
    @Override
    public boolean shouldInformAdmins() {
        return true;
    }
    
    @Override
    public File getServerDirectory() {
        return this.minecraft.gameDirectory;
    }
    
    @Override
    public boolean isDedicatedServer() {
        return false;
    }
    
    @Override
    public boolean isEpollEnabled() {
        return false;
    }
    
    public void onServerCrash(final CrashReport d) {
        this.minecraft.delayCrash(d);
    }
    
    @Override
    public CrashReport fillReport(CrashReport d) {
        d = super.fillReport(d);
        d.getSystemDetails().setDetail("Type", "Integrated Server (map_client.txt)");
        d.getSystemDetails().setDetail("Is Modded", (CrashReportDetail<String>)(() -> {
            String string2 = ClientBrandRetriever.getClientModName();
            if (!string2.equals("vanilla")) {
                return "Definitely; Client brand changed to '" + string2 + "'";
            }
            string2 = this.getServerModName();
            if (!"vanilla".equals(string2)) {
                return "Definitely; Server brand changed to '" + string2 + "'";
            }
            if (Minecraft.class.getSigners() == null) {
                return "Very likely; Jar signature invalidated";
            }
            return "Probably not. Jar signature remains and both client + server brands are untouched.";
        }));
        return d;
    }
    
    @Override
    public void populateSnooper(final Snooper ahq) {
        super.populateSnooper(ahq);
        ahq.setDynamicData("snooper_partner", this.minecraft.getSnooper().getToken());
    }
    
    @Override
    public boolean publishServer(final GameType bho, final boolean boolean2, final int integer) {
        try {
            this.getConnection().startTcpServerListener(null, integer);
            IntegratedServer.LOGGER.info("Started serving on {}", integer);
            this.publishedPort = integer;
            (this.lanPinger = new LanServerPinger(this.getMotd(), new StringBuilder().append(integer).append("").toString())).start();
            this.getPlayerList().setOverrideGameMode(bho);
            this.getPlayerList().setAllowCheatsForAllPlayers(boolean2);
            final int integer2 = this.getProfilePermissions(this.minecraft.player.getGameProfile());
            this.minecraft.player.setPermissionLevel(integer2);
            for (final ServerPlayer vl7 : this.getPlayerList().getPlayers()) {
                this.getCommands().sendCommands(vl7);
            }
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    public void stopServer() {
        super.stopServer();
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }
    
    @Override
    public void halt(final boolean boolean1) {
        this.executeBlocking(() -> {
            final List<ServerPlayer> list2 = (List<ServerPlayer>)Lists.newArrayList((Iterable)this.getPlayerList().getPlayers());
            for (final ServerPlayer vl4 : list2) {
                if (!vl4.getUUID().equals(this.uuid)) {
                    this.getPlayerList().remove(vl4);
                }
            }
        });
        super.halt(boolean1);
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }
    
    @Override
    public boolean isPublished() {
        return this.publishedPort > -1;
    }
    
    @Override
    public int getPort() {
        return this.publishedPort;
    }
    
    @Override
    public void setDefaultGameMode(final GameType bho) {
        super.setDefaultGameMode(bho);
        this.getPlayerList().setOverrideGameMode(bho);
    }
    
    @Override
    public boolean isCommandBlockEnabled() {
        return true;
    }
    
    @Override
    public int getOperatorUserPermissionLevel() {
        return 2;
    }
    
    @Override
    public int getFunctionCompilationLevel() {
        return 2;
    }
    
    public void setUUID(final UUID uUID) {
        this.uuid = uUID;
    }
    
    @Override
    public boolean isSingleplayerOwner(final GameProfile gameProfile) {
        return gameProfile.getName().equalsIgnoreCase(this.getSingleplayerName());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
