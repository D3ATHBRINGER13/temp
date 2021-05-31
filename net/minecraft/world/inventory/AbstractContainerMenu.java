package net.minecraft.world.inventory;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import java.util.Iterator;
import net.minecraft.world.Container;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.function.BiFunction;
import net.minecraft.world.level.block.Block;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.player.Player;
import java.util.Set;
import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

public abstract class AbstractContainerMenu {
    private final NonNullList<ItemStack> lastSlots;
    public final List<Slot> slots;
    private final List<DataSlot> dataSlots;
    @Nullable
    private final MenuType<?> menuType;
    public final int containerId;
    private short changeUid;
    private int quickcraftType;
    private int quickcraftStatus;
    private final Set<Slot> quickcraftSlots;
    private final List<ContainerListener> containerListeners;
    private final Set<Player> unSynchedPlayers;
    
    protected AbstractContainerMenu(@Nullable final MenuType<?> azl, final int integer) {
        this.lastSlots = NonNullList.<ItemStack>create();
        this.slots = (List<Slot>)Lists.newArrayList();
        this.dataSlots = (List<DataSlot>)Lists.newArrayList();
        this.quickcraftType = -1;
        this.quickcraftSlots = (Set<Slot>)Sets.newHashSet();
        this.containerListeners = (List<ContainerListener>)Lists.newArrayList();
        this.unSynchedPlayers = (Set<Player>)Sets.newHashSet();
        this.menuType = azl;
        this.containerId = integer;
    }
    
    protected static boolean stillValid(final ContainerLevelAccess ayu, final Player awg, final Block bmv) {
        return ayu.<Boolean>evaluate((java.util.function.BiFunction<Level, BlockPos, Boolean>)((bhr, ew) -> {
            if (bhr.getBlockState(ew).getBlock() != bmv) {
                return false;
            }
            return awg.distanceToSqr(ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5) <= 64.0;
        }), true);
    }
    
    public MenuType<?> getType() {
        if (this.menuType == null) {
            throw new UnsupportedOperationException("Unable to construct this menu by type");
        }
        return this.menuType;
    }
    
    protected static void checkContainerSize(final Container ahc, final int integer) {
        final int integer2 = ahc.getContainerSize();
        if (integer2 < integer) {
            throw new IllegalArgumentException(new StringBuilder().append("Container size ").append(integer2).append(" is smaller than expected ").append(integer).toString());
        }
    }
    
    protected static void checkContainerDataCount(final ContainerData ayt, final int integer) {
        final int integer2 = ayt.getCount();
        if (integer2 < integer) {
            throw new IllegalArgumentException(new StringBuilder().append("Container data count ").append(integer2).append(" is smaller than expected ").append(integer).toString());
        }
    }
    
    protected Slot addSlot(final Slot azx) {
        azx.index = this.slots.size();
        this.slots.add(azx);
        this.lastSlots.add(ItemStack.EMPTY);
        return azx;
    }
    
    protected DataSlot addDataSlot(final DataSlot ayy) {
        this.dataSlots.add(ayy);
        return ayy;
    }
    
    protected void addDataSlots(final ContainerData ayt) {
        for (int integer3 = 0; integer3 < ayt.getCount(); ++integer3) {
            this.addDataSlot(DataSlot.forContainer(ayt, integer3));
        }
    }
    
    public void addSlotListener(final ContainerListener ayv) {
        if (this.containerListeners.contains(ayv)) {
            return;
        }
        this.containerListeners.add(ayv);
        ayv.refreshContainer(this, this.getItems());
        this.broadcastChanges();
    }
    
    public void removeSlotListener(final ContainerListener ayv) {
        this.containerListeners.remove(ayv);
    }
    
    public NonNullList<ItemStack> getItems() {
        final NonNullList<ItemStack> fk2 = NonNullList.<ItemStack>create();
        for (int integer3 = 0; integer3 < this.slots.size(); ++integer3) {
            fk2.add(((Slot)this.slots.get(integer3)).getItem());
        }
        return fk2;
    }
    
