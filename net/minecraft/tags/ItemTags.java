package net.minecraft.tags;

import java.util.Collection;
import java.util.function.Function;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemTags {
    private static TagCollection<Item> source;
    private static int resetCount;
    public static final Tag<Item> WOOL;
    public static final Tag<Item> PLANKS;
    public static final Tag<Item> STONE_BRICKS;
    public static final Tag<Item> WOODEN_BUTTONS;
    public static final Tag<Item> BUTTONS;
    public static final Tag<Item> CARPETS;
    public static final Tag<Item> WOODEN_DOORS;
    public static final Tag<Item> WOODEN_STAIRS;
    public static final Tag<Item> WOODEN_SLABS;
    public static final Tag<Item> WOODEN_FENCES;
    public static final Tag<Item> WOODEN_PRESSURE_PLATES;
    public static final Tag<Item> WOODEN_TRAPDOORS;
    public static final Tag<Item> DOORS;
    public static final Tag<Item> SAPLINGS;
    public static final Tag<Item> LOGS;
    public static final Tag<Item> DARK_OAK_LOGS;
    public static final Tag<Item> OAK_LOGS;
    public static final Tag<Item> BIRCH_LOGS;
    public static final Tag<Item> ACACIA_LOGS;
    public static final Tag<Item> JUNGLE_LOGS;
    public static final Tag<Item> SPRUCE_LOGS;
    public static final Tag<Item> BANNERS;
    public static final Tag<Item> SAND;
    public static final Tag<Item> STAIRS;
    public static final Tag<Item> SLABS;
    public static final Tag<Item> WALLS;
    public static final Tag<Item> ANVIL;
    public static final Tag<Item> RAILS;
    public static final Tag<Item> LEAVES;
    public static final Tag<Item> TRAPDOORS;
    public static final Tag<Item> SMALL_FLOWERS;
    public static final Tag<Item> BEDS;
    public static final Tag<Item> FENCES;
    public static final Tag<Item> BOATS;
    public static final Tag<Item> FISHES;
    public static final Tag<Item> SIGNS;
    public static final Tag<Item> MUSIC_DISCS;
    public static final Tag<Item> COALS;
    public static final Tag<Item> ARROWS;
    
    public static void reset(final TagCollection<Item> zh) {
        ItemTags.source = zh;
        ++ItemTags.resetCount;
    }
    
    public static TagCollection<Item> getAllTags() {
        return ItemTags.source;
    }
    
    private static Tag<Item> bind(final String string) {
        return new Wrapper(new ResourceLocation(string));
    }
    
    static {
        ItemTags.source = new TagCollection<Item>((java.util.function.Function<ResourceLocation, java.util.Optional<Item>>)(qv -> Optional.empty()), "", false, "");
        WOOL = bind("wool");
        PLANKS = bind("planks");
        STONE_BRICKS = bind("stone_bricks");
        WOODEN_BUTTONS = bind("wooden_buttons");
        BUTTONS = bind("buttons");
        CARPETS = bind("carpets");
        WOODEN_DOORS = bind("wooden_doors");
        WOODEN_STAIRS = bind("wooden_stairs");
        WOODEN_SLABS = bind("wooden_slabs");
        WOODEN_FENCES = bind("wooden_fences");
        WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
        WOODEN_TRAPDOORS = bind("wooden_trapdoors");
        DOORS = bind("doors");
        SAPLINGS = bind("saplings");
        LOGS = bind("logs");
        DARK_OAK_LOGS = bind("dark_oak_logs");
        OAK_LOGS = bind("oak_logs");
        BIRCH_LOGS = bind("birch_logs");
        ACACIA_LOGS = bind("acacia_logs");
        JUNGLE_LOGS = bind("jungle_logs");
        SPRUCE_LOGS = bind("spruce_logs");
        BANNERS = bind("banners");
        SAND = bind("sand");
        STAIRS = bind("stairs");
        SLABS = bind("slabs");
        WALLS = bind("walls");
        ANVIL = bind("anvil");
        RAILS = bind("rails");
        LEAVES = bind("leaves");
        TRAPDOORS = bind("trapdoors");
        SMALL_FLOWERS = bind("small_flowers");
        BEDS = bind("beds");
        FENCES = bind("fences");
        BOATS = bind("boats");
        FISHES = bind("fishes");
        SIGNS = bind("signs");
        MUSIC_DISCS = bind("music_discs");
        COALS = bind("coals");
        ARROWS = bind("arrows");
    }
    
    public static class Wrapper extends Tag<Item> {
        private int check;
        private Tag<Item> actual;
        
        public Wrapper(final ResourceLocation qv) {
            super(qv);
            this.check = -1;
        }
        
        @Override
        public boolean contains(final Item bce) {
            if (this.check != ItemTags.resetCount) {
                this.actual = ItemTags.source.getTagOrEmpty(this.getId());
                this.check = ItemTags.resetCount;
            }
            return this.actual.contains(bce);
        }
        
        @Override
        public Collection<Item> getValues() {
            if (this.check != ItemTags.resetCount) {
                this.actual = ItemTags.source.getTagOrEmpty(this.getId());
                this.check = ItemTags.resetCount;
            }
            return this.actual.getValues();
        }
        
        @Override
        public Collection<Entry<Item>> getSource() {
            if (this.check != ItemTags.resetCount) {
                this.actual = ItemTags.source.getTagOrEmpty(this.getId());
                this.check = ItemTags.resetCount;
            }
            return this.actual.getSource();
        }
    }
}
