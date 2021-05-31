package net.minecraft.world.level.dimension;

import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.tags.BlockTags;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.level.biome.CheckerboardBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.levelgen.OverworldLevelSource;
import net.minecraft.world.level.levelgen.TheEndLevelSource;
import net.minecraft.world.level.levelgen.NetherLevelSource;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.DebugGeneratorSettings;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.TheEndGeneratorSettings;
import net.minecraft.world.level.levelgen.NetherGeneratorSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.biome.OverworldBiomeSourceSettings;
import net.minecraft.world.level.biome.CheckerboardBiomeSourceSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import com.mojang.datafixers.types.JsonOps;
import com.google.gson.JsonElement;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSourceSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.Level;

public class NormalDimension extends Dimension {
    public NormalDimension(final Level bhr, final DimensionType byn) {
        super(bhr, byn);
    }
    
    @Override
    public DimensionType getType() {
        return DimensionType.OVERWORLD;
    }
    
    @Override
    public ChunkGenerator<? extends ChunkGeneratorSettings> createRandomLevelGenerator() {
        final LevelType bhy2 = this.level.getLevelData().getGeneratorType();
        final ChunkGeneratorType<FlatLevelGeneratorSettings, FlatLevelSource> bxk3 = ChunkGeneratorType.FLAT;
        final ChunkGeneratorType<DebugGeneratorSettings, DebugLevelSource> bxk4 = ChunkGeneratorType.DEBUG;
        final ChunkGeneratorType<NetherGeneratorSettings, NetherLevelSource> bxk5 = ChunkGeneratorType.CAVES;
        final ChunkGeneratorType<TheEndGeneratorSettings, TheEndLevelSource> bxk6 = ChunkGeneratorType.FLOATING_ISLANDS;
        final ChunkGeneratorType<OverworldGeneratorSettings, OverworldLevelSource> bxk7 = ChunkGeneratorType.SURFACE;
        final BiomeSourceType<FixedBiomeSourceSettings, FixedBiomeSource> bis8 = BiomeSourceType.FIXED;
        final BiomeSourceType<OverworldBiomeSourceSettings, OverworldBiomeSource> bis9 = BiomeSourceType.VANILLA_LAYERED;
        final BiomeSourceType<CheckerboardBiomeSourceSettings, CheckerboardBiomeSource> bis10 = BiomeSourceType.CHECKERBOARD;
        if (bhy2 == LevelType.FLAT) {
            final FlatLevelGeneratorSettings cfx11 = FlatLevelGeneratorSettings.fromObject(new Dynamic((DynamicOps)NbtOps.INSTANCE, this.level.getLevelData().getGeneratorOptions()));
            final FixedBiomeSourceSettings bjo12 = bis8.createSettings().setBiome(cfx11.getBiome());
            return bxk3.create(this.level, bis8.create(bjo12), cfx11);
        }
        if (bhy2 == LevelType.DEBUG_ALL_BLOCK_STATES) {
            final FixedBiomeSourceSettings bjo13 = bis8.createSettings().setBiome(Biomes.PLAINS);
            return bxk4.create(this.level, bis8.create(bjo13), bxk4.createSettings());
        }
        if (bhy2 == LevelType.BUFFET) {
            BiomeSource biq11 = null;
            final JsonElement jsonElement12 = (JsonElement)Dynamic.convert((DynamicOps)NbtOps.INSTANCE, (DynamicOps)JsonOps.INSTANCE, this.level.getLevelData().getGeneratorOptions());
            final JsonObject jsonObject13 = jsonElement12.getAsJsonObject();
            final JsonObject jsonObject14 = jsonObject13.getAsJsonObject("biome_source");
            if (jsonObject14 != null && jsonObject14.has("type") && jsonObject14.has("options")) {
                final BiomeSourceType<?, ?> bis11 = Registry.BIOME_SOURCE_TYPE.get(new ResourceLocation(jsonObject14.getAsJsonPrimitive("type").getAsString()));
                final JsonObject jsonObject15 = jsonObject14.getAsJsonObject("options");
                Biome[] arr17 = { Biomes.OCEAN };
                if (jsonObject15.has("biomes")) {
                    final JsonArray jsonArray18 = jsonObject15.getAsJsonArray("biomes");
                    arr17 = ((jsonArray18.size() > 0) ? new Biome[jsonArray18.size()] : new Biome[] { Biomes.OCEAN });
                    for (int integer19 = 0; integer19 < jsonArray18.size(); ++integer19) {
                        arr17[integer19] = (Biome)Registry.BIOME.getOptional(new ResourceLocation(jsonArray18.get(integer19).getAsString())).orElse(Biomes.OCEAN);
                    }
                }
                if (BiomeSourceType.FIXED == bis11) {
                    final FixedBiomeSourceSettings bjo14 = bis8.createSettings().setBiome(arr17[0]);
                    biq11 = bis8.create(bjo14);
                }
                if (BiomeSourceType.CHECKERBOARD == bis11) {
                    final int integer20 = jsonObject15.has("size") ? jsonObject15.getAsJsonPrimitive("size").getAsInt() : 2;
                    final CheckerboardBiomeSourceSettings bix19 = bis10.createSettings().setAllowedBiomes(arr17).setSize(integer20);
                    biq11 = bis10.create(bix19);
                }
                if (BiomeSourceType.VANILLA_LAYERED == bis11) {
                    final OverworldBiomeSourceSettings bkp18 = bis9.createSettings().setGeneratorSettings(new OverworldGeneratorSettings()).setLevelData(this.level.getLevelData());
                    biq11 = bis9.create(bkp18);
                }
            }
            if (biq11 == null) {
                biq11 = bis8.create(bis8.createSettings().setBiome(Biomes.OCEAN));
            }
            BlockState bvt15 = Blocks.STONE.defaultBlockState();
            BlockState bvt16 = Blocks.WATER.defaultBlockState();
            final JsonObject jsonObject16 = jsonObject13.getAsJsonObject("chunk_generator");
            if (jsonObject16 != null && jsonObject16.has("options")) {
                final JsonObject jsonObject17 = jsonObject16.getAsJsonObject("options");
                if (jsonObject17.has("default_block")) {
                    final String string19 = jsonObject17.getAsJsonPrimitive("default_block").getAsString();
                    bvt15 = Registry.BLOCK.get(new ResourceLocation(string19)).defaultBlockState();
                }
                if (jsonObject17.has("default_fluid")) {
                    final String string19 = jsonObject17.getAsJsonPrimitive("default_fluid").getAsString();
                    bvt16 = Registry.BLOCK.get(new ResourceLocation(string19)).defaultBlockState();
                }
            }
            if (jsonObject16 != null && jsonObject16.has("type")) {
                final ChunkGeneratorType<?, ?> bxk8 = Registry.CHUNK_GENERATOR_TYPE.get(new ResourceLocation(jsonObject16.getAsJsonPrimitive("type").getAsString()));
                if (ChunkGeneratorType.CAVES == bxk8) {
                    final NetherGeneratorSettings bzb19 = bxk5.createSettings();
                    bzb19.setDefaultBlock(bvt15);
                    bzb19.setDefaultFluid(bvt16);
                    return bxk5.create(this.level, biq11, bzb19);
                }
                if (ChunkGeneratorType.FLOATING_ISLANDS == bxk8) {
                    final TheEndGeneratorSettings bzi19 = bxk6.createSettings();
                    bzi19.setSpawnPosition(new BlockPos(0, 64, 0));
                    bzi19.setDefaultBlock(bvt15);
                    bzi19.setDefaultFluid(bvt16);
                    return bxk6.create(this.level, biq11, bzi19);
                }
            }
            final OverworldGeneratorSettings bze18 = bxk7.createSettings();
            bze18.setDefaultBlock(bvt15);
            bze18.setDefaultFluid(bvt16);
            return bxk7.create(this.level, biq11, bze18);
        }
        final OverworldGeneratorSettings bze19 = bxk7.createSettings();
        final OverworldBiomeSourceSettings bkp19 = bis9.createSettings().setLevelData(this.level.getLevelData()).setGeneratorSettings(bze19);
        return bxk7.create(this.level, bis9.create(bkp19), bze19);
    }
    
