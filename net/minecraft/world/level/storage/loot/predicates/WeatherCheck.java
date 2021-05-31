package net.minecraft.world.level.storage.loot.predicates;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import javax.annotation.Nullable;

public class WeatherCheck implements LootItemCondition {
    @Nullable
    private final Boolean isRaining;
    @Nullable
    private final Boolean isThundering;
    
    private WeatherCheck(@Nullable final Boolean boolean1, @Nullable final Boolean boolean2) {
        this.isRaining = boolean1;
        this.isThundering = boolean2;
    }
    
    public boolean test(final LootContext coy) {
        final ServerLevel vk3 = coy.getLevel();
        return (this.isRaining == null || this.isRaining == vk3.isRaining()) && (this.isThundering == null || this.isThundering == vk3.isThundering());
    }
    
    public static class Serializer extends LootItemCondition.Serializer<WeatherCheck> {
        public Serializer() {
            super(new ResourceLocation("weather_check"), WeatherCheck.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final WeatherCheck crr, final JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("raining", crr.isRaining);
            jsonObject.addProperty("thundering", crr.isThundering);
        }
        
        @Override
        public WeatherCheck deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final Boolean boolean4 = jsonObject.has("raining") ? Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, "raining")) : null;
            final Boolean boolean5 = jsonObject.has("thundering") ? Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, "thundering")) : null;
            return new WeatherCheck(boolean4, boolean5, null);
        }
    }
}
