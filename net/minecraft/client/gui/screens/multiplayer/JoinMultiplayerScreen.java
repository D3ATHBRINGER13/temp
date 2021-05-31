package net.minecraft.client.gui.screens.multiplayer;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.screens.ConnectScreen;
import com.google.common.collect.Lists;
import com.google.common.base.Splitter;
import net.minecraft.client.server.LanServer;
import java.util.List;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.EditServerScreen;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.server.LanServerDetection;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.gui.screens.Screen;

public class JoinMultiplayerScreen extends Screen {
    private static final Logger LOGGER;
    private final ServerStatusPinger pinger;
    private final Screen lastScreen;
    protected ServerSelectionList serverSelectionList;
    private ServerList servers;
    private Button editButton;
    private Button selectButton;
    private Button deleteButton;
    private String toolTip;
    private ServerData editingServer;
    private LanServerDetection.LanServerList lanServerList;
    private LanServerDetection.LanServerDetector lanServerDetector;
    private boolean initedOnce;
    
    public JoinMultiplayerScreen(final Screen dcl) {
        super(new TranslatableComponent("multiplayer.title", new Object[0]));
        this.pinger = new ServerStatusPinger();
        this.lastScreen = dcl;
    }
    
    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        if (this.initedOnce) {
            this.serverSelectionList.updateSize(this.width, this.height, 32, this.height - 64);
        }
        else {
            this.initedOnce = true;
            (this.servers = new ServerList(this.minecraft)).load();
            this.lanServerList = new LanServerDetection.LanServerList();
            try {
                (this.lanServerDetector = new LanServerDetection.LanServerDetector(this.lanServerList)).start();
            }
            catch (Exception exception2) {
                JoinMultiplayerScreen.LOGGER.warn("Unable to start LAN server detection: {}", exception2.getMessage());
            }
            (this.serverSelectionList = new ServerSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36)).updateOnlineServers(this.servers);
        }
        this.children.add(this.serverSelectionList);
        this.selectButton = this.<Button>addButton(new Button(this.width / 2 - 154, this.height - 52, 100, 20, I18n.get("selectServer.select"), czi -> this.joinSelectedServer()));
        this.<Button>addButton(new Button(this.width / 2 - 50, this.height - 52, 100, 20, I18n.get("selectServer.direct"), czi -> {
            this.editingServer = new ServerData(I18n.get("selectServer.defaultName"), "", false);
            this.minecraft.setScreen(new DirectJoinServerScreen(this::directJoinCallback, this.editingServer));
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.get("selectServer.add"), czi -> {
            this.editingServer = new ServerData(I18n.get("selectServer.defaultName"), "", false);
            this.minecraft.setScreen(new EditServerScreen(this::addServerCallback, this.editingServer));
            return;
        }));
        final ServerSelectionList.Entry a3;
        ServerData dki4;
        this.editButton = this.<Button>addButton(new Button(this.width / 2 - 154, this.height - 28, 70, 20, I18n.get("selectServer.edit"), czi -> {
            a3 = this.serverSelectionList.getSelected();
            if (a3 instanceof ServerSelectionList.OnlineServerEntry) {
                dki4 = ((ServerSelectionList.OnlineServerEntry)a3).getServerData();
                (this.editingServer = new ServerData(dki4.name, dki4.ip, false)).copyFrom(dki4);
                this.minecraft.setScreen(new EditServerScreen(this::editServerCallback, this.editingServer));
            }
            return;
        }));
        final ServerSelectionList.Entry a4;
        String string4;
        Component jo5;
        final TranslatableComponent translatableComponent;
        Component jo6;
        String string5;
        String string6;
        this.deleteButton = this.<Button>addButton(new Button(this.width / 2 - 74, this.height - 28, 70, 20, I18n.get("selectServer.delete"), czi -> {
            a4 = this.serverSelectionList.getSelected();
            if (a4 instanceof ServerSelectionList.OnlineServerEntry) {
                string4 = ((ServerSelectionList.OnlineServerEntry)a4).getServerData().name;
                if (string4 != null) {
                    jo5 = new TranslatableComponent("selectServer.deleteQuestion", new Object[0]);
                    new TranslatableComponent("selectServer.deleteWarning", new Object[] { string4 });
                    jo6 = translatableComponent;
                    string5 = I18n.get("selectServer.deleteButton");
                    string6 = I18n.get("gui.cancel");
                    this.minecraft.setScreen(new ConfirmScreen(this::deleteCallback, jo5, jo6, string5, string6));
                }
            }
            return;
        }));
        this.<Button>addButton(new Button(this.width / 2 + 4, this.height - 28, 70, 20, I18n.get("selectServer.refresh"), czi -> this.refreshServerList()));
        this.<Button>addButton(new Button(this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.get("gui.cancel"), czi -> this.minecraft.setScreen(this.lastScreen)));
        this.onSelectedChange();
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.lanServerList.isDirty()) {
            final List<LanServer> list2 = this.lanServerList.getServers();
            this.lanServerList.markClean();
            this.serverSelectionList.updateNetworkServers(list2);
        }
        this.pinger.tick();
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }
        this.pinger.removeAll();
    }
    
    private void refreshServerList() {
        this.minecraft.setScreen(new JoinMultiplayerScreen(this.lastScreen));
    }
    
    private void deleteCallback(final boolean boolean1) {
        final ServerSelectionList.Entry a3 = this.serverSelectionList.getSelected();
        if (boolean1 && a3 instanceof ServerSelectionList.OnlineServerEntry) {
            this.servers.remove(((ServerSelectionList.OnlineServerEntry)a3).getServerData());
            this.servers.save();
            this.serverSelectionList.setSelected(null);
            this.serverSelectionList.updateOnlineServers(this.servers);
        }
        this.minecraft.setScreen(this);
    }
    
    private void editServerCallback(final boolean boolean1) {
        final ServerSelectionList.Entry a3 = this.serverSelectionList.getSelected();
        if (boolean1 && a3 instanceof ServerSelectionList.OnlineServerEntry) {
            final ServerData dki4 = ((ServerSelectionList.OnlineServerEntry)a3).getServerData();
            dki4.name = this.editingServer.name;
            dki4.ip = this.editingServer.ip;
            dki4.copyFrom(this.editingServer);
            this.servers.save();
            this.serverSelectionList.updateOnlineServers(this.servers);
        }
        this.minecraft.setScreen(this);
    }
    
    private void addServerCallback(final boolean boolean1) {
        if (boolean1) {
            this.servers.add(this.editingServer);
            this.servers.save();
            this.serverSelectionList.setSelected(null);
            this.serverSelectionList.updateOnlineServers(this.servers);
        }
        this.minecraft.setScreen(this);
    }
    
    private void directJoinCallback(final boolean boolean1) {
        if (boolean1) {
            this.join(this.editingServer);
        }
        else {
            this.minecraft.setScreen(this);
        }
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (super.keyPressed(integer1, integer2, integer3)) {
            return true;
        }
        if (integer1 == 294) {
            this.refreshServerList();
            return true;
        }
        if (this.serverSelectionList.getSelected() != null && (integer1 == 257 || integer1 == 335)) {
            this.joinSelectedServer();
            return true;
        }
        return false;
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.toolTip = null;
        this.renderBackground();
        this.serverSelectionList.render(integer1, integer2, float3);
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
        super.render(integer1, integer2, float3);
        if (this.toolTip != null) {
            this.renderTooltip((List<String>)Lists.newArrayList(Splitter.on("\n").split((CharSequence)this.toolTip)), integer1, integer2);
        }
    }
    
    public void joinSelectedServer() {
        final ServerSelectionList.Entry a2 = this.serverSelectionList.getSelected();
        if (a2 instanceof ServerSelectionList.OnlineServerEntry) {
            this.join(((ServerSelectionList.OnlineServerEntry)a2).getServerData());
        }
        else if (a2 instanceof ServerSelectionList.NetworkServerEntry) {
            final LanServer ead3 = ((ServerSelectionList.NetworkServerEntry)a2).getServerData();
            this.join(new ServerData(ead3.getMotd(), ead3.getAddress(), true));
        }
    }
    
    private void join(final ServerData dki) {
        this.minecraft.setScreen(new ConnectScreen(this, this.minecraft, dki));
    }
    
    public void setSelected(final ServerSelectionList.Entry a) {
        this.serverSelectionList.setSelected(a);
        this.onSelectedChange();
    }
    
    protected void onSelectedChange() {
        this.selectButton.active = false;
        this.editButton.active = false;
        this.deleteButton.active = false;
        final ServerSelectionList.Entry a2 = this.serverSelectionList.getSelected();
        if (a2 != null && !(a2 instanceof ServerSelectionList.LANHeader)) {
            this.selectButton.active = true;
            if (a2 instanceof ServerSelectionList.OnlineServerEntry) {
                this.editButton.active = true;
                this.deleteButton.active = true;
            }
        }
    }
    
    public ServerStatusPinger getPinger() {
        return this.pinger;
    }
    
    public void setToolTip(final String string) {
        this.toolTip = string;
    }
    
    public ServerList getServers() {
        return this.servers;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
