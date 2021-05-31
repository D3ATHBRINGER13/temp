package net.minecraft.client.gui.components;

import java.util.function.Consumer;
import java.util.EnumMap;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.server.level.ChunkHolder;
import com.mojang.datafixers.util.Either;
import net.minecraft.util.FrameTimer;
import net.minecraft.Util;
import net.minecraft.world.level.material.FluidState;
import java.util.Iterator;
import net.minecraft.world.level.block.state.properties.Property;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.world.level.chunk.ChunkStatus;
import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import net.minecraft.world.level.lighting.LevelLightEngine;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LightLayer;
import net.minecraft.util.Mth;
import java.util.Locale;
import net.minecraft.world.level.dimension.DimensionType;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.server.level.ServerLevel;
import java.util.Objects;
import com.google.common.collect.Lists;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import java.util.List;
import com.google.common.base.Strings;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.chunk.LevelChunk;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.Map;
import net.minecraft.client.gui.GuiComponent;

public class DebugScreenOverlay extends GuiComponent {
    private static final Map<Heightmap.Types, String> HEIGHTMAP_NAMES;
    private final Minecraft minecraft;
    private final Font font;
    private HitResult block;
    private HitResult liquid;
    @Nullable
    private ChunkPos lastPos;
    @Nullable
    private LevelChunk clientChunk;
    @Nullable
    private CompletableFuture<LevelChunk> serverChunk;
    
    public DebugScreenOverlay(final Minecraft cyc) {
        this.minecraft = cyc;
        this.font = cyc.font;
    }
    
    public void clearChunkCache() {
        this.serverChunk = null;
        this.clientChunk = null;
    }
    
    public void render() {
        this.minecraft.getProfiler().push("debug");
        GlStateManager.pushMatrix();
        final Entity aio2 = this.minecraft.getCameraEntity();
        this.block = aio2.pick(20.0, 0.0f, false);
        this.liquid = aio2.pick(20.0, 0.0f, true);
        this.drawGameInformation();
        this.drawSystemInformation();
        GlStateManager.popMatrix();
        if (this.minecraft.options.renderFpsChart) {
            final int integer3 = this.minecraft.window.getGuiScaledWidth();
            this.drawChart(this.minecraft.getFrameTimer(), 0, integer3 / 2, true);
            final IntegratedServer eac4 = this.minecraft.getSingleplayerServer();
            if (eac4 != null) {
                this.drawChart(eac4.getFrameTimer(), integer3 - Math.min(integer3 / 2, 240), integer3 / 2, false);
            }
        }
        this.minecraft.getProfiler().pop();
    }
    
    protected void drawGameInformation() {
        final List<String> list2 = this.getGameInformation();
        list2.add("");
        final boolean boolean3 = this.minecraft.getSingleplayerServer() != null;
        list2.add(new StringBuilder().append("Debug: Pie [shift]: ").append(this.minecraft.options.renderDebugCharts ? "visible" : "hidden").append(boolean3 ? " FPS + TPS" : " FPS").append(" [alt]: ").append(this.minecraft.options.renderFpsChart ? "visible" : "hidden").toString());
        list2.add("For help: press F3 + Q");
        for (int integer4 = 0; integer4 < list2.size(); ++integer4) {
            final String string5 = (String)list2.get(integer4);
            if (!Strings.isNullOrEmpty(string5)) {
                this.font.getClass();
                final int integer5 = 9;
                final int integer6 = this.font.width(string5);
                final int integer7 = 2;
                final int integer8 = 2 + integer5 * integer4;
                GuiComponent.fill(1, integer8 - 1, 2 + integer6 + 1, integer8 + integer5 - 1, -1873784752);
                this.font.draw(string5, 2.0f, (float)integer8, 14737632);
            }
        }
    }
    
