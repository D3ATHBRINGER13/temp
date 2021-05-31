package net.minecraft.client.gui.screens.resourcepacks;

import net.minecraft.server.packs.repository.PackRepository;
import java.util.Iterator;
import net.minecraft.client.resources.UnopenedResourcePack;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import net.minecraft.client.gui.screens.resourcepacks.lists.ResourcePackList;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.components.Button;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.screens.resourcepacks.lists.SelectedResourcePackList;
import net.minecraft.client.gui.screens.resourcepacks.lists.AvailableResourcePackList;
import net.minecraft.client.gui.screens.Screen;

public class ResourcePackSelectScreen extends Screen {
    private final Screen parentScreen;
    private AvailableResourcePackList availableResourcePackList;
    private SelectedResourcePackList selectedResourcePackList;
    private boolean changed;
    
    public ResourcePackSelectScreen(final Screen dcl) {
        super(new TranslatableComponent("resourcePack.title", new Object[0]));
        this.parentScreen = dcl;
    }
    
    @Override
    protected void init() {
        this.<Button>addButton(new Button(this.width / 2 - 154, this.height - 48, 150, 20, I18n.get("resourcePack.openFolder"), czi -> Util.getPlatform().openFile(this.minecraft.getResourcePackDirectory())));
        List<UnopenedResourcePack> list3;
        final Iterator iterator;
        ResourcePackList.ResourcePackEntry a5;
        final Iterator iterator2;
        UnopenedResourcePack dxw5;
        this.<Button>addButton(new Button(this.width / 2 + 4, this.height - 48, 150, 20, I18n.get("gui.done"), czi -> {
            if (this.changed) {
                list3 = (List<UnopenedResourcePack>)Lists.newArrayList();
                this.selectedResourcePackList.children().iterator();
                while (iterator.hasNext()) {
                    a5 = (ResourcePackList.ResourcePackEntry)iterator.next();
                    list3.add(a5.getResourcePack());
                }
                Collections.reverse((List)list3);
                this.minecraft.getResourcePackRepository().setSelected((java.util.Collection<UnopenedResourcePack>)list3);
                this.minecraft.options.resourcePacks.clear();
                this.minecraft.options.incompatibleResourcePacks.clear();
                list3.iterator();
                while (iterator2.hasNext()) {
                    dxw5 = (UnopenedResourcePack)iterator2.next();
                    if (!dxw5.isFixedPosition()) {
                        this.minecraft.options.resourcePacks.add(dxw5.getId());
                        if (!dxw5.getCompatibility().isCompatible()) {
                            this.minecraft.options.incompatibleResourcePacks.add(dxw5.getId());
                        }
                        else {
                            continue;
                        }
                    }
                }
                this.minecraft.options.save();
                this.minecraft.setScreen(this.parentScreen);
                this.minecraft.reloadResourcePacks();
            }
            else {
                this.minecraft.setScreen(this.parentScreen);
            }
            return;
        }));
        final AvailableResourcePackList dfk2 = this.availableResourcePackList;
        final SelectedResourcePackList dfm3 = this.selectedResourcePackList;
        (this.availableResourcePackList = new AvailableResourcePackList(this.minecraft, 200, this.height)).setLeftPos(this.width / 2 - 4 - 200);
        if (dfk2 != null) {
            this.availableResourcePackList.children().addAll((Collection)dfk2.children());
        }
        this.children.add(this.availableResourcePackList);
        (this.selectedResourcePackList = new SelectedResourcePackList(this.minecraft, 200, this.height)).setLeftPos(this.width / 2 + 4);
        if (dfm3 != null) {
            this.selectedResourcePackList.children().addAll((Collection)dfm3.children());
        }
        this.children.add(this.selectedResourcePackList);
        if (!this.changed) {
            this.availableResourcePackList.children().clear();
            this.selectedResourcePackList.children().clear();
            final PackRepository<UnopenedResourcePack> wx4 = this.minecraft.getResourcePackRepository();
            wx4.reload();
            final List<UnopenedResourcePack> list4 = (List<UnopenedResourcePack>)Lists.newArrayList((Iterable)wx4.getAvailable());
            list4.removeAll((Collection)wx4.getSelected());
            for (final UnopenedResourcePack dxw6 : list4) {
                this.availableResourcePackList.addResourcePackEntry(new ResourcePackList.ResourcePackEntry(this.availableResourcePackList, this, dxw6));
            }
            for (final UnopenedResourcePack dxw6 : Lists.reverse((List)Lists.newArrayList((Iterable)wx4.getSelected()))) {
                this.selectedResourcePackList.addResourcePackEntry(new ResourcePackList.ResourcePackEntry(this.selectedResourcePackList, this, dxw6));
            }
        }
    }
    
    public void select(final ResourcePackList.ResourcePackEntry a) {
        this.availableResourcePackList.children().remove(a);
        a.addToList(this.selectedResourcePackList);
        this.setChanged();
    }
    
    public void deselect(final ResourcePackList.ResourcePackEntry a) {
        this.selectedResourcePackList.children().remove(a);
        this.availableResourcePackList.addResourcePackEntry(a);
        this.setChanged();
    }
    
    public boolean isSelected(final ResourcePackList.ResourcePackEntry a) {
        return this.selectedResourcePackList.children().contains(a);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderDirtBackground(0);
        this.availableResourcePackList.render(integer1, integer2, float3);
        this.selectedResourcePackList.render(integer1, integer2, float3);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.font, I18n.get("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
        super.render(integer1, integer2, float3);
    }
    
    public void setChanged() {
        this.changed = true;
    }
}
