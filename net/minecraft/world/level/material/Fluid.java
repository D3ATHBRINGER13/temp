package net.minecraft.world.level.material;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.IdMapper;

public abstract class Fluid {
    public static final IdMapper<FluidState> FLUID_STATE_REGISTRY;
    protected final StateDefinition<Fluid, FluidState> stateDefinition;
    private FluidState defaultFluidState;
    
    protected Fluid() {
        final StateDefinition.Builder<Fluid, FluidState> a2 = new StateDefinition.Builder<Fluid, FluidState>(this);
        this.createFluidStateDefinition(a2);
        this.stateDefinition = a2.<FluidStateImpl>create(FluidStateImpl::new);
        this.registerDefaultState(this.stateDefinition.any());
    }
    
    protected void createFluidStateDefinition(final StateDefinition.Builder<Fluid, FluidState> a) {
    }
    
    public StateDefinition<Fluid, FluidState> getStateDefinition() {
        return this.stateDefinition;
    }
    
    protected final void registerDefaultState(final FluidState clk) {
        this.defaultFluidState = clk;
    }
    
    public final FluidState defaultFluidState() {
        return this.defaultFluidState;
    }
    
    protected abstract BlockLayer getRenderLayer();
    
    public abstract Item getBucket();
    
    protected void animateTick(final Level bhr, final BlockPos ew, final FluidState clk, final Random random) {
    }
    
    protected void tick(final Level bhr, final BlockPos ew, final FluidState clk) {
    }
    
    protected void randomTick(final Level bhr, final BlockPos ew, final FluidState clk, final Random random) {
    }
    
    @Nullable
    protected ParticleOptions getDripParticle() {
        return null;
    }
    
    protected abstract boolean canBeReplacedWith(final FluidState clk, final BlockGetter bhb, final BlockPos ew, final Fluid clj, final Direction fb);
    
    protected abstract Vec3 getFlow(final BlockGetter bhb, final BlockPos ew, final FluidState clk);
    
    public abstract int getTickDelay(final LevelReader bhu);
    
    protected boolean isRandomlyTicking() {
        return false;
    }
    
    protected boolean isEmpty() {
        return false;
    }
    
    protected abstract float getExplosionResistance();
    
    public abstract float getHeight(final FluidState clk, final BlockGetter bhb, final BlockPos ew);
    
    public abstract float getOwnHeight(final FluidState clk);
    
    protected abstract BlockState createLegacyBlock(final FluidState clk);
    
    public abstract boolean isSource(final FluidState clk);
    
    public abstract int getAmount(final FluidState clk);
    
    public boolean isSame(final Fluid clj) {
        return clj == this;
    }
    
    public boolean is(final Tag<Fluid> zg) {
        return zg.contains(this);
    }
    
    public abstract VoxelShape getShape(final FluidState clk, final BlockGetter bhb, final BlockPos ew);
    
    static {
        FLUID_STATE_REGISTRY = new IdMapper<FluidState>();
    }
}
