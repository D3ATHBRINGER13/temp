package net.minecraft.world.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ItemLike;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;

public abstract class Fireball extends AbstractHurtingProjectile implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;
    
    public Fireball(final EntityType<? extends Fireball> ais, final Level bhr) {
        super(ais, bhr);
    }
    
    public Fireball(final EntityType<? extends Fireball> ais, final double double2, final double double3, final double double4, final double double5, final double double6, final double double7, final Level bhr) {
        super(ais, double2, double3, double4, double5, double6, double7, bhr);
    }
    
    public Fireball(final EntityType<? extends Fireball> ais, final LivingEntity aix, final double double3, final double double4, final double double5, final Level bhr) {
        super(ais, aix, double3, double4, double5, bhr);
    }
    
    public void setItem(final ItemStack bcj) {
        if (bcj.getItem() != Items.FIRE_CHARGE || bcj.hasTag()) {
            this.getEntityData().<ItemStack>set(Fireball.DATA_ITEM_STACK, (ItemStack)Util.<T>make((T)bcj.copy(), (java.util.function.Consumer<T>)(bcj -> bcj.setCount(1))));
        }
    }
    
    protected ItemStack getItemRaw() {
        return this.getEntityData().<ItemStack>get(Fireball.DATA_ITEM_STACK);
    }
    
    @Override
    public ItemStack getItem() {
        final ItemStack bcj2 = this.getItemRaw();
        return bcj2.isEmpty() ? new ItemStack(Items.FIRE_CHARGE) : bcj2;
    }
    
    @Override
    protected void defineSynchedData() {
        this.getEntityData().<ItemStack>define(Fireball.DATA_ITEM_STACK, ItemStack.EMPTY);
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
        DATA_ITEM_STACK = SynchedEntityData.<ItemStack>defineId(Fireball.class, EntityDataSerializers.ITEM_STACK);
    }
}
