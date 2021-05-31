package com.mojang.realmsclient.gui.screens;

import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.Realms;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.realms.RealmsScreen;

public class RealmsGenericErrorScreen extends RealmsScreen {
    private final RealmsScreen nextScreen;
    private String line1;
    private String line2;
    
    public RealmsGenericErrorScreen(final RealmsServiceException cvu, final RealmsScreen realmsScreen) {
        this.nextScreen = realmsScreen;
        this.errorMessage(cvu);
    }
    
    public RealmsGenericErrorScreen(final String string, final RealmsScreen realmsScreen) {
        this.nextScreen = realmsScreen;
        this.errorMessage(string);
    }
    
    public RealmsGenericErrorScreen(final String string1, final String string2, final RealmsScreen realmsScreen) {
        this.nextScreen = realmsScreen;
        this.errorMessage(string1, string2);
    }
    
    private void errorMessage(final RealmsServiceException cvu) {
        if (cvu.errorCode == -1) {
            this.line1 = new StringBuilder().append("An error occurred (").append(cvu.httpResultCode).append("):").toString();
            this.line2 = cvu.httpResponseContent;
        }
        else {
            this.line1 = new StringBuilder().append("Realms (").append(cvu.errorCode).append("):").toString();
            final String string3 = new StringBuilder().append("mco.errorMessage.").append(cvu.errorCode).toString();
            final String string4 = RealmsScreen.getLocalizedString(string3);
            this.line2 = (string4.equals(string3) ? cvu.errorMsg : string4);
        }
    }
    
    private void errorMessage(final String string) {
        this.line1 = "An error occurred: ";
        this.line2 = string;
    }
    
    private void errorMessage(final String string1, final String string2) {
        this.line1 = string1;
        this.line2 = string2;
    }
    
    @Override
    public void init() {
        Realms.narrateNow(this.line1 + ": " + this.line2);
        this.buttonsAdd(new RealmsButton(10, this.width() / 2 - 100, this.height() - 52, 200, 20, "Ok") {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsGenericErrorScreen.this.nextScreen);
            }
        });
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.line1, this.width() / 2, 80, 16777215);
        this.drawCenteredString(this.line2, this.width() / 2, 100, 16711680);
        super.render(integer1, integer2, float3);
    }
}
