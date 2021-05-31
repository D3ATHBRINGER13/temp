package net.minecraft.world.level.saveddata.maps;

import java.util.Collection;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.world.item.MapItem;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.BlockGetter;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.player.Player;
import java.util.Map;
import java.util.List;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;

public class MapItemSavedData extends SavedData {
    public int x;
    public int z;
    public DimensionType dimension;
    public boolean trackingPosition;
    public boolean unlimitedTracking;
    public byte scale;
    public byte[] colors;
    public boolean locked;
    public final List<HoldingPlayer> carriedBy;
    private final Map<Player, HoldingPlayer> carriedByPlayers;
    private final Map<String, MapBanner> bannerMarkers;
    public final Map<String, MapDecoration> decorations;
    private final Map<String, MapFrame> frameMarkers;
    
    public MapItemSavedData(final String string) {
        super(string);
        this.colors = new byte[16384];
        this.carriedBy = (List<HoldingPlayer>)Lists.newArrayList();
        this.carriedByPlayers = (Map<Player, HoldingPlayer>)Maps.newHashMap();
        this.bannerMarkers = (Map<String, MapBanner>)Maps.newHashMap();
        this.decorations = (Map<String, MapDecoration>)Maps.newLinkedHashMap();
        this.frameMarkers = (Map<String, MapFrame>)Maps.newHashMap();
    }
    
    public void setProperties(final int integer1, final int integer2, final int integer3, final boolean boolean4, final boolean boolean5, final DimensionType byn) {
        this.scale = (byte)integer3;
        this.setOrigin(integer1, integer2, this.scale);
        this.dimension = byn;
        this.trackingPosition = boolean4;
        this.unlimitedTracking = boolean5;
        this.setDirty();
    }
    
    public void setOrigin(final double double1, final double double2, final int integer) {
        final int integer2 = 128 * (1 << integer);
        final int integer3 = Mth.floor((double1 + 64.0) / integer2);
        final int integer4 = Mth.floor((double2 + 64.0) / integer2);
        this.x = integer3 * integer2 + integer2 / 2 - 64;
        this.z = integer4 * integer2 + integer2 / 2 - 64;
    }
    
    @Override
    public void load(final CompoundTag id) {
        final int integer3 = id.getInt("dimension");
        final DimensionType byn4 = DimensionType.getById(integer3);
        if (byn4 == null) {
            throw new IllegalArgumentException(new StringBuilder().append("Invalid map dimension: ").append(integer3).toString());
        }
        this.dimension = byn4;
        this.x = id.getInt("xCenter");
        this.z = id.getInt("zCenter");
        this.scale = (byte)Mth.clamp(id.getByte("scale"), 0, 4);
        this.trackingPosition = (!id.contains("trackingPosition", 1) || id.getBoolean("trackingPosition"));
        this.unlimitedTracking = id.getBoolean("unlimitedTracking");
        this.locked = id.getBoolean("locked");
        this.colors = id.getByteArray("colors");
        if (this.colors.length != 16384) {
            this.colors = new byte[16384];
        }
        final ListTag ik5 = id.getList("banners", 10);
        for (int integer4 = 0; integer4 < ik5.size(); ++integer4) {
            final MapBanner cod7 = MapBanner.load(ik5.getCompound(integer4));
            this.bannerMarkers.put(cod7.getId(), cod7);
            this.addDecoration(cod7.getDecoration(), null, cod7.getId(), cod7.getPos().getX(), cod7.getPos().getZ(), 180.0, cod7.getName());
        }
        final ListTag ik6 = id.getList("frames", 10);
        for (int integer5 = 0; integer5 < ik6.size(); ++integer5) {
            final MapFrame cof8 = MapFrame.load(ik6.getCompound(integer5));
            this.frameMarkers.put(cof8.getId(), cof8);
            this.addDecoration(MapDecoration.Type.FRAME, null, new StringBuilder().append("frame-").append(cof8.getEntityId()).toString(), cof8.getPos().getX(), cof8.getPos().getZ(), cof8.getRotation(), null);
        }
    }
    
