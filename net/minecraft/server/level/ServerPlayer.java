package net.minecraft.server.level;

import org.apache.logging.log4j.LogManager;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.stats.RecipeBook;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import java.util.Collection;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.OptionalInt;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.util.Unit;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.scores.Team;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.scores.Score;
import java.util.function.Consumer;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReport;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.world.item.ComplexItem;
import java.util.Iterator;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.world.item.ServerItemCooldowns;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import net.minecraft.Util;
import com.google.common.collect.Lists;
import net.minecraft.world.level.Level;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.SectionPos;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.server.PlayerAdvancements;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.entity.player.Player;

public class ServerPlayer extends Player implements ContainerListener {
    private static final Logger LOGGER;
    private String language;
    public ServerGamePacketListenerImpl connection;
    public final MinecraftServer server;
    public final ServerPlayerGameMode gameMode;
    private final List<Integer> entitiesToRemove;
    private final PlayerAdvancements advancements;
    private final ServerStatsCounter stats;
    private float lastRecordedHealthAndAbsorption;
    private int lastRecordedFoodLevel;
    private int lastRecordedAirLevel;
    private int lastRecordedArmor;
    private int lastRecordedLevel;
    private int lastRecordedExperience;
    private float lastSentHealth;
    private int lastSentFood;
    private boolean lastFoodSaturationZero;
    private int lastSentExp;
    private int spawnInvulnerableTime;
    private ChatVisiblity chatVisibility;
    private boolean canChatColor;
    private long lastActionTime;
    private Entity camera;
    private boolean isChangingDimension;
    private boolean seenCredits;
    private final ServerRecipeBook recipeBook;
    private Vec3 levitationStartPos;
    private int levitationStartTime;
    private boolean disconnected;
    @Nullable
    private Vec3 enteredNetherPosition;
    private SectionPos lastSectionPos;
    private int containerCounter;
    public boolean ignoreSlotUpdateHack;
    public int latency;
    public boolean wonGame;
    
    public ServerPlayer(final MinecraftServer minecraftServer, final ServerLevel vk, final GameProfile gameProfile, final ServerPlayerGameMode vm) {
        super(vk, gameProfile);
        this.language = "en_US";
        this.entitiesToRemove = (List<Integer>)Lists.newLinkedList();
        this.lastRecordedHealthAndAbsorption = Float.MIN_VALUE;
        this.lastRecordedFoodLevel = Integer.MIN_VALUE;
        this.lastRecordedAirLevel = Integer.MIN_VALUE;
        this.lastRecordedArmor = Integer.MIN_VALUE;
        this.lastRecordedLevel = Integer.MIN_VALUE;
        this.lastRecordedExperience = Integer.MIN_VALUE;
        this.lastSentHealth = -1.0E8f;
        this.lastSentFood = -99999999;
        this.lastFoodSaturationZero = true;
        this.lastSentExp = -99999999;
        this.spawnInvulnerableTime = 60;
        this.canChatColor = true;
        this.lastActionTime = Util.getMillis();
        this.lastSectionPos = SectionPos.of(0, 0, 0);
        vm.player = this;
        this.gameMode = vm;
        this.server = minecraftServer;
        this.recipeBook = new ServerRecipeBook(minecraftServer.getRecipeManager());
        this.stats = minecraftServer.getPlayerList().getPlayerStats(this);
        this.advancements = minecraftServer.getPlayerList().getPlayerAdvancements(this);
        this.maxUpStep = 1.0f;
        this.fudgeSpawnLocation(vk);
    }
    
    private void fudgeSpawnLocation(final ServerLevel vk) {
        final BlockPos ew3 = vk.getSharedSpawnPos();
        if (vk.dimension.isHasSkyLight() && vk.getLevelData().getGameType() != GameType.ADVENTURE) {
            int integer4 = Math.max(0, this.server.getSpawnRadius(vk));
            final int integer5 = Mth.floor(vk.getWorldBorder().getDistanceToBorder(ew3.getX(), ew3.getZ()));
            if (integer5 < integer4) {
                integer4 = integer5;
            }
            if (integer5 <= 1) {
                integer4 = 1;
            }
            final long long6 = integer4 * 2 + 1;
            final long long7 = long6 * long6;
            final int integer6 = (long7 > 2147483647L) ? Integer.MAX_VALUE : ((int)long7);
            final int integer7 = this.getCoprime(integer6);
            final int integer8 = new Random().nextInt(integer6);
            for (int integer9 = 0; integer9 < integer6; ++integer9) {
                final int integer10 = (integer8 + integer7 * integer9) % integer6;
                final int integer11 = integer10 % (integer4 * 2 + 1);
                final int integer12 = integer10 / (integer4 * 2 + 1);
                final BlockPos ew4 = vk.getDimension().getValidSpawnPosition(ew3.getX() + integer11 - integer4, ew3.getZ() + integer12 - integer4, false);
                if (ew4 != null) {
                    this.moveTo(ew4, 0.0f, 0.0f);
                    if (vk.noCollision(this)) {
                        break;
                    }
                }
            }
        }
        else {
            this.moveTo(ew3, 0.0f, 0.0f);
            while (!vk.noCollision(this) && this.y < 255.0) {
                this.setPos(this.x, this.y + 1.0, this.z);
            }
        }
    }
    
