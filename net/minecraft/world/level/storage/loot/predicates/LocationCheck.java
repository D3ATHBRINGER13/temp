package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.advancements.critereon.LocationPredicate;

public class LocationCheck implements LootItemCondition {
    private final LocationPredicate predicate;
    
    private LocationCheck(final LocationPredicate bg) {
        this.predicate = bg;
    }
    
    public boolean test(final LootContext coy) {
        final BlockPos ew3 = coy.<BlockPos>getParamOrNull(LootContextParams.BLOCK_POS);
        return ew3 != null && this.predicate.matches(coy.getLevel(), (float)ew3.getX(), (float)ew3.getY(), (float)ew3.getZ());
    }
    
    public static Builder checkLocation(final LocationPredicate.Builder a) {
        return () -> new LocationCheck(a.build());
    }
    
    public static class Serializer extends LootItemCondition.Serializer<LocationCheck> {
        public Serializer() {
            super(new ResourceLocation("location_check"), LocationCheck.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final LocationCheck cri, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", cri.predicate.serializeToJson());
        }
        
        @Override
        public LocationCheck deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final LocationPredicate bg4 = LocationPredicate.fromJson(jsonObject.get("predicate"));
            return new LocationCheck(bg4, null);
        }
    }
}
