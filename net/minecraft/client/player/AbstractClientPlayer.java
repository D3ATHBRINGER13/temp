package net.minecraft.client.player;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import com.google.common.hash.Hashing;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.HttpTextureProcessor;
import java.io.File;
import net.minecraft.client.renderer.MobSkinTextureProcessor;
import net.minecraft.util.StringUtil;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;
import net.minecraft.world.level.GameType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractClientPlayer extends Player {
    private PlayerInfo playerInfo;
    public float elytraRotX;
    public float elytraRotY;
    public float elytraRotZ;
    public final MultiPlayerLevel clientLevel;
    
    public AbstractClientPlayer(final MultiPlayerLevel dkf, final GameProfile gameProfile) {
        super(dkf, gameProfile);
        this.clientLevel = dkf;
    }
    
    @Override
    public boolean isSpectator() {
        final PlayerInfo dkg2 = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
        return dkg2 != null && dkg2.getGameMode() == GameType.SPECTATOR;
    }
    
    @Override
    public boolean isCreative() {
        final PlayerInfo dkg2 = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
        return dkg2 != null && dkg2.getGameMode() == GameType.CREATIVE;
    }
    
    public boolean isCapeLoaded() {
        return this.getPlayerInfo() != null;
    }
    
    @Nullable
    protected PlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUUID());
        }
        return this.playerInfo;
    }
    
    public boolean isSkinLoaded() {
        final PlayerInfo dkg2 = this.getPlayerInfo();
        return dkg2 != null && dkg2.isSkinLoaded();
    }
    
    public ResourceLocation getSkinTextureLocation() {
        final PlayerInfo dkg2 = this.getPlayerInfo();
        return (dkg2 == null) ? DefaultPlayerSkin.getDefaultSkin(this.getUUID()) : dkg2.getSkinLocation();
    }
    
    @Nullable
    public ResourceLocation getCloakTextureLocation() {
        final PlayerInfo dkg2 = this.getPlayerInfo();
        return (dkg2 == null) ? null : dkg2.getCapeLocation();
    }
    
    public boolean isElytraLoaded() {
        return this.getPlayerInfo() != null;
    }
    
    @Nullable
    public ResourceLocation getElytraTextureLocation() {
        final PlayerInfo dkg2 = this.getPlayerInfo();
        return (dkg2 == null) ? null : dkg2.getElytraLocation();
    }
    
    public static HttpTexture registerSkinTexture(final ResourceLocation qv, final String string) {
        final TextureManager dxc3 = Minecraft.getInstance().getTextureManager();
        TextureObject dxd4 = dxc3.getTexture(qv);
        if (dxd4 == null) {
            dxd4 = new HttpTexture(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] { StringUtil.stripColor(string) }), DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(string)), new MobSkinTextureProcessor());
            dxc3.register(qv, dxd4);
        }
        return (HttpTexture)dxd4;
    }
    
    public static ResourceLocation getSkinLocation(final String string) {
        return new ResourceLocation(new StringBuilder().append("skins/").append(Hashing.sha1().hashUnencodedChars((CharSequence)StringUtil.stripColor(string))).toString());
    }
    
    public String getModelName() {
        final PlayerInfo dkg2 = this.getPlayerInfo();
        return (dkg2 == null) ? DefaultPlayerSkin.getSkinModelName(this.getUUID()) : dkg2.getModelName();
    }
    
    public float getFieldOfViewModifier() {
        float float2 = 1.0f;
        if (this.abilities.flying) {
            float2 *= 1.1f;
        }
        final AttributeInstance ajo3 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        float2 *= (float)((ajo3.getValue() / this.abilities.getWalkingSpeed() + 1.0) / 2.0);
        if (this.abilities.getWalkingSpeed() == 0.0f || Float.isNaN(float2) || Float.isInfinite(float2)) {
            float2 = 1.0f;
        }
        if (this.isUsingItem() && this.getUseItem().getItem() == Items.BOW) {
            final int integer4 = this.getTicksUsingItem();
            float float3 = integer4 / 20.0f;
            if (float3 > 1.0f) {
                float3 = 1.0f;
            }
            else {
                float3 *= float3;
            }
            float2 *= 1.0f - float3 * 0.15f;
        }
        return float2;
    }
}