    private int getCoprime(final int integer) {
        return (integer <= 16) ? (integer - 1) : 17;
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("playerGameType", 99)) {
            if (this.getServer().getForceGameType()) {
                this.gameMode.setGameModeForPlayer(this.getServer().getDefaultGameType());
            }
            else {
                this.gameMode.setGameModeForPlayer(GameType.byId(id.getInt("playerGameType")));
            }
        }
        if (id.contains("enteredNetherPosition", 10)) {
            final CompoundTag id2 = id.getCompound("enteredNetherPosition");
            this.enteredNetherPosition = new Vec3(id2.getDouble("x"), id2.getDouble("y"), id2.getDouble("z"));
        }
        this.seenCredits = id.getBoolean("seenCredits");
        if (id.contains("recipeBook", 10)) {
            this.recipeBook.fromNbt(id.getCompound("recipeBook"));
        }
        if (this.isSleeping()) {
            this.stopSleeping();
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("playerGameType", this.gameMode.getGameModeForPlayer().getId());
        id.putBoolean("seenCredits", this.seenCredits);
        if (this.enteredNetherPosition != null) {
            final CompoundTag id2 = new CompoundTag();
            id2.putDouble("x", this.enteredNetherPosition.x);
            id2.putDouble("y", this.enteredNetherPosition.y);
            id2.putDouble("z", this.enteredNetherPosition.z);
            id.put("enteredNetherPosition", (Tag)id2);
        }
        final Entity aio3 = this.getRootVehicle();
        final Entity aio4 = this.getVehicle();
        if (aio4 != null && aio3 != this && aio3.hasOnePlayerPassenger()) {
            final CompoundTag id3 = new CompoundTag();
            final CompoundTag id4 = new CompoundTag();
            aio3.save(id4);
            id3.putUUID("Attach", aio4.getUUID());
            id3.put("Entity", (Tag)id4);
            id.put("RootVehicle", (Tag)id3);
        }
        id.put("recipeBook", (Tag)this.recipeBook.toNbt());
    }
    
    public void setExperiencePoints(final int integer) {
        final float float3 = (float)this.getXpNeededForNextLevel();
        final float float4 = (float3 - 1.0f) / float3;
        this.experienceProgress = Mth.clamp(integer / float3, 0.0f, float4);
        this.lastSentExp = -1;
    }
    
    public void setExperienceLevels(final int integer) {
        this.experienceLevel = integer;
        this.lastSentExp = -1;
    }
    
    @Override
    public void giveExperienceLevels(final int integer) {
        super.giveExperienceLevels(integer);
        this.lastSentExp = -1;
    }
    
    @Override
    public void onEnchantmentPerformed(final ItemStack bcj, final int integer) {
        super.onEnchantmentPerformed(bcj, integer);
        this.lastSentExp = -1;
    }
    
    public void initMenu() {
        this.containerMenu.addSlotListener(this);
    }
    
