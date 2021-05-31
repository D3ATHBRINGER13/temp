package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.HashBiMap;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.BiMap;

public class LootContextParamSets {
    private static final BiMap<ResourceLocation, LootContextParamSet> REGISTRY;
    public static final LootContextParamSet EMPTY;
    public static final LootContextParamSet CHEST;
    public static final LootContextParamSet FISHING;
    public static final LootContextParamSet ENTITY;
    public static final LootContextParamSet GIFT;
    public static final LootContextParamSet ADVANCEMENT_REWARD;
    public static final LootContextParamSet ALL_PARAMS;
    public static final LootContextParamSet BLOCK;
    
    private static LootContextParamSet register(final String string, final Consumer<LootContextParamSet.Builder> consumer) {
        final LootContextParamSet.Builder a3 = new LootContextParamSet.Builder();
        consumer.accept(a3);
        final LootContextParamSet cqx4 = a3.build();
        final ResourceLocation qv5 = new ResourceLocation(string);
        final LootContextParamSet cqx5 = (LootContextParamSet)LootContextParamSets.REGISTRY.put(qv5, cqx4);
        if (cqx5 != null) {
            throw new IllegalStateException(new StringBuilder().append("Loot table parameter set ").append(qv5).append(" is already registered").toString());
        }
        return cqx4;
    }
    
    @Nullable
    public static LootContextParamSet get(final ResourceLocation qv) {
        return (LootContextParamSet)LootContextParamSets.REGISTRY.get(qv);
    }
    
    @Nullable
    public static ResourceLocation getKey(final LootContextParamSet cqx) {
        return (ResourceLocation)LootContextParamSets.REGISTRY.inverse().get(cqx);
    }
    
    static {
        REGISTRY = (BiMap)HashBiMap.create();
        EMPTY = register("empty", (Consumer<LootContextParamSet.Builder>)(a -> {}));
        CHEST = register("chest", (Consumer<LootContextParamSet.Builder>)(a -> a.required(LootContextParams.BLOCK_POS).optional(LootContextParams.THIS_ENTITY)));
        FISHING = register("fishing", (Consumer<LootContextParamSet.Builder>)(a -> a.required(LootContextParams.BLOCK_POS).required(LootContextParams.TOOL)));
        ENTITY = register("entity", (Consumer<LootContextParamSet.Builder>)(a -> a.required(LootContextParams.THIS_ENTITY).required(LootContextParams.BLOCK_POS).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.KILLER_ENTITY).optional(LootContextParams.DIRECT_KILLER_ENTITY).optional(LootContextParams.LAST_DAMAGE_PLAYER)));
        GIFT = register("gift", (Consumer<LootContextParamSet.Builder>)(a -> a.required(LootContextParams.BLOCK_POS).required(LootContextParams.THIS_ENTITY)));
        ADVANCEMENT_REWARD = register("advancement_reward", (Consumer<LootContextParamSet.Builder>)(a -> a.required(LootContextParams.THIS_ENTITY).required(LootContextParams.BLOCK_POS)));
        ALL_PARAMS = register("generic", (Consumer<LootContextParamSet.Builder>)(a -> a.required(LootContextParams.THIS_ENTITY).required(LootContextParams.LAST_DAMAGE_PLAYER).required(LootContextParams.DAMAGE_SOURCE).required(LootContextParams.KILLER_ENTITY).required(LootContextParams.DIRECT_KILLER_ENTITY).required(LootContextParams.BLOCK_POS).required(LootContextParams.BLOCK_STATE).required(LootContextParams.BLOCK_ENTITY).required(LootContextParams.TOOL).required(LootContextParams.EXPLOSION_RADIUS)));
        BLOCK = register("block", (Consumer<LootContextParamSet.Builder>)(a -> a.required(LootContextParams.BLOCK_STATE).required(LootContextParams.BLOCK_POS).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.BLOCK_ENTITY).optional(LootContextParams.EXPLOSION_RADIUS)));
    }
}
