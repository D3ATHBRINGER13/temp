package net.minecraft.realms.pluginapi;

import net.minecraft.realms.RealmsScreen;

public interface LoadedRealmsPlugin {
    RealmsScreen getMainScreen(final RealmsScreen realmsScreen);
    
    RealmsScreen getNotificationsScreen(final RealmsScreen realmsScreen);
}
