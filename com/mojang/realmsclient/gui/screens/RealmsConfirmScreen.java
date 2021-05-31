package com.mojang.realmsclient.gui.screens;

import java.util.Iterator;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.RealmsScreen;

public class RealmsConfirmScreen extends RealmsScreen {
    protected RealmsScreen parent;
    protected String title1;
    private final String title2;
    protected String yesButton;
    protected String noButton;
    protected int id;
    private int delayTicker;
    
    public RealmsConfirmScreen(final RealmsScreen realmsScreen, final String string2, final String string3, final int integer) {
        this.parent = realmsScreen;
        this.title1 = string2;
        this.title2 = string3;
        this.id = integer;
        this.yesButton = RealmsScreen.getLocalizedString("gui.yes");
        this.noButton = RealmsScreen.getLocalizedString("gui.no");
    }
    
    @Override
    public void init() {
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 105, RealmsConstants.row(9), 100, 20, this.yesButton) {
            @Override
            public void onPress() {
                RealmsConfirmScreen.this.parent.confirmResult(true, RealmsConfirmScreen.this.id);
            }
        });
        this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, RealmsConstants.row(9), 100, 20, this.noButton) {
            @Override
            public void onPress() {
                RealmsConfirmScreen.this.parent.confirmResult(false, RealmsConfirmScreen.this.id);
            }
        });
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.title1, this.width() / 2, RealmsConstants.row(3), 16777215);
        this.drawCenteredString(this.title2, this.width() / 2, RealmsConstants.row(5), 16777215);
        super.render(integer1, integer2, float3);
    }
    
    @Override
    public void tick() {
        super.tick();
        final int delayTicker = this.delayTicker - 1;
        this.delayTicker = delayTicker;
        if (delayTicker == 0) {
            for (final AbstractRealmsButton<?> abstractRealmsButton3 : this.buttons()) {
                abstractRealmsButton3.active(true);
            }
        }
    }
}
