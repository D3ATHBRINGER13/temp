package net.minecraft.world.level.block.piston;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import java.util.Collections;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.BaseEntityBlock;

public class MovingPistonBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    public static final EnumProperty<PistonType> TYPE;
    
    public MovingPistonBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)MovingPistonBlock.FACING, Direction.NORTH)).<PistonType, PistonType>setValue(MovingPistonBlock.TYPE, PistonType.DEFAULT));
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return null;
    }
    
    public static BlockEntity newMovingBlockEntity(final BlockState bvt, final Direction fb, final boolean boolean3, final boolean boolean4) {
        return new PistonMovingBlockEntity(bvt, fb, boolean3, boolean4);
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        if (btw7 instanceof PistonMovingBlockEntity) {
            ((PistonMovingBlockEntity)btw7).finalTick();
        }
    }
    
    @Override
    public void destroy(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt) {
        final BlockPos ew2 = ew.relative(bvt.<Direction>getValue((Property<Direction>)MovingPistonBlock.FACING).getOpposite());
        final BlockState bvt2 = bhs.getBlockState(ew2);
        if (bvt2.getBlock() instanceof PistonBaseBlock && bvt2.<Boolean>getValue((Property<Boolean>)PistonBaseBlock.EXTENDED)) {
            bhs.removeBlock(ew2, false);
        }
    }
    
    @Override
    public boolean canOcclude(final BlockState bvt) {
        return false;
    }
    
    @Override
    public boolean isRedstoneConductor(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return false;
    }
    
    @Override
    public boolean isViewBlocking(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return false;
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (!bhr.isClientSide && bhr.getBlockEntity(ew) == null) {
            bhr.removeBlock(ew, false);
            return true;
        }
        return false;
    }
    
    @Override
    public List<ItemStack> getDrops(final BlockState bvt, final LootContext.Builder a) {
        final PistonMovingBlockEntity bvp4 = this.getBlockEntity(a.getLevel(), a.<BlockPos>getParameter(LootContextParams.BLOCK_POS));
        if (bvp4 == null) {
            return (List<ItemStack>)Collections.emptyList();
        }
        return bvp4.getMovedState().getDrops(a);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return Shapes.empty();
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final PistonMovingBlockEntity bvp6 = this.getBlockEntity(bhb, ew);
        if (bvp6 != null) {
            return bvp6.getCollisionShape(bhb, ew);
        }
        return Shapes.empty();
    }
    
    @Nullable
    private PistonMovingBlockEntity getBlockEntity(final BlockGetter bhb, final BlockPos ew) {
        final BlockEntity btw4 = bhb.getBlockEntity(ew);
        if (btw4 instanceof PistonMovingBlockEntity) {
            return (PistonMovingBlockEntity)btw4;
        }
        return null;
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)MovingPistonBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)MovingPistonBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)MovingPistonBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(MovingPistonBlock.FACING, MovingPistonBlock.TYPE);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        FACING = PistonHeadBlock.FACING;
        TYPE = PistonHeadBlock.TYPE;
    }
}
