package net.minecraft.client.gui.screens;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class SkinCustomizationScreen extends Screen {
    private final Screen lastScreen;
    
    public SkinCustomizationScreen(final Screen dcl) {
        super(new TranslatableComponent("options.skinCustomisation.title", new Object[0]));
        this.lastScreen = dcl;
    }
    
    @Override
    protected void init() {
        int integer2 = 0;
        for (final PlayerModelPart awh6 : PlayerModelPart.values()) {
            final PlayerModelPart playerModelPart;
            this.<Button>addButton(new Button(this.width / 2 - 155 + integer2 % 2 * 160, this.height / 6 + 24 * (integer2 >> 1), 150, 20, this.getMessage(awh6), czi -> {
                this.minecraft.options.toggleModelPart(playerModelPart);
                czi.setMessage(this.getMessage(playerModelPart));
                return;
            }));
            ++integer2;
        }
        this.<OptionButton>addButton(new OptionButton(this.width / 2 - 155 + integer2 % 2 * 160, this.height / 6 + 24 * (integer2 >> 1), 150, 20, Option.MAIN_HAND, Option.MAIN_HAND.getMessage(this.minecraft.options), czi -> {
            Option.MAIN_HAND.toggle(this.minecraft.options, 1);
            this.minecraft.options.save();
            czi.setMessage(Option.MAIN_HAND.getMessage(this.minecraft.options));
            this.minecraft.options.broadcastOptions();
            return;
        }));
        if (++integer2 % 2 == 1) {
            ++integer2;
        }
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (integer2 >> 1), 200, 20, I18n.get("gui.done"), czi -> this.minecraft.setScreen(this.lastScreen)));
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
    
    private String getMessage(final PlayerModelPart awh) {
        String string3;
        if (this.minecraft.options.getModelParts().contains(awh)) {
            string3 = I18n.get("options.on");
        }
        else {
            string3 = I18n.get("options.off");
        }
        return awh.getName().getColoredString() + ": " + string3;
    }
}
