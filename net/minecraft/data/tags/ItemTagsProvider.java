package net.minecraft.data.tags;

import org.apache.logging.log4j.LogManager;
import net.minecraft.tags.TagCollection;
import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.item.Item;

public class ItemTagsProvider extends TagsProvider<Item> {
    private static final Logger LOGGER;
    
    public ItemTagsProvider(final DataGenerator gk) {
        super(gk, Registry.ITEM);
    }
    
    @Override
    protected void addTags() {
        this.copy(BlockTags.WOOL, ItemTags.WOOL);
        this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
        this.copy(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
        this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
        this.copy(BlockTags.CARPETS, ItemTags.CARPETS);
        this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        this.copy(BlockTags.DOORS, ItemTags.DOORS);
        this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        this.copy(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
        this.copy(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
        this.copy(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
        this.copy(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
        this.copy(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
        this.copy(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
        this.copy(BlockTags.LOGS, ItemTags.LOGS);
        this.copy(BlockTags.SAND, ItemTags.SAND);
        this.copy(BlockTags.SLABS, ItemTags.SLABS);
        this.copy(BlockTags.WALLS, ItemTags.WALLS);
        this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
        this.copy(BlockTags.ANVIL, ItemTags.ANVIL);
        this.copy(BlockTags.RAILS, ItemTags.RAILS);
        this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
        this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
        this.copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
        this.copy(BlockTags.BEDS, ItemTags.BEDS);
        this.copy(BlockTags.FENCES, ItemTags.FENCES);
        this.tag(ItemTags.BANNERS).add(Items.WHITE_BANNER, Items.ORANGE_BANNER, Items.MAGENTA_BANNER, Items.LIGHT_BLUE_BANNER, Items.YELLOW_BANNER, Items.LIME_BANNER, Items.PINK_BANNER, Items.GRAY_BANNER, Items.LIGHT_GRAY_BANNER, Items.CYAN_BANNER, Items.PURPLE_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER, Items.GREEN_BANNER, Items.RED_BANNER, Items.BLACK_BANNER);
        this.tag(ItemTags.BOATS).add(Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT);
        this.tag(ItemTags.FISHES).add(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
        this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
        this.tag(ItemTags.MUSIC_DISCS).add(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT);
        this.tag(ItemTags.COALS).add(Items.COAL, Items.CHARCOAL);
        this.tag(ItemTags.ARROWS).add(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW);
    }
    
    protected void copy(final Tag<Block> zg1, final Tag<Item> zg2) {
        final Tag.Builder<Item> a4 = this.tag(zg2);
        for (final Tag.Entry<Block> b6 : zg1.getSource()) {
            final Tag.Entry<Item> b7 = this.copy(b6);
            a4.add(b7);
        }
    }
    
    private Tag.Entry<Item> copy(final Tag.Entry<Block> b) {
        if (b instanceof Tag.TagEntry) {
            return new Tag.TagEntry<Item>(((Tag.TagEntry)b).getId());
        }
        if (b instanceof Tag.ValuesEntry) {
            final List<Item> list3 = (List<Item>)Lists.newArrayList();
            for (final Block bmv5 : ((Tag.ValuesEntry)b).getValues()) {
                final Item bce6 = bmv5.asItem();
                if (bce6 == Items.AIR) {
                    ItemTagsProvider.LOGGER.warn("Itemless block copied to item tag: {}", Registry.BLOCK.getKey(bmv5));
                }
                else {
                    list3.add(bce6);
                }
            }
            return new Tag.ValuesEntry<Item>((java.util.Collection<Item>)list3);
        }
        throw new UnsupportedOperationException(new StringBuilder().append("Unknown tag entry ").append(b).toString());
    }
    
    @Override
    protected Path getPath(final ResourceLocation qv) {
        return this.generator.getOutputFolder().resolve("data/" + qv.getNamespace() + "/tags/items/" + qv.getPath() + ".json");
    }
    
    public String getName() {
        return "Item Tags";
    }
    
    @Override
    protected void useTags(final TagCollection<Item> zh) {
        ItemTags.reset(zh);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