    @Override
    public CompoundTag save(final CompoundTag id) {
        id.putInt("dimension", this.dimension.getId());
        id.putInt("xCenter", this.x);
        id.putInt("zCenter", this.z);
        id.putByte("scale", this.scale);
        id.putByteArray("colors", this.colors);
        id.putBoolean("trackingPosition", this.trackingPosition);
        id.putBoolean("unlimitedTracking", this.unlimitedTracking);
        id.putBoolean("locked", this.locked);
        final ListTag ik3 = new ListTag();
        for (final MapBanner cod5 : this.bannerMarkers.values()) {
            ik3.add(cod5.save());
        }
        id.put("banners", (Tag)ik3);
        final ListTag ik4 = new ListTag();
        for (final MapFrame cof6 : this.frameMarkers.values()) {
            ik4.add(cof6.save());
        }
        id.put("frames", (Tag)ik4);
        return id;
    }
    
    public void lockData(final MapItemSavedData coh) {
        this.locked = true;
        this.x = coh.x;
        this.z = coh.z;
        this.bannerMarkers.putAll((Map)coh.bannerMarkers);
        this.decorations.putAll((Map)coh.decorations);
        System.arraycopy(coh.colors, 0, this.colors, 0, coh.colors.length);
        this.setDirty();
    }
    
    public void tickCarriedBy(final Player awg, final ItemStack bcj) {
        if (!this.carriedByPlayers.containsKey(awg)) {
            final HoldingPlayer a4 = new HoldingPlayer(awg);
            this.carriedByPlayers.put(awg, a4);
            this.carriedBy.add(a4);
        }
        if (!awg.inventory.contains(bcj)) {
            this.decorations.remove(awg.getName().getString());
        }
        for (int integer4 = 0; integer4 < this.carriedBy.size(); ++integer4) {
            final HoldingPlayer a5 = (HoldingPlayer)this.carriedBy.get(integer4);
            final String string6 = a5.player.getName().getString();
            if (a5.player.removed || (!a5.player.inventory.contains(bcj) && !bcj.isFramed())) {
                this.carriedByPlayers.remove(a5.player);
                this.carriedBy.remove(a5);
                this.decorations.remove(string6);
            }
            else if (!bcj.isFramed() && a5.player.dimension == this.dimension && this.trackingPosition) {
                this.addDecoration(MapDecoration.Type.PLAYER, a5.player.level, string6, a5.player.x, a5.player.z, a5.player.yRot, null);
            }
        }
        if (bcj.isFramed() && this.trackingPosition) {
            final ItemFrame atn4 = bcj.getFrame();
            final BlockPos ew5 = atn4.getPos();
            final MapFrame cof6 = (MapFrame)this.frameMarkers.get(MapFrame.frameId(ew5));
            if (cof6 != null && atn4.getId() != cof6.getEntityId() && this.frameMarkers.containsKey(cof6.getId())) {
                this.decorations.remove(new StringBuilder().append("frame-").append(cof6.getEntityId()).toString());
            }
            final MapFrame cof7 = new MapFrame(ew5, atn4.getDirection().get2DDataValue() * 90, atn4.getId());
            this.addDecoration(MapDecoration.Type.FRAME, awg.level, new StringBuilder().append("frame-").append(atn4.getId()).toString(), ew5.getX(), ew5.getZ(), atn4.getDirection().get2DDataValue() * 90, null);
            this.frameMarkers.put(cof7.getId(), cof7);
        }
        final CompoundTag id4 = bcj.getTag();
        if (id4 != null && id4.contains("Decorations", 9)) {
            final ListTag ik5 = id4.getList("Decorations", 10);
            for (int integer5 = 0; integer5 < ik5.size(); ++integer5) {
                final CompoundTag id5 = ik5.getCompound(integer5);
                if (!this.decorations.containsKey(id5.getString("id"))) {
                    this.addDecoration(MapDecoration.Type.byIcon(id5.getByte("type")), awg.level, id5.getString("id"), id5.getDouble("x"), id5.getDouble("z"), id5.getDouble("rot"), null);
                }
            }
        }
    }
    
