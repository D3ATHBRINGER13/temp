package com.mojang.realmsclient.gui.screens;

import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.Realms;
import net.minecraft.realms.AbstractRealmsButton;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.realms.RealmsScreen;

public class RealmsSettingsScreen extends RealmsScreen {
    private final RealmsConfigureWorldScreen configureWorldScreen;
    private final RealmsServer serverData;
    private final int COMPONENT_WIDTH = 212;
    private RealmsButton doneButton;
    private RealmsEditBox descEdit;
    private RealmsEditBox nameEdit;
    private RealmsLabel titleLabel;
    
    public RealmsSettingsScreen(final RealmsConfigureWorldScreen cwg, final RealmsServer realmsServer) {
        this.configureWorldScreen = cwg;
        this.serverData = realmsServer;
    }
    
    @Override
    public void tick() {
        this.nameEdit.tick();
        this.descEdit.tick();
        this.doneButton.active(this.nameEdit.getValue() != null && !this.nameEdit.getValue().trim().isEmpty());
    }
    
    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        final int integer2 = this.width() / 2 - 106;
        this.buttonsAdd(this.doneButton = new RealmsButton(1, integer2 - 2, RealmsConstants.row(12), 106, 20, RealmsScreen.getLocalizedString("mco.configure.world.buttons.done")) {
            @Override
            public void onPress() {
                RealmsSettingsScreen.this.save();
            }
        });
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 2, RealmsConstants.row(12), 106, 20, RealmsScreen.getLocalizedString("gui.cancel")) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsSettingsScreen.this.configureWorldScreen);
            }
        });
        this.buttonsAdd(new RealmsButton(5, this.width() / 2 - 53, RealmsConstants.row(0), 106, 20, RealmsScreen.getLocalizedString(this.serverData.state.equals(RealmsServer.State.OPEN) ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open")) {
            @Override
            public void onPress() {
                if (RealmsSettingsScreen.this.serverData.state.equals(RealmsServer.State.OPEN)) {
                    final String string2 = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line1");
                    final String string3 = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line2");
                    Realms.setScreen(new RealmsLongConfirmationScreen(RealmsSettingsScreen.this, RealmsLongConfirmationScreen.Type.Info, string2, string3, true, 5));
                }
                else {
                    RealmsSettingsScreen.this.configureWorldScreen.openTheWorld(false, RealmsSettingsScreen.this);
                }
            }
        });
        (this.nameEdit = this.newEditBox(2, integer2, RealmsConstants.row(4), 212, 20, RealmsScreen.getLocalizedString("mco.configure.world.name"))).setMaxLength(32);
        if (this.serverData.getName() != null) {
            this.nameEdit.setValue(this.serverData.getName());
        }
        this.addWidget(this.nameEdit);
        this.focusOn(this.nameEdit);
        (this.descEdit = this.newEditBox(3, integer2, RealmsConstants.row(8), 212, 20, RealmsScreen.getLocalizedString("mco.configure.world.description"))).setMaxLength(32);
        if (this.serverData.getDescription() != null) {
            this.descEdit.setValue(this.serverData.getDescription());
        }
        this.addWidget(this.descEdit);
        this.addWidget(this.titleLabel = new RealmsLabel(RealmsScreen.getLocalizedString("mco.configure.world.settings.title"), this.width() / 2, 17, 16777215));
        this.narrateLabels();
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }
    
    @Override
    public void confirmResult(final boolean boolean1, final int integer) {
        switch (integer) {
            case 5: {
                if (boolean1) {
                    this.configureWorldScreen.closeTheWorld(this);
                    break;
                }
                Realms.setScreen(this);
                break;
            }
        }
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        switch (integer1) {
            case 256: {
                Realms.setScreen(this.configureWorldScreen);
                return true;
            }
            default: {
                return super.keyPressed(integer1, integer2, integer3);
            }
        }
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.titleLabel.render(this);
        this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.name"), this.width() / 2 - 106, RealmsConstants.row(3), 10526880);
        this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.description"), this.width() / 2 - 106, RealmsConstants.row(7), 10526880);
        this.nameEdit.render(integer1, integer2, float3);
        this.descEdit.render(integer1, integer2, float3);
        super.render(integer1, integer2, float3);
    }
    
    public void save() {
        this.configureWorldScreen.saveSettings(this.nameEdit.getValue(), this.descEdit.getValue());
    }
}
