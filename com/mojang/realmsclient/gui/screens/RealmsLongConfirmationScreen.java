package com.mojang.realmsclient.gui.screens;

import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.RealmsScreen;

public class RealmsLongConfirmationScreen extends RealmsScreen {
    private final Type type;
    private final String line2;
    private final String line3;
    protected final RealmsConfirmResultListener listener;
    protected final String yesButton;
    protected final String noButton;
    private final String okButton;
    protected final int id;
    private final boolean yesNoQuestion;
    
    public RealmsLongConfirmationScreen(final RealmsConfirmResultListener realmsConfirmResultListener, final Type a, final String string3, final String string4, final boolean boolean5, final int integer) {
        this.listener = realmsConfirmResultListener;
        this.id = integer;
        this.type = a;
        this.line2 = string3;
        this.line3 = string4;
        this.yesNoQuestion = boolean5;
        this.yesButton = RealmsScreen.getLocalizedString("gui.yes");
        this.noButton = RealmsScreen.getLocalizedString("gui.no");
        this.okButton = RealmsScreen.getLocalizedString("mco.gui.ok");
    }
    
    @Override
    public void init() {
        Realms.narrateNow(this.type.text, this.line2, this.line3);
        if (this.yesNoQuestion) {
            this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 105, RealmsConstants.row(8), 100, 20, this.yesButton) {
                @Override
                public void onPress() {
                    RealmsLongConfirmationScreen.this.listener.confirmResult(true, RealmsLongConfirmationScreen.this.id);
                }
            });
            this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, RealmsConstants.row(8), 100, 20, this.noButton) {
                @Override
                public void onPress() {
                    RealmsLongConfirmationScreen.this.listener.confirmResult(false, RealmsLongConfirmationScreen.this.id);
                }
            });
        }
        else {
            this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, RealmsConstants.row(8), 100, 20, this.okButton) {
                @Override
                public void onPress() {
                    RealmsLongConfirmationScreen.this.listener.confirmResult(true, RealmsLongConfirmationScreen.this.id);
                }
            });
        }
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            this.listener.confirmResult(false, this.id);
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.type.text, this.width() / 2, RealmsConstants.row(2), this.type.colorCode);
        this.drawCenteredString(this.line2, this.width() / 2, RealmsConstants.row(4), 16777215);
        this.drawCenteredString(this.line3, this.width() / 2, RealmsConstants.row(6), 16777215);
        super.render(integer1, integer2, float3);
    }
    
    public enum Type {
        Warning("Warning!", 16711680), 
        Info("Info!", 8226750);
        
        public final int colorCode;
        public final String text;
        
        private Type(final String string3, final int integer4) {
            this.text = string3;
            this.colorCode = integer4;
        }
    }
}
