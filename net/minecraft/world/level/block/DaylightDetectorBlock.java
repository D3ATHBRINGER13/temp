package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class DaylightDetectorBlock extends BaseEntityBlock {
    public static final IntegerProperty POWER;
    public static final BooleanProperty INVERTED;
    protected static final VoxelShape SHAPE;
    
    public DaylightDetectorBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)DaylightDetectorBlock.POWER, 0)).<Comparable, Boolean>setValue((Property<Comparable>)DaylightDetectorBlock.INVERTED, false));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return DaylightDetectorBlock.SHAPE;
    }
    
    @Override
    public boolean useShapeForLightOcclusion(final BlockState bvt) {
        return true;
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        return bvt.<Integer>getValue((Property<Integer>)DaylightDetectorBlock.POWER);
    }
    
    public static void updateSignalStrength(final BlockState bvt, final Level bhr, final BlockPos ew) {
        if (!bhr.dimension.isHasSkyLight()) {
            return;
        }
        int integer4 = bhr.getBrightness(LightLayer.SKY, ew) - bhr.getSkyDarken();
        float float5 = bhr.getSunAngle(1.0f);
        final boolean boolean6 = bvt.<Boolean>getValue((Property<Boolean>)DaylightDetectorBlock.INVERTED);
        if (boolean6) {
            integer4 = 15 - integer4;
        }
        else if (integer4 > 0) {
            final float float6 = (float5 < 3.1415927f) ? 0.0f : 6.2831855f;
            float5 += (float6 - float5) * 0.2f;
            integer4 = Math.round(integer4 * Mth.cos(float5));
        }
        integer4 = Mth.clamp(integer4, 0, 15);
        if (bvt.<Integer>getValue((Property<Integer>)DaylightDetectorBlock.POWER) != integer4) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)DaylightDetectorBlock.POWER, integer4), 3);
        }
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (!awg.mayBuild()) {
            return super.use(bvt, bhr, ew, awg, ahi, csd);
        }
        if (bhr.isClientSide) {
            return true;
        }
        final BlockState bvt2 = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable>cycle((Property<Comparable>)DaylightDetectorBlock.INVERTED);
        bhr.setBlock(ew, bvt2, 4);
        updateSignalStrength(bvt2, bhr, ew);
        return true;
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new DaylightDetectorBlockEntity();
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(DaylightDetectorBlock.POWER, DaylightDetectorBlock.INVERTED);
    }
    
    static {
        POWER = BlockStateProperties.POWER;
        INVERTED = BlockStateProperties.INVERTED;
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);
    }
}
