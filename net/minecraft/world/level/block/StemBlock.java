package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.core.Direction;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class StemBlock extends BushBlock implements BonemealableBlock {
    public static final IntegerProperty AGE;
    protected static final VoxelShape[] SHAPE_BY_AGE;
    private final StemGrownBlock fruit;
    
    protected StemBlock(final StemGrownBlock bsh, final Properties c) {
        super(c);
        this.fruit = bsh;
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)StemBlock.AGE, 0));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return StemBlock.SHAPE_BY_AGE[bvt.<Integer>getValue((Property<Integer>)StemBlock.AGE)];
    }
    
    @Override
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return bvt.getBlock() == Blocks.FARMLAND;
    }
    
    @Override
    public void tick(BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        super.tick(bvt, bhr, ew, random);
        if (bhr.getRawBrightness(ew, 0) < 9) {
            return;
        }
        final float float6 = CropBlock.getGrowthSpeed(this, bhr, ew);
        if (random.nextInt((int)(25.0f / float6) + 1) == 0) {
            final int integer7 = bvt.<Integer>getValue((Property<Integer>)StemBlock.AGE);
            if (integer7 < 7) {
                bvt = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)StemBlock.AGE, integer7 + 1);
                bhr.setBlock(ew, bvt, 2);
            }
            else {
                final Direction fb8 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                final BlockPos ew2 = ew.relative(fb8);
                final Block bmv10 = bhr.getBlockState(ew2.below()).getBlock();
                if (bhr.getBlockState(ew2).isAir() && (bmv10 == Blocks.FARMLAND || bmv10 == Blocks.DIRT || bmv10 == Blocks.COARSE_DIRT || bmv10 == Blocks.PODZOL || bmv10 == Blocks.GRASS_BLOCK)) {
                    bhr.setBlockAndUpdate(ew2, this.fruit.defaultBlockState());
                    bhr.setBlockAndUpdate(ew, ((AbstractStateHolder<O, BlockState>)this.fruit.getAttachedStem().defaultBlockState()).<Comparable, Direction>setValue((Property<Comparable>)HorizontalDirectionalBlock.FACING, fb8));
                }
            }
        }
    }
    
    @Nullable
    protected Item getSeedItem() {
        if (this.fruit == Blocks.PUMPKIN) {
            return Items.PUMPKIN_SEEDS;
        }
        if (this.fruit == Blocks.MELON) {
            return Items.MELON_SEEDS;
        }
        return null;
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        final Item bce5 = this.getSeedItem();
        return (bce5 == null) ? ItemStack.EMPTY : new ItemStack(bce5);
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return bvt.<Integer>getValue((Property<Integer>)StemBlock.AGE) != 7;
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return true;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        final int integer6 = Math.min(7, bvt.<Integer>getValue((Property<Integer>)StemBlock.AGE) + Mth.nextInt(bhr.random, 2, 5));
        final BlockState bvt2 = ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)StemBlock.AGE, integer6);
        bhr.setBlock(ew, bvt2, 2);
        if (integer6 == 7) {
            bvt2.tick(bhr, ew, bhr.random);
        }
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(StemBlock.AGE);
    }
    
    public StemGrownBlock getFruit() {
        return this.fruit;
    }
    
    static {
        AGE = BlockStateProperties.AGE_7;
        SHAPE_BY_AGE = new VoxelShape[] { Block.box(7.0, 0.0, 7.0, 9.0, 2.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 4.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 6.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 8.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 10.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 12.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 14.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0) };
    }
}
