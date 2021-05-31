package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.LevelAccessor;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class FaceAttachedHorizontalDirectionalBlock extends HorizontalDirectionalBlock {
    public static final EnumProperty<AttachFace> FACE;
    
    protected FaceAttachedHorizontalDirectionalBlock(final Properties c) {
        super(c);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return canAttach(bhu, ew, getConnectedDirection(bvt).getOpposite());
    }
    
    public static boolean canAttach(final LevelReader bhu, final BlockPos ew, final Direction fb) {
        final BlockPos ew2 = ew.relative(fb);
        return bhu.getBlockState(ew2).isFaceSturdy(bhu, ew2, fb.getOpposite());
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        for (final Direction fb6 : ban.getNearestLookingDirections()) {
            BlockState bvt7;
            if (fb6.getAxis() == Direction.Axis.Y) {
                bvt7 = (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)FaceAttachedHorizontalDirectionalBlock.FACE, (fb6 == Direction.UP) ? AttachFace.CEILING : AttachFace.FLOOR)).<Comparable, Direction>setValue((Property<Comparable>)FaceAttachedHorizontalDirectionalBlock.FACING, ban.getHorizontalDirection());
            }
            else {
                bvt7 = (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue(FaceAttachedHorizontalDirectionalBlock.FACE, AttachFace.WALL)).<Comparable, Direction>setValue((Property<Comparable>)FaceAttachedHorizontalDirectionalBlock.FACING, fb6.getOpposite());
            }
            if (bvt7.canSurvive(ban.getLevel(), ban.getClickedPos())) {
                return bvt7;
            }
        }
        return null;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (getConnectedDirection(bvt1).getOpposite() == fb && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    protected static Direction getConnectedDirection(final BlockState bvt) {
        switch (bvt.<AttachFace>getValue(FaceAttachedHorizontalDirectionalBlock.FACE)) {
            case CEILING: {
                return Direction.DOWN;
            }
            case FLOOR: {
                return Direction.UP;
            }
            default: {
                return bvt.<Direction>getValue((Property<Direction>)FaceAttachedHorizontalDirectionalBlock.FACING);
            }
        }
    }
    
    static {
        FACE = BlockStateProperties.ATTACH_FACE;
    }
}
