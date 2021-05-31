package net.minecraft.world.entity.projectile;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ItemLike;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

public class EyeOfEnder extends Entity implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;
    private double tx;
    private double ty;
    private double tz;
    private int life;
    private boolean surviveAfterDeath;
    
    public EyeOfEnder(final EntityType<? extends EyeOfEnder> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public EyeOfEnder(final Level bhr, final double double2, final double double3, final double double4) {
        this(EntityType.EYE_OF_ENDER, bhr);
        this.life = 0;
        this.setPos(double2, double3, double4);
    }
    
    public void setItem(final ItemStack bcj) {
        if (bcj.getItem() != Items.ENDER_EYE || bcj.hasTag()) {
            this.getEntityData().<ItemStack>set(EyeOfEnder.DATA_ITEM_STACK, (ItemStack)Util.<T>make((T)bcj.copy(), (java.util.function.Consumer<T>)(bcj -> bcj.setCount(1))));
        }
    }
    
    private ItemStack getItemRaw() {
        return this.getEntityData().<ItemStack>get(EyeOfEnder.DATA_ITEM_STACK);
    }
    
    @Override
    public ItemStack getItem() {
        final ItemStack bcj2 = this.getItemRaw();
        return bcj2.isEmpty() ? new ItemStack(Items.ENDER_EYE) : bcj2;
    }
    
    @Override
    protected void defineSynchedData() {
        this.getEntityData().<ItemStack>define(EyeOfEnder.DATA_ITEM_STACK, ItemStack.EMPTY);
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(final double double1) {
        double double2 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(double2)) {
            double2 = 4.0;
        }
        double2 *= 64.0;
        return double1 < double2 * double2;
    }
    
    public void signalTo(final BlockPos ew) {
        final double double3 = ew.getX();
        final int integer5 = ew.getY();
        final double double4 = ew.getZ();
        final double double5 = double3 - this.x;
        final double double6 = double4 - this.z;
        final float float12 = Mth.sqrt(double5 * double5 + double6 * double6);
        if (float12 > 12.0f) {
            this.tx = this.x + double5 / float12 * 12.0;
            this.tz = this.z + double6 / float12 * 12.0;
            this.ty = this.y + 8.0;
        }
        else {
            this.tx = double3;
            this.ty = integer5;
            this.tz = double4;
        }
        this.life = 0;
        this.surviveAfterDeath = (this.random.nextInt(5) > 0);
    }
    
    @Override
    public void lerpMotion(final double double1, final double double2, final double double3) {
        this.setDeltaMovement(double1, double2, double3);
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            final float float8 = Mth.sqrt(double1 * double1 + double3 * double3);
            this.yRot = (float)(Mth.atan2(double1, double3) * 57.2957763671875);
            this.xRot = (float)(Mth.atan2(double2, float8) * 57.2957763671875);
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
        }
    }
    
    @Override
    public void tick() {
        this.xOld = this.x;
        this.yOld = this.y;
        this.zOld = this.z;
        super.tick();
        Vec3 csi2 = this.getDeltaMovement();
        this.x += csi2.x;
        this.y += csi2.y;
        this.z += csi2.z;
        final float float3 = Mth.sqrt(Entity.getHorizontalDistanceSqr(csi2));
        this.yRot = (float)(Mth.atan2(csi2.x, csi2.z) * 57.2957763671875);
        this.xRot = (float)(Mth.atan2(csi2.y, float3) * 57.2957763671875);
        while (this.xRot - this.xRotO < -180.0f) {
            this.xRotO -= 360.0f;
        }
        while (this.xRot - this.xRotO >= 180.0f) {
            this.xRotO += 360.0f;
        }
        while (this.yRot - this.yRotO < -180.0f) {
            this.yRotO -= 360.0f;
        }
        while (this.yRot - this.yRotO >= 180.0f) {
            this.yRotO += 360.0f;
        }
        this.xRot = Mth.lerp(0.2f, this.xRotO, this.xRot);
        this.yRot = Mth.lerp(0.2f, this.yRotO, this.yRot);
        if (!this.level.isClientSide) {
            final double double4 = this.tx - this.x;
            final double double5 = this.tz - this.z;
            final float float4 = (float)Math.sqrt(double4 * double4 + double5 * double5);
            final float float5 = (float)Mth.atan2(double5, double4);
            double double6 = Mth.lerp(0.0025, float3, float4);
            double double7 = csi2.y;
            if (float4 < 1.0f) {
                double6 *= 0.8;
                double7 *= 0.8;
            }
            final int integer14 = (this.y < this.ty) ? 1 : -1;
            csi2 = new Vec3(Math.cos((double)float5) * double6, double7 + (integer14 - double7) * 0.014999999664723873, Math.sin((double)float5) * double6);
            this.setDeltaMovement(csi2);
        }
        final float float6 = 0.25f;
        if (this.isInWater()) {
            for (int integer15 = 0; integer15 < 4; ++integer15) {
                this.level.addParticle(ParticleTypes.BUBBLE, this.x - csi2.x * 0.25, this.y - csi2.y * 0.25, this.z - csi2.z * 0.25, csi2.x, csi2.y, csi2.z);
            }
        }
        else {
            this.level.addParticle(ParticleTypes.PORTAL, this.x - csi2.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3, this.y - csi2.y * 0.25 - 0.5, this.z - csi2.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3, csi2.x, csi2.y, csi2.z);
        }
        if (!this.level.isClientSide) {
            this.setPos(this.x, this.y, this.z);
            ++this.life;
            if (this.life > 80 && !this.level.isClientSide) {
                this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0f, 1.0f);
                this.remove();
                if (this.surviveAfterDeath) {
                    this.level.addFreshEntity(new ItemEntity(this.level, this.x, this.y, this.z, this.getItem()));
                }
                else {
                    this.level.levelEvent(2003, new BlockPos(this), 0);
                }
            }
        }
    }
    
    public void addAdditionalSaveData(final CompoundTag id) {
        final ItemStack bcj3 = this.getItemRaw();
        if (!bcj3.isEmpty()) {
            id.put("Item", (Tag)bcj3.save(new CompoundTag()));
        }
    }
    
    public void readAdditionalSaveData(final CompoundTag id) {
        final ItemStack bcj3 = ItemStack.of(id.getCompound("Item"));
        this.setItem(bcj3);
    }
    
    @Override
    public float getBrightness() {
        return 1.0f;
    }
    
    @Override
    public int getLightColor() {
        return 15728880;
    }
    
    @Override
    public boolean isAttackable() {
        return false;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
    
    static {
        DATA_ITEM_STACK = SynchedEntityData.<ItemStack>defineId(EyeOfEnder.class, EntityDataSerializers.ITEM_STACK);
    }
}
