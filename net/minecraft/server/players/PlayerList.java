package net.minecraft.server.players;

import org.apache.logging.log4j.LogManager;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.chat.ChatType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import java.util.Collection;
import net.minecraft.world.scores.Team;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelReader;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.level.DemoMode;
import java.net.SocketAddress;
import net.minecraft.stats.Stats;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.network.protocol.game.ClientboundSetBorderPacket;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.scores.Objective;
import java.util.Set;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import com.google.common.collect.Sets;
import net.minecraft.server.ServerScoreboard;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.nbt.CompoundTag;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.entity.Entity;
import java.util.function.Function;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.FriendlyByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.world.level.GameRules;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.network.Connection;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.PlayerIO;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.stats.ServerStatsCounter;
import java.util.UUID;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.Logger;
import java.io.File;

public abstract class PlayerList {
    public static final File USERBANLIST_FILE;
    public static final File IPBANLIST_FILE;
    public static final File OPLIST_FILE;
    public static final File WHITELIST_FILE;
    private static final Logger LOGGER;
    private static final SimpleDateFormat BAN_DATE_FORMAT;
    private final MinecraftServer server;
    private final List<ServerPlayer> players;
    private final Map<UUID, ServerPlayer> playersByUUID;
    private final UserBanList bans;
    private final IpBanList ipBans;
    private final ServerOpList ops;
    private final UserWhiteList whitelist;
    private final Map<UUID, ServerStatsCounter> stats;
    private final Map<UUID, PlayerAdvancements> advancements;
    private PlayerIO playerIo;
    private boolean doWhiteList;
    protected final int maxPlayers;
    private int viewDistance;
    private GameType overrideGameMode;
    private boolean allowCheatsForAllPlayers;
    private int sendAllPlayerInfoIn;
    
    public PlayerList(final MinecraftServer minecraftServer, final int integer) {
        this.players = (List<ServerPlayer>)Lists.newArrayList();
        this.playersByUUID = (Map<UUID, ServerPlayer>)Maps.newHashMap();
        this.bans = new UserBanList(PlayerList.USERBANLIST_FILE);
        this.ipBans = new IpBanList(PlayerList.IPBANLIST_FILE);
        this.ops = new ServerOpList(PlayerList.OPLIST_FILE);
        this.whitelist = new UserWhiteList(PlayerList.WHITELIST_FILE);
        this.stats = (Map<UUID, ServerStatsCounter>)Maps.newHashMap();
        this.advancements = (Map<UUID, PlayerAdvancements>)Maps.newHashMap();
        this.server = minecraftServer;
        this.maxPlayers = integer;
        this.getBans().setEnabled(true);
        this.getIpBans().setEnabled(true);
    }
    
