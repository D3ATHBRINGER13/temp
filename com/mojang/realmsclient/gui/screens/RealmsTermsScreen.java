package com.mojang.realmsclient.gui.screens;

import org.apache.logging.log4j.LogManager;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.util.RealmsTasks;
import java.util.concurrent.locks.ReentrantLock;
import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.realms.Realms;
import net.minecraft.realms.AbstractRealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.RealmsMainScreen;
import org.apache.logging.log4j.Logger;
import net.minecraft.realms.RealmsScreen;

public class RealmsTermsScreen extends RealmsScreen {
    private static final Logger LOGGER;
    private final RealmsScreen lastScreen;
    private final RealmsMainScreen mainScreen;
    private final RealmsServer realmsServer;
    private RealmsButton agreeButton;
    private boolean onLink;
    private final String realmsToSUrl = "https://minecraft.net/realms/terms";
    
    public RealmsTermsScreen(final RealmsScreen realmsScreen, final RealmsMainScreen cvi, final RealmsServer realmsServer) {
        this.lastScreen = realmsScreen;
        this.mainScreen = cvi;
        this.realmsServer = realmsServer;
    }
    
    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        final int integer2 = this.width() / 4;
        final int integer3 = this.width() / 4 - 2;
        final int integer4 = this.width() / 2 + 4;
        this.buttonsAdd(this.agreeButton = new RealmsButton(1, integer2, RealmsConstants.row(12), integer3, 20, RealmsScreen.getLocalizedString("mco.terms.buttons.agree")) {
            @Override
            public void onPress() {
                RealmsTermsScreen.this.agreedToTos();
            }
        });
        this.buttonsAdd(new RealmsButton(2, integer4, RealmsConstants.row(12), integer3, 20, RealmsScreen.getLocalizedString("mco.terms.buttons.disagree")) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsTermsScreen.this.lastScreen);
            }
        });
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            Realms.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    private void agreedToTos() {
        final RealmsClient cvm2 = RealmsClient.createRealmsClient();
        try {
            cvm2.agreeToTos();
            final RealmsLongRunningMcoTaskScreen cwo3 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsTasks.RealmsGetServerDetailsTask(this.mainScreen, this.lastScreen, this.realmsServer, new ReentrantLock()));
            cwo3.start();
            Realms.setScreen(cwo3);
        }
        catch (RealmsServiceException cvu3) {
            RealmsTermsScreen.LOGGER.error("Couldn't agree to TOS");
        }
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (this.onLink) {
            Realms.setClipboard("https://minecraft.net/realms/terms");
            RealmsUtil.browseTo("https://minecraft.net/realms/terms");
            return true;
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(RealmsScreen.getLocalizedString("mco.terms.title"), this.width() / 2, 17, 16777215);
        this.drawString(RealmsScreen.getLocalizedString("mco.terms.sentence.1"), this.width() / 2 - 120, RealmsConstants.row(5), 16777215);
        final int integer3 = this.fontWidth(RealmsScreen.getLocalizedString("mco.terms.sentence.1"));
        final int integer4 = this.width() / 2 - 121 + integer3;
        final int integer5 = RealmsConstants.row(5);
        final int integer6 = integer4 + this.fontWidth("mco.terms.sentence.2") + 1;
        final int integer7 = integer5 + 1 + this.fontLineHeight();
        if (integer4 <= integer1 && integer1 <= integer6 && integer5 <= integer2 && integer2 <= integer7) {
            this.onLink = true;
            this.drawString(" " + RealmsScreen.getLocalizedString("mco.terms.sentence.2"), this.width() / 2 - 120 + integer3, RealmsConstants.row(5), 7107012);
        }
        else {
            this.onLink = false;
            this.drawString(" " + RealmsScreen.getLocalizedString("mco.terms.sentence.2"), this.width() / 2 - 120 + integer3, RealmsConstants.row(5), 3368635);
        }
        super.render(integer1, integer2, float3);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
