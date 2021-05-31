package net.minecraft.client.gui.screens.worldselection;

import net.minecraft.world.level.storage.LevelData;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import net.minecraft.nbt.NbtOps;
import com.google.gson.JsonElement;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.GameType;
import org.apache.commons.lang3.StringUtils;
import java.util.Random;
import net.minecraft.FileUtil;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.world.level.LevelType;
import java.util.function.Consumer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;

public class CreateWorldScreen extends Screen {
    private final Screen lastScreen;
    private EditBox nameEdit;
    private EditBox seedEdit;
    private String resultFolder;
    private String gameModeName;
    private String oldGameModeName;
    private boolean features;
    private boolean commands;
    private boolean commandsChanged;
    private boolean bonusItems;
    private boolean hardCore;
    private boolean done;
    private boolean displayOptions;
    private Button createButton;
    private Button modeButton;
    private Button moreOptionsButton;
    private Button featuresButton;
    private Button bonusItemsButton;
    private Button typeButton;
    private Button commandsButton;
    private Button customizeTypeButton;
    private String gameModeHelp1;
    private String gameModeHelp2;
    private String initSeed;
    private String initName;
    private int levelTypeIndex;
    public CompoundTag levelTypeOptions;
    
    public CreateWorldScreen(final Screen dcl) {
        super(new TranslatableComponent("selectWorld.create", new Object[0]));
        this.gameModeName = "survival";
        this.features = true;
        this.levelTypeOptions = new CompoundTag();
        this.lastScreen = dcl;
        this.initSeed = "";
        this.initName = I18n.get("selectWorld.newWorld");
    }
    
