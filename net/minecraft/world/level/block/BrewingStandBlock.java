package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import java.util.Random;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BrewingStandBlock extends BaseEntityBlock {
    public static final BooleanProperty[] HAS_BOTTLE;
    protected static final VoxelShape SHAPE;
    
    public BrewingStandBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)BrewingStandBlock.HAS_BOTTLE[0], false)).setValue((Property<Comparable>)BrewingStandBlock.HAS_BOTTLE[1], false)).<Comparable, Boolean>setValue((Property<Comparable>)BrewingStandBlock.HAS_BOTTLE[2], false));
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new BrewingStandBlockEntity();
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return BrewingStandBlock.SHAPE;
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bhr.isClientSide) {
            return true;
        }
        final BlockEntity btw8 = bhr.getBlockEntity(ew);
        if (btw8 instanceof BrewingStandBlockEntity) {
            awg.openMenu((MenuProvider)btw8);
            awg.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
        }
        return true;
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        if (bcj.hasCustomHoverName()) {
            final BlockEntity btw7 = bhr.getBlockEntity(ew);
            if (btw7 instanceof BrewingStandBlockEntity) {
                ((BrewingStandBlockEntity)btw7).setCustomName(bcj.getHoverName());
            }
        }
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final double double6 = ew.getX() + 0.4f + random.nextFloat() * 0.2f;
        final double double7 = ew.getY() + 0.7f + random.nextFloat() * 0.3f;
        final double double8 = ew.getZ() + 0.4f + random.nextFloat() * 0.2f;
        bhr.addParticle(ParticleTypes.SMOKE, double6, double7, double8, 0.0, 0.0, 0.0);
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        if (btw7 instanceof BrewingStandBlockEntity) {
            Containers.dropContents(bhr, ew, (Container)btw7);
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    @Override
    public boolean hasAnalogOutputSignal(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(final BlockState bvt, final Level bhr, final BlockPos ew) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(bhr.getBlockEntity(ew));
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(BrewingStandBlock.HAS_BOTTLE[0], BrewingStandBlock.HAS_BOTTLE[1], BrewingStandBlock.HAS_BOTTLE[2]);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        HAS_BOTTLE = new BooleanProperty[] { BlockStateProperties.HAS_BOTTLE_0, BlockStateProperties.HAS_BOTTLE_1, BlockStateProperties.HAS_BOTTLE_2 };
        SHAPE = Shapes.or(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(7.0, 0.0, 7.0, 9.0, 14.0, 9.0));
    }
}
