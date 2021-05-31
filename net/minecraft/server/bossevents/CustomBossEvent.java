package net.minecraft.server.bossevents;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.network.chat.Style;
import java.util.function.Consumer;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerPlayer;
import com.google.common.collect.Sets;
import net.minecraft.world.BossEvent;
import net.minecraft.network.chat.Component;
import java.util.UUID;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;

public class CustomBossEvent extends ServerBossEvent {
    private final ResourceLocation id;
    private final Set<UUID> players;
    private int value;
    private int max;
    
    public CustomBossEvent(final ResourceLocation qv, final Component jo) {
        super(jo, BossBarColor.WHITE, BossBarOverlay.PROGRESS);
        this.players = (Set<UUID>)Sets.newHashSet();
        this.max = 100;
        this.id = qv;
        this.setPercent(0.0f);
    }
    
    public ResourceLocation getTextId() {
        return this.id;
    }
    
    @Override
    public void addPlayer(final ServerPlayer vl) {
        super.addPlayer(vl);
        this.players.add(vl.getUUID());
    }
    
    public void addOfflinePlayer(final UUID uUID) {
        this.players.add(uUID);
    }
    
    @Override
    public void removePlayer(final ServerPlayer vl) {
        super.removePlayer(vl);
        this.players.remove(vl.getUUID());
    }
    
    @Override
    public void removeAllPlayers() {
        super.removeAllPlayers();
        this.players.clear();
    }
    
    public int getValue() {
        return this.value;
    }
    
    public int getMax() {
        return this.max;
    }
    
    public void setValue(final int integer) {
        this.value = integer;
        this.setPercent(Mth.clamp(integer / (float)this.max, 0.0f, 1.0f));
    }
    
    public void setMax(final int integer) {
        this.max = integer;
        this.setPercent(Mth.clamp(this.value / (float)integer, 0.0f, 1.0f));
    }
    
    public final Component getDisplayName() {
        return ComponentUtils.wrapInSquareBrackets(this.getName()).withStyle((Consumer<Style>)(jw -> jw.setColor(this.getColor().getFormatting()).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(this.getTextId().toString()))).setInsertion(this.getTextId().toString())));
    }
    
    public boolean setPlayers(final Collection<ServerPlayer> collection) {
        final Set<UUID> set3 = (Set<UUID>)Sets.newHashSet();
        final Set<ServerPlayer> set4 = (Set<ServerPlayer>)Sets.newHashSet();
        for (final UUID uUID6 : this.players) {
            boolean boolean7 = false;
            for (final ServerPlayer vl9 : collection) {
                if (vl9.getUUID().equals(uUID6)) {
                    boolean7 = true;
                    break;
                }
            }
            if (!boolean7) {
                set3.add(uUID6);
            }
        }
        for (final ServerPlayer vl10 : collection) {
            boolean boolean7 = false;
            for (final UUID uUID7 : this.players) {
                if (vl10.getUUID().equals(uUID7)) {
                    boolean7 = true;
                    break;
                }
            }
            if (!boolean7) {
                set4.add(vl10);
            }
        }
        for (final UUID uUID6 : set3) {
            for (final ServerPlayer vl11 : this.getPlayers()) {
                if (vl11.getUUID().equals(uUID6)) {
                    this.removePlayer(vl11);
                    break;
                }
            }
            this.players.remove(uUID6);
        }
        for (final ServerPlayer vl10 : set4) {
            this.addPlayer(vl10);
        }
        return !set3.isEmpty() || !set4.isEmpty();
    }
    
    public CompoundTag save() {
        final CompoundTag id2 = new CompoundTag();
        id2.putString("Name", Component.Serializer.toJson(this.name));
        id2.putBoolean("Visible", this.isVisible());
        id2.putInt("Value", this.value);
        id2.putInt("Max", this.max);
        id2.putString("Color", this.getColor().getName());
        id2.putString("Overlay", this.getOverlay().getName());
        id2.putBoolean("DarkenScreen", this.shouldDarkenScreen());
        id2.putBoolean("PlayBossMusic", this.shouldPlayBossMusic());
        id2.putBoolean("CreateWorldFog", this.shouldCreateWorldFog());
        final ListTag ik3 = new ListTag();
        for (final UUID uUID5 : this.players) {
            ik3.add(NbtUtils.createUUIDTag(uUID5));
        }
        id2.put("Players", (Tag)ik3);
        return id2;
    }
    
    public static CustomBossEvent load(final CompoundTag id, final ResourceLocation qv) {
        final CustomBossEvent rl3 = new CustomBossEvent(qv, Component.Serializer.fromJson(id.getString("Name")));
        rl3.setVisible(id.getBoolean("Visible"));
        rl3.setValue(id.getInt("Value"));
        rl3.setMax(id.getInt("Max"));
        rl3.setColor(BossBarColor.byName(id.getString("Color")));
        rl3.setOverlay(BossBarOverlay.byName(id.getString("Overlay")));
        rl3.setDarkenScreen(id.getBoolean("DarkenScreen"));
        rl3.setPlayBossMusic(id.getBoolean("PlayBossMusic"));
        rl3.setCreateWorldFog(id.getBoolean("CreateWorldFog"));
        final ListTag ik4 = id.getList("Players", 10);
        for (int integer5 = 0; integer5 < ik4.size(); ++integer5) {
            rl3.addOfflinePlayer(NbtUtils.loadUUIDTag(ik4.getCompound(integer5)));
        }
        return rl3;
    }
    
    public void onPlayerConnect(final ServerPlayer vl) {
        if (this.players.contains(vl.getUUID())) {
            this.addPlayer(vl);
        }
    }
    
    public void onPlayerDisconnect(final ServerPlayer vl) {
        super.removePlayer(vl);
    }
}
