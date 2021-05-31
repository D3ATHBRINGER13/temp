package com.mojang.realmsclient.gui.screens;

import org.apache.logging.log4j.LogManager;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.util.RealmsTasks;
import java.io.UnsupportedEncodingException;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nonnull;
import java.io.IOException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.client.RealmsClient;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.RealmsMainScreen;
import org.apache.logging.log4j.Logger;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.dto.WorldTemplate;

public class RealmsConfigureWorldScreen extends RealmsScreenWithCallback<WorldTemplate> implements RealmsWorldSlotButton.Listener {
    private static final Logger LOGGER;
    private String toolTip;
    private final RealmsMainScreen lastScreen;
    private RealmsServer serverData;
    private final long serverId;
    private int left_x;
    private int right_x;
    private final int default_button_width = 80;
    private final int default_button_offset = 5;
    private RealmsButton playersButton;
    private RealmsButton settingsButton;
    private RealmsButton subscriptionButton;
    private RealmsButton optionsButton;
    private RealmsButton backupButton;
    private RealmsButton resetWorldButton;
    private RealmsButton switchMinigameButton;
    private boolean stateChanged;
    private int animTick;
    private int clicks;
    
    public RealmsConfigureWorldScreen(final RealmsMainScreen cvi, final long long2) {
        this.lastScreen = cvi;
        this.serverId = long2;
    }
    
