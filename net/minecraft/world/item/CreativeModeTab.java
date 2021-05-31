package net.minecraft.world.item;

import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import java.util.Iterator;
import net.minecraft.core.Registry;
import net.minecraft.core.NonNullList;
import javax.annotation.Nullable;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class CreativeModeTab {
    public static final CreativeModeTab[] TABS;
    public static final CreativeModeTab TAB_BUILDING_BLOCKS;
    public static final CreativeModeTab TAB_DECORATIONS;
    public static final CreativeModeTab TAB_REDSTONE;
    public static final CreativeModeTab TAB_TRANSPORTATION;
    public static final CreativeModeTab TAB_MISC;
    public static final CreativeModeTab TAB_SEARCH;
    public static final CreativeModeTab TAB_FOOD;
    public static final CreativeModeTab TAB_TOOLS;
    public static final CreativeModeTab TAB_COMBAT;
    public static final CreativeModeTab TAB_BREWING;
    public static final CreativeModeTab TAB_MATERIALS;
    public static final CreativeModeTab TAB_HOTBAR;
    public static final CreativeModeTab TAB_INVENTORY;
    private final int id;
    private final String langId;
    private String recipeFolderName;
    private String backgroundSuffix;
    private boolean canScroll;
    private boolean showTitle;
    private EnchantmentCategory[] enchantmentCategories;
    private ItemStack iconItemStack;
    
    public CreativeModeTab(final int integer, final String string) {
        this.backgroundSuffix = "items.png";
        this.canScroll = true;
        this.showTitle = true;
        this.enchantmentCategories = new EnchantmentCategory[0];
        this.id = integer;
        this.langId = string;
        this.iconItemStack = ItemStack.EMPTY;
        CreativeModeTab.TABS[integer] = this;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getLangId() {
        return this.langId;
    }
    
    public String getRecipeFolderName() {
        return (this.recipeFolderName == null) ? this.langId : this.recipeFolderName;
    }
    
    public String getName() {
        return "itemGroup." + this.getLangId();
    }
    
    public ItemStack getIconItem() {
        if (this.iconItemStack.isEmpty()) {
            this.iconItemStack = this.makeIcon();
        }
        return this.iconItemStack;
    }
    
    public abstract ItemStack makeIcon();
    
    public String getBackgroundSuffix() {
        return this.backgroundSuffix;
    }
    
    public CreativeModeTab setBackgroundSuffix(final String string) {
        this.backgroundSuffix = string;
        return this;
    }
    
    public CreativeModeTab setRecipeFolderName(final String string) {
        this.recipeFolderName = string;
        return this;
    }
    
    public boolean showTitle() {
        return this.showTitle;
    }
    
    public CreativeModeTab hideTitle() {
        this.showTitle = false;
        return this;
    }
    
    public boolean canScroll() {
        return this.canScroll;
    }
    
    public CreativeModeTab hideScroll() {
        this.canScroll = false;
        return this;
    }
    
    public int getColumn() {
        return this.id % 6;
    }
    
    public boolean isTopRow() {
        return this.id < 6;
    }
    
    public boolean isAlignedRight() {
        return this.getColumn() == 5;
    }
    
    public EnchantmentCategory[] getEnchantmentCategories() {
        return this.enchantmentCategories;
    }
    
    public CreativeModeTab setEnchantmentCategories(final EnchantmentCategory... arr) {
        this.enchantmentCategories = arr;
        return this;
    }
    
    public boolean hasEnchantmentCategory(@Nullable final EnchantmentCategory bft) {
        if (bft != null) {
            for (final EnchantmentCategory bft2 : this.enchantmentCategories) {
                if (bft2 == bft) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void fillItemList(final NonNullList<ItemStack> fk) {
        for (final Item bce4 : Registry.ITEM) {
            bce4.fillItemCategory(this, fk);
        }
    }
    
    static {
        TABS = new CreativeModeTab[12];
        TAB_BUILDING_BLOCKS = new CreativeModeTab(0, "buildingBlocks") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Blocks.BRICKS);
            }
        }.setRecipeFolderName("building_blocks");
        TAB_DECORATIONS = new CreativeModeTab(1, "decorations") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Blocks.PEONY);
            }
        };
        TAB_REDSTONE = new CreativeModeTab(2, "redstone") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Items.REDSTONE);
            }
        };
        TAB_TRANSPORTATION = new CreativeModeTab(3, "transportation") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Blocks.POWERED_RAIL);
            }
        };
        TAB_MISC = new CreativeModeTab(6, "misc") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Items.LAVA_BUCKET);
            }
        };
        TAB_SEARCH = new CreativeModeTab(5, "search") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Items.COMPASS);
            }
        }.setBackgroundSuffix("item_search.png");
        TAB_FOOD = new CreativeModeTab(7, "food") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Items.APPLE);
            }
        };
        TAB_TOOLS = new CreativeModeTab(8, "tools") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Items.IRON_AXE);
            }
        }.setEnchantmentCategories(EnchantmentCategory.ALL, EnchantmentCategory.DIGGER, EnchantmentCategory.FISHING_ROD, EnchantmentCategory.BREAKABLE);
        TAB_COMBAT = new CreativeModeTab(9, "combat") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Items.GOLDEN_SWORD);
            }
        }.setEnchantmentCategories(EnchantmentCategory.ALL, EnchantmentCategory.ARMOR, EnchantmentCategory.ARMOR_FEET, EnchantmentCategory.ARMOR_HEAD, EnchantmentCategory.ARMOR_LEGS, EnchantmentCategory.ARMOR_CHEST, EnchantmentCategory.BOW, EnchantmentCategory.WEAPON, EnchantmentCategory.WEARABLE, EnchantmentCategory.BREAKABLE, EnchantmentCategory.TRIDENT, EnchantmentCategory.CROSSBOW);
        TAB_BREWING = new CreativeModeTab(10, "brewing") {
            @Override
            public ItemStack makeIcon() {
                return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
            }
        };
        TAB_MATERIALS = CreativeModeTab.TAB_MISC;
        TAB_HOTBAR = new CreativeModeTab(4, "hotbar") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Blocks.BOOKSHELF);
            }
            
            @Override
            public void fillItemList(final NonNullList<ItemStack> fk) {
                throw new RuntimeException("Implement exception client-side.");
            }
            
            @Override
            public boolean isAlignedRight() {
                return true;
            }
        };
        TAB_INVENTORY = new CreativeModeTab(11, "inventory") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Blocks.CHEST);
            }
        }.setBackgroundSuffix("inventory.png").hideScroll().hideTitle();
    }
}
