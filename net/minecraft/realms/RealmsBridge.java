package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import javax.annotation.Nullable;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.client.gui.screens.Screen;

public class RealmsBridge extends RealmsScreen {
    private Screen previousScreen;
    
    public void switchToRealms(final Screen dcl) {
        this.previousScreen = dcl;
        Realms.setScreen(new RealmsMainScreen(this));
    }
    
    @Nullable
    public RealmsScreenProxy getNotificationScreen(final Screen dcl) {
        this.previousScreen = dcl;
        return new RealmsNotificationsScreen(this).getProxy();
    }
    
    @Override
    public void init() {
        Minecraft.getInstance().setScreen(this.previousScreen);
    }
}
