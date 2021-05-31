package net.minecraft.client.gui.screens;

import java.util.function.Consumer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.world.level.chunk.ChunkStatus;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.server.level.progress.StoringChunkProgressListener;

public class LevelLoadingScreen extends Screen {
    private final StoringChunkProgressListener progressListener;
    private long lastNarration;
    private static final Object2IntMap<ChunkStatus> COLORS;
    
    public LevelLoadingScreen(final StoringChunkProgressListener vx) {
        super(NarratorChatListener.NO_TITLE);
        this.lastNarration = -1L;
        this.progressListener = vx;
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public void removed() {
        NarratorChatListener.INSTANCE.sayNow(I18n.get("narrator.loading.done"));
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        final String string5 = new StringBuilder().append(Mth.clamp(this.progressListener.getProgress(), 0, 100)).append("%").toString();
        final long long6 = Util.getMillis();
        if (long6 - this.lastNarration > 2000L) {
            this.lastNarration = long6;
            NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.loading", new Object[] { string5 }).getString());
        }
        final int integer3 = this.width / 2;
        final int integer4 = this.height / 2;
        final int integer5 = 30;
        renderChunks(this.progressListener, integer3, integer4 + 30, 2, 0);
        final Font font = this.font;
        final String string6 = string5;
        final int integer6 = integer3;
        final int n = integer4;
        this.font.getClass();
        this.drawCenteredString(font, string6, integer6, n - 9 / 2 - 30, 16777215);
    }
    
    public static void renderChunks(final StoringChunkProgressListener vx, final int integer2, final int integer3, final int integer4, final int integer5) {
        final int integer6 = integer4 + integer5;
        final int integer7 = vx.getFullDiameter();
        final int integer8 = integer7 * integer6 - integer5;
        final int integer9 = vx.getDiameter();
        final int integer10 = integer9 * integer6 - integer5;
        final int integer11 = integer2 - integer10 / 2;
        final int integer12 = integer3 - integer10 / 2;
        final int integer13 = integer8 / 2 + 1;
        final int integer14 = -16772609;
        if (integer5 != 0) {
            GuiComponent.fill(integer2 - integer13, integer3 - integer13, integer2 - integer13 + 1, integer3 + integer13, -16772609);
            GuiComponent.fill(integer2 + integer13 - 1, integer3 - integer13, integer2 + integer13, integer3 + integer13, -16772609);
            GuiComponent.fill(integer2 - integer13, integer3 - integer13, integer2 + integer13, integer3 - integer13 + 1, -16772609);
            GuiComponent.fill(integer2 - integer13, integer3 + integer13 - 1, integer2 + integer13, integer3 + integer13, -16772609);
        }
        for (int integer15 = 0; integer15 < integer9; ++integer15) {
            for (int integer16 = 0; integer16 < integer9; ++integer16) {
                final ChunkStatus bxm17 = vx.getStatus(integer15, integer16);
                final int integer17 = integer11 + integer15 * integer6;
                final int integer18 = integer12 + integer16 * integer6;
                GuiComponent.fill(integer17, integer18, integer17 + integer4, integer18 + integer4, LevelLoadingScreen.COLORS.getInt(bxm17) | 0xFF000000);
            }
        }
    }
    
    static {
        COLORS = Util.<Object2IntMap>make((Object2IntMap)new Object2IntOpenHashMap(), (java.util.function.Consumer<Object2IntMap>)(object2IntOpenHashMap -> {
            object2IntOpenHashMap.defaultReturnValue(0);
            object2IntOpenHashMap.put(ChunkStatus.EMPTY, 5526612);
            object2IntOpenHashMap.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
            object2IntOpenHashMap.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
            object2IntOpenHashMap.put(ChunkStatus.BIOMES, 8434258);
            object2IntOpenHashMap.put(ChunkStatus.NOISE, 13750737);
            object2IntOpenHashMap.put(ChunkStatus.SURFACE, 7497737);
            object2IntOpenHashMap.put(ChunkStatus.CARVERS, 7169628);
            object2IntOpenHashMap.put(ChunkStatus.LIQUID_CARVERS, 3159410);
            object2IntOpenHashMap.put(ChunkStatus.FEATURES, 2213376);
            object2IntOpenHashMap.put(ChunkStatus.LIGHT, 13421772);
            object2IntOpenHashMap.put(ChunkStatus.SPAWN, 15884384);
            object2IntOpenHashMap.put(ChunkStatus.HEIGHTMAPS, 15658734);
            object2IntOpenHashMap.put(ChunkStatus.FULL, 16777215);
        }));
    }
}
