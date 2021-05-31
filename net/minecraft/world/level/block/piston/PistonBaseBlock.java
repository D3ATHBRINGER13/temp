package net.minecraft.world.level.block.piston;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import net.minecraft.world.level.LevelAccessor;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.DirectionalBlock;

public class PistonBaseBlock extends DirectionalBlock {
    public static final BooleanProperty EXTENDED;
    protected static final VoxelShape EAST_AABB;
    protected static final VoxelShape WEST_AABB;
    protected static final VoxelShape SOUTH_AABB;
    protected static final VoxelShape NORTH_AABB;
    protected static final VoxelShape UP_AABB;
    protected static final VoxelShape DOWN_AABB;
    private final boolean isSticky;
    
    public PistonBaseBlock(final boolean boolean1, final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)PistonBaseBlock.FACING, Direction.NORTH)).<Comparable, Boolean>setValue((Property<Comparable>)PistonBaseBlock.EXTENDED, false));
        this.isSticky = boolean1;
    }
    
    @Override
    public boolean isViewBlocking(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return !bvt.<Boolean>getValue((Property<Boolean>)PistonBaseBlock.EXTENDED);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)PistonBaseBlock.EXTENDED)) {
            return Shapes.block();
        }
        switch (bvt.<Direction>getValue((Property<Direction>)PistonBaseBlock.FACING)) {
            case DOWN: {
                return PistonBaseBlock.DOWN_AABB;
            }
            default: {
                return PistonBaseBlock.UP_AABB;
            }
            case NORTH: {
                return PistonBaseBlock.NORTH_AABB;
            }
            case SOUTH: {
                return PistonBaseBlock.SOUTH_AABB;
            }
            case WEST: {
                return PistonBaseBlock.WEST_AABB;
            }
            case EAST: {
                return PistonBaseBlock.EAST_AABB;
            }
        }
    }
    
    @Override
    public boolean isRedstoneConductor(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return false;
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        if (!bhr.isClientSide) {
            this.checkIfExtend(bhr, ew, bvt);
        }
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (!bhr.isClientSide) {
            this.checkIfExtend(bhr, ew3, bvt);
        }
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt4.getBlock() == bvt1.getBlock()) {
            return;
        }
        if (!bhr.isClientSide && bhr.getBlockEntity(ew) == null) {
            this.checkIfExtend(bhr, ew, bvt1);
        }
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)PistonBaseBlock.FACING, ban.getNearestLookingDirection().getOpposite())).<Comparable, Boolean>setValue((Property<Comparable>)PistonBaseBlock.EXTENDED, false);
    }
    
    private void checkIfExtend(final Level bhr, final BlockPos ew, final BlockState bvt) {
        final Direction fb5 = bvt.<Direction>getValue((Property<Direction>)PistonBaseBlock.FACING);
        final boolean boolean6 = this.getNeighborSignal(bhr, ew, fb5);
        if (boolean6 && !bvt.<Boolean>getValue((Property<Boolean>)PistonBaseBlock.EXTENDED)) {
            if (new PistonStructureResolver(bhr, ew, fb5, true).resolve()) {
                bhr.blockEvent(ew, this, 0, fb5.get3DDataValue());
            }
        }
        else if (!boolean6 && bvt.<Boolean>getValue((Property<Boolean>)PistonBaseBlock.EXTENDED)) {
            final BlockPos ew2 = ew.relative(fb5, 2);
            final BlockState bvt2 = bhr.getBlockState(ew2);
            int integer9 = 1;
            if (bvt2.getBlock() == Blocks.MOVING_PISTON && bvt2.<Comparable>getValue((Property<Comparable>)PistonBaseBlock.FACING) == fb5) {
                final BlockEntity btw10 = bhr.getBlockEntity(ew2);
                if (btw10 instanceof PistonMovingBlockEntity) {
                    final PistonMovingBlockEntity bvp11 = (PistonMovingBlockEntity)btw10;
                    if (bvp11.isExtending() && (bvp11.getProgress(0.0f) < 0.5f || bhr.getGameTime() == bvp11.getLastTicked() || ((ServerLevel)bhr).isHandlingTick())) {
                        integer9 = 2;
                    }
                }
            }
            bhr.blockEvent(ew, this, integer9, fb5.get3DDataValue());
        }
    }
    
    private boolean getNeighborSignal(final Level bhr, final BlockPos ew, final Direction fb) {
        for (final Direction fb2 : Direction.values()) {
            if (fb2 != fb && bhr.hasSignal(ew.relative(fb2), fb2)) {
                return true;
            }
        }
        if (bhr.hasSignal(ew, Direction.DOWN)) {
            return true;
        }
        final BlockPos ew2 = ew.above();
        for (final Direction fb3 : Direction.values()) {
            if (fb3 != Direction.DOWN && bhr.hasSignal(ew2.relative(fb3), fb3)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean triggerEvent(final BlockState bvt, final Level bhr, final BlockPos ew, final int integer4, final int integer5) {
        final Direction fb7 = bvt.<Direction>getValue((Property<Direction>)PistonBaseBlock.FACING);
        if (!bhr.isClientSide) {
            final boolean boolean8 = this.getNeighborSignal(bhr, ew, fb7);
            if (boolean8 && (integer4 == 1 || integer4 == 2)) {
                bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)PistonBaseBlock.EXTENDED, true), 2);
                return false;
            }
            if (!boolean8 && integer4 == 0) {
                return false;
            }
        }
        if (integer4 == 0) {
            if (!this.moveBlocks(bhr, ew, fb7, true)) {
                return false;
            }
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)PistonBaseBlock.EXTENDED, true), 67);
            bhr.playSound(null, ew, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5f, bhr.random.nextFloat() * 0.25f + 0.6f);
        }
        else if (integer4 == 1 || integer4 == 2) {
            final BlockEntity btw8 = bhr.getBlockEntity(ew.relative(fb7));
            if (btw8 instanceof PistonMovingBlockEntity) {
                ((PistonMovingBlockEntity)btw8).finalTick();
            }
            bhr.setBlock(ew, (((AbstractStateHolder<O, BlockState>)Blocks.MOVING_PISTON.defaultBlockState()).setValue((Property<Comparable>)MovingPistonBlock.FACING, fb7)).<Comparable, PistonType>setValue((Property<Comparable>)MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT), 3);
            bhr.setBlockEntity(ew, MovingPistonBlock.newMovingBlockEntity(((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)PistonBaseBlock.FACING, Direction.from3DDataValue(integer5 & 0x7)), fb7, false, true));
            if (this.isSticky) {
                final BlockPos ew2 = ew.offset(fb7.getStepX() * 2, fb7.getStepY() * 2, fb7.getStepZ() * 2);
                final BlockState bvt2 = bhr.getBlockState(ew2);
                final Block bmv11 = bvt2.getBlock();
                boolean boolean9 = false;
                if (bmv11 == Blocks.MOVING_PISTON) {
                    final BlockEntity btw9 = bhr.getBlockEntity(ew2);
                    if (btw9 instanceof PistonMovingBlockEntity) {
                        final PistonMovingBlockEntity bvp14 = (PistonMovingBlockEntity)btw9;
                        if (bvp14.getDirection() == fb7 && bvp14.isExtending()) {
                            bvp14.finalTick();
                            boolean9 = true;
                        }
                    }
                }
                if (!boolean9) {
                    if (integer4 == 1 && !bvt2.isAir() && isPushable(bvt2, bhr, ew2, fb7.getOpposite(), false, fb7) && (bvt2.getPistonPushReaction() == PushReaction.NORMAL || bmv11 == Blocks.PISTON || bmv11 == Blocks.STICKY_PISTON)) {
                        this.moveBlocks(bhr, ew, fb7, false);
                    }
                    else {
                        bhr.removeBlock(ew.relative(fb7), false);
                    }
                }
            }
            else {
                bhr.removeBlock(ew.relative(fb7), false);
            }
            bhr.playSound(null, ew, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5f, bhr.random.nextFloat() * 0.15f + 0.6f);
        }
        return true;
    }
    
    public static boolean isPushable(final BlockState bvt, final Level bhr, final BlockPos ew, final Direction fb4, final boolean boolean5, final Direction fb6) {
        final Block bmv7 = bvt.getBlock();
        if (bmv7 == Blocks.OBSIDIAN) {
            return false;
        }
        if (!bhr.getWorldBorder().isWithinBounds(ew)) {
            return false;
        }
        if (ew.getY() < 0 || (fb4 == Direction.DOWN && ew.getY() == 0)) {
            return false;
        }
        if (ew.getY() > bhr.getMaxBuildHeight() - 1 || (fb4 == Direction.UP && ew.getY() == bhr.getMaxBuildHeight() - 1)) {
            return false;
        }
        if (bmv7 == Blocks.PISTON || bmv7 == Blocks.STICKY_PISTON) {
            if (bvt.<Boolean>getValue((Property<Boolean>)PistonBaseBlock.EXTENDED)) {
                return false;
            }
        }
        else {
            if (bvt.getDestroySpeed(bhr, ew) == -1.0f) {
                return false;
            }
            switch (bvt.getPistonPushReaction()) {
                case BLOCK: {
                    return false;
                }
                case DESTROY: {
                    return boolean5;
                }
                case PUSH_ONLY: {
                    return fb4 == fb6;
                }
            }
        }
        return !bmv7.isEntityBlock();
    }
    
    private boolean moveBlocks(final Level bhr, final BlockPos ew, final Direction fb, final boolean boolean4) {
        final BlockPos ew2 = ew.relative(fb);
        if (!boolean4 && bhr.getBlockState(ew2).getBlock() == Blocks.PISTON_HEAD) {
            bhr.setBlock(ew2, Blocks.AIR.defaultBlockState(), 20);
        }
        final PistonStructureResolver bvq7 = new PistonStructureResolver(bhr, ew, fb, boolean4);
        if (!bvq7.resolve()) {
            return false;
        }
        final List<BlockPos> list8 = bvq7.getToPush();
        final List<BlockState> list9 = (List<BlockState>)Lists.newArrayList();
        for (int integer10 = 0; integer10 < list8.size(); ++integer10) {
            final BlockPos ew3 = (BlockPos)list8.get(integer10);
            list9.add(bhr.getBlockState(ew3));
        }
        final List<BlockPos> list10 = bvq7.getToDestroy();
        int integer11 = list8.size() + list10.size();
        final BlockState[] arr12 = new BlockState[integer11];
        final Direction fb2 = boolean4 ? fb : fb.getOpposite();
        final Set<BlockPos> set14 = (Set<BlockPos>)Sets.newHashSet((Iterable)list8);
        for (int integer12 = list10.size() - 1; integer12 >= 0; --integer12) {
            final BlockPos ew4 = (BlockPos)list10.get(integer12);
            final BlockState bvt17 = bhr.getBlockState(ew4);
            final BlockEntity btw18 = bvt17.getBlock().isEntityBlock() ? bhr.getBlockEntity(ew4) : null;
            Block.dropResources(bvt17, bhr, ew4, btw18);
            bhr.setBlock(ew4, Blocks.AIR.defaultBlockState(), 18);
            arr12[--integer11] = bvt17;
        }
        for (int integer12 = list8.size() - 1; integer12 >= 0; --integer12) {
            BlockPos ew4 = (BlockPos)list8.get(integer12);
            final BlockState bvt17 = bhr.getBlockState(ew4);
            ew4 = ew4.relative(fb2);
            set14.remove(ew4);
            bhr.setBlock(ew4, ((AbstractStateHolder<O, BlockState>)Blocks.MOVING_PISTON.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)PistonBaseBlock.FACING, fb), 68);
            bhr.setBlockEntity(ew4, MovingPistonBlock.newMovingBlockEntity((BlockState)list9.get(integer12), fb, boolean4, false));
            arr12[--integer11] = bvt17;
        }
        if (boolean4) {
            final PistonType bwv15 = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            final BlockState bvt18 = (((AbstractStateHolder<O, BlockState>)Blocks.PISTON_HEAD.defaultBlockState()).setValue((Property<Comparable>)PistonHeadBlock.FACING, fb)).<PistonType, PistonType>setValue(PistonHeadBlock.TYPE, bwv15);
            final BlockState bvt17 = (((AbstractStateHolder<O, BlockState>)Blocks.MOVING_PISTON.defaultBlockState()).setValue((Property<Comparable>)MovingPistonBlock.FACING, fb)).<Comparable, PistonType>setValue((Property<Comparable>)MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            set14.remove(ew2);
            bhr.setBlock(ew2, bvt17, 68);
            bhr.setBlockEntity(ew2, MovingPistonBlock.newMovingBlockEntity(bvt18, fb, true, true));
        }
        final Iterator iterator = set14.iterator();
        while (iterator.hasNext()) {
            final BlockPos ew4 = (BlockPos)iterator.next();
            bhr.setBlock(ew4, Blocks.AIR.defaultBlockState(), 66);
        }
        for (int integer12 = list10.size() - 1; integer12 >= 0; --integer12) {
            final BlockState bvt18 = arr12[integer11++];
            final BlockPos ew5 = (BlockPos)list10.get(integer12);
            bvt18.updateIndirectNeighbourShapes(bhr, ew5, 2);
            bhr.updateNeighborsAt(ew5, bvt18.getBlock());
        }
        for (int integer12 = list8.size() - 1; integer12 >= 0; --integer12) {
            bhr.updateNeighborsAt((BlockPos)list8.get(integer12), arr12[integer11++].getBlock());
        }
        if (boolean4) {
            bhr.updateNeighborsAt(ew2, Blocks.PISTON_HEAD);
        }
        return true;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)PistonBaseBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)PistonBaseBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)PistonBaseBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(PistonBaseBlock.FACING, PistonBaseBlock.EXTENDED);
    }
    
    @Override
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return bvt.<Boolean>getValue((Property<Boolean>)PistonBaseBlock.EXTENDED);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        EXTENDED = BlockStateProperties.EXTENDED;
        EAST_AABB = Block.box(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
        WEST_AABB = Block.box(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
        NORTH_AABB = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
        UP_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
        DOWN_AABB = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
    }
}
