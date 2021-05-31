package net.minecraft.client.gui.screens.worldselection;

import java.util.function.Consumer;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.Util;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Iterator;
import net.minecraft.client.gui.Font;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.util.worldupdate.WorldUpgrader;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.world.level.dimension.DimensionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.screens.Screen;

public class OptimizeWorldScreen extends Screen {
    private static final Object2IntMap<DimensionType> DIMENSION_COLORS;
    private final BooleanConsumer callback;
    private final WorldUpgrader upgrader;
    
    public OptimizeWorldScreen(final BooleanConsumer booleanConsumer, final String string, final LevelStorageSource coq, final boolean boolean4) {
        super(new TranslatableComponent("optimizeWorld.title", new Object[] { coq.getDataTagFor(string).getLevelName() }));
        this.callback = booleanConsumer;
        this.upgrader = new WorldUpgrader(string, coq, coq.getDataTagFor(string), boolean4);
    }
    
    @Override
    protected void init() {
        super.init();
        this.<Button>addButton(new Button(this.width / 2 - 100, this.height / 4 + 150, 200, 20, I18n.get("gui.cancel"), czi -> {
            this.upgrader.cancel();
            this.callback.accept(false);
        }));
    }
    
    @Override
    public void tick() {
        if (this.upgrader.isFinished()) {
            this.callback.accept(true);
        }
    }
    
    @Override
    public void removed() {
        this.upgrader.cancel();
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
        final int integer3 = this.width / 2 - 150;
        final int integer4 = this.width / 2 + 150;
        final int integer5 = this.height / 4 + 100;
        final int integer6 = integer5 + 10;
        final Font font = this.font;
        final String coloredString = this.upgrader.getStatus().getColoredString();
        final int integer10 = this.width / 2;
        final int n = integer5;
        this.font.getClass();
        this.drawCenteredString(font, coloredString, integer10, n - 9 - 2, 10526880);
        if (this.upgrader.getTotalChunks() > 0) {
            GuiComponent.fill(integer3 - 1, integer5 - 1, integer4 + 1, integer6 + 1, -16777216);
            this.drawString(this.font, I18n.get("optimizeWorld.info.converted", this.upgrader.getConverted()), integer3, 40, 10526880);
            final Font font2 = this.font;
            final String value = I18n.get("optimizeWorld.info.skipped", this.upgrader.getSkipped());
            final int integer11 = integer3;
            final int n2 = 40;
            this.font.getClass();
            this.drawString(font2, value, integer11, n2 + 9 + 3, 10526880);
            final Font font3 = this.font;
            final String value2 = I18n.get("optimizeWorld.info.total", this.upgrader.getTotalChunks());
            final int integer12 = integer3;
            final int n3 = 40;
            this.font.getClass();
            this.drawString(font3, value2, integer12, n3 + (9 + 3) * 2, 10526880);
            int integer7 = 0;
            for (final DimensionType byn11 : DimensionType.getAllTypes()) {
                final int integer8 = Mth.floor(this.upgrader.dimensionProgress(byn11) * (integer4 - integer3));
                GuiComponent.fill(integer3 + integer7, integer5, integer3 + integer7 + integer8, integer6, OptimizeWorldScreen.DIMENSION_COLORS.getInt(byn11));
                integer7 += integer8;
            }
            final int integer9 = this.upgrader.getConverted() + this.upgrader.getSkipped();
            final Font font4 = this.font;
            final String string = new StringBuilder().append(integer9).append(" / ").append(this.upgrader.getTotalChunks()).toString();
            final int integer13 = this.width / 2;
            final int n4 = integer5;
            final int n5 = 2;
            this.font.getClass();
            this.drawCenteredString(font4, string, integer13, n4 + n5 * 9 + 2, 10526880);
            final Font font5 = this.font;
            final String string2 = new StringBuilder().append(Mth.floor(this.upgrader.getProgress() * 100.0f)).append("%").toString();
            final int integer14 = this.width / 2;
            final int n6 = integer5 + (integer6 - integer5) / 2;
            this.font.getClass();
            this.drawCenteredString(font5, string2, integer14, n6 - 9 / 2, 10526880);
        }
        super.render(integer1, integer2, float3);
    }
    
    static {
        DIMENSION_COLORS = Util.<Object2IntMap>make((Object2IntMap)new Object2IntOpenCustomHashMap((Hash.Strategy)Util.identityStrategy()), (java.util.function.Consumer<Object2IntMap>)(object2IntOpenCustomHashMap -> {
            object2IntOpenCustomHashMap.put(DimensionType.OVERWORLD, -13408734);
            object2IntOpenCustomHashMap.put(DimensionType.NETHER, -10075085);
            object2IntOpenCustomHashMap.put(DimensionType.THE_END, -8943531);
            object2IntOpenCustomHashMap.defaultReturnValue(-2236963);
        }));
    }
}
