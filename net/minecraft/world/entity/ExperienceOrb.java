package net.minecraft.world.entity;

import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.network.protocol.Packet;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class ExperienceOrb extends Entity {
    public int tickCount;
    public int age;
    public int throwTime;
    private int health;
    private int value;
    private Player followingPlayer;
    private int followingTime;
    
    public ExperienceOrb(final Level bhr, final double double2, final double double3, final double double4, final int integer) {
        this(EntityType.EXPERIENCE_ORB, bhr);
        this.setPos(double2, double3, double4);
        this.yRot = (float)(this.random.nextDouble() * 360.0);
        this.setDeltaMovement((this.random.nextDouble() * 0.20000000298023224 - 0.10000000149011612) * 2.0, this.random.nextDouble() * 0.2 * 2.0, (this.random.nextDouble() * 0.20000000298023224 - 0.10000000149011612) * 2.0);
        this.value = integer;
    }
    
    public ExperienceOrb(final EntityType<? extends ExperienceOrb> ais, final Level bhr) {
        super(ais, bhr);
        this.health = 5;
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected void defineSynchedData() {
    }
    
    @Override
    public int getLightColor() {
        float float2 = 0.5f;
        float2 = Mth.clamp(float2, 0.0f, 1.0f);
        final int integer3 = super.getLightColor();
        int integer4 = integer3 & 0xFF;
        final int integer5 = integer3 >> 16 & 0xFF;
        integer4 += (int)(float2 * 15.0f * 16.0f);
        if (integer4 > 240) {
            integer4 = 240;
        }
        return integer4 | integer5 << 16;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.throwTime > 0) {
            --this.throwTime;
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.isUnderLiquid(FluidTags.WATER)) {
            this.setUnderwaterMovement();
        }
        else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03, 0.0));
        }
        if (this.level.getFluidState(new BlockPos(this)).is(FluidTags.LAVA)) {
            this.setDeltaMovement((this.random.nextFloat() - this.random.nextFloat()) * 0.2f, 0.20000000298023224, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            this.playSound(SoundEvents.GENERIC_BURN, 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
        }
        if (!this.level.noCollision(this.getBoundingBox())) {
            this.checkInBlock(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
        }
        final double double2 = 8.0;
        if (this.followingTime < this.tickCount - 20 + this.getId() % 100) {
            if (this.followingPlayer == null || this.followingPlayer.distanceToSqr(this) > 64.0) {
                this.followingPlayer = this.level.getNearestPlayer(this, 8.0);
            }
            this.followingTime = this.tickCount;
        }
        if (this.followingPlayer != null && this.followingPlayer.isSpectator()) {
            this.followingPlayer = null;
        }
        if (this.followingPlayer != null) {
            final Vec3 csi4 = new Vec3(this.followingPlayer.x - this.x, this.followingPlayer.y + this.followingPlayer.getEyeHeight() / 2.0 - this.y, this.followingPlayer.z - this.z);
            final double double3 = csi4.lengthSqr();
            if (double3 < 64.0) {
                final double double4 = 1.0 - Math.sqrt(double3) / 8.0;
                this.setDeltaMovement(this.getDeltaMovement().add(csi4.normalize().scale(double4 * double4 * 0.1)));
            }
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        float float4 = 0.98f;
        if (this.onGround) {
            float4 = this.level.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFriction() * 0.98f;
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(float4, 0.98, float4));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, -0.9, 1.0));
        }
        ++this.tickCount;
        ++this.age;
        if (this.age >= 6000) {
            this.remove();
        }
    }
    
    private void setUnderwaterMovement() {
        final Vec3 csi2 = this.getDeltaMovement();
        this.setDeltaMovement(csi2.x * 0.9900000095367432, Math.min(csi2.y + 5.000000237487257E-4, 0.05999999865889549), csi2.z * 0.9900000095367432);
    }
    
    @Override
    protected void doWaterSplashEffect() {
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
        id.putShort("Value", (short)this.value);
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
        this.health = id.getShort("Health");
        this.age = id.getShort("Age");
        this.value = id.getShort("Value");
    }
    
    @Override
    public void playerTouch(final Player awg) {
        if (this.level.isClientSide) {
            return;
        }
        if (this.throwTime == 0 && awg.takeXpDelay == 0) {
            awg.takeXpDelay = 2;
            awg.take(this, 1);
            final Map.Entry<EquipmentSlot, ItemStack> entry3 = EnchantmentHelper.getRandomItemWith(Enchantments.MENDING, awg);
            if (entry3 != null) {
                final ItemStack bcj4 = (ItemStack)entry3.getValue();
                if (!bcj4.isEmpty() && bcj4.isDamaged()) {
                    final int integer5 = Math.min(this.xpToDurability(this.value), bcj4.getDamageValue());
                    this.value -= this.durabilityToXp(integer5);
                    bcj4.setDamageValue(bcj4.getDamageValue() - integer5);
                }
            }
            if (this.value > 0) {
                awg.giveExperiencePoints(this.value);
            }
            this.remove();
        }
    }
    
    private int durabilityToXp(final int integer) {
        return integer / 2;
    }
    
    private int xpToDurability(final int integer) {
        return integer * 2;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public int getIcon() {
        if (this.value >= 2477) {
            return 10;
        }
        if (this.value >= 1237) {
            return 9;
        }
        if (this.value >= 617) {
            return 8;
        }
        if (this.value >= 307) {
            return 7;
        }
        if (this.value >= 149) {
            return 6;
        }
        if (this.value >= 73) {
            return 5;
        }
        if (this.value >= 37) {
            return 4;
        }
        if (this.value >= 17) {
            return 3;
        }
        if (this.value >= 7) {
            return 2;
        }
        if (this.value >= 3) {
            return 1;
        }
        return 0;
    }
    
    public static int getExperienceValue(final int integer) {
        if (integer >= 2477) {
            return 2477;
        }
        if (integer >= 1237) {
            return 1237;
        }
        if (integer >= 617) {
            return 617;
        }
        if (integer >= 307) {
            return 307;
        }
        if (integer >= 149) {
            return 149;
        }
        if (integer >= 73) {
            return 73;
        }
        if (integer >= 37) {
            return 37;
        }
        if (integer >= 17) {
            return 17;
        }
        if (integer >= 7) {
            return 7;
        }
        if (integer >= 3) {
            return 3;
        }
        return 1;
    }
    
    @Override
    public boolean isAttackable() {
        return false;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddExperienceOrbPacket(this);
    }
}
