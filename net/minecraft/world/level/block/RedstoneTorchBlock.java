package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import java.util.WeakHashMap;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import com.google.common.collect.Lists;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.DustParticleOptions;
import java.util.Random;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import net.minecraft.world.level.BlockGetter;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RedstoneTorchBlock extends TorchBlock {
    public static final BooleanProperty LIT;
    private static final Map<BlockGetter, List<Toggle>> RECENT_TOGGLES;
    
    protected RedstoneTorchBlock(final Properties c) {
        super(c);
        this.registerDefaultState(((AbstractStateHolder<O, BlockState>)this.stateDefinition.any()).<Comparable, Boolean>setValue((Property<Comparable>)RedstoneTorchBlock.LIT, true));
    }
    
    @Override
    public int getTickDelay(final LevelReader bhu) {
        return 2;
    }
    
    @Override
    public void onPlace(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        for (final Direction fb10 : Direction.values()) {
            bhr.updateNeighborsAt(ew.relative(fb10), this);
        }
    }
    
    @Override
    public void onRemove(final BlockState bvt1, final Level bhr, final BlockPos ew, final BlockState bvt4, final boolean boolean5) {
        if (boolean5) {
            return;
        }
        for (final Direction fb10 : Direction.values()) {
            bhr.updateNeighborsAt(ew.relative(fb10), this);
        }
    }
    
    @Override
    public int getSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (bvt.<Boolean>getValue((Property<Boolean>)RedstoneTorchBlock.LIT) && Direction.UP != fb) {
            return 15;
        }
        return 0;
    }
    
    protected boolean hasNeighborSignal(final Level bhr, final BlockPos ew, final BlockState bvt) {
        return bhr.hasSignal(ew.below(), Direction.DOWN);
    }
    
    @Override
    public void tick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        handleTick(bvt, bhr, ew, random, this.hasNeighborSignal(bhr, ew, bvt));
    }
    
    public static void handleTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random, final boolean boolean5) {
        final List<Toggle> list6 = (List<Toggle>)RedstoneTorchBlock.RECENT_TOGGLES.get(bhr);
        while (list6 != null && !list6.isEmpty() && bhr.getGameTime() - ((Toggle)list6.get(0)).when > 60L) {
            list6.remove(0);
        }
        if (bvt.<Boolean>getValue((Property<Boolean>)RedstoneTorchBlock.LIT)) {
            if (boolean5) {
                bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)RedstoneTorchBlock.LIT, false), 3);
                if (isToggledTooFrequently(bhr, ew, true)) {
                    bhr.levelEvent(1502, ew, 0);
                    bhr.getBlockTicks().scheduleTick(ew, bhr.getBlockState(ew).getBlock(), 160);
                }
            }
        }
        else if (!boolean5 && !isToggledTooFrequently(bhr, ew, false)) {
            bhr.setBlock(ew, ((AbstractStateHolder<O, BlockState>)bvt).<Comparable, Boolean>setValue((Property<Comparable>)RedstoneTorchBlock.LIT, true), 3);
        }
    }
    
    @Override
    public void neighborChanged(final BlockState bvt, final Level bhr, final BlockPos ew3, final Block bmv, final BlockPos ew5, final boolean boolean6) {
        if (bvt.<Boolean>getValue((Property<Boolean>)RedstoneTorchBlock.LIT) == this.hasNeighborSignal(bhr, ew3, bvt) && !bhr.getBlockTicks().willTickThisTick(ew3, this)) {
            bhr.getBlockTicks().scheduleTick(ew3, this, this.getTickDelay(bhr));
        }
    }
    
    @Override
    public int getDirectSignal(final BlockState bvt, final BlockGetter bhb, final BlockPos ew, final Direction fb) {
        if (fb == Direction.DOWN) {
            return bvt.getSignal(bhb, ew, fb);
        }
        return 0;
    }
    
    @Override
    public boolean isSignalSource(final BlockState bvt) {
        return true;
    }
    
    @Override
    public void animateTick(final BlockState bvt, final Level bhr, final BlockPos ew, final Random random) {
        if (!bvt.<Boolean>getValue((Property<Boolean>)RedstoneTorchBlock.LIT)) {
            return;
        }
        final double double6 = ew.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        final double double7 = ew.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
        final double double8 = ew.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        bhr.addParticle(DustParticleOptions.REDSTONE, double6, double7, double8, 0.0, 0.0, 0.0);
    }
    
    @Override
    public int getLightEmission(final BlockState bvt) {
        return bvt.<Boolean>getValue((Property<Boolean>)RedstoneTorchBlock.LIT) ? super.getLightEmission(bvt) : 0;
    }
    
    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> a) {
        a.add(RedstoneTorchBlock.LIT);
    }
    
    private static boolean isToggledTooFrequently(final Level bhr, final BlockPos ew, final boolean boolean3) {
        final List<Toggle> list4 = (List<Toggle>)RedstoneTorchBlock.RECENT_TOGGLES.computeIfAbsent(bhr, bhb -> Lists.newArrayList());
        if (boolean3) {
            list4.add(new Toggle(ew.immutable(), bhr.getGameTime()));
        }
        int integer5 = 0;
        for (int integer6 = 0; integer6 < list4.size(); ++integer6) {
            final Toggle a7 = (Toggle)list4.get(integer6);
            if (a7.pos.equals(ew) && ++integer5 >= 8) {
                return true;
            }
        }
        return false;
    }
    
    static {
        LIT = BlockStateProperties.LIT;
        RECENT_TOGGLES = (Map)new WeakHashMap();
    }
    
    public static class Toggle {
        private final BlockPos pos;
        private final long when;
        
        public Toggle(final BlockPos ew, final long long2) {
            this.pos = ew;
            this.when = long2;
        }
    }
}
