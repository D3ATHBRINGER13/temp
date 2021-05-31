package net.minecraft.client.gui.screens.controls;

import net.minecraft.Util;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.Option;
import net.minecraft.client.gui.screens.Screen;

public class ControlsScreen extends Screen {
    private static final Option[] OPTIONS;
    private final Screen lastScreen;
    private final Options options;
    public KeyMapping selectedKey;
    public long lastKeySelection;
    private ControlList controlList;
    private Button resetButton;
    
    public ControlsScreen(final Screen dcl, final Options cyg) {
        super(new TranslatableComponent("controls.title", new Object[0]));
        this.lastScreen = dcl;
        this.options = cyg;
    }
    
    @Override
    protected void init() {
        this.<Button>addButton(new Button(this.width / 2 - 155, 18, 150, 20, I18n.get("options.mouse_settings"), czi -> this.minecraft.setScreen(new MouseSettingsScreen(this))));
        this.<AbstractWidget>addButton(Option.AUTO_JUMP.createButton(this.minecraft.options, this.width / 2 - 155 + 160, 18, 150));
        this.controlList = new ControlList(this, this.minecraft);
        this.children.add(this.controlList);
        final KeyMapping[] keyMappings;
        int length;
        int i = 0;
        KeyMapping cxz6;
        this.resetButton = this.<Button>addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, I18n.get("controls.resetAll"), czi -> {
            keyMappings = this.minecraft.options.keyMappings;
            for (length = keyMappings.length; i < length; ++i) {
                cxz6 = keyMappings[i];
                cxz6.setKey(cxz6.getDefaultKey());
            }
            KeyMapping.resetMapping();
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.get("gui.done"), czi -> this.minecraft.setScreen(this.lastScreen)));
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double2, final int integer) {
        if (this.selectedKey != null) {
            this.options.setKey(this.selectedKey, InputConstants.Type.MOUSE.getOrCreate(integer));
            this.selectedKey = null;
            KeyMapping.resetMapping();
            return true;
        }
        return super.mouseClicked(double1, double2, integer);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (this.selectedKey != null) {
            if (integer1 == 256) {
                this.options.setKey(this.selectedKey, InputConstants.UNKNOWN);
            }
            else {
                this.options.setKey(this.selectedKey, InputConstants.getKey(integer1, integer2));
            }
            this.selectedKey = null;
            this.lastKeySelection = Util.getMillis();
            KeyMapping.resetMapping();
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.controlList.render(integer1, integer2, float3);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 8, 16777215);
        boolean boolean5 = false;
        for (final KeyMapping cxz9 : this.options.keyMappings) {
            if (!cxz9.isDefault()) {
                boolean5 = true;
                break;
            }
        }
        this.resetButton.active = boolean5;
        super.render(integer1, integer2, float3);
    }
    
    static {
        OPTIONS = new Option[] { Option.INVERT_MOUSE, Option.SENSITIVITY, Option.TOUCHSCREEN, Option.AUTO_JUMP };
    }
}
