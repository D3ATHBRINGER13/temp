package net.minecraft.client.multiplayer;

import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import java.io.File;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;

public class ServerList {
    private static final Logger LOGGER;
    private final Minecraft minecraft;
    private final List<ServerData> serverList;
    
    public ServerList(final Minecraft cyc) {
        this.serverList = (List<ServerData>)Lists.newArrayList();
        this.minecraft = cyc;
        this.load();
    }
    
    public void load() {
        try {
            this.serverList.clear();
            final CompoundTag id2 = NbtIo.read(new File(this.minecraft.gameDirectory, "servers.dat"));
            if (id2 == null) {
                return;
            }
            final ListTag ik3 = id2.getList("servers", 10);
            for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
                this.serverList.add(ServerData.read(ik3.getCompound(integer4)));
            }
        }
        catch (Exception exception2) {
            ServerList.LOGGER.error("Couldn't load server list", (Throwable)exception2);
        }
    }
    
    public void save() {
        try {
            final ListTag ik2 = new ListTag();
            for (final ServerData dki4 : this.serverList) {
                ik2.add(dki4.write());
            }
            final CompoundTag id3 = new CompoundTag();
            id3.put("servers", (Tag)ik2);
            NbtIo.safeWrite(id3, new File(this.minecraft.gameDirectory, "servers.dat"));
        }
        catch (Exception exception2) {
            ServerList.LOGGER.error("Couldn't save server list", (Throwable)exception2);
        }
    }
    
    public ServerData get(final int integer) {
        return (ServerData)this.serverList.get(integer);
    }
    
    public void remove(final ServerData dki) {
        this.serverList.remove(dki);
    }
    
    public void add(final ServerData dki) {
        this.serverList.add(dki);
    }
    
    public int size() {
        return this.serverList.size();
    }
    
    public void swap(final int integer1, final int integer2) {
        final ServerData dki4 = this.get(integer1);
        this.serverList.set(integer1, this.get(integer2));
        this.serverList.set(integer2, dki4);
        this.save();
    }
    
    public void replace(final int integer, final ServerData dki) {
        this.serverList.set(integer, dki);
    }
    
    public static void saveSingleServer(final ServerData dki) {
        final ServerList dkj2 = new ServerList(Minecraft.getInstance());
        dkj2.load();
        for (int integer3 = 0; integer3 < dkj2.size(); ++integer3) {
            final ServerData dki2 = dkj2.get(integer3);
            if (dki2.name.equals(dki.name) && dki2.ip.equals(dki.ip)) {
                dkj2.replace(integer3, dki);
                break;
            }
        }
        dkj2.save();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
