package net.minecraft.world.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Item;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;

public abstract class ThrowableItemProjectile extends ThrowableProjectile implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;
    
    public ThrowableItemProjectile(final EntityType<? extends ThrowableItemProjectile> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public ThrowableItemProjectile(final EntityType<? extends ThrowableItemProjectile> ais, final double double2, final double double3, final double double4, final Level bhr) {
        super(ais, double2, double3, double4, bhr);
    }
    
    public ThrowableItemProjectile(final EntityType<? extends ThrowableItemProjectile> ais, final LivingEntity aix, final Level bhr) {
        super(ais, aix, bhr);
    }
    
    public void setItem(final ItemStack bcj) {
        if (bcj.getItem() != this.getDefaultItem() || bcj.hasTag()) {
            this.getEntityData().<ItemStack>set(ThrowableItemProjectile.DATA_ITEM_STACK, (ItemStack)Util.<T>make((T)bcj.copy(), (java.util.function.Consumer<T>)(bcj -> bcj.setCount(1))));
        }
    }
    
    protected abstract Item getDefaultItem();
    
    protected ItemStack getItemRaw() {
        return this.getEntityData().<ItemStack>get(ThrowableItemProjectile.DATA_ITEM_STACK);
    }
    
    @Override
    public ItemStack getItem() {
        final ItemStack bcj2 = this.getItemRaw();
        return bcj2.isEmpty() ? new ItemStack(this.getDefaultItem()) : bcj2;
    }
    
    @Override
    protected void defineSynchedData() {
        this.getEntityData().<ItemStack>define(ThrowableItemProjectile.DATA_ITEM_STACK, ItemStack.EMPTY);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        final ItemStack bcj3 = this.getItemRaw();
        if (!bcj3.isEmpty()) {
            id.put("Item", (Tag)bcj3.save(new CompoundTag()));
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        final ItemStack bcj3 = ItemStack.of(id.getCompound("Item"));
        this.setItem(bcj3);
    }
    
    static {
        DATA_ITEM_STACK = SynchedEntityData.<ItemStack>defineId(ThrowableItemProjectile.class, EntityDataSerializers.ITEM_STACK);
    }
}
