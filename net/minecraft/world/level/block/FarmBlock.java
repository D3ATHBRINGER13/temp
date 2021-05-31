package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import java.util.Iterator;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FarmBlock extends Block {
    public static final IntegerProperty MOISTURE;
    protected static final VoxelShape SHAPE;
    
    protected FarmBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)FarmBlock.MOISTURE, 0));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb == Direction.UP && !bvt1.canSurvive(bhs, ew5)) {
            bhs.getBlockTicks().scheduleTick(ew5, this, 1);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockState bvt2 = bhu.getBlockState(ew.above());
        return !bvt2.getMaterial().isSolid() || bvt2.getBlock() instanceof FenceGateBlock;
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        if (!this.defaultBlockState().canSurvive(ban.getLevel(), ban.getClickedPos())) {
            return Blocks.DIRT.defaultBlockState();
        }
        return super.getStateForPlacement(ban);
    }
    
    @Override
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return true;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return FarmBlock.SHAPE;
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.canSurvive(bhr, ew)) {
            turnToDirt(bvt, bhr, ew);
            return;
        }
        final int integer6 = bvt.<Integer>getValue((Property<Integer>)FarmBlock.MOISTURE);
        if (isNearWater(bhr, ew) || bhr.isRainingAt(ew.above())) {
            if (integer6 < 7) {
                bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)FarmBlock.MOISTURE, 7), 2);
            }
        }
        else if (integer6 > 0) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)FarmBlock.MOISTURE, integer6 - 1), 2);
        }
        else if (!isUnderCrops(bhr, ew)) {
            turnToDirt(bvt, bhr, ew);
        }
    }
    
    @Override
    public void fallOn(final Level bhr, final BlockPos ew, final Entity aio, final float float4) {
        if (!bhr.isClientSide && bhr.random.nextFloat() < float4 - 0.5f && aio instanceof LivingEntity && (aio instanceof Player || bhr.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) && aio.getBbWidth() * aio.getBbWidth() * aio.getBbHeight() > 0.512f) {
            turnToDirt(bhr.getBlockState(ew), bhr, ew);
        }
        super.fallOn(bhr, ew, aio, float4);
    }
    
    public static void turnToDirt(final BlockState bvt, final Level bhr, final BlockPos ew) {
        bhr.setBlockAndUpdate(ew, Block.pushEntitiesUp(bvt, Blocks.DIRT.defaultBlockState(), bhr, ew));
    }
    
    private static boolean isUnderCrops(final BlockGetter bhb, final BlockPos ew) {
        final Block bmv3 = bhb.getBlockState(ew.above()).getBlock();
        return bmv3 instanceof CropBlock || bmv3 instanceof StemBlock || bmv3 instanceof AttachedStemBlock;
    }
    
    private static boolean isNearWater(final LevelReader bhu, final BlockPos ew) {
        for (final BlockPos ew2 : BlockPos.betweenClosed(ew.offset(-4, 0, -4), ew.offset(4, 1, 4))) {
            if (bhu.getFluidState(ew2).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(FarmBlock.MOISTURE);
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        MOISTURE = BlockStateProperties.MOISTURE;
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);
    }
}
