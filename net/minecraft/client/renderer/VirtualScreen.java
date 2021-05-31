package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.ScreenManager;
import net.minecraft.client.Minecraft;

public final class VirtualScreen implements AutoCloseable {
    private final Minecraft minecraft;
    private final ScreenManager screenManager;
    
    public VirtualScreen(final Minecraft cyc) {
        this.minecraft = cyc;
        this.screenManager = new ScreenManager(Monitor::new);
    }
    
    public Window newWindow(final DisplayData cuc, final String string2, final String string3) {
        return new Window(this.minecraft, this.screenManager, cuc, string2, string3);
    }
    
    public void close() {
        this.screenManager.shutdown();
    }
}
