package net.minecraft.client;

import java.util.function.Predicate;
import java.util.function.BiFunction;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.components.ChatComponent;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.client.gui.chat.NarratorChatListener;
import java.util.concurrent.Executor;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.components.AbstractWidget;

public abstract class Option {
    public static final ProgressOption BIOME_BLEND_RADIUS;
    public static final ProgressOption CHAT_HEIGHT_FOCUSED;
    public static final ProgressOption CHAT_HEIGHT_UNFOCUSED;
    public static final ProgressOption CHAT_OPACITY;
    public static final ProgressOption CHAT_SCALE;
    public static final ProgressOption CHAT_WIDTH;
    public static final ProgressOption FOV;
    public static final ProgressOption FRAMERATE_LIMIT;
    public static final ProgressOption GAMMA;
    public static final ProgressOption MIPMAP_LEVELS;
    public static final ProgressOption MOUSE_WHEEL_SENSITIVITY;
    public static final BooleanOption RAW_MOUSE_INPUT;
    public static final ProgressOption RENDER_DISTANCE;
    public static final ProgressOption SENSITIVITY;
    public static final ProgressOption TEXT_BACKGROUND_OPACITY;
    public static final CycleOption AMBIENT_OCCLUSION;
    public static final CycleOption ATTACK_INDICATOR;
    public static final CycleOption CHAT_VISIBILITY;
    public static final CycleOption GRAPHICS;
    public static final CycleOption GUI_SCALE;
    public static final CycleOption MAIN_HAND;
    public static final CycleOption NARRATOR;
    public static final CycleOption PARTICLES;
    public static final CycleOption RENDER_CLOUDS;
    public static final CycleOption TEXT_BACKGROUND;
    public static final BooleanOption AUTO_JUMP;
    public static final BooleanOption AUTO_SUGGESTIONS;
    public static final BooleanOption CHAT_COLOR;
    public static final BooleanOption CHAT_LINKS;
    public static final BooleanOption CHAT_LINKS_PROMPT;
    public static final BooleanOption DISCRETE_MOUSE_SCROLL;
    public static final BooleanOption ENABLE_VSYNC;
    public static final BooleanOption ENTITY_SHADOWS;
    public static final BooleanOption FORCE_UNICODE_FONT;
    public static final BooleanOption INVERT_MOUSE;
    public static final BooleanOption REALMS_NOTIFICATIONS;
    public static final BooleanOption REDUCED_DEBUG_INFO;
    public static final BooleanOption SHOW_SUBTITLES;
    public static final BooleanOption SNOOPER_ENABLED;
    public static final BooleanOption TOUCHSCREEN;
    public static final BooleanOption USE_FULLSCREEN;
    public static final BooleanOption VIEW_BOBBING;
    private final String captionId;
    
    public Option(final String string) {
        this.captionId = string;
    }
    
    public abstract AbstractWidget createButton(final Options cyg, final int integer2, final int integer3, final int integer4);
    
    public String getCaption() {
        return I18n.get(this.captionId) + ": ";
    }
    
