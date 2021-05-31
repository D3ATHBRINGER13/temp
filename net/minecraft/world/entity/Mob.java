package net.minecraft.world.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.Mth;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.SwordItem;
import java.util.List;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.LootContext;
import java.util.UUID;
import java.util.Iterator;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import java.util.Arrays;
import com.google.common.collect.Maps;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.ai.sensing.Sensing;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.network.syncher.EntityDataAccessor;

public abstract class Mob extends LivingEntity {
    private static final EntityDataAccessor<Byte> DATA_MOB_FLAGS_ID;
    public int ambientSoundTime;
    protected int xpReward;
    protected LookControl lookControl;
    protected MoveControl moveControl;
    protected JumpControl jumpControl;
    private final BodyRotationControl bodyRotationControl;
    protected PathNavigation navigation;
    protected final GoalSelector goalSelector;
    protected final GoalSelector targetSelector;
    private LivingEntity target;
    private final Sensing sensing;
    private final NonNullList<ItemStack> handItems;
    protected final float[] handDropChances;
    private final NonNullList<ItemStack> armorItems;
    protected final float[] armorDropChances;
    private boolean canPickUpLoot;
    private boolean persistenceRequired;
    private final Map<BlockPathTypes, Float> pathfindingMalus;
    private ResourceLocation lootTable;
    private long lootTableSeed;
    @Nullable
    private Entity leashHolder;
    private int delayedLeashHolderId;
    @Nullable
    private CompoundTag leashInfoTag;
    private BlockPos restrictCenter;
    private float restrictRadius;
    
