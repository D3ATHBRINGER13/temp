package net.minecraft.server.dedicated;

import net.minecraft.util.Mth;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.Properties;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.Difficulty;

public class DedicatedServerProperties extends Settings<DedicatedServerProperties> {
    public final boolean onlineMode;
    public final boolean preventProxyConnections;
    public final String serverIp;
    public final boolean spawnAnimals;
    public final boolean spawnNpcs;
    public final boolean pvp;
    public final boolean allowFlight;
    public final String resourcePack;
    public final String motd;
    public final boolean forceGameMode;
    public final boolean enforceWhitelist;
    public final boolean generateStructures;
    public final Difficulty difficulty;
    public final GameType gamemode;
    public final String levelName;
    public final String levelSeed;
    public final LevelType levelType;
    public final String generatorSettings;
    public final int serverPort;
    public final int maxBuildHeight;
    public final Boolean announcePlayerAchievements;
    public final boolean enableQuery;
    public final int queryPort;
    public final boolean enableRcon;
    public final int rconPort;
    public final String rconPassword;
    public final String resourcePackHash;
    public final String resourcePackSha1;
    public final boolean hardcore;
    public final boolean allowNether;
    public final boolean spawnMonsters;
    public final boolean snooperEnabled;
    public final boolean useNativeTransport;
    public final boolean enableCommandBlock;
    public final int spawnProtection;
    public final int opPermissionLevel;
    public final int functionPermissionLevel;
    public final long maxTickTime;
    public final int viewDistance;
    public final int maxPlayers;
    public final int networkCompressionThreshold;
    public final boolean broadcastRconToOps;
    public final boolean broadcastConsoleToOps;
    public final int maxWorldSize;
    public final MutableValue<Integer> playerIdleTimeout;
    public final MutableValue<Boolean> whiteList;
    
    public DedicatedServerProperties(final Properties properties) {
        super(properties);
        this.onlineMode = this.get("online-mode", true);
        this.preventProxyConnections = this.get("prevent-proxy-connections", false);
        this.serverIp = this.get("server-ip", "");
        this.spawnAnimals = this.get("spawn-animals", true);
        this.spawnNpcs = this.get("spawn-npcs", true);
        this.pvp = this.get("pvp", true);
        this.allowFlight = this.get("allow-flight", false);
        this.resourcePack = this.get("resource-pack", "");
        this.motd = this.get("motd", "A Minecraft Server");
        this.forceGameMode = this.get("force-gamemode", false);
        this.enforceWhitelist = this.get("enforce-whitelist", false);
        this.generateStructures = this.get("generate-structures", true);
        this.difficulty = this.<Difficulty>get("difficulty", (java.util.function.Function<String, Difficulty>)Settings.<V>dispatchNumberOrString((java.util.function.IntFunction<V>)Difficulty::byId, (java.util.function.Function<String, V>)Difficulty::byName), (java.util.function.Function<Difficulty, String>)Difficulty::getKey, Difficulty.EASY);
        this.gamemode = this.<GameType>get("gamemode", (java.util.function.Function<String, GameType>)Settings.<V>dispatchNumberOrString((java.util.function.IntFunction<V>)GameType::byId, (java.util.function.Function<String, V>)GameType::byName), (java.util.function.Function<GameType, String>)GameType::getName, GameType.SURVIVAL);
        this.levelName = this.get("level-name", "world");
        this.levelSeed = this.get("level-seed", "");
        this.levelType = this.<LevelType>get("level-type", (java.util.function.Function<String, LevelType>)LevelType::getLevelType, (java.util.function.Function<LevelType, String>)LevelType::getName, LevelType.NORMAL);
        this.generatorSettings = this.get("generator-settings", "");
        this.serverPort = this.get("server-port", 25565);
        this.maxBuildHeight = this.get("max-build-height", (UnaryOperator<Integer>)(integer -> Mth.clamp((integer + 8) / 16 * 16, 64, 256)), 256);
        this.announcePlayerAchievements = this.getLegacyBoolean("announce-player-achievements");
        this.enableQuery = this.get("enable-query", false);
        this.queryPort = this.get("query.port", 25565);
        this.enableRcon = this.get("enable-rcon", false);
        this.rconPort = this.get("rcon.port", 25575);
        this.rconPassword = this.get("rcon.password", "");
        this.resourcePackHash = this.getLegacyString("resource-pack-hash");
        this.resourcePackSha1 = this.get("resource-pack-sha1", "");
        this.hardcore = this.get("hardcore", false);
        this.allowNether = this.get("allow-nether", true);
        this.spawnMonsters = this.get("spawn-monsters", true);
        if (this.get("snooper-enabled", true)) {}
        this.snooperEnabled = false;
        this.useNativeTransport = this.get("use-native-transport", true);
        this.enableCommandBlock = this.get("enable-command-block", false);
        this.spawnProtection = this.get("spawn-protection", 16);
        this.opPermissionLevel = this.get("op-permission-level", 4);
        this.functionPermissionLevel = this.get("function-permission-level", 2);
        this.maxTickTime = this.get("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
        this.viewDistance = this.get("view-distance", 10);
        this.maxPlayers = this.get("max-players", 20);
        this.networkCompressionThreshold = this.get("network-compression-threshold", 256);
        this.broadcastRconToOps = this.get("broadcast-rcon-to-ops", true);
        this.broadcastConsoleToOps = this.get("broadcast-console-to-ops", true);
        this.maxWorldSize = this.get("max-world-size", (UnaryOperator<Integer>)(integer -> Mth.clamp(integer, 1, 29999984)), 29999984);
        this.playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
        this.whiteList = this.getMutable("white-list", false);
    }
    
    public static DedicatedServerProperties fromFile(final Path path) {
        return new DedicatedServerProperties(Settings.loadFromFile(path));
    }
    
    @Override
    protected DedicatedServerProperties reload(final Properties properties) {
        return new DedicatedServerProperties(properties);
    }
}
