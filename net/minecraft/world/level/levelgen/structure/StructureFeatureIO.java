package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.levelgen.feature.Feature;
import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import org.apache.logging.log4j.Logger;

public class StructureFeatureIO {
    private static final Logger LOGGER;
    public static final StructureFeature<?> MINESHAFT;
    public static final StructureFeature<?> PILLAGER_OUTPOST;
    public static final StructureFeature<?> NETHER_FORTRESS;
    public static final StructureFeature<?> STRONGHOLD;
    public static final StructureFeature<?> JUNGLE_PYRAMID;
    public static final StructureFeature<?> OCEAN_RUIN;
    public static final StructureFeature<?> DESERT_PYRAMID;
    public static final StructureFeature<?> IGLOO;
    public static final StructureFeature<?> SWAMP_HUT;
    public static final StructureFeature<?> OCEAN_MONUMENT;
    public static final StructureFeature<?> END_CITY;
    public static final StructureFeature<?> WOODLAND_MANSION;
    public static final StructureFeature<?> BURIED_TREASURE;
    public static final StructureFeature<?> SHIPWRECK;
    public static final StructureFeature<?> VILLAGE;
    
    private static StructureFeature<?> register(final String string, final StructureFeature<?> ceu) {
        return Registry.<StructureFeature<?>>register(Registry.STRUCTURE_FEATURE, string.toLowerCase(Locale.ROOT), ceu);
    }
    
    public static void bootstrap() {
    }
    
    @Nullable
    public static StructureStart loadStaticStart(final ChunkGenerator<?> bxi, final StructureManager cjp, final BiomeSource biq, final CompoundTag id) {
        final String string5 = id.getString("id");
        if ("INVALID".equals(string5)) {
            return StructureStart.INVALID_START;
        }
        final StructureFeature<?> ceu6 = Registry.STRUCTURE_FEATURE.get(new ResourceLocation(string5.toLowerCase(Locale.ROOT)));
        if (ceu6 == null) {
            StructureFeatureIO.LOGGER.error("Unknown feature id: {}", string5);
            return null;
        }
        final int integer7 = id.getInt("ChunkX");
        final int integer8 = id.getInt("ChunkZ");
        final Biome bio9 = id.contains("biome") ? Registry.BIOME.get(new ResourceLocation(id.getString("biome"))) : biq.getBiome(new BlockPos((integer7 << 4) + 9, 0, (integer8 << 4) + 9));
        final BoundingBox cic10 = id.contains("BB") ? new BoundingBox(id.getIntArray("BB")) : BoundingBox.getUnknownBox();
        final ListTag ik11 = id.getList("Children", 10);
        try {
            final StructureStart ciw12 = ceu6.getStartFactory().create(ceu6, integer7, integer8, bio9, cic10, 0, bxi.getSeed());
            for (int integer9 = 0; integer9 < ik11.size(); ++integer9) {
                final CompoundTag id2 = ik11.getCompound(integer9);
                final String string6 = id2.getString("id");
                final StructurePieceType cev16 = Registry.STRUCTURE_PIECE.get(new ResourceLocation(string6.toLowerCase(Locale.ROOT)));
                if (cev16 == null) {
                    StructureFeatureIO.LOGGER.error("Unknown structure piece id: {}", string6);
                }
                else {
                    try {
                        final StructurePiece civ17 = cev16.load(cjp, id2);
                        ciw12.pieces.add(civ17);
                    }
                    catch (Exception exception17) {
                        StructureFeatureIO.LOGGER.error("Exception loading structure piece with id {}", string6, exception17);
                    }
                }
            }
            return ciw12;
        }
        catch (Exception exception18) {
            StructureFeatureIO.LOGGER.error("Failed Start with id {}", string5, exception18);
            return null;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MINESHAFT = register("Mineshaft", Feature.MINESHAFT);
        PILLAGER_OUTPOST = register("Pillager_Outpost", Feature.PILLAGER_OUTPOST);
        NETHER_FORTRESS = register("Fortress", Feature.NETHER_BRIDGE);
        STRONGHOLD = register("Stronghold", Feature.STRONGHOLD);
        JUNGLE_PYRAMID = register("Jungle_Pyramid", Feature.JUNGLE_TEMPLE);
        OCEAN_RUIN = register("Ocean_Ruin", Feature.OCEAN_RUIN);
        DESERT_PYRAMID = register("Desert_Pyramid", Feature.DESERT_PYRAMID);
        IGLOO = register("Igloo", Feature.IGLOO);
        SWAMP_HUT = register("Swamp_Hut", Feature.SWAMP_HUT);
        OCEAN_MONUMENT = register("Monument", Feature.OCEAN_MONUMENT);
        END_CITY = register("EndCity", Feature.END_CITY);
        WOODLAND_MANSION = register("Mansion", Feature.WOODLAND_MANSION);
        BURIED_TREASURE = register("Buried_Treasure", Feature.BURIED_TREASURE);
        SHIPWRECK = register("Shipwreck", Feature.SHIPWRECK);
        VILLAGE = register("Village", Feature.VILLAGE);
    }
}
