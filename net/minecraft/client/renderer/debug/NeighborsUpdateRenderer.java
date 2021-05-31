package net.minecraft.client.renderer.debug;

import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.AABB;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import net.minecraft.core.BlockPos;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class NeighborsUpdateRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<Long, Map<BlockPos, Integer>> lastUpdate;
    
    NeighborsUpdateRenderer(final Minecraft cyc) {
        this.lastUpdate = (Map<Long, Map<BlockPos, Integer>>)Maps.newTreeMap((Comparator)Ordering.natural().reverse());
        this.minecraft = cyc;
    }
    
    public void addUpdate(final long long1, final BlockPos ew) {
        Map<BlockPos, Integer> map5 = (Map<BlockPos, Integer>)this.lastUpdate.get(long1);
        if (map5 == null) {
            map5 = (Map<BlockPos, Integer>)Maps.newHashMap();
            this.lastUpdate.put(long1, map5);
        }
        Integer integer6 = (Integer)map5.get(ew);
        if (integer6 == null) {
            integer6 = 0;
        }
        map5.put(ew, (integer6 + 1));
    }
    
    public void render(final long long1) {
        final long long2 = this.minecraft.level.getGameTime();
        final Camera cxq6 = this.minecraft.gameRenderer.getMainCamera();
        final double double7 = cxq6.getPosition().x;
        final double double8 = cxq6.getPosition().y;
        final double double9 = cxq6.getPosition().z;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.lineWidth(2.0f);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        final int integer13 = 200;
        final double double10 = 0.0025;
        final Set<BlockPos> set16 = (Set<BlockPos>)Sets.newHashSet();
        final Map<BlockPos, Integer> map17 = (Map<BlockPos, Integer>)Maps.newHashMap();
        final Iterator<Map.Entry<Long, Map<BlockPos, Integer>>> iterator18 = (Iterator<Map.Entry<Long, Map<BlockPos, Integer>>>)this.lastUpdate.entrySet().iterator();
        while (iterator18.hasNext()) {
            final Map.Entry<Long, Map<BlockPos, Integer>> entry19 = (Map.Entry<Long, Map<BlockPos, Integer>>)iterator18.next();
            final Long long3 = (Long)entry19.getKey();
            final Map<BlockPos, Integer> map18 = (Map<BlockPos, Integer>)entry19.getValue();
            final long long4 = long2 - long3;
            if (long4 > 200L) {
                iterator18.remove();
            }
            else {
                for (final Map.Entry<BlockPos, Integer> entry20 : map18.entrySet()) {
                    final BlockPos ew26 = (BlockPos)entry20.getKey();
                    final Integer integer14 = (Integer)entry20.getValue();
                    if (set16.add(ew26)) {
                        LevelRenderer.renderLineBox(new AABB(BlockPos.ZERO).inflate(0.002).deflate(0.0025 * long4).move(ew26.getX(), ew26.getY(), ew26.getZ()).move(-double7, -double8, -double9), 1.0f, 1.0f, 1.0f, 1.0f);
                        map17.put(ew26, integer14);
                    }
                }
            }
        }
        for (final Map.Entry<BlockPos, Integer> entry21 : map17.entrySet()) {
            final BlockPos ew27 = (BlockPos)entry21.getKey();
            final Integer integer15 = (Integer)entry21.getValue();
            DebugRenderer.renderFloatingText(String.valueOf(integer15), ew27.getX(), ew27.getY(), ew27.getZ(), -1);
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}
