package net.minecraft.client.color.item;

import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.MapItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.IdMapper;

public class ItemColors {
    private final IdMapper<ItemColor> itemColors;
    
    public ItemColors() {
        this.itemColors = new IdMapper<ItemColor>(32);
    }
    
    public static ItemColors createDefault(final BlockColors cyp) {
        final ItemColors cys2 = new ItemColors();
        cys2.register((bcj, integer) -> (integer > 0) ? -1 : ((DyeableLeatherItem)bcj.getItem()).getColor(bcj), Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR);
        cys2.register((bcj, integer) -> GrassColor.get(0.5, 1.0), Blocks.TALL_GRASS, Blocks.LARGE_FERN);
        CompoundTag id3;
        int[] arr4;
        int integer2;
        int integer3;
        int integer4;
        final int[] array;
        int length;
        int i = 0;
        int integer5;
        int integer6;
        int integer7;
        int integer8;
        cys2.register((bcj, integer) -> {
            if (integer != 1) {
                return -1;
            }
            else {
                id3 = bcj.getTagElement("Explosion");
                arr4 = (int[])((id3 != null && id3.contains("Colors", 11)) ? id3.getIntArray("Colors") : null);
                if (arr4 == null) {
                    return 9079434;
                }
                else if (arr4.length == 1) {
                    return arr4[0];
                }
                else {
                    integer2 = 0;
                    integer3 = 0;
                    integer4 = 0;
                    for (length = array.length; i < length; ++i) {
                        integer5 = array[i];
                        integer2 += (integer5 & 0xFF0000) >> 16;
                        integer3 += (integer5 & 0xFF00) >> 8;
                        integer4 += (integer5 & 0xFF) >> 0;
                    }
                    integer6 = integer2 / arr4.length;
                    integer7 = integer3 / arr4.length;
                    integer8 = integer4 / arr4.length;
                    return integer6 << 16 | integer7 << 8 | integer8;
                }
            }
        }, Items.FIREWORK_STAR);
        cys2.register((bcj, integer) -> (integer > 0) ? -1 : PotionUtils.getColor(bcj), Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        for (final SpawnEggItem bdh4 : SpawnEggItem.eggs()) {
            cys2.register((bcj, integer) -> bdh4.getColor(integer), bdh4);
        }
        final BlockState bvt4;
        cys2.register((bcj, integer) -> {
            bvt4 = ((BlockItem)bcj.getItem()).getBlock().defaultBlockState();
            return cyp.getColor(bvt4, null, null, integer);
        }, Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
        cys2.register((bcj, integer) -> (integer == 0) ? PotionUtils.getColor(bcj) : -1, Items.TIPPED_ARROW);
        cys2.register((bcj, integer) -> (integer == 0) ? -1 : MapItem.getColor(bcj), Items.FILLED_MAP);
        return cys2;
    }
    
    public int getColor(final ItemStack bcj, final int integer) {
        final ItemColor cyr4 = this.itemColors.byId(Registry.ITEM.getId(bcj.getItem()));
        return (cyr4 == null) ? -1 : cyr4.getColor(bcj, integer);
    }
    
    public void register(final ItemColor cyr, final ItemLike... arr) {
        for (final ItemLike bhq7 : arr) {
            this.itemColors.addMapping(cyr, Item.getId(bhq7.asItem()));
        }
    }
}
