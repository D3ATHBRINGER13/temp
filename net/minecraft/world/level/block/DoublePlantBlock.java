package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class DoublePlantBlock extends BushBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF;
    
    public DoublePlantBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<DoubleBlockHalf, DoubleBlockHalf>setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        final DoubleBlockHalf bwq8 = bvt1.<DoubleBlockHalf>getValue(DoublePlantBlock.HALF);
        if (fb.getAxis() == Direction.Axis.Y && bwq8 == DoubleBlockHalf.LOWER == (fb == Direction.UP) && (bvt3.getBlock() != this || bvt3.<DoubleBlockHalf>getValue(DoublePlantBlock.HALF) == bwq8)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (bwq8 == DoubleBlockHalf.LOWER && fb == Direction.DOWN && !bvt1.canSurvive(bhs, ew5)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockPos ew3 = ban.getClickedPos();
        if (ew3.getY() < 255 && ban.getLevel().getBlockState(ew3.above()).canBeReplaced(ban)) {
            return super.getStateForPlacement(ban);
        }
        return null;
    }
    
    @Override
    public void setPlacedBy(final Level bhr, final BlockPos ew, final BlockState bvt, final LivingEntity aix, final ItemStack bcj) {
        bhr.setBlock(ew.above(), ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<DoubleBlockHalf, DoubleBlockHalf>setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 3);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        if (bvt.<DoubleBlockHalf>getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
            final BlockState bvt2 = bhu.getBlockState(ew.below());
            return bvt2.getBlock() == this && bvt2.<DoubleBlockHalf>getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER;
        }
        return super.canSurvive(bvt, bhu, ew);
    }
    
    public void placeAt(final LevelAccessor bhs, final BlockPos ew, final int integer) {
        bhs.setBlock(ew, ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<DoubleBlockHalf, DoubleBlockHalf>setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), integer);
        bhs.setBlock(ew.above(), ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<DoubleBlockHalf, DoubleBlockHalf>setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), integer);
    }
    
    @Override
    public void playerDestroy(final Level bhr, final Player awg, final BlockPos ew, final BlockState bvt, @Nullable final BlockEntity btw, final ItemStack bcj) {
        super.playerDestroy(bhr, awg, ew, Blocks.AIR.defaultBlockState(), btw, bcj);
    }
    
    @Override
    public void playerWillDestroy(final Level bhr, final BlockPos ew, final BlockState bvt, final Player awg) {
        final DoubleBlockHalf bwq6 = bvt.<DoubleBlockHalf>getValue(DoublePlantBlock.HALF);
        final BlockPos ew2 = (bwq6 == DoubleBlockHalf.LOWER) ? ew.above() : ew.below();
        final BlockState bvt2 = bhr.getBlockState(ew2);
        if (bvt2.getBlock() == this && bvt2.<DoubleBlockHalf>getValue(DoublePlantBlock.HALF) != bwq6) {
            bhr.setBlock(ew2, Blocks.AIR.defaultBlockState(), 35);
            bhr.levelEvent(awg, 2001, ew2, Block.getId(bvt2));
            if (!bhr.isClientSide && !awg.isCreative()) {
                Block.dropResources(bvt, bhr, ew, null, awg, awg.getMainHandItem());
                Block.dropResources(bvt2, bhr, ew2, null, awg, awg.getMainHandItem());
            }
        }
        super.playerWillDestroy(bhr, ew, bvt, awg);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(DoublePlantBlock.HALF);
    }
    
    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
    
    @Override
    public long getSeed(final BlockState bvt, final BlockPos ew) {
        return Mth.getSeed(ew.getX(), ew.below((bvt.<DoubleBlockHalf>getValue(DoublePlantBlock.HALF) != DoubleBlockHalf.LOWER) ? 1 : 0).getY(), ew.getZ());
    }
    
    static {
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    }
}
