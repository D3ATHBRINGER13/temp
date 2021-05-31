package net.minecraft.world.entity.raid;

import java.util.Locale;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import javax.annotation.Nullable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import java.util.stream.Stream;
import java.util.Comparator;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.Difficulty;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import java.util.function.Predicate;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.BossEvent;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.Optional;
import java.util.Random;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import java.util.UUID;
import java.util.Set;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class Raid {
    private static final TranslatableComponent RAID_NAME_COMPONENT;
    private static final TranslatableComponent VICTORY;
    private static final TranslatableComponent DEFEAT;
    private static final Component RAID_BAR_VICTORY_COMPONENT;
    private static final Component RAID_BAR_DEFEAT_COMPONENT;
    private final Map<Integer, Raider> groupToLeaderMap;
    private final Map<Integer, Set<Raider>> groupRaiderMap;
    private final Set<UUID> heroesOfTheVillage;
    private long ticksActive;
    private BlockPos center;
    private final ServerLevel level;
    private boolean started;
    private final int id;
    private float totalHealth;
    private int badOmenLevel;
    private boolean active;
    private int groupsSpawned;
    private final ServerBossEvent raidEvent;
    private int postRaidTicks;
    private int raidCooldownTicks;
    private final Random random;
    private final int numGroups;
    private RaidStatus status;
    private int celebrationTicks;
    private Optional<BlockPos> waveSpawnPos;
    
    public Raid(final int integer, final ServerLevel vk, final BlockPos ew) {
        this.groupToLeaderMap = (Map<Integer, Raider>)Maps.newHashMap();
        this.groupRaiderMap = (Map<Integer, Set<Raider>>)Maps.newHashMap();
        this.heroesOfTheVillage = (Set<UUID>)Sets.newHashSet();
        this.raidEvent = new ServerBossEvent(Raid.RAID_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
        this.random = new Random();
        this.waveSpawnPos = (Optional<BlockPos>)Optional.empty();
        this.id = integer;
        this.level = vk;
        this.active = true;
        this.raidCooldownTicks = 300;
        this.raidEvent.setPercent(0.0f);
        this.center = ew;
        this.numGroups = this.getNumGroups(vk.getDifficulty());
        this.status = RaidStatus.ONGOING;
    }
    
    public Raid(final ServerLevel vk, final CompoundTag id) {
        this.groupToLeaderMap = (Map<Integer, Raider>)Maps.newHashMap();
        this.groupRaiderMap = (Map<Integer, Set<Raider>>)Maps.newHashMap();
        this.heroesOfTheVillage = (Set<UUID>)Sets.newHashSet();
        this.raidEvent = new ServerBossEvent(Raid.RAID_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
        this.random = new Random();
        this.waveSpawnPos = (Optional<BlockPos>)Optional.empty();
        this.level = vk;
        this.id = id.getInt("Id");
        this.started = id.getBoolean("Started");
        this.active = id.getBoolean("Active");
        this.ticksActive = id.getLong("TicksActive");
        this.badOmenLevel = id.getInt("BadOmenLevel");
        this.groupsSpawned = id.getInt("GroupsSpawned");
        this.raidCooldownTicks = id.getInt("PreRaidTicks");
        this.postRaidTicks = id.getInt("PostRaidTicks");
        this.totalHealth = id.getFloat("TotalHealth");
        this.center = new BlockPos(id.getInt("CX"), id.getInt("CY"), id.getInt("CZ"));
        this.numGroups = id.getInt("NumGroups");
        this.status = getByName(id.getString("Status"));
        this.heroesOfTheVillage.clear();
        if (id.contains("HeroesOfTheVillage", 9)) {
            final ListTag ik4 = id.getList("HeroesOfTheVillage", 10);
            for (int integer5 = 0; integer5 < ik4.size(); ++integer5) {
                final CompoundTag id2 = ik4.getCompound(integer5);
                final UUID uUID7 = id2.getUUID("UUID");
                this.heroesOfTheVillage.add(uUID7);
            }
        }
    }
    
    public boolean isOver() {
        return this.isVictory() || this.isLoss();
    }
    
    public boolean isBetweenWaves() {
        return this.hasFirstWaveSpawned() && this.getTotalRaidersAlive() == 0 && this.raidCooldownTicks > 0;
    }
    
    public boolean hasFirstWaveSpawned() {
        return this.groupsSpawned > 0;
    }
    
    public boolean isStopped() {
        return this.status == RaidStatus.STOPPED;
    }
    
    public boolean isVictory() {
        return this.status == RaidStatus.VICTORY;
    }
    
    public boolean isLoss() {
        return this.status == RaidStatus.LOSS;
    }
    
    public Level getLevel() {
        return this.level;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public int getGroupsSpawned() {
        return this.groupsSpawned;
    }
    
    private Predicate<ServerPlayer> validPlayer() {
        return (Predicate<ServerPlayer>)(vl -> {
            final BlockPos ew3 = new BlockPos(vl);
            return vl.isAlive() && this.level.getRaidAt(ew3) == this;
        });
    }
    
    private void updatePlayers() {
        final Set<ServerPlayer> set2 = (Set<ServerPlayer>)Sets.newHashSet((Iterable)this.raidEvent.getPlayers());
        final List<ServerPlayer> list3 = this.level.getPlayers(this.validPlayer());
        for (final ServerPlayer vl5 : list3) {
            if (!set2.contains(vl5)) {
                this.raidEvent.addPlayer(vl5);
            }
        }
        for (final ServerPlayer vl5 : set2) {
            if (!list3.contains(vl5)) {
                this.raidEvent.removePlayer(vl5);
            }
        }
    }
    
    public int getMaxBadOmenLevel() {
        return 5;
    }
    
    public int getBadOmenLevel() {
        return this.badOmenLevel;
    }
    
    public void absorbBadOmen(final Player awg) {
        if (awg.hasEffect(MobEffects.BAD_OMEN)) {
            this.badOmenLevel += awg.getEffect(MobEffects.BAD_OMEN).getAmplifier() + 1;
            this.badOmenLevel = Mth.clamp(this.badOmenLevel, 0, this.getMaxBadOmenLevel());
        }
        awg.removeEffect(MobEffects.BAD_OMEN);
    }
    
    public void stop() {
        this.active = false;
        this.raidEvent.removeAllPlayers();
        this.status = RaidStatus.STOPPED;
    }
    
    public void tick() {
        if (this.isStopped()) {
            return;
        }
        if (this.status == RaidStatus.ONGOING) {
            final boolean boolean2 = this.active;
            this.active = this.level.hasChunkAt(this.center);
            if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                this.stop();
                return;
            }
            if (boolean2 != this.active) {
                this.raidEvent.setVisible(this.active);
            }
            if (!this.active) {
                return;
            }
            if (!this.level.isVillage(this.center)) {
                this.moveRaidCenterToNearbyVillageSection();
            }
            if (!this.level.isVillage(this.center)) {
                if (this.groupsSpawned > 0) {
                    this.status = RaidStatus.LOSS;
                }
                else {
                    this.stop();
                }
            }
            ++this.ticksActive;
            if (this.ticksActive >= 48000L) {
                this.stop();
                return;
            }
            final int integer3 = this.getTotalRaidersAlive();
            if (integer3 == 0 && this.hasMoreWaves()) {
                if (this.raidCooldownTicks > 0) {
                    final boolean boolean3 = this.waveSpawnPos.isPresent();
                    boolean boolean4 = !boolean3 && this.raidCooldownTicks % 5 == 0;
                    if (boolean3 && !this.level.getChunkSource().isEntityTickingChunk(new ChunkPos((BlockPos)this.waveSpawnPos.get()))) {
                        boolean4 = true;
                    }
                    if (boolean4) {
                        int integer4 = 0;
                        if (this.raidCooldownTicks < 100) {
                            integer4 = 1;
                        }
                        else if (this.raidCooldownTicks < 40) {
                            integer4 = 2;
                        }
                        this.waveSpawnPos = this.getValidSpawnPos(integer4);
                    }
                    if (this.raidCooldownTicks == 300 || this.raidCooldownTicks % 20 == 0) {
                        this.updatePlayers();
                    }
                    --this.raidCooldownTicks;
                    this.raidEvent.setPercent(Mth.clamp((300 - this.raidCooldownTicks) / 300.0f, 0.0f, 1.0f));
                }
                else if (this.raidCooldownTicks == 0 && this.groupsSpawned > 0) {
                    this.raidCooldownTicks = 300;
                    this.raidEvent.setName(Raid.RAID_NAME_COMPONENT);
                    return;
                }
            }
            if (this.ticksActive % 20L == 0L) {
                this.updatePlayers();
                this.updateRaiders();
                if (integer3 > 0) {
                    if (integer3 <= 2) {
                        this.raidEvent.setName(Raid.RAID_NAME_COMPONENT.copy().append(" - ").append(new TranslatableComponent("event.minecraft.raid.raiders_remaining", new Object[] { integer3 })));
                    }
                    else {
                        this.raidEvent.setName(Raid.RAID_NAME_COMPONENT);
                    }
                }
                else {
                    this.raidEvent.setName(Raid.RAID_NAME_COMPONENT);
                }
            }
            boolean boolean3 = false;
            int integer5 = 0;
            while (this.shouldSpawnGroup()) {
                final BlockPos ew6 = (BlockPos)(this.waveSpawnPos.isPresent() ? this.waveSpawnPos.get() : this.findRandomSpawnPos(integer5, 20));
                if (ew6 != null) {
                    this.started = true;
                    this.spawnGroup(ew6);
                    if (!boolean3) {
                        this.playSound(ew6);
                        boolean3 = true;
                    }
                }
                else {
                    ++integer5;
                }
                if (integer5 > 3) {
                    this.stop();
                    break;
                }
            }
            if (this.isStarted() && !this.hasMoreWaves() && integer3 == 0) {
                if (this.postRaidTicks < 40) {
                    ++this.postRaidTicks;
                }
                else {
                    this.status = RaidStatus.VICTORY;
                    for (final UUID uUID7 : this.heroesOfTheVillage) {
                        final Entity aio8 = this.level.getEntity(uUID7);
                        if (aio8 instanceof LivingEntity && !aio8.isSpectator()) {
                            final LivingEntity aix9 = (LivingEntity)aio8;
                            aix9.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
                            if (!(aix9 instanceof ServerPlayer)) {
                                continue;
                            }
                            final ServerPlayer vl10 = (ServerPlayer)aix9;
                            vl10.awardStat(Stats.RAID_WIN);
                            CriteriaTriggers.RAID_WIN.trigger(vl10);
                        }
                    }
                }
            }
            this.setDirty();
        }
        else if (this.isOver()) {
            ++this.celebrationTicks;
            if (this.celebrationTicks >= 600) {
                this.stop();
                return;
            }
            if (this.celebrationTicks % 20 == 0) {
                this.updatePlayers();
                this.raidEvent.setVisible(true);
                if (this.isVictory()) {
                    this.raidEvent.setPercent(0.0f);
                    this.raidEvent.setName(Raid.RAID_BAR_VICTORY_COMPONENT);
                }
                else {
                    this.raidEvent.setName(Raid.RAID_BAR_DEFEAT_COMPONENT);
                }
            }
        }
    }
    
    private void moveRaidCenterToNearbyVillageSection() {
        final Stream<SectionPos> stream2 = SectionPos.cube(SectionPos.of(this.center), 2);
        stream2.filter(this.level::isVillage).map(SectionPos::center).min(Comparator.comparingDouble(ew -> ew.distSqr(this.center))).ifPresent(this::setCenter);
    }
    
    private Optional<BlockPos> getValidSpawnPos(final int integer) {
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            final BlockPos ew4 = this.findRandomSpawnPos(integer, 1);
            if (ew4 != null) {
                return (Optional<BlockPos>)Optional.of(ew4);
            }
        }
        return (Optional<BlockPos>)Optional.empty();
    }
    
    private boolean hasMoreWaves() {
        if (this.hasBonusWave()) {
            return !this.hasSpawnedBonusWave();
        }
        return !this.isFinalWave();
    }
    
    private boolean isFinalWave() {
        return this.getGroupsSpawned() == this.numGroups;
    }
    
    private boolean hasBonusWave() {
        return this.badOmenLevel > 1;
    }
    
    private boolean hasSpawnedBonusWave() {
        return this.getGroupsSpawned() > this.numGroups;
    }
    
    private boolean shouldSpawnBonusGroup() {
        return this.isFinalWave() && this.getTotalRaidersAlive() == 0 && this.hasBonusWave();
    }
    
    private void updateRaiders() {
        final Iterator<Set<Raider>> iterator2 = (Iterator<Set<Raider>>)this.groupRaiderMap.values().iterator();
        final Set<Raider> set3 = (Set<Raider>)Sets.newHashSet();
        while (iterator2.hasNext()) {
            final Set<Raider> set4 = (Set<Raider>)iterator2.next();
            for (final Raider axl6 : set4) {
                final BlockPos ew7 = new BlockPos(axl6);
                if (axl6.removed || axl6.dimension != this.level.getDimension().getType() || this.center.distSqr(ew7) >= 12544.0) {
                    set3.add(axl6);
                }
                else {
                    if (axl6.tickCount <= 600) {
                        continue;
                    }
                    if (this.level.getEntity(axl6.getUUID()) == null) {
                        set3.add(axl6);
                    }
                    if (!this.level.isVillage(ew7) && axl6.getNoActionTime() > 2400) {
                        axl6.setTicksOutsideRaid(axl6.getTicksOutsideRaid() + 1);
                    }
                    if (axl6.getTicksOutsideRaid() < 30) {
                        continue;
                    }
                    set3.add(axl6);
                }
            }
        }
        for (final Raider axl7 : set3) {
            this.removeFromRaid(axl7, true);
        }
    }
    
    private void playSound(final BlockPos ew) {
        final float float3 = 13.0f;
        final int integer4 = 64;
        for (final Player awg6 : this.level.players()) {
            final Vec3 csi7 = new Vec3(awg6.x, awg6.y, awg6.z);
            final Vec3 csi8 = new Vec3(ew.getX(), ew.getY(), ew.getZ());
            final float float4 = Mth.sqrt((csi8.x - csi7.x) * (csi8.x - csi7.x) + (csi8.z - csi7.z) * (csi8.z - csi7.z));
            final double double10 = csi7.x + 13.0f / float4 * (csi8.x - csi7.x);
            final double double11 = csi7.z + 13.0f / float4 * (csi8.z - csi7.z);
            if (float4 <= 64.0f || this.level.isVillage(new BlockPos(awg6))) {
                ((ServerPlayer)awg6).connection.send(new ClientboundSoundPacket(SoundEvents.RAID_HORN, SoundSource.NEUTRAL, double10, awg6.y, double11, 64.0f, 1.0f));
            }
        }
    }
    
    private void spawnGroup(final BlockPos ew) {
        boolean boolean3 = false;
        final int integer4 = this.groupsSpawned + 1;
        this.totalHealth = 0.0f;
        final DifficultyInstance ahh5 = this.level.getCurrentDifficultyAt(ew);
        final boolean boolean4 = this.shouldSpawnBonusGroup();
        for (final RaiderType b10 : RaiderType.VALUES) {
            final int integer5 = this.getDefaultNumSpawns(b10, integer4, boolean4) + this.getPotentialBonusSpawns(b10, this.random, integer4, ahh5, boolean4);
            int integer6 = 0;
            for (int integer7 = 0; integer7 < integer5; ++integer7) {
                final Raider axl14 = b10.entityType.create(this.level);
                if (!boolean3 && axl14.canBeLeader()) {
                    axl14.setPatrolLeader(true);
                    this.setLeader(integer4, axl14);
                    boolean3 = true;
                }
                this.joinRaid(integer4, axl14, ew, false);
                if (b10.entityType == EntityType.RAVAGER) {
                    Raider axl15 = null;
                    if (integer4 == this.getNumGroups(Difficulty.NORMAL)) {
                        axl15 = EntityType.PILLAGER.create(this.level);
                    }
                    else if (integer4 >= this.getNumGroups(Difficulty.HARD)) {
                        if (integer6 == 0) {
                            axl15 = EntityType.EVOKER.create(this.level);
                        }
                        else {
                            axl15 = EntityType.VINDICATOR.create(this.level);
                        }
                    }
                    ++integer6;
                    if (axl15 != null) {
                        this.joinRaid(integer4, axl15, ew, false);
                        axl15.moveTo(ew, 0.0f, 0.0f);
                        axl15.startRiding(axl14);
                    }
                }
            }
        }
        this.waveSpawnPos = (Optional<BlockPos>)Optional.empty();
        ++this.groupsSpawned;
        this.updateBossbar();
        this.setDirty();
    }
    
    public void joinRaid(final int integer, final Raider axl, @Nullable final BlockPos ew, final boolean boolean4) {
        final boolean boolean5 = this.addWaveMob(integer, axl);
        if (boolean5) {
            axl.setCurrentRaid(this);
            axl.setWave(integer);
            axl.setCanJoinRaid(true);
            axl.setTicksOutsideRaid(0);
            if (!boolean4 && ew != null) {
                axl.setPos(ew.getX() + 0.5, ew.getY() + 1.0, ew.getZ() + 0.5);
                axl.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(ew), MobSpawnType.EVENT, null, null);
                axl.applyRaidBuffs(integer, false);
                axl.onGround = true;
                this.level.addFreshEntity(axl);
            }
        }
    }
    
    public void updateBossbar() {
        this.raidEvent.setPercent(Mth.clamp(this.getHealthOfLivingRaiders() / this.totalHealth, 0.0f, 1.0f));
    }
    
    public float getHealthOfLivingRaiders() {
        float float2 = 0.0f;
        for (final Set<Raider> set4 : this.groupRaiderMap.values()) {
            for (final Raider axl6 : set4) {
                float2 += axl6.getHealth();
            }
        }
        return float2;
    }
    
    private boolean shouldSpawnGroup() {
        return this.raidCooldownTicks == 0 && (this.groupsSpawned < this.numGroups || this.shouldSpawnBonusGroup()) && this.getTotalRaidersAlive() == 0;
    }
    
    public int getTotalRaidersAlive() {
        return this.groupRaiderMap.values().stream().mapToInt(Set::size).sum();
    }
    
    public void removeFromRaid(@Nonnull final Raider axl, final boolean boolean2) {
        final Set<Raider> set4 = (Set<Raider>)this.groupRaiderMap.get(axl.getWave());
        if (set4 != null) {
            final boolean boolean3 = set4.remove(axl);
            if (boolean3) {
                if (boolean2) {
                    this.totalHealth -= axl.getHealth();
                }
                axl.setCurrentRaid(null);
                this.updateBossbar();
                this.setDirty();
            }
        }
    }
    
    private void setDirty() {
        this.level.getRaids().setDirty();
    }
    
    public static ItemStack getLeaderBannerInstance() {
        final ItemStack bcj1 = new ItemStack(Items.WHITE_BANNER);
        final CompoundTag id2 = bcj1.getOrCreateTagElement("BlockEntityTag");
        final ListTag ik3 = new BannerPattern.Builder().addPattern(BannerPattern.RHOMBUS_MIDDLE, DyeColor.CYAN).addPattern(BannerPattern.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.STRIPE_CENTER, DyeColor.GRAY).addPattern(BannerPattern.BORDER, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK).addPattern(BannerPattern.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.BORDER, DyeColor.BLACK).toListTag();
        id2.put("Patterns", (Tag)ik3);
        bcj1.setHoverName(new TranslatableComponent("block.minecraft.ominous_banner", new Object[0]).withStyle(ChatFormatting.GOLD));
        return bcj1;
    }
    
    @Nullable
    public Raider getLeader(final int integer) {
        return (Raider)this.groupToLeaderMap.get(integer);
    }
    
    @Nullable
    private BlockPos findRandomSpawnPos(final int integer1, final int integer2) {
        final int integer3 = (integer1 == 0) ? 2 : (2 - integer1);
        final BlockPos.MutableBlockPos a8 = new BlockPos.MutableBlockPos();
        for (int integer4 = 0; integer4 < integer2; ++integer4) {
            final float float10 = this.level.random.nextFloat() * 6.2831855f;
            final int integer5 = this.center.getX() + Mth.floor(Mth.cos(float10) * 32.0f * integer3) + this.level.random.nextInt(5);
            final int integer6 = this.center.getZ() + Mth.floor(Mth.sin(float10) * 32.0f * integer3) + this.level.random.nextInt(5);
            final int integer7 = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, integer5, integer6);
            a8.set(integer5, integer7, integer6);
            if (!this.level.isVillage(a8) || integer1 >= 2) {
                if (this.level.hasChunksAt(a8.getX() - 10, a8.getY() - 10, a8.getZ() - 10, a8.getX() + 10, a8.getY() + 10, a8.getZ() + 10)) {
                    if (this.level.getChunkSource().isEntityTickingChunk(new ChunkPos(a8))) {
                        if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, this.level, a8, EntityType.RAVAGER) || (this.level.getBlockState(a8.below()).getBlock() == Blocks.SNOW && this.level.getBlockState(a8).isAir())) {
                            return a8;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private boolean addWaveMob(final int integer, final Raider axl) {
        return this.addWaveMob(integer, axl, true);
    }
    
    public boolean addWaveMob(final int integer, final Raider axl, final boolean boolean3) {
        this.groupRaiderMap.computeIfAbsent(integer, integer -> Sets.newHashSet());
        final Set<Raider> set5 = (Set<Raider>)this.groupRaiderMap.get(integer);
        Raider axl2 = null;
        for (final Raider axl3 : set5) {
            if (axl3.getUUID().equals(axl.getUUID())) {
                axl2 = axl3;
                break;
            }
        }
        if (axl2 != null) {
            set5.remove(axl2);
            set5.add(axl);
        }
        set5.add(axl);
        if (boolean3) {
            this.totalHealth += axl.getHealth();
        }
        this.updateBossbar();
        this.setDirty();
        return true;
    }
    
    public void setLeader(final int integer, final Raider axl) {
        this.groupToLeaderMap.put(integer, axl);
        axl.setItemSlot(EquipmentSlot.HEAD, getLeaderBannerInstance());
        axl.setDropChance(EquipmentSlot.HEAD, 2.0f);
    }
    
    public void removeLeader(final int integer) {
        this.groupToLeaderMap.remove(integer);
    }
    
    public BlockPos getCenter() {
        return this.center;
    }
    
    private void setCenter(final BlockPos ew) {
        this.center = ew;
    }
    
    public int getId() {
        return this.id;
    }
    
    private int getDefaultNumSpawns(final RaiderType b, final int integer, final boolean boolean3) {
        return boolean3 ? b.spawnsPerWaveBeforeBonus[this.numGroups] : b.spawnsPerWaveBeforeBonus[integer];
    }
    
    private int getPotentialBonusSpawns(final RaiderType b, final Random random, final int integer, final DifficultyInstance ahh, final boolean boolean5) {
        final Difficulty ahg7 = ahh.getDifficulty();
        final boolean boolean6 = ahg7 == Difficulty.EASY;
        final boolean boolean7 = ahg7 == Difficulty.NORMAL;
        int integer2 = 0;
        switch (b) {
            case WITCH: {
                if (!boolean6 && integer > 2 && integer != 4) {
                    integer2 = 1;
                    break;
                }
                return 0;
            }
            case PILLAGER:
            case VINDICATOR: {
                if (boolean6) {
                    integer2 = random.nextInt(2);
                    break;
                }
                if (boolean7) {
                    integer2 = 1;
                    break;
                }
                integer2 = 2;
                break;
            }
            case RAVAGER: {
                integer2 = ((!boolean6 && boolean5) ? 1 : 0);
                break;
            }
            default: {
                return 0;
            }
        }
        return (integer2 > 0) ? random.nextInt(integer2 + 1) : 0;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public CompoundTag save(final CompoundTag id) {
        id.putInt("Id", this.id);
        id.putBoolean("Started", this.started);
        id.putBoolean("Active", this.active);
        id.putLong("TicksActive", this.ticksActive);
        id.putInt("BadOmenLevel", this.badOmenLevel);
        id.putInt("GroupsSpawned", this.groupsSpawned);
        id.putInt("PreRaidTicks", this.raidCooldownTicks);
        id.putInt("PostRaidTicks", this.postRaidTicks);
        id.putFloat("TotalHealth", this.totalHealth);
        id.putInt("NumGroups", this.numGroups);
        id.putString("Status", this.status.getName());
        id.putInt("CX", this.center.getX());
        id.putInt("CY", this.center.getY());
        id.putInt("CZ", this.center.getZ());
        final ListTag ik3 = new ListTag();
        for (final UUID uUID5 : this.heroesOfTheVillage) {
            final CompoundTag id2 = new CompoundTag();
            id2.putUUID("UUID", uUID5);
            ik3.add(id2);
        }
        id.put("HeroesOfTheVillage", (Tag)ik3);
        return id;
    }
    
    public int getNumGroups(final Difficulty ahg) {
        switch (ahg) {
            case EASY: {
                return 3;
            }
            case NORMAL: {
                return 5;
            }
            case HARD: {
                return 7;
            }
            default: {
                return 0;
            }
        }
    }
    
    public float getEnchantOdds() {
        final int integer2 = this.getBadOmenLevel();
        if (integer2 == 2) {
            return 0.1f;
        }
        if (integer2 == 3) {
            return 0.25f;
        }
        if (integer2 == 4) {
            return 0.5f;
        }
        if (integer2 == 5) {
            return 0.75f;
        }
        return 0.0f;
    }
    
    public void addHeroOfTheVillage(final Entity aio) {
        this.heroesOfTheVillage.add(aio.getUUID());
    }
    
    static {
        RAID_NAME_COMPONENT = new TranslatableComponent("event.minecraft.raid", new Object[0]);
        VICTORY = new TranslatableComponent("event.minecraft.raid.victory", new Object[0]);
        DEFEAT = new TranslatableComponent("event.minecraft.raid.defeat", new Object[0]);
        RAID_BAR_VICTORY_COMPONENT = Raid.RAID_NAME_COMPONENT.copy().append(" - ").append(Raid.VICTORY);
        RAID_BAR_DEFEAT_COMPONENT = Raid.RAID_NAME_COMPONENT.copy().append(" - ").append(Raid.DEFEAT);
    }
    
    enum RaidStatus {
        ONGOING, 
        VICTORY, 
        LOSS, 
        STOPPED;
        
        private static final RaidStatus[] VALUES;
        
        private static RaidStatus getByName(final String string) {
            for (final RaidStatus a5 : RaidStatus.VALUES) {
                if (string.equalsIgnoreCase(a5.name())) {
                    return a5;
                }
            }
            return RaidStatus.ONGOING;
        }
        
        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
        
        static {
            VALUES = values();
        }
    }
    
    enum RaiderType {
        VINDICATOR(EntityType.VINDICATOR, new int[] { 0, 0, 2, 0, 1, 4, 2, 5 }), 
        EVOKER(EntityType.EVOKER, new int[] { 0, 0, 0, 0, 0, 1, 1, 2 }), 
        PILLAGER(EntityType.PILLAGER, new int[] { 0, 4, 3, 3, 4, 4, 4, 2 }), 
        WITCH(EntityType.WITCH, new int[] { 0, 0, 0, 0, 3, 0, 0, 1 }), 
        RAVAGER(EntityType.RAVAGER, new int[] { 0, 0, 0, 1, 0, 1, 0, 2 });
        
        private static final RaiderType[] VALUES;
        private final EntityType<? extends Raider> entityType;
        private final int[] spawnsPerWaveBeforeBonus;
        
        private RaiderType(final EntityType<? extends Raider> ais, final int[] arr) {
            this.entityType = ais;
            this.spawnsPerWaveBeforeBonus = arr;
        }
        
        static {
            VALUES = values();
        }
    }
}
