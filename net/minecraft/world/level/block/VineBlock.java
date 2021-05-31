package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.stream.Collector;
import net.minecraft.Util;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockPlaceContext;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import java.util.Iterator;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class VineBlock extends Block {
    public static final BooleanProperty UP;
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
    protected static final VoxelShape UP_AABB;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape NORTH_AABB;
    
    public VineBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)VineBlock.UP, false)).setValue((Property<Comparable>)VineBlock.NORTH, false)).setValue((Property<Comparable>)VineBlock.EAST, false)).setValue((Property<Comparable>)VineBlock.SOUTH, false)).<Comparable, Boolean>setValue((Property<Comparable>)VineBlock.WEST, false));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        VoxelShape ctc6 = Shapes.empty();
        if (bvt.<Boolean>getValue((Property<Boolean>)VineBlock.UP)) {
            ctc6 = Shapes.or(ctc6, VineBlock.UP_AABB);
        }
        if (bvt.<Boolean>getValue((Property<Boolean>)VineBlock.NORTH)) {
            ctc6 = Shapes.or(ctc6, VineBlock.SOUTH_AABB);
        }
        if (bvt.<Boolean>getValue((Property<Boolean>)VineBlock.EAST)) {
            ctc6 = Shapes.or(ctc6, VineBlock.WEST_AABB);
        }
        if (bvt.<Boolean>getValue((Property<Boolean>)VineBlock.SOUTH)) {
            ctc6 = Shapes.or(ctc6, VineBlock.NORTH_AABB);
        }
        if (bvt.<Boolean>getValue((Property<Boolean>)VineBlock.WEST)) {
            ctc6 = Shapes.or(ctc6, VineBlock.EAST_AABB);
        }
        return ctc6;
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return this.hasFaces(this.getUpdatedState(bvt, bhu, ew));
    }
    
    private boolean hasFaces(final BlockState bvt) {
        return this.countFaces(bvt) > 0;
    }
    
    private int countFaces(final BlockState bvt) {
        int integer3 = 0;
        for (final BooleanProperty bwl5 : VineBlock.PROPERTY_BY_DIRECTION.values()) {
            if (bvt.<Boolean>getValue((Property<Boolean>)bwl5)) {
                ++integer3;
            }
        }
        return integer3;
    }
    
    private boolean canSupportAtFace(final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (fb == Direction.DOWN) {
            return false;
        }
        final BlockPos ew2 = ew.relative(fb);
        if (isAcceptableNeighbour(bhb, ew2, fb)) {
            return true;
        }
        if (fb.getAxis() != Direction.Axis.Y) {
            final BooleanProperty bwl6 = (BooleanProperty)VineBlock.PROPERTY_BY_DIRECTION.get(fb);
            final BlockState bvt7 = bhb.getBlockState(ew.above());
            return bvt7.getBlock() == this && bvt7.<Boolean>getValue((Property<Boolean>)bwl6);
        }
        return false;
    }
    
    public static boolean isAcceptableNeighbour(final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        final BlockState bvt4 = bhb.getBlockState(ew);
        return Block.isFaceFull(bvt4.getCollisionShape(bhb, ew), fb.getOpposite());
    }
    
    private BlockState getUpdatedState(BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        final BlockPos ew2 = ew.above();
        if (bvt.<Boolean>getValue((Property<Boolean>)VineBlock.UP)) {
            bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)VineBlock.UP, isAcceptableNeighbour(bhb, ew2, Direction.DOWN));
        }
        BlockState bvt2 = null;
        for (final Direction fb8 : Direction.Plane.HORIZONTAL) {
            final BooleanProperty bwl9 = getPropertyForFace(fb8);
            if (bvt.<Boolean>getValue((Property<Boolean>)bwl9)) {
                boolean boolean10 = this.canSupportAtFace(bhb, ew, fb8);
                if (!boolean10) {
                    if (bvt2 == null) {
                        bvt2 = bhb.getBlockState(ew2);
                    }
                    boolean10 = (bvt2.getBlock() == this && bvt2.<Boolean>getValue((Property<Boolean>)bwl9));
                }
                bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)bwl9, boolean10);
            }
        }
        return bvt;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.DOWN) {
            return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
        }
        final BlockState bvt4 = this.getUpdatedState(bvt1, bhs, ew5);
        if (!this.hasFaces(bvt4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return bvt4;
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.isClientSide) {
            return;
        }
        final BlockState bvt2 = this.getUpdatedState(bvt, bhr, ew);
        if (bvt2 != bvt) {
            if (this.hasFaces(bvt2)) {
                bhr.setBlock(ew, bvt2, 2);
            }
            else {
                Block.dropResources(bvt, bhr, ew);
                bhr.removeBlock(ew, false);
            }
            return;
        }
        if (bhr.random.nextInt(4) != 0) {
            return;
        }
        final Direction fb7 = Direction.getRandomFace(random);
        final BlockPos ew2 = ew.above();
        if (!fb7.getAxis().isHorizontal() || bvt.<Boolean>getValue((Property<Boolean>)getPropertyForFace(fb7))) {
            if (fb7 == Direction.UP && ew.getY() < 255) {
                if (this.canSupportAtFace(bhr, ew, fb7)) {
                    bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)VineBlock.UP, true), 2);
                    return;
                }
                if (bhr.isEmptyBlock(ew2)) {
                    if (!this.canSpread(bhr, ew)) {
                        return;
                    }
                    BlockState bvt3 = bvt;
                    for (final Direction fb8 : Direction.Plane.HORIZONTAL) {
                        if (random.nextBoolean() || !isAcceptableNeighbour(bhr, ew2.relative(fb8), Direction.UP)) {
                            bvt3 = ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Boolean>setValue((Property<Comparable>)getPropertyForFace(fb8), false);
                        }
                    }
                    if (this.hasHorizontalConnection(bvt3)) {
                        bhr.setBlock(ew2, bvt3, 2);
                    }
                    return;
                }
            }
            if (ew.getY() > 0) {
                final BlockPos ew3 = ew.below();
                final BlockState bvt4 = bhr.getBlockState(ew3);
                if (bvt4.isAir() || bvt4.getBlock() == this) {
                    final BlockState bvt5 = bvt4.isAir() ? this.defaultBlockState() : bvt4;
                    final BlockState bvt6 = this.copyRandomFaces(bvt, bvt5, random);
                    if (bvt5 != bvt6 && this.hasHorizontalConnection(bvt6)) {
                        bhr.setBlock(ew3, bvt6, 2);
                    }
                }
            }
            return;
        }
        if (!this.canSpread(bhr, ew)) {
            return;
        }
        final BlockPos ew3 = ew.relative(fb7);
        final BlockState bvt4 = bhr.getBlockState(ew3);
        if (bvt4.isAir()) {
            final Direction fb8 = fb7.getClockWise();
            final Direction fb9 = fb7.getCounterClockWise();
            final boolean boolean13 = bvt.<Boolean>getValue((Property<Boolean>)getPropertyForFace(fb8));
            final boolean boolean14 = bvt.<Boolean>getValue((Property<Boolean>)getPropertyForFace(fb9));
            final BlockPos ew4 = ew3.relative(fb8);
            final BlockPos ew5 = ew3.relative(fb9);
            if (boolean13 && isAcceptableNeighbour(bhr, ew4, fb8)) {
                bhr.setBlock(ew3, ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)getPropertyForFace(fb8), true), 2);
            }
            else if (boolean14 && isAcceptableNeighbour(bhr, ew5, fb9)) {
                bhr.setBlock(ew3, ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)getPropertyForFace(fb9), true), 2);
            }
            else {
                final Direction fb10 = fb7.getOpposite();
                if (boolean13 && bhr.isEmptyBlock(ew4) && isAcceptableNeighbour(bhr, ew.relative(fb8), fb10)) {
                    bhr.setBlock(ew4, ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)getPropertyForFace(fb10), true), 2);
                }
                else if (boolean14 && bhr.isEmptyBlock(ew5) && isAcceptableNeighbour(bhr, ew.relative(fb9), fb10)) {
                    bhr.setBlock(ew5, ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)getPropertyForFace(fb10), true), 2);
                }
                else if (bhr.random.nextFloat() < 0.05 && isAcceptableNeighbour(bhr, ew3.above(), Direction.UP)) {
                    bhr.setBlock(ew3, ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)VineBlock.UP, true), 2);
                }
            }
        }
        else if (isAcceptableNeighbour(bhr, ew3, fb7)) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)getPropertyForFace(fb7), true), 2);
        }
    }
    
    private BlockState copyRandomFaces(final BlockState bvt1, BlockState bvt2, final Random random) {
        for (final Direction fb6 : Direction.Plane.HORIZONTAL) {
            if (random.nextBoolean()) {
                final BooleanProperty bwl7 = getPropertyForFace(fb6);
                if (!bvt1.<Boolean>getValue((Property<Boolean>)bwl7)) {
                    continue;
                }
                bvt2 = ((AbstractStateHolder<O, BlockState>)bvt2).<Comparable, Boolean>setValue((Property<Comparable>)bwl7, true);
            }
        }
        return bvt2;
    }
    
    private boolean hasHorizontalConnection(final BlockState bvt) {
        return bvt.<Boolean>getValue((Property<Boolean>)VineBlock.NORTH) || bvt.<Boolean>getValue((Property<Boolean>)VineBlock.EAST) || bvt.<Boolean>getValue((Property<Boolean>)VineBlock.SOUTH) || bvt.<Boolean>getValue((Property<Boolean>)VineBlock.WEST);
    }
    
    private boolean canSpread(final BlockGetter bhb, final BlockPos ew) {
        final int integer4 = 4;
        final Iterable<BlockPos> iterable5 = BlockPos.betweenClosed(ew.getX() - 4, ew.getY() - 1, ew.getZ() - 4, ew.getX() + 4, ew.getY() + 1, ew.getZ() + 4);
        int integer5 = 5;
        for (final BlockPos ew2 : iterable5) {
            if (bhb.getBlockState(ew2).getBlock() == this && --integer5 <= 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean canBeReplaced(final BlockState bvt, final BlockPlaceContext ban) {
        final BlockState bvt2 = ban.getLevel().getBlockState(ban.getClickedPos());
        if (bvt2.getBlock() == this) {
            return this.countFaces(bvt2) < VineBlock.PROPERTY_BY_DIRECTION.size();
        }
        return super.canBeReplaced(bvt, ban);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockState bvt3 = ban.getLevel().getBlockState(ban.getClickedPos());
        final boolean boolean4 = bvt3.getBlock() == this;
        final BlockState bvt4 = boolean4 ? bvt3 : this.defaultBlockState();
        for (final Direction fb9 : ban.getNearestLookingDirections()) {
            if (fb9 != Direction.DOWN) {
                final BooleanProperty bwl10 = getPropertyForFace(fb9);
                final boolean boolean5 = boolean4 && bvt3.<Boolean>getValue((Property<Boolean>)bwl10);
                if (!boolean5 && this.canSupportAtFace(ban.getLevel(), ban.getClickedPos(), fb9)) {
                    return ((AbstractStateHolder<O, BlockState>)bvt4).<Comparable, Boolean>setValue((Property<Comparable>)bwl10, true);
                }
            }
        }
        return boolean4 ? bvt4 : null;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(VineBlock.UP, VineBlock.NORTH, VineBlock.EAST, VineBlock.SOUTH, VineBlock.WEST);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        switch (brg) {
            case CLOCKWISE_180: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)VineBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.SOUTH))).setValue((Property<Comparable>)VineBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.WEST))).setValue((Property<Comparable>)VineBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.NORTH))).<Comparable, Comparable>setValue((Property<Comparable>)VineBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)VineBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.EAST))).setValue((Property<Comparable>)VineBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.SOUTH))).setValue((Property<Comparable>)VineBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.WEST))).<Comparable, Comparable>setValue((Property<Comparable>)VineBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.NORTH));
            }
            case CLOCKWISE_90: {
                return (((((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)VineBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.WEST))).setValue((Property<Comparable>)VineBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.NORTH))).setValue((Property<Comparable>)VineBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.EAST))).<Comparable, Comparable>setValue((Property<Comparable>)VineBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.SOUTH));
            }
            default: {
                return bvt;
            }
        }
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        switch (bqg) {
            case LEFT_RIGHT: {
                return (((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)VineBlock.NORTH, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.SOUTH))).<Comparable, Comparable>setValue((Property<Comparable>)VineBlock.SOUTH, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.NORTH));
            }
            case FRONT_BACK: {
                return (((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)VineBlock.EAST, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.WEST))).<Comparable, Comparable>setValue((Property<Comparable>)VineBlock.WEST, (Comparable)bvt.<V>getValue((Property<V>)VineBlock.EAST));
            }
            default: {
                return super.mirror(bvt, bqg);
            }
        }
    }
    
    public static BooleanProperty getPropertyForFace(final Direction fb) {
        return (BooleanProperty)VineBlock.PROPERTY_BY_DIRECTION.get(fb);
    }
    
    static {
        UP = PipeBlock.UP;
        NORTH = PipeBlock.NORTH;
        EAST = PipeBlock.EAST;
        SOUTH = PipeBlock.SOUTH;
        WEST = PipeBlock.WEST;
        PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect((Collector)Util.toMap());
        UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
        EAST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
        WEST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
        NORTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    }
}
