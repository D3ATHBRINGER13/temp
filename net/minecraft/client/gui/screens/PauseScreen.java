package net.minecraft.client.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class PauseScreen extends Screen {
    private final boolean showPauseMenu;
    
    public PauseScreen(final boolean boolean1) {
        super(boolean1 ? new TranslatableComponent("menu.game", new Object[0]) : new TranslatableComponent("menu.paused", new Object[0]));
        this.showPauseMenu = boolean1;
    }
    
    @Override
    protected void init() {
        if (this.showPauseMenu) {
            this.createPauseMenu();
        }
    }
    
    private void createPauseMenu() {
        final int integer2 = -16;
        final int integer3 = 98;
        this.<Button>addButton(new Button(this.width / 2 - 102, this.height / 4 + 24 - 16, 204, 20, I18n.get("menu.returnToGame"), czi -> {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 - 102, this.height / 4 + 48 - 16, 98, 20, I18n.get("gui.advancements"), czi -> this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()))));
        this.<Button>addButton(new Button(this.width / 2 + 4, this.height / 4 + 48 - 16, 98, 20, I18n.get("gui.stats"), czi -> this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()))));
        final String string4 = SharedConstants.getCurrentVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
        final String string5;
        this.<Button>addButton(new Button(this.width / 2 - 102, this.height / 4 + 72 - 16, 98, 20, I18n.get("menu.sendFeedback"), czi -> this.minecraft.setScreen(new ConfirmLinkScreen(boolean2 -> {
            if (boolean2) {
                Util.getPlatform().openUri(string5);
            }
            this.minecraft.setScreen(this);
        }, string5, true))));
        this.<Button>addButton(new Button(this.width / 2 + 4, this.height / 4 + 72 - 16, 98, 20, I18n.get("menu.reportBugs"), czi -> this.minecraft.setScreen(new ConfirmLinkScreen(boolean1 -> {
            if (boolean1) {
                Util.getPlatform().openUri("https://aka.ms/snapshotbugs?ref=game");
            }
            this.minecraft.setScreen(this);
        }, "https://aka.ms/snapshotbugs?ref=game", true))));
        this.<Button>addButton(new Button(this.width / 2 - 102, this.height / 4 + 96 - 16, 98, 20, I18n.get("menu.options"), czi -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));
        final Button czi2 = this.<Button>addButton(new Button(this.width / 2 + 4, this.height / 4 + 96 - 16, 98, 20, I18n.get("menu.shareToLan"), czi -> this.minecraft.setScreen(new ShareToLanScreen(this))));
        czi2.active = (this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished());
        final boolean boolean3;
        final boolean boolean4;
        Minecraft minecraft;
        final GenericDirtMessageScreen dcl;
        RealmsBridge realmsBridge5;
        Minecraft minecraft2;
        final JoinMultiplayerScreen screen;
        final Button czi3 = this.<Button>addButton(new Button(this.width / 2 - 102, this.height / 4 + 120 - 16, 204, 20, I18n.get("menu.returnToMenu"), czi -> {
            boolean3 = this.minecraft.isLocalServer();
            boolean4 = this.minecraft.isConnectedToRealms();
            czi.active = false;
            this.minecraft.level.disconnect();
            if (boolean3) {
                minecraft = this.minecraft;
                new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel", new Object[0]));
                minecraft.clearLevel(dcl);
            }
            else {
                this.minecraft.clearLevel();
            }
            if (boolean3) {
                this.minecraft.setScreen(new TitleScreen());
            }
            else if (boolean4) {
                realmsBridge5 = new RealmsBridge();
                realmsBridge5.switchToRealms(new TitleScreen());
            }
            else {
                minecraft2 = this.minecraft;
                new JoinMultiplayerScreen(new TitleScreen());
                minecraft2.setScreen(screen);
            }
            return;
        }));
        if (!this.minecraft.isLocalServer()) {
            czi3.setMessage(I18n.get("menu.disconnect"));
        }
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        if (this.showPauseMenu) {
            this.renderBackground();
            this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 40, 16777215);
        }
        else {
            this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 10, 16777215);
        }
        super.render(integer1, integer2, float3);
    }
}
