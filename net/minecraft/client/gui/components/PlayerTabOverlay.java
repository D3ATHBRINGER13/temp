package net.minecraft.client.gui.components;

import com.google.common.collect.ComparisonChain;
import java.util.Comparator;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.PlayerModelPart;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import javax.annotation.Nullable;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.Util;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import com.google.common.collect.Ordering;
import net.minecraft.client.gui.GuiComponent;

public class PlayerTabOverlay extends GuiComponent {
    private static final Ordering<PlayerInfo> PLAYER_ORDERING;
    private final Minecraft minecraft;
    private final Gui gui;
    private Component footer;
    private Component header;
    private long visibilityId;
    private boolean visible;
    
    public PlayerTabOverlay(final Minecraft cyc, final Gui cyv) {
        this.minecraft = cyc;
        this.gui = cyv;
    }
    
    public Component getNameForDisplay(final PlayerInfo dkg) {
        if (dkg.getTabListDisplayName() != null) {
            return dkg.getTabListDisplayName();
        }
        return PlayerTeam.formatNameForTeam(dkg.getTeam(), new TextComponent(dkg.getProfile().getName()));
    }
    
    public void setVisible(final boolean boolean1) {
        if (boolean1 && !this.visible) {
            this.visibilityId = Util.getMillis();
        }
        this.visible = boolean1;
    }
    
