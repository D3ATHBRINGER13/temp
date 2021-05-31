package net.minecraft.world.inventory;

import org.apache.logging.log4j.LogManager;
import java.util.function.BiFunction;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.function.BiConsumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import org.apache.logging.log4j.Logger;

public class AnvilMenu extends AbstractContainerMenu {
    private static final Logger LOGGER;
    private final Container resultSlots;
    private final Container repairSlots;
    private final DataSlot cost;
    private final ContainerLevelAccess access;
    private int repairItemCountCost;
    private String itemName;
    private final Player player;
    
    public AnvilMenu(final int integer, final Inventory awf) {
        this(integer, awf, ContainerLevelAccess.NULL);
    }
    
    public AnvilMenu(final int integer, final Inventory awf, final ContainerLevelAccess ayu) {
        super(MenuType.ANVIL, integer);
        this.resultSlots = new ResultContainer();
        this.repairSlots = new SimpleContainer(2) {
            @Override
            public void setChanged() {
                super.setChanged();
                AnvilMenu.this.slotsChanged(this);
            }
        };
        this.cost = DataSlot.standalone();
        this.access = ayu;
        this.player = awf.player;
        this.addDataSlot(this.cost);
        this.addSlot(new Slot(this.repairSlots, 0, 27, 47));
        this.addSlot(new Slot(this.repairSlots, 1, 76, 47));
        this.addSlot(new Slot(this.resultSlots, 2, 134, 47) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return false;
            }
            
            @Override
            public boolean mayPickup(final Player awg) {
                return (awg.abilities.instabuild || awg.experienceLevel >= AnvilMenu.this.cost.get()) && AnvilMenu.this.cost.get() > 0 && this.hasItem();
            }
            
            @Override
            public ItemStack onTake(final Player awg, final ItemStack bcj) {
                if (!awg.abilities.instabuild) {
                    awg.giveExperienceLevels(-AnvilMenu.this.cost.get());
                }
                AnvilMenu.this.repairSlots.setItem(0, ItemStack.EMPTY);
                if (AnvilMenu.this.repairItemCountCost > 0) {
                    final ItemStack bcj2 = AnvilMenu.this.repairSlots.getItem(1);
                    if (!bcj2.isEmpty() && bcj2.getCount() > AnvilMenu.this.repairItemCountCost) {
                        bcj2.shrink(AnvilMenu.this.repairItemCountCost);
                        AnvilMenu.this.repairSlots.setItem(1, bcj2);
                    }
                    else {
                        AnvilMenu.this.repairSlots.setItem(1, ItemStack.EMPTY);
                    }
                }
                else {
                    AnvilMenu.this.repairSlots.setItem(1, ItemStack.EMPTY);
                }
                AnvilMenu.this.cost.set(0);
                ayu.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> {
                    final BlockState bvt4 = bhr.getBlockState(ew);
                    if (!awg.abilities.instabuild && bvt4.is(BlockTags.ANVIL) && awg.getRandom().nextFloat() < 0.12f) {
                        final BlockState bvt5 = AnvilBlock.damage(bvt4);
                        if (bvt5 == null) {
                            bhr.removeBlock(ew, false);
                            bhr.levelEvent(1029, ew, 0);
                        }
                        else {
                            bhr.setBlock(ew, bvt5, 2);
                            bhr.levelEvent(1030, ew, 0);
                        }
                    }
                    else {
                        bhr.levelEvent(1030, ew, 0);
                    }
                }));
                return bcj;
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
    public void slotsChanged(final Container ahc) {
        super.slotsChanged(ahc);
        if (ahc == this.repairSlots) {
            this.createResult();
        }
    }
    
