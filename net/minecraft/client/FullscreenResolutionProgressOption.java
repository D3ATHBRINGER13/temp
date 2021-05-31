package net.minecraft.client;

import com.mojang.blaze3d.platform.VideoMode;
import java.util.Optional;
import net.minecraft.client.resources.language.I18n;
import java.util.function.BiFunction;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.Window;

public class FullscreenResolutionProgressOption extends ProgressOption {
    public FullscreenResolutionProgressOption(final Window cuo) {
        this(cuo, cuo.findBestMonitor());
    }
    
    private FullscreenResolutionProgressOption(final Window cuo, @Nullable final Monitor cuh) {
        super("options.fullscreen.resolution", -1.0, (cuh != null) ? ((double)(cuh.getModeCount() - 1)) : -1.0, 1.0f, (Function<Options, Double>)(cyg -> {
            if (cuh == null) {
                return -1.0;
            }
            final Optional<VideoMode> optional4 = cuo.getPreferredFullscreenVideoMode();
            return (Double)optional4.map(cun -> cuh.getVideoModeIndex(cun)).orElse((-1.0));
        }), (BiConsumer<Options, Double>)((cyg, double4) -> {
            if (cuh == null) {
                return;
            }
            if (double4 == -1.0) {
                cuo.setPreferredFullscreenVideoMode((Optional<VideoMode>)Optional.empty());
            }
            else {
                cuo.setPreferredFullscreenVideoMode((Optional<VideoMode>)Optional.of(cuh.getMode(double4.intValue())));
            }
        }), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            if (cuh == null) {
                return I18n.get("options.fullscreen.unavailable");
            }
            final double double4 = cyi.get(cyg);
            final String string6 = cyi.getCaption();
            if (double4 == -1.0) {
                return string6 + I18n.get("options.fullscreen.current");
            }
            return cuh.getMode((int)double4).toString();
        }));
    }
}