    public void render(final int integer, final Scoreboard cti, @Nullable final Objective ctf) {
        final ClientPacketListener dkc5 = this.minecraft.player.connection;
        List<PlayerInfo> list6 = (List<PlayerInfo>)PlayerTabOverlay.PLAYER_ORDERING.sortedCopy((Iterable)dkc5.getOnlinePlayers());
        int integer2 = 0;
        int integer3 = 0;
        for (final PlayerInfo dkg10 : list6) {
            int integer4 = this.minecraft.font.width(this.getNameForDisplay(dkg10).getColoredString());
            integer2 = Math.max(integer2, integer4);
            if (ctf != null && ctf.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
                integer4 = this.minecraft.font.width(new StringBuilder().append(" ").append(cti.getOrCreatePlayerScore(dkg10.getProfile().getName(), ctf).getScore()).toString());
                integer3 = Math.max(integer3, integer4);
            }
        }
        list6 = (List<PlayerInfo>)list6.subList(0, Math.min(list6.size(), 80));
        int integer4;
        int integer6;
        int integer5;
        for (integer5 = (integer6 = list6.size()), integer4 = 1; integer6 > 20; integer6 = (integer5 + integer4 - 1) / integer4) {
            ++integer4;
        }
        final boolean boolean12 = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
        int integer7;
        if (ctf != null) {
            if (ctf.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
                integer7 = 90;
            }
            else {
                integer7 = integer3;
            }
        }
        else {
            integer7 = 0;
        }
        final int integer8 = Math.min(integer4 * ((boolean12 ? 9 : 0) + integer2 + integer7 + 13), integer - 50) / integer4;
        final int integer9 = integer / 2 - (integer8 * integer4 + (integer4 - 1) * 5) / 2;
        int integer10 = 10;
        int integer11 = integer8 * integer4 + (integer4 - 1) * 5;
        List<String> list7 = null;
        if (this.header != null) {
            list7 = this.minecraft.font.split(this.header.getColoredString(), integer - 50);
            for (final String string20 : list7) {
                integer11 = Math.max(integer11, this.minecraft.font.width(string20));
            }
        }
        List<String> list8 = null;
        if (this.footer != null) {
            list8 = this.minecraft.font.split(this.footer.getColoredString(), integer - 50);
            for (final String string21 : list8) {
                integer11 = Math.max(integer11, this.minecraft.font.width(string21));
            }
        }
        if (list7 != null) {
            final int integer23 = integer / 2 - integer11 / 2 - 1;
            final int integer24 = integer10 - 1;
            final int integer25 = integer / 2 + integer11 / 2 + 1;
            final int n = integer10;
            final int size = list7.size();
            this.minecraft.font.getClass();
            GuiComponent.fill(integer23, integer24, integer25, n + size * 9, Integer.MIN_VALUE);
            for (final String string21 : list7) {
                final int integer12 = this.minecraft.font.width(string21);
                this.minecraft.font.drawShadow(string21, (float)(integer / 2 - integer12 / 2), (float)integer10, -1);
                final int n2 = integer10;
                this.minecraft.font.getClass();
                integer10 = n2 + 9;
            }
            ++integer10;
        }
        GuiComponent.fill(integer / 2 - integer11 / 2 - 1, integer10 - 1, integer / 2 + integer11 / 2 + 1, integer10 + integer6 * 9, Integer.MIN_VALUE);
        final int integer13 = this.minecraft.options.getBackgroundColor(553648127);
        for (int integer14 = 0; integer14 < integer5; ++integer14) {
            final int integer12 = integer14 / integer6;
            final int integer15 = integer14 % integer6;
            int integer16 = integer9 + integer12 * integer8 + integer12 * 5;
            final int integer17 = integer10 + integer15 * 9;
            GuiComponent.fill(integer16, integer17, integer16 + integer8, integer17 + 8, integer13);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableAlphaTest();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            if (integer14 < list6.size()) {
                final PlayerInfo dkg11 = (PlayerInfo)list6.get(integer14);
                final GameProfile gameProfile27 = dkg11.getProfile();
                if (boolean12) {
                    final Player awg28 = this.minecraft.level.getPlayerByUUID(gameProfile27.getId());
                    final boolean boolean13 = awg28 != null && awg28.isModelPartShown(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameProfile27.getName()) || "Grumm".equals(gameProfile27.getName()));
                    this.minecraft.getTextureManager().bind(dkg11.getSkinLocation());
                    final int integer18 = 8 + (boolean13 ? 8 : 0);
                    final int integer19 = 8 * (boolean13 ? -1 : 1);
                    GuiComponent.blit(integer16, integer17, 8, 8, 8.0f, (float)integer18, 8, integer19, 64, 64);
                    if (awg28 != null && awg28.isModelPartShown(PlayerModelPart.HAT)) {
                        final int integer20 = 8 + (boolean13 ? 8 : 0);
                        final int integer21 = 8 * (boolean13 ? -1 : 1);
                        GuiComponent.blit(integer16, integer17, 8, 8, 40.0f, (float)integer20, 8, integer21, 64, 64);
                    }
                    integer16 += 9;
                }
                final String string22 = this.getNameForDisplay(dkg11).getColoredString();
                if (dkg11.getGameMode() == GameType.SPECTATOR) {
                    this.minecraft.font.drawShadow(ChatFormatting.ITALIC + string22, (float)integer16, (float)integer17, -1862270977);
                }
                else {
                    this.minecraft.font.drawShadow(string22, (float)integer16, (float)integer17, -1);
                }
                if (ctf != null && dkg11.getGameMode() != GameType.SPECTATOR) {
                    final int integer22 = integer16 + integer2 + 1;
                    final int integer18 = integer22 + integer7;
                    if (integer18 - integer22 > 5) {
                        this.renderTablistScore(ctf, integer17, gameProfile27.getName(), integer22, integer18, dkg11);
                    }
                }
                this.renderPingIcon(integer8, integer16 - (boolean12 ? 9 : 0), integer17, dkg11);
            }
        }
        if (list8 != null) {
            integer10 += integer6 * 9 + 1;
            final int integer26 = integer / 2 - integer11 / 2 - 1;
            final int integer27 = integer10 - 1;
            final int integer28 = integer / 2 + integer11 / 2 + 1;
            final int n3 = integer10;
            final int size2 = list8.size();
            this.minecraft.font.getClass();
            GuiComponent.fill(integer26, integer27, integer28, n3 + size2 * 9, Integer.MIN_VALUE);
            for (final String string23 : list8) {
                final int integer15 = this.minecraft.font.width(string23);
                this.minecraft.font.drawShadow(string23, (float)(integer / 2 - integer15 / 2), (float)integer10, -1);
                final int n4 = integer10;
                this.minecraft.font.getClass();
                integer10 = n4 + 9;
            }
        }
    }
    
    protected void renderPingIcon(final int integer1, final int integer2, final int integer3, final PlayerInfo dkg) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(PlayerTabOverlay.GUI_ICONS_LOCATION);
        final int integer4 = 0;
        int integer5;
        if (dkg.getLatency() < 0) {
            integer5 = 5;
        }
        else if (dkg.getLatency() < 150) {
            integer5 = 0;
        }
        else if (dkg.getLatency() < 300) {
            integer5 = 1;
        }
        else if (dkg.getLatency() < 600) {
            integer5 = 2;
        }
        else if (dkg.getLatency() < 1000) {
            integer5 = 3;
        }
        else {
            integer5 = 4;
        }
        this.blitOffset += 100;
        this.blit(integer2 + integer1 - 11, integer3, 0, 176 + integer5 * 8, 10, 8);
        this.blitOffset -= 100;
    }
    
    private void renderTablistScore(final Objective ctf, final int integer2, final String string, final int integer4, final int integer5, final PlayerInfo dkg) {
        final int integer6 = ctf.getScoreboard().getOrCreatePlayerScore(string, ctf).getScore();
        if (ctf.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
            this.minecraft.getTextureManager().bind(PlayerTabOverlay.GUI_ICONS_LOCATION);
            final long long9 = Util.getMillis();
            if (this.visibilityId == dkg.getRenderVisibilityId()) {
                if (integer6 < dkg.getLastHealth()) {
                    dkg.setLastHealthTime(long9);
                    dkg.setHealthBlinkTime(this.gui.getGuiTicks() + 20);
                }
                else if (integer6 > dkg.getLastHealth()) {
                    dkg.setLastHealthTime(long9);
                    dkg.setHealthBlinkTime(this.gui.getGuiTicks() + 10);
                }
            }
            if (long9 - dkg.getLastHealthTime() > 1000L || this.visibilityId != dkg.getRenderVisibilityId()) {
                dkg.setLastHealth(integer6);
                dkg.setDisplayHealth(integer6);
                dkg.setLastHealthTime(long9);
            }
            dkg.setRenderVisibilityId(this.visibilityId);
            dkg.setLastHealth(integer6);
            final int integer7 = Mth.ceil(Math.max(integer6, dkg.getDisplayHealth()) / 2.0f);
            final int integer8 = Math.max(Mth.ceil((float)(integer6 / 2)), Math.max(Mth.ceil((float)(dkg.getDisplayHealth() / 2)), 10));
            final boolean boolean13 = dkg.getHealthBlinkTime() > this.gui.getGuiTicks() && (dkg.getHealthBlinkTime() - this.gui.getGuiTicks()) / 3L % 2L == 1L;
            if (integer7 > 0) {
                final int integer9 = Mth.floor(Math.min((integer5 - integer4 - 4) / (float)integer8, 9.0f));
                if (integer9 > 3) {
                    for (int integer10 = integer7; integer10 < integer8; ++integer10) {
                        this.blit(integer4 + integer10 * integer9, integer2, boolean13 ? 25 : 16, 0, 9, 9);
                    }
                    for (int integer10 = 0; integer10 < integer7; ++integer10) {
                        this.blit(integer4 + integer10 * integer9, integer2, boolean13 ? 25 : 16, 0, 9, 9);
                        if (boolean13) {
                            if (integer10 * 2 + 1 < dkg.getDisplayHealth()) {
                                this.blit(integer4 + integer10 * integer9, integer2, 70, 0, 9, 9);
                            }
                            if (integer10 * 2 + 1 == dkg.getDisplayHealth()) {
                                this.blit(integer4 + integer10 * integer9, integer2, 79, 0, 9, 9);
                            }
                        }
                        if (integer10 * 2 + 1 < integer6) {
                            this.blit(integer4 + integer10 * integer9, integer2, (integer10 >= 10) ? 160 : 52, 0, 9, 9);
                        }
                        if (integer10 * 2 + 1 == integer6) {
                            this.blit(integer4 + integer10 * integer9, integer2, (integer10 >= 10) ? 169 : 61, 0, 9, 9);
                        }
                    }
                }
                else {
                    final float float15 = Mth.clamp(integer6 / 20.0f, 0.0f, 1.0f);
                    final int integer11 = (int)((1.0f - float15) * 255.0f) << 16 | (int)(float15 * 255.0f) << 8;
                    String string2 = new StringBuilder().append("").append(integer6 / 2.0f).toString();
                    if (integer5 - this.minecraft.font.width(string2 + "hp") >= integer4) {
                        string2 += "hp";
                    }
                    this.minecraft.font.drawShadow(string2, (float)((integer5 + integer4) / 2 - this.minecraft.font.width(string2) / 2), (float)integer2, integer11);
                }
            }
        }
        else {
            final String string3 = new StringBuilder().append(ChatFormatting.YELLOW).append("").append(integer6).toString();
            this.minecraft.font.drawShadow(string3, (float)(integer5 - this.minecraft.font.width(string3)), (float)integer2, 16777215);
        }
    }
    
    public void setFooter(@Nullable final Component jo) {
        this.footer = jo;
    }
    
    public void setHeader(@Nullable final Component jo) {
        this.header = jo;
    }
    
    public void reset() {
        this.header = null;
        this.footer = null;
    }
    
    static {
        PLAYER_ORDERING = Ordering.from((Comparator)new PlayerInfoComparator());
    }
    
    static class PlayerInfoComparator implements Comparator<PlayerInfo> {
        private PlayerInfoComparator() {
        }
        
        public int compare(final PlayerInfo dkg1, final PlayerInfo dkg2) {
            final PlayerTeam ctg4 = dkg1.getTeam();
            final PlayerTeam ctg5 = dkg2.getTeam();
            return ComparisonChain.start().compareTrueFirst(dkg1.getGameMode() != GameType.SPECTATOR, dkg2.getGameMode() != GameType.SPECTATOR).compare((ctg4 != null) ? ctg4.getName() : "", (ctg5 != null) ? ctg5.getName() : "").compare(dkg1.getProfile().getName(), dkg2.getProfile().getName(), String::compareToIgnoreCase).result();
        }
    }
}
