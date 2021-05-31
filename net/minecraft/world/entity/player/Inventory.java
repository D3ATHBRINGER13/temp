package net.minecraft.world.entity.player;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.tags.Tag;
import java.util.function.Consumer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ContainerHelper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import java.util.Iterator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import java.util.function.Predicate;
import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Nameable;
import net.minecraft.world.Container;

public class Inventory implements Container, Nameable {
    public final NonNullList<ItemStack> items;
    public final NonNullList<ItemStack> armor;
    public final NonNullList<ItemStack> offhand;
    private final List<NonNullList<ItemStack>> compartments;
    public int selected;
    public final Player player;
    private ItemStack carried;
    private int timesChanged;
    
    public Inventory(final Player awg) {
        this.items = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
        this.armor = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
        this.offhand = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
        this.compartments = (List<NonNullList<ItemStack>>)ImmutableList.of(this.items, this.armor, this.offhand);
        this.carried = ItemStack.EMPTY;
        this.player = awg;
    }
    
    public ItemStack getSelected() {
        if (isHotbarSlot(this.selected)) {
            return this.items.get(this.selected);
        }
        return ItemStack.EMPTY;
    }
    
    public static int getSelectionSize() {
        return 9;
    }
    
    private boolean hasRemainingSpaceForItem(final ItemStack bcj1, final ItemStack bcj2) {
        return !bcj1.isEmpty() && this.isSameItem(bcj1, bcj2) && bcj1.isStackable() && bcj1.getCount() < bcj1.getMaxStackSize() && bcj1.getCount() < this.getMaxStackSize();
    }
    
    private boolean isSameItem(final ItemStack bcj1, final ItemStack bcj2) {
        return bcj1.getItem() == bcj2.getItem() && ItemStack.tagMatches(bcj1, bcj2);
    }
    
    public int getFreeSlot() {
        for (int integer2 = 0; integer2 < this.items.size(); ++integer2) {
            if (this.items.get(integer2).isEmpty()) {
                return integer2;
            }
        }
        return -1;
    }
    
    public void setPickedItem(final ItemStack bcj) {
        final int integer3 = this.findSlotMatchingItem(bcj);
        if (isHotbarSlot(integer3)) {
            this.selected = integer3;
            return;
        }
        if (integer3 == -1) {
            this.selected = this.getSuitableHotbarSlot();
            if (!this.items.get(this.selected).isEmpty()) {
                final int integer4 = this.getFreeSlot();
                if (integer4 != -1) {
                    this.items.set(integer4, this.items.get(this.selected));
                }
            }
            this.items.set(this.selected, bcj);
        }
        else {
            this.pickSlot(integer3);
        }
    }
    
    public void pickSlot(final int integer) {
        this.selected = this.getSuitableHotbarSlot();
        final ItemStack bcj3 = this.items.get(this.selected);
        this.items.set(this.selected, this.items.get(integer));
        this.items.set(integer, bcj3);
    }
    
    public static boolean isHotbarSlot(final int integer) {
        return integer >= 0 && integer < 9;
    }
    
    public int findSlotMatchingItem(final ItemStack bcj) {
        for (int integer3 = 0; integer3 < this.items.size(); ++integer3) {
            if (!this.items.get(integer3).isEmpty() && this.isSameItem(bcj, this.items.get(integer3))) {
                return integer3;
            }
        }
        return -1;
    }
    
    public int findSlotMatchingUnusedItem(final ItemStack bcj) {
        for (int integer3 = 0; integer3 < this.items.size(); ++integer3) {
            final ItemStack bcj2 = this.items.get(integer3);
            if (!this.items.get(integer3).isEmpty() && this.isSameItem(bcj, this.items.get(integer3)) && !this.items.get(integer3).isDamaged() && !bcj2.isEnchanted() && !bcj2.hasCustomHoverName()) {
                return integer3;
            }
        }
        return -1;
    }
    
    public int getSuitableHotbarSlot() {
        for (int integer2 = 0; integer2 < 9; ++integer2) {
            final int integer3 = (this.selected + integer2) % 9;
            if (this.items.get(integer3).isEmpty()) {
                return integer3;
            }
        }
        for (int integer2 = 0; integer2 < 9; ++integer2) {
            final int integer3 = (this.selected + integer2) % 9;
            if (!this.items.get(integer3).isEnchanted()) {
                return integer3;
            }
        }
        return this.selected;
    }
    
