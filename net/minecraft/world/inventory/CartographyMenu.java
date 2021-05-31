package net.minecraft.world.inventory;

import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.MapItem;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.function.BiFunction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;

public class CartographyMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private boolean quickMoved;
    public final Container container;
    private final ResultContainer resultContainer;
    
    public CartographyMenu(final int integer, final Inventory awf) {
        this(integer, awf, ContainerLevelAccess.NULL);
    }
    
    public CartographyMenu(final int integer, final Inventory awf, final ContainerLevelAccess ayu) {
        super(MenuType.CARTOGRAPHY, integer);
        this.container = new SimpleContainer(2) {
            @Override
            public void setChanged() {
                CartographyMenu.this.slotsChanged(this);
                super.setChanged();
            }
        };
        this.resultContainer = new ResultContainer() {
            @Override
            public void setChanged() {
                CartographyMenu.this.slotsChanged(this);
                super.setChanged();
            }
        };
        this.access = ayu;
        this.addSlot(new Slot(this.container, 0, 15, 15) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return bcj.getItem() == Items.FILLED_MAP;
            }
        });
        this.addSlot(new Slot(this.container, 1, 15, 52) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                final Item bce3 = bcj.getItem();
                return bce3 == Items.PAPER || bce3 == Items.MAP || bce3 == Items.GLASS_PANE;
            }
        });
        this.addSlot(new Slot(this.resultContainer, 2, 145, 39) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return false;
            }
            
            @Override
            public ItemStack remove(final int integer) {
                final ItemStack bcj3 = super.remove(integer);
                final ItemStack bcj4 = (ItemStack)ayu.evaluate((java.util.function.BiFunction<Level, BlockPos, Object>)((bhr, ew) -> {
                    if (!CartographyMenu.this.quickMoved && CartographyMenu.this.container.getItem(1).getItem() == Items.GLASS_PANE) {
                        final ItemStack bcj2 = MapItem.lockMap(bhr, CartographyMenu.this.container.getItem(0));
                        if (bcj2 != null) {
                            bcj2.setCount(1);
                            return bcj2;
                        }
                    }
                    return bcj3;
                })).orElse(bcj3);
                CartographyMenu.this.container.removeItem(0, 1);
                CartographyMenu.this.container.removeItem(1, 1);
                return bcj4;
            }
            
            @Override
            protected void onQuickCraft(final ItemStack bcj, final int integer) {
                this.remove(integer);
                super.onQuickCraft(bcj, integer);
            }
            
            @Override
            public ItemStack onTake(final Player awg, final ItemStack bcj) {
                bcj.getItem().onCraftedBy(bcj, awg.level, awg);
                ayu.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> bhr.playSound(null, ew, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f)));
                return super.onTake(awg, bcj);
            }
        });
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                this.addSlot(new Slot(awf, integer3 + integer2 * 9 + 9, 8 + integer3 * 18, 84 + integer2 * 18));
            }
        }
        for (int integer2 = 0; integer2 < 9; ++integer2) {
            this.addSlot(new Slot(awf, integer2, 8 + integer2 * 18, 142));
        }
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return AbstractContainerMenu.stillValid(this.access, awg, Blocks.CARTOGRAPHY_TABLE);
    }
    
    @Override
    public void slotsChanged(final Container ahc) {
        final ItemStack bcj3 = this.container.getItem(0);
        final ItemStack bcj4 = this.container.getItem(1);
        final ItemStack bcj5 = this.resultContainer.getItem(2);
        if (!bcj5.isEmpty() && (bcj3.isEmpty() || bcj4.isEmpty())) {
            this.resultContainer.removeItemNoUpdate(2);
        }
        else if (!bcj3.isEmpty() && !bcj4.isEmpty()) {
            this.setupResultSlot(bcj3, bcj4, bcj5);
        }
    }
    
    private void setupResultSlot(final ItemStack bcj1, final ItemStack bcj2, final ItemStack bcj3) {
        this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> {
            final Item bce7 = bcj2.getItem();
            final MapItemSavedData coh8 = MapItem.getSavedData(bcj1, bhr);
            if (coh8 == null) {
                return;
            }
            ItemStack bcj4;
            if (bce7 == Items.PAPER && !coh8.locked && coh8.scale < 4) {
                bcj4 = bcj1.copy();
                bcj4.setCount(1);
                bcj4.getOrCreateTag().putInt("map_scale_direction", 1);
                this.broadcastChanges();
            }
            else if (bce7 == Items.GLASS_PANE && !coh8.locked) {
                bcj4 = bcj1.copy();
                bcj4.setCount(1);
                this.broadcastChanges();
            }
            else {
                if (bce7 != Items.MAP) {
                    this.resultContainer.removeItemNoUpdate(2);
                    this.broadcastChanges();
                    return;
                }
                bcj4 = bcj1.copy();
                bcj4.setCount(2);
                this.broadcastChanges();
            }
            if (!ItemStack.matches(bcj4, bcj3)) {
                this.resultContainer.setItem(2, bcj4);
                this.broadcastChanges();
            }
        }));
    }
    
    @Override
    public boolean canTakeItemForPickAll(final ItemStack bcj, final Slot azx) {
        return false;
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            ItemStack bcj6;
            final ItemStack bcj5 = bcj6 = azx5.getItem();
            final Item bce8 = bcj6.getItem();
            bcj4 = bcj6.copy();
            if (integer == 2) {
                if (this.container.getItem(1).getItem() == Items.GLASS_PANE) {
                    bcj6 = (ItemStack)this.access.evaluate((java.util.function.BiFunction<Level, BlockPos, Object>)((bhr, ew) -> {
                        final ItemStack bcj2 = MapItem.lockMap(bhr, this.container.getItem(0));
                        if (bcj2 != null) {
                            bcj2.setCount(1);
                            return bcj2;
                        }
                        return bcj5;
                    })).orElse(bcj6);
                }
                bce8.onCraftedBy(bcj6, awg.level, awg);
                if (!this.moveItemStackTo(bcj6, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                azx5.onQuickCraft(bcj6, bcj4);
            }
            else if (integer == 1 || integer == 0) {
                if (!this.moveItemStackTo(bcj6, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (bce8 == Items.FILLED_MAP) {
                if (!this.moveItemStackTo(bcj6, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (bce8 == Items.PAPER || bce8 == Items.MAP || bce8 == Items.GLASS_PANE) {
                if (!this.moveItemStackTo(bcj6, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 3 && integer < 30) {
                if (!this.moveItemStackTo(bcj6, 30, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 30 && integer < 39 && !this.moveItemStackTo(bcj6, 3, 30, false)) {
                return ItemStack.EMPTY;
            }
            if (bcj6.isEmpty()) {
                azx5.set(ItemStack.EMPTY);
            }
            azx5.setChanged();
            if (bcj6.getCount() == bcj4.getCount()) {
                return ItemStack.EMPTY;
            }
            this.quickMoved = true;
            azx5.onTake(awg, bcj6);
            this.quickMoved = false;
            this.broadcastChanges();
        }
        return bcj4;
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        this.resultContainer.removeItemNoUpdate(2);
        this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> this.clearContainer(awg, awg.level, this.container)));
    }
}
