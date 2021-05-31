package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class AnvilBlock extends FallingBlock {
    public static final DirectionProperty FACING;
    private static final VoxelShape BASE;
    private static final VoxelShape X_LEG1;
    private static final VoxelShape X_LEG2;
    private static final VoxelShape X_TOP;
    private static final VoxelShape Z_LEG1;
    private static final VoxelShape Z_LEG2;
    private static final VoxelShape Z_TOP;
    private static final VoxelShape X_AXIS_AABB;
    private static final VoxelShape Z_AXIS_AABB;
    private static final TranslatableComponent CONTAINER_TITLE;
    
    public AnvilBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Direction>setValue((Property<Comparable>)AnvilBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)AnvilBlock.FACING, ban.getHorizontalDirection().getClockWise());
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        awg.openMenu(bvt.getMenuProvider(bhr, ew));
        return true;
    }
    
    @Nullable
    @Override
    public MenuProvider getMenuProvider(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return new SimpleMenuProvider((integer, awf, awg) -> new AnvilMenu(integer, awf, ContainerLevelAccess.create(bhr, ew)), AnvilBlock.CONTAINER_TITLE);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        final Direction fb6 = bvt.<Direction>getValue((Property<Direction>)AnvilBlock.FACING);
        if (fb6.getAxis() == Direction.Axis.X) {
            return AnvilBlock.X_AXIS_AABB;
        }
        return AnvilBlock.Z_AXIS_AABB;
    }
    
    @Override
    protected void falling(final FallingBlockEntity atw) {
        atw.setHurtsEntities(true);
    }
    
    @Override
    public void onLand(final Level bhr, final BlockPos ew, final BlockState bvt3, final BlockState bvt4) {
        bhr.levelEvent(1031, ew, 0);
    }
    
    @Override
    public void onBroken(final Level bhr, final BlockPos ew) {
        bhr.levelEvent(1029, ew, 0);
    }
    
    @Nullable
    public static BlockState damage(final BlockState bvt) {
        final Block bmv2 = bvt.getBlock();
        if (bmv2 == Blocks.ANVIL) {
            return ((AbstractStateHolder<O, BlockState>)Blocks.CHIPPED_ANVIL.defaultBlockState()).<Comparable, Comparable>setValue((Property<Comparable>)AnvilBlock.FACING, (Comparable)bvt.<V>getValue((Property<V>)AnvilBlock.FACING));
        }
        if (bmv2 == Blocks.CHIPPED_ANVIL) {
            return ((AbstractStateHolder<O, BlockState>)Blocks.DAMAGED_ANVIL.defaultBlockState()).<Comparable, Comparable>setValue((Property<Comparable>)AnvilBlock.FACING, (Comparable)bvt.<V>getValue((Property<V>)AnvilBlock.FACING));
        }
        return null;
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)AnvilBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)AnvilBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(AnvilBlock.FACING);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        BASE = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
        X_LEG1 = Block.box(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
        X_LEG2 = Block.box(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
        X_TOP = Block.box(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
        Z_LEG1 = Block.box(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
        Z_LEG2 = Block.box(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
        Z_TOP = Block.box(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
        X_AXIS_AABB = Shapes.or(AnvilBlock.BASE, AnvilBlock.X_LEG1, AnvilBlock.X_LEG2, AnvilBlock.X_TOP);
        Z_AXIS_AABB = Shapes.or(AnvilBlock.BASE, AnvilBlock.Z_LEG1, AnvilBlock.Z_LEG2, AnvilBlock.Z_TOP);
        CONTAINER_TITLE = new TranslatableComponent("container.repair", new Object[0]);
    }
}
