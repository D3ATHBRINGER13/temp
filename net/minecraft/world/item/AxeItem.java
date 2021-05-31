package net.minecraft.world.item;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.function.Consumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import java.util.Set;

public class AxeItem extends DiggerItem {
    private static final Set<Block> DIGGABLES;
    protected static final Map<Block, Block> STRIPABLES;
    
    protected AxeItem(final Tier bdn, final float float2, final float float3, final Properties a) {
        super(float2, float3, bdn, AxeItem.DIGGABLES, a);
    }
    
    @Override
    public float getDestroySpeed(final ItemStack bcj, final BlockState bvt) {
        final Material clo4 = bvt.getMaterial();
        if (clo4 == Material.WOOD || clo4 == Material.PLANT || clo4 == Material.REPLACEABLE_PLANT || clo4 == Material.BAMBOO) {
            return this.speed;
        }
        return super.getDestroySpeed(bcj, bvt);
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        final BlockPos ew4 = bdu.getClickedPos();
        final BlockState bvt5 = bhr3.getBlockState(ew4);
        final Block bmv6 = (Block)AxeItem.STRIPABLES.get(bvt5.getBlock());
        if (bmv6 != null) {
            final Player awg7 = bdu.getPlayer();
            bhr3.playSound(awg7, ew4, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!bhr3.isClientSide) {
                bhr3.setBlock(ew4, ((AbstractStateHolder<O, BlockState>)bmv6.defaultBlockState()).<Direction.Axis, Comparable>setValue(RotatedPillarBlock.AXIS, (Comparable)bvt5.<V>getValue((Property<V>)RotatedPillarBlock.AXIS)), 11);
                if (awg7 != null) {
                    bdu.getItemInHand().<Player>hurtAndBreak(1, awg7, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(bdu.getHand())));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    
    static {
        DIGGABLES = (Set)Sets.newHashSet((Object[])new Block[] { Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.BOOKSHELF, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.CHEST, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.MELON, Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.OAK_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE });
        STRIPABLES = (Map)new ImmutableMap.Builder().put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).build();
    }
}
