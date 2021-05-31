package net.minecraft.client.renderer.debug;

import javax.annotation.Nullable;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.entity.Entity;
import java.util.Collection;
import net.minecraft.world.entity.player.Player;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.network.protocol.game.DebugVillagerNameGenerator;
import net.minecraft.client.Camera;
import net.minecraft.core.Position;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Iterator;
import net.minecraft.core.Vec3i;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.UUID;
import net.minecraft.core.SectionPos;
import java.util.Set;
import net.minecraft.core.BlockPos;
import java.util.Map;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;

public class VillageDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private static final Logger LOGGER;
    private final Minecraft minecraft;
    private final Map<BlockPos, PoiInfo> pois;
    private final Set<SectionPos> villageSections;
    private final Map<UUID, BrainDump> brainDumpsPerEntity;
    private UUID lastLookedAtUuid;
    
    public VillageDebugRenderer(final Minecraft cyc) {
        this.pois = (Map<BlockPos, PoiInfo>)Maps.newHashMap();
        this.villageSections = (Set<SectionPos>)Sets.newHashSet();
        this.brainDumpsPerEntity = (Map<UUID, BrainDump>)Maps.newHashMap();
        this.minecraft = cyc;
    }
    
    public void clear() {
        this.pois.clear();
        this.villageSections.clear();
        this.brainDumpsPerEntity.clear();
        this.lastLookedAtUuid = null;
    }
    
    public void addPoi(final PoiInfo b) {
        this.pois.put(b.pos, b);
    }
    
    public void removePoi(final BlockPos ew) {
        this.pois.remove(ew);
    }
    
    public void setFreeTicketCount(final BlockPos ew, final int integer) {
        final PoiInfo b4 = (PoiInfo)this.pois.get(ew);
        if (b4 == null) {
            VillageDebugRenderer.LOGGER.warn(new StringBuilder().append("Strange, setFreeTicketCount was called for an unknown POI: ").append(ew).toString());
            return;
        }
        b4.freeTicketCount = integer;
    }
    
    public void setVillageSection(final SectionPos fp) {
        this.villageSections.add(fp);
    }
    
    public void setNotVillageSection(final SectionPos fp) {
        this.villageSections.remove(fp);
    }
    
    public void addOrUpdateBrainDump(final BrainDump a) {
        this.brainDumpsPerEntity.put(a.uuid, a);
    }
    
    public void render(final long long1) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        this.doRender();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        if (!this.minecraft.player.isSpectator()) {
            this.updateLastLookedAtUuid();
        }
    }
    
    private void doRender() {
        final BlockPos ew2 = this.getCamera().getBlockPosition();
        this.villageSections.forEach(fp -> {
            if (ew2.closerThan(fp.center(), 60.0)) {
                highlightVillageSection(fp);
            }
        });
        this.brainDumpsPerEntity.values().forEach(a -> {
            if (this.isPlayerCloseEnoughToMob(a)) {
                this.renderVillagerInfo(a);
            }
        });
        for (final BlockPos ew3 : this.pois.keySet()) {
            if (ew2.closerThan(ew3, 30.0)) {
                highlightPoi(ew3);
            }
        }
        this.pois.values().forEach(b -> {
            if (ew2.closerThan(b.pos, 30.0)) {
                this.renderPoiInfo(b);
            }
        });
        this.getGhostPois().forEach((ew2, list) -> {
            if (ew2.closerThan(ew2, 30.0)) {
                this.renderGhostPoi(ew2, (List<String>)list);
            }
        });
    }
    
    private static void highlightVillageSection(final SectionPos fp) {
        final float float2 = 1.0f;
        final BlockPos ew3 = fp.center();
        final BlockPos ew4 = ew3.offset(-1.0, -1.0, -1.0);
        final BlockPos ew5 = ew3.offset(1.0, 1.0, 1.0);
        DebugRenderer.renderFilledBox(ew4, ew5, 0.2f, 1.0f, 0.2f, 0.15f);
    }
    
    private static void highlightPoi(final BlockPos ew) {
        final float float2 = 0.05f;
        DebugRenderer.renderFilledBox(ew, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }
    
    private void renderGhostPoi(final BlockPos ew, final List<String> list) {
        final float float4 = 0.05f;
        DebugRenderer.renderFilledBox(ew, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        renderTextOverPos(new StringBuilder().append("").append(list).toString(), ew, 0, -256);
        renderTextOverPos("Ghost POI", ew, 1, -65536);
    }
    
    private void renderPoiInfo(final PoiInfo b) {
        int integer3 = 0;
        if (this.getTicketHolderNames(b).size() < 4) {
            renderTextOverPoi(new StringBuilder().append("").append(this.getTicketHolderNames(b)).toString(), b, integer3, -256);
        }
        else {
            renderTextOverPoi(new StringBuilder().append("").append(this.getTicketHolderNames(b).size()).append(" ticket holders").toString(), b, integer3, -256);
        }
        ++integer3;
        renderTextOverPoi(new StringBuilder().append("Free tickets: ").append(b.freeTicketCount).toString(), b, integer3, -256);
        ++integer3;
        renderTextOverPoi(b.type, b, integer3, -1);
    }
    
    private void renderPath(final BrainDump a) {
        if (a.path != null) {
            PathfindingRenderer.renderPath(this.getCamera(), a.path, 0.5f, false, false);
        }
    }
    
    private void renderVillagerInfo(final BrainDump a) {
        final boolean boolean3 = this.isVillagerSelected(a);
        int integer4 = 0;
        renderTextOverMob(a.pos, integer4, a.name, -1, 0.03f);
        ++integer4;
        if (boolean3) {
            renderTextOverMob(a.pos, integer4, a.profession + " " + a.xp + "xp", -1, 0.02f);
            ++integer4;
        }
        if (boolean3 && !a.inventory.equals("")) {
            renderTextOverMob(a.pos, integer4, a.inventory, -98404, 0.02f);
            ++integer4;
        }
        if (boolean3) {
            for (final String string6 : a.behaviors) {
                renderTextOverMob(a.pos, integer4, string6, -16711681, 0.02f);
                ++integer4;
            }
        }
        if (boolean3) {
            for (final String string6 : a.activities) {
                renderTextOverMob(a.pos, integer4, string6, -16711936, 0.02f);
                ++integer4;
            }
        }
        if (a.wantsGolem) {
            renderTextOverMob(a.pos, integer4, "Wants Golem", -23296, 0.02f);
            ++integer4;
        }
        if (boolean3) {
            for (final String string6 : a.gossips) {
                if (string6.startsWith(a.name)) {
                    renderTextOverMob(a.pos, integer4, string6, -1, 0.02f);
                }
                else {
                    renderTextOverMob(a.pos, integer4, string6, -23296, 0.02f);
                }
                ++integer4;
            }
        }
        if (boolean3) {
            for (final String string6 : Lists.reverse((List)a.memories)) {
                renderTextOverMob(a.pos, integer4, string6, -3355444, 0.02f);
                ++integer4;
            }
        }
        if (boolean3) {
            this.renderPath(a);
        }
    }
    
    private static void renderTextOverPoi(final String string, final PoiInfo b, final int integer3, final int integer4) {
        final BlockPos ew5 = b.pos;
        renderTextOverPos(string, ew5, integer3, integer4);
    }
    
    private static void renderTextOverPos(final String string, final BlockPos ew, final int integer3, final int integer4) {
        final double double5 = 1.3;
        final double double6 = 0.2;
        final double double7 = ew.getX() + 0.5;
        final double double8 = ew.getY() + 1.3 + integer3 * 0.2;
        final double double9 = ew.getZ() + 0.5;
        DebugRenderer.renderFloatingText(string, double7, double8, double9, integer4, 0.02f, true, 0.0f, true);
    }
    
    private static void renderTextOverMob(final Position fl, final int integer2, final String string, final int integer4, final float float5) {
        final double double6 = 2.4;
        final double double7 = 0.25;
        final BlockPos ew10 = new BlockPos(fl);
        final double double8 = ew10.getX() + 0.5;
        final double double9 = fl.y() + 2.4 + integer2 * 0.25;
        final double double10 = ew10.getZ() + 0.5;
        final float float6 = 0.5f;
        DebugRenderer.renderFloatingText(string, double8, double9, double10, integer4, float5, false, 0.5f, true);
    }
    
    private Camera getCamera() {
        return this.minecraft.gameRenderer.getMainCamera();
    }
    
    private Set<String> getTicketHolderNames(final PoiInfo b) {
        return (Set<String>)this.getTicketHolders(b.pos).stream().map(DebugVillagerNameGenerator::getVillagerName).collect(Collectors.toSet());
    }
    
    private boolean isVillagerSelected(final BrainDump a) {
        return Objects.equals(this.lastLookedAtUuid, a.uuid);
    }
    
    private boolean isPlayerCloseEnoughToMob(final BrainDump a) {
        final Player awg3 = this.minecraft.player;
        final BlockPos ew4 = new BlockPos(awg3.x, a.pos.y(), awg3.z);
        final BlockPos ew5 = new BlockPos(a.pos);
        return ew4.closerThan(ew5, 30.0);
    }
    
    private Collection<UUID> getTicketHolders(final BlockPos ew) {
        return (Collection<UUID>)this.brainDumpsPerEntity.values().stream().filter(a -> a.hasPoi(ew)).map(BrainDump::getUuid).collect(Collectors.toSet());
    }
    
    private Map<BlockPos, List<String>> getGhostPois() {
        final Map<BlockPos, List<String>> map2 = (Map<BlockPos, List<String>>)Maps.newHashMap();
        for (final BrainDump a4 : this.brainDumpsPerEntity.values()) {
            for (final BlockPos ew6 : a4.pois) {
                if (!this.pois.containsKey(ew6)) {
                    List<String> list7 = (List<String>)map2.get(ew6);
                    if (list7 == null) {
                        list7 = (List<String>)Lists.newArrayList();
                        map2.put(ew6, list7);
                    }
                    list7.add(a4.name);
                }
            }
        }
        return map2;
    }
    
    private void updateLastLookedAtUuid() {
        DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent(aio -> this.lastLookedAtUuid = aio.getUUID());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class PoiInfo {
        public final BlockPos pos;
        public String type;
        public int freeTicketCount;
        
        public PoiInfo(final BlockPos ew, final String string, final int integer) {
            this.pos = ew;
            this.type = string;
            this.freeTicketCount = integer;
        }
    }
    
    public static class BrainDump {
        public final UUID uuid;
        public final int id;
        public final String name;
        public final String profession;
        public final int xp;
        public final Position pos;
        public final String inventory;
        public final Path path;
        public final boolean wantsGolem;
        public final List<String> activities;
        public final List<String> behaviors;
        public final List<String> memories;
        public final List<String> gossips;
        public final Set<BlockPos> pois;
        
        public BrainDump(final UUID uUID, final int integer2, final String string3, final String string4, final int integer5, final Position fl, final String string7, @Nullable final Path cnr, final boolean boolean9) {
            this.activities = (List<String>)Lists.newArrayList();
            this.behaviors = (List<String>)Lists.newArrayList();
            this.memories = (List<String>)Lists.newArrayList();
            this.gossips = (List<String>)Lists.newArrayList();
            this.pois = (Set<BlockPos>)Sets.newHashSet();
            this.uuid = uUID;
            this.id = integer2;
            this.name = string3;
            this.profession = string4;
            this.xp = integer5;
            this.pos = fl;
            this.inventory = string7;
            this.path = cnr;
            this.wantsGolem = boolean9;
        }
        
        private boolean hasPoi(final BlockPos ew) {
            return this.pois.stream().anyMatch(ew::equals);
        }
        
        public UUID getUuid() {
            return this.uuid;
        }
    }
}