    public void swapPaint(double double1) {
        if (double1 > 0.0) {
            double1 = 1.0;
        }
        if (double1 < 0.0) {
            double1 = -1.0;
        }
        this.selected -= (int)double1;
        while (this.selected < 0) {
            this.selected += 9;
        }
        while (this.selected >= 9) {
            this.selected -= 9;
        }
    }
    
    public int clearInventory(final Predicate<ItemStack> predicate, final int integer) {
        int integer2 = 0;
        for (int integer3 = 0; integer3 < this.getContainerSize(); ++integer3) {
            final ItemStack bcj6 = this.getItem(integer3);
            if (!bcj6.isEmpty()) {
                if (predicate.test(bcj6)) {
                    final int integer4 = (integer <= 0) ? bcj6.getCount() : Math.min(integer - integer2, bcj6.getCount());
                    integer2 += integer4;
                    if (integer != 0) {
                        bcj6.shrink(integer4);
                        if (bcj6.isEmpty()) {
                            this.setItem(integer3, ItemStack.EMPTY);
                        }
                        if (integer > 0 && integer2 >= integer) {
                            return integer2;
                        }
                    }
                }
            }
        }
        if (!this.carried.isEmpty() && predicate.test(this.carried)) {
            final int integer3 = (integer <= 0) ? this.carried.getCount() : Math.min(integer - integer2, this.carried.getCount());
            integer2 += integer3;
            if (integer != 0) {
                this.carried.shrink(integer3);
                if (this.carried.isEmpty()) {
                    this.carried = ItemStack.EMPTY;
                }
                if (integer > 0 && integer2 >= integer) {
                    return integer2;
                }
            }
        }
        return integer2;
    }
    
    private int addResource(final ItemStack bcj) {
        int integer3 = this.getSlotWithRemainingSpace(bcj);
        if (integer3 == -1) {
            integer3 = this.getFreeSlot();
        }
        if (integer3 == -1) {
            return bcj.getCount();
        }
        return this.addResource(integer3, bcj);
    }
    
    private int addResource(final int integer, final ItemStack bcj) {
        final Item bce4 = bcj.getItem();
        int integer2 = bcj.getCount();
        ItemStack bcj2 = this.getItem(integer);
        if (bcj2.isEmpty()) {
            bcj2 = new ItemStack(bce4, 0);
            if (bcj.hasTag()) {
                bcj2.setTag(bcj.getTag().copy());
            }
            this.setItem(integer, bcj2);
        }
        int integer3 = integer2;
        if (integer3 > bcj2.getMaxStackSize() - bcj2.getCount()) {
            integer3 = bcj2.getMaxStackSize() - bcj2.getCount();
        }
        if (integer3 > this.getMaxStackSize() - bcj2.getCount()) {
            integer3 = this.getMaxStackSize() - bcj2.getCount();
        }
        if (integer3 == 0) {
            return integer2;
        }
        integer2 -= integer3;
        bcj2.grow(integer3);
        bcj2.setPopTime(5);
        return integer2;
    }
    
    public int getSlotWithRemainingSpace(final ItemStack bcj) {
        if (this.hasRemainingSpaceForItem(this.getItem(this.selected), bcj)) {
            return this.selected;
        }
        if (this.hasRemainingSpaceForItem(this.getItem(40), bcj)) {
            return 40;
        }
        for (int integer3 = 0; integer3 < this.items.size(); ++integer3) {
            if (this.hasRemainingSpaceForItem(this.items.get(integer3), bcj)) {
                return integer3;
            }
        }
        return -1;
    }
    
    public void tick() {
        for (final NonNullList<ItemStack> fk3 : this.compartments) {
            for (int integer4 = 0; integer4 < fk3.size(); ++integer4) {
                if (!fk3.get(integer4).isEmpty()) {
                    fk3.get(integer4).inventoryTick(this.player.level, this.player, integer4, this.selected == integer4);
                }
            }
        }
    }
    
    public boolean add(final ItemStack bcj) {
        return this.add(-1, bcj);
    }
    
