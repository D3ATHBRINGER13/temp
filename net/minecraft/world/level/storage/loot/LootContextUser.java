package net.minecraft.world.level.storage.loot;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Function;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;

public interface LootContextUser {
    default Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of();
    }
    
    default void validate(final LootTableProblemCollector cpc, final Function<ResourceLocation, LootTable> function, final Set<ResourceLocation> set, final LootContextParamSet cqx) {
        cqx.validateUser(cpc, this);
    }
}
