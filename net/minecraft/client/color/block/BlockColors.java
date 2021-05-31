package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nullable;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.ShearableDoublePlantBlock;
import com.google.common.collect.Maps;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import java.util.Map;
import net.minecraft.core.IdMapper;

public class BlockColors {
    private final IdMapper<BlockColor> blockColors;
    private final Map<Block, Set<Property<?>>> coloringStates;
    
    public BlockColors() {
        this.blockColors = new IdMapper<BlockColor>(32);
        this.coloringStates = (Map<Block, Set<Property<?>>>)Maps.newHashMap();
    }
    
    public static BlockColors createDefault() {
        final BlockColors cyp1 = new BlockColors();
        cyp1.register((bvt, bgz, ew, integer) -> {
            if (bgz == null || ew == null) {
                return -1;
            }
            else {
                return BiomeColors.getAverageGrassColor(bgz, (bvt.<DoubleBlockHalf>getValue(ShearableDoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) ? ew.below() : ew);
            }
        }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        cyp1.addColoringState(ShearableDoublePlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        cyp1.register((bvt, bgz, ew, integer) -> {
            if (bgz == null || ew == null) {
                return GrassColor.get(0.5, 1.0);
            }
            else {
                return BiomeColors.getAverageGrassColor(bgz, ew);
            }
        }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);
        cyp1.register((bvt, bgz, ew, integer) -> FoliageColor.getEvergreenColor(), Blocks.SPRUCE_LEAVES);
        cyp1.register((bvt, bgz, ew, integer) -> FoliageColor.getBirchColor(), Blocks.BIRCH_LEAVES);
        cyp1.register((bvt, bgz, ew, integer) -> {
            if (bgz == null || ew == null) {
                return FoliageColor.getDefaultColor();
            }
            else {
                return BiomeColors.getAverageFoliageColor(bgz, ew);
            }
        }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE);
        cyp1.register((bvt, bgz, ew, integer) -> {
            if (bgz == null || ew == null) {
                return -1;
            }
            else {
                return BiomeColors.getAverageWaterColor(bgz, ew);
            }
        }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.CAULDRON);
        cyp1.register((bvt, bgz, ew, integer) -> RedStoneWireBlock.getColorForData(bvt.<Integer>getValue((Property<Integer>)RedStoneWireBlock.POWER)), Blocks.REDSTONE_WIRE);
        cyp1.addColoringState(RedStoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
        cyp1.register((bvt, bgz, ew, integer) -> {
            if (bgz == null || ew == null) {
                return -1;
            }
            else {
                return BiomeColors.getAverageGrassColor(bgz, ew);
            }
        }, Blocks.SUGAR_CANE);
        cyp1.register((bvt, bgz, ew, integer) -> 14731036, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        final int integer5;
        final int integer6;
        final int integer7;
        final int integer8;
        cyp1.register((bvt, bgz, ew, integer) -> {
            integer5 = bvt.<Integer>getValue((Property<Integer>)StemBlock.AGE);
            integer6 = integer5 * 32;
            integer7 = 255 - integer5 * 8;
            integer8 = integer5 * 4;
            return integer6 << 16 | integer7 << 8 | integer8;
        }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        cyp1.addColoringState(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        cyp1.register((bvt, bgz, ew, integer) -> {
            if (bgz == null || ew == null) {
                return 7455580;
            }
            else {
                return 2129968;
            }
        }, Blocks.LILY_PAD);
        return cyp1;
    }
    
    public int getColor(final BlockState bvt, final Level bhr, final BlockPos ew) {
        final BlockColor cyo5 = this.blockColors.byId(Registry.BLOCK.getId(bvt.getBlock()));
        if (cyo5 != null) {
            return cyo5.getColor(bvt, null, null, 0);
        }
        final MaterialColor clp6 = bvt.getMapColor(bhr, ew);
        return (clp6 != null) ? clp6.col : -1;
    }
    
    public int getColor(final BlockState bvt, @Nullable final BlockAndBiomeGetter bgz, @Nullable final BlockPos ew, final int integer) {
        final BlockColor cyo6 = this.blockColors.byId(Registry.BLOCK.getId(bvt.getBlock()));
        return (cyo6 == null) ? -1 : cyo6.getColor(bvt, bgz, ew, integer);
    }
    
    public void register(final BlockColor cyo, final Block... arr) {
        for (final Block bmv7 : arr) {
            this.blockColors.addMapping(cyo, Registry.BLOCK.getId(bmv7));
        }
    }
    
    private void addColoringStates(final Set<Property<?>> set, final Block... arr) {
        for (final Block bmv7 : arr) {
            this.coloringStates.put(bmv7, set);
        }
    }
    
    private void addColoringState(final Property<?> bww, final Block... arr) {
        this.addColoringStates((Set<Property<?>>)ImmutableSet.of(bww), arr);
    }
    
    public Set<Property<?>> getColoringProperties(final Block bmv) {
        return (Set<Property<?>>)this.coloringStates.getOrDefault(bmv, ImmutableSet.of());
    }
}
