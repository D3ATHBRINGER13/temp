package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.material.Fluids;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.material.FluidState;

public class SpringConfiguration implements FeatureConfiguration {
    public final FluidState state;
    
    public SpringConfiguration(final FluidState clk) {
        this.state = clk;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("state"), FluidState.<T>serialize(dynamicOps, this.state).getValue())));
    }
    
    public static <T> SpringConfiguration deserialize(final Dynamic<T> dynamic) {
        final FluidState clk2 = (FluidState)dynamic.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.defaultFluidState());
        return new SpringConfiguration(clk2);
    }
}
