package com.mojang.realmsclient.gui.screens;

import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.Realms;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

public class RealmsResetNormalWorldScreen extends RealmsScreen {
    private final RealmsResetWorldScreen lastScreen;
    private RealmsLabel titleLabel;
    private RealmsEditBox seedEdit;
    private Boolean generateStructures;
    private Integer levelTypeIndex;
    String[] levelTypes;
    private final int BUTTON_CANCEL_ID = 0;
    private final int BUTTON_RESET_ID = 1;
    private final int SEED_EDIT_BOX = 4;
    private RealmsButton resetButton;
    private RealmsButton levelTypeButton;
    private RealmsButton generateStructuresButton;
    private String buttonTitle;
    
    public RealmsResetNormalWorldScreen(final RealmsResetWorldScreen cwu) {
        this.generateStructures = true;
        this.levelTypeIndex = 0;
        this.buttonTitle = RealmsScreen.getLocalizedString("mco.backup.button.reset");
        this.lastScreen = cwu;
    }
    
    public RealmsResetNormalWorldScreen(final RealmsResetWorldScreen cwu, final String string) {
        this(cwu);
        this.buttonTitle = string;
    }
    
    @Override
    public void tick() {
        this.seedEdit.tick();
        super.tick();
    }
    
    @Override
    public void init() {
        this.levelTypes = new String[] { RealmsScreen.getLocalizedString("generator.default"), RealmsScreen.getLocalizedString("generator.flat"), RealmsScreen.getLocalizedString("generator.largeBiomes"), RealmsScreen.getLocalizedString("generator.amplified") };
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 8, RealmsConstants.row(12), 97, 20, RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsResetNormalWorldScreen.this.lastScreen);
            }
        });
        this.buttonsAdd(this.resetButton = new RealmsButton(1, this.width() / 2 - 102, RealmsConstants.row(12), 97, 20, this.buttonTitle) {
            @Override
            public void onPress() {
                RealmsResetNormalWorldScreen.this.onReset();
            }
        });
        (this.seedEdit = this.newEditBox(4, this.width() / 2 - 100, RealmsConstants.row(2), 200, 20, RealmsScreen.getLocalizedString("mco.reset.world.seed"))).setMaxLength(32);
        this.seedEdit.setValue("");
        this.addWidget(this.seedEdit);
        this.focusOn(this.seedEdit);
        this.buttonsAdd(this.levelTypeButton = new RealmsButton(2, this.width() / 2 - 102, RealmsConstants.row(4), 205, 20, this.levelTypeTitle()) {
            @Override
            public void onPress() {
                RealmsResetNormalWorldScreen.this.levelTypeIndex = (RealmsResetNormalWorldScreen.this.levelTypeIndex + 1) % RealmsResetNormalWorldScreen.this.levelTypes.length;
                this.setMessage(RealmsResetNormalWorldScreen.this.levelTypeTitle());
            }
        });
        this.buttonsAdd(this.generateStructuresButton = new RealmsButton(3, this.width() / 2 - 102, RealmsConstants.row(6) - 2, 205, 20, this.generateStructuresTitle()) {
            @Override
            public void onPress() {
                RealmsResetNormalWorldScreen.this.generateStructures = !RealmsResetNormalWorldScreen.this.generateStructures;
                this.setMessage(RealmsResetNormalWorldScreen.this.generateStructuresTitle());
            }
        });
        this.addWidget(this.titleLabel = new RealmsLabel(RealmsScreen.getLocalizedString("mco.reset.world.generate"), this.width() / 2, 17, 16777215));
        this.narrateLabels();
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
    
    private void onReset() {
        this.lastScreen.resetWorld(new RealmsResetWorldScreen.ResetWorldInfo(this.seedEdit.getValue(), this.levelTypeIndex, this.generateStructures));
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.titleLabel.render(this);
        this.drawString(RealmsScreen.getLocalizedString("mco.reset.world.seed"), this.width() / 2 - 100, RealmsConstants.row(1), 10526880);
        this.seedEdit.render(integer1, integer2, float3);
        super.render(integer1, integer2, float3);
    }
    
    private String levelTypeTitle() {
        final String string2 = RealmsScreen.getLocalizedString("selectWorld.mapType");
        return string2 + " " + this.levelTypes[this.levelTypeIndex];
    }
    
    private String generateStructuresTitle() {
        return RealmsScreen.getLocalizedString("selectWorld.mapFeatures") + " " + RealmsScreen.getLocalizedString(((boolean)this.generateStructures) ? "mco.configure.world.on" : "mco.configure.world.off");
    }
}
