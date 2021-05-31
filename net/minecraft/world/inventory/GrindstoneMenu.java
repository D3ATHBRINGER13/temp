package net.minecraft.world.inventory;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ItemLike;
import java.util.stream.Collectors;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import java.util.Iterator;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.Map;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;

public class GrindstoneMenu extends AbstractContainerMenu {
    private final Container resultSlots;
    private final Container repairSlots;
    private final ContainerLevelAccess access;
    
    public GrindstoneMenu(final int integer, final Inventory awf) {
        this(integer, awf, ContainerLevelAccess.NULL);
    }
    
    public GrindstoneMenu(final int integer, final Inventory awf, final ContainerLevelAccess ayu) {
        super(MenuType.GRINDSTONE, integer);
        this.resultSlots = new ResultContainer();
        this.repairSlots = new SimpleContainer(2) {
            @Override
            public void setChanged() {
                super.setChanged();
                GrindstoneMenu.this.slotsChanged(this);
            }
        };
        this.access = ayu;
        this.addSlot(new Slot(this.repairSlots, 0, 49, 19) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return bcj.isDamageableItem() || bcj.getItem() == Items.ENCHANTED_BOOK || bcj.isEnchanted();
            }
        });
        this.addSlot(new Slot(this.repairSlots, 1, 49, 40) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return bcj.isDamageableItem() || bcj.getItem() == Items.ENCHANTED_BOOK || bcj.isEnchanted();
            }
        });
        this.addSlot(new Slot(this.resultSlots, 2, 129, 34) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return false;
            }
            
            @Override
            public ItemStack onTake(final Player awg, final ItemStack bcj) {
                ayu.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> {
                    int integer4 = this.getExperienceAmount(bhr);
                    while (integer4 > 0) {
                        final int integer5 = ExperienceOrb.getExperienceValue(integer4);
                        integer4 -= integer5;
                        bhr.addFreshEntity(new ExperienceOrb(bhr, ew.getX(), ew.getY() + 0.5, ew.getZ() + 0.5, integer5));
                    }
                    bhr.levelEvent(1042, ew, 0);
                }));
                GrindstoneMenu.this.repairSlots.setItem(0, ItemStack.EMPTY);
                GrindstoneMenu.this.repairSlots.setItem(1, ItemStack.EMPTY);
                return bcj;
            }
            
            private int getExperienceAmount(final Level bhr) {
                int integer3 = 0;
                integer3 += this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(0));
                integer3 += this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(1));
                if (integer3 > 0) {
                    final int integer4 = (int)Math.ceil(integer3 / 2.0);
                    return integer4 + bhr.random.nextInt(integer4);
                }
                return 0;
            }
            
            private int getExperienceFromItem(final ItemStack bcj) {
                int integer3 = 0;
                final Map<Enchantment, Integer> map4 = EnchantmentHelper.getEnchantments(bcj);
                for (final Map.Entry<Enchantment, Integer> entry6 : map4.entrySet()) {
                    final Enchantment bfs7 = (Enchantment)entry6.getKey();
                    final Integer integer4 = (Integer)entry6.getValue();
                    if (!bfs7.isCurse()) {
                        integer3 += bfs7.getMinCost(integer4);
                    }
                }
                return integer3;
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
    
    private void createResult() {
        final ItemStack bcj2 = this.repairSlots.getItem(0);
        final ItemStack bcj3 = this.repairSlots.getItem(1);
        final boolean boolean4 = !bcj2.isEmpty() || !bcj3.isEmpty();
        final boolean boolean5 = !bcj2.isEmpty() && !bcj3.isEmpty();
        if (boolean4) {
            final boolean boolean6 = (!bcj2.isEmpty() && bcj2.getItem() != Items.ENCHANTED_BOOK && !bcj2.isEnchanted()) || (!bcj3.isEmpty() && bcj3.getItem() != Items.ENCHANTED_BOOK && !bcj3.isEnchanted());
            if (bcj2.getCount() > 1 || bcj3.getCount() > 1 || (!boolean5 && boolean6)) {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
                this.broadcastChanges();
                return;
            }
            int integer8 = 1;
            int integer12;
            ItemStack bcj4;
            if (boolean5) {
                if (bcj2.getItem() != bcj3.getItem()) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.broadcastChanges();
                    return;
                }
                final Item bce10 = bcj2.getItem();
                final int integer9 = bce10.getMaxDamage() - bcj2.getDamageValue();
                final int integer10 = bce10.getMaxDamage() - bcj3.getDamageValue();
                final int integer11 = integer9 + integer10 + bce10.getMaxDamage() * 5 / 100;
                integer12 = Math.max(bce10.getMaxDamage() - integer11, 0);
                bcj4 = this.mergeEnchants(bcj2, bcj3);
                if (!bcj4.isDamageableItem()) {
                    if (!ItemStack.matches(bcj2, bcj3)) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.broadcastChanges();
                        return;
                    }
                    integer8 = 2;
                }
            }
            else {
                final boolean boolean7 = !bcj2.isEmpty();
                integer12 = (boolean7 ? bcj2.getDamageValue() : bcj3.getDamageValue());
                bcj4 = (boolean7 ? bcj2 : bcj3);
            }
            this.resultSlots.setItem(0, this.removeNonCurses(bcj4, integer12, integer8));
        }
        else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        }
        this.broadcastChanges();
    }
    
    private ItemStack mergeEnchants(final ItemStack bcj1, final ItemStack bcj2) {
        final ItemStack bcj3 = bcj1.copy();
        final Map<Enchantment, Integer> map5 = EnchantmentHelper.getEnchantments(bcj2);
        for (final Map.Entry<Enchantment, Integer> entry7 : map5.entrySet()) {
            final Enchantment bfs8 = (Enchantment)entry7.getKey();
            if (!bfs8.isCurse() || EnchantmentHelper.getItemEnchantmentLevel(bfs8, bcj3) == 0) {
                bcj3.enchant(bfs8, (int)entry7.getValue());
            }
        }
        return bcj3;
    }
    
    private ItemStack removeNonCurses(final ItemStack bcj, final int integer2, final int integer3) {
        ItemStack bcj2 = bcj.copy();
        bcj2.removeTagKey("Enchantments");
        bcj2.removeTagKey("StoredEnchantments");
        if (integer2 > 0) {
            bcj2.setDamageValue(integer2);
        }
        else {
            bcj2.removeTagKey("Damage");
        }
        bcj2.setCount(integer3);
        final Map<Enchantment, Integer> map6 = (Map<Enchantment, Integer>)EnchantmentHelper.getEnchantments(bcj).entrySet().stream().filter(entry -> ((Enchantment)entry.getKey()).isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.setEnchantments(map6, bcj2);
        bcj2.setRepairCost(0);
        if (bcj2.getItem() == Items.ENCHANTED_BOOK && map6.size() == 0) {
            bcj2 = new ItemStack(Items.BOOK);
            if (bcj.hasCustomHoverName()) {
                bcj2.setHoverName(bcj.getHoverName());
            }
        }
        for (int integer4 = 0; integer4 < map6.size(); ++integer4) {
            bcj2.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(bcj2.getBaseRepairCost()));
        }
        return bcj2;
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> this.clearContainer(awg, bhr, this.repairSlots)));
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return AbstractContainerMenu.stillValid(this.access, awg, Blocks.GRINDSTONE);
    }
    
    @Override
    public ItemStack quickMoveStack(final Player awg, final int integer) {
        ItemStack bcj4 = ItemStack.EMPTY;
        final Slot azx5 = (Slot)this.slots.get(integer);
        if (azx5 != null && azx5.hasItem()) {
            final ItemStack bcj5 = azx5.getItem();
            bcj4 = bcj5.copy();
            final ItemStack bcj6 = this.repairSlots.getItem(0);
            final ItemStack bcj7 = this.repairSlots.getItem(1);
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
            else if (bcj6.isEmpty() || bcj7.isEmpty()) {
                if (!this.moveItemStackTo(bcj5, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 3 && integer < 30) {
                if (!this.moveItemStackTo(bcj5, 30, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 30 && integer < 39 && !this.moveItemStackTo(bcj5, 3, 30, false)) {
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
}
