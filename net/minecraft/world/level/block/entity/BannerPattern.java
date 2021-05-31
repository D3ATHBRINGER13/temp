package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import com.google.common.collect.Lists;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;

public enum BannerPattern {
    BASE("base", "b"), 
    SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "), 
    SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"), 
    SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "), 
    SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "), 
    STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"), 
    STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "), 
    STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "), 
    STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"), 
    STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "), 
    STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "), 
    STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"), 
    STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "), 
    STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "), 
    CROSS("cross", "cr", "# #", " # ", "# #"), 
    STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "), 
    TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"), 
    TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "), 
    TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "), 
    TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "), 
    DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "), 
    DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"), 
    DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "), 
    DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "), 
    CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "), 
    RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "), 
    HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "), 
    HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "), 
    HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"), 
    HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"), 
    BORDER("border", "bo", "###", "# #", "###"), 
    CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.VINE)), 
    GRADIENT("gradient", "gra", "# #", " # ", " # "), 
    GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"), 
    BRICKS("bricks", "bri", new ItemStack(Blocks.BRICKS)), 
    GLOBE("globe", "glb"), 
    CREEPER("creeper", "cre", new ItemStack(Items.CREEPER_HEAD)), 
    SKULL("skull", "sku", new ItemStack(Items.WITHER_SKELETON_SKULL)), 
    FLOWER("flower", "flo", new ItemStack(Blocks.OXEYE_DAISY)), 
    MOJANG("mojang", "moj", new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
    
    public static final int COUNT;
    public static final int AVAILABLE_PATTERNS;
    private final String filename;
    private final String hashname;
    private final String[] patterns;
    private ItemStack patternItem;
    
    private BannerPattern(final String string3, final String string4) {
        this.patterns = new String[3];
        this.patternItem = ItemStack.EMPTY;
        this.filename = string3;
        this.hashname = string4;
    }
    
    private BannerPattern(final String string3, final String string4, final ItemStack bcj) {
        this(string3, string4);
        this.patternItem = bcj;
    }
    
    private BannerPattern(final String string3, final String string4, final String string5, final String string6, final String string7) {
        this(string3, string4);
        this.patterns[0] = string5;
        this.patterns[1] = string6;
        this.patterns[2] = string7;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public String getHashname() {
        return this.hashname;
    }
    
    @Nullable
    public static BannerPattern byHash(final String string) {
        for (final BannerPattern btp5 : values()) {
            if (btp5.hashname.equals(string)) {
                return btp5;
            }
        }
        return null;
    }
    
    static {
        COUNT = values().length;
        AVAILABLE_PATTERNS = BannerPattern.COUNT - 5 - 1;
    }
    
    public static class Builder {
        private final List<Pair<BannerPattern, DyeColor>> patterns;
        
        public Builder() {
            this.patterns = (List<Pair<BannerPattern, DyeColor>>)Lists.newArrayList();
        }
        
        public Builder addPattern(final BannerPattern btp, final DyeColor bbg) {
            this.patterns.add(Pair.of((Object)btp, (Object)bbg));
            return this;
        }
        
        public ListTag toListTag() {
            final ListTag ik2 = new ListTag();
            for (final Pair<BannerPattern, DyeColor> pair4 : this.patterns) {
                final CompoundTag id5 = new CompoundTag();
                id5.putString("Pattern", ((BannerPattern)pair4.getLeft()).hashname);
                id5.putInt("Color", ((DyeColor)pair4.getRight()).getId());
                ik2.add(id5);
            }
            return ik2;
        }
    }
}
