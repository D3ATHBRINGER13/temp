package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Material;
import java.util.Iterator;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class CactusBlock extends Block {
    public static final IntegerProperty AGE;
    protected static final VoxelShape COLLISION_SHAPE;
    protected static final VoxelShape OUTLINE_SHAPE;
    
    protected CactusBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)CactusBlock.AGE, 0));
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.canSurvive(bhr, ew)) {
            bhr.destroyBlock(ew, true);
            return;
        }
        final BlockPos ew2 = ew.above();
        if (!bhr.isEmptyBlock(ew2)) {
            return;
        }
        int integer7;
        for (integer7 = 1; bhr.getBlockState(ew.below(integer7)).getBlock() == this; ++integer7) {}
        if (integer7 >= 3) {
            return;
        }
        final int integer8 = bvt.<Integer>getValue((Property<Integer>)CactusBlock.AGE);
        if (integer8 == 15) {
            bhr.setBlockAndUpdate(ew2, this.defaultBlockState());
            final BlockState bvt2 = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)CactusBlock.AGE, 0);
            bhr.setBlock(ew, bvt2, 4);
            bvt2.neighborChanged(bhr, ew2, this, ew, false);
        }
        else {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)CactusBlock.AGE, integer8 + 1), 4);
        }
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return CactusBlock.COLLISION_SHAPE;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return CactusBlock.OUTLINE_SHAPE;
    }
    
    @Override
    public boolean canOcclude(final BlockState bvt) {
        return true;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bvt1.canSurvive(bhs, ew5)) {
            bhs.getBlockTicks().scheduleTick(ew5, this, 1);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        for (final Direction fb6 : Direction.Plane.HORIZONTAL) {
            final BlockState bvt2 = bhu.getBlockState(ew.relative(fb6));
            final Material clo8 = bvt2.getMaterial();
            if (clo8.isSolid() || bhu.getFluidState(ew.relative(fb6)).is(FluidTags.LAVA)) {
                return false;
            }
        }
        final Block bmv5 = bhu.getBlockState(ew.below()).getBlock();
        return (bmv5 == Blocks.CACTUS || bmv5 == Blocks.SAND || bmv5 == Blocks.RED_SAND) && !bhu.getBlockState(ew.above()).getMaterial().isLiquid();
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        aio.hurt(DamageSource.CACTUS, 1.0f);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(CactusBlock.AGE);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        AGE = BlockStateProperties.AGE_15;
        COLLISION_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
        OUTLINE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    }
}
