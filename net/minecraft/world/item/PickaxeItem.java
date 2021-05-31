package net.minecraft.world.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import java.util.Set;

public class PickaxeItem extends DiggerItem {
    private static final Set<Block> DIGGABLES;
    
    protected PickaxeItem(final Tier bdn, final int integer, final float float3, final Properties a) {
        super((float)integer, float3, bdn, PickaxeItem.DIGGABLES, a);
    }
    
    @Override
    public boolean canDestroySpecial(final BlockState bvt) {
        final Block bmv3 = bvt.getBlock();
        final int integer4 = this.getTier().getLevel();
        if (bmv3 == Blocks.OBSIDIAN) {
            return integer4 == 3;
        }
        if (bmv3 == Blocks.DIAMOND_BLOCK || bmv3 == Blocks.DIAMOND_ORE || bmv3 == Blocks.EMERALD_ORE || bmv3 == Blocks.EMERALD_BLOCK || bmv3 == Blocks.GOLD_BLOCK || bmv3 == Blocks.GOLD_ORE || bmv3 == Blocks.REDSTONE_ORE) {
            return integer4 >= 2;
        }
        if (bmv3 == Blocks.IRON_BLOCK || bmv3 == Blocks.IRON_ORE || bmv3 == Blocks.LAPIS_BLOCK || bmv3 == Blocks.LAPIS_ORE) {
            return integer4 >= 1;
        }
        final Material clo5 = bvt.getMaterial();
        return clo5 == Material.STONE || clo5 == Material.METAL || clo5 == Material.HEAVY_METAL;
    }
    
    @Override
    public float getDestroySpeed(final ItemStack bcj, final BlockState bvt) {
        final Material clo4 = bvt.getMaterial();
        if (clo4 == Material.METAL || clo4 == Material.HEAVY_METAL || clo4 == Material.STONE) {
            return this.speed;
        }
        return super.getDestroySpeed(bcj, bvt);
    }
    
    static {
        DIGGABLES = (Set)ImmutableSet.of(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, (Object[])new Block[] { Blocks.POWERED_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB, Blocks.SANDSTONE_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB, Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.POLISHED_GRANITE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX });
    }
}
