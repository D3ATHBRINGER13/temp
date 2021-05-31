package com.mojang.realmsclient.gui.screens;

import org.apache.logging.log4j.LogManager;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.util.RealmsTasks;
import net.minecraft.realms.Realms;
import java.util.concurrent.locks.ReentrantLock;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsResourcePackScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private final RealmsScreen lastScreen;
    private final RealmsServerAddress serverAddress;
    private final ReentrantLock connectLock;
    
    public RealmsResourcePackScreen(final RealmsScreen realmsScreen, final RealmsServerAddress realmsServerAddress, final ReentrantLock reentrantLock) {
        this.lastScreen = realmsScreen;
        this.serverAddress = realmsServerAddress;
        this.connectLock = reentrantLock;
    }
    
    @Override
    public void confirmResult(final boolean boolean1, final int integer) {
        try {
            if (!boolean1) {
                Realms.setScreen(this.lastScreen);
            }
            else {
                try {
                    Realms.downloadResourcePack(this.serverAddress.resourcePackUrl, this.serverAddress.resourcePackHash).thenRun(() -> {
                        final RealmsLongRunningMcoTaskScreen cwo2 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsTasks.RealmsConnectTask(this.lastScreen, this.serverAddress));
                        cwo2.start();
                        Realms.setScreen(cwo2);
                    }).exceptionally(throwable -> {
                        Realms.clearResourcePack();
                        RealmsResourcePackScreen.LOGGER.error(throwable);
                        Realms.setScreen(new RealmsGenericErrorScreen("Failed to download resource pack!", this.lastScreen));
                        return null;
                    });
                }
                catch (Exception exception4) {
                    Realms.clearResourcePack();
                    RealmsResourcePackScreen.LOGGER.error(exception4);
                    Realms.setScreen(new RealmsGenericErrorScreen("Failed to download resource pack!", this.lastScreen));
                }
            }
        }
        finally {
            if (this.connectLock != null && this.connectLock.isHeldByCurrentThread()) {
                this.connectLock.unlock();
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
