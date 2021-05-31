package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.Iterator;
import net.minecraft.core.Direction;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.RailShape;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class RailState {
    private final Level level;
    private final BlockPos pos;
    private final BaseRailBlock block;
    private BlockState state;
    private final boolean isStraight;
    private final List<BlockPos> connections;
    
    public RailState(final Level bhr, final BlockPos ew, final BlockState bvt) {
        this.connections = (List<BlockPos>)Lists.newArrayList();
        this.level = bhr;
        this.pos = ew;
        this.state = bvt;
        this.block = (BaseRailBlock)bvt.getBlock();
        final RailShape bwx5 = bvt.<RailShape>getValue(this.block.getShapeProperty());
        this.isStraight = this.block.isStraight();
        this.updateConnections(bwx5);
    }
    
    public List<BlockPos> getConnections() {
        return this.connections;
    }
    
    private void updateConnections(final RailShape bwx) {
        this.connections.clear();
        switch (bwx) {
            case NORTH_SOUTH: {
                this.connections.add(this.pos.north());
                this.connections.add(this.pos.south());
                break;
            }
            case EAST_WEST: {
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.east());
                break;
            }
            case ASCENDING_EAST: {
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.east().above());
                break;
            }
            case ASCENDING_WEST: {
                this.connections.add(this.pos.west().above());
                this.connections.add(this.pos.east());
                break;
            }
            case ASCENDING_NORTH: {
                this.connections.add(this.pos.north().above());
                this.connections.add(this.pos.south());
                break;
            }
            case ASCENDING_SOUTH: {
                this.connections.add(this.pos.north());
                this.connections.add(this.pos.south().above());
                break;
            }
            case SOUTH_EAST: {
                this.connections.add(this.pos.east());
                this.connections.add(this.pos.south());
                break;
            }
            case SOUTH_WEST: {
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.south());
                break;
            }
            case NORTH_WEST: {
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.north());
                break;
            }
            case NORTH_EAST: {
                this.connections.add(this.pos.east());
                this.connections.add(this.pos.north());
                break;
            }
        }
    }
    
    private void removeSoftConnections() {
        for (int integer2 = 0; integer2 < this.connections.size(); ++integer2) {
            final RailState bqx3 = this.getRail((BlockPos)this.connections.get(integer2));
            if (bqx3 == null || !bqx3.connectsTo(this)) {
                this.connections.remove(integer2--);
            }
            else {
                this.connections.set(integer2, bqx3.pos);
            }
        }
    }
    
    private boolean hasRail(final BlockPos ew) {
        return BaseRailBlock.isRail(this.level, ew) || BaseRailBlock.isRail(this.level, ew.above()) || BaseRailBlock.isRail(this.level, ew.below());
    }
    
    @Nullable
    private RailState getRail(final BlockPos ew) {
        BlockPos ew2 = ew;
        BlockState bvt4 = this.level.getBlockState(ew2);
        if (BaseRailBlock.isRail(bvt4)) {
            return new RailState(this.level, ew2, bvt4);
        }
        ew2 = ew.above();
        bvt4 = this.level.getBlockState(ew2);
        if (BaseRailBlock.isRail(bvt4)) {
            return new RailState(this.level, ew2, bvt4);
        }
        ew2 = ew.below();
        bvt4 = this.level.getBlockState(ew2);
        if (BaseRailBlock.isRail(bvt4)) {
            return new RailState(this.level, ew2, bvt4);
        }
        return null;
    }
    
    private boolean connectsTo(final RailState bqx) {
        return this.hasConnection(bqx.pos);
    }
    
    private boolean hasConnection(final BlockPos ew) {
        for (int integer3 = 0; integer3 < this.connections.size(); ++integer3) {
            final BlockPos ew2 = (BlockPos)this.connections.get(integer3);
            if (ew2.getX() == ew.getX() && ew2.getZ() == ew.getZ()) {
                return true;
            }
        }
        return false;
    }
    
    protected int countPotentialConnections() {
        int integer2 = 0;
        for (final Direction fb4 : Direction.Plane.HORIZONTAL) {
            if (this.hasRail(this.pos.relative(fb4))) {
                ++integer2;
            }
        }
        return integer2;
    }
    
    private boolean canConnectTo(final RailState bqx) {
        return this.connectsTo(bqx) || this.connections.size() != 2;
    }
    
    private void connectTo(final RailState bqx) {
        this.connections.add(bqx.pos);
        final BlockPos ew3 = this.pos.north();
        final BlockPos ew4 = this.pos.south();
        final BlockPos ew5 = this.pos.west();
        final BlockPos ew6 = this.pos.east();
        final boolean boolean7 = this.hasConnection(ew3);
        final boolean boolean8 = this.hasConnection(ew4);
        final boolean boolean9 = this.hasConnection(ew5);
        final boolean boolean10 = this.hasConnection(ew6);
        RailShape bwx11 = null;
        if (boolean7 || boolean8) {
            bwx11 = RailShape.NORTH_SOUTH;
        }
        if (boolean9 || boolean10) {
            bwx11 = RailShape.EAST_WEST;
        }
        if (!this.isStraight) {
            if (boolean8 && boolean10 && !boolean7 && !boolean9) {
                bwx11 = RailShape.SOUTH_EAST;
            }
            if (boolean8 && boolean9 && !boolean7 && !boolean10) {
                bwx11 = RailShape.SOUTH_WEST;
            }
            if (boolean7 && boolean9 && !boolean8 && !boolean10) {
                bwx11 = RailShape.NORTH_WEST;
            }
            if (boolean7 && boolean10 && !boolean8 && !boolean9) {
                bwx11 = RailShape.NORTH_EAST;
            }
        }
        if (bwx11 == RailShape.NORTH_SOUTH) {
            if (BaseRailBlock.isRail(this.level, ew3.above())) {
                bwx11 = RailShape.ASCENDING_NORTH;
            }
            if (BaseRailBlock.isRail(this.level, ew4.above())) {
                bwx11 = RailShape.ASCENDING_SOUTH;
            }
        }
        if (bwx11 == RailShape.EAST_WEST) {
            if (BaseRailBlock.isRail(this.level, ew6.above())) {
                bwx11 = RailShape.ASCENDING_EAST;
            }
            if (BaseRailBlock.isRail(this.level, ew5.above())) {
                bwx11 = RailShape.ASCENDING_WEST;
            }
        }
        if (bwx11 == null) {
            bwx11 = RailShape.NORTH_SOUTH;
        }
        this.state = ((AbstractStateHolder<O, BlockState>)this.state).<RailShape, RailShape>setValue(this.block.getShapeProperty(), bwx11);
        this.level.setBlock(this.pos, this.state, 3);
    }
    
    private boolean hasNeighborRail(final BlockPos ew) {
        final RailState bqx3 = this.getRail(ew);
        if (bqx3 == null) {
            return false;
        }
        bqx3.removeSoftConnections();
        return bqx3.canConnectTo(this);
    }
    
    public RailState place(final boolean boolean1, final boolean boolean2) {
        final BlockPos ew4 = this.pos.north();
        final BlockPos ew5 = this.pos.south();
        final BlockPos ew6 = this.pos.west();
        final BlockPos ew7 = this.pos.east();
        final boolean boolean3 = this.hasNeighborRail(ew4);
        final boolean boolean4 = this.hasNeighborRail(ew5);
        final boolean boolean5 = this.hasNeighborRail(ew6);
        final boolean boolean6 = this.hasNeighborRail(ew7);
        RailShape bwx12 = null;
        if ((boolean3 || boolean4) && !boolean5 && !boolean6) {
            bwx12 = RailShape.NORTH_SOUTH;
        }
        if ((boolean5 || boolean6) && !boolean3 && !boolean4) {
            bwx12 = RailShape.EAST_WEST;
        }
        if (!this.isStraight) {
            if (boolean4 && boolean6 && !boolean3 && !boolean5) {
                bwx12 = RailShape.SOUTH_EAST;
            }
            if (boolean4 && boolean5 && !boolean3 && !boolean6) {
                bwx12 = RailShape.SOUTH_WEST;
            }
            if (boolean3 && boolean5 && !boolean4 && !boolean6) {
                bwx12 = RailShape.NORTH_WEST;
            }
            if (boolean3 && boolean6 && !boolean4 && !boolean5) {
                bwx12 = RailShape.NORTH_EAST;
            }
        }
        if (bwx12 == null) {
            if (boolean3 || boolean4) {
                bwx12 = RailShape.NORTH_SOUTH;
            }
            if (boolean5 || boolean6) {
                bwx12 = RailShape.EAST_WEST;
            }
            if (!this.isStraight) {
                if (boolean1) {
                    if (boolean4 && boolean6) {
                        bwx12 = RailShape.SOUTH_EAST;
                    }
                    if (boolean5 && boolean4) {
                        bwx12 = RailShape.SOUTH_WEST;
                    }
                    if (boolean6 && boolean3) {
                        bwx12 = RailShape.NORTH_EAST;
                    }
                    if (boolean3 && boolean5) {
                        bwx12 = RailShape.NORTH_WEST;
                    }
                }
                else {
                    if (boolean3 && boolean5) {
                        bwx12 = RailShape.NORTH_WEST;
                    }
                    if (boolean6 && boolean3) {
                        bwx12 = RailShape.NORTH_EAST;
                    }
                    if (boolean5 && boolean4) {
                        bwx12 = RailShape.SOUTH_WEST;
                    }
                    if (boolean4 && boolean6) {
                        bwx12 = RailShape.SOUTH_EAST;
                    }
                }
            }
        }
        if (bwx12 == RailShape.NORTH_SOUTH) {
            if (BaseRailBlock.isRail(this.level, ew4.above())) {
                bwx12 = RailShape.ASCENDING_NORTH;
            }
            if (BaseRailBlock.isRail(this.level, ew5.above())) {
                bwx12 = RailShape.ASCENDING_SOUTH;
            }
        }
        if (bwx12 == RailShape.EAST_WEST) {
            if (BaseRailBlock.isRail(this.level, ew7.above())) {
                bwx12 = RailShape.ASCENDING_EAST;
            }
            if (BaseRailBlock.isRail(this.level, ew6.above())) {
                bwx12 = RailShape.ASCENDING_WEST;
            }
        }
        if (bwx12 == null) {
            bwx12 = RailShape.NORTH_SOUTH;
        }
        this.updateConnections(bwx12);
        this.state = ((AbstractStateHolder<O, BlockState>)this.state).<RailShape, RailShape>setValue(this.block.getShapeProperty(), bwx12);
        if (boolean2 || this.level.getBlockState(this.pos) != this.state) {
            this.level.setBlock(this.pos, this.state, 3);
            for (int integer13 = 0; integer13 < this.connections.size(); ++integer13) {
                final RailState bqx14 = this.getRail((BlockPos)this.connections.get(integer13));
                if (bqx14 != null) {
                    bqx14.removeSoftConnections();
                    if (bqx14.canConnectTo(this)) {
                        bqx14.connectTo(this);
                    }
                }
            }
        }
        return this;
    }
    
    public BlockState getState() {
        return this.state;
    }
}
