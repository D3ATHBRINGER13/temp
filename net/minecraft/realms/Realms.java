package net.minecraft.realms;

import java.time.Duration;
import java.util.Arrays;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.ChatType;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import net.minecraft.nbt.NbtIo;
import java.io.FileInputStream;
import java.io.File;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.level.GameType;
import net.minecraft.client.gui.screens.Screen;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.Util;
import net.minecraft.client.User;
import java.net.Proxy;
import net.minecraft.client.Minecraft;

public class Realms {
    private static final RepeatedNarrator REPEATED_NARRATOR;
    
    public static boolean isTouchScreen() {
        return Minecraft.getInstance().options.touchscreen;
    }
    
    public static Proxy getProxy() {
        return Minecraft.getInstance().getProxy();
    }
    
    public static String sessionId() {
        final User cyn1 = Minecraft.getInstance().getUser();
        if (cyn1 == null) {
            return null;
        }
        return cyn1.getSessionId();
    }
    
    public static String userName() {
        final User cyn1 = Minecraft.getInstance().getUser();
        if (cyn1 == null) {
            return null;
        }
        return cyn1.getName();
    }
    
    public static long currentTimeMillis() {
        return Util.getMillis();
    }
    
    public static String getSessionId() {
        return Minecraft.getInstance().getUser().getSessionId();
    }
    
    public static String getUUID() {
        return Minecraft.getInstance().getUser().getUuid();
    }
    
    public static String getName() {
        return Minecraft.getInstance().getUser().getName();
    }
    
    public static String uuidToName(final String string) {
        return Minecraft.getInstance().getMinecraftSessionService().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), (String)null), false).getName();
    }
    
    public static <V> CompletableFuture<V> execute(final Supplier<V> supplier) {
        return Minecraft.getInstance().<V>submit(supplier);
    }
    
    public static void execute(final Runnable runnable) {
        Minecraft.getInstance().execute(runnable);
    }
    
    public static void setScreen(final RealmsScreen realmsScreen) {
        Realms.execute((java.util.function.Supplier<Object>)(() -> {
            setScreenDirect(realmsScreen);
            return null;
        }));
    }
    
    public static void setScreenDirect(final RealmsScreen realmsScreen) {
        Minecraft.getInstance().setScreen(realmsScreen.getProxy());
    }
    
    public static String getGameDirectoryPath() {
        return Minecraft.getInstance().gameDirectory.getAbsolutePath();
    }
    
    public static int survivalId() {
        return GameType.SURVIVAL.getId();
    }
    
    public static int creativeId() {
        return GameType.CREATIVE.getId();
    }
    
    public static int adventureId() {
        return GameType.ADVENTURE.getId();
    }
    
    public static int spectatorId() {
        return GameType.SPECTATOR.getId();
    }
    
    public static void setConnectedToRealms(final boolean boolean1) {
        Minecraft.getInstance().setConnectedToRealms(boolean1);
    }
    
    public static CompletableFuture<?> downloadResourcePack(final String string1, final String string2) {
        return Minecraft.getInstance().getClientPackSource().downloadAndSelectResourcePack(string1, string2);
    }
    
    public static void clearResourcePack() {
        Minecraft.getInstance().getClientPackSource().clearServerPack();
    }
    
    public static boolean getRealmsNotificationsEnabled() {
        return Minecraft.getInstance().options.realmsNotifications;
    }
    
    public static boolean inTitleScreen() {
        return Minecraft.getInstance().screen != null && Minecraft.getInstance().screen instanceof TitleScreen;
    }
    
    public static void deletePlayerTag(final File file) {
        if (file.exists()) {
            try {
                final CompoundTag id2 = NbtIo.readCompressed((InputStream)new FileInputStream(file));
                final CompoundTag id3 = id2.getCompound("Data");
                id3.remove("Player");
                NbtIo.writeCompressed(id2, (OutputStream)new FileOutputStream(file));
            }
            catch (Exception exception2) {
                exception2.printStackTrace();
            }
        }
    }
    
    public static void openUri(final String string) {
        Util.getPlatform().openUri(string);
    }
    
    public static void setClipboard(final String string) {
        Minecraft.getInstance().keyboardHandler.setClipboard(string);
    }
    
    public static String getMinecraftVersionString() {
        return SharedConstants.getCurrentVersion().getName();
    }
    
    public static ResourceLocation resourceLocation(final String string) {
        return new ResourceLocation(string);
    }
    
    public static String getLocalizedString(final String string, final Object... arr) {
        return I18n.get(string, arr);
    }
    
    public static void bind(final String string) {
        final ResourceLocation qv2 = new ResourceLocation(string);
        Minecraft.getInstance().getTextureManager().bind(qv2);
    }
    
    public static void narrateNow(final String string) {
        final NarratorChatListener cyz2 = NarratorChatListener.INSTANCE;
        cyz2.clear();
        cyz2.handle(ChatType.SYSTEM, new TextComponent(fixNarrationNewlines(string)));
    }
    
    private static String fixNarrationNewlines(final String string) {
        return string.replace("\\n", (CharSequence)System.lineSeparator());
    }
    
    public static void narrateNow(final String... arr) {
        narrateNow((Iterable<String>)Arrays.asList((Object[])arr));
    }
    
    public static void narrateNow(final Iterable<String> iterable) {
        narrateNow(joinNarrations(iterable));
    }
    
    public static String joinNarrations(final Iterable<String> iterable) {
        return String.join((CharSequence)System.lineSeparator(), (Iterable)iterable);
    }
    
    public static void narrateRepeatedly(final String string) {
        Realms.REPEATED_NARRATOR.narrate(fixNarrationNewlines(string));
    }
    
    static {
        REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));
    }
}
