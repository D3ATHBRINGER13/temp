package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import java.io.OutputStream;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffect;
import java.util.TreeSet;
import net.minecraft.locale.Language;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.core.Registry;
import org.apache.logging.log4j.Logger;
import java.io.PrintStream;

public class Bootstrap {
    public static final PrintStream STDOUT;
    private static boolean isBootstrapped;
    private static final Logger LOGGER;
    
    public static void bootStrap() {
        if (Bootstrap.isBootstrapped) {
            return;
        }
        Bootstrap.isBootstrapped = true;
        if (Registry.REGISTRY.isEmpty()) {
            throw new IllegalStateException("Unable to load registries");
        }
        FireBlock.bootStrap();
        ComposterBlock.bootStrap();
        if (EntityType.getKey(EntityType.PLAYER) == null) {
            throw new IllegalStateException("Failed loading EntityTypes");
        }
        PotionBrewing.bootStrap();
        EntitySelectorOptions.bootStrap();
        DispenseItemBehavior.bootStrap();
        ArgumentTypes.bootStrap();
        wrapStreams();
    }
    
    private static <T> void checkTranslations(final Registry<T> fn, final Function<T, String> function, final Set<String> set) {
        final Language hy4 = Language.getInstance();
        fn.iterator().forEachRemaining(object -> {
            final String string5 = (String)function.apply(object);
            if (!hy4.exists(string5)) {
                set.add(string5);
            }
        });
    }
    
    public static Set<String> getMissingTranslations() {
        final Set<String> set1 = (Set<String>)new TreeSet();
        Bootstrap.<EntityType<?>>checkTranslations(Registry.ENTITY_TYPE, (java.util.function.Function<EntityType<?>, String>)EntityType::getDescriptionId, set1);
        Bootstrap.<MobEffect>checkTranslations(Registry.MOB_EFFECT, (java.util.function.Function<MobEffect, String>)MobEffect::getDescriptionId, set1);
        Bootstrap.<Item>checkTranslations(Registry.ITEM, (java.util.function.Function<Item, String>)Item::getDescriptionId, set1);
        Bootstrap.<Enchantment>checkTranslations(Registry.ENCHANTMENT, (java.util.function.Function<Enchantment, String>)Enchantment::getDescriptionId, set1);
        Bootstrap.<Biome>checkTranslations(Registry.BIOME, (java.util.function.Function<Biome, String>)Biome::getDescriptionId, set1);
        Bootstrap.<Block>checkTranslations(Registry.BLOCK, (java.util.function.Function<Block, String>)Block::getDescriptionId, set1);
        Bootstrap.<ResourceLocation>checkTranslations(Registry.CUSTOM_STAT, (java.util.function.Function<ResourceLocation, String>)(qv -> "stat." + qv.toString().replace(':', '.')), set1);
        return set1;
    }
    
    public static void validate() {
        if (!Bootstrap.isBootstrapped) {
            throw new IllegalArgumentException("Not bootstrapped");
        }
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            return;
        }
        getMissingTranslations().forEach(string -> Bootstrap.LOGGER.error("Missing translations: " + string));
    }
    
    private static void wrapStreams() {
        if (Bootstrap.LOGGER.isDebugEnabled()) {
            System.setErr((PrintStream)new DebugLoggedPrintStream("STDERR", (OutputStream)System.err));
            System.setOut((PrintStream)new DebugLoggedPrintStream("STDOUT", (OutputStream)Bootstrap.STDOUT));
        }
        else {
            System.setErr((PrintStream)new LoggedPrintStream("STDERR", (OutputStream)System.err));
            System.setOut((PrintStream)new LoggedPrintStream("STDOUT", (OutputStream)Bootstrap.STDOUT));
        }
    }
    
    public static void realStdoutPrintln(final String string) {
        Bootstrap.STDOUT.println(string);
    }
    
    static {
        STDOUT = System.out;
        LOGGER = LogManager.getLogger();
    }
}
