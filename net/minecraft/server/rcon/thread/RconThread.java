package net.minecraft.server.rcon.thread;

import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import com.google.common.collect.Maps;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.ServerInterface;
import java.net.SocketAddress;
import java.util.Map;
import java.net.ServerSocket;

public class RconThread extends GenericThread {
    private final int port;
    private String serverIp;
    private ServerSocket socket;
    private final String rconPassword;
    private Map<SocketAddress, RconClient> clients;
    
    public RconThread(final ServerInterface ri) {
        super(ri, "RCON Listener");
        final DedicatedServerProperties ul3 = ri.getProperties();
        this.port = ul3.rconPort;
        this.rconPassword = ul3.rconPassword;
        this.serverIp = ri.getServerIp();
        if (this.serverIp.isEmpty()) {
            this.serverIp = "0.0.0.0";
        }
        this.initClients();
        this.socket = null;
    }
    
    private void initClients() {
        this.clients = (Map<SocketAddress, RconClient>)Maps.newHashMap();
    }
    
    private void clearClients() {
        final Iterator<Map.Entry<SocketAddress, RconClient>> iterator2 = (Iterator<Map.Entry<SocketAddress, RconClient>>)this.clients.entrySet().iterator();
        while (iterator2.hasNext()) {
            final Map.Entry<SocketAddress, RconClient> entry3 = (Map.Entry<SocketAddress, RconClient>)iterator2.next();
            if (!((RconClient)entry3.getValue()).isRunning()) {
                iterator2.remove();
            }
        }
    }
    
    public void run() {
        this.info("RCON running on " + this.serverIp + ":" + this.port);
        try {
            while (this.running) {
                try {
                    final Socket socket2 = this.socket.accept();
                    socket2.setSoTimeout(500);
                    final RconClient yl3 = new RconClient(this.serverInterface, this.rconPassword, socket2);
                    yl3.start();
                    this.clients.put(socket2.getRemoteSocketAddress(), yl3);
                    this.clearClients();
                }
                catch (SocketTimeoutException socketTimeoutException2) {
                    this.clearClients();
                }
                catch (IOException iOException2) {
                    if (!this.running) {
                        continue;
                    }
                    this.info("IO: " + iOException2.getMessage());
                }
            }
        }
        finally {
            this.closeSocket(this.socket);
        }
    }
    
    @Override
    public void start() {
        if (this.rconPassword.isEmpty()) {
            this.warn("No rcon password set in server.properties, rcon disabled!");
            return;
        }
        if (0 >= this.port || 65535 < this.port) {
            this.warn(new StringBuilder().append("Invalid rcon port ").append(this.port).append(" found in server.properties, rcon disabled!").toString());
            return;
        }
        if (this.running) {
            return;
        }
        try {
            (this.socket = new ServerSocket(this.port, 0, InetAddress.getByName(this.serverIp))).setSoTimeout(500);
            super.start();
        }
        catch (IOException iOException2) {
            this.warn("Unable to initialise rcon on " + this.serverIp + ":" + this.port + " : " + iOException2.getMessage());
        }
    }
    
    @Override
    public void stop() {
        super.stop();
        for (final Map.Entry<SocketAddress, RconClient> entry3 : this.clients.entrySet()) {
            ((RconClient)entry3.getValue()).stop();
        }
        this.closeSocket(this.socket);
        this.initClients();
    }
}
