package net.minecraft.client.gui.screens;

import java.util.Iterator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.List;
import net.minecraft.network.chat.Component;

public class DisconnectedScreen extends Screen {
    private final Component reason;
    private List<String> lines;
    private final Screen parent;
    private int textHeight;
    
    public DisconnectedScreen(final Screen dcl, final String string, final Component jo) {
        super(new TranslatableComponent(string, new Object[0]));
        this.parent = dcl;
        this.reason = jo;
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    protected void init() {
        this.lines = this.font.split(this.reason.getColoredString(), this.width - 50);
        final int size = this.lines.size();
        this.font.getClass();
        this.textHeight = size * 9;
        final int integer1 = this.width / 2 - 100;
        final int n = this.height / 2 + this.textHeight / 2;
        this.font.getClass();
        this.<Button>addButton(new Button(integer1, Math.min(n + 9, this.height - 30), 200, 20, I18n.get("gui.toMenu"), czi -> this.minecraft.setScreen(this.parent)));
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        final Font font = this.font;
        final String coloredString = this.title.getColoredString();
        final int integer4 = this.width / 2;
        final int n = this.height / 2 - this.textHeight / 2;
        this.font.getClass();
        this.drawCenteredString(font, coloredString, integer4, n - 9 * 2, 11184810);
        int integer3 = this.height / 2 - this.textHeight / 2;
        if (this.lines != null) {
            for (final String string7 : this.lines) {
                this.drawCenteredString(this.font, string7, this.width / 2, integer3, 16777215);
                final int n2 = integer3;
                this.font.getClass();
                integer3 = n2 + 9;
            }
        }
        super.render(integer1, integer2, float3);
    }
}
