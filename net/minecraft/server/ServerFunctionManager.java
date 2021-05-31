package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.packs.resources.Resource;
import java.io.IOException;
import java.util.concurrent.CompletionException;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.tags.Tag;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.packs.resources.SimpleResource;
import java.util.function.Predicate;
import net.minecraft.server.packs.resources.ResourceManager;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.world.level.GameRules;
import java.util.Optional;
import java.util.function.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.tags.TagCollection;
import java.util.List;
import java.util.ArrayDeque;
import net.minecraft.commands.CommandFunction;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ServerFunctionManager implements ResourceManagerReloadListener {
    private static final Logger LOGGER;
    private static final ResourceLocation TICK_FUNCTION_TAG;
    private static final ResourceLocation LOAD_FUNCTION_TAG;
    public static final int PATH_PREFIX_LENGTH;
    public static final int PATH_SUFFIX_LENGTH;
    private final MinecraftServer server;
    private final Map<ResourceLocation, CommandFunction> functions;
    private boolean isInFunction;
    private final ArrayDeque<QueuedCommand> commandQueue;
    private final List<QueuedCommand> nestedCalls;
    private final TagCollection<CommandFunction> tags;
    private final List<CommandFunction> ticking;
    private boolean postReload;
    
    public ServerFunctionManager(final MinecraftServer minecraftServer) {
        this.functions = (Map<ResourceLocation, CommandFunction>)Maps.newHashMap();
        this.commandQueue = (ArrayDeque<QueuedCommand>)new ArrayDeque();
        this.nestedCalls = (List<QueuedCommand>)Lists.newArrayList();
        this.tags = new TagCollection<CommandFunction>((java.util.function.Function<ResourceLocation, java.util.Optional<CommandFunction>>)this::get, "tags/functions", true, "function");
        this.ticking = (List<CommandFunction>)Lists.newArrayList();
        this.server = minecraftServer;
    }
    
    public Optional<CommandFunction> get(final ResourceLocation qv) {
        return (Optional<CommandFunction>)Optional.ofNullable(this.functions.get(qv));
    }
    
    public MinecraftServer getServer() {
        return this.server;
    }
    
    public int getCommandLimit() {
        return this.server.getGameRules().getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
    }
    
    public Map<ResourceLocation, CommandFunction> getFunctions() {
        return this.functions;
    }
    
    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.server.getCommands().getDispatcher();
    }
    
    public void tick() {
        this.server.getProfiler().push((Supplier<String>)ServerFunctionManager.TICK_FUNCTION_TAG::toString);
        for (final CommandFunction ca3 : this.ticking) {
            this.execute(ca3, this.getGameLoopSender());
        }
        this.server.getProfiler().pop();
        if (this.postReload) {
            this.postReload = false;
            final Collection<CommandFunction> collection2 = this.getTags().getTagOrEmpty(ServerFunctionManager.LOAD_FUNCTION_TAG).getValues();
            this.server.getProfiler().push((Supplier<String>)ServerFunctionManager.LOAD_FUNCTION_TAG::toString);
            for (final CommandFunction ca4 : collection2) {
                this.execute(ca4, this.getGameLoopSender());
            }
            this.server.getProfiler().pop();
        }
    }
    
    public int execute(final CommandFunction ca, final CommandSourceStack cd) {
        final int integer4 = this.getCommandLimit();
        if (this.isInFunction) {
            if (this.commandQueue.size() + this.nestedCalls.size() < integer4) {
                this.nestedCalls.add(new QueuedCommand(this, cd, new CommandFunction.FunctionEntry(ca)));
            }
            return 0;
        }
        try {
            this.isInFunction = true;
            int integer5 = 0;
            final CommandFunction.Entry[] arr6 = ca.getEntries();
            for (int integer6 = arr6.length - 1; integer6 >= 0; --integer6) {
                this.commandQueue.push(new QueuedCommand(this, cd, arr6[integer6]));
            }
            while (!this.commandQueue.isEmpty()) {
                try {
                    final QueuedCommand a7 = (QueuedCommand)this.commandQueue.removeFirst();
                    this.server.getProfiler().push((Supplier<String>)a7::toString);
                    a7.execute(this.commandQueue, integer4);
                    if (!this.nestedCalls.isEmpty()) {
                        Lists.reverse((List)this.nestedCalls).forEach(this.commandQueue::addFirst);
                        this.nestedCalls.clear();
                    }
                }
                finally {
                    this.server.getProfiler().pop();
                }
                if (++integer5 >= integer4) {
                    return integer5;
                }
            }
            return integer5;
        }
        finally {
            this.commandQueue.clear();
            this.nestedCalls.clear();
            this.isInFunction = false;
        }
    }
    
    public void onResourceManagerReload(final ResourceManager xi) {
        this.functions.clear();
        this.ticking.clear();
        final Collection<ResourceLocation> collection3 = xi.listResources("functions", (Predicate<String>)(string -> string.endsWith(".mcfunction")));
        final List<CompletableFuture<CommandFunction>> list4 = (List<CompletableFuture<CommandFunction>>)Lists.newArrayList();
        for (final ResourceLocation qv6 : collection3) {
            final String string7 = qv6.getPath();
            final ResourceLocation qv7 = new ResourceLocation(qv6.getNamespace(), string7.substring(ServerFunctionManager.PATH_PREFIX_LENGTH, string7.length() - ServerFunctionManager.PATH_SUFFIX_LENGTH));
            list4.add(CompletableFuture.supplyAsync(() -> readLinesAsync(xi, qv6), SimpleResource.IO_EXECUTOR).thenApplyAsync(list -> CommandFunction.fromLines(qv7, this, (List<String>)list), this.server.getBackgroundTaskExecutor()).handle((ca, throwable) -> this.addFunction(ca, throwable, qv6)));
        }
        CompletableFuture.allOf((CompletableFuture[])list4.toArray((Object[])new CompletableFuture[0])).join();
        if (!this.functions.isEmpty()) {
            ServerFunctionManager.LOGGER.info("Loaded {} custom command functions", this.functions.size());
        }
        this.tags.load((java.util.Map<ResourceLocation, Tag.Builder<CommandFunction>>)this.tags.prepare(xi, this.server.getBackgroundTaskExecutor()).join());
        this.ticking.addAll((Collection)this.tags.getTagOrEmpty(ServerFunctionManager.TICK_FUNCTION_TAG).getValues());
        this.postReload = true;
    }
    
    @Nullable
    private CommandFunction addFunction(final CommandFunction ca, @Nullable final Throwable throwable, final ResourceLocation qv) {
        if (throwable != null) {
            ServerFunctionManager.LOGGER.error("Couldn't load function at {}", qv, throwable);
            return null;
        }
        synchronized (this.functions) {
            this.functions.put(ca.getId(), ca);
        }
        return ca;
    }
    
    private static List<String> readLinesAsync(final ResourceManager xi, final ResourceLocation qv) {
        try (final Resource xh3 = xi.getResource(qv)) {
            return (List<String>)IOUtils.readLines(xh3.getInputStream(), StandardCharsets.UTF_8);
        }
        catch (IOException iOException3) {
            throw new CompletionException((Throwable)iOException3);
        }
    }
    
    public CommandSourceStack getGameLoopSender() {
        return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
    }
    
    public CommandSourceStack getCompilationContext() {
        return new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, (ServerLevel)null, this.server.getFunctionCompilationLevel(), "", (Component)new TextComponent(""), this.server, (Entity)null);
    }
    
    public TagCollection<CommandFunction> getTags() {
        return this.tags;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        TICK_FUNCTION_TAG = new ResourceLocation("tick");
        LOAD_FUNCTION_TAG = new ResourceLocation("load");
        PATH_PREFIX_LENGTH = "functions/".length();
        PATH_SUFFIX_LENGTH = ".mcfunction".length();
    }
    
    public static class QueuedCommand {
        private final ServerFunctionManager manager;
        private final CommandSourceStack sender;
        private final CommandFunction.Entry entry;
        
        public QueuedCommand(final ServerFunctionManager rh, final CommandSourceStack cd, final CommandFunction.Entry c) {
            this.manager = rh;
            this.sender = cd;
            this.entry = c;
        }
        
        public void execute(final ArrayDeque<QueuedCommand> arrayDeque, final int integer) {
            try {
                this.entry.execute(this.manager, this.sender, arrayDeque, integer);
            }
            catch (Throwable t) {}
        }
        
        public String toString() {
            return this.entry.toString();
        }
    }
}
