package net.minecraft.world.entity.decoration;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.global.LightningBolt;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.LivingEntity;

public class ArmorStand extends LivingEntity {
    private static final Rotations DEFAULT_HEAD_POSE;
    private static final Rotations DEFAULT_BODY_POSE;
    private static final Rotations DEFAULT_LEFT_ARM_POSE;
    private static final Rotations DEFAULT_RIGHT_ARM_POSE;
    private static final Rotations DEFAULT_LEFT_LEG_POSE;
    private static final Rotations DEFAULT_RIGHT_LEG_POSE;
    public static final EntityDataAccessor<Byte> DATA_CLIENT_FLAGS;
    public static final EntityDataAccessor<Rotations> DATA_HEAD_POSE;
    public static final EntityDataAccessor<Rotations> DATA_BODY_POSE;
    public static final EntityDataAccessor<Rotations> DATA_LEFT_ARM_POSE;
    public static final EntityDataAccessor<Rotations> DATA_RIGHT_ARM_POSE;
    public static final EntityDataAccessor<Rotations> DATA_LEFT_LEG_POSE;
    public static final EntityDataAccessor<Rotations> DATA_RIGHT_LEG_POSE;
    private static final Predicate<Entity> RIDABLE_MINECARTS;
    private final NonNullList<ItemStack> handItems;
    private final NonNullList<ItemStack> armorItems;
    private boolean invisible;
    public long lastHit;
    private int disabledSlots;
    private Rotations headPose;
    private Rotations bodyPose;
    private Rotations leftArmPose;
    private Rotations rightArmPose;
    private Rotations leftLegPose;
    private Rotations rightLegPose;
    
    public ArmorStand(final EntityType<? extends ArmorStand> ais, final Level bhr) {
        super(ais, bhr);
        this.handItems = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
        this.armorItems = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
        this.headPose = ArmorStand.DEFAULT_HEAD_POSE;
        this.bodyPose = ArmorStand.DEFAULT_BODY_POSE;
        this.leftArmPose = ArmorStand.DEFAULT_LEFT_ARM_POSE;
        this.rightArmPose = ArmorStand.DEFAULT_RIGHT_ARM_POSE;
        this.leftLegPose = ArmorStand.DEFAULT_LEFT_LEG_POSE;
        this.rightLegPose = ArmorStand.DEFAULT_RIGHT_LEG_POSE;
        this.maxUpStep = 0.0f;
    }
    
    public ArmorStand(final Level bhr, final double double2, final double double3, final double double4) {
        this(EntityType.ARMOR_STAND, bhr);
        this.setPos(double2, double3, double4);
    }
    
    @Override
    public void refreshDimensions() {
        final double double2 = this.x;
        final double double3 = this.y;
        final double double4 = this.z;
        super.refreshDimensions();
        this.setPos(double2, double3, double4);
    }
    
    private boolean hasPhysics() {
        return !this.isMarker() && !this.isNoGravity();
    }
    
