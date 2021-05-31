package net.minecraft.client.gui.screens;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.world.inventory.CartographyMenu;
import net.minecraft.client.gui.screens.inventory.CartographyScreen;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import javax.annotation.Nullable;
import net.minecraft.world.inventory.MenuType;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class MenuScreens {
    private static final Logger LOGGER;
    private static final Map<MenuType<?>, ScreenConstructor<?, ?>> SCREENS;
    
    public static <T extends AbstractContainerMenu> void create(@Nullable final MenuType<T> azl, final Minecraft cyc, final int integer, final Component jo) {
        if (azl == null) {
            MenuScreens.LOGGER.warn("Trying to open invalid screen with name: {}", jo.getString());
            return;
        }
        final ScreenConstructor<T, ?> a5 = MenuScreens.<T>getConstructor(azl);
        if (a5 == null) {
            MenuScreens.LOGGER.warn("Failed to create screen for menu type: {}", Registry.MENU.getKey(azl));
            return;
        }
        a5.fromPacket(jo, azl, cyc, integer);
    }
    
    @Nullable
    private static <T extends AbstractContainerMenu> ScreenConstructor<T, ?> getConstructor(final MenuType<T> azl) {
        return MenuScreens.SCREENS.get(azl);
    }
    
    private static <M extends AbstractContainerMenu, U extends Screen> void register(final MenuType<? extends M> azl, final ScreenConstructor<M, U> a) {
        final ScreenConstructor<?, ?> a2 = MenuScreens.SCREENS.put(azl, a);
        if (a2 != null) {
            throw new IllegalStateException(new StringBuilder().append("Duplicate registration for ").append(Registry.MENU.getKey(azl)).toString());
        }
    }
    
    public static boolean selfTest() {
        boolean boolean1 = false;
        for (final MenuType<?> azl3 : Registry.MENU) {
            if (!MenuScreens.SCREENS.containsKey(azl3)) {
                MenuScreens.LOGGER.debug("Menu {} has no matching screen", Registry.MENU.getKey(azl3));
                boolean1 = true;
            }
        }
        return boolean1;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        SCREENS = (Map)Maps.newHashMap();
        MenuScreens.<ChestMenu, ContainerScreen>register(MenuType.GENERIC_9x1, ContainerScreen::new);
        MenuScreens.<ChestMenu, ContainerScreen>register(MenuType.GENERIC_9x2, ContainerScreen::new);
        MenuScreens.<ChestMenu, ContainerScreen>register(MenuType.GENERIC_9x3, ContainerScreen::new);
        MenuScreens.<ChestMenu, ContainerScreen>register(MenuType.GENERIC_9x4, ContainerScreen::new);
        MenuScreens.<ChestMenu, ContainerScreen>register(MenuType.GENERIC_9x5, ContainerScreen::new);
        MenuScreens.<ChestMenu, ContainerScreen>register(MenuType.GENERIC_9x6, ContainerScreen::new);
        MenuScreens.<DispenserMenu, DispenserScreen>register(MenuType.GENERIC_3x3, DispenserScreen::new);
        MenuScreens.<AnvilMenu, AnvilScreen>register(MenuType.ANVIL, AnvilScreen::new);
        MenuScreens.<BeaconMenu, BeaconScreen>register(MenuType.BEACON, BeaconScreen::new);
        MenuScreens.<BlastFurnaceMenu, BlastFurnaceScreen>register(MenuType.BLAST_FURNACE, BlastFurnaceScreen::new);
        MenuScreens.<BrewingStandMenu, BrewingStandScreen>register(MenuType.BREWING_STAND, BrewingStandScreen::new);
        MenuScreens.<CraftingMenu, CraftingScreen>register(MenuType.CRAFTING, CraftingScreen::new);
        MenuScreens.<EnchantmentMenu, EnchantmentScreen>register(MenuType.ENCHANTMENT, EnchantmentScreen::new);
        MenuScreens.<FurnaceMenu, FurnaceScreen>register(MenuType.FURNACE, FurnaceScreen::new);
        MenuScreens.<GrindstoneMenu, GrindstoneScreen>register(MenuType.GRINDSTONE, GrindstoneScreen::new);
        MenuScreens.<HopperMenu, HopperScreen>register(MenuType.HOPPER, HopperScreen::new);
        MenuScreens.<LecternMenu, LecternScreen>register(MenuType.LECTERN, LecternScreen::new);
        MenuScreens.<LoomMenu, LoomScreen>register(MenuType.LOOM, LoomScreen::new);
        MenuScreens.<MerchantMenu, MerchantScreen>register(MenuType.MERCHANT, MerchantScreen::new);
        MenuScreens.<ShulkerBoxMenu, ShulkerBoxScreen>register(MenuType.SHULKER_BOX, ShulkerBoxScreen::new);
        MenuScreens.<SmokerMenu, SmokerScreen>register(MenuType.SMOKER, SmokerScreen::new);
        MenuScreens.<CartographyMenu, CartographyScreen>register(MenuType.CARTOGRAPHY, CartographyScreen::new);
        MenuScreens.<StonecutterMenu, StonecutterScreen>register(MenuType.STONECUTTER, StonecutterScreen::new);
    }
    
    interface ScreenConstructor<T extends AbstractContainerMenu, U extends net.minecraft.client.gui.screens.Screen> {
        default void fromPacket(final Component jo, final MenuType<T> azl, final Minecraft cyc, final int integer) {
            final U dcl6 = this.create(azl.create(integer, cyc.player.inventory), cyc.player.inventory, jo);
            cyc.player.containerMenu = ((MenuAccess)dcl6).getMenu();
            cyc.setScreen((Screen)dcl6);
        }
        
        U create(final T ayk, final Inventory awf, final Component jo);
    }
}
