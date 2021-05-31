package net.minecraft.world.level.block.entity;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import java.util.Collection;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import net.minecraft.core.Direction;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.ContainerHelper;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import com.google.common.collect.Maps;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.WorldlyContainer;

public abstract class AbstractFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible, TickableBlockEntity {
    private static final int[] SLOTS_FOR_UP;
    private static final int[] SLOTS_FOR_DOWN;
    private static final int[] SLOTS_FOR_SIDES;
    protected NonNullList<ItemStack> items;
    private int litTime;
    private int litDuration;
    private int cookingProgress;
    private int cookingTotalTime;
    protected final ContainerData dataAccess;
    private final Map<ResourceLocation, Integer> recipesUsed;
    protected final RecipeType<? extends AbstractCookingRecipe> recipeType;
    
    protected AbstractFurnaceBlockEntity(final BlockEntityType<?> btx, final RecipeType<? extends AbstractCookingRecipe> beu) {
        super(btx);
        this.items = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
        this.dataAccess = new ContainerData() {
            public int get(final int integer) {
                switch (integer) {
                    case 0: {
                        return AbstractFurnaceBlockEntity.this.litTime;
                    }
                    case 1: {
                        return AbstractFurnaceBlockEntity.this.litDuration;
                    }
                    case 2: {
                        return AbstractFurnaceBlockEntity.this.cookingProgress;
                    }
                    case 3: {
                        return AbstractFurnaceBlockEntity.this.cookingTotalTime;
                    }
                    default: {
                        return 0;
                    }
                }
            }
            
            public void set(final int integer1, final int integer2) {
                switch (integer1) {
                    case 0: {
                        AbstractFurnaceBlockEntity.this.litTime = integer2;
                        break;
                    }
                    case 1: {
                        AbstractFurnaceBlockEntity.this.litDuration = integer2;
                        break;
                    }
                    case 2: {
                        AbstractFurnaceBlockEntity.this.cookingProgress = integer2;
                        break;
                    }
                    case 3: {
                        AbstractFurnaceBlockEntity.this.cookingTotalTime = integer2;
                        break;
                    }
                }
            }
            
            public int getCount() {
                return 4;
            }
        };
        this.recipesUsed = (Map<ResourceLocation, Integer>)Maps.newHashMap();
        this.recipeType = beu;
    }
    
