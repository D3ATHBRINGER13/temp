package net.minecraft.client.multiplayer;

import org.apache.logging.log4j.LogManager;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.util.HttpUtil;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import java.math.BigInteger;
import net.minecraft.util.Crypt;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;

public class ClientHandshakePacketListenerImpl implements ClientLoginPacketListener {
    private static final Logger LOGGER;
    private final Minecraft minecraft;
    @Nullable
    private final Screen parent;
    private final Consumer<Component> updateStatus;
    private final Connection connection;
    private GameProfile localGameProfile;
    
    public ClientHandshakePacketListenerImpl(final Connection jc, final Minecraft cyc, @Nullable final Screen dcl, final Consumer<Component> consumer) {
        this.connection = jc;
        this.minecraft = cyc;
        this.parent = dcl;
        this.updateStatus = consumer;
    }
    
    public void handleHello(final ClientboundHelloPacket pt) {
        final SecretKey secretKey3 = Crypt.generateSecretKey();
        final PublicKey publicKey4 = pt.getPublicKey();
        final String string5 = new BigInteger(Crypt.digestData(pt.getServerId(), publicKey4, secretKey3)).toString(16);
        final ServerboundKeyPacket pz6 = new ServerboundKeyPacket(secretKey3, publicKey4, pt.getNonce());
        this.updateStatus.accept(new TranslatableComponent("connect.authorizing", new Object[0]));
        HttpUtil.DOWNLOAD_EXECUTOR.submit(() -> {
            final Component jo5 = this.authenticateServer(string5);
            if (jo5 != null) {
                if (this.minecraft.getCurrentServer() == null || !this.minecraft.getCurrentServer().isLan()) {
                    this.connection.disconnect(jo5);
                    return;
                }
                ClientHandshakePacketListenerImpl.LOGGER.warn(jo5.getString());
            }
            this.updateStatus.accept(new TranslatableComponent("connect.encrypting", new Object[0]));
            this.connection.send(pz6, (future -> this.connection.setEncryptionKey(secretKey3)));
        });
    }
    
    @Nullable
    private Component authenticateServer(final String string) {
        try {
            this.getMinecraftSessionService().joinServer(this.minecraft.getUser().getGameProfile(), this.minecraft.getUser().getAccessToken(), string);
        }
        catch (AuthenticationUnavailableException authenticationUnavailableException3) {
            return new TranslatableComponent("disconnect.loginFailedInfo", new Object[] { new TranslatableComponent("disconnect.loginFailedInfo.serversUnavailable", new Object[0]) });
        }
        catch (InvalidCredentialsException invalidCredentialsException3) {
            return new TranslatableComponent("disconnect.loginFailedInfo", new Object[] { new TranslatableComponent("disconnect.loginFailedInfo.invalidSession", new Object[0]) });
        }
        catch (AuthenticationException authenticationException3) {
            return new TranslatableComponent("disconnect.loginFailedInfo", new Object[] { authenticationException3.getMessage() });
        }
        return null;
    }
    
    private MinecraftSessionService getMinecraftSessionService() {
        return this.minecraft.getMinecraftSessionService();
    }
    
    public void handleGameProfile(final ClientboundGameProfilePacket ps) {
        this.updateStatus.accept(new TranslatableComponent("connect.joining", new Object[0]));
        this.localGameProfile = ps.getGameProfile();
        this.connection.setProtocol(ConnectionProtocol.PLAY);
        this.connection.setListener(new ClientPacketListener(this.minecraft, this.parent, this.connection, this.localGameProfile));
    }
    
    public void onDisconnect(final Component jo) {
        if (this.parent != null && this.parent instanceof RealmsScreenProxy) {
            this.minecraft.setScreen(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.parent).getScreen(), "connect.failed", jo).getProxy());
        }
        else {
            this.minecraft.setScreen(new DisconnectedScreen(this.parent, "connect.failed", jo));
        }
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public void handleDisconnect(final ClientboundLoginDisconnectPacket pv) {
        this.connection.disconnect(pv.getReason());
    }
    
    public void handleCompression(final ClientboundLoginCompressionPacket pu) {
        if (!this.connection.isMemoryConnection()) {
            this.connection.setupCompression(pu.getCompressionThreshold());
        }
    }
    
    public void handleCustomQuery(final ClientboundCustomQueryPacket pr) {
        this.updateStatus.accept(new TranslatableComponent("connect.negotiating", new Object[0]));
        this.connection.send(new ServerboundCustomQueryPacket(pr.getTransactionId(), null));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
