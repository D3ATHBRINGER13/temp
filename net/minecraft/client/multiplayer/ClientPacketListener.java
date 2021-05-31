package net.minecraft.client.multiplayer;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.stats.RecipeBook;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.world.level.LightLayer;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.world.scores.Score;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.core.Position;
import java.util.Collection;
import net.minecraft.core.PositionImpl;
import net.minecraft.client.renderer.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.renderer.debug.VillageDebugRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptRenderer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.debug.NeighborsUpdateRenderer;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.io.UnsupportedEncodingException;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatPacket;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.BlockTags;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.stats.Stat;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.item.MapItem;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.DemoIntroScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.world.level.GameType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundSetEquippedItemPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.protocol.game.ServerboundContainerAckPacket;
import net.minecraft.network.protocol.game.ClientboundContainerAckPacket;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.world.level.Explosion;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.Iterator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import java.util.List;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.network.protocol.game.ClientboundAddPaintingPacket;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.network.protocol.game.ClientboundAddGlobalEntityPacket;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.network.FriendlyByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import com.google.common.collect.Maps;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.commands.SharedSuggestionProvider;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Random;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.tags.TagManager;
import java.util.UUID;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientPacketListener implements ClientGamePacketListener {
    private static final Logger LOGGER;
    private final Connection connection;
    private final GameProfile localGameProfile;
    private final Screen callbackScreen;
    private Minecraft minecraft;
    private MultiPlayerLevel level;
    private boolean started;
    private final Map<UUID, PlayerInfo> playerInfoMap;
    private final ClientAdvancements advancements;
    private final ClientSuggestionProvider suggestionsProvider;
    private TagManager tags;
    private final DebugQueryHandler debugQueryHandler;
    private int serverChunkRadius;
    private final Random random;
    private CommandDispatcher<SharedSuggestionProvider> commands;
    private final RecipeManager recipeManager;
    private final UUID id;
    
    public ClientPacketListener(final Minecraft cyc, final Screen dcl, final Connection jc, final GameProfile gameProfile) {
        this.playerInfoMap = (Map<UUID, PlayerInfo>)Maps.newHashMap();
        this.tags = new TagManager();
        this.debugQueryHandler = new DebugQueryHandler(this);
        this.serverChunkRadius = 3;
        this.random = new Random();
        this.commands = (CommandDispatcher<SharedSuggestionProvider>)new CommandDispatcher();
        this.recipeManager = new RecipeManager();
        this.id = UUID.randomUUID();
        this.minecraft = cyc;
        this.callbackScreen = dcl;
        this.connection = jc;
        this.localGameProfile = gameProfile;
        this.advancements = new ClientAdvancements(cyc);
        this.suggestionsProvider = new ClientSuggestionProvider(this, cyc);
    }
    
    public ClientSuggestionProvider getSuggestionsProvider() {
        return this.suggestionsProvider;
    }
    
    public void cleanup() {
        this.level = null;
    }
    
    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }
    
    public void handleLogin(final ClientboundLoginPacket ls) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ls, this, this.minecraft);
        this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, this);
        this.serverChunkRadius = ls.getChunkRadius();
        this.level = new MultiPlayerLevel(this, new LevelSettings(0L, ls.getGameType(), false, ls.isHardcore(), ls.getLevelType()), ls.getDimension(), this.serverChunkRadius, this.minecraft.getProfiler(), this.minecraft.levelRenderer);
        this.minecraft.setLevel(this.level);
        if (this.minecraft.player == null) {
            this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook(this.level.getRecipeManager()));
            this.minecraft.player.yRot = -180.0f;
            if (this.minecraft.getSingleplayerServer() != null) {
                this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
            }
        }
        this.minecraft.debugRenderer.clear();
        this.minecraft.player.resetPos();
        final int integer3 = ls.getPlayerId();
        this.level.addPlayer(integer3, this.minecraft.player);
        this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
        this.minecraft.cameraEntity = this.minecraft.player;
        this.minecraft.player.dimension = ls.getDimension();
        this.minecraft.setScreen(new ReceivingLevelScreen());
        this.minecraft.player.setId(integer3);
        this.minecraft.player.setReducedDebugInfo(ls.isReducedDebugInfo());
        this.minecraft.gameMode.setLocalMode(ls.getGameType());
        this.minecraft.options.broadcastOptions();
        this.connection.send(new ServerboundCustomPayloadPacket(ServerboundCustomPayloadPacket.BRAND, new FriendlyByteBuf(Unpooled.buffer()).writeUtf(ClientBrandRetriever.getClientModName())));
        this.minecraft.getGame().onStartGameSession();
    }
    
    public void handleAddEntity(final ClientboundAddEntityPacket kg) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kg, this, this.minecraft);
        final double double3 = kg.getX();
        final double double4 = kg.getY();
        final double double5 = kg.getZ();
        final EntityType<?> ais10 = kg.getType();
        Entity aio9;
        if (ais10 == EntityType.CHEST_MINECART) {
            aio9 = new MinecartChest(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.FURNACE_MINECART) {
            aio9 = new MinecartFurnace(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.TNT_MINECART) {
            aio9 = new MinecartTNT(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.SPAWNER_MINECART) {
            aio9 = new MinecartSpawner(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.HOPPER_MINECART) {
            aio9 = new MinecartHopper(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.COMMAND_BLOCK_MINECART) {
            aio9 = new MinecartCommandBlock(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.MINECART) {
            aio9 = new Minecart(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.FISHING_BOBBER) {
            final Entity aio10 = this.level.getEntity(kg.getData());
            if (aio10 instanceof Player) {
                aio9 = new FishingHook(this.level, (Player)aio10, double3, double4, double5);
            }
            else {
                aio9 = null;
            }
        }
        else if (ais10 == EntityType.ARROW) {
            aio9 = new Arrow(this.level, double3, double4, double5);
            final Entity aio10 = this.level.getEntity(kg.getData());
            if (aio10 != null) {
                ((AbstractArrow)aio9).setOwner(aio10);
            }
        }
        else if (ais10 == EntityType.SPECTRAL_ARROW) {
            aio9 = new SpectralArrow(this.level, double3, double4, double5);
            final Entity aio10 = this.level.getEntity(kg.getData());
            if (aio10 != null) {
                ((AbstractArrow)aio9).setOwner(aio10);
            }
        }
        else if (ais10 == EntityType.TRIDENT) {
            aio9 = new ThrownTrident(this.level, double3, double4, double5);
            final Entity aio10 = this.level.getEntity(kg.getData());
            if (aio10 != null) {
                ((AbstractArrow)aio9).setOwner(aio10);
            }
        }
        else if (ais10 == EntityType.SNOWBALL) {
            aio9 = new Snowball(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.LLAMA_SPIT) {
            aio9 = new LlamaSpit(this.level, double3, double4, double5, kg.getXa(), kg.getYa(), kg.getZa());
        }
        else if (ais10 == EntityType.ITEM_FRAME) {
            aio9 = new ItemFrame(this.level, new BlockPos(double3, double4, double5), Direction.from3DDataValue(kg.getData()));
        }
        else if (ais10 == EntityType.LEASH_KNOT) {
            aio9 = new LeashFenceKnotEntity(this.level, new BlockPos(double3, double4, double5));
        }
        else if (ais10 == EntityType.ENDER_PEARL) {
            aio9 = new ThrownEnderpearl(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.EYE_OF_ENDER) {
            aio9 = new EyeOfEnder(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.FIREWORK_ROCKET) {
            aio9 = new FireworkRocketEntity(this.level, double3, double4, double5, ItemStack.EMPTY);
        }
        else if (ais10 == EntityType.FIREBALL) {
            aio9 = new LargeFireball(this.level, double3, double4, double5, kg.getXa(), kg.getYa(), kg.getZa());
        }
        else if (ais10 == EntityType.DRAGON_FIREBALL) {
            aio9 = new DragonFireball(this.level, double3, double4, double5, kg.getXa(), kg.getYa(), kg.getZa());
        }
        else if (ais10 == EntityType.SMALL_FIREBALL) {
            aio9 = new SmallFireball(this.level, double3, double4, double5, kg.getXa(), kg.getYa(), kg.getZa());
        }
        else if (ais10 == EntityType.WITHER_SKULL) {
            aio9 = new WitherSkull(this.level, double3, double4, double5, kg.getXa(), kg.getYa(), kg.getZa());
        }
        else if (ais10 == EntityType.SHULKER_BULLET) {
            aio9 = new ShulkerBullet(this.level, double3, double4, double5, kg.getXa(), kg.getYa(), kg.getZa());
        }
        else if (ais10 == EntityType.EGG) {
            aio9 = new ThrownEgg(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.EVOKER_FANGS) {
            aio9 = new EvokerFangs(this.level, double3, double4, double5, 0.0f, 0, null);
        }
        else if (ais10 == EntityType.POTION) {
            aio9 = new ThrownPotion(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.EXPERIENCE_BOTTLE) {
            aio9 = new ThrownExperienceBottle(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.BOAT) {
            aio9 = new Boat(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.TNT) {
            aio9 = new PrimedTnt(this.level, double3, double4, double5, null);
        }
        else if (ais10 == EntityType.ARMOR_STAND) {
            aio9 = new ArmorStand(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.END_CRYSTAL) {
            aio9 = new EndCrystal(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.ITEM) {
            aio9 = new ItemEntity(this.level, double3, double4, double5);
        }
        else if (ais10 == EntityType.FALLING_BLOCK) {
            aio9 = new FallingBlockEntity(this.level, double3, double4, double5, Block.stateById(kg.getData()));
        }
        else if (ais10 == EntityType.AREA_EFFECT_CLOUD) {
            aio9 = new AreaEffectCloud(this.level, double3, double4, double5);
        }
        else {
            aio9 = null;
        }
        if (aio9 != null) {
            final int integer11 = kg.getId();
            aio9.setPacketCoordinates(double3, double4, double5);
            aio9.xRot = kg.getxRot() * 360 / 256.0f;
            aio9.yRot = kg.getyRot() * 360 / 256.0f;
            aio9.setId(integer11);
            aio9.setUUID(kg.getUUID());
            this.level.putNonPlayerEntity(integer11, aio9);
            if (aio9 instanceof AbstractMinecart) {
                this.minecraft.getSoundManager().play(new MinecartSoundInstance((AbstractMinecart)aio9));
            }
        }
    }
    
    public void handleAddExperienceOrb(final ClientboundAddExperienceOrbPacket kh) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kh, this, this.minecraft);
        final double double3 = kh.getX();
        final double double4 = kh.getY();
        final double double5 = kh.getZ();
        final Entity aio9 = new ExperienceOrb(this.level, double3, double4, double5, kh.getValue());
        aio9.setPacketCoordinates(double3, double4, double5);
        aio9.yRot = 0.0f;
        aio9.xRot = 0.0f;
        aio9.setId(kh.getId());
        this.level.putNonPlayerEntity(kh.getId(), aio9);
    }
    
    public void handleAddGlobalEntity(final ClientboundAddGlobalEntityPacket ki) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ki, this, this.minecraft);
        final double double3 = ki.getX();
        final double double4 = ki.getY();
        final double double5 = ki.getZ();
        if (ki.getType() == 1) {
            final LightningBolt atu9 = new LightningBolt(this.level, double3, double4, double5, false);
            atu9.setPacketCoordinates(double3, double4, double5);
            atu9.yRot = 0.0f;
            atu9.xRot = 0.0f;
            atu9.setId(ki.getId());
            this.level.addLightning(atu9);
        }
    }
    
    public void handleAddPainting(final ClientboundAddPaintingPacket kk) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kk, this, this.minecraft);
        final Painting atq3 = new Painting(this.level, kk.getPos(), kk.getDirection(), kk.getMotive());
        atq3.setId(kk.getId());
        atq3.setUUID(kk.getUUID());
        this.level.putNonPlayerEntity(kk.getId(), atq3);
    }
    
    public void handleSetEntityMotion(final ClientboundSetEntityMotionPacket mv) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mv, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(mv.getId());
        if (aio3 == null) {
            return;
        }
        aio3.lerpMotion(mv.getXa() / 8000.0, mv.getYa() / 8000.0, mv.getZa() / 8000.0);
    }
    
    public void handleSetEntityData(final ClientboundSetEntityDataPacket mt) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mt, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(mt.getId());
        if (aio3 != null && mt.getUnpackedData() != null) {
            aio3.getEntityData().assignValues(mt.getUnpackedData());
        }
    }
    
    public void handleAddPlayer(final ClientboundAddPlayerPacket kl) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kl, this, this.minecraft);
        final double double3 = kl.getX();
        final double double4 = kl.getY();
        final double double5 = kl.getZ();
        final float float9 = kl.getyRot() * 360 / 256.0f;
        final float float10 = kl.getxRot() * 360 / 256.0f;
        final int integer11 = kl.getEntityId();
        final RemotePlayer dmq12 = new RemotePlayer(this.minecraft.level, this.getPlayerInfo(kl.getPlayerId()).getProfile());
        dmq12.setId(integer11);
        dmq12.xo = double3;
        dmq12.xOld = double3;
        dmq12.yo = double4;
        dmq12.yOld = double4;
        dmq12.zo = double5;
        dmq12.setPacketCoordinates(double3, double4, dmq12.zOld = double5);
        dmq12.absMoveTo(double3, double4, double5, float9, float10);
        this.level.addPlayer(integer11, dmq12);
        final List<SynchedEntityData.DataItem<?>> list13 = kl.getUnpackedData();
        if (list13 != null) {
            dmq12.getEntityData().assignValues(list13);
        }
    }
    
    public void handleTeleportEntity(final ClientboundTeleportEntityPacket nm) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)nm, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(nm.getId());
        if (aio3 == null) {
            return;
        }
        final double double4 = nm.getX();
        final double double5 = nm.getY();
        final double double6 = nm.getZ();
        aio3.setPacketCoordinates(double4, double5, double6);
        if (!aio3.isControlledByLocalInstance()) {
            final float float10 = nm.getyRot() * 360 / 256.0f;
            final float float11 = nm.getxRot() * 360 / 256.0f;
            if (Math.abs(aio3.x - double4) >= 0.03125 || Math.abs(aio3.y - double5) >= 0.015625 || Math.abs(aio3.z - double6) >= 0.03125) {
                aio3.lerpTo(double4, double5, double6, float10, float11, 3, true);
            }
            else {
                aio3.lerpTo(aio3.x, aio3.y, aio3.z, float10, float11, 0, true);
            }
            aio3.onGround = nm.isOnGround();
        }
    }
    
    public void handleSetCarriedItem(final ClientboundSetCarriedItemPacket mp) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mp, this, this.minecraft);
        if (Inventory.isHotbarSlot(mp.getSlot())) {
            this.minecraft.player.inventory.selected = mp.getSlot();
        }
    }
    
    public void handleMoveEntity(final ClientboundMoveEntityPacket lv) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lv, this, this.minecraft);
        final Entity aio3 = lv.getEntity(this.level);
        if (aio3 == null) {
            return;
        }
        final Entity entity = aio3;
        entity.xp += lv.getXa();
        final Entity entity2 = aio3;
        entity2.yp += lv.getYa();
        final Entity entity3 = aio3;
        entity3.zp += lv.getZa();
        final Vec3 csi4 = ClientboundMoveEntityPacket.packetToEntity(aio3.xp, aio3.yp, aio3.zp);
        if (!aio3.isControlledByLocalInstance()) {
            final float float5 = lv.hasRotation() ? (lv.getyRot() * 360 / 256.0f) : aio3.yRot;
            final float float6 = lv.hasRotation() ? (lv.getxRot() * 360 / 256.0f) : aio3.xRot;
            aio3.lerpTo(csi4.x, csi4.y, csi4.z, float5, float6, 3, false);
            aio3.onGround = lv.isOnGround();
        }
    }
    
    public void handleRotateMob(final ClientboundRotateHeadPacket ml) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ml, this, this.minecraft);
        final Entity aio3 = ml.getEntity(this.level);
        if (aio3 == null) {
            return;
        }
        final float float4 = ml.getYHeadRot() * 360 / 256.0f;
        aio3.lerpHeadTo(float4, 3);
    }
    
    public void handleRemoveEntity(final ClientboundRemoveEntitiesPacket mh) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mh, this, this.minecraft);
        for (int integer3 = 0; integer3 < mh.getEntityIds().length; ++integer3) {
            final int integer4 = mh.getEntityIds()[integer3];
            this.level.removeEntity(integer4);
        }
    }
    
    public void handleMovePlayer(final ClientboundPlayerPositionPacket mf) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mf, this, this.minecraft);
        final Player awg3 = this.minecraft.player;
        double double4 = mf.getX();
        double double5 = mf.getY();
        double double6 = mf.getZ();
        float float10 = mf.getYRot();
        float float11 = mf.getXRot();
        final Vec3 csi12 = awg3.getDeltaMovement();
        double double7 = csi12.x;
        double double8 = csi12.y;
        double double9 = csi12.z;
        if (mf.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.X)) {
            final Player player = awg3;
            player.xOld += double4;
            double4 += awg3.x;
        }
        else {
            awg3.xOld = double4;
            double7 = 0.0;
        }
        if (mf.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.Y)) {
            final Player player2 = awg3;
            player2.yOld += double5;
            double5 += awg3.y;
        }
        else {
            awg3.yOld = double5;
            double8 = 0.0;
        }
        if (mf.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.Z)) {
            final Player player3 = awg3;
            player3.zOld += double6;
            double6 += awg3.z;
        }
        else {
            awg3.zOld = double6;
            double9 = 0.0;
        }
        awg3.setDeltaMovement(double7, double8, double9);
        if (mf.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT)) {
            float11 += awg3.xRot;
        }
        if (mf.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT)) {
            float10 += awg3.yRot;
        }
        awg3.absMoveTo(double4, double5, double6, float10, float11);
        this.connection.send(new ServerboundAcceptTeleportationPacket(mf.getId()));
        this.connection.send(new ServerboundMovePlayerPacket.PosRot(awg3.x, awg3.getBoundingBox().minY, awg3.z, awg3.yRot, awg3.xRot, false));
        if (!this.started) {
            this.minecraft.player.xo = this.minecraft.player.x;
            this.minecraft.player.yo = this.minecraft.player.y;
            this.minecraft.player.zo = this.minecraft.player.z;
            this.started = true;
            this.minecraft.setScreen(null);
        }
    }
    
    public void handleChunkBlocksUpdate(final ClientboundChunkBlocksUpdatePacket kw) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kw, this, this.minecraft);
        for (final ClientboundChunkBlocksUpdatePacket.BlockUpdate a6 : kw.getUpdates()) {
            this.level.setKnownState(a6.getPos(), a6.getBlock());
        }
    }
    
    public void handleLevelChunk(final ClientboundLevelChunkPacket lo) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lo, this, this.minecraft);
        final int integer3 = lo.getX();
        final int integer4 = lo.getZ();
        final LevelChunk bxt5 = this.level.getChunkSource().replaceWithPacketData(this.level, integer3, integer4, lo.getReadBuffer(), lo.getHeightmaps(), lo.getAvailableSections(), lo.isFullChunk());
        if (bxt5 != null && lo.isFullChunk()) {
            this.level.reAddEntitiesToChunk(bxt5);
        }
        for (int integer5 = 0; integer5 < 16; ++integer5) {
            this.level.setSectionDirtyWithNeighbors(integer3, integer5, integer4);
        }
        for (final CompoundTag id7 : lo.getBlockEntitiesTags()) {
            final BlockPos ew8 = new BlockPos(id7.getInt("x"), id7.getInt("y"), id7.getInt("z"));
            final BlockEntity btw9 = this.level.getBlockEntity(ew8);
            if (btw9 != null) {
                btw9.load(id7);
            }
        }
    }
    
    public void handleForgetLevelChunk(final ClientboundForgetLevelChunkPacket lk) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lk, this, this.minecraft);
        final int integer3 = lk.getX();
        final int integer4 = lk.getZ();
        final ClientChunkCache dka5 = this.level.getChunkSource();
        dka5.drop(integer3, integer4);
        final LevelLightEngine clb6 = dka5.getLightEngine();
        for (int integer5 = 0; integer5 < 16; ++integer5) {
            this.level.setSectionDirtyWithNeighbors(integer3, integer5, integer4);
            clb6.updateSectionStatus(SectionPos.of(integer3, integer5, integer4), true);
        }
        clb6.enableLightSources(new ChunkPos(integer3, integer4), false);
    }
    
    public void handleBlockUpdate(final ClientboundBlockUpdatePacket ks) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ks, this, this.minecraft);
        this.level.setKnownState(ks.getPos(), ks.getBlockState());
    }
    
    public void handleDisconnect(final ClientboundDisconnectPacket lh) {
        this.connection.disconnect(lh.getReason());
    }
    
    public void onDisconnect(final Component jo) {
        this.minecraft.clearLevel();
        if (this.callbackScreen != null) {
            if (this.callbackScreen instanceof RealmsScreenProxy) {
                this.minecraft.setScreen(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.callbackScreen).getScreen(), "disconnect.lost", jo).getProxy());
            }
            else {
                this.minecraft.setScreen(new DisconnectedScreen(this.callbackScreen, "disconnect.lost", jo));
            }
        }
        else {
            this.minecraft.setScreen(new DisconnectedScreen((Screen)new JoinMultiplayerScreen(new TitleScreen()), "disconnect.lost", jo));
        }
    }
    
    public void send(final Packet<?> kc) {
        this.connection.send(kc);
    }
    
    public void handleTakeItemEntity(final ClientboundTakeItemEntityPacket nl) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)nl, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(nl.getItemId());
        LivingEntity aix4 = (LivingEntity)this.level.getEntity(nl.getPlayerId());
        if (aix4 == null) {
            aix4 = this.minecraft.player;
        }
        if (aio3 != null) {
            if (aio3 instanceof ExperienceOrb) {
                this.level.playLocalSound(aio3.x, aio3.y, aio3.z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1f, (this.random.nextFloat() - this.random.nextFloat()) * 0.35f + 0.9f, false);
            }
            else {
                this.level.playLocalSound(aio3.x, aio3.y, aio3.z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, (this.random.nextFloat() - this.random.nextFloat()) * 1.4f + 2.0f, false);
            }
            if (aio3 instanceof ItemEntity) {
                ((ItemEntity)aio3).getItem().setCount(nl.getAmount());
            }
            this.minecraft.particleEngine.add(new ItemPickupParticle(this.level, aio3, aix4, 0.5f));
            this.level.removeEntity(nl.getItemId());
        }
    }
    
    public void handleChat(final ClientboundChatPacket kv) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kv, this, this.minecraft);
        this.minecraft.gui.handleChat(kv.getType(), kv.getMessage());
    }
    
    public void handleAnimate(final ClientboundAnimatePacket km) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)km, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(km.getId());
        if (aio3 == null) {
            return;
        }
        if (km.getAction() == 0) {
            final LivingEntity aix4 = (LivingEntity)aio3;
            aix4.swing(InteractionHand.MAIN_HAND);
        }
        else if (km.getAction() == 3) {
            final LivingEntity aix4 = (LivingEntity)aio3;
            aix4.swing(InteractionHand.OFF_HAND);
        }
        else if (km.getAction() == 1) {
            aio3.animateHurt();
        }
        else if (km.getAction() == 2) {
            final Player awg4 = (Player)aio3;
            awg4.stopSleepInBed(false, false, false);
        }
        else if (km.getAction() == 4) {
            this.minecraft.particleEngine.createTrackingEmitter(aio3, ParticleTypes.CRIT);
        }
        else if (km.getAction() == 5) {
            this.minecraft.particleEngine.createTrackingEmitter(aio3, ParticleTypes.ENCHANTED_HIT);
        }
    }
    
    public void handleAddMob(final ClientboundAddMobPacket kj) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kj, this, this.minecraft);
        final double double3 = kj.getX();
        final double double4 = kj.getY();
        final double double5 = kj.getZ();
        final float float9 = kj.getyRot() * 360 / 256.0f;
        final float float10 = kj.getxRot() * 360 / 256.0f;
        final LivingEntity aix11 = (LivingEntity)EntityType.create(kj.getType(), this.minecraft.level);
        if (aix11 != null) {
            aix11.setPacketCoordinates(double3, double4, double5);
            aix11.yBodyRot = kj.getyHeadRot() * 360 / 256.0f;
            aix11.yHeadRot = kj.getyHeadRot() * 360 / 256.0f;
            if (aix11 instanceof EnderDragon) {
                final EnderDragonPart[] arr12 = ((EnderDragon)aix11).getSubEntities();
                for (int integer13 = 0; integer13 < arr12.length; ++integer13) {
                    arr12[integer13].setId(integer13 + kj.getId());
                }
            }
            aix11.setId(kj.getId());
            aix11.setUUID(kj.getUUID());
            aix11.absMoveTo(double3, double4, double5, float9, float10);
            aix11.setDeltaMovement(kj.getXd() / 8000.0f, kj.getYd() / 8000.0f, kj.getZd() / 8000.0f);
            this.level.putNonPlayerEntity(kj.getId(), aix11);
            final List<SynchedEntityData.DataItem<?>> list12 = kj.getUnpackedData();
            if (list12 != null) {
                aix11.getEntityData().assignValues(list12);
            }
        }
        else {
            ClientPacketListener.LOGGER.warn("Skipping Entity with id {}", kj.getType());
        }
    }
    
    public void handleSetTime(final ClientboundSetTimePacket ne) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ne, this, this.minecraft);
        this.minecraft.level.setGameTime(ne.getGameTime());
        this.minecraft.level.setDayTime(ne.getDayTime());
    }
    
    public void handleSetSpawn(final ClientboundSetSpawnPositionPacket nd) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)nd, this, this.minecraft);
        this.minecraft.player.setRespawnPosition(nd.getPos(), true);
        this.minecraft.level.getLevelData().setSpawn(nd.getPos());
    }
    
    public void handleSetEntityPassengersPacket(final ClientboundSetPassengersPacket na) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)na, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(na.getVehicle());
        if (aio3 == null) {
            ClientPacketListener.LOGGER.warn("Received passengers for unknown entity");
            return;
        }
        final boolean boolean4 = aio3.hasIndirectPassenger(this.minecraft.player);
        aio3.ejectPassengers();
        for (final int integer8 : na.getPassengers()) {
            final Entity aio4 = this.level.getEntity(integer8);
            if (aio4 != null) {
                aio4.startRiding(aio3, true);
                if (aio4 == this.minecraft.player && !boolean4) {
                    this.minecraft.gui.setOverlayMessage(I18n.get("mount.onboard", this.minecraft.options.keySneak.getTranslatedKeyMessage()), false);
                }
            }
        }
    }
    
    public void handleEntityLinkPacket(final ClientboundSetEntityLinkPacket mu) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mu, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(mu.getSourceId());
        if (aio3 instanceof Mob) {
            ((Mob)aio3).setDelayedLeashHolderId(mu.getDestId());
        }
    }
    
    private static ItemStack findTotem(final Player awg) {
        for (final InteractionHand ahi5 : InteractionHand.values()) {
            final ItemStack bcj6 = awg.getItemInHand(ahi5);
            if (bcj6.getItem() == Items.TOTEM_OF_UNDYING) {
                return bcj6;
            }
        }
        return new ItemStack(Items.TOTEM_OF_UNDYING);
    }
    
    public void handleEntityEvent(final ClientboundEntityEventPacket li) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)li, this, this.minecraft);
        final Entity aio3 = li.getEntity(this.level);
        if (aio3 != null) {
            if (li.getEventId() == 21) {
                this.minecraft.getSoundManager().play(new GuardianAttackSoundInstance((Guardian)aio3));
            }
            else if (li.getEventId() == 35) {
                final int integer4 = 40;
                this.minecraft.particleEngine.createTrackingEmitter(aio3, ParticleTypes.TOTEM_OF_UNDYING, 30);
                this.level.playLocalSound(aio3.x, aio3.y, aio3.z, SoundEvents.TOTEM_USE, aio3.getSoundSource(), 1.0f, 1.0f, false);
                if (aio3 == this.minecraft.player) {
                    this.minecraft.gameRenderer.displayItemActivation(findTotem(this.minecraft.player));
                }
            }
            else {
                aio3.handleEntityEvent(li.getEventId());
            }
        }
    }
    
    public void handleSetHealth(final ClientboundSetHealthPacket my) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)my, this, this.minecraft);
        this.minecraft.player.hurtTo(my.getHealth());
        this.minecraft.player.getFoodData().setFoodLevel(my.getFood());
        this.minecraft.player.getFoodData().setSaturation(my.getSaturation());
    }
    
    public void handleSetExperience(final ClientboundSetExperiencePacket mx) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mx, this, this.minecraft);
        this.minecraft.player.setExperienceValues(mx.getExperienceProgress(), mx.getTotalExperience(), mx.getExperienceLevel());
    }
    
    public void handleRespawn(final ClientboundRespawnPacket mk) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mk, this, this.minecraft);
        final DimensionType byn3 = mk.getDimension();
        final LocalPlayer dmp4 = this.minecraft.player;
        final int integer5 = dmp4.getId();
        if (byn3 != dmp4.dimension) {
            this.started = false;
            final Scoreboard cti6 = this.level.getScoreboard();
            (this.level = new MultiPlayerLevel(this, new LevelSettings(0L, mk.getPlayerGameType(), false, this.minecraft.level.getLevelData().isHardcore(), mk.getLevelType()), mk.getDimension(), this.serverChunkRadius, this.minecraft.getProfiler(), this.minecraft.levelRenderer)).setScoreboard(cti6);
            this.minecraft.setLevel(this.level);
            this.minecraft.setScreen(new ReceivingLevelScreen());
        }
        this.level.validateSpawn();
        this.level.removeAllPendingEntityRemovals();
        final String string6 = dmp4.getServerBrand();
        this.minecraft.cameraEntity = null;
        final LocalPlayer dmp5 = this.minecraft.gameMode.createPlayer(this.level, dmp4.getStats(), dmp4.getRecipeBook());
        dmp5.setId(integer5);
        dmp5.dimension = byn3;
        this.minecraft.player = dmp5;
        this.minecraft.cameraEntity = dmp5;
        dmp5.getEntityData().assignValues(dmp4.getEntityData().getAll());
        dmp5.resetPos();
        dmp5.setServerBrand(string6);
        this.level.addPlayer(integer5, dmp5);
        dmp5.yRot = -180.0f;
        dmp5.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(dmp5);
        dmp5.setReducedDebugInfo(dmp4.isReducedDebugInfo());
        if (this.minecraft.screen instanceof DeathScreen) {
            this.minecraft.setScreen(null);
        }
        this.minecraft.gameMode.setLocalMode(mk.getPlayerGameType());
    }
    
    public void handleExplosion(final ClientboundExplodePacket lj) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lj, this, this.minecraft);
        final Explosion bhk3 = new Explosion(this.minecraft.level, null, lj.getX(), lj.getY(), lj.getZ(), lj.getPower(), lj.getToBlow());
        bhk3.finalizeExplosion(true);
        this.minecraft.player.setDeltaMovement(this.minecraft.player.getDeltaMovement().add(lj.getKnockbackX(), lj.getKnockbackY(), lj.getKnockbackZ()));
    }
    
    public void handleHorseScreenOpen(final ClientboundHorseScreenOpenPacket lm) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lm, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(lm.getEntityId());
        if (aio3 instanceof AbstractHorse) {
            final LocalPlayer dmp4 = this.minecraft.player;
            final AbstractHorse asb5 = (AbstractHorse)aio3;
            final SimpleContainer aho6 = new SimpleContainer(lm.getSize());
            final HorseInventoryMenu azg7 = new HorseInventoryMenu(lm.getContainerId(), dmp4.inventory, aho6, asb5);
            dmp4.containerMenu = azg7;
            this.minecraft.setScreen(new HorseInventoryScreen(azg7, dmp4.inventory, asb5));
        }
    }
    
    public void handleOpenScreen(final ClientboundOpenScreenPacket ly) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ly, this, this.minecraft);
        MenuScreens.create(ly.getType(), this.minecraft, ly.getContainerId(), ly.getTitle());
    }
    
    public void handleContainerSetSlot(final ClientboundContainerSetSlotPacket ld) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ld, this, this.minecraft);
        final Player awg3 = this.minecraft.player;
        final ItemStack bcj4 = ld.getItem();
        final int integer5 = ld.getSlot();
        this.minecraft.getTutorial().onGetItem(bcj4);
        if (ld.getContainerId() == -1) {
            if (!(this.minecraft.screen instanceof CreativeModeInventoryScreen)) {
                awg3.inventory.setCarried(bcj4);
            }
        }
        else if (ld.getContainerId() == -2) {
            awg3.inventory.setItem(integer5, bcj4);
        }
        else {
            boolean boolean6 = false;
            if (this.minecraft.screen instanceof CreativeModeInventoryScreen) {
                final CreativeModeInventoryScreen dds7 = (CreativeModeInventoryScreen)this.minecraft.screen;
                boolean6 = (dds7.getSelectedTab() != CreativeModeTab.TAB_INVENTORY.getId());
            }
            if (ld.getContainerId() == 0 && ld.getSlot() >= 36 && integer5 < 45) {
                if (!bcj4.isEmpty()) {
                    final ItemStack bcj5 = awg3.inventoryMenu.getSlot(integer5).getItem();
                    if (bcj5.isEmpty() || bcj5.getCount() < bcj4.getCount()) {
                        bcj4.setPopTime(5);
                    }
                }
                awg3.inventoryMenu.setItem(integer5, bcj4);
            }
            else if (ld.getContainerId() == awg3.containerMenu.containerId && (ld.getContainerId() != 0 || !boolean6)) {
                awg3.containerMenu.setItem(integer5, bcj4);
            }
        }
    }
    
    public void handleContainerAck(final ClientboundContainerAckPacket kz) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kz, this, this.minecraft);
        AbstractContainerMenu ayk3 = null;
        final Player awg4 = this.minecraft.player;
        if (kz.getContainerId() == 0) {
            ayk3 = awg4.inventoryMenu;
        }
        else if (kz.getContainerId() == awg4.containerMenu.containerId) {
            ayk3 = awg4.containerMenu;
        }
        if (ayk3 != null && !kz.isAccepted()) {
            this.send(new ServerboundContainerAckPacket(kz.getContainerId(), kz.getUid(), true));
        }
    }
    
    public void handleContainerContent(final ClientboundContainerSetContentPacket lb) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lb, this, this.minecraft);
        final Player awg3 = this.minecraft.player;
        if (lb.getContainerId() == 0) {
            awg3.inventoryMenu.setAll(lb.getItems());
        }
        else if (lb.getContainerId() == awg3.containerMenu.containerId) {
            awg3.containerMenu.setAll(lb.getItems());
        }
    }
    
    public void handleOpenSignEditor(final ClientboundOpenSignEditorPacket lz) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lz, this, this.minecraft);
        BlockEntity btw3 = this.level.getBlockEntity(lz.getPos());
        if (!(btw3 instanceof SignBlockEntity)) {
            btw3 = new SignBlockEntity();
            btw3.setLevel(this.level);
            btw3.setPosition(lz.getPos());
        }
        this.minecraft.player.openTextEdit((SignBlockEntity)btw3);
    }
    
    public void handleBlockEntityData(final ClientboundBlockEntityDataPacket kq) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kq, this, this.minecraft);
        if (this.minecraft.level.hasChunkAt(kq.getPos())) {
            final BlockEntity btw3 = this.minecraft.level.getBlockEntity(kq.getPos());
            final int integer4 = kq.getType();
            final boolean boolean5 = integer4 == 2 && btw3 instanceof CommandBlockEntity;
            if ((integer4 == 1 && btw3 instanceof SpawnerBlockEntity) || boolean5 || (integer4 == 3 && btw3 instanceof BeaconBlockEntity) || (integer4 == 4 && btw3 instanceof SkullBlockEntity) || (integer4 == 6 && btw3 instanceof BannerBlockEntity) || (integer4 == 7 && btw3 instanceof StructureBlockEntity) || (integer4 == 8 && btw3 instanceof TheEndGatewayBlockEntity) || (integer4 == 9 && btw3 instanceof SignBlockEntity) || (integer4 == 11 && btw3 instanceof BedBlockEntity) || (integer4 == 5 && btw3 instanceof ConduitBlockEntity) || (integer4 == 12 && btw3 instanceof JigsawBlockEntity) || (integer4 == 13 && btw3 instanceof CampfireBlockEntity)) {
                btw3.load(kq.getTag());
            }
            if (boolean5 && this.minecraft.screen instanceof CommandBlockEditScreen) {
                ((CommandBlockEditScreen)this.minecraft.screen).updateGui();
            }
        }
    }
    
    public void handleContainerSetData(final ClientboundContainerSetDataPacket lc) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lc, this, this.minecraft);
        final Player awg3 = this.minecraft.player;
        if (awg3.containerMenu != null && awg3.containerMenu.containerId == lc.getContainerId()) {
            awg3.containerMenu.setData(lc.getId(), lc.getValue());
        }
    }
    
    public void handleSetEquippedItem(final ClientboundSetEquippedItemPacket mw) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mw, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(mw.getEntity());
        if (aio3 != null) {
            aio3.setItemSlot(mw.getSlot(), mw.getItem());
        }
    }
    
    public void handleContainerClose(final ClientboundContainerClosePacket la) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)la, this, this.minecraft);
        this.minecraft.player.clientSideCloseContainer();
    }
    
    public void handleBlockEvent(final ClientboundBlockEventPacket kr) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kr, this, this.minecraft);
        this.minecraft.level.blockEvent(kr.getPos(), kr.getBlock(), kr.getB0(), kr.getB1());
    }
    
    public void handleBlockDestruction(final ClientboundBlockDestructionPacket kp) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kp, this, this.minecraft);
        this.minecraft.level.destroyBlockProgress(kp.getId(), kp.getPos(), kp.getProgress());
    }
    
    public void handleGameEvent(final ClientboundGameEventPacket ll) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ll, this, this.minecraft);
        final Player awg3 = this.minecraft.player;
        final int integer4 = ll.getEvent();
        final float float5 = ll.getParam();
        final int integer5 = Mth.floor(float5 + 0.5f);
        if (integer4 >= 0 && integer4 < ClientboundGameEventPacket.EVENT_LANGUAGE_ID.length && ClientboundGameEventPacket.EVENT_LANGUAGE_ID[integer4] != null) {
            awg3.displayClientMessage(new TranslatableComponent(ClientboundGameEventPacket.EVENT_LANGUAGE_ID[integer4], new Object[0]), false);
        }
        if (integer4 == 1) {
            this.level.getLevelData().setRaining(true);
            this.level.setRainLevel(0.0f);
        }
        else if (integer4 == 2) {
            this.level.getLevelData().setRaining(false);
            this.level.setRainLevel(1.0f);
        }
        else if (integer4 == 3) {
            this.minecraft.gameMode.setLocalMode(GameType.byId(integer5));
        }
        else if (integer4 == 4) {
            if (integer5 == 0) {
                this.minecraft.player.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
                this.minecraft.setScreen(new ReceivingLevelScreen());
            }
            else if (integer5 == 1) {
                this.minecraft.setScreen(new WinScreen(true, () -> this.minecraft.player.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN))));
            }
        }
        else if (integer4 == 5) {
            final Options cyg7 = this.minecraft.options;
            if (float5 == 0.0f) {
                this.minecraft.setScreen(new DemoIntroScreen());
            }
            else if (float5 == 101.0f) {
                this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.help.movement", new Object[] { cyg7.keyUp.getTranslatedKeyMessage(), cyg7.keyLeft.getTranslatedKeyMessage(), cyg7.keyDown.getTranslatedKeyMessage(), cyg7.keyRight.getTranslatedKeyMessage() }));
            }
            else if (float5 == 102.0f) {
                this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.help.jump", new Object[] { cyg7.keyJump.getTranslatedKeyMessage() }));
            }
            else if (float5 == 103.0f) {
                this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.help.inventory", new Object[] { cyg7.keyInventory.getTranslatedKeyMessage() }));
            }
            else if (float5 == 104.0f) {
                this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.day.6", new Object[] { cyg7.keyScreenshot.getTranslatedKeyMessage() }));
            }
        }
        else if (integer4 == 6) {
            this.level.playSound(awg3, awg3.x, awg3.y + awg3.getEyeHeight(), awg3.z, SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.18f, 0.45f);
        }
        else if (integer4 == 7) {
            this.level.setRainLevel(float5);
        }
        else if (integer4 == 8) {
            this.level.setThunderLevel(float5);
        }
        else if (integer4 == 9) {
            this.level.playSound(awg3, awg3.x, awg3.y, awg3.z, SoundEvents.PUFFER_FISH_STING, SoundSource.NEUTRAL, 1.0f, 1.0f);
        }
        else if (integer4 == 10) {
            this.level.addParticle(ParticleTypes.ELDER_GUARDIAN, awg3.x, awg3.y, awg3.z, 0.0, 0.0, 0.0);
            this.level.playSound(awg3, awg3.x, awg3.y, awg3.z, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0f, 1.0f);
        }
    }
    
    public void handleMapItemData(final ClientboundMapItemDataPacket lt) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lt, this, this.minecraft);
        final MapRenderer cyx3 = this.minecraft.gameRenderer.getMapRenderer();
        final String string4 = MapItem.makeKey(lt.getMapId());
        MapItemSavedData coh5 = this.minecraft.level.getMapData(string4);
        if (coh5 == null) {
            coh5 = new MapItemSavedData(string4);
            if (cyx3.getMapInstanceIfExists(string4) != null) {
                final MapItemSavedData coh6 = cyx3.getData(cyx3.getMapInstanceIfExists(string4));
                if (coh6 != null) {
                    coh5 = coh6;
                }
            }
            this.minecraft.level.setMapData(coh5);
        }
        lt.applyToMap(coh5);
        cyx3.update(coh5);
    }
    
    public void handleLevelEvent(final ClientboundLevelEventPacket lp) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lp, this, this.minecraft);
        if (lp.isGlobalEvent()) {
            this.minecraft.level.globalLevelEvent(lp.getType(), lp.getPos(), lp.getData());
        }
        else {
            this.minecraft.level.levelEvent(lp.getType(), lp.getPos(), lp.getData());
        }
    }
    
    public void handleUpdateAdvancementsPacket(final ClientboundUpdateAdvancementsPacket nn) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)nn, this, this.minecraft);
        this.advancements.update(nn);
    }
    
    public void handleSelectAdvancementsTab(final ClientboundSelectAdvancementsTabPacket mm) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mm, this, this.minecraft);
        final ResourceLocation qv3 = mm.getTab();
        if (qv3 == null) {
            this.advancements.setSelectedTab(null, false);
        }
        else {
            final Advancement q4 = this.advancements.getAdvancements().get(qv3);
            this.advancements.setSelectedTab(q4, false);
        }
    }
    
    public void handleCommands(final ClientboundCommandsPacket ky) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ky, this, this.minecraft);
        this.commands = (CommandDispatcher<SharedSuggestionProvider>)new CommandDispatcher((RootCommandNode)ky.getRoot());
    }
    
    public void handleStopSoundEvent(final ClientboundStopSoundPacket ni) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ni, this, this.minecraft);
        this.minecraft.getSoundManager().stop(ni.getName(), ni.getSource());
    }
    
    public void handleCommandSuggestions(final ClientboundCommandSuggestionsPacket kx) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kx, this, this.minecraft);
        this.suggestionsProvider.completeCustomSuggestions(kx.getId(), kx.getSuggestions());
    }
    
    public void handleUpdateRecipes(final ClientboundUpdateRecipesPacket nq) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)nq, this, this.minecraft);
        this.recipeManager.replaceRecipes((Iterable<Recipe<?>>)nq.getRecipes());
        final MutableSearchTree<RecipeCollection> dzu3 = this.minecraft.<RecipeCollection>getSearchTree(SearchRegistry.RECIPE_COLLECTIONS);
        dzu3.clear();
        final ClientRecipeBook cxr4 = this.minecraft.player.getRecipeBook();
        cxr4.setupCollections();
        cxr4.getCollections().forEach(dzu3::add);
        dzu3.refresh();
    }
    
    public void handleLookAt(final ClientboundPlayerLookAtPacket me) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)me, this, this.minecraft);
        final Vec3 csi3 = me.getPosition(this.level);
        if (csi3 != null) {
            this.minecraft.player.lookAt(me.getFromAnchor(), csi3);
        }
    }
    
    public void handleTagQueryPacket(final ClientboundTagQueryPacket nk) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)nk, this, this.minecraft);
        if (!this.debugQueryHandler.handleResponse(nk.getTransactionId(), nk.getTag())) {
            ClientPacketListener.LOGGER.debug("Got unhandled response to tag query {}", nk.getTransactionId());
        }
    }
    
    public void handleAwardStats(final ClientboundAwardStatsPacket kn) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kn, this, this.minecraft);
        for (final Map.Entry<Stat<?>, Integer> entry4 : kn.getStats().entrySet()) {
            final Stat<?> yv5 = entry4.getKey();
            final int integer6 = (int)entry4.getValue();
            this.minecraft.player.getStats().setValue(this.minecraft.player, yv5, integer6);
        }
        if (this.minecraft.screen instanceof StatsUpdateListener) {
            ((StatsUpdateListener)this.minecraft.screen).onStatsUpdated();
        }
    }
    
    public void handleAddOrRemoveRecipes(final ClientboundRecipePacket mg) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mg, this, this.minecraft);
        final ClientRecipeBook cxr3 = this.minecraft.player.getRecipeBook();
        cxr3.setGuiOpen(mg.isGuiOpen());
        cxr3.setFilteringCraftable(mg.isFilteringCraftable());
        cxr3.setFurnaceGuiOpen(mg.isFurnaceGuiOpen());
        cxr3.setFurnaceFilteringCraftable(mg.isFurnaceFilteringCraftable());
        final ClientboundRecipePacket.State a4 = mg.getState();
        switch (a4) {
            case REMOVE: {
                for (final ResourceLocation qv6 : mg.getRecipes()) {
                    this.recipeManager.byKey(qv6).ifPresent(cxr3::remove);
                }
                break;
            }
            case INIT: {
                for (final ResourceLocation qv6 : mg.getRecipes()) {
                    this.recipeManager.byKey(qv6).ifPresent(cxr3::add);
                }
                for (final ResourceLocation qv6 : mg.getHighlights()) {
                    this.recipeManager.byKey(qv6).ifPresent(cxr3::addHighlight);
                }
                break;
            }
            case ADD: {
                for (final ResourceLocation qv6 : mg.getRecipes()) {
                    this.recipeManager.byKey(qv6).ifPresent(ber -> {
                        cxr3.add(ber);
                        cxr3.addHighlight(ber);
                        RecipeToast.addOrUpdate(this.minecraft.getToasts(), ber);
                    });
                }
                break;
            }
        }
        cxr3.getCollections().forEach(dfc -> dfc.updateKnownRecipes(cxr3));
        if (this.minecraft.screen instanceof RecipeUpdateListener) {
            ((RecipeUpdateListener)this.minecraft.screen).recipesUpdated();
        }
    }
    
    public void handleUpdateMobEffect(final ClientboundUpdateMobEffectPacket np) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)np, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(np.getEntityId());
        if (!(aio3 instanceof LivingEntity)) {
            return;
        }
        final MobEffect aig4 = MobEffect.byId(np.getEffectId());
        if (aig4 == null) {
            return;
        }
        final MobEffectInstance aii5 = new MobEffectInstance(aig4, np.getEffectDurationTicks(), np.getEffectAmplifier(), np.isEffectAmbient(), np.isEffectVisible(), np.effectShowsIcon());
        aii5.setNoCounter(np.isSuperLongDuration());
        ((LivingEntity)aio3).addEffect(aii5);
    }
    
    public void handleUpdateTags(final ClientboundUpdateTagsPacket nr) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)nr, this, this.minecraft);
        this.tags = nr.getTags();
        if (!this.connection.isMemoryConnection()) {
            BlockTags.reset(this.tags.getBlocks());
            ItemTags.reset(this.tags.getItems());
            FluidTags.reset(this.tags.getFluids());
            EntityTypeTags.reset(this.tags.getEntityTypes());
        }
        this.minecraft.<ItemStack>getSearchTree(SearchRegistry.CREATIVE_TAGS).refresh();
    }
    
    public void handlePlayerCombat(final ClientboundPlayerCombatPacket mc) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mc, this, this.minecraft);
        if (mc.event == ClientboundPlayerCombatPacket.Event.ENTITY_DIED) {
            final Entity aio3 = this.level.getEntity(mc.playerId);
            if (aio3 == this.minecraft.player) {
                this.minecraft.setScreen(new DeathScreen(mc.message, this.level.getLevelData().isHardcore()));
            }
        }
    }
    
    public void handleChangeDifficulty(final ClientboundChangeDifficultyPacket ku) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ku, this, this.minecraft);
        this.minecraft.level.getLevelData().setDifficulty(ku.getDifficulty());
        this.minecraft.level.getLevelData().setDifficultyLocked(ku.isLocked());
    }
    
    public void handleSetCamera(final ClientboundSetCameraPacket mo) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mo, this, this.minecraft);
        final Entity aio3 = mo.getEntity(this.level);
        if (aio3 != null) {
            this.minecraft.setCameraEntity(aio3);
        }
    }
    
    public void handleSetBorder(final ClientboundSetBorderPacket mn) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mn, this, this.minecraft);
        mn.applyChanges(this.level.getWorldBorder());
    }
    
    public void handleSetTitles(final ClientboundSetTitlesPacket nf) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)nf, this, this.minecraft);
        final ClientboundSetTitlesPacket.Type a3 = nf.getType();
        String string4 = null;
        String string5 = null;
        final String string6 = (nf.getText() != null) ? nf.getText().getColoredString() : "";
        switch (a3) {
            case TITLE: {
                string4 = string6;
                break;
            }
            case SUBTITLE: {
                string5 = string6;
                break;
            }
            case ACTIONBAR: {
                this.minecraft.gui.setOverlayMessage(string6, false);
                return;
            }
            case RESET: {
                this.minecraft.gui.setTitles("", "", -1, -1, -1);
                this.minecraft.gui.resetTitleTimes();
                return;
            }
        }
        this.minecraft.gui.setTitles(string4, string5, nf.getFadeInTime(), nf.getStayTime(), nf.getFadeOutTime());
    }
    
    public void handleTabListCustomisation(final ClientboundTabListPacket nj) {
        this.minecraft.gui.getTabList().setHeader(nj.getHeader().getColoredString().isEmpty() ? null : nj.getHeader());
        this.minecraft.gui.getTabList().setFooter(nj.getFooter().getColoredString().isEmpty() ? null : nj.getFooter());
    }
    
    public void handleRemoveMobEffect(final ClientboundRemoveMobEffectPacket mi) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mi, this, this.minecraft);
        final Entity aio3 = mi.getEntity(this.level);
        if (aio3 instanceof LivingEntity) {
            ((LivingEntity)aio3).removeEffectNoUpdate(mi.getEffect());
        }
    }
    
    public void handlePlayerInfo(final ClientboundPlayerInfoPacket md) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)md, this, this.minecraft);
        for (final ClientboundPlayerInfoPacket.PlayerUpdate b4 : md.getEntries()) {
            if (md.getAction() == ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER) {
                this.playerInfoMap.remove(b4.getProfile().getId());
            }
            else {
                PlayerInfo dkg5 = (PlayerInfo)this.playerInfoMap.get(b4.getProfile().getId());
                if (md.getAction() == ClientboundPlayerInfoPacket.Action.ADD_PLAYER) {
                    dkg5 = new PlayerInfo(b4);
                    this.playerInfoMap.put(dkg5.getProfile().getId(), dkg5);
                }
                if (dkg5 == null) {
                    continue;
                }
                switch (md.getAction()) {
                    case ADD_PLAYER: {
                        dkg5.setGameMode(b4.getGameMode());
                        dkg5.setLatency(b4.getLatency());
                        dkg5.setTabListDisplayName(b4.getDisplayName());
                        continue;
                    }
                    case UPDATE_GAME_MODE: {
                        dkg5.setGameMode(b4.getGameMode());
                        continue;
                    }
                    case UPDATE_LATENCY: {
                        dkg5.setLatency(b4.getLatency());
                        continue;
                    }
                    case UPDATE_DISPLAY_NAME: {
                        dkg5.setTabListDisplayName(b4.getDisplayName());
                        continue;
                    }
                }
            }
        }
    }
    
    public void handleKeepAlive(final ClientboundKeepAlivePacket ln) {
        this.send(new ServerboundKeepAlivePacket(ln.getId()));
    }
    
    public void handlePlayerAbilities(final ClientboundPlayerAbilitiesPacket mb) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mb, this, this.minecraft);
        final Player awg3 = this.minecraft.player;
        awg3.abilities.flying = mb.isFlying();
        awg3.abilities.instabuild = mb.canInstabuild();
        awg3.abilities.invulnerable = mb.isInvulnerable();
        awg3.abilities.mayfly = mb.canFly();
        awg3.abilities.setFlyingSpeed(mb.getFlyingSpeed());
        awg3.abilities.setWalkingSpeed(mb.getWalkingSpeed());
    }
    
    public void handleSoundEvent(final ClientboundSoundPacket nh) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)nh, this, this.minecraft);
        this.minecraft.level.playSound(this.minecraft.player, nh.getX(), nh.getY(), nh.getZ(), nh.getSound(), nh.getSource(), nh.getVolume(), nh.getPitch());
    }
    
    public void handleSoundEntityEvent(final ClientboundSoundEntityPacket ng) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ng, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(ng.getId());
        if (aio3 == null) {
            return;
        }
        this.minecraft.level.playSound(this.minecraft.player, aio3, ng.getSound(), ng.getSource(), ng.getVolume(), ng.getPitch());
    }
    
    public void handleCustomSoundEvent(final ClientboundCustomSoundPacket lg) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lg, this, this.minecraft);
        this.minecraft.getSoundManager().play(new SimpleSoundInstance(lg.getName(), lg.getSource(), lg.getVolume(), lg.getPitch(), false, 0, SoundInstance.Attenuation.LINEAR, (float)lg.getX(), (float)lg.getY(), (float)lg.getZ(), false));
    }
    
    public void handleResourcePack(final ClientboundResourcePackPacket mj) {
        final String string3 = mj.getUrl();
        final String string4 = mj.getHash();
        if (!this.validateResourcePackUrl(string3)) {
            return;
        }
        if (string3.startsWith("level://")) {
            try {
                final String string5 = URLDecoder.decode(string3.substring("level://".length()), StandardCharsets.UTF_8.toString());
                final File file6 = new File(this.minecraft.gameDirectory, "saves");
                final File file7 = new File(file6, string5);
                if (file7.isFile()) {
                    this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
                    final CompletableFuture<?> completableFuture8 = this.minecraft.getClientPackSource().setServerPack(file7);
                    this.downloadCallback(completableFuture8);
                    return;
                }
            }
            catch (UnsupportedEncodingException ex) {}
            this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
            return;
        }
        final ServerData dki5 = this.minecraft.getCurrentServer();
        if (dki5 != null && dki5.getResourcePackStatus() == ServerData.ServerPackStatus.ENABLED) {
            this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
            this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(string3, string4));
        }
        else if (dki5 == null || dki5.getResourcePackStatus() == ServerData.ServerPackStatus.PROMPT) {
            this.minecraft.execute(() -> this.minecraft.setScreen(new ConfirmScreen(boolean3 -> {
                this.minecraft = Minecraft.getInstance();
                final ServerData dki5 = this.minecraft.getCurrentServer();
                if (boolean3) {
                    if (dki5 != null) {
                        dki5.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                    }
                    this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
                    this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(string3, string4));
                }
                else {
                    if (dki5 != null) {
                        dki5.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
                    }
                    this.send(ServerboundResourcePackPacket.Action.DECLINED);
                }
                ServerList.saveSingleServer(dki5);
                this.minecraft.setScreen(null);
            }, new TranslatableComponent("multiplayer.texturePrompt.line1", new Object[0]), new TranslatableComponent("multiplayer.texturePrompt.line2", new Object[0]))));
        }
        else {
            this.send(ServerboundResourcePackPacket.Action.DECLINED);
        }
    }
    
    private boolean validateResourcePackUrl(final String string) {
        try {
            final URI uRI3 = new URI(string);
            final String string2 = uRI3.getScheme();
            final boolean boolean5 = "level".equals(string2);
            if (!"http".equals(string2) && !"https".equals(string2) && !boolean5) {
                throw new URISyntaxException(string, "Wrong protocol");
            }
            if (boolean5 && (string.contains("..") || !string.endsWith("/resources.zip"))) {
                throw new URISyntaxException(string, "Invalid levelstorage resourcepack path");
            }
        }
        catch (URISyntaxException uRISyntaxException3) {
            this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
            return false;
        }
        return true;
    }
    
    private void downloadCallback(final CompletableFuture<?> completableFuture) {
        completableFuture.thenRun(() -> this.send(ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED)).exceptionally(throwable -> {
            this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
            return null;
        });
    }
    
    private void send(final ServerboundResourcePackPacket.Action a) {
        this.connection.send(new ServerboundResourcePackPacket(a));
    }
    
    public void handleBossUpdate(final ClientboundBossEventPacket kt) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)kt, this, this.minecraft);
        this.minecraft.gui.getBossOverlay().update(kt);
    }
    
    public void handleItemCooldown(final ClientboundCooldownPacket le) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)le, this, this.minecraft);
        if (le.getDuration() == 0) {
            this.minecraft.player.getCooldowns().removeCooldown(le.getItem());
        }
        else {
            this.minecraft.player.getCooldowns().addCooldown(le.getItem(), le.getDuration());
        }
    }
    
    public void handleMoveVehicle(final ClientboundMoveVehiclePacket lw) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lw, this, this.minecraft);
        final Entity aio3 = this.minecraft.player.getRootVehicle();
        if (aio3 != this.minecraft.player && aio3.isControlledByLocalInstance()) {
            aio3.absMoveTo(lw.getX(), lw.getY(), lw.getZ(), lw.getYRot(), lw.getXRot());
            this.connection.send(new ServerboundMoveVehiclePacket(aio3));
        }
    }
    
    public void handleOpenBook(final ClientboundOpenBookPacket lx) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lx, this, this.minecraft);
        final ItemStack bcj3 = this.minecraft.player.getItemInHand(lx.getHand());
        if (bcj3.getItem() == Items.WRITTEN_BOOK) {
            this.minecraft.setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(bcj3)));
        }
    }
    
    public void handleCustomPayload(final ClientboundCustomPayloadPacket lf) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lf, this, this.minecraft);
        final ResourceLocation qv3 = lf.getIdentifier();
        FriendlyByteBuf je4 = null;
        try {
            je4 = lf.getData();
            if (ClientboundCustomPayloadPacket.BRAND.equals(qv3)) {
                this.minecraft.player.setServerBrand(je4.readUtf(32767));
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_PATHFINDING_PACKET.equals(qv3)) {
                final int integer5 = je4.readInt();
                final float float6 = je4.readFloat();
                final Path cnr7 = Path.createFromStream(je4);
                this.minecraft.debugRenderer.pathfindingRenderer.addPath(integer5, cnr7, float6);
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_NEIGHBORSUPDATE_PACKET.equals(qv3)) {
                final long long5 = je4.readVarLong();
                final BlockPos ew7 = je4.readBlockPos();
                ((NeighborsUpdateRenderer)this.minecraft.debugRenderer.neighborsUpdateRenderer).addUpdate(long5, ew7);
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_CAVES_PACKET.equals(qv3)) {
                final BlockPos ew8 = je4.readBlockPos();
                final int integer6 = je4.readInt();
                final List<BlockPos> list7 = (List<BlockPos>)Lists.newArrayList();
                final List<Float> list8 = (List<Float>)Lists.newArrayList();
                for (int integer7 = 0; integer7 < integer6; ++integer7) {
                    list7.add(je4.readBlockPos());
                    list8.add(je4.readFloat());
                }
                this.minecraft.debugRenderer.caveRenderer.addTunnel(ew8, list7, list8);
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_STRUCTURES_PACKET.equals(qv3)) {
                final DimensionType byn5 = DimensionType.getById(je4.readInt());
                final BoundingBox cic6 = new BoundingBox(je4.readInt(), je4.readInt(), je4.readInt(), je4.readInt(), je4.readInt(), je4.readInt());
                final int integer8 = je4.readInt();
                final List<BoundingBox> list9 = (List<BoundingBox>)Lists.newArrayList();
                final List<Boolean> list10 = (List<Boolean>)Lists.newArrayList();
                for (int integer9 = 0; integer9 < integer8; ++integer9) {
                    list9.add(new BoundingBox(je4.readInt(), je4.readInt(), je4.readInt(), je4.readInt(), je4.readInt(), je4.readInt()));
                    list10.add(je4.readBoolean());
                }
                this.minecraft.debugRenderer.structureRenderer.addBoundingBox(cic6, list9, list10, byn5);
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_WORLDGENATTEMPT_PACKET.equals(qv3)) {
                ((WorldGenAttemptRenderer)this.minecraft.debugRenderer.worldGenAttemptRenderer).addPos(je4.readBlockPos(), je4.readFloat(), je4.readFloat(), je4.readFloat(), je4.readFloat(), je4.readFloat());
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_VILLAGE_SECTIONS.equals(qv3)) {
                for (int integer5 = je4.readInt(), integer6 = 0; integer6 < integer5; ++integer6) {
                    this.minecraft.debugRenderer.villageDebugRenderer.setVillageSection(je4.readSectionPos());
                }
                for (int integer6 = je4.readInt(), integer8 = 0; integer8 < integer6; ++integer8) {
                    this.minecraft.debugRenderer.villageDebugRenderer.setNotVillageSection(je4.readSectionPos());
                }
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_POI_ADDED_PACKET.equals(qv3)) {
                final BlockPos ew8 = je4.readBlockPos();
                final String string6 = je4.readUtf();
                final int integer8 = je4.readInt();
                final VillageDebugRenderer.PoiInfo b8 = new VillageDebugRenderer.PoiInfo(ew8, string6, integer8);
                this.minecraft.debugRenderer.villageDebugRenderer.addPoi(b8);
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_POI_REMOVED_PACKET.equals(qv3)) {
                final BlockPos ew8 = je4.readBlockPos();
                this.minecraft.debugRenderer.villageDebugRenderer.removePoi(ew8);
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_POI_TICKET_COUNT_PACKET.equals(qv3)) {
                final BlockPos ew8 = je4.readBlockPos();
                final int integer6 = je4.readInt();
                this.minecraft.debugRenderer.villageDebugRenderer.setFreeTicketCount(ew8, integer6);
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_GOAL_SELECTOR.equals(qv3)) {
                final BlockPos ew8 = je4.readBlockPos();
                final int integer6 = je4.readInt();
                final int integer8 = je4.readInt();
                final List<GoalSelectorDebugRenderer.DebugGoal> list11 = (List<GoalSelectorDebugRenderer.DebugGoal>)Lists.newArrayList();
                for (int integer7 = 0; integer7 < integer8; ++integer7) {
                    final int integer9 = je4.readInt();
                    final boolean boolean11 = je4.readBoolean();
                    final String string7 = je4.readUtf(255);
                    list11.add(new GoalSelectorDebugRenderer.DebugGoal(ew8, integer9, string7, boolean11));
                }
                this.minecraft.debugRenderer.goalSelectorRenderer.addGoalSelector(integer6, list11);
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_RAIDS.equals(qv3)) {
                final int integer5 = je4.readInt();
                final Collection<BlockPos> collection6 = (Collection<BlockPos>)Lists.newArrayList();
                for (int integer8 = 0; integer8 < integer5; ++integer8) {
                    collection6.add(je4.readBlockPos());
                }
                this.minecraft.debugRenderer.raidDebugRenderer.setRaidCenters(collection6);
            }
            else if (ClientboundCustomPayloadPacket.DEBUG_BRAIN.equals(qv3)) {
                final double double5 = je4.readDouble();
                final double double6 = je4.readDouble();
                final double double7 = je4.readDouble();
                final Position fl11 = new PositionImpl(double5, double6, double7);
                final UUID uUID12 = je4.readUUID();
                final int integer10 = je4.readInt();
                final String string8 = je4.readUtf();
                final String string9 = je4.readUtf();
                final int integer11 = je4.readInt();
                final String string10 = je4.readUtf();
                final boolean boolean12 = je4.readBoolean();
                Path cnr8;
                if (boolean12) {
                    cnr8 = Path.createFromStream(je4);
                }
                else {
                    cnr8 = null;
                }
                final boolean boolean13 = je4.readBoolean();
                final VillageDebugRenderer.BrainDump a21 = new VillageDebugRenderer.BrainDump(uUID12, integer10, string8, string9, integer11, fl11, string10, cnr8, boolean13);
                for (int integer12 = je4.readInt(), integer13 = 0; integer13 < integer12; ++integer13) {
                    final String string11 = je4.readUtf();
                    a21.activities.add(string11);
                }
                for (int integer13 = je4.readInt(), integer14 = 0; integer14 < integer13; ++integer14) {
                    final String string12 = je4.readUtf();
                    a21.behaviors.add(string12);
                }
                for (int integer14 = je4.readInt(), integer15 = 0; integer15 < integer14; ++integer15) {
                    final String string13 = je4.readUtf();
                    a21.memories.add(string13);
                }
                for (int integer15 = je4.readInt(), integer16 = 0; integer16 < integer15; ++integer16) {
                    final BlockPos ew9 = je4.readBlockPos();
                    a21.pois.add(ew9);
                }
                for (int integer16 = je4.readInt(), integer17 = 0; integer17 < integer16; ++integer17) {
                    final String string14 = je4.readUtf();
                    a21.gossips.add(string14);
                }
                this.minecraft.debugRenderer.villageDebugRenderer.addOrUpdateBrainDump(a21);
            }
            else {
                ClientPacketListener.LOGGER.warn("Unknown custom packed identifier: {}", qv3);
            }
        }
        finally {
            if (je4 != null) {
                je4.release();
            }
        }
    }
    
    public void handleAddObjective(final ClientboundSetObjectivePacket mz) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mz, this, this.minecraft);
        final Scoreboard cti3 = this.level.getScoreboard();
        final String string4 = mz.getObjectiveName();
        if (mz.getMethod() == 0) {
            cti3.addObjective(string4, ObjectiveCriteria.DUMMY, mz.getDisplayName(), mz.getRenderType());
        }
        else if (cti3.hasObjective(string4)) {
            final Objective ctf5 = cti3.getObjective(string4);
            if (mz.getMethod() == 1) {
                cti3.removeObjective(ctf5);
            }
            else if (mz.getMethod() == 2) {
                ctf5.setRenderType(mz.getRenderType());
                ctf5.setDisplayName(mz.getDisplayName());
            }
        }
    }
    
    public void handleSetScore(final ClientboundSetScorePacket nc) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)nc, this, this.minecraft);
        final Scoreboard cti3 = this.level.getScoreboard();
        final String string4 = nc.getObjectiveName();
        switch (nc.getMethod()) {
            case CHANGE: {
                final Objective ctf5 = cti3.getOrCreateObjective(string4);
                final Score cth6 = cti3.getOrCreatePlayerScore(nc.getOwner(), ctf5);
                cth6.setScore(nc.getScore());
                break;
            }
            case REMOVE: {
                cti3.resetPlayerScore(nc.getOwner(), cti3.getObjective(string4));
                break;
            }
        }
    }
    
    public void handleSetDisplayObjective(final ClientboundSetDisplayObjectivePacket ms) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ms, this, this.minecraft);
        final Scoreboard cti3 = this.level.getScoreboard();
        final String string4 = ms.getObjectiveName();
        final Objective ctf5 = (string4 == null) ? null : cti3.getOrCreateObjective(string4);
        cti3.setDisplayObjective(ms.getSlot(), ctf5);
    }
    
    public void handleSetPlayerTeamPacket(final ClientboundSetPlayerTeamPacket nb) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)nb, this, this.minecraft);
        final Scoreboard cti3 = this.level.getScoreboard();
        PlayerTeam ctg4;
        if (nb.getMethod() == 0) {
            ctg4 = cti3.addPlayerTeam(nb.getName());
        }
        else {
            ctg4 = cti3.getPlayerTeam(nb.getName());
        }
        if (nb.getMethod() == 0 || nb.getMethod() == 2) {
            ctg4.setDisplayName(nb.getDisplayName());
            ctg4.setColor(nb.getColor());
            ctg4.unpackOptions(nb.getOptions());
            final Team.Visibility b5 = Team.Visibility.byName(nb.getNametagVisibility());
            if (b5 != null) {
                ctg4.setNameTagVisibility(b5);
            }
            final Team.CollisionRule a6 = Team.CollisionRule.byName(nb.getCollisionRule());
            if (a6 != null) {
                ctg4.setCollisionRule(a6);
            }
            ctg4.setPlayerPrefix(nb.getPlayerPrefix());
            ctg4.setPlayerSuffix(nb.getPlayerSuffix());
        }
        if (nb.getMethod() == 0 || nb.getMethod() == 3) {
            for (final String string6 : nb.getPlayers()) {
                cti3.addPlayerToTeam(string6, ctg4);
            }
        }
        if (nb.getMethod() == 4) {
            for (final String string6 : nb.getPlayers()) {
                cti3.removePlayerFromTeam(string6, ctg4);
            }
        }
        if (nb.getMethod() == 1) {
            cti3.removePlayerTeam(ctg4);
        }
    }
    
    public void handleParticleEvent(final ClientboundLevelParticlesPacket lq) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lq, this, this.minecraft);
        if (lq.getCount() == 0) {
            final double double3 = lq.getMaxSpeed() * lq.getXDist();
            final double double4 = lq.getMaxSpeed() * lq.getYDist();
            final double double5 = lq.getMaxSpeed() * lq.getZDist();
            try {
                this.level.addParticle(lq.getParticle(), lq.isOverrideLimiter(), lq.getX(), lq.getY(), lq.getZ(), double3, double4, double5);
            }
            catch (Throwable throwable9) {
                ClientPacketListener.LOGGER.warn("Could not spawn particle effect {}", lq.getParticle());
            }
        }
        else {
            for (int integer3 = 0; integer3 < lq.getCount(); ++integer3) {
                final double double6 = this.random.nextGaussian() * lq.getXDist();
                final double double7 = this.random.nextGaussian() * lq.getYDist();
                final double double8 = this.random.nextGaussian() * lq.getZDist();
                final double double9 = this.random.nextGaussian() * lq.getMaxSpeed();
                final double double10 = this.random.nextGaussian() * lq.getMaxSpeed();
                final double double11 = this.random.nextGaussian() * lq.getMaxSpeed();
                try {
                    this.level.addParticle(lq.getParticle(), lq.isOverrideLimiter(), lq.getX() + double6, lq.getY() + double7, lq.getZ() + double8, double9, double10, double11);
                }
                catch (Throwable throwable10) {
                    ClientPacketListener.LOGGER.warn("Could not spawn particle effect {}", lq.getParticle());
                    return;
                }
            }
        }
    }
    
    public void handleUpdateAttributes(final ClientboundUpdateAttributesPacket no) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)no, this, this.minecraft);
        final Entity aio3 = this.level.getEntity(no.getEntityId());
        if (aio3 == null) {
            return;
        }
        if (!(aio3 instanceof LivingEntity)) {
            throw new IllegalStateException(new StringBuilder().append("Server tried to update attributes of a non-living entity (actually: ").append(aio3).append(")").toString());
        }
        final BaseAttributeMap ajr4 = ((LivingEntity)aio3).getAttributes();
        for (final ClientboundUpdateAttributesPacket.AttributeSnapshot a6 : no.getValues()) {
            AttributeInstance ajo7 = ajr4.getInstance(a6.getName());
            if (ajo7 == null) {
                ajo7 = ajr4.registerAttribute(new RangedAttribute(null, a6.getName(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
            }
            ajo7.setBaseValue(a6.getBase());
            ajo7.removeModifiers();
            for (final AttributeModifier ajp9 : a6.getModifiers()) {
                ajo7.addModifier(ajp9);
            }
        }
    }
    
    public void handlePlaceRecipe(final ClientboundPlaceGhostRecipePacket ma) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ma, this, this.minecraft);
        final AbstractContainerMenu ayk3 = this.minecraft.player.containerMenu;
        if (ayk3.containerId != ma.getContainerId() || !ayk3.isSynched(this.minecraft.player)) {
            return;
        }
        this.recipeManager.byKey(ma.getRecipe()).ifPresent(ber -> {
            if (this.minecraft.screen instanceof RecipeUpdateListener) {
                final RecipeBookComponent dey4 = ((RecipeUpdateListener)this.minecraft.screen).getRecipeBookComponent();
                dey4.setupGhostRecipe(ber, ayk3.slots);
            }
        });
    }
    
    public void handleLightUpdatePacked(final ClientboundLightUpdatePacket lr) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lr, this, this.minecraft);
        final int integer3 = lr.getX();
        final int integer4 = lr.getZ();
        final LevelLightEngine clb5 = this.level.getChunkSource().getLightEngine();
        final int integer5 = lr.getSkyYMask();
        final int integer6 = lr.getEmptySkyYMask();
        final Iterator<byte[]> iterator8 = (Iterator<byte[]>)lr.getSkyUpdates().iterator();
        this.readSectionList(integer3, integer4, clb5, LightLayer.SKY, integer5, integer6, iterator8);
        final int integer7 = lr.getBlockYMask();
        final int integer8 = lr.getEmptyBlockYMask();
        final Iterator<byte[]> iterator9 = (Iterator<byte[]>)lr.getBlockUpdates().iterator();
        this.readSectionList(integer3, integer4, clb5, LightLayer.BLOCK, integer7, integer8, iterator9);
    }
    
    public void handleMerchantOffers(final ClientboundMerchantOffersPacket lu) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)lu, this, this.minecraft);
        final AbstractContainerMenu ayk3 = this.minecraft.player.containerMenu;
        if (lu.getContainerId() == ayk3.containerId && ayk3 instanceof MerchantMenu) {
            ((MerchantMenu)ayk3).setOffers(new MerchantOffers(lu.getOffers().createTag()));
            ((MerchantMenu)ayk3).setXp(lu.getVillagerXp());
            ((MerchantMenu)ayk3).setMerchantLevel(lu.getVillagerLevel());
            ((MerchantMenu)ayk3).setShowProgressBar(lu.showProgress());
            ((MerchantMenu)ayk3).setCanRestock(lu.canRestock());
        }
    }
    
    public void handleSetChunkCacheRadius(final ClientboundSetChunkCacheRadiusPacket mr) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mr, this, this.minecraft);
        this.serverChunkRadius = mr.getRadius();
        this.level.getChunkSource().updateViewRadius(mr.getRadius());
    }
    
    public void handleSetChunkCacheCenter(final ClientboundSetChunkCacheCenterPacket mq) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)mq, this, this.minecraft);
        this.level.getChunkSource().updateViewCenter(mq.getX(), mq.getZ());
    }
    
    public void handleBlockBreakAck(final ClientboundBlockBreakAckPacket ko) {
        PacketUtils.<ClientPacketListener>ensureRunningOnSameThread((Packet<ClientPacketListener>)ko, this, this.minecraft);
        this.minecraft.gameMode.handleBlockBreakAck(this.level, ko.getPos(), ko.getState(), ko.action(), ko.allGood());
    }
    
    private void readSectionList(final int integer1, final int integer2, final LevelLightEngine clb, final LightLayer bia, final int integer5, final int integer6, final Iterator<byte[]> iterator) {
        for (int integer7 = 0; integer7 < 18; ++integer7) {
            final int integer8 = -1 + integer7;
            final boolean boolean11 = (integer5 & 1 << integer7) != 0x0;
            final boolean boolean12 = (integer6 & 1 << integer7) != 0x0;
            if (boolean11 || boolean12) {
                clb.queueSectionData(bia, SectionPos.of(integer1, integer8, integer2), boolean11 ? new DataLayer(((byte[])iterator.next()).clone()) : new DataLayer());
                this.level.setSectionDirtyWithNeighbors(integer1, integer8, integer2);
            }
        }
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public Collection<PlayerInfo> getOnlinePlayers() {
        return (Collection<PlayerInfo>)this.playerInfoMap.values();
    }
    
    @Nullable
    public PlayerInfo getPlayerInfo(final UUID uUID) {
        return (PlayerInfo)this.playerInfoMap.get(uUID);
    }
    
    @Nullable
    public PlayerInfo getPlayerInfo(final String string) {
        for (final PlayerInfo dkg4 : this.playerInfoMap.values()) {
            if (dkg4.getProfile().getName().equals(string)) {
                return dkg4;
            }
        }
        return null;
    }
    
    public GameProfile getLocalGameProfile() {
        return this.localGameProfile;
    }
    
    public ClientAdvancements getAdvancements() {
        return this.advancements;
    }
    
    public CommandDispatcher<SharedSuggestionProvider> getCommands() {
        return this.commands;
    }
    
    public MultiPlayerLevel getLevel() {
        return this.level;
    }
    
    public TagManager getTags() {
        return this.tags;
    }
    
    public DebugQueryHandler getDebugQueryHandler() {
        return this.debugQueryHandler;
    }
    
    public UUID getId() {
        return this.id;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
