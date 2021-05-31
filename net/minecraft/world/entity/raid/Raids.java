package net.minecraft.world.entity.raid;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.phys.Vec3;
import java.util.stream.Collectors;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.server.level.ServerPlayer;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.level.GameRules;
import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerLevel;
import java.util.Map;
import net.minecraft.world.level.saveddata.SavedData;

public class Raids extends SavedData {
    private final Map<Integer, Raid> raidMap;
    private final ServerLevel level;
    private int nextAvailableID;
    private int tick;
    
    public Raids(final ServerLevel vk) {
        super(getFileId(vk.dimension));
        this.raidMap = (Map<Integer, Raid>)Maps.newHashMap();
        this.level = vk;
        this.nextAvailableID = 1;
        this.setDirty();
    }
    
    public Raid get(final int integer) {
        return (Raid)this.raidMap.get(integer);
    }
    
    public void tick() {
        ++this.tick;
        final Iterator<Raid> iterator2 = (Iterator<Raid>)this.raidMap.values().iterator();
        while (iterator2.hasNext()) {
            final Raid axk3 = (Raid)iterator2.next();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                axk3.stop();
            }
            if (axk3.isStopped()) {
                iterator2.remove();
                this.setDirty();
            }
            else {
                axk3.tick();
            }
        }
        if (this.tick % 200 == 0) {
            this.setDirty();
        }
        DebugPackets.sendRaids(this.level, (Collection<Raid>)this.raidMap.values());
    }
    
    public static boolean canJoinRaid(final Raider axl, final Raid axk) {
        return axl != null && axk != null && axk.getLevel() != null && axl.isAlive() && axl.canJoinRaid() && axl.getNoActionTime() <= 2400 && axl.level.getDimension().getType() == axk.getLevel().getDimension().getType();
    }
    
    @Nullable
    public Raid createOrExtendRaid(final ServerPlayer vl) {
        if (vl.isSpectator()) {
            return null;
        }
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            return null;
        }
        final DimensionType byn3 = vl.level.getDimension().getType();
        if (byn3 == DimensionType.NETHER) {
            return null;
        }
        final BlockPos ew4 = new BlockPos(vl);
        final List<PoiRecord> list6 = (List<PoiRecord>)this.level.getPoiManager().getInRange(PoiType.ALL, ew4, 64, PoiManager.Occupancy.IS_OCCUPIED).collect(Collectors.toList());
        int integer7 = 0;
        Vec3 csi8 = new Vec3(0.0, 0.0, 0.0);
        for (final PoiRecord aqq10 : list6) {
            final BlockPos ew5 = aqq10.getPos();
            csi8 = csi8.add(ew5.getX(), ew5.getY(), ew5.getZ());
            ++integer7;
        }
        BlockPos ew6;
        if (integer7 > 0) {
            csi8 = csi8.scale(1.0 / integer7);
            ew6 = new BlockPos(csi8);
        }
        else {
            ew6 = ew4;
        }
        final Raid axk9 = this.getOrCreateRaid(vl.getLevel(), ew6);
        boolean boolean10 = false;
        if (!axk9.isStarted()) {
            if (!this.raidMap.containsKey(axk9.getId())) {
                this.raidMap.put(axk9.getId(), axk9);
            }
            boolean10 = true;
        }
        else if (axk9.getBadOmenLevel() < axk9.getMaxBadOmenLevel()) {
            boolean10 = true;
        }
        else {
            vl.removeEffect(MobEffects.BAD_OMEN);
            vl.connection.send(new ClientboundEntityEventPacket(vl, (byte)43));
        }
        if (boolean10) {
            axk9.absorbBadOmen(vl);
            vl.connection.send(new ClientboundEntityEventPacket(vl, (byte)43));
            if (!axk9.hasFirstWaveSpawned()) {
                vl.awardStat(Stats.RAID_TRIGGER);
                CriteriaTriggers.BAD_OMEN.trigger(vl);
            }
        }
        this.setDirty();
        return axk9;
    }
    
    private Raid getOrCreateRaid(final ServerLevel vk, final BlockPos ew) {
        final Raid axk4 = vk.getRaidAt(ew);
        return (axk4 != null) ? axk4 : new Raid(this.getUniqueId(), vk, ew);
    }
    
    @Override
    public void load(final CompoundTag id) {
        this.nextAvailableID = id.getInt("NextAvailableID");
        this.tick = id.getInt("Tick");
        final ListTag ik3 = id.getList("Raids", 10);
        for (int integer4 = 0; integer4 < ik3.size(); ++integer4) {
            final CompoundTag id2 = ik3.getCompound(integer4);
            final Raid axk6 = new Raid(this.level, id2);
            this.raidMap.put(axk6.getId(), axk6);
        }
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        id.putInt("NextAvailableID", this.nextAvailableID);
        id.putInt("Tick", this.tick);
        final ListTag ik3 = new ListTag();
        for (final Raid axk5 : this.raidMap.values()) {
            final CompoundTag id2 = new CompoundTag();
            axk5.save(id2);
            ik3.add(id2);
        }
        id.put("Raids", (Tag)ik3);
        return id;
    }
    
    public static String getFileId(final Dimension bym) {
        return "raids" + bym.getType().getFileSuffix();
    }
    
    private int getUniqueId() {
        return ++this.nextAvailableID;
    }
    
    @Nullable
    public Raid getNearbyRaid(final BlockPos ew, final int integer) {
        Raid axk4 = null;
        double double5 = integer;
        for (final Raid axk5 : this.raidMap.values()) {
            final double double6 = axk5.getCenter().distSqr(ew);
            if (!axk5.isActive()) {
                continue;
            }
            if (double6 >= double5) {
                continue;
            }
            axk4 = axk5;
            double5 = double6;
        }
        return axk4;
    }
}