    @Nullable
    @Override
    public BlockPos getSpawnPosInChunk(final ChunkPos bhd, final boolean boolean2) {
        for (int integer4 = bhd.getMinBlockX(); integer4 <= bhd.getMaxBlockX(); ++integer4) {
            for (int integer5 = bhd.getMinBlockZ(); integer5 <= bhd.getMaxBlockZ(); ++integer5) {
                final BlockPos ew6 = this.getValidSpawnPosition(integer4, integer5, boolean2);
                if (ew6 != null) {
                    return ew6;
                }
            }
        }
        return null;
    }
    
    @Nullable
    @Override
    public BlockPos getValidSpawnPosition(final int integer1, final int integer2, final boolean boolean3) {
        final BlockPos.MutableBlockPos a5 = new BlockPos.MutableBlockPos(integer1, 0, integer2);
        final Biome bio6 = this.level.getBiome(a5);
        final BlockState bvt7 = bio6.getSurfaceBuilderConfig().getTopMaterial();
        if (boolean3 && !bvt7.getBlock().is(BlockTags.VALID_SPAWN)) {
            return null;
        }
        final LevelChunk bxt8 = this.level.getChunk(integer1 >> 4, integer2 >> 4);
        final int integer3 = bxt8.getHeight(Heightmap.Types.MOTION_BLOCKING, integer1 & 0xF, integer2 & 0xF);
        if (integer3 < 0) {
            return null;
        }
        if (bxt8.getHeight(Heightmap.Types.WORLD_SURFACE, integer1 & 0xF, integer2 & 0xF) > bxt8.getHeight(Heightmap.Types.OCEAN_FLOOR, integer1 & 0xF, integer2 & 0xF)) {
            return null;
        }
        for (int integer4 = integer3 + 1; integer4 >= 0; --integer4) {
            a5.set(integer1, integer4, integer2);
            final BlockState bvt8 = this.level.getBlockState(a5);
            if (!bvt8.getFluidState().isEmpty()) {
                break;
            }
            if (bvt8.equals(bvt7)) {
                return a5.above().immutable();
            }
        }
        return null;
    }
    
    @Override
    public float getTimeOfDay(final long long1, final float float2) {
        final double double5 = Mth.frac(long1 / 24000.0 - 0.25);
        final double double6 = 0.5 - Math.cos(double5 * 3.141592653589793) / 2.0;
        return (float)(double5 * 2.0 + double6) / 3.0f;
    }
    
    @Override
    public boolean isNaturalDimension() {
        return true;
    }
    
    @Override
    public Vec3 getFogColor(final float float1, final float float2) {
        float float3 = Mth.cos(float1 * 6.2831855f) * 2.0f + 0.5f;
        float3 = Mth.clamp(float3, 0.0f, 1.0f);
        float float4 = 0.7529412f;
        float float5 = 0.84705883f;
        float float6 = 1.0f;
        float4 *= float3 * 0.94f + 0.06f;
        float5 *= float3 * 0.94f + 0.06f;
        float6 *= float3 * 0.91f + 0.09f;
        return new Vec3(float4, float5, float6);
    }
    
    @Override
    public boolean mayRespawn() {
        return true;
    }
    
    @Override
    public boolean isFoggyAt(final int integer1, final int integer2) {
        return false;
    }
}