    public void placeNewPlayer(final Connection jc, final ServerPlayer vl) {
        final GameProfile gameProfile4 = vl.getGameProfile();
        final GameProfileCache xr5 = this.server.getProfileCache();
        final GameProfile gameProfile5 = xr5.get(gameProfile4.getId());
        final String string7 = (gameProfile5 == null) ? gameProfile4.getName() : gameProfile5.getName();
        xr5.add(gameProfile4);
        final CompoundTag id8 = this.load(vl);
        final ServerLevel vk9 = this.server.getLevel(vl.dimension);
        vl.setLevel(vk9);
        vl.gameMode.setLevel((ServerLevel)vl.level);
        String string8 = "local";
        if (jc.getRemoteAddress() != null) {
            string8 = jc.getRemoteAddress().toString();
        }
        PlayerList.LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", vl.getName().getString(), string8, vl.getId(), vl.x, vl.y, vl.z);
        final LevelData com11 = vk9.getLevelData();
        this.updatePlayerGameMode(vl, null, vk9);
        final ServerGamePacketListenerImpl wc12 = new ServerGamePacketListenerImpl(this.server, jc, vl);
        wc12.send(new ClientboundLoginPacket(vl.getId(), vl.gameMode.getGameModeForPlayer(), com11.isHardcore(), vk9.dimension.getType(), this.getMaxPlayers(), com11.getGeneratorType(), this.viewDistance, vk9.getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO)));
        wc12.send(new ClientboundCustomPayloadPacket(ClientboundCustomPayloadPacket.BRAND, new FriendlyByteBuf(Unpooled.buffer()).writeUtf(this.getServer().getServerModName())));
        wc12.send(new ClientboundChangeDifficultyPacket(com11.getDifficulty(), com11.isDifficultyLocked()));
        wc12.send(new ClientboundPlayerAbilitiesPacket(vl.abilities));
        wc12.send(new ClientboundSetCarriedItemPacket(vl.inventory.selected));
        wc12.send(new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
        wc12.send(new ClientboundUpdateTagsPacket(this.server.getTags()));
        this.sendPlayerPermissionLevel(vl);
        vl.getStats().markAllDirty();
        vl.getRecipeBook().sendInitialRecipeBook(vl);
        this.updateEntireScoreboard(vk9.getScoreboard(), vl);
        this.server.invalidateStatus();
        Component jo13;
        if (vl.getGameProfile().getName().equalsIgnoreCase(string7)) {
            jo13 = new TranslatableComponent("multiplayer.player.joined", new Object[] { vl.getDisplayName() });
        }
        else {
            jo13 = new TranslatableComponent("multiplayer.player.joined.renamed", new Object[] { vl.getDisplayName(), string7 });
        }
        this.broadcastMessage(jo13.withStyle(ChatFormatting.YELLOW));
        wc12.teleport(vl.x, vl.y, vl.z, vl.yRot, vl.xRot);
        this.players.add(vl);
        this.playersByUUID.put(vl.getUUID(), vl);
        this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, new ServerPlayer[] { vl }));
        for (int integer14 = 0; integer14 < this.players.size(); ++integer14) {
            vl.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, new ServerPlayer[] { (ServerPlayer)this.players.get(integer14) }));
        }
        vk9.addNewPlayer(vl);
        this.server.getCustomBossEvents().onPlayerConnect(vl);
        this.sendLevelInfo(vl, vk9);
        if (!this.server.getResourcePack().isEmpty()) {
            vl.sendTexturePack(this.server.getResourcePack(), this.server.getResourcePackHash());
        }
        for (final MobEffectInstance aii15 : vl.getActiveEffects()) {
            wc12.send(new ClientboundUpdateMobEffectPacket(vl.getId(), aii15));
        }
        if (id8 != null && id8.contains("RootVehicle", 10)) {
            final CompoundTag id9 = id8.getCompound("RootVehicle");
            final Entity aio15 = EntityType.loadEntityRecursive(id9.getCompound("Entity"), vk9, (Function<Entity, Entity>)(aio -> {
                if (!vk9.addWithUUID(aio)) {
                    return null;
                }
                return aio;
            }));
            if (aio15 != null) {
                final UUID uUID16 = id9.getUUID("Attach");
                if (aio15.getUUID().equals(uUID16)) {
                    vl.startRiding(aio15, true);
                }
                else {
                    for (final Entity aio16 : aio15.getIndirectPassengers()) {
                        if (aio16.getUUID().equals(uUID16)) {
                            vl.startRiding(aio16, true);
                            break;
                        }
                    }
                }
                if (!vl.isPassenger()) {
                    PlayerList.LOGGER.warn("Couldn't reattach entity to player");
                    vk9.despawn(aio15);
                    for (final Entity aio16 : aio15.getIndirectPassengers()) {
                        vk9.despawn(aio16);
                    }
                }
            }
        }
        vl.initMenu();
    }
    
    protected void updateEntireScoreboard(final ServerScoreboard rj, final ServerPlayer vl) {
        final Set<Objective> set4 = (Set<Objective>)Sets.newHashSet();
        for (final PlayerTeam ctg6 : rj.getPlayerTeams()) {
            vl.connection.send(new ClientboundSetPlayerTeamPacket(ctg6, 0));
        }
        for (int integer5 = 0; integer5 < 19; ++integer5) {
            final Objective ctf6 = rj.getDisplayObjective(integer5);
            if (ctf6 != null && !set4.contains(ctf6)) {
                final List<Packet<?>> list7 = rj.getStartTrackingPackets(ctf6);
                for (final Packet<?> kc9 : list7) {
                    vl.connection.send(kc9);
                }
                set4.add(ctf6);
            }
        }
    }
    
    public void setLevel(final ServerLevel vk) {
        this.playerIo = vk.getLevelStorage();
        vk.getWorldBorder().addListener(new BorderChangeListener() {
            public void onBorderSizeSet(final WorldBorder bxf, final double double2) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(bxf, ClientboundSetBorderPacket.Type.SET_SIZE));
            }
            
            public void onBorderSizeLerping(final WorldBorder bxf, final double double2, final double double3, final long long4) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(bxf, ClientboundSetBorderPacket.Type.LERP_SIZE));
            }
            
            public void onBorderCenterSet(final WorldBorder bxf, final double double2, final double double3) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(bxf, ClientboundSetBorderPacket.Type.SET_CENTER));
            }
            
            public void onBorderSetWarningTime(final WorldBorder bxf, final int integer) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(bxf, ClientboundSetBorderPacket.Type.SET_WARNING_TIME));
            }
            
            public void onBorderSetWarningBlocks(final WorldBorder bxf, final int integer) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(bxf, ClientboundSetBorderPacket.Type.SET_WARNING_BLOCKS));
            }
            
            public void onBorderSetDamagePerBlock(final WorldBorder bxf, final double double2) {
            }
            
            public void onBorderSetDamageSafeZOne(final WorldBorder bxf, final double double2) {
            }
        });
    }
    
    @Nullable
    public CompoundTag load(final ServerPlayer vl) {
        final CompoundTag id3 = this.server.getLevel(DimensionType.OVERWORLD).getLevelData().getLoadedPlayerTag();
        CompoundTag id4;
        if (vl.getName().getString().equals(this.server.getSingleplayerName()) && id3 != null) {
            id4 = id3;
            vl.load(id4);
            PlayerList.LOGGER.debug("loading single player");
        }
        else {
            id4 = this.playerIo.load(vl);
        }
        return id4;
    }
    
    protected void save(final ServerPlayer vl) {
        this.playerIo.save(vl);
        final ServerStatsCounter yu3 = (ServerStatsCounter)this.stats.get(vl.getUUID());
        if (yu3 != null) {
            yu3.save();
        }
        final PlayerAdvancements re4 = (PlayerAdvancements)this.advancements.get(vl.getUUID());
        if (re4 != null) {
            re4.save();
        }
    }
    
    public void remove(final ServerPlayer vl) {
        final ServerLevel vk3 = vl.getLevel();
        vl.awardStat(Stats.LEAVE_GAME);
        this.save(vl);
        if (vl.isPassenger()) {
            final Entity aio4 = vl.getRootVehicle();
            if (aio4.hasOnePlayerPassenger()) {
                PlayerList.LOGGER.debug("Removing player mount");
                vl.stopRiding();
                vk3.despawn(aio4);
                for (final Entity aio5 : aio4.getIndirectPassengers()) {
                    vk3.despawn(aio5);
                }
                vk3.getChunk(vl.xChunk, vl.zChunk).markUnsaved();
            }
        }
        vl.unRide();
        vk3.removePlayerImmediately(vl);
        vl.getAdvancements().stopListening();
        this.players.remove(vl);
        this.server.getCustomBossEvents().onPlayerDisconnect(vl);
        final UUID uUID4 = vl.getUUID();
        final ServerPlayer vl2 = (ServerPlayer)this.playersByUUID.get(uUID4);
        if (vl2 == vl) {
            this.playersByUUID.remove(uUID4);
            this.stats.remove(uUID4);
            this.advancements.remove(uUID4);
        }
        this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, new ServerPlayer[] { vl }));
    }
    
    @Nullable
    public Component canPlayerLogin(final SocketAddress socketAddress, final GameProfile gameProfile) {
        if (this.bans.isBanned(gameProfile)) {
            final UserBanListEntry yb4 = this.bans.get(gameProfile);
            final Component jo5 = new TranslatableComponent("multiplayer.disconnect.banned.reason", new Object[] { yb4.getReason() });
            if (yb4.getExpires() != null) {
                jo5.append(new TranslatableComponent("multiplayer.disconnect.banned.expiration", new Object[] { PlayerList.BAN_DATE_FORMAT.format(yb4.getExpires()) }));
            }
            return jo5;
        }
        if (!this.isWhiteListed(gameProfile)) {
            return new TranslatableComponent("multiplayer.disconnect.not_whitelisted", new Object[0]);
        }
        if (this.ipBans.isBanned(socketAddress)) {
            final IpBanListEntry xt4 = this.ipBans.get(socketAddress);
            final Component jo5 = new TranslatableComponent("multiplayer.disconnect.banned_ip.reason", new Object[] { xt4.getReason() });
            if (xt4.getExpires() != null) {
                jo5.append(new TranslatableComponent("multiplayer.disconnect.banned_ip.expiration", new Object[] { PlayerList.BAN_DATE_FORMAT.format(xt4.getExpires()) }));
            }
            return jo5;
        }
        if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameProfile)) {
            return new TranslatableComponent("multiplayer.disconnect.server_full", new Object[0]);
        }
        return null;
    }
    
    public ServerPlayer getPlayerForLogin(final GameProfile gameProfile) {
        final UUID uUID3 = Player.createPlayerUUID(gameProfile);
        final List<ServerPlayer> list4 = (List<ServerPlayer>)Lists.newArrayList();
        for (int integer5 = 0; integer5 < this.players.size(); ++integer5) {
            final ServerPlayer vl6 = (ServerPlayer)this.players.get(integer5);
            if (vl6.getUUID().equals(uUID3)) {
                list4.add(vl6);
            }
        }
        final ServerPlayer vl7 = (ServerPlayer)this.playersByUUID.get(gameProfile.getId());
        if (vl7 != null && !list4.contains(vl7)) {
            list4.add(vl7);
        }
        for (final ServerPlayer vl8 : list4) {
            vl8.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.duplicate_login", new Object[0]));
        }
        ServerPlayerGameMode vm6;
        if (this.server.isDemo()) {
            vm6 = new DemoMode(this.server.getLevel(DimensionType.OVERWORLD));
        }
        else {
            vm6 = new ServerPlayerGameMode(this.server.getLevel(DimensionType.OVERWORLD));
        }
        return new ServerPlayer(this.server, this.server.getLevel(DimensionType.OVERWORLD), gameProfile, vm6);
    }
    
    public ServerPlayer respawn(final ServerPlayer vl, final DimensionType byn, final boolean boolean3) {
        this.players.remove(vl);
        vl.getLevel().removePlayerImmediately(vl);
        final BlockPos ew5 = vl.getRespawnPosition();
        final boolean boolean4 = vl.isRespawnForced();
        vl.dimension = byn;
        ServerPlayerGameMode vm7;
        if (this.server.isDemo()) {
            vm7 = new DemoMode(this.server.getLevel(vl.dimension));
        }
        else {
            vm7 = new ServerPlayerGameMode(this.server.getLevel(vl.dimension));
        }
        final ServerPlayer vl2 = new ServerPlayer(this.server, this.server.getLevel(vl.dimension), vl.getGameProfile(), vm7);
        vl2.connection = vl.connection;
        vl2.restoreFrom(vl, boolean3);
        vl2.setId(vl.getId());
        vl2.setMainArm(vl.getMainArm());
        for (final String string10 : vl.getTags()) {
            vl2.addTag(string10);
        }
        final ServerLevel vk9 = this.server.getLevel(vl.dimension);
        this.updatePlayerGameMode(vl2, vl, vk9);
        if (ew5 != null) {
            final Optional<Vec3> optional10 = Player.checkBedValidRespawnPosition(this.server.getLevel(vl.dimension), ew5, boolean4);
            if (optional10.isPresent()) {
                final Vec3 csi11 = (Vec3)optional10.get();
                vl2.moveTo(csi11.x, csi11.y, csi11.z, 0.0f, 0.0f);
                vl2.setRespawnPosition(ew5, boolean4);
            }
            else {
                vl2.connection.send(new ClientboundGameEventPacket(0, 0.0f));
            }
        }
        while (!vk9.noCollision(vl2) && vl2.y < 256.0) {
            vl2.setPos(vl2.x, vl2.y + 1.0, vl2.z);
        }
        final LevelData com10 = vl2.level.getLevelData();
        vl2.connection.send(new ClientboundRespawnPacket(vl2.dimension, com10.getGeneratorType(), vl2.gameMode.getGameModeForPlayer()));
        final BlockPos ew6 = vk9.getSharedSpawnPos();
        vl2.connection.teleport(vl2.x, vl2.y, vl2.z, vl2.yRot, vl2.xRot);
        vl2.connection.send(new ClientboundSetSpawnPositionPacket(ew6));
        vl2.connection.send(new ClientboundChangeDifficultyPacket(com10.getDifficulty(), com10.isDifficultyLocked()));
        vl2.connection.send(new ClientboundSetExperiencePacket(vl2.experienceProgress, vl2.totalExperience, vl2.experienceLevel));
        this.sendLevelInfo(vl2, vk9);
        this.sendPlayerPermissionLevel(vl2);
        vk9.addRespawnedPlayer(vl2);
        this.players.add(vl2);
        this.playersByUUID.put(vl2.getUUID(), vl2);
        vl2.initMenu();
        vl2.setHealth(vl2.getHealth());
        return vl2;
    }
    
    public void sendPlayerPermissionLevel(final ServerPlayer vl) {
        final GameProfile gameProfile3 = vl.getGameProfile();
        final int integer4 = this.server.getProfilePermissions(gameProfile3);
        this.sendPlayerPermissionLevel(vl, integer4);
    }
    
    public void tick() {
        if (++this.sendAllPlayerInfoIn > 600) {
            this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_LATENCY, (Iterable<ServerPlayer>)this.players));
            this.sendAllPlayerInfoIn = 0;
        }
    }
    
    public void broadcastAll(final Packet<?> kc) {
        for (int integer3 = 0; integer3 < this.players.size(); ++integer3) {
            ((ServerPlayer)this.players.get(integer3)).connection.send(kc);
        }
    }
    
    public void broadcastAll(final Packet<?> kc, final DimensionType byn) {
        for (int integer4 = 0; integer4 < this.players.size(); ++integer4) {
            final ServerPlayer vl5 = (ServerPlayer)this.players.get(integer4);
            if (vl5.dimension == byn) {
                vl5.connection.send(kc);
            }
        }
    }
    
    public void broadcastToTeam(final Player awg, final Component jo) {
        final Team ctk4 = awg.getTeam();
        if (ctk4 == null) {
            return;
        }
        final Collection<String> collection5 = ctk4.getPlayers();
        for (final String string7 : collection5) {
            final ServerPlayer vl8 = this.getPlayerByName(string7);
            if (vl8 != null) {
                if (vl8 == awg) {
                    continue;
                }
                vl8.sendMessage(jo);
            }
        }
    }
    
    public void broadcastToAllExceptTeam(final Player awg, final Component jo) {
        final Team ctk4 = awg.getTeam();
        if (ctk4 == null) {
            this.broadcastMessage(jo);
            return;
        }
        for (int integer5 = 0; integer5 < this.players.size(); ++integer5) {
            final ServerPlayer vl6 = (ServerPlayer)this.players.get(integer5);
            if (vl6.getTeam() != ctk4) {
                vl6.sendMessage(jo);
            }
        }
    }
    
    public String[] getPlayerNamesArray() {
        final String[] arr2 = new String[this.players.size()];
        for (int integer3 = 0; integer3 < this.players.size(); ++integer3) {
            arr2[integer3] = ((ServerPlayer)this.players.get(integer3)).getGameProfile().getName();
        }
        return arr2;
    }
    
    public UserBanList getBans() {
        return this.bans;
    }
    
    public IpBanList getIpBans() {
        return this.ipBans;
    }
    
    public void op(final GameProfile gameProfile) {
        ((StoredUserList<K, ServerOpListEntry>)this.ops).add(new ServerOpListEntry(gameProfile, this.server.getOperatorUserPermissionLevel(), this.ops.canBypassPlayerLimit(gameProfile)));
        final ServerPlayer vl3 = this.getPlayer(gameProfile.getId());
        if (vl3 != null) {
            this.sendPlayerPermissionLevel(vl3);
        }
    }
    
    public void deop(final GameProfile gameProfile) {
        ((StoredUserList<GameProfile, V>)this.ops).remove(gameProfile);
        final ServerPlayer vl3 = this.getPlayer(gameProfile.getId());
        if (vl3 != null) {
            this.sendPlayerPermissionLevel(vl3);
        }
    }
    
    private void sendPlayerPermissionLevel(final ServerPlayer vl, final int integer) {
        if (vl.connection != null) {
            byte byte4;
            if (integer <= 0) {
                byte4 = 24;
            }
            else if (integer >= 4) {
                byte4 = 28;
            }
            else {
                byte4 = (byte)(24 + integer);
            }
            vl.connection.send(new ClientboundEntityEventPacket(vl, byte4));
        }
        this.server.getCommands().sendCommands(vl);
    }
    
    public boolean isWhiteListed(final GameProfile gameProfile) {
        return !this.doWhiteList || ((StoredUserList<GameProfile, V>)this.ops).contains(gameProfile) || ((StoredUserList<GameProfile, V>)this.whitelist).contains(gameProfile);
    }
    
    public boolean isOp(final GameProfile gameProfile) {
        return ((StoredUserList<GameProfile, V>)this.ops).contains(gameProfile) || (this.server.isSingleplayerOwner(gameProfile) && this.server.getLevel(DimensionType.OVERWORLD).getLevelData().getAllowCommands()) || this.allowCheatsForAllPlayers;
    }
    
    @Nullable
    public ServerPlayer getPlayerByName(final String string) {
        for (final ServerPlayer vl4 : this.players) {
            if (vl4.getGameProfile().getName().equalsIgnoreCase(string)) {
                return vl4;
            }
        }
        return null;
    }
    
    public void broadcast(@Nullable final Player awg, final double double2, final double double3, final double double4, final double double5, final DimensionType byn, final Packet<?> kc) {
        for (int integer13 = 0; integer13 < this.players.size(); ++integer13) {
            final ServerPlayer vl14 = (ServerPlayer)this.players.get(integer13);
            if (vl14 != awg) {
                if (vl14.dimension == byn) {
                    final double double6 = double2 - vl14.x;
                    final double double7 = double3 - vl14.y;
                    final double double8 = double4 - vl14.z;
                    if (double6 * double6 + double7 * double7 + double8 * double8 < double5 * double5) {
                        vl14.connection.send(kc);
                    }
                }
            }
        }
    }
    
    public void saveAll() {
        for (int integer2 = 0; integer2 < this.players.size(); ++integer2) {
            this.save((ServerPlayer)this.players.get(integer2));
        }
    }
    
    public UserWhiteList getWhiteList() {
        return this.whitelist;
    }
    
    public String[] getWhiteListNames() {
        return this.whitelist.getUserList();
    }
    
    public ServerOpList getOps() {
        return this.ops;
    }
    
    public String[] getOpNames() {
        return this.ops.getUserList();
    }
    
    public void reloadWhiteList() {
    }
    
    public void sendLevelInfo(final ServerPlayer vl, final ServerLevel vk) {
        final WorldBorder bxf4 = this.server.getLevel(DimensionType.OVERWORLD).getWorldBorder();
        vl.connection.send(new ClientboundSetBorderPacket(bxf4, ClientboundSetBorderPacket.Type.INITIALIZE));
        vl.connection.send(new ClientboundSetTimePacket(vk.getGameTime(), vk.getDayTime(), vk.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
        final BlockPos ew5 = vk.getSharedSpawnPos();
        vl.connection.send(new ClientboundSetSpawnPositionPacket(ew5));
        if (vk.isRaining()) {
            vl.connection.send(new ClientboundGameEventPacket(1, 0.0f));
            vl.connection.send(new ClientboundGameEventPacket(7, vk.getRainLevel(1.0f)));
            vl.connection.send(new ClientboundGameEventPacket(8, vk.getThunderLevel(1.0f)));
        }
    }
    
    public void sendAllPlayerInfo(final ServerPlayer vl) {
        vl.refreshContainer(vl.inventoryMenu);
        vl.resetSentInfo();
        vl.connection.send(new ClientboundSetCarriedItemPacket(vl.inventory.selected));
    }
    
    public int getPlayerCount() {
        return this.players.size();
    }
    
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    
    public boolean isUsingWhitelist() {
        return this.doWhiteList;
    }
    
    public void setUsingWhiteList(final boolean boolean1) {
        this.doWhiteList = boolean1;
    }
    
    public List<ServerPlayer> getPlayersWithAddress(final String string) {
        final List<ServerPlayer> list3 = (List<ServerPlayer>)Lists.newArrayList();
        for (final ServerPlayer vl5 : this.players) {
            if (vl5.getIpAddress().equals(string)) {
                list3.add(vl5);
            }
        }
        return list3;
    }
    
    public int getViewDistance() {
        return this.viewDistance;
    }
    
    public MinecraftServer getServer() {
        return this.server;
    }
    
    public CompoundTag getSingleplayerData() {
        return null;
    }
    
    public void setOverrideGameMode(final GameType bho) {
        this.overrideGameMode = bho;
    }
    
    private void updatePlayerGameMode(final ServerPlayer vl1, final ServerPlayer vl2, final LevelAccessor bhs) {
        if (vl2 != null) {
            vl1.gameMode.setGameModeForPlayer(vl2.gameMode.getGameModeForPlayer());
        }
        else if (this.overrideGameMode != null) {
            vl1.gameMode.setGameModeForPlayer(this.overrideGameMode);
        }
        vl1.gameMode.updateGameMode(bhs.getLevelData().getGameType());
    }
    
    public void setAllowCheatsForAllPlayers(final boolean boolean1) {
        this.allowCheatsForAllPlayers = boolean1;
    }
    
    public void removeAll() {
        for (int integer2 = 0; integer2 < this.players.size(); ++integer2) {
            ((ServerPlayer)this.players.get(integer2)).connection.disconnect(new TranslatableComponent("multiplayer.disconnect.server_shutdown", new Object[0]));
        }
    }
    
    public void broadcastMessage(final Component jo, final boolean boolean2) {
        this.server.sendMessage(jo);
        final ChatType jm4 = boolean2 ? ChatType.SYSTEM : ChatType.CHAT;
        this.broadcastAll(new ClientboundChatPacket(jo, jm4));
    }
    
    public void broadcastMessage(final Component jo) {
        this.broadcastMessage(jo, true);
    }
    
    public ServerStatsCounter getPlayerStats(final Player awg) {
        final UUID uUID3 = awg.getUUID();
        ServerStatsCounter yu4 = (uUID3 == null) ? null : ((ServerStatsCounter)this.stats.get(uUID3));
        if (yu4 == null) {
            final File file5 = new File(this.server.getLevel(DimensionType.OVERWORLD).getLevelStorage().getFolder(), "stats");
            final File file6 = new File(file5, new StringBuilder().append(uUID3).append(".json").toString());
            if (!file6.exists()) {
                final File file7 = new File(file5, awg.getName().getString() + ".json");
                if (file7.exists() && file7.isFile()) {
                    file7.renameTo(file6);
                }
            }
            yu4 = new ServerStatsCounter(this.server, file6);
            this.stats.put(uUID3, yu4);
        }
        return yu4;
    }
    
    public PlayerAdvancements getPlayerAdvancements(final ServerPlayer vl) {
        final UUID uUID3 = vl.getUUID();
        PlayerAdvancements re4 = (PlayerAdvancements)this.advancements.get(uUID3);
        if (re4 == null) {
            final File file5 = new File(this.server.getLevel(DimensionType.OVERWORLD).getLevelStorage().getFolder(), "advancements");
            final File file6 = new File(file5, new StringBuilder().append(uUID3).append(".json").toString());
            re4 = new PlayerAdvancements(this.server, file6, vl);
            this.advancements.put(uUID3, re4);
        }
        re4.setPlayer(vl);
        return re4;
    }
    
    public void setViewDistance(final int integer) {
        this.viewDistance = integer;
        this.broadcastAll(new ClientboundSetChunkCacheRadiusPacket(integer));
        for (final ServerLevel vk4 : this.server.getAllLevels()) {
            if (vk4 != null) {
                vk4.getChunkSource().setViewDistance(integer);
            }
        }
    }
    
    public List<ServerPlayer> getPlayers() {
        return this.players;
    }
    
    @Nullable
    public ServerPlayer getPlayer(final UUID uUID) {
        return (ServerPlayer)this.playersByUUID.get(uUID);
    }
    
    public boolean canBypassPlayerLimit(final GameProfile gameProfile) {
        return false;
    }
    
    public void reloadResources() {
        for (final PlayerAdvancements re3 : this.advancements.values()) {
            re3.reload();
        }
        this.broadcastAll(new ClientboundUpdateTagsPacket(this.server.getTags()));
        final ClientboundUpdateRecipesPacket nq2 = new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes());
        for (final ServerPlayer vl4 : this.players) {
            vl4.connection.send(nq2);
            vl4.getRecipeBook().sendInitialRecipeBook(vl4);
        }
    }
    
    public boolean isAllowCheatsForAllPlayers() {
        return this.allowCheatsForAllPlayers;
    }
    
    static {
        USERBANLIST_FILE = new File("banned-players.json");
        IPBANLIST_FILE = new File("banned-ips.json");
        OPLIST_FILE = new File("ops.json");
        WHITELIST_FILE = new File("whitelist.json");
        LOGGER = LogManager.getLogger();
        BAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    }
}
