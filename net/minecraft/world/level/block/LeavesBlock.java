package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class LeavesBlock extends Block {
    public static final IntegerProperty DISTANCE;
    public static final BooleanProperty PERSISTENT;
    protected static boolean renderCutout;
    
    public LeavesBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)LeavesBlock.DISTANCE, 7)).<Comparable, Boolean>setValue((Property<Comparable>)LeavesBlock.PERSISTENT, false));
    }
    
    @Override
    public boolean isRandomlyTicking(final BlockState bvt) {
        return bvt.<Integer>getValue((Property<Integer>)LeavesBlock.DISTANCE) == 7 && !bvt.<Boolean>getValue((Property<Boolean>)LeavesBlock.PERSISTENT);
    }
    
    @Override
    public void randomTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)LeavesBlock.PERSISTENT) && bvt.<Integer>getValue((Property<Integer>)LeavesBlock.DISTANCE) == 7) {
            Block.dropResources(bvt, bhr, ew);
            bhr.removeBlock(ew, false);
        }
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        bhr.setBlock(ew, updateDistance(bvt, bhr, ew), 3);
    }
    
    @Override
    public int getLightBlock(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return 1;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        final int integer8 = getDistanceAt(bvt3) + 1;
        if (integer8 != 1 || bvt1.<Integer>getValue((Property<Integer>)LeavesBlock.DISTANCE) != integer8) {
            bhs.getBlockTicks().scheduleTick(ew5, this, 1);
        }
        return bvt1;
    }
    
    private static BlockState updateDistance(final BlockState bvt, final LevelAccessor bhs, final BlockPos ew) {
        int integer4 = 7;
        try (final BlockPos.PooledMutableBlockPos b5 = BlockPos.PooledMutableBlockPos.acquire()) {
            for (final Direction fb10 : Direction.values()) {
                b5.set(ew).move(fb10);
                integer4 = Math.min(integer4, getDistanceAt(bhs.getBlockState(b5)) + 1);
                if (integer4 == 1) {
                    break;
                }
            }
        }
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)LeavesBlock.DISTANCE, integer4);
    }
    
    private static int getDistanceAt(final BlockState bvt) {
        if (BlockTags.LOGS.contains(bvt.getBlock())) {
            return 0;
        }
        if (bvt.getBlock() instanceof LeavesBlock) {
            return bvt.<Integer>getValue((Property<Integer>)LeavesBlock.DISTANCE);
        }
        return 7;
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bhr.isRainingAt(ew.above())) {
            return;
        }
        if (random.nextInt(15) != 1) {
            return;
        }
        final BlockPos ew2 = ew.below();
        final BlockState bvt2 = bhr.getBlockState(ew2);
        if (bvt2.canOcclude() && bvt2.isFaceSturdy(bhr, ew2, Direction.UP)) {
            return;
        }
        final double double8 = ew.getX() + random.nextFloat();
        final double double9 = ew.getY() - 0.05;
        final double double10 = ew.getZ() + random.nextFloat();
        bhr.addParticle(ParticleTypes.DRIPPING_WATER, double8, double9, double10, 0.0, 0.0, 0.0);
    }
    
    public static void setFancy(final boolean boolean1) {
        LeavesBlock.renderCutout = boolean1;
    }
    
    @Override
    public boolean canOcclude(final BlockState bvt) {
        return false;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return LeavesBlock.renderCutout ? BlockLayer.CUTOUT_MIPPED : BlockLayer.SOLID;
    }
    
    @Override
    public boolean isViewBlocking(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return false;
    }
    
    @Override
    public boolean isValidSpawn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final EntityType<?> ais) {
        return ais == EntityType.OCELOT || ais == EntityType.PARROT;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(LeavesBlock.DISTANCE, LeavesBlock.PERSISTENT);
    }
    
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        return updateDistance(((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)LeavesBlock.PERSISTENT, true), ban.getLevel(), ban.getClickedPos());
    }
    
    static {
        DISTANCE = BlockStateProperties.DISTANCE;
        PERSISTENT = BlockStateProperties.PERSISTENT;
    }
}
