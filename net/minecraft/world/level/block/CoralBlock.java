package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CoralBlock extends Block {
    private final Block deadBlock;
    
    public CoralBlock(final Block bmv, final Properties c) {
        super(c);
        this.deadBlock = bmv;
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!this.scanForWater(bhr, ew)) {
            bhr.setBlock(ew, this.deadBlock.defaultBlockState(), 2);
        }
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!this.scanForWater(bhs, ew5)) {
            bhs.getBlockTicks().scheduleTick(ew5, this, 60 + bhs.getRandom().nextInt(40));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    protected boolean scanForWater(final BlockGetter bhb, final BlockPos ew) {
        for (final Direction fb7 : Direction.values()) {
            final FluidState clk8 = bhb.getFluidState(ew.relative(fb7));
            if (clk8.is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        if (!this.scanForWater(ban.getLevel(), ban.getClickedPos())) {
            ban.getLevel().getBlockTicks().scheduleTick(ban.getClickedPos(), this, 60 + ban.getLevel().getRandom().nextInt(40));
        }
        return this.defaultBlockState();
    }
}
