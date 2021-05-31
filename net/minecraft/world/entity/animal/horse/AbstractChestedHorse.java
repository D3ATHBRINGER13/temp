package net.minecraft.world.entity.animal.horse;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.syncher.EntityDataAccessor;

public abstract class AbstractChestedHorse extends AbstractHorse {
    private static final EntityDataAccessor<Boolean> DATA_ID_CHEST;
    
    protected AbstractChestedHorse(final EntityType<? extends AbstractChestedHorse> ais, final Level bhr) {
        super(ais, bhr);
        this.canGallop = false;
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.<Boolean>define(AbstractChestedHorse.DATA_ID_CHEST, false);
    }
    
    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.generateRandomMaxHealth());
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.17499999701976776);
        this.getAttribute(AbstractChestedHorse.JUMP_STRENGTH).setBaseValue(0.5);
    }
    
    public boolean hasChest() {
        return this.entityData.<Boolean>get(AbstractChestedHorse.DATA_ID_CHEST);
    }
    
    public void setChest(final boolean boolean1) {
        this.entityData.<Boolean>set(AbstractChestedHorse.DATA_ID_CHEST, boolean1);
    }
    
    @Override
    protected int getInventorySize() {
        if (this.hasChest()) {
            return 17;
        }
        return super.getInventorySize();
    }
    
    public double getRideHeight() {
        return super.getRideHeight() - 0.25;
    }
    
    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.DONKEY_ANGRY;
    }
    
    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.hasChest()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Blocks.CHEST);
            }
            this.setChest(false);
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag id) {
        super.addAdditionalSaveData(id);
        id.putBoolean("ChestedHorse", this.hasChest());
        if (this.hasChest()) {
            final ListTag ik3 = new ListTag();
            for (int integer4 = 2; integer4 < this.inventory.getContainerSize(); ++integer4) {
                final ItemStack bcj5 = this.inventory.getItem(integer4);
                if (!bcj5.isEmpty()) {
                    final CompoundTag id2 = new CompoundTag();
                    id2.putByte("Slot", (byte)integer4);
                    bcj5.save(id2);
                    ik3.add(id2);
                }
            }
            id.put("Items", (Tag)ik3);
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag id) {
        super.readAdditionalSaveData(id);
        this.setChest(id.getBoolean("ChestedHorse"));
        if (this.hasChest()) {
            final ListTag ik3 = id.getList("Items", 10);
            this.createInventory();
            for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
                final CompoundTag id2 = ik3.getCompound(integer4);
                final int integer5 = id2.getByte("Slot") & 0xFF;
                if (integer5 >= 2 && integer5 < this.inventory.getContainerSize()) {
                    this.inventory.setItem(integer5, ItemStack.of(id2));
                }
            }
        }
        this.updateEquipment();
    }
    
    @Override
    public boolean setSlot(final int integer, final ItemStack bcj) {
        if (integer == 499) {
            if (this.hasChest() && bcj.isEmpty()) {
                this.setChest(false);
                this.createInventory();
                return true;
            }
            if (!this.hasChest() && bcj.getItem() == Blocks.CHEST.asItem()) {
                this.setChest(true);
                this.createInventory();
                return true;
            }
        }
        return super.setSlot(integer, bcj);
    }
    
    @Override
    public boolean mobInteract(final Player awg, final InteractionHand ahi) {
        final ItemStack bcj4 = awg.getItemInHand(ahi);
        if (bcj4.getItem() instanceof SpawnEggItem) {
            return super.mobInteract(awg, ahi);
        }
        if (!this.isBaby()) {
            if (this.isTamed() && awg.isSneaking()) {
                this.openInventory(awg);
                return true;
            }
            if (this.isVehicle()) {
                return super.mobInteract(awg, ahi);
            }
        }
        if (!bcj4.isEmpty()) {
            boolean boolean5 = this.handleEating(awg, bcj4);
            if (!boolean5) {
                if (!this.isTamed() || bcj4.getItem() == Items.NAME_TAG) {
                    if (bcj4.interactEnemy(awg, this, ahi)) {
                        return true;
                    }
                    this.makeMad();
                    return true;
                }
                else {
                    if (!this.hasChest() && bcj4.getItem() == Blocks.CHEST.asItem()) {
                        this.setChest(true);
                        this.playChestEquipsSound();
                        boolean5 = true;
                        this.createInventory();
                    }
                    if (!this.isBaby() && !this.isSaddled() && bcj4.getItem() == Items.SADDLE) {
                        this.openInventory(awg);
                        return true;
                    }
                }
            }
            if (boolean5) {
                if (!awg.abilities.instabuild) {
                    bcj4.shrink(1);
                }
                return true;
            }
        }
        if (this.isBaby()) {
            return super.mobInteract(awg, ahi);
        }
        this.doPlayerRide(awg);
        return true;
    }
    
    protected void playChestEquipsSound() {
        this.playSound(SoundEvents.DONKEY_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }
    
    public int getInventoryColumns() {
        return 5;
    }
    
    static {
        DATA_ID_CHEST = SynchedEntityData.<Boolean>defineId(AbstractChestedHorse.class, EntityDataSerializers.BOOLEAN);
    }
}
