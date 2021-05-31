package net.minecraft.world.inventory;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import java.util.List;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;
import com.google.common.collect.ImmutableList;

public class StonecutterMenu extends AbstractContainerMenu {
    static final ImmutableList<Item> validItems;
    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex;
    private final Level level;
    private List<StonecutterRecipe> recipes;
    private ItemStack input;
    private long lastSoundTime;
    final Slot inputSlot;
    final Slot resultSlot;
    private Runnable slotUpdateListener;
    public final Container container;
    private final ResultContainer resultContainer;
    
    public StonecutterMenu(final int integer, final Inventory awf) {
        this(integer, awf, ContainerLevelAccess.NULL);
    }
    
    public StonecutterMenu(final int integer, final Inventory awf, final ContainerLevelAccess ayu) {
        super(MenuType.STONECUTTER, integer);
        this.selectedRecipeIndex = DataSlot.standalone();
        this.recipes = (List<StonecutterRecipe>)Lists.newArrayList();
        this.input = ItemStack.EMPTY;
        this.slotUpdateListener = (() -> {});
        this.container = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                StonecutterMenu.this.slotsChanged(this);
                StonecutterMenu.this.slotUpdateListener.run();
            }
        };
        this.resultContainer = new ResultContainer();
        this.access = ayu;
        this.level = awf.player.level;
        this.inputSlot = this.addSlot(new Slot(this.container, 0, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
            @Override
            public boolean mayPlace(final ItemStack bcj) {
                return false;
            }
            
            @Override
            public ItemStack onTake(final Player awg, final ItemStack bcj) {
                final ItemStack bcj2 = StonecutterMenu.this.inputSlot.remove(1);
                if (!bcj2.isEmpty()) {
                    StonecutterMenu.this.setupResultSlot();
                }
                bcj.getItem().onCraftedBy(bcj, awg.level, awg);
                ayu.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> {
                    final long long4 = bhr.getGameTime();
                    if (StonecutterMenu.this.lastSoundTime != long4) {
                        bhr.playSound(null, ew, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);
                        StonecutterMenu.this.lastSoundTime = long4;
                    }
                }));
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
        this.addDataSlot(this.selectedRecipeIndex);
    }
    
    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }
    
    public List<StonecutterRecipe> getRecipes() {
        return this.recipes;
    }
    
    public int getNumRecipes() {
        return this.recipes.size();
    }
    
    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.recipes.isEmpty();
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return AbstractContainerMenu.stillValid(this.access, awg, Blocks.STONECUTTER);
    }
    
    @Override
    public boolean clickMenuButton(final Player awg, final int integer) {
        if (integer >= 0 && integer < this.recipes.size()) {
            this.selectedRecipeIndex.set(integer);
            this.setupResultSlot();
        }
        return true;
    }
    
    @Override
    public void slotsChanged(final Container ahc) {
        final ItemStack bcj3 = this.inputSlot.getItem();
        if (bcj3.getItem() != this.input.getItem()) {
            this.input = bcj3.copy();
            this.setupRecipeList(ahc, bcj3);
        }
    }
    
    private void setupRecipeList(final Container ahc, final ItemStack bcj) {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!bcj.isEmpty()) {
            this.recipes = this.level.getRecipeManager().<Container, StonecutterRecipe>getRecipesFor(RecipeType.STONECUTTING, ahc, this.level);
        }
    }
    
    private void setupResultSlot() {
        if (!this.recipes.isEmpty()) {
            final StonecutterRecipe bff2 = (StonecutterRecipe)this.recipes.get(this.selectedRecipeIndex.get());
            this.resultSlot.set(bff2.assemble(this.container));
        }
        else {
            this.resultSlot.set(ItemStack.EMPTY);
        }
        this.broadcastChanges();
    }
    
    @Override
    public MenuType<?> getType() {
        return MenuType.STONECUTTER;
    }
    
    public void registerUpdateListener(final Runnable runnable) {
        this.slotUpdateListener = runnable;
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
            final ItemStack bcj5 = azx5.getItem();
            final Item bce7 = bcj5.getItem();
            bcj4 = bcj5.copy();
            if (integer == 1) {
                bce7.onCraftedBy(bcj5, awg.level, awg);
                if (!this.moveItemStackTo(bcj5, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                azx5.onQuickCraft(bcj5, bcj4);
            }
            else if (integer == 0) {
                if (!this.moveItemStackTo(bcj5, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (StonecutterMenu.validItems.contains(bce7)) {
                if (!this.moveItemStackTo(bcj5, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 2 && integer < 29) {
                if (!this.moveItemStackTo(bcj5, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (integer >= 29 && integer < 38 && !this.moveItemStackTo(bcj5, 2, 29, false)) {
                return ItemStack.EMPTY;
            }
            if (bcj5.isEmpty()) {
                azx5.set(ItemStack.EMPTY);
            }
            azx5.setChanged();
            if (bcj5.getCount() == bcj4.getCount()) {
                return ItemStack.EMPTY;
            }
            azx5.onTake(awg, bcj5);
            this.broadcastChanges();
        }
        return bcj4;
    }
    
    @Override
    public void removed(final Player awg) {
        super.removed(awg);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((BiConsumer<Level, BlockPos>)((bhr, ew) -> this.clearContainer(awg, awg.level, this.container)));
    }
    
    static {
        validItems = ImmutableList.of(Items.STONE, Items.SANDSTONE, Items.RED_SANDSTONE, Items.QUARTZ_BLOCK, Items.COBBLESTONE, Items.STONE_BRICKS, Items.BRICKS, Items.NETHER_BRICKS, Items.RED_NETHER_BRICKS, Items.PURPUR_BLOCK, Items.PRISMARINE, Items.PRISMARINE_BRICKS, (Object[])new Item[] { Items.DARK_PRISMARINE, Items.ANDESITE, Items.POLISHED_ANDESITE, Items.GRANITE, Items.POLISHED_GRANITE, Items.DIORITE, Items.POLISHED_DIORITE, Items.MOSSY_STONE_BRICKS, Items.MOSSY_COBBLESTONE, Items.SMOOTH_SANDSTONE, Items.SMOOTH_RED_SANDSTONE, Items.SMOOTH_QUARTZ, Items.END_STONE, Items.END_STONE_BRICKS, Items.SMOOTH_STONE, Items.CUT_SANDSTONE, Items.CUT_RED_SANDSTONE });
    }
}
