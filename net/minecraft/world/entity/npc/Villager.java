package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.core.SerializableLong;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.phys.AABB;
import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.scores.Team;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Style;
import java.util.function.Consumer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.gossip.GossipType;
import java.util.function.Predicate;
import net.minecraft.server.MinecraftServer;
import java.util.Optional;
import java.util.List;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import java.util.Iterator;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.server.level.ServerLevel;
import java.util.Collection;
import net.minecraft.world.entity.ai.Brain;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import java.util.function.BiPredicate;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import java.util.Set;
import net.minecraft.world.item.Item;
import java.util.Map;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.ReputationEventHandler;

public class Villager extends AbstractVillager implements ReputationEventHandler, VillagerDataHolder {
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA;
    public static final Map<Item, Integer> FOOD_POINTS;
    private static final Set<Item> WANTED_ITEMS;
    private int updateMerchantTimer;
    private boolean increaseProfessionLevelOnUpdate;
    @Nullable
    private Player lastTradedPlayer;
    private byte foodLevel;
    private final GossipContainer gossips;
    private long lastGossipTime;
    private long lastGossipDecayTime;
    private int villagerXp;
    private long lastRestockGameTime;
    private int numberOfRestocksToday;
    private long lastRestockCheckDayTime;
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;
    private static final ImmutableList<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES;
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<Villager, PoiType>> POI_MEMORIES;
    
    public Villager(final EntityType<? extends Villager> ais, final Level bhr) {
        this(ais, bhr, VillagerType.PLAINS);
    }
    
