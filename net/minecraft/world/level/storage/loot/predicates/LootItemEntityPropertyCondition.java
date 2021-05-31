package net.minecraft.world.level.storage.loot.predicates;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.advancements.critereon.EntityPredicate;

public class LootItemEntityPropertyCondition implements LootItemCondition {
    private final EntityPredicate predicate;
    private final LootContext.EntityTarget entityTarget;
    
    private LootItemEntityPropertyCondition(final EntityPredicate av, final LootContext.EntityTarget c) {
        this.predicate = av;
        this.entityTarget = c;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(LootContextParams.BLOCK_POS, this.entityTarget.getParam());
    }
    
    public boolean test(final LootContext coy) {
        final Entity aio3 = coy.<Entity>getParamOrNull(this.entityTarget.getParam());
        final BlockPos ew4 = coy.<BlockPos>getParamOrNull(LootContextParams.BLOCK_POS);
        return ew4 != null && this.predicate.matches(coy.getLevel(), new Vec3(ew4), aio3);
    }
    
    public static Builder entityPresent(final LootContext.EntityTarget c) {
        return hasProperties(c, EntityPredicate.Builder.entity());
    }
    
    public static Builder hasProperties(final LootContext.EntityTarget c, final EntityPredicate.Builder a) {
        return () -> new LootItemEntityPropertyCondition(a.build(), c);
    }
    
    public static class Serializer extends LootItemCondition.Serializer<LootItemEntityPropertyCondition> {
        protected Serializer() {
            super(new ResourceLocation("entity_properties"), LootItemEntityPropertyCondition.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final LootItemEntityPropertyCondition crm, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", crm.predicate.serializeToJson());
            jsonObject.add("entity", jsonSerializationContext.serialize(crm.entityTarget));
        }
        
        @Override
        public LootItemEntityPropertyCondition deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final EntityPredicate av4 = EntityPredicate.fromJson(jsonObject.get("predicate"));
            return new LootItemEntityPropertyCondition(av4, GsonHelper.<LootContext.EntityTarget>getAsObject(jsonObject, "entity", jsonDeserializationContext, (java.lang.Class<? extends LootContext.EntityTarget>)LootContext.EntityTarget.class), null);
        }
    }
}
