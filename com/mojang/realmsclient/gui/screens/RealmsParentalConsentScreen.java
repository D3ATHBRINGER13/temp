package com.mojang.realmsclient.gui.screens;

import java.util.Iterator;
import java.util.List;
import net.minecraft.realms.AbstractRealmsButton;
import com.mojang.realmsclient.util.RealmsUtil;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;

public class RealmsParentalConsentScreen extends RealmsScreen {
    private final RealmsScreen nextScreen;
    
    public RealmsParentalConsentScreen(final RealmsScreen realmsScreen) {
        this.nextScreen = realmsScreen;
    }
    
    @Override
    public void init() {
        Realms.narrateNow(RealmsScreen.getLocalizedString("mco.account.privacyinfo"));
        final String string2 = RealmsScreen.getLocalizedString("mco.account.update");
        final String string3 = RealmsScreen.getLocalizedString("gui.back");
        final int integer4 = Math.max(this.fontWidth(string2), this.fontWidth(string3)) + 30;
        final String string4 = RealmsScreen.getLocalizedString("mco.account.privacy.info");
        final int integer5 = (int)(this.fontWidth(string4) * 1.2);
        this.buttonsAdd(new RealmsButton(1, this.width() / 2 - integer5 / 2, RealmsConstants.row(11), integer5, 20, string4) {
            @Override
            public void onPress() {
                RealmsUtil.browseTo("https://minecraft.net/privacy/gdpr/");
            }
        });
        this.buttonsAdd(new RealmsButton(1, this.width() / 2 - (integer4 + 5), RealmsConstants.row(13), integer4, 20, string2) {
            @Override
            public void onPress() {
                RealmsUtil.browseTo("https://minecraft.net/update-account");
            }
        });
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 5, RealmsConstants.row(13), integer4, 20, string3) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsParentalConsentScreen.this.nextScreen);
            }
        });
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        final List<String> list5 = this.getLocalizedStringWithLineWidth("mco.account.privacyinfo", (int)Math.round(this.width() * 0.9));
        int integer3 = 15;
        for (final String string8 : list5) {
            this.drawCenteredString(string8, this.width() / 2, integer3, 16777215);
            integer3 += 15;
        }
        super.render(integer1, integer2, float3);
    }
}
