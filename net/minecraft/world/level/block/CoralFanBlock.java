package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CoralFanBlock extends BaseCoralFanBlock {
    private final Block deadBlock;
    
    protected CoralFanBlock(final Block bmv, final Properties c) {
        super(c);
        this.deadBlock = bmv;
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        this.tryScheduleDieTick(bvt1, bhr, ew);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!BaseCoralPlantTypeBlock.scanForWater(bvt, bhr, ew)) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)this.deadBlock.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)CoralFanBlock.WATERLOGGED, false), 2);
        }
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.DOWN && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        this.tryScheduleDieTick(bvt1, bhs, ew5);
        if (bvt1.<Boolean>getValue((Property<Boolean>)CoralFanBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
}
