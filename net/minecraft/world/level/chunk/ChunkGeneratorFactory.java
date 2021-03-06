package net.minecraft.world.level.chunk;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

interface ChunkGeneratorFactory<C extends ChunkGeneratorSettings, T extends ChunkGenerator<C>> {
    T create(final Level bhr, final BiomeSource biq, final C byv);
}
