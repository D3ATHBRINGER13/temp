package net.minecraft.world.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;

public class StandingAndWallBlockItem extends BlockItem {
    protected final Block wallBlock;
    
    public StandingAndWallBlockItem(final Block bmv1, final Block bmv2, final Properties a) {
        super(bmv1, a);
        this.wallBlock = bmv2;
    }
    
    @Nullable
    @Override
    protected BlockState getPlacementState(final BlockPlaceContext ban) {
        final BlockState bvt3 = this.wallBlock.getStateForPlacement(ban);
        BlockState bvt4 = null;
        final LevelReader bhu5 = ban.getLevel();
        final BlockPos ew6 = ban.getClickedPos();
        for (final Direction fb10 : ban.getNearestLookingDirections()) {
            if (fb10 != Direction.UP) {
                final BlockState bvt5 = (fb10 == Direction.DOWN) ? this.getBlock().getStateForPlacement(ban) : bvt3;
                if (bvt5 != null && bvt5.canSurvive(bhu5, ew6)) {
                    bvt4 = bvt5;
                    break;
                }
            }
        }
        return (bvt4 != null && bhu5.isUnobstructed(bvt4, ew6, CollisionContext.empty())) ? bvt4 : null;
    }
    
    @Override
    public void registerBlocks(final Map<Block, Item> map, final Item bce) {
        super.registerBlocks(map, bce);
        map.put(this.wallBlock, bce);
    }
}
