package net.minecraft.world.entity.player;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ArmorItem;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.network.chat.Style;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.crafting.Recipe;
import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BedBlock;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;
import com.mojang.datafixers.util.Either;
import java.util.Iterator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.trading.MerchantOffers;
import java.util.OptionalInt;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.InteractionHand;
import java.util.function.Consumer;
import net.minecraft.world.scores.Team;
import net.minecraft.world.item.AxeItem;
import net.minecraft.nbt.Tag;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.stats.Stat;
import net.minecraft.world.damagesource.DamageSource;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Pose;
import java.util.Map;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;

public abstract class Player extends LivingEntity {
    public static final EntityDimensions STANDING_DIMENSIONS;
    private static final Map<Pose, EntityDimensions> POSES;
    private static final EntityDataAccessor<Float> DATA_PLAYER_ABSORPTION_ID;
    private static final EntityDataAccessor<Integer> DATA_SCORE_ID;
    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION;
    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MAIN_HAND;
    protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_LEFT;
    protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_RIGHT;
    private long timeEntitySatOnShoulder;
    public final Inventory inventory;
    protected PlayerEnderChestContainer enderChestInventory;
    public final InventoryMenu inventoryMenu;
    public AbstractContainerMenu containerMenu;
    protected FoodData foodData;
    protected int jumpTriggerTime;
    public float oBob;
    public float bob;
    public int takeXpDelay;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    private int sleepCounter;
    protected boolean wasUnderwater;
    private BlockPos respawnPosition;
    private boolean respawnForced;
    public final Abilities abilities;
    public int experienceLevel;
    public int totalExperience;
    public float experienceProgress;
    protected int enchantmentSeed;
    protected final float defaultFlySpeed = 0.02f;
    private int lastLevelUpTime;
    private final GameProfile gameProfile;
    private boolean reducedDebugInfo;
    private ItemStack lastItemInMainHand;
    private final ItemCooldowns cooldowns;
    @Nullable
    public FishingHook fishing;
    
    public Player(final Level bhr, final GameProfile gameProfile) {
        super(EntityType.PLAYER, bhr);
        this.inventory = new Inventory(this);
        this.enderChestInventory = new PlayerEnderChestContainer();
        this.foodData = new FoodData();
        this.abilities = new Abilities();
        this.lastItemInMainHand = ItemStack.EMPTY;
        this.cooldowns = this.createItemCooldowns();
        this.setUUID(createPlayerUUID(gameProfile));
        this.gameProfile = gameProfile;
        this.inventoryMenu = new InventoryMenu(this.inventory, !bhr.isClientSide, this);
        this.containerMenu = this.inventoryMenu;
        final BlockPos ew4 = bhr.getSharedSpawnPos();
        this.moveTo(ew4.getX() + 0.5, ew4.getY() + 1, ew4.getZ() + 0.5, 0.0f, 0.0f);
        this.rotOffs = 180.0f;
    }
    
