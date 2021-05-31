package net.minecraft.server;

import net.minecraft.server.level.ServerPlayer;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import java.util.Collection;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import java.util.Arrays;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.world.scores.Score;
import com.google.common.collect.Sets;
import net.minecraft.world.scores.Objective;
import java.util.Set;
import net.minecraft.world.scores.Scoreboard;

public class ServerScoreboard extends Scoreboard {
    private final MinecraftServer server;
    private final Set<Objective> trackedObjectives;
    private Runnable[] dirtyListeners;
    
    public ServerScoreboard(final MinecraftServer minecraftServer) {
        this.trackedObjectives = (Set<Objective>)Sets.newHashSet();
        this.dirtyListeners = new Runnable[0];
        this.server = minecraftServer;
    }
    
    @Override
    public void onScoreChanged(final Score cth) {
        super.onScoreChanged(cth);
        if (this.trackedObjectives.contains(cth.getObjective())) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(Method.CHANGE, cth.getObjective().getName(), cth.getOwner(), cth.getScore()));
        }
        this.setDirty();
    }
    
    @Override
    public void onPlayerRemoved(final String string) {
        super.onPlayerRemoved(string);
        this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(Method.REMOVE, null, string, 0));
        this.setDirty();
    }
    
    @Override
    public void onPlayerScoreRemoved(final String string, final Objective ctf) {
        super.onPlayerScoreRemoved(string, ctf);
        if (this.trackedObjectives.contains(ctf)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(Method.REMOVE, ctf.getName(), string, 0));
        }
        this.setDirty();
    }
    
    @Override
    public void setDisplayObjective(final int integer, @Nullable final Objective ctf) {
        final Objective ctf2 = this.getDisplayObjective(integer);
        super.setDisplayObjective(integer, ctf);
        if (ctf2 != ctf && ctf2 != null) {
            if (this.getObjectiveDisplaySlotCount(ctf2) > 0) {
                this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(integer, ctf));
            }
            else {
                this.stopTrackingObjective(ctf2);
            }
        }
        if (ctf != null) {
            if (this.trackedObjectives.contains(ctf)) {
                this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(integer, ctf));
            }
            else {
                this.startTrackingObjective(ctf);
            }
        }
        this.setDirty();
    }
    
    @Override
    public boolean addPlayerToTeam(final String string, final PlayerTeam ctg) {
        if (super.addPlayerToTeam(string, ctg)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(ctg, (Collection<String>)Arrays.asList((Object[])new String[] { string }), 3));
            this.setDirty();
            return true;
        }
        return false;
    }
    
    @Override
    public void removePlayerFromTeam(final String string, final PlayerTeam ctg) {
        super.removePlayerFromTeam(string, ctg);
        this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(ctg, (Collection<String>)Arrays.asList((Object[])new String[] { string }), 4));
        this.setDirty();
    }
    
    @Override
    public void onObjectiveAdded(final Objective ctf) {
        super.onObjectiveAdded(ctf);
        this.setDirty();
    }
    
    @Override
    public void onObjectiveChanged(final Objective ctf) {
        super.onObjectiveChanged(ctf);
        if (this.trackedObjectives.contains(ctf)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetObjectivePacket(ctf, 2));
        }
        this.setDirty();
    }
    
    @Override
    public void onObjectiveRemoved(final Objective ctf) {
        super.onObjectiveRemoved(ctf);
        if (this.trackedObjectives.contains(ctf)) {
            this.stopTrackingObjective(ctf);
        }
        this.setDirty();
    }
    
    @Override
    public void onTeamAdded(final PlayerTeam ctg) {
        super.onTeamAdded(ctg);
        this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(ctg, 0));
        this.setDirty();
    }
    
    @Override
    public void onTeamChanged(final PlayerTeam ctg) {
        super.onTeamChanged(ctg);
        this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(ctg, 2));
        this.setDirty();
    }
    
    @Override
    public void onTeamRemoved(final PlayerTeam ctg) {
        super.onTeamRemoved(ctg);
        this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(ctg, 1));
        this.setDirty();
    }
    
    public void addDirtyListener(final Runnable runnable) {
        (this.dirtyListeners = (Runnable[])Arrays.copyOf((Object[])this.dirtyListeners, this.dirtyListeners.length + 1))[this.dirtyListeners.length - 1] = runnable;
    }
    
    protected void setDirty() {
        for (final Runnable runnable5 : this.dirtyListeners) {
            runnable5.run();
        }
    }
    
    public List<Packet<?>> getStartTrackingPackets(final Objective ctf) {
        final List<Packet<?>> list3 = (List<Packet<?>>)Lists.newArrayList();
        list3.add(new ClientboundSetObjectivePacket(ctf, 0));
        for (int integer4 = 0; integer4 < 19; ++integer4) {
            if (this.getDisplayObjective(integer4) == ctf) {
                list3.add(new ClientboundSetDisplayObjectivePacket(integer4, ctf));
            }
        }
        for (final Score cth5 : this.getPlayerScores(ctf)) {
            list3.add(new ClientboundSetScorePacket(Method.CHANGE, cth5.getObjective().getName(), cth5.getOwner(), cth5.getScore()));
        }
        return list3;
    }
    
    public void startTrackingObjective(final Objective ctf) {
        final List<Packet<?>> list3 = this.getStartTrackingPackets(ctf);
        for (final ServerPlayer vl5 : this.server.getPlayerList().getPlayers()) {
            for (final Packet<?> kc7 : list3) {
                vl5.connection.send(kc7);
            }
        }
        this.trackedObjectives.add(ctf);
    }
    
    public List<Packet<?>> getStopTrackingPackets(final Objective ctf) {
        final List<Packet<?>> list3 = (List<Packet<?>>)Lists.newArrayList();
        list3.add(new ClientboundSetObjectivePacket(ctf, 1));
        for (int integer4 = 0; integer4 < 19; ++integer4) {
            if (this.getDisplayObjective(integer4) == ctf) {
                list3.add(new ClientboundSetDisplayObjectivePacket(integer4, ctf));
            }
        }
        return list3;
    }
    
    public void stopTrackingObjective(final Objective ctf) {
        final List<Packet<?>> list3 = this.getStopTrackingPackets(ctf);
        for (final ServerPlayer vl5 : this.server.getPlayerList().getPlayers()) {
            for (final Packet<?> kc7 : list3) {
                vl5.connection.send(kc7);
            }
        }
        this.trackedObjectives.remove(ctf);
    }
    
    public int getObjectiveDisplaySlotCount(final Objective ctf) {
        int integer3 = 0;
        for (int integer4 = 0; integer4 < 19; ++integer4) {
            if (this.getDisplayObjective(integer4) == ctf) {
                ++integer3;
            }
        }
        return integer3;
    }
    
    public enum Method {
        CHANGE, 
        REMOVE;
    }
}
