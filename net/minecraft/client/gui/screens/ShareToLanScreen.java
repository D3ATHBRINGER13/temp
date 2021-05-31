package net.minecraft.client.gui.screens;

import net.minecraft.world.level.GameType;
import net.minecraft.util.HttpUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.components.Button;

public class ShareToLanScreen extends Screen {
    private final Screen lastScreen;
    private Button commandsButton;
    private Button modeButton;
    private String gameModeName;
    private boolean commands;
    
    public ShareToLanScreen(final Screen dcl) {
        super(new TranslatableComponent("lanServer.title", new Object[0]));
        this.gameModeName = "survival";
        this.lastScreen = dcl;
    }
    
    @Override
    protected void init() {
        final int integer3;
        final TranslatableComponent translatableComponent;
        Component jo4;
        this.<Button>addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("lanServer.start"), czi -> {
            this.minecraft.setScreen(null);
            integer3 = HttpUtil.getAvailablePort();
            if (this.minecraft.getSingleplayerServer().publishServer(GameType.byName(this.gameModeName), this.commands, integer3)) {
                new TranslatableComponent("commands.publish.started", new Object[] { integer3 });
                jo4 = translatableComponent;
            }
            else {
                jo4 = new TranslatableComponent("commands.publish.failed", new Object[0]);
            }
            this.minecraft.gui.getChat().addMessage(jo4);
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), czi -> this.minecraft.setScreen(this.lastScreen)));
        this.modeButton = this.<Button>addButton(new Button(this.width / 2 - 155, 100, 150, 20, I18n.get("selectWorld.gameMode"), czi -> {
            if ("spectator".equals(this.gameModeName)) {
                this.gameModeName = "creative";
            }
            else if ("creative".equals(this.gameModeName)) {
                this.gameModeName = "adventure";
            }
            else if ("adventure".equals(this.gameModeName)) {
                this.gameModeName = "survival";
            }
            else {
                this.gameModeName = "spectator";
            }
            this.updateSelectionStrings();
            return;
        }));
        this.commandsButton = this.<Button>addButton(new Button(this.width / 2 + 5, 100, 150, 20, I18n.get("selectWorld.allowCommands"), czi -> {
            this.commands = !this.commands;
            this.updateSelectionStrings();
            return;
        }));
        this.updateSelectionStrings();
    }
    
    private void updateSelectionStrings() {
        this.modeButton.setMessage(I18n.get("selectWorld.gameMode") + ": " + I18n.get("selectWorld.gameMode." + this.gameModeName));
        this.commandsButton.setMessage(I18n.get("selectWorld.allowCommands") + ' ' + I18n.get(this.commands ? "options.on" : "options.off"));
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 50, 16777215);
        this.drawCenteredString(this.font, I18n.get("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
        super.render(integer1, integer2, float3);
    }
}