    public void createResult() {
        final ItemStack bcj2 = this.repairSlots.getItem(0);
        this.cost.set(1);
        int integer3 = 0;
        int integer4 = 0;
        int integer5 = 0;
        if (bcj2.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
            return;
        }
        ItemStack bcj3 = bcj2.copy();
        final ItemStack bcj4 = this.repairSlots.getItem(1);
        final Map<Enchantment, Integer> map8 = EnchantmentHelper.getEnchantments(bcj3);
        integer4 += bcj2.getBaseRepairCost() + (bcj4.isEmpty() ? 0 : bcj4.getBaseRepairCost());
        this.repairItemCountCost = 0;
        if (!bcj4.isEmpty()) {
            final boolean boolean9 = bcj4.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(bcj4).isEmpty();
            if (bcj3.isDamageableItem() && bcj3.getItem().isValidRepairItem(bcj2, bcj4)) {
                int integer6 = Math.min(bcj3.getDamageValue(), bcj3.getMaxDamage() / 4);
                if (integer6 <= 0) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }
                int integer7;
                for (integer7 = 0; integer6 > 0 && integer7 < bcj4.getCount(); integer6 = Math.min(bcj3.getDamageValue(), bcj3.getMaxDamage() / 4), ++integer7) {
                    final int integer8 = bcj3.getDamageValue() - integer6;
                    bcj3.setDamageValue(integer8);
                    ++integer3;
                }
                this.repairItemCountCost = integer7;
            }
            else {
                if (!boolean9 && (bcj3.getItem() != bcj4.getItem() || !bcj3.isDamageableItem())) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }
                if (bcj3.isDamageableItem() && !boolean9) {
                    final int integer6 = bcj2.getMaxDamage() - bcj2.getDamageValue();
                    final int integer7 = bcj4.getMaxDamage() - bcj4.getDamageValue();
                    final int integer8 = integer7 + bcj3.getMaxDamage() * 12 / 100;
                    final int integer9 = integer6 + integer8;
                    int integer10 = bcj3.getMaxDamage() - integer9;
                    if (integer10 < 0) {
                        integer10 = 0;
                    }
                    if (integer10 < bcj3.getDamageValue()) {
                        bcj3.setDamageValue(integer10);
                        integer3 += 2;
                    }
                }
                final Map<Enchantment, Integer> map9 = EnchantmentHelper.getEnchantments(bcj4);
                boolean boolean10 = false;
                boolean boolean11 = false;
                for (final Enchantment bfs14 : map9.keySet()) {
                    if (bfs14 == null) {
                        continue;
                    }
                    final int integer11 = (int)(map8.containsKey(bfs14) ? map8.get(bfs14) : 0);
                    int integer12 = (int)map9.get(bfs14);
                    integer12 = ((integer11 == integer12) ? (integer12 + 1) : Math.max(integer12, integer11));
                    boolean boolean12 = bfs14.canEnchant(bcj2);
                    if (this.player.abilities.instabuild || bcj2.getItem() == Items.ENCHANTED_BOOK) {
                        boolean12 = true;
                    }
                    for (final Enchantment bfs15 : map8.keySet()) {
                        if (bfs15 != bfs14 && !bfs14.isCompatibleWith(bfs15)) {
                            boolean12 = false;
                            ++integer3;
                        }
                    }
                    if (!boolean12) {
                        boolean11 = true;
                    }
                    else {
                        boolean10 = true;
                        if (integer12 > bfs14.getMaxLevel()) {
                            integer12 = bfs14.getMaxLevel();
                        }
                        map8.put(bfs14, integer12);
                        int integer13 = 0;
                        switch (bfs14.getRarity()) {
                            case COMMON: {
                                integer13 = 1;
                                break;
                            }
                            case UNCOMMON: {
                                integer13 = 2;
                                break;
                            }
                            case RARE: {
                                integer13 = 4;
                                break;
                            }
                            case VERY_RARE: {
                                integer13 = 8;
                                break;
                            }
                        }
                        if (boolean9) {
                            integer13 = Math.max(1, integer13 / 2);
                        }
                        integer3 += integer13 * integer12;
                        if (bcj2.getCount() <= 1) {
                            continue;
                        }
                        integer3 = 40;
                    }
                }
                if (boolean11 && !boolean10) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }
            }
        }
        if (StringUtils.isBlank((CharSequence)this.itemName)) {
            if (bcj2.hasCustomHoverName()) {
                integer5 = 1;
                integer3 += integer5;
                bcj3.resetHoverName();
            }
        }
        else if (!this.itemName.equals(bcj2.getHoverName().getString())) {
            integer5 = 1;
            integer3 += integer5;
            bcj3.setHoverName(new TextComponent(this.itemName));
        }
        this.cost.set(integer4 + integer3);
        if (integer3 <= 0) {
            bcj3 = ItemStack.EMPTY;
        }
        if (integer5 == integer3 && integer5 > 0 && this.cost.get() >= 40) {
            this.cost.set(39);
        }
        if (this.cost.get() >= 40 && !this.player.abilities.instabuild) {
            bcj3 = ItemStack.EMPTY;
        }
        if (!bcj3.isEmpty()) {
            int integer14 = bcj3.getBaseRepairCost();
            if (!bcj4.isEmpty() && integer14 < bcj4.getBaseRepairCost()) {
                integer14 = bcj4.getBaseRepairCost();
            }
            if (integer5 != integer3 || integer5 == 0) {
                integer14 = calculateIncreasedRepairCost(integer14);
            }
            bcj3.setRepairCost(integer14);
            EnchantmentHelper.setEnchantments(map8, bcj3);
        }
        this.resultSlots.setItem(0, bcj3);
        this.broadcastChanges();
    }
    
    public static int calculateIncreasedRepairCost(final int integer) {
        return integer * 2 + 1;
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> this.clearContainer(awg, bhr, this.repairSlots)));
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.access.<Boolean>evaluate((java.util.function.BiFunction<Level, BlockPos, Boolean>)((bhr, ew) -> {
            if (!bhr.getBlockState(ew).is(BlockTags.ANVIL)) {
                return false;
            }
            return awg.distanceToSqr(ew.getX() + 0.5, ew.getY() + 0.5, ew.getZ() + 0.5) <= 64.0;
        }), true);
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            if (integer == 2) {
                if (!this.moveItemStackTo(bcj5, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                azx5.onQuickCraft(bcj5, bcj4);
            }
            else if (integer == 0 || integer == 1) {
                if (!this.moveItemStackTo(bcj5, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 3 && integer < 39 && !this.moveItemStackTo(bcj5, 0, 2, false)) {
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
    
    public void setItemName(final String string) {
        this.itemName = string;
        if (this.getSlot(2).hasItem()) {
            final ItemStack bcj3 = this.getSlot(2).getItem();
            if (StringUtils.isBlank((CharSequence)string)) {
                bcj3.resetHoverName();
            }
            else {
                bcj3.setHoverName(new TextComponent(this.itemName));
            }
        }
        this.createResult();
    }
    
    public int getCost() {
        return this.cost.get();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
