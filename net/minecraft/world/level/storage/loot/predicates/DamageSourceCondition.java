package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import net.minecraft.advancements.critereon.DamageSourcePredicate;

public class DamageSourceCondition implements LootItemCondition {
    private final DamageSourcePredicate predicate;
    
    private DamageSourceCondition(final DamageSourcePredicate am) {
        this.predicate = am;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(LootContextParams.BLOCK_POS, LootContextParams.DAMAGE_SOURCE);
    }
    
    public boolean test(final LootContext coy) {
        final DamageSource ahx3 = coy.<DamageSource>getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        final BlockPos ew4 = coy.<BlockPos>getParamOrNull(LootContextParams.BLOCK_POS);
        return ew4 != null && ahx3 != null && this.predicate.matches(coy.getLevel(), new Vec3(ew4), ahx3);
    }
    
    public static Builder hasDamageSource(final DamageSourcePredicate.Builder a) {
        return () -> new DamageSourceCondition(a.build());
    }
    
    public static class Serializer extends LootItemCondition.Serializer<DamageSourceCondition> {
        protected Serializer() {
            super(new ResourceLocation("damage_source_properties"), DamageSourceCondition.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final DamageSourceCondition cre, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", cre.predicate.serializeToJson());
        }
        
        @Override
        public DamageSourceCondition deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final DamageSourcePredicate am4 = DamageSourcePredicate.fromJson(jsonObject.get("predicate"));
            return new DamageSourceCondition(am4, null);
        }
    }
}
