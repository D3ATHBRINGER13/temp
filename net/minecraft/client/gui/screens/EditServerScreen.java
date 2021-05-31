package net.minecraft.client.gui.screens;

import java.net.IDN;
import net.minecraft.util.StringUtil;
import net.minecraft.client.Minecraft;
import java.util.function.Consumer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.function.Predicate;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.Button;

public class EditServerScreen extends Screen {
    private Button addButton;
    private final BooleanConsumer callback;
    private final ServerData serverData;
    private EditBox ipEdit;
    private EditBox nameEdit;
    private Button serverPackButton;
    private final Predicate<String> addressFilter;
    
    public EditServerScreen(final BooleanConsumer booleanConsumer, final ServerData dki) {
        super(new TranslatableComponent("addServer.title", new Object[0]));
        this.addressFilter = (Predicate<String>)(string -> {
            if (StringUtil.isNullOrEmpty(string)) {
                return true;
            }
            final String[] arr2 = string.split(":");
            if (arr2.length == 0) {
                return true;
            }
            try {
                final String string2 = IDN.toASCII(arr2[0]);
                return true;
            }
            catch (IllegalArgumentException illegalArgumentException3) {
                return false;
            }
        });
        this.callback = booleanConsumer;
        this.serverData = dki;
    }
    
    @Override
    public void tick() {
        this.nameEdit.tick();
        this.ipEdit.tick();
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        (this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 66, 200, 20, I18n.get("addServer.enterName"))).setFocus(true);
        this.nameEdit.setValue(this.serverData.name);
        this.nameEdit.setResponder((Consumer<String>)this::onEdited);
        this.children.add(this.nameEdit);
        (this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 106, 200, 20, I18n.get("addServer.enterIp"))).setMaxLength(128);
        this.ipEdit.setValue(this.serverData.ip);
        this.ipEdit.setFilter(this.addressFilter);
        this.ipEdit.setResponder((Consumer<String>)this::onEdited);
        this.children.add(this.ipEdit);
        this.serverPackButton = this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, I18n.get("addServer.resourcePack") + ": " + this.serverData.getResourcePackStatus().getName().getColoredString(), czi -> {
            this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.values()[(this.serverData.getResourcePackStatus().ordinal() + 1) % ServerData.ServerPackStatus.values().length]);
            this.serverPackButton.setMessage(I18n.get("addServer.resourcePack") + ": " + this.serverData.getResourcePackStatus().getName().getColoredString());
            return;
        }));
        this.addButton = this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, I18n.get("addServer.add"), czi -> this.onAdd()));
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, I18n.get("gui.cancel"), czi -> this.callback.accept(false)));
        this.onClose();
    }
    
    @Override
    public void resize(final Minecraft cyc, final int integer2, final int integer3) {
        final String string5 = this.ipEdit.getValue();
        final String string6 = this.nameEdit.getValue();
        this.init(cyc, integer2, integer3);
        this.ipEdit.setValue(string5);
        this.nameEdit.setValue(string6);
    }
    
    private void onEdited(final String string) {
        this.onClose();
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
    
    private void onAdd() {
        this.serverData.name = this.nameEdit.getValue();
        this.serverData.ip = this.ipEdit.getValue();
        this.callback.accept(true);
    }
    
    @Override
    public void onClose() {
        this.addButton.active = (!this.ipEdit.getValue().isEmpty() && this.ipEdit.getValue().split(":").length > 0 && !this.nameEdit.getValue().isEmpty());
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 17, 16777215);
        this.drawString(this.font, I18n.get("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
        this.drawString(this.font, I18n.get("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
        this.nameEdit.render(integer1, integer2, float3);
        this.ipEdit.render(integer1, integer2, float3);
        super.render(integer1, integer2, float3);
    }
}
