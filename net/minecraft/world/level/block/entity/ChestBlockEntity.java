package net.minecraft.world.level.block.entity;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.sounds.SoundEvent;
import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.ContainerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import java.util.Iterator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

public class ChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity, TickableBlockEntity {
    private NonNullList<ItemStack> items;
    protected float openness;
    protected float oOpenness;
    protected int openCount;
    private int tickInterval;
    
    protected ChestBlockEntity(final BlockEntityType<?> btx) {
        super(btx);
        this.items = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
    }
    
    public ChestBlockEntity() {
        this(BlockEntityType.CHEST);
    }
    
    public int getContainerSize() {
        return 27;
    }
    
    public boolean isEmpty() {
        for (final ItemStack bcj3 : this.items) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.chest", new Object[0]);
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
    public void tick() {
        final int integer2 = this.worldPosition.getX();
        final int integer3 = this.worldPosition.getY();
        final int integer4 = this.worldPosition.getZ();
        ++this.tickInterval;
        this.openCount = getOpenCount(this.level, this, this.tickInterval, integer2, integer3, integer4, this.openCount);
        this.oOpenness = this.openness;
        final float float5 = 0.1f;
        if (this.openCount > 0 && this.openness == 0.0f) {
            this.playSound(SoundEvents.CHEST_OPEN);
        }
        if ((this.openCount == 0 && this.openness > 0.0f) || (this.openCount > 0 && this.openness < 1.0f)) {
            final float float6 = this.openness;
            if (this.openCount > 0) {
                this.openness += 0.1f;
            }
            else {
                this.openness -= 0.1f;
            }
            if (this.openness > 1.0f) {
                this.openness = 1.0f;
            }
            final float float7 = 0.5f;
            if (this.openness < 0.5f && float6 >= 0.5f) {
                this.playSound(SoundEvents.CHEST_CLOSE);
            }
            if (this.openness < 0.0f) {
                this.openness = 0.0f;
            }
        }
    }
    
    public static int getOpenCount(final Level bhr, final BaseContainerBlockEntity btr, final int integer3, final int integer4, final int integer5, final int integer6, int integer7) {
        if (!bhr.isClientSide && integer7 != 0 && (integer3 + integer4 + integer5 + integer6) % 200 == 0) {
            integer7 = getOpenCount(bhr, btr, integer4, integer5, integer6);
        }
        return integer7;
    }
    
    public static int getOpenCount(final Level bhr, final BaseContainerBlockEntity btr, final int integer3, final int integer4, final int integer5) {
        int integer6 = 0;
        final float float7 = 5.0f;
        final List<Player> list8 = bhr.<Player>getEntitiesOfClass((java.lang.Class<? extends Player>)Player.class, new AABB(integer3 - 5.0f, integer4 - 5.0f, integer5 - 5.0f, integer3 + 1 + 5.0f, integer4 + 1 + 5.0f, integer5 + 1 + 5.0f));
        for (final Player awg10 : list8) {
            if (awg10.containerMenu instanceof ChestMenu) {
                final Container ahc11 = ((ChestMenu)awg10.containerMenu).getContainer();
                if (ahc11 != btr && (!(ahc11 instanceof CompoundContainer) || !((CompoundContainer)ahc11).contains(btr))) {
                    continue;
                }
                ++integer6;
            }
        }
        return integer6;
    }
    
    private void playSound(final SoundEvent yo) {
        final ChestType bwm3 = this.getBlockState().<ChestType>getValue(ChestBlock.TYPE);
        if (bwm3 == ChestType.LEFT) {
            return;
        }
        double double4 = this.worldPosition.getX() + 0.5;
        final double double5 = this.worldPosition.getY() + 0.5;
        double double6 = this.worldPosition.getZ() + 0.5;
        if (bwm3 == ChestType.RIGHT) {
            final Direction fb10 = ChestBlock.getConnectedDirection(this.getBlockState());
            double4 += fb10.getStepX() * 0.5;
            double6 += fb10.getStepZ() * 0.5;
        }
        this.level.playSound(null, double4, double5, double6, yo, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
    }
    
    public boolean triggerEvent(final int integer1, final int integer2) {
        if (integer1 == 1) {
            this.openCount = integer2;
            return true;
        }
        return super.triggerEvent(integer1, integer2);
    }
    
    public void startOpen(final Player awg) {
        if (!awg.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++this.openCount;
            this.signalOpenCount();
        }
    }
    
    public void stopOpen(final Player awg) {
        if (!awg.isSpectator()) {
            --this.openCount;
            this.signalOpenCount();
        }
    }
    
    protected void signalOpenCount() {
        final Block bmv2 = this.getBlockState().getBlock();
        if (bmv2 instanceof ChestBlock) {
            this.level.blockEvent(this.worldPosition, bmv2, 1, this.openCount);
            this.level.updateNeighborsAt(this.worldPosition, bmv2);
        }
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
    public float getOpenNess(final float float1) {
        return Mth.lerp(float1, this.oOpenness, this.openness);
    }
    
    public static int getOpenCount(final BlockGetter bhb, final BlockPos ew) {
        final BlockState bvt3 = bhb.getBlockState(ew);
        if (bvt3.getBlock().isEntityBlock()) {
            final BlockEntity btw4 = bhb.getBlockEntity(ew);
            if (btw4 instanceof ChestBlockEntity) {
                return ((ChestBlockEntity)btw4).openCount;
            }
        }
        return 0;
    }
    
    public static void swapContents(final ChestBlockEntity bua1, final ChestBlockEntity bua2) {
        final NonNullList<ItemStack> fk3 = bua1.getItems();
        bua1.setItems(bua2.getItems());
        bua2.setItems(fk3);
    }
    
    @Override
    protected AbstractContainerMenu createMenu(final int integer, final Inventory awf) {
        return ChestMenu.threeRows(integer, awf, this);
    }
}