    public static Map<Item, Integer> getFuel() {
        final Map<Item, Integer> map1 = (Map<Item, Integer>)Maps.newLinkedHashMap();
        add(map1, Items.LAVA_BUCKET, 20000);
        add(map1, Blocks.COAL_BLOCK, 16000);
        add(map1, Items.BLAZE_ROD, 2400);
        add(map1, Items.COAL, 1600);
        add(map1, Items.CHARCOAL, 1600);
        add(map1, ItemTags.LOGS, 300);
        add(map1, ItemTags.PLANKS, 300);
        add(map1, ItemTags.WOODEN_STAIRS, 300);
        add(map1, ItemTags.WOODEN_SLABS, 150);
        add(map1, ItemTags.WOODEN_TRAPDOORS, 300);
        add(map1, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        add(map1, Blocks.OAK_FENCE, 300);
        add(map1, Blocks.BIRCH_FENCE, 300);
        add(map1, Blocks.SPRUCE_FENCE, 300);
        add(map1, Blocks.JUNGLE_FENCE, 300);
        add(map1, Blocks.DARK_OAK_FENCE, 300);
        add(map1, Blocks.ACACIA_FENCE, 300);
        add(map1, Blocks.OAK_FENCE_GATE, 300);
        add(map1, Blocks.BIRCH_FENCE_GATE, 300);
        add(map1, Blocks.SPRUCE_FENCE_GATE, 300);
        add(map1, Blocks.JUNGLE_FENCE_GATE, 300);
        add(map1, Blocks.DARK_OAK_FENCE_GATE, 300);
        add(map1, Blocks.ACACIA_FENCE_GATE, 300);
        add(map1, Blocks.NOTE_BLOCK, 300);
        add(map1, Blocks.BOOKSHELF, 300);
        add(map1, Blocks.LECTERN, 300);
        add(map1, Blocks.JUKEBOX, 300);
        add(map1, Blocks.CHEST, 300);
        add(map1, Blocks.TRAPPED_CHEST, 300);
        add(map1, Blocks.CRAFTING_TABLE, 300);
        add(map1, Blocks.DAYLIGHT_DETECTOR, 300);
        add(map1, ItemTags.BANNERS, 300);
        add(map1, Items.BOW, 300);
        add(map1, Items.FISHING_ROD, 300);
        add(map1, Blocks.LADDER, 300);
        add(map1, ItemTags.SIGNS, 200);
        add(map1, Items.WOODEN_SHOVEL, 200);
        add(map1, Items.WOODEN_SWORD, 200);
        add(map1, Items.WOODEN_HOE, 200);
        add(map1, Items.WOODEN_AXE, 200);
        add(map1, Items.WOODEN_PICKAXE, 200);
        add(map1, ItemTags.WOODEN_DOORS, 200);
        add(map1, ItemTags.BOATS, 200);
        add(map1, ItemTags.WOOL, 100);
        add(map1, ItemTags.WOODEN_BUTTONS, 100);
        add(map1, Items.STICK, 100);
        add(map1, ItemTags.SAPLINGS, 100);
        add(map1, Items.BOWL, 100);
        add(map1, ItemTags.CARPETS, 67);
        add(map1, Blocks.DRIED_KELP_BLOCK, 4001);
        add(map1, Items.CROSSBOW, 300);
        add(map1, Blocks.BAMBOO, 50);
        add(map1, Blocks.DEAD_BUSH, 100);
        add(map1, Blocks.SCAFFOLDING, 50);
        add(map1, Blocks.LOOM, 300);
        add(map1, Blocks.BARREL, 300);
        add(map1, Blocks.CARTOGRAPHY_TABLE, 300);
        add(map1, Blocks.FLETCHING_TABLE, 300);
        add(map1, Blocks.SMITHING_TABLE, 300);
        add(map1, Blocks.COMPOSTER, 300);
        return map1;
    }
    
    private static void add(final Map<Item, Integer> map, final Tag<Item> zg, final int integer) {
        for (final Item bce5 : zg.getValues()) {
            map.put(bce5, integer);
        }
    }
    
    private static void add(final Map<Item, Integer> map, final ItemLike bhq, final int integer) {
        map.put(bhq.asItem(), integer);
    }
    
    private boolean isLit() {
        return this.litTime > 0;
    }
    
    @Override
    public void load(final CompoundTag id) {
        super.load(id);
        ContainerHelper.loadAllItems(id, this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY));
        this.litTime = id.getShort("BurnTime");
        this.cookingProgress = id.getShort("CookTime");
        this.cookingTotalTime = id.getShort("CookTimeTotal");
        this.litDuration = this.getBurnDuration(this.items.get(1));
        for (int integer3 = id.getShort("RecipesUsedSize"), integer4 = 0; integer4 < integer3; ++integer4) {
            final ResourceLocation qv5 = new ResourceLocation(id.getString(new StringBuilder().append("RecipeLocation").append(integer4).toString()));
            final int integer5 = id.getInt(new StringBuilder().append("RecipeAmount").append(integer4).toString());
            this.recipesUsed.put(qv5, integer5);
        }
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        super.save(id);
        id.putShort("BurnTime", (short)this.litTime);
        id.putShort("CookTime", (short)this.cookingProgress);
        id.putShort("CookTimeTotal", (short)this.cookingTotalTime);
        ContainerHelper.saveAllItems(id, this.items);
        id.putShort("RecipesUsedSize", (short)this.recipesUsed.size());
        int integer3 = 0;
        for (final Map.Entry<ResourceLocation, Integer> entry5 : this.recipesUsed.entrySet()) {
            id.putString(new StringBuilder().append("RecipeLocation").append(integer3).toString(), ((ResourceLocation)entry5.getKey()).toString());
            id.putInt(new StringBuilder().append("RecipeAmount").append(integer3).toString(), (int)entry5.getValue());
            ++integer3;
        }
        return id;
    }
    
