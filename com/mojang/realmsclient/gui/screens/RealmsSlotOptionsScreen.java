package com.mojang.realmsclient.gui.screens;

import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsGuiEventListener;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsSliderButton;
import net.minecraft.realms.RealmsButton;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsScreen;

public class RealmsSlotOptionsScreen extends RealmsScreen {
    private RealmsEditBox nameEdit;
    protected final RealmsConfigureWorldScreen parent;
    private int column1_x;
    private int column_width;
    private int column2_x;
    private final RealmsWorldOptions options;
    private final RealmsServer.WorldType worldType;
    private final int activeSlot;
    private int difficultyIndex;
    private int gameModeIndex;
    private Boolean pvp;
    private Boolean spawnNPCs;
    private Boolean spawnAnimals;
    private Boolean spawnMonsters;
    private Integer spawnProtection;
    private Boolean commandBlocks;
    private Boolean forceGameMode;
    private RealmsButton pvpButton;
    private RealmsButton spawnAnimalsButton;
    private RealmsButton spawnMonstersButton;
    private RealmsButton spawnNPCsButton;
    private RealmsSliderButton spawnProtectionButton;
    private RealmsButton commandBlocksButton;
    private RealmsButton forceGameModeButton;
    String[] difficulties;
    String[] gameModes;
    String[][] gameModeHints;
    private RealmsLabel titleLabel;
    private RealmsLabel warningLabel;
    
