package net.minecraft.client.gui.screens;

import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.resourcepacks.ResourcePackSelectScreen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.Options;
import net.minecraft.client.Option;

public class OptionsScreen extends Screen {
    private static final Option[] OPTION_SCREEN_OPTIONS;
    private final Screen lastScreen;
    private final Options options;
    private Button difficultyButton;
    private LockIconButton lockButton;
    private Difficulty currentDifficulty;
    
    public OptionsScreen(final Screen dcl, final Options cyg) {
        super(new TranslatableComponent("options.title", new Object[0]));
        this.lastScreen = dcl;
        this.options = cyg;
    }
    
    @Override
    protected void init() {
        int integer2 = 0;
        for (final Option cyf6 : OptionsScreen.OPTION_SCREEN_OPTIONS) {
            final int integer3 = this.width / 2 - 155 + integer2 % 2 * 160;
            final int integer4 = this.height / 6 - 12 + 24 * (integer2 >> 1);
            this.<AbstractWidget>addButton(cyf6.createButton(this.minecraft.options, integer3, integer4, 150));
            ++integer2;
        }
        if (this.minecraft.level != null) {
            this.currentDifficulty = this.minecraft.level.getDifficulty();
            this.difficultyButton = this.<Button>addButton(new Button(this.width / 2 - 155 + integer2 % 2 * 160, this.height / 6 - 12 + 24 * (integer2 >> 1), 150, 20, this.getDifficultyText(this.currentDifficulty), czi -> {
                this.currentDifficulty = Difficulty.byId(this.currentDifficulty.getId() + 1);
                this.minecraft.getConnection().send(new ServerboundChangeDifficultyPacket(this.currentDifficulty));
                this.difficultyButton.setMessage(this.getDifficultyText(this.currentDifficulty));
                return;
            }));
            if (this.minecraft.hasSingleplayerServer() && !this.minecraft.level.getLevelData().isHardcore()) {
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
                final Minecraft minecraft;
                final TranslatableComponent jo2;
                final Object[] arr;
                final TranslatableComponent translatableComponent;
                final Object o;
                final Component jo3;
                final String string;
                final Screen screen;
                final BooleanConsumer booleanConsumer;
                (this.lockButton = this.<LockIconButton>addButton(new LockIconButton(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, czi -> {
                    minecraft = this.minecraft;
                    // new(net.minecraft.client.gui.screens.ConfirmScreen.class)
                    this::lockCallback;
                    jo2 = new TranslatableComponent("difficulty.lock.title", new Object[0]);
                    // new(net.minecraft.network.chat.TranslatableComponent.class)
                    arr = new Object[] { null };
                    new TranslatableComponent("options.difficulty." + this.minecraft.level.getLevelData().getDifficulty().getKey(), new Object[0]);
                    arr[o] = translatableComponent;
                    new TranslatableComponent(string, arr);
                    new ConfirmScreen(booleanConsumer, jo2, jo3);
                    minecraft.setScreen(screen);
                    return;
                }))).setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
                this.lockButton.active = !this.lockButton.isLocked();
                this.difficultyButton.active = !this.lockButton.isLocked();
            }
            else {
                this.difficultyButton.active = false;
            }
        }
        else {
            this.<OptionButton>addButton(new OptionButton(this.width / 2 - 155 + integer2 % 2 * 160, this.height / 6 - 12 + 24 * (integer2 >> 1), 150, 20, Option.REALMS_NOTIFICATIONS, Option.REALMS_NOTIFICATIONS.getMessage(this.options), czi -> {
                Option.REALMS_NOTIFICATIONS.toggle(this.options);
                this.options.save();
                czi.setMessage(Option.REALMS_NOTIFICATIONS.getMessage(this.options));
                return;
            }));
        }
        this.<Button>addButton(new Button(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.get("options.skinCustomisation"), czi -> this.minecraft.setScreen(new SkinCustomizationScreen(this))));
        this.<Button>addButton(new Button(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.get("options.sounds"), czi -> this.minecraft.setScreen(new SoundOptionsScreen(this, this.options))));
        this.<Button>addButton(new Button(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.get("options.video"), czi -> this.minecraft.setScreen(new VideoSettingsScreen(this, this.options))));
        this.<Button>addButton(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.get("options.controls"), czi -> this.minecraft.setScreen(new ControlsScreen(this, this.options))));
        this.<Button>addButton(new Button(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.get("options.language"), czi -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.options, this.minecraft.getLanguageManager()))));
        this.<Button>addButton(new Button(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.get("options.chat.title"), czi -> this.minecraft.setScreen(new ChatOptionsScreen(this, this.options))));
        this.<Button>addButton(new Button(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.get("options.resourcepack"), czi -> this.minecraft.setScreen(new ResourcePackSelectScreen(this))));
        this.<Button>addButton(new Button(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.get("options.accessibility.title"), czi -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.options))));
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.get("gui.done"), czi -> this.minecraft.setScreen(this.lastScreen)));
    }
    
    public String getDifficultyText(final Difficulty ahg) {
        return new TranslatableComponent("options.difficulty", new Object[0]).append(": ").append(ahg.getDisplayName()).getColoredString();
    }
    
    private void lockCallback(final boolean boolean1) {
        this.minecraft.setScreen(this);
        if (boolean1 && this.minecraft.level != null) {
            this.minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
            this.lockButton.setLocked(true);
            this.lockButton.active = false;
            this.difficultyButton.active = false;
        }
    }
    
    @Override
    public void removed() {
        this.options.save();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 15, 16777215);
        super.render(integer1, integer2, float3);
    }
    
    static {
        OPTION_SCREEN_OPTIONS = new Option[] { Option.FOV };
    }
}
