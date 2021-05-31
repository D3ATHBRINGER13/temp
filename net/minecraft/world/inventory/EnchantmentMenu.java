package net.minecraft.world.inventory;

import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.function.BiConsumer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import java.util.Random;
import net.minecraft.world.Container;

public class EnchantmentMenu extends AbstractContainerMenu {
    private final Container enchantSlots;
    private final ContainerLevelAccess access;
    private final Random random;
    private final DataSlot enchantmentSeed;
    public final int[] costs;
    public final int[] enchantClue;
    public final int[] levelClue;
    
    public EnchantmentMenu(final int integer, final Inventory awf) {
        this(integer, awf, ContainerLevelAccess.NULL);
    }
    
    public EnchantmentMenu(final int integer, final Inventory awf, final ContainerLevelAccess ayu) {
        super(MenuType.ENCHANTMENT, integer);
        this.enchantSlots = new SimpleContainer(2) {
            @Override
            public void setChanged() {
                super.setChanged();
                EnchantmentMenu.this.slotsChanged(this);
            }
        };
        this.random = new Random();
        this.enchantmentSeed = DataSlot.standalone();
        this.costs = new int[3];
        this.enchantClue = new int[] { -1, -1, -1 };
        this.levelClue = new int[] { -1, -1, -1 };
        this.access = ayu;
        this.addSlot(new Slot(this.enchantSlots, 0, 15, 47) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return true;
            }
            
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.enchantSlots, 1, 35, 47) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return bcj.getItem() == Items.LAPIS_LAZULI;
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
        this.addDataSlot(DataSlot.shared(this.costs, 0));
        this.addDataSlot(DataSlot.shared(this.costs, 1));
        this.addDataSlot(DataSlot.shared(this.costs, 2));
        this.addDataSlot(this.enchantmentSeed).set(awf.player.getEnchantmentSeed());
        this.addDataSlot(DataSlot.shared(this.enchantClue, 0));
        this.addDataSlot(DataSlot.shared(this.enchantClue, 1));
        this.addDataSlot(DataSlot.shared(this.enchantClue, 2));
        this.addDataSlot(DataSlot.shared(this.levelClue, 0));
        this.addDataSlot(DataSlot.shared(this.levelClue, 1));
        this.addDataSlot(DataSlot.shared(this.levelClue, 2));
    }
    
    @Override
    public void slotsChanged(final Container ahc) {
        if (ahc == this.enchantSlots) {
            final ItemStack bcj3 = ahc.getItem(0);
            if (bcj3.isEmpty() || !bcj3.isEnchantable()) {
                for (int integer4 = 0; integer4 < 3; ++integer4) {
                    this.costs[integer4] = 0;
                    this.enchantClue[integer4] = -1;
                    this.levelClue[integer4] = -1;
                }
            }
            else {
                this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> {
                    int integer5 = 0;
                    for (int integer6 = -1; integer6 <= 1; ++integer6) {
                        for (int integer7 = -1; integer7 <= 1; ++integer7) {
                            if (integer6 != 0 || integer7 != 0) {
                                if (bhr.isEmptyBlock(ew.offset(integer7, 0, integer6)) && bhr.isEmptyBlock(ew.offset(integer7, 1, integer6))) {
                                    if (bhr.getBlockState(ew.offset(integer7 * 2, 0, integer6 * 2)).getBlock() == Blocks.BOOKSHELF) {
                                        ++integer5;
                                    }
                                    if (bhr.getBlockState(ew.offset(integer7 * 2, 1, integer6 * 2)).getBlock() == Blocks.BOOKSHELF) {
                                        ++integer5;
                                    }
                                    if (integer7 != 0 && integer6 != 0) {
                                        if (bhr.getBlockState(ew.offset(integer7 * 2, 0, integer6)).getBlock() == Blocks.BOOKSHELF) {
                                            ++integer5;
                                        }
                                        if (bhr.getBlockState(ew.offset(integer7 * 2, 1, integer6)).getBlock() == Blocks.BOOKSHELF) {
                                            ++integer5;
                                        }
                                        if (bhr.getBlockState(ew.offset(integer7, 0, integer6 * 2)).getBlock() == Blocks.BOOKSHELF) {
                                            ++integer5;
                                        }
                                        if (bhr.getBlockState(ew.offset(integer7, 1, integer6 * 2)).getBlock() == Blocks.BOOKSHELF) {
                                            ++integer5;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.random.setSeed((long)this.enchantmentSeed.get());
                    for (int integer6 = 0; integer6 < 3; ++integer6) {
                        this.costs[integer6] = EnchantmentHelper.getEnchantmentCost(this.random, integer6, integer5, bcj3);
                        this.enchantClue[integer6] = -1;
                        this.levelClue[integer6] = -1;
                        if (this.costs[integer6] < integer6 + 1) {
                            this.costs[integer6] = 0;
                        }
                    }
                    for (int integer6 = 0; integer6 < 3; ++integer6) {
                        if (this.costs[integer6] > 0) {
                            final List<EnchantmentInstance> list7 = this.getEnchantmentList(bcj3, integer6, this.costs[integer6]);
                            if (list7 != null && !list7.isEmpty()) {
                                final EnchantmentInstance bfv8 = (EnchantmentInstance)list7.get(this.random.nextInt(list7.size()));
                                this.enchantClue[integer6] = Registry.ENCHANTMENT.getId(bfv8.enchantment);
                                this.levelClue[integer6] = bfv8.level;
                            }
                        }
                    }
                    this.broadcastChanges();
                }));
            }
        }
    }
    
    @Override
    public boolean clickMenuButton(final Player awg, final int integer) {
        final ItemStack bcj4 = this.enchantSlots.getItem(0);
        final ItemStack bcj5 = this.enchantSlots.getItem(1);
        final int integer2 = integer + 1;
        if ((bcj5.isEmpty() || bcj5.getCount() < integer2) && !awg.abilities.instabuild) {
            return false;
        }
        if (this.costs[integer] > 0 && !bcj4.isEmpty() && ((awg.experienceLevel >= integer2 && awg.experienceLevel >= this.costs[integer]) || awg.abilities.instabuild)) {
            this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> {
                ItemStack bcj6 = bcj4;
                final List<EnchantmentInstance> list10 = this.getEnchantmentList(bcj6, integer, this.costs[integer]);
                if (!list10.isEmpty()) {
                    awg.onEnchantmentPerformed(bcj6, integer2);
                    final boolean boolean11 = bcj6.getItem() == Items.BOOK;
                    if (boolean11) {
                        bcj6 = new ItemStack(Items.ENCHANTED_BOOK);
                        this.enchantSlots.setItem(0, bcj6);
                    }
                    for (int integer5 = 0; integer5 < list10.size(); ++integer5) {
                        final EnchantmentInstance bfv13 = (EnchantmentInstance)list10.get(integer5);
                        if (boolean11) {
                            EnchantedBookItem.addEnchantment(bcj6, bfv13);
                        }
                        else {
                            bcj6.enchant(bfv13.enchantment, bfv13.level);
                        }
                    }
                    if (!awg.abilities.instabuild) {
                        bcj5.shrink(integer2);
                        if (bcj5.isEmpty()) {
                            this.enchantSlots.setItem(1, ItemStack.EMPTY);
                        }
                    }
                    awg.awardStat(Stats.ENCHANT_ITEM);
                    if (awg instanceof ServerPlayer) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)awg, bcj6, integer2);
                    }
                    this.enchantSlots.setChanged();
                    this.enchantmentSeed.set(awg.getEnchantmentSeed());
                    this.slotsChanged(this.enchantSlots);
                    bhr.playSound(null, ew, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, bhr.random.nextFloat() * 0.1f + 0.9f);
                }
            }));
            return true;
        }
        return false;
    }
    
    private List<EnchantmentInstance> getEnchantmentList(final ItemStack bcj, final int integer2, final int integer3) {
        this.random.setSeed((long)(this.enchantmentSeed.get() + integer2));
        final List<EnchantmentInstance> list5 = EnchantmentHelper.selectEnchantment(this.random, bcj, integer3, false);
        if (bcj.getItem() == Items.BOOK && list5.size() > 1) {
            list5.remove(this.random.nextInt(list5.size()));
        }
        return list5;
    }
    
    public int getGoldCount() {
        final ItemStack bcj2 = this.enchantSlots.getItem(1);
        if (bcj2.isEmpty()) {
            return 0;
        }
        return bcj2.getCount();
    }
    
    public int getEnchantmentSeed() {
        return this.enchantmentSeed.get();
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> this.clearContainer(awg, awg.level, this.enchantSlots)));
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return AbstractContainerMenu.stillValid(this.access, awg, Blocks.ENCHANTING_TABLE);
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if (integer == 0) {
                if (!this.moveItemStackTo(bcj5, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer == 1) {
                if (!this.moveItemStackTo(bcj5, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (bcj5.getItem() == Items.LAPIS_LAZULI) {
                if (!this.moveItemStackTo(bcj5, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (((Slot)this.slots.get(0)).hasItem() || !((Slot)this.slots.get(0)).mayPlace(bcj5)) {
                    return ItemStack.EMPTY;
                }
                if (bcj5.hasTag() && bcj5.getCount() == 1) {
                    ((Slot)this.slots.get(0)).set(bcj5.copy());
                    bcj5.setCount(0);
                }
                else if (!bcj5.isEmpty()) {
                    ((Slot)this.slots.get(0)).set(new ItemStack(bcj5.getItem()));
                    bcj5.shrink(1);
                }
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
}
