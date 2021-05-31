package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.Options;
import net.minecraft.client.Option;

public class ChatOptionsScreen extends Screen {
    private static final Option[] CHAT_OPTIONS;
    private final Screen lastScreen;
    private final Options options;
    private AbstractWidget narratorButton;
    
    public ChatOptionsScreen(final Screen dcl, final Options cyg) {
        super(new TranslatableComponent("options.chat.title", new Object[0]));
        this.lastScreen = dcl;
        this.options = cyg;
    }
    
    @Override
    protected void init() {
        int integer2 = 0;
        for (final Option cyf6 : ChatOptionsScreen.CHAT_OPTIONS) {
            final int integer3 = this.width / 2 - 155 + integer2 % 2 * 160;
            final int integer4 = this.height / 6 + 24 * (integer2 >> 1);
            final AbstractWidget czg9 = this.<AbstractWidget>addButton(cyf6.createButton(this.minecraft.options, integer3, integer4, 150));
            if (cyf6 == Option.NARRATOR) {
                this.narratorButton = czg9;
                czg9.active = NarratorChatListener.INSTANCE.isActive();
            }
            ++integer2;
        }
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (integer2 + 1) / 2, 200, 20, I18n.get("gui.done"), czi -> this.minecraft.setScreen(this.lastScreen)));
    }
    
    @Override
    public void removed() {
        this.minecraft.options.save();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
        super.render(integer1, integer2, float3);
    }
    
    public void updateNarratorButton() {
        this.narratorButton.setMessage(Option.NARRATOR.getMessage(this.options));
    }
    
    static {
        CHAT_OPTIONS = new Option[] { Option.CHAT_VISIBILITY, Option.CHAT_COLOR, Option.CHAT_LINKS, Option.CHAT_LINKS_PROMPT, Option.CHAT_OPACITY, Option.TEXT_BACKGROUND_OPACITY, Option.CHAT_SCALE, Option.CHAT_WIDTH, Option.CHAT_HEIGHT_FOCUSED, Option.CHAT_HEIGHT_UNFOCUSED, Option.REDUCED_DEBUG_INFO, Option.AUTO_SUGGESTIONS, Option.NARRATOR };
    }
}
