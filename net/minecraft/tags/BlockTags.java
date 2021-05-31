package net.minecraft.tags;

import java.util.Collection;
import java.util.function.Function;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class BlockTags {
    private static TagCollection<Block> source;
    private static int resetCount;
    public static final Tag<Block> WOOL;
    public static final Tag<Block> PLANKS;
    public static final Tag<Block> STONE_BRICKS;
    public static final Tag<Block> WOODEN_BUTTONS;
    public static final Tag<Block> BUTTONS;
    public static final Tag<Block> CARPETS;
    public static final Tag<Block> WOODEN_DOORS;
    public static final Tag<Block> WOODEN_STAIRS;
    public static final Tag<Block> WOODEN_SLABS;
    public static final Tag<Block> WOODEN_FENCES;
    public static final Tag<Block> WOODEN_PRESSURE_PLATES;
    public static final Tag<Block> WOODEN_TRAPDOORS;
    public static final Tag<Block> DOORS;
    public static final Tag<Block> SAPLINGS;
    public static final Tag<Block> LOGS;
    public static final Tag<Block> DARK_OAK_LOGS;
    public static final Tag<Block> OAK_LOGS;
    public static final Tag<Block> BIRCH_LOGS;
    public static final Tag<Block> ACACIA_LOGS;
    public static final Tag<Block> JUNGLE_LOGS;
    public static final Tag<Block> SPRUCE_LOGS;
    public static final Tag<Block> BANNERS;
    public static final Tag<Block> SAND;
    public static final Tag<Block> STAIRS;
    public static final Tag<Block> SLABS;
    public static final Tag<Block> WALLS;
    public static final Tag<Block> ANVIL;
    public static final Tag<Block> RAILS;
    public static final Tag<Block> LEAVES;
    public static final Tag<Block> TRAPDOORS;
    public static final Tag<Block> SMALL_FLOWERS;
    public static final Tag<Block> BEDS;
    public static final Tag<Block> FENCES;
    public static final Tag<Block> FLOWER_POTS;
    public static final Tag<Block> ENDERMAN_HOLDABLE;
    public static final Tag<Block> ICE;
    public static final Tag<Block> VALID_SPAWN;
    public static final Tag<Block> IMPERMEABLE;
    public static final Tag<Block> UNDERWATER_BONEMEALS;
    public static final Tag<Block> CORAL_BLOCKS;
    public static final Tag<Block> WALL_CORALS;
    public static final Tag<Block> CORAL_PLANTS;
    public static final Tag<Block> CORALS;
    public static final Tag<Block> BAMBOO_PLANTABLE_ON;
    public static final Tag<Block> DIRT_LIKE;
    public static final Tag<Block> STANDING_SIGNS;
    public static final Tag<Block> WALL_SIGNS;
    public static final Tag<Block> SIGNS;
    public static final Tag<Block> DRAGON_IMMUNE;
    public static final Tag<Block> WITHER_IMMUNE;
    
    public static void reset(final TagCollection<Block> zh) {
        BlockTags.source = zh;
        ++BlockTags.resetCount;
    }
    
    public static TagCollection<Block> getAllTags() {
        return BlockTags.source;
    }
    
    private static Tag<Block> bind(final String string) {
        return new Wrapper(new ResourceLocation(string));
    }
    
    static {
        BlockTags.source = new TagCollection<Block>((java.util.function.Function<ResourceLocation, java.util.Optional<Block>>)(qv -> Optional.empty()), "", false, "");
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
        FLOWER_POTS = bind("flower_pots");
        ENDERMAN_HOLDABLE = bind("enderman_holdable");
        ICE = bind("ice");
        VALID_SPAWN = bind("valid_spawn");
        IMPERMEABLE = bind("impermeable");
        UNDERWATER_BONEMEALS = bind("underwater_bonemeals");
        CORAL_BLOCKS = bind("coral_blocks");
        WALL_CORALS = bind("wall_corals");
        CORAL_PLANTS = bind("coral_plants");
        CORALS = bind("corals");
        BAMBOO_PLANTABLE_ON = bind("bamboo_plantable_on");
        DIRT_LIKE = bind("dirt_like");
        STANDING_SIGNS = bind("standing_signs");
        WALL_SIGNS = bind("wall_signs");
        SIGNS = bind("signs");
        DRAGON_IMMUNE = bind("dragon_immune");
        WITHER_IMMUNE = bind("wither_immune");
    }
    
    static class Wrapper extends Tag<Block> {
        private int check;
        private Tag<Block> actual;
        
        public Wrapper(final ResourceLocation qv) {
            super(qv);
            this.check = -1;
        }
        
        @Override
        public boolean contains(final Block bmv) {
            if (this.check != BlockTags.resetCount) {
                this.actual = BlockTags.source.getTagOrEmpty(this.getId());
                this.check = BlockTags.resetCount;
            }
            return this.actual.contains(bmv);
        }
        
        @Override
        public Collection<Block> getValues() {
            if (this.check != BlockTags.resetCount) {
                this.actual = BlockTags.source.getTagOrEmpty(this.getId());
                this.check = BlockTags.resetCount;
            }
            return this.actual.getValues();
        }
        
        @Override
        public Collection<Entry<Block>> getSource() {
            if (this.check != BlockTags.resetCount) {
                this.actual = BlockTags.source.getTagOrEmpty(this.getId());
                this.check = BlockTags.resetCount;
            }
            return this.actual.getSource();
        }
    }
}
