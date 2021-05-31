package com.mojang.realmsclient.gui.screens;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.RealmsMth;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.util.RealmsTasks;
import java.io.IOException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.client.RealmsClient;
import java.util.Iterator;
import net.minecraft.realms.Realms;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import java.util.Map;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import java.util.ArrayList;
import java.util.List;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.RealmsMainScreen;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsBrokenWorldScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private final RealmsScreen lastScreen;
    private final RealmsMainScreen mainScreen;
    private RealmsServer serverData;
    private final long serverId;
    private String title;
    private final String message;
    private int left_x;
    private int right_x;
    private final int default_button_width = 80;
    private final int default_button_offset = 5;
    private static final List<Integer> playButtonIds;
    private static final List<Integer> resetButtonIds;
    private static final List<Integer> downloadButtonIds;
    private static final List<Integer> downloadConfirmationIds;
    private final List<Integer> slotsThatHasBeenDownloaded;
    private int animTick;
    
    public RealmsBrokenWorldScreen(final RealmsScreen realmsScreen, final RealmsMainScreen cvi, final long long3) {
        this.title = RealmsScreen.getLocalizedString("mco.brokenworld.title");
        this.message = RealmsScreen.getLocalizedString("mco.brokenworld.message.line1") + "\\n" + RealmsScreen.getLocalizedString("mco.brokenworld.message.line2");
        this.slotsThatHasBeenDownloaded = (List<Integer>)new ArrayList();
        this.lastScreen = realmsScreen;
        this.mainScreen = cvi;
        this.serverId = long3;
    }
    
    public void setTitle(final String string) {
        this.title = string;
    }
    
    @Override
    public void init() {
        this.left_x = this.width() / 2 - 150;
        this.right_x = this.width() / 2 + 190;
        this.buttonsAdd(new RealmsButton(0, this.right_x - 80 + 8, RealmsConstants.row(13) - 5, 70, 20, RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                RealmsBrokenWorldScreen.this.backButtonClicked();
            }
        });
        if (this.serverData == null) {
            this.fetchServerData(this.serverId);
        }
        else {
            this.addButtons();
        }
        this.setKeyboardHandlerSendRepeatsToGui(true);
    }
    
    public void addButtons() {
        for (final Map.Entry<Integer, RealmsWorldOptions> entry3 : this.serverData.slots.entrySet()) {
            final RealmsWorldOptions realmsWorldOptions4 = (RealmsWorldOptions)entry3.getValue();
            final boolean boolean5 = (int)entry3.getKey() != this.serverData.activeSlot || this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME);
            RealmsButton realmsButton6;
            if (boolean5) {
                realmsButton6 = new PlayButton((int)RealmsBrokenWorldScreen.playButtonIds.get((int)entry3.getKey() - 1), this.getFramePositionX((int)entry3.getKey()), RealmsScreen.getLocalizedString("mco.brokenworld.play"));
            }
            else {
                realmsButton6 = new DownloadButton((int)RealmsBrokenWorldScreen.downloadButtonIds.get((int)entry3.getKey() - 1), this.getFramePositionX((int)entry3.getKey()), RealmsScreen.getLocalizedString("mco.brokenworld.download"));
            }
            if (this.slotsThatHasBeenDownloaded.contains(entry3.getKey())) {
                realmsButton6.active(false);
                realmsButton6.setMessage(RealmsScreen.getLocalizedString("mco.brokenworld.downloaded"));
            }
            this.buttonsAdd(realmsButton6);
            this.buttonsAdd(new RealmsButton((int)RealmsBrokenWorldScreen.resetButtonIds.get((int)entry3.getKey() - 1), this.getFramePositionX((int)entry3.getKey()), RealmsConstants.row(10), 80, 20, RealmsScreen.getLocalizedString("mco.brokenworld.reset")) {
                @Override
                public void onPress() {
                    final int integer2 = RealmsBrokenWorldScreen.resetButtonIds.indexOf(this.id()) + 1;
                    final RealmsResetWorldScreen cwu3 = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.serverData, RealmsBrokenWorldScreen.this);
                    if (integer2 != RealmsBrokenWorldScreen.this.serverData.activeSlot || RealmsBrokenWorldScreen.this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME)) {
                        cwu3.setSlot(integer2);
                    }
                    cwu3.setConfirmationId(14);
                    Realms.setScreen(cwu3);
                }
            });
        }
    }
    
    @Override
    public void tick() {
        ++this.animTick;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        super.render(integer1, integer2, float3);
        this.drawCenteredString(this.title, this.width() / 2, 17, 16777215);
        final String[] arr5 = this.message.split("\\\\n");
        for (int integer3 = 0; integer3 < arr5.length; ++integer3) {
            this.drawCenteredString(arr5[integer3], this.width() / 2, RealmsConstants.row(-1) + 3 + integer3 * 12, 10526880);
        }
        if (this.serverData == null) {
            return;
        }
        for (final Map.Entry<Integer, RealmsWorldOptions> entry7 : this.serverData.slots.entrySet()) {
            if (((RealmsWorldOptions)entry7.getValue()).templateImage != null && ((RealmsWorldOptions)entry7.getValue()).templateId != -1L) {
                this.drawSlotFrame(this.getFramePositionX((int)entry7.getKey()), RealmsConstants.row(1) + 5, integer1, integer2, this.serverData.activeSlot == (int)entry7.getKey() && !this.isMinigame(), ((RealmsWorldOptions)entry7.getValue()).getSlotName((int)entry7.getKey()), (int)entry7.getKey(), ((RealmsWorldOptions)entry7.getValue()).templateId, ((RealmsWorldOptions)entry7.getValue()).templateImage, ((RealmsWorldOptions)entry7.getValue()).empty);
            }
            else {
                this.drawSlotFrame(this.getFramePositionX((int)entry7.getKey()), RealmsConstants.row(1) + 5, integer1, integer2, this.serverData.activeSlot == (int)entry7.getKey() && !this.isMinigame(), ((RealmsWorldOptions)entry7.getValue()).getSlotName((int)entry7.getKey()), (int)entry7.getKey(), -1L, null, ((RealmsWorldOptions)entry7.getValue()).empty);
            }
        }
    }
    
    private int getFramePositionX(final int integer) {
        return this.left_x + (integer - 1) * 110;
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    private void backButtonClicked() {
        Realms.setScreen(this.lastScreen);
    }
    
    private void fetchServerData(final long long1) {
        new Thread() {
            public void run() {
                final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                try {
                    RealmsBrokenWorldScreen.this.serverData = cvm2.getOwnWorld(long1);
                    RealmsBrokenWorldScreen.this.addButtons();
                }
                catch (RealmsServiceException cvu3) {
                    RealmsBrokenWorldScreen.LOGGER.error("Couldn't get own world");
                    Realms.setScreen(new RealmsGenericErrorScreen(cvu3.getMessage(), RealmsBrokenWorldScreen.this.lastScreen));
                }
                catch (IOException iOException3) {
                    RealmsBrokenWorldScreen.LOGGER.error("Couldn't parse response getting own world");
                }
            }
        }.start();
    }
    
    @Override
    public void confirmResult(final boolean boolean1, final int integer) {
        if (!boolean1) {
            Realms.setScreen(this);
            return;
        }
        if (integer == 13 || integer == 14) {
            new Thread() {
                public void run() {
                    final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                    if (RealmsBrokenWorldScreen.this.serverData.state.equals(RealmsServer.State.CLOSED)) {
                        final RealmsTasks.OpenServerTask c3 = new RealmsTasks.OpenServerTask(RealmsBrokenWorldScreen.this.serverData, RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.lastScreen, true);
                        final RealmsLongRunningMcoTaskScreen cwo4 = new RealmsLongRunningMcoTaskScreen(RealmsBrokenWorldScreen.this, c3);
                        cwo4.start();
                        Realms.setScreen(cwo4);
                    }
                    else {
                        try {
                            RealmsBrokenWorldScreen.this.mainScreen.newScreen().play(cvm2.getOwnWorld(RealmsBrokenWorldScreen.this.serverId), RealmsBrokenWorldScreen.this);
                        }
                        catch (RealmsServiceException cvu3) {
                            RealmsBrokenWorldScreen.LOGGER.error("Couldn't get own world");
                            Realms.setScreen(RealmsBrokenWorldScreen.this.lastScreen);
                        }
                        catch (IOException iOException3) {
                            RealmsBrokenWorldScreen.LOGGER.error("Couldn't parse response getting own world");
                            Realms.setScreen(RealmsBrokenWorldScreen.this.lastScreen);
                        }
                    }
                }
            }.start();
        }
        else if (RealmsBrokenWorldScreen.downloadButtonIds.contains(integer)) {
            this.downloadWorld(RealmsBrokenWorldScreen.downloadButtonIds.indexOf(integer) + 1);
        }
        else if (RealmsBrokenWorldScreen.downloadConfirmationIds.contains(integer)) {
            this.slotsThatHasBeenDownloaded.add((RealmsBrokenWorldScreen.downloadConfirmationIds.indexOf((Object)integer) + 1));
            this.childrenClear();
            this.addButtons();
        }
    }
    
    private void downloadWorld(final int integer) {
        final RealmsClient cvm3 = RealmsClient.createRealmsClient();
        try {
            final WorldDownload worldDownload4 = cvm3.download(this.serverData.id, integer);
            final RealmsDownloadLatestWorldScreen cwk5 = new RealmsDownloadLatestWorldScreen(this, worldDownload4, this.serverData.name + " (" + ((RealmsWorldOptions)this.serverData.slots.get(integer)).getSlotName(integer) + ")");
            cwk5.setConfirmationId((int)RealmsBrokenWorldScreen.downloadConfirmationIds.get(integer - 1));
            Realms.setScreen(cwk5);
        }
        catch (RealmsServiceException cvu4) {
            RealmsBrokenWorldScreen.LOGGER.error("Couldn't download world data");
            Realms.setScreen(new RealmsGenericErrorScreen(cvu4, this));
        }
    }
    
    private boolean isMinigame() {
        return this.serverData != null && this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME);
    }
    
    private void drawSlotFrame(final int integer1, final int integer2, final int integer3, final int integer4, final boolean boolean5, final String string6, final int integer7, final long long8, final String string9, final boolean boolean10) {
        if (boolean10) {
            RealmsScreen.bind("realms:textures/gui/realms/empty_frame.png");
        }
        else if (string9 != null && long8 != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(long8), string9);
        }
        else if (integer7 == 1) {
            RealmsScreen.bind("textures/gui/title/background/panorama_0.png");
        }
        else if (integer7 == 2) {
            RealmsScreen.bind("textures/gui/title/background/panorama_2.png");
        }
        else if (integer7 == 3) {
            RealmsScreen.bind("textures/gui/title/background/panorama_3.png");
        }
        else {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage);
        }
        if (!boolean5) {
            GlStateManager.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        else if (boolean5) {
            final float float13 = 0.9f + 0.1f * RealmsMth.cos(this.animTick * 0.2f);
            GlStateManager.color4f(float13, float13, float13, 1.0f);
        }
        RealmsScreen.blit(integer1 + 3, integer2 + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        RealmsScreen.bind("realms:textures/gui/realms/slot_frame.png");
        if (boolean5) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        else {
            GlStateManager.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsScreen.blit(integer1, integer2, 0.0f, 0.0f, 80, 80, 80, 80);
        this.drawCenteredString(string6, integer1 + 40, integer2 + 66, 16777215);
    }
    
    private void switchSlot(final int integer) {
        final RealmsTasks.SwitchSlotTask i3 = new RealmsTasks.SwitchSlotTask(this.serverData.id, integer, this, 13);
        final RealmsLongRunningMcoTaskScreen cwo4 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, i3);
        cwo4.start();
        Realms.setScreen(cwo4);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        playButtonIds = Arrays.asList((Object[])new Integer[] { 1, 2, 3 });
        resetButtonIds = Arrays.asList((Object[])new Integer[] { 4, 5, 6 });
        downloadButtonIds = Arrays.asList((Object[])new Integer[] { 7, 8, 9 });
        downloadConfirmationIds = Arrays.asList((Object[])new Integer[] { 10, 11, 12 });
    }
    
    class PlayButton extends RealmsButton {
        public PlayButton(final int integer2, final int integer3, final String string) {
            super(integer2, integer3, RealmsConstants.row(8), 80, 20, string);
        }
        
        @Override
        public void onPress() {
            final int integer2 = RealmsBrokenWorldScreen.playButtonIds.indexOf(this.id()) + 1;
            if (((RealmsWorldOptions)RealmsBrokenWorldScreen.this.serverData.slots.get(integer2)).empty) {
                final RealmsResetWorldScreen cwu3 = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.serverData, RealmsBrokenWorldScreen.this, RealmsScreen.getLocalizedString("mco.configure.world.switch.slot"), RealmsScreen.getLocalizedString("mco.configure.world.switch.slot.subtitle"), 10526880, RealmsScreen.getLocalizedString("gui.cancel"));
                cwu3.setSlot(integer2);
                cwu3.setResetTitle(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
                cwu3.setConfirmationId(14);
                Realms.setScreen(cwu3);
            }
            else {
                RealmsBrokenWorldScreen.this.switchSlot(integer2);
            }
        }
    }
    
    class DownloadButton extends RealmsButton {
        public DownloadButton(final int integer2, final int integer3, final String string) {
            super(integer2, integer3, RealmsConstants.row(8), 80, 20, string);
        }
        
        @Override
        public void onPress() {
            final String string2 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line1");
            final String string3 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(RealmsBrokenWorldScreen.this, RealmsLongConfirmationScreen.Type.Info, string2, string3, true, this.id()));
        }
    }
}
