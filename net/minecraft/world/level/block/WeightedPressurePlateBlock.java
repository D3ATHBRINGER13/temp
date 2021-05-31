package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WeightedPressurePlateBlock extends BasePressurePlateBlock {
    public static final IntegerProperty POWER;
    private final int maxWeight;
    
    protected WeightedPressurePlateBlock(final int integer, final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)WeightedPressurePlateBlock.POWER, 0));
        this.maxWeight = integer;
    }
    
    @Override
    protected int getSignalStrength(final Level bhr, final BlockPos ew) {
        final int integer4 = Math.min(bhr.<Entity>getEntitiesOfClass((java.lang.Class<? extends Entity>)Entity.class, WeightedPressurePlateBlock.TOUCH_AABB.move(ew)).size(), this.maxWeight);
        if (integer4 > 0) {
            final float float5 = Math.min(this.maxWeight, integer4) / (float)this.maxWeight;
            return Mth.ceil(float5 * 15.0f);
        }
        return 0;
    }
    
    @Override
    protected void playOnSound(final LevelAccessor bhs, final BlockPos ew) {
        bhs.playSound(null, ew, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3f, 0.90000004f);
    }
    
    @Override
    protected void playOffSound(final LevelAccessor bhs, final BlockPos ew) {
        bhs.playSound(null, ew, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3f, 0.75f);
    }
    
    @Override
    protected int getSignalForState(final BlockState bvt) {
        return bvt.<Integer>getValue((Property<Integer>)WeightedPressurePlateBlock.POWER);
    }
    
    @Override
    protected BlockState setSignalForState(final BlockState bvt, final int integer) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)WeightedPressurePlateBlock.POWER, integer);
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 10;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(WeightedPressurePlateBlock.POWER);
    }
    
    static {
        POWER = BlockStateProperties.POWER;
    }
}
