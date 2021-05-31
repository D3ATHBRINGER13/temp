package net.minecraft.world.level.material;

import net.minecraft.world.level.block.state.properties.Property;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.AbstractStateHolder;

public class FluidStateImpl extends AbstractStateHolder<Fluid, FluidState> implements FluidState {
    public FluidStateImpl(final Fluid clj, final ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
        super(clj, immutableMap);
    }
    
    @Override
    public Fluid getType() {
        return (Fluid)this.owner;
    }
}