    @Override
    public void onEnterCombat() {
        super.onEnterCombat();
        this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTER_COMBAT));
    }
    
    @Override
    public void onLeaveCombat() {
        super.onLeaveCombat();
        this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.END_COMBAT));
    }
    
    protected void onInsideBlock(final BlockState bvt) {
        CriteriaTriggers.ENTER_BLOCK.trigger(this, bvt);
    }
    
    @Override
    protected ItemCooldowns createItemCooldowns() {
        return new ServerItemCooldowns(this);
    }
    
    @Override
    public void tick() {
        this.gameMode.tick();
        --this.spawnInvulnerableTime;
        if (this.invulnerableTime > 0) {
            --this.invulnerableTime;
        }
        this.containerMenu.broadcastChanges();
        if (!this.level.isClientSide && !this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }
        while (!this.entitiesToRemove.isEmpty()) {
            final int integer2 = Math.min(this.entitiesToRemove.size(), Integer.MAX_VALUE);
            final int[] arr3 = new int[integer2];
            final Iterator<Integer> iterator4 = (Iterator<Integer>)this.entitiesToRemove.iterator();
            int integer3 = 0;
            while (iterator4.hasNext() && integer3 < integer2) {
                arr3[integer3++] = (int)iterator4.next();
                iterator4.remove();
            }
            this.connection.send(new ClientboundRemoveEntitiesPacket(arr3));
        }
        final Entity aio2 = this.getCamera();
        if (aio2 != this) {
            if (aio2.isAlive()) {
                this.absMoveTo(aio2.x, aio2.y, aio2.z, aio2.yRot, aio2.xRot);
                this.getLevel().getChunkSource().move(this);
                if (this.isSneaking()) {
                    this.setCamera(this);
                }
            }
            else {
                this.setCamera(this);
            }
        }
        CriteriaTriggers.TICK.trigger(this);
        if (this.levitationStartPos != null) {
            CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.tickCount - this.levitationStartTime);
        }
        this.advancements.flushDirty(this);
    }
    
    public void doTick() {
        try {
            if (!this.isSpectator() || this.level.hasChunkAt(new BlockPos(this))) {
                super.tick();
            }
            for (int integer2 = 0; integer2 < this.inventory.getContainerSize(); ++integer2) {
                final ItemStack bcj3 = this.inventory.getItem(integer2);
                if (bcj3.getItem().isComplex()) {
                    final Packet<?> kc4 = ((ComplexItem)bcj3.getItem()).getUpdatePacket(bcj3, this.level, this);
                    if (kc4 != null) {
                        this.connection.send(kc4);
                    }
                }
            }
            if (this.getHealth() != this.lastSentHealth || this.lastSentFood != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0f != this.lastFoodSaturationZero) {
                this.connection.send(new ClientboundSetHealthPacket(this.getHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
                this.lastSentHealth = this.getHealth();
                this.lastSentFood = this.foodData.getFoodLevel();
                this.lastFoodSaturationZero = (this.foodData.getSaturationLevel() == 0.0f);
            }
            if (this.getHealth() + this.getAbsorptionAmount() != this.lastRecordedHealthAndAbsorption) {
                this.lastRecordedHealthAndAbsorption = this.getHealth() + this.getAbsorptionAmount();
                this.updateScoreForCriteria(ObjectiveCriteria.HEALTH, Mth.ceil(this.lastRecordedHealthAndAbsorption));
            }
            if (this.foodData.getFoodLevel() != this.lastRecordedFoodLevel) {
                this.lastRecordedFoodLevel = this.foodData.getFoodLevel();
                this.updateScoreForCriteria(ObjectiveCriteria.FOOD, Mth.ceil((float)this.lastRecordedFoodLevel));
            }
            if (this.getAirSupply() != this.lastRecordedAirLevel) {
                this.lastRecordedAirLevel = this.getAirSupply();
                this.updateScoreForCriteria(ObjectiveCriteria.AIR, Mth.ceil((float)this.lastRecordedAirLevel));
            }
            if (this.getArmorValue() != this.lastRecordedArmor) {
                this.lastRecordedArmor = this.getArmorValue();
                this.updateScoreForCriteria(ObjectiveCriteria.ARMOR, Mth.ceil((float)this.lastRecordedArmor));
            }
            if (this.totalExperience != this.lastRecordedExperience) {
                this.lastRecordedExperience = this.totalExperience;
                this.updateScoreForCriteria(ObjectiveCriteria.EXPERIENCE, Mth.ceil((float)this.lastRecordedExperience));
            }
            if (this.experienceLevel != this.lastRecordedLevel) {
                this.lastRecordedLevel = this.experienceLevel;
                this.updateScoreForCriteria(ObjectiveCriteria.LEVEL, Mth.ceil((float)this.lastRecordedLevel));
            }
            if (this.totalExperience != this.lastSentExp) {
                this.lastSentExp = this.totalExperience;
                this.connection.send(new ClientboundSetExperiencePacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
            }
            if (this.tickCount % 20 == 0) {
                CriteriaTriggers.LOCATION.trigger(this);
            }
        }
        catch (Throwable throwable2) {
            final CrashReport d3 = CrashReport.forThrowable(throwable2, "Ticking player");
            final CrashReportCategory e4 = d3.addCategory("Player being ticked");
            this.fillCrashReportCategory(e4);
            throw new ReportedException(d3);
        }
    }
    
    private void updateScoreForCriteria(final ObjectiveCriteria ctl, final int integer) {
        this.getScoreboard().forAllObjectives(ctl, this.getScoreboardName(), (Consumer<Score>)(cth -> cth.setScore(integer)));
    }
    
    @Override
    public void die(final DamageSource ahx) {
        final boolean boolean3 = this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
        if (boolean3) {
            final Component jo4 = this.getCombatTracker().getDeathMessage();
            this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED, jo4), (future -> {
                if (!future.isSuccess()) {
                    final int integer4 = 256;
                    final String string5 = jo4.getString(256);
                    final Component jo2 = new TranslatableComponent("death.attack.message_too_long", new Object[] { new TextComponent(string5).withStyle(ChatFormatting.YELLOW) });
                    final Component jo3 = new TranslatableComponent("death.attack.even_more_magic", new Object[] { this.getDisplayName() }).withStyle((Consumer<Style>)(jw -> jw.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, jo2))));
                    this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED, jo3));
                }
            }));
            final Team ctk5 = this.getTeam();
            if (ctk5 == null || ctk5.getDeathMessageVisibility() == Team.Visibility.ALWAYS) {
                this.server.getPlayerList().broadcastMessage(jo4);
            }
            else if (ctk5.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
                this.server.getPlayerList().broadcastToTeam(this, jo4);
            }
            else if (ctk5.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
                this.server.getPlayerList().broadcastToAllExceptTeam(this, jo4);
            }
        }
        else {
            this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED));
        }
        this.removeEntitiesOnShoulder();
        if (!this.isSpectator()) {
            this.dropAllDeathLoot(ahx);
        }
        this.getScoreboard().forAllObjectives(ObjectiveCriteria.DEATH_COUNT, this.getScoreboardName(), (Consumer<Score>)Score::increment);
        final LivingEntity aix4 = this.getKillCredit();
        if (aix4 != null) {
            this.awardStat(Stats.ENTITY_KILLED_BY.get(aix4.getType()));
            aix4.awardKillScore(this, this.deathScore, ahx);
            if (!this.level.isClientSide && aix4 instanceof WitherBoss) {
                boolean boolean4 = false;
                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    final BlockPos ew6 = new BlockPos(this.x, this.y, this.z);
                    final BlockState bvt7 = Blocks.WITHER_ROSE.defaultBlockState();
                    if (this.level.getBlockState(ew6).isAir() && bvt7.canSurvive(this.level, ew6)) {
                        this.level.setBlock(ew6, bvt7, 3);
                        boolean4 = true;
                    }
                }
                if (!boolean4) {
                    final ItemEntity atx6 = new ItemEntity(this.level, this.x, this.y, this.z, new ItemStack(Items.WITHER_ROSE));
                    this.level.addFreshEntity(atx6);
                }
            }
        }
        this.awardStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        this.clearFire();
        this.setSharedFlag(0, false);
        this.getCombatTracker().recheckStatus();
    }
    
    public void awardKillScore(final Entity aio, final int integer, final DamageSource ahx) {
        if (aio == this) {
            return;
        }
        super.awardKillScore(aio, integer, ahx);
        this.increaseScore(integer);
        final String string5 = this.getScoreboardName();
        final String string6 = aio.getScoreboardName();
        this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_ALL, string5, (Consumer<Score>)Score::increment);
        if (aio instanceof Player) {
            this.awardStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_PLAYERS, string5, (Consumer<Score>)Score::increment);
        }
        else {
            this.awardStat(Stats.MOB_KILLS);
        }
        this.handleTeamKill(string5, string6, ObjectiveCriteria.TEAM_KILL);
        this.handleTeamKill(string6, string5, ObjectiveCriteria.KILLED_BY_TEAM);
        CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, aio, ahx);
    }
    
    private void handleTeamKill(final String string1, final String string2, final ObjectiveCriteria[] arr) {
        final PlayerTeam ctg5 = this.getScoreboard().getPlayersTeam(string2);
        if (ctg5 != null) {
            final int integer6 = ctg5.getColor().getId();
            if (integer6 >= 0 && integer6 < arr.length) {
                this.getScoreboard().forAllObjectives(arr[integer6], string1, (Consumer<Score>)Score::increment);
            }
        }
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        final boolean boolean4 = this.server.isDedicatedServer() && this.isPvpAllowed() && "fall".equals(ahx.msgId);
        if (!boolean4 && this.spawnInvulnerableTime > 0 && ahx != DamageSource.OUT_OF_WORLD) {
            return false;
        }
        if (ahx instanceof EntityDamageSource) {
            final Entity aio5 = ahx.getEntity();
            if (aio5 instanceof Player && !this.canHarmPlayer((Player)aio5)) {
                return false;
            }
            if (aio5 instanceof AbstractArrow) {
                final AbstractArrow awk6 = (AbstractArrow)aio5;
                final Entity aio6 = awk6.getOwner();
                if (aio6 instanceof Player && !this.canHarmPlayer((Player)aio6)) {
                    return false;
                }
            }
        }
        return super.hurt(ahx, float2);
    }
    
    @Override
    public boolean canHarmPlayer(final Player awg) {
        return this.isPvpAllowed() && super.canHarmPlayer(awg);
    }
    
    private boolean isPvpAllowed() {
        return this.server.isPvpAllowed();
    }
    
    @Nullable
    public Entity changeDimension(final DimensionType byn) {
        this.isChangingDimension = true;
        final DimensionType byn2 = this.dimension;
        if (byn2 == DimensionType.THE_END && byn == DimensionType.OVERWORLD) {
            this.unRide();
            this.getLevel().removePlayerImmediately(this);
            if (!this.wonGame) {
                this.wonGame = true;
                this.connection.send(new ClientboundGameEventPacket(4, this.seenCredits ? 0.0f : 1.0f));
                this.seenCredits = true;
            }
            return this;
        }
        final ServerLevel vk4 = this.server.getLevel(byn2);
        this.dimension = byn;
        final ServerLevel vk5 = this.server.getLevel(byn);
        final LevelData com6 = this.level.getLevelData();
        this.connection.send(new ClientboundRespawnPacket(byn, com6.getGeneratorType(), this.gameMode.getGameModeForPlayer()));
        this.connection.send(new ClientboundChangeDifficultyPacket(com6.getDifficulty(), com6.isDifficultyLocked()));
        final PlayerList xv7 = this.server.getPlayerList();
        xv7.sendPlayerPermissionLevel(this);
        vk4.removePlayerImmediately(this);
        this.removed = false;
        double double8 = this.x;
        double double9 = this.y;
        double double10 = this.z;
        float float14 = this.xRot;
        float float15 = this.yRot;
        final double double11 = 8.0;
        final float float16 = float15;
        vk4.getProfiler().push("moving");
        if (byn2 == DimensionType.OVERWORLD && byn == DimensionType.NETHER) {
            this.enteredNetherPosition = new Vec3(this.x, this.y, this.z);
            double8 /= 8.0;
            double10 /= 8.0;
        }
        else if (byn2 == DimensionType.NETHER && byn == DimensionType.OVERWORLD) {
            double8 *= 8.0;
            double10 *= 8.0;
        }
        else if (byn2 == DimensionType.OVERWORLD && byn == DimensionType.THE_END) {
            final BlockPos ew19 = vk5.getDimensionSpecificSpawn();
            double8 = ew19.getX();
            double9 = ew19.getY();
            double10 = ew19.getZ();
            float15 = 90.0f;
            float14 = 0.0f;
        }
        this.moveTo(double8, double9, double10, float15, float14);
        vk4.getProfiler().pop();
        vk4.getProfiler().push("placing");
        final double double12 = Math.min(-2.9999872E7, vk5.getWorldBorder().getMinX() + 16.0);
        final double double13 = Math.min(-2.9999872E7, vk5.getWorldBorder().getMinZ() + 16.0);
        final double double14 = Math.min(2.9999872E7, vk5.getWorldBorder().getMaxX() - 16.0);
        final double double15 = Math.min(2.9999872E7, vk5.getWorldBorder().getMaxZ() - 16.0);
        double8 = Mth.clamp(double8, double12, double14);
        double10 = Mth.clamp(double10, double13, double15);
        this.moveTo(double8, double9, double10, float15, float14);
        if (byn == DimensionType.THE_END) {
            final int integer27 = Mth.floor(this.x);
            final int integer28 = Mth.floor(this.y) - 1;
            final int integer29 = Mth.floor(this.z);
            final int integer30 = 1;
            final int integer31 = 0;
            for (int integer32 = -2; integer32 <= 2; ++integer32) {
                for (int integer33 = -2; integer33 <= 2; ++integer33) {
                    for (int integer34 = -1; integer34 < 3; ++integer34) {
                        final int integer35 = integer27 + integer33 * 1 + integer32 * 0;
                        final int integer36 = integer28 + integer34;
                        final int integer37 = integer29 + integer33 * 0 - integer32 * 1;
                        final boolean boolean38 = integer34 < 0;
                        vk5.setBlockAndUpdate(new BlockPos(integer35, integer36, integer37), boolean38 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState());
                    }
                }
            }
            this.moveTo(integer27, integer28, integer29, float15, 0.0f);
            this.setDeltaMovement(Vec3.ZERO);
        }
        else if (!vk5.getPortalForcer().findAndMoveToPortal(this, float16)) {
            vk5.getPortalForcer().createPortal(this);
            vk5.getPortalForcer().findAndMoveToPortal(this, float16);
        }
        vk4.getProfiler().pop();
        this.setLevel(vk5);
        vk5.addDuringPortalTeleport(this);
        this.triggerDimensionChangeTriggers(vk4);
        this.connection.teleport(this.x, this.y, this.z, float15, float14);
        this.gameMode.setLevel(vk5);
        this.connection.send(new ClientboundPlayerAbilitiesPacket(this.abilities));
        xv7.sendLevelInfo(this, vk5);
        xv7.sendAllPlayerInfo(this);
        for (final MobEffectInstance aii28 : this.getActiveEffects()) {
            this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), aii28));
        }
        this.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
        this.lastSentExp = -1;
        this.lastSentHealth = -1.0f;
        this.lastSentFood = -1;
        return this;
    }
    
    private void triggerDimensionChangeTriggers(final ServerLevel vk) {
        final DimensionType byn3 = vk.dimension.getType();
        final DimensionType byn4 = this.level.dimension.getType();
        CriteriaTriggers.CHANGED_DIMENSION.trigger(this, byn3, byn4);
        if (byn3 == DimensionType.NETHER && byn4 == DimensionType.OVERWORLD && this.enteredNetherPosition != null) {
            CriteriaTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
        }
        if (byn4 != DimensionType.NETHER) {
            this.enteredNetherPosition = null;
        }
    }
    
    public boolean broadcastToPlayer(final ServerPlayer vl) {
        if (vl.isSpectator()) {
            return this.getCamera() == this;
        }
        return !this.isSpectator() && super.broadcastToPlayer(vl);
    }
    
    private void broadcast(final BlockEntity btw) {
        if (btw != null) {
            final ClientboundBlockEntityDataPacket kq3 = btw.getUpdatePacket();
            if (kq3 != null) {
                this.connection.send(kq3);
            }
        }
    }
    
    @Override
    public void take(final Entity aio, final int integer) {
        super.take(aio, integer);
        this.containerMenu.broadcastChanges();
    }
    
    @Override
    public Either<BedSleepingProblem, Unit> startSleepInBed(final BlockPos ew) {
        return (Either<BedSleepingProblem, Unit>)super.startSleepInBed(ew).ifRight(aag -> {
            this.awardStat(Stats.SLEEP_IN_BED);
            CriteriaTriggers.SLEPT_IN_BED.trigger(this);
        });
    }
    
    @Override
    public void stopSleepInBed(final boolean boolean1, final boolean boolean2, final boolean boolean3) {
        if (this.isSleeping()) {
            this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(this, 2));
        }
        super.stopSleepInBed(boolean1, boolean2, boolean3);
        if (this.connection != null) {
            this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
        }
    }
    
    public boolean startRiding(final Entity aio, final boolean boolean2) {
        final Entity aio2 = this.getVehicle();
        if (!super.startRiding(aio, boolean2)) {
            return false;
        }
        final Entity aio3 = this.getVehicle();
        if (aio3 != aio2 && this.connection != null) {
            this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
        }
        return true;
    }
    
    @Override
    public void stopRiding() {
        final Entity aio2 = this.getVehicle();
        super.stopRiding();
        final Entity aio3 = this.getVehicle();
        if (aio3 != aio2 && this.connection != null) {
            this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
        }
    }
    
    public boolean isInvulnerableTo(final DamageSource ahx) {
        return super.isInvulnerableTo(ahx) || this.isChangingDimension() || (this.abilities.invulnerable && ahx == DamageSource.WITHER);
    }
    
    @Override
    protected void checkFallDamage(final double double1, final boolean boolean2, final BlockState bvt, final BlockPos ew) {
    }
    
    @Override
    protected void onChangedBlock(final BlockPos ew) {
        if (!this.isSpectator()) {
            super.onChangedBlock(ew);
        }
    }
    
    public void doCheckFallDamage(final double double1, final boolean boolean2) {
        final int integer5 = Mth.floor(this.x);
        final int integer6 = Mth.floor(this.y - 0.20000000298023224);
        final int integer7 = Mth.floor(this.z);
        BlockPos ew8 = new BlockPos(integer5, integer6, integer7);
        if (!this.level.hasChunkAt(ew8)) {
            return;
        }
        BlockState bvt9 = this.level.getBlockState(ew8);
        if (bvt9.isAir()) {
            final BlockPos ew9 = ew8.below();
            final BlockState bvt10 = this.level.getBlockState(ew9);
            final Block bmv12 = bvt10.getBlock();
            if (bmv12.is(BlockTags.FENCES) || bmv12.is(BlockTags.WALLS) || bmv12 instanceof FenceGateBlock) {
                ew8 = ew9;
                bvt9 = bvt10;
            }
        }
        super.checkFallDamage(double1, boolean2, bvt9, ew8);
    }
    
    @Override
    public void openTextEdit(final SignBlockEntity bus) {
        bus.setAllowedPlayerEditor(this);
        this.connection.send(new ClientboundOpenSignEditorPacket(bus.getBlockPos()));
    }
    
    private void nextContainerCounter() {
        this.containerCounter = this.containerCounter % 100 + 1;
    }
    
    @Override
    public OptionalInt openMenu(@Nullable final MenuProvider ahm) {
        if (ahm == null) {
            return OptionalInt.empty();
        }
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        final AbstractContainerMenu ayk3 = ahm.createMenu(this.containerCounter, this.inventory, this);
        if (ayk3 == null) {
            if (this.isSpectator()) {
                this.displayClientMessage(new TranslatableComponent("container.spectatorCantOpen", new Object[0]).withStyle(ChatFormatting.RED), true);
            }
            return OptionalInt.empty();
        }
        this.connection.send(new ClientboundOpenScreenPacket(ayk3.containerId, ayk3.getType(), ahm.getDisplayName()));
        ayk3.addSlotListener(this);
        this.containerMenu = ayk3;
        return OptionalInt.of(this.containerCounter);
    }
    
    @Override
    public void sendMerchantOffers(final int integer1, final MerchantOffers bgv, final int integer3, final int integer4, final boolean boolean5, final boolean boolean6) {
        this.connection.send(new ClientboundMerchantOffersPacket(integer1, bgv, integer3, integer4, boolean5, boolean6));
    }
    
    @Override
    public void openHorseInventory(final AbstractHorse asb, final Container ahc) {
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        this.connection.send(new ClientboundHorseScreenOpenPacket(this.containerCounter, ahc.getContainerSize(), asb.getId()));
        (this.containerMenu = new HorseInventoryMenu(this.containerCounter, this.inventory, ahc, asb)).addSlotListener(this);
    }
    
    @Override
    public void openItemGui(final ItemStack bcj, final InteractionHand ahi) {
        final Item bce4 = bcj.getItem();
        if (bce4 == Items.WRITTEN_BOOK) {
            if (WrittenBookItem.resolveBookComponents(bcj, this.createCommandSourceStack(), this)) {
                this.containerMenu.broadcastChanges();
            }
            this.connection.send(new ClientboundOpenBookPacket(ahi));
        }
    }
    
    @Override
    public void openCommandBlock(final CommandBlockEntity bub) {
        bub.setSendToClient(true);
        this.broadcast(bub);
    }
    
    @Override
    public void slotChanged(final AbstractContainerMenu ayk, final int integer, final ItemStack bcj) {
        if (ayk.getSlot(integer) instanceof ResultSlot) {
            return;
        }
        if (ayk == this.inventoryMenu) {
            CriteriaTriggers.INVENTORY_CHANGED.trigger(this, this.inventory);
        }
        if (this.ignoreSlotUpdateHack) {
            return;
        }
        this.connection.send(new ClientboundContainerSetSlotPacket(ayk.containerId, integer, bcj));
    }
    
    public void refreshContainer(final AbstractContainerMenu ayk) {
        this.refreshContainer(ayk, ayk.getItems());
    }
    
    @Override
    public void refreshContainer(final AbstractContainerMenu ayk, final NonNullList<ItemStack> fk) {
        this.connection.send(new ClientboundContainerSetContentPacket(ayk.containerId, fk));
        this.connection.send(new ClientboundContainerSetSlotPacket(-1, -1, this.inventory.getCarried()));
    }
    
    @Override
    public void setContainerData(final AbstractContainerMenu ayk, final int integer2, final int integer3) {
        this.connection.send(new ClientboundContainerSetDataPacket(ayk.containerId, integer2, integer3));
    }
    
    public void closeContainer() {
        this.connection.send(new ClientboundContainerClosePacket(this.containerMenu.containerId));
        this.doCloseContainer();
    }
    
    public void broadcastCarriedItem() {
        if (this.ignoreSlotUpdateHack) {
            return;
        }
        this.connection.send(new ClientboundContainerSetSlotPacket(-1, -1, this.inventory.getCarried()));
    }
    
    public void doCloseContainer() {
        this.containerMenu.removed(this);
        this.containerMenu = this.inventoryMenu;
    }
    
    public void setPlayerInput(final float float1, final float float2, final boolean boolean3, final boolean boolean4) {
        if (this.isPassenger()) {
            if (float1 >= -1.0f && float1 <= 1.0f) {
                this.xxa = float1;
            }
            if (float2 >= -1.0f && float2 <= 1.0f) {
                this.zza = float2;
            }
            this.jumping = boolean3;
            this.setSneaking(boolean4);
        }
    }
    
    @Override
    public void awardStat(final Stat<?> yv, final int integer) {
        this.stats.increment(this, yv, integer);
        this.getScoreboard().forAllObjectives(yv, this.getScoreboardName(), (Consumer<Score>)(cth -> cth.add(integer)));
    }
    
    @Override
    public void resetStat(final Stat<?> yv) {
        this.stats.setValue(this, yv, 0);
        this.getScoreboard().forAllObjectives(yv, this.getScoreboardName(), (Consumer<Score>)Score::reset);
    }
    
    @Override
    public int awardRecipes(final Collection<Recipe<?>> collection) {
        return this.recipeBook.addRecipes(collection, this);
    }
    
    @Override
    public void awardRecipesByKey(final ResourceLocation[] arr) {
        final List<Recipe<?>> list3 = (List<Recipe<?>>)Lists.newArrayList();
        for (final ResourceLocation qv7 : arr) {
            this.server.getRecipeManager().byKey(qv7).ifPresent(list3::add);
        }
        this.awardRecipes((Collection<Recipe<?>>)list3);
    }
    
    @Override
    public int resetRecipes(final Collection<Recipe<?>> collection) {
        return this.recipeBook.removeRecipes(collection, this);
    }
    
    @Override
    public void giveExperiencePoints(final int integer) {
        super.giveExperiencePoints(integer);
        this.lastSentExp = -1;
    }
    
    public void disconnect() {
        this.disconnected = true;
        this.ejectPassengers();
        if (this.isSleeping()) {
            this.stopSleepInBed(true, false, false);
        }
    }
    
    public boolean hasDisconnected() {
        return this.disconnected;
    }
    
    public void resetSentInfo() {
        this.lastSentHealth = -1.0E8f;
    }
    
    @Override
    public void displayClientMessage(final Component jo, final boolean boolean2) {
        this.connection.send(new ClientboundChatPacket(jo, boolean2 ? ChatType.GAME_INFO : ChatType.CHAT));
    }
    
    @Override
    protected void completeUsingItem() {
        if (!this.useItem.isEmpty() && this.isUsingItem()) {
            this.connection.send(new ClientboundEntityEventPacket(this, (byte)9));
            super.completeUsingItem();
        }
    }
    
    @Override
    public void lookAt(final EntityAnchorArgument.Anchor a, final Vec3 csi) {
        super.lookAt(a, csi);
        this.connection.send(new ClientboundPlayerLookAtPacket(a, csi.x, csi.y, csi.z));
    }
    
    public void lookAt(final EntityAnchorArgument.Anchor a1, final Entity aio, final EntityAnchorArgument.Anchor a3) {
        final Vec3 csi5 = a3.apply(aio);
        super.lookAt(a1, csi5);
        this.connection.send(new ClientboundPlayerLookAtPacket(a1, aio, a3));
    }
    
    public void restoreFrom(final ServerPlayer vl, final boolean boolean2) {
        if (boolean2) {
            this.inventory.replaceWith(vl.inventory);
            this.setHealth(vl.getHealth());
            this.foodData = vl.foodData;
            this.experienceLevel = vl.experienceLevel;
            this.totalExperience = vl.totalExperience;
            this.experienceProgress = vl.experienceProgress;
            this.setScore(vl.getScore());
            this.portalEntranceBlock = vl.portalEntranceBlock;
            this.portalEntranceOffset = vl.portalEntranceOffset;
            this.portalEntranceForwards = vl.portalEntranceForwards;
        }
        else if (this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || vl.isSpectator()) {
            this.inventory.replaceWith(vl.inventory);
            this.experienceLevel = vl.experienceLevel;
            this.totalExperience = vl.totalExperience;
            this.experienceProgress = vl.experienceProgress;
            this.setScore(vl.getScore());
        }
        this.enchantmentSeed = vl.enchantmentSeed;
        this.enderChestInventory = vl.enderChestInventory;
        this.getEntityData().<Byte>set(ServerPlayer.DATA_PLAYER_MODE_CUSTOMISATION, (Byte)vl.getEntityData().<T>get((EntityDataAccessor<T>)ServerPlayer.DATA_PLAYER_MODE_CUSTOMISATION));
        this.lastSentExp = -1;
        this.lastSentHealth = -1.0f;
        this.lastSentFood = -1;
        this.recipeBook.copyOverData(vl.recipeBook);
        this.entitiesToRemove.addAll((Collection)vl.entitiesToRemove);
        this.seenCredits = vl.seenCredits;
        this.enteredNetherPosition = vl.enteredNetherPosition;
        this.setShoulderEntityLeft(vl.getShoulderEntityLeft());
        this.setShoulderEntityRight(vl.getShoulderEntityRight());
    }
    
    @Override
    protected void onEffectAdded(final MobEffectInstance aii) {
        super.onEffectAdded(aii);
        this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), aii));
        if (aii.getEffect() == MobEffects.LEVITATION) {
            this.levitationStartTime = this.tickCount;
            this.levitationStartPos = new Vec3(this.x, this.y, this.z);
        }
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
    }
    
    @Override
    protected void onEffectUpdated(final MobEffectInstance aii, final boolean boolean2) {
        super.onEffectUpdated(aii, boolean2);
        this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), aii));
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
    }
    
    @Override
    protected void onEffectRemoved(final MobEffectInstance aii) {
        super.onEffectRemoved(aii);
        this.connection.send(new ClientboundRemoveMobEffectPacket(this.getId(), aii.getEffect()));
        if (aii.getEffect() == MobEffects.LEVITATION) {
            this.levitationStartPos = null;
        }
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
    }
    
    public void teleportTo(final double double1, final double double2, final double double3) {
        this.connection.teleport(double1, double2, double3, this.yRot, this.xRot);
    }
    
    @Override
    public void crit(final Entity aio) {
        this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(aio, 4));
    }
    
    @Override
    public void magicCrit(final Entity aio) {
        this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(aio, 5));
    }
    
    @Override
    public void onUpdateAbilities() {
        if (this.connection == null) {
            return;
        }
        this.connection.send(new ClientboundPlayerAbilitiesPacket(this.abilities));
        this.updateInvisibilityStatus();
    }
    
    public ServerLevel getLevel() {
        return (ServerLevel)this.level;
    }
    
    @Override
    public void setGameMode(final GameType bho) {
        this.gameMode.setGameModeForPlayer(bho);
        this.connection.send(new ClientboundGameEventPacket(3, (float)bho.getId()));
        if (bho == GameType.SPECTATOR) {
            this.removeEntitiesOnShoulder();
            this.stopRiding();
        }
        else {
            this.setCamera(this);
        }
        this.onUpdateAbilities();
        this.updateEffectVisibility();
    }
    
    @Override
    public boolean isSpectator() {
        return this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
    }
    
    @Override
    public boolean isCreative() {
        return this.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
    }
    
    public void sendMessage(final Component jo) {
        this.sendMessage(jo, ChatType.SYSTEM);
    }
    
    public void sendMessage(final Component jo, final ChatType jm) {
        this.connection.send(new ClientboundChatPacket(jo, jm), (future -> {
            if (!future.isSuccess() && (jm == ChatType.GAME_INFO || jm == ChatType.SYSTEM)) {
                final int integer5 = 256;
                final String string6 = jo.getString(256);
                final Component jo2 = new TextComponent(string6).withStyle(ChatFormatting.YELLOW);
                this.connection.send(new ClientboundChatPacket(new TranslatableComponent("multiplayer.message_not_delivered", new Object[] { jo2 }).withStyle(ChatFormatting.RED), ChatType.SYSTEM));
            }
        }));
    }
    
    public String getIpAddress() {
        String string2 = this.connection.connection.getRemoteAddress().toString();
        string2 = string2.substring(string2.indexOf("/") + 1);
        string2 = string2.substring(0, string2.indexOf(":"));
        return string2;
    }
    
    public void updateOptions(final ServerboundClientInformationPacket oa) {
        this.language = oa.getLanguage();
        this.chatVisibility = oa.getChatVisibility();
        this.canChatColor = oa.getChatColors();
        this.getEntityData().<Byte>set(ServerPlayer.DATA_PLAYER_MODE_CUSTOMISATION, (byte)oa.getModelCustomisation());
        this.getEntityData().<Byte>set(ServerPlayer.DATA_PLAYER_MAIN_HAND, (byte)((oa.getMainHand() != HumanoidArm.LEFT) ? 1 : 0));
    }
    
    public ChatVisiblity getChatVisibility() {
        return this.chatVisibility;
    }
    
    public void sendTexturePack(final String string1, final String string2) {
        this.connection.send(new ClientboundResourcePackPacket(string1, string2));
    }
    
    protected int getPermissionLevel() {
        return this.server.getProfilePermissions(this.getGameProfile());
    }
    
    public void resetLastActionTime() {
        this.lastActionTime = Util.getMillis();
    }
    
    public ServerStatsCounter getStats() {
        return this.stats;
    }
    
    public ServerRecipeBook getRecipeBook() {
        return this.recipeBook;
    }
    
    public void sendRemoveEntity(final Entity aio) {
        if (aio instanceof Player) {
            this.connection.send(new ClientboundRemoveEntitiesPacket(new int[] { aio.getId() }));
        }
        else {
            this.entitiesToRemove.add(aio.getId());
        }
    }
    
    public void cancelRemoveEntity(final Entity aio) {
        this.entitiesToRemove.remove(aio.getId());
    }
    
    @Override
    protected void updateInvisibilityStatus() {
        if (this.isSpectator()) {
            this.removeEffectParticles();
            this.setInvisible(true);
        }
        else {
            super.updateInvisibilityStatus();
        }
    }
    
    public Entity getCamera() {
        return (this.camera == null) ? this : this.camera;
    }
    
    public void setCamera(final Entity aio) {
        final Entity aio2 = this.getCamera();
        this.camera = ((aio == null) ? this : aio);
        if (aio2 != this.camera) {
            this.connection.send(new ClientboundSetCameraPacket(this.camera));
            this.teleportTo(this.camera.x, this.camera.y, this.camera.z);
        }
    }
    
    protected void processDimensionDelay() {
        if (this.changingDimensionDelay > 0 && !this.isChangingDimension) {
            --this.changingDimensionDelay;
        }
    }
    
    @Override
    public void attack(final Entity aio) {
        if (this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
            this.setCamera(aio);
        }
        else {
            super.attack(aio);
        }
    }
    
    public long getLastActionTime() {
        return this.lastActionTime;
    }
    
    @Nullable
    public Component getTabListDisplayName() {
        return null;
    }
    
    @Override
    public void swing(final InteractionHand ahi) {
        super.swing(ahi);
        this.resetAttackStrengthTicker();
    }
    
    public boolean isChangingDimension() {
        return this.isChangingDimension;
    }
    
    public void hasChangedDimension() {
        this.isChangingDimension = false;
    }
    
    public void startFallFlying() {
        this.setSharedFlag(7, true);
    }
    
    public void stopFallFlying() {
        this.setSharedFlag(7, true);
        this.setSharedFlag(7, false);
    }
    
    public PlayerAdvancements getAdvancements() {
        return this.advancements;
    }
    
    public void teleportTo(final ServerLevel vk, final double double2, final double double3, final double double4, final float float5, final float float6) {
        this.setCamera(this);
        this.stopRiding();
        if (vk == this.level) {
            this.connection.teleport(double2, double3, double4, float5, float6);
        }
        else {
            final ServerLevel vk2 = this.getLevel();
            this.dimension = vk.dimension.getType();
            final LevelData com12 = vk.getLevelData();
            this.connection.send(new ClientboundRespawnPacket(this.dimension, com12.getGeneratorType(), this.gameMode.getGameModeForPlayer()));
            this.connection.send(new ClientboundChangeDifficultyPacket(com12.getDifficulty(), com12.isDifficultyLocked()));
            this.server.getPlayerList().sendPlayerPermissionLevel(this);
            vk2.removePlayerImmediately(this);
            this.removed = false;
            this.moveTo(double2, double3, double4, float5, float6);
            this.setLevel(vk);
            vk.addDuringCommandTeleport(this);
            this.triggerDimensionChangeTriggers(vk2);
            this.connection.teleport(double2, double3, double4, float5, float6);
            this.gameMode.setLevel(vk);
            this.server.getPlayerList().sendLevelInfo(this, vk);
            this.server.getPlayerList().sendAllPlayerInfo(this);
        }
    }
    
    public void trackChunk(final ChunkPos bhd, final Packet<?> kc2, final Packet<?> kc3) {
        this.connection.send(kc3);
        this.connection.send(kc2);
    }
    
    public void untrackChunk(final ChunkPos bhd) {
        this.connection.send(new ClientboundForgetLevelChunkPacket(bhd.x, bhd.z));
    }
    
    public SectionPos getLastSectionPos() {
        return this.lastSectionPos;
    }
    
    public void setLastSectionPos(final SectionPos fp) {
        this.lastSectionPos = fp;
    }
    
    @Override
    public void playNotifySound(final SoundEvent yo, final SoundSource yq, final float float3, final float float4) {
        this.connection.send(new ClientboundSoundPacket(yo, yq, this.x, this.y, this.z, float3, float4));
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddPlayerPacket(this);
    }
    
    @Override
    public ItemEntity drop(final ItemStack bcj, final boolean boolean2, final boolean boolean3) {
        final ItemEntity atx5 = super.drop(bcj, boolean2, boolean3);
        if (atx5 == null) {
            return null;
        }
        this.level.addFreshEntity(atx5);
        final ItemStack bcj2 = atx5.getItem();
        if (boolean3) {
            if (!bcj2.isEmpty()) {
                this.awardStat(Stats.ITEM_DROPPED.get(bcj2.getItem()), bcj.getCount());
            }
            this.awardStat(Stats.DROP);
        }
        return atx5;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
