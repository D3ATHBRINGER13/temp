package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.Random;
import net.minecraft.world.level.storage.loot.LootContext;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;

public class ExplosionCondition implements LootItemCondition {
    private static final ExplosionCondition INSTANCE;
    
    private ExplosionCondition() {
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(LootContextParams.EXPLOSION_RADIUS);
    }
    
    public boolean test(final LootContext coy) {
        final Float float3 = coy.<Float>getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
        if (float3 != null) {
            final Random random4 = coy.getRandom();
            final float float4 = 1.0f / float3;
            return random4.nextFloat() <= float4;
        }
        return true;
    }
    
    public static Builder survivesExplosion() {
        return () -> ExplosionCondition.INSTANCE;
    }
    
    static {
        INSTANCE = new ExplosionCondition();
    }
    
    public static class Serializer extends LootItemCondition.Serializer<ExplosionCondition> {
        protected Serializer() {
            super(new ResourceLocation("survives_explosion"), ExplosionCondition.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final ExplosionCondition crg, final JsonSerializationContext jsonSerializationContext) {
        }
        
        @Override
        public ExplosionCondition deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            return ExplosionCondition.INSTANCE;
        }
    }
}
