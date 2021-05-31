package net.minecraft.client.gui.spectator.categories;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mth;
import java.util.Collection;
import net.minecraft.client.player.AbstractClientPlayer;
import java.util.Random;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import java.util.Iterator;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.client.Minecraft;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;

public class TeleportToTeamMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
    private final List<SpectatorMenuItem> items;
    
    public TeleportToTeamMenuCategory() {
        this.items = (List<SpectatorMenuItem>)Lists.newArrayList();
        final Minecraft cyc2 = Minecraft.getInstance();
        for (final PlayerTeam ctg4 : cyc2.level.getScoreboard().getPlayerTeams()) {
            this.items.add(new TeamSelectionItem(ctg4));
        }
    }
    
    public List<SpectatorMenuItem> getItems() {
        return this.items;
    }
    
    public Component getPrompt() {
        return new TranslatableComponent("spectatorMenu.team_teleport.prompt", new Object[0]);
    }
    
    public void selectItem(final SpectatorMenu dfy) {
        dfy.selectCategory(this);
    }
    
    public Component getName() {
        return new TranslatableComponent("spectatorMenu.team_teleport", new Object[0]);
    }
    
    public void renderIcon(final float float1, final int integer) {
        Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
        GuiComponent.blit(0, 0, 16.0f, 0.0f, 16, 16, 256, 256);
    }
    
    public boolean isEnabled() {
        for (final SpectatorMenuItem dga3 : this.items) {
            if (dga3.isEnabled()) {
                return true;
            }
        }
        return false;
    }
    
    class TeamSelectionItem implements SpectatorMenuItem {
        private final PlayerTeam team;
        private final ResourceLocation location;
        private final List<PlayerInfo> players;
        
        public TeamSelectionItem(final PlayerTeam ctg) {
            this.team = ctg;
            this.players = (List<PlayerInfo>)Lists.newArrayList();
            for (final String string5 : ctg.getPlayers()) {
                final PlayerInfo dkg6 = Minecraft.getInstance().getConnection().getPlayerInfo(string5);
                if (dkg6 != null) {
                    this.players.add(dkg6);
                }
            }
            if (this.players.isEmpty()) {
                this.location = DefaultPlayerSkin.getDefaultSkin();
            }
            else {
                final String string6 = ((PlayerInfo)this.players.get(new Random().nextInt(this.players.size()))).getProfile().getName();
                AbstractClientPlayer.registerSkinTexture(this.location = AbstractClientPlayer.getSkinLocation(string6), string6);
            }
        }
        
        public void selectItem(final SpectatorMenu dfy) {
            dfy.selectCategory(new TeleportToPlayerMenuCategory((Collection<PlayerInfo>)this.players));
        }
        
        public Component getName() {
            return this.team.getDisplayName();
        }
        
        public void renderIcon(final float float1, final int integer) {
            final Integer integer2 = this.team.getColor().getColor();
            if (integer2 != null) {
                final float float2 = (integer2 >> 16 & 0xFF) / 255.0f;
                final float float3 = (integer2 >> 8 & 0xFF) / 255.0f;
                final float float4 = (integer2 & 0xFF) / 255.0f;
                GuiComponent.fill(1, 1, 15, 15, Mth.color(float2 * float1, float3 * float1, float4 * float1) | integer << 24);
            }
            Minecraft.getInstance().getTextureManager().bind(this.location);
            GlStateManager.color4f(float1, float1, float1, integer / 255.0f);
            GuiComponent.blit(2, 2, 12, 12, 8.0f, 8.0f, 8, 8, 64, 64);
            GuiComponent.blit(2, 2, 12, 12, 40.0f, 8.0f, 8, 8, 64, 64);
        }
        
        public boolean isEnabled() {
            return !this.players.isEmpty();
        }
    }
}
