package net.minecraft.world.level.block.entity;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.Iterator;
import net.minecraft.world.ContainerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

public class BarrelBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> items;
    private int openCount;
    
    private BarrelBlockEntity(final BlockEntityType<?> btx) {
        super(btx);
        this.items = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
    }
    
    public BarrelBlockEntity() {
        this(BlockEntityType.BARREL);
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
    public void load(final CompoundTag id) {
        super.load(id);
        this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(id)) {
            ContainerHelper.loadAllItems(id, this.items);
        }
    }
    
    @Override
    public int getContainerSize() {
        return 27;
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
    public ItemStack getItem(final int integer) {
        return this.items.get(integer);
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
        this.items.set(integer, bcj);
        if (bcj.getCount() > this.getMaxStackSize()) {
            bcj.setCount(this.getMaxStackSize());
        }
    }
    
    @Override
    public void clearContent() {
        this.items.clear();
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
    protected Component getDefaultName() {
        return new TranslatableComponent("container.barrel", new Object[0]);
    }
    
    @Override
    protected AbstractContainerMenu createMenu(final int integer, final Inventory awf) {
        return ChestMenu.threeRows(integer, awf, this);
    }
    
    @Override
    public void startOpen(final Player awg) {
        if (!awg.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++this.openCount;
            final BlockState bvt3 = this.getBlockState();
            final boolean boolean4 = bvt3.<Boolean>getValue((Property<Boolean>)BarrelBlock.OPEN);
            if (!boolean4) {
                this.playSound(bvt3, SoundEvents.BARREL_OPEN);
                this.updateBlockState(bvt3, true);
            }
            this.scheduleRecheck();
        }
    }
    
    private void scheduleRecheck() {
        this.level.getBlockTicks().scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 5);
    }
    
    public void recheckOpen() {
        final int integer2 = this.worldPosition.getX();
        final int integer3 = this.worldPosition.getY();
        final int integer4 = this.worldPosition.getZ();
        this.openCount = ChestBlockEntity.getOpenCount(this.level, this, integer2, integer3, integer4);
        if (this.openCount > 0) {
            this.scheduleRecheck();
        }
        else {
            final BlockState bvt5 = this.getBlockState();
            if (bvt5.getBlock() != Blocks.BARREL) {
                this.setRemoved();
                return;
            }
            final boolean boolean6 = bvt5.<Boolean>getValue((Property<Boolean>)BarrelBlock.OPEN);
            if (boolean6) {
                this.playSound(bvt5, SoundEvents.BARREL_CLOSE);
                this.updateBlockState(bvt5, false);
            }
        }
    }
    
    @Override
    public void stopOpen(final Player awg) {
        if (!awg.isSpectator()) {
            --this.openCount;
        }
    }
    
    private void updateBlockState(final BlockState bvt, final boolean boolean2) {
        this.level.setBlock(this.getBlockPos(), ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)BarrelBlock.OPEN, boolean2), 3);
    }
    
    private void playSound(final BlockState bvt, final SoundEvent yo) {
        final Vec3i fs4 = bvt.<Direction>getValue((Property<Direction>)BarrelBlock.FACING).getNormal();
        final double double5 = this.worldPosition.getX() + 0.5 + fs4.getX() / 2.0;
        final double double6 = this.worldPosition.getY() + 0.5 + fs4.getY() / 2.0;
        final double double7 = this.worldPosition.getZ() + 0.5 + fs4.getZ() / 2.0;
        this.level.playSound(null, double5, double6, double7, yo, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
    }
}
