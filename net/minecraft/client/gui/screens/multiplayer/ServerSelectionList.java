package net.minecraft.client.gui.screens.multiplayer;

import java.net.UnknownHostException;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureObject;
import org.apache.commons.lang3.Validate;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import com.google.common.hash.Hashing;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.gui.Font;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import net.minecraft.DefaultUncaughtExceptionHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.components.AbstractSelectionList;
import java.util.Iterator;
import net.minecraft.client.server.LanServer;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class ServerSelectionList extends ObjectSelectionList<Entry> {
    private static final Logger LOGGER;
    private static final ThreadPoolExecutor THREAD_POOL;
    private static final ResourceLocation ICON_MISSING;
    private static final ResourceLocation ICON_OVERLAY_LOCATION;
    private final JoinMultiplayerScreen screen;
    private final List<OnlineServerEntry> onlineServers;
    private final Entry lanHeader;
    private final List<NetworkServerEntry> networkServers;
    
    public ServerSelectionList(final JoinMultiplayerScreen deq, final Minecraft cyc, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7) {
        super(cyc, integer3, integer4, integer5, integer6, integer7);
        this.onlineServers = (List<OnlineServerEntry>)Lists.newArrayList();
        this.lanHeader = new LANHeader();
        this.networkServers = (List<NetworkServerEntry>)Lists.newArrayList();
        this.screen = deq;
    }
    
    private void refreshEntries() {
        this.clearEntries();
        this.onlineServers.forEach(this::addEntry);
        this.addEntry(this.lanHeader);
        this.networkServers.forEach(this::addEntry);
    }
    
    @Override
    public void setSelected(final Entry a) {
        super.setSelected(a);
        if (this.getSelected() instanceof OnlineServerEntry) {
            NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", new Object[] { ((AbstractSelectionList<OnlineServerEntry>)this).getSelected().serverData.name }).getString());
        }
    }
    
    @Override
    protected void moveSelection(final int integer) {
        final int integer2 = this.children().indexOf(((AbstractSelectionList<Object>)this).getSelected());
        final int integer3 = Mth.clamp(integer2 + integer, 0, this.getItemCount() - 1);
        final Entry a5 = (Entry)this.children().get(integer3);
        super.setSelected(a5);
        if (!(a5 instanceof LANHeader)) {
            this.ensureVisible(a5);
            this.screen.onSelectedChange();
            return;
        }
        if (integer > 0 && integer3 == this.getItemCount() - 1) {
            return;
        }
        if (integer < 0 && integer3 == 0) {
            return;
        }
        this.moveSelection(integer);
    }
    
    public void updateOnlineServers(final ServerList dkj) {
        this.onlineServers.clear();
        for (int integer3 = 0; integer3 < dkj.size(); ++integer3) {
            this.onlineServers.add(new OnlineServerEntry(this.screen, dkj.get(integer3)));
        }
        this.refreshEntries();
    }
    
    public void updateNetworkServers(final List<LanServer> list) {
        this.networkServers.clear();
        for (final LanServer ead4 : list) {
            this.networkServers.add(new NetworkServerEntry(this.screen, ead4));
        }
        this.refreshEntries();
    }
    
    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 30;
    }
    
    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }
    
    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        THREAD_POOL = (ThreadPoolExecutor)new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(ServerSelectionList.LOGGER)).build());
        ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
        ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/server_selection.png");
    }
    
    public abstract static class Entry extends ObjectSelectionList.Entry<Entry> {
    }
    
    public static class LANHeader extends Entry {
        private final Minecraft minecraft;
        
        public LANHeader() {
            this.minecraft = Minecraft.getInstance();
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            final int n = integer2 + integer5 / 2;
            this.minecraft.font.getClass();
            final int integer8 = n - 9 / 2;
            this.minecraft.font.draw(I18n.get("lanServer.scanning"), (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(I18n.get("lanServer.scanning")) / 2), (float)integer8, 16777215);
            String string12 = null;
            switch ((int)(Util.getMillis() / 300L % 4L)) {
                default: {
                    string12 = "O o o";
                    break;
                }
                case 1:
                case 3: {
                    string12 = "o O o";
                    break;
                }
                case 2: {
                    string12 = "o o O";
                    break;
                }
            }
            final Font font = this.minecraft.font;
            final String string13 = string12;
            final float float10 = (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(string12) / 2);
            final int n2 = integer8;
            this.minecraft.font.getClass();
            font.draw(string13, float10, (float)(n2 + 9), 8421504);
        }
    }
    
    public static class NetworkServerEntry extends Entry {
        private final JoinMultiplayerScreen screen;
        protected final Minecraft minecraft;
        protected final LanServer serverData;
        private long lastClickTime;
        
        protected NetworkServerEntry(final JoinMultiplayerScreen deq, final LanServer ead) {
            this.screen = deq;
            this.serverData = ead;
            this.minecraft = Minecraft.getInstance();
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            this.minecraft.font.draw(I18n.get("lanServer.title"), (float)(integer3 + 32 + 3), (float)(integer2 + 1), 16777215);
            this.minecraft.font.draw(this.serverData.getMotd(), (float)(integer3 + 32 + 3), (float)(integer2 + 12), 8421504);
            if (this.minecraft.options.hideServerAddress) {
                this.minecraft.font.draw(I18n.get("selectServer.hiddenAddress"), (float)(integer3 + 32 + 3), (float)(integer2 + 12 + 11), 3158064);
            }
            else {
                this.minecraft.font.draw(this.serverData.getAddress(), (float)(integer3 + 32 + 3), (float)(integer2 + 12 + 11), 3158064);
            }
        }
        
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.screen.joinSelectedServer();
            }
            this.lastClickTime = Util.getMillis();
            return false;
        }
        
        public LanServer getServerData() {
            return this.serverData;
        }
    }
    
    public class OnlineServerEntry extends Entry {
        private final JoinMultiplayerScreen screen;
        private final Minecraft minecraft;
        private final ServerData serverData;
        private final ResourceLocation iconLocation;
        private String lastIconB64;
        private DynamicTexture icon;
        private long lastClickTime;
        
        protected OnlineServerEntry(final JoinMultiplayerScreen deq, final ServerData dki) {
            this.screen = deq;
            this.serverData = dki;
            this.minecraft = Minecraft.getInstance();
            this.iconLocation = new ResourceLocation(new StringBuilder().append("servers/").append(Hashing.sha1().hashUnencodedChars((CharSequence)dki.ip)).append("/icon").toString());
            this.icon = (DynamicTexture)this.minecraft.getTextureManager().getTexture(this.iconLocation);
        }
        
        @Override
        public void render(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final boolean boolean8, final float float9) {
            if (!this.serverData.pinged) {
                this.serverData.pinged = true;
                this.serverData.ping = -2L;
                this.serverData.motd = "";
                this.serverData.status = "";
                ServerSelectionList.THREAD_POOL.submit(() -> {
                    try {
                        this.screen.getPinger().pingServer(this.serverData);
                    }
                    catch (UnknownHostException unknownHostException2) {
                        this.serverData.ping = -1L;
                        this.serverData.motd = ChatFormatting.DARK_RED + I18n.get("multiplayer.status.cannot_resolve");
                    }
                    catch (Exception exception2) {
                        this.serverData.ping = -1L;
                        this.serverData.motd = ChatFormatting.DARK_RED + I18n.get("multiplayer.status.cannot_connect");
                    }
                });
            }
            final boolean boolean9 = this.serverData.protocol > SharedConstants.getCurrentVersion().getProtocolVersion();
            final boolean boolean10 = this.serverData.protocol < SharedConstants.getCurrentVersion().getProtocolVersion();
            final boolean boolean11 = boolean9 || boolean10;
            this.minecraft.font.draw(this.serverData.name, (float)(integer3 + 32 + 3), (float)(integer2 + 1), 16777215);
            final List<String> list14 = this.minecraft.font.split(this.serverData.motd, integer4 - 32 - 2);
            for (int integer8 = 0; integer8 < Math.min(list14.size(), 2); ++integer8) {
                final Font font = this.minecraft.font;
                final String string18 = (String)list14.get(integer8);
                final float float10 = (float)(integer3 + 32 + 3);
                final int n = integer2 + 12;
                this.minecraft.font.getClass();
                font.draw(string18, float10, (float)(n + 9 * integer8), 8421504);
            }
            final String string15 = boolean11 ? (ChatFormatting.DARK_RED + this.serverData.version) : this.serverData.status;
            final int integer9 = this.minecraft.font.width(string15);
            this.minecraft.font.draw(string15, (float)(integer3 + integer4 - integer9 - 15 - 2), (float)(integer2 + 1), 8421504);
            int integer10 = 0;
            String string16 = null;
            int integer11;
            String string17;
            if (boolean11) {
                integer11 = 5;
                string17 = I18n.get(boolean9 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
                string16 = this.serverData.playerList;
            }
            else if (this.serverData.pinged && this.serverData.ping != -2L) {
                if (this.serverData.ping < 0L) {
                    integer11 = 5;
                }
                else if (this.serverData.ping < 150L) {
                    integer11 = 0;
                }
                else if (this.serverData.ping < 300L) {
                    integer11 = 1;
                }
                else if (this.serverData.ping < 600L) {
                    integer11 = 2;
                }
                else if (this.serverData.ping < 1000L) {
                    integer11 = 3;
                }
                else {
                    integer11 = 4;
                }
                if (this.serverData.ping < 0L) {
                    string17 = I18n.get("multiplayer.status.no_connection");
                }
                else {
                    string17 = new StringBuilder().append(this.serverData.ping).append("ms").toString();
                    string16 = this.serverData.playerList;
                }
            }
            else {
                integer10 = 1;
                integer11 = (int)(Util.getMillis() / 100L + integer1 * 2 & 0x7L);
                if (integer11 > 4) {
                    integer11 = 8 - integer11;
                }
                string17 = I18n.get("multiplayer.status.pinging");
            }
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.minecraft.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
            GuiComponent.blit(integer3 + integer4 - 15, integer2, (float)(integer10 * 10), (float)(176 + integer11 * 8), 10, 8, 256, 256);
            if (this.serverData.getIconB64() != null && !this.serverData.getIconB64().equals(this.lastIconB64)) {
                this.lastIconB64 = this.serverData.getIconB64();
                this.loadServerIcon();
                this.screen.getServers().save();
            }
            if (this.icon != null) {
                this.drawIcon(integer3, integer2, this.iconLocation);
            }
            else {
                this.drawIcon(integer3, integer2, ServerSelectionList.ICON_MISSING);
            }
            final int integer12 = integer6 - integer3;
            final int integer13 = integer7 - integer2;
            if (integer12 >= integer4 - 15 && integer12 <= integer4 - 5 && integer13 >= 0 && integer13 <= 8) {
                this.screen.setToolTip(string17);
            }
            else if (integer12 >= integer4 - integer9 - 15 - 2 && integer12 <= integer4 - 15 - 2 && integer13 >= 0 && integer13 <= 8) {
                this.screen.setToolTip(string16);
            }
            if (this.minecraft.options.touchscreen || boolean8) {
                this.minecraft.getTextureManager().bind(ServerSelectionList.ICON_OVERLAY_LOCATION);
                GuiComponent.fill(integer3, integer2, integer3 + 32, integer2 + 32, -1601138544);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                final int integer14 = integer6 - integer3;
                final int integer15 = integer7 - integer2;
                if (this.canJoin()) {
                    if (integer14 < 32 && integer14 > 16) {
                        GuiComponent.blit(integer3, integer2, 0.0f, 32.0f, 32, 32, 256, 256);
                    }
                    else {
                        GuiComponent.blit(integer3, integer2, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (integer1 > 0) {
                    if (integer14 < 16 && integer15 < 16) {
                        GuiComponent.blit(integer3, integer2, 96.0f, 32.0f, 32, 32, 256, 256);
                    }
                    else {
                        GuiComponent.blit(integer3, integer2, 96.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (integer1 < this.screen.getServers().size() - 1) {
                    if (integer14 < 16 && integer15 > 16) {
                        GuiComponent.blit(integer3, integer2, 64.0f, 32.0f, 32, 32, 256, 256);
                    }
                    else {
                        GuiComponent.blit(integer3, integer2, 64.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
            }
        }
        
        protected void drawIcon(final int integer1, final int integer2, final ResourceLocation qv) {
            this.minecraft.getTextureManager().bind(qv);
            GlStateManager.enableBlend();
            GuiComponent.blit(integer1, integer2, 0.0f, 0.0f, 32, 32, 32, 32);
            GlStateManager.disableBlend();
        }
        
        private boolean canJoin() {
            return true;
        }
        
        private void loadServerIcon() {
            final String string2 = this.serverData.getIconB64();
            if (string2 == null) {
                this.minecraft.getTextureManager().release(this.iconLocation);
                if (this.icon != null && this.icon.getPixels() != null) {
                    this.icon.getPixels().close();
                }
                this.icon = null;
            }
            else {
                try {
                    final NativeImage cuj3 = NativeImage.fromBase64(string2);
                    Validate.validState(cuj3.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(cuj3.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    if (this.icon == null) {
                        this.icon = new DynamicTexture(cuj3);
                    }
                    else {
                        this.icon.setPixels(cuj3);
                        this.icon.upload();
                    }
                    this.minecraft.getTextureManager().register(this.iconLocation, this.icon);
                }
                catch (Throwable throwable3) {
                    ServerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.serverData.name, this.serverData.ip, throwable3);
                    this.serverData.setIconB64(null);
                }
            }
        }
        
        public boolean mouseClicked(final double double1, final double double2, final int integer) {
            final double double3 = double1 - AbstractSelectionList.this.getRowLeft();
            final double double4 = double2 - AbstractSelectionList.this.getRowTop(ServerSelectionList.this.children().indexOf(this));
            if (double3 <= 32.0) {
                if (double3 < 32.0 && double3 > 16.0 && this.canJoin()) {
                    this.screen.setSelected(this);
                    this.screen.joinSelectedServer();
                    return true;
                }
                final int integer2 = this.screen.serverSelectionList.children().indexOf(this);
                if (double3 < 16.0 && double4 < 16.0 && integer2 > 0) {
                    final int integer3 = Screen.hasShiftDown() ? 0 : (integer2 - 1);
                    this.screen.getServers().swap(integer2, integer3);
                    if (this.screen.serverSelectionList.getSelected() == this) {
                        this.screen.setSelected(this);
                    }
                    this.screen.serverSelectionList.updateOnlineServers(this.screen.getServers());
                    return true;
                }
                if (double3 < 16.0 && double4 > 16.0 && integer2 < this.screen.getServers().size() - 1) {
                    final ServerList dkj12 = this.screen.getServers();
                    final int integer4 = Screen.hasShiftDown() ? (dkj12.size() - 1) : (integer2 + 1);
                    dkj12.swap(integer2, integer4);
                    if (this.screen.serverSelectionList.getSelected() == this) {
                        this.screen.setSelected(this);
                    }
                    this.screen.serverSelectionList.updateOnlineServers(dkj12);
                    return true;
                }
            }
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.screen.joinSelectedServer();
            }
            this.lastClickTime = Util.getMillis();
            return false;
        }
        
        public ServerData getServerData() {
            return this.serverData;
        }
    }
}
