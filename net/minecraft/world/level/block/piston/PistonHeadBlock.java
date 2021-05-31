package net.minecraft.world.level.block.piston;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.DirectionalBlock;

public class PistonHeadBlock extends DirectionalBlock {
    public static final EnumProperty<PistonType> TYPE;
    public static final BooleanProperty SHORT;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape UP_AABB;
    protected static final VoxelShape DOWN_AABB;
    protected static final VoxelShape UP_ARM_AABB;
    protected static final VoxelShape DOWN_ARM_AABB;
    protected static final VoxelShape SOUTH_ARM_AABB;
    protected static final VoxelShape NORTH_ARM_AABB;
    protected static final VoxelShape EAST_ARM_AABB;
    protected static final VoxelShape WEST_ARM_AABB;
    protected static final VoxelShape SHORT_UP_ARM_AABB;
    protected static final VoxelShape SHORT_DOWN_ARM_AABB;
    protected static final VoxelShape SHORT_SOUTH_ARM_AABB;
    protected static final VoxelShape SHORT_NORTH_ARM_AABB;
    protected static final VoxelShape SHORT_EAST_ARM_AABB;
    protected static final VoxelShape SHORT_WEST_ARM_AABB;
    
    public PistonHeadBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)PistonHeadBlock.FACING, Direction.NORTH)).setValue(PistonHeadBlock.TYPE, PistonType.DEFAULT)).<Comparable, Boolean>setValue((Property<Comparable>)PistonHeadBlock.SHORT, false));
    }
    
    private VoxelShape getBaseShape(final BlockState bvt) {
        switch (bvt.<Direction>getValue((Property<Direction>)PistonHeadBlock.FACING)) {
            default: {
                return PistonHeadBlock.DOWN_AABB;
            }
            case UP: {
                return PistonHeadBlock.UP_AABB;
            }
            case NORTH: {
                return PistonHeadBlock.NORTH_AABB;
            }
            case SOUTH: {
                return PistonHeadBlock.SOUTH_AABB;
            }
            case WEST: {
                return PistonHeadBlock.WEST_AABB;
            }
            case EAST: {
                return PistonHeadBlock.EAST_AABB;
            }
        }
    }
    
    @Override
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return true;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return Shapes.or(this.getBaseShape(bvt), this.getArmShape(bvt));
    }
    
    private VoxelShape getArmShape(final BlockState bvt) {
        final boolean boolean3 = bvt.<Boolean>getValue((Property<Boolean>)PistonHeadBlock.SHORT);
        switch (bvt.<Direction>getValue((Property<Direction>)PistonHeadBlock.FACING)) {
            default: {
                return boolean3 ? PistonHeadBlock.SHORT_DOWN_ARM_AABB : PistonHeadBlock.DOWN_ARM_AABB;
            }
            case UP: {
                return boolean3 ? PistonHeadBlock.SHORT_UP_ARM_AABB : PistonHeadBlock.UP_ARM_AABB;
            }
            case NORTH: {
                return boolean3 ? PistonHeadBlock.SHORT_NORTH_ARM_AABB : PistonHeadBlock.NORTH_ARM_AABB;
            }
            case SOUTH: {
                return boolean3 ? PistonHeadBlock.SHORT_SOUTH_ARM_AABB : PistonHeadBlock.SOUTH_ARM_AABB;
            }
            case WEST: {
                return boolean3 ? PistonHeadBlock.SHORT_WEST_ARM_AABB : PistonHeadBlock.WEST_ARM_AABB;
            }
            case EAST: {
                return boolean3 ? PistonHeadBlock.SHORT_EAST_ARM_AABB : PistonHeadBlock.EAST_ARM_AABB;
            }
        }
    }
    
    @Override
    public void playerWillDestroy(final Level bhr, final BlockPos ew, final BlockState bvt, final Player awg) {
        if (!bhr.isClientSide && awg.abilities.instabuild) {
            final BlockPos ew2 = ew.relative(bvt.<Direction>getValue((Property<Direction>)PistonHeadBlock.FACING).getOpposite());
            final Block bmv7 = bhr.getBlockState(ew2).getBlock();
            if (bmv7 == Blocks.PISTON || bmv7 == Blocks.STICKY_PISTON) {
                bhr.removeBlock(ew2, false);
            }
        }
        super.playerWillDestroy(bhr, ew, bvt, awg);
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
        final Direction fb7 = bvt1.<Direction>getValue((Property<Direction>)PistonHeadBlock.FACING).getOpposite();
        ew = ew.relative(fb7);
        final BlockState bvt5 = bhr.getBlockState(ew);
        if ((bvt5.getBlock() == Blocks.PISTON || bvt5.getBlock() == Blocks.STICKY_PISTON) && bvt5.<Boolean>getValue((Property<Boolean>)PistonBaseBlock.EXTENDED)) {
            Block.dropResources(bvt5, bhr, ew);
            bhr.removeBlock(ew, false);
        }
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb.getOpposite() == bvt1.<Comparable>getValue((Property<Comparable>)PistonHeadBlock.FACING) && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final Block bmv5 = bhu.getBlockState(ew.relative(bvt.<Direction>getValue((Property<Direction>)PistonHeadBlock.FACING).getOpposite())).getBlock();
        return bmv5 == Blocks.PISTON || bmv5 == Blocks.STICKY_PISTON || bmv5 == Blocks.MOVING_PISTON;
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bvt.canSurvive(bhr, ew3)) {
            final BlockPos ew6 = ew3.relative(bvt.<Direction>getValue((Property<Direction>)PistonHeadBlock.FACING).getOpposite());
            bhr.getBlockState(ew6).neighborChanged(bhr, ew6, bmv, ew5, false);
        }
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return new ItemStack((bvt.<PistonType>getValue(PistonHeadBlock.TYPE) == PistonType.STICKY) ? Blocks.STICKY_PISTON : Blocks.PISTON);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)PistonHeadBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)PistonHeadBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)PistonHeadBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(PistonHeadBlock.FACING, PistonHeadBlock.TYPE, PistonHeadBlock.SHORT);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        TYPE = BlockStateProperties.PISTON_TYPE;
        SHORT = BlockStateProperties.SHORT;
        EAST_AABB = Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        WEST_AABB = Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
        SOUTH_AABB = Block.box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
        NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
        UP_AABB = Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
        DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
        UP_ARM_AABB = Block.box(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
        DOWN_ARM_AABB = Block.box(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
        SOUTH_ARM_AABB = Block.box(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
        NORTH_ARM_AABB = Block.box(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
        EAST_ARM_AABB = Block.box(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
        WEST_ARM_AABB = Block.box(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
        SHORT_UP_ARM_AABB = Block.box(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
        SHORT_DOWN_ARM_AABB = Block.box(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
        SHORT_SOUTH_ARM_AABB = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
        SHORT_NORTH_ARM_AABB = Block.box(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
        SHORT_EAST_ARM_AABB = Block.box(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
        SHORT_WEST_ARM_AABB = Block.box(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    }
}