    @Override
    public void tick() {
        this.nameEdit.tick();
        this.seedEdit.tick();
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        (this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, I18n.get("selectWorld.enterName"))).setValue(this.initName);
        this.nameEdit.setResponder((Consumer<String>)(string -> {
            this.initName = string;
            this.createButton.active = !this.nameEdit.getValue().isEmpty();
            this.updateResultFolder();
        }));
        this.children.add(this.nameEdit);
        this.modeButton = this.<Button>addButton(new Button(this.width / 2 - 75, 115, 150, 20, I18n.get("selectWorld.gameMode"), czi -> {
            if ("survival".equals(this.gameModeName)) {
                if (!this.commandsChanged) {
                    this.commands = false;
                }
                this.hardCore = false;
                this.gameModeName = "hardcore";
                this.hardCore = true;
                this.commandsButton.active = false;
                this.bonusItemsButton.active = false;
                this.updateSelectionStrings();
            }
            else if ("hardcore".equals(this.gameModeName)) {
                if (!this.commandsChanged) {
                    this.commands = true;
                }
                this.hardCore = false;
                this.gameModeName = "creative";
                this.updateSelectionStrings();
                this.hardCore = false;
                this.commandsButton.active = true;
                this.bonusItemsButton.active = true;
            }
            else {
                if (!this.commandsChanged) {
                    this.commands = false;
                }
                this.gameModeName = "survival";
                this.updateSelectionStrings();
                this.commandsButton.active = true;
                this.bonusItemsButton.active = true;
                this.hardCore = false;
            }
            this.updateSelectionStrings();
            return;
        }));
        (this.seedEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, I18n.get("selectWorld.enterSeed"))).setValue(this.initSeed);
        this.seedEdit.setResponder((Consumer<String>)(string -> this.initSeed = this.seedEdit.getValue()));
        this.children.add(this.seedEdit);
        this.featuresButton = this.<Button>addButton(new Button(this.width / 2 - 155, 100, 150, 20, I18n.get("selectWorld.mapFeatures"), czi -> {
            this.features = !this.features;
            this.updateSelectionStrings();
            return;
        }));
        this.featuresButton.visible = false;
        this.typeButton = this.<Button>addButton(new Button(this.width / 2 + 5, 100, 150, 20, I18n.get("selectWorld.mapType"), czi -> {
            ++this.levelTypeIndex;
            if (this.levelTypeIndex >= LevelType.LEVEL_TYPES.length) {
                this.levelTypeIndex = 0;
            }
            while (!this.isValidLevelType()) {
                ++this.levelTypeIndex;
                if (this.levelTypeIndex >= LevelType.LEVEL_TYPES.length) {
                    this.levelTypeIndex = 0;
                }
            }
            this.levelTypeOptions = new CompoundTag();
            this.updateSelectionStrings();
            this.setDisplayOptions(this.displayOptions);
            return;
        }));
        this.typeButton.visible = false;
        this.customizeTypeButton = this.<Button>addButton(new Button(this.width / 2 + 5, 120, 150, 20, I18n.get("selectWorld.customizeType"), czi -> {
            if (LevelType.LEVEL_TYPES[this.levelTypeIndex] == LevelType.FLAT) {
                this.minecraft.setScreen(new CreateFlatWorldScreen(this, this.levelTypeOptions));
            }
            if (LevelType.LEVEL_TYPES[this.levelTypeIndex] == LevelType.BUFFET) {
                this.minecraft.setScreen(new CreateBuffetWorldScreen(this, this.levelTypeOptions));
            }
            return;
        }));
        this.customizeTypeButton.visible = false;
        this.commandsButton = this.<Button>addButton(new Button(this.width / 2 - 155, 151, 150, 20, I18n.get("selectWorld.allowCommands"), czi -> {
            this.commandsChanged = true;
            this.commands = !this.commands;
            this.updateSelectionStrings();
            return;
        }));
        this.commandsButton.visible = false;
        this.bonusItemsButton = this.<Button>addButton(new Button(this.width / 2 + 5, 151, 150, 20, I18n.get("selectWorld.bonusItems"), czi -> {
            this.bonusItems = !this.bonusItems;
            this.updateSelectionStrings();
            return;
        }));
        this.bonusItemsButton.visible = false;
        this.moreOptionsButton = this.<Button>addButton(new Button(this.width / 2 - 75, 187, 150, 20, I18n.get("selectWorld.moreWorldOptions"), czi -> this.toggleDisplayOptions()));
        this.createButton = this.<Button>addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("selectWorld.create"), czi -> this.onCreate()));
        this.<Button>addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), czi -> this.minecraft.setScreen(this.lastScreen)));
        this.setDisplayOptions(this.displayOptions);
        this.setInitialFocus(this.nameEdit);
        this.updateResultFolder();
        this.updateSelectionStrings();
    }
    
    private void updateResultFolder() {
        this.resultFolder = this.nameEdit.getValue().trim();
        if (this.resultFolder.length() == 0) {
            this.resultFolder = "World";
        }
        try {
            this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
        }
        catch (Exception exception4) {
            this.resultFolder = "World";
            try {
                this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
            }
            catch (Exception exception3) {
                throw new RuntimeException("Could not create save folder", (Throwable)exception3);
            }
        }
    }
    
    private void updateSelectionStrings() {
        this.modeButton.setMessage(I18n.get("selectWorld.gameMode") + ": " + I18n.get("selectWorld.gameMode." + this.gameModeName));
        this.gameModeHelp1 = I18n.get("selectWorld.gameMode." + this.gameModeName + ".line1");
        this.gameModeHelp2 = I18n.get("selectWorld.gameMode." + this.gameModeName + ".line2");
        this.featuresButton.setMessage(I18n.get("selectWorld.mapFeatures") + ' ' + I18n.get(this.features ? "options.on" : "options.off"));
        this.bonusItemsButton.setMessage(I18n.get("selectWorld.bonusItems") + ' ' + I18n.get((this.bonusItems && !this.hardCore) ? "options.on" : "options.off"));
        this.typeButton.setMessage(I18n.get("selectWorld.mapType") + ' ' + I18n.get(LevelType.LEVEL_TYPES[this.levelTypeIndex].getDescriptionId()));
        this.commandsButton.setMessage(I18n.get("selectWorld.allowCommands") + ' ' + I18n.get((this.commands && !this.hardCore) ? "options.on" : "options.off"));
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
    
    private void onCreate() {
        this.minecraft.setScreen(null);
        if (this.done) {
            return;
        }
        this.done = true;
        long long2 = new Random().nextLong();
        final String string4 = this.seedEdit.getValue();
        if (!StringUtils.isEmpty((CharSequence)string4)) {
            try {
                final long long3 = Long.parseLong(string4);
                if (long3 != 0L) {
                    long2 = long3;
                }
            }
            catch (NumberFormatException numberFormatException5) {
                long2 = string4.hashCode();
            }
        }
        final LevelSettings bhv5 = new LevelSettings(long2, GameType.byName(this.gameModeName), this.features, this.hardCore, LevelType.LEVEL_TYPES[this.levelTypeIndex]);
        bhv5.setLevelTypeOptions((JsonElement)Dynamic.convert((DynamicOps)NbtOps.INSTANCE, (DynamicOps)JsonOps.INSTANCE, this.levelTypeOptions));
        if (this.bonusItems && !this.hardCore) {
            bhv5.enableStartingBonusItems();
        }
        if (this.commands && !this.hardCore) {
            bhv5.enableSinglePlayerCommands();
        }
        this.minecraft.selectLevel(this.resultFolder, this.nameEdit.getValue().trim(), bhv5);
    }
    
    private boolean isValidLevelType() {
        final LevelType bhy2 = LevelType.LEVEL_TYPES[this.levelTypeIndex];
        return bhy2 != null && bhy2.isSelectable() && (bhy2 != LevelType.DEBUG_ALL_BLOCK_STATES || Screen.hasShiftDown());
    }
    
    private void toggleDisplayOptions() {
        this.setDisplayOptions(!this.displayOptions);
    }
    
    private void setDisplayOptions(final boolean boolean1) {
        this.displayOptions = boolean1;
        if (LevelType.LEVEL_TYPES[this.levelTypeIndex] == LevelType.DEBUG_ALL_BLOCK_STATES) {
            this.modeButton.visible = !this.displayOptions;
            this.modeButton.active = false;
            if (this.oldGameModeName == null) {
                this.oldGameModeName = this.gameModeName;
            }
            this.gameModeName = "spectator";
            this.featuresButton.visible = false;
            this.bonusItemsButton.visible = false;
            this.typeButton.visible = this.displayOptions;
            this.commandsButton.visible = false;
            this.customizeTypeButton.visible = false;
        }
        else {
            this.modeButton.visible = !this.displayOptions;
            this.modeButton.active = true;
            if (this.oldGameModeName != null) {
                this.gameModeName = this.oldGameModeName;
                this.oldGameModeName = null;
            }
            this.featuresButton.visible = (this.displayOptions && LevelType.LEVEL_TYPES[this.levelTypeIndex] != LevelType.CUSTOMIZED);
            this.bonusItemsButton.visible = this.displayOptions;
            this.typeButton.visible = this.displayOptions;
            this.commandsButton.visible = this.displayOptions;
            this.customizeTypeButton.visible = (this.displayOptions && LevelType.LEVEL_TYPES[this.levelTypeIndex].hasCustomOptions());
        }
        this.updateSelectionStrings();
        this.seedEdit.setVisible(this.displayOptions);
        this.nameEdit.setVisible(!this.displayOptions);
        if (this.displayOptions) {
            this.moreOptionsButton.setMessage(I18n.get("gui.done"));
        }
        else {
            this.moreOptionsButton.setMessage(I18n.get("selectWorld.moreWorldOptions"));
        }
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (super.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        if (integer1 == 257 || integer1 == 335) {
            this.onCreate();
            return true;
        }
        return false;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, -1);
        if (this.displayOptions) {
            this.drawString(this.font, I18n.get("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
            this.drawString(this.font, I18n.get("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
            if (this.featuresButton.visible) {
                this.drawString(this.font, I18n.get("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
            }
            if (this.commandsButton.visible) {
                this.drawString(this.font, I18n.get("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
            }
            this.seedEdit.render(integer1, integer2, float3);
            if (LevelType.LEVEL_TYPES[this.levelTypeIndex].hasHelpText()) {
                this.font.drawWordWrap(I18n.get(LevelType.LEVEL_TYPES[this.levelTypeIndex].getHelpTextId()), this.typeButton.x + 2, this.typeButton.y + 22, this.typeButton.getWidth(), 10526880);
            }
        }
        else {
            this.drawString(this.font, I18n.get("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
            this.drawString(this.font, I18n.get("selectWorld.resultFolder") + " " + this.resultFolder, this.width / 2 - 100, 85, -6250336);
            this.nameEdit.render(integer1, integer2, float3);
            this.drawCenteredString(this.font, this.gameModeHelp1, this.width / 2, 137, -6250336);
            this.drawCenteredString(this.font, this.gameModeHelp2, this.width / 2, 149, -6250336);
        }
        super.render(integer1, integer2, float3);
    }
    
    public void copyFromWorld(final LevelData com) {
        this.initName = com.getLevelName();
        this.initSeed = new StringBuilder().append(com.getSeed()).append("").toString();
        final LevelType bhy3 = (com.getGeneratorType() == LevelType.CUSTOMIZED) ? LevelType.NORMAL : com.getGeneratorType();
        this.levelTypeIndex = bhy3.getId();
        this.levelTypeOptions = com.getGeneratorOptions();
        this.features = com.isGenerateMapFeatures();
        this.commands = com.getAllowCommands();
        if (com.isHardcore()) {
            this.gameModeName = "hardcore";
        }
        else if (com.getGameType().isSurvival()) {
            this.gameModeName = "survival";
        }
        else if (com.getGameType().isCreative()) {
            this.gameModeName = "creative";
        }
    }
}
