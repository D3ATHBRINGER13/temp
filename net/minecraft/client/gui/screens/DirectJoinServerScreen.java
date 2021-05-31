package net.minecraft.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.function.Consumer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.gui.components.Button;

public class DirectJoinServerScreen extends Screen {
    private Button selectButton;
    private final ServerData serverData;
    private EditBox ipEdit;
    private final BooleanConsumer callback;
    
    public DirectJoinServerScreen(final BooleanConsumer booleanConsumer, final ServerData dki) {
        super(new TranslatableComponent("selectServer.direct", new Object[0]));
        this.serverData = dki;
        this.callback = booleanConsumer;
    }
    
    @Override
    public void tick() {
        this.ipEdit.tick();
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (this.getFocused() == this.ipEdit && (integer1 == 257 || integer1 == 335)) {
            this.onSelect();
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.selectButton = this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, I18n.get("selectServer.select"), czi -> this.onSelect()));
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.get("gui.cancel"), czi -> this.callback.accept(false)));
        (this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 116, 200, 20, I18n.get("addServer.enterIp"))).setMaxLength(128);
        this.ipEdit.setFocus(true);
        this.ipEdit.setValue(this.minecraft.options.lastMpIp);
        this.ipEdit.setResponder((Consumer<String>)(string -> this.updateSelectButtonStatus()));
        this.children.add(this.ipEdit);
        this.setInitialFocus(this.ipEdit);
        this.updateSelectButtonStatus();
    }
    
    @Override
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        final String string5 = this.ipEdit.getValue();
        this.init(cyc, integer2, integer3);
        this.ipEdit.setValue(string5);
    }
    
    private void onSelect() {
        this.serverData.ip = this.ipEdit.getValue();
        this.callback.accept(true);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        this.minecraft.options.lastMpIp = this.ipEdit.getValue();
        this.minecraft.options.save();
    }
    
    private void updateSelectButtonStatus() {
        this.selectButton.active = (!this.ipEdit.getValue().isEmpty() && this.ipEdit.getValue().split(":").length > 0);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
        this.drawString(this.font, I18n.get("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
        this.ipEdit.render(integer1, integer2, float3);
        super.render(integer1, integer2, float3);
    }
}