    @Override
    public boolean isEffectiveAi() {
        return super.isEffectiveAi() && this.hasPhysics();
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Byte>define(ArmorStand.DATA_CLIENT_FLAGS, (Byte)0);
        this.entityData.<Rotations>define(ArmorStand.DATA_HEAD_POSE, ArmorStand.DEFAULT_HEAD_POSE);
        this.entityData.<Rotations>define(ArmorStand.DATA_BODY_POSE, ArmorStand.DEFAULT_BODY_POSE);
        this.entityData.<Rotations>define(ArmorStand.DATA_LEFT_ARM_POSE, ArmorStand.DEFAULT_LEFT_ARM_POSE);
        this.entityData.<Rotations>define(ArmorStand.DATA_RIGHT_ARM_POSE, ArmorStand.DEFAULT_RIGHT_ARM_POSE);
        this.entityData.<Rotations>define(ArmorStand.DATA_LEFT_LEG_POSE, ArmorStand.DEFAULT_LEFT_LEG_POSE);
        this.entityData.<Rotations>define(ArmorStand.DATA_RIGHT_LEG_POSE, ArmorStand.DEFAULT_RIGHT_LEG_POSE);
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
                this.playEquipSound(bcj);
                this.handItems.set(ait.getIndex(), bcj);
                break;
            }
            case ARMOR: {
                this.playEquipSound(bcj);
                this.armorItems.set(ait.getIndex(), bcj);
                break;
            }
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
        if (bcj.isEmpty() || Mob.isValidSlotForItem(ait4, bcj) || ait4 == EquipmentSlot.HEAD) {
            this.setItemSlot(ait4, bcj);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean canTakeItem(final ItemStack bcj) {
        final EquipmentSlot ait3 = Mob.getEquipmentSlotForItem(bcj);
        return this.getItemBySlot(ait3).isEmpty() && !this.isDisabled(ait3);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
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
        id.putBoolean("Invisible", this.isInvisible());
        id.putBoolean("Small", this.isSmall());
        id.putBoolean("ShowArms", this.isShowArms());
        id.putInt("DisabledSlots", this.disabledSlots);
        id.putBoolean("NoBasePlate", this.isNoBasePlate());
        if (this.isMarker()) {
            id.putBoolean("Marker", this.isMarker());
        }
        id.put("Pose", (Tag)this.writePose());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
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
        this.setInvisible(id.getBoolean("Invisible"));
        this.setSmall(id.getBoolean("Small"));
        this.setShowArms(id.getBoolean("ShowArms"));
        this.disabledSlots = id.getInt("DisabledSlots");
        this.setNoBasePlate(id.getBoolean("NoBasePlate"));
        this.setMarker(id.getBoolean("Marker"));
        this.noPhysics = !this.hasPhysics();
        final CompoundTag id2 = id.getCompound("Pose");
        this.readPose(id2);
    }
    
    private void readPose(final CompoundTag id) {
        final ListTag ik3 = id.getList("Head", 5);
        this.setHeadPose(ik3.isEmpty() ? ArmorStand.DEFAULT_HEAD_POSE : new Rotations(ik3));
        final ListTag ik4 = id.getList("Body", 5);
        this.setBodyPose(ik4.isEmpty() ? ArmorStand.DEFAULT_BODY_POSE : new Rotations(ik4));
        final ListTag ik5 = id.getList("LeftArm", 5);
        this.setLeftArmPose(ik5.isEmpty() ? ArmorStand.DEFAULT_LEFT_ARM_POSE : new Rotations(ik5));
        final ListTag ik6 = id.getList("RightArm", 5);
        this.setRightArmPose(ik6.isEmpty() ? ArmorStand.DEFAULT_RIGHT_ARM_POSE : new Rotations(ik6));
        final ListTag ik7 = id.getList("LeftLeg", 5);
        this.setLeftLegPose(ik7.isEmpty() ? ArmorStand.DEFAULT_LEFT_LEG_POSE : new Rotations(ik7));
        final ListTag ik8 = id.getList("RightLeg", 5);
        this.setRightLegPose(ik8.isEmpty() ? ArmorStand.DEFAULT_RIGHT_LEG_POSE : new Rotations(ik8));
    }
    
    private CompoundTag writePose() {
        final CompoundTag id2 = new CompoundTag();
        if (!ArmorStand.DEFAULT_HEAD_POSE.equals(this.headPose)) {
            id2.put("Head", (Tag)this.headPose.save());
        }
        if (!ArmorStand.DEFAULT_BODY_POSE.equals(this.bodyPose)) {
            id2.put("Body", (Tag)this.bodyPose.save());
        }
        if (!ArmorStand.DEFAULT_LEFT_ARM_POSE.equals(this.leftArmPose)) {
            id2.put("LeftArm", (Tag)this.leftArmPose.save());
        }
        if (!ArmorStand.DEFAULT_RIGHT_ARM_POSE.equals(this.rightArmPose)) {
            id2.put("RightArm", (Tag)this.rightArmPose.save());
        }
        if (!ArmorStand.DEFAULT_LEFT_LEG_POSE.equals(this.leftLegPose)) {
            id2.put("LeftLeg", (Tag)this.leftLegPose.save());
        }
        if (!ArmorStand.DEFAULT_RIGHT_LEG_POSE.equals(this.rightLegPose)) {
            id2.put("RightLeg", (Tag)this.rightLegPose.save());
        }
        return id2;
    }
    
    @Override
    public boolean isPushable() {
        return false;
    }
    
    @Override
    protected void doPush(final Entity aio) {
    }
    
    @Override
    protected void pushEntities() {
        final List<Entity> list2 = this.level.getEntities(this, this.getBoundingBox(), ArmorStand.RIDABLE_MINECARTS);
        for (int integer3 = 0; integer3 < list2.size(); ++integer3) {
            final Entity aio4 = (Entity)list2.get(integer3);
            if (this.distanceToSqr(aio4) <= 0.2) {
                aio4.push(this);
            }
        }
    }
    
    @Override
    public InteractionResult interactAt(final Player awg, final Vec3 csi, final InteractionHand ahi) {
        final ItemStack bcj5 = awg.getItemInHand(ahi);
        if (this.isMarker() || bcj5.getItem() == Items.NAME_TAG) {
            return InteractionResult.PASS;
        }
        if (this.level.isClientSide || awg.isSpectator()) {
            return InteractionResult.SUCCESS;
        }
        final EquipmentSlot ait6 = Mob.getEquipmentSlotForItem(bcj5);
        if (bcj5.isEmpty()) {
            final EquipmentSlot ait7 = this.getClickedSlot(csi);
            final EquipmentSlot ait8 = this.isDisabled(ait7) ? ait6 : ait7;
            if (this.hasItemInSlot(ait8)) {
                this.swapItem(awg, ait8, bcj5, ahi);
            }
        }
        else {
            if (this.isDisabled(ait6)) {
                return InteractionResult.FAIL;
            }
            if (ait6.getType() == EquipmentSlot.Type.HAND && !this.isShowArms()) {
                return InteractionResult.FAIL;
            }
            this.swapItem(awg, ait6, bcj5, ahi);
        }
        return InteractionResult.SUCCESS;
    }
    
    protected EquipmentSlot getClickedSlot(final Vec3 csi) {
        EquipmentSlot ait3 = EquipmentSlot.MAINHAND;
        final boolean boolean4 = this.isSmall();
        final double double5 = boolean4 ? (csi.y * 2.0) : csi.y;
        final EquipmentSlot ait4 = EquipmentSlot.FEET;
        if (double5 >= 0.1 && double5 < 0.1 + (boolean4 ? 0.8 : 0.45) && this.hasItemInSlot(ait4)) {
            ait3 = EquipmentSlot.FEET;
        }
        else if (double5 >= 0.9 + (boolean4 ? 0.3 : 0.0) && double5 < 0.9 + (boolean4 ? 1.0 : 0.7) && this.hasItemInSlot(EquipmentSlot.CHEST)) {
            ait3 = EquipmentSlot.CHEST;
        }
        else if (double5 >= 0.4 && double5 < 0.4 + (boolean4 ? 1.0 : 0.8) && this.hasItemInSlot(EquipmentSlot.LEGS)) {
            ait3 = EquipmentSlot.LEGS;
        }
        else if (double5 >= 1.6 && this.hasItemInSlot(EquipmentSlot.HEAD)) {
            ait3 = EquipmentSlot.HEAD;
        }
        else if (!this.hasItemInSlot(EquipmentSlot.MAINHAND) && this.hasItemInSlot(EquipmentSlot.OFFHAND)) {
            ait3 = EquipmentSlot.OFFHAND;
        }
        return ait3;
    }
    
    public boolean isDisabled(final EquipmentSlot ait) {
        return (this.disabledSlots & 1 << ait.getFilterFlag()) != 0x0 || (ait.getType() == EquipmentSlot.Type.HAND && !this.isShowArms());
    }
    
    private void swapItem(final Player awg, final EquipmentSlot ait, final ItemStack bcj, final InteractionHand ahi) {
        final ItemStack bcj2 = this.getItemBySlot(ait);
        if (!bcj2.isEmpty() && (this.disabledSlots & 1 << ait.getFilterFlag() + 8) != 0x0) {
            return;
        }
        if (bcj2.isEmpty() && (this.disabledSlots & 1 << ait.getFilterFlag() + 16) != 0x0) {
            return;
        }
        if (awg.abilities.instabuild && bcj2.isEmpty() && !bcj.isEmpty()) {
            final ItemStack bcj3 = bcj.copy();
            bcj3.setCount(1);
            this.setItemSlot(ait, bcj3);
            return;
        }
        if (bcj.isEmpty() || bcj.getCount() <= 1) {
            this.setItemSlot(ait, bcj);
            awg.setItemInHand(ahi, bcj2);
            return;
        }
        if (!bcj2.isEmpty()) {
            return;
        }
        final ItemStack bcj3 = bcj.copy();
        bcj3.setCount(1);
        this.setItemSlot(ait, bcj3);
        bcj.shrink(1);
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.level.isClientSide || this.removed) {
            return false;
        }
        if (DamageSource.OUT_OF_WORLD.equals(ahx)) {
            this.remove();
            return false;
        }
        if (this.isInvulnerableTo(ahx) || this.invisible || this.isMarker()) {
            return false;
        }
        if (ahx.isExplosion()) {
            this.brokenByAnything(ahx);
            this.remove();
            return false;
        }
        if (DamageSource.IN_FIRE.equals(ahx)) {
            if (this.isOnFire()) {
                this.causeDamage(ahx, 0.15f);
            }
            else {
                this.setSecondsOnFire(5);
            }
            return false;
        }
        if (DamageSource.ON_FIRE.equals(ahx) && this.getHealth() > 0.5f) {
            this.causeDamage(ahx, 4.0f);
            return false;
        }
        final boolean boolean4 = ahx.getDirectEntity() instanceof AbstractArrow;
        final boolean boolean5 = boolean4 && ((AbstractArrow)ahx.getDirectEntity()).getPierceLevel() > 0;
        final boolean boolean6 = "player".equals(ahx.getMsgId());
        if (!boolean6 && !boolean4) {
            return false;
        }
        if (ahx.getEntity() instanceof Player && !((Player)ahx.getEntity()).abilities.mayBuild) {
            return false;
        }
        if (ahx.isCreativePlayer()) {
            this.playBrokenSound();
            this.showBreakingParticles();
            this.remove();
            return boolean5;
        }
        final long long7 = this.level.getGameTime();
        if (long7 - this.lastHit <= 5L || boolean4) {
            this.brokenByPlayer(ahx);
            this.showBreakingParticles();
            this.remove();
        }
        else {
            this.level.broadcastEntityEvent(this, (byte)32);
            this.lastHit = long7;
        }
        return true;
    }
    
