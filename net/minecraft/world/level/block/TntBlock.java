package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class TntBlock extends Block {
    public static final BooleanProperty UNSTABLE;
    
    public TntBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.defaultBlockState()).<Comparable, Boolean>setValue((Property<Comparable>)TntBlock.UNSTABLE, false));
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (bvt4.getBlock() == bvt1.getBlock()) {
            return;
        }
        if (bhr.hasNeighborSignal(ew)) {
            explode(bhr, ew);
            bhr.removeBlock(ew, false);
        }
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bhr.hasNeighborSignal(ew3)) {
            explode(bhr, ew3);
            bhr.removeBlock(ew3, false);
        }
    }
    
    @Override
    public void playerWillDestroy(final Level bhr, final BlockPos ew, final BlockState bvt, final Player awg) {
        if (!bhr.isClientSide() && !awg.isCreative() && bvt.<Boolean>getValue((Property<Boolean>)TntBlock.UNSTABLE)) {
            explode(bhr, ew);
        }
        super.playerWillDestroy(bhr, ew, bvt, awg);
    }
    
    @Override
    public void wasExploded(final Level bhr, final BlockPos ew, final Explosion bhk) {
        if (bhr.isClientSide) {
            return;
        }
        final PrimedTnt aty5 = new PrimedTnt(bhr, ew.getX() + 0.5f, ew.getY(), ew.getZ() + 0.5f, bhk.getSourceMob());
        aty5.setFuse((short)(bhr.random.nextInt(aty5.getLife() / 4) + aty5.getLife() / 8));
        bhr.addFreshEntity(aty5);
    }
    
    public static void explode(final Level bhr, final BlockPos ew) {
        explode(bhr, ew, null);
    }
    
    private static void explode(final Level bhr, final BlockPos ew, @Nullable final LivingEntity aix) {
        if (bhr.isClientSide) {
            return;
        }
        final PrimedTnt aty4 = new PrimedTnt(bhr, ew.getX() + 0.5f, ew.getY(), ew.getZ() + 0.5f, aix);
        bhr.addFreshEntity(aty4);
        bhr.playSound(null, aty4.x, aty4.y, aty4.z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
    }
    
    @Override
    public boolean use(final BlockState bvt, final Level bhr, final BlockPos ew, final Player awg, final InteractionHand ahi, final BlockHitResult csd) {
        final ItemStack bcj8 = awg.getItemInHand(ahi);
        final Item bce9 = bcj8.getItem();
        if (bce9 == Items.FLINT_AND_STEEL || bce9 == Items.FIRE_CHARGE) {
            explode(bhr, ew, awg);
            bhr.setBlock(ew, Blocks.AIR.defaultBlockState(), 11);
            if (bce9 == Items.FLINT_AND_STEEL) {
                bcj8.<Player>hurtAndBreak(1, awg, (java.util.function.Consumer<Player>)(awg -> awg.broadcastBreakEvent(ahi)));
            }
            else {
                bcj8.shrink(1);
            }
            return true;
        }
        return super.use(bvt, bhr, ew, awg, ahi, csd);
    }
    
    @Override
    public void onProjectileHit(final Level bhr, final BlockState bvt, final BlockHitResult csd, final Entity aio) {
        if (!bhr.isClientSide && aio instanceof AbstractArrow) {
            final AbstractArrow awk6 = (AbstractArrow)aio;
            final Entity aio2 = awk6.getOwner();
            if (awk6.isOnFire()) {
                final BlockPos ew8 = csd.getBlockPos();
                explode(bhr, ew8, (aio2 instanceof LivingEntity) ? ((LivingEntity)aio2) : null);
                bhr.removeBlock(ew8, false);
            }
        }
    }
    
    @Override
    public boolean dropFromExplosion(final Explosion bhk) {
        return false;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(TntBlock.UNSTABLE);
    }
    
    static {
        UNSTABLE = BlockStateProperties.UNSTABLE;
    }
}
