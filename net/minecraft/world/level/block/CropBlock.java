package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.util.Mth;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class CropBlock extends BushBlock implements BonemealableBlock {
    public static final IntegerProperty AGE;
    private static final VoxelShape[] SHAPE_BY_AGE;
    
    protected CropBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)this.getAgeProperty(), 0));
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return CropBlock.SHAPE_BY_AGE[bvt.<Integer>getValue((Property<Integer>)this.getAgeProperty())];
    }
    
    @Override
    protected boolean mayPlaceOn(final BlockState bvt, final BlockGetter bhb, final BlockPos ew) {
        return bvt.getBlock() == Blocks.FARMLAND;
    }
    
    public IntegerProperty getAgeProperty() {
        return CropBlock.AGE;
    }
    
    public int getMaxAge() {
        return 7;
    }
    
    protected int getAge(final BlockState bvt) {
        return bvt.<Integer>getValue((Property<Integer>)this.getAgeProperty());
    }
    
    public BlockState getStateForAge(final int integer) {
        return ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)this.getAgeProperty(), integer);
    }
    
    public boolean isMaxAge(final BlockState bvt) {
        return bvt.<Integer>getValue((Property<Integer>)this.getAgeProperty()) >= this.getMaxAge();
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        super.tick(bvt, bhr, ew, random);
        if (bhr.getRawBrightness(ew, 0) >= 9) {
            final int integer6 = this.getAge(bvt);
            if (integer6 < this.getMaxAge()) {
                final float float7 = getGrowthSpeed(this, bhr, ew);
                if (random.nextInt((int)(25.0f / float7) + 1) == 0) {
                    bhr.setBlock(ew, this.getStateForAge(integer6 + 1), 2);
                }
            }
        }
    }
    
    public void growCrops(final Level bhr, final BlockPos ew, final BlockState bvt) {
        int integer5 = this.getAge(bvt) + this.getBonemealAgeIncrease(bhr);
        final int integer6 = this.getMaxAge();
        if (integer5 > integer6) {
            integer5 = integer6;
        }
        bhr.setBlock(ew, this.getStateForAge(integer5), 2);
    }
    
    protected int getBonemealAgeIncrease(final Level bhr) {
        return Mth.nextInt(bhr.random, 2, 5);
    }
    
    protected static float getGrowthSpeed(final Block bmv, final BlockGetter bhb, final BlockPos ew) {
        float float4 = 1.0f;
        final BlockPos ew2 = ew.below();
        for (int integer6 = -1; integer6 <= 1; ++integer6) {
            for (int integer7 = -1; integer7 <= 1; ++integer7) {
                float float5 = 0.0f;
                final BlockState bvt9 = bhb.getBlockState(ew2.offset(integer6, 0, integer7));
                if (bvt9.getBlock() == Blocks.FARMLAND) {
                    float5 = 1.0f;
                    if (bvt9.<Integer>getValue((Property<Integer>)FarmBlock.MOISTURE) > 0) {
                        float5 = 3.0f;
                    }
                }
                if (integer6 != 0 || integer7 != 0) {
                    float5 /= 4.0f;
                }
                float4 += float5;
            }
        }
        final BlockPos ew3 = ew.north();
        final BlockPos ew4 = ew.south();
        final BlockPos ew5 = ew.west();
        final BlockPos ew6 = ew.east();
        final boolean boolean10 = bmv == bhb.getBlockState(ew5).getBlock() || bmv == bhb.getBlockState(ew6).getBlock();
        final boolean boolean11 = bmv == bhb.getBlockState(ew3).getBlock() || bmv == bhb.getBlockState(ew4).getBlock();
        if (boolean10 && boolean11) {
            float4 /= 2.0f;
        }
        else {
            final boolean boolean12 = bmv == bhb.getBlockState(ew5.north()).getBlock() || bmv == bhb.getBlockState(ew6.north()).getBlock() || bmv == bhb.getBlockState(ew6.south()).getBlock() || bmv == bhb.getBlockState(ew5.south()).getBlock();
            if (boolean12) {
                float4 /= 2.0f;
            }
        }
        return float4;
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        return (bhu.getRawBrightness(ew, 0) >= 8 || bhu.canSeeSky(ew)) && super.canSurvive(bvt, bhu, ew);
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        if (aio instanceof Ravager && bhr.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            bhr.destroyBlock(ew, true);
        }
        super.entityInside(bvt, bhr, ew, aio);
    }
    
    protected ItemLike getBaseSeedId() {
        return Items.WHEAT_SEEDS;
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return new ItemStack(this.getBaseSeedId());
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return !this.isMaxAge(bvt);
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return true;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        this.growCrops(bhr, ew, bvt);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(CropBlock.AGE);
    }
    
    static {
        AGE = BlockStateProperties.AGE_7;
        SHAPE_BY_AGE = new VoxelShape[] { Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0) };
    }
}
