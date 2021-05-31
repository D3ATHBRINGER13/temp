package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelAccessor;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ChorusFlowerBlock extends Block {
    public static final IntegerProperty AGE;
    private final ChorusPlantBlock plant;
    
    protected ChorusFlowerBlock(final ChorusPlantBlock bnm, final Properties c) {
        super(c);
        this.plant = bnm;
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)ChorusFlowerBlock.AGE, 0));
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.canSurvive(bhr, ew)) {
            bhr.destroyBlock(ew, true);
            return;
        }
        final BlockPos ew2 = ew.above();
        if (!bhr.isEmptyBlock(ew2) || ew2.getY() >= 256) {
            return;
        }
        final int integer7 = bvt.<Integer>getValue((Property<Integer>)ChorusFlowerBlock.AGE);
        if (integer7 >= 5) {
            return;
        }
        boolean boolean8 = false;
        boolean boolean9 = false;
        final BlockState bvt2 = bhr.getBlockState(ew.below());
        final Block bmv11 = bvt2.getBlock();
        if (bmv11 == Blocks.END_STONE) {
            boolean8 = true;
        }
        else if (bmv11 == this.plant) {
            int integer8 = 1;
            int integer9 = 0;
            while (integer9 < 4) {
                final Block bmv12 = bhr.getBlockState(ew.below(integer8 + 1)).getBlock();
                if (bmv12 == this.plant) {
                    ++integer8;
                    ++integer9;
                }
                else {
                    if (bmv12 == Blocks.END_STONE) {
                        boolean9 = true;
                        break;
                    }
                    break;
                }
            }
            if (integer8 < 2 || integer8 <= random.nextInt(boolean9 ? 5 : 4)) {
                boolean8 = true;
            }
        }
        else if (bvt2.isAir()) {
            boolean8 = true;
        }
        if (boolean8 && allNeighborsEmpty(bhr, ew2, null) && bhr.isEmptyBlock(ew.above(2))) {
            bhr.setBlock(ew, this.plant.getStateForPlacement(bhr, ew), 2);
            this.placeGrownFlower(bhr, ew2, integer7);
        }
        else if (integer7 < 4) {
            int integer8 = random.nextInt(4);
            if (boolean9) {
                ++integer8;
            }
            boolean boolean10 = false;
            for (int integer10 = 0; integer10 < integer8; ++integer10) {
                final Direction fb15 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                final BlockPos ew3 = ew.relative(fb15);
                if (bhr.isEmptyBlock(ew3) && bhr.isEmptyBlock(ew3.below()) && allNeighborsEmpty(bhr, ew3, fb15.getOpposite())) {
                    this.placeGrownFlower(bhr, ew3, integer7 + 1);
                    boolean10 = true;
                }
            }
            if (boolean10) {
                bhr.setBlock(ew, this.plant.getStateForPlacement(bhr, ew), 2);
            }
            else {
                this.placeDeadFlower(bhr, ew);
            }
        }
        else {
            this.placeDeadFlower(bhr, ew);
        }
    }
    
    private void placeGrownFlower(final Level bhr, final BlockPos ew, final int integer) {
        bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)ChorusFlowerBlock.AGE, integer), 2);
        bhr.levelEvent(1033, ew, 0);
    }
    
    private void placeDeadFlower(final Level bhr, final BlockPos ew) {
        bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)ChorusFlowerBlock.AGE, 5), 2);
        bhr.levelEvent(1034, ew, 0);
    }
    
    private static boolean allNeighborsEmpty(final LevelReader bhu, final BlockPos ew, @Nullable final Direction fb) {
        for (final Direction fb2 : Direction.Plane.HORIZONTAL) {
            if (fb2 != fb && !bhu.isEmptyBlock(ew.relative(fb2))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (fb != Direction.UP && !bvt1.canSurvive(bhs, ew5)) {
            bhs.getBlockTicks().scheduleTick(ew5, this, 1);
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    @Override
    public boolean canSurvive(final BlockState bvt, final LevelReader bhu, final BlockPos ew) {
        final BlockState bvt2 = bhu.getBlockState(ew.below());
        final Block bmv6 = bvt2.getBlock();
        if (bmv6 == this.plant || bmv6 == Blocks.END_STONE) {
            return true;
        }
        if (!bvt2.isAir()) {
            return false;
        }
        boolean boolean7 = false;
        for (final Direction fb9 : Direction.Plane.HORIZONTAL) {
            final BlockState bvt3 = bhu.getBlockState(ew.relative(fb9));
            if (bvt3.getBlock() == this.plant) {
                if (boolean7) {
                    return false;
                }
                boolean7 = true;
            }
            else {
                if (!bvt3.isAir()) {
                    return false;
                }
                continue;
            }
        }
        return boolean7;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(ChorusFlowerBlock.AGE);
    }
    
    public static void generatePlant(final LevelAccessor bhs, final BlockPos ew, final Random random, final int integer) {
        bhs.setBlock(ew, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).getStateForPlacement(bhs, ew), 2);
        growTreeRecursive(bhs, ew, random, ew, integer, 0);
    }
    
    private static void growTreeRecursive(final LevelAccessor bhs, final BlockPos ew2, final Random random, final BlockPos ew4, final int integer5, final int integer6) {
        final ChorusPlantBlock bnm7 = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
        int integer7 = random.nextInt(4) + 1;
        if (integer6 == 0) {
            ++integer7;
        }
        for (int integer8 = 0; integer8 < integer7; ++integer8) {
            final BlockPos ew5 = ew2.above(integer8 + 1);
            if (!allNeighborsEmpty(bhs, ew5, null)) {
                return;
            }
            bhs.setBlock(ew5, bnm7.getStateForPlacement(bhs, ew5), 2);
            bhs.setBlock(ew5.below(), bnm7.getStateForPlacement(bhs, ew5.below()), 2);
        }
        boolean boolean9 = false;
        if (integer6 < 4) {
            int integer9 = random.nextInt(4);
            if (integer6 == 0) {
                ++integer9;
            }
            for (int integer10 = 0; integer10 < integer9; ++integer10) {
                final Direction fb12 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                final BlockPos ew6 = ew2.above(integer7).relative(fb12);
                if (Math.abs(ew6.getX() - ew4.getX()) < integer5) {
                    if (Math.abs(ew6.getZ() - ew4.getZ()) < integer5) {
                        if (bhs.isEmptyBlock(ew6) && bhs.isEmptyBlock(ew6.below()) && allNeighborsEmpty(bhs, ew6, fb12.getOpposite())) {
                            boolean9 = true;
                            bhs.setBlock(ew6, bnm7.getStateForPlacement(bhs, ew6), 2);
                            bhs.setBlock(ew6.relative(fb12.getOpposite()), bnm7.getStateForPlacement(bhs, ew6.relative(fb12.getOpposite())), 2);
                            growTreeRecursive(bhs, ew6, random, ew4, integer5, integer6 + 1);
                        }
                    }
                }
            }
        }
        if (!boolean9) {
            bhs.setBlock(ew2.above(integer7), ((AbstractStateHolder<O, BlockState>)Blocks.CHORUS_FLOWER.defaultBlockState()).<Comparable, Integer>setValue((Property<Comparable>)ChorusFlowerBlock.AGE, 5), 2);
        }
    }
    
    @Override
    public void onProjectileHit(final Level bhr, final BlockState bvt, final BlockHitResult csd, final Entity aio) {
        final BlockPos ew6 = csd.getBlockPos();
        Block.popResource(bhr, ew6, new ItemStack(this));
        bhr.destroyBlock(ew6, true);
    }
    
    static {
        AGE = BlockStateProperties.AGE_5;
    }
}