    public Villager(final EntityType<? extends Villager> ais, final Level bhr, final VillagerType avy) {
        super(ais, bhr);
        this.gossips = new GossipContainer();
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);
        this.setCanPickUpLoot(true);
        this.setVillagerData(this.getVillagerData().setType(avy).setProfession(VillagerProfession.NONE));
        this.brain = this.makeBrain(new Dynamic((DynamicOps)NbtOps.INSTANCE, new CompoundTag()));
    }
    
    public Brain<Villager> getBrain() {
        return (Brain<Villager>)super.getBrain();
    }
    
    protected Brain<?> makeBrain(final Dynamic<?> dynamic) {
        final Brain<Villager> ajm3 = new Brain<Villager>((Collection<MemoryModuleType<?>>)Villager.MEMORY_TYPES, (java.util.Collection<SensorType<? extends Sensor<? super Villager>>>)Villager.SENSOR_TYPES, dynamic);
        this.registerBrainGoals(ajm3);
        return ajm3;
    }
    
    public void refreshBrain(final ServerLevel vk) {
        final Brain<Villager> ajm3 = this.getBrain();
        ajm3.stopAll(vk, this);
        this.brain = ajm3.copyWithoutGoals();
        this.registerBrainGoals(this.getBrain());
    }
    
    private void registerBrainGoals(final Brain<Villager> ajm) {
        final VillagerProfession avw3 = this.getVillagerData().getProfession();
        final float float4 = (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
        if (this.isBaby()) {
            ajm.setSchedule(Schedule.VILLAGER_BABY);
            ajm.addActivity(Activity.PLAY, VillagerGoalPackages.getPlayPackage(float4));
        }
        else {
            ajm.setSchedule(Schedule.VILLAGER_DEFAULT);
            ajm.addActivity(Activity.WORK, VillagerGoalPackages.getWorkPackage(avw3, float4), (Set<Pair<MemoryModuleType<?>, MemoryStatus>>)ImmutableSet.of(Pair.of((Object)MemoryModuleType.JOB_SITE, (Object)MemoryStatus.VALUE_PRESENT)));
        }
        ajm.addActivity(Activity.CORE, VillagerGoalPackages.getCorePackage(avw3, float4));
        ajm.addActivity(Activity.MEET, VillagerGoalPackages.getMeetPackage(avw3, float4), (Set<Pair<MemoryModuleType<?>, MemoryStatus>>)ImmutableSet.of(Pair.of((Object)MemoryModuleType.MEETING_POINT, (Object)MemoryStatus.VALUE_PRESENT)));
        ajm.addActivity(Activity.REST, VillagerGoalPackages.getRestPackage(avw3, float4));
        ajm.addActivity(Activity.IDLE, VillagerGoalPackages.getIdlePackage(avw3, float4));
        ajm.addActivity(Activity.PANIC, VillagerGoalPackages.getPanicPackage(avw3, float4));
        ajm.addActivity(Activity.PRE_RAID, VillagerGoalPackages.getPreRaidPackage(avw3, float4));
        ajm.addActivity(Activity.RAID, VillagerGoalPackages.getRaidPackage(avw3, float4));
        ajm.addActivity(Activity.HIDE, VillagerGoalPackages.getHidePackage(avw3, float4));
        ajm.setCoreActivities((Set<Activity>)ImmutableSet.of(Activity.CORE));
        ajm.setDefaultActivity(Activity.IDLE);
        ajm.setActivity(Activity.IDLE);
        ajm.updateActivity(this.level.getDayTime(), this.level.getGameTime());
    }
    
    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (this.level instanceof ServerLevel) {
            this.refreshBrain((ServerLevel)this.level);
        }
    }
    
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0);
    }
    
    protected void customServerAiStep() {
        this.level.getProfiler().push("brain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        if (!this.isTrading() && this.updateMerchantTimer > 0) {
            --this.updateMerchantTimer;
            if (this.updateMerchantTimer <= 0) {
                if (this.increaseProfessionLevelOnUpdate) {
                    this.increaseMerchantCareer();
                    this.increaseProfessionLevelOnUpdate = false;
                }
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
            }
        }
        if (this.lastTradedPlayer != null && this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).onReputationEvent(ReputationEventType.TRADE, this.lastTradedPlayer, this);
            this.level.broadcastEntityEvent(this, (byte)14);
            this.lastTradedPlayer = null;
        }
        if (!this.isNoAi() && this.random.nextInt(100) == 0) {
            final Raid axk2 = ((ServerLevel)this.level).getRaidAt(new BlockPos(this));
            if (axk2 != null && axk2.isActive() && !axk2.isOver()) {
                this.level.broadcastEntityEvent(this, (byte)42);
            }
        }
        if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.isTrading()) {
            this.stopTrading();
        }
        super.customServerAiStep();
    }
    
    public void tick() {
        super.tick();
        if (this.getUnhappyCounter() > 0) {
            this.setUnhappyCounter(this.getUnhappyCounter() - 1);
        }
        this.maybeDecayGossip();
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        final boolean boolean5 = bcj4.getItem() == Items.NAME_TAG;
        if (boolean5) {
            bcj4.interactEnemy(awg, this, ahi);
            return true;
        }
        if (bcj4.getItem() == Items.VILLAGER_SPAWN_EGG || !this.isAlive() || this.isTrading() || this.isSleeping()) {
            return super.mobInteract(awg, ahi);
        }
        if (this.isBaby()) {
            this.setUnhappy();
            return super.mobInteract(awg, ahi);
        }
        final boolean boolean6 = this.getOffers().isEmpty();
        if (ahi == InteractionHand.MAIN_HAND) {
            if (boolean6 && !this.level.isClientSide) {
                this.setUnhappy();
            }
            awg.awardStat(Stats.TALKED_TO_VILLAGER);
        }
        if (boolean6) {
            return super.mobInteract(awg, ahi);
        }
        if (!this.level.isClientSide && !this.offers.isEmpty()) {
            this.startTrading(awg);
        }
        return true;
    }
    
    private void setUnhappy() {
        this.setUnhappyCounter(40);
        if (!this.level.isClientSide()) {
            this.playSound(SoundEvents.VILLAGER_NO, this.getSoundVolume(), this.getVoicePitch());
        }
    }
    
    private void startTrading(final Player awg) {
        this.updateSpecialPrices(awg);
        this.setTradingPlayer(awg);
        this.openTradingScreen(awg, this.getDisplayName(), this.getVillagerData().getLevel());
    }
    
    @Override
    public void setTradingPlayer(@Nullable final Player awg) {
        final boolean boolean3 = this.getTradingPlayer() != null && awg == null;
        super.setTradingPlayer(awg);
        if (boolean3) {
            this.stopTrading();
        }
    }
    
    @Override
    protected void stopTrading() {
        super.stopTrading();
        this.resetSpecialPrices();
    }
    
    private void resetSpecialPrices() {
        for (final MerchantOffer bgu3 : this.getOffers()) {
            bgu3.resetSpecialPriceDiff();
        }
    }
    
    @Override
    public boolean canRestock() {
        return true;
    }
    
    public void restock() {
        this.updateDemand();
        for (final MerchantOffer bgu3 : this.getOffers()) {
            bgu3.resetUses();
        }
        if (this.getVillagerData().getProfession() == VillagerProfession.FARMER) {
            this.makeBread();
        }
        this.lastRestockGameTime = this.level.getGameTime();
        ++this.numberOfRestocksToday;
    }
    
    private boolean needsToRestock() {
        for (final MerchantOffer bgu3 : this.getOffers()) {
            if (bgu3.isOutOfStock()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean allowedToRestock() {
        return this.numberOfRestocksToday < 2 && this.level.getGameTime() > this.lastRestockGameTime + 2400L;
    }
    
    public boolean shouldRestock() {
        final long long2 = this.lastRestockGameTime + 12000L;
        boolean boolean4 = this.level.getGameTime() > long2;
        final long long3 = this.level.getDayTime();
        if (this.lastRestockCheckDayTime > 0L) {
            final long long4 = this.lastRestockCheckDayTime / 24000L;
            final long long5 = long3 / 24000L;
            boolean4 |= (long5 > long4);
        }
        this.lastRestockCheckDayTime = long3;
        if (boolean4) {
            this.resetNumberOfRestocks();
        }
        return this.allowedToRestock() && this.needsToRestock();
    }
    
    private void catchUpDemand() {
        final int integer2 = 2 - this.numberOfRestocksToday;
        if (integer2 > 0) {
            for (final MerchantOffer bgu4 : this.getOffers()) {
                bgu4.resetUses();
            }
        }
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            this.updateDemand();
        }
    }
    
    private void updateDemand() {
        for (final MerchantOffer bgu3 : this.getOffers()) {
            bgu3.updateDemand();
        }
    }
    
    private void updateSpecialPrices(final Player awg) {
        final int integer3 = this.getPlayerReputation(awg);
        if (integer3 != 0) {
            for (final MerchantOffer bgu5 : this.getOffers()) {
                bgu5.addToSpecialPriceDiff(-Mth.floor(integer3 * bgu5.getPriceMultiplier()));
            }
        }
        if (awg.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            final MobEffectInstance aii4 = awg.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
            final int integer4 = aii4.getAmplifier();
            for (final MerchantOffer bgu6 : this.getOffers()) {
                final double double8 = 0.3 + 0.0625 * integer4;
                final int integer5 = (int)Math.floor(double8 * bgu6.getBaseCostA().getCount());
                bgu6.addToSpecialPriceDiff(-Math.max(integer5, 1));
            }
        }
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<VillagerData>define(Villager.DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.put("VillagerData", (Tag)this.getVillagerData().<Tag>serialize((com.mojang.datafixers.types.DynamicOps<Tag>)NbtOps.INSTANCE));
        id.putByte("FoodLevel", this.foodLevel);
        id.put("Gossips", (Tag)this.gossips.store((com.mojang.datafixers.types.DynamicOps<Object>)NbtOps.INSTANCE).getValue());
        id.putInt("Xp", this.villagerXp);
        id.putLong("LastRestock", this.lastRestockGameTime);
        id.putLong("LastGossipDecay", this.lastGossipDecayTime);
        id.putInt("RestocksToday", this.numberOfRestocksToday);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("VillagerData", 10)) {
            this.setVillagerData(new VillagerData(new Dynamic((DynamicOps)NbtOps.INSTANCE, id.get("VillagerData"))));
        }
        if (id.contains("Offers", 10)) {
            this.offers = new MerchantOffers(id.getCompound("Offers"));
        }
        if (id.contains("FoodLevel", 1)) {
            this.foodLevel = id.getByte("FoodLevel");
        }
        final ListTag ik3 = id.getList("Gossips", 10);
        this.gossips.update(new Dynamic((DynamicOps)NbtOps.INSTANCE, ik3));
        if (id.contains("Xp", 3)) {
            this.villagerXp = id.getInt("Xp");
        }
        this.lastRestockGameTime = id.getLong("LastRestock");
        this.lastGossipDecayTime = id.getLong("LastGossipDecay");
        this.setCanPickUpLoot(true);
        this.refreshBrain((ServerLevel)this.level);
        this.numberOfRestocksToday = id.getInt("RestocksToday");
    }
    
    public boolean removeWhenFarAway(final double double1) {
        return false;
    }
    
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        if (this.isTrading()) {
            return SoundEvents.VILLAGER_TRADE;
        }
        return SoundEvents.VILLAGER_AMBIENT;
    }
    
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.VILLAGER_HURT;
    }
    
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }
    
    public void playWorkSound() {
        final SoundEvent yo2 = this.getVillagerData().getProfession().getJobPoiType().getUseSound();
        if (yo2 != null) {
            this.playSound(yo2, this.getSoundVolume(), this.getVoicePitch());
        }
    }
    
    public void setVillagerData(final VillagerData avu) {
        final VillagerData avu2 = this.getVillagerData();
        if (avu2.getProfession() != avu.getProfession()) {
            this.offers = null;
        }
        this.entityData.<VillagerData>set(Villager.DATA_VILLAGER_DATA, avu);
    }
    
    @Override
    public VillagerData getVillagerData() {
        return this.entityData.<VillagerData>get(Villager.DATA_VILLAGER_DATA);
    }
    
    @Override
    protected void rewardTradeXp(final MerchantOffer bgu) {
        int integer3 = 3 + this.random.nextInt(4);
        this.villagerXp += bgu.getXp();
        this.lastTradedPlayer = this.getTradingPlayer();
        if (this.shouldIncreaseLevel()) {
            this.updateMerchantTimer = 40;
            this.increaseProfessionLevelOnUpdate = true;
            integer3 += 5;
        }
        if (bgu.shouldRewardExp()) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.x, this.y + 0.5, this.z, integer3));
        }
    }
    
    public void setLastHurtByMob(@Nullable final LivingEntity aix) {
        if (aix != null && this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).onReputationEvent(ReputationEventType.VILLAGER_HURT, aix, this);
            if (this.isAlive() && aix instanceof Player) {
                this.level.broadcastEntityEvent(this, (byte)13);
            }
        }
        super.setLastHurtByMob(aix);
    }
    
    @Override
    public void die(final DamageSource ahx) {
        final Entity aio3 = ahx.getEntity();
        if (aio3 != null) {
            this.tellWitnessesThatIWasMurdered(aio3);
        }
        this.releasePoi(MemoryModuleType.HOME);
        this.releasePoi(MemoryModuleType.JOB_SITE);
        this.releasePoi(MemoryModuleType.MEETING_POINT);
        super.die(ahx);
    }
    
    private void tellWitnessesThatIWasMurdered(final Entity aio) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        final Optional<List<LivingEntity>> optional3 = this.brain.<List<LivingEntity>>getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
        if (!optional3.isPresent()) {
            return;
        }
        final ServerLevel vk4 = (ServerLevel)this.level;
        ((List)optional3.get()).stream().filter(aix -> aix instanceof ReputationEventHandler).forEach(aix -> vk4.onReputationEvent(ReputationEventType.VILLAGER_KILLED, aio, (ReputationEventHandler)aix));
    }
    
    public void releasePoi(final MemoryModuleType<GlobalPos> apj) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        final MinecraftServer minecraftServer3 = ((ServerLevel)this.level).getServer();
        this.brain.<GlobalPos>getMemory(apj).ifPresent(fd -> {
            final ServerLevel vk5 = minecraftServer3.getLevel(fd.dimension());
            final PoiManager aqp6 = vk5.getPoiManager();
            final Optional<PoiType> optional7 = aqp6.getType(fd.pos());
            final BiPredicate<Villager, PoiType> biPredicate8 = (BiPredicate<Villager, PoiType>)Villager.POI_MEMORIES.get(apj);
            if (optional7.isPresent() && biPredicate8.test(this, optional7.get())) {
                aqp6.release(fd.pos());
                DebugPackets.sendPoiTicketCountPacket(vk5, fd.pos());
            }
        });
    }
    
    public boolean canBreed() {
        return this.foodLevel + this.countFoodPointsInInventory() >= 12 && this.getAge() == 0;
    }
    
    private boolean hungry() {
        return this.foodLevel < 12;
    }
    
    private void eatUntilFull() {
        if (!this.hungry() || this.countFoodPointsInInventory() == 0) {
            return;
        }
        for (int integer2 = 0; integer2 < this.getInventory().getContainerSize(); ++integer2) {
            final ItemStack bcj3 = this.getInventory().getItem(integer2);
            if (!bcj3.isEmpty()) {
                final Integer integer3 = (Integer)Villager.FOOD_POINTS.get(bcj3.getItem());
                if (integer3 != null) {
                    int integer5;
                    for (int integer4 = integer5 = bcj3.getCount(); integer5 > 0; --integer5) {
                        this.foodLevel += (byte)integer3;
                        this.getInventory().removeItem(integer2, 1);
                        if (!this.hungry()) {
                            return;
                        }
                    }
                }
            }
        }
    }
    
    public int getPlayerReputation(final Player awg) {
        return this.gossips.getReputation(awg.getUUID(), (Predicate<GossipType>)(aph -> true));
    }
    
    private void digestFood(final int integer) {
        this.foodLevel -= (byte)integer;
    }
    
    public void eatAndDigestFood() {
        this.eatUntilFull();
        this.digestFood(12);
    }
    
    public void setOffers(final MerchantOffers bgv) {
        this.offers = bgv;
    }
    
    private boolean shouldIncreaseLevel() {
        final int integer2 = this.getVillagerData().getLevel();
        return VillagerData.canLevelUp(integer2) && this.villagerXp >= VillagerData.getMaxXpPerLevel(integer2);
    }
    
    private void increaseMerchantCareer() {
        this.setVillagerData(this.getVillagerData().setLevel(this.getVillagerData().getLevel() + 1));
        this.updateTrades();
    }
    
    public Component getDisplayName() {
        final Team ctk2 = this.getTeam();
        final Component jo3 = this.getCustomName();
        if (jo3 != null) {
            return PlayerTeam.formatNameForTeam(ctk2, jo3).withStyle((Consumer<Style>)(jw -> jw.setHoverEvent(this.createHoverEvent()).setInsertion(this.getStringUUID())));
        }
        final VillagerProfession avw4 = this.getVillagerData().getProfession();
        final Component jo4 = new TranslatableComponent(this.getType().getDescriptionId() + '.' + Registry.VILLAGER_PROFESSION.getKey(avw4).getPath(), new Object[0]).withStyle((Consumer<Style>)(jw -> jw.setHoverEvent(this.createHoverEvent()).setInsertion(this.getStringUUID())));
        if (ctk2 != null) {
            jo4.withStyle(ctk2.getColor());
        }
        return jo4;
    }
    
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 12) {
            this.addParticlesAroundSelf(ParticleTypes.HEART);
        }
        else if (byte1 == 13) {
            this.addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
        }
        else if (byte1 == 14) {
            this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
        }
        else if (byte1 == 42) {
            this.addParticlesAroundSelf(ParticleTypes.SPLASH);
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    @Nullable
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        if (aja == MobSpawnType.BREEDING) {
            this.setVillagerData(this.getVillagerData().setProfession(VillagerProfession.NONE));
        }
        if (aja == MobSpawnType.COMMAND || aja == MobSpawnType.SPAWN_EGG || aja == MobSpawnType.SPAWNER) {
            this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(bhs.getBiome(new BlockPos(this)))));
        }
        return super.finalizeSpawn(bhs, ahh, aja, ajj, id);
    }
    
    @Override
    public Villager getBreedOffspring(final AgableMob aim) {
        final double double4 = this.random.nextDouble();
        VillagerType avy3;
        if (double4 < 0.5) {
            avy3 = VillagerType.byBiome(this.level.getBiome(new BlockPos(this)));
        }
        else if (double4 < 0.75) {
            avy3 = this.getVillagerData().getType();
        }
        else {
            avy3 = ((Villager)aim).getVillagerData().getType();
        }
        final Villager avt6 = new Villager(EntityType.VILLAGER, this.level, avy3);
        avt6.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(new BlockPos(avt6)), MobSpawnType.BREEDING, null, null);
        return avt6;
    }
    
    public void thunderHit(final LightningBolt atu) {
        final Witch avk3 = EntityType.WITCH.create(this.level);
        avk3.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
        avk3.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(new BlockPos(avk3)), MobSpawnType.CONVERSION, null, null);
        avk3.setNoAi(this.isNoAi());
        if (this.hasCustomName()) {
            avk3.setCustomName(this.getCustomName());
            avk3.setCustomNameVisible(this.isCustomNameVisible());
        }
        this.level.addFreshEntity(avk3);
        this.remove();
    }
    
    protected void pickUpItem(final ItemEntity atx) {
        final ItemStack bcj3 = atx.getItem();
        final Item bce4 = bcj3.getItem();
        if (this.wantToPickUp(bce4)) {
            final SimpleContainer aho5 = this.getInventory();
            boolean boolean6 = false;
            for (int integer7 = 0; integer7 < aho5.getContainerSize(); ++integer7) {
                final ItemStack bcj4 = aho5.getItem(integer7);
                if (bcj4.isEmpty() || (bcj4.getItem() == bce4 && bcj4.getCount() < bcj4.getMaxStackSize())) {
                    boolean6 = true;
                    break;
                }
            }
            if (!boolean6) {
                return;
            }
            int integer7 = aho5.countItem(bce4);
            if (integer7 == 256) {
                return;
            }
            if (integer7 > 256) {
                aho5.removeItemType(bce4, integer7 - 256);
                return;
            }
            this.take(atx, bcj3.getCount());
            final ItemStack bcj4 = aho5.addItem(bcj3);
            if (bcj4.isEmpty()) {
                atx.remove();
            }
            else {
                bcj3.setCount(bcj4.getCount());
            }
        }
    }
    
    public boolean wantToPickUp(final Item bce) {
        return Villager.WANTED_ITEMS.contains(bce) || this.getVillagerData().getProfession().getRequestedItems().contains(bce);
    }
    
    public boolean hasExcessFood() {
        return this.countFoodPointsInInventory() >= 24;
    }
    
    public boolean wantsMoreFood() {
        return this.countFoodPointsInInventory() < 12;
    }
    
    private int countFoodPointsInInventory() {
        final SimpleContainer aho2 = this.getInventory();
        return Villager.FOOD_POINTS.entrySet().stream().mapToInt(entry -> aho2.countItem((Item)entry.getKey()) * (int)entry.getValue()).sum();
    }
    
    private void makeBread() {
        final SimpleContainer aho2 = this.getInventory();
        final int integer3 = aho2.countItem(Items.WHEAT);
        final int integer4 = integer3 / 3;
        if (integer4 == 0) {
            return;
        }
        final int integer5 = integer4 * 3;
        aho2.removeItemType(Items.WHEAT, integer5);
        final ItemStack bcj6 = aho2.addItem(new ItemStack(Items.BREAD, integer4));
        if (!bcj6.isEmpty()) {
            this.spawnAtLocation(bcj6, 0.5f);
        }
    }
    
    public boolean hasFarmSeeds() {
        final SimpleContainer aho2 = this.getInventory();
        return aho2.hasAnyOf((Set<Item>)ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
    }
    
    @Override
    protected void updateTrades() {
        final VillagerData avu2 = this.getVillagerData();
        final Int2ObjectMap<VillagerTrades.ItemListing[]> int2ObjectMap3 = (Int2ObjectMap<VillagerTrades.ItemListing[]>)VillagerTrades.TRADES.get(avu2.getProfession());
        if (int2ObjectMap3 == null || int2ObjectMap3.isEmpty()) {
            return;
        }
        final VillagerTrades.ItemListing[] arr4 = (VillagerTrades.ItemListing[])int2ObjectMap3.get(avu2.getLevel());
        if (arr4 == null) {
            return;
        }
        final MerchantOffers bgv5 = this.getOffers();
        this.addOffersFromItemListings(bgv5, arr4, 2);
    }
    
    public void gossip(final Villager avt, final long long2) {
        if ((long2 >= this.lastGossipTime && long2 < this.lastGossipTime + 1200L) || (long2 >= avt.lastGossipTime && long2 < avt.lastGossipTime + 1200L)) {
            return;
        }
        this.gossips.transferFrom(avt.gossips, this.random, 10);
        this.lastGossipTime = long2;
        this.spawnGolemIfNeeded(avt.lastGossipTime = long2, 5);
    }
    
    private void maybeDecayGossip() {
        final long long2 = this.level.getGameTime();
        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = long2;
            return;
        }
        if (long2 < this.lastGossipDecayTime + 24000L) {
            return;
        }
        this.gossips.decay();
        this.lastGossipDecayTime = long2;
    }
    
    public void spawnGolemIfNeeded(final long long1, final int integer) {
        if (!this.wantsToSpawnGolem(long1)) {
            return;
        }
        final AABB csc5 = this.getBoundingBox().inflate(10.0, 10.0, 10.0);
        final List<Villager> list6 = this.level.<Villager>getEntitiesOfClass((java.lang.Class<? extends Villager>)Villager.class, csc5);
        final List<Villager> list7 = (List<Villager>)list6.stream().filter(avt -> avt.wantsToSpawnGolem(long1)).limit(5L).collect(Collectors.toList());
        if (list7.size() < integer) {
            return;
        }
        final IronGolem ari8 = this.trySpawnGolem();
        if (ari8 == null) {
            return;
        }
        list6.forEach(avt -> avt.sawGolem(long1));
    }
    
    private void sawGolem(final long long1) {
        this.brain.<Long>setMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, long1);
    }
    
    private boolean hasSeenGolemRecently(final long long1) {
        final Optional<Long> optional4 = this.brain.<Long>getMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME);
        if (!optional4.isPresent()) {
            return false;
        }
        final Long long2 = (Long)optional4.get();
        return long1 - long2 <= 600L;
    }
    
    public boolean wantsToSpawnGolem(final long long1) {
        final VillagerData avu4 = this.getVillagerData();
        return avu4.getProfession() != VillagerProfession.NONE && avu4.getProfession() != VillagerProfession.NITWIT && this.golemSpawnConditionsMet(this.level.getGameTime()) && !this.hasSeenGolemRecently(long1);
    }
    
    @Nullable
    private IronGolem trySpawnGolem() {
        final BlockPos ew2 = new BlockPos(this);
        for (int integer3 = 0; integer3 < 10; ++integer3) {
            final double double4 = this.level.random.nextInt(16) - 8;
            final double double5 = this.level.random.nextInt(16) - 8;
            double double6 = 6.0;
            for (int integer4 = 0; integer4 >= -12; --integer4) {
                final BlockPos ew3 = ew2.offset(double4, double6 + integer4, double5);
                if ((this.level.getBlockState(ew3).isAir() || this.level.getBlockState(ew3).getMaterial().isLiquid()) && this.level.getBlockState(ew3.below()).getMaterial().isSolidBlocking()) {
                    double6 += integer4;
                    break;
                }
            }
            final BlockPos ew4 = ew2.offset(double4, double6, double5);
            final IronGolem ari11 = EntityType.IRON_GOLEM.create(this.level, null, null, null, ew4, MobSpawnType.MOB_SUMMONED, false, false);
            if (ari11 != null) {
                if (ari11.checkSpawnRules(this.level, MobSpawnType.MOB_SUMMONED) && ari11.checkSpawnObstruction(this.level)) {
                    this.level.addFreshEntity(ari11);
                    return ari11;
                }
                ari11.remove();
            }
        }
        return null;
    }
    
    @Override
    public void onReputationEventFrom(final ReputationEventType aqm, final Entity aio) {
        if (aqm == ReputationEventType.ZOMBIE_VILLAGER_CURED) {
            this.gossips.add(aio.getUUID(), GossipType.MAJOR_POSITIVE, 20);
            this.gossips.add(aio.getUUID(), GossipType.MINOR_POSITIVE, 25);
        }
        else if (aqm == ReputationEventType.TRADE) {
            this.gossips.add(aio.getUUID(), GossipType.TRADING, 2);
        }
        else if (aqm == ReputationEventType.VILLAGER_HURT) {
            this.gossips.add(aio.getUUID(), GossipType.MINOR_NEGATIVE, 25);
        }
        else if (aqm == ReputationEventType.VILLAGER_KILLED) {
            this.gossips.add(aio.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
        }
    }
    
    @Override
    public int getVillagerXp() {
        return this.villagerXp;
    }
    
    public void setVillagerXp(final int integer) {
        this.villagerXp = integer;
    }
    
    private void resetNumberOfRestocks() {
        this.catchUpDemand();
        this.numberOfRestocksToday = 0;
    }
    
    public GossipContainer getGossips() {
        return this.gossips;
    }
    
    public void setGossips(final Tag iu) {
        this.gossips.update(new Dynamic((DynamicOps)NbtOps.INSTANCE, iu));
    }
    
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }
    
    public void startSleeping(final BlockPos ew) {
        super.startSleeping(ew);
        this.brain.<SerializableLong>setMemory(MemoryModuleType.LAST_SLEPT, SerializableLong.of(this.level.getGameTime()));
    }
    
    private boolean golemSpawnConditionsMet(final long long1) {
        final Optional<SerializableLong> optional4 = this.brain.<SerializableLong>getMemory(MemoryModuleType.LAST_SLEPT);
        final Optional<SerializableLong> optional5 = this.brain.<SerializableLong>getMemory(MemoryModuleType.LAST_WORKED_AT_POI);
        return optional4.isPresent() && optional5.isPresent() && long1 - ((SerializableLong)optional4.get()).value() < 24000L && long1 - ((SerializableLong)optional5.get()).value() < 36000L;
    }
    
    static {
        DATA_VILLAGER_DATA = SynchedEntityData.<VillagerData>defineId(Villager.class, EntityDataSerializers.VILLAGER_DATA);
        FOOD_POINTS = (Map)ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
        WANTED_ITEMS = (Set)ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, (Object[])new Item[] { Items.BEETROOT_SEEDS });
        MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, (Object[])new MemoryModuleType[] { MemoryModuleType.PATH, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.OPENED_DOORS, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_LAST_SEEN_TIME });
        SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.INTERACTABLE_DOORS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_LAST_SEEN);
        POI_MEMORIES = (Map)ImmutableMap.of(MemoryModuleType.HOME, ((avt, aqs) -> aqs == PoiType.HOME), MemoryModuleType.JOB_SITE, ((avt, aqs) -> avt.getVillagerData().getProfession().getJobPoiType() == aqs), MemoryModuleType.MEETING_POINT, ((avt, aqs) -> aqs == PoiType.MEETING));
    }
}
