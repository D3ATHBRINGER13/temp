package net.minecraft.world.level.block.entity;

import java.util.stream.Stream;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.WorldlyContainer;
import java.util.stream.IntStream;
import net.minecraft.world.Container;
import net.minecraft.core.Direction;
import java.util.Iterator;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.HopperBlock;
import java.util.function.Supplier;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.ContainerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

public class HopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper, TickableBlockEntity {
    private NonNullList<ItemStack> items;
    private int cooldownTime;
    private long tickedGameTime;
    
    public HopperBlockEntity() {
        super(BlockEntityType.HOPPER);
        this.items = NonNullList.<ItemStack>withSize(5, ItemStack.EMPTY);
        this.cooldownTime = -1;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(id)) {
            ContainerHelper.loadAllItems(id, this.items);
        }
        this.cooldownTime = id.getInt("TransferCooldown");
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        if (!this.trySaveLootTable(id)) {
            ContainerHelper.saveAllItems(id, this.items);
        }
        id.putInt("TransferCooldown", this.cooldownTime);
        return id;
    }
    
    public int getContainerSize() {
        return this.items.size();
    }
    
    @Override
    public ItemStack removeItem(final int integer1, final int integer2) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem((List<ItemStack>)this.getItems(), integer1, integer2);
    }
    
    @Override
    public void setItem(final int integer, final ItemStack bcj) {
        this.unpackLootTable(null);
        this.getItems().set(integer, bcj);
        if (bcj.getCount() > this.getMaxStackSize()) {
            bcj.setCount(this.getMaxStackSize());
        }
    }
    
    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.hopper", new Object[0]);
    }
    
    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        --this.cooldownTime;
        this.tickedGameTime = this.level.getGameTime();
        if (!this.isOnCooldown()) {
            this.setCooldown(0);
            this.tryMoveItems((Supplier<Boolean>)(() -> suckInItems(this)));
        }
    }
    
    private boolean tryMoveItems(final Supplier<Boolean> supplier) {
        if (this.level == null || this.level.isClientSide) {
            return false;
        }
        if (!this.isOnCooldown() && this.getBlockState().<Boolean>getValue((Property<Boolean>)HopperBlock.ENABLED)) {
            boolean boolean3 = false;
            if (!this.inventoryEmpty()) {
                boolean3 = this.ejectItems();
            }
            if (!this.inventoryFull()) {
                boolean3 |= (boolean)supplier.get();
            }
            if (boolean3) {
                this.setCooldown(8);
                this.setChanged();
                return true;
            }
        }
        return false;
    }
    
    private boolean inventoryEmpty() {
        for (final ItemStack bcj3 : this.items) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isEmpty() {
        return this.inventoryEmpty();
    }
    
    private boolean inventoryFull() {
        for (final ItemStack bcj3 : this.items) {
            if (bcj3.isEmpty() || bcj3.getCount() != bcj3.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }
    
    private boolean ejectItems() {
        final Container ahc2 = this.getAttachedContainer();
        if (ahc2 == null) {
            return false;
        }
        final Direction fb3 = this.getBlockState().<Direction>getValue((Property<Direction>)HopperBlock.FACING).getOpposite();
        if (this.isFullContainer(ahc2, fb3)) {
            return false;
        }
        for (int integer4 = 0; integer4 < this.getContainerSize(); ++integer4) {
            if (!this.getItem(integer4).isEmpty()) {
                final ItemStack bcj5 = this.getItem(integer4).copy();
                final ItemStack bcj6 = addItem(this, ahc2, this.removeItem(integer4, 1), fb3);
                if (bcj6.isEmpty()) {
                    ahc2.setChanged();
                    return true;
                }
                this.setItem(integer4, bcj5);
            }
        }
        return false;
    }
    
    private static IntStream getSlots(final Container ahc, final Direction fb) {
        if (ahc instanceof WorldlyContainer) {
            return IntStream.of(((WorldlyContainer)ahc).getSlotsForFace(fb));
        }
        return IntStream.range(0, ahc.getContainerSize());
    }
    
    private boolean isFullContainer(final Container ahc, final Direction fb) {
        return getSlots(ahc, fb).allMatch(integer -> {
            final ItemStack bcj3 = ahc.getItem(integer);
            return bcj3.getCount() >= bcj3.getMaxStackSize();
        });
    }
    
    private static boolean isEmptyContainer(final Container ahc, final Direction fb) {
        return getSlots(ahc, fb).allMatch(integer -> ahc.getItem(integer).isEmpty());
    }
    
    public static boolean suckInItems(final Hopper buk) {
        final Container ahc2 = getSourceContainer(buk);
        if (ahc2 != null) {
            final Direction fb3 = Direction.DOWN;
            return !isEmptyContainer(ahc2, fb3) && getSlots(ahc2, fb3).anyMatch(integer -> tryTakeInItemFromSlot(buk, ahc2, integer, fb3));
        }
        for (final ItemEntity atx4 : getItemsAtAndAbove(buk)) {
            if (addItem(buk, atx4)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean tryTakeInItemFromSlot(final Hopper buk, final Container ahc, final int integer, final Direction fb) {
        final ItemStack bcj5 = ahc.getItem(integer);
        if (!bcj5.isEmpty() && canTakeItemFromContainer(ahc, bcj5, integer, fb)) {
            final ItemStack bcj6 = bcj5.copy();
            final ItemStack bcj7 = addItem(ahc, buk, ahc.removeItem(integer, 1), null);
            if (bcj7.isEmpty()) {
                ahc.setChanged();
                return true;
            }
            ahc.setItem(integer, bcj6);
        }
        return false;
    }
    
    public static boolean addItem(final Container ahc, final ItemEntity atx) {
        boolean boolean3 = false;
        final ItemStack bcj4 = atx.getItem().copy();
        final ItemStack bcj5 = addItem(null, ahc, bcj4, null);
        if (bcj5.isEmpty()) {
            boolean3 = true;
            atx.remove();
        }
        else {
            atx.setItem(bcj5);
        }
        return boolean3;
    }
    
    public static ItemStack addItem(@Nullable final Container ahc1, final Container ahc2, ItemStack bcj, @Nullable final Direction fb) {
        if (ahc2 instanceof WorldlyContainer && fb != null) {
            final WorldlyContainer ahs5 = (WorldlyContainer)ahc2;
            final int[] arr6 = ahs5.getSlotsForFace(fb);
            for (int integer7 = 0; integer7 < arr6.length && !bcj.isEmpty(); bcj = tryMoveInItem(ahc1, ahc2, bcj, arr6[integer7], fb), ++integer7) {}
        }
        else {
            for (int integer8 = ahc2.getContainerSize(), integer9 = 0; integer9 < integer8 && !bcj.isEmpty(); bcj = tryMoveInItem(ahc1, ahc2, bcj, integer9, fb), ++integer9) {}
        }
        return bcj;
    }
    
    private static boolean canPlaceItemInContainer(final Container ahc, final ItemStack bcj, final int integer, @Nullable final Direction fb) {
        return ahc.canPlaceItem(integer, bcj) && (!(ahc instanceof WorldlyContainer) || ((WorldlyContainer)ahc).canPlaceItemThroughFace(integer, bcj, fb));
    }
    
    private static boolean canTakeItemFromContainer(final Container ahc, final ItemStack bcj, final int integer, final Direction fb) {
        return !(ahc instanceof WorldlyContainer) || ((WorldlyContainer)ahc).canTakeItemThroughFace(integer, bcj, fb);
    }
    
    private static ItemStack tryMoveInItem(@Nullable final Container ahc1, final Container ahc2, ItemStack bcj, final int integer, @Nullable final Direction fb) {
        final ItemStack bcj2 = ahc2.getItem(integer);
        if (canPlaceItemInContainer(ahc2, bcj, integer, fb)) {
            boolean boolean7 = false;
            final boolean boolean8 = ahc2.isEmpty();
            if (bcj2.isEmpty()) {
                ahc2.setItem(integer, bcj);
                bcj = ItemStack.EMPTY;
                boolean7 = true;
            }
            else if (canMergeItems(bcj2, bcj)) {
                final int integer2 = bcj.getMaxStackSize() - bcj2.getCount();
                final int integer3 = Math.min(bcj.getCount(), integer2);
                bcj.shrink(integer3);
                bcj2.grow(integer3);
                boolean7 = (integer3 > 0);
            }
            if (boolean7) {
                if (boolean8 && ahc2 instanceof HopperBlockEntity) {
                    final HopperBlockEntity bul9 = (HopperBlockEntity)ahc2;
                    if (!bul9.isOnCustomCooldown()) {
                        int integer3 = 0;
                        if (ahc1 instanceof HopperBlockEntity) {
                            final HopperBlockEntity bul10 = (HopperBlockEntity)ahc1;
                            if (bul9.tickedGameTime >= bul10.tickedGameTime) {
                                integer3 = 1;
                            }
                        }
                        bul9.setCooldown(8 - integer3);
                    }
                }
                ahc2.setChanged();
            }
        }
        return bcj;
    }
    
    @Nullable
    private Container getAttachedContainer() {
        final Direction fb2 = this.getBlockState().<Direction>getValue((Property<Direction>)HopperBlock.FACING);
        return getContainerAt(this.getLevel(), this.worldPosition.relative(fb2));
    }
    
    @Nullable
    public static Container getSourceContainer(final Hopper buk) {
        return getContainerAt(buk.getLevel(), buk.getLevelX(), buk.getLevelY() + 1.0, buk.getLevelZ());
    }
    
    public static List<ItemEntity> getItemsAtAndAbove(final Hopper buk) {
        return (List<ItemEntity>)buk.getSuckShape().toAabbs().stream().flatMap(csc -> buk.getLevel().<Entity>getEntitiesOfClass((java.lang.Class<? extends Entity>)ItemEntity.class, csc.move(buk.getLevelX() - 0.5, buk.getLevelY() - 0.5, buk.getLevelZ() - 0.5), (java.util.function.Predicate<? super Entity>)EntitySelector.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
    }
    
    @Nullable
    public static Container getContainerAt(final Level bhr, final BlockPos ew) {
        return getContainerAt(bhr, ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5);
    }
    
    @Nullable
    public static Container getContainerAt(final Level bhr, final double double2, final double double3, final double double4) {
        Container ahc8 = null;
        final BlockPos ew9 = new BlockPos(double2, double3, double4);
        final BlockState bvt10 = bhr.getBlockState(ew9);
        final Block bmv11 = bvt10.getBlock();
        if (bmv11 instanceof WorldlyContainerHolder) {
            ahc8 = ((WorldlyContainerHolder)bmv11).getContainer(bvt10, bhr, ew9);
        }
        else if (bmv11.isEntityBlock()) {
            final BlockEntity btw12 = bhr.getBlockEntity(ew9);
            if (btw12 instanceof Container) {
                ahc8 = (Container)btw12;
                if (ahc8 instanceof ChestBlockEntity && bmv11 instanceof ChestBlock) {
                    ahc8 = ChestBlock.getContainer(bvt10, bhr, ew9, true);
                }
            }
        }
        if (ahc8 == null) {
            final List<Entity> list12 = bhr.getEntities((Entity)null, new AABB(double2 - 0.5, double3 - 0.5, double4 - 0.5, double2 + 0.5, double3 + 0.5, double4 + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR);
            if (!list12.isEmpty()) {
                ahc8 = (Container)list12.get(bhr.random.nextInt(list12.size()));
            }
        }
        return ahc8;
    }
    
    private static boolean canMergeItems(final ItemStack bcj1, final ItemStack bcj2) {
        return bcj1.getItem() == bcj2.getItem() && bcj1.getDamageValue() == bcj2.getDamageValue() && bcj1.getCount() <= bcj1.getMaxStackSize() && ItemStack.tagMatches(bcj1, bcj2);
    }
    
    @Override
    public double getLevelX() {
        return this.worldPosition.getX() + 0.5;
    }
    
    @Override
    public double getLevelY() {
        return this.worldPosition.getY() + 0.5;
    }
    
    @Override
    public double getLevelZ() {
        return this.worldPosition.getZ() + 0.5;
    }
    
    private void setCooldown(final int integer) {
        this.cooldownTime = integer;
    }
    
    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }
    
    private boolean isOnCustomCooldown() {
        return this.cooldownTime > 8;
    }
    
    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }
    
    @Override
    protected void setItems(final NonNullList<ItemStack> fk) {
        this.items = fk;
    }
    
    public void entityInside(final Entity aio) {
        if (aio instanceof ItemEntity) {
            final BlockPos ew3 = this.getBlockPos();
            if (Shapes.joinIsNotEmpty(Shapes.create(aio.getBoundingBox().move(-ew3.getX(), -ew3.getY(), -ew3.getZ())), this.getSuckShape(), BooleanOp.AND)) {
                this.tryMoveItems((Supplier<Boolean>)(() -> addItem(this, (ItemEntity)aio)));
            }
        }
    }
    
    @Override
    protected AbstractContainerMenu createMenu(final int integer, final Inventory awf) {
        return new HopperMenu(integer, awf, this);
    }
}
