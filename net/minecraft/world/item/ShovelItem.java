package net.minecraft.world.item;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.function.Consumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import java.util.Set;

public class ShovelItem extends DiggerItem {
    private static final Set<Block> DIGGABLES;
    protected static final Map<Block, BlockState> FLATTENABLES;
    
    public ShovelItem(final Tier bdn, final float float2, final float float3, final Properties a) {
        super(float2, float3, bdn, ShovelItem.DIGGABLES, a);
    }
    
    @Override
    public boolean canDestroySpecial(final BlockState bvt) {
        final Block bmv3 = bvt.getBlock();
        return bmv3 == Blocks.SNOW || bmv3 == Blocks.SNOW_BLOCK;
    }
    
    @Override
    public InteractionResult useOn(final UseOnContext bdu) {
        final Level bhr3 = bdu.getLevel();
        final BlockPos ew4 = bdu.getClickedPos();
        if (bdu.getClickedFace() != Direction.DOWN && bhr3.getBlockState(ew4.above()).isAir()) {
            final BlockState bvt5 = (BlockState)ShovelItem.FLATTENABLES.get(bhr3.getBlockState(ew4).getBlock());
            if (bvt5 != null) {
                final Player awg6 = bdu.getPlayer();
                bhr3.playSound(awg6, ew4, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (!bhr3.isClientSide) {
                    bhr3.setBlock(ew4, bvt5, 11);
                    if (awg6 != null) {
                        bdu.getItemInHand().<Player>hurtAndBreak(1, awg6, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(bdu.getHand())));
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
    
    static {
        DIGGABLES = (Set)Sets.newHashSet((Object[])new Block[] { Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER });
        FLATTENABLES = (Map)Maps.newHashMap((Map)ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH.defaultBlockState()));
    }
}
