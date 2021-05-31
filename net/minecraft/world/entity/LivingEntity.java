package net.minecraft.world.entity;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.world.item.UseAnim;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.network.protocol.game.ClientboundSetEquippedItemPacket;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.ai.attributes.ModifiableAttributeMap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.animal.Wolf;
import java.util.function.Consumer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.PotionUtils;
import java.util.ConcurrentModificationException;
import net.minecraft.world.scores.PlayerTeam;
import java.util.Iterator;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.sounds.SoundEvents;
import java.util.Random;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import com.google.common.base.Objects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import com.google.common.collect.Maps;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.damagesource.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import java.util.Map;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.core.BlockPos;
import java.util.Optional;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.UUID;

public abstract class LivingEntity extends Entity {
    private static final UUID SPEED_MODIFIER_SPRINTING_UUID;
    private static final AttributeModifier SPEED_MODIFIER_SPRINTING;
    protected static final EntityDataAccessor<Byte> DATA_LIVING_ENTITY_FLAGS;
    private static final EntityDataAccessor<Float> DATA_HEALTH_ID;
    private static final EntityDataAccessor<Integer> DATA_EFFECT_COLOR_ID;
    private static final EntityDataAccessor<Boolean> DATA_EFFECT_AMBIENCE_ID;
    private static final EntityDataAccessor<Integer> DATA_ARROW_COUNT_ID;
    private static final EntityDataAccessor<Optional<BlockPos>> SLEEPING_POS_ID;
    protected static final EntityDimensions SLEEPING_DIMENSIONS;
    private BaseAttributeMap attributes;
    private final CombatTracker combatTracker;
    private final Map<MobEffect, MobEffectInstance> activeEffects;
    private final NonNullList<ItemStack> lastHandItemStacks;
    private final NonNullList<ItemStack> lastArmorItemStacks;
    public boolean swinging;
    public InteractionHand swingingArm;
    public int swingTime;
    public int removeArrowTime;
    public int hurtTime;
    public int hurtDuration;
    public float hurtDir;
    public int deathTime;
    public float oAttackAnim;
    public float attackAnim;
    protected int attackStrengthTicker;
    public float animationSpeedOld;
    public float animationSpeed;
    public float animationPosition;
    public final int invulnerableDuration = 20;
    public final float timeOffs;
    public final float rotA;
    public float yBodyRot;
    public float yBodyRotO;
    public float yHeadRot;
    public float yHeadRotO;
    public float flyingSpeed;
    protected Player lastHurtByPlayer;
    protected int lastHurtByPlayerTime;
    protected boolean dead;
    protected int noActionTime;
    protected float oRun;
    protected float run;
    protected float animStep;
    protected float animStepO;
    protected float rotOffs;
    protected int deathScore;
    protected float lastHurt;
    protected boolean jumping;
    public float xxa;
    public float yya;
    public float zza;
    public float yRotA;
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;
    protected double lyHeadRot;
    protected int lerpHeadSteps;
    private boolean effectsDirty;
    @Nullable
    private LivingEntity lastHurtByMob;
    private int lastHurtByMobTimestamp;
    private LivingEntity lastHurtMob;
    private int lastHurtMobTimestamp;
    private float speed;
    private int noJumpDelay;
    private float absorptionAmount;
    protected ItemStack useItem;
    protected int useItemRemaining;
    protected int fallFlyTicks;
    private BlockPos lastPos;
    private DamageSource lastDamageSource;
    private long lastDamageStamp;
    protected int autoSpinAttackTicks;
    private float swimAmount;
    private float swimAmountO;
    protected Brain<?> brain;
    
    protected LivingEntity(final EntityType<? extends LivingEntity> ais, final Level bhr) {
        super(ais, bhr);
        this.combatTracker = new CombatTracker(this);
        this.activeEffects = (Map<MobEffect, MobEffectInstance>)Maps.newHashMap();
        this.lastHandItemStacks = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
        this.lastArmorItemStacks = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
        this.flyingSpeed = 0.02f;
        this.effectsDirty = true;
        this.useItem = ItemStack.EMPTY;
        this.registerAttributes();
        this.setHealth(this.getMaxHealth());
        this.blocksBuilding = true;
        this.rotA = (float)((Math.random() + 1.0) * 0.009999999776482582);
        this.setPos(this.x, this.y, this.z);
        this.timeOffs = (float)Math.random() * 12398.0f;
        this.yRot = (float)(Math.random() * 6.2831854820251465);
        this.yHeadRot = this.yRot;
        this.maxUpStep = 0.6f;
        this.brain = this.makeBrain(new Dynamic((DynamicOps)NbtOps.INSTANCE, new CompoundTag()));
    }
    
    public Brain<?> getBrain() {
        return this.brain;
    }
    
    protected Brain<?> makeBrain(final Dynamic<?> dynamic) {
        return new Brain<>((Collection<MemoryModuleType<?>>)ImmutableList.of(), (java.util.Collection<SensorType<? extends Sensor<?>>>)ImmutableList.of(), dynamic);
    }
    
    @Override
    public void kill() {
        this.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }
    
    public boolean canAttackType(final EntityType<?> ais) {
        return true;
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.<Byte>define(LivingEntity.DATA_LIVING_ENTITY_FLAGS, (Byte)0);
        this.entityData.<Integer>define(LivingEntity.DATA_EFFECT_COLOR_ID, 0);
        this.entityData.<Boolean>define(LivingEntity.DATA_EFFECT_AMBIENCE_ID, false);
        this.entityData.<Integer>define(LivingEntity.DATA_ARROW_COUNT_ID, 0);
        this.entityData.<Float>define(LivingEntity.DATA_HEALTH_ID, 1.0f);
        this.entityData.<Optional<BlockPos>>define(LivingEntity.SLEEPING_POS_ID, (Optional<BlockPos>)Optional.empty());
    }
    
