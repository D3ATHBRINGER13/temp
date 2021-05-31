package net.minecraft.world.level.block.entity;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import net.minecraft.world.ContainerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BrewingStandBlock;
import java.util.Arrays;
import net.minecraft.world.item.Items;
import java.util.Iterator;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.WorldlyContainer;

public class BrewingStandBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, TickableBlockEntity {
    private static final int[] SLOTS_FOR_UP;
    private static final int[] SLOTS_FOR_DOWN;
    private static final int[] SLOTS_FOR_SIDES;
    private NonNullList<ItemStack> items;
    private int brewTime;
    private boolean[] lastPotionCount;
    private Item ingredient;
    private int fuel;
    protected final ContainerData dataAccess;
    
    public BrewingStandBlockEntity() {
        super(BlockEntityType.BREWING_STAND);
        this.items = NonNullList.<ItemStack>withSize(5, ItemStack.EMPTY);
        this.dataAccess = new ContainerData() {
            public int get(final int integer) {
                switch (integer) {
                    case 0: {
                        return BrewingStandBlockEntity.this.brewTime;
                    }
                    case 1: {
                        return BrewingStandBlockEntity.this.fuel;
                    }
                    default: {
                        return 0;
                    }
                }
            }
            
            public void set(final int integer1, final int integer2) {
                switch (integer1) {
                    case 0: {
                        BrewingStandBlockEntity.this.brewTime = integer2;
                        break;
                    }
                    case 1: {
                        BrewingStandBlockEntity.this.fuel = integer2;
                        break;
                    }
                }
            }
            
            public int getCount() {
                return 2;
            }
        };
    }
    
    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.brewing", new Object[0]);
    }
    
    @Override
    public int getContainerSize() {
        return this.items.size();
    }
    
    @Override
    public boolean isEmpty() {
        for (final ItemStack bcj3 : this.items) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void tick() {
        final ItemStack bcj2 = this.items.get(4);
        if (this.fuel <= 0 && bcj2.getItem() == Items.BLAZE_POWDER) {
            this.fuel = 20;
            bcj2.shrink(1);
            this.setChanged();
        }
        final boolean boolean3 = this.isBrewable();
        final boolean boolean4 = this.brewTime > 0;
        final ItemStack bcj3 = this.items.get(3);
        if (boolean4) {
            --this.brewTime;
            final boolean boolean5 = this.brewTime == 0;
            if (boolean5 && boolean3) {
                this.doBrew();
                this.setChanged();
            }
            else if (!boolean3) {
                this.brewTime = 0;
                this.setChanged();
            }
            else if (this.ingredient != bcj3.getItem()) {
                this.brewTime = 0;
                this.setChanged();
            }
        }
        else if (boolean3 && this.fuel > 0) {
            --this.fuel;
            this.brewTime = 400;
            this.ingredient = bcj3.getItem();
            this.setChanged();
        }
        if (!this.level.isClientSide) {
            final boolean[] arr6 = this.getPotionBits();
            if (!Arrays.equals(arr6, this.lastPotionCount)) {
                this.lastPotionCount = arr6;
                BlockState bvt7 = this.level.getBlockState(this.getBlockPos());
                if (!(bvt7.getBlock() instanceof BrewingStandBlock)) {
                    return;
                }
                for (int integer8 = 0; integer8 < BrewingStandBlock.HAS_BOTTLE.length; ++integer8) {
                    bvt7 = ((AbstractStateHolder<O, BlockState>)bvt7).<Comparable, Boolean>setValue((Property<Comparable>)BrewingStandBlock.HAS_BOTTLE[integer8], arr6[integer8]);
                }
                this.level.setBlock(this.worldPosition, bvt7, 2);
            }
        }
    }
    
    public boolean[] getPotionBits() {
        final boolean[] arr2 = new boolean[3];
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            if (!this.items.get(integer3).isEmpty()) {
                arr2[integer3] = true;
            }
        }
        return arr2;
    }
    
    private boolean isBrewable() {
        final ItemStack bcj2 = this.items.get(3);
        if (bcj2.isEmpty()) {
            return false;
        }
        if (!PotionBrewing.isIngredient(bcj2)) {
            return false;
        }
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            final ItemStack bcj3 = this.items.get(integer3);
            if (!bcj3.isEmpty()) {
                if (PotionBrewing.hasMix(bcj3, bcj2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void doBrew() {
        ItemStack bcj2 = this.items.get(3);
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            this.items.set(integer3, PotionBrewing.mix(bcj2, this.items.get(integer3)));
        }
        bcj2.shrink(1);
        final BlockPos ew3 = this.getBlockPos();
        if (bcj2.getItem().hasCraftingRemainingItem()) {
            final ItemStack bcj3 = new ItemStack(bcj2.getItem().getCraftingRemainingItem());
            if (bcj2.isEmpty()) {
                bcj2 = bcj3;
            }
            else if (!this.level.isClientSide) {
                Containers.dropItemStack(this.level, ew3.getX(), ew3.getY(), ew3.getZ(), bcj3);
            }
        }
        this.items.set(3, bcj2);
        this.level.levelEvent(1035, ew3, 0);
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        ContainerHelper.loadAllItems(id, this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY));
        this.brewTime = id.getShort("BrewTime");
        this.fuel = id.getByte("Fuel");
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        id.putShort("BrewTime", (short)this.brewTime);
        ContainerHelper.saveAllItems(id, this.items);
        id.putByte("Fuel", (byte)this.fuel);
        return id;
    }
    
    @Override
    public ItemStack getItem(final int integer) {
        if (integer >= 0 && integer < this.items.size()) {
            return this.items.get(integer);
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public ItemStack removeItem(final int integer1, final int integer2) {
        return ContainerHelper.removeItem((List<ItemStack>)this.items, integer1, integer2);
    }
    
    @Override
    public ItemStack removeItemNoUpdate(final int integer) {
        return ContainerHelper.takeItem((List<ItemStack>)this.items, integer);
    }
    
    @Override
    public void setItem(final int integer, final ItemStack bcj) {
        if (integer >= 0 && integer < this.items.size()) {
            this.items.set(integer, bcj);
        }
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.level.getBlockEntity(this.worldPosition) == this && awg.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }
    
    @Override
    public boolean canPlaceItem(final int integer, final ItemStack bcj) {
        if (integer == 3) {
            return PotionBrewing.isIngredient(bcj);
        }
        final Item bce4 = bcj.getItem();
        if (integer == 4) {
            return bce4 == Items.BLAZE_POWDER;
        }
        return (bce4 == Items.POTION || bce4 == Items.SPLASH_POTION || bce4 == Items.LINGERING_POTION || bce4 == Items.GLASS_BOTTLE) && this.getItem(integer).isEmpty();
    }
    
    @Override
    public int[] getSlotsForFace(final Direction fb) {
        if (fb == Direction.UP) {
            return BrewingStandBlockEntity.SLOTS_FOR_UP;
        }
        if (fb == Direction.DOWN) {
            return BrewingStandBlockEntity.SLOTS_FOR_DOWN;
        }
        return BrewingStandBlockEntity.SLOTS_FOR_SIDES;
    }
    
    @Override
    public boolean canPlaceItemThroughFace(final int integer, final ItemStack bcj, @Nullable final Direction fb) {
        return this.canPlaceItem(integer, bcj);
    }
    
    @Override
    public boolean canTakeItemThroughFace(final int integer, final ItemStack bcj, final Direction fb) {
        return integer != 3 || bcj.getItem() == Items.GLASS_BOTTLE;
    }
    
    public void clearContent() {
        this.items.clear();
    }
    
    @Override
    protected AbstractContainerMenu createMenu(final int integer, final Inventory awf) {
        return new BrewingStandMenu(integer, awf, this, this.dataAccess);
    }
    
    static {
        SLOTS_FOR_UP = new int[] { 3 };
        SLOTS_FOR_DOWN = new int[] { 0, 1, 2, 3 };
        SLOTS_FOR_SIDES = new int[] { 0, 1, 2, 4 };
    }
}