    public static void addTargetDecoration(final ItemStack bcj, final BlockPos ew, final String string, final MapDecoration.Type a) {
        ListTag ik5;
        if (bcj.hasTag() && bcj.getTag().contains("Decorations", 9)) {
            ik5 = bcj.getTag().getList("Decorations", 10);
        }
        else {
            ik5 = new ListTag();
            bcj.addTagElement("Decorations", (Tag)ik5);
        }
        final CompoundTag id6 = new CompoundTag();
        id6.putByte("type", a.getIcon());
        id6.putString("id", string);
        id6.putDouble("x", (double)ew.getX());
        id6.putDouble("z", (double)ew.getZ());
        id6.putDouble("rot", 180.0);
        ik5.add(id6);
        if (a.hasMapColor()) {
            final CompoundTag id7 = bcj.getOrCreateTagElement("display");
            id7.putInt("MapColor", a.getMapColor());
        }
    }
    
    private void addDecoration(MapDecoration.Type a, @Nullable final LevelAccessor bhs, final String string, final double double4, final double double5, double double6, @Nullable final Component jo) {
        final int integer12 = 1 << this.scale;
        final float float13 = (float)(double4 - this.x) / integer12;
        final float float14 = (float)(double5 - this.z) / integer12;
        byte byte15 = (byte)(float13 * 2.0f + 0.5);
        byte byte16 = (byte)(float14 * 2.0f + 0.5);
        final int integer13 = 63;
        byte byte17;
        if (float13 >= -63.0f && float14 >= -63.0f && float13 <= 63.0f && float14 <= 63.0f) {
            double6 += ((double6 < 0.0) ? -8.0 : 8.0);
            byte17 = (byte)(double6 * 16.0 / 360.0);
            if (this.dimension == DimensionType.NETHER && bhs != null) {
                final int integer14 = (int)(bhs.getLevelData().getDayTime() / 10L);
                byte17 = (byte)(integer14 * integer14 * 34187121 + integer14 * 121 >> 15 & 0xF);
            }
        }
        else {
            if (a != MapDecoration.Type.PLAYER) {
                this.decorations.remove(string);
                return;
            }
            final int integer14 = 320;
            if (Math.abs(float13) < 320.0f && Math.abs(float14) < 320.0f) {
                a = MapDecoration.Type.PLAYER_OFF_MAP;
            }
            else {
                if (!this.unlimitedTracking) {
                    this.decorations.remove(string);
                    return;
                }
                a = MapDecoration.Type.PLAYER_OFF_LIMITS;
            }
            byte17 = 0;
            if (float13 <= -63.0f) {
                byte15 = -128;
            }
            if (float14 <= -63.0f) {
                byte16 = -128;
            }
            if (float13 >= 63.0f) {
                byte15 = 127;
            }
            if (float14 >= 63.0f) {
                byte16 = 127;
            }
        }
        this.decorations.put(string, new MapDecoration(a, byte15, byte16, byte17, jo));
    }
    
    @Nullable
    public Packet<?> getUpdatePacket(final ItemStack bcj, final BlockGetter bhb, final Player awg) {
        final HoldingPlayer a5 = (HoldingPlayer)this.carriedByPlayers.get(awg);
        if (a5 == null) {
            return null;
        }
        return a5.nextUpdatePacket(bcj);
    }
    
    public void setDirty(final int integer1, final int integer2) {
        this.setDirty();
        for (final HoldingPlayer a5 : this.carriedBy) {
            a5.markDirty(integer1, integer2);
        }
    }
    
    public HoldingPlayer getHoldingPlayer(final Player awg) {
        HoldingPlayer a3 = (HoldingPlayer)this.carriedByPlayers.get(awg);
        if (a3 == null) {
            a3 = new HoldingPlayer(awg);
            this.carriedByPlayers.put(awg, a3);
            this.carriedBy.add(a3);
        }
        return a3;
    }
    
