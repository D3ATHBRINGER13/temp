package net.minecraft.world.level.block.entity;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.ContainerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import java.util.Iterator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import java.util.Random;

public class DispenserBlockEntity extends RandomizableContainerBlockEntity {
    private static final Random RANDOM;
    private NonNullList<ItemStack> items;
    
    protected DispenserBlockEntity(final BlockEntityType<?> btx) {
        super(btx);
        this.items = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
    }
    
    public DispenserBlockEntity() {
        this(BlockEntityType.DISPENSER);
    }
    
    @Override
    public int getContainerSize() {
        return 9;
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
    
    public int getRandomSlot() {
        this.unpackLootTable(null);
        int integer2 = -1;
        int integer3 = 1;
        for (int integer4 = 0; integer4 < this.items.size(); ++integer4) {
            if (!this.items.get(integer4).isEmpty() && DispenserBlockEntity.RANDOM.nextInt(integer3++) == 0) {
                integer2 = integer4;
            }
        }
        return integer2;
    }
    
    public int addItem(final ItemStack bcj) {
        for (int integer3 = 0; integer3 < this.items.size(); ++integer3) {
            if (this.items.get(integer3).isEmpty()) {
                this.setItem(integer3, bcj);
                return integer3;
            }
        }
        return -1;
    }
    
    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.dispenser", new Object[0]);
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(id)) {
            ContainerHelper.loadAllItems(id, this.items);
        }
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        if (!this.trySaveLootTable(id)) {
            ContainerHelper.saveAllItems(id, this.items);
        }
        return id;
    }
    
    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }
    
    @Override
    protected void setItems(final NonNullList<ItemStack> fk) {
        this.items = fk;
    }
    
    @Override
    protected AbstractContainerMenu createMenu(final int integer, final Inventory awf) {
        return new DispenserMenu(integer, awf, this);
    }
    
    static {
        RANDOM = new Random();
    }
}