    @Override
    public void tick() {
        final boolean boolean2 = this.isLit();
        boolean boolean3 = false;
        if (this.isLit()) {
            --this.litTime;
        }
        if (!this.level.isClientSide) {
            final ItemStack bcj4 = this.items.get(1);
            if (this.isLit() || (!bcj4.isEmpty() && !this.items.get(0).isEmpty())) {
                final Recipe<?> ber5 = this.level.getRecipeManager().getRecipeFor(this.recipeType, this, this.level).orElse(null);
                if (!this.isLit() && this.canBurn(ber5)) {
                    this.litTime = this.getBurnDuration(bcj4);
                    this.litDuration = this.litTime;
                    if (this.isLit()) {
                        boolean3 = true;
                        if (!bcj4.isEmpty()) {
                            final Item bce6 = bcj4.getItem();
                            bcj4.shrink(1);
                            if (bcj4.isEmpty()) {
                                final Item bce7 = bce6.getCraftingRemainingItem();
                                this.items.set(1, (bce7 == null) ? ItemStack.EMPTY : new ItemStack(bce7));
                            }
                        }
                    }
                }
                if (this.isLit() && this.canBurn(ber5)) {
                    ++this.cookingProgress;
                    if (this.cookingProgress == this.cookingTotalTime) {
                        this.cookingProgress = 0;
                        this.cookingTotalTime = this.getTotalCookTime();
                        this.burn(ber5);
                        boolean3 = true;
                    }
                }
                else {
                    this.cookingProgress = 0;
                }
            }
            else if (!this.isLit() && this.cookingProgress > 0) {
                this.cookingProgress = Mth.clamp(this.cookingProgress - 2, 0, this.cookingTotalTime);
            }
            if (boolean2 != this.isLit()) {
                boolean3 = true;
                this.level.setBlock(this.worldPosition, ((AbstractStateHolder<O, BlockState>)this.level.getBlockState(this.worldPosition)).<Comparable, Boolean>setValue((Property<Comparable>)AbstractFurnaceBlock.LIT, this.isLit()), 3);
            }
        }
        if (boolean3) {
            this.setChanged();
        }
    }
    
    protected boolean canBurn(@Nullable final Recipe<?> ber) {
        if (this.items.get(0).isEmpty() || ber == null) {
            return false;
        }
        final ItemStack bcj3 = ber.getResultItem();
        if (bcj3.isEmpty()) {
            return false;
        }
        final ItemStack bcj4 = this.items.get(2);
        return bcj4.isEmpty() || (bcj4.sameItem(bcj3) && ((bcj4.getCount() < this.getMaxStackSize() && bcj4.getCount() < bcj4.getMaxStackSize()) || bcj4.getCount() < bcj3.getMaxStackSize()));
    }
    
    private void burn(@Nullable final Recipe<?> ber) {
        if (ber == null || !this.canBurn(ber)) {
            return;
        }
        final ItemStack bcj3 = this.items.get(0);
        final ItemStack bcj4 = ber.getResultItem();
        final ItemStack bcj5 = this.items.get(2);
        if (bcj5.isEmpty()) {
            this.items.set(2, bcj4.copy());
        }
        else if (bcj5.getItem() == bcj4.getItem()) {
            bcj5.grow(1);
        }
        if (!this.level.isClientSide) {
            this.setRecipeUsed(ber);
        }
        if (bcj3.getItem() == Blocks.WET_SPONGE.asItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET) {
            this.items.set(1, new ItemStack(Items.WATER_BUCKET));
        }
        bcj3.shrink(1);
    }
    
    protected int getBurnDuration(final ItemStack bcj) {
        if (bcj.isEmpty()) {
            return 0;
        }
        final Item bce3 = bcj.getItem();
        return (int)getFuel().getOrDefault(bce3, 0);
    }
    
    protected int getTotalCookTime() {
        return (int)this.level.getRecipeManager().getRecipeFor(this.recipeType, this, this.level).map(AbstractCookingRecipe::getCookingTime).orElse(200);
    }
    
    public static boolean isFuel(final ItemStack bcj) {
        return getFuel().containsKey(bcj.getItem());
    }
    
    @Override
    public int[] getSlotsForFace(final Direction fb) {
        if (fb == Direction.DOWN) {
            return AbstractFurnaceBlockEntity.SLOTS_FOR_DOWN;
        }
        if (fb == Direction.UP) {
            return AbstractFurnaceBlockEntity.SLOTS_FOR_UP;
        }
        return AbstractFurnaceBlockEntity.SLOTS_FOR_SIDES;
    }
    
