package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import java.util.Iterator;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.world.level.GameType;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import java.util.List;
import net.minecraft.client.multiplayer.PlayerInfo;
import com.google.common.collect.Ordering;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;

public class TeleportToPlayerMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
    private static final Ordering<PlayerInfo> PROFILE_ORDER;
    private final List<SpectatorMenuItem> items;
    
    public TeleportToPlayerMenuCategory() {
        this((Collection<PlayerInfo>)TeleportToPlayerMenuCategory.PROFILE_ORDER.sortedCopy((Iterable)Minecraft.getInstance().getConnection().getOnlinePlayers()));
    }
    
    public TeleportToPlayerMenuCategory(final Collection<PlayerInfo> collection) {
        this.items = (List<SpectatorMenuItem>)Lists.newArrayList();
        for (final PlayerInfo dkg4 : TeleportToPlayerMenuCategory.PROFILE_ORDER.sortedCopy((Iterable)collection)) {
            if (dkg4.getGameMode() != GameType.SPECTATOR) {
                this.items.add(new PlayerMenuItem(dkg4.getProfile()));
            }
        }
    }
    
    public List<SpectatorMenuItem> getItems() {
        return this.items;
    }
    
    public Component getPrompt() {
        return new TranslatableComponent("spectatorMenu.teleport.prompt", new Object[0]);
    }
    
    public void selectItem(final SpectatorMenu dfy) {
        dfy.selectCategory(this);
    }
    
    public Component getName() {
        return new TranslatableComponent("spectatorMenu.teleport", new Object[0]);
    }
    
    public void renderIcon(final float float1, final int integer) {
        Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
        GuiComponent.blit(0, 0, 0.0f, 0.0f, 16, 16, 256, 256);
    }
    
    public boolean isEnabled() {
        return !this.items.isEmpty();
    }
    
    static {
        PROFILE_ORDER = Ordering.from((dkg1, dkg2) -> ComparisonChain.start().compare((Comparable)dkg1.getProfile().getId(), (Comparable)dkg2.getProfile().getId()).result());
    }
}
