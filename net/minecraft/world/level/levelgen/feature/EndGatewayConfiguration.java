package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.core.BlockPos;
import java.util.Optional;

public class EndGatewayConfiguration implements FeatureConfiguration {
    private final Optional<BlockPos> exit;
    private final boolean exact;
    
    private EndGatewayConfiguration(final Optional<BlockPos> optional, final boolean boolean2) {
        this.exit = optional;
        this.exact = boolean2;
    }
    
    public static EndGatewayConfiguration knownExit(final BlockPos ew, final boolean boolean2) {
        return new EndGatewayConfiguration((Optional<BlockPos>)Optional.of(ew), boolean2);
    }
    
    public static EndGatewayConfiguration delayedExitSearch() {
        return new EndGatewayConfiguration((Optional<BlockPos>)Optional.empty(), false);
    }
    
    public Optional<BlockPos> getExit() {
        return this.exit;
    }
    
    public boolean isExitExact() {
        return this.exact;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, this.exit.map(ew -> dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("exit_x"), dynamicOps.createInt(ew.getX()), dynamicOps.createString("exit_y"), dynamicOps.createInt(ew.getY()), dynamicOps.createString("exit_z"), dynamicOps.createInt(ew.getZ()), dynamicOps.createString("exact"), dynamicOps.createBoolean(this.exact)))).orElse(dynamicOps.emptyMap()));
    }
    
    public static <T> EndGatewayConfiguration deserialize(final Dynamic<T> dynamic) {
        final Optional<BlockPos> optional2 = (Optional<BlockPos>)dynamic.get("exit_x").asNumber().flatMap(number -> dynamic.get("exit_y").asNumber().flatMap(number3 -> dynamic.get("exit_z").asNumber().map(number3 -> new BlockPos(number.intValue(), number3.intValue(), number3.intValue()))));
        final boolean boolean3 = dynamic.get("exact").asBoolean(false);
        return new EndGatewayConfiguration(optional2, boolean3);
    }
}
