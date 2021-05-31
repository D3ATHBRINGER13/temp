package net.minecraft.client.gui.screens.worldselection;

import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.base.Splitter;
import net.minecraft.client.gui.components.events.GuiEventListener;
import java.util.function.Supplier;
import java.util.function.Consumer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class SelectWorldScreen extends Screen {
    protected final Screen lastScreen;
    private String toolTip;
    private Button deleteButton;
    private Button selectButton;
    private Button renameButton;
    private Button copyButton;
    protected EditBox searchBox;
    private WorldSelectionList list;
    
    public SelectWorldScreen(final Screen dcl) {
        super(new TranslatableComponent("selectWorld.title", new Object[0]));
        this.lastScreen = dcl;
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double2, final double double3) {
        return super.mouseScrolled(double1, double2, double3);
    }
    
    @Override
    public void tick() {
        this.searchBox.tick();
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        (this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, I18n.get("selectWorld.search"))).setResponder((Consumer<String>)(string -> this.list.refreshList((Supplier<String>)(() -> string), false)));
        this.list = new WorldSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, (Supplier<String>)(() -> this.searchBox.getValue()), this.list);
        this.children.add(this.searchBox);
        this.children.add(this.list);
        this.selectButton = this.<Button>addButton(new Button(this.width / 2 - 154, this.height - 52, 150, 20, I18n.get("selectWorld.select"), czi -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::joinWorld)));
        this.<Button>addButton(new Button(this.width / 2 + 4, this.height - 52, 150, 20, I18n.get("selectWorld.create"), czi -> this.minecraft.setScreen(new CreateWorldScreen(this))));
        this.renameButton = this.<Button>addButton(new Button(this.width / 2 - 154, this.height - 28, 72, 20, I18n.get("selectWorld.edit"), czi -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld)));
        this.deleteButton = this.<Button>addButton(new Button(this.width / 2 - 76, this.height - 28, 72, 20, I18n.get("selectWorld.delete"), czi -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld)));
        this.copyButton = this.<Button>addButton(new Button(this.width / 2 + 4, this.height - 28, 72, 20, I18n.get("selectWorld.recreate"), czi -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::recreateWorld)));
        this.<Button>addButton(new Button(this.width / 2 + 82, this.height - 28, 72, 20, I18n.get("gui.cancel"), czi -> this.minecraft.setScreen(this.lastScreen)));
        this.updateButtonStatus(false);
        this.setInitialFocus(this.searchBox);
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        return super.keyPressed(integer1, integer2, integer3) || this.searchBox.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public boolean charTyped(final char character, final int integer) {
        return this.searchBox.charTyped(character, integer);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.toolTip = null;
        this.list.render(integer1, integer2, float3);
        this.searchBox.render(integer1, integer2, float3);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 8, 16777215);
        super.render(integer1, integer2, float3);
        if (this.toolTip != null) {
            this.renderTooltip((List<String>)Lists.newArrayList(Splitter.on("\n").split((CharSequence)this.toolTip)), integer1, integer2);
        }
    }
    
    public void setToolTip(final String string) {
        this.toolTip = string;
    }
    
    public void updateButtonStatus(final boolean boolean1) {
        this.selectButton.active = boolean1;
        this.deleteButton.active = boolean1;
        this.renameButton.active = boolean1;
        this.copyButton.active = boolean1;
    }
    
    @Override
    public void removed() {
        if (this.list != null) {
            this.list.children().forEach(WorldSelectionList.WorldListEntry::close);
        }
    }
}