    protected void drawSystemInformation() {
        final List<String> list2 = this.getSystemInformation();
        for (int integer3 = 0; integer3 < list2.size(); ++integer3) {
            final String string4 = (String)list2.get(integer3);
            if (!Strings.isNullOrEmpty(string4)) {
                this.font.getClass();
                final int integer4 = 9;
                final int integer5 = this.font.width(string4);
                final int integer6 = this.minecraft.window.getGuiScaledWidth() - 2 - integer5;
                final int integer7 = 2 + integer4 * integer3;
                GuiComponent.fill(integer6 - 1, integer7 - 1, integer6 + integer5 + 1, integer7 + integer4 - 1, -1873784752);
                this.font.draw(string4, (float)integer6, (float)integer7, 14737632);
            }
        }
    }
    
    protected List<String> getGameInformation() {
        final IntegratedServer eac3 = this.minecraft.getSingleplayerServer();
        final Connection jc4 = this.minecraft.getConnection().getConnection();
        final float float5 = jc4.getAverageSentPackets();
        final float float6 = jc4.getAverageReceivedPackets();
        String string2;
        if (eac3 != null) {
            string2 = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", new Object[] { eac3.getAverageTickTime(), float5, float6 });
        }
        else {
            string2 = String.format("\"%s\" server, %.0f tx, %.0f rx", new Object[] { this.minecraft.player.getServerBrand(), float5, float6 });
        }
        final BlockPos ew7 = new BlockPos(this.minecraft.getCameraEntity().x, this.minecraft.getCameraEntity().getBoundingBox().minY, this.minecraft.getCameraEntity().z);
        if (this.minecraft.showOnlyReducedInfo()) {
            return (List<String>)Lists.newArrayList((Object[])new String[] { "Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.minecraft.fpsString, string2, this.minecraft.levelRenderer.getChunkStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats(), "", String.format("Chunk-relative: %d %d %d", new Object[] { ew7.getX() & 0xF, ew7.getY() & 0xF, ew7.getZ() & 0xF }) });
        }
        final Entity aio8 = this.minecraft.getCameraEntity();
        final Direction fb9 = aio8.getDirection();
        String string3 = null;
        switch (fb9) {
            case NORTH: {
                string3 = "Towards negative Z";
                break;
            }
            case SOUTH: {
                string3 = "Towards positive Z";
                break;
            }
            case WEST: {
                string3 = "Towards negative X";
                break;
            }
            case EAST: {
                string3 = "Towards positive X";
                break;
            }
            default: {
                string3 = "Invalid";
                break;
            }
        }
        final ChunkPos bhd11 = new ChunkPos(ew7);
        if (!Objects.equals(this.lastPos, bhd11)) {
            this.lastPos = bhd11;
            this.clearChunkCache();
        }
        final Level bhr12 = this.getLevel();
        final LongSet longSet13 = (bhr12 instanceof ServerLevel) ? ((ServerLevel)bhr12).getForcedChunks() : LongSets.EMPTY_SET;
        final List<String> list14 = (List<String>)Lists.newArrayList((Object[])new String[] { "Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : ("/" + this.minecraft.getVersionType())) + ")", this.minecraft.fpsString, string2, this.minecraft.levelRenderer.getChunkStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats() });
        final String string4 = this.getServerChunkStats();
        if (string4 != null) {
            list14.add(string4);
        }
        list14.add((DimensionType.getName(this.minecraft.level.dimension.getType()).toString() + " FC: " + Integer.toString(longSet13.size())));
        list14.add("");
        list14.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", new Object[] { this.minecraft.getCameraEntity().x, this.minecraft.getCameraEntity().getBoundingBox().minY, this.minecraft.getCameraEntity().z }));
        list14.add(String.format("Block: %d %d %d", new Object[] { ew7.getX(), ew7.getY(), ew7.getZ() }));
        list14.add(String.format("Chunk: %d %d %d in %d %d %d", new Object[] { ew7.getX() & 0xF, ew7.getY() & 0xF, ew7.getZ() & 0xF, ew7.getX() >> 4, ew7.getY() >> 4, ew7.getZ() >> 4 }));
        list14.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", new Object[] { fb9, string3, Mth.wrapDegrees(aio8.yRot), Mth.wrapDegrees(aio8.xRot) }));
        if (this.minecraft.level != null) {
            if (this.minecraft.level.hasChunkAt(ew7)) {
                final LevelChunk bxt16 = this.getClientChunk();
                if (bxt16.isEmpty()) {
                    list14.add("Waiting for chunk...");
                }
                else {
                    list14.add(new StringBuilder().append("Client Light: ").append(bxt16.getRawBrightness(ew7, 0)).append(" (").append(this.minecraft.level.getBrightness(LightLayer.SKY, ew7)).append(" sky, ").append(this.minecraft.level.getBrightness(LightLayer.BLOCK, ew7)).append(" block)").toString());
                    final LevelChunk bxt17 = this.getServerChunk();
                    if (bxt17 != null) {
                        final LevelLightEngine clb18 = bhr12.getChunkSource().getLightEngine();
                        list14.add(new StringBuilder().append("Server Light: (").append(clb18.getLayerListener(LightLayer.SKY).getLightValue(ew7)).append(" sky, ").append(clb18.getLayerListener(LightLayer.BLOCK).getLightValue(ew7)).append(" block)").toString());
                    }
                    final StringBuilder stringBuilder18 = new StringBuilder("CH");
                    for (final Heightmap.Types a22 : Heightmap.Types.values()) {
                        if (a22.sendToClient()) {
                            stringBuilder18.append(" ").append((String)DebugScreenOverlay.HEIGHTMAP_NAMES.get(a22)).append(": ").append(bxt16.getHeight(a22, ew7.getX(), ew7.getZ()));
                        }
                    }
                    list14.add(stringBuilder18.toString());
                    if (bxt17 != null) {
                        stringBuilder18.setLength(0);
                        stringBuilder18.append("SH");
                        for (final Heightmap.Types a22 : Heightmap.Types.values()) {
                            if (a22.keepAfterWorldgen()) {
                                stringBuilder18.append(" ").append((String)DebugScreenOverlay.HEIGHTMAP_NAMES.get(a22)).append(": ").append(bxt17.getHeight(a22, ew7.getX(), ew7.getZ()));
                            }
                        }
                        list14.add(stringBuilder18.toString());
                    }
                    if (ew7.getY() >= 0 && ew7.getY() < 256) {
                        list14.add(new StringBuilder().append("Biome: ").append((Object)Registry.BIOME.getKey(bxt16.getBiome(ew7))).toString());
                        long long19 = 0L;
                        float float7 = 0.0f;
                        if (bxt17 != null) {
                            float7 = bhr12.getMoonBrightness();
                            long19 = bxt17.getInhabitedTime();
                        }
                        final DifficultyInstance ahh22 = new DifficultyInstance(bhr12.getDifficulty(), bhr12.getDayTime(), long19, float7);
                        list14.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", new Object[] { ahh22.getEffectiveDifficulty(), ahh22.getSpecialMultiplier(), this.minecraft.level.getDayTime() / 24000L }));
                    }
                }
            }
            else {
                list14.add("Outside of world...");
            }
        }
        else {
            list14.add("Outside of world...");
        }
        if (this.minecraft.gameRenderer != null && this.minecraft.gameRenderer.postEffectActive()) {
            list14.add(("Shader: " + this.minecraft.gameRenderer.currentEffect().getName()));
        }
        if (this.block.getType() == HitResult.Type.BLOCK) {
            final BlockPos ew8 = ((BlockHitResult)this.block).getBlockPos();
            list14.add(String.format("Looking at block: %d %d %d", new Object[] { ew8.getX(), ew8.getY(), ew8.getZ() }));
        }
        if (this.liquid.getType() == HitResult.Type.BLOCK) {
            final BlockPos ew8 = ((BlockHitResult)this.liquid).getBlockPos();
            list14.add(String.format("Looking at liquid: %d %d %d", new Object[] { ew8.getX(), ew8.getY(), ew8.getZ() }));
        }
        list14.add(this.minecraft.getSoundManager().getDebugString());
        return list14;
    }
    
    @Nullable
    private String getServerChunkStats() {
        final IntegratedServer eac2 = this.minecraft.getSingleplayerServer();
        if (eac2 != null) {
            final ServerLevel vk3 = eac2.getLevel(this.minecraft.level.getDimension().getType());
            if (vk3 != null) {
                return vk3.gatherChunkSourceStats();
            }
        }
        return null;
    }
    
    private Level getLevel() {
        return (Level)DataFixUtils.orElse(Optional.ofNullable(this.minecraft.getSingleplayerServer()).map(eac -> eac.getLevel(this.minecraft.level.dimension.getType())), this.minecraft.level);
    }
    
    @Nullable
    private LevelChunk getServerChunk() {
        if (this.serverChunk == null) {
            final IntegratedServer eac2 = this.minecraft.getSingleplayerServer();
            if (eac2 != null) {
                final ServerLevel vk3 = eac2.getLevel(this.minecraft.level.dimension.getType());
                if (vk3 != null) {
                    this.serverChunk = (CompletableFuture<LevelChunk>)vk3.getChunkSource().getChunkFuture(this.lastPos.x, this.lastPos.z, ChunkStatus.FULL, false).thenApply(either -> (LevelChunk)either.map(bxh -> (LevelChunk)bxh, a -> null));
                }
            }
            if (this.serverChunk == null) {
                this.serverChunk = (CompletableFuture<LevelChunk>)CompletableFuture.completedFuture(this.getClientChunk());
            }
        }
        return (LevelChunk)this.serverChunk.getNow(null);
    }
    
    private LevelChunk getClientChunk() {
        if (this.clientChunk == null) {
            this.clientChunk = this.minecraft.level.getChunk(this.lastPos.x, this.lastPos.z);
        }
        return this.clientChunk;
    }
    
    protected List<String> getSystemInformation() {
        final long long2 = Runtime.getRuntime().maxMemory();
        final long long3 = Runtime.getRuntime().totalMemory();
        final long long4 = Runtime.getRuntime().freeMemory();
        final long long5 = long3 - long4;
        final List<String> list10 = (List<String>)Lists.newArrayList((Object[])new String[] { String.format("Java: %s %dbit", new Object[] { System.getProperty("java.version"), this.minecraft.is64Bit() ? 64 : 32 }), String.format("Mem: % 2d%% %03d/%03dMB", new Object[] { long5 * 100L / long2, bytesToMegabytes(long5), bytesToMegabytes(long2) }), String.format("Allocated: % 2d%% %03dMB", new Object[] { long3 * 100L / long2, bytesToMegabytes(long3) }), "", String.format("CPU: %s", new Object[] { GLX.getCpuInfo() }), "", String.format("Display: %dx%d (%s)", new Object[] { Minecraft.getInstance().window.getWidth(), Minecraft.getInstance().window.getHeight(), GLX.getVendor() }), GLX.getRenderer(), GLX.getOpenGLVersion() });
        if (this.minecraft.showOnlyReducedInfo()) {
            return list10;
        }
        if (this.block.getType() == HitResult.Type.BLOCK) {
            final BlockPos ew11 = ((BlockHitResult)this.block).getBlockPos();
            final BlockState bvt12 = this.minecraft.level.getBlockState(ew11);
            list10.add("");
            list10.add(new StringBuilder().append((Object)ChatFormatting.UNDERLINE).append("Targeted Block").toString());
            list10.add(String.valueOf((Object)Registry.BLOCK.getKey(bvt12.getBlock())));
            for (final Map.Entry<Property<?>, Comparable<?>> entry14 : bvt12.getValues().entrySet()) {
                list10.add(this.getPropertyValueString(entry14));
            }
            for (final ResourceLocation qv14 : this.minecraft.getConnection().getTags().getBlocks().getMatchingTags(bvt12.getBlock())) {
                list10.add(new StringBuilder().append("#").append((Object)qv14).toString());
            }
        }
        if (this.liquid.getType() == HitResult.Type.BLOCK) {
            final BlockPos ew11 = ((BlockHitResult)this.liquid).getBlockPos();
            final FluidState clk12 = this.minecraft.level.getFluidState(ew11);
            list10.add("");
            list10.add(new StringBuilder().append((Object)ChatFormatting.UNDERLINE).append("Targeted Fluid").toString());
            list10.add(String.valueOf((Object)Registry.FLUID.getKey(clk12.getType())));
            for (final Map.Entry<Property<?>, Comparable<?>> entry14 : clk12.getValues().entrySet()) {
                list10.add(this.getPropertyValueString(entry14));
            }
            for (final ResourceLocation qv14 : this.minecraft.getConnection().getTags().getFluids().getMatchingTags(clk12.getType())) {
                list10.add(new StringBuilder().append("#").append((Object)qv14).toString());
            }
        }
        final Entity aio11 = this.minecraft.crosshairPickEntity;
        if (aio11 != null) {
            list10.add("");
            list10.add(new StringBuilder().append((Object)ChatFormatting.UNDERLINE).append("Targeted Entity").toString());
            list10.add(String.valueOf((Object)Registry.ENTITY_TYPE.getKey(aio11.getType())));
        }
        return list10;
    }
    
    private String getPropertyValueString(final Map.Entry<Property<?>, Comparable<?>> entry) {
        final Property<?> bww3 = entry.getKey();
        final Comparable<?> comparable4 = entry.getValue();
        String string5 = Util.getPropertyName(bww3, comparable4);
        if (Boolean.TRUE.equals(comparable4)) {
            string5 = ChatFormatting.GREEN + string5;
        }
        else if (Boolean.FALSE.equals(comparable4)) {
            string5 = ChatFormatting.RED + string5;
        }
        return bww3.getName() + ": " + string5;
    }
    
    private void drawChart(final FrameTimer zr, final int integer2, final int integer3, final boolean boolean4) {
        GlStateManager.disableDepthTest();
        final int integer4 = zr.getLogStart();
        final int integer5 = zr.getLogEnd();
        final long[] arr8 = zr.getLog();
        int integer6 = integer4;
        int integer7 = integer2;
        final int integer8 = Math.max(0, arr8.length - integer3);
        final int integer9 = arr8.length - integer8;
        integer6 = zr.wrapIndex(integer6 + integer8);
        long long13 = 0L;
        int integer10 = Integer.MAX_VALUE;
        int integer11 = Integer.MIN_VALUE;
        for (int integer12 = 0; integer12 < integer9; ++integer12) {
            final int integer13 = (int)(arr8[zr.wrapIndex(integer6 + integer12)] / 1000000L);
            integer10 = Math.min(integer10, integer13);
            integer11 = Math.max(integer11, integer13);
            long13 += integer13;
        }
        int integer12 = this.minecraft.window.getGuiScaledHeight();
        GuiComponent.fill(integer2, integer12 - 60, integer2 + integer9, integer12, -1873784752);
        while (integer6 != integer5) {
            final int integer13 = zr.scaleSampleTo(arr8[integer6], boolean4 ? 30 : 60, boolean4 ? 60 : 20);
            final int integer14 = boolean4 ? 100 : 60;
            final int integer15 = this.getSampleColor(Mth.clamp(integer13, 0, integer14), 0, integer14 / 2, integer14);
            this.vLine(integer7, integer12, integer12 - integer13, integer15);
            ++integer7;
            integer6 = zr.wrapIndex(integer6 + 1);
        }
        if (boolean4) {
            GuiComponent.fill(integer2 + 1, integer12 - 30 + 1, integer2 + 14, integer12 - 30 + 10, -1873784752);
            this.font.draw("60 FPS", (float)(integer2 + 2), (float)(integer12 - 30 + 2), 14737632);
            this.hLine(integer2, integer2 + integer9 - 1, integer12 - 30, -1);
            GuiComponent.fill(integer2 + 1, integer12 - 60 + 1, integer2 + 14, integer12 - 60 + 10, -1873784752);
            this.font.draw("30 FPS", (float)(integer2 + 2), (float)(integer12 - 60 + 2), 14737632);
            this.hLine(integer2, integer2 + integer9 - 1, integer12 - 60, -1);
        }
        else {
            GuiComponent.fill(integer2 + 1, integer12 - 60 + 1, integer2 + 14, integer12 - 60 + 10, -1873784752);
            this.font.draw("20 TPS", (float)(integer2 + 2), (float)(integer12 - 60 + 2), 14737632);
            this.hLine(integer2, integer2 + integer9 - 1, integer12 - 60, -1);
        }
        this.hLine(integer2, integer2 + integer9 - 1, integer12 - 1, -1);
        this.vLine(integer2, integer12 - 60, integer12, -1);
        this.vLine(integer2 + integer9 - 1, integer12 - 60, integer12, -1);
        if (boolean4 && this.minecraft.options.framerateLimit > 0 && this.minecraft.options.framerateLimit <= 250) {
            this.hLine(integer2, integer2 + integer9 - 1, integer12 - 1 - (int)(1800.0 / this.minecraft.options.framerateLimit), -16711681);
        }
        final String string18 = new StringBuilder().append(integer10).append(" ms min").toString();
        final String string19 = new StringBuilder().append(long13 / integer9).append(" ms avg").toString();
        final String string20 = new StringBuilder().append(integer11).append(" ms max").toString();
        final Font font = this.font;
        final String string21 = string18;
        final float float2 = (float)(integer2 + 2);
        final int n = integer12 - 60;
        this.font.getClass();
        font.drawShadow(string21, float2, (float)(n - 9), 14737632);
        final Font font2 = this.font;
        final String string22 = string19;
        final float float3 = (float)(integer2 + integer9 / 2 - this.font.width(string19) / 2);
        final int n2 = integer12 - 60;
        this.font.getClass();
        font2.drawShadow(string22, float3, (float)(n2 - 9), 14737632);
        final Font font3 = this.font;
        final String string23 = string20;
        final float float4 = (float)(integer2 + integer9 - this.font.width(string20));
        final int n3 = integer12 - 60;
        this.font.getClass();
        font3.drawShadow(string23, float4, (float)(n3 - 9), 14737632);
        GlStateManager.enableDepthTest();
    }
    
    private int getSampleColor(final int integer1, final int integer2, final int integer3, final int integer4) {
        if (integer1 < integer3) {
            return this.colorLerp(-16711936, -256, integer1 / (float)integer3);
        }
        return this.colorLerp(-256, -65536, (integer1 - integer3) / (float)(integer4 - integer3));
    }
    
    private int colorLerp(final int integer1, final int integer2, final float float3) {
        final int integer3 = integer1 >> 24 & 0xFF;
        final int integer4 = integer1 >> 16 & 0xFF;
        final int integer5 = integer1 >> 8 & 0xFF;
        final int integer6 = integer1 & 0xFF;
        final int integer7 = integer2 >> 24 & 0xFF;
        final int integer8 = integer2 >> 16 & 0xFF;
        final int integer9 = integer2 >> 8 & 0xFF;
        final int integer10 = integer2 & 0xFF;
        final int integer11 = Mth.clamp((int)Mth.lerp(float3, (float)integer3, (float)integer7), 0, 255);
        final int integer12 = Mth.clamp((int)Mth.lerp(float3, (float)integer4, (float)integer8), 0, 255);
        final int integer13 = Mth.clamp((int)Mth.lerp(float3, (float)integer5, (float)integer9), 0, 255);
        final int integer14 = Mth.clamp((int)Mth.lerp(float3, (float)integer6, (float)integer10), 0, 255);
        return integer11 << 24 | integer12 << 16 | integer13 << 8 | integer14;
    }
    
    private static long bytesToMegabytes(final long long1) {
        return long1 / 1024L / 1024L;
    }
    
    static {
        HEIGHTMAP_NAMES = Util.<Map>make((Map)new EnumMap((Class)Heightmap.Types.class), (java.util.function.Consumer<Map>)(enumMap -> {
            enumMap.put((Enum)Heightmap.Types.WORLD_SURFACE_WG, "SW");
            enumMap.put((Enum)Heightmap.Types.WORLD_SURFACE, "S");
            enumMap.put((Enum)Heightmap.Types.OCEAN_FLOOR_WG, "OW");
            enumMap.put((Enum)Heightmap.Types.OCEAN_FLOOR, "O");
            enumMap.put((Enum)Heightmap.Types.MOTION_BLOCKING, "M");
            enumMap.put((Enum)Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, "ML");
        }));
    }
}
