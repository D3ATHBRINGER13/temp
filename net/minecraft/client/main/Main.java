package net.minecraft.client.main;

import org.apache.logging.log4j.LogManager;
import joptsimple.ArgumentAcceptingOptionSpec;
import javax.annotation.Nullable;
import com.google.gson.Gson;
import java.util.OptionalInt;
import java.util.List;
import joptsimple.OptionSet;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.DisplayData;
import net.minecraft.client.User;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.GsonHelper;
import java.lang.reflect.Type;
import com.mojang.authlib.properties.PropertyMap;
import com.google.gson.GsonBuilder;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import joptsimple.OptionSpec;
import net.minecraft.Util;
import java.io.File;
import joptsimple.OptionParser;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER;
    
    public static void main(final String[] arr) {
        final OptionParser optionParser2 = new OptionParser();
        optionParser2.allowsUnrecognizedOptions();
        optionParser2.accepts("demo");
        optionParser2.accepts("fullscreen");
        optionParser2.accepts("checkGlErrors");
        final OptionSpec<String> optionSpec3 = (OptionSpec<String>)optionParser2.accepts("server").withRequiredArg();
        final OptionSpec<Integer> optionSpec4 = (OptionSpec<Integer>)optionParser2.accepts("port").withRequiredArg().ofType((Class)Integer.class).defaultsTo(25565, (Object[])new Integer[0]);
        final OptionSpec<File> optionSpec5 = (OptionSpec<File>)optionParser2.accepts("gameDir").withRequiredArg().ofType((Class)File.class).defaultsTo(new File("."), (Object[])new File[0]);
        final OptionSpec<File> optionSpec6 = (OptionSpec<File>)optionParser2.accepts("assetsDir").withRequiredArg().ofType((Class)File.class);
        final OptionSpec<File> optionSpec7 = (OptionSpec<File>)optionParser2.accepts("resourcePackDir").withRequiredArg().ofType((Class)File.class);
        final OptionSpec<String> optionSpec8 = (OptionSpec<String>)optionParser2.accepts("proxyHost").withRequiredArg();
        final OptionSpec<Integer> optionSpec9 = (OptionSpec<Integer>)optionParser2.accepts("proxyPort").withRequiredArg().defaultsTo("8080", (Object[])new String[0]).ofType((Class)Integer.class);
        final OptionSpec<String> optionSpec10 = (OptionSpec<String>)optionParser2.accepts("proxyUser").withRequiredArg();
        final OptionSpec<String> optionSpec11 = (OptionSpec<String>)optionParser2.accepts("proxyPass").withRequiredArg();
        final OptionSpec<String> optionSpec12 = (OptionSpec<String>)optionParser2.accepts("username").withRequiredArg().defaultsTo(new StringBuilder().append("Player").append(Util.getMillis() % 1000L).toString(), (Object[])new String[0]);
        final OptionSpec<String> optionSpec13 = (OptionSpec<String>)optionParser2.accepts("uuid").withRequiredArg();
        final OptionSpec<String> optionSpec14 = (OptionSpec<String>)optionParser2.accepts("accessToken").withRequiredArg().required();
        final OptionSpec<String> optionSpec15 = (OptionSpec<String>)optionParser2.accepts("version").withRequiredArg().required();
        final OptionSpec<Integer> optionSpec16 = (OptionSpec<Integer>)optionParser2.accepts("width").withRequiredArg().ofType((Class)Integer.class).defaultsTo(854, (Object[])new Integer[0]);
        final OptionSpec<Integer> optionSpec17 = (OptionSpec<Integer>)optionParser2.accepts("height").withRequiredArg().ofType((Class)Integer.class).defaultsTo(480, (Object[])new Integer[0]);
        final OptionSpec<Integer> optionSpec18 = (OptionSpec<Integer>)optionParser2.accepts("fullscreenWidth").withRequiredArg().ofType((Class)Integer.class);
        final OptionSpec<Integer> optionSpec19 = (OptionSpec<Integer>)optionParser2.accepts("fullscreenHeight").withRequiredArg().ofType((Class)Integer.class);
        final OptionSpec<String> optionSpec20 = (OptionSpec<String>)optionParser2.accepts("userProperties").withRequiredArg().defaultsTo("{}", (Object[])new String[0]);
        final OptionSpec<String> optionSpec21 = (OptionSpec<String>)optionParser2.accepts("profileProperties").withRequiredArg().defaultsTo("{}", (Object[])new String[0]);
        final OptionSpec<String> optionSpec22 = (OptionSpec<String>)optionParser2.accepts("assetIndex").withRequiredArg();
        final OptionSpec<String> optionSpec23 = (OptionSpec<String>)optionParser2.accepts("userType").withRequiredArg().defaultsTo("legacy", (Object[])new String[0]);
        final OptionSpec<String> optionSpec24 = (OptionSpec<String>)optionParser2.accepts("versionType").withRequiredArg().defaultsTo("release", (Object[])new String[0]);
        final OptionSpec<String> optionSpec25 = (OptionSpec<String>)optionParser2.nonOptions();
        final OptionSet optionSet26 = optionParser2.parse(arr);
        final List<String> list27 = (List<String>)optionSet26.valuesOf((OptionSpec)optionSpec25);
        if (!list27.isEmpty()) {
            System.out.println(new StringBuilder().append("Completely ignored arguments: ").append(list27).toString());
        }
        final String string28 = Main.<String>parseArgument(optionSet26, optionSpec8);
        Proxy proxy29 = Proxy.NO_PROXY;
        if (string28 != null) {
            try {
                proxy29 = new Proxy(Proxy.Type.SOCKS, (SocketAddress)new InetSocketAddress(string28, (int)Main.<Integer>parseArgument(optionSet26, optionSpec9)));
            }
            catch (Exception ex) {}
        }
        final String string29 = Main.<String>parseArgument(optionSet26, optionSpec10);
        final String string30 = Main.<String>parseArgument(optionSet26, optionSpec11);
        if (!proxy29.equals(Proxy.NO_PROXY) && stringHasValue(string29) && stringHasValue(string30)) {
            Authenticator.setDefault((Authenticator)new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(string29, string30.toCharArray());
                }
            });
        }
        final int integer32 = Main.<Integer>parseArgument(optionSet26, optionSpec16);
        final int integer33 = Main.<Integer>parseArgument(optionSet26, optionSpec17);
        final OptionalInt optionalInt34 = ofNullable(Main.<Integer>parseArgument(optionSet26, optionSpec18));
        final OptionalInt optionalInt35 = ofNullable(Main.<Integer>parseArgument(optionSet26, optionSpec19));
        final boolean boolean36 = optionSet26.has("fullscreen");
        final boolean boolean37 = optionSet26.has("demo");
        final String string31 = Main.<String>parseArgument(optionSet26, optionSpec15);
        final Gson gson39 = new GsonBuilder().registerTypeAdapter((Type)PropertyMap.class, new PropertyMap.Serializer()).create();
        final PropertyMap propertyMap40 = GsonHelper.<PropertyMap>fromJson(gson39, Main.<String>parseArgument(optionSet26, optionSpec20), PropertyMap.class);
        final PropertyMap propertyMap41 = GsonHelper.<PropertyMap>fromJson(gson39, Main.<String>parseArgument(optionSet26, optionSpec21), PropertyMap.class);
        final String string32 = Main.<String>parseArgument(optionSet26, optionSpec24);
        final File file43 = Main.<File>parseArgument(optionSet26, optionSpec5);
        final File file44 = optionSet26.has((OptionSpec)optionSpec6) ? Main.<File>parseArgument(optionSet26, optionSpec6) : new File(file43, "assets/");
        final File file45 = optionSet26.has((OptionSpec)optionSpec7) ? Main.<File>parseArgument(optionSet26, optionSpec7) : new File(file43, "resourcepacks/");
        final String string33 = (String)(optionSet26.has((OptionSpec)optionSpec13) ? optionSpec13.value(optionSet26) : Player.createPlayerUUID((String)optionSpec12.value(optionSet26)).toString());
        final String string34 = optionSet26.has((OptionSpec)optionSpec22) ? ((String)optionSpec22.value(optionSet26)) : null;
        final String string35 = Main.<String>parseArgument(optionSet26, optionSpec3);
        final Integer integer34 = Main.<Integer>parseArgument(optionSet26, optionSpec4);
        final User cyn50 = new User((String)optionSpec12.value(optionSet26), string33, (String)optionSpec14.value(optionSet26), (String)optionSpec23.value(optionSet26));
        final GameConfig dgh51 = new GameConfig(new GameConfig.UserData(cyn50, propertyMap40, propertyMap41, proxy29), new DisplayData(integer32, integer33, optionalInt34, optionalInt35, boolean36), new GameConfig.FolderData(file43, file45, file44, string34), new GameConfig.GameData(boolean37, string31, string32), new GameConfig.ServerData(string35, integer34));
        final Thread thread52 = new Thread("Client Shutdown Thread") {
            public void run() {
                final Minecraft cyc2 = Minecraft.getInstance();
                if (cyc2 == null) {
                    return;
                }
                final IntegratedServer eac3 = cyc2.getSingleplayerServer();
                if (eac3 != null) {
                    eac3.halt(true);
                }
            }
        };
        thread52.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(Main.LOGGER));
        Runtime.getRuntime().addShutdownHook(thread52);
        Thread.currentThread().setName("Client thread");
        new Minecraft(dgh51).run();
    }
    
    private static OptionalInt ofNullable(@Nullable final Integer integer) {
        return (integer != null) ? OptionalInt.of((int)integer) : OptionalInt.empty();
    }
    
    private static <T> T parseArgument(final OptionSet optionSet, final OptionSpec<T> optionSpec) {
        try {
            return (T)optionSet.valueOf((OptionSpec)optionSpec);
        }
        catch (Throwable throwable3) {
            if (optionSpec instanceof ArgumentAcceptingOptionSpec) {
                final ArgumentAcceptingOptionSpec<T> argumentAcceptingOptionSpec4 = (ArgumentAcceptingOptionSpec<T>)optionSpec;
                final List<T> list5 = (List<T>)argumentAcceptingOptionSpec4.defaultValues();
                if (!list5.isEmpty()) {
                    return (T)list5.get(0);
                }
            }
            throw throwable3;
        }
    }
    
    private static boolean stringHasValue(final String string) {
        return string != null && !string.isEmpty();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        System.setProperty("java.awt.headless", "true");
    }
}
