package net.minecraft.client.gui.spectator;

import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import java.util.Map;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.Player;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import com.mojang.authlib.GameProfile;

public class PlayerMenuItem implements SpectatorMenuItem {
    private final GameProfile profile;
    private final ResourceLocation location;
    
    public PlayerMenuItem(final GameProfile gameProfile) {
        this.profile = gameProfile;
        final Minecraft cyc3 = Minecraft.getInstance();
        final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map4 = cyc3.getSkinManager().getInsecureSkinInformation(gameProfile);
        if (map4.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            this.location = cyc3.getSkinManager().registerTexture((MinecraftProfileTexture)map4.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
        }
        else {
            this.location = DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(gameProfile));
        }
    }
    
    public void selectItem(final SpectatorMenu dfy) {
        Minecraft.getInstance().getConnection().send(new ServerboundTeleportToEntityPacket(this.profile.getId()));
    }
    
    public Component getName() {
        return new TextComponent(this.profile.getName());
    }
    
    public void renderIcon(final float float1, final int integer) {
        Minecraft.getInstance().getTextureManager().bind(this.location);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, integer / 255.0f);
        GuiComponent.blit(2, 2, 12, 12, 8.0f, 8.0f, 8, 8, 64, 64);
        GuiComponent.blit(2, 2, 12, 12, 40.0f, 8.0f, 8, 8, 64, 64);
    }
    
    public boolean isEnabled() {
        return true;
    }
}