    protected void registerAttributes() {
        this.getAttributes().registerAttribute(SharedMonsterAttributes.MAX_HEALTH);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ARMOR);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS);
    }
    
    @Override
    protected void checkFallDamage(final double double1, final boolean boolean2, final BlockState bvt, final BlockPos ew) {
        if (!this.isInWater()) {
            this.updateInWaterState();
        }
        if (!this.level.isClientSide && this.fallDistance > 3.0f && boolean2) {
            final float float7 = (float)Mth.ceil(this.fallDistance - 3.0f);
            if (!bvt.isAir()) {
                final double double2 = Math.min((double)(0.2f + float7 / 15.0f), 2.5);
                final int integer10 = (int)(150.0 * double2);
                ((ServerLevel)this.level).<BlockParticleOption>sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, bvt), this.x, this.y, this.z, integer10, 0.0, 0.0, 0.0, 0.15000000596046448);
            }
        }
        super.checkFallDamage(double1, boolean2, bvt, ew);
    }
    
    public boolean canBreatheUnderwater() {
        return this.getMobType() == MobType.UNDEAD;
    }
    
    public float getSwimAmount(final float float1) {
        return Mth.lerp(float1, this.swimAmountO, this.swimAmount);
    }
    
    @Override
    public void baseTick() {
        this.oAttackAnim = this.attackAnim;
        if (this.firstTick) {
            this.getSleepingPos().ifPresent(this::setPosToBed);
        }
        super.baseTick();
        this.level.getProfiler().push("livingEntityBaseTick");
        final boolean boolean2 = this instanceof Player;
        if (this.isAlive()) {
            if (this.isInWall()) {
                this.hurt(DamageSource.IN_WALL, 1.0f);
            }
            else if (boolean2 && !this.level.getWorldBorder().isWithinBounds(this.getBoundingBox())) {
                final double double3 = this.level.getWorldBorder().getDistanceToBorder(this) + this.level.getWorldBorder().getDamageSafeZone();
                if (double3 < 0.0) {
                    final double double4 = this.level.getWorldBorder().getDamagePerBlock();
                    if (double4 > 0.0) {
                        this.hurt(DamageSource.IN_WALL, (float)Math.max(1, Mth.floor(-double3 * double4)));
                    }
                }
            }
        }
        if (this.fireImmune() || this.level.isClientSide) {
            this.clearFire();
        }
        final boolean boolean3 = boolean2 && ((Player)this).abilities.invulnerable;
        if (this.isAlive()) {
            if (this.isUnderLiquid(FluidTags.WATER) && this.level.getBlockState(new BlockPos(this.x, this.y + this.getEyeHeight(), this.z)).getBlock() != Blocks.BUBBLE_COLUMN) {
                if (!this.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(this) && !boolean3) {
                    this.setAirSupply(this.decreaseAirSupply(this.getAirSupply()));
                    if (this.getAirSupply() == -20) {
                        this.setAirSupply(0);
                        final Vec3 csi4 = this.getDeltaMovement();
                        for (int integer5 = 0; integer5 < 8; ++integer5) {
                            final float float6 = this.random.nextFloat() - this.random.nextFloat();
                            final float float7 = this.random.nextFloat() - this.random.nextFloat();
                            final float float8 = this.random.nextFloat() - this.random.nextFloat();
                            this.level.addParticle(ParticleTypes.BUBBLE, this.x + float6, this.y + float7, this.z + float8, csi4.x, csi4.y, csi4.z);
                        }
                        this.hurt(DamageSource.DROWN, 2.0f);
                    }
                }
                if (!this.level.isClientSide && this.isPassenger() && this.getVehicle() != null && !this.getVehicle().rideableUnderWater()) {
                    this.stopRiding();
                }
            }
            else if (this.getAirSupply() < this.getMaxAirSupply()) {
                this.setAirSupply(this.increaseAirSupply(this.getAirSupply()));
            }
            if (!this.level.isClientSide) {
                final BlockPos ew4 = new BlockPos(this);
                if (!Objects.equal(this.lastPos, ew4)) {
                    this.onChangedBlock(this.lastPos = ew4);
                }
            }
        }
        if (this.isAlive() && this.isInWaterRainOrBubble()) {
            this.clearFire();
        }
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.invulnerableTime > 0 && !(this instanceof ServerPlayer)) {
            --this.invulnerableTime;
        }
        if (this.getHealth() <= 0.0f) {
            this.tickDeath();
        }
        if (this.lastHurtByPlayerTime > 0) {
            --this.lastHurtByPlayerTime;
        }
        else {
            this.lastHurtByPlayer = null;
        }
        if (this.lastHurtMob != null && !this.lastHurtMob.isAlive()) {
            this.lastHurtMob = null;
        }
        if (this.lastHurtByMob != null) {
            if (!this.lastHurtByMob.isAlive()) {
                this.setLastHurtByMob(null);
            }
            else if (this.tickCount - this.lastHurtByMobTimestamp > 100) {
                this.setLastHurtByMob(null);
            }
        }
        this.tickEffects();
        this.animStepO = this.animStep;
        this.yBodyRotO = this.yBodyRot;
        this.yHeadRotO = this.yHeadRot;
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
        this.level.getProfiler().pop();
    }
    
    protected void onChangedBlock(final BlockPos ew) {
        final int integer3 = EnchantmentHelper.getEnchantmentLevel(Enchantments.FROST_WALKER, this);
        if (integer3 > 0) {
            FrostWalkerEnchantment.onEntityMoved(this, this.level, ew, integer3);
        }
    }
    
    public boolean isBaby() {
        return false;
    }
    
    public float getScale() {
        return this.isBaby() ? 0.5f : 1.0f;
    }
    
    @Override
    public boolean rideableUnderWater() {
        return false;
    }
    
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            if (!this.level.isClientSide && (this.isAlwaysExperienceDropper() || (this.lastHurtByPlayerTime > 0 && this.shouldDropExperience() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)))) {
                int integer2 = this.getExperienceReward(this.lastHurtByPlayer);
                while (integer2 > 0) {
                    final int integer3 = ExperienceOrb.getExperienceValue(integer2);
                    integer2 -= integer3;
                    this.level.addFreshEntity(new ExperienceOrb(this.level, this.x, this.y, this.z, integer3));
                }
            }
            this.remove();
            for (int integer2 = 0; integer2 < 20; ++integer2) {
                final double double3 = this.random.nextGaussian() * 0.02;
                final double double4 = this.random.nextGaussian() * 0.02;
                final double double5 = this.random.nextGaussian() * 0.02;
                this.level.addParticle(ParticleTypes.POOF, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), this.y + this.random.nextFloat() * this.getBbHeight(), this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth(), double3, double4, double5);
            }
        }
    }
    
    protected boolean shouldDropExperience() {
        return !this.isBaby();
    }
    
    protected int decreaseAirSupply(final int integer) {
        final int integer2 = EnchantmentHelper.getRespiration(this);
        if (integer2 > 0 && this.random.nextInt(integer2 + 1) > 0) {
            return integer;
        }
        return integer - 1;
    }
    
    protected int increaseAirSupply(final int integer) {
        return Math.min(integer + 4, this.getMaxAirSupply());
    }
    
    protected int getExperienceReward(final Player awg) {
        return 0;
    }
    
    protected boolean isAlwaysExperienceDropper() {
        return false;
    }
    
    public Random getRandom() {
        return this.random;
    }
    
    @Nullable
    public LivingEntity getLastHurtByMob() {
        return this.lastHurtByMob;
    }
    
    public int getLastHurtByMobTimestamp() {
        return this.lastHurtByMobTimestamp;
    }
    
    public void setLastHurtByMob(@Nullable final LivingEntity aix) {
        this.lastHurtByMob = aix;
        this.lastHurtByMobTimestamp = this.tickCount;
    }
    
    @Nullable
    public LivingEntity getLastHurtMob() {
        return this.lastHurtMob;
    }
    
    public int getLastHurtMobTimestamp() {
        return this.lastHurtMobTimestamp;
    }
    
    public void setLastHurtMob(final Entity aio) {
        if (aio instanceof LivingEntity) {
            this.lastHurtMob = (LivingEntity)aio;
        }
        else {
            this.lastHurtMob = null;
        }
        this.lastHurtMobTimestamp = this.tickCount;
    }
    
    public int getNoActionTime() {
        return this.noActionTime;
    }
    
    public void setNoActionTime(final int integer) {
        this.noActionTime = integer;
    }
    
    protected void playEquipSound(final ItemStack bcj) {
        if (bcj.isEmpty()) {
            return;
        }
        SoundEvent yo3 = SoundEvents.ARMOR_EQUIP_GENERIC;
        final Item bce4 = bcj.getItem();
        if (bce4 instanceof ArmorItem) {
            yo3 = ((ArmorItem)bce4).getMaterial().getEquipSound();
        }
        else if (bce4 == Items.ELYTRA) {
            yo3 = SoundEvents.ARMOR_EQUIP_ELYTRA;
        }
        this.playSound(yo3, 1.0f, 1.0f);
    }
    
    public void addAdditionalSaveData(final CompoundTag id) {
        id.putFloat("Health", this.getHealth());
        id.putShort("HurtTime", (short)this.hurtTime);
        id.putInt("HurtByTimestamp", this.lastHurtByMobTimestamp);
        id.putShort("DeathTime", (short)this.deathTime);
        id.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
        for (final EquipmentSlot ait6 : EquipmentSlot.values()) {
            final ItemStack bcj7 = this.getItemBySlot(ait6);
            if (!bcj7.isEmpty()) {
                this.getAttributes().removeAttributeModifiers(bcj7.getAttributeModifiers(ait6));
            }
        }
        id.put("Attributes", (Tag)SharedMonsterAttributes.saveAttributes(this.getAttributes()));
        for (final EquipmentSlot ait6 : EquipmentSlot.values()) {
            final ItemStack bcj7 = this.getItemBySlot(ait6);
            if (!bcj7.isEmpty()) {
                this.getAttributes().addAttributeModifiers(bcj7.getAttributeModifiers(ait6));
            }
        }
        if (!this.activeEffects.isEmpty()) {
            final ListTag ik3 = new ListTag();
            for (final MobEffectInstance aii5 : this.activeEffects.values()) {
                ik3.add(aii5.save(new CompoundTag()));
            }
            id.put("ActiveEffects", (Tag)ik3);
        }
        id.putBoolean("FallFlying", this.isFallFlying());
        this.getSleepingPos().ifPresent(ew -> {
            id.putInt("SleepingX", ew.getX());
            id.putInt("SleepingY", ew.getY());
            id.putInt("SleepingZ", ew.getZ());
        });
        id.put("Brain", (Tag)this.brain.<Tag>serialize((com.mojang.datafixers.types.DynamicOps<Tag>)NbtOps.INSTANCE));
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
        this.setAbsorptionAmount(id.getFloat("AbsorptionAmount"));
        if (id.contains("Attributes", 9) && this.level != null && !this.level.isClientSide) {
            SharedMonsterAttributes.loadAttributes(this.getAttributes(), id.getList("Attributes", 10));
        }
        if (id.contains("ActiveEffects", 9)) {
            final ListTag ik3 = id.getList("ActiveEffects", 10);
            for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
                final CompoundTag id2 = ik3.getCompound(integer4);
                final MobEffectInstance aii6 = MobEffectInstance.load(id2);
                if (aii6 != null) {
                    this.activeEffects.put(aii6.getEffect(), aii6);
                }
            }
        }
        if (id.contains("Health", 99)) {
            this.setHealth(id.getFloat("Health"));
        }
        this.hurtTime = id.getShort("HurtTime");
        this.deathTime = id.getShort("DeathTime");
        this.lastHurtByMobTimestamp = id.getInt("HurtByTimestamp");
        if (id.contains("Team", 8)) {
            final String string3 = id.getString("Team");
            final PlayerTeam ctg4 = this.level.getScoreboard().getPlayerTeam(string3);
            final boolean boolean5 = ctg4 != null && this.level.getScoreboard().addPlayerToTeam(this.getStringUUID(), ctg4);
            if (!boolean5) {
                LivingEntity.LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", string3);
            }
        }
        if (id.getBoolean("FallFlying")) {
            this.setSharedFlag(7, true);
        }
        if (id.contains("SleepingX", 99) && id.contains("SleepingY", 99) && id.contains("SleepingZ", 99)) {
            final BlockPos ew3 = new BlockPos(id.getInt("SleepingX"), id.getInt("SleepingY"), id.getInt("SleepingZ"));
            this.setSleepingPos(ew3);
            this.entityData.<Pose>set(LivingEntity.DATA_POSE, Pose.SLEEPING);
            if (!this.firstTick) {
                this.setPosToBed(ew3);
            }
        }
        if (id.contains("Brain", 10)) {
            this.brain = this.makeBrain(new Dynamic((DynamicOps)NbtOps.INSTANCE, id.get("Brain")));
        }
    }
    
    protected void tickEffects() {
        final Iterator<MobEffect> iterator2 = (Iterator<MobEffect>)this.activeEffects.keySet().iterator();
        try {
            while (iterator2.hasNext()) {
                final MobEffect aig3 = (MobEffect)iterator2.next();
                final MobEffectInstance aii4 = (MobEffectInstance)this.activeEffects.get(aig3);
                if (!aii4.tick(this)) {
                    if (this.level.isClientSide) {
                        continue;
                    }
                    iterator2.remove();
                    this.onEffectRemoved(aii4);
                }
                else {
                    if (aii4.getDuration() % 600 != 0) {
                        continue;
                    }
                    this.onEffectUpdated(aii4, false);
                }
            }
        }
        catch (ConcurrentModificationException ex) {}
        if (this.effectsDirty) {
            if (!this.level.isClientSide) {
                this.updateInvisibilityStatus();
            }
            this.effectsDirty = false;
        }
        final int integer3 = this.entityData.<Integer>get(LivingEntity.DATA_EFFECT_COLOR_ID);
        final boolean boolean4 = this.entityData.<Boolean>get(LivingEntity.DATA_EFFECT_AMBIENCE_ID);
        if (integer3 > 0) {
            boolean boolean5;
            if (this.isInvisible()) {
                boolean5 = (this.random.nextInt(15) == 0);
            }
            else {
                boolean5 = this.random.nextBoolean();
            }
            if (boolean4) {
                boolean5 &= (this.random.nextInt(5) == 0);
            }
            if (boolean5 && integer3 > 0) {
                final double double6 = (integer3 >> 16 & 0xFF) / 255.0;
                final double double7 = (integer3 >> 8 & 0xFF) / 255.0;
                final double double8 = (integer3 >> 0 & 0xFF) / 255.0;
                this.level.addParticle(boolean4 ? ParticleTypes.AMBIENT_ENTITY_EFFECT : ParticleTypes.ENTITY_EFFECT, this.x + (this.random.nextDouble() - 0.5) * this.getBbWidth(), this.y + this.random.nextDouble() * this.getBbHeight(), this.z + (this.random.nextDouble() - 0.5) * this.getBbWidth(), double6, double7, double8);
            }
        }
    }
    
    protected void updateInvisibilityStatus() {
        if (this.activeEffects.isEmpty()) {
            this.removeEffectParticles();
            this.setInvisible(false);
        }
        else {
            final Collection<MobEffectInstance> collection2 = (Collection<MobEffectInstance>)this.activeEffects.values();
            this.entityData.<Boolean>set(LivingEntity.DATA_EFFECT_AMBIENCE_ID, areAllEffectsAmbient(collection2));
            this.entityData.<Integer>set(LivingEntity.DATA_EFFECT_COLOR_ID, PotionUtils.getColor(collection2));
            this.setInvisible(this.hasEffect(MobEffects.INVISIBILITY));
        }
    }
    
    public double getVisibilityPercent(@Nullable final Entity aio) {
        double double3 = 1.0;
        if (this.isSneaking()) {
            double3 *= 0.8;
        }
        if (this.isInvisible()) {
            float float5 = this.getArmorCoverPercentage();
            if (float5 < 0.1f) {
                float5 = 0.1f;
            }
            double3 *= 0.7 * float5;
        }
        if (aio != null) {
            final ItemStack bcj5 = this.getItemBySlot(EquipmentSlot.HEAD);
            final Item bce6 = bcj5.getItem();
            final EntityType<?> ais7 = aio.getType();
            if ((ais7 == EntityType.SKELETON && bce6 == Items.SKELETON_SKULL) || (ais7 == EntityType.ZOMBIE && bce6 == Items.ZOMBIE_HEAD) || (ais7 == EntityType.CREEPER && bce6 == Items.CREEPER_HEAD)) {
                double3 *= 0.5;
            }
        }
        return double3;
    }
    
    public boolean canAttack(final LivingEntity aix) {
        return true;
    }
    
    public boolean canAttack(final LivingEntity aix, final TargetingConditions aqi) {
        return aqi.test(this, aix);
    }
    
    public static boolean areAllEffectsAmbient(final Collection<MobEffectInstance> collection) {
        for (final MobEffectInstance aii3 : collection) {
            if (!aii3.isAmbient()) {
                return false;
            }
        }
        return true;
    }
    
    protected void removeEffectParticles() {
        this.entityData.<Boolean>set(LivingEntity.DATA_EFFECT_AMBIENCE_ID, false);
        this.entityData.<Integer>set(LivingEntity.DATA_EFFECT_COLOR_ID, 0);
    }
    
    public boolean removeAllEffects() {
        if (this.level.isClientSide) {
            return false;
        }
        final Iterator<MobEffectInstance> iterator2 = (Iterator<MobEffectInstance>)this.activeEffects.values().iterator();
        boolean boolean3 = false;
        while (iterator2.hasNext()) {
            this.onEffectRemoved((MobEffectInstance)iterator2.next());
            iterator2.remove();
            boolean3 = true;
        }
        return boolean3;
    }
    
    public Collection<MobEffectInstance> getActiveEffects() {
        return (Collection<MobEffectInstance>)this.activeEffects.values();
    }
    
    public Map<MobEffect, MobEffectInstance> getActiveEffectsMap() {
        return this.activeEffects;
    }
    
    public boolean hasEffect(final MobEffect aig) {
        return this.activeEffects.containsKey(aig);
    }
    
    @Nullable
    public MobEffectInstance getEffect(final MobEffect aig) {
        return (MobEffectInstance)this.activeEffects.get(aig);
    }
    
    public boolean addEffect(final MobEffectInstance aii) {
        if (!this.canBeAffected(aii)) {
            return false;
        }
        final MobEffectInstance aii2 = (MobEffectInstance)this.activeEffects.get(aii.getEffect());
        if (aii2 == null) {
            this.activeEffects.put(aii.getEffect(), aii);
            this.onEffectAdded(aii);
            return true;
        }
        if (aii2.update(aii)) {
            this.onEffectUpdated(aii2, true);
            return true;
        }
        return false;
    }
    
    public boolean canBeAffected(final MobEffectInstance aii) {
        if (this.getMobType() == MobType.UNDEAD) {
            final MobEffect aig3 = aii.getEffect();
            if (aig3 == MobEffects.REGENERATION || aig3 == MobEffects.POISON) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isInvertedHealAndHarm() {
        return this.getMobType() == MobType.UNDEAD;
    }
    
    @Nullable
    public MobEffectInstance removeEffectNoUpdate(@Nullable final MobEffect aig) {
        return (MobEffectInstance)this.activeEffects.remove(aig);
    }
    
    public boolean removeEffect(final MobEffect aig) {
        final MobEffectInstance aii3 = this.removeEffectNoUpdate(aig);
        if (aii3 != null) {
            this.onEffectRemoved(aii3);
            return true;
        }
        return false;
    }
    
    protected void onEffectAdded(final MobEffectInstance aii) {
        this.effectsDirty = true;
        if (!this.level.isClientSide) {
            aii.getEffect().addAttributeModifiers(this, this.getAttributes(), aii.getAmplifier());
        }
    }
    
    protected void onEffectUpdated(final MobEffectInstance aii, final boolean boolean2) {
        this.effectsDirty = true;
        if (boolean2 && !this.level.isClientSide) {
            final MobEffect aig4 = aii.getEffect();
            aig4.removeAttributeModifiers(this, this.getAttributes(), aii.getAmplifier());
            aig4.addAttributeModifiers(this, this.getAttributes(), aii.getAmplifier());
        }
    }
    
    protected void onEffectRemoved(final MobEffectInstance aii) {
        this.effectsDirty = true;
        if (!this.level.isClientSide) {
            aii.getEffect().removeAttributeModifiers(this, this.getAttributes(), aii.getAmplifier());
        }
    }
    
    public void heal(final float float1) {
        final float float2 = this.getHealth();
        if (float2 > 0.0f) {
            this.setHealth(float2 + float1);
        }
    }
    
    public float getHealth() {
        return this.entityData.<Float>get(LivingEntity.DATA_HEALTH_ID);
    }
    
    public void setHealth(final float float1) {
        this.entityData.<Float>set(LivingEntity.DATA_HEALTH_ID, Mth.clamp(float1, 0.0f, this.getMaxHealth()));
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        if (this.level.isClientSide) {
            return false;
        }
        if (this.getHealth() <= 0.0f) {
            return false;
        }
        if (ahx.isFire() && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        }
        if (this.isSleeping() && !this.level.isClientSide) {
            this.stopSleeping();
        }
        this.noActionTime = 0;
        final float float3 = float2;
        if ((ahx == DamageSource.ANVIL || ahx == DamageSource.FALLING_BLOCK) && !this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            this.getItemBySlot(EquipmentSlot.HEAD).<LivingEntity>hurtAndBreak((int)(float2 * 4.0f + this.random.nextFloat() * float2 * 2.0f), this, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent(EquipmentSlot.HEAD)));
            float2 *= 0.75f;
        }
        boolean boolean5 = false;
        float float4 = 0.0f;
        if (float2 > 0.0f && this.isDamageSourceBlocked(ahx)) {
            this.hurtCurrentlyUsedShield(float2);
            float4 = float2;
            float2 = 0.0f;
            if (!ahx.isProjectile()) {
                final Entity aio7 = ahx.getDirectEntity();
                if (aio7 instanceof LivingEntity) {
                    this.blockUsingShield((LivingEntity)aio7);
                }
            }
            boolean5 = true;
        }
        this.animationSpeed = 1.5f;
        boolean boolean6 = true;
        if (this.invulnerableTime > 10.0f) {
            if (float2 <= this.lastHurt) {
                return false;
            }
            this.actuallyHurt(ahx, float2 - this.lastHurt);
            this.lastHurt = float2;
            boolean6 = false;
        }
        else {
            this.lastHurt = float2;
            this.invulnerableTime = 20;
            this.actuallyHurt(ahx, float2);
            this.hurtDuration = 10;
            this.hurtTime = this.hurtDuration;
        }
        this.hurtDir = 0.0f;
        final Entity aio8 = ahx.getEntity();
        if (aio8 != null) {
            if (aio8 instanceof LivingEntity) {
                this.setLastHurtByMob((LivingEntity)aio8);
            }
            if (aio8 instanceof Player) {
                this.lastHurtByPlayerTime = 100;
                this.lastHurtByPlayer = (Player)aio8;
            }
            else if (aio8 instanceof Wolf) {
                final Wolf arz9 = (Wolf)aio8;
                if (arz9.isTame()) {
                    this.lastHurtByPlayerTime = 100;
                    final LivingEntity aix10 = arz9.getOwner();
                    if (aix10 != null && aix10.getType() == EntityType.PLAYER) {
                        this.lastHurtByPlayer = (Player)aix10;
                    }
                    else {
                        this.lastHurtByPlayer = null;
                    }
                }
            }
        }
        if (boolean6) {
            if (boolean5) {
                this.level.broadcastEntityEvent(this, (byte)29);
            }
            else if (ahx instanceof EntityDamageSource && ((EntityDamageSource)ahx).isThorns()) {
                this.level.broadcastEntityEvent(this, (byte)33);
            }
            else {
                byte byte9;
                if (ahx == DamageSource.DROWN) {
                    byte9 = 36;
                }
                else if (ahx.isFire()) {
                    byte9 = 37;
                }
                else if (ahx == DamageSource.SWEET_BERRY_BUSH) {
                    byte9 = 44;
                }
                else {
                    byte9 = 2;
                }
                this.level.broadcastEntityEvent(this, byte9);
            }
            if (ahx != DamageSource.DROWN && (!boolean5 || float2 > 0.0f)) {
                this.markHurt();
            }
            if (aio8 != null) {
                double double9;
                double double10;
                for (double9 = aio8.x - this.x, double10 = aio8.z - this.z; double9 * double9 + double10 * double10 < 1.0E-4; double9 = (Math.random() - Math.random()) * 0.01, double10 = (Math.random() - Math.random()) * 0.01) {}
                this.hurtDir = (float)(Mth.atan2(double10, double9) * 57.2957763671875 - this.yRot);
                this.knockback(aio8, 0.4f, double9, double10);
            }
            else {
                this.hurtDir = (float)((int)(Math.random() * 2.0) * 180);
            }
        }
        if (this.getHealth() <= 0.0f) {
            if (!this.checkTotemDeathProtection(ahx)) {
                final SoundEvent yo9 = this.getDeathSound();
                if (boolean6 && yo9 != null) {
                    this.playSound(yo9, this.getSoundVolume(), this.getVoicePitch());
                }
                this.die(ahx);
            }
        }
        else if (boolean6) {
            this.playHurtSound(ahx);
        }
        final boolean boolean7 = !boolean5 || float2 > 0.0f;
        if (boolean7) {
            this.lastDamageSource = ahx;
            this.lastDamageStamp = this.level.getGameTime();
        }
        if (this instanceof ServerPlayer) {
            CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayer)this, ahx, float3, float2, boolean5);
            if (float4 > 0.0f && float4 < 3.4028235E37f) {
                ((ServerPlayer)this).awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(float4 * 10.0f));
            }
        }
        if (aio8 instanceof ServerPlayer) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer)aio8, this, ahx, float3, float2, boolean5);
        }
        return boolean7;
    }
    
    protected void blockUsingShield(final LivingEntity aix) {
        aix.blockedByShield(this);
    }
    
    protected void blockedByShield(final LivingEntity aix) {
        aix.knockback(this, 0.5f, aix.x - this.x, aix.z - this.z);
    }
    
    private boolean checkTotemDeathProtection(final DamageSource ahx) {
        if (ahx.isBypassInvul()) {
            return false;
        }
        ItemStack bcj3 = null;
        for (final InteractionHand ahi8 : InteractionHand.values()) {
            final ItemStack bcj4 = this.getItemInHand(ahi8);
            if (bcj4.getItem() == Items.TOTEM_OF_UNDYING) {
                bcj3 = bcj4.copy();
                bcj4.shrink(1);
                break;
            }
        }
        if (bcj3 != null) {
            if (this instanceof ServerPlayer) {
                final ServerPlayer vl5 = (ServerPlayer)this;
                vl5.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                CriteriaTriggers.USED_TOTEM.trigger(vl5, bcj3);
            }
            this.setHealth(1.0f);
            this.removeAllEffects();
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            this.level.broadcastEntityEvent(this, (byte)35);
        }
        return bcj3 != null;
    }
    
    @Nullable
    public DamageSource getLastDamageSource() {
        if (this.level.getGameTime() - this.lastDamageStamp > 40L) {
            this.lastDamageSource = null;
        }
        return this.lastDamageSource;
    }
    
    protected void playHurtSound(final DamageSource ahx) {
        final SoundEvent yo3 = this.getHurtSound(ahx);
        if (yo3 != null) {
            this.playSound(yo3, this.getSoundVolume(), this.getVoicePitch());
        }
    }
    
    private boolean isDamageSourceBlocked(final DamageSource ahx) {
        final Entity aio3 = ahx.getDirectEntity();
        boolean boolean4 = false;
        if (aio3 instanceof AbstractArrow) {
            final AbstractArrow awk5 = (AbstractArrow)aio3;
            if (awk5.getPierceLevel() > 0) {
                boolean4 = true;
            }
        }
        if (!ahx.isBypassArmor() && this.isBlocking() && !boolean4) {
            final Vec3 csi5 = ahx.getSourcePosition();
            if (csi5 != null) {
                final Vec3 csi6 = this.getViewVector(1.0f);
                Vec3 csi7 = csi5.vectorTo(new Vec3(this.x, this.y, this.z)).normalize();
                csi7 = new Vec3(csi7.x, 0.0, csi7.z);
                if (csi7.dot(csi6) < 0.0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void breakItem(final ItemStack bcj) {
        if (!bcj.isEmpty()) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.ITEM_BREAK, this.getSoundSource(), 0.8f, 0.8f + this.level.random.nextFloat() * 0.4f, false);
            }
            this.spawnItemParticles(bcj, 5);
        }
    }
    
    public void die(final DamageSource ahx) {
        if (this.dead) {
            return;
        }
        final Entity aio3 = ahx.getEntity();
        final LivingEntity aix4 = this.getKillCredit();
        if (this.deathScore >= 0 && aix4 != null) {
            aix4.awardKillScore(this, this.deathScore, ahx);
        }
        if (aio3 != null) {
            aio3.killed(this);
        }
        if (this.isSleeping()) {
            this.stopSleeping();
        }
        this.dead = true;
        this.getCombatTracker().recheckStatus();
        if (!this.level.isClientSide) {
            this.dropAllDeathLoot(ahx);
            boolean boolean5 = false;
            if (aix4 instanceof WitherBoss) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    final BlockPos ew6 = new BlockPos(this.x, this.y, this.z);
                    final BlockState bvt7 = Blocks.WITHER_ROSE.defaultBlockState();
                    if (this.level.getBlockState(ew6).isAir() && bvt7.canSurvive(this.level, ew6)) {
                        this.level.setBlock(ew6, bvt7, 3);
                        boolean5 = true;
                    }
                }
                if (!boolean5) {
                    final ItemEntity atx6 = new ItemEntity(this.level, this.x, this.y, this.z, new ItemStack(Items.WITHER_ROSE));
                    this.level.addFreshEntity(atx6);
                }
            }
        }
        this.level.broadcastEntityEvent(this, (byte)3);
        this.setPose(Pose.DYING);
    }
    
    protected void dropAllDeathLoot(final DamageSource ahx) {
        final Entity aio3 = ahx.getEntity();
        int integer4;
        if (aio3 instanceof Player) {
            integer4 = EnchantmentHelper.getMobLooting((LivingEntity)aio3);
        }
        else {
            integer4 = 0;
        }
        final boolean boolean5 = this.lastHurtByPlayerTime > 0;
        if (this.shouldDropExperience() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.dropFromLootTable(ahx, boolean5);
            this.dropCustomDeathLoot(ahx, integer4, boolean5);
        }
        this.dropEquipment();
    }
    
    protected void dropEquipment() {
    }
    
    protected void dropCustomDeathLoot(final DamageSource ahx, final int integer, final boolean boolean3) {
    }
    
    public ResourceLocation getLootTable() {
        return this.getType().getDefaultLootTable();
    }
    
    protected void dropFromLootTable(final DamageSource ahx, final boolean boolean2) {
        final ResourceLocation qv4 = this.getLootTable();
        final LootTable cpb5 = this.level.getServer().getLootTables().get(qv4);
        final LootContext.Builder a6 = this.createLootContext(boolean2, ahx);
        cpb5.getRandomItems(a6.create(LootContextParamSets.ENTITY), (Consumer<ItemStack>)this::spawnAtLocation);
    }
    
    protected LootContext.Builder createLootContext(final boolean boolean1, final DamageSource ahx) {
        LootContext.Builder a4 = new LootContext.Builder((ServerLevel)this.level).withRandom(this.random).<Entity>withParameter(LootContextParams.THIS_ENTITY, this).<BlockPos>withParameter(LootContextParams.BLOCK_POS, new BlockPos(this)).<DamageSource>withParameter(LootContextParams.DAMAGE_SOURCE, ahx).<Entity>withOptionalParameter(LootContextParams.KILLER_ENTITY, ahx.getEntity()).<Entity>withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, ahx.getDirectEntity());
        if (boolean1 && this.lastHurtByPlayer != null) {
            a4 = a4.<Player>withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
        }
        return a4;
    }
    
    public void knockback(final Entity aio, final float float2, final double double3, final double double4) {
        if (this.random.nextDouble() < this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getValue()) {
            return;
        }
        this.hasImpulse = true;
        final Vec3 csi8 = this.getDeltaMovement();
        final Vec3 csi9 = new Vec3(double3, 0.0, double4).normalize().scale(float2);
        this.setDeltaMovement(csi8.x / 2.0 - csi9.x, this.onGround ? Math.min(0.4, csi8.y / 2.0 + float2) : csi8.y, csi8.z / 2.0 - csi9.z);
    }
    
    @Nullable
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.GENERIC_HURT;
    }
    
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }
    
    protected SoundEvent getFallDamageSound(final int integer) {
        if (integer > 4) {
            return SoundEvents.GENERIC_BIG_FALL;
        }
        return SoundEvents.GENERIC_SMALL_FALL;
    }
    
    protected SoundEvent getDrinkingSound(final ItemStack bcj) {
        return SoundEvents.GENERIC_DRINK;
    }
    
    public SoundEvent getEatingSound(final ItemStack bcj) {
        return SoundEvents.GENERIC_EAT;
    }
    
    public boolean onLadder() {
        if (this.isSpectator()) {
            return false;
        }
        final BlockState bvt2 = this.getFeetBlockState();
        final Block bmv3 = bvt2.getBlock();
        return bmv3 == Blocks.LADDER || bmv3 == Blocks.VINE || bmv3 == Blocks.SCAFFOLDING || (bmv3 instanceof TrapDoorBlock && this.trapdoorUsableAsLadder(new BlockPos(this), bvt2));
    }
    
    public BlockState getFeetBlockState() {
        return this.level.getBlockState(new BlockPos(this));
    }
    
    private boolean trapdoorUsableAsLadder(final BlockPos ew, final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)TrapDoorBlock.OPEN)) {
            final BlockState bvt2 = this.level.getBlockState(ew.below());
            if (bvt2.getBlock() == Blocks.LADDER && bvt2.<Comparable>getValue((Property<Comparable>)LadderBlock.FACING) == bvt.<Comparable>getValue((Property<Comparable>)TrapDoorBlock.FACING)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isAlive() {
        return !this.removed && this.getHealth() > 0.0f;
    }
    
    @Override
    public void causeFallDamage(final float float1, final float float2) {
        super.causeFallDamage(float1, float2);
        final MobEffectInstance aii4 = this.getEffect(MobEffects.JUMP);
        final float float3 = (aii4 == null) ? 0.0f : ((float)(aii4.getAmplifier() + 1));
        final int integer6 = Mth.ceil((float1 - 3.0f - float3) * float2);
        if (integer6 > 0) {
            this.playSound(this.getFallDamageSound(integer6), 1.0f, 1.0f);
            this.hurt(DamageSource.FALL, (float)integer6);
            final int integer7 = Mth.floor(this.x);
            final int integer8 = Mth.floor(this.y - 0.20000000298023224);
            final int integer9 = Mth.floor(this.z);
            final BlockState bvt10 = this.level.getBlockState(new BlockPos(integer7, integer8, integer9));
            if (!bvt10.isAir()) {
                final SoundType bry11 = bvt10.getSoundType();
                this.playSound(bry11.getFallSound(), bry11.getVolume() * 0.5f, bry11.getPitch() * 0.75f);
            }
        }
    }
    
    @Override
    public void animateHurt() {
        this.hurtDuration = 10;
        this.hurtTime = this.hurtDuration;
        this.hurtDir = 0.0f;
    }
    
    public int getArmorValue() {
        final AttributeInstance ajo2 = this.getAttribute(SharedMonsterAttributes.ARMOR);
        return Mth.floor(ajo2.getValue());
    }
    
    protected void hurtArmor(final float float1) {
    }
    
    protected void hurtCurrentlyUsedShield(final float float1) {
    }
    
    protected float getDamageAfterArmorAbsorb(final DamageSource ahx, float float2) {
        if (!ahx.isBypassArmor()) {
            this.hurtArmor(float2);
            float2 = CombatRules.getDamageAfterAbsorb(float2, (float)this.getArmorValue(), (float)this.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getValue());
        }
        return float2;
    }
    
    protected float getDamageAfterMagicAbsorb(final DamageSource ahx, float float2) {
        if (ahx.isBypassMagic()) {
            return float2;
        }
        if (this.hasEffect(MobEffects.DAMAGE_RESISTANCE) && ahx != DamageSource.OUT_OF_WORLD) {
            final int integer4 = (this.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1) * 5;
            final int integer5 = 25 - integer4;
            final float float3 = float2 * integer5;
            final float float4 = float2;
            float2 = Math.max(float3 / 25.0f, 0.0f);
            final float float5 = float4 - float2;
            if (float5 > 0.0f && float5 < 3.4028235E37f) {
                if (this instanceof ServerPlayer) {
                    ((ServerPlayer)this).awardStat(Stats.DAMAGE_RESISTED, Math.round(float5 * 10.0f));
                }
                else if (ahx.getEntity() instanceof ServerPlayer) {
                    ((ServerPlayer)ahx.getEntity()).awardStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(float5 * 10.0f));
                }
            }
        }
        if (float2 <= 0.0f) {
            return 0.0f;
        }
        final int integer4 = EnchantmentHelper.getDamageProtection(this.getArmorSlots(), ahx);
        if (integer4 > 0) {
            float2 = CombatRules.getDamageAfterMagicAbsorb(float2, (float)integer4);
        }
        return float2;
    }
    
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
        if (float4 > 0.0f && float4 < 3.4028235E37f && ahx.getEntity() instanceof ServerPlayer) {
            ((ServerPlayer)ahx.getEntity()).awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(float4 * 10.0f));
        }
        if (float2 == 0.0f) {
            return;
        }
        final float float5 = this.getHealth();
        this.setHealth(float5 - float2);
        this.getCombatTracker().recordDamage(ahx, float5, float2);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - float2);
    }
    
    public CombatTracker getCombatTracker() {
        return this.combatTracker;
    }
    
    @Nullable
    public LivingEntity getKillCredit() {
        if (this.combatTracker.getKiller() != null) {
            return this.combatTracker.getKiller();
        }
        if (this.lastHurtByPlayer != null) {
            return this.lastHurtByPlayer;
        }
        if (this.lastHurtByMob != null) {
            return this.lastHurtByMob;
        }
        return null;
    }
    
    public final float getMaxHealth() {
        return (float)this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();
    }
    
    public final int getArrowCount() {
        return this.entityData.<Integer>get(LivingEntity.DATA_ARROW_COUNT_ID);
    }
    
    public final void setArrowCount(final int integer) {
        this.entityData.<Integer>set(LivingEntity.DATA_ARROW_COUNT_ID, integer);
    }
    
    private int getCurrentSwingDuration() {
        if (MobEffectUtil.hasDigSpeed(this)) {
            return 6 - (1 + MobEffectUtil.getDigSpeedAmplification(this));
        }
        if (this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            return 6 + (1 + this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) * 2;
        }
        return 6;
    }
    
    public void swing(final InteractionHand ahi) {
        if (!this.swinging || this.swingTime >= this.getCurrentSwingDuration() / 2 || this.swingTime < 0) {
            this.swingTime = -1;
            this.swinging = true;
            this.swingingArm = ahi;
            if (this.level instanceof ServerLevel) {
                ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundAnimatePacket(this, (ahi == InteractionHand.MAIN_HAND) ? 0 : 3));
            }
        }
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        switch (byte1) {
            case 2:
            case 33:
            case 36:
            case 37:
            case 44: {
                final boolean boolean3 = byte1 == 33;
                final boolean boolean4 = byte1 == 36;
                final boolean boolean5 = byte1 == 37;
                final boolean boolean6 = byte1 == 44;
                this.animationSpeed = 1.5f;
                this.invulnerableTime = 20;
                this.hurtDuration = 10;
                this.hurtTime = this.hurtDuration;
                this.hurtDir = 0.0f;
                if (boolean3) {
                    this.playSound(SoundEvents.THORNS_HIT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                DamageSource ahx7;
                if (boolean5) {
                    ahx7 = DamageSource.ON_FIRE;
                }
                else if (boolean4) {
                    ahx7 = DamageSource.DROWN;
                }
                else if (boolean6) {
                    ahx7 = DamageSource.SWEET_BERRY_BUSH;
                }
                else {
                    ahx7 = DamageSource.GENERIC;
                }
                final SoundEvent yo8 = this.getHurtSound(ahx7);
                if (yo8 != null) {
                    this.playSound(yo8, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                this.hurt(DamageSource.GENERIC, 0.0f);
                break;
            }
            case 3: {
                final SoundEvent yo9 = this.getDeathSound();
                if (yo9 != null) {
                    this.playSound(yo9, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                this.setHealth(0.0f);
                this.die(DamageSource.GENERIC);
                break;
            }
            case 30: {
                this.playSound(SoundEvents.SHIELD_BREAK, 0.8f, 0.8f + this.level.random.nextFloat() * 0.4f);
                break;
            }
            case 29: {
                this.playSound(SoundEvents.SHIELD_BLOCK, 1.0f, 0.8f + this.level.random.nextFloat() * 0.4f);
                break;
            }
            case 46: {
                final int integer3 = 128;
                for (int integer4 = 0; integer4 < 128; ++integer4) {
                    final double double5 = integer4 / 127.0;
                    final float float7 = (this.random.nextFloat() - 0.5f) * 0.2f;
                    final float float8 = (this.random.nextFloat() - 0.5f) * 0.2f;
                    final float float9 = (this.random.nextFloat() - 0.5f) * 0.2f;
                    final double double6 = Mth.lerp(double5, this.xo, this.x) + (this.random.nextDouble() - 0.5) * this.getBbWidth() * 2.0;
                    final double double7 = Mth.lerp(double5, this.yo, this.y) + this.random.nextDouble() * this.getBbHeight();
                    final double double8 = Mth.lerp(double5, this.zo, this.z) + (this.random.nextDouble() - 0.5) * this.getBbWidth() * 2.0;
                    this.level.addParticle(ParticleTypes.PORTAL, double6, double7, double8, float7, float8, float9);
                }
                break;
            }
            case 47: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.MAINHAND));
                break;
            }
            case 48: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.OFFHAND));
                break;
            }
            case 49: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.HEAD));
                break;
            }
            case 50: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.CHEST));
                break;
            }
            case 51: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.LEGS));
                break;
            }
            case 52: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.FEET));
                break;
            }
            default: {
                super.handleEntityEvent(byte1);
                break;
            }
        }
    }
    
    @Override
    protected void outOfWorld() {
        this.hurt(DamageSource.OUT_OF_WORLD, 4.0f);
    }
    
    protected void updateSwingTime() {
        final int integer2 = this.getCurrentSwingDuration();
        if (this.swinging) {
            ++this.swingTime;
            if (this.swingTime >= integer2) {
                this.swingTime = 0;
                this.swinging = false;
            }
        }
        else {
            this.swingTime = 0;
        }
        this.attackAnim = this.swingTime / (float)integer2;
    }
    
    public AttributeInstance getAttribute(final Attribute ajn) {
        return this.getAttributes().getInstance(ajn);
    }
    
    public BaseAttributeMap getAttributes() {
        if (this.attributes == null) {
            this.attributes = new ModifiableAttributeMap();
        }
        return this.attributes;
    }
    
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }
    
    public ItemStack getMainHandItem() {
        return this.getItemBySlot(EquipmentSlot.MAINHAND);
    }
    
    public ItemStack getOffhandItem() {
        return this.getItemBySlot(EquipmentSlot.OFFHAND);
    }
    
    public ItemStack getItemInHand(final InteractionHand ahi) {
        if (ahi == InteractionHand.MAIN_HAND) {
            return this.getItemBySlot(EquipmentSlot.MAINHAND);
        }
        if (ahi == InteractionHand.OFF_HAND) {
            return this.getItemBySlot(EquipmentSlot.OFFHAND);
        }
        throw new IllegalArgumentException(new StringBuilder().append("Invalid hand ").append(ahi).toString());
    }
    
    public void setItemInHand(final InteractionHand ahi, final ItemStack bcj) {
        if (ahi == InteractionHand.MAIN_HAND) {
            this.setItemSlot(EquipmentSlot.MAINHAND, bcj);
        }
        else {
            if (ahi != InteractionHand.OFF_HAND) {
                throw new IllegalArgumentException(new StringBuilder().append("Invalid hand ").append(ahi).toString());
            }
            this.setItemSlot(EquipmentSlot.OFFHAND, bcj);
        }
    }
    
    public boolean hasItemInSlot(final EquipmentSlot ait) {
        return !this.getItemBySlot(ait).isEmpty();
    }
    
    @Override
    public abstract Iterable<ItemStack> getArmorSlots();
    
    public abstract ItemStack getItemBySlot(final EquipmentSlot ait);
    
    @Override
    public abstract void setItemSlot(final EquipmentSlot ait, final ItemStack bcj);
    
    public float getArmorCoverPercentage() {
        final Iterable<ItemStack> iterable2 = this.getArmorSlots();
        int integer3 = 0;
        int integer4 = 0;
        for (final ItemStack bcj6 : iterable2) {
            if (!bcj6.isEmpty()) {
                ++integer4;
            }
            ++integer3;
        }
        return (integer3 > 0) ? (integer4 / (float)integer3) : 0.0f;
    }
    
    @Override
    public void setSprinting(final boolean boolean1) {
        super.setSprinting(boolean1);
        final AttributeInstance ajo3 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (ajo3.getModifier(LivingEntity.SPEED_MODIFIER_SPRINTING_UUID) != null) {
            ajo3.removeModifier(LivingEntity.SPEED_MODIFIER_SPRINTING);
        }
        if (boolean1) {
            ajo3.addModifier(LivingEntity.SPEED_MODIFIER_SPRINTING);
        }
    }
    
    protected float getSoundVolume() {
        return 1.0f;
    }
    
    protected float getVoicePitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.5f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
    }
    
    protected boolean isImmobile() {
        return this.getHealth() <= 0.0f;
    }
    
    @Override
    public void push(final Entity aio) {
        if (!this.isSleeping()) {
            super.push(aio);
        }
    }
    
    public void findStandUpPosition(final Entity aio) {
        if (!(aio instanceof Boat) && !(aio instanceof AbstractHorse)) {
            double double4 = aio.x;
            double double5 = aio.getBoundingBox().minY + aio.getBbHeight();
            double double6 = aio.z;
            final Direction fb10 = aio.getMotionDirection();
            if (fb10 != null) {
                final Direction fb11 = fb10.getClockWise();
                final int[][] arr12 = { { 0, 1 }, { 0, -1 }, { -1, 1 }, { -1, -1 }, { 1, 1 }, { 1, -1 }, { -1, 0 }, { 1, 0 }, { 0, 1 } };
                final double double7 = Math.floor(this.x) + 0.5;
                final double double8 = Math.floor(this.z) + 0.5;
                final double double9 = this.getBoundingBox().maxX - this.getBoundingBox().minX;
                final double double10 = this.getBoundingBox().maxZ - this.getBoundingBox().minZ;
                final AABB csc21 = new AABB(double7 - double9 / 2.0, aio.getBoundingBox().minY, double8 - double10 / 2.0, double7 + double9 / 2.0, Math.floor(aio.getBoundingBox().minY) + this.getBbHeight(), double8 + double10 / 2.0);
                for (final int[] arr13 : arr12) {
                    final double double11 = fb10.getStepX() * arr13[0] + fb11.getStepX() * arr13[1];
                    final double double12 = fb10.getStepZ() * arr13[0] + fb11.getStepZ() * arr13[1];
                    final double double13 = double7 + double11;
                    final double double14 = double8 + double12;
                    final AABB csc22 = csc21.move(double11, 0.0, double12);
                    if (this.level.noCollision(this, csc22)) {
                        final BlockPos ew34 = new BlockPos(double13, this.y, double14);
                        if (this.level.getBlockState(ew34).entityCanStandOn(this.level, ew34, this)) {
                            this.teleportTo(double13, this.y + 1.0, double14);
                            return;
                        }
                        final BlockPos ew35 = new BlockPos(double13, this.y - 1.0, double14);
                        if (this.level.getBlockState(ew35).entityCanStandOn(this.level, ew35, this) || this.level.getFluidState(ew35).is(FluidTags.WATER)) {
                            double4 = double13;
                            double5 = this.y + 1.0;
                            double6 = double14;
                        }
                    }
                    else {
                        final BlockPos ew34 = new BlockPos(double13, this.y + 1.0, double14);
                        if (this.level.noCollision(this, csc22.move(0.0, 1.0, 0.0)) && this.level.getBlockState(ew34).entityCanStandOn(this.level, ew34, this)) {
                            double4 = double13;
                            double5 = this.y + 2.0;
                            double6 = double14;
                        }
                    }
                }
            }
            this.teleportTo(double4, double5, double6);
            return;
        }
        final double double15 = this.getBbWidth() / 2.0f + aio.getBbWidth() / 2.0f + 0.4;
        float float5;
        if (aio instanceof Boat) {
            float5 = 0.0f;
        }
        else {
            float5 = 1.5707964f * ((this.getMainArm() == HumanoidArm.RIGHT) ? -1 : 1);
        }
        final float float6 = -Mth.sin(-this.yRot * 0.017453292f - 3.1415927f + float5);
        final float float7 = -Mth.cos(-this.yRot * 0.017453292f - 3.1415927f + float5);
        double double6 = (Math.abs(float6) > Math.abs(float7)) ? (double15 / Math.abs(float6)) : (double15 / Math.abs(float7));
        final double double16 = this.x + float6 * double6;
        final double double17 = this.z + float7 * double6;
        this.setPos(double16, aio.y + aio.getBbHeight() + 0.001, double17);
        if (this.level.noCollision(this, this.getBoundingBox().minmax(aio.getBoundingBox()))) {
            return;
        }
        this.setPos(double16, aio.y + aio.getBbHeight() + 1.001, double17);
        if (this.level.noCollision(this, this.getBoundingBox().minmax(aio.getBoundingBox()))) {
            return;
        }
        this.setPos(aio.x, aio.y + this.getBbHeight() + 0.001, aio.z);
    }
    
    @Override
    public boolean shouldShowName() {
        return this.isCustomNameVisible();
    }
    
    protected float getJumpPower() {
        return 0.42f;
    }
    
    protected void jumpFromGround() {
        float float2;
        if (this.hasEffect(MobEffects.JUMP)) {
            float2 = this.getJumpPower() + 0.1f * (this.getEffect(MobEffects.JUMP).getAmplifier() + 1);
        }
        else {
            float2 = this.getJumpPower();
        }
        final Vec3 csi3 = this.getDeltaMovement();
        this.setDeltaMovement(csi3.x, float2, csi3.z);
        if (this.isSprinting()) {
            final float float3 = this.yRot * 0.017453292f;
            this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(float3) * 0.2f, 0.0, Mth.cos(float3) * 0.2f));
        }
        this.hasImpulse = true;
    }
    
    protected void goDownInWater() {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03999999910593033, 0.0));
    }
    
    protected void jumpInLiquid(final net.minecraft.tags.Tag<Fluid> zg) {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.03999999910593033, 0.0));
    }
    
    protected float getWaterSlowDown() {
        return 0.8f;
    }
    
    public void travel(final Vec3 csi) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            double double3 = 0.08;
            final boolean boolean5 = this.getDeltaMovement().y <= 0.0;
            if (boolean5 && this.hasEffect(MobEffects.SLOW_FALLING)) {
                double3 = 0.01;
                this.fallDistance = 0.0f;
            }
            if (this.isInWater() && (!(this instanceof Player) || !((Player)this).abilities.flying)) {
                final double double4 = this.y;
                float float8 = this.isSprinting() ? 0.9f : this.getWaterSlowDown();
                float float9 = 0.02f;
                float float10 = (float)EnchantmentHelper.getDepthStrider(this);
                if (float10 > 3.0f) {
                    float10 = 3.0f;
                }
                if (!this.onGround) {
                    float10 *= 0.5f;
                }
                if (float10 > 0.0f) {
                    float8 += (0.54600006f - float8) * float10 / 3.0f;
                    float9 += (this.getSpeed() - float9) * float10 / 3.0f;
                }
                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    float8 = 0.96f;
                }
                this.moveRelative(float9, csi);
                this.move(MoverType.SELF, this.getDeltaMovement());
                Vec3 csi2 = this.getDeltaMovement();
                if (this.horizontalCollision && this.onLadder()) {
                    csi2 = new Vec3(csi2.x, 0.2, csi2.z);
                }
                this.setDeltaMovement(csi2.multiply(float8, 0.800000011920929, float8));
                if (!this.isNoGravity() && !this.isSprinting()) {
                    final Vec3 csi3 = this.getDeltaMovement();
                    double double5;
                    if (boolean5 && Math.abs(csi3.y - 0.005) >= 0.003 && Math.abs(csi3.y - double3 / 16.0) < 0.003) {
                        double5 = -0.003;
                    }
                    else {
                        double5 = csi3.y - double3 / 16.0;
                    }
                    this.setDeltaMovement(csi3.x, double5, csi3.z);
                }
                final Vec3 csi3 = this.getDeltaMovement();
                if (this.horizontalCollision && this.isFree(csi3.x, csi3.y + 0.6000000238418579 - this.y + double4, csi3.z)) {
                    this.setDeltaMovement(csi3.x, 0.30000001192092896, csi3.z);
                }
            }
            else if (this.isInLava() && (!(this instanceof Player) || !((Player)this).abilities.flying)) {
                final double double4 = this.y;
                this.moveRelative(0.02f, csi);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, -double3 / 4.0, 0.0));
                }
                final Vec3 csi4 = this.getDeltaMovement();
                if (this.horizontalCollision && this.isFree(csi4.x, csi4.y + 0.6000000238418579 - this.y + double4, csi4.z)) {
                    this.setDeltaMovement(csi4.x, 0.30000001192092896, csi4.z);
                }
            }
            else if (this.isFallFlying()) {
                Vec3 csi5 = this.getDeltaMovement();
                if (csi5.y > -0.5) {
                    this.fallDistance = 1.0f;
                }
                final Vec3 csi6 = this.getLookAngle();
                final float float8 = this.xRot * 0.017453292f;
                final double double6 = Math.sqrt(csi6.x * csi6.x + csi6.z * csi6.z);
                final double double7 = Math.sqrt(Entity.getHorizontalDistanceSqr(csi5));
                final double double5 = csi6.length();
                float float11 = Mth.cos(float8);
                float11 *= (float)(float11 * Math.min(1.0, double5 / 0.4));
                csi5 = this.getDeltaMovement().add(0.0, double3 * (-1.0 + float11 * 0.75), 0.0);
                if (csi5.y < 0.0 && double6 > 0.0) {
                    final double double8 = csi5.y * -0.1 * float11;
                    csi5 = csi5.add(csi6.x * double8 / double6, double8, csi6.z * double8 / double6);
                }
                if (float8 < 0.0f && double6 > 0.0) {
                    final double double8 = double7 * -Mth.sin(float8) * 0.04;
                    csi5 = csi5.add(-csi6.x * double8 / double6, double8 * 3.2, -csi6.z * double8 / double6);
                }
                if (double6 > 0.0) {
                    csi5 = csi5.add((csi6.x / double6 * double7 - csi5.x) * 0.1, 0.0, (csi6.z / double6 * double7 - csi5.z) * 0.1);
                }
                this.setDeltaMovement(csi5.multiply(0.9900000095367432, 0.9800000190734863, 0.9900000095367432));
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.horizontalCollision && !this.level.isClientSide) {
                    final double double8 = Math.sqrt(Entity.getHorizontalDistanceSqr(this.getDeltaMovement()));
                    final double double9 = double7 - double8;
                    final float float12 = (float)(double9 * 10.0 - 3.0);
                    if (float12 > 0.0f) {
                        this.playSound(this.getFallDamageSound((int)float12), 1.0f, 1.0f);
                        this.hurt(DamageSource.FLY_INTO_WALL, float12);
                    }
                }
                if (this.onGround && !this.level.isClientSide) {
                    this.setSharedFlag(7, false);
                }
            }
            else {
                final BlockPos ew6 = new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z);
                final float float13 = this.level.getBlockState(ew6).getBlock().getFriction();
                final float float8 = this.onGround ? (float13 * 0.91f) : 0.91f;
                this.moveRelative(this.getFrictionInfluencedSpeed(float13), csi);
                this.setDeltaMovement(this.handleOnClimbable(this.getDeltaMovement()));
                this.move(MoverType.SELF, this.getDeltaMovement());
                Vec3 csi7 = this.getDeltaMovement();
                if ((this.horizontalCollision || this.jumping) && this.onLadder()) {
                    csi7 = new Vec3(csi7.x, 0.2, csi7.z);
                }
                double double10 = csi7.y;
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    double10 += (0.05 * (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - csi7.y) * 0.2;
                    this.fallDistance = 0.0f;
                }
                else if (!this.level.isClientSide || this.level.hasChunkAt(ew6)) {
                    if (!this.isNoGravity()) {
                        double10 -= double3;
                    }
                }
                else if (this.y > 0.0) {
                    double10 = -0.1;
                }
                else {
                    double10 = 0.0;
                }
                this.setDeltaMovement(csi7.x * float8, double10 * 0.9800000190734863, csi7.z * float8);
            }
        }
        this.animationSpeedOld = this.animationSpeed;
        double double3 = this.x - this.xo;
        final double double11 = this.z - this.zo;
        final double double12 = (this instanceof FlyingAnimal) ? (this.y - this.yo) : 0.0;
        float float9 = Mth.sqrt(double3 * double3 + double12 * double12 + double11 * double11) * 4.0f;
        if (float9 > 1.0f) {
            float9 = 1.0f;
        }
        this.animationSpeed += (float9 - this.animationSpeed) * 0.4f;
        this.animationPosition += this.animationSpeed;
    }
    
    private Vec3 handleOnClimbable(Vec3 csi) {
        if (this.onLadder()) {
            this.fallDistance = 0.0f;
            final float float3 = 0.15f;
            final double double4 = Mth.clamp(csi.x, -0.15000000596046448, 0.15000000596046448);
            final double double5 = Mth.clamp(csi.z, -0.15000000596046448, 0.15000000596046448);
            double double6 = Math.max(csi.y, -0.15000000596046448);
            if (double6 < 0.0 && this.getFeetBlockState().getBlock() != Blocks.SCAFFOLDING && this.isSneaking() && this instanceof Player) {
                double6 = 0.0;
            }
            csi = new Vec3(double4, double6, double5);
        }
        return csi;
    }
    
    private float getFrictionInfluencedSpeed(final float float1) {
        if (this.onGround) {
            return this.getSpeed() * (0.21600002f / (float1 * float1 * float1));
        }
        return this.flyingSpeed;
    }
    
    public float getSpeed() {
        return this.speed;
    }
    
    public void setSpeed(final float float1) {
        this.speed = float1;
    }
    
    public boolean doHurtTarget(final Entity aio) {
        this.setLastHurtMob(aio);
        return false;
    }
    
    @Override
    public void tick() {
        super.tick();
        this.updatingUsingItem();
        this.updateSwimAmount();
        if (!this.level.isClientSide) {
            final int integer2 = this.getArrowCount();
            if (integer2 > 0) {
                if (this.removeArrowTime <= 0) {
                    this.removeArrowTime = 20 * (30 - integer2);
                }
                --this.removeArrowTime;
                if (this.removeArrowTime <= 0) {
                    this.setArrowCount(integer2 - 1);
                }
            }
            for (final EquipmentSlot ait6 : EquipmentSlot.values()) {
                Label_0371: {
                    ItemStack bcj7 = null;
                    switch (ait6.getType()) {
                        case HAND: {
                            bcj7 = this.lastHandItemStacks.get(ait6.getIndex());
                            break;
                        }
                        case ARMOR: {
                            bcj7 = this.lastArmorItemStacks.get(ait6.getIndex());
                            break;
                        }
                        default: {
                            break Label_0371;
                        }
                    }
                    final ItemStack bcj8 = this.getItemBySlot(ait6);
                    if (!ItemStack.matches(bcj8, bcj7)) {
                        ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundSetEquippedItemPacket(this.getId(), ait6, bcj8));
                        if (!bcj7.isEmpty()) {
                            this.getAttributes().removeAttributeModifiers(bcj7.getAttributeModifiers(ait6));
                        }
                        if (!bcj8.isEmpty()) {
                            this.getAttributes().addAttributeModifiers(bcj8.getAttributeModifiers(ait6));
                        }
                        switch (ait6.getType()) {
                            case HAND: {
                                this.lastHandItemStacks.set(ait6.getIndex(), bcj8.isEmpty() ? ItemStack.EMPTY : bcj8.copy());
                                break;
                            }
                            case ARMOR: {
                                this.lastArmorItemStacks.set(ait6.getIndex(), bcj8.isEmpty() ? ItemStack.EMPTY : bcj8.copy());
                                break;
                            }
                        }
                    }
                }
            }
            if (this.tickCount % 20 == 0) {
                this.getCombatTracker().recheckStatus();
            }
            if (!this.glowing) {
                final boolean boolean3 = this.hasEffect(MobEffects.GLOWING);
                if (this.getSharedFlag(6) != boolean3) {
                    this.setSharedFlag(6, boolean3);
                }
            }
            if (this.isSleeping() && !this.checkBedExists()) {
                this.stopSleeping();
            }
        }
        this.aiStep();
        final double double2 = this.x - this.xo;
        final double double3 = this.z - this.zo;
        final float float6 = (float)(double2 * double2 + double3 * double3);
        float float7 = this.yBodyRot;
        float float8 = 0.0f;
        this.oRun = this.run;
        float float9 = 0.0f;
        if (float6 > 0.0025000002f) {
            float9 = 1.0f;
            float8 = (float)Math.sqrt((double)float6) * 3.0f;
            final float float10 = (float)Mth.atan2(double3, double2) * 57.295776f - 90.0f;
            final float float11 = Mth.abs(Mth.wrapDegrees(this.yRot) - float10);
            if (95.0f < float11 && float11 < 265.0f) {
                float7 = float10 - 180.0f;
            }
            else {
                float7 = float10;
            }
        }
        if (this.attackAnim > 0.0f) {
            float7 = this.yRot;
        }
        if (!this.onGround) {
            float9 = 0.0f;
        }
        this.run += (float9 - this.run) * 0.3f;
        this.level.getProfiler().push("headTurn");
        float8 = this.tickHeadTurn(float7, float8);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("rangeChecks");
        while (this.yRot - this.yRotO < -180.0f) {
            this.yRotO -= 360.0f;
        }
        while (this.yRot - this.yRotO >= 180.0f) {
            this.yRotO += 360.0f;
        }
        while (this.yBodyRot - this.yBodyRotO < -180.0f) {
            this.yBodyRotO -= 360.0f;
        }
        while (this.yBodyRot - this.yBodyRotO >= 180.0f) {
            this.yBodyRotO += 360.0f;
        }
        while (this.xRot - this.xRotO < -180.0f) {
            this.xRotO -= 360.0f;
        }
        while (this.xRot - this.xRotO >= 180.0f) {
            this.xRotO += 360.0f;
        }
        while (this.yHeadRot - this.yHeadRotO < -180.0f) {
            this.yHeadRotO -= 360.0f;
        }
        while (this.yHeadRot - this.yHeadRotO >= 180.0f) {
            this.yHeadRotO += 360.0f;
        }
        this.level.getProfiler().pop();
        this.animStep += float8;
        if (this.isFallFlying()) {
            ++this.fallFlyTicks;
        }
        else {
            this.fallFlyTicks = 0;
        }
        if (this.isSleeping()) {
            this.xRot = 0.0f;
        }
    }
    
    protected float tickHeadTurn(final float float1, float float2) {
        final float float3 = Mth.wrapDegrees(float1 - this.yBodyRot);
        this.yBodyRot += float3 * 0.3f;
        float float4 = Mth.wrapDegrees(this.yRot - this.yBodyRot);
        final boolean boolean6 = float4 < -90.0f || float4 >= 90.0f;
        if (float4 < -75.0f) {
            float4 = -75.0f;
        }
        if (float4 >= 75.0f) {
            float4 = 75.0f;
        }
        this.yBodyRot = this.yRot - float4;
        if (float4 * float4 > 2500.0f) {
            this.yBodyRot += float4 * 0.2f;
        }
        if (boolean6) {
            float2 *= -1.0f;
        }
        return float2;
    }
    
    public void aiStep() {
        if (this.noJumpDelay > 0) {
            --this.noJumpDelay;
        }
        if (this.lerpSteps > 0 && !this.isControlledByLocalInstance()) {
            final double double2 = this.x + (this.lerpX - this.x) / this.lerpSteps;
            final double double3 = this.y + (this.lerpY - this.y) / this.lerpSteps;
            final double double4 = this.z + (this.lerpZ - this.z) / this.lerpSteps;
            final double double5 = Mth.wrapDegrees(this.lerpYRot - this.yRot);
            this.yRot += (float)(double5 / this.lerpSteps);
            this.xRot += (float)((this.lerpXRot - this.xRot) / this.lerpSteps);
            --this.lerpSteps;
            this.setPos(double2, double3, double4);
            this.setRot(this.yRot, this.xRot);
        }
        else if (!this.isEffectiveAi()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        }
        if (this.lerpHeadSteps > 0) {
            this.yHeadRot += (float)(Mth.wrapDegrees(this.lyHeadRot - this.yHeadRot) / this.lerpHeadSteps);
            --this.lerpHeadSteps;
        }
        final Vec3 csi2 = this.getDeltaMovement();
        double double6 = csi2.x;
        double double7 = csi2.y;
        double double8 = csi2.z;
        if (Math.abs(csi2.x) < 0.003) {
            double6 = 0.0;
        }
        if (Math.abs(csi2.y) < 0.003) {
            double7 = 0.0;
        }
        if (Math.abs(csi2.z) < 0.003) {
            double8 = 0.0;
        }
        this.setDeltaMovement(double6, double7, double8);
        this.level.getProfiler().push("ai");
        if (this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0f;
            this.zza = 0.0f;
            this.yRotA = 0.0f;
        }
        else if (this.isEffectiveAi()) {
            this.level.getProfiler().push("newAi");
            this.serverAiStep();
            this.level.getProfiler().pop();
        }
        this.level.getProfiler().pop();
        this.level.getProfiler().push("jump");
        if (this.jumping) {
            if (this.waterHeight > 0.0 && (!this.onGround || this.waterHeight > 0.4)) {
                this.jumpInLiquid(FluidTags.WATER);
            }
            else if (this.isInLava()) {
                this.jumpInLiquid(FluidTags.LAVA);
            }
            else if ((this.onGround || (this.waterHeight > 0.0 && this.waterHeight <= 0.4)) && this.noJumpDelay == 0) {
                this.jumpFromGround();
                this.noJumpDelay = 10;
            }
        }
        else {
            this.noJumpDelay = 0;
        }
        this.level.getProfiler().pop();
        this.level.getProfiler().push("travel");
        this.xxa *= 0.98f;
        this.zza *= 0.98f;
        this.yRotA *= 0.9f;
        this.updateFallFlying();
        final AABB csc9 = this.getBoundingBox();
        this.travel(new Vec3(this.xxa, this.yya, this.zza));
        this.level.getProfiler().pop();
        this.level.getProfiler().push("push");
        if (this.autoSpinAttackTicks > 0) {
            --this.autoSpinAttackTicks;
            this.checkAutoSpinAttack(csc9, this.getBoundingBox());
        }
        this.pushEntities();
        this.level.getProfiler().pop();
    }
    
    private void updateFallFlying() {
        boolean boolean2 = this.getSharedFlag(7);
        if (boolean2 && !this.onGround && !this.isPassenger()) {
            final ItemStack bcj3 = this.getItemBySlot(EquipmentSlot.CHEST);
            if (bcj3.getItem() == Items.ELYTRA && ElytraItem.isFlyEnabled(bcj3)) {
                boolean2 = true;
                if (!this.level.isClientSide && (this.fallFlyTicks + 1) % 20 == 0) {
                    bcj3.<LivingEntity>hurtAndBreak(1, this, (java.util.function.Consumer<LivingEntity>)(aix -> aix.broadcastBreakEvent(EquipmentSlot.CHEST)));
                }
            }
            else {
                boolean2 = false;
            }
        }
        else {
            boolean2 = false;
        }
        if (!this.level.isClientSide) {
            this.setSharedFlag(7, boolean2);
        }
    }
    
    protected void serverAiStep() {
    }
    
    protected void pushEntities() {
        final List<Entity> list2 = this.level.getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
        if (!list2.isEmpty()) {
            final int integer3 = this.level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
            if (integer3 > 0 && list2.size() > integer3 - 1 && this.random.nextInt(4) == 0) {
                int integer4 = 0;
                for (int integer5 = 0; integer5 < list2.size(); ++integer5) {
                    if (!((Entity)list2.get(integer5)).isPassenger()) {
                        ++integer4;
                    }
                }
                if (integer4 > integer3 - 1) {
                    this.hurt(DamageSource.CRAMMING, 6.0f);
                }
            }
            for (int integer4 = 0; integer4 < list2.size(); ++integer4) {
                final Entity aio5 = (Entity)list2.get(integer4);
                this.doPush(aio5);
            }
        }
    }
    
    protected void checkAutoSpinAttack(final AABB csc1, final AABB csc2) {
        final AABB csc3 = csc1.minmax(csc2);
        final List<Entity> list5 = this.level.getEntities(this, csc3);
        if (!list5.isEmpty()) {
            for (int integer6 = 0; integer6 < list5.size(); ++integer6) {
                final Entity aio7 = (Entity)list5.get(integer6);
                if (aio7 instanceof LivingEntity) {
                    this.doAutoAttackOnTouch((LivingEntity)aio7);
                    this.autoSpinAttackTicks = 0;
                    this.setDeltaMovement(this.getDeltaMovement().scale(-0.2));
                    break;
                }
            }
        }
        else if (this.horizontalCollision) {
            this.autoSpinAttackTicks = 0;
        }
        if (!this.level.isClientSide && this.autoSpinAttackTicks <= 0) {
            this.setLivingEntityFlag(4, false);
        }
    }
    
    protected void doPush(final Entity aio) {
        aio.push(this);
    }
    
    protected void doAutoAttackOnTouch(final LivingEntity aix) {
    }
    
    public void startAutoSpinAttack(final int integer) {
        this.autoSpinAttackTicks = integer;
        if (!this.level.isClientSide) {
            this.setLivingEntityFlag(4, true);
        }
    }
    
    public boolean isAutoSpinAttack() {
        return (this.entityData.<Byte>get(LivingEntity.DATA_LIVING_ENTITY_FLAGS) & 0x4) != 0x0;
    }
    
    @Override
    public void stopRiding() {
        final Entity aio2 = this.getVehicle();
        super.stopRiding();
        if (aio2 != null && aio2 != this.getVehicle() && !this.level.isClientSide) {
            this.findStandUpPosition(aio2);
        }
    }
    
    @Override
    public void rideTick() {
        super.rideTick();
        this.oRun = this.run;
        this.run = 0.0f;
        this.fallDistance = 0.0f;
    }
    
    @Override
    public void lerpTo(final double double1, final double double2, final double double3, final float float4, final float float5, final int integer, final boolean boolean7) {
        this.lerpX = double1;
        this.lerpY = double2;
        this.lerpZ = double3;
        this.lerpYRot = float4;
        this.lerpXRot = float5;
        this.lerpSteps = integer;
    }
    
    @Override
    public void lerpHeadTo(final float float1, final int integer) {
        this.lyHeadRot = float1;
        this.lerpHeadSteps = integer;
    }
    
    public void setJumping(final boolean boolean1) {
        this.jumping = boolean1;
    }
    
    public void take(final Entity aio, final int integer) {
        if (!aio.removed && !this.level.isClientSide && (aio instanceof ItemEntity || aio instanceof AbstractArrow || aio instanceof ExperienceOrb)) {
            ((ServerLevel)this.level).getChunkSource().broadcast(aio, new ClientboundTakeItemEntityPacket(aio.getId(), this.getId(), integer));
        }
    }
    
    public boolean canSee(final Entity aio) {
        final Vec3 csi3 = new Vec3(this.x, this.y + this.getEyeHeight(), this.z);
        final Vec3 csi4 = new Vec3(aio.x, aio.y + aio.getEyeHeight(), aio.z);
        return this.level.clip(new ClipContext(csi3, csi4, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }
    
    @Override
    public float getViewYRot(final float float1) {
        if (float1 == 1.0f) {
            return this.yHeadRot;
        }
        return Mth.lerp(float1, this.yHeadRotO, this.yHeadRot);
    }
    
    public float getAttackAnim(final float float1) {
        float float2 = this.attackAnim - this.oAttackAnim;
        if (float2 < 0.0f) {
            ++float2;
        }
        return this.oAttackAnim + float2 * float1;
    }
    
    public boolean isEffectiveAi() {
        return !this.level.isClientSide;
    }
    
    @Override
    public boolean isPickable() {
        return !this.removed;
    }
    
    @Override
    public boolean isPushable() {
        return this.isAlive() && !this.onLadder();
    }
    
    @Override
    protected void markHurt() {
        this.hurtMarked = (this.random.nextDouble() >= this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getValue());
    }
    
    @Override
    public float getYHeadRot() {
        return this.yHeadRot;
    }
    
    @Override
    public void setYHeadRot(final float float1) {
        this.yHeadRot = float1;
    }
    
    @Override
    public void setYBodyRot(final float float1) {
        this.yBodyRot = float1;
    }
    
    public float getAbsorptionAmount() {
        return this.absorptionAmount;
    }
    
    public void setAbsorptionAmount(float float1) {
        if (float1 < 0.0f) {
            float1 = 0.0f;
        }
        this.absorptionAmount = float1;
    }
    
    public void onEnterCombat() {
    }
    
    public void onLeaveCombat() {
    }
    
    protected void updateEffectVisibility() {
        this.effectsDirty = true;
    }
    
    public abstract HumanoidArm getMainArm();
    
    public boolean isUsingItem() {
        return (this.entityData.<Byte>get(LivingEntity.DATA_LIVING_ENTITY_FLAGS) & 0x1) > 0;
    }
    
    public InteractionHand getUsedItemHand() {
        return ((this.entityData.<Byte>get(LivingEntity.DATA_LIVING_ENTITY_FLAGS) & 0x2) > 0) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }
    
    private void updatingUsingItem() {
        if (this.isUsingItem()) {
            if (ItemStack.isSameIgnoreDurability(this.getItemInHand(this.getUsedItemHand()), this.useItem)) {
                this.useItem.onUseTick(this.level, this, this.getUseItemRemainingTicks());
                if (this.getUseItemRemainingTicks() <= 25 && this.getUseItemRemainingTicks() % 4 == 0) {
                    this.spawnItemUseParticles(this.useItem, 5);
                }
                if (--this.useItemRemaining == 0 && !this.level.isClientSide && !this.useItem.useOnRelease()) {
                    this.completeUsingItem();
                }
            }
            else {
                this.stopUsingItem();
            }
        }
    }
    
    private void updateSwimAmount() {
        this.swimAmountO = this.swimAmount;
        if (this.isVisuallySwimming()) {
            this.swimAmount = Math.min(1.0f, this.swimAmount + 0.09f);
        }
        else {
            this.swimAmount = Math.max(0.0f, this.swimAmount - 0.09f);
        }
    }
    
    protected void setLivingEntityFlag(final int integer, final boolean boolean2) {
        int integer2 = this.entityData.<Byte>get(LivingEntity.DATA_LIVING_ENTITY_FLAGS);
        if (boolean2) {
            integer2 |= integer;
        }
        else {
            integer2 &= ~integer;
        }
        this.entityData.<Byte>set(LivingEntity.DATA_LIVING_ENTITY_FLAGS, (byte)integer2);
    }
    
    public void startUsingItem(final InteractionHand ahi) {
        final ItemStack bcj3 = this.getItemInHand(ahi);
        if (bcj3.isEmpty() || this.isUsingItem()) {
            return;
        }
        this.useItem = bcj3;
        this.useItemRemaining = bcj3.getUseDuration();
        if (!this.level.isClientSide) {
            this.setLivingEntityFlag(1, true);
            this.setLivingEntityFlag(2, ahi == InteractionHand.OFF_HAND);
        }
    }
    
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        super.onSyncedDataUpdated(qk);
        if (LivingEntity.SLEEPING_POS_ID.equals(qk)) {
            if (this.level.isClientSide) {
                this.getSleepingPos().ifPresent(this::setPosToBed);
            }
        }
        else if (LivingEntity.DATA_LIVING_ENTITY_FLAGS.equals(qk) && this.level.isClientSide) {
            if (this.isUsingItem() && this.useItem.isEmpty()) {
                this.useItem = this.getItemInHand(this.getUsedItemHand());
                if (!this.useItem.isEmpty()) {
                    this.useItemRemaining = this.useItem.getUseDuration();
                }
            }
            else if (!this.isUsingItem() && !this.useItem.isEmpty()) {
                this.useItem = ItemStack.EMPTY;
                this.useItemRemaining = 0;
            }
        }
    }
    
    @Override
    public void lookAt(final EntityAnchorArgument.Anchor a, final Vec3 csi) {
        super.lookAt(a, csi);
        this.yHeadRotO = this.yHeadRot;
        this.yBodyRot = this.yHeadRot;
        this.yBodyRotO = this.yBodyRot;
    }
    
    protected void spawnItemUseParticles(final ItemStack bcj, final int integer) {
        if (bcj.isEmpty() || !this.isUsingItem()) {
            return;
        }
        if (bcj.getUseAnimation() == UseAnim.DRINK) {
            this.playSound(this.getDrinkingSound(bcj), 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
        }
        if (bcj.getUseAnimation() == UseAnim.EAT) {
            this.spawnItemParticles(bcj, integer);
            this.playSound(this.getEatingSound(bcj), 0.5f + 0.5f * this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
        }
    }
    
    private void spawnItemParticles(final ItemStack bcj, final int integer) {
        for (int integer2 = 0; integer2 < integer; ++integer2) {
            Vec3 csi5 = new Vec3((this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            csi5 = csi5.xRot(-this.xRot * 0.017453292f);
            csi5 = csi5.yRot(-this.yRot * 0.017453292f);
            final double double6 = -this.random.nextFloat() * 0.6 - 0.3;
            Vec3 csi6 = new Vec3((this.random.nextFloat() - 0.5) * 0.3, double6, 0.6);
            csi6 = csi6.xRot(-this.xRot * 0.017453292f);
            csi6 = csi6.yRot(-this.yRot * 0.017453292f);
            csi6 = csi6.add(this.x, this.y + this.getEyeHeight(), this.z);
            this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, bcj), csi6.x, csi6.y, csi6.z, csi5.x, csi5.y + 0.05, csi5.z);
        }
    }
    
    protected void completeUsingItem() {
        if (!this.useItem.isEmpty() && this.isUsingItem()) {
            this.spawnItemUseParticles(this.useItem, 16);
            this.setItemInHand(this.getUsedItemHand(), this.useItem.finishUsingItem(this.level, this));
            this.stopUsingItem();
        }
    }
    
    public ItemStack getUseItem() {
        return this.useItem;
    }
    
    public int getUseItemRemainingTicks() {
        return this.useItemRemaining;
    }
    
    public int getTicksUsingItem() {
        if (this.isUsingItem()) {
            return this.useItem.getUseDuration() - this.getUseItemRemainingTicks();
        }
        return 0;
    }
    
    public void releaseUsingItem() {
        if (!this.useItem.isEmpty()) {
            this.useItem.releaseUsing(this.level, this, this.getUseItemRemainingTicks());
            if (this.useItem.useOnRelease()) {
                this.updatingUsingItem();
            }
        }
        this.stopUsingItem();
    }
    
    public void stopUsingItem() {
        if (!this.level.isClientSide) {
            this.setLivingEntityFlag(1, false);
        }
        this.useItem = ItemStack.EMPTY;
        this.useItemRemaining = 0;
    }
    
    public boolean isBlocking() {
        if (!this.isUsingItem() || this.useItem.isEmpty()) {
            return false;
        }
        final Item bce2 = this.useItem.getItem();
        return bce2.getUseAnimation(this.useItem) == UseAnim.BLOCK && bce2.getUseDuration(this.useItem) - this.useItemRemaining >= 5;
    }
    
    public boolean isFallFlying() {
        return this.getSharedFlag(7);
    }
    
    @Override
    public boolean isVisuallySwimming() {
        return super.isVisuallySwimming() || (!this.isFallFlying() && this.getPose() == Pose.FALL_FLYING);
    }
    
    public int getFallFlyingTicks() {
        return this.fallFlyTicks;
    }
    
    public boolean randomTeleport(final double double1, final double double2, final double double3, final boolean boolean4) {
        final double double4 = this.x;
        final double double5 = this.y;
        final double double6 = this.z;
        this.x = double1;
        this.y = double2;
        this.z = double3;
        boolean boolean5 = false;
        BlockPos ew16 = new BlockPos(this);
        final Level bhr17 = this.level;
        if (bhr17.hasChunkAt(ew16)) {
            boolean boolean6 = false;
            while (!boolean6 && ew16.getY() > 0) {
                final BlockPos ew17 = ew16.below();
                final BlockState bvt20 = bhr17.getBlockState(ew17);
                if (bvt20.getMaterial().blocksMotion()) {
                    boolean6 = true;
                }
                else {
                    --this.y;
                    ew16 = ew17;
                }
            }
            if (boolean6) {
                this.teleportTo(this.x, this.y, this.z);
                if (bhr17.noCollision(this) && !bhr17.containsAnyLiquid(this.getBoundingBox())) {
                    boolean5 = true;
                }
            }
        }
        if (!boolean5) {
            this.teleportTo(double4, double5, double6);
            return false;
        }
        if (boolean4) {
            bhr17.broadcastEntityEvent(this, (byte)46);
        }
        if (this instanceof PathfinderMob) {
            ((PathfinderMob)this).getNavigation().stop();
        }
        return true;
    }
    
    public boolean isAffectedByPotions() {
        return true;
    }
    
    public boolean attackable() {
        return true;
    }
    
    public void setRecordPlayingNearby(final BlockPos ew, final boolean boolean2) {
    }
    
    public boolean canTakeItem(final ItemStack bcj) {
        return false;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddMobPacket(this);
    }
    
    @Override
    public EntityDimensions getDimensions(final Pose ajh) {
        return (ajh == Pose.SLEEPING) ? LivingEntity.SLEEPING_DIMENSIONS : super.getDimensions(ajh).scale(this.getScale());
    }
    
    public Optional<BlockPos> getSleepingPos() {
        return this.entityData.<Optional<BlockPos>>get(LivingEntity.SLEEPING_POS_ID);
    }
    
    public void setSleepingPos(final BlockPos ew) {
        this.entityData.<Optional<BlockPos>>set(LivingEntity.SLEEPING_POS_ID, (Optional<BlockPos>)Optional.of(ew));
    }
    
    public void clearSleepingPos() {
        this.entityData.<Optional<BlockPos>>set(LivingEntity.SLEEPING_POS_ID, (Optional<BlockPos>)Optional.empty());
    }
    
    public boolean isSleeping() {
        return this.getSleepingPos().isPresent();
    }
    
    public void startSleeping(final BlockPos ew) {
        if (this.isPassenger()) {
            this.stopRiding();
        }
        final BlockState bvt3 = this.level.getBlockState(ew);
        if (bvt3.getBlock() instanceof BedBlock) {
            this.level.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Boolean>setValue((Property<Comparable>)BedBlock.OCCUPIED, true), 3);
        }
        this.setPose(Pose.SLEEPING);
        this.setPosToBed(ew);
        this.setSleepingPos(ew);
        this.setDeltaMovement(Vec3.ZERO);
        this.hasImpulse = true;
    }
    
    private void setPosToBed(final BlockPos ew) {
        this.setPos(ew.getX() + 0.5, ew.getY() + 0.6875f, ew.getZ() + 0.5);
    }
    
    private boolean checkBedExists() {
        return (boolean)this.getSleepingPos().map(ew -> this.level.getBlockState(ew).getBlock() instanceof BedBlock).orElse(false);
    }
    
    public void stopSleeping() {
        this.getSleepingPos().filter(this.level::hasChunkAt).ifPresent(ew -> {
            final BlockState bvt3 = this.level.getBlockState(ew);
            if (bvt3.getBlock() instanceof BedBlock) {
                this.level.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Boolean>setValue((Property<Comparable>)BedBlock.OCCUPIED, false), 3);
                final Vec3 csi4 = (Vec3)BedBlock.findStandUpPosition(this.getType(), this.level, ew, 0).orElseGet(() -> {
                    final BlockPos ew2 = ew.above();
                    return new Vec3(ew2.getX() + 0.5, ew2.getY() + 0.1, ew2.getZ() + 0.5);
                });
                this.setPos(csi4.x, csi4.y, csi4.z);
            }
        });
        this.setPose(Pose.STANDING);
        this.clearSleepingPos();
    }
    
    @Nullable
    public Direction getBedOrientation() {
        final BlockPos ew2 = (BlockPos)this.getSleepingPos().orElse(null);
        return (ew2 != null) ? BedBlock.getBedOrientation(this.level, ew2) : null;
    }
    
    @Override
    public boolean isInWall() {
        return !this.isSleeping() && super.isInWall();
    }
    
    @Override
    protected final float getEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return (ajh == Pose.SLEEPING) ? 0.2f : this.getStandingEyeHeight(ajh, aip);
    }
    
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return super.getEyeHeight(ajh, aip);
    }
    
    public ItemStack getProjectile(final ItemStack bcj) {
        return ItemStack.EMPTY;
    }
    
    public ItemStack eat(final Level bhr, final ItemStack bcj) {
        if (bcj.isEdible()) {
            bhr.playSound(null, this.x, this.y, this.z, this.getEatingSound(bcj), SoundSource.NEUTRAL, 1.0f, 1.0f + (bhr.random.nextFloat() - bhr.random.nextFloat()) * 0.4f);
            this.addEatEffect(bcj, bhr, this);
            bcj.shrink(1);
        }
        return bcj;
    }
    
    private void addEatEffect(final ItemStack bcj, final Level bhr, final LivingEntity aix) {
        final Item bce5 = bcj.getItem();
        if (bce5.isEdible()) {
            final List<Pair<MobEffectInstance, Float>> list6 = bce5.getFoodProperties().getEffects();
            for (final Pair<MobEffectInstance, Float> pair8 : list6) {
                if (!bhr.isClientSide && pair8.getLeft() != null && bhr.random.nextFloat() < (float)pair8.getRight()) {
                    aix.addEffect(new MobEffectInstance((MobEffectInstance)pair8.getLeft()));
                }
            }
        }
    }
    
    private static byte entityEventForEquipmentBreak(final EquipmentSlot ait) {
        switch (ait) {
            case MAINHAND: {
                return 47;
            }
            case OFFHAND: {
                return 48;
            }
            case HEAD: {
                return 49;
            }
            case CHEST: {
                return 50;
            }
            case FEET: {
                return 52;
            }
            case LEGS: {
                return 51;
            }
            default: {
                return 47;
            }
        }
    }
    
    public void broadcastBreakEvent(final EquipmentSlot ait) {
        this.level.broadcastEntityEvent(this, entityEventForEquipmentBreak(ait));
    }
    
    public void broadcastBreakEvent(final InteractionHand ahi) {
        this.broadcastBreakEvent((ahi == InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }
    
    static {
        SPEED_MODIFIER_SPRINTING_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
        SPEED_MODIFIER_SPRINTING = new AttributeModifier(LivingEntity.SPEED_MODIFIER_SPRINTING_UUID, "Sprinting speed boost", 0.30000001192092896, AttributeModifier.Operation.MULTIPLY_TOTAL).setSerialize(false);
        DATA_LIVING_ENTITY_FLAGS = SynchedEntityData.<Byte>defineId(LivingEntity.class, EntityDataSerializers.BYTE);
        DATA_HEALTH_ID = SynchedEntityData.<Float>defineId(LivingEntity.class, EntityDataSerializers.FLOAT);
        DATA_EFFECT_COLOR_ID = SynchedEntityData.<Integer>defineId(LivingEntity.class, EntityDataSerializers.INT);
        DATA_EFFECT_AMBIENCE_ID = SynchedEntityData.<Boolean>defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);
        DATA_ARROW_COUNT_ID = SynchedEntityData.<Integer>defineId(LivingEntity.class, EntityDataSerializers.INT);
        SLEEPING_POS_ID = SynchedEntityData.<Optional<BlockPos>>defineId(LivingEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
        SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2f, 0.2f);
    }
}
