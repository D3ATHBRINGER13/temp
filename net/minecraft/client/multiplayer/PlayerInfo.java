package net.minecraft.client.multiplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.PlayerTeam;
import javax.annotation.Nullable;
import com.google.common.base.MoreObjects;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import com.google.common.collect.Maps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import net.minecraft.resources.ResourceLocation;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import com.mojang.authlib.GameProfile;

public class PlayerInfo {
    private final GameProfile profile;
    private final Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;
    private GameType gameMode;
    private int latency;
    private boolean pendingTextures;
    private String skinModel;
    private Component tabListDisplayName;
    private int lastHealth;
    private int displayHealth;
    private long lastHealthTime;
    private long healthBlinkTime;
    private long renderVisibilityId;
    
    public PlayerInfo(final GameProfile gameProfile) {
        this.textureLocations = (Map<MinecraftProfileTexture.Type, ResourceLocation>)Maps.newEnumMap((Class)MinecraftProfileTexture.Type.class);
        this.profile = gameProfile;
    }
    
    public PlayerInfo(final ClientboundPlayerInfoPacket.PlayerUpdate b) {
        this.textureLocations = (Map<MinecraftProfileTexture.Type, ResourceLocation>)Maps.newEnumMap((Class)MinecraftProfileTexture.Type.class);
        this.profile = b.getProfile();
        this.gameMode = b.getGameMode();
        this.latency = b.getLatency();
        this.tabListDisplayName = b.getDisplayName();
    }
    
    public GameProfile getProfile() {
        return this.profile;
    }
    
    public GameType getGameMode() {
        return this.gameMode;
    }
    
    protected void setGameMode(final GameType bho) {
        this.gameMode = bho;
    }
    
    public int getLatency() {
        return this.latency;
    }
    
    protected void setLatency(final int integer) {
        this.latency = integer;
    }
    
    public boolean isSkinLoaded() {
        return this.getSkinLocation() != null;
    }
    
    public String getModelName() {
        if (this.skinModel == null) {
            return DefaultPlayerSkin.getSkinModelName(this.profile.getId());
        }
        return this.skinModel;
    }
    
    public ResourceLocation getSkinLocation() {
        this.registerTextures();
        return (ResourceLocation)MoreObjects.firstNonNull(this.textureLocations.get(MinecraftProfileTexture.Type.SKIN), DefaultPlayerSkin.getDefaultSkin(this.profile.getId()));
    }
    
    @Nullable
    public ResourceLocation getCapeLocation() {
        this.registerTextures();
        return (ResourceLocation)this.textureLocations.get(MinecraftProfileTexture.Type.CAPE);
    }
    
    @Nullable
    public ResourceLocation getElytraLocation() {
        this.registerTextures();
        return (ResourceLocation)this.textureLocations.get(MinecraftProfileTexture.Type.ELYTRA);
    }
    
    @Nullable
    public PlayerTeam getTeam() {
        return Minecraft.getInstance().level.getScoreboard().getPlayersTeam(this.getProfile().getName());
    }
    
    protected void registerTextures() {
        synchronized (this) {
            if (!this.pendingTextures) {
                this.pendingTextures = true;
                Minecraft.getInstance().getSkinManager().registerSkins(this.profile, (type, qv, minecraftProfileTexture) -> {
                    switch (type) {
                        case SKIN: {
                            this.textureLocations.put(MinecraftProfileTexture.Type.SKIN, qv);
                            this.skinModel = minecraftProfileTexture.getMetadata("model");
                            if (this.skinModel == null) {
                                this.skinModel = "default";
                                break;
                            }
                            else {
                                break;
                            }
                            break;
                        }
                        case CAPE: {
                            this.textureLocations.put(MinecraftProfileTexture.Type.CAPE, qv);
                            break;
                        }
                        case ELYTRA: {
                            this.textureLocations.put(MinecraftProfileTexture.Type.ELYTRA, qv);
                            break;
                        }
                    }
                }, true);
            }
        }
    }
    
    public void setTabListDisplayName(@Nullable final Component jo) {
        this.tabListDisplayName = jo;
    }
    
    @Nullable
    public Component getTabListDisplayName() {
        return this.tabListDisplayName;
    }
    
    public int getLastHealth() {
        return this.lastHealth;
    }
    
    public void setLastHealth(final int integer) {
        this.lastHealth = integer;
    }
    
    public int getDisplayHealth() {
        return this.displayHealth;
    }
    
    public void setDisplayHealth(final int integer) {
        this.displayHealth = integer;
    }
    
    public long getLastHealthTime() {
        return this.lastHealthTime;
    }
    
    public void setLastHealthTime(final long long1) {
        this.lastHealthTime = long1;
    }
    
    public long getHealthBlinkTime() {
        return this.healthBlinkTime;
    }
    
    public void setHealthBlinkTime(final long long1) {
        this.healthBlinkTime = long1;
    }
    
    public long getRenderVisibilityId() {
        return this.renderVisibilityId;
    }
    
    public void setRenderVisibilityId(final long long1) {
        this.renderVisibilityId = long1;
    }
}