    public boolean add(int integer, final ItemStack bcj) {
        if (bcj.isEmpty()) {
            return false;
        }
        try {
            if (!bcj.isDamaged()) {
                int integer2;
                do {
                    integer2 = bcj.getCount();
                    if (integer == -1) {
                        bcj.setCount(this.addResource(bcj));
                    }
                    else {
                        bcj.setCount(this.addResource(integer, bcj));
                    }
                } while (!bcj.isEmpty() && bcj.getCount() < integer2);
                if (bcj.getCount() == integer2 && this.player.abilities.instabuild) {
                    bcj.setCount(0);
                    return true;
                }
                return bcj.getCount() < integer2;
            }
            else {
                if (integer == -1) {
                    integer = this.getFreeSlot();
                }
                if (integer >= 0) {
                    this.items.set(integer, bcj.copy());
                    this.items.get(integer).setPopTime(5);
                    bcj.setCount(0);
                    return true;
                }
                if (this.player.abilities.instabuild) {
                    bcj.setCount(0);
                    return true;
                }
                return false;
            }
        }
        catch (Throwable throwable4) {
            final CrashReport d5 = CrashReport.forThrowable(throwable4, "Adding item to inventory");
            final CrashReportCategory e6 = d5.addCategory("Item being added");
            e6.setDetail("Item ID", Item.getId(bcj.getItem()));
            e6.setDetail("Item data", bcj.getDamageValue());
            e6.setDetail("Item name", (CrashReportDetail<String>)(() -> bcj.getHoverName().getString()));
            throw new ReportedException(d5);
        }
    }
    
    public void placeItemBackInInventory(final Level bhr, final ItemStack bcj) {
        if (bhr.isClientSide) {
            return;
        }
        while (!bcj.isEmpty()) {
            int integer4 = this.getSlotWithRemainingSpace(bcj);
            if (integer4 == -1) {
                integer4 = this.getFreeSlot();
            }
            if (integer4 == -1) {
                this.player.drop(bcj, false);
                break;
            }
            final int integer5 = bcj.getMaxStackSize() - this.getItem(integer4).getCount();
            if (!this.add(integer4, bcj.split(integer5))) {
                continue;
            }
            ((ServerPlayer)this.player).connection.send(new ClientboundContainerSetSlotPacket(-2, integer4, this.getItem(integer4)));
        }
    }
    
    public ItemStack removeItem(int integer1, final int integer2) {
        List<ItemStack> list4 = null;
        for (final NonNullList<ItemStack> fk6 : this.compartments) {
            if (integer1 < fk6.size()) {
                list4 = (List<ItemStack>)fk6;
                break;
            }
            integer1 -= fk6.size();
        }
        if (list4 != null && !((ItemStack)list4.get(integer1)).isEmpty()) {
            return ContainerHelper.removeItem(list4, integer1, integer2);
        }
        return ItemStack.EMPTY;
    }
    
    public void removeItem(final ItemStack bcj) {
        for (final NonNullList<ItemStack> fk4 : this.compartments) {
            for (int integer5 = 0; integer5 < fk4.size(); ++integer5) {
                if (fk4.get(integer5) == bcj) {
                    fk4.set(integer5, ItemStack.EMPTY);
                    break;
                }
            }
        }
    }
    
    public ItemStack removeItemNoUpdate(int integer) {
        NonNullList<ItemStack> fk3 = null;
        for (final NonNullList<ItemStack> fk4 : this.compartments) {
            if (integer < fk4.size()) {
                fk3 = fk4;
                break;
            }
            integer -= fk4.size();
        }
        if (fk3 != null && !fk3.get(integer).isEmpty()) {
            final ItemStack bcj4 = fk3.get(integer);
            fk3.set(integer, ItemStack.EMPTY);
            return bcj4;
        }
        return ItemStack.EMPTY;
    }
    
    public void setItem(int integer, final ItemStack bcj) {
        NonNullList<ItemStack> fk4 = null;
        for (final NonNullList<ItemStack> fk5 : this.compartments) {
            if (integer < fk5.size()) {
                fk4 = fk5;
                break;
            }
            integer -= fk5.size();
        }
        if (fk4 != null) {
            fk4.set(integer, bcj);
        }
    }
    
    public float getDestroySpeed(final BlockState bvt) {
        return this.items.get(this.selected).getDestroySpeed(bvt);
    }
    
    public ListTag save(final ListTag ik) {
        for (int integer3 = 0; integer3 < this.items.size(); ++integer3) {
            if (!this.items.get(integer3).isEmpty()) {
                final CompoundTag id4 = new CompoundTag();
                id4.putByte("Slot", (byte)integer3);
                this.items.get(integer3).save(id4);
                ik.add(id4);
            }
        }
        for (int integer3 = 0; integer3 < this.armor.size(); ++integer3) {
            if (!this.armor.get(integer3).isEmpty()) {
                final CompoundTag id4 = new CompoundTag();
                id4.putByte("Slot", (byte)(integer3 + 100));
                this.armor.get(integer3).save(id4);
                ik.add(id4);
            }
        }
        for (int integer3 = 0; integer3 < this.offhand.size(); ++integer3) {
            if (!this.offhand.get(integer3).isEmpty()) {
                final CompoundTag id4 = new CompoundTag();
                id4.putByte("Slot", (byte)(integer3 + 150));
                this.offhand.get(integer3).save(id4);
                ik.add(id4);
            }
        }
        return ik;
    }
    