    public boolean blockActionRestricted(final Level bhr, final BlockPos ew, final GameType bho) {
        if (!bho.isBlockPlacingRestricted()) {
            return false;
        }
        if (bho == GameType.SPECTATOR) {
            return true;
        }
        if (this.mayBuild()) {
            return false;
        }
        final ItemStack bcj5 = this.getMainHandItem();
        return bcj5.isEmpty() || !bcj5.hasAdventureModeBreakTagForBlock(bhr.getTagManager(), new BlockInWorld(bhr, ew, false));
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.10000000149011612);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.LUCK);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Float>define(Player.DATA_PLAYER_ABSORPTION_ID, 0.0f);
        this.entityData.<Integer>define(Player.DATA_SCORE_ID, 0);
        this.entityData.<Byte>define(Player.DATA_PLAYER_MODE_CUSTOMISATION, (Byte)0);
        this.entityData.<Byte>define(Player.DATA_PLAYER_MAIN_HAND, (Byte)1);
        this.entityData.<CompoundTag>define(Player.DATA_SHOULDER_LEFT, new CompoundTag());
        this.entityData.<CompoundTag>define(Player.DATA_SHOULDER_RIGHT, new CompoundTag());
    }
    
    @Override
    public void tick() {
        this.noPhysics = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }
        if (this.takeXpDelay > 0) {
            --this.takeXpDelay;
        }
        if (this.isSleeping()) {
            ++this.sleepCounter;
            if (this.sleepCounter > 100) {
                this.sleepCounter = 100;
            }
            if (!this.level.isClientSide && this.level.isDay()) {
                this.stopSleepInBed(false, true, true);
            }
        }
        else if (this.sleepCounter > 0) {
            ++this.sleepCounter;
            if (this.sleepCounter >= 110) {
                this.sleepCounter = 0;
            }
        }
        this.updateIsUnderwater();
        super.tick();
        if (!this.level.isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }
        if (this.isOnFire() && this.abilities.invulnerable) {
            this.clearFire();
        }
        this.moveCloak();
        if (!this.level.isClientSide) {
            this.foodData.tick(this);
            this.awardStat(Stats.PLAY_ONE_MINUTE);
            if (this.isAlive()) {
                this.awardStat(Stats.TIME_SINCE_DEATH);
            }
            if (this.isSneaking()) {
                this.awardStat(Stats.SNEAK_TIME);
            }
            if (!this.isSleeping()) {
                this.awardStat(Stats.TIME_SINCE_REST);
            }
        }
        final int integer2 = 29999999;
        final double double3 = Mth.clamp(this.x, -2.9999999E7, 2.9999999E7);
        final double double4 = Mth.clamp(this.z, -2.9999999E7, 2.9999999E7);
        if (double3 != this.x || double4 != this.z) {
            this.setPos(double3, this.y, double4);
        }
        ++this.attackStrengthTicker;
        final ItemStack bcj7 = this.getMainHandItem();
        if (!ItemStack.matches(this.lastItemInMainHand, bcj7)) {
            if (!ItemStack.isSameIgnoreDurability(this.lastItemInMainHand, bcj7)) {
                this.resetAttackStrengthTicker();
            }
            this.lastItemInMainHand = (bcj7.isEmpty() ? ItemStack.EMPTY : bcj7.copy());
        }
        this.turtleHelmetTick();
        this.cooldowns.tick();
        this.updatePlayerPose();
    }
    
    protected boolean updateIsUnderwater() {
        return this.wasUnderwater = this.isUnderLiquid(FluidTags.WATER, true);
    }
    
    private void turtleHelmetTick() {
        final ItemStack bcj2 = this.getItemBySlot(EquipmentSlot.HEAD);
        if (bcj2.getItem() == Items.TURTLE_HELMET && !this.isUnderLiquid(FluidTags.WATER)) {
            this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
        }
    }
    
    protected ItemCooldowns createItemCooldowns() {
        return new ItemCooldowns();
    }
    
    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        final double double2 = this.x - this.xCloak;
        final double double3 = this.y - this.yCloak;
        final double double4 = this.z - this.zCloak;
        final double double5 = 10.0;
        if (double2 > 10.0) {
            this.xCloak = this.x;
            this.xCloakO = this.xCloak;
        }
        if (double4 > 10.0) {
            this.zCloak = this.z;
            this.zCloakO = this.zCloak;
        }
        if (double3 > 10.0) {
            this.yCloak = this.y;
            this.yCloakO = this.yCloak;
        }
        if (double2 < -10.0) {
            this.xCloak = this.x;
            this.xCloakO = this.xCloak;
        }
        if (double4 < -10.0) {
            this.zCloak = this.z;
            this.zCloakO = this.zCloak;
        }
        if (double3 < -10.0) {
            this.yCloak = this.y;
            this.yCloakO = this.yCloak;
        }
        this.xCloak += double2 * 0.25;
        this.zCloak += double4 * 0.25;
        this.yCloak += double3 * 0.25;
    }
    
    protected void updatePlayerPose() {
        if (!this.canEnterPose(Pose.SWIMMING)) {
            return;
        }
        Pose ajh2;
        if (this.isFallFlying()) {
            ajh2 = Pose.FALL_FLYING;
        }
        else if (this.isSleeping()) {
            ajh2 = Pose.SLEEPING;
        }
        else if (this.isSwimming()) {
            ajh2 = Pose.SWIMMING;
        }
        else if (this.isAutoSpinAttack()) {
            ajh2 = Pose.SPIN_ATTACK;
        }
        else if (this.isSneaking() && !this.abilities.flying) {
            ajh2 = Pose.SNEAKING;
        }
        else {
            ajh2 = Pose.STANDING;
        }
        Pose ajh3;
        if (this.isSpectator() || this.isPassenger() || this.canEnterPose(ajh2)) {
            ajh3 = ajh2;
        }
        else if (this.canEnterPose(Pose.SNEAKING)) {
            ajh3 = Pose.SNEAKING;
        }
        else {
            ajh3 = Pose.SWIMMING;
        }
        this.setPose(ajh3);
    }
    
    @Override
    public int getPortalWaitTime() {
        return this.abilities.invulnerable ? 1 : 80;
    }
    
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.PLAYER_SWIM;
    }
    
    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.PLAYER_SPLASH;
    }
    
    @Override
    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
    }
    
    @Override
    public int getDimensionChangingDelay() {
        return 10;
    }
    
    @Override
    public void playSound(final SoundEvent yo, final float float2, final float float3) {
        this.level.playSound(this, this.x, this.y, this.z, yo, this.getSoundSource(), float2, float3);
    }
    
    public void playNotifySound(final SoundEvent yo, final SoundSource yq, final float float3, final float float4) {
    }
    
    @Override
    public SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }
    
    @Override
    protected int getFireImmuneTicks() {
        return 20;
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 9) {
            this.completeUsingItem();
        }
        else if (byte1 == 23) {
            this.reducedDebugInfo = false;
        }
        else if (byte1 == 22) {
            this.reducedDebugInfo = true;
        }
        else if (byte1 == 43) {
            this.addParticlesAroundSelf(ParticleTypes.CLOUD);
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    private void addParticlesAroundSelf(final ParticleOptions gf) {
        for (int integer3 = 0; integer3 < 5; ++integer3) {
            final double double4 = this.random.nextGaussian() * 0.02;
            final double double5 = this.random.nextGaussian() * 0.02;
            final double double6 = this.random.nextGaussian() * 0.02;
            this.level.addParticle(gf, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + 1.0 + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), double4, double5, double6);
        }
    }
    
    protected void closeContainer() {
        this.containerMenu = this.inventoryMenu;
    }
    
    @Override
    public void rideTick() {
        if (!this.level.isClientSide && this.isSneaking() && this.isPassenger()) {
            this.stopRiding();
            this.setSneaking(false);
            return;
        }
        final double double2 = this.x;
        final double double3 = this.y;
        final double double4 = this.z;
        final float float8 = this.yRot;
        final float float9 = this.xRot;
        super.rideTick();
        this.oBob = this.bob;
        this.bob = 0.0f;
        this.checkRidingStatistiscs(this.x - double2, this.y - double3, this.z - double4);
        if (this.getVehicle() instanceof Pig) {
            this.xRot = float9;
            this.yRot = float8;
            this.yBodyRot = ((Pig)this.getVehicle()).yBodyRot;
        }
    }
    
    public void resetPos() {
        this.setPose(Pose.STANDING);
        super.resetPos();
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }
    
    @Override
    protected void serverAiStep() {
        super.serverAiStep();
        this.updateSwingTime();
        this.yHeadRot = this.yRot;
    }
    
    @Override
    public void aiStep() {
        if (this.jumpTriggerTime > 0) {
            --this.jumpTriggerTime;
        }
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
                this.heal(1.0f);
            }
            if (this.foodData.needsFood() && this.tickCount % 10 == 0) {
                this.foodData.setFoodLevel(this.foodData.getFoodLevel() + 1);
            }
        }
        this.inventory.tick();
        this.oBob = this.bob;
        super.aiStep();
        final AttributeInstance ajo2 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (!this.level.isClientSide) {
            ajo2.setBaseValue(this.abilities.getWalkingSpeed());
        }
        this.flyingSpeed = 0.02f;
        if (this.isSprinting()) {
            this.flyingSpeed += (float)0.005999999865889549;
        }
        this.setSpeed((float)ajo2.getValue());
        float float3;
        if (!this.onGround || this.getHealth() <= 0.0f || this.isSwimming()) {
            float3 = 0.0f;
        }
        else {
            float3 = Math.min(0.1f, Mth.sqrt(Entity.getHorizontalDistanceSqr(this.getDeltaMovement())));
        }
        this.bob += (float3 - this.bob) * 0.4f;
        if (this.getHealth() > 0.0f && !this.isSpectator()) {
            AABB csc4;
            if (this.isPassenger() && !this.getVehicle().removed) {
                csc4 = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0, 0.0, 1.0);
            }
            else {
                csc4 = this.getBoundingBox().inflate(1.0, 0.5, 1.0);
            }
            final List<Entity> list5 = this.level.getEntities(this, csc4);
            for (int integer6 = 0; integer6 < list5.size(); ++integer6) {
                final Entity aio7 = (Entity)list5.get(integer6);
                if (!aio7.removed) {
                    this.touch(aio7);
                }
            }
        }
        this.playShoulderEntityAmbientSound(this.getShoulderEntityLeft());
        this.playShoulderEntityAmbientSound(this.getShoulderEntityRight());
        if ((!this.level.isClientSide && (this.fallDistance > 0.5f || this.isInWater() || this.isPassenger())) || this.abilities.flying || this.isSleeping()) {
            this.removeEntitiesOnShoulder();
        }
    }
    
    private void playShoulderEntityAmbientSound(@Nullable final CompoundTag id) {
        if ((id != null && !id.contains("Silent")) || !id.getBoolean("Silent")) {
            final String string3 = id.getString("id");
            EntityType.byString(string3).filter(ais -> ais == EntityType.PARROT).ifPresent(ais -> Parrot.playAmbientSound(this.level, this));
        }
    }
    
    private void touch(final Entity aio) {
        aio.playerTouch(this);
    }
    
    public int getScore() {
        return this.entityData.<Integer>get(Player.DATA_SCORE_ID);
    }
    
    public void setScore(final int integer) {
        this.entityData.<Integer>set(Player.DATA_SCORE_ID, integer);
    }
    
    public void increaseScore(final int integer) {
        final int integer2 = this.getScore();
        this.entityData.<Integer>set(Player.DATA_SCORE_ID, integer2 + integer);
    }
    
    @Override
    public void die(final DamageSource ahx) {
        super.die(ahx);
        this.setPos(this.x, this.y, this.z);
        if (!this.isSpectator()) {
            this.dropAllDeathLoot(ahx);
        }
        if (ahx != null) {
            this.setDeltaMovement(-Mth.cos((this.hurtDir + this.yRot) * 0.017453292f) * 0.1f, 0.10000000149011612, -Mth.sin((this.hurtDir + this.yRot) * 0.017453292f) * 0.1f);
        }
        else {
            this.setDeltaMovement(0.0, 0.1, 0.0);
        }
        this.awardStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        this.clearFire();
        this.setSharedFlag(0, false);
    }
    
    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            this.destroyVanishingCursedItems();
            this.inventory.dropAll();
        }
    }
    
    protected void destroyVanishingCursedItems() {
        for (int integer2 = 0; integer2 < this.inventory.getContainerSize(); ++integer2) {
            final ItemStack bcj3 = this.inventory.getItem(integer2);
            if (!bcj3.isEmpty() && EnchantmentHelper.hasVanishingCurse(bcj3)) {
                this.inventory.removeItemNoUpdate(integer2);
            }
        }
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        if (ahx == DamageSource.ON_FIRE) {
            return SoundEvents.PLAYER_HURT_ON_FIRE;
        }
        if (ahx == DamageSource.DROWN) {
            return SoundEvents.PLAYER_HURT_DROWN;
        }
        if (ahx == DamageSource.SWEET_BERRY_BUSH) {
            return SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH;
        }
        return SoundEvents.PLAYER_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }
    
    @Nullable
    public ItemEntity drop(final boolean boolean1) {
        return this.drop(this.inventory.removeItem(this.inventory.selected, (boolean1 && !this.inventory.getSelected().isEmpty()) ? this.inventory.getSelected().getCount() : 1), false, true);
    }
    
    @Nullable
    public ItemEntity drop(final ItemStack bcj, final boolean boolean2) {
        return this.drop(bcj, false, boolean2);
    }
    
    @Nullable
    public ItemEntity drop(final ItemStack bcj, final boolean boolean2, final boolean boolean3) {
        if (bcj.isEmpty()) {
            return null;
        }
        final double double5 = this.y - 0.30000001192092896 + this.getEyeHeight();
        final ItemEntity atx7 = new ItemEntity(this.level, this.x, double5, this.z, bcj);
        atx7.setPickUpDelay(40);
        if (boolean3) {
            atx7.setThrower(this.getUUID());
        }
        if (boolean2) {
            final float float8 = this.random.nextFloat() * 0.5f;
            final float float9 = this.random.nextFloat() * 6.2831855f;
            this.setDeltaMovement(-Mth.sin(float9) * float8, 0.20000000298023224, Mth.cos(float9) * float8);
        }
        else {
            final float float8 = 0.3f;
            final float float9 = Mth.sin(this.xRot * 0.017453292f);
            final float float10 = Mth.cos(this.xRot * 0.017453292f);
            final float float11 = Mth.sin(this.yRot * 0.017453292f);
            final float float12 = Mth.cos(this.yRot * 0.017453292f);
            final float float13 = this.random.nextFloat() * 6.2831855f;
            final float float14 = 0.02f * this.random.nextFloat();
            atx7.setDeltaMovement(-float11 * float10 * 0.3f + Math.cos((double)float13) * float14, -float9 * 0.3f + 0.1f + (this.random.nextFloat() - this.random.nextFloat()) * 0.1f, float12 * float10 * 0.3f + Math.sin((double)float13) * float14);
        }
        return atx7;
    }
    
    public float getDestroySpeed(final BlockState bvt) {
        float float3 = this.inventory.getDestroySpeed(bvt);
        if (float3 > 1.0f) {
            final int integer4 = EnchantmentHelper.getBlockEfficiency(this);
            final ItemStack bcj5 = this.getMainHandItem();
            if (integer4 > 0 && !bcj5.isEmpty()) {
                float3 += integer4 * integer4 + 1;
            }
        }
        if (MobEffectUtil.hasDigSpeed(this)) {
            float3 *= 1.0f + (MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2f;
        }
        if (this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float float4 = 0.0f;
            switch (this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0: {
                    float4 = 0.3f;
                    break;
                }
                case 1: {
                    float4 = 0.09f;
                    break;
                }
                case 2: {
                    float4 = 0.0027f;
                    break;
                }
                default: {
                    float4 = 8.1E-4f;
                    break;
                }
            }
            float3 *= float4;
        }
        if (this.isUnderLiquid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
            float3 /= 5.0f;
        }
        if (!this.onGround) {
            float3 /= 5.0f;
        }
        return float3;
    }
    
    public boolean canDestroy(final BlockState bvt) {
        return bvt.getMaterial().isAlwaysDestroyable() || this.inventory.canDestroy(bvt);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setUUID(createPlayerUUID(this.gameProfile));
        final ListTag ik3 = id.getList("Inventory", 10);
        this.inventory.load(ik3);
        this.inventory.selected = id.getInt("SelectedItemSlot");
        this.sleepCounter = id.getShort("SleepTimer");
        this.experienceProgress = id.getFloat("XpP");
        this.experienceLevel = id.getInt("XpLevel");
        this.totalExperience = id.getInt("XpTotal");
        this.enchantmentSeed = id.getInt("XpSeed");
        if (this.enchantmentSeed == 0) {
            this.enchantmentSeed = this.random.nextInt();
        }
        this.setScore(id.getInt("Score"));
        if (id.contains("SpawnX", 99) && id.contains("SpawnY", 99) && id.contains("SpawnZ", 99)) {
            this.respawnPosition = new BlockPos(id.getInt("SpawnX"), id.getInt("SpawnY"), id.getInt("SpawnZ"));
            this.respawnForced = id.getBoolean("SpawnForced");
        }
        this.foodData.readAdditionalSaveData(id);
        this.abilities.loadSaveData(id);
        if (id.contains("EnderItems", 9)) {
            this.enderChestInventory.fromTag(id.getList("EnderItems", 10));
        }
        if (id.contains("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft(id.getCompound("ShoulderEntityLeft"));
        }
        if (id.contains("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight(id.getCompound("ShoulderEntityRight"));
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        id.put("Inventory", (Tag)this.inventory.save(new ListTag()));
        id.putInt("SelectedItemSlot", this.inventory.selected);
        id.putShort("SleepTimer", (short)this.sleepCounter);
        id.putFloat("XpP", this.experienceProgress);
        id.putInt("XpLevel", this.experienceLevel);
        id.putInt("XpTotal", this.totalExperience);
        id.putInt("XpSeed", this.enchantmentSeed);
        id.putInt("Score", this.getScore());
        if (this.respawnPosition != null) {
            id.putInt("SpawnX", this.respawnPosition.getX());
            id.putInt("SpawnY", this.respawnPosition.getY());
            id.putInt("SpawnZ", this.respawnPosition.getZ());
            id.putBoolean("SpawnForced", this.respawnForced);
        }
        this.foodData.addAdditionalSaveData(id);
        this.abilities.addSaveData(id);
        id.put("EnderItems", (Tag)this.enderChestInventory.createTag());
        if (!this.getShoulderEntityLeft().isEmpty()) {
            id.put("ShoulderEntityLeft", (Tag)this.getShoulderEntityLeft());
        }
        if (!this.getShoulderEntityRight().isEmpty()) {
            id.put("ShoulderEntityRight", (Tag)this.getShoulderEntityRight());
        }
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        if (this.abilities.invulnerable && !ahx.isBypassInvul()) {
            return false;
        }
        this.noActionTime = 0;
        if (this.getHealth() <= 0.0f) {
            return false;
        }
        this.removeEntitiesOnShoulder();
        if (ahx.scalesWithDifficulty()) {
            if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                float2 = 0.0f;
            }
            if (this.level.getDifficulty() == Difficulty.EASY) {
                float2 = Math.min(float2 / 2.0f + 1.0f, float2);
            }
            if (this.level.getDifficulty() == Difficulty.HARD) {
                float2 = float2 * 3.0f / 2.0f;
            }
        }
        return float2 != 0.0f && super.hurt(ahx, float2);
    }
    
    @Override
    protected void blockUsingShield(final LivingEntity aix) {
        super.blockUsingShield(aix);
        if (aix.getMainHandItem().getItem() instanceof AxeItem) {
            this.disableShield(true);
        }
    }
    
    public boolean canHarmPlayer(final Player awg) {
        final Team ctk3 = this.getTeam();
        final Team ctk4 = awg.getTeam();
        return ctk3 == null || !ctk3.isAlliedTo(ctk4) || ctk3.isAllowFriendlyFire();
    }
    
    @Override
    protected void hurtArmor(final float float1) {
        this.inventory.hurtArmor(float1);
    }
    
    @Override
    protected void hurtCurrentlyUsedShield(final float float1) {
        if (float1 >= 3.0f && this.useItem.getItem() == Items.SHIELD) {
            final int integer3 = 1 + Mth.floor(float1);
            final InteractionHand ahi4 = this.getUsedItemHand();
            this.useItem.<Player>hurtAndBreak(integer3, this, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(ahi4)));
            if (this.useItem.isEmpty()) {
                if (ahi4 == InteractionHand.MAIN_HAND) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                }
                else {
                    this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                this.useItem = ItemStack.EMPTY;
                this.playSound(SoundEvents.SHIELD_BREAK, 0.8f, 0.8f + this.level.random.nextFloat() * 0.4f);
            }
        }
    }
    
    @Override
    protected void actuallyHurt(final DamageSource ahx, float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return;
        }
        float2 = this.getDamageAfterArmorAbsorb(ahx, float2);
        final float float3;
        float2 = (float3 = this.getDamageAfterMagicAbsorb(ahx, float2));
        float2 = Math.max(float2 - this.getAbsorptionAmount(), 0.0f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (float3 - float2));
        final float float4 = float3 - float2;
        if (float4 > 0.0f && float4 < 3.4028235E37f) {
            this.awardStat(Stats.DAMAGE_ABSORBED, Math.round(float4 * 10.0f));
        }
        if (float2 == 0.0f) {
            return;
        }
        this.causeFoodExhaustion(ahx.getFoodExhaustion());
        final float float5 = this.getHealth();
        this.setHealth(this.getHealth() - float2);
        this.getCombatTracker().recordDamage(ahx, float5, float2);
        if (float2 < 3.4028235E37f) {
            this.awardStat(Stats.DAMAGE_TAKEN, Math.round(float2 * 10.0f));
        }
    }
    
    public void openTextEdit(final SignBlockEntity bus) {
    }
    
    public void openMinecartCommandBlock(final BaseCommandBlock bgx) {
    }
    
    public void openCommandBlock(final CommandBlockEntity bub) {
    }
    
    public void openStructureBlock(final StructureBlockEntity buw) {
    }
    
    public void openJigsawBlock(final JigsawBlockEntity bum) {
    }
    
    public void openHorseInventory(final AbstractHorse asb, final Container ahc) {
    }
    
    public OptionalInt openMenu(@Nullable final MenuProvider ahm) {
        return OptionalInt.empty();
    }
    
    public void sendMerchantOffers(final int integer1, final MerchantOffers bgv, final int integer3, final int integer4, final boolean boolean5, final boolean boolean6) {
    }
    
    public void openItemGui(final ItemStack bcj, final InteractionHand ahi) {
    }
    
    public InteractionResult interactOn(final Entity aio, final InteractionHand ahi) {
        if (this.isSpectator()) {
            if (aio instanceof MenuProvider) {
                this.openMenu((MenuProvider)aio);
            }
            return InteractionResult.PASS;
        }
        ItemStack bcj4 = this.getItemInHand(ahi);
        final ItemStack bcj5 = bcj4.isEmpty() ? ItemStack.EMPTY : bcj4.copy();
        if (aio.interact(this, ahi)) {
            if (this.abilities.instabuild && bcj4 == this.getItemInHand(ahi) && bcj4.getCount() < bcj5.getCount()) {
                bcj4.setCount(bcj5.getCount());
            }
            return InteractionResult.SUCCESS;
        }
        if (!bcj4.isEmpty() && aio instanceof LivingEntity) {
            if (this.abilities.instabuild) {
                bcj4 = bcj5;
            }
            if (bcj4.interactEnemy(this, (LivingEntity)aio, ahi)) {
                if (bcj4.isEmpty() && !this.abilities.instabuild) {
                    this.setItemInHand(ahi, ItemStack.EMPTY);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
    
    @Override
    public double getRidingHeight() {
        return -0.35;
    }
    
    @Override
    public void stopRiding() {
        super.stopRiding();
        this.boardingCooldown = 0;
    }
    
    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isSleeping();
    }
    
    public void attack(final Entity aio) {
        if (!aio.isAttackable()) {
            return;
        }
        if (aio.skipAttackInteraction(this)) {
            return;
        }
        float float3 = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
        float float4;
        if (aio instanceof LivingEntity) {
            float4 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)aio).getMobType());
        }
        else {
            float4 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), MobType.UNDEFINED);
        }
        final float float5 = this.getAttackStrengthScale(0.5f);
        float3 *= 0.2f + float5 * float5 * 0.8f;
        float4 *= float5;
        this.resetAttackStrengthTicker();
        if (float3 > 0.0f || float4 > 0.0f) {
            final boolean boolean6 = float5 > 0.9f;
            boolean boolean7 = false;
            int integer8 = 0;
            integer8 += EnchantmentHelper.getKnockbackBonus(this);
            if (this.isSprinting() && boolean6) {
                this.level.playSound(null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0f, 1.0f);
                ++integer8;
                boolean7 = true;
            }
            boolean boolean8 = boolean6 && this.fallDistance > 0.0f && !this.onGround && !this.onLadder() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && aio instanceof LivingEntity;
            boolean8 = (boolean8 && !this.isSprinting());
            if (boolean8) {
                float3 *= 1.5f;
            }
            float3 += float4;
            boolean boolean9 = false;
            final double double11 = this.walkDist - this.walkDistO;
            if (boolean6 && !boolean8 && !boolean7 && this.onGround && double11 < this.getSpeed()) {
                final ItemStack bcj13 = this.getItemInHand(InteractionHand.MAIN_HAND);
                if (bcj13.getItem() instanceof SwordItem) {
                    boolean9 = true;
                }
            }
            float float6 = 0.0f;
            boolean boolean10 = false;
            final int integer9 = EnchantmentHelper.getFireAspect(this);
            if (aio instanceof LivingEntity) {
                float6 = ((LivingEntity)aio).getHealth();
                if (integer9 > 0 && !aio.isOnFire()) {
                    boolean10 = true;
                    aio.setSecondsOnFire(1);
                }
            }
            final Vec3 csi16 = aio.getDeltaMovement();
            final boolean boolean11 = aio.hurt(DamageSource.playerAttack(this), float3);
            if (boolean11) {
                if (integer8 > 0) {
                    if (aio instanceof LivingEntity) {
                        ((LivingEntity)aio).knockback(this, integer8 * 0.5f, Mth.sin(this.yRot * 0.017453292f), -Mth.cos(this.yRot * 0.017453292f));
                    }
                    else {
                        aio.push(-Mth.sin(this.yRot * 0.017453292f) * integer8 * 0.5f, 0.1, Mth.cos(this.yRot * 0.017453292f) * integer8 * 0.5f);
                    }
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
                    this.setSprinting(false);
                }
                if (boolean9) {
                    final float float7 = 1.0f + EnchantmentHelper.getSweepingDamageRatio(this) * float3;
                    final List<LivingEntity> list19 = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, aio.getBoundingBox().inflate(1.0, 0.25, 1.0));
                    for (final LivingEntity aix21 : list19) {
                        if (aix21 != this && aix21 != aio) {
                            if (this.isAlliedTo(aix21)) {
                                continue;
                            }
                            if (aix21 instanceof ArmorStand && ((ArmorStand)aix21).isMarker()) {
                                continue;
                            }
                            if (this.distanceToSqr(aix21) >= 9.0) {
                                continue;
                            }
                            aix21.knockback(this, 0.4f, Mth.sin(this.yRot * 0.017453292f), -Mth.cos(this.yRot * 0.017453292f));
                            aix21.hurt(DamageSource.playerAttack(this), float7);
                        }
                    }
                    this.level.playSound(null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0f, 1.0f);
                    this.sweepAttack();
                }
                if (aio instanceof ServerPlayer && aio.hurtMarked) {
                    ((ServerPlayer)aio).connection.send(new ClientboundSetEntityMotionPacket(aio));
                    aio.hurtMarked = false;
                    aio.setDeltaMovement(csi16);
                }
                if (boolean8) {
                    this.level.playSound(null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0f, 1.0f);
                    this.crit(aio);
                }
                if (!boolean8 && !boolean9) {
                    if (boolean6) {
                        this.level.playSound(null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0f, 1.0f);
                    }
                    else {
                        this.level.playSound(null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0f, 1.0f);
                    }
                }
                if (float4 > 0.0f) {
                    this.magicCrit(aio);
                }
                this.setLastHurtMob(aio);
                if (aio instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects((LivingEntity)aio, this);
                }
                EnchantmentHelper.doPostDamageEffects(this, aio);
                final ItemStack bcj14 = this.getMainHandItem();
                Entity aio2 = aio;
                if (aio instanceof EnderDragonPart) {
                    aio2 = ((EnderDragonPart)aio).parentMob;
                }
                if (!this.level.isClientSide && !bcj14.isEmpty() && aio2 instanceof LivingEntity) {
                    bcj14.hurtEnemy((LivingEntity)aio2, this);
                    if (bcj14.isEmpty()) {
                        this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    }
                }
                if (aio instanceof LivingEntity) {
                    final float float8 = float6 - ((LivingEntity)aio).getHealth();
                    this.awardStat(Stats.DAMAGE_DEALT, Math.round(float8 * 10.0f));
                    if (integer9 > 0) {
                        aio.setSecondsOnFire(integer9 * 4);
                    }
                    if (this.level instanceof ServerLevel && float8 > 2.0f) {
                        final int integer10 = (int)(float8 * 0.5);
                        ((ServerLevel)this.level).<SimpleParticleType>sendParticles(ParticleTypes.DAMAGE_INDICATOR, aio.x, aio.y + aio.getBbHeight() * 0.5f, aio.z, integer10, 0.1, 0.0, 0.1, 0.2);
                    }
                }
                this.causeFoodExhaustion(0.1f);
            }
            else {
                this.level.playSound(null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0f, 1.0f);
                if (boolean10) {
                    aio.clearFire();
                }
            }
        }
    }
    
    @Override
    protected void doAutoAttackOnTouch(final LivingEntity aix) {
        this.attack(aix);
    }
    
    public void disableShield(final boolean boolean1) {
        float float3 = 0.25f + EnchantmentHelper.getBlockEfficiency(this) * 0.05f;
        if (boolean1) {
            float3 += 0.75f;
        }
        if (this.random.nextFloat() < float3) {
            this.getCooldowns().addCooldown(Items.SHIELD, 100);
            this.stopUsingItem();
            this.level.broadcastEntityEvent(this, (byte)30);
        }
    }
    
    public void crit(final Entity aio) {
    }
    
    public void magicCrit(final Entity aio) {
    }
    
    public void sweepAttack() {
        final double double2 = -Mth.sin(this.yRot * 0.017453292f);
        final double double3 = Mth.cos(this.yRot * 0.017453292f);
        if (this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).<SimpleParticleType>sendParticles(ParticleTypes.SWEEP_ATTACK, this.x + double2, this.y + this.getBbHeight() * 0.5, this.z + double3, 0, double2, 0.0, double3, 0.0);
        }
    }
    
    public void respawn() {
    }
    
    @Override
    public void remove() {
        super.remove();
        this.inventoryMenu.removed(this);
        if (this.containerMenu != null) {
            this.containerMenu.removed(this);
        }
    }
    
    public boolean isLocalPlayer() {
        return false;
    }
    
    public GameProfile getGameProfile() {
        return this.gameProfile;
    }
    
    public Either<BedSleepingProblem, Unit> startSleepInBed(final BlockPos ew) {
        final Direction fb3 = this.level.getBlockState(ew).<Direction>getValue((Property<Direction>)HorizontalDirectionalBlock.FACING);
        if (!this.level.isClientSide) {
            if (this.isSleeping() || !this.isAlive()) {
                return (Either<BedSleepingProblem, Unit>)Either.left(BedSleepingProblem.OTHER_PROBLEM);
            }
            if (!this.level.dimension.isNaturalDimension()) {
                return (Either<BedSleepingProblem, Unit>)Either.left(BedSleepingProblem.NOT_POSSIBLE_HERE);
            }
            if (this.level.isDay()) {
                return (Either<BedSleepingProblem, Unit>)Either.left(BedSleepingProblem.NOT_POSSIBLE_NOW);
            }
            if (!this.bedInRange(ew, fb3)) {
                return (Either<BedSleepingProblem, Unit>)Either.left(BedSleepingProblem.TOO_FAR_AWAY);
            }
            if (this.bedBlocked(ew, fb3)) {
                return (Either<BedSleepingProblem, Unit>)Either.left(BedSleepingProblem.OBSTRUCTED);
            }
            if (!this.isCreative()) {
                final double double4 = 8.0;
                final double double5 = 5.0;
                final List<Monster> list8 = this.level.<Monster>getEntitiesOfClass((java.lang.Class<? extends Monster>)Monster.class, new AABB(ew.getX() - 8.0, ew.getY() - 5.0, ew.getZ() - 8.0, ew.getX() + 8.0, ew.getY() + 5.0, ew.getZ() + 8.0), (java.util.function.Predicate<? super Monster>)(aus -> aus.isPreventingPlayerRest(this)));
                if (!list8.isEmpty()) {
                    return (Either<BedSleepingProblem, Unit>)Either.left(BedSleepingProblem.NOT_SAFE);
                }
            }
        }
        this.startSleeping(ew);
        this.sleepCounter = 0;
        if (this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).updateSleepingPlayerList();
        }
        return (Either<BedSleepingProblem, Unit>)Either.right(Unit.INSTANCE);
    }
    
    @Override
    public void startSleeping(final BlockPos ew) {
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        super.startSleeping(ew);
    }
    
    private boolean bedInRange(final BlockPos ew, final Direction fb) {
        if (Math.abs(this.x - ew.getX()) <= 3.0 && Math.abs(this.y - ew.getY()) <= 2.0 && Math.abs(this.z - ew.getZ()) <= 3.0) {
            return true;
        }
        final BlockPos ew2 = ew.relative(fb.getOpposite());
        return Math.abs(this.x - ew2.getX()) <= 3.0 && Math.abs(this.y - ew2.getY()) <= 2.0 && Math.abs(this.z - ew2.getZ()) <= 3.0;
    }
    
    private boolean bedBlocked(final BlockPos ew, final Direction fb) {
        final BlockPos ew2 = ew.above();
        return !this.freeAt(ew2) || !this.freeAt(ew2.relative(fb.getOpposite()));
    }
    
    public void stopSleepInBed(final boolean boolean1, final boolean boolean2, final boolean boolean3) {
        final Optional<BlockPos> optional5 = this.getSleepingPos();
        super.stopSleeping();
        if (this.level instanceof ServerLevel && boolean2) {
            ((ServerLevel)this.level).updateSleepingPlayerList();
        }
        this.sleepCounter = (boolean1 ? 0 : 100);
        if (boolean3) {
            optional5.ifPresent(ew -> this.setRespawnPosition(ew, false));
        }
    }
    
    @Override
    public void stopSleeping() {
        this.stopSleepInBed(true, true, false);
    }
    
    public static Optional<Vec3> checkBedValidRespawnPosition(final LevelReader bhu, final BlockPos ew, final boolean boolean3) {
        final Block bmv4 = bhu.getBlockState(ew).getBlock();
        if (bmv4 instanceof BedBlock) {
            return BedBlock.findStandUpPosition(EntityType.PLAYER, bhu, ew, 0);
        }
        if (!boolean3) {
            return (Optional<Vec3>)Optional.empty();
        }
        final boolean boolean4 = bmv4.isPossibleToRespawnInThis();
        final boolean boolean5 = bhu.getBlockState(ew.above()).getBlock().isPossibleToRespawnInThis();
        if (boolean4 && boolean5) {
            return (Optional<Vec3>)Optional.of(new Vec3(ew.getX() + 0.5, ew.getY() + 0.1, ew.getZ() + 0.5));
        }
        return (Optional<Vec3>)Optional.empty();
    }
    
    public boolean isSleepingLongEnough() {
        return this.isSleeping() && this.sleepCounter >= 100;
    }
    
    public int getSleepTimer() {
        return this.sleepCounter;
    }
    
    public void displayClientMessage(final Component jo, final boolean boolean2) {
    }
    
    public BlockPos getRespawnPosition() {
        return this.respawnPosition;
    }
    
    public boolean isRespawnForced() {
        return this.respawnForced;
    }
    
    public void setRespawnPosition(final BlockPos ew, final boolean boolean2) {
        if (ew != null) {
            this.respawnPosition = ew;
            this.respawnForced = boolean2;
        }
        else {
            this.respawnPosition = null;
            this.respawnForced = false;
        }
    }
    
    public void awardStat(final ResourceLocation qv) {
        this.awardStat(Stats.CUSTOM.get(qv));
    }
    
    public void awardStat(final ResourceLocation qv, final int integer) {
        this.awardStat(Stats.CUSTOM.get(qv), integer);
    }
    
    public void awardStat(final Stat<?> yv) {
        this.awardStat(yv, 1);
    }
    
    public void awardStat(final Stat<?> yv, final int integer) {
    }
    
    public void resetStat(final Stat<?> yv) {
    }
    
    public int awardRecipes(final Collection<Recipe<?>> collection) {
        return 0;
    }
    
    public void awardRecipesByKey(final ResourceLocation[] arr) {
    }
    
    public int resetRecipes(final Collection<Recipe<?>> collection) {
        return 0;
    }
    
    public void jumpFromGround() {
        super.jumpFromGround();
        this.awardStat(Stats.JUMP);
        if (this.isSprinting()) {
            this.causeFoodExhaustion(0.2f);
        }
        else {
            this.causeFoodExhaustion(0.05f);
        }
    }
    
    @Override
    public void travel(final Vec3 csi) {
        final double double3 = this.x;
        final double double4 = this.y;
        final double double5 = this.z;
        if (this.isSwimming() && !this.isPassenger()) {
            final double double6 = this.getLookAngle().y;
            final double double7 = (double6 < -0.2) ? 0.085 : 0.06;
            if (double6 <= 0.0 || this.jumping || !this.level.getBlockState(new BlockPos(this.x, this.y + 1.0 - 0.1, this.z)).getFluidState().isEmpty()) {
                final Vec3 csi2 = this.getDeltaMovement();
                this.setDeltaMovement(csi2.add(0.0, (double6 - csi2.y) * double7, 0.0));
            }
        }
        if (this.abilities.flying && !this.isPassenger()) {
            final double double6 = this.getDeltaMovement().y;
            final float float11 = this.flyingSpeed;
            this.flyingSpeed = this.abilities.getFlyingSpeed() * (this.isSprinting() ? 2 : 1);
            super.travel(csi);
            final Vec3 csi3 = this.getDeltaMovement();
            this.setDeltaMovement(csi3.x, double6 * 0.6, csi3.z);
            this.flyingSpeed = float11;
            this.fallDistance = 0.0f;
            this.setSharedFlag(7, false);
        }
        else {
            super.travel(csi);
        }
        this.checkMovementStatistics(this.x - double3, this.y - double4, this.z - double5);
    }
    
    @Override
    public void updateSwimming() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        }
        else {
            super.updateSwimming();
        }
    }
    
    protected boolean freeAt(final BlockPos ew) {
        return !this.level.getBlockState(ew).isViewBlocking(this.level, ew);
    }
    
    @Override
    public float getSpeed() {
        return (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
    }
    
    public void checkMovementStatistics(final double double1, final double double2, final double double3) {
        if (this.isPassenger()) {
            return;
        }
        if (this.isSwimming()) {
            final int integer8 = Math.round(Mth.sqrt(double1 * double1 + double2 * double2 + double3 * double3) * 100.0f);
            if (integer8 > 0) {
                this.awardStat(Stats.SWIM_ONE_CM, integer8);
                this.causeFoodExhaustion(0.01f * integer8 * 0.01f);
            }
        }
        else if (this.isUnderLiquid(FluidTags.WATER, true)) {
            final int integer8 = Math.round(Mth.sqrt(double1 * double1 + double2 * double2 + double3 * double3) * 100.0f);
            if (integer8 > 0) {
                this.awardStat(Stats.WALK_UNDER_WATER_ONE_CM, integer8);
                this.causeFoodExhaustion(0.01f * integer8 * 0.01f);
            }
        }
        else if (this.isInWater()) {
            final int integer8 = Math.round(Mth.sqrt(double1 * double1 + double3 * double3) * 100.0f);
            if (integer8 > 0) {
                this.awardStat(Stats.WALK_ON_WATER_ONE_CM, integer8);
                this.causeFoodExhaustion(0.01f * integer8 * 0.01f);
            }
        }
        else if (this.onLadder()) {
            if (double2 > 0.0) {
                this.awardStat(Stats.CLIMB_ONE_CM, (int)Math.round(double2 * 100.0));
            }
        }
        else if (this.onGround) {
            final int integer8 = Math.round(Mth.sqrt(double1 * double1 + double3 * double3) * 100.0f);
            if (integer8 > 0) {
                if (this.isSprinting()) {
                    this.awardStat(Stats.SPRINT_ONE_CM, integer8);
                    this.causeFoodExhaustion(0.1f * integer8 * 0.01f);
                }
                else if (this.isSneaking()) {
                    this.awardStat(Stats.CROUCH_ONE_CM, integer8);
                    this.causeFoodExhaustion(0.0f * integer8 * 0.01f);
                }
                else {
                    this.awardStat(Stats.WALK_ONE_CM, integer8);
                    this.causeFoodExhaustion(0.0f * integer8 * 0.01f);
                }
            }
        }
        else if (this.isFallFlying()) {
            final int integer8 = Math.round(Mth.sqrt(double1 * double1 + double2 * double2 + double3 * double3) * 100.0f);
            this.awardStat(Stats.AVIATE_ONE_CM, integer8);
        }
        else {
            final int integer8 = Math.round(Mth.sqrt(double1 * double1 + double3 * double3) * 100.0f);
            if (integer8 > 25) {
                this.awardStat(Stats.FLY_ONE_CM, integer8);
            }
        }
    }
    
    private void checkRidingStatistiscs(final double double1, final double double2, final double double3) {
        if (this.isPassenger()) {
            final int integer8 = Math.round(Mth.sqrt(double1 * double1 + double2 * double2 + double3 * double3) * 100.0f);
            if (integer8 > 0) {
                if (this.getVehicle() instanceof AbstractMinecart) {
                    this.awardStat(Stats.MINECART_ONE_CM, integer8);
                }
                else if (this.getVehicle() instanceof Boat) {
                    this.awardStat(Stats.BOAT_ONE_CM, integer8);
                }
                else if (this.getVehicle() instanceof Pig) {
                    this.awardStat(Stats.PIG_ONE_CM, integer8);
                }
                else if (this.getVehicle() instanceof AbstractHorse) {
                    this.awardStat(Stats.HORSE_ONE_CM, integer8);
                }
            }
        }
    }
    
    @Override
    public void causeFallDamage(final float float1, final float float2) {
        if (this.abilities.mayfly) {
            return;
        }
        if (float1 >= 2.0f) {
            this.awardStat(Stats.FALL_ONE_CM, (int)Math.round(float1 * 100.0));
        }
        super.causeFallDamage(float1, float2);
    }
    
    @Override
    protected void doWaterSplashEffect() {
        if (!this.isSpectator()) {
            super.doWaterSplashEffect();
        }
    }
    
    @Override
    protected SoundEvent getFallDamageSound(final int integer) {
        if (integer > 4) {
            return SoundEvents.PLAYER_BIG_FALL;
        }
        return SoundEvents.PLAYER_SMALL_FALL;
    }
    
    @Override
    public void killed(final LivingEntity aix) {
        this.awardStat(Stats.ENTITY_KILLED.get(aix.getType()));
    }
    
    @Override
    public void makeStuckInBlock(final BlockState bvt, final Vec3 csi) {
        if (!this.abilities.flying) {
            super.makeStuckInBlock(bvt, csi);
        }
    }
    
    public void giveExperiencePoints(final int integer) {
        this.increaseScore(integer);
        this.experienceProgress += integer / (float)this.getXpNeededForNextLevel();
        this.totalExperience = Mth.clamp(this.totalExperience + integer, 0, Integer.MAX_VALUE);
        while (this.experienceProgress < 0.0f) {
            final float float3 = this.experienceProgress * this.getXpNeededForNextLevel();
            if (this.experienceLevel > 0) {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 1.0f + float3 / this.getXpNeededForNextLevel();
            }
            else {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 0.0f;
            }
        }
        while (this.experienceProgress >= 1.0f) {
            this.experienceProgress = (this.experienceProgress - 1.0f) * this.getXpNeededForNextLevel();
            this.giveExperienceLevels(1);
            this.experienceProgress /= this.getXpNeededForNextLevel();
        }
    }
    
    public int getEnchantmentSeed() {
        return this.enchantmentSeed;
    }
    
    public void onEnchantmentPerformed(final ItemStack bcj, final int integer) {
        this.experienceLevel -= integer;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0f;
            this.totalExperience = 0;
        }
        this.enchantmentSeed = this.random.nextInt();
    }
    
    public void giveExperienceLevels(final int integer) {
        this.experienceLevel += integer;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0f;
            this.totalExperience = 0;
        }
        if (integer > 0 && this.experienceLevel % 5 == 0 && this.lastLevelUpTime < this.tickCount - 100.0f) {
            final float float3 = (this.experienceLevel > 30) ? 1.0f : (this.experienceLevel / 30.0f);
            this.level.playSound(null, this.x, this.y, this.z, SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), float3 * 0.75f, 1.0f);
            this.lastLevelUpTime = this.tickCount;
        }
    }
    
    public int getXpNeededForNextLevel() {
        if (this.experienceLevel >= 30) {
            return 112 + (this.experienceLevel - 30) * 9;
        }
        if (this.experienceLevel >= 15) {
            return 37 + (this.experienceLevel - 15) * 5;
        }
        return 7 + this.experienceLevel * 2;
    }
    
    public void causeFoodExhaustion(final float float1) {
        if (this.abilities.invulnerable) {
            return;
        }
        if (!this.level.isClientSide) {
            this.foodData.addExhaustion(float1);
        }
    }
    
    public FoodData getFoodData() {
        return this.foodData;
    }
    
    public boolean canEat(final boolean boolean1) {
        return !this.abilities.invulnerable && (boolean1 || this.foodData.needsFood());
    }
    
    public boolean isHurt() {
        return this.getHealth() > 0.0f && this.getHealth() < this.getMaxHealth();
    }
    
    public boolean mayBuild() {
        return this.abilities.mayBuild;
    }
    
    public boolean mayUseItemAt(final BlockPos ew, final Direction fb, final ItemStack bcj) {
        if (this.abilities.mayBuild) {
            return true;
        }
        final BlockPos ew2 = ew.relative(fb.getOpposite());
        final BlockInWorld bvx6 = new BlockInWorld(this.level, ew2, false);
        return bcj.hasAdventureModePlaceTagForBlock(this.level.getTagManager(), bvx6);
    }
    
    @Override
    protected int getExperienceReward(final Player awg) {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || this.isSpectator()) {
            return 0;
        }
        final int integer3 = this.experienceLevel * 7;
        if (integer3 > 100) {
            return 100;
        }
        return integer3;
    }
    
    @Override
    protected boolean isAlwaysExperienceDropper() {
        return true;
    }
    
    @Override
    public boolean shouldShowName() {
        return true;
    }
    
    @Override
    protected boolean makeStepSound() {
        return !this.abilities.flying;
    }
    
    public void onUpdateAbilities() {
    }
    
    public void setGameMode(final GameType bho) {
    }
    
    @Override
    public Component getName() {
        return new TextComponent(this.gameProfile.getName());
    }
    
    public PlayerEnderChestContainer getEnderChestInventory() {
        return this.enderChestInventory;
    }
    
    @Override
    public ItemStack getItemBySlot(final EquipmentSlot ait) {
        if (ait == EquipmentSlot.MAINHAND) {
            return this.inventory.getSelected();
        }
        if (ait == EquipmentSlot.OFFHAND) {
            return this.inventory.offhand.get(0);
        }
        if (ait.getType() == EquipmentSlot.Type.ARMOR) {
            return this.inventory.armor.get(ait.getIndex());
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public void setItemSlot(final EquipmentSlot ait, final ItemStack bcj) {
        if (ait == EquipmentSlot.MAINHAND) {
            this.playEquipSound(bcj);
            this.inventory.items.set(this.inventory.selected, bcj);
        }
        else if (ait == EquipmentSlot.OFFHAND) {
            this.playEquipSound(bcj);
            this.inventory.offhand.set(0, bcj);
        }
        else if (ait.getType() == EquipmentSlot.Type.ARMOR) {
            this.playEquipSound(bcj);
            this.inventory.armor.set(ait.getIndex(), bcj);
        }
    }
    
    public boolean addItem(final ItemStack bcj) {
        this.playEquipSound(bcj);
        return this.inventory.add(bcj);
    }
    
    @Override
    public Iterable<ItemStack> getHandSlots() {
        return (Iterable<ItemStack>)Lists.newArrayList((Object[])new ItemStack[] { this.getMainHandItem(), this.getOffhandItem() });
    }
    
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return (Iterable<ItemStack>)this.inventory.armor;
    }
    
    public boolean setEntityOnShoulder(final CompoundTag id) {
        if (this.isPassenger() || !this.onGround || this.isInWater()) {
            return false;
        }
        if (this.getShoulderEntityLeft().isEmpty()) {
            this.setShoulderEntityLeft(id);
            this.timeEntitySatOnShoulder = this.level.getGameTime();
            return true;
        }
        if (this.getShoulderEntityRight().isEmpty()) {
            this.setShoulderEntityRight(id);
            this.timeEntitySatOnShoulder = this.level.getGameTime();
            return true;
        }
        return false;
    }
    
    protected void removeEntitiesOnShoulder() {
        if (this.timeEntitySatOnShoulder + 20L < this.level.getGameTime()) {
            this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new CompoundTag());
            this.respawnEntityOnShoulder(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new CompoundTag());
        }
    }
    
    private void respawnEntityOnShoulder(final CompoundTag id) {
        if (!this.level.isClientSide && !id.isEmpty()) {
            EntityType.create(id, this.level).ifPresent(aio -> {
                if (aio instanceof TamableAnimal) {
                    ((TamableAnimal)aio).setOwnerUUID(this.uuid);
                }
                aio.setPos(this.x, this.y + 0.699999988079071, this.z);
                ((ServerLevel)this.level).addWithUUID(aio);
            });
        }
    }
    
    @Override
    public boolean isInvisibleTo(final Player awg) {
        if (!this.isInvisible()) {
            return false;
        }
        if (awg.isSpectator()) {
            return false;
        }
        final Team ctk3 = this.getTeam();
        return ctk3 == null || awg == null || awg.getTeam() != ctk3 || !ctk3.canSeeFriendlyInvisibles();
    }
    
    @Override
    public abstract boolean isSpectator();
    
    @Override
    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }
    
    public abstract boolean isCreative();
    
    @Override
    public boolean isPushedByWater() {
        return !this.abilities.flying;
    }
    
    public Scoreboard getScoreboard() {
        return this.level.getScoreboard();
    }
    
    @Override
    public Component getDisplayName() {
        final Component jo2 = PlayerTeam.formatNameForTeam(this.getTeam(), this.getName());
        return this.decorateDisplayNameComponent(jo2);
    }
    
    public Component getDisplayNameWithUuid() {
        return new TextComponent("").append(this.getName()).append(" (").append(this.gameProfile.getId().toString()).append(")");
    }
    
    private Component decorateDisplayNameComponent(final Component jo) {
        final String string3 = this.getGameProfile().getName();
        return jo.withStyle((Consumer<Style>)(jw -> jw.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + string3 + " ")).setHoverEvent(this.createHoverEvent()).setInsertion(string3)));
    }
    
    @Override
    public String getScoreboardName() {
        return this.getGameProfile().getName();
    }
    
    public float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        switch (ajh) {
            case SWIMMING:
            case FALL_FLYING:
            case SPIN_ATTACK: {
                return 0.4f;
            }
            case SNEAKING: {
                return 1.27f;
            }
            default: {
                return 1.62f;
            }
        }
    }
    
    @Override
    public void setAbsorptionAmount(float float1) {
        if (float1 < 0.0f) {
            float1 = 0.0f;
        }
        this.getEntityData().<Float>set(Player.DATA_PLAYER_ABSORPTION_ID, float1);
    }
    
    @Override
    public float getAbsorptionAmount() {
        return this.getEntityData().<Float>get(Player.DATA_PLAYER_ABSORPTION_ID);
    }
    
    public static UUID createPlayerUUID(final GameProfile gameProfile) {
        UUID uUID2 = gameProfile.getId();
        if (uUID2 == null) {
            uUID2 = createPlayerUUID(gameProfile.getName());
        }
        return uUID2;
    }
    
    public static UUID createPlayerUUID(final String string) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(StandardCharsets.UTF_8));
    }
    
    public boolean isModelPartShown(final PlayerModelPart awh) {
        return (this.getEntityData().<Byte>get(Player.DATA_PLAYER_MODE_CUSTOMISATION) & awh.getMask()) == awh.getMask();
    }
    
    @Override
    public boolean setSlot(final int integer, final ItemStack bcj) {
        if (integer >= 0 && integer < this.inventory.items.size()) {
            this.inventory.setItem(integer, bcj);
            return true;
        }
        EquipmentSlot ait4;
        if (integer == 100 + EquipmentSlot.HEAD.getIndex()) {
            ait4 = EquipmentSlot.HEAD;
        }
        else if (integer == 100 + EquipmentSlot.CHEST.getIndex()) {
            ait4 = EquipmentSlot.CHEST;
        }
        else if (integer == 100 + EquipmentSlot.LEGS.getIndex()) {
            ait4 = EquipmentSlot.LEGS;
        }
        else if (integer == 100 + EquipmentSlot.FEET.getIndex()) {
            ait4 = EquipmentSlot.FEET;
        }
        else {
            ait4 = null;
        }
        if (integer == 98) {
            this.setItemSlot(EquipmentSlot.MAINHAND, bcj);
            return true;
        }
        if (integer == 99) {
            this.setItemSlot(EquipmentSlot.OFFHAND, bcj);
            return true;
        }
        if (ait4 != null) {
            if (!bcj.isEmpty()) {
                if (bcj.getItem() instanceof ArmorItem || bcj.getItem() instanceof ElytraItem) {
                    if (Mob.getEquipmentSlotForItem(bcj) != ait4) {
                        return false;
                    }
                }
                else if (ait4 != EquipmentSlot.HEAD) {
                    return false;
                }
            }
            this.inventory.setItem(ait4.getIndex() + this.inventory.items.size(), bcj);
            return true;
        }
        final int integer2 = integer - 200;
        if (integer2 >= 0 && integer2 < this.enderChestInventory.getContainerSize()) {
            this.enderChestInventory.setItem(integer2, bcj);
            return true;
        }
        return false;
    }
    
    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }
    
    public void setReducedDebugInfo(final boolean boolean1) {
        this.reducedDebugInfo = boolean1;
    }
    
    @Override
    public HumanoidArm getMainArm() {
        return (this.entityData.<Byte>get(Player.DATA_PLAYER_MAIN_HAND) == 0) ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }
    
    public void setMainArm(final HumanoidArm aiw) {
        this.entityData.<Byte>set(Player.DATA_PLAYER_MAIN_HAND, (byte)((aiw != HumanoidArm.LEFT) ? 1 : 0));
    }
    
    public CompoundTag getShoulderEntityLeft() {
        return this.entityData.<CompoundTag>get(Player.DATA_SHOULDER_LEFT);
    }
    
    protected void setShoulderEntityLeft(final CompoundTag id) {
        this.entityData.<CompoundTag>set(Player.DATA_SHOULDER_LEFT, id);
    }
    
    public CompoundTag getShoulderEntityRight() {
        return this.entityData.<CompoundTag>get(Player.DATA_SHOULDER_RIGHT);
    }
    
    protected void setShoulderEntityRight(final CompoundTag id) {
        this.entityData.<CompoundTag>set(Player.DATA_SHOULDER_RIGHT, id);
    }
    
    public float getCurrentItemAttackStrengthDelay() {
        return (float)(1.0 / this.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getValue() * 20.0);
    }
    
    public float getAttackStrengthScale(final float float1) {
        return Mth.clamp((this.attackStrengthTicker + float1) / this.getCurrentItemAttackStrengthDelay(), 0.0f, 1.0f);
    }
    
    public void resetAttackStrengthTicker() {
        this.attackStrengthTicker = 0;
    }
    
    public ItemCooldowns getCooldowns() {
        return this.cooldowns;
    }
    
    public float getLuck() {
        return (float)this.getAttribute(SharedMonsterAttributes.LUCK).getValue();
    }
    
    public boolean canUseGameMasterBlocks() {
        return this.abilities.instabuild && this.getPermissionLevel() >= 2;
    }
    
    @Override
    public boolean canTakeItem(final ItemStack bcj) {
        final EquipmentSlot ait3 = Mob.getEquipmentSlotForItem(bcj);
        return this.getItemBySlot(ait3).isEmpty();
    }
    
    @Override
    public EntityDimensions getDimensions(final Pose ajh) {
        return (EntityDimensions)Player.POSES.getOrDefault(ajh, Player.STANDING_DIMENSIONS);
    }
    
    @Override
    public ItemStack getProjectile(final ItemStack bcj) {
        if (!(bcj.getItem() instanceof ProjectileWeaponItem)) {
            return ItemStack.EMPTY;
        }
        Predicate<ItemStack> predicate3 = ((ProjectileWeaponItem)bcj.getItem()).getSupportedHeldProjectiles();
        final ItemStack bcj2 = ProjectileWeaponItem.getHeldProjectile(this, predicate3);
        if (!bcj2.isEmpty()) {
            return bcj2;
        }
        predicate3 = ((ProjectileWeaponItem)bcj.getItem()).getAllSupportedProjectiles();
        for (int integer5 = 0; integer5 < this.inventory.getContainerSize(); ++integer5) {
            final ItemStack bcj3 = this.inventory.getItem(integer5);
            if (predicate3.test(bcj3)) {
                return bcj3;
            }
        }
        return this.abilities.instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
    }
    
    @Override
    public ItemStack eat(final Level bhr, final ItemStack bcj) {
        this.getFoodData().eat(bcj.getItem(), bcj);
        this.awardStat(Stats.ITEM_USED.get(bcj.getItem()));
        bhr.playSound(null, this.x, this.y, this.z, SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5f, bhr.random.nextFloat() * 0.1f + 0.9f);
        if (this instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)this, bcj);
        }
        return super.eat(bhr, bcj);
    }
    
    static {
        STANDING_DIMENSIONS = EntityDimensions.scalable(0.6f, 1.8f);
        POSES = (Map)ImmutableMap.builder().put(Pose.STANDING, Player.STANDING_DIMENSIONS).put(Pose.SLEEPING, Player.SLEEPING_DIMENSIONS).put(Pose.FALL_FLYING, EntityDimensions.scalable(0.6f, 0.6f)).put(Pose.SWIMMING, EntityDimensions.scalable(0.6f, 0.6f)).put(Pose.SPIN_ATTACK, EntityDimensions.scalable(0.6f, 0.6f)).put(Pose.SNEAKING, EntityDimensions.scalable(0.6f, 1.5f)).put(Pose.DYING, EntityDimensions.fixed(0.2f, 0.2f)).build();
        DATA_PLAYER_ABSORPTION_ID = SynchedEntityData.<Float>defineId(Player.class, EntityDataSerializers.FLOAT);
        DATA_SCORE_ID = SynchedEntityData.<Integer>defineId(Player.class, EntityDataSerializers.INT);
        DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.<Byte>defineId(Player.class, EntityDataSerializers.BYTE);
        DATA_PLAYER_MAIN_HAND = SynchedEntityData.<Byte>defineId(Player.class, EntityDataSerializers.BYTE);
        DATA_SHOULDER_LEFT = SynchedEntityData.<CompoundTag>defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
        DATA_SHOULDER_RIGHT = SynchedEntityData.<CompoundTag>defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
    }
    
    public enum BedSleepingProblem {
        NOT_POSSIBLE_HERE, 
        NOT_POSSIBLE_NOW((Component)new TranslatableComponent("block.minecraft.bed.no_sleep", new Object[0])), 
        TOO_FAR_AWAY((Component)new TranslatableComponent("block.minecraft.bed.too_far_away", new Object[0])), 
        OBSTRUCTED((Component)new TranslatableComponent("block.minecraft.bed.obstructed", new Object[0])), 
        OTHER_PROBLEM, 
        NOT_SAFE((Component)new TranslatableComponent("block.minecraft.bed.not_safe", new Object[0]));
        
        @Nullable
        private final Component message;
        
        private BedSleepingProblem() {
            this.message = null;
        }
        
        private BedSleepingProblem(final Component jo) {
            this.message = jo;
        }
        
        @Nullable
        public Component getMessage() {
            return this.message;
        }
    }
}
