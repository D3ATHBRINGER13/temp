package net.minecraft.world.level.biome;

import net.minecraft.core.Registry;
import java.util.function.Supplier;
import java.util.function.Function;

public class BiomeSourceType<C extends BiomeSourceSettings, T extends BiomeSource> {
    public static final BiomeSourceType<CheckerboardBiomeSourceSettings, CheckerboardBiomeSource> CHECKERBOARD;
    public static final BiomeSourceType<FixedBiomeSourceSettings, FixedBiomeSource> FIXED;
    public static final BiomeSourceType<OverworldBiomeSourceSettings, OverworldBiomeSource> VANILLA_LAYERED;
    public static final BiomeSourceType<TheEndBiomeSourceSettings, TheEndBiomeSource> THE_END;
    private final Function<C, T> factory;
    private final Supplier<C> settingsFactory;
    
    private static <C extends BiomeSourceSettings, T extends BiomeSource> BiomeSourceType<C, T> register(final String string, final Function<C, T> function, final Supplier<C> supplier) {
        return Registry.<BiomeSourceType<C, T>>register(Registry.BIOME_SOURCE_TYPE, string, new BiomeSourceType<C, T>(function, supplier));
    }
    
    public BiomeSourceType(final Function<C, T> function, final Supplier<C> supplier) {
        this.factory = function;
        this.settingsFactory = supplier;
    }
    
    public T create(final C bir) {
        return (T)this.factory.apply(bir);
    }
    
    public C createSettings() {
        return (C)this.settingsFactory.get();
    }
    
    static {
        CHECKERBOARD = BiomeSourceType.<CheckerboardBiomeSourceSettings, CheckerboardBiomeSource>register("checkerboard", (java.util.function.Function<CheckerboardBiomeSourceSettings, CheckerboardBiomeSource>)CheckerboardBiomeSource::new, (java.util.function.Supplier<CheckerboardBiomeSourceSettings>)CheckerboardBiomeSourceSettings::new);
        FIXED = BiomeSourceType.<FixedBiomeSourceSettings, FixedBiomeSource>register("fixed", (java.util.function.Function<FixedBiomeSourceSettings, FixedBiomeSource>)FixedBiomeSource::new, (java.util.function.Supplier<FixedBiomeSourceSettings>)FixedBiomeSourceSettings::new);
        VANILLA_LAYERED = BiomeSourceType.<OverworldBiomeSourceSettings, OverworldBiomeSource>register("vanilla_layered", (java.util.function.Function<OverworldBiomeSourceSettings, OverworldBiomeSource>)OverworldBiomeSource::new, (java.util.function.Supplier<OverworldBiomeSourceSettings>)OverworldBiomeSourceSettings::new);
        THE_END = BiomeSourceType.<TheEndBiomeSourceSettings, TheEndBiomeSource>register("the_end", (java.util.function.Function<TheEndBiomeSourceSettings, TheEndBiomeSource>)TheEndBiomeSource::new, (java.util.function.Supplier<TheEndBiomeSourceSettings>)TheEndBiomeSourceSettings::new);
    }
}
