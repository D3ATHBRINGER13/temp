package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseRailBlock extends Block {
    protected static final VoxelShape FLAT_AABB;
    protected static final VoxelShape HALF_BLOCK_AABB;
    private final boolean isStraight;
    
    public static boolean isRail(final Level bhr, final BlockPos ew) {
        return isRail(bhr.getBlockState(ew));
    }
    
    public static boolean isRail(final BlockState bvt) {
        return bvt.is(BlockTags.RAILS);
    }
    
    protected BaseRailBlock(final boolean boolean1, final Properties c) {
        super(c);
        this.isStraight = boolean1;
    }
    
    public boolean isStraight() {
        return this.isStraight;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final RailShape bwx6 = (bvt.getBlock() == this) ? bvt.<RailShape>getValue(this.getShapeProperty()) : null;
        if (bwx6 != null && bwx6.isAscending()) {
            return BaseRailBlock.HALF_BLOCK_AABB;
        }
        return BaseRailBlock.FLAT_AABB;
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return Block.canSupportRigidBlock(bhu, ew.below());
    }
    
    @Override
    public void onPlace(BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt4.getBlock() == bvt1.getBlock()) {
            return;
        }
        if (!bhr.isClientSide) {
            bvt1 = this.updateDir(bhr, ew, bvt1, true);
            if (this.isStraight) {
                bvt1.neighborChanged(bhr, ew, this, ew, boolean5);
            }
        }
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bhr.isClientSide) {
            return;
        }
        final RailShape bwx8 = bvt.<RailShape>getValue(this.getShapeProperty());
        boolean boolean7 = false;
        final BlockPos ew6 = ew3.below();
        if (!Block.canSupportRigidBlock(bhr, ew6)) {
            boolean7 = true;
        }
        final BlockPos ew7 = ew3.east();
        if (bwx8 == RailShape.ASCENDING_EAST && !Block.canSupportRigidBlock(bhr, ew7)) {
            boolean7 = true;
        }
        else {
            final BlockPos ew8 = ew3.west();
            if (bwx8 == RailShape.ASCENDING_WEST && !Block.canSupportRigidBlock(bhr, ew8)) {
                boolean7 = true;
            }
            else {
                final BlockPos ew9 = ew3.north();
                if (bwx8 == RailShape.ASCENDING_NORTH && !Block.canSupportRigidBlock(bhr, ew9)) {
                    boolean7 = true;
                }
                else {
                    final BlockPos ew10 = ew3.south();
                    if (bwx8 == RailShape.ASCENDING_SOUTH && !Block.canSupportRigidBlock(bhr, ew10)) {
                        boolean7 = true;
                    }
                }
            }
        }
        if (boolean7 && !bhr.isEmptyBlock(ew3)) {
            if (!boolean6) {
                Block.dropResources(bvt, bhr, ew3);
            }
            bhr.removeBlock(ew3, boolean6);
        }
        else {
            this.updateState(bvt, bhr, ew3, bmv);
        }
    }
    
    protected void updateState(final BlockState bvt, final Level bhr, final BlockPos ew, final Block bmv) {
    }
    
    protected BlockState updateDir(final Level bhr, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        if (bhr.isClientSide) {
            return bvt;
        }
        return new RailState(bhr, ew, bvt).place(bhr.hasNeighborSignal(ew), boolean4).getState();
    }
    
    @Override
    public PushReaction getPistonPushReaction(final BlockState bvt) {
        return PushReaction.NORMAL;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (boolean5) {
            return;
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
        if (bvt1.<RailShape>getValue(this.getShapeProperty()).isAscending()) {
            bhr.updateNeighborsAt(ew.above(), this);
        }
        if (this.isStraight) {
            bhr.updateNeighborsAt(ew, this);
            bhr.updateNeighborsAt(ew.below(), this);
        }
    }
    
    public abstract Property<RailShape> getShapeProperty();
    
    static {
        FLAT_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        HALF_BLOCK_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    }
}