    static {
        BIOME_BLEND_RADIUS = new ProgressOption("options.biomeBlendRadius", 0.0, 7.0, 1.0f, (Function<Options, Double>)(cyg -> cyg.biomeBlendRadius), (BiConsumer<Options, Double>)((cyg, double2) -> {
            cyg.biomeBlendRadius = Mth.clamp((int)(double)double2, 0, 7);
            Minecraft.getInstance().levelRenderer.allChanged();
        }), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.get(cyg);
            final String string5 = cyi.getCaption();
            if (double3 == 0.0) {
                return string5 + I18n.get("options.off");
            }
            final int integer6 = (int)double3 * 2 + 1;
            return string5 + integer6 + "x" + integer6;
        }));
        CHAT_HEIGHT_FOCUSED = new ProgressOption("options.chat.height.focused", 0.0, 1.0, 0.0f, (Function<Options, Double>)(cyg -> cyg.chatHeightFocused), (BiConsumer<Options, Double>)((cyg, double2) -> {
            cyg.chatHeightFocused = double2;
            Minecraft.getInstance().gui.getChat().rescaleChat();
        }), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.toPct(cyi.get(cyg));
            return cyi.getCaption() + ChatComponent.getHeight(double3) + "px";
        }));
        CHAT_HEIGHT_UNFOCUSED = new ProgressOption("options.chat.height.unfocused", 0.0, 1.0, 0.0f, (Function<Options, Double>)(cyg -> cyg.chatHeightUnfocused), (BiConsumer<Options, Double>)((cyg, double2) -> {
            cyg.chatHeightUnfocused = double2;
            Minecraft.getInstance().gui.getChat().rescaleChat();
        }), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.toPct(cyi.get(cyg));
            return cyi.getCaption() + ChatComponent.getHeight(double3) + "px";
        }));
        CHAT_OPACITY = new ProgressOption("options.chat.opacity", 0.0, 1.0, 0.0f, (Function<Options, Double>)(cyg -> cyg.chatOpacity), (BiConsumer<Options, Double>)((cyg, double2) -> {
            cyg.chatOpacity = double2;
            Minecraft.getInstance().gui.getChat().rescaleChat();
        }), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.toPct(cyi.get(cyg));
            return cyi.getCaption() + (int)(double3 * 90.0 + 10.0) + "%";
        }));
        CHAT_SCALE = new ProgressOption("options.chat.scale", 0.0, 1.0, 0.0f, (Function<Options, Double>)(cyg -> cyg.chatScale), (BiConsumer<Options, Double>)((cyg, double2) -> {
            cyg.chatScale = double2;
            Minecraft.getInstance().gui.getChat().rescaleChat();
        }), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.toPct(cyi.get(cyg));
            final String string5 = cyi.getCaption();
            if (double3 == 0.0) {
                return string5 + I18n.get("options.off");
            }
            return string5 + (int)(double3 * 100.0) + "%";
        }));
        CHAT_WIDTH = new ProgressOption("options.chat.width", 0.0, 1.0, 0.0f, (Function<Options, Double>)(cyg -> cyg.chatWidth), (BiConsumer<Options, Double>)((cyg, double2) -> {
            cyg.chatWidth = double2;
            Minecraft.getInstance().gui.getChat().rescaleChat();
        }), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.toPct(cyi.get(cyg));
            return cyi.getCaption() + ChatComponent.getWidth(double3) + "px";
        }));
        FOV = new ProgressOption("options.fov", 30.0, 110.0, 1.0f, (Function<Options, Double>)(cyg -> cyg.fov), (BiConsumer<Options, Double>)((cyg, double2) -> cyg.fov = double2), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.get(cyg);
            final String string5 = cyi.getCaption();
            if (double3 == 70.0) {
                return string5 + I18n.get("options.fov.min");
            }
            if (double3 == cyi.getMaxValue()) {
                return string5 + I18n.get("options.fov.max");
            }
            return string5 + (int)double3;
        }));
        FRAMERATE_LIMIT = new ProgressOption("options.framerateLimit", 10.0, 260.0, 10.0f, (Function<Options, Double>)(cyg -> cyg.framerateLimit), (BiConsumer<Options, Double>)((cyg, double2) -> {
            cyg.framerateLimit = (int)(double)double2;
            Minecraft.getInstance().window.setFramerateLimit(cyg.framerateLimit);
        }), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.get(cyg);
            final String string5 = cyi.getCaption();
            if (double3 == cyi.getMaxValue()) {
                return string5 + I18n.get("options.framerateLimit.max");
            }
            return string5 + I18n.get("options.framerate", (int)double3);
        }));
        GAMMA = new ProgressOption("options.gamma", 0.0, 1.0, 0.0f, (Function<Options, Double>)(cyg -> cyg.gamma), (BiConsumer<Options, Double>)((cyg, double2) -> cyg.gamma = double2), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.toPct(cyi.get(cyg));
            final String string5 = cyi.getCaption();
            if (double3 == 0.0) {
                return string5 + I18n.get("options.gamma.min");
            }
            if (double3 == 1.0) {
                return string5 + I18n.get("options.gamma.max");
            }
            return string5 + "+" + (int)(double3 * 100.0) + "%";
        }));
        MIPMAP_LEVELS = new ProgressOption("options.mipmapLevels", 0.0, 4.0, 1.0f, (Function<Options, Double>)(cyg -> cyg.mipmapLevels), (BiConsumer<Options, Double>)((cyg, double2) -> cyg.mipmapLevels = (int)(double)double2), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.get(cyg);
            final String string5 = cyi.getCaption();
            if (double3 == 0.0) {
                return string5 + I18n.get("options.off");
            }
            return string5 + (int)double3;
        }));
        MOUSE_WHEEL_SENSITIVITY = new LogaritmicProgressOption("options.mouseWheelSensitivity", 0.01, 10.0, 0.01f, (Function<Options, Double>)(cyg -> cyg.mouseWheelSensitivity), (BiConsumer<Options, Double>)((cyg, double2) -> cyg.mouseWheelSensitivity = double2), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.toPct(cyi.get(cyg));
            return cyi.getCaption() + String.format("%.2f", new Object[] { cyi.toValue(double3) });
        }));
        RAW_MOUSE_INPUT = new BooleanOption("options.rawMouseInput", (Predicate<Options>)(cyg -> cyg.rawMouseInput), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> {
            cyg.rawMouseInput = boolean2;
            final Window cuo3 = Minecraft.getInstance().window;
            if (cuo3 != null) {
                cuo3.updateRawMouseInput(boolean2);
            }
        }));
        RENDER_DISTANCE = new ProgressOption("options.renderDistance", 2.0, 16.0, 1.0f, (Function<Options, Double>)(cyg -> cyg.renderDistance), (BiConsumer<Options, Double>)((cyg, double2) -> {
            cyg.renderDistance = (int)(double)double2;
            Minecraft.getInstance().levelRenderer.needsUpdate();
        }), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.get(cyg);
            return cyi.getCaption() + I18n.get("options.chunks", (int)double3);
        }));
        SENSITIVITY = new ProgressOption("options.sensitivity", 0.0, 1.0, 0.0f, (Function<Options, Double>)(cyg -> cyg.sensitivity), (BiConsumer<Options, Double>)((cyg, double2) -> cyg.sensitivity = double2), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> {
            final double double3 = cyi.toPct(cyi.get(cyg));
            final String string5 = cyi.getCaption();
            if (double3 == 0.0) {
                return string5 + I18n.get("options.sensitivity.min");
            }
            if (double3 == 1.0) {
                return string5 + I18n.get("options.sensitivity.max");
            }
            return string5 + (int)(double3 * 200.0) + "%";
        }));
        TEXT_BACKGROUND_OPACITY = new ProgressOption("options.accessibility.text_background_opacity", 0.0, 1.0, 0.0f, (Function<Options, Double>)(cyg -> cyg.textBackgroundOpacity), (BiConsumer<Options, Double>)((cyg, double2) -> {
            cyg.textBackgroundOpacity = double2;
            Minecraft.getInstance().gui.getChat().rescaleChat();
        }), (BiFunction<Options, ProgressOption, String>)((cyg, cyi) -> cyi.getCaption() + (int)(cyi.toPct(cyi.get(cyg)) * 100.0) + "%"));
        AMBIENT_OCCLUSION = new CycleOption("options.ao", (BiConsumer<Options, Integer>)((cyg, integer) -> {
            cyg.ambientOcclusion = AmbientOcclusionStatus.byId(cyg.ambientOcclusion.getId() + integer);
            Minecraft.getInstance().levelRenderer.allChanged();
        }), (BiFunction<Options, CycleOption, String>)((cyg, cxt) -> cxt.getCaption() + I18n.get(cyg.ambientOcclusion.getKey())));
        ATTACK_INDICATOR = new CycleOption("options.attackIndicator", (BiConsumer<Options, Integer>)((cyg, integer) -> cyg.attackIndicator = AttackIndicatorStatus.byId(cyg.attackIndicator.getId() + integer)), (BiFunction<Options, CycleOption, String>)((cyg, cxt) -> cxt.getCaption() + I18n.get(cyg.attackIndicator.getKey())));
        CHAT_VISIBILITY = new CycleOption("options.chat.visibility", (BiConsumer<Options, Integer>)((cyg, integer) -> cyg.chatVisibility = ChatVisiblity.byId((cyg.chatVisibility.getId() + integer) % 3)), (BiFunction<Options, CycleOption, String>)((cyg, cxt) -> cxt.getCaption() + I18n.get(cyg.chatVisibility.getKey())));
        GRAPHICS = new CycleOption("options.graphics", (BiConsumer<Options, Integer>)((cyg, integer) -> {
            cyg.fancyGraphics = !cyg.fancyGraphics;
            Minecraft.getInstance().levelRenderer.allChanged();
        }), (BiFunction<Options, CycleOption, String>)((cyg, cxt) -> {
            if (cyg.fancyGraphics) {
                return cxt.getCaption() + I18n.get("options.graphics.fancy");
            }
            return cxt.getCaption() + I18n.get("options.graphics.fast");
        }));
        GUI_SCALE = new CycleOption("options.guiScale", (BiConsumer<Options, Integer>)((cyg, integer) -> cyg.guiScale = Integer.remainderUnsigned(cyg.guiScale + integer, Minecraft.getInstance().window.calculateScale(0, Minecraft.getInstance().isEnforceUnicode()) + 1)), (BiFunction<Options, CycleOption, String>)((cyg, cxt) -> cxt.getCaption() + ((cyg.guiScale == 0) ? I18n.get("options.guiScale.auto") : Integer.valueOf(cyg.guiScale))));
        MAIN_HAND = new CycleOption("options.mainHand", (BiConsumer<Options, Integer>)((cyg, integer) -> cyg.mainHand = cyg.mainHand.getOpposite()), (BiFunction<Options, CycleOption, String>)((cyg, cxt) -> cxt.getCaption() + cyg.mainHand));
        NARRATOR = new CycleOption("options.narrator", (BiConsumer<Options, Integer>)((cyg, integer) -> {
            if (NarratorChatListener.INSTANCE.isActive()) {
                cyg.narratorStatus = NarratorStatus.byId(cyg.narratorStatus.getId() + integer);
            }
            else {
                cyg.narratorStatus = NarratorStatus.OFF;
            }
            NarratorChatListener.INSTANCE.updateNarratorStatus(cyg.narratorStatus);
        }), (BiFunction<Options, CycleOption, String>)((cyg, cxt) -> {
            if (NarratorChatListener.INSTANCE.isActive()) {
                return cxt.getCaption() + I18n.get(cyg.narratorStatus.getKey());
            }
            return cxt.getCaption() + I18n.get("options.narrator.notavailable");
        }));
        PARTICLES = new CycleOption("options.particles", (BiConsumer<Options, Integer>)((cyg, integer) -> cyg.particles = ParticleStatus.byId(cyg.particles.getId() + integer)), (BiFunction<Options, CycleOption, String>)((cyg, cxt) -> cxt.getCaption() + I18n.get(cyg.particles.getKey())));
        RENDER_CLOUDS = new CycleOption("options.renderClouds", (BiConsumer<Options, Integer>)((cyg, integer) -> cyg.renderClouds = CloudStatus.byId(cyg.renderClouds.getId() + integer)), (BiFunction<Options, CycleOption, String>)((cyg, cxt) -> cxt.getCaption() + I18n.get(cyg.renderClouds.getKey())));
        TEXT_BACKGROUND = new CycleOption("options.accessibility.text_background", (BiConsumer<Options, Integer>)((cyg, integer) -> cyg.backgroundForChatOnly = !cyg.backgroundForChatOnly), (BiFunction<Options, CycleOption, String>)((cyg, cxt) -> cxt.getCaption() + I18n.get(cyg.backgroundForChatOnly ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere")));
        AUTO_JUMP = new BooleanOption("options.autoJump", (Predicate<Options>)(cyg -> cyg.autoJump), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.autoJump = boolean2));
        AUTO_SUGGESTIONS = new BooleanOption("options.autoSuggestCommands", (Predicate<Options>)(cyg -> cyg.autoSuggestions), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.autoSuggestions = boolean2));
        CHAT_COLOR = new BooleanOption("options.chat.color", (Predicate<Options>)(cyg -> cyg.chatColors), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.chatColors = boolean2));
        CHAT_LINKS = new BooleanOption("options.chat.links", (Predicate<Options>)(cyg -> cyg.chatLinks), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.chatLinks = boolean2));
        CHAT_LINKS_PROMPT = new BooleanOption("options.chat.links.prompt", (Predicate<Options>)(cyg -> cyg.chatLinksPrompt), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.chatLinksPrompt = boolean2));
        DISCRETE_MOUSE_SCROLL = new BooleanOption("options.discrete_mouse_scroll", (Predicate<Options>)(cyg -> cyg.discreteMouseScroll), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.discreteMouseScroll = boolean2));
        ENABLE_VSYNC = new BooleanOption("options.vsync", (Predicate<Options>)(cyg -> cyg.enableVsync), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> {
            cyg.enableVsync = boolean2;
            if (Minecraft.getInstance().window != null) {
                Minecraft.getInstance().window.updateVsync(cyg.enableVsync);
            }
        }));
        ENTITY_SHADOWS = new BooleanOption("options.entityShadows", (Predicate<Options>)(cyg -> cyg.entityShadows), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.entityShadows = boolean2));
        FORCE_UNICODE_FONT = new BooleanOption("options.forceUnicodeFont", (Predicate<Options>)(cyg -> cyg.forceUnicodeFont), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> {
            cyg.forceUnicodeFont = boolean2;
            final Minecraft cyc3 = Minecraft.getInstance();
            if (cyc3.getFontManager() != null) {
                cyc3.getFontManager().setForceUnicode(cyg.forceUnicodeFont, Util.backgroundExecutor(), (Executor)cyc3);
            }
        }));
        INVERT_MOUSE = new BooleanOption("options.invertMouse", (Predicate<Options>)(cyg -> cyg.invertYMouse), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.invertYMouse = boolean2));
        REALMS_NOTIFICATIONS = new BooleanOption("options.realmsNotifications", (Predicate<Options>)(cyg -> cyg.realmsNotifications), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.realmsNotifications = boolean2));
        REDUCED_DEBUG_INFO = new BooleanOption("options.reducedDebugInfo", (Predicate<Options>)(cyg -> cyg.reducedDebugInfo), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.reducedDebugInfo = boolean2));
        SHOW_SUBTITLES = new BooleanOption("options.showSubtitles", (Predicate<Options>)(cyg -> cyg.showSubtitles), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.showSubtitles = boolean2));
        SNOOPER_ENABLED = new BooleanOption("options.snooper", (Predicate<Options>)(cyg -> {
            if (cyg.snooperEnabled) {}
            return false;
        }), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.snooperEnabled = boolean2));
        TOUCHSCREEN = new BooleanOption("options.touchscreen", (Predicate<Options>)(cyg -> cyg.touchscreen), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.touchscreen = boolean2));
        USE_FULLSCREEN = new BooleanOption("options.fullscreen", (Predicate<Options>)(cyg -> cyg.fullscreen), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> {
            cyg.fullscreen = boolean2;
            final Minecraft cyc3 = Minecraft.getInstance();
            if (cyc3.window != null && cyc3.window.isFullscreen() != cyg.fullscreen) {
                cyc3.window.toggleFullScreen();
                cyg.fullscreen = cyc3.window.isFullscreen();
            }
        }));
        VIEW_BOBBING = new BooleanOption("options.viewBobbing", (Predicate<Options>)(cyg -> cyg.bobView), (BiConsumer<Options, Boolean>)((cyg, boolean2) -> cyg.bobView = boolean2));
    }
}
