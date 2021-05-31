package net.minecraft.stats;

import org.apache.logging.log4j.LogManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.SharedConstants;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.Tag;
import java.util.Map;
import com.google.gson.JsonObject;
import java.util.Optional;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import com.google.gson.JsonElement;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import com.google.gson.internal.Streams;
import java.io.Reader;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import com.mojang.datafixers.DataFixer;
import net.minecraft.world.entity.player.Player;
import com.google.gson.JsonParseException;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import com.google.common.collect.Sets;
import java.util.Set;
import java.io.File;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

public class ServerStatsCounter extends StatsCounter {
    private static final Logger LOGGER;
    private final MinecraftServer server;
    private final File file;
    private final Set<Stat<?>> dirty;
    private int lastStatRequest;
    
    public ServerStatsCounter(final MinecraftServer minecraftServer, final File file) {
        this.dirty = (Set<Stat<?>>)Sets.newHashSet();
        this.lastStatRequest = -300;
        this.server = minecraftServer;
        this.file = file;
        if (file.isFile()) {
            try {
                this.parseLocal(minecraftServer.getFixerUpper(), FileUtils.readFileToString(file));
            }
            catch (IOException iOException4) {
                ServerStatsCounter.LOGGER.error("Couldn't read statistics file {}", file, iOException4);
            }
            catch (JsonParseException jsonParseException4) {
                ServerStatsCounter.LOGGER.error("Couldn't parse statistics file {}", file, jsonParseException4);
            }
        }
    }
    
    public void save() {
        try {
            FileUtils.writeStringToFile(this.file, this.toJson());
        }
        catch (IOException iOException2) {
            ServerStatsCounter.LOGGER.error("Couldn't save stats", (Throwable)iOException2);
        }
    }
    
    @Override
    public void setValue(final Player awg, final Stat<?> yv, final int integer) {
        super.setValue(awg, yv, integer);
        this.dirty.add(yv);
    }
    
    private Set<Stat<?>> getDirty() {
        final Set<Stat<?>> set2 = (Set<Stat<?>>)Sets.newHashSet((Iterable)this.dirty);
        this.dirty.clear();
        return set2;
    }
    
    public void parseLocal(final DataFixer dataFixer, final String string) {
        try (final JsonReader jsonReader4 = new JsonReader((Reader)new StringReader(string))) {
            jsonReader4.setLenient(false);
            final JsonElement jsonElement6 = Streams.parse(jsonReader4);
            if (jsonElement6.isJsonNull()) {
                ServerStatsCounter.LOGGER.error("Unable to parse Stat data from {}", this.file);
                return;
            }
            CompoundTag id7 = fromJson(jsonElement6.getAsJsonObject());
            if (!id7.contains("DataVersion", 99)) {
                id7.putInt("DataVersion", 1343);
            }
            id7 = NbtUtils.update(dataFixer, DataFixTypes.STATS, id7, id7.getInt("DataVersion"));
            if (id7.contains("stats", 10)) {
                final CompoundTag id8 = id7.getCompound("stats");
                for (final String string2 : id8.getAllKeys()) {
                    if (id8.contains(string2, 10)) {
                        Util.<StatType<?>>ifElse(Registry.STAT_TYPE.getOptional(new ResourceLocation(string2)), (java.util.function.Consumer<StatType<?>>)(yx -> {
                            final CompoundTag id2 = id8.getCompound(string2);
                            for (final String string2 : id2.getAllKeys()) {
                                if (id2.contains(string2, 99)) {
                                    Util.<Stat<Object>>ifElse(this.getStat((StatType<Object>)yx, string2), (java.util.function.Consumer<Stat<Object>>)(yv -> this.stats.put(yv, id2.getInt(string2))), () -> ServerStatsCounter.LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, string2));
                                }
                                else {
                                    ServerStatsCounter.LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.file, id2.get(string2), string2);
                                }
                            }
                        }), () -> ServerStatsCounter.LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, string2));
                    }
                }
            }
        }
        catch (JsonParseException | IOException ex2) {
            final Exception ex;
            final Exception exception4 = ex;
            ServerStatsCounter.LOGGER.error("Unable to parse Stat data from {}", this.file, exception4);
        }
    }
    
    private <T> Optional<Stat<T>> getStat(final StatType<T> yx, final String string) {
        return (Optional<Stat<T>>)Optional.ofNullable(ResourceLocation.tryParse(string)).flatMap(yx.getRegistry()::getOptional).map(yx::get);
    }
    
    private static CompoundTag fromJson(final JsonObject jsonObject) {
        final CompoundTag id2 = new CompoundTag();
        for (final Map.Entry<String, JsonElement> entry4 : jsonObject.entrySet()) {
            final JsonElement jsonElement5 = (JsonElement)entry4.getValue();
            if (jsonElement5.isJsonObject()) {
                id2.put((String)entry4.getKey(), fromJson(jsonElement5.getAsJsonObject()));
            }
            else {
                if (!jsonElement5.isJsonPrimitive()) {
                    continue;
                }
                final JsonPrimitive jsonPrimitive6 = jsonElement5.getAsJsonPrimitive();
                if (!jsonPrimitive6.isNumber()) {
                    continue;
                }
                id2.putInt((String)entry4.getKey(), jsonPrimitive6.getAsInt());
            }
        }
        return id2;
    }
    
    protected String toJson() {
        final Map<StatType<?>, JsonObject> map2 = (Map<StatType<?>, JsonObject>)Maps.newHashMap();
        for (final Object2IntMap.Entry<Stat<?>> entry4 : this.stats.object2IntEntrySet()) {
            final Stat<?> yv5 = entry4.getKey();
            ((JsonObject)map2.computeIfAbsent(yv5.getType(), yx -> new JsonObject())).addProperty(ServerStatsCounter.getKey(yv5).toString(), (Number)entry4.getIntValue());
        }
        final JsonObject jsonObject3 = new JsonObject();
        for (final Map.Entry<StatType<?>, JsonObject> entry5 : map2.entrySet()) {
            jsonObject3.add(Registry.STAT_TYPE.getKey(entry5.getKey()).toString(), (JsonElement)entry5.getValue());
        }
        final JsonObject jsonObject4 = new JsonObject();
        jsonObject4.add("stats", (JsonElement)jsonObject3);
        jsonObject4.addProperty("DataVersion", (Number)SharedConstants.getCurrentVersion().getWorldVersion());
        return jsonObject4.toString();
    }
    
    private static <T> ResourceLocation getKey(final Stat<T> yv) {
        return yv.getType().getRegistry().getKey(yv.getValue());
    }
    
    public void markAllDirty() {
        this.dirty.addAll((Collection)this.stats.keySet());
    }
    
    public void sendStats(final ServerPlayer vl) {
        final int integer3 = this.server.getTickCount();
        final Object2IntMap<Stat<?>> object2IntMap4 = (Object2IntMap<Stat<?>>)new Object2IntOpenHashMap();
        if (integer3 - this.lastStatRequest > 300) {
            this.lastStatRequest = integer3;
            for (final Stat<?> yv6 : this.getDirty()) {
                object2IntMap4.put(yv6, this.getValue(yv6));
            }
        }
        vl.connection.send(new ClientboundAwardStatsPacket(object2IntMap4));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
