package net.minecraft.server.rcon.thread;

import java.util.Random;
import java.nio.charset.StandardCharsets;
import java.net.SocketException;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import net.minecraft.Util;
import net.minecraft.server.rcon.PktUtils;
import java.io.IOException;
import java.util.Date;
import com.google.common.collect.Maps;
import java.net.UnknownHostException;
import java.net.InetAddress;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.rcon.NetworkDataOutputStream;
import java.net.SocketAddress;
import java.util.Map;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class QueryThreadGs4 extends GenericThread {
    private long lastChallengeCheck;
    private final int port;
    private final int serverPort;
    private final int maxPlayers;
    private final String serverName;
    private final String worldName;
    private DatagramSocket socket;
    private final byte[] buffer;
    private DatagramPacket request;
    private final Map<SocketAddress, String> idents;
    private String hostIp;
    private String serverIp;
    private final Map<SocketAddress, RequestChallenge> validChallenges;
    private final long lastChallengeClean;
    private final NetworkDataOutputStream rulesResponse;
    private long lastRulesResponse;
    
    public QueryThreadGs4(final ServerInterface ri) {
        super(ri, "Query Listener");
        this.buffer = new byte[1460];
        this.port = ri.getProperties().queryPort;
        this.serverIp = ri.getServerIp();
        this.serverPort = ri.getServerPort();
        this.serverName = ri.getServerName();
        this.maxPlayers = ri.getMaxPlayers();
        this.worldName = ri.getLevelIdName();
        this.lastRulesResponse = 0L;
        this.hostIp = "0.0.0.0";
        if (this.serverIp.isEmpty() || this.hostIp.equals(this.serverIp)) {
            this.serverIp = "0.0.0.0";
            try {
                final InetAddress inetAddress3 = InetAddress.getLocalHost();
                this.hostIp = inetAddress3.getHostAddress();
            }
            catch (UnknownHostException unknownHostException3) {
                this.warn("Unable to determine local host IP, please set server-ip in server.properties: " + unknownHostException3.getMessage());
            }
        }
        else {
            this.hostIp = this.serverIp;
        }
        this.idents = (Map<SocketAddress, String>)Maps.newHashMap();
        this.rulesResponse = new NetworkDataOutputStream(1460);
        this.validChallenges = (Map<SocketAddress, RequestChallenge>)Maps.newHashMap();
        this.lastChallengeClean = new Date().getTime();
    }
    
    private void sendTo(final byte[] arr, final DatagramPacket datagramPacket) throws IOException {
        this.socket.send(new DatagramPacket(arr, arr.length, datagramPacket.getSocketAddress()));
    }
    
    private boolean processPacket(final DatagramPacket datagramPacket) throws IOException {
        final byte[] arr3 = datagramPacket.getData();
        final int integer4 = datagramPacket.getLength();
        final SocketAddress socketAddress5 = datagramPacket.getSocketAddress();
        this.debug(new StringBuilder().append("Packet len ").append(integer4).append(" [").append(socketAddress5).append("]").toString());
        if (3 > integer4 || -2 != arr3[0] || -3 != arr3[1]) {
            this.debug(new StringBuilder().append("Invalid packet [").append(socketAddress5).append("]").toString());
            return false;
        }
        this.debug("Packet '" + PktUtils.toHexString(arr3[2]) + "' [" + socketAddress5 + "]");
        switch (arr3[2]) {
            case 9: {
                this.sendChallenge(datagramPacket);
                this.debug(new StringBuilder().append("Challenge [").append(socketAddress5).append("]").toString());
                return true;
            }
            case 0: {
                if (!this.validChallenge(datagramPacket)) {
                    this.debug(new StringBuilder().append("Invalid challenge [").append(socketAddress5).append("]").toString());
                    return false;
                }
                if (15 == integer4) {
                    this.sendTo(this.buildRuleResponse(datagramPacket), datagramPacket);
                    this.debug(new StringBuilder().append("Rules [").append(socketAddress5).append("]").toString());
                    break;
                }
                final NetworkDataOutputStream yf6 = new NetworkDataOutputStream(1460);
                yf6.write(0);
                yf6.writeBytes(this.getIdentBytes(datagramPacket.getSocketAddress()));
                yf6.writeString(this.serverName);
                yf6.writeString("SMP");
                yf6.writeString(this.worldName);
                yf6.writeString(Integer.toString(this.currentPlayerCount()));
                yf6.writeString(Integer.toString(this.maxPlayers));
                yf6.writeShort((short)this.serverPort);
                yf6.writeString(this.hostIp);
                this.sendTo(yf6.toByteArray(), datagramPacket);
                this.debug(new StringBuilder().append("Status [").append(socketAddress5).append("]").toString());
                break;
            }
        }
        return true;
    }
    
    private byte[] buildRuleResponse(final DatagramPacket datagramPacket) throws IOException {
        final long long3 = Util.getMillis();
        if (long3 < this.lastRulesResponse + 5000L) {
            final byte[] arr5 = this.rulesResponse.toByteArray();
            final byte[] arr6 = this.getIdentBytes(datagramPacket.getSocketAddress());
            arr5[1] = arr6[0];
            arr5[2] = arr6[1];
            arr5[3] = arr6[2];
            arr5[4] = arr6[3];
            return arr5;
        }
        this.lastRulesResponse = long3;
        this.rulesResponse.reset();
        this.rulesResponse.write(0);
        this.rulesResponse.writeBytes(this.getIdentBytes(datagramPacket.getSocketAddress()));
        this.rulesResponse.writeString("splitnum");
        this.rulesResponse.write(128);
        this.rulesResponse.write(0);
        this.rulesResponse.writeString("hostname");
        this.rulesResponse.writeString(this.serverName);
        this.rulesResponse.writeString("gametype");
        this.rulesResponse.writeString("SMP");
        this.rulesResponse.writeString("game_id");
        this.rulesResponse.writeString("MINECRAFT");
        this.rulesResponse.writeString("version");
        this.rulesResponse.writeString(this.serverInterface.getServerVersion());
        this.rulesResponse.writeString("plugins");
        this.rulesResponse.writeString(this.serverInterface.getPluginNames());
        this.rulesResponse.writeString("map");
        this.rulesResponse.writeString(this.worldName);
        this.rulesResponse.writeString("numplayers");
        this.rulesResponse.writeString(new StringBuilder().append("").append(this.currentPlayerCount()).toString());
        this.rulesResponse.writeString("maxplayers");
        this.rulesResponse.writeString(new StringBuilder().append("").append(this.maxPlayers).toString());
        this.rulesResponse.writeString("hostport");
        this.rulesResponse.writeString(new StringBuilder().append("").append(this.serverPort).toString());
        this.rulesResponse.writeString("hostip");
        this.rulesResponse.writeString(this.hostIp);
        this.rulesResponse.write(0);
        this.rulesResponse.write(1);
        this.rulesResponse.writeString("player_");
        this.rulesResponse.write(0);
        final String[] playerNames;
        final String[] arr7 = playerNames = this.serverInterface.getPlayerNames();
        for (final String string9 : playerNames) {
            this.rulesResponse.writeString(string9);
        }
        this.rulesResponse.write(0);
        return this.rulesResponse.toByteArray();
    }
    
    private byte[] getIdentBytes(final SocketAddress socketAddress) {
        return ((RequestChallenge)this.validChallenges.get(socketAddress)).getIdentBytes();
    }
    
    private Boolean validChallenge(final DatagramPacket datagramPacket) {
        final SocketAddress socketAddress3 = datagramPacket.getSocketAddress();
        if (!this.validChallenges.containsKey(socketAddress3)) {
            return false;
        }
        final byte[] arr4 = datagramPacket.getData();
        if (((RequestChallenge)this.validChallenges.get(socketAddress3)).getChallenge() != PktUtils.intFromNetworkByteArray(arr4, 7, datagramPacket.getLength())) {
            return false;
        }
        return true;
    }
    
    private void sendChallenge(final DatagramPacket datagramPacket) throws IOException {
        final RequestChallenge a3 = new RequestChallenge(datagramPacket);
        this.validChallenges.put(datagramPacket.getSocketAddress(), a3);
        this.sendTo(a3.getChallengeBytes(), datagramPacket);
    }
    
    private void pruneChallenges() {
        if (!this.running) {
            return;
        }
        final long long2 = Util.getMillis();
        if (long2 < this.lastChallengeCheck + 30000L) {
            return;
        }
        this.lastChallengeCheck = long2;
        final Iterator<Map.Entry<SocketAddress, RequestChallenge>> iterator4 = (Iterator<Map.Entry<SocketAddress, RequestChallenge>>)this.validChallenges.entrySet().iterator();
        while (iterator4.hasNext()) {
            final Map.Entry<SocketAddress, RequestChallenge> entry5 = (Map.Entry<SocketAddress, RequestChallenge>)iterator4.next();
            if (((RequestChallenge)entry5.getValue()).before(long2)) {
                iterator4.remove();
            }
        }
    }
    
    public void run() {
        this.info("Query running on " + this.serverIp + ":" + this.port);
        this.lastChallengeCheck = Util.getMillis();
        this.request = new DatagramPacket(this.buffer, this.buffer.length);
        try {
            while (this.running) {
                try {
                    this.socket.receive(this.request);
                    this.pruneChallenges();
                    this.processPacket(this.request);
                }
                catch (SocketTimeoutException socketTimeoutException2) {
                    this.pruneChallenges();
                }
                catch (PortUnreachableException ex) {}
                catch (IOException iOException2) {
                    this.recoverSocketError((Exception)iOException2);
                }
            }
        }
        finally {
            this.closeSockets();
        }
    }
    
    @Override
    public void start() {
        if (this.running) {
            return;
        }
        if (0 >= this.port || 65535 < this.port) {
            this.warn(new StringBuilder().append("Invalid query port ").append(this.port).append(" found in server.properties (queries disabled)").toString());
            return;
        }
        if (this.initSocket()) {
            super.start();
        }
    }
    
    private void recoverSocketError(final Exception exception) {
        if (!this.running) {
            return;
        }
        this.warn(new StringBuilder().append("Unexpected exception, buggy JRE? (").append(exception).append(")").toString());
        if (!this.initSocket()) {
            this.error("Failed to recover from buggy JRE, shutting down!");
            this.running = false;
        }
    }
    
    private boolean initSocket() {
        try {
            this.registerSocket(this.socket = new DatagramSocket(this.port, InetAddress.getByName(this.serverIp)));
            this.socket.setSoTimeout(500);
            return true;
        }
        catch (SocketException socketException2) {
            this.warn("Unable to initialise query system on " + this.serverIp + ":" + this.port + " (Socket): " + socketException2.getMessage());
        }
        catch (UnknownHostException unknownHostException2) {
            this.warn("Unable to initialise query system on " + this.serverIp + ":" + this.port + " (Unknown Host): " + unknownHostException2.getMessage());
        }
        catch (Exception exception2) {
            this.warn("Unable to initialise query system on " + this.serverIp + ":" + this.port + " (E): " + exception2.getMessage());
        }
        return false;
    }
    
    class RequestChallenge {
        private final long time;
        private final int challenge;
        private final byte[] identBytes;
        private final byte[] challengeBytes;
        private final String ident;
        
        public RequestChallenge(final DatagramPacket datagramPacket) {
            this.time = new Date().getTime();
            final byte[] arr4 = datagramPacket.getData();
            (this.identBytes = new byte[4])[0] = arr4[3];
            this.identBytes[1] = arr4[4];
            this.identBytes[2] = arr4[5];
            this.identBytes[3] = arr4[6];
            this.ident = new String(this.identBytes, StandardCharsets.UTF_8);
            this.challenge = new Random().nextInt(16777216);
            this.challengeBytes = String.format("\t%s%d\u0000", new Object[] { this.ident, this.challenge }).getBytes(StandardCharsets.UTF_8);
        }
        
        public Boolean before(final long long1) {
            return this.time < long1;
        }
        
        public int getChallenge() {
            return this.challenge;
        }
        
        public byte[] getChallengeBytes() {
            return this.challengeBytes;
        }
        
        public byte[] getIdentBytes() {
            return this.identBytes;
        }
    }
}
