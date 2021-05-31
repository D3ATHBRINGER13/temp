package net.minecraft.world.level.chunk;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.Level;
import net.minecraft.core.Registry;
import java.util.function.Supplier;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.DebugGeneratorSettings;
import net.minecraft.world.level.levelgen.TheEndLevelSource;
import net.minecraft.world.level.levelgen.TheEndGeneratorSettings;
import net.minecraft.world.level.levelgen.NetherLevelSource;
import net.minecraft.world.level.levelgen.NetherGeneratorSettings;
import net.minecraft.world.level.levelgen.OverworldLevelSource;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class ChunkGeneratorType<C extends ChunkGeneratorSettings, T extends ChunkGenerator<C>> implements ChunkGeneratorFactory<C, T> {
    public static final ChunkGeneratorType<OverworldGeneratorSettings, OverworldLevelSource> SURFACE;
    public static final ChunkGeneratorType<NetherGeneratorSettings, NetherLevelSource> CAVES;
    public static final ChunkGeneratorType<TheEndGeneratorSettings, TheEndLevelSource> FLOATING_ISLANDS;
    public static final ChunkGeneratorType<DebugGeneratorSettings, DebugLevelSource> DEBUG;
    public static final ChunkGeneratorType<FlatLevelGeneratorSettings, FlatLevelSource> FLAT;
    private final ChunkGeneratorFactory<C, T> factory;
    private final boolean isPublic;
    private final Supplier<C> settingsFactory;
    
    private static <C extends ChunkGeneratorSettings, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> register(final String string, final ChunkGeneratorFactory<C, T> bxj, final Supplier<C> supplier, final boolean boolean4) {
        return Registry.<ChunkGeneratorType<C, T>>register(Registry.CHUNK_GENERATOR_TYPE, string, new ChunkGeneratorType<C, T>(bxj, boolean4, supplier));
    }
    
    public ChunkGeneratorType(final ChunkGeneratorFactory<C, T> bxj, final boolean boolean2, final Supplier<C> supplier) {
        this.factory = bxj;
        this.isPublic = boolean2;
        this.settingsFactory = supplier;
    }
    
    public T create(final Level bhr, final BiomeSource biq, final C byv) {
        return this.factory.create(bhr, biq, byv);
    }
    
    public C createSettings() {
        return (C)this.settingsFactory.get();
    }
    
    public boolean isPublic() {
        return this.isPublic;
    }
    
    static {
        SURFACE = ChunkGeneratorType.<OverworldGeneratorSettings, OverworldLevelSource>register("surface", OverworldLevelSource::new, (java.util.function.Supplier<OverworldGeneratorSettings>)OverworldGeneratorSettings::new, true);
        CAVES = ChunkGeneratorType.<NetherGeneratorSettings, NetherLevelSource>register("caves", NetherLevelSource::new, (java.util.function.Supplier<NetherGeneratorSettings>)NetherGeneratorSettings::new, true);
        FLOATING_ISLANDS = ChunkGeneratorType.<TheEndGeneratorSettings, TheEndLevelSource>register("floating_islands", TheEndLevelSource::new, (java.util.function.Supplier<TheEndGeneratorSettings>)TheEndGeneratorSettings::new, true);
        DEBUG = ChunkGeneratorType.<DebugGeneratorSettings, DebugLevelSource>register("debug", DebugLevelSource::new, (java.util.function.Supplier<DebugGeneratorSettings>)DebugGeneratorSettings::new, false);
        FLAT = ChunkGeneratorType.<FlatLevelGeneratorSettings, FlatLevelSource>register("flat", FlatLevelSource::new, (java.util.function.Supplier<FlatLevelGeneratorSettings>)FlatLevelGeneratorSettings::new, false);
    }
}
