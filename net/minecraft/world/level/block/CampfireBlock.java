package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import java.util.Random;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import javax.annotation.Nullable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CampfireBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE;
    public static final BooleanProperty LIT;
    public static final BooleanProperty SIGNAL_FIRE;
    public static final BooleanProperty WATERLOGGED;
    public static final DirectionProperty FACING;
    
    public CampfireBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)CampfireBlock.LIT, true)).setValue((Property<Comparable>)CampfireBlock.SIGNAL_FIRE, false)).setValue((Property<Comparable>)CampfireBlock.WATERLOGGED, false)).<Comparable, Direction>setValue((Property<Comparable>)CampfireBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        if (bvt.<Boolean>getValue((Property<Boolean>)CampfireBlock.LIT)) {
            final BlockEntity btw8 = bhr.getBlockEntity(ew);
            if (btw8 instanceof CampfireBlockEntity) {
                final CampfireBlockEntity btz9 = (CampfireBlockEntity)btw8;
                final ItemStack bcj10 = awg.getItemInHand(ahi);
                final Optional<CampfireCookingRecipe> optional11 = btz9.getCookableRecipe(bcj10);
                if (optional11.isPresent()) {
                    if (!bhr.isClientSide && btz9.placeFood(awg.abilities.instabuild ? bcj10.copy() : bcj10, ((CampfireCookingRecipe)optional11.get()).getCookingTime())) {
                        awg.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void entityInside(final BlockState bvt, final Level bhr, final BlockPos ew, final Entity aio) {
        if (!aio.fireImmune() && bvt.<Boolean>getValue((Property<Boolean>)CampfireBlock.LIT) && aio instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)aio)) {
            aio.hurt(DamageSource.IN_FIRE, 1.0f);
        }
        super.entityInside(bvt, bhr, ew, aio);
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt1.getBlock() == bvt4.getBlock()) {
            return;
        }
        final BlockEntity btw7 = bhr.getBlockEntity(ew);
        if (btw7 instanceof CampfireBlockEntity) {
            Containers.dropContents(bhr, ew, ((CampfireBlockEntity)btw7).getItems());
        }
        super.onRemove(bvt1, bhr, ew, bvt4, boolean5);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final LevelAccessor bhs3 = ban.getLevel();
        final BlockPos ew4 = ban.getClickedPos();
        final boolean boolean5 = bhs3.getFluidState(ew4).getType() == Fluids.WATER;
        return (((((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).setValue((Property<Comparable>)CampfireBlock.WATERLOGGED, boolean5)).setValue((Property<Comparable>)CampfireBlock.SIGNAL_FIRE, this.isSmokeSource(bhs3.getBlockState(ew4.below())))).setValue((Property<Comparable>)CampfireBlock.LIT, !boolean5)).<Comparable, Direction>setValue((Property<Comparable>)CampfireBlock.FACING, ban.getHorizontalDirection());
    }
    
    @Override
    public BlockState updateShape(final BlockState bvt1, final Direction fb, final BlockState bvt3, final LevelAccessor bhs, final BlockPos ew5, final BlockPos ew6) {
        if (bvt1.<Boolean>getValue((Property<Boolean>)CampfireBlock.WATERLOGGED)) {
            bhs.getLiquidTicks().scheduleTick(ew5, Fluids.WATER, Fluids.WATER.getTickDelay(bhs));
        }
        if (fb == Direction.DOWN) {
            return ((AbstractStateHolder<O, BlockState>)bvt1).<Comparable, Boolean>setValue((Property<Comparable>)CampfireBlock.SIGNAL_FIRE, this.isSmokeSource(bvt3));
        }
        return super.updateShape(bvt1, fb, bvt3, bhs, ew5, ew6);
    }
    
    private boolean isSmokeSource(final BlockState bvt) {
        return bvt.getBlock() == Blocks.HAY_BLOCK;
    }
    
    @Override
    public int getLightEmission(final BlockState bvt) {
        return bvt.<Boolean>getValue((Property<Boolean>)CampfireBlock.LIT) ? super.getLightEmission(bvt) : 0;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        return CampfireBlock.SHAPE;
    }
    
    @Override
    public RenderShape getRenderShape(final BlockState bvt) {
        return RenderShape.MODEL;
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)CampfireBlock.LIT)) {
            return;
        }
        if (random.nextInt(10) == 0) {
            bhr.playLocalSound(ew.getX() + 0.5f, ew.getY() + 0.5f, ew.getZ() + 0.5f, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5f + random.nextFloat(), random.nextFloat() * 0.7f + 0.6f, false);
        }
        if (random.nextInt(5) == 0) {
            for (int integer6 = 0; integer6 < random.nextInt(1) + 1; ++integer6) {
                bhr.addParticle(ParticleTypes.LAVA, ew.getX() + 0.5f, ew.getY() + 0.5f, ew.getZ() + 0.5f, random.nextFloat() / 2.0f, 5.0E-5, random.nextFloat() / 2.0f);
            }
        }
    }
    
    @Override
    public boolean placeLiquid(final LevelAccessor bhs, final BlockPos ew, final BlockState bvt, final FluidState clk) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)BlockStateProperties.WATERLOGGED) && clk.getType() == Fluids.WATER) {
            final boolean boolean6 = bvt.<Boolean>getValue((Property<Boolean>)CampfireBlock.LIT);
            if (boolean6) {
                if (bhs.isClientSide()) {
                    for (int integer7 = 0; integer7 < 20; ++integer7) {
                        makeParticles(bhs.getLevel(), ew, bvt.<Boolean>getValue((Property<Boolean>)CampfireBlock.SIGNAL_FIRE), true);
                    }
                }
                else {
                    bhs.playSound(null, ew, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                final BlockEntity btw7 = bhs.getBlockEntity(ew);
                if (btw7 instanceof CampfireBlockEntity) {
                    ((CampfireBlockEntity)btw7).dowse();
                }
            }
            bhs.setBlock(ew, (((AbstractStateHolder<O, BlockState>)bvt).setValue((Property<Comparable>)CampfireBlock.WATERLOGGED, true)).<Comparable, Boolean>setValue((Property<Comparable>)CampfireBlock.LIT, false), 3);
            bhs.getLiquidTicks().scheduleTick(ew, clk.getType(), clk.getType().getTickDelay(bhs));
            return true;
        }
        return false;
    }
    
    @Override
    public void onProjectileHit(final Level bhr, final BlockState bvt, final BlockHitResult csd, final Entity aio) {
        if (!bhr.isClientSide && aio instanceof AbstractArrow) {
            final AbstractArrow awk6 = (AbstractArrow)aio;
            if (awk6.isOnFire() && !bvt.<Boolean>getValue((Property<Boolean>)CampfireBlock.LIT) && !bvt.<Boolean>getValue((Property<Boolean>)CampfireBlock.WATERLOGGED)) {
                final BlockPos ew7 = csd.getBlockPos();
                bhr.setBlock(ew7, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)BlockStateProperties.LIT, true), 11);
            }
        }
    }
    
    public static void makeParticles(final Level bhr, final BlockPos ew, final boolean boolean3, final boolean boolean4) {
        final Random random5 = bhr.getRandom();
        final SimpleParticleType gi6 = boolean3 ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        bhr.addAlwaysVisibleParticle(gi6, true, ew.getX() + 0.5 + random5.nextDouble() / 3.0 * (random5.nextBoolean() ? 1 : -1), ew.getY() + random5.nextDouble() + random5.nextDouble(), ew.getZ() + 0.5 + random5.nextDouble() / 3.0 * (random5.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if (boolean4) {
            bhr.addParticle(ParticleTypes.SMOKE, ew.getX() + 0.25 + random5.nextDouble() / 2.0 * (random5.nextBoolean() ? 1 : -1), ew.getY() + 0.4, ew.getZ() + 0.25 + random5.nextDouble() / 2.0 * (random5.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
        }
    }
    
    @Override
    public FluidState getFluidState(final BlockState bvt) {
        if (bvt.<Boolean>getValue((Property<Boolean>)CampfireBlock.WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(bvt);
    }
    
    @Override
    public BlockState rotate(final BlockState bvt, final Rotation brg) {
        return ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Direction>setValue((Property<Comparable>)CampfireBlock.FACING, brg.rotate(bvt.<Direction>getValue((Property<Direction>)CampfireBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState bvt, final Mirror bqg) {
        return bvt.rotate(bqg.getRotation(bvt.<Direction>getValue((Property<Direction>)CampfireBlock.FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(CampfireBlock.LIT, CampfireBlock.SIGNAL_FIRE, CampfireBlock.WATERLOGGED, CampfireBlock.FACING);
    }
    
    @Override
    public BlockEntity newBlockEntity(final BlockGetter bhb) {
        return new CampfireBlockEntity();
    }
    
    @Override
    public boolean isPathfindable(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final PathComputationType cns) {
        return false;
    }
    
    static {
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
        LIT = BlockStateProperties.LIT;
        SIGNAL_FIRE = BlockStateProperties.SIGNAL_FIRE;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        FACING = BlockStateProperties.HORIZONTAL_FACING;
    }
}