    protected Mob(final EntityType<? extends Mob> ais, final Level bhr) {
        super(ais, bhr);
        this.handItems = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
        this.handDropChances = new float[2];
        this.armorItems = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
        this.armorDropChances = new float[4];
        this.pathfindingMalus = (Map<BlockPathTypes, Float>)Maps.newEnumMap((Class)BlockPathTypes.class);
        this.restrictCenter = BlockPos.ZERO;
        this.restrictRadius = -1.0f;
        this.goalSelector = new GoalSelector((bhr == null || bhr.getProfiler() == null) ? null : bhr.getProfiler());
        this.targetSelector = new GoalSelector((bhr == null || bhr.getProfiler() == null) ? null : bhr.getProfiler());
        this.lookControl = new LookControl(this);
        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);
        this.bodyRotationControl = this.createBodyControl();
        this.navigation = this.createNavigation(bhr);
        this.sensing = new Sensing(this);
        Arrays.fill(this.armorDropChances, 0.085f);
        Arrays.fill(this.handDropChances, 0.085f);
        if (bhr != null && !bhr.isClientSide) {
            this.registerGoals();
        }
    }
    
    protected void registerGoals() {
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK);
    }
    
    protected PathNavigation createNavigation(final Level bhr) {
        return new GroundPathNavigation(this, bhr);
    }
    
    public float getPathfindingMalus(final BlockPathTypes cnn) {
        final Float float3 = (Float)this.pathfindingMalus.get(cnn);
        return (float3 == null) ? cnn.getMalus() : float3;
    }
    
    public void setPathfindingMalus(final BlockPathTypes cnn, final float float2) {
        this.pathfindingMalus.put(cnn, float2);
    }
    
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this);
    }
    
    public LookControl getLookControl() {
        return this.lookControl;
    }
    
    public MoveControl getMoveControl() {
        if (this.isPassenger() && this.getVehicle() instanceof Mob) {
            final Mob aiy2 = (Mob)this.getVehicle();
            return aiy2.getMoveControl();
        }
        return this.moveControl;
    }
    
    public JumpControl getJumpControl() {
        return this.jumpControl;
    }
    
    public PathNavigation getNavigation() {
        if (this.isPassenger() && this.getVehicle() instanceof Mob) {
            final Mob aiy2 = (Mob)this.getVehicle();
            return aiy2.getNavigation();
        }
        return this.navigation;
    }
    
    public Sensing getSensing() {
        return this.sensing;
    }
    
    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }
    
    public void setTarget(@Nullable final LivingEntity aix) {
        this.target = aix;
    }
    
    @Override
    public boolean canAttackType(final EntityType<?> ais) {
        return ais != EntityType.GHAST;
    }
    
    public void ate() {
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Byte>define(Mob.DATA_MOB_FLAGS_ID, (Byte)0);
    }
    
    public int getAmbientSoundInterval() {
        return 80;
    }
    
    public void playAmbientSound() {
        final SoundEvent yo2 = this.getAmbientSound();
        if (yo2 != null) {
            this.playSound(yo2, this.getSoundVolume(), this.getVoicePitch());
        }
    }
    
    @Override
    public void baseTick() {
        super.baseTick();
        this.level.getProfiler().push("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundTime++) {
            this.resetAmbientSoundTime();
            this.playAmbientSound();
        }
        this.level.getProfiler().pop();
    }
    
    @Override
    protected void playHurtSound(final DamageSource ahx) {
        this.resetAmbientSoundTime();
        super.playHurtSound(ahx);
    }
    
    private void resetAmbientSoundTime() {
        this.ambientSoundTime = -this.getAmbientSoundInterval();
    }
    
    @Override
    protected int getExperienceReward(final Player awg) {
        if (this.xpReward > 0) {
            int integer3 = this.xpReward;
            for (int integer4 = 0; integer4 < this.armorItems.size(); ++integer4) {
                if (!this.armorItems.get(integer4).isEmpty() && this.armorDropChances[integer4] <= 1.0f) {
                    integer3 += 1 + this.random.nextInt(3);
                }
            }
            for (int integer4 = 0; integer4 < this.handItems.size(); ++integer4) {
                if (!this.handItems.get(integer4).isEmpty() && this.handDropChances[integer4] <= 1.0f) {
                    integer3 += 1 + this.random.nextInt(3);
                }
            }
            return integer3;
        }
        return this.xpReward;
    }
    
    public void spawnAnim() {
        if (this.level.isClientSide) {
            for (int integer2 = 0; integer2 < 20; ++integer2) {
                final double double3 = this.random.nextGaussian() * 0.02;
                final double double4 = this.random.nextGaussian() * 0.02;
                final double double5 = this.random.nextGaussian() * 0.02;
                final double double6 = 10.0;
                this.level.addParticle(ParticleTypes.POOF, this.x + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth() - double3 * 10.0, this.y + this.random.nextFloat() * this.getBbHeight() - double4 * 10.0, this.z + this.random.nextFloat() * this.getBbWidth() * 2.0f - this.getBbWidth() - double5 * 10.0, double3, double4, double5);
            }
        }
        else {
            this.level.broadcastEntityEvent(this, (byte)20);
        }
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 20) {
            this.spawnAnim();
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.tickLeash();
            if (this.tickCount % 5 == 0) {
                this.updateControlFlags();
            }
        }
    }
    
    protected void updateControlFlags() {
        final boolean boolean2 = !(this.getControllingPassenger() instanceof Mob);
        final boolean boolean3 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, boolean2);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, boolean2 && boolean3);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, boolean2);
    }
    
    @Override
    protected float tickHeadTurn(final float float1, final float float2) {
        this.bodyRotationControl.clientTick();
        return float2;
    }
    
    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("CanPickUpLoot", this.canPickUpLoot());
        id.putBoolean("PersistenceRequired", this.persistenceRequired);
        final ListTag ik3 = new ListTag();
        for (final ItemStack bcj5 : this.armorItems) {
            final CompoundTag id2 = new CompoundTag();
            if (!bcj5.isEmpty()) {
                bcj5.save(id2);
            }
            ik3.add(id2);
        }
        id.put("ArmorItems", (Tag)ik3);
        final ListTag ik4 = new ListTag();
        for (final ItemStack bcj6 : this.handItems) {
            final CompoundTag id3 = new CompoundTag();
            if (!bcj6.isEmpty()) {
                bcj6.save(id3);
            }
            ik4.add(id3);
        }
        id.put("HandItems", (Tag)ik4);
        final ListTag ik5 = new ListTag();
        for (final float float9 : this.armorDropChances) {
            ik5.add(new FloatTag(float9));
        }
        id.put("ArmorDropChances", (Tag)ik5);
        final ListTag ik6 = new ListTag();
        for (final float float10 : this.handDropChances) {
            ik6.add(new FloatTag(float10));
        }
        id.put("HandDropChances", (Tag)ik6);
        if (this.leashHolder != null) {
            final CompoundTag id3 = new CompoundTag();
            if (this.leashHolder instanceof LivingEntity) {
                final UUID uUID8 = this.leashHolder.getUUID();
                id3.putUUID("UUID", uUID8);
            }
            else if (this.leashHolder instanceof HangingEntity) {
                final BlockPos ew8 = ((HangingEntity)this.leashHolder).getPos();
                id3.putInt("X", ew8.getX());
                id3.putInt("Y", ew8.getY());
                id3.putInt("Z", ew8.getZ());
            }
            id.put("Leash", (Tag)id3);
        }
        id.putBoolean("LeftHanded", this.isLeftHanded());
        if (this.lootTable != null) {
            id.putString("DeathLootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                id.putLong("DeathLootTableSeed", this.lootTableSeed);
            }
        }
        if (this.isNoAi()) {
            id.putBoolean("NoAI", this.isNoAi());
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot(id.getBoolean("CanPickUpLoot"));
        }
        this.persistenceRequired = id.getBoolean("PersistenceRequired");
        if (id.contains("ArmorItems", 9)) {
            final ListTag ik3 = id.getList("ArmorItems", 10);
            for (int integer4 = 0; integer4 < this.armorItems.size(); ++integer4) {
                this.armorItems.set(integer4, ItemStack.of(ik3.getCompound(integer4)));
            }
        }
        if (id.contains("HandItems", 9)) {
            final ListTag ik3 = id.getList("HandItems", 10);
            for (int integer4 = 0; integer4 < this.handItems.size(); ++integer4) {
                this.handItems.set(integer4, ItemStack.of(ik3.getCompound(integer4)));
            }
        }
        if (id.contains("ArmorDropChances", 9)) {
            final ListTag ik3 = id.getList("ArmorDropChances", 5);
            for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
                this.armorDropChances[integer4] = ik3.getFloat(integer4);
            }
        }
        if (id.contains("HandDropChances", 9)) {
            final ListTag ik3 = id.getList("HandDropChances", 5);
            for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
                this.handDropChances[integer4] = ik3.getFloat(integer4);
            }
        }
        if (id.contains("Leash", 10)) {
            this.leashInfoTag = id.getCompound("Leash");
        }
        this.setLeftHanded(id.getBoolean("LeftHanded"));
        if (id.contains("DeathLootTable", 8)) {
            this.lootTable = new ResourceLocation(id.getString("DeathLootTable"));
            this.lootTableSeed = id.getLong("DeathLootTableSeed");
        }
        this.setNoAi(id.getBoolean("NoAI"));
    }
    
    @Override
    protected void dropFromLootTable(final DamageSource ahx, final boolean boolean2) {
        super.dropFromLootTable(ahx, boolean2);
        this.lootTable = null;
    }
    
    @Override
    protected LootContext.Builder createLootContext(final boolean boolean1, final DamageSource ahx) {
        return super.createLootContext(boolean1, ahx).withOptionalRandomSeed(this.lootTableSeed, this.random);
    }
    
    @Override
    public final ResourceLocation getLootTable() {
        return (this.lootTable == null) ? this.getDefaultLootTable() : this.lootTable;
    }
    
    protected ResourceLocation getDefaultLootTable() {
        return super.getLootTable();
    }
    
    public void setZza(final float float1) {
        this.zza = float1;
    }
    
    public void setYya(final float float1) {
        this.yya = float1;
    }
    
    public void setXxa(final float float1) {
        this.xxa = float1;
    }
    
    @Override
    public void setSpeed(final float float1) {
        super.setSpeed(float1);
        this.setZza(float1);
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        this.level.getProfiler().push("looting");
        if (!this.level.isClientSide && this.canPickUpLoot() && this.isAlive() && !this.dead && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            final List<ItemEntity> list2 = this.level.<ItemEntity>getEntitiesOfClass((java.lang.Class<? extends ItemEntity>)ItemEntity.class, this.getBoundingBox().inflate(1.0, 0.0, 1.0));
            for (final ItemEntity atx4 : list2) {
                if (!atx4.removed && !atx4.getItem().isEmpty()) {
                    if (atx4.hasPickUpDelay()) {
                        continue;
                    }
                    this.pickUpItem(atx4);
                }
            }
        }
        this.level.getProfiler().pop();
    }
    
    protected void pickUpItem(final ItemEntity atx) {
        final ItemStack bcj3 = atx.getItem();
        final EquipmentSlot ait4 = getEquipmentSlotForItem(bcj3);
        final ItemStack bcj4 = this.getItemBySlot(ait4);
        final boolean boolean6 = this.canReplaceCurrentItem(bcj3, bcj4, ait4);
        if (boolean6 && this.canHoldItem(bcj3)) {
            final double double7 = this.getEquipmentDropChance(ait4);
            if (!bcj4.isEmpty() && this.random.nextFloat() - 0.1f < double7) {
                this.spawnAtLocation(bcj4);
            }
            this.setItemSlot(ait4, bcj3);
            switch (ait4.getType()) {
                case HAND: {
                    this.handDropChances[ait4.getIndex()] = 2.0f;
                    break;
                }
                case ARMOR: {
                    this.armorDropChances[ait4.getIndex()] = 2.0f;
                    break;
                }
            }
            this.persistenceRequired = true;
            this.take(atx, bcj3.getCount());
            atx.remove();
        }
    }
    
    protected boolean canReplaceCurrentItem(final ItemStack bcj1, final ItemStack bcj2, final EquipmentSlot ait) {
        boolean boolean5 = true;
        if (!bcj2.isEmpty()) {
            if (ait.getType() == EquipmentSlot.Type.HAND) {
                if (bcj1.getItem() instanceof SwordItem && !(bcj2.getItem() instanceof SwordItem)) {
                    boolean5 = true;
                }
                else if (bcj1.getItem() instanceof SwordItem && bcj2.getItem() instanceof SwordItem) {
                    final SwordItem bdm6 = (SwordItem)bcj1.getItem();
                    final SwordItem bdm7 = (SwordItem)bcj2.getItem();
                    if (bdm6.getDamage() == bdm7.getDamage()) {
                        boolean5 = (bcj1.getDamageValue() < bcj2.getDamageValue() || (bcj1.hasTag() && !bcj2.hasTag()));
                    }
                    else {
                        boolean5 = (bdm6.getDamage() > bdm7.getDamage());
                    }
                }
                else {
                    boolean5 = (bcj1.getItem() instanceof BowItem && bcj2.getItem() instanceof BowItem && bcj1.hasTag() && !bcj2.hasTag());
                }
            }
            else if (bcj1.getItem() instanceof ArmorItem && !(bcj2.getItem() instanceof ArmorItem)) {
                boolean5 = true;
            }
            else if (bcj1.getItem() instanceof ArmorItem && bcj2.getItem() instanceof ArmorItem && !EnchantmentHelper.hasBindingCurse(bcj2)) {
                final ArmorItem bad6 = (ArmorItem)bcj1.getItem();
                final ArmorItem bad7 = (ArmorItem)bcj2.getItem();
                if (bad6.getDefense() == bad7.getDefense()) {
                    boolean5 = (bcj1.getDamageValue() < bcj2.getDamageValue() || (bcj1.hasTag() && !bcj2.hasTag()));
                }
                else {
                    boolean5 = (bad6.getDefense() > bad7.getDefense());
                }
            }
            else {
                boolean5 = false;
            }
        }
        return boolean5;
    }
    
    protected boolean canHoldItem(final ItemStack bcj) {
        return true;
    }
    
    public boolean removeWhenFarAway(final double double1) {
        return true;
    }
    
    public boolean requiresCustomPersistence() {
        return false;
    }
    
    protected void checkDespawn() {
        if (this.isPersistenceRequired() || this.requiresCustomPersistence()) {
            this.noActionTime = 0;
            return;
        }
        final Entity aio2 = this.level.getNearestPlayer(this, -1.0);
        if (aio2 != null) {
            final double double3 = aio2.distanceToSqr(this);
            if (double3 > 16384.0 && this.removeWhenFarAway(double3)) {
                this.remove();
            }
            if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && double3 > 1024.0 && this.removeWhenFarAway(double3)) {
                this.remove();
            }
            else if (double3 < 1024.0) {
                this.noActionTime = 0;
            }
        }
    }
    
    @Override
    protected final void serverAiStep() {
        ++this.noActionTime;
        this.level.getProfiler().push("checkDespawn");
        this.checkDespawn();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("sensing");
        this.sensing.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("targetSelector");
        this.targetSelector.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("goalSelector");
        this.goalSelector.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("navigation");
        this.navigation.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("mob tick");
        this.customServerAiStep();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("controls");
        this.level.getProfiler().push("move");
        this.moveControl.tick();
        this.level.getProfiler().popPush("look");
        this.lookControl.tick();
        this.level.getProfiler().popPush("jump");
        this.jumpControl.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().pop();
        this.sendDebugPackets();
    }
    
    protected void sendDebugPackets() {
        DebugPackets.sendGoalSelector(this.level, this, this.goalSelector);
    }
    
    protected void customServerAiStep() {
    }
    
    public int getMaxHeadXRot() {
        return 40;
    }
    
    public int getMaxHeadYRot() {
        return 75;
    }
    
    public int getHeadRotSpeed() {
        return 10;
    }
    
    public void lookAt(final Entity aio, final float float2, final float float3) {
        final double double5 = aio.x - this.x;
        final double double6 = aio.z - this.z;
        double double7;
        if (aio instanceof LivingEntity) {
            final LivingEntity aix11 = (LivingEntity)aio;
            double7 = aix11.y + aix11.getEyeHeight() - (this.y + this.getEyeHeight());
        }
        else {
            double7 = (aio.getBoundingBox().minY + aio.getBoundingBox().maxY) / 2.0 - (this.y + this.getEyeHeight());
        }
        final double double8 = Mth.sqrt(double5 * double5 + double6 * double6);
        final float float4 = (float)(Mth.atan2(double6, double5) * 57.2957763671875) - 90.0f;
        final float float5 = (float)(-(Mth.atan2(double7, double8) * 57.2957763671875));
        this.xRot = this.rotlerp(this.xRot, float5, float3);
        this.yRot = this.rotlerp(this.yRot, float4, float2);
    }
    
    private float rotlerp(final float float1, final float float2, final float float3) {
        float float4 = Mth.wrapDegrees(float2 - float1);
        if (float4 > float3) {
            float4 = float3;
        }
        if (float4 < -float3) {
            float4 = -float3;
        }
        return float1 + float4;
    }
    
    public static boolean checkMobSpawnRules(final EntityType<? extends Mob> ais, final LevelAccessor bhs, final MobSpawnType aja, final BlockPos ew, final Random random) {
        final BlockPos ew2 = ew.below();
        return aja == MobSpawnType.SPAWNER || bhs.getBlockState(ew2).isValidSpawn(bhs, ew2, ais);
    }
    
    public boolean checkSpawnRules(final LevelAccessor bhs, final MobSpawnType aja) {
        return true;
    }
    
    public boolean checkSpawnObstruction(final LevelReader bhu) {
        return !bhu.containsAnyLiquid(this.getBoundingBox()) && bhu.isUnobstructed(this);
    }
    
    public int getMaxSpawnClusterSize() {
        return 4;
    }
    
    public boolean isMaxGroupSizeReached(final int integer) {
        return false;
    }
    
    @Override
    public int getMaxFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        int integer2 = (int)(this.getHealth() - this.getMaxHealth() * 0.33f);
        integer2 -= (3 - this.level.getDifficulty().getId()) * 4;
        if (integer2 < 0) {
            integer2 = 0;
        }
        return integer2 + 3;
    }
    
    @Override
    public Iterable<ItemStack> getHandSlots() {
        return (Iterable<ItemStack>)this.handItems;
    }
    
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return (Iterable<ItemStack>)this.armorItems;
    }
    
    @Override
    public ItemStack getItemBySlot(final EquipmentSlot ait) {
        switch (ait.getType()) {
            case HAND: {
                return this.handItems.get(ait.getIndex());
            }
            case ARMOR: {
                return this.armorItems.get(ait.getIndex());
            }
            default: {
                return ItemStack.EMPTY;
            }
        }
    }
    
    @Override
    public void setItemSlot(final EquipmentSlot ait, final ItemStack bcj) {
        switch (ait.getType()) {
            case HAND: {
                this.handItems.set(ait.getIndex(), bcj);
                break;
            }
            case ARMOR: {
                this.armorItems.set(ait.getIndex(), bcj);
                break;
            }
        }
    }
    
    @Override
    protected void dropCustomDeathLoot(final DamageSource ahx, final int integer, final boolean boolean3) {
        super.dropCustomDeathLoot(ahx, integer, boolean3);
        for (final EquipmentSlot ait8 : EquipmentSlot.values()) {
            final ItemStack bcj9 = this.getItemBySlot(ait8);
            final float float10 = this.getEquipmentDropChance(ait8);
            final boolean boolean4 = float10 > 1.0f;
            if (!bcj9.isEmpty() && !EnchantmentHelper.hasVanishingCurse(bcj9) && (boolean3 || boolean4) && this.random.nextFloat() - integer * 0.01f < float10) {
                if (!boolean4 && bcj9.isDamageableItem()) {
                    bcj9.setDamageValue(bcj9.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(bcj9.getMaxDamage() - 3, 1))));
                }
                this.spawnAtLocation(bcj9);
            }
        }
    }
    
    protected float getEquipmentDropChance(final EquipmentSlot ait) {
        float float3 = 0.0f;
        switch (ait.getType()) {
            case HAND: {
                float3 = this.handDropChances[ait.getIndex()];
                break;
            }
            case ARMOR: {
                float3 = this.armorDropChances[ait.getIndex()];
                break;
            }
            default: {
                float3 = 0.0f;
                break;
            }
        }
        return float3;
    }
    
    protected void populateDefaultEquipmentSlots(final DifficultyInstance ahh) {
        if (this.random.nextFloat() < 0.15f * ahh.getSpecialMultiplier()) {
            int integer3 = this.random.nextInt(2);
            final float float4 = (this.level.getDifficulty() == Difficulty.HARD) ? 0.1f : 0.25f;
            if (this.random.nextFloat() < 0.095f) {
                ++integer3;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++integer3;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++integer3;
            }
            boolean boolean5 = true;
            for (final EquipmentSlot ait9 : EquipmentSlot.values()) {
                if (ait9.getType() == EquipmentSlot.Type.ARMOR) {
                    final ItemStack bcj10 = this.getItemBySlot(ait9);
                    if (!boolean5 && this.random.nextFloat() < float4) {
                        break;
                    }
                    boolean5 = false;
                    if (bcj10.isEmpty()) {
                        final Item bce11 = getEquipmentForSlot(ait9, integer3);
                        if (bce11 != null) {
                            this.setItemSlot(ait9, new ItemStack(bce11));
                        }
                    }
                }
            }
        }
    }
    
    public static EquipmentSlot getEquipmentSlotForItem(final ItemStack bcj) {
        final Item bce2 = bcj.getItem();
        if (bce2 == Blocks.CARVED_PUMPKIN.asItem() || (bce2 instanceof BlockItem && ((BlockItem)bce2).getBlock() instanceof AbstractSkullBlock)) {
            return EquipmentSlot.HEAD;
        }
        if (bce2 instanceof ArmorItem) {
            return ((ArmorItem)bce2).getSlot();
        }
        if (bce2 == Items.ELYTRA) {
            return EquipmentSlot.CHEST;
        }
        if (bce2 == Items.SHIELD) {
            return EquipmentSlot.OFFHAND;
        }
        return EquipmentSlot.MAINHAND;
    }
    
    @Nullable
    public static Item getEquipmentForSlot(final EquipmentSlot ait, final int integer) {
        switch (ait) {
            case HEAD: {
                if (integer == 0) {
                    return Items.LEATHER_HELMET;
                }
                if (integer == 1) {
                    return Items.GOLDEN_HELMET;
                }
                if (integer == 2) {
                    return Items.CHAINMAIL_HELMET;
                }
                if (integer == 3) {
                    return Items.IRON_HELMET;
                }
                if (integer == 4) {
                    return Items.DIAMOND_HELMET;
                }
            }
            case CHEST: {
                if (integer == 0) {
                    return Items.LEATHER_CHESTPLATE;
                }
                if (integer == 1) {
                    return Items.GOLDEN_CHESTPLATE;
                }
                if (integer == 2) {
                    return Items.CHAINMAIL_CHESTPLATE;
                }
                if (integer == 3) {
                    return Items.IRON_CHESTPLATE;
                }
                if (integer == 4) {
                    return Items.DIAMOND_CHESTPLATE;
                }
            }
            case LEGS: {
                if (integer == 0) {
                    return Items.LEATHER_LEGGINGS;
                }
                if (integer == 1) {
                    return Items.GOLDEN_LEGGINGS;
                }
                if (integer == 2) {
                    return Items.CHAINMAIL_LEGGINGS;
                }
                if (integer == 3) {
                    return Items.IRON_LEGGINGS;
                }
                if (integer == 4) {
                    return Items.DIAMOND_LEGGINGS;
                }
            }
            case FEET: {
                if (integer == 0) {
                    return Items.LEATHER_BOOTS;
                }
                if (integer == 1) {
                    return Items.GOLDEN_BOOTS;
                }
                if (integer == 2) {
                    return Items.CHAINMAIL_BOOTS;
                }
                if (integer == 3) {
                    return Items.IRON_BOOTS;
                }
                if (integer == 4) {
                    return Items.DIAMOND_BOOTS;
                }
                break;
            }
        }
        return null;
    }
    
    protected void populateDefaultEquipmentEnchantments(final DifficultyInstance ahh) {
        final float float3 = ahh.getSpecialMultiplier();
        if (!this.getMainHandItem().isEmpty() && this.random.nextFloat() < 0.25f * float3) {
            this.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(this.random, this.getMainHandItem(), (int)(5.0f + float3 * this.random.nextInt(18)), false));
        }
        for (final EquipmentSlot ait7 : EquipmentSlot.values()) {
            if (ait7.getType() == EquipmentSlot.Type.ARMOR) {
                final ItemStack bcj8 = this.getItemBySlot(ait7);
                if (!bcj8.isEmpty() && this.random.nextFloat() < 0.5f * float3) {
                    this.setItemSlot(ait7, EnchantmentHelper.enchantItem(this.random, bcj8, (int)(5.0f + float3 * this.random.nextInt(18)), false));
                }
            }
        }
    }
    
    @Nullable
    public SpawnGroupData finalizeSpawn(final LevelAccessor bhs, final DifficultyInstance ahh, final MobSpawnType aja, @Nullable final SpawnGroupData ajj, @Nullable final CompoundTag id) {
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).addModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, AttributeModifier.Operation.MULTIPLY_BASE));
        if (this.random.nextFloat() < 0.05f) {
            this.setLeftHanded(true);
        }
        else {
            this.setLeftHanded(false);
        }
        return ajj;
    }
    
    public boolean canBeControlledByRider() {
        return false;
    }
    
    public void setPersistenceRequired() {
        this.persistenceRequired = true;
    }
    
    public void setDropChance(final EquipmentSlot ait, final float float2) {
        switch (ait.getType()) {
            case HAND: {
                this.handDropChances[ait.getIndex()] = float2;
                break;
            }
            case ARMOR: {
                this.armorDropChances[ait.getIndex()] = float2;
                break;
            }
        }
    }
    
    public boolean canPickUpLoot() {
        return this.canPickUpLoot;
    }
    
    public void setCanPickUpLoot(final boolean boolean1) {
        this.canPickUpLoot = boolean1;
    }
    
    @Override
    public boolean canTakeItem(final ItemStack bcj) {
        final EquipmentSlot ait3 = getEquipmentSlotForItem(bcj);
        return this.getItemBySlot(ait3).isEmpty() && this.canPickUpLoot();
    }
    
    public boolean isPersistenceRequired() {
        return this.persistenceRequired;
    }
    
    @Override
    public final boolean interact(final Player awg, final InteractionHand ahi) {
        if (!this.isAlive()) {
            return false;
        }
        if (this.getLeashHolder() == awg) {
            this.dropLeash(true, !awg.abilities.instabuild);
            return true;
        }
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() == Items.LEAD && this.canBeLeashed(awg)) {
            this.setLeashedTo(awg, true);
            bcj4.shrink(1);
            return true;
        }
        return this.mobInteract(awg, ahi) || super.interact(awg, ahi);
    }
    
    protected boolean mobInteract(final Player awg, final InteractionHand ahi) {
        return false;
    }
    
    public boolean isWithinRestriction() {
        return this.isWithinRestriction(new BlockPos(this));
    }
    
    public boolean isWithinRestriction(final BlockPos ew) {
        return this.restrictRadius == -1.0f || this.restrictCenter.distSqr(ew) < this.restrictRadius * this.restrictRadius;
    }
    
    public void restrictTo(final BlockPos ew, final int integer) {
        this.restrictCenter = ew;
        this.restrictRadius = (float)integer;
    }
    
    public BlockPos getRestrictCenter() {
        return this.restrictCenter;
    }
    
    public float getRestrictRadius() {
        return this.restrictRadius;
    }
    
    public boolean hasRestriction() {
        return this.restrictRadius != -1.0f;
    }
    
    protected void tickLeash() {
        if (this.leashInfoTag != null) {
            this.restoreLeashFromSave();
        }
        if (this.leashHolder == null) {
            return;
        }
        if (!this.isAlive() || !this.leashHolder.isAlive()) {
            this.dropLeash(true, true);
        }
    }
    
    public void dropLeash(final boolean boolean1, final boolean boolean2) {
        if (this.leashHolder != null) {
            this.forcedLoading = false;
            if (!(this.leashHolder instanceof Player)) {
                this.leashHolder.forcedLoading = false;
            }
            this.leashHolder = null;
            if (!this.level.isClientSide && boolean2) {
                this.spawnAtLocation(Items.LEAD);
            }
            if (!this.level.isClientSide && boolean1 && this.level instanceof ServerLevel) {
                ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, null));
            }
        }
    }
    
    public boolean canBeLeashed(final Player awg) {
        return !this.isLeashed() && !(this instanceof Enemy);
    }
    
    public boolean isLeashed() {
        return this.leashHolder != null;
    }
    
    @Nullable
    public Entity getLeashHolder() {
        if (this.leashHolder == null && this.delayedLeashHolderId != 0 && this.level.isClientSide) {
            this.leashHolder = this.level.getEntity(this.delayedLeashHolderId);
        }
        return this.leashHolder;
    }
    
    public void setLeashedTo(final Entity aio, final boolean boolean2) {
        this.leashHolder = aio;
        this.forcedLoading = true;
        if (!(this.leashHolder instanceof Player)) {
            this.leashHolder.forcedLoading = true;
        }
        if (!this.level.isClientSide && boolean2 && this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, this.leashHolder));
        }
        if (this.isPassenger()) {
            this.stopRiding();
        }
    }
    
    public void setDelayedLeashHolderId(final int integer) {
        this.delayedLeashHolderId = integer;
        this.dropLeash(false, false);
    }
    
    @Override
    public boolean startRiding(final Entity aio, final boolean boolean2) {
        final boolean boolean3 = super.startRiding(aio, boolean2);
        if (boolean3 && this.isLeashed()) {
            this.dropLeash(true, true);
        }
        return boolean3;
    }
    
    private void restoreLeashFromSave() {
        if (this.leashInfoTag != null && this.level instanceof ServerLevel) {
            if (this.leashInfoTag.hasUUID("UUID")) {
                final UUID uUID2 = this.leashInfoTag.getUUID("UUID");
                final Entity aio3 = ((ServerLevel)this.level).getEntity(uUID2);
                if (aio3 != null) {
                    this.setLeashedTo(aio3, true);
                }
            }
            else if (this.leashInfoTag.contains("X", 99) && this.leashInfoTag.contains("Y", 99) && this.leashInfoTag.contains("Z", 99)) {
                final BlockPos ew2 = new BlockPos(this.leashInfoTag.getInt("X"), this.leashInfoTag.getInt("Y"), this.leashInfoTag.getInt("Z"));
                this.setLeashedTo(LeashFenceKnotEntity.getOrCreateKnot(this.level, ew2), true);
            }
            else {
                this.dropLeash(false, true);
            }
            this.leashInfoTag = null;
        }
    }
    
    @Override
    public boolean setSlot(final int integer, final ItemStack bcj) {
        EquipmentSlot ait4;
        if (integer == 98) {
            ait4 = EquipmentSlot.MAINHAND;
        }
        else if (integer == 99) {
            ait4 = EquipmentSlot.OFFHAND;
        }
        else if (integer == 100 + EquipmentSlot.HEAD.getIndex()) {
            ait4 = EquipmentSlot.HEAD;
        }
        else if (integer == 100 + EquipmentSlot.CHEST.getIndex()) {
            ait4 = EquipmentSlot.CHEST;
        }
        else if (integer == 100 + EquipmentSlot.LEGS.getIndex()) {
            ait4 = EquipmentSlot.LEGS;
        }
        else {
            if (integer != 100 + EquipmentSlot.FEET.getIndex()) {
                return false;
            }
            ait4 = EquipmentSlot.FEET;
        }
        if (bcj.isEmpty() || isValidSlotForItem(ait4, bcj) || ait4 == EquipmentSlot.HEAD) {
            this.setItemSlot(ait4, bcj);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isControlledByLocalInstance() {
        return this.canBeControlledByRider() && super.isControlledByLocalInstance();
    }
    
    public static boolean isValidSlotForItem(final EquipmentSlot ait, final ItemStack bcj) {
        final EquipmentSlot ait2 = getEquipmentSlotForItem(bcj);
        return ait2 == ait || (ait2 == EquipmentSlot.MAINHAND && ait == EquipmentSlot.OFFHAND) || (ait2 == EquipmentSlot.OFFHAND && ait == EquipmentSlot.MAINHAND);
    }
    
    @Override
    public boolean isEffectiveAi() {
        return super.isEffectiveAi() && !this.isNoAi();
    }
    
    public void setNoAi(final boolean boolean1) {
        final byte byte3 = this.entityData.<Byte>get(Mob.DATA_MOB_FLAGS_ID);
        this.entityData.<Byte>set(Mob.DATA_MOB_FLAGS_ID, boolean1 ? ((byte)(byte3 | 0x1)) : ((byte)(byte3 & 0xFFFFFFFE)));
    }
    
    public void setLeftHanded(final boolean boolean1) {
        final byte byte3 = this.entityData.<Byte>get(Mob.DATA_MOB_FLAGS_ID);
        this.entityData.<Byte>set(Mob.DATA_MOB_FLAGS_ID, boolean1 ? ((byte)(byte3 | 0x2)) : ((byte)(byte3 & 0xFFFFFFFD)));
    }
    
    public void setAggressive(final boolean boolean1) {
        final byte byte3 = this.entityData.<Byte>get(Mob.DATA_MOB_FLAGS_ID);
        this.entityData.<Byte>set(Mob.DATA_MOB_FLAGS_ID, boolean1 ? ((byte)(byte3 | 0x4)) : ((byte)(byte3 & 0xFFFFFFFB)));
    }
    
    public boolean isNoAi() {
        return (this.entityData.<Byte>get(Mob.DATA_MOB_FLAGS_ID) & 0x1) != 0x0;
    }
    
    public boolean isLeftHanded() {
        return (this.entityData.<Byte>get(Mob.DATA_MOB_FLAGS_ID) & 0x2) != 0x0;
    }
    
    public boolean isAggressive() {
        return (this.entityData.<Byte>get(Mob.DATA_MOB_FLAGS_ID) & 0x4) != 0x0;
    }
    
    @Override
    public HumanoidArm getMainArm() {
        return this.isLeftHanded() ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }
    
    @Override
    public boolean canAttack(final LivingEntity aix) {
        return (aix.getType() != EntityType.PLAYER || !((Player)aix).abilities.invulnerable) && super.canAttack(aix);
    }
    
    @Override
    public boolean doHurtTarget(final Entity aio) {
        float float3 = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
        float float4 = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).getValue();
        if (aio instanceof LivingEntity) {
            float3 += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)aio).getMobType());
            float4 += EnchantmentHelper.getKnockbackBonus(this);
        }
        final int integer5 = EnchantmentHelper.getFireAspect(this);
        if (integer5 > 0) {
            aio.setSecondsOnFire(integer5 * 4);
        }
        final boolean boolean6 = aio.hurt(DamageSource.mobAttack(this), float3);
        if (boolean6) {
            if (float4 > 0.0f && aio instanceof LivingEntity) {
                ((LivingEntity)aio).knockback(this, float4 * 0.5f, Mth.sin(this.yRot * 0.017453292f), -Mth.cos(this.yRot * 0.017453292f));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }
            if (aio instanceof Player) {
                final Player awg7 = (Player)aio;
                final ItemStack bcj8 = this.getMainHandItem();
                final ItemStack bcj9 = awg7.isUsingItem() ? awg7.getUseItem() : ItemStack.EMPTY;
                if (!bcj8.isEmpty() && !bcj9.isEmpty() && bcj8.getItem() instanceof AxeItem && bcj9.getItem() == Items.SHIELD) {
                    final float float5 = 0.25f + EnchantmentHelper.getBlockEfficiency(this) * 0.05f;
                    if (this.random.nextFloat() < float5) {
                        awg7.getCooldowns().addCooldown(Items.SHIELD, 100);
                        this.level.broadcastEntityEvent(awg7, (byte)30);
                    }
                }
            }
            this.doEnchantDamageEffects(this, aio);
        }
        return boolean6;
    }
    
    protected boolean isSunBurnTick() {
        if (this.level.isDay() && !this.level.isClientSide) {
            final float float2 = this.getBrightness();
            final BlockPos ew3 = (this.getVehicle() instanceof Boat) ? new BlockPos(this.x, (double)Math.round(this.y), this.z).above() : new BlockPos(this.x, (double)Math.round(this.y), this.z);
            if (float2 > 0.5f && this.random.nextFloat() * 30.0f < (float2 - 0.4f) * 2.0f && this.level.canSeeSky(ew3)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void jumpInLiquid(final net.minecraft.tags.Tag<Fluid> zg) {
        if (this.getNavigation().canFloat()) {
            super.jumpInLiquid(zg);
        }
        else {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.3, 0.0));
        }
    }
    
    public boolean isHolding(final Item bce) {
        return this.getMainHandItem().getItem() == bce || this.getOffhandItem().getItem() == bce;
    }
    
    static {
        DATA_MOB_FLAGS_ID = SynchedEntityData.<Byte>defineId(Mob.class, EntityDataSerializers.BYTE);
    }
}
