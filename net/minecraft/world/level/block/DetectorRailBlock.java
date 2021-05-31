package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import javax.annotation.Nullable;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class DetectorRailBlock extends BaseRailBlock {
    public static final EnumProperty<RailShape> SHAPE;
    public static final BooleanProperty POWERED;
    
    public DetectorRailBlock(final Properties c) {
        super(true, c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)DetectorRailBlock.POWERED, false)).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_SOUTH));
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 20;
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        if (bhr.isClientSide) {
            return;
        }
        if (bvt.<Boolean>getValue((Property<Boolean>)DetectorRailBlock.POWERED)) {
            return;
        }
        this.checkPressed(bhr, ew, bvt);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (bhr.isClientSide || !bvt.<Boolean>getValue((Property<Boolean>)DetectorRailBlock.POWERED)) {
            return;
        }
        this.checkPressed(bhr, ew, bvt);
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return bvt.<Boolean>getValue((Property<Boolean>)DetectorRailBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)DetectorRailBlock.POWERED)) {
            return 0;
        }
        return (fb == Direction.UP) ? 15 : 0;
    }
    
    private void checkPressed(final Level bhr, final BlockPos ew, final BlockState bvt) {
        final boolean boolean5 = bvt.<Boolean>getValue((Property<Boolean>)DetectorRailBlock.POWERED);
        boolean boolean6 = false;
        final List<AbstractMinecart> list7 = this.<AbstractMinecart>getInteractingMinecartOfType(bhr, ew, AbstractMinecart.class, null);
        if (!list7.isEmpty()) {
            boolean6 = true;
        }
        if (boolean6 && !boolean5) {
            final BlockState bvt2 = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)DetectorRailBlock.POWERED, true);
            bhr.setBlock(ew, bvt2, 3);
            this.updatePowerToConnected(bhr, ew, bvt2, true);
            bhr.updateNeighborsAt(ew, this);
            bhr.updateNeighborsAt(ew.below(), this);
            bhr.setBlocksDirty(ew, bvt, bvt2);
        }
        if (!boolean6 && boolean5) {
            final BlockState bvt2 = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)DetectorRailBlock.POWERED, false);
            bhr.setBlock(ew, bvt2, 3);
            this.updatePowerToConnected(bhr, ew, bvt2, false);
            bhr.updateNeighborsAt(ew, this);
            bhr.updateNeighborsAt(ew.below(), this);
            bhr.setBlocksDirty(ew, bvt, bvt2);
        }
        if (boolean6) {
            bhr.getBlockTicks().scheduleTick(ew, this, this.getTickDelay(bhr));
        }
        bhr.updateNeighbourForOutputSignal(ew, this);
    }
    
    protected void updatePowerToConnected(final Level bhr, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        final RailState bqx6 = new RailState(bhr, ew, bvt);
        final List<BlockPos> list7 = bqx6.getConnections();
        for (final BlockPos ew2 : list7) {
            final BlockState bvt2 = bhr.getBlockState(ew2);
            bvt2.neighborChanged(bhr, ew2, bvt2.getBlock(), ew, false);
        }
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt4.getBlock() == bvt1.getBlock()) {
            return;
        }
        super.onPlace(bvt1, bhr, ew, bvt4, boolean5);
        this.checkPressed(bhr, ew, bvt1);
    }
    
    @Override
    public Property<RailShape> getShapeProperty() {
        return DetectorRailBlock.SHAPE;
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        if (bvt.<Boolean>getValue((Property<Boolean>)DetectorRailBlock.POWERED)) {
            final List<MinecartCommandBlock> list5 = this.<MinecartCommandBlock>getInteractingMinecartOfType(bhr, ew, MinecartCommandBlock.class, null);
            if (!list5.isEmpty()) {
                return ((MinecartCommandBlock)list5.get(0)).getCommandBlock().getSuccessCount();
            }
            final List<AbstractMinecart> list6 = this.<AbstractMinecart>getInteractingMinecartOfType(bhr, ew, AbstractMinecart.class, EntitySelector.CONTAINER_ENTITY_SELECTOR);
            if (!list6.isEmpty()) {
                return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)list6.get(0));
            }
        }
        return 0;
    }
    
    protected <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(final Level bhr, final BlockPos ew, final Class<T> class3, @Nullable final Predicate<Entity> predicate) {
        return bhr.<T>getEntitiesOfClass((java.lang.Class<? extends T>)class3, this.getSearchBB(ew), (java.util.function.Predicate<? super T>)predicate);
    }
    
    private AABB getSearchBB(final BlockPos ew) {
        final float float3 = 0.2f;
        return new AABB(ew.getX() + 0.2f, ew.getY(), ew.getZ() + 0.2f, ew.getX() + 1 - 0.2f, ew.getY() + 1 - 0.2f, ew.getZ() + 1 - 0.2f);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        Label_0424: {
            switch (brg) {
                case CLOCKWISE_180: {
                    switch (bvt.<RailShape>getValue(DetectorRailBlock.SHAPE)) {
                        case ASCENDING_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_WEST);
                        }
                        case ASCENDING_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_EAST);
                        }
                        case ASCENDING_NORTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_SOUTH);
                        }
                        case ASCENDING_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_NORTH);
                        }
                        case SOUTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_WEST);
                        }
                        case SOUTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_EAST);
                        }
                        case NORTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.SOUTH_EAST);
                        }
                        case NORTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.SOUTH_WEST);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
                case COUNTERCLOCKWISE_90: {
                    switch (bvt.<RailShape>getValue(DetectorRailBlock.SHAPE)) {
                        case NORTH_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.EAST_WEST);
                        }
                        case EAST_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_SOUTH);
                        }
                        case ASCENDING_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_NORTH);
                        }
                        case ASCENDING_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_SOUTH);
                        }
                        case ASCENDING_NORTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_WEST);
                        }
                        case ASCENDING_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_EAST);
                        }
                        case SOUTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_EAST);
                        }
                        case SOUTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.SOUTH_EAST);
                        }
                        case NORTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.SOUTH_WEST);
                        }
                        case NORTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_WEST);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
                case CLOCKWISE_90: {
                    switch (bvt.<RailShape>getValue(DetectorRailBlock.SHAPE)) {
                        case NORTH_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.EAST_WEST);
                        }
                        case EAST_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_SOUTH);
                        }
                        case ASCENDING_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_SOUTH);
                        }
                        case ASCENDING_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_NORTH);
                        }
                        case ASCENDING_NORTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_EAST);
                        }
                        case ASCENDING_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_WEST);
                        }
                        case SOUTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.SOUTH_WEST);
                        }
                        case SOUTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_WEST);
                        }
                        case NORTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_EAST);
                        }
                        case NORTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.SOUTH_EAST);
                        }
                        default: {
                            break Label_0424;
                        }
                    }
                    break;
                }
            }
        }
        return bvt;
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        final RailShape bwx4 = bvt.<RailShape>getValue(DetectorRailBlock.SHAPE);
        Label_0319: {
            switch (bqg) {
                case LEFT_RIGHT: {
                    switch (bwx4) {
                        case ASCENDING_NORTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_SOUTH);
                        }
                        case ASCENDING_SOUTH: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_NORTH);
                        }
                        case SOUTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_EAST);
                        }
                        case SOUTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_WEST);
                        }
                        case NORTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.SOUTH_WEST);
                        }
                        case NORTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.SOUTH_EAST);
                        }
                        default: {
                            break Label_0319;
                        }
                    }
                    break;
                }
                case FRONT_BACK: {
                    switch (bwx4) {
                        case ASCENDING_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_WEST);
                        }
                        case ASCENDING_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.ASCENDING_EAST);
                        }
                        case SOUTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.SOUTH_WEST);
                        }
                        case SOUTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.SOUTH_EAST);
                        }
                        case NORTH_WEST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_EAST);
                        }
                        case NORTH_EAST: {
                            return ((AbstractStateHolder<O, BlockState>)bvt).<RailShape, RailShape>setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_WEST);
                        }
                        default: {
                            break Label_0319;
                        }
                    }
                    break;
                }
            }
        }
        return super.mirror(bvt, bqg);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(DetectorRailBlock.SHAPE, DetectorRailBlock.POWERED);
    }
    
    static {
        SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
        POWERED = BlockStateProperties.POWERED;
    }
}
