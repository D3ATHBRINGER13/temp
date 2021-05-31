package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.Locale;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.MapItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.apache.logging.log4j.Logger;

public class ExplorationMapFunction extends LootItemConditionalFunction {
    private static final Logger LOGGER;
    public static final MapDecoration.Type DEFAULT_DECORATION;
    private final String destination;
    private final MapDecoration.Type mapDecoration;
    private final byte zoom;
    private final int searchRadius;
    private final boolean skipKnownStructures;
    
    private ExplorationMapFunction(final LootItemCondition[] arr, final String string, final MapDecoration.Type a, final byte byte4, final int integer, final boolean boolean6) {
        super(arr);
        this.destination = string;
        this.mapDecoration = a;
        this.zoom = byte4;
        this.searchRadius = integer;
        this.skipKnownStructures = boolean6;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(LootContextParams.BLOCK_POS);
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        if (bcj.getItem() != Items.MAP) {
            return bcj;
        }
        final BlockPos ew4 = coy.<BlockPos>getParamOrNull(LootContextParams.BLOCK_POS);
        if (ew4 != null) {
            final ServerLevel vk5 = coy.getLevel();
            final BlockPos ew5 = vk5.findNearestMapFeature(this.destination, ew4, this.searchRadius, this.skipKnownStructures);
            if (ew5 != null) {
                final ItemStack bcj2 = MapItem.create(vk5, ew5.getX(), ew5.getZ(), this.zoom, true, true);
                MapItem.renderBiomePreviewMap(vk5, bcj2);
                MapItemSavedData.addTargetDecoration(bcj2, ew5, "+", this.mapDecoration);
                bcj2.setHoverName(new TranslatableComponent("filled_map." + this.destination.toLowerCase(Locale.ROOT), new Object[0]));
                return bcj2;
            }
        }
        return bcj;
    }
    
    public static Builder makeExplorationMap() {
        return new Builder();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DEFAULT_DECORATION = MapDecoration.Type.MANSION;
    }
    
    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private String destination;
        private MapDecoration.Type mapDecoration;
        private byte zoom;
        private int searchRadius;
        private boolean skipKnownStructures;
        
        public Builder() {
            this.destination = "Buried_Treasure";
            this.mapDecoration = ExplorationMapFunction.DEFAULT_DECORATION;
            this.zoom = 2;
            this.searchRadius = 50;
            this.skipKnownStructures = true;
        }
        
        @Override
        protected Builder getThis() {
            return this;
        }
        
        public Builder setDestination(final String string) {
            this.destination = string;
            return this;
        }
        
        public Builder setMapDecoration(final MapDecoration.Type a) {
            this.mapDecoration = a;
            return this;
        }
        
        public Builder setZoom(final byte byte1) {
            this.zoom = byte1;
            return this;
        }
        
        public Builder setSkipKnownStructures(final boolean boolean1) {
            this.skipKnownStructures = boolean1;
            return this;
        }
        
        public LootItemFunction build() {
            return new ExplorationMapFunction(this.getConditions(), this.destination, this.mapDecoration, this.zoom, this.searchRadius, this.skipKnownStructures, null);
        }
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<ExplorationMapFunction> {
        protected Serializer() {
            super(new ResourceLocation("exploration_map"), ExplorationMapFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final ExplorationMapFunction cqc, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqc, jsonSerializationContext);
            if (!cqc.destination.equals("Buried_Treasure")) {
                jsonObject.add("destination", jsonSerializationContext.serialize(cqc.destination));
            }
            if (cqc.mapDecoration != ExplorationMapFunction.DEFAULT_DECORATION) {
                jsonObject.add("decoration", jsonSerializationContext.serialize(cqc.mapDecoration.toString().toLowerCase(Locale.ROOT)));
            }
            if (cqc.zoom != 2) {
                jsonObject.addProperty("zoom", (Number)cqc.zoom);
            }
            if (cqc.searchRadius != 50) {
                jsonObject.addProperty("search_radius", (Number)cqc.searchRadius);
            }
            if (!cqc.skipKnownStructures) {
                jsonObject.addProperty("skip_existing_chunks", Boolean.valueOf(cqc.skipKnownStructures));
            }
        }
        
        @Override
        public ExplorationMapFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            String string5 = jsonObject.has("destination") ? GsonHelper.getAsString(jsonObject, "destination") : "Buried_Treasure";
            string5 = (Feature.STRUCTURES_REGISTRY.containsKey(string5.toLowerCase(Locale.ROOT)) ? string5 : "Buried_Treasure");
            final String string6 = jsonObject.has("decoration") ? GsonHelper.getAsString(jsonObject, "decoration") : "mansion";
            MapDecoration.Type a7 = ExplorationMapFunction.DEFAULT_DECORATION;
            try {
                a7 = MapDecoration.Type.valueOf(string6.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException illegalArgumentException8) {
                ExplorationMapFunction.LOGGER.error(new StringBuilder().append("Error while parsing loot table decoration entry. Found {}. Defaulting to ").append(ExplorationMapFunction.DEFAULT_DECORATION).toString(), string6);
            }
            final byte byte8 = GsonHelper.getAsByte(jsonObject, "zoom", (byte)2);
            final int integer9 = GsonHelper.getAsInt(jsonObject, "search_radius", 50);
            final boolean boolean10 = GsonHelper.getAsBoolean(jsonObject, "skip_existing_chunks", true);
            return new ExplorationMapFunction(arr, string5, a7, byte8, integer9, boolean10, null);
        }
    }
}
