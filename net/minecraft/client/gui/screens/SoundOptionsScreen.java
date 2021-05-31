package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.VolumeSlider;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.Options;

public class SoundOptionsScreen extends Screen {
    private final Screen lastScreen;
    private final Options options;
    
    public SoundOptionsScreen(final Screen dcl, final Options cyg) {
        super(new TranslatableComponent("options.sounds.title", new Object[0]));
        this.lastScreen = dcl;
        this.options = cyg;
    }
    
    @Override
    protected void init() {
        int integer2 = 0;
        this.<VolumeSlider>addButton(new VolumeSlider(this.minecraft, this.width / 2 - 155 + integer2 % 2 * 160, this.height / 6 - 12 + 24 * (integer2 >> 1), SoundSource.MASTER, 310));
        integer2 += 2;
        for (final SoundSource yq6 : SoundSource.values()) {
            if (yq6 != SoundSource.MASTER) {
                this.<VolumeSlider>addButton(new VolumeSlider(this.minecraft, this.width / 2 - 155 + integer2 % 2 * 160, this.height / 6 - 12 + 24 * (integer2 >> 1), yq6, 150));
                ++integer2;
            }
        }
        this.<OptionButton>addButton(new OptionButton(this.width / 2 - 75, this.height / 6 - 12 + 24 * (++integer2 >> 1), 150, 20, Option.SHOW_SUBTITLES, Option.SHOW_SUBTITLES.getMessage(this.options), czi -> {
            Option.SHOW_SUBTITLES.toggle(this.minecraft.options);
            czi.setMessage(Option.SHOW_SUBTITLES.getMessage(this.minecraft.options));
            this.minecraft.options.save();
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.get("gui.done"), czi -> this.minecraft.setScreen(this.lastScreen)));
    }
    
    @Override
    public void removed() {
        this.minecraft.options.save();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 15, 16777215);
        super.render(integer1, integer2, float3);
    }
}
