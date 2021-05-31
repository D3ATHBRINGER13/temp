package net.minecraft.client.gui.screens;

import org.apache.logging.log4j.LogManager;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.DefaultUncaughtExceptionHandler;
import java.net.UnknownHostException;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import java.net.InetAddress;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.Connection;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectScreen extends Screen {
    private static final AtomicInteger UNIQUE_THREAD_ID;
    private static final Logger LOGGER;
    private Connection connection;
    private boolean aborted;
    private final Screen parent;
    private Component status;
    private long lastNarration;
    
    public ConnectScreen(final Screen dcl, final Minecraft cyc, final ServerData dki) {
        super(NarratorChatListener.NO_TITLE);
        this.status = new TranslatableComponent("connect.connecting", new Object[0]);
        this.lastNarration = -1L;
        this.minecraft = cyc;
        this.parent = dcl;
        final ServerAddress dkh5 = ServerAddress.parseString(dki.ip);
        cyc.clearLevel();
        cyc.setCurrentServer(dki);
        this.connect(dkh5.getHost(), dkh5.getPort());
    }
    
    public ConnectScreen(final Screen dcl, final Minecraft cyc, final String string, final int integer) {
        super(NarratorChatListener.NO_TITLE);
        this.status = new TranslatableComponent("connect.connecting", new Object[0]);
        this.lastNarration = -1L;
        this.minecraft = cyc;
        this.parent = dcl;
        cyc.clearLevel();
        this.connect(string, integer);
    }
    
    private void connect(final String string, final int integer) {
        ConnectScreen.LOGGER.info("Connecting to {}, {}", string, integer);
        final Thread thread4 = new Thread(new StringBuilder().append("Server Connector #").append(ConnectScreen.UNIQUE_THREAD_ID.incrementAndGet()).toString()) {
            public void run() {
                InetAddress inetAddress2 = null;
                try {
                    if (ConnectScreen.this.aborted) {
                        return;
                    }
                    inetAddress2 = InetAddress.getByName(string);
                    ConnectScreen.this.connection = Connection.connectToServer(inetAddress2, integer, ConnectScreen.this.minecraft.options.useNativeTransport());
                    ConnectScreen.this.connection.setListener(new ClientHandshakePacketListenerImpl(ConnectScreen.this.connection, ConnectScreen.this.minecraft, ConnectScreen.this.parent, (Consumer<Component>)(jo -> dbo.updateStatus(jo))));
                    ConnectScreen.this.connection.send(new ClientIntentionPacket(string, integer, ConnectionProtocol.LOGIN));
                    ConnectScreen.this.connection.send(new ServerboundHelloPacket(ConnectScreen.this.minecraft.getUser().getGameProfile()));
                }
                catch (UnknownHostException unknownHostException3) {
                    if (ConnectScreen.this.aborted) {
                        return;
                    }
                    ConnectScreen.LOGGER.error("Couldn't connect to server", (Throwable)unknownHostException3);
                    ConnectScreen.this.minecraft.execute(() -> ConnectScreen.this.minecraft.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", (Component)new TranslatableComponent("disconnect.genericReason", new Object[] { "Unknown host" }))));
                }
                catch (Exception exception3) {
                    if (ConnectScreen.this.aborted) {
                        return;
                    }
                    ConnectScreen.LOGGER.error("Couldn't connect to server", (Throwable)exception3);
                    final String string4 = (inetAddress2 == null) ? exception3.toString() : exception3.toString().replaceAll(new StringBuilder().append(inetAddress2).append(":").append(integer).toString(), "");
                    ConnectScreen.this.minecraft.execute(() -> ConnectScreen.this.minecraft.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", (Component)new TranslatableComponent("disconnect.genericReason", new Object[] { string4 }))));
                }
            }
        };
        thread4.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(ConnectScreen.LOGGER));
        thread4.start();
    }
    
    private void updateStatus(final Component jo) {
        this.status = jo;
    }
    
    @Override
    public void tick() {
        if (this.connection != null) {
            if (this.connection.isConnected()) {
                this.connection.tick();
            }
            else {
                this.connection.handleDisconnection();
            }
        }
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    protected void init() {
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.get("gui.cancel"), czi -> {
            this.aborted = true;
            if (this.connection != null) {
                this.connection.disconnect(new TranslatableComponent("connect.aborted", new Object[0]));
            }
            this.minecraft.setScreen(this.parent);
        }));
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        final long long5 = Util.getMillis();
        if (long5 - this.lastNarration > 2000L) {
            this.lastNarration = long5;
            NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.joining", new Object[0]).getString());
        }
        this.drawCenteredString(this.font, this.status.getColoredString(), this.width / 2, this.height / 2 - 50, 16777215);
        super.render(integer1, integer2, float3);
    }
    
    static {
        UNIQUE_THREAD_ID = new AtomicInteger(0);
        LOGGER = LogManager.getLogger();
    }
}