    public void toggleBanner(final LevelAccessor bhs, final BlockPos ew) {
        final float float4 = ew.getX() + 0.5f;
        final float float5 = ew.getZ() + 0.5f;
        final int integer6 = 1 << this.scale;
        final float float6 = (float4 - this.x) / integer6;
        final float float7 = (float5 - this.z) / integer6;
        final int integer7 = 63;
        boolean boolean10 = false;
        if (float6 >= -63.0f && float7 >= -63.0f && float6 <= 63.0f && float7 <= 63.0f) {
            final MapBanner cod11 = MapBanner.fromWorld(bhs, ew);
            if (cod11 == null) {
                return;
            }
            boolean boolean11 = true;
            if (this.bannerMarkers.containsKey(cod11.getId()) && ((MapBanner)this.bannerMarkers.get(cod11.getId())).equals(cod11)) {
                this.bannerMarkers.remove(cod11.getId());
                this.decorations.remove(cod11.getId());
                boolean11 = false;
                boolean10 = true;
            }
            if (boolean11) {
                this.bannerMarkers.put(cod11.getId(), cod11);
                this.addDecoration(cod11.getDecoration(), bhs, cod11.getId(), float4, float5, 180.0, cod11.getName());
                boolean10 = true;
            }
            if (boolean10) {
                this.setDirty();
            }
        }
    }
    
    public void checkBanners(final BlockGetter bhb, final int integer2, final int integer3) {
        final Iterator<MapBanner> iterator5 = (Iterator<MapBanner>)this.bannerMarkers.values().iterator();
        while (iterator5.hasNext()) {
            final MapBanner cod6 = (MapBanner)iterator5.next();
            if (cod6.getPos().getX() == integer2 && cod6.getPos().getZ() == integer3) {
                final MapBanner cod7 = MapBanner.fromWorld(bhb, cod6.getPos());
                if (cod6.equals(cod7)) {
                    continue;
                }
                iterator5.remove();
                this.decorations.remove(cod6.getId());
            }
        }
    }
    
    public void removedFromFrame(final BlockPos ew, final int integer) {
        this.decorations.remove(new StringBuilder().append("frame-").append(integer).toString());
        this.frameMarkers.remove(MapFrame.frameId(ew));
    }
    
    public class HoldingPlayer {
        public final Player player;
        private boolean dirtyData;
        private int minDirtyX;
        private int minDirtyY;
        private int maxDirtyX;
        private int maxDirtyY;
        private int tick;
        public int step;
        
        public HoldingPlayer(final Player awg) {
            this.dirtyData = true;
            this.maxDirtyX = 127;
            this.maxDirtyY = 127;
            this.player = awg;
        }
        
        @Nullable
        public Packet<?> nextUpdatePacket(final ItemStack bcj) {
            if (this.dirtyData) {
                this.dirtyData = false;
                return new ClientboundMapItemDataPacket(MapItem.getMapId(bcj), MapItemSavedData.this.scale, MapItemSavedData.this.trackingPosition, MapItemSavedData.this.locked, (Collection<MapDecoration>)MapItemSavedData.this.decorations.values(), MapItemSavedData.this.colors, this.minDirtyX, this.minDirtyY, this.maxDirtyX + 1 - this.minDirtyX, this.maxDirtyY + 1 - this.minDirtyY);
            }
            if (this.tick++ % 5 == 0) {
                return new ClientboundMapItemDataPacket(MapItem.getMapId(bcj), MapItemSavedData.this.scale, MapItemSavedData.this.trackingPosition, MapItemSavedData.this.locked, (Collection<MapDecoration>)MapItemSavedData.this.decorations.values(), MapItemSavedData.this.colors, 0, 0, 0, 0);
            }
            return null;
        }
        
        public void markDirty(final int integer1, final int integer2) {
            if (this.dirtyData) {
                this.minDirtyX = Math.min(this.minDirtyX, integer1);
                this.minDirtyY = Math.min(this.minDirtyY, integer2);
                this.maxDirtyX = Math.max(this.maxDirtyX, integer1);
                this.maxDirtyY = Math.max(this.maxDirtyY, integer2);
            }
            else {
                this.dirtyData = true;
                this.minDirtyX = integer1;
                this.minDirtyY = integer2;
                this.maxDirtyX = integer1;
                this.maxDirtyY = integer2;
            }
        }
    }
}
