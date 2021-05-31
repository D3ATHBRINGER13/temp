package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class WallTorchBlock extends TorchBlock {
    public static final DirectionProperty FACING;
    private static final Map<Direction, VoxelShape> AABBS;
    
    protected WallTorchBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Direction>setValue((Property<Comparable>)WallTorchBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return getShape(bvt);
    }
    
    public static VoxelShape getShape(final BlockState bvt) {
        return (VoxelShape)WallTorchBlock.AABBS.get(bvt.getValue((Property<Object>)WallTorchBlock.FACING));
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)WallTorchBlock.FACING);
        final BlockPos ew2 = ew.relative(fb5.getOpposite());
        final BlockState bvt2 = bhu.getBlockState(ew2);
        return bvt2.isFaceSturdy(bhu, ew2, fb5);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        BlockState bvt3 = this.defaultBlockState();
        final LevelReader bhu4 = ban.getLevel();
        final BlockPos ew5 = ban.getClickedPos();
        final Direction[] nearestLookingDirections;
        final Direction[] arr6 = nearestLookingDirections = ban.getNearestLookingDirections();
        for (final Direction fb10 : nearestLookingDirections) {
            if (fb10.getAxis().isHorizontal()) {
                final Direction fb11 = fb10.getOpposite();
                bvt3 = ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Direction>setValue((Property<Comparable>)WallTorchBlock.FACING, fb11);
                if (bvt3.canSurvive(bhu4, ew5)) {
                    return bvt3;
                }
            }
        }
        return null;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb.getOpposite() == bvt1.<Comparable>getValue((Property<Comparable>)WallTorchBlock.FACING) && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return bvt1;
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final Direction fb6 = bvt.<Direction>getValue((Property<Direction>)WallTorchBlock.FACING);
        final double double7 = ew.getX() + 0.5;
        final double double8 = ew.getY() + 0.7;
        final double double9 = ew.getZ() + 0.5;
        final double double10 = 0.22;
        final double double11 = 0.27;
        final Direction fb7 = fb6.getOpposite();
        bhr.addParticle(ParticleTypes.SMOKE, double7 + 0.27 * fb7.getStepX(), double8 + 0.22, double9 + 0.27 * fb7.getStepZ(), 0.0, 0.0, 0.0);
        bhr.addParticle(ParticleTypes.FLAME, double7 + 0.27 * fb7.getStepX(), double8 + 0.22, double9 + 0.27 * fb7.getStepZ(), 0.0, 0.0, 0.0);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)WallTorchBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)WallTorchBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)WallTorchBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(WallTorchBlock.FACING);
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        AABBS = (Map)Maps.newEnumMap((Map)ImmutableMap.of(Direction.NORTH, Block.box(5.5, 3.0, 11.0, 10.5, 13.0, 16.0), Direction.SOUTH, Block.box(5.5, 3.0, 0.0, 10.5, 13.0, 5.0), Direction.WEST, Block.box(11.0, 3.0, 5.5, 16.0, 13.0, 10.5), Direction.EAST, Block.box(0.0, 3.0, 5.5, 5.0, 13.0, 10.5)));
    }
}
