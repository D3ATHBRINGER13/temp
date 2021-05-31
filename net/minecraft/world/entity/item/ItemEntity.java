package net.minecraft.world.entity.item;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import javax.annotation.Nullable;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import net.minecraft.world.damagesource.DamageSource;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MoverType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import java.util.UUID;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

public class ItemEntity extends Entity {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM;
    private int age;
    private int pickupDelay;
    private int health;
    private UUID thrower;
    private UUID owner;
    public final float bobOffs;
    
    public ItemEntity(final EntityType<? extends ItemEntity> ais, final Level bhr) {
        super(ais, bhr);
        this.health = 5;
        this.bobOffs = (float)(Math.random() * 3.141592653589793 * 2.0);
    }
    
    public ItemEntity(final Level bhr, final double double2, final double double3, final double double4) {
        this(EntityType.ITEM, bhr);
        this.setPos(double2, double3, double4);
        this.yRot = this.random.nextFloat() * 360.0f;
        this.setDeltaMovement(this.random.nextDouble() * 0.2 - 0.1, 0.2, this.random.nextDouble() * 0.2 - 0.1);
    }
    
    public ItemEntity(final Level bhr, final double double2, final double double3, final double double4, final ItemStack bcj) {
        this(bhr, double2, double3, double4);
        this.setItem(bcj);
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected void defineSynchedData() {
        this.getEntityData().<ItemStack>define(ItemEntity.DATA_ITEM, ItemStack.EMPTY);
    }
    
    @Override
    public void tick() {
        if (this.getItem().isEmpty()) {
            this.remove();
            return;
        }
        super.tick();
        if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        final Vec3 csi2 = this.getDeltaMovement();
        if (this.isUnderLiquid(FluidTags.WATER)) {
            this.setUnderwaterMovement();
        }
        else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        if (this.level.isClientSide) {
            this.noPhysics = false;
        }
        else {
            this.noPhysics = !this.level.noCollision(this);
            if (this.noPhysics) {
                this.checkInBlock(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
            }
        }
        if (!this.onGround || Entity.getHorizontalDistanceSqr(this.getDeltaMovement()) > 9.999999747378752E-6 || (this.tickCount + this.getId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float float3 = 0.98f;
            if (this.onGround) {
                float3 = this.level.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFriction() * 0.98f;
            }
            this.setDeltaMovement(this.getDeltaMovement().multiply(float3, 0.98, float3));
            if (this.onGround) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, -0.5, 1.0));
            }
        }
        final boolean boolean3 = Mth.floor(this.xo) != Mth.floor(this.x) || Mth.floor(this.yo) != Mth.floor(this.y) || Mth.floor(this.zo) != Mth.floor(this.z);
        final int integer4 = boolean3 ? 2 : 40;
        if (this.tickCount % integer4 == 0) {
            if (this.level.getFluidState(new BlockPos(this)).is(FluidTags.LAVA)) {
                this.setDeltaMovement((this.random.nextFloat() - this.random.nextFloat()) * 0.2f, 0.20000000298023224, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
                this.playSound(SoundEvents.GENERIC_BURN, 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
            }
            if (!this.level.isClientSide && this.isMergable()) {
                this.mergeWithNeighbours();
            }
        }
        if (this.age != -32768) {
            ++this.age;
        }
        this.hasImpulse |= this.updateInWaterState();
        if (!this.level.isClientSide) {
            final double double5 = this.getDeltaMovement().subtract(csi2).lengthSqr();
            if (double5 > 0.01) {
                this.hasImpulse = true;
            }
        }
        if (!this.level.isClientSide && this.age >= 6000) {
            this.remove();
        }
    }
    
    private void setUnderwaterMovement() {
        final Vec3 csi2 = this.getDeltaMovement();
        this.setDeltaMovement(csi2.x * 0.9900000095367432, csi2.y + ((csi2.y < 0.05999999865889549) ? 5.0E-4f : 0.0f), csi2.z * 0.9900000095367432);
    }
    
    private void mergeWithNeighbours() {
        final List<ItemEntity> list2 = this.level.<ItemEntity>getEntitiesOfClass((java.lang.Class<? extends ItemEntity>)ItemEntity.class, this.getBoundingBox().inflate(0.5, 0.0, 0.5), (java.util.function.Predicate<? super ItemEntity>)(atx -> atx != this && atx.isMergable()));
        if (!list2.isEmpty()) {
            for (final ItemEntity atx4 : list2) {
                if (!this.isMergable()) {
                    return;
                }
                this.merge(atx4);
            }
        }
    }
    
    private boolean isMergable() {
        final ItemStack bcj2 = this.getItem();
        return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && bcj2.getCount() < bcj2.getMaxStackSize();
    }
    
    private void merge(final ItemEntity atx) {
        final ItemStack bcj3 = this.getItem();
        final ItemStack bcj4 = atx.getItem();
        if (bcj4.getItem() != bcj3.getItem()) {
            return;
        }
        if (bcj4.getCount() + bcj3.getCount() > bcj4.getMaxStackSize()) {
            return;
        }
        if (bcj4.hasTag() ^ bcj3.hasTag()) {
            return;
        }
        if (bcj4.hasTag() && !bcj4.getTag().equals(bcj3.getTag())) {
            return;
        }
        if (bcj4.getCount() < bcj3.getCount()) {
            merge(this, bcj3, atx, bcj4);
        }
        else {
            merge(atx, bcj4, this, bcj3);
        }
    }
    
    private static void merge(final ItemEntity atx1, final ItemStack bcj2, final ItemEntity atx3, final ItemStack bcj4) {
        final int integer5 = Math.min(bcj2.getMaxStackSize() - bcj2.getCount(), bcj4.getCount());
        final ItemStack bcj5 = bcj2.copy();
        bcj5.grow(integer5);
        atx1.setItem(bcj5);
        bcj4.shrink(integer5);
        atx3.setItem(bcj4);
        atx1.pickupDelay = Math.max(atx1.pickupDelay, atx3.pickupDelay);
        atx1.age = Math.min(atx1.age, atx3.age);
        if (bcj4.isEmpty()) {
            atx3.remove();
        }
    }
    
    public void setShortLifeTime() {
        this.age = 4800;
    }
    
    @Override
    protected void burn(final int integer) {
        this.hurt(DamageSource.IN_FIRE, (float)integer);
    }
    
    @Override
    public boolean hurt(final DamageSource ahx, final float float2) {
        if (this.isInvulnerableTo(ahx)) {
            return false;
        }
        if (!this.getItem().isEmpty() && this.getItem().getItem() == Items.NETHER_STAR && ahx.isExplosion()) {
            return false;
        }
        this.markHurt();
        this.health -= (int)float2;
        if (this.health <= 0) {
            this.remove();
        }
        return false;
    }
    
    public void addAdditionalSaveData(final CompoundTag id) {
        id.putShort("Health", (short)this.health);
        id.putShort("Age", (short)this.age);
        id.putShort("PickupDelay", (short)this.pickupDelay);
        if (this.getThrower() != null) {
            id.put("Thrower", (Tag)NbtUtils.createUUIDTag(this.getThrower()));
        }
        if (this.getOwner() != null) {
            id.put("Owner", (Tag)NbtUtils.createUUIDTag(this.getOwner()));
        }
        if (!this.getItem().isEmpty()) {
            id.put("Item", (Tag)this.getItem().save(new CompoundTag()));
        }
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
        this.health = id.getShort("Health");
        this.age = id.getShort("Age");
        if (id.contains("PickupDelay")) {
            this.pickupDelay = id.getShort("PickupDelay");
        }
        if (id.contains("Owner", 10)) {
            this.owner = NbtUtils.loadUUIDTag(id.getCompound("Owner"));
        }
        if (id.contains("Thrower", 10)) {
            this.thrower = NbtUtils.loadUUIDTag(id.getCompound("Thrower"));
        }
        final CompoundTag id2 = id.getCompound("Item");
        this.setItem(ItemStack.of(id2));
        if (this.getItem().isEmpty()) {
            this.remove();
        }
    }
    
    @Override
    public void playerTouch(final Player awg) {
        if (this.level.isClientSide) {
            return;
        }
        final ItemStack bcj3 = this.getItem();
        final Item bce4 = bcj3.getItem();
        final int integer5 = bcj3.getCount();
        if (this.pickupDelay == 0 && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(awg.getUUID())) && awg.inventory.add(bcj3)) {
            awg.take(this, integer5);
            if (bcj3.isEmpty()) {
                this.remove();
                bcj3.setCount(integer5);
            }
            awg.awardStat(Stats.ITEM_PICKED_UP.get(bce4), integer5);
        }
    }
    
    @Override
    public Component getName() {
        final Component jo2 = this.getCustomName();
        if (jo2 != null) {
            return jo2;
        }
        return new TranslatableComponent(this.getItem().getDescriptionId(), new Object[0]);
    }
    
    @Override
    public boolean isAttackable() {
        return false;
    }
    
    @Nullable
    @Override
    public Entity changeDimension(final DimensionType byn) {
        final Entity aio3 = super.changeDimension(byn);
        if (!this.level.isClientSide && aio3 instanceof ItemEntity) {
            ((ItemEntity)aio3).mergeWithNeighbours();
        }
        return aio3;
    }
    
    public ItemStack getItem() {
        return this.getEntityData().<ItemStack>get(ItemEntity.DATA_ITEM);
    }
    
    public void setItem(final ItemStack bcj) {
        this.getEntityData().<ItemStack>set(ItemEntity.DATA_ITEM, bcj);
    }
    
    @Nullable
    public UUID getOwner() {
        return this.owner;
    }
    
    public void setOwner(@Nullable final UUID uUID) {
        this.owner = uUID;
    }
    
    @Nullable
    public UUID getThrower() {
        return this.thrower;
    }
    
    public void setThrower(@Nullable final UUID uUID) {
        this.thrower = uUID;
    }
    
    public int getAge() {
        return this.age;
    }
    
    public void setDefaultPickUpDelay() {
        this.pickupDelay = 10;
    }
    
    public void setNoPickUpDelay() {
        this.pickupDelay = 0;
    }
    
    public void setNeverPickUp() {
        this.pickupDelay = 32767;
    }
    
    public void setPickUpDelay(final int integer) {
        this.pickupDelay = integer;
    }
    
    public boolean hasPickUpDelay() {
        return this.pickupDelay > 0;
    }
    
    public void setExtendedLifetime() {
        this.age = -6000;
    }
    
    public void makeFakeItem() {
        this.setNeverPickUp();
        this.age = 5999;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
    
    static {
        DATA_ITEM = SynchedEntityData.<ItemStack>defineId(ItemEntity.class, EntityDataSerializers.ITEM_STACK);
    }
}
