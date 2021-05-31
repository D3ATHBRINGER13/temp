package net.minecraft.data.info;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.google.gson.JsonElement;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import java.nio.file.Paths;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.MinecraftServer;
import java.io.File;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.util.UUID;
import java.net.Proxy;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import net.minecraft.data.DataProvider;

public class CommandsReport implements DataProvider {
    private static final Gson GSON;
    private final DataGenerator generator;
    
    public CommandsReport(final DataGenerator gk) {
        this.generator = gk;
    }
    
    public void run(final HashCache gm) throws IOException {
        final YggdrasilAuthenticationService yggdrasilAuthenticationService3 = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
        final MinecraftSessionService minecraftSessionService4 = yggdrasilAuthenticationService3.createMinecraftSessionService();
        final GameProfileRepository gameProfileRepository5 = yggdrasilAuthenticationService3.createProfileRepository();
        final File file6 = new File(this.generator.getOutputFolder().toFile(), "tmp");
        final GameProfileCache xr7 = new GameProfileCache(gameProfileRepository5, new File(file6, MinecraftServer.USERID_CACHE_FILE.getName()));
        final DedicatedServerSettings um8 = new DedicatedServerSettings(Paths.get("server.properties", new String[0]));
        final MinecraftServer minecraftServer9 = new DedicatedServer(file6, um8, DataFixers.getDataFixer(), yggdrasilAuthenticationService3, minecraftSessionService4, gameProfileRepository5, xr7, LoggerChunkProgressListener::new, um8.getProperties().levelName);
        final Path path10 = this.generator.getOutputFolder().resolve("reports/commands.json");
        final CommandDispatcher<CommandSourceStack> commandDispatcher11 = minecraftServer9.getCommands().getDispatcher();
        DataProvider.save(CommandsReport.GSON, gm, (JsonElement)ArgumentTypes.<CommandSourceStack>serializeNodeToJson(commandDispatcher11, (com.mojang.brigadier.tree.CommandNode<CommandSourceStack>)commandDispatcher11.getRoot()), path10);
    }
    
    public String getName() {
        return "Command Syntax";
    }
    
    static {
        GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }
}