    public void broadcastChanges() {
        for (int integer2 = 0; integer2 < this.slots.size(); ++integer2) {
            final ItemStack bcj3 = ((Slot)this.slots.get(integer2)).getItem();
            ItemStack bcj4 = this.lastSlots.get(integer2);
            if (!ItemStack.matches(bcj4, bcj3)) {
                bcj4 = (bcj3.isEmpty() ? ItemStack.EMPTY : bcj3.copy());
                this.lastSlots.set(integer2, bcj4);
                for (final ContainerListener ayv6 : this.containerListeners) {
                    ayv6.slotChanged(this, integer2, bcj4);
                }
            }
        }
        for (int integer2 = 0; integer2 < this.dataSlots.size(); ++integer2) {
            final DataSlot ayy3 = (DataSlot)this.dataSlots.get(integer2);
            if (ayy3.checkAndClearUpdateFlag()) {
                for (final ContainerListener ayv7 : this.containerListeners) {
                    ayv7.setContainerData(this, integer2, ayy3.get());
                }
            }
        }
    }
    
    public boolean clickMenuButton(final Player awg, final int integer) {
        return false;
    }
    
    public Slot getSlot(final int integer) {
        return (Slot)this.slots.get(integer);
    }
    
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        final Slot azx4 = (Slot)this.slots.get(integer);
        if (azx4 != null) {
            return azx4.getItem();
        }
        return ItemStack.EMPTY;
    }
    
    public ItemStack clicked(final int integer1, final int integer2, final ClickType ays, final Player awg) {
        ItemStack bcj6 = ItemStack.EMPTY;
        final Inventory awf7 = awg.inventory;
        if (ays == ClickType.QUICK_CRAFT) {
            final int integer3 = this.quickcraftStatus;
            this.quickcraftStatus = getQuickcraftHeader(integer2);
            if ((integer3 != 1 || this.quickcraftStatus != 2) && integer3 != this.quickcraftStatus) {
                this.resetQuickCraft();
            }
            else if (awf7.getCarried().isEmpty()) {
                this.resetQuickCraft();
            }
            else if (this.quickcraftStatus == 0) {
                this.quickcraftType = getQuickcraftType(integer2);
                if (isValidQuickcraftType(this.quickcraftType, awg)) {
                    this.quickcraftStatus = 1;
                    this.quickcraftSlots.clear();
                }
                else {
                    this.resetQuickCraft();
                }
            }
            else if (this.quickcraftStatus == 1) {
                final Slot azx9 = (Slot)this.slots.get(integer1);
                final ItemStack bcj7 = awf7.getCarried();
                if (azx9 != null && canItemQuickReplace(azx9, bcj7, true) && azx9.mayPlace(bcj7) && (this.quickcraftType == 2 || bcj7.getCount() > this.quickcraftSlots.size()) && this.canDragTo(azx9)) {
                    this.quickcraftSlots.add(azx9);
                }
            }
            else if (this.quickcraftStatus == 2) {
                if (!this.quickcraftSlots.isEmpty()) {
                    final ItemStack bcj8 = awf7.getCarried().copy();
                    int integer4 = awf7.getCarried().getCount();
                    for (final Slot azx10 : this.quickcraftSlots) {
                        final ItemStack bcj9 = awf7.getCarried();
                        if (azx10 != null && canItemQuickReplace(azx10, bcj9, true) && azx10.mayPlace(bcj9) && (this.quickcraftType == 2 || bcj9.getCount() >= this.quickcraftSlots.size()) && this.canDragTo(azx10)) {
                            final ItemStack bcj10 = bcj8.copy();
                            final int integer5 = azx10.hasItem() ? azx10.getItem().getCount() : 0;
                            getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, bcj10, integer5);
                            final int integer6 = Math.min(bcj10.getMaxStackSize(), azx10.getMaxStackSize(bcj10));
                            if (bcj10.getCount() > integer6) {
                                bcj10.setCount(integer6);
                            }
                            integer4 -= bcj10.getCount() - integer5;
                            azx10.set(bcj10);
                        }
                    }
                    bcj8.setCount(integer4);
                    awf7.setCarried(bcj8);
                }
                this.resetQuickCraft();
            }
            else {
                this.resetQuickCraft();
            }
        }
        else if (this.quickcraftStatus != 0) {
            this.resetQuickCraft();
        }
        else if ((ays == ClickType.PICKUP || ays == ClickType.QUICK_MOVE) && (integer2 == 0 || integer2 == 1)) {
            if (integer1 == -999) {
                if (!awf7.getCarried().isEmpty()) {
                    if (integer2 == 0) {
                        awg.drop(awf7.getCarried(), true);
                        awf7.setCarried(ItemStack.EMPTY);
                    }
                    if (integer2 == 1) {
                        awg.drop(awf7.getCarried().split(1), true);
                    }
                }
            }
            else if (ays == ClickType.QUICK_MOVE) {
                if (integer1 < 0) {
                    return ItemStack.EMPTY;
                }
                final Slot azx11 = (Slot)this.slots.get(integer1);
                if (azx11 == null || !azx11.mayPickup(awg)) {
                    return ItemStack.EMPTY;
                }
                for (ItemStack bcj8 = this.quickMoveStack(awg, integer1); !bcj8.isEmpty() && ItemStack.isSame(azx11.getItem(), bcj8); bcj8 = this.quickMoveStack(awg, integer1)) {
                    bcj6 = bcj8.copy();
                }
            }
            else {
                if (integer1 < 0) {
                    return ItemStack.EMPTY;
                }
                final Slot azx11 = (Slot)this.slots.get(integer1);
                if (azx11 != null) {
                    ItemStack bcj8 = azx11.getItem();
                    final ItemStack bcj7 = awf7.getCarried();
                    if (!bcj8.isEmpty()) {
                        bcj6 = bcj8.copy();
                    }
                    if (bcj8.isEmpty()) {
                        if (!bcj7.isEmpty() && azx11.mayPlace(bcj7)) {
                            int integer7 = (integer2 == 0) ? bcj7.getCount() : 1;
                            if (integer7 > azx11.getMaxStackSize(bcj7)) {
                                integer7 = azx11.getMaxStackSize(bcj7);
                            }
                            azx11.set(bcj7.split(integer7));
                        }
                    }
                    else if (azx11.mayPickup(awg)) {
                        if (bcj7.isEmpty()) {
                            if (bcj8.isEmpty()) {
                                azx11.set(ItemStack.EMPTY);
                                awf7.setCarried(ItemStack.EMPTY);
                            }
                            else {
                                final int integer7 = (integer2 == 0) ? bcj8.getCount() : ((bcj8.getCount() + 1) / 2);
                                awf7.setCarried(azx11.remove(integer7));
                                if (bcj8.isEmpty()) {
                                    azx11.set(ItemStack.EMPTY);
                                }
                                azx11.onTake(awg, awf7.getCarried());
                            }
                        }
                        else if (azx11.mayPlace(bcj7)) {
                            if (consideredTheSameItem(bcj8, bcj7)) {
                                int integer7 = (integer2 == 0) ? bcj7.getCount() : 1;
                                if (integer7 > azx11.getMaxStackSize(bcj7) - bcj8.getCount()) {
                                    integer7 = azx11.getMaxStackSize(bcj7) - bcj8.getCount();
                                }
                                if (integer7 > bcj7.getMaxStackSize() - bcj8.getCount()) {
                                    integer7 = bcj7.getMaxStackSize() - bcj8.getCount();
                                }
                                bcj7.shrink(integer7);
                                bcj8.grow(integer7);
                            }
                            else if (bcj7.getCount() <= azx11.getMaxStackSize(bcj7)) {
                                azx11.set(bcj7);
                                awf7.setCarried(bcj8);
                            }
                        }
                        else if (bcj7.getMaxStackSize() > 1 && consideredTheSameItem(bcj8, bcj7) && !bcj8.isEmpty()) {
                            final int integer7 = bcj8.getCount();
                            if (integer7 + bcj7.getCount() <= bcj7.getMaxStackSize()) {
                                bcj7.grow(integer7);
                                bcj8 = azx11.remove(integer7);
                                if (bcj8.isEmpty()) {
                                    azx11.set(ItemStack.EMPTY);
                                }
                                azx11.onTake(awg, awf7.getCarried());
                            }
                        }
                    }
                    azx11.setChanged();
                }
            }
        }
        else if (ays == ClickType.SWAP && integer2 >= 0 && integer2 < 9) {
            final Slot azx11 = (Slot)this.slots.get(integer1);
            final ItemStack bcj8 = awf7.getItem(integer2);
            final ItemStack bcj7 = azx11.getItem();
            if (!bcj8.isEmpty() || !bcj7.isEmpty()) {
                if (bcj8.isEmpty()) {
                    if (azx11.mayPickup(awg)) {
                        awf7.setItem(integer2, bcj7);
                        azx11.onSwapCraft(bcj7.getCount());
                        azx11.set(ItemStack.EMPTY);
                        azx11.onTake(awg, bcj7);
                    }
                }
                else if (bcj7.isEmpty()) {
                    if (azx11.mayPlace(bcj8)) {
                        final int integer7 = azx11.getMaxStackSize(bcj8);
                        if (bcj8.getCount() > integer7) {
                            azx11.set(bcj8.split(integer7));
                        }
                        else {
                            azx11.set(bcj8);
                            awf7.setItem(integer2, ItemStack.EMPTY);
                        }
                    }
                }
                else if (azx11.mayPickup(awg) && azx11.mayPlace(bcj8)) {
                    final int integer7 = azx11.getMaxStackSize(bcj8);
                    if (bcj8.getCount() > integer7) {
                        azx11.set(bcj8.split(integer7));
                        azx11.onTake(awg, bcj7);
                        if (!awf7.add(bcj7)) {
                            awg.drop(bcj7, true);
                        }
                    }
                    else {
                        azx11.set(bcj8);
                        awf7.setItem(integer2, bcj7);
                        azx11.onTake(awg, bcj7);
                    }
                }
            }
        }
        else if (ays == ClickType.CLONE && awg.abilities.instabuild && awf7.getCarried().isEmpty() && integer1 >= 0) {
            final Slot azx11 = (Slot)this.slots.get(integer1);
            if (azx11 != null && azx11.hasItem()) {
                final ItemStack bcj8 = azx11.getItem().copy();
                bcj8.setCount(bcj8.getMaxStackSize());
                awf7.setCarried(bcj8);
            }
        }
        else if (ays == ClickType.THROW && awf7.getCarried().isEmpty() && integer1 >= 0) {
            final Slot azx11 = (Slot)this.slots.get(integer1);
            if (azx11 != null && azx11.hasItem() && azx11.mayPickup(awg)) {
                final ItemStack bcj8 = azx11.remove((integer2 == 0) ? 1 : azx11.getItem().getCount());
                azx11.onTake(awg, bcj8);
                awg.drop(bcj8, true);
            }
        }
        else if (ays == ClickType.PICKUP_ALL && integer1 >= 0) {
            final Slot azx11 = (Slot)this.slots.get(integer1);
            final ItemStack bcj8 = awf7.getCarried();
            if (!bcj8.isEmpty() && (azx11 == null || !azx11.hasItem() || !azx11.mayPickup(awg))) {
                final int integer4 = (integer2 == 0) ? 0 : (this.slots.size() - 1);
                final int integer7 = (integer2 == 0) ? 1 : -1;
                for (int integer8 = 0; integer8 < 2; ++integer8) {
                    for (int integer9 = integer4; integer9 >= 0 && integer9 < this.slots.size() && bcj8.getCount() < bcj8.getMaxStackSize(); integer9 += integer7) {
                        final Slot azx12 = (Slot)this.slots.get(integer9);
                        if (azx12.hasItem() && canItemQuickReplace(azx12, bcj8, true) && azx12.mayPickup(awg) && this.canTakeItemForPickAll(bcj8, azx12)) {
                            final ItemStack bcj11 = azx12.getItem();
                            if (integer8 != 0 || bcj11.getCount() != bcj11.getMaxStackSize()) {
                                final int integer6 = Math.min(bcj8.getMaxStackSize() - bcj8.getCount(), bcj11.getCount());
                                final ItemStack bcj12 = azx12.remove(integer6);
                                bcj8.grow(integer6);
                                if (bcj12.isEmpty()) {
                                    azx12.set(ItemStack.EMPTY);
                                }
                                azx12.onTake(awg, bcj12);
                            }
                        }
                    }
                }
            }
            this.broadcastChanges();
        }
        return bcj6;
    }
    
    public static boolean consideredTheSameItem(final ItemStack bcj1, final ItemStack bcj2) {
        return bcj1.getItem() == bcj2.getItem() && ItemStack.tagMatches(bcj1, bcj2);
    }
    
    public boolean canTakeItemForPickAll(final ItemStack bcj, final Slot azx) {
        return true;
    }
    
    public void removed(final Player awg) {
        final Inventory awf3 = awg.inventory;
        if (!awf3.getCarried().isEmpty()) {
            awg.drop(awf3.getCarried(), false);
            awf3.setCarried(ItemStack.EMPTY);
        }
    }
    
    protected void clearContainer(final Player awg, final Level bhr, final Container ahc) {
        if (!awg.isAlive() || (awg instanceof ServerPlayer && ((ServerPlayer)awg).hasDisconnected())) {
            for (int integer5 = 0; integer5 < ahc.getContainerSize(); ++integer5) {
                awg.drop(ahc.removeItemNoUpdate(integer5), false);
            }
            return;
        }
        for (int integer5 = 0; integer5 < ahc.getContainerSize(); ++integer5) {
            awg.inventory.placeItemBackInInventory(bhr, ahc.removeItemNoUpdate(integer5));
        }
    }
    
    public void slotsChanged(final Container ahc) {
        this.broadcastChanges();
    }
    
    public void setItem(final int integer, final ItemStack bcj) {
        this.getSlot(integer).set(bcj);
    }
    
    public void setAll(final List<ItemStack> list) {
        for (int integer3 = 0; integer3 < list.size(); ++integer3) {
            this.getSlot(integer3).set((ItemStack)list.get(integer3));
        }
    }
    
    public void setData(final int integer1, final int integer2) {
        ((DataSlot)this.dataSlots.get(integer1)).set(integer2);
    }
    
    public short backup(final Inventory awf) {
        return (short)(++this.changeUid);
    }
    
    public boolean isSynched(final Player awg) {
        return !this.unSynchedPlayers.contains(awg);
    }
    
    public void setSynched(final Player awg, final boolean boolean2) {
        if (boolean2) {
            this.unSynchedPlayers.remove(awg);
        }
        else {
            this.unSynchedPlayers.add(awg);
        }
    }
    
    public abstract boolean stillValid(final Player awg);
    
    protected boolean moveItemStackTo(final ItemStack bcj, final int integer2, final int integer3, final boolean boolean4) {
        boolean boolean5 = false;
        int integer4 = integer2;
        if (boolean4) {
            integer4 = integer3 - 1;
        }
        if (bcj.isStackable()) {
            while (!bcj.isEmpty()) {
                if (boolean4) {
                    if (integer4 < integer2) {
                        break;
                    }
                }
                else if (integer4 >= integer3) {
                    break;
                }
                final Slot azx8 = (Slot)this.slots.get(integer4);
                final ItemStack bcj2 = azx8.getItem();
                if (!bcj2.isEmpty() && consideredTheSameItem(bcj, bcj2)) {
                    final int integer5 = bcj2.getCount() + bcj.getCount();
                    if (integer5 <= bcj.getMaxStackSize()) {
                        bcj.setCount(0);
                        bcj2.setCount(integer5);
                        azx8.setChanged();
                        boolean5 = true;
                    }
                    else if (bcj2.getCount() < bcj.getMaxStackSize()) {
                        bcj.shrink(bcj.getMaxStackSize() - bcj2.getCount());
                        bcj2.setCount(bcj.getMaxStackSize());
                        azx8.setChanged();
                        boolean5 = true;
                    }
                }
                if (boolean4) {
                    --integer4;
                }
                else {
                    ++integer4;
                }
            }
        }
        if (!bcj.isEmpty()) {
            if (boolean4) {
                integer4 = integer3 - 1;
            }
            else {
                integer4 = integer2;
            }
            while (true) {
                if (boolean4) {
                    if (integer4 < integer2) {
                        break;
                    }
                }
                else if (integer4 >= integer3) {
                    break;
                }
                final Slot azx8 = (Slot)this.slots.get(integer4);
                final ItemStack bcj2 = azx8.getItem();
                if (bcj2.isEmpty() && azx8.mayPlace(bcj)) {
                    if (bcj.getCount() > azx8.getMaxStackSize()) {
                        azx8.set(bcj.split(azx8.getMaxStackSize()));
                    }
                    else {
                        azx8.set(bcj.split(bcj.getCount()));
                    }
                    azx8.setChanged();
                    boolean5 = true;
                    break;
                }
                if (boolean4) {
                    --integer4;
                }
                else {
                    ++integer4;
                }
            }
        }
        return boolean5;
    }
    
    public static int getQuickcraftType(final int integer) {
        return integer >> 2 & 0x3;
    }
    
    public static int getQuickcraftHeader(final int integer) {
        return integer & 0x3;
    }
    
    public static int getQuickcraftMask(final int integer1, final int integer2) {
        return (integer1 & 0x3) | (integer2 & 0x3) << 2;
    }
    
    public static boolean isValidQuickcraftType(final int integer, final Player awg) {
        return integer == 0 || integer == 1 || (integer == 2 && awg.abilities.instabuild);
    }
    
    protected void resetQuickCraft() {
        this.quickcraftStatus = 0;
        this.quickcraftSlots.clear();
    }
    
    public static boolean canItemQuickReplace(@Nullable final Slot azx, final ItemStack bcj, final boolean boolean3) {
        final boolean boolean4 = azx == null || !azx.hasItem();
        if (!boolean4 && bcj.sameItem(azx.getItem()) && ItemStack.tagMatches(azx.getItem(), bcj)) {
            return azx.getItem().getCount() + (boolean3 ? 0 : bcj.getCount()) <= bcj.getMaxStackSize();
        }
        return boolean4;
    }
    
    public static void getQuickCraftSlotCount(final Set<Slot> set, final int integer2, final ItemStack bcj, final int integer4) {
        switch (integer2) {
            case 0: {
                bcj.setCount(Mth.floor(bcj.getCount() / (float)set.size()));
                break;
            }
            case 1: {
                bcj.setCount(1);
                break;
            }
            case 2: {
                bcj.setCount(bcj.getItem().getMaxStackSize());
                break;
            }
        }
        bcj.grow(integer4);
    }
    
    public boolean canDragTo(final Slot azx) {
        return true;
    }
    
    public static int getRedstoneSignalFromBlockEntity(@Nullable final BlockEntity btw) {
        if (btw instanceof Container) {
            return getRedstoneSignalFromContainer((Container)btw);
        }
        return 0;
    }
    
    public static int getRedstoneSignalFromContainer(@Nullable final Container ahc) {
        if (ahc == null) {
            return 0;
        }
        int integer2 = 0;
        float float3 = 0.0f;
        for (int integer3 = 0; integer3 < ahc.getContainerSize(); ++integer3) {
            final ItemStack bcj5 = ahc.getItem(integer3);
            if (!bcj5.isEmpty()) {
                float3 += bcj5.getCount() / (float)Math.min(ahc.getMaxStackSize(), bcj5.getMaxStackSize());
                ++integer2;
            }
        }
        float3 /= ahc.getContainerSize();
        return Mth.floor(float3 * 14.0f) + ((integer2 > 0) ? 1 : 0);
    }
}
