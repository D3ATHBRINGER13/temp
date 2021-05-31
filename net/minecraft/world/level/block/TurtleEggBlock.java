package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.level.BlockGetter;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurtleEggBlock extends Block {
    private static final VoxelShape ONE_EGG_AABB;
    private static final VoxelShape MULTIPLE_EGGS_AABB;
    public static final IntegerProperty HATCH;
    public static final IntegerProperty EGGS;
    
    public TurtleEggBlock(final Properties c) {
        super(c);
        this.registerDefaultState((((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).setValue((Property<Comparable>)TurtleEggBlock.HATCH, 0)).<Comparable, Integer>setValue((Property<Comparable>)TurtleEggBlock.EGGS, 1));
    }
    
    @Override
    public void stepOn(final Level bhr, final BlockPos ew, final Entity aio) {
        this.destroyEgg(bhr, ew, aio, 100);
        super.stepOn(bhr, ew, aio);
    }
    
    @Override
    public void fallOn(final Level bhr, final BlockPos ew, final Entity aio, final float float4) {
        if (!(aio instanceof Zombie)) {
            this.destroyEgg(bhr, ew, aio, 3);
        }
        super.fallOn(bhr, ew, aio, float4);
    }
    
    private void destroyEgg(final Level bhr, final BlockPos ew, final Entity aio, final int integer) {
        if (!this.canDestroyEgg(bhr, aio)) {
            super.stepOn(bhr, ew, aio);
            return;
        }
        if (!bhr.isClientSide && bhr.random.nextInt(integer) == 0) {
            this.decreaseEggs(bhr, ew, bhr.getBlockState(ew));
        }
    }
    
    private void decreaseEggs(final Level bhr, final BlockPos ew, final BlockState bvt) {
        bhr.playSound(null, ew, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7f, 0.9f + bhr.random.nextFloat() * 0.2f);
        final int integer5 = bvt.<Integer>getValue((Property<Integer>)TurtleEggBlock.EGGS);
        if (integer5 <= 1) {
            bhr.destroyBlock(ew, false);
        }
        else {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)TurtleEggBlock.EGGS, integer5 - 1), 2);
            bhr.levelEvent(2001, ew, Block.getId(bvt));
        }
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (this.shouldUpdateHatchLevel(bhr) && this.onSand(bhr, ew)) {
            final int integer6 = bvt.<Integer>getValue((Property<Integer>)TurtleEggBlock.HATCH);
            if (integer6 < 2) {
                bhr.playSound(null, ew, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Integer>setValue((Property<Comparable>)TurtleEggBlock.HATCH, integer6 + 1), 2);
            }
            else {
                bhr.playSound(null, ew, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                bhr.removeBlock(ew, false);
                if (!bhr.isClientSide) {
                    for (int integer7 = 0; integer7 < bvt.<Integer>getValue((Property<Integer>)TurtleEggBlock.EGGS); ++integer7) {
                        bhr.levelEvent(2001, ew, Block.getId(bvt));
                        final Turtle arx8 = EntityType.TURTLE.create(bhr);
                        arx8.setAge(-24000);
                        arx8.setHomePos(ew);
                        arx8.moveTo(ew.getX() + 0.3 + integer7 * 0.2, ew.getY(), ew.getZ() + 0.3, 0.0f, 0.0f);
                        bhr.addFreshEntity(arx8);
                    }
                }
            }
        }
    }
    
    private boolean onSand(final BlockGetter bhb, final BlockPos ew) {
        return bhb.getBlockState(ew.below()).getBlock() == Blocks.SAND;
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (this.onSand(bhr, ew) && !bhr.isClientSide) {
            bhr.levelEvent(2005, ew, 0);
        }
    }
    
    private boolean shouldUpdateHatchLevel(final Level bhr) {
        final float float3 = bhr.getTimeOfDay(1.0f);
        return (float3 < 0.69 && float3 > 0.65) || bhr.random.nextInt(500) == 0;
    }
    
    @Override
    public void playerDestroy(final Level bhr, final Player awg, final BlockPos ew, final BlockState bvt, @Nullable final BlockEntity btw, final ItemStack bcj) {
        super.playerDestroy(bhr, awg, ew, bvt, btw, bcj);
        this.decreaseEggs(bhr, ew, bvt);
    }
    
    @Override
    public boolean canBeReplaced(final BlockState bvt, final BlockPlaceContext ban) {
        return (ban.getItemInHand().getItem() == this.asItem() && bvt.<Integer>getValue((Property<Integer>)TurtleEggBlock.EGGS) < 4) || super.canBeReplaced(bvt, ban);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext ban) {
        final BlockState bvt3 = ban.getLevel().getBlockState(ban.getClickedPos());
        if (bvt3.getBlock() == this) {
            return ((AbstractStateHolder<O, BlockState>)bvt3).<Comparable, Integer>setValue((Property<Comparable>)TurtleEggBlock.EGGS, Math.min(4, bvt3.<Integer>getValue((Property<Integer>)TurtleEggBlock.EGGS) + 1));
        }
        return super.getStateForPlacement(ban);
    }
    
    @Override
    public BlockLayer getRenderLayer() {
        return BlockLayer.CUTOUT;
    }
    
    @Override
    public VoxelShape getShape(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final CollisionContext csn) {
        if (bvt.<Integer>getValue((Property<Integer>)TurtleEggBlock.EGGS) > 1) {
            return TurtleEggBlock.MULTIPLE_EGGS_AABB;
        }
        return TurtleEggBlock.ONE_EGG_AABB;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(TurtleEggBlock.HATCH, TurtleEggBlock.EGGS);
    }
    
    private boolean canDestroyEgg(final Level bhr, final Entity aio) {
        return !(aio instanceof Turtle) && (!(aio instanceof LivingEntity) || aio instanceof Player || bhr.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING));
    }
    
    static {
        ONE_EGG_AABB = Block.box(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
        MULTIPLE_EGGS_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
        HATCH = BlockStateProperties.HATCH;
        EGGS = BlockStateProperties.EGGS;
    }
}