    @Override
    public boolean canPlaceItemThroughFace(final int integer, final ItemStack bcj, @Nullable final Direction fb) {
        return this.canPlaceItem(integer, bcj);
    }
    
    @Override
    public boolean canTakeItemThroughFace(final int integer, final ItemStack bcj, final Direction fb) {
        if (fb == Direction.DOWN && integer == 1) {
            final Item bce5 = bcj.getItem();
            if (bce5 != Items.WATER_BUCKET && bce5 != Items.BUCKET) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int getContainerSize() {
        return this.items.size();
    }
    
    @Override
    public boolean isEmpty() {
        for (final ItemStack bcj3 : this.items) {
            if (!bcj3.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getItem(final int integer) {
        return this.items.get(integer);
    }
    
    @Override
    public ItemStack removeItem(final int integer1, final int integer2) {
        return ContainerHelper.removeItem((List<ItemStack>)this.items, integer1, integer2);
    }
    
    @Override
    public ItemStack removeItemNoUpdate(final int integer) {
        return ContainerHelper.takeItem((List<ItemStack>)this.items, integer);
    }
    
    @Override
    public void setItem(final int integer, final ItemStack bcj) {
        final ItemStack bcj2 = this.items.get(integer);
        final boolean boolean5 = !bcj.isEmpty() && bcj.sameItem(bcj2) && ItemStack.tagMatches(bcj, bcj2);
        this.items.set(integer, bcj);
        if (bcj.getCount() > this.getMaxStackSize()) {
            bcj.setCount(this.getMaxStackSize());
        }
        if (integer == 0 && !boolean5) {
            this.cookingTotalTime = this.getTotalCookTime();
            this.cookingProgress = 0;
            this.setChanged();
        }
    }
    
    @Override
    public boolean stillValid(final Player awg) {
        return this.level.getBlockEntity(this.worldPosition) == this && awg.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }
    
    @Override
    public boolean canPlaceItem(final int integer, final ItemStack bcj) {
        if (integer == 2) {
            return false;
        }
        if (integer == 1) {
            final ItemStack bcj2 = this.items.get(1);
            return isFuel(bcj) || (bcj.getItem() == Items.BUCKET && bcj2.getItem() != Items.BUCKET);
        }
        return true;
    }
    
    public void clearContent() {
        this.items.clear();
    }
    
    @Override
    public void setRecipeUsed(@Nullable final Recipe<?> ber) {
        if (ber != null) {
            this.recipesUsed.compute(ber.getId(), (qv, integer) -> 1 + ((integer == null) ? 0 : integer));
        }
    }
    
    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }
    
    @Override
    public void awardAndReset(final Player awg) {
    }
    
    public void awardResetAndExperience(final Player awg) {
        final List<Recipe<?>> list3 = (List<Recipe<?>>)Lists.newArrayList();
        for (final Map.Entry<ResourceLocation, Integer> entry5 : this.recipesUsed.entrySet()) {
            awg.level.getRecipeManager().byKey((ResourceLocation)entry5.getKey()).ifPresent(ber -> {
                list3.add(ber);
                createExperience(awg, (int)entry5.getValue(), ((AbstractCookingRecipe)ber).getExperience());
            });
        }
        awg.awardRecipes((Collection<Recipe<?>>)list3);
        this.recipesUsed.clear();
    }
    
    private static void createExperience(final Player awg, int integer, final float float3) {
        if (float3 == 0.0f) {
            integer = 0;
        }
        else if (float3 < 1.0f) {
            int integer2 = Mth.floor(integer * float3);
            if (integer2 < Mth.ceil(integer * float3) && Math.random() < integer * float3 - integer2) {
                ++integer2;
            }
            integer = integer2;
        }
        while (integer > 0) {
            final int integer2 = ExperienceOrb.getExperienceValue(integer);
            integer -= integer2;
            awg.level.addFreshEntity(new ExperienceOrb(awg.level, awg.x, awg.y + 0.5, awg.z + 0.5, integer2));
        }
    }
    
    @Override
    public void fillStackedContents(final StackedContents awi) {
        for (final ItemStack bcj4 : this.items) {
            awi.accountStack(bcj4);
        }
    }
    
    static {
        SLOTS_FOR_UP = new int[] { 0 };
        SLOTS_FOR_DOWN = new int[] { 2, 1 };
        SLOTS_FOR_SIDES = new int[] { 1 };
    }
}
