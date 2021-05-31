package com.mojang.realmsclient.gui.screens;

import org.apache.logging.log4j.LogManager;
import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.Realms;
import net.minecraft.realms.AbstractRealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.realms.RealmsEditBox;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsInviteScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private RealmsEditBox profileName;
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;
    private final RealmsScreen lastScreen;
    private final int BUTTON_INVITE_ID = 0;
    private final int BUTTON_CANCEL_ID = 1;
    private RealmsButton inviteButton;
    private final int PROFILENAME_EDIT_BOX = 2;
    private String errorMsg;
    private boolean showError;
    
    public RealmsInviteScreen(final RealmsConfigureWorldScreen cwg, final RealmsScreen realmsScreen, final RealmsServer realmsServer) {
        this.configureScreen = cwg;
        this.lastScreen = realmsScreen;
        this.serverData = realmsServer;
    }
    
    @Override
    public void tick() {
        this.profileName.tick();
    }
    
    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.buttonsAdd(this.inviteButton = new RealmsButton(0, this.width() / 2 - 100, RealmsConstants.row(10), RealmsScreen.getLocalizedString("mco.configure.world.buttons.invite")) {
            @Override
            public void onPress() {
                RealmsInviteScreen.this.onInvite();
            }
        });
        this.buttonsAdd(new RealmsButton(1, this.width() / 2 - 100, RealmsConstants.row(12), RealmsScreen.getLocalizedString("gui.cancel")) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsInviteScreen.this.lastScreen);
            }
        });
        this.focusOn(this.profileName = this.newEditBox(2, this.width() / 2 - 100, RealmsConstants.row(2), 200, 20, RealmsScreen.getLocalizedString("mco.configure.world.invite.profile.name")));
        this.addWidget(this.profileName);
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }
    
    private void onInvite() {
        final RealmsClient cvm2 = RealmsClient.createRealmsClient();
        if (this.profileName.getValue() == null || this.profileName.getValue().isEmpty()) {
            this.showError(RealmsScreen.getLocalizedString("mco.configure.world.players.error"));
            return;
        }
        try {
            final RealmsServer realmsServer3 = cvm2.invite(this.serverData.id, this.profileName.getValue().trim());
            if (realmsServer3 != null) {
                this.serverData.players = realmsServer3.players;
                Realms.setScreen(new RealmsPlayerScreen(this.configureScreen, this.serverData));
            }
            else {
                this.showError(RealmsScreen.getLocalizedString("mco.configure.world.players.error"));
            }
        }
        catch (Exception exception3) {
            RealmsInviteScreen.LOGGER.error("Couldn't invite user");
            this.showError(RealmsScreen.getLocalizedString("mco.configure.world.players.error"));
        }
    }
    
    private void showError(final String string) {
        this.showError = true;
        Realms.narrateNow(this.errorMsg = string);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            Realms.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.invite.profile.name"), this.width() / 2 - 100, RealmsConstants.row(1), 10526880);
        if (this.showError) {
            this.drawCenteredString(this.errorMsg, this.width() / 2, RealmsConstants.row(5), 16711680);
        }
        this.profileName.render(integer1, integer2, float3);
        super.render(integer1, integer2, float3);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