    public void load(final ListTag ik) {
        this.items.clear();
        this.armor.clear();
        this.offhand.clear();
        for (int integer3 = 0; integer3 < ik.size(); ++integer3) {
            final CompoundTag id4 = ik.getCompound(integer3);
            final int integer4 = id4.getByte("Slot") & 0xFF;
            final ItemStack bcj6 = ItemStack.of(id4);
            if (!bcj6.isEmpty()) {
                if (integer4 >= 0 && integer4 < this.items.size()) {
                    this.items.set(integer4, bcj6);
                }
                else if (integer4 >= 100 && integer4 < this.armor.size() + 100) {
                    this.armor.set(integer4 - 100, bcj6);
                }
                else if (integer4 >= 150 && integer4 < this.offhand.size() + 150) {
                    this.offhand.set(integer4 - 150, bcj6);
                }
            }
        }
    }
    
    public int getContainerSize() {
        return this.items.size() + this.armor.size() + this.offhand.size();
    }
    
    public boolean isEmpty() {
        for (final ItemStack bcj3 : this.items) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        for (final ItemStack bcj3 : this.armor) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        for (final ItemStack bcj3 : this.offhand) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public ItemStack getItem(int integer) {
        List<ItemStack> list3 = null;
        for (final NonNullList<ItemStack> fk5 : this.compartments) {
            if (integer < fk5.size()) {
                list3 = (List<ItemStack>)fk5;
                break;
            }
            integer -= fk5.size();
        }
        return (ItemStack)((list3 == null) ? ItemStack.EMPTY : list3.get(integer));
    }
    
    public Component getName() {
        return new TranslatableComponent("container.inventory", new Object[0]);
    }
    
    public boolean canDestroy(final BlockState bvt) {
        return this.getItem(this.selected).canDestroySpecial(bvt);
    }
    
    public ItemStack getArmor(final int integer) {
        return this.armor.get(integer);
    }
    
    public void hurtArmor(float float1) {
        if (float1 <= 0.0f) {
            return;
        }
        float1 /= 4.0f;
        if (float1 < 1.0f) {
            float1 = 1.0f;
        }
        for (int integer3 = 0; integer3 < this.armor.size(); ++integer3) {
            final ItemStack bcj4 = this.armor.get(integer3);
            if (bcj4.getItem() instanceof ArmorItem) {
                final int integer4 = integer3;
                bcj4.<Player>hurtAndBreak((int)float1, this.player, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, integer4))));
            }
        }
    }
    
    public void dropAll() {
        for (final List<ItemStack> list3 : this.compartments) {
            for (int integer4 = 0; integer4 < list3.size(); ++integer4) {
                final ItemStack bcj5 = (ItemStack)list3.get(integer4);
                if (!bcj5.isEmpty()) {
                    this.player.drop(bcj5, true, false);
                    list3.set(integer4, ItemStack.EMPTY);
                }
            }
        }
    }
    
    public void setChanged() {
        ++this.timesChanged;
    }
    
    public int getTimesChanged() {
        return this.timesChanged;
    }
    
    public void setCarried(final ItemStack bcj) {
        this.carried = bcj;
    }
    
    public ItemStack getCarried() {
        return this.carried;
    }
    
    public boolean stillValid(final Player awg) {
        return !this.player.removed && awg.distanceToSqr(this.player) <= 64.0;
    }
    
    public boolean contains(final ItemStack bcj) {
        for (final List<ItemStack> list4 : this.compartments) {
            for (final ItemStack bcj2 : list4) {
                if (!bcj2.isEmpty() && bcj2.sameItem(bcj)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean contains(final Tag<Item> zg) {
        for (final List<ItemStack> list4 : this.compartments) {
            for (final ItemStack bcj6 : list4) {
                if (!bcj6.isEmpty() && zg.contains(bcj6.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void replaceWith(final Inventory awf) {
        for (int integer3 = 0; integer3 < this.getContainerSize(); ++integer3) {
            this.setItem(integer3, awf.getItem(integer3));
        }
        this.selected = awf.selected;
    }
    
    public void clearContent() {
        for (final List<ItemStack> list3 : this.compartments) {
            list3.clear();
        }
    }
    
    public void fillStackedContents(final StackedContents awi) {
        for (final ItemStack bcj4 : this.items) {
            awi.accountSimpleStack(bcj4);
        }
    }
}
