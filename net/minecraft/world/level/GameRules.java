package net.minecraft.world.level;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import javax.annotation.Nullable;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.function.BiConsumer;
import java.util.function.Function;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import com.google.common.collect.Maps;
import java.util.Comparator;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class GameRules {
    private static final Logger LOGGER;
    private static final Map<Key<?>, Type<?>> GAME_RULE_TYPES;
    public static final Key<BooleanValue> RULE_DOFIRETICK;
    public static final Key<BooleanValue> RULE_MOBGRIEFING;
    public static final Key<BooleanValue> RULE_KEEPINVENTORY;
    public static final Key<BooleanValue> RULE_DOMOBSPAWNING;
    public static final Key<BooleanValue> RULE_DOMOBLOOT;
    public static final Key<BooleanValue> RULE_DOBLOCKDROPS;
    public static final Key<BooleanValue> RULE_DOENTITYDROPS;
    public static final Key<BooleanValue> RULE_COMMANDBLOCKOUTPUT;
    public static final Key<BooleanValue> RULE_NATURAL_REGENERATION;
    public static final Key<BooleanValue> RULE_DAYLIGHT;
    public static final Key<BooleanValue> RULE_LOGADMINCOMMANDS;
    public static final Key<BooleanValue> RULE_SHOWDEATHMESSAGES;
    public static final Key<IntegerValue> RULE_RANDOMTICKING;
    public static final Key<BooleanValue> RULE_SENDCOMMANDFEEDBACK;
    public static final Key<BooleanValue> RULE_REDUCEDDEBUGINFO;
    public static final Key<BooleanValue> RULE_SPECTATORSGENERATECHUNKS;
    public static final Key<IntegerValue> RULE_SPAWN_RADIUS;
    public static final Key<BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK;
    public static final Key<IntegerValue> RULE_MAX_ENTITY_CRAMMING;
    public static final Key<BooleanValue> RULE_WEATHER_CYCLE;
    public static final Key<BooleanValue> RULE_LIMITED_CRAFTING;
    public static final Key<IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH;
    public static final Key<BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS;
    public static final Key<BooleanValue> RULE_DISABLE_RAIDS;
    private final Map<Key<?>, Value<?>> rules;
    
    private static <T extends Value<T>> Key<T> register(final String string, final Type<T> e) {
        final Key<T> d3 = new Key<T>(string);
        final Type<?> e2 = GameRules.GAME_RULE_TYPES.put(d3, e);
        if (e2 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + string);
        }
        return d3;
    }
    
    public GameRules() {
        this.rules = (Map<Key<?>, Value<?>>)GameRules.GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((Type)entry.getValue()).createRule()));
    }
    
    public <T extends Value<T>> T getRule(final Key<T> d) {
        return (T)this.rules.get(d);
    }
    
    public CompoundTag createTag() {
        final CompoundTag id2 = new CompoundTag();
        this.rules.forEach((d, f) -> id2.putString(d.id2, f.serialize()));
        return id2;
    }
    
    public void loadFromTag(final CompoundTag id) {
        this.rules.forEach((d, f) -> f.deserialize(id.getString(d.id)));
    }
    
    public static void visitGameRuleTypes(final GameRuleTypeVisitor b) {
        GameRules.GAME_RULE_TYPES.forEach((d, e) -> GameRules.<Value>cap(b, d, e));
    }
    
    private static <T extends Value<T>> void cap(final GameRuleTypeVisitor b, final Key<?> d, final Type<?> e) {
        final Key<T> d2 = (Key<T>)d;
        final Type<T> e2 = (Type<T>)e;
        b.<T>visit(d2, e2);
    }
    
    public boolean getBoolean(final Key<BooleanValue> d) {
        return this.<BooleanValue>getRule(d).get();
    }
    
    public int getInt(final Key<IntegerValue> d) {
        return this.<IntegerValue>getRule(d).get();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GAME_RULE_TYPES = (Map)Maps.newTreeMap(Comparator.comparing(d -> d.id));
        RULE_DOFIRETICK = GameRules.<BooleanValue>register("doFireTick", (Type<BooleanValue>)create(true));
        RULE_MOBGRIEFING = GameRules.<BooleanValue>register("mobGriefing", (Type<BooleanValue>)create(true));
        RULE_KEEPINVENTORY = GameRules.<BooleanValue>register("keepInventory", (Type<BooleanValue>)create(false));
        RULE_DOMOBSPAWNING = GameRules.<BooleanValue>register("doMobSpawning", (Type<BooleanValue>)create(true));
        RULE_DOMOBLOOT = GameRules.<BooleanValue>register("doMobLoot", (Type<BooleanValue>)create(true));
        RULE_DOBLOCKDROPS = GameRules.<BooleanValue>register("doTileDrops", (Type<BooleanValue>)create(true));
        RULE_DOENTITYDROPS = GameRules.<BooleanValue>register("doEntityDrops", (Type<BooleanValue>)create(true));
        RULE_COMMANDBLOCKOUTPUT = GameRules.<BooleanValue>register("commandBlockOutput", (Type<BooleanValue>)create(true));
        RULE_NATURAL_REGENERATION = GameRules.<BooleanValue>register("naturalRegeneration", (Type<BooleanValue>)create(true));
        RULE_DAYLIGHT = GameRules.<BooleanValue>register("doDaylightCycle", (Type<BooleanValue>)create(true));
        RULE_LOGADMINCOMMANDS = GameRules.<BooleanValue>register("logAdminCommands", (Type<BooleanValue>)create(true));
        RULE_SHOWDEATHMESSAGES = GameRules.<BooleanValue>register("showDeathMessages", (Type<BooleanValue>)create(true));
        RULE_RANDOMTICKING = GameRules.<IntegerValue>register("randomTickSpeed", (Type<IntegerValue>)create(3));
        RULE_SENDCOMMANDFEEDBACK = GameRules.<BooleanValue>register("sendCommandFeedback", (Type<BooleanValue>)create(true));
        RULE_REDUCEDDEBUGINFO = GameRules.<BooleanValue>register("reducedDebugInfo", (Type<BooleanValue>)create(false, (BiConsumer<MinecraftServer, BooleanValue>)((minecraftServer, a) -> {
            final byte byte3 = (byte)(a.get() ? 22 : 23);
            for (final ServerPlayer vl5 : minecraftServer.getPlayerList().getPlayers()) {
                vl5.connection.send(new ClientboundEntityEventPacket(vl5, byte3));
            }
        })));
        RULE_SPECTATORSGENERATECHUNKS = GameRules.<BooleanValue>register("spectatorsGenerateChunks", (Type<BooleanValue>)create(true));
        RULE_SPAWN_RADIUS = GameRules.<IntegerValue>register("spawnRadius", (Type<IntegerValue>)create(10));
        RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.<BooleanValue>register("disableElytraMovementCheck", (Type<BooleanValue>)create(false));
        RULE_MAX_ENTITY_CRAMMING = GameRules.<IntegerValue>register("maxEntityCramming", (Type<IntegerValue>)create(24));
        RULE_WEATHER_CYCLE = GameRules.<BooleanValue>register("doWeatherCycle", (Type<BooleanValue>)create(true));
        RULE_LIMITED_CRAFTING = GameRules.<BooleanValue>register("doLimitedCrafting", (Type<BooleanValue>)create(false));
        RULE_MAX_COMMAND_CHAIN_LENGTH = GameRules.<IntegerValue>register("maxCommandChainLength", (Type<IntegerValue>)create(65536));
        RULE_ANNOUNCE_ADVANCEMENTS = GameRules.<BooleanValue>register("announceAdvancements", (Type<BooleanValue>)create(true));
        RULE_DISABLE_RAIDS = GameRules.<BooleanValue>register("disableRaids", (Type<BooleanValue>)create(false));
    }
    
    public static final class Key<T extends Value<T>> {
        private final String id;
        
        public Key(final String string) {
            this.id = string;
        }
        
        public String toString() {
            return this.id;
        }
        
        public boolean equals(final Object object) {
            return this == object || (object instanceof Key && ((Key)object).id.equals(this.id));
        }
        
        public int hashCode() {
            return this.id.hashCode();
        }
        
        public String getId() {
            return this.id;
        }
    }
    
    public static class Type<T extends Value<T>> {
        private final Supplier<ArgumentType<?>> argument;
        private final Function<Type<T>, T> constructor;
        private final BiConsumer<MinecraftServer, T> callback;
        
        private Type(final Supplier<ArgumentType<?>> supplier, final Function<Type<T>, T> function, final BiConsumer<MinecraftServer, T> biConsumer) {
            this.argument = supplier;
            this.constructor = function;
            this.callback = biConsumer;
        }
        
        public RequiredArgumentBuilder<CommandSourceStack, ?> createArgument(final String string) {
            return Commands.argument(string, (com.mojang.brigadier.arguments.ArgumentType<?>)this.argument.get());
        }
        
        public T createRule() {
            return (T)this.constructor.apply(this);
        }
    }
    
    public abstract static class Value<T extends Value<T>> {
        private final Type<T> type;
        
        public Value(final Type<T> e) {
            this.type = e;
        }
        
        protected abstract void updateFromArgument(final CommandContext<CommandSourceStack> commandContext, final String string);
        
        public void setFromArgument(final CommandContext<CommandSourceStack> commandContext, final String string) {
            this.updateFromArgument(commandContext, string);
            this.onChanged(((CommandSourceStack)commandContext.getSource()).getServer());
        }
        
        protected void onChanged(@Nullable final MinecraftServer minecraftServer) {
            if (minecraftServer != null) {
                ((Type<Value>)this.type).callback.accept(minecraftServer, this.getSelf());
            }
        }
        
        protected abstract void deserialize(final String string);
        
        protected abstract String serialize();
        
        public String toString() {
            return this.serialize();
        }
        
        public abstract int getCommandResult();
        
        protected abstract T getSelf();
    }
    
    public static class IntegerValue extends Value<IntegerValue> {
        private int value;
        
        private static Type<IntegerValue> create(final int integer, final BiConsumer<MinecraftServer, IntegerValue> biConsumer) {
            return new Type<IntegerValue>(IntegerArgumentType::integer, e -> new IntegerValue(e, integer), (BiConsumer)biConsumer);
        }
        
        private static Type<IntegerValue> create(final int integer) {
            return create(integer, (BiConsumer<MinecraftServer, IntegerValue>)((minecraftServer, c) -> {}));
        }
        
        public IntegerValue(final Type<IntegerValue> e, final int integer) {
            super(e);
            this.value = integer;
        }
        
        @Override
        protected void updateFromArgument(final CommandContext<CommandSourceStack> commandContext, final String string) {
            this.value = IntegerArgumentType.getInteger((CommandContext)commandContext, string);
        }
        
        public int get() {
            return this.value;
        }
        
        @Override
        protected String serialize() {
            return Integer.toString(this.value);
        }
        
        @Override
        protected void deserialize(final String string) {
            this.value = safeParse(string);
        }
        
        private static int safeParse(final String string) {
            if (!string.isEmpty()) {
                try {
                    return Integer.parseInt(string);
                }
                catch (NumberFormatException numberFormatException2) {
                    GameRules.LOGGER.warn("Failed to parse integer {}", string);
                }
            }
            return 0;
        }
        
        @Override
        public int getCommandResult() {
            return this.value;
        }
        
        @Override
        protected IntegerValue getSelf() {
            return this;
        }
    }
    
    public static class BooleanValue extends Value<BooleanValue> {
        private boolean value;
        
        private static Type<BooleanValue> create(final boolean boolean1, final BiConsumer<MinecraftServer, BooleanValue> biConsumer) {
            return new Type<BooleanValue>(BoolArgumentType::bool, e -> new BooleanValue(e, boolean1), (BiConsumer)biConsumer);
        }
        
        private static Type<BooleanValue> create(final boolean boolean1) {
            return create(boolean1, (BiConsumer<MinecraftServer, BooleanValue>)((minecraftServer, a) -> {}));
        }
        
        public BooleanValue(final Type<BooleanValue> e, final boolean boolean2) {
            super(e);
            this.value = boolean2;
        }
        
        @Override
        protected void updateFromArgument(final CommandContext<CommandSourceStack> commandContext, final String string) {
            this.value = BoolArgumentType.getBool((CommandContext)commandContext, string);
        }
        
        public boolean get() {
            return this.value;
        }
        
        public void set(final boolean boolean1, @Nullable final MinecraftServer minecraftServer) {
            this.value = boolean1;
            this.onChanged(minecraftServer);
        }
        
        @Override
        protected String serialize() {
            return Boolean.toString(this.value);
        }
        
        @Override
        protected void deserialize(final String string) {
            this.value = Boolean.parseBoolean(string);
        }
        
        @Override
        public int getCommandResult() {
            return this.value ? 1 : 0;
        }
        
        @Override
        protected BooleanValue getSelf() {
            return this;
        }
    }
    
    @FunctionalInterface
    public interface GameRuleTypeVisitor {
         <T extends Value<T>> void visit(final Key<T> d, final Type<T> e);
    }
}
