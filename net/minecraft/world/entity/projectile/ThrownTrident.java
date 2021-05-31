package net.minecraft.world.entity.projectile;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import javax.annotation.Nullable;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;

public class ThrownTrident extends AbstractArrow {
    private static final EntityDataAccessor<Byte> ID_LOYALTY;
    private ItemStack tridentItem;
    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;
    
    public ThrownTrident(final EntityType<? extends ThrownTrident> ais, final Level bhr) {
        super(ais, bhr);
        this.tridentItem = new ItemStack(Items.TRIDENT);
    }
    
    public ThrownTrident(final Level bhr, final LivingEntity aix, final ItemStack bcj) {
        super(EntityType.TRIDENT, aix, bhr);
        this.tridentItem = new ItemStack(Items.TRIDENT);
        this.tridentItem = bcj.copy();
        this.entityData.<Byte>set(ThrownTrident.ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(bcj));
    }
    
    public ThrownTrident(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.TRIDENT, double2, double3, double4, bhr);
        this.tridentItem = new ItemStack(Items.TRIDENT);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Byte>define(ThrownTrident.ID_LOYALTY, (Byte)0);
    }
    
    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        final Entity aio2 = this.getOwner();
        if ((this.dealtDamage || this.isNoPhysics()) && aio2 != null) {
            final int integer3 = this.entityData.<Byte>get(ThrownTrident.ID_LOYALTY);
            if (integer3 > 0 && !this.isAcceptibleReturnOwner()) {
                if (!this.level.isClientSide && this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1f);
                }
                this.remove();
            }
            else if (integer3 > 0) {
                this.setNoPhysics(true);
                final Vec3 csi4 = new Vec3(aio2.x - this.x, aio2.y + aio2.getEyeHeight() - this.y, aio2.z - this.z);
                this.y += csi4.y * 0.015 * integer3;
                if (this.level.isClientSide) {
                    this.yOld = this.y;
                }
                final double double5 = 0.05 * integer3;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(csi4.normalize().scale(double5)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0f, 1.0f);
                }
                ++this.clientSideReturnTridentTickCount;
            }
        }
        super.tick();
    }
    
    private boolean isAcceptibleReturnOwner() {
        final Entity aio2 = this.getOwner();
        return aio2 != null && aio2.isAlive() && (!(aio2 instanceof ServerPlayer) || !aio2.isSpectator());
    }
    
    @Override
    protected ItemStack getPickupItem() {
        return this.tridentItem.copy();
    }
    
    @Nullable
    @Override
    protected EntityHitResult findHitEntity(final Vec3 csi1, final Vec3 csi2) {
        if (this.dealtDamage) {
            return null;
        }
        return super.findHitEntity(csi1, csi2);
    }
    
    @Override
    protected void onHitEntity(final EntityHitResult cse) {
        final Entity aio3 = cse.getEntity();
        float float4 = 8.0f;
        if (aio3 instanceof LivingEntity) {
            final LivingEntity aix5 = (LivingEntity)aio3;
            float4 += EnchantmentHelper.getDamageBonus(this.tridentItem, aix5.getMobType());
        }
        final Entity aio4 = this.getOwner();
        final DamageSource ahx6 = DamageSource.trident(this, (aio4 == null) ? this : aio4);
        this.dealtDamage = true;
        SoundEvent yo7 = SoundEvents.TRIDENT_HIT;
        if (aio3.hurt(ahx6, float4) && aio3 instanceof LivingEntity) {
            final LivingEntity aix6 = (LivingEntity)aio3;
            if (aio4 instanceof LivingEntity) {
                EnchantmentHelper.doPostHurtEffects(aix6, aio4);
                EnchantmentHelper.doPostDamageEffects((LivingEntity)aio4, aix6);
            }
            this.doPostHurtEffects(aix6);
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        float float5 = 1.0f;
        if (this.level instanceof ServerLevel && this.level.isThundering() && EnchantmentHelper.hasChanneling(this.tridentItem)) {
            final BlockPos ew9 = aio3.getCommandSenderBlockPosition();
            if (this.level.canSeeSky(ew9)) {
                final LightningBolt atu10 = new LightningBolt(this.level, ew9.getX() + 0.5, ew9.getY(), ew9.getZ() + 0.5, false);
                atu10.setCause((aio4 instanceof ServerPlayer) ? ((ServerPlayer)aio4) : null);
                ((ServerLevel)this.level).addGlobalEntity(atu10);
                yo7 = SoundEvents.TRIDENT_THUNDER;
                float5 = 5.0f;
            }
        }
        this.playSound(yo7, float5, 1.0f);
    }
    
    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }
    
    @Override
    public void playerTouch(final Player awg) {
        final Entity aio3 = this.getOwner();
        if (aio3 != null && aio3.getUUID() != awg.getUUID()) {
            return;
        }
        super.playerTouch(awg);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        if (id.contains("Trident", 10)) {
            this.tridentItem = ItemStack.of(id.getCompound("Trident"));
        }
        this.dealtDamage = id.getBoolean("DealtDamage");
        this.entityData.<Byte>set(ThrownTrident.ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentItem));
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.put("Trident", (Tag)this.tridentItem.save(new CompoundTag()));
        id.putBoolean("DealtDamage", this.dealtDamage);
    }
    
    @Override
    protected void checkDespawn() {
        final int integer2 = this.entityData.<Byte>get(ThrownTrident.ID_LOYALTY);
        if (this.pickup != Pickup.ALLOWED || integer2 <= 0) {
            super.checkDespawn();
        }
    }
    
    @Override
    protected float getWaterInertia() {
        return 0.99f;
    }
    
    @Override
    public boolean shouldRender(final double double1, final double double2, final double double3) {
        return true;
    }
    
    static {
        ID_LOYALTY = SynchedEntityData.<Byte>defineId(ThrownTrident.class, EntityDataSerializers.BYTE);
    }
}
