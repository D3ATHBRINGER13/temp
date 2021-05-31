package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class SweetBerryBushBlock extends BushBlock implements BonemealableBlock {
    public static final IntegerProperty AGE;
    private static final VoxelShape SAPLING_SHAPE;
    private static final VoxelShape MID_GROWTH_SHAPE;
    
    public SweetBerryBushBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Integer>setValue((Property<Comparable>)SweetBerryBushBlock.AGE, 0));
    }
    
    @Override
    public ItemStack getCloneItemStack(final BlockGetter bhb, final BlockPos ew, final BlockState bvt) {
        return new ItemStack(Items.SWEET_BERRIES);
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        if (bvt.<Integer>getValue((Property<Integer>)SweetBerryBushBlock.AGE) == 0) {
            return SweetBerryBushBlock.SAPLING_SHAPE;
        }
        if (bvt.<Integer>getValue((Property<Integer>)SweetBerryBushBlock.AGE) < 3) {
            return SweetBerryBushBlock.MID_GROWTH_SHAPE;
        }
        return super.getShape(bvt, bhb, ew, csn);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        super.tick(bvt, bhr, ew, random);
        final int integer6 = bvt.<Integer>getValue((Property<Integer>)SweetBerryBushBlock.AGE);
        if (integer6 < 3 && random.nextInt(5) == 0 && bhr.getRawBrightness(ew.above(), 0) >= 9) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)SweetBerryBushBlock.AGE, integer6 + 1), 2);
        }
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        if (!(aio instanceof LivingEntity) || aio.getType() == EntityType.FOX) {
            return;
        }
        aio.makeStuckInBlock(bvt, new Vec3(0.800000011920929, 0.75, 0.800000011920929));
        if (!bhr.isClientSide && bvt.<Integer>getValue((Property<Integer>)SweetBerryBushBlock.AGE) > 0 && (aio.xOld != aio.x || aio.zOld != aio.z)) {
            final double double6 = Math.abs(aio.x - aio.xOld);
            final double double7 = Math.abs(aio.z - aio.zOld);
            if (double6 >= 0.003000000026077032 || double7 >= 0.003000000026077032) {
                aio.hurt(DamageSource.SWEET_BERRY_BUSH, 1.0f);
            }
        }
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        final int integer8 = bvt.<Integer>getValue((Property<Integer>)SweetBerryBushBlock.AGE);
        final boolean boolean9 = integer8 == 3;
        if (!boolean9 && awg.getItemInHand(ahi).getItem() == Items.BONE_MEAL) {
            return false;
        }
        if (integer8 > 1) {
            final int integer9 = 1 + bhr.random.nextInt(2);
            Block.popResource(bhr, ew, new ItemStack(Items.SWEET_BERRIES, integer9 + (boolean9 ? 1 : 0)));
            bhr.playSound(null, ew, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0f, 0.8f + bhr.random.nextFloat() * 0.4f);
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)SweetBerryBushBlock.AGE, 1), 2);
            return true;
        }
        return super.use(bvt, bhr, ew, awg, ahi, csd);
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(SweetBerryBushBlock.AGE);
    }
    
    @Override
    public boolean isValidBonemealTarget(final BlockGetter bhb, final BlockPos ew, final BlockState bvt, final boolean boolean4) {
        return bvt.<Integer>getValue((Property<Integer>)SweetBerryBushBlock.AGE) < 3;
    }
    
    @Override
    public boolean isBonemealSuccess(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        return true;
    }
    
    @Override
    public void performBonemeal(final Level bhr, final Random random, final BlockPos ew, final BlockState bvt) {
        final int integer6 = Math.min(3, bvt.<Integer>getValue((Property<Integer>)SweetBerryBushBlock.AGE) + 1);
        bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)SweetBerryBushBlock.AGE, integer6), 2);
    }
    
    static {
        AGE = BlockStateProperties.AGE_3;
        SAPLING_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
        MID_GROWTH_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    }
}
