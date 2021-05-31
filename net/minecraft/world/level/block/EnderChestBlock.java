package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import java.util.Random;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class EnderChestBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;
    public static final TranslatableComponent CONTAINER_TITLE;
    
    protected EnderChestBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)EnderChestBlock.FACING, Direction.NORTH)).<Comparable, Boolean>setValue((Property<Comparable>)EnderChestBlock.WATERLOGGED, false));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return EnderChestBlock.SHAPE;
    }
    
    @Override
    public boolean hasCustomBreakingProgress(final BlockState bvt) {
        return true;
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final FluidState clk3 = ban.getLevel().getFluidState(ban.getClickedPos());
        return (((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)EnderChestBlock.FACING, ban.getHorizontalDirection().getOpposite())).<Comparable, Boolean>setValue((Property<Comparable>)EnderChestBlock.WATERLOGGED, clk3.getType() == Fluids.WATER);
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        final PlayerEnderChestContainer azp8 = awg.getEnderChestInventory();
        final BlockEntity btw9 = bhr.getBlockEntity(ew);
        if (azp8 == null || !(btw9 instanceof EnderChestBlockEntity)) {
            return true;
        }
        final BlockPos ew2 = ew.above();
        if (bhr.getBlockState(ew2).isRedstoneConductor(bhr, ew2)) {
            return true;
        }
        if (bhr.isClientSide) {
            return true;
        }
        final EnderChestBlockEntity bui11 = (EnderChestBlockEntity)btw9;
        azp8.setActiveChest(bui11);
        awg.openMenu(new SimpleMenuProvider((integer, awf, awg) -> ChestMenu.threeRows(integer, awf, azp8), EnderChestBlock.CONTAINER_TITLE));
        awg.awardStat(Stats.OPEN_ENDERCHEST);
        return true;
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new EnderChestBlockEntity();
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        for (int integer6 = 0; integer6 < 3; ++integer6) {
            final int integer7 = random.nextInt(2) * 2 - 1;
            final int integer8 = random.nextInt(2) * 2 - 1;
            final double double9 = ew.getX() + 0.5 + 0.25 * integer7;
            final double double10 = ew.getY() + random.nextFloat();
            final double double11 = ew.getZ() + 0.5 + 0.25 * integer8;
            final double double12 = random.nextFloat() * integer7;
            final double double13 = (random.nextFloat() - 0.5) * 0.125;
            final double double14 = random.nextFloat() * integer8;
            bhr.addParticle(ParticleTypes.PORTAL, double9, double10, double11, double12, double13, double14);
        }
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)EnderChestBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)EnderChestBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)EnderChestBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(EnderChestBlock.FACING, EnderChestBlock.WATERLOGGED);
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)EnderChestBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)EnderChestBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
        CONTAINER_TITLE = new TranslatableComponent("container.enderchest", new Object[0]);
    }
}
