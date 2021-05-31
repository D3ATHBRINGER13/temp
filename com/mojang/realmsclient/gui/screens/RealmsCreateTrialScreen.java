package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.util.RealmsTasks;
import net.minecraft.realms.Realms;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.realms.RealmsScreen;

public class RealmsCreateTrialScreen extends RealmsScreen {
    private final RealmsMainScreen lastScreen;
    private RealmsEditBox nameBox;
    private RealmsEditBox descriptionBox;
    private boolean initialized;
    private RealmsButton createButton;
    
    public RealmsCreateTrialScreen(final RealmsMainScreen cvi) {
        this.lastScreen = cvi;
    }
    
    @Override
    public void tick() {
        if (this.nameBox != null) {
            this.nameBox.tick();
            this.createButton.active(this.valid());
        }
        if (this.descriptionBox != null) {
            this.descriptionBox.tick();
        }
    }
    
    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        if (!this.initialized) {
            this.initialized = true;
            this.focusOn(this.nameBox = this.newEditBox(3, this.width() / 2 - 100, 65, 200, 20, RealmsScreen.getLocalizedString("mco.configure.world.name")));
            this.descriptionBox = this.newEditBox(4, this.width() / 2 - 100, 115, 200, 20, RealmsScreen.getLocalizedString("mco.configure.world.description"));
        }
        this.buttonsAdd(this.createButton = new RealmsButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 17, 97, 20, RealmsScreen.getLocalizedString("mco.create.world")) {
            @Override
            public void onPress() {
                RealmsCreateTrialScreen.this.createWorld();
            }
        });
        this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, this.height() / 4 + 120 + 17, 95, 20, RealmsScreen.getLocalizedString("gui.cancel")) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsCreateTrialScreen.this.lastScreen);
            }
        });
        this.createButton.active(this.valid());
        this.addWidget(this.nameBox);
        this.addWidget(this.descriptionBox);
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }
    
    @Override
    public boolean charTyped(final char character, final int integer) {
        this.createButton.active(this.valid());
        return false;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        switch (integer1) {
            case 256: {
                Realms.setScreen(this.lastScreen);
                return true;
            }
            default: {
                this.createButton.active(this.valid());
                return false;
            }
        }
    }
    
    private void createWorld() {
        if (this.valid()) {
            final RealmsTasks.TrialCreationTask j2 = new RealmsTasks.TrialCreationTask(this.nameBox.getValue(), this.descriptionBox.getValue(), this.lastScreen);
            final RealmsLongRunningMcoTaskScreen cwo3 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, j2);
            cwo3.start();
            Realms.setScreen(cwo3);
        }
    }
    
    private boolean valid() {
        return this.nameBox != null && this.nameBox.getValue() != null && !this.nameBox.getValue().trim().isEmpty();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(RealmsScreen.getLocalizedString("mco.trial.title"), this.width() / 2, 11, 16777215);
        this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.name"), this.width() / 2 - 100, 52, 10526880);
        this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.description"), this.width() / 2 - 100, 102, 10526880);
        if (this.nameBox != null) {
            this.nameBox.render(integer1, integer2, float3);
        }
        if (this.descriptionBox != null) {
            this.descriptionBox.render(integer1, integer2, float3);
        }
        super.render(integer1, integer2, float3);
    }
}