    public RealmsSlotOptionsScreen(final RealmsConfigureWorldScreen cwg, final RealmsWorldOptions realmsWorldOptions, final RealmsServer.WorldType c, final int integer) {
        this.warningLabel = null;
        this.parent = cwg;
        this.options = realmsWorldOptions;
        this.worldType = c;
        this.activeSlot = integer;
    }
    
    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }
    
    @Override
    public void tick() {
        this.nameEdit.tick();
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        switch (integer1) {
            case 256: {
                Realms.setScreen(this.parent);
                return true;
            }
            default: {
                return super.keyPressed(integer1, integer2, integer3);
            }
        }
    }
    
    @Override
    public void init() {
        this.column_width = 170;
        this.column1_x = this.width() / 2 - this.column_width * 2 / 2;
        this.column2_x = this.width() / 2 + 10;
        this.createDifficultyAndGameMode();
        this.difficultyIndex = this.options.difficulty;
        this.gameModeIndex = this.options.gameMode;
        if (this.worldType.equals(RealmsServer.WorldType.NORMAL)) {
            this.pvp = this.options.pvp;
            this.spawnProtection = this.options.spawnProtection;
            this.forceGameMode = this.options.forceGameMode;
            this.spawnAnimals = this.options.spawnAnimals;
            this.spawnMonsters = this.options.spawnMonsters;
            this.spawnNPCs = this.options.spawnNPCs;
            this.commandBlocks = this.options.commandBlocks;
        }
        else {
            String string2;
            if (this.worldType.equals(RealmsServer.WorldType.ADVENTUREMAP)) {
                string2 = RealmsScreen.getLocalizedString("mco.configure.world.edit.subscreen.adventuremap");
            }
            else if (this.worldType.equals(RealmsServer.WorldType.INSPIRATION)) {
                string2 = RealmsScreen.getLocalizedString("mco.configure.world.edit.subscreen.inspiration");
            }
            else {
                string2 = RealmsScreen.getLocalizedString("mco.configure.world.edit.subscreen.experience");
            }
            this.warningLabel = new RealmsLabel(string2, this.width() / 2, 26, 16711680);
            this.pvp = true;
            this.spawnProtection = 0;
            this.forceGameMode = false;
            this.spawnAnimals = true;
            this.spawnMonsters = true;
            this.spawnNPCs = true;
            this.commandBlocks = true;
        }
        (this.nameEdit = this.newEditBox(11, this.column1_x + 2, RealmsConstants.row(1), this.column_width - 4, 20, RealmsScreen.getLocalizedString("mco.configure.world.edit.slot.name"))).setMaxLength(10);
        this.nameEdit.setValue(this.options.getSlotName(this.activeSlot));
        this.focusOn(this.nameEdit);
        this.buttonsAdd(this.pvpButton = new RealmsButton(4, this.column2_x, RealmsConstants.row(1), this.column_width, 20, this.pvpTitle()) {
            @Override
            public void onPress() {
                RealmsSlotOptionsScreen.this.pvp = !RealmsSlotOptionsScreen.this.pvp;
                this.setMessage(RealmsSlotOptionsScreen.this.pvpTitle());
            }
        });
        this.buttonsAdd(new RealmsButton(3, this.column1_x, RealmsConstants.row(3), this.column_width, 20, this.gameModeTitle()) {
            @Override
            public void onPress() {
                RealmsSlotOptionsScreen.this.gameModeIndex = (RealmsSlotOptionsScreen.this.gameModeIndex + 1) % RealmsSlotOptionsScreen.this.gameModes.length;
                this.setMessage(RealmsSlotOptionsScreen.this.gameModeTitle());
            }
        });
        this.buttonsAdd(this.spawnAnimalsButton = new RealmsButton(5, this.column2_x, RealmsConstants.row(3), this.column_width, 20, this.spawnAnimalsTitle()) {
            @Override
            public void onPress() {
                RealmsSlotOptionsScreen.this.spawnAnimals = !RealmsSlotOptionsScreen.this.spawnAnimals;
                this.setMessage(RealmsSlotOptionsScreen.this.spawnAnimalsTitle());
            }
        });
        this.buttonsAdd(new RealmsButton(2, this.column1_x, RealmsConstants.row(5), this.column_width, 20, this.difficultyTitle()) {
            @Override
            public void onPress() {
                RealmsSlotOptionsScreen.this.difficultyIndex = (RealmsSlotOptionsScreen.this.difficultyIndex + 1) % RealmsSlotOptionsScreen.this.difficulties.length;
                this.setMessage(RealmsSlotOptionsScreen.this.difficultyTitle());
                if (RealmsSlotOptionsScreen.this.worldType.equals(RealmsServer.WorldType.NORMAL)) {
                    RealmsSlotOptionsScreen.this.spawnMonstersButton.active(RealmsSlotOptionsScreen.this.difficultyIndex != 0);
                    RealmsSlotOptionsScreen.this.spawnMonstersButton.setMessage(RealmsSlotOptionsScreen.this.spawnMonstersTitle());
                }
            }
        });
        this.buttonsAdd(this.spawnMonstersButton = new RealmsButton(6, this.column2_x, RealmsConstants.row(5), this.column_width, 20, this.spawnMonstersTitle()) {
            @Override
            public void onPress() {
                RealmsSlotOptionsScreen.this.spawnMonsters = !RealmsSlotOptionsScreen.this.spawnMonsters;
                this.setMessage(RealmsSlotOptionsScreen.this.spawnMonstersTitle());
            }
        });
        this.buttonsAdd(this.spawnProtectionButton = new SettingsSlider(8, this.column1_x, RealmsConstants.row(7), this.column_width, this.spawnProtection, 0.0f, 16.0f));
        this.buttonsAdd(this.spawnNPCsButton = new RealmsButton(7, this.column2_x, RealmsConstants.row(7), this.column_width, 20, this.spawnNPCsTitle()) {
            @Override
            public void onPress() {
                RealmsSlotOptionsScreen.this.spawnNPCs = !RealmsSlotOptionsScreen.this.spawnNPCs;
                this.setMessage(RealmsSlotOptionsScreen.this.spawnNPCsTitle());
            }
        });
        this.buttonsAdd(this.forceGameModeButton = new RealmsButton(10, this.column1_x, RealmsConstants.row(9), this.column_width, 20, this.forceGameModeTitle()) {
            @Override
            public void onPress() {
                RealmsSlotOptionsScreen.this.forceGameMode = !RealmsSlotOptionsScreen.this.forceGameMode;
                this.setMessage(RealmsSlotOptionsScreen.this.forceGameModeTitle());
            }
        });
        this.buttonsAdd(this.commandBlocksButton = new RealmsButton(9, this.column2_x, RealmsConstants.row(9), this.column_width, 20, this.commandBlocksTitle()) {
            @Override
            public void onPress() {
                RealmsSlotOptionsScreen.this.commandBlocks = !RealmsSlotOptionsScreen.this.commandBlocks;
                this.setMessage(RealmsSlotOptionsScreen.this.commandBlocksTitle());
            }
        });
        if (!this.worldType.equals(RealmsServer.WorldType.NORMAL)) {
            this.pvpButton.active(false);
            this.spawnAnimalsButton.active(false);
            this.spawnNPCsButton.active(false);
            this.spawnMonstersButton.active(false);
            this.spawnProtectionButton.active(false);
            this.commandBlocksButton.active(false);
            this.spawnProtectionButton.active(false);
            this.forceGameModeButton.active(false);
        }
        if (this.difficultyIndex == 0) {
            this.spawnMonstersButton.active(false);
        }
        this.buttonsAdd(new RealmsButton(1, this.column1_x, RealmsConstants.row(13), this.column_width, 20, RealmsScreen.getLocalizedString("mco.configure.world.buttons.done")) {
            @Override
            public void onPress() {
                RealmsSlotOptionsScreen.this.saveSettings();
            }
        });
        this.buttonsAdd(new RealmsButton(0, this.column2_x, RealmsConstants.row(13), this.column_width, 20, RealmsScreen.getLocalizedString("gui.cancel")) {
            @Override
            public void onPress() {
                Realms.setScreen(RealmsSlotOptionsScreen.this.parent);
            }
        });
        this.addWidget(this.nameEdit);
        this.addWidget(this.titleLabel = new RealmsLabel(RealmsScreen.getLocalizedString("mco.configure.world.buttons.options"), this.width() / 2, 17, 16777215));
        if (this.warningLabel != null) {
            this.addWidget(this.warningLabel);
        }
        this.narrateLabels();
    }
    
    private void createDifficultyAndGameMode() {
        this.difficulties = new String[] { RealmsScreen.getLocalizedString("options.difficulty.peaceful"), RealmsScreen.getLocalizedString("options.difficulty.easy"), RealmsScreen.getLocalizedString("options.difficulty.normal"), RealmsScreen.getLocalizedString("options.difficulty.hard") };
        this.gameModes = new String[] { RealmsScreen.getLocalizedString("selectWorld.gameMode.survival"), RealmsScreen.getLocalizedString("selectWorld.gameMode.creative"), RealmsScreen.getLocalizedString("selectWorld.gameMode.adventure") };
        this.gameModeHints = new String[][] { { RealmsScreen.getLocalizedString("selectWorld.gameMode.survival.line1"), RealmsScreen.getLocalizedString("selectWorld.gameMode.survival.line2") }, { RealmsScreen.getLocalizedString("selectWorld.gameMode.creative.line1"), RealmsScreen.getLocalizedString("selectWorld.gameMode.creative.line2") }, { RealmsScreen.getLocalizedString("selectWorld.gameMode.adventure.line1"), RealmsScreen.getLocalizedString("selectWorld.gameMode.adventure.line2") } };
    }
    
    private String difficultyTitle() {
        final String string2 = RealmsScreen.getLocalizedString("options.difficulty");
        return string2 + ": " + this.difficulties[this.difficultyIndex];
    }
    
    private String gameModeTitle() {
        final String string2 = RealmsScreen.getLocalizedString("selectWorld.gameMode");
        return string2 + ": " + this.gameModes[this.gameModeIndex];
    }
    
    private String pvpTitle() {
        return RealmsScreen.getLocalizedString("mco.configure.world.pvp") + ": " + RealmsScreen.getLocalizedString(((boolean)this.pvp) ? "mco.configure.world.on" : "mco.configure.world.off");
    }
    
    private String spawnAnimalsTitle() {
        return RealmsScreen.getLocalizedString("mco.configure.world.spawnAnimals") + ": " + RealmsScreen.getLocalizedString(((boolean)this.spawnAnimals) ? "mco.configure.world.on" : "mco.configure.world.off");
    }
    
    private String spawnMonstersTitle() {
        if (this.difficultyIndex == 0) {
            return RealmsScreen.getLocalizedString("mco.configure.world.spawnMonsters") + ": " + RealmsScreen.getLocalizedString("mco.configure.world.off");
        }
        return RealmsScreen.getLocalizedString("mco.configure.world.spawnMonsters") + ": " + RealmsScreen.getLocalizedString(((boolean)this.spawnMonsters) ? "mco.configure.world.on" : "mco.configure.world.off");
    }
    
    private String spawnNPCsTitle() {
        return RealmsScreen.getLocalizedString("mco.configure.world.spawnNPCs") + ": " + RealmsScreen.getLocalizedString(((boolean)this.spawnNPCs) ? "mco.configure.world.on" : "mco.configure.world.off");
    }
    
    private String commandBlocksTitle() {
        return RealmsScreen.getLocalizedString("mco.configure.world.commandBlocks") + ": " + RealmsScreen.getLocalizedString(((boolean)this.commandBlocks) ? "mco.configure.world.on" : "mco.configure.world.off");
    }
    
    private String forceGameModeTitle() {
        return RealmsScreen.getLocalizedString("mco.configure.world.forceGameMode") + ": " + RealmsScreen.getLocalizedString(((boolean)this.forceGameMode) ? "mco.configure.world.on" : "mco.configure.world.off");
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        final String string5 = RealmsScreen.getLocalizedString("mco.configure.world.edit.slot.name");
        this.drawString(string5, this.column1_x + this.column_width / 2 - this.fontWidth(string5) / 2, RealmsConstants.row(0) - 5, 16777215);
        this.titleLabel.render(this);
        if (this.warningLabel != null) {
            this.warningLabel.render(this);
        }
        this.nameEdit.render(integer1, integer2, float3);
        super.render(integer1, integer2, float3);
    }
    
    private String getSlotName() {
        if (this.nameEdit.getValue().equals(this.options.getDefaultSlotName(this.activeSlot))) {
            return "";
        }
        return this.nameEdit.getValue();
    }
    
    private void saveSettings() {
        if (this.worldType.equals(RealmsServer.WorldType.ADVENTUREMAP) || this.worldType.equals(RealmsServer.WorldType.EXPERIENCE) || this.worldType.equals(RealmsServer.WorldType.INSPIRATION)) {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, this.difficultyIndex, this.gameModeIndex, this.options.forceGameMode, this.getSlotName()));
        }
        else {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficultyIndex, this.gameModeIndex, this.forceGameMode, this.getSlotName()));
        }
    }
    
    class SettingsSlider extends RealmsSliderButton {
        public SettingsSlider(final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final float float7, final float float8) {
            super(integer2, integer3, integer4, integer5, integer6, float7, float8);
        }
        
        @Override
        public void applyValue() {
            if (!RealmsSlotOptionsScreen.this.spawnProtectionButton.active()) {
                return;
            }
            RealmsSlotOptionsScreen.this.spawnProtection = (int)this.toValue(this.getValue());
        }
        
        @Override
        public String getMessage() {
            return RealmsScreen.getLocalizedString("mco.configure.world.spawnProtection") + ": " + ((RealmsSlotOptionsScreen.this.spawnProtection == 0) ? RealmsScreen.getLocalizedString("mco.configure.world.off") : RealmsSlotOptionsScreen.this.spawnProtection);
        }
    }
}
