package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BubbleColumnBlock extends Block implements BucketPickup {
    public static final BooleanProperty DRAG_DOWN;
    
    public BubbleColumnBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Boolean>setValue((Property<Comparable>)BubbleColumnBlock.DRAG_DOWN, true));
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        final BlockState bvt2 = bhr.getBlockState(ew.above());
        if (bvt2.isAir()) {
            aio.onAboveBubbleCol(bvt.<Boolean>getValue((Property<Boolean>)BubbleColumnBlock.DRAG_DOWN));
            if (!bhr.isClientSide) {
                final ServerLevel vk7 = (ServerLevel)bhr;
                for (int integer8 = 0; integer8 < 2; ++integer8) {
                    vk7.<SimpleParticleType>sendParticles(ParticleTypes.SPLASH, ew.getX() + bhr.random.nextFloat(), ew.getY() + 1, ew.getZ() + bhr.random.nextFloat(), 1, 0.0, 0.0, 0.0, 1.0);
                    vk7.<SimpleParticleType>sendParticles(ParticleTypes.BUBBLE, ew.getX() + bhr.random.nextFloat(), ew.getY() + 1, ew.getZ() + bhr.random.nextFloat(), 1, 0.0, 0.01, 0.0, 0.2);
                }
            }
        }
        else {
            aio.onInsideBubbleColumn(bvt.<Boolean>getValue((Property<Boolean>)BubbleColumnBlock.DRAG_DOWN));
        }
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        growColumn(bhr, ew.above(), getDrag(bhr, ew.below()));
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        growColumn(bhr, ew.above(), getDrag(bhr, ew));
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        return Fluids.WATER.getSource(false);
    }
    
    public static void growColumn(final LevelAccessor bhs, final BlockPos ew, final boolean boolean3) {
        if (canExistIn(bhs, ew)) {
            bhs.setBlock(ew, ((AbstractStateHolder<O, BlockState>)Blocks.BUBBLE_COLUMN.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)BubbleColumnBlock.DRAG_DOWN, boolean3), 2);
        }
    }
    
    public static boolean canExistIn(final LevelAccessor bhs, final BlockPos ew) {
        final FluidState clk3 = bhs.getFluidState(ew);
        return bhs.getBlockState(ew).getBlock() == Blocks.WATER && clk3.getAmount() >= 8 && clk3.isSource();
    }
    
    private static boolean getDrag(final BlockGetter bhb, final BlockPos ew) {
        final BlockState bvt3 = bhb.getBlockState(ew);
        final Block bmv4 = bvt3.getBlock();
        if (bmv4 == Blocks.BUBBLE_COLUMN) {
            return bvt3.<Boolean>getValue((Property<Boolean>)BubbleColumnBlock.DRAG_DOWN);
        }
        return bmv4 != Blocks.SOUL_SAND;
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 5;
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        final double double6 = ew.getX();
        final double double7 = ew.getY();
        final double double8 = ew.getZ();
        if (bvt.<Boolean>getValue((Property<Boolean>)BubbleColumnBlock.DRAG_DOWN)) {
            bhr.addAlwaysVisibleParticle(ParticleTypes.CURRENT_DOWN, double6 + 0.5, double7 + 0.8, double8, 0.0, 0.0, 0.0);
            if (random.nextInt(200) == 0) {
                bhr.playLocalSound(double6, double7, double8, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        }
        else {
            bhr.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, double6 + 0.5, double7, double8 + 0.5, 0.0, 0.04, 0.0);
            bhr.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, double6 + random.nextFloat(), double7 + random.nextFloat(), double8 + random.nextFloat(), 0.0, 0.04, 0.0);
            if (random.nextInt(200) == 0) {
                bhr.playLocalSound(double6, double7, double8, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        }
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (!bvt1.canSurvive(bhs, ew5)) {
            return Blocks.WATER.defaultBlockState();
        }
        if (fb == Direction.DOWN) {
            bhs.setBlock(ew5, ((AbstractStateHolder<O, BlockState>)Blocks.BUBBLE_COLUMN.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)BubbleColumnBlock.DRAG_DOWN, getDrag(bhs, ew6)), 2);
        }
        else if (fb == Direction.UP && bvt3.getBlock() != Blocks.BUBBLE_COLUMN && canExistIn(bhs, ew6)) {
            bhs.getBlockTicks().scheduleTick(ew5, this, this.getTickDelay(bhs));
        }
        bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final Block bmv5 = bhu.getBlockState(ew.below()).getBlock();
        return bmv5 == Blocks.BUBBLE_COLUMN || bmv5 == Blocks.MAGMA_BLOCK || bmv5 == Blocks.SOUL_SAND;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return Shapes.empty();
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.TRANSLUCENT;
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.INVISIBLE;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(BubbleColumnBlock.DRAG_DOWN);
    }
    
    @Override
    public Fluid takeLiquid(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt) {
        bhs.setBlock(ew, Blocks.AIR.defaultBlockState(), 11);
        return Fluids.WATER;
    }
    
    static {
        DRAG_DOWN = BlockStateProperties.DRAG;
    }
}