    @Override
    public void init() {
        if (this.serverData == null) {
            this.fetchServerData(this.serverId);
        }
        this.left_x = this.width() / 2 - 187;
        this.right_x = this.width() / 2 + 190;
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.buttonsAdd(this.playersButton = new RealmsButton(2, this.centerButton(0, 3), RealmsConstants.row(0), 100, 20, RealmsScreen.getLocalizedString("mco.configure.world.buttons.players")) {
            @Override
            public void onPress() {
                Realms.setScreen(new RealmsPlayerScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData));
            }
        });
        this.buttonsAdd(this.settingsButton = new RealmsButton(3, this.centerButton(1, 3), RealmsConstants.row(0), 100, 20, RealmsScreen.getLocalizedString("mco.configure.world.buttons.settings")) {
            @Override
            public void onPress() {
                Realms.setScreen(new RealmsSettingsScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone()));
            }
        });
        this.buttonsAdd(this.subscriptionButton = new RealmsButton(4, this.centerButton(2, 3), RealmsConstants.row(0), 100, 20, RealmsScreen.getLocalizedString("mco.configure.world.buttons.subscription")) {
            @Override
            public void onPress() {
                Realms.setScreen(new RealmsSubscriptionInfoScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.lastScreen));
            }
        });
        for (int integer2 = 1; integer2 < 5; ++integer2) {
            this.addSlotButton(integer2);
        }
        this.buttonsAdd(this.switchMinigameButton = new RealmsButton(8, this.leftButton(0), RealmsConstants.row(13) - 5, 100, 20, RealmsScreen.getLocalizedString("mco.configure.world.buttons.switchminigame")) {
            @Override
            public void onPress() {
                final RealmsSelectWorldTemplateScreen cwy2 = new RealmsSelectWorldTemplateScreen(RealmsConfigureWorldScreen.this, RealmsServer.WorldType.MINIGAME);
                cwy2.setTitle(RealmsScreen.getLocalizedString("mco.template.title.minigame"));
                Realms.setScreen(cwy2);
            }
        });
        this.buttonsAdd(this.optionsButton = new RealmsButton(5, this.leftButton(0), RealmsConstants.row(13) - 5, 90, 20, RealmsScreen.getLocalizedString("mco.configure.world.buttons.options")) {
            @Override
            public void onPress() {
                Realms.setScreen(new RealmsSlotOptionsScreen(RealmsConfigureWorldScreen.this, ((RealmsWorldOptions)RealmsConfigureWorldScreen.this.serverData.slots.get(RealmsConfigureWorldScreen.this.serverData.activeSlot)).clone(), RealmsConfigureWorldScreen.this.serverData.worldType, RealmsConfigureWorldScreen.this.serverData.activeSlot));
            }
        });
        this.buttonsAdd(this.backupButton = new RealmsButton(6, this.leftButton(1), RealmsConstants.row(13) - 5, 90, 20, RealmsScreen.getLocalizedString("mco.configure.world.backup")) {
            @Override
            public void onPress() {
                Realms.setScreen(new RealmsBackupScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.serverData.activeSlot));
            }
        });
        this.buttonsAdd(this.resetWorldButton = new RealmsButton(7, this.leftButton(2), RealmsConstants.row(13) - 5, 90, 20, RealmsScreen.getLocalizedString("mco.configure.world.buttons.resetworld")) {
            @Override
            public void onPress() {
                Realms.setScreen(new RealmsResetWorldScreen(RealmsConfigureWorldScreen.this, RealmsConfigureWorldScreen.this.serverData.clone(), RealmsConfigureWorldScreen.this.getNewScreen()));
            }
        });
        this.buttonsAdd(new RealmsButton(0, this.right_x - 80 + 8, RealmsConstants.row(13) - 5, 70, 20, RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                RealmsConfigureWorldScreen.this.backButtonClicked();
            }
        });
        this.backupButton.active(true);
        if (this.serverData == null) {
            this.hideMinigameButtons();
            this.hideRegularButtons();
            this.playersButton.active(false);
            this.settingsButton.active(false);
            this.subscriptionButton.active(false);
        }
        else {
            this.disableButtons();
            if (this.isMinigame()) {
                this.hideRegularButtons();
            }
            else {
                this.hideMinigameButtons();
            }
        }
    }
    
    private void addSlotButton(final int integer) {
        final int integer2 = this.frame(integer);
        final int integer3 = RealmsConstants.row(5) + 5;
        final int integer4 = 100 + integer;
        final RealmsWorldSlotButton cwa6 = new RealmsWorldSlotButton(integer2, integer3, 80, 80, (Supplier<RealmsServer>)(() -> this.serverData), (Consumer<String>)(string -> this.toolTip = string), integer4, integer, this);
        this.getProxy().buttonsAdd(cwa6);
    }
    
    private int leftButton(final int integer) {
        return this.left_x + integer * 95;
    }
    
    private int centerButton(final int integer1, final int integer2) {
        return this.width() / 2 - (integer2 * 105 - 5) / 2 + integer1 * 105;
    }
    
    @Override
    public void tick() {
        this.tickButtons();
        ++this.animTick;
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.toolTip = null;
        this.renderBackground();
        this.drawCenteredString(RealmsScreen.getLocalizedString("mco.configure.worlds.title"), this.width() / 2, RealmsConstants.row(4), 16777215);
        super.render(integer1, integer2, float3);
        if (this.serverData == null) {
            this.drawCenteredString(RealmsScreen.getLocalizedString("mco.configure.world.title"), this.width() / 2, 17, 16777215);
            return;
        }
        final String string5 = this.serverData.getName();
        final int integer3 = this.fontWidth(string5);
        final int integer4 = (this.serverData.state == RealmsServer.State.CLOSED) ? 10526880 : 8388479;
        final int integer5 = this.fontWidth(RealmsScreen.getLocalizedString("mco.configure.world.title"));
        this.drawCenteredString(RealmsScreen.getLocalizedString("mco.configure.world.title"), this.width() / 2, 12, 16777215);
        this.drawCenteredString(string5, this.width() / 2, 24, integer4);
        final int integer6 = Math.min(this.centerButton(2, 3) + 80 - 11, this.width() / 2 + integer3 / 2 + integer5 / 2 + 10);
        this.drawServerStatus(integer6, 7, integer1, integer2);
        if (this.isMinigame()) {
            this.drawString(RealmsScreen.getLocalizedString("mco.configure.current.minigame") + ": " + this.serverData.getMinigameName(), this.left_x + 80 + 20 + 10, RealmsConstants.row(13), 16777215);
        }
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, integer1, integer2);
        }
    }
    
    private int frame(final int integer) {
        return this.left_x + (integer - 1) * 98;
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }
    
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    private void backButtonClicked() {
        if (this.stateChanged) {
            this.lastScreen.removeSelection();
        }
        Realms.setScreen(this.lastScreen);
    }
    
    private void fetchServerData(final long long1) {
        new Thread() {
            public void run() {
                final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                try {
                    RealmsConfigureWorldScreen.this.serverData = cvm2.getOwnWorld(long1);
                    RealmsConfigureWorldScreen.this.disableButtons();
                    if (RealmsConfigureWorldScreen.this.isMinigame()) {
                        RealmsConfigureWorldScreen.this.showMinigameButtons();
                    }
                    else {
                        RealmsConfigureWorldScreen.this.showRegularButtons();
                    }
                }
                catch (RealmsServiceException cvu3) {
                    RealmsConfigureWorldScreen.LOGGER.error("Couldn't get own world");
                    Realms.setScreen(new RealmsGenericErrorScreen(cvu3.getMessage(), RealmsConfigureWorldScreen.this.lastScreen));
                }
                catch (IOException iOException3) {
                    RealmsConfigureWorldScreen.LOGGER.error("Couldn't parse response getting own world");
                }
            }
        }.start();
    }
    
    private void disableButtons() {
        this.playersButton.active(!this.serverData.expired);
        this.settingsButton.active(!this.serverData.expired);
        this.subscriptionButton.active(true);
        this.switchMinigameButton.active(!this.serverData.expired);
        this.optionsButton.active(!this.serverData.expired);
        this.resetWorldButton.active(!this.serverData.expired);
    }
    
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return super.mouseClicked(double1, double2, integer);
    }
    
    private void joinRealm(final RealmsServer realmsServer) {
        if (this.serverData.state == RealmsServer.State.OPEN) {
            this.lastScreen.play(realmsServer, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
        }
        else {
            this.openTheWorld(true, new RealmsConfigureWorldScreen(this.lastScreen.newScreen(), this.serverId));
        }
    }
    
    @Override
    public void onSlotClick(final int integer, @Nonnull final RealmsWorldSlotButton.Action a, final boolean boolean3, final boolean boolean4) {
        switch (a) {
            case NOTHING: {
                break;
            }
            case JOIN: {
                this.joinRealm(this.serverData);
                break;
            }
            case SWITCH_SLOT: {
                if (boolean3) {
                    this.switchToMinigame();
                    break;
                }
                if (boolean4) {
                    this.switchToEmptySlot(integer, this.serverData);
                    break;
                }
                this.switchToFullSlot(integer, this.serverData);
                break;
            }
            default: {
                throw new IllegalStateException(new StringBuilder().append("Unknown action ").append(a).toString());
            }
        }
    }
    
    private void switchToMinigame() {
        final RealmsSelectWorldTemplateScreen cwy2 = new RealmsSelectWorldTemplateScreen(this, RealmsServer.WorldType.MINIGAME);
        cwy2.setTitle(RealmsScreen.getLocalizedString("mco.template.title.minigame"));
        cwy2.setWarning(RealmsScreen.getLocalizedString("mco.minigame.world.info.line1") + "\\n" + RealmsScreen.getLocalizedString("mco.minigame.world.info.line2"));
        Realms.setScreen(cwy2);
    }
    
    private void switchToFullSlot(final int integer, final RealmsServer realmsServer) {
        final String string4 = RealmsScreen.getLocalizedString("mco.configure.world.slot.switch.question.line1");
        final String string5 = RealmsScreen.getLocalizedString("mco.configure.world.slot.switch.question.line2");
        Realms.setScreen(new RealmsLongConfirmationScreen((boolean3, integer4) -> {
            if (boolean3) {
                this.switchSlot(realmsServer.id, integer);
            }
            else {
                Realms.setScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, string4, string5, true, 9));
    }
    
    private void switchToEmptySlot(final int integer, final RealmsServer realmsServer) {
        final String string4 = RealmsScreen.getLocalizedString("mco.configure.world.slot.switch.question.line1");
        final String string5 = RealmsScreen.getLocalizedString("mco.configure.world.slot.switch.question.line2");
        RealmsResetWorldScreen cwu6;
        Realms.setScreen(new RealmsLongConfirmationScreen((boolean3, integer4) -> {
            if (boolean3) {
                cwu6 = new RealmsResetWorldScreen(this, realmsServer, this.getNewScreen(), RealmsScreen.getLocalizedString("mco.configure.world.switch.slot"), RealmsScreen.getLocalizedString("mco.configure.world.switch.slot.subtitle"), 10526880, RealmsScreen.getLocalizedString("gui.cancel"));
                cwu6.setSlot(integer);
                cwu6.setResetTitle(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
                Realms.setScreen(cwu6);
            }
            else {
                Realms.setScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, string4, string5, true, 10));
    }
    
    protected void renderMousehoverTooltip(final String string, final int integer2, final int integer3) {
        if (string == null) {
            return;
        }
        int integer4 = integer2 + 12;
        final int integer5 = integer3 - 12;
        final int integer6 = this.fontWidth(string);
        if (integer4 + integer6 + 3 > this.right_x) {
            integer4 = integer4 - integer6 - 20;
        }
        this.fillGradient(integer4 - 3, integer5 - 3, integer4 + integer6 + 3, integer5 + 8 + 3, -1073741824, -1073741824);
        this.fontDrawShadow(string, integer4, integer5, 16777215);
    }
    
    private void drawServerStatus(final int integer1, final int integer2, final int integer3, final int integer4) {
        if (this.serverData.expired) {
            this.drawExpired(integer1, integer2, integer3, integer4);
        }
        else if (this.serverData.state == RealmsServer.State.CLOSED) {
            this.drawClose(integer1, integer2, integer3, integer4);
        }
        else if (this.serverData.state == RealmsServer.State.OPEN) {
            if (this.serverData.daysLeft < 7) {
                this.drawExpiring(integer1, integer2, integer3, integer4, this.serverData.daysLeft);
            }
            else {
                this.drawOpen(integer1, integer2, integer3, integer4);
            }
        }
    }
    
    private void drawExpired(final int integer1, final int integer2, final int integer3, final int integer4) {
        RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer1, integer2, 0.0f, 0.0f, 10, 28, 10, 28);
        GlStateManager.popMatrix();
        if (integer3 >= integer1 && integer3 <= integer1 + 9 && integer4 >= integer2 && integer4 <= integer2 + 27) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.expired");
        }
    }
    
    private void drawExpiring(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        if (this.animTick % 20 < 10) {
            RealmsScreen.blit(integer1, integer2, 0.0f, 0.0f, 10, 28, 20, 28);
        }
        else {
            RealmsScreen.blit(integer1, integer2, 10.0f, 0.0f, 10, 28, 20, 28);
        }
        GlStateManager.popMatrix();
        if (integer3 >= integer1 && integer3 <= integer1 + 9 && integer4 >= integer2 && integer4 <= integer2 + 27) {
            if (integer5 <= 0) {
                this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.expires.soon");
            }
            else if (integer5 == 1) {
                this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.expires.day");
            }
            else {
                this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.expires.days", integer5);
            }
        }
    }
    
    private void drawOpen(final int integer1, final int integer2, final int integer3, final int integer4) {
        RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer1, integer2, 0.0f, 0.0f, 10, 28, 10, 28);
        GlStateManager.popMatrix();
        if (integer3 >= integer1 && integer3 <= integer1 + 9 && integer4 >= integer2 && integer4 <= integer2 + 27) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.open");
        }
    }
    
    private void drawClose(final int integer1, final int integer2, final int integer3, final int integer4) {
        RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(integer1, integer2, 0.0f, 0.0f, 10, 28, 10, 28);
        GlStateManager.popMatrix();
        if (integer3 >= integer1 && integer3 <= integer1 + 9 && integer4 >= integer2 && integer4 <= integer2 + 27) {
            this.toolTip = RealmsScreen.getLocalizedString("mco.selectServer.closed");
        }
    }
    
    private boolean isMinigame() {
        return this.serverData != null && this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME);
    }
    
    private void hideRegularButtons() {
        this.hide(this.optionsButton);
        this.hide(this.backupButton);
        this.hide(this.resetWorldButton);
    }
    
    private void hide(final RealmsButton realmsButton) {
        realmsButton.setVisible(false);
        this.removeButton(realmsButton);
    }
    
    private void showRegularButtons() {
        this.show(this.optionsButton);
        this.show(this.backupButton);
        this.show(this.resetWorldButton);
    }
    
    private void show(final RealmsButton realmsButton) {
        realmsButton.setVisible(true);
        this.buttonsAdd(realmsButton);
    }
    
    private void hideMinigameButtons() {
        this.hide(this.switchMinigameButton);
    }
    
    private void showMinigameButtons() {
        this.show(this.switchMinigameButton);
    }
    
    public void saveSlotSettings(final RealmsWorldOptions realmsWorldOptions) {
        final RealmsWorldOptions realmsWorldOptions2 = (RealmsWorldOptions)this.serverData.slots.get(this.serverData.activeSlot);
        realmsWorldOptions.templateId = realmsWorldOptions2.templateId;
        realmsWorldOptions.templateImage = realmsWorldOptions2.templateImage;
        final RealmsClient cvm4 = RealmsClient.createRealmsClient();
        try {
            cvm4.updateSlot(this.serverData.id, this.serverData.activeSlot, realmsWorldOptions);
            this.serverData.slots.put(this.serverData.activeSlot, realmsWorldOptions);
        }
        catch (RealmsServiceException cvu5) {
            RealmsConfigureWorldScreen.LOGGER.error("Couldn't save slot settings");
            Realms.setScreen(new RealmsGenericErrorScreen(cvu5, this));
            return;
        }
        catch (UnsupportedEncodingException unsupportedEncodingException5) {
            RealmsConfigureWorldScreen.LOGGER.error("Couldn't save slot settings");
        }
        Realms.setScreen(this);
    }
    
    public void saveSettings(final String string1, final String string2) {
        final String string3 = (string2 == null || string2.trim().isEmpty()) ? null : string2;
        final RealmsClient cvm5 = RealmsClient.createRealmsClient();
        try {
            cvm5.update(this.serverData.id, string1, string3);
            this.serverData.setName(string1);
            this.serverData.setDescription(string3);
        }
        catch (RealmsServiceException cvu6) {
            RealmsConfigureWorldScreen.LOGGER.error("Couldn't save settings");
            Realms.setScreen(new RealmsGenericErrorScreen(cvu6, this));
            return;
        }
        catch (UnsupportedEncodingException unsupportedEncodingException6) {
            RealmsConfigureWorldScreen.LOGGER.error("Couldn't save settings");
        }
        Realms.setScreen(this);
    }
    
    public void openTheWorld(final boolean boolean1, final RealmsScreen realmsScreen) {
        final RealmsTasks.OpenServerTask c4 = new RealmsTasks.OpenServerTask(this.serverData, this, this.lastScreen, boolean1);
        final RealmsLongRunningMcoTaskScreen cwo5 = new RealmsLongRunningMcoTaskScreen(realmsScreen, c4);
        cwo5.start();
        Realms.setScreen(cwo5);
    }
    
    public void closeTheWorld(final RealmsScreen realmsScreen) {
        final RealmsTasks.CloseServerTask a3 = new RealmsTasks.CloseServerTask(this.serverData, this);
        final RealmsLongRunningMcoTaskScreen cwo4 = new RealmsLongRunningMcoTaskScreen(realmsScreen, a3);
        cwo4.start();
        Realms.setScreen(cwo4);
    }
    
    public void stateChanged() {
        this.stateChanged = true;
    }
    
    @Override
    void callback(final WorldTemplate worldTemplate) {
        if (worldTemplate == null) {
            return;
        }
        if (WorldTemplate.WorldTemplateType.MINIGAME.equals(worldTemplate.type)) {
            this.switchMinigame(worldTemplate);
        }
    }
    
    private void switchSlot(final long long1, final int integer) {
        final RealmsConfigureWorldScreen cwg5 = this.getNewScreen();
        final RealmsTasks.SwitchSlotTask i6 = new RealmsTasks.SwitchSlotTask(long1, integer, (boolean2, integer) -> Realms.setScreen(cwg5), 11);
        final RealmsLongRunningMcoTaskScreen cwo7 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, i6);
        cwo7.start();
        Realms.setScreen(cwo7);
    }
    
    private void switchMinigame(final WorldTemplate worldTemplate) {
        final RealmsTasks.SwitchMinigameTask h3 = new RealmsTasks.SwitchMinigameTask(this.serverData.id, worldTemplate, this.getNewScreen());
        final RealmsLongRunningMcoTaskScreen cwo4 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, h3);
        cwo4.start();
        Realms.setScreen(cwo4);
    }
    
    public RealmsConfigureWorldScreen getNewScreen() {
        return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
