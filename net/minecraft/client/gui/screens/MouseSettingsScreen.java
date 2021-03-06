package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import java.util.stream.Stream;
import java.util.Arrays;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.OptionsList;

public class MouseSettingsScreen extends Screen {
    private final Screen lastScreen;
    private OptionsList list;
    private static final Option[] OPTIONS;
    
    public MouseSettingsScreen(final Screen dcl) {
        super(new TranslatableComponent("options.mouse_settings.title", new Object[0]));
        this.lastScreen = dcl;
    }
    
    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        if (InputConstants.isRawMouseInputSupported()) {
            this.list.addSmall((Option[])Stream.concat(Arrays.stream((Object[])MouseSettingsScreen.OPTIONS), Stream.of(Option.RAW_MOUSE_INPUT)).toArray(Option[]::new));
        }
        else {
            this.list.addSmall(MouseSettingsScreen.OPTIONS);
        }
        this.children.add(this.list);
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, I18n.get("gui.done"), czi -> {
            this.minecraft.options.save();
            this.minecraft.setScreen(this.lastScreen);
        }));
    }
    
    @Override
    public void removed() {
        this.minecraft.options.save();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.list.render(integer1, integer2, float3);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 5, 16777215);
        super.render(integer1, integer2, float3);
    }
    
    static {
        OPTIONS = new Option[] { Option.SENSITIVITY, Option.INVERT_MOUSE, Option.MOUSE_WHEEL_SENSITIVITY, Option.DISCRETE_MOUSE_SCROLL, Option.TOUCHSCREEN };
    }
}
