package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

public class ConfirmLinkScreen extends ConfirmScreen {
    private final String warning;
    private final String copyButton;
    private final String url;
    private final boolean showWarning;
    
    public ConfirmLinkScreen(final BooleanConsumer booleanConsumer, final String string, final boolean boolean3) {
        super(booleanConsumer, new TranslatableComponent(boolean3 ? "chat.link.confirmTrusted" : "chat.link.confirm", new Object[0]), new TextComponent(string));
        this.yesButton = I18n.get(boolean3 ? "chat.link.open" : "gui.yes");
        this.noButton = I18n.get(boolean3 ? "gui.cancel" : "gui.no");
        this.copyButton = I18n.get("chat.copy");
        this.warning = I18n.get("chat.link.warning");
        this.showWarning = !boolean3;
        this.url = string;
    }
    
    @Override
    protected void init() {
        super.init();
        this.buttons.clear();
        this.children.clear();
        this.<Button>addButton(new Button(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesButton, czi -> this.callback.accept(true)));
        this.<Button>addButton(new Button(this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyButton, czi -> {
            this.copyToClipboard();
            this.callback.accept(false);
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noButton, czi -> this.callback.accept(false)));
    }
    
    public void copyToClipboard() {
        this.minecraft.keyboardHandler.setClipboard(this.url);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        super.render(integer1, integer2, float3);
        if (this.showWarning) {
            this.drawCenteredString(this.font, this.warning, this.width / 2, 110, 16764108);
        }
    }
}
