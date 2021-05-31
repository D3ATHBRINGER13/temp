package net.minecraft.data.info;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Path;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.resources.ResourceLocation;
import java.util.Iterator;
import net.minecraft.world.level.block.state.BlockState;
import com.google.gson.JsonElement;
import net.minecraft.Util;
import com.google.gson.JsonArray;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Registry;
import com.google.gson.JsonObject;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import net.minecraft.data.DataProvider;

public class BlockListReport implements DataProvider {
    private static final Gson GSON;
    private final DataGenerator generator;
    
    public BlockListReport(final DataGenerator gk) {
        this.generator = gk;
    }
    
    public void run(final HashCache gm) throws IOException {
        final JsonObject jsonObject3 = new JsonObject();
        for (final Block bmv5 : Registry.BLOCK) {
            final ResourceLocation qv6 = Registry.BLOCK.getKey(bmv5);
            final JsonObject jsonObject4 = new JsonObject();
            final StateDefinition<Block, BlockState> bvu8 = bmv5.getStateDefinition();
            if (!bvu8.getProperties().isEmpty()) {
                final JsonObject jsonObject5 = new JsonObject();
                for (final Property<?> bww11 : bvu8.getProperties()) {
                    final JsonArray jsonArray12 = new JsonArray();
                    for (final Comparable<?> comparable14 : bww11.getPossibleValues()) {
                        jsonArray12.add(Util.getPropertyName(bww11, comparable14));
                    }
                    jsonObject5.add(bww11.getName(), (JsonElement)jsonArray12);
                }
                jsonObject4.add("properties", (JsonElement)jsonObject5);
            }
            final JsonArray jsonArray13 = new JsonArray();
            for (final BlockState bvt11 : bvu8.getPossibleStates()) {
                final JsonObject jsonObject6 = new JsonObject();
                final JsonObject jsonObject7 = new JsonObject();
                for (final Property<?> bww12 : bvu8.getProperties()) {
                    jsonObject7.addProperty(bww12.getName(), Util.getPropertyName(bww12, bvt11.getValue(bww12)));
                }
                if (jsonObject7.size() > 0) {
                    jsonObject6.add("properties", (JsonElement)jsonObject7);
                }
                jsonObject6.addProperty("id", (Number)Block.getId(bvt11));
                if (bvt11 == bmv5.defaultBlockState()) {
                    jsonObject6.addProperty("default", Boolean.valueOf(true));
                }
                jsonArray13.add((JsonElement)jsonObject6);
            }
            jsonObject4.add("states", (JsonElement)jsonArray13);
            jsonObject3.add(qv6.toString(), (JsonElement)jsonObject4);
        }
        final Path path4 = this.generator.getOutputFolder().resolve("reports/blocks.json");
        DataProvider.save(BlockListReport.GSON, gm, (JsonElement)jsonObject3, path4);
    }
    
    public String getName() {
        return "Block List";
    }
    
    static {
        GSON = new GsonBuilder().setPrettyPrinting().create();
    }
}
