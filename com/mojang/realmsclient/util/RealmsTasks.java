package com.mojang.realmsclient.util;

import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import java.io.UnsupportedEncodingException;
import net.minecraft.realms.RealmsConnect;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsResourcePackScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import java.io.IOException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.util.concurrent.locks.ReentrantLock;
import com.mojang.realmsclient.dto.WorldTemplate;
import net.minecraft.realms.RealmsConfirmResultListener;
import com.mojang.realmsclient.exception.RetryCallException;
import net.minecraft.realms.Realms;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.realms.RealmsScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.LongRunningTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsTasks {
    private static final Logger LOGGER;
    
    private static void pause(final int integer) {
        try {
            Thread.sleep((long)(integer * 1000));
        }
        catch (InterruptedException interruptedException2) {
            RealmsTasks.LOGGER.error("", (Throwable)interruptedException2);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class OpenServerTask extends LongRunningTask {
        private final RealmsServer serverData;
        private final RealmsScreen returnScreen;
        private final boolean join;
        private final RealmsScreen mainScreen;
        
        public OpenServerTask(final RealmsServer realmsServer, final RealmsScreen realmsScreen2, final RealmsScreen realmsScreen3, final boolean boolean4) {
            this.serverData = realmsServer;
            this.returnScreen = realmsScreen2;
            this.join = boolean4;
            this.mainScreen = realmsScreen3;
        }
        
        public void run() {
            this.setTitle(RealmsScreen.getLocalizedString("mco.configure.world.opening"));
            final RealmsClient cvm2 = RealmsClient.createRealmsClient();
            for (int integer3 = 0; integer3 < 25; ++integer3) {
                if (this.aborted()) {
                    return;
                }
                try {
                    final boolean boolean4 = cvm2.open(this.serverData.id);
                    if (boolean4) {
                        if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
                            ((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
                        }
                        this.serverData.state = RealmsServer.State.OPEN;
                        if (this.join) {
                            ((RealmsMainScreen)this.mainScreen).play(this.serverData, this.returnScreen);
                            break;
                        }
                        Realms.setScreen(this.returnScreen);
                        break;
                    }
                }
                catch (RetryCallException cvv4) {
                    if (this.aborted()) {
                        return;
                    }
                    pause(cvv4.delaySeconds);
                }
                catch (Exception exception4) {
                    if (this.aborted()) {
                        return;
                    }
                    RealmsTasks.LOGGER.error("Failed to open server", (Throwable)exception4);
                    this.error("Failed to open the server");
                }
            }
        }
    }
    
    public static class CloseServerTask extends LongRunningTask {
        private final RealmsServer serverData;
        private final RealmsConfigureWorldScreen configureScreen;
        
        public CloseServerTask(final RealmsServer realmsServer, final RealmsConfigureWorldScreen cwg) {
            this.serverData = realmsServer;
            this.configureScreen = cwg;
        }
        
        public void run() {
            this.setTitle(RealmsScreen.getLocalizedString("mco.configure.world.closing"));
            final RealmsClient cvm2 = RealmsClient.createRealmsClient();
            for (int integer3 = 0; integer3 < 25; ++integer3) {
                if (this.aborted()) {
                    return;
                }
                try {
                    final boolean boolean4 = cvm2.close(this.serverData.id);
                    if (boolean4) {
                        this.configureScreen.stateChanged();
                        this.serverData.state = RealmsServer.State.CLOSED;
                        Realms.setScreen(this.configureScreen);
                        break;
                    }
                }
                catch (RetryCallException cvv4) {
                    if (this.aborted()) {
                        return;
                    }
                    pause(cvv4.delaySeconds);
                }
                catch (Exception exception4) {
                    if (this.aborted()) {
                        return;
                    }
                    RealmsTasks.LOGGER.error("Failed to close server", (Throwable)exception4);
                    this.error("Failed to close the server");
                }
            }
        }
    }
    
    public static class SwitchSlotTask extends LongRunningTask {
        private final long worldId;
        private final int slot;
        private final RealmsConfirmResultListener listener;
        private final int confirmId;
        
        public SwitchSlotTask(final long long1, final int integer2, final RealmsConfirmResultListener realmsConfirmResultListener, final int integer4) {
            this.worldId = long1;
            this.slot = integer2;
            this.listener = realmsConfirmResultListener;
            this.confirmId = integer4;
        }
        
        public void run() {
            final RealmsClient cvm2 = RealmsClient.createRealmsClient();
            final String string3 = RealmsScreen.getLocalizedString("mco.minigame.world.slot.screen.title");
            this.setTitle(string3);
            for (int integer4 = 0; integer4 < 25; ++integer4) {
                try {
                    if (this.aborted()) {
                        return;
                    }
                    if (cvm2.switchSlot(this.worldId, this.slot)) {
                        this.listener.confirmResult(true, this.confirmId);
                        break;
                    }
                }
                catch (RetryCallException cvv5) {
                    if (this.aborted()) {
                        return;
                    }
                    pause(cvv5.delaySeconds);
                }
                catch (Exception exception5) {
                    if (this.aborted()) {
                        return;
                    }
                    RealmsTasks.LOGGER.error("Couldn't switch world!");
                    this.error(exception5.toString());
                }
            }
        }
    }
    
    public static class SwitchMinigameTask extends LongRunningTask {
        private final long worldId;
        private final WorldTemplate worldTemplate;
        private final RealmsConfigureWorldScreen lastScreen;
        
        public SwitchMinigameTask(final long long1, final WorldTemplate worldTemplate, final RealmsConfigureWorldScreen cwg) {
            this.worldId = long1;
            this.worldTemplate = worldTemplate;
            this.lastScreen = cwg;
        }
        
        public void run() {
            final RealmsClient cvm2 = RealmsClient.createRealmsClient();
            final String string3 = RealmsScreen.getLocalizedString("mco.minigame.world.starting.screen.title");
            this.setTitle(string3);
            for (int integer4 = 0; integer4 < 25; ++integer4) {
                try {
                    if (this.aborted()) {
                        return;
                    }
                    if (cvm2.putIntoMinigameMode(this.worldId, this.worldTemplate.id)) {
                        Realms.setScreen(this.lastScreen);
                        break;
                    }
                }
                catch (RetryCallException cvv5) {
                    if (this.aborted()) {
                        return;
                    }
                    pause(cvv5.delaySeconds);
                }
                catch (Exception exception5) {
                    if (this.aborted()) {
                        return;
                    }
                    RealmsTasks.LOGGER.error("Couldn't start mini game!");
                    this.error(exception5.toString());
                }
            }
        }
    }
    
    public static class ResettingWorldTask extends LongRunningTask {
        private final String seed;
        private final WorldTemplate worldTemplate;
        private final int levelType;
        private final boolean generateStructures;
        private final long serverId;
        private final RealmsScreen lastScreen;
        private int confirmationId;
        private String title;
        
        public ResettingWorldTask(final long long1, final RealmsScreen realmsScreen, final WorldTemplate worldTemplate) {
            this.confirmationId = -1;
            this.title = RealmsScreen.getLocalizedString("mco.reset.world.resetting.screen.title");
            this.seed = null;
            this.worldTemplate = worldTemplate;
            this.levelType = -1;
            this.generateStructures = true;
            this.serverId = long1;
            this.lastScreen = realmsScreen;
        }
        
        public ResettingWorldTask(final long long1, final RealmsScreen realmsScreen, final String string, final int integer, final boolean boolean5) {
            this.confirmationId = -1;
            this.title = RealmsScreen.getLocalizedString("mco.reset.world.resetting.screen.title");
            this.seed = string;
            this.worldTemplate = null;
            this.levelType = integer;
            this.generateStructures = boolean5;
            this.serverId = long1;
            this.lastScreen = realmsScreen;
        }
        
        public void setConfirmationId(final int integer) {
            this.confirmationId = integer;
        }
        
        public void setResetTitle(final String string) {
            this.title = string;
        }
        
        public void run() {
            final RealmsClient cvm2 = RealmsClient.createRealmsClient();
            this.setTitle(this.title);
            for (int integer3 = 0; integer3 < 25; ++integer3) {
                try {
                    if (this.aborted()) {
                        return;
                    }
                    if (this.worldTemplate != null) {
                        cvm2.resetWorldWithTemplate(this.serverId, this.worldTemplate.id);
                    }
                    else {
                        cvm2.resetWorldWithSeed(this.serverId, this.seed, this.levelType, this.generateStructures);
                    }
                    if (this.aborted()) {
                        return;
                    }
                    if (this.confirmationId == -1) {
                        Realms.setScreen(this.lastScreen);
                    }
                    else {
                        this.lastScreen.confirmResult(true, this.confirmationId);
                    }
                    return;
                }
                catch (RetryCallException cvv4) {
                    if (this.aborted()) {
                        return;
                    }
                    pause(cvv4.delaySeconds);
                }
                catch (Exception exception4) {
                    if (this.aborted()) {
                        return;
                    }
                    RealmsTasks.LOGGER.error("Couldn't reset world");
                    this.error(exception4.toString());
                    return;
                }
            }
        }
    }
    
    public static class RealmsGetServerDetailsTask extends LongRunningTask {
        private final RealmsServer server;
        private final RealmsScreen lastScreen;
        private final RealmsMainScreen mainScreen;
        private final ReentrantLock connectLock;
        
        public RealmsGetServerDetailsTask(final RealmsMainScreen cvi, final RealmsScreen realmsScreen, final RealmsServer realmsServer, final ReentrantLock reentrantLock) {
            this.lastScreen = realmsScreen;
            this.mainScreen = cvi;
            this.server = realmsServer;
            this.connectLock = reentrantLock;
        }
        
        public void run() {
            this.setTitle(RealmsScreen.getLocalizedString("mco.connect.connecting"));
            final RealmsClient cvm2 = RealmsClient.createRealmsClient();
            boolean boolean3 = false;
            boolean boolean4 = false;
            int integer5 = 5;
            RealmsServerAddress realmsServerAddress6 = null;
            boolean boolean5 = false;
            boolean boolean6 = false;
            for (int integer6 = 0; integer6 < 40; ++integer6) {
                if (this.aborted()) {
                    break;
                }
                try {
                    realmsServerAddress6 = cvm2.join(this.server.id);
                    boolean3 = true;
                }
                catch (RetryCallException cvv10) {
                    integer5 = cvv10.delaySeconds;
                }
                catch (RealmsServiceException cvu10) {
                    if (cvu10.errorCode == 6002) {
                        boolean5 = true;
                        break;
                    }
                    if (cvu10.errorCode == 6006) {
                        boolean6 = true;
                        break;
                    }
                    boolean4 = true;
                    this.error(cvu10.toString());
                    RealmsTasks.LOGGER.error("Couldn't connect to world", (Throwable)cvu10);
                    break;
                }
                catch (IOException iOException10) {
                    RealmsTasks.LOGGER.error("Couldn't parse response connecting to world", (Throwable)iOException10);
                }
                catch (Exception exception10) {
                    boolean4 = true;
                    RealmsTasks.LOGGER.error("Couldn't connect to world", (Throwable)exception10);
                    this.error(exception10.getLocalizedMessage());
                    break;
                }
                if (boolean3) {
                    break;
                }
                this.sleep(integer5);
            }
            if (boolean5) {
                Realms.setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
            }
            else if (boolean6) {
                if (this.server.ownerUUID.equals(Realms.getUUID())) {
                    final RealmsBrokenWorldScreen cwe9 = new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id);
                    if (this.server.worldType.equals(RealmsServer.WorldType.MINIGAME)) {
                        cwe9.setTitle(RealmsScreen.getLocalizedString("mco.brokenworld.minigame.title"));
                    }
                    Realms.setScreen(cwe9);
                }
                else {
                    Realms.setScreen(new RealmsGenericErrorScreen(RealmsScreen.getLocalizedString("mco.brokenworld.nonowner.title"), RealmsScreen.getLocalizedString("mco.brokenworld.nonowner.error"), this.lastScreen));
                }
            }
            else if (!this.aborted() && !boolean4) {
                if (boolean3) {
                    if (realmsServerAddress6.resourcePackUrl != null && realmsServerAddress6.resourcePackHash != null) {
                        final String string9 = RealmsScreen.getLocalizedString("mco.configure.world.resourcepack.question.line1");
                        final String string10 = RealmsScreen.getLocalizedString("mco.configure.world.resourcepack.question.line2");
                        Realms.setScreen(new RealmsLongConfirmationScreen(new RealmsResourcePackScreen(this.lastScreen, realmsServerAddress6, this.connectLock), RealmsLongConfirmationScreen.Type.Info, string9, string10, true, 100));
                    }
                    else {
                        final RealmsLongRunningMcoTaskScreen cwo9 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsConnectTask(this.lastScreen, realmsServerAddress6));
                        cwo9.start();
                        Realms.setScreen(cwo9);
                    }
                }
                else {
                    this.error(RealmsScreen.getLocalizedString("mco.errorMessage.connectionFailure"));
                }
            }
        }
        
        private void sleep(final int integer) {
            try {
                Thread.sleep((long)(integer * 1000));
            }
            catch (InterruptedException interruptedException3) {
                RealmsTasks.LOGGER.warn(interruptedException3.getLocalizedMessage());
            }
        }
    }
    
    public static class RealmsConnectTask extends LongRunningTask {
        private final RealmsConnect realmsConnect;
        private final RealmsServerAddress a;
        
        public RealmsConnectTask(final RealmsScreen realmsScreen, final RealmsServerAddress realmsServerAddress) {
            this.a = realmsServerAddress;
            this.realmsConnect = new RealmsConnect(realmsScreen);
        }
        
        public void run() {
            this.setTitle(RealmsScreen.getLocalizedString("mco.connect.connecting"));
            final net.minecraft.realms.RealmsServerAddress realmsServerAddress2 = net.minecraft.realms.RealmsServerAddress.parseString(this.a.address);
            this.realmsConnect.connect(realmsServerAddress2.getHost(), realmsServerAddress2.getPort());
        }
        
        @Override
        public void abortTask() {
            this.realmsConnect.abort();
            Realms.clearResourcePack();
        }
        
        @Override
        public void tick() {
            this.realmsConnect.tick();
        }
    }
    
    public static class WorldCreationTask extends LongRunningTask {
        private final String name;
        private final String motd;
        private final long worldId;
        private final RealmsScreen lastScreen;
        
        public WorldCreationTask(final long long1, final String string2, final String string3, final RealmsScreen realmsScreen) {
            this.worldId = long1;
            this.name = string2;
            this.motd = string3;
            this.lastScreen = realmsScreen;
        }
        
        public void run() {
            final String string2 = RealmsScreen.getLocalizedString("mco.create.world.wait");
            this.setTitle(string2);
            final RealmsClient cvm3 = RealmsClient.createRealmsClient();
            try {
                cvm3.initializeWorld(this.worldId, this.name, this.motd);
                Realms.setScreen(this.lastScreen);
            }
            catch (RealmsServiceException cvu4) {
                RealmsTasks.LOGGER.error("Couldn't create world");
                this.error(cvu4.toString());
            }
            catch (UnsupportedEncodingException unsupportedEncodingException4) {
                RealmsTasks.LOGGER.error("Couldn't create world");
                this.error(unsupportedEncodingException4.getLocalizedMessage());
            }
            catch (IOException iOException4) {
                RealmsTasks.LOGGER.error("Could not parse response creating world");
                this.error(iOException4.getLocalizedMessage());
            }
            catch (Exception exception4) {
                RealmsTasks.LOGGER.error("Could not create world");
                this.error(exception4.getLocalizedMessage());
            }
        }
    }
    
    public static class TrialCreationTask extends LongRunningTask {
        private final String name;
        private final String motd;
        private final RealmsMainScreen lastScreen;
        
        public TrialCreationTask(final String string1, final String string2, final RealmsMainScreen cvi) {
            this.name = string1;
            this.motd = string2;
            this.lastScreen = cvi;
        }
        
        public void run() {
            final String string2 = RealmsScreen.getLocalizedString("mco.create.world.wait");
            this.setTitle(string2);
            final RealmsClient cvm3 = RealmsClient.createRealmsClient();
            try {
                final RealmsServer realmsServer4 = cvm3.createTrial(this.name, this.motd);
                if (realmsServer4 != null) {
                    this.lastScreen.setCreatedTrial(true);
                    this.lastScreen.closePopup();
                    final RealmsResetWorldScreen cwu5 = new RealmsResetWorldScreen(this.lastScreen, realmsServer4, this.lastScreen.newScreen(), RealmsScreen.getLocalizedString("mco.selectServer.create"), RealmsScreen.getLocalizedString("mco.create.world.subtitle"), 10526880, RealmsScreen.getLocalizedString("mco.create.world.skip"));
                    cwu5.setResetTitle(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
                    Realms.setScreen(cwu5);
                }
                else {
                    this.error(RealmsScreen.getLocalizedString("mco.trial.unavailable"));
                }
            }
            catch (RealmsServiceException cvu4) {
                RealmsTasks.LOGGER.error("Couldn't create trial");
                this.error(cvu4.toString());
            }
            catch (UnsupportedEncodingException unsupportedEncodingException4) {
                RealmsTasks.LOGGER.error("Couldn't create trial");
                this.error(unsupportedEncodingException4.getLocalizedMessage());
            }
            catch (IOException iOException4) {
                RealmsTasks.LOGGER.error("Could not parse response creating trial");
                this.error(iOException4.getLocalizedMessage());
            }
            catch (Exception exception4) {
                RealmsTasks.LOGGER.error("Could not create trial");
                this.error(exception4.getLocalizedMessage());
            }
        }
    }
    
    public static class RestoreTask extends LongRunningTask {
        private final Backup backup;
        private final long worldId;
        private final RealmsConfigureWorldScreen lastScreen;
        
        public RestoreTask(final Backup backup, final long long2, final RealmsConfigureWorldScreen cwg) {
            this.backup = backup;
            this.worldId = long2;
            this.lastScreen = cwg;
        }
        
        public void run() {
            this.setTitle(RealmsScreen.getLocalizedString("mco.backup.restoring"));
            final RealmsClient cvm2 = RealmsClient.createRealmsClient();
            for (int integer3 = 0; integer3 < 25; ++integer3) {
                try {
                    if (this.aborted()) {
                        return;
                    }
                    cvm2.restoreWorld(this.worldId, this.backup.backupId);
                    pause(1);
                    if (this.aborted()) {
                        return;
                    }
                    Realms.setScreen(this.lastScreen.getNewScreen());
                    return;
                }
                catch (RetryCallException cvv4) {
                    if (this.aborted()) {
                        return;
                    }
                    pause(cvv4.delaySeconds);
                }
                catch (RealmsServiceException cvu4) {
                    if (this.aborted()) {
                        return;
                    }
                    RealmsTasks.LOGGER.error("Couldn't restore backup", (Throwable)cvu4);
                    Realms.setScreen(new RealmsGenericErrorScreen(cvu4, this.lastScreen));
                    return;
                }
                catch (Exception exception4) {
                    if (this.aborted()) {
                        return;
                    }
                    RealmsTasks.LOGGER.error("Couldn't restore backup", (Throwable)exception4);
                    this.error(exception4.getLocalizedMessage());
                    return;
                }
            }
        }
    }
    
    public static class DownloadTask extends LongRunningTask {
        private final long worldId;
        private final int slot;
        private final RealmsScreen lastScreen;
        private final String downloadName;
        
        public DownloadTask(final long long1, final int integer, final String string, final RealmsScreen realmsScreen) {
            this.worldId = long1;
            this.slot = integer;
            this.lastScreen = realmsScreen;
            this.downloadName = string;
        }
        
        public void run() {
            this.setTitle(RealmsScreen.getLocalizedString("mco.download.preparing"));
            final RealmsClient cvm2 = RealmsClient.createRealmsClient();
            for (int integer3 = 0; integer3 < 25; ++integer3) {
                try {
                    if (this.aborted()) {
                        return;
                    }
                    final WorldDownload worldDownload4 = cvm2.download(this.worldId, this.slot);
                    pause(1);
                    if (this.aborted()) {
                        return;
                    }
                    Realms.setScreen(new RealmsDownloadLatestWorldScreen(this.lastScreen, worldDownload4, this.downloadName));
                    return;
                }
                catch (RetryCallException cvv4) {
                    if (this.aborted()) {
                        return;
                    }
                    pause(cvv4.delaySeconds);
                }
                catch (RealmsServiceException cvu4) {
                    if (this.aborted()) {
                        return;
                    }
                    RealmsTasks.LOGGER.error("Couldn't download world data");
                    Realms.setScreen(new RealmsGenericErrorScreen(cvu4, this.lastScreen));
                    return;
                }
                catch (Exception exception4) {
                    if (this.aborted()) {
                        return;
                    }
                    RealmsTasks.LOGGER.error("Couldn't download world data", (Throwable)exception4);
                    this.error(exception4.getLocalizedMessage());
                    return;
                }
            }
        }
    }
}
