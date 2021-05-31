package net.minecraft.world.inventory;

import net.minecraft.world.item.DyeColor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;

public class LoomMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final DataSlot selectedBannerPatternIndex;
    private Runnable slotUpdateListener;
    private final Slot bannerSlot;
    private final Slot dyeSlot;
    private final Slot patternSlot;
    private final Slot resultSlot;
    private final Container inputContainer;
    private final Container outputContainer;
    
    public LoomMenu(final int integer, final Inventory awf) {
        this(integer, awf, ContainerLevelAccess.NULL);
    }
    
    public LoomMenu(final int integer, final Inventory awf, final ContainerLevelAccess ayu) {
        super(MenuType.LOOM, integer);
        this.selectedBannerPatternIndex = DataSlot.standalone();
        this.slotUpdateListener = (() -> {});
        this.inputContainer = new SimpleContainer(3) {
            @Override
            public void setChanged() {
                super.setChanged();
                LoomMenu.this.slotsChanged(this);
                LoomMenu.this.slotUpdateListener.run();
            }
        };
        this.outputContainer = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                LoomMenu.this.slotUpdateListener.run();
            }
        };
        this.access = ayu;
        this.bannerSlot = this.addSlot(new Slot(this.inputContainer, 0, 13, 26) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return bcj.getItem() instanceof BannerItem;
            }
        });
        this.dyeSlot = this.addSlot(new Slot(this.inputContainer, 1, 33, 26) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return bcj.getItem() instanceof DyeItem;
            }
        });
        this.patternSlot = this.addSlot(new Slot(this.inputContainer, 2, 23, 45) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return bcj.getItem() instanceof BannerPatternItem;
            }
        });
        this.resultSlot = this.addSlot(new Slot(this.outputContainer, 0, 143, 58) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return false;
            }
            
            @Override
            public ItemStack onTake(final Player awg, final ItemStack bcj) {
                LoomMenu.this.bannerSlot.remove(1);
                LoomMenu.this.dyeSlot.remove(1);
                if (!LoomMenu.this.bannerSlot.hasItem() || !LoomMenu.this.dyeSlot.hasItem()) {
                    LoomMenu.this.selectedBannerPatternIndex.set(0);
                }
                ayu.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> bhr.playSound(null, ew, SoundEvents.UI_LOOM_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f)));
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
        this.addDataSlot(this.selectedBannerPatternIndex);
    }
    
    public int getSelectedBannerPatternIndex() {
        return this.selectedBannerPatternIndex.get();
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return AbstractContainerMenu.stillValid(this.access, awg, Blocks.LOOM);
    }
    
    @Override
    public boolean clickMenuButton(final Player awg, final int integer) {
        if (integer > 0 && integer <= BannerPattern.AVAILABLE_PATTERNS) {
            this.selectedBannerPatternIndex.set(integer);
            this.setupResultSlot();
            return true;
        }
        return false;
    }
    
    @Override
    public void slotsChanged(final Container ahc) {
        final ItemStack bcj3 = this.bannerSlot.getItem();
        final ItemStack bcj4 = this.dyeSlot.getItem();
        final ItemStack bcj5 = this.patternSlot.getItem();
        final ItemStack bcj6 = this.resultSlot.getItem();
        if (!bcj6.isEmpty() && (bcj3.isEmpty() || bcj4.isEmpty() || this.selectedBannerPatternIndex.get() <= 0 || (this.selectedBannerPatternIndex.get() >= BannerPattern.COUNT - 5 && bcj5.isEmpty()))) {
            this.resultSlot.set(ItemStack.EMPTY);
            this.selectedBannerPatternIndex.set(0);
        }
        else if (!bcj5.isEmpty() && bcj5.getItem() instanceof BannerPatternItem) {
            final CompoundTag id7 = bcj3.getOrCreateTagElement("BlockEntityTag");
            final boolean boolean8 = id7.contains("Patterns", 9) && !bcj3.isEmpty() && id7.getList("Patterns", 10).size() >= 6;
            if (boolean8) {
                this.selectedBannerPatternIndex.set(0);
            }
            else {
                this.selectedBannerPatternIndex.set(((BannerPatternItem)bcj5.getItem()).getBannerPattern().ordinal());
            }
        }
        this.setupResultSlot();
        this.broadcastChanges();
    }
    
    public void registerUpdateListener(final Runnable runnable) {
        this.slotUpdateListener = runnable;
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if (integer == this.resultSlot.index) {
                if (!this.moveItemStackTo(bcj5, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                azx5.onQuickCraft(bcj5, bcj4);
            }
            else if (integer == this.dyeSlot.index || integer == this.bannerSlot.index || integer == this.patternSlot.index) {
                if (!this.moveItemStackTo(bcj5, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (bcj5.getItem() instanceof BannerItem) {
                if (!this.moveItemStackTo(bcj5, this.bannerSlot.index, this.bannerSlot.index + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (bcj5.getItem() instanceof DyeItem) {
                if (!this.moveItemStackTo(bcj5, this.dyeSlot.index, this.dyeSlot.index + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (bcj5.getItem() instanceof BannerPatternItem) {
                if (!this.moveItemStackTo(bcj5, this.patternSlot.index, this.patternSlot.index + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 4 && integer < 31) {
                if (!this.moveItemStackTo(bcj5, 31, 40, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 31 && integer < 40 && !this.moveItemStackTo(bcj5, 4, 31, false)) {
                return ItemStack.EMPTY;
            }
            if (bcj5.isEmpty()) {
                azx5.set(ItemStack.EMPTY);
            }
            else {
                azx5.setChanged();
            }
            if (bcj5.getCount() == bcj4.getCount()) {
                return ItemStack.EMPTY;
            }
            azx5.onTake(awg, bcj5);
        }
        return bcj4;
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> this.clearContainer(awg, awg.level, this.inputContainer)));
    }
    
    private void setupResultSlot() {
        if (this.selectedBannerPatternIndex.get() > 0) {
            final ItemStack bcj2 = this.bannerSlot.getItem();
            final ItemStack bcj3 = this.dyeSlot.getItem();
            ItemStack bcj4 = ItemStack.EMPTY;
            if (!bcj2.isEmpty() && !bcj3.isEmpty()) {
                bcj4 = bcj2.copy();
                bcj4.setCount(1);
                final BannerPattern btp5 = BannerPattern.values()[this.selectedBannerPatternIndex.get()];
                final DyeColor bbg6 = ((DyeItem)bcj3.getItem()).getDyeColor();
                final CompoundTag id7 = bcj4.getOrCreateTagElement("BlockEntityTag");
                ListTag ik8;
                if (id7.contains("Patterns", 9)) {
                    ik8 = id7.getList("Patterns", 10);
                }
                else {
                    ik8 = new ListTag();
                    id7.put("Patterns", (Tag)ik8);
                }
                final CompoundTag id8 = new CompoundTag();
                id8.putString("Pattern", btp5.getHashname());
                id8.putInt("Color", bbg6.getId());
                ik8.add(id8);
            }
            if (!ItemStack.matches(bcj4, this.resultSlot.getItem())) {
                this.resultSlot.set(bcj4);
            }
        }
    }
    
    public Slot getBannerSlot() {
        return this.bannerSlot;
    }
    
    public Slot getDyeSlot() {
        return this.dyeSlot;
    }
    
    public Slot getPatternSlot() {
        return this.patternSlot;
    }
    
    public Slot getResultSlot() {
        return this.resultSlot;
    }
}
