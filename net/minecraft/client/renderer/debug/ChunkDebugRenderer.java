package net.minecraft.client.renderer.debug;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.Camera;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.CompletableFuture;
import java.util.Iterator;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.ChunkPos;
import java.util.Map;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.Util;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public class ChunkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private double lastUpdateTime;
    private final int radius = 12;
    @Nullable
    private ChunkData data;
    
    public ChunkDebugRenderer(final Minecraft cyc) {
        this.lastUpdateTime = Double.MIN_VALUE;
        this.minecraft = cyc;
    }
    
    public void render(final long long1) {
        final double double4 = (double)Util.getNanos();
        if (double4 - this.lastUpdateTime > 3.0E9) {
            this.lastUpdateTime = double4;
            final IntegratedServer eac6 = this.minecraft.getSingleplayerServer();
            if (eac6 != null) {
                this.data = new ChunkData(eac6);
            }
            else {
                this.data = null;
            }
        }
        if (this.data != null) {
            GlStateManager.disableFog();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(2.0f);
            GlStateManager.disableTexture();
            GlStateManager.depthMask(false);
            final Map<ChunkPos, String> map6 = (Map<ChunkPos, String>)this.data.serverData.getNow(null);
            final double double5 = this.minecraft.gameRenderer.getMainCamera().getPosition().y * 0.85;
            for (final Map.Entry<ChunkPos, String> entry10 : this.data.clientData.entrySet()) {
                final ChunkPos bhd11 = (ChunkPos)entry10.getKey();
                String string12 = (String)entry10.getValue();
                if (map6 != null) {
                    string12 += (String)map6.get(bhd11);
                }
                final String[] arr13 = string12.split("\n");
                int integer14 = 0;
                for (final String string13 : arr13) {
                    DebugRenderer.renderFloatingText(string13, (bhd11.x << 4) + 8, double5 + integer14, (bhd11.z << 4) + 8, -1, 0.15f);
                    integer14 -= 2;
                }
            }
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
            GlStateManager.enableFog();
        }
    }
    
    final class ChunkData {
        private final Map<ChunkPos, String> clientData;
        private final CompletableFuture<Map<ChunkPos, String>> serverData;
        
        private ChunkData(final IntegratedServer eac) {
            final MultiPlayerLevel dkf4 = ChunkDebugRenderer.this.minecraft.level;
            final DimensionType byn5 = ChunkDebugRenderer.this.minecraft.level.dimension.getType();
            ServerLevel vk6;
            if (eac.getLevel(byn5) != null) {
                vk6 = eac.getLevel(byn5);
            }
            else {
                vk6 = null;
            }
            final Camera cxq7 = ChunkDebugRenderer.this.minecraft.gameRenderer.getMainCamera();
            final int integer8 = (int)cxq7.getPosition().x >> 4;
            final int integer9 = (int)cxq7.getPosition().z >> 4;
            final ImmutableMap.Builder<ChunkPos, String> builder10 = (ImmutableMap.Builder<ChunkPos, String>)ImmutableMap.builder();
            final ClientChunkCache dka11 = dkf4.getChunkSource();
            for (int integer10 = integer8 - 12; integer10 <= integer8 + 12; ++integer10) {
                for (int integer11 = integer9 - 12; integer11 <= integer9 + 12; ++integer11) {
                    final ChunkPos bhd14 = new ChunkPos(integer10, integer11);
                    String string15 = "";
                    final LevelChunk bxt16 = dka11.getChunk(integer10, integer11, false);
                    string15 += "Client: ";
                    if (bxt16 == null) {
                        string15 += "0n/a\n";
                    }
                    else {
                        string15 += (bxt16.isEmpty() ? " E" : "");
                        string15 += "\n";
                    }
                    builder10.put(bhd14, string15);
                }
            }
            this.clientData = (Map<ChunkPos, String>)builder10.build();
            this.serverData = eac.<Map<ChunkPos, String>>submit((java.util.function.Supplier<Map<ChunkPos, String>>)(() -> {
                final ImmutableMap.Builder<ChunkPos, String> builder5 = (ImmutableMap.Builder<ChunkPos, String>)ImmutableMap.builder();
                final ServerChunkCache vi6 = vk6.getChunkSource();
                for (int integer4 = integer8 - 12; integer4 <= integer8 + 12; ++integer4) {
                    for (int integer5 = integer9 - 12; integer5 <= integer9 + 12; ++integer5) {
                        final ChunkPos bhd9 = new ChunkPos(integer4, integer5);
                        builder5.put(bhd9, ("Server: " + vi6.getChunkDebugData(bhd9)));
                    }
                }
                return builder5.build();
            }));
        }
    }
}
