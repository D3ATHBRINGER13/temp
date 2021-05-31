package net.minecraft.client.gui.spectator;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import java.util.List;

public class SpectatorMenu {
    private static final SpectatorMenuItem CLOSE_ITEM;
    private static final SpectatorMenuItem SCROLL_LEFT;
    private static final SpectatorMenuItem SCROLL_RIGHT_ENABLED;
    private static final SpectatorMenuItem SCROLL_RIGHT_DISABLED;
    public static final SpectatorMenuItem EMPTY_SLOT;
    private final SpectatorMenuListener listener;
    private final List<SpectatorPage> previousCategories;
    private SpectatorMenuCategory category;
    private int selectedSlot;
    private int page;
    
    public SpectatorMenu(final SpectatorMenuListener dgb) {
        this.previousCategories = (List<SpectatorPage>)Lists.newArrayList();
        this.selectedSlot = -1;
        this.category = new RootSpectatorMenuCategory();
        this.listener = dgb;
    }
    
    public SpectatorMenuItem getItem(final int integer) {
        final int integer2 = integer + this.page * 6;
        if (this.page > 0 && integer == 0) {
            return SpectatorMenu.SCROLL_LEFT;
        }
        if (integer == 7) {
            if (integer2 < this.category.getItems().size()) {
                return SpectatorMenu.SCROLL_RIGHT_ENABLED;
            }
            return SpectatorMenu.SCROLL_RIGHT_DISABLED;
        }
        else {
            if (integer == 8) {
                return SpectatorMenu.CLOSE_ITEM;
            }
            if (integer2 < 0 || integer2 >= this.category.getItems().size()) {
                return SpectatorMenu.EMPTY_SLOT;
            }
            return (SpectatorMenuItem)MoreObjects.firstNonNull(this.category.getItems().get(integer2), SpectatorMenu.EMPTY_SLOT);
        }
    }
    
    public List<SpectatorMenuItem> getItems() {
        final List<SpectatorMenuItem> list2 = (List<SpectatorMenuItem>)Lists.newArrayList();
        for (int integer3 = 0; integer3 <= 8; ++integer3) {
            list2.add(this.getItem(integer3));
        }
        return list2;
    }
    
    public SpectatorMenuItem getSelectedItem() {
        return this.getItem(this.selectedSlot);
    }
    
    public SpectatorMenuCategory getSelectedCategory() {
        return this.category;
    }
    
    public void selectSlot(final int integer) {
        final SpectatorMenuItem dga3 = this.getItem(integer);
        if (dga3 != SpectatorMenu.EMPTY_SLOT) {
            if (this.selectedSlot == integer && dga3.isEnabled()) {
                dga3.selectItem(this);
            }
            else {
                this.selectedSlot = integer;
            }
        }
    }
    
    public void exit() {
        this.listener.onSpectatorMenuClosed(this);
    }
    
    public int getSelectedSlot() {
        return this.selectedSlot;
    }
    
    public void selectCategory(final SpectatorMenuCategory dfz) {
        this.previousCategories.add(this.getCurrentPage());
        this.category = dfz;
        this.selectedSlot = -1;
        this.page = 0;
    }
    
    public SpectatorPage getCurrentPage() {
        return new SpectatorPage(this.category, this.getItems(), this.selectedSlot);
    }
    
    static {
        CLOSE_ITEM = new CloseSpectatorItem();
        SCROLL_LEFT = new ScrollMenuItem(-1, true);
        SCROLL_RIGHT_ENABLED = new ScrollMenuItem(1, true);
        SCROLL_RIGHT_DISABLED = new ScrollMenuItem(1, false);
        EMPTY_SLOT = new SpectatorMenuItem() {
            public void selectItem(final SpectatorMenu dfy) {
            }
            
            public Component getName() {
                return new TextComponent("");
            }
            
            public void renderIcon(final float float1, final int integer) {
            }
            
            public boolean isEnabled() {
                return false;
            }
        };
    }
    
    static class CloseSpectatorItem implements SpectatorMenuItem {
        private CloseSpectatorItem() {
        }
        
        public void selectItem(final SpectatorMenu dfy) {
            dfy.exit();
        }
        
        public Component getName() {
            return new TranslatableComponent("spectatorMenu.close", new Object[0]);
        }
        
        public void renderIcon(final float float1, final int integer) {
            Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
            GuiComponent.blit(0, 0, 128.0f, 0.0f, 16, 16, 256, 256);
        }
        
        public boolean isEnabled() {
            return true;
        }
    }
    
    static class ScrollMenuItem implements SpectatorMenuItem {
        private final int direction;
        private final boolean enabled;
        
        public ScrollMenuItem(final int integer, final boolean boolean2) {
            this.direction = integer;
            this.enabled = boolean2;
        }
        
        public void selectItem(final SpectatorMenu dfy) {
            dfy.page += this.direction;
        }
        
        public Component getName() {
            if (this.direction < 0) {
                return new TranslatableComponent("spectatorMenu.previous_page", new Object[0]);
            }
            return new TranslatableComponent("spectatorMenu.next_page", new Object[0]);
        }
        
        public void renderIcon(final float float1, final int integer) {
            Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
            if (this.direction < 0) {
                GuiComponent.blit(0, 0, 144.0f, 0.0f, 16, 16, 256, 256);
            }
            else {
                GuiComponent.blit(0, 0, 160.0f, 0.0f, 16, 16, 256, 256);
            }
        }
        
        public boolean isEnabled() {
            return this.enabled;
        }
    }
}
