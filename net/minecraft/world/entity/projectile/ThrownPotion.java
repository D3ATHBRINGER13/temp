package net.minecraft.world.entity.projectile;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.effect.MobEffect;
import javax.annotation.Nullable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.damagesource.DamageSource;
import java.util.Iterator;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.List;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;

public class ThrownPotion extends ThrowableProjectile implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;
    private static final Logger LOGGER;
    public static final Predicate<LivingEntity> WATER_SENSITIVE;
    
    public ThrownPotion(final EntityType<? extends ThrownPotion> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public ThrownPotion(final Level bhr, final LivingEntity aix) {
        super(EntityType.POTION, aix, bhr);
    }
    
    public ThrownPotion(final Level bhr, final double double2, final double double3, final double double4) {
        super(EntityType.POTION, double2, double3, double4, bhr);
    }
    
    @Override
    protected void defineSynchedData() {
        this.getEntityData().<ItemStack>define(ThrownPotion.DATA_ITEM_STACK, ItemStack.EMPTY);
    }
    
    @Override
    public ItemStack getItem() {
        final ItemStack bcj2 = this.getEntityData().<ItemStack>get(ThrownPotion.DATA_ITEM_STACK);
        if (bcj2.getItem() != Items.SPLASH_POTION && bcj2.getItem() != Items.LINGERING_POTION) {
            if (this.level != null) {
                ThrownPotion.LOGGER.error("ThrownPotion entity {} has no item?!", this.getId());
            }
            return new ItemStack(Items.SPLASH_POTION);
        }
        return bcj2;
    }
    
    public void setItem(final ItemStack bcj) {
        this.getEntityData().<ItemStack>set(ThrownPotion.DATA_ITEM_STACK, bcj.copy());
    }
    
    @Override
    protected float getGravity() {
        return 0.05f;
    }
    
    @Override
    protected void onHit(final HitResult csf) {
        if (this.level.isClientSide) {
            return;
        }
        final ItemStack bcj3 = this.getItem();
        final Potion bdy4 = PotionUtils.getPotion(bcj3);
        final List<MobEffectInstance> list5 = PotionUtils.getMobEffects(bcj3);
        final boolean boolean6 = bdy4 == Potions.WATER && list5.isEmpty();
        if (csf.getType() == HitResult.Type.BLOCK && boolean6) {
            final BlockHitResult csd7 = (BlockHitResult)csf;
            final Direction fb8 = csd7.getDirection();
            final BlockPos ew9 = csd7.getBlockPos().relative(fb8);
            this.dowseFire(ew9, fb8);
            this.dowseFire(ew9.relative(fb8.getOpposite()), fb8);
            for (final Direction fb9 : Direction.Plane.HORIZONTAL) {
                this.dowseFire(ew9.relative(fb9), fb9);
            }
        }
        if (boolean6) {
            this.applyWater();
        }
        else if (!list5.isEmpty()) {
            if (this.isLingering()) {
                this.makeAreaOfEffectCloud(bcj3, bdy4);
            }
            else {
                this.applySplash(list5, (csf.getType() == HitResult.Type.ENTITY) ? ((EntityHitResult)csf).getEntity() : null);
            }
        }
        final int integer7 = bdy4.hasInstantEffects() ? 2007 : 2002;
        this.level.levelEvent(integer7, new BlockPos(this), PotionUtils.getColor(bcj3));
        this.remove();
    }
    
    private void applyWater() {
        final AABB csc2 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
        final List<LivingEntity> list3 = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, csc2, (java.util.function.Predicate<? super LivingEntity>)ThrownPotion.WATER_SENSITIVE);
        if (!list3.isEmpty()) {
            for (final LivingEntity aix5 : list3) {
                final double double6 = this.distanceToSqr(aix5);
                if (double6 < 16.0 && isWaterSensitiveEntity(aix5)) {
                    aix5.hurt(DamageSource.indirectMagic(aix5, this.getOwner()), 1.0f);
                }
            }
        }
    }
    
    private void applySplash(final List<MobEffectInstance> list, @Nullable final Entity aio) {
        final AABB csc4 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
        final List<LivingEntity> list2 = this.level.<LivingEntity>getEntitiesOfClass((java.lang.Class<? extends LivingEntity>)LivingEntity.class, csc4);
        if (!list2.isEmpty()) {
            for (final LivingEntity aix7 : list2) {
                if (!aix7.isAffectedByPotions()) {
                    continue;
                }
                final double double8 = this.distanceToSqr(aix7);
                if (double8 >= 16.0) {
                    continue;
                }
                double double9 = 1.0 - Math.sqrt(double8) / 4.0;
                if (aix7 == aio) {
                    double9 = 1.0;
                }
                for (final MobEffectInstance aii13 : list) {
                    final MobEffect aig14 = aii13.getEffect();
                    if (aig14.isInstantenous()) {
                        aig14.applyInstantenousEffect(this, this.getOwner(), aix7, aii13.getAmplifier(), double9);
                    }
                    else {
                        final int integer15 = (int)(double9 * aii13.getDuration() + 0.5);
                        if (integer15 <= 20) {
                            continue;
                        }
                        aix7.addEffect(new MobEffectInstance(aig14, integer15, aii13.getAmplifier(), aii13.isAmbient(), aii13.isVisible()));
                    }
                }
            }
        }
    }
    
    private void makeAreaOfEffectCloud(final ItemStack bcj, final Potion bdy) {
        final AreaEffectCloud ain4 = new AreaEffectCloud(this.level, this.x, this.y, this.z);
        ain4.setOwner(this.getOwner());
        ain4.setRadius(3.0f);
        ain4.setRadiusOnUse(-0.5f);
        ain4.setWaitTime(10);
        ain4.setRadiusPerTick(-ain4.getRadius() / ain4.getDuration());
        ain4.setPotion(bdy);
        for (final MobEffectInstance aii6 : PotionUtils.getCustomEffects(bcj)) {
            ain4.addEffect(new MobEffectInstance(aii6));
        }
        final CompoundTag id5 = bcj.getTag();
        if (id5 != null && id5.contains("CustomPotionColor", 99)) {
            ain4.setFixedColor(id5.getInt("CustomPotionColor"));
        }
        this.level.addFreshEntity(ain4);
    }
    
    private boolean isLingering() {
        return this.getItem().getItem() == Items.LINGERING_POTION;
    }
    
    private void dowseFire(final BlockPos ew, final Direction fb) {
        final BlockState bvt4 = this.level.getBlockState(ew);
        final Block bmv5 = bvt4.getBlock();
        if (bmv5 == Blocks.FIRE) {
            this.level.extinguishFire(null, ew.relative(fb), fb.getOpposite());
        }
        else if (bmv5 == Blocks.CAMPFIRE && bvt4.<Boolean>getValue((Property<Boolean>)CampfireBlock.LIT)) {
            this.level.levelEvent(null, 1009, ew, 0);
            this.level.setBlockAndUpdate(ew, ((AbstractStateHolder<O, BlockState>)bvt4).<Comparable, Boolean>setValue((Property<Comparable>)CampfireBlock.LIT, false));
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        final ItemStack bcj3 = ItemStack.of(id.getCompound("Potion"));
        if (bcj3.isEmpty()) {
            this.remove();
        }
        else {
            this.setItem(bcj3);
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        final ItemStack bcj3 = this.getItem();
        if (!bcj3.isEmpty()) {
            id.put("Potion", (Tag)bcj3.save(new CompoundTag()));
        }
    }
    
    private static boolean isWaterSensitiveEntity(final LivingEntity aix) {
        return aix instanceof EnderMan || aix instanceof Blaze;
    }
    
    static {
        DATA_ITEM_STACK = SynchedEntityData.<ItemStack>defineId(ThrownPotion.class, EntityDataSerializers.ITEM_STACK);
        LOGGER = LogManager.getLogger();
        WATER_SENSITIVE = ThrownPotion::isWaterSensitiveEntity;
    }
}