    @Override
    public void handleEntityEvent(final byte byte1) {
        if (byte1 == 32) {
            if (this.level.isClientSide) {
                this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3f, 1.0f, false);
                this.lastHit = this.level.getGameTime();
            }
        }
        else {
            super.handleEntityEvent(byte1);
        }
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        double double2 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(double2) || double2 == 0.0) {
            double2 = 4.0;
        }
        double2 *= 64.0;
        return double1 < double2 * double2;
    }
    
    private void showBreakingParticles() {
        if (this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).<BlockParticleOption>sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), this.x, this.y + this.getBbHeight() / 1.5, this.z, 10, this.getBbWidth() / 4.0f, this.getBbHeight() / 4.0f, this.getBbWidth() / 4.0f, 0.05);
        }
    }
    
    private void causeDamage(final DamageSource ahx, final float float2) {
        float float3 = this.getHealth();
        float3 -= float2;
        if (float3 <= 0.5f) {
            this.brokenByAnything(ahx);
            this.remove();
        }
        else {
            this.setHealth(float3);
        }
    }
    
    private void brokenByPlayer(final DamageSource ahx) {
        Block.popResource(this.level, new BlockPos(this), new ItemStack(Items.ARMOR_STAND));
        this.brokenByAnything(ahx);
    }
    
    private void brokenByAnything(final DamageSource ahx) {
        this.playBrokenSound();
        this.dropAllDeathLoot(ahx);
        for (int integer3 = 0; integer3 < this.handItems.size(); ++integer3) {
            final ItemStack bcj4 = this.handItems.get(integer3);
            if (!bcj4.isEmpty()) {
                Block.popResource(this.level, new BlockPos(this).above(), bcj4);
                this.handItems.set(integer3, ItemStack.EMPTY);
            }
        }
        for (int integer3 = 0; integer3 < this.armorItems.size(); ++integer3) {
            final ItemStack bcj4 = this.armorItems.get(integer3);
            if (!bcj4.isEmpty()) {
                Block.popResource(this.level, new BlockPos(this).above(), bcj4);
                this.armorItems.set(integer3, ItemStack.EMPTY);
            }
        }
    }
    
    private void playBrokenSound() {
        this.level.playSound(null, this.x, this.y, this.z, SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0f, 1.0f);
    }
    
    @Override
    protected float tickHeadTurn(final float float1, final float float2) {
        this.yBodyRotO = this.yRotO;
        this.yBodyRot = this.yRot;
        return 0.0f;
    }
    
    @Override
    protected float getStandingEyeHeight(final Pose ajh, final EntityDimensions aip) {
        return aip.height * (this.isBaby() ? 0.5f : 0.9f);
    }
    
    @Override
    public double getRidingHeight() {
        return this.isMarker() ? 0.0 : 0.10000000149011612;
    }
    
    @Override
    public void travel(final Vec3 csi) {
        if (!this.hasPhysics()) {
            return;
        }
        super.travel(csi);
    }
    
    @Override
    public void setYBodyRot(final float float1) {
        this.yRotO = float1;
        this.yBodyRotO = float1;
        this.yHeadRot = float1;
        this.yHeadRotO = float1;
    }
    
    @Override
    public void setYHeadRot(final float float1) {
        this.yRotO = float1;
        this.yBodyRotO = float1;
        this.yHeadRot = float1;
        this.yHeadRotO = float1;
    }
    
    @Override
    public void tick() {
        super.tick();
        final Rotations fo2 = this.entityData.<Rotations>get(ArmorStand.DATA_HEAD_POSE);
        if (!this.headPose.equals(fo2)) {
            this.setHeadPose(fo2);
        }
        final Rotations fo3 = this.entityData.<Rotations>get(ArmorStand.DATA_BODY_POSE);
        if (!this.bodyPose.equals(fo3)) {
            this.setBodyPose(fo3);
        }
        final Rotations fo4 = this.entityData.<Rotations>get(ArmorStand.DATA_LEFT_ARM_POSE);
        if (!this.leftArmPose.equals(fo4)) {
            this.setLeftArmPose(fo4);
        }
        final Rotations fo5 = this.entityData.<Rotations>get(ArmorStand.DATA_RIGHT_ARM_POSE);
        if (!this.rightArmPose.equals(fo5)) {
            this.setRightArmPose(fo5);
        }
        final Rotations fo6 = this.entityData.<Rotations>get(ArmorStand.DATA_LEFT_LEG_POSE);
        if (!this.leftLegPose.equals(fo6)) {
            this.setLeftLegPose(fo6);
        }
        final Rotations fo7 = this.entityData.<Rotations>get(ArmorStand.DATA_RIGHT_LEG_POSE);
        if (!this.rightLegPose.equals(fo7)) {
            this.setRightLegPose(fo7);
        }
    }
    
    @Override
    protected void updateInvisibilityStatus() {
        this.setInvisible(this.invisible);
    }
    
    @Override
    public void setInvisible(final boolean boolean1) {
        super.setInvisible(this.invisible = boolean1);
    }
    
    @Override
    public boolean isBaby() {
        return this.isSmall();
    }
    
    @Override
    public void kill() {
        this.remove();
    }
    
    @Override
    public boolean ignoreExplosion() {
        return this.isInvisible();
    }
    
    @Override
    public PushReaction getPistonPushReaction() {
        if (this.isMarker()) {
            return PushReaction.IGNORE;
        }
        return super.getPistonPushReaction();
    }
    
    private void setSmall(final boolean boolean1) {
        this.entityData.<Byte>set(ArmorStand.DATA_CLIENT_FLAGS, this.setBit(this.entityData.<Byte>get(ArmorStand.DATA_CLIENT_FLAGS), 1, boolean1));
    }
    
    public boolean isSmall() {
        return (this.entityData.<Byte>get(ArmorStand.DATA_CLIENT_FLAGS) & 0x1) != 0x0;
    }
    
    private void setShowArms(final boolean boolean1) {
        this.entityData.<Byte>set(ArmorStand.DATA_CLIENT_FLAGS, this.setBit(this.entityData.<Byte>get(ArmorStand.DATA_CLIENT_FLAGS), 4, boolean1));
    }
    
    public boolean isShowArms() {
        return (this.entityData.<Byte>get(ArmorStand.DATA_CLIENT_FLAGS) & 0x4) != 0x0;
    }
    
    private void setNoBasePlate(final boolean boolean1) {
        this.entityData.<Byte>set(ArmorStand.DATA_CLIENT_FLAGS, this.setBit(this.entityData.<Byte>get(ArmorStand.DATA_CLIENT_FLAGS), 8, boolean1));
    }
    
    public boolean isNoBasePlate() {
        return (this.entityData.<Byte>get(ArmorStand.DATA_CLIENT_FLAGS) & 0x8) != 0x0;
    }
    
    private void setMarker(final boolean boolean1) {
        this.entityData.<Byte>set(ArmorStand.DATA_CLIENT_FLAGS, this.setBit(this.entityData.<Byte>get(ArmorStand.DATA_CLIENT_FLAGS), 16, boolean1));
    }
    
    public boolean isMarker() {
        return (this.entityData.<Byte>get(ArmorStand.DATA_CLIENT_FLAGS) & 0x10) != 0x0;
    }
    
    private byte setBit(byte byte1, final int integer, final boolean boolean3) {
        if (boolean3) {
            byte1 |= (byte)integer;
        }
        else {
            byte1 &= (byte)~integer;
        }
        return byte1;
    }
    
    public void setHeadPose(final Rotations fo) {
        this.headPose = fo;
        this.entityData.<Rotations>set(ArmorStand.DATA_HEAD_POSE, fo);
    }
    
    public void setBodyPose(final Rotations fo) {
        this.bodyPose = fo;
        this.entityData.<Rotations>set(ArmorStand.DATA_BODY_POSE, fo);
    }
    
    public void setLeftArmPose(final Rotations fo) {
        this.leftArmPose = fo;
        this.entityData.<Rotations>set(ArmorStand.DATA_LEFT_ARM_POSE, fo);
    }
    
    public void setRightArmPose(final Rotations fo) {
        this.rightArmPose = fo;
        this.entityData.<Rotations>set(ArmorStand.DATA_RIGHT_ARM_POSE, fo);
    }
    
    public void setLeftLegPose(final Rotations fo) {
        this.leftLegPose = fo;
        this.entityData.<Rotations>set(ArmorStand.DATA_LEFT_LEG_POSE, fo);
    }
    
    public void setRightLegPose(final Rotations fo) {
        this.rightLegPose = fo;
        this.entityData.<Rotations>set(ArmorStand.DATA_RIGHT_LEG_POSE, fo);
    }
    
    public Rotations getHeadPose() {
        return this.headPose;
    }
    
    public Rotations getBodyPose() {
        return this.bodyPose;
    }
    
    public Rotations getLeftArmPose() {
        return this.leftArmPose;
    }
    
    public Rotations getRightArmPose() {
        return this.rightArmPose;
    }
    
    public Rotations getLeftLegPose() {
        return this.leftLegPose;
    }
    
    public Rotations getRightLegPose() {
        return this.rightLegPose;
    }
    
    @Override
    public boolean isPickable() {
        return super.isPickable() && !this.isMarker();
    }
    
    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
    
    @Override
    protected SoundEvent getFallDamageSound(final int integer) {
        return SoundEvents.ARMOR_STAND_FALL;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource ahx) {
        return SoundEvents.ARMOR_STAND_HIT;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }
    
    @Override
    public void thunderHit(final LightningBolt atu) {
    }
    
    @Override
    public boolean isAffectedByPotions() {
        return false;
    }
    
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> qk) {
        if (ArmorStand.DATA_CLIENT_FLAGS.equals(qk)) {
            this.refreshDimensions();
            this.blocksBuilding = !this.isMarker();
        }
        super.onSyncedDataUpdated(qk);
    }
    
    @Override
    public boolean attackable() {
        return false;
    }
    
    @Override
    public EntityDimensions getDimensions(final Pose ajh) {
        final float float3 = this.isMarker() ? 0.0f : (this.isBaby() ? 0.5f : 1.0f);
        return this.getType().getDimensions().scale(float3);
    }
    
    static {
        DEFAULT_HEAD_POSE = new Rotations(0.0f, 0.0f, 0.0f);
        DEFAULT_BODY_POSE = new Rotations(0.0f, 0.0f, 0.0f);
        DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0f, 0.0f, -10.0f);
        DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0f, 0.0f, 10.0f);
        DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0f, 0.0f, -1.0f);
        DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0f, 0.0f, 1.0f);
        DATA_CLIENT_FLAGS = SynchedEntityData.<Byte>defineId(ArmorStand.class, EntityDataSerializers.BYTE);
        DATA_HEAD_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
        DATA_BODY_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
        DATA_LEFT_ARM_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
        DATA_RIGHT_ARM_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
        DATA_LEFT_LEG_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
        DATA_RIGHT_LEG_POSE = SynchedEntityData.<Rotations>defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
        RIDABLE_MINECARTS = (aio -> aio instanceof AbstractMinecart && ((AbstractMinecart)aio).getMinecartType() == AbstractMinecart.Type.RIDEABLE);
    }
}
