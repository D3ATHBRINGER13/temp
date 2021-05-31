package net.minecraft.world.level.levelgen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import java.util.List;

public class SpikeConfiguration implements FeatureConfiguration {
    private final boolean crystalInvulnerable;
    private final List<SpikeFeature.EndSpike> spikes;
    @Nullable
    private final BlockPos crystalBeamTarget;
    
    public SpikeConfiguration(final boolean boolean1, final List<SpikeFeature.EndSpike> list, @Nullable final BlockPos ew) {
        this.crystalInvulnerable = boolean1;
        this.spikes = list;
        this.crystalBeamTarget = ew;
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("crystalInvulnerable"), dynamicOps.createBoolean(this.crystalInvulnerable), dynamicOps.createString("spikes"), dynamicOps.createList(this.spikes.stream().map(a -> a.serialize((com.mojang.datafixers.types.DynamicOps<Object>)dynamicOps).getValue())), dynamicOps.createString("crystalBeamTarget"), (this.crystalBeamTarget == null) ? dynamicOps.createList(Stream.empty()) : dynamicOps.createList(IntStream.of(new int[] { this.crystalBeamTarget.getX(), this.crystalBeamTarget.getY(), this.crystalBeamTarget.getZ() }).mapToObj(dynamicOps::createInt)))));
    }
    
    public static <T> SpikeConfiguration deserialize(final Dynamic<T> dynamic) {
        final List<SpikeFeature.EndSpike> list2 = (List<SpikeFeature.EndSpike>)dynamic.get("spikes").asList(SpikeFeature.EndSpike::deserialize);
        final List<Integer> list3 = (List<Integer>)dynamic.get("crystalBeamTarget").asList(dynamic -> dynamic.asInt(0));
        BlockPos ew4;
        if (list3.size() == 3) {
            ew4 = new BlockPos((int)list3.get(0), (int)list3.get(1), (int)list3.get(2));
        }
        else {
            ew4 = null;
        }
        return new SpikeConfiguration(dynamic.get("crystalInvulnerable").asBoolean(false), list2, ew4);
    }
    
    public boolean isCrystalInvulnerable() {
        return this.crystalInvulnerable;
    }
    
    public List<SpikeFeature.EndSpike> getSpikes() {
        return this.spikes;
    }
    
    @Nullable
    public BlockPos getCrystalBeamTarget() {
        return this.crystalBeamTarget;
    }
}
