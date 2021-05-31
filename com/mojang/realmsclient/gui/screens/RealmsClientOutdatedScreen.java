package com.mojang.realmsclient.gui.screens;

import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.RealmsScreen;

public class RealmsClientOutdatedScreen extends RealmsScreen {
    private final RealmsScreen lastScreen;
    private final boolean outdated;
    
    public RealmsClientOutdatedScreen(final RealmsScreen realmsScreen, final boolean boolean2) {
        this.lastScreen = realmsScreen;
        this.outdated = boolean2;
    }
    
    @Override
    public void init() {
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, RealmsConstants.row(12), RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsClientOutdatedScreen.this.lastScreen);
            }
        });
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        final String string5 = RealmsScreen.getLocalizedString(this.outdated ? "mco.client.outdated.title" : "mco.client.incompatible.title");
        this.drawCenteredString(string5, this.width() / 2, RealmsConstants.row(3), 16711680);
        for (int integer3 = this.outdated ? 2 : 3, integer4 = 0; integer4 < integer3; ++integer4) {
            final String string6 = RealmsScreen.getLocalizedString(new StringBuilder().append(this.outdated ? "mco.client.outdated.msg.line" : "mco.client.incompatible.msg.line").append(integer4 + 1).toString());
            this.drawCenteredString(string6, this.width() / 2, RealmsConstants.row(5) + integer4 * 12, 16777215);
        }
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 257 || integer1 == 335 || integer1 == 256) {
            Realms.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
}
