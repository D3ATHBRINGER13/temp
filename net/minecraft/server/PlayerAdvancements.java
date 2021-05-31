package net.minecraft.server;

import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.Packet;
import java.util.Collection;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.Criterion;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameRules;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.google.common.base.Charsets;
import java.io.FileOutputStream;
import java.util.stream.Stream;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.Comparator;
import com.google.gson.JsonParseException;
import com.google.gson.JsonElement;
import net.minecraft.SharedConstants;
import net.minecraft.util.datafix.DataFixTypes;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import com.google.gson.internal.Streams;
import com.mojang.datafixers.types.JsonOps;
import java.io.Reader;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import java.util.Set;
import net.minecraft.advancements.Advancement;
import java.io.File;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancements {
    private static final Logger LOGGER;
    private static final Gson GSON;
    private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> TYPE_TOKEN;
    private final MinecraftServer server;
    private final File file;
    private final Map<Advancement, AdvancementProgress> advancements;
    private final Set<Advancement> visible;
    private final Set<Advancement> visibilityChanged;
    private final Set<Advancement> progressChanged;
    private ServerPlayer player;
    @Nullable
    private Advancement lastSelectedTab;
    private boolean isFirstPacket;
    
    public PlayerAdvancements(final MinecraftServer minecraftServer, final File file, final ServerPlayer vl) {
        this.advancements = (Map<Advancement, AdvancementProgress>)Maps.newLinkedHashMap();
        this.visible = (Set<Advancement>)Sets.newLinkedHashSet();
        this.visibilityChanged = (Set<Advancement>)Sets.newLinkedHashSet();
        this.progressChanged = (Set<Advancement>)Sets.newLinkedHashSet();
        this.isFirstPacket = true;
        this.server = minecraftServer;
        this.file = file;
        this.player = vl;
        this.load();
    }
    
    public void setPlayer(final ServerPlayer vl) {
        this.player = vl;
    }
    
    public void stopListening() {
        for (final CriterionTrigger<?> x3 : CriteriaTriggers.all()) {
            x3.removePlayerListeners(this);
        }
    }
    
    public void reload() {
        this.stopListening();
        this.advancements.clear();
        this.visible.clear();
        this.visibilityChanged.clear();
        this.progressChanged.clear();
        this.isFirstPacket = true;
        this.lastSelectedTab = null;
        this.load();
    }
    
    private void registerListeners() {
        for (final Advancement q3 : this.server.getAdvancements().getAllAdvancements()) {
            this.registerListeners(q3);
        }
    }
    
    private void ensureAllVisible() {
        final List<Advancement> list2 = (List<Advancement>)Lists.newArrayList();
        for (final Map.Entry<Advancement, AdvancementProgress> entry4 : this.advancements.entrySet()) {
            if (((AdvancementProgress)entry4.getValue()).isDone()) {
                list2.add(entry4.getKey());
                this.progressChanged.add(entry4.getKey());
            }
        }
        for (final Advancement q4 : list2) {
            this.ensureVisibility(q4);
        }
    }
    
    private void checkForAutomaticTriggers() {
        for (final Advancement q3 : this.server.getAdvancements().getAllAdvancements()) {
            if (q3.getCriteria().isEmpty()) {
                this.award(q3, "");
                q3.getRewards().grant(this.player);
            }
        }
    }
    
    private void load() {
        if (this.file.isFile()) {
            try (final JsonReader jsonReader2 = new JsonReader((Reader)new StringReader(Files.toString(this.file, StandardCharsets.UTF_8)))) {
                jsonReader2.setLenient(false);
                Dynamic<JsonElement> dynamic4 = (Dynamic<JsonElement>)new Dynamic((DynamicOps)JsonOps.INSTANCE, Streams.parse(jsonReader2));
                if (!dynamic4.get("DataVersion").asNumber().isPresent()) {
                    dynamic4 = (Dynamic<JsonElement>)dynamic4.set("DataVersion", dynamic4.createInt(1343));
                }
                dynamic4 = (Dynamic<JsonElement>)this.server.getFixerUpper().update(DataFixTypes.ADVANCEMENTS.getType(), (Dynamic)dynamic4, dynamic4.get("DataVersion").asInt(0), SharedConstants.getCurrentVersion().getWorldVersion());
                dynamic4 = (Dynamic<JsonElement>)dynamic4.remove("DataVersion");
                final Map<ResourceLocation, AdvancementProgress> map5 = (Map<ResourceLocation, AdvancementProgress>)PlayerAdvancements.GSON.getAdapter((TypeToken)PlayerAdvancements.TYPE_TOKEN).fromJsonTree((JsonElement)dynamic4.getValue());
                if (map5 == null) {
                    throw new JsonParseException("Found null for advancements");
                }
                final Stream<Map.Entry<ResourceLocation, AdvancementProgress>> stream6 = (Stream<Map.Entry<ResourceLocation, AdvancementProgress>>)map5.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue));
                for (final Map.Entry<ResourceLocation, AdvancementProgress> entry8 : (List)stream6.collect(Collectors.toList())) {
                    final Advancement q9 = this.server.getAdvancements().getAdvancement((ResourceLocation)entry8.getKey());
                    if (q9 == null) {
                        PlayerAdvancements.LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry8.getKey(), this.file);
                    }
                    else {
                        this.startProgress(q9, (AdvancementProgress)entry8.getValue());
                    }
                }
            }
            catch (JsonParseException jsonParseException2) {
                PlayerAdvancements.LOGGER.error("Couldn't parse player advancements in {}", this.file, jsonParseException2);
            }
            catch (IOException iOException2) {
                PlayerAdvancements.LOGGER.error("Couldn't access player advancements in {}", this.file, iOException2);
            }
        }
        this.checkForAutomaticTriggers();
        this.ensureAllVisible();
        this.registerListeners();
    }
    
    public void save() {
        final Map<ResourceLocation, AdvancementProgress> map2 = (Map<ResourceLocation, AdvancementProgress>)Maps.newHashMap();
        for (final Map.Entry<Advancement, AdvancementProgress> entry4 : this.advancements.entrySet()) {
            final AdvancementProgress s5 = (AdvancementProgress)entry4.getValue();
            if (s5.hasProgress()) {
                map2.put(((Advancement)entry4.getKey()).getId(), s5);
            }
        }
        if (this.file.getParentFile() != null) {
            this.file.getParentFile().mkdirs();
        }
        final JsonElement jsonElement3 = PlayerAdvancements.GSON.toJsonTree(map2);
        jsonElement3.getAsJsonObject().addProperty("DataVersion", (Number)SharedConstants.getCurrentVersion().getWorldVersion());
        try (final OutputStream outputStream4 = (OutputStream)new FileOutputStream(this.file);
             final Writer writer6 = (Writer)new OutputStreamWriter(outputStream4, Charsets.UTF_8.newEncoder())) {
            PlayerAdvancements.GSON.toJson(jsonElement3, (Appendable)writer6);
        }
        catch (IOException iOException4) {
            PlayerAdvancements.LOGGER.error("Couldn't save player advancements to {}", this.file, iOException4);
        }
    }
    
    public boolean award(final Advancement q, final String string) {
        boolean boolean4 = false;
        final AdvancementProgress s5 = this.getOrStartProgress(q);
        final boolean boolean5 = s5.isDone();
        if (s5.grantProgress(string)) {
            this.unregisterListeners(q);
            this.progressChanged.add(q);
            boolean4 = true;
            if (!boolean5 && s5.isDone()) {
                q.getRewards().grant(this.player);
                if (q.getDisplay() != null && q.getDisplay().shouldAnnounceChat() && this.player.level.getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
                    this.server.getPlayerList().broadcastMessage(new TranslatableComponent("chat.type.advancement." + q.getDisplay().getFrame().getName(), new Object[] { this.player.getDisplayName(), q.getChatComponent() }));
                }
            }
        }
        if (s5.isDone()) {
            this.ensureVisibility(q);
        }
        return boolean4;
    }
    
    public boolean revoke(final Advancement q, final String string) {
        boolean boolean4 = false;
        final AdvancementProgress s5 = this.getOrStartProgress(q);
        if (s5.revokeProgress(string)) {
            this.registerListeners(q);
            this.progressChanged.add(q);
            boolean4 = true;
        }
        if (!s5.hasProgress()) {
            this.ensureVisibility(q);
        }
        return boolean4;
    }
    
    private void registerListeners(final Advancement q) {
        final AdvancementProgress s3 = this.getOrStartProgress(q);
        if (s3.isDone()) {
            return;
        }
        for (final Map.Entry<String, Criterion> entry5 : q.getCriteria().entrySet()) {
            final CriterionProgress w6 = s3.getCriterion((String)entry5.getKey());
            if (w6 != null) {
                if (w6.isDone()) {
                    continue;
                }
                final CriterionTriggerInstance y7 = ((Criterion)entry5.getValue()).getTrigger();
                if (y7 == null) {
                    continue;
                }
                final CriterionTrigger<CriterionTriggerInstance> x8 = CriteriaTriggers.<CriterionTriggerInstance>getCriterion(y7.getCriterion());
                if (x8 == null) {
                    continue;
                }
                x8.addPlayerListener(this, new CriterionTrigger.Listener<CriterionTriggerInstance>(y7, q, (String)entry5.getKey()));
            }
        }
    }
    
    private void unregisterListeners(final Advancement q) {
        final AdvancementProgress s3 = this.getOrStartProgress(q);
        for (final Map.Entry<String, Criterion> entry5 : q.getCriteria().entrySet()) {
            final CriterionProgress w6 = s3.getCriterion((String)entry5.getKey());
            if (w6 != null) {
                if (!w6.isDone() && !s3.isDone()) {
                    continue;
                }
                final CriterionTriggerInstance y7 = ((Criterion)entry5.getValue()).getTrigger();
                if (y7 == null) {
                    continue;
                }
                final CriterionTrigger<CriterionTriggerInstance> x8 = CriteriaTriggers.<CriterionTriggerInstance>getCriterion(y7.getCriterion());
                if (x8 == null) {
                    continue;
                }
                x8.removePlayerListener(this, new CriterionTrigger.Listener<CriterionTriggerInstance>(y7, q, (String)entry5.getKey()));
            }
        }
    }
    
    public void flushDirty(final ServerPlayer vl) {
        if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()) {
            final Map<ResourceLocation, AdvancementProgress> map3 = (Map<ResourceLocation, AdvancementProgress>)Maps.newHashMap();
            final Set<Advancement> set4 = (Set<Advancement>)Sets.newLinkedHashSet();
            final Set<ResourceLocation> set5 = (Set<ResourceLocation>)Sets.newLinkedHashSet();
            for (final Advancement q7 : this.progressChanged) {
                if (this.visible.contains(q7)) {
                    map3.put(q7.getId(), this.advancements.get(q7));
                }
            }
            for (final Advancement q7 : this.visibilityChanged) {
                if (this.visible.contains(q7)) {
                    set4.add(q7);
                }
                else {
                    set5.add(q7.getId());
                }
            }
            if (this.isFirstPacket || !map3.isEmpty() || !set4.isEmpty() || !set5.isEmpty()) {
                vl.connection.send(new ClientboundUpdateAdvancementsPacket(this.isFirstPacket, (Collection<Advancement>)set4, set5, map3));
                this.visibilityChanged.clear();
                this.progressChanged.clear();
            }
        }
        this.isFirstPacket = false;
    }
    
    public void setSelectedTab(@Nullable final Advancement q) {
        final Advancement q2 = this.lastSelectedTab;
        if (q != null && q.getParent() == null && q.getDisplay() != null) {
            this.lastSelectedTab = q;
        }
        else {
            this.lastSelectedTab = null;
        }
        if (q2 != this.lastSelectedTab) {
            this.player.connection.send(new ClientboundSelectAdvancementsTabPacket((this.lastSelectedTab == null) ? null : this.lastSelectedTab.getId()));
        }
    }
    
    public AdvancementProgress getOrStartProgress(final Advancement q) {
        AdvancementProgress s3 = (AdvancementProgress)this.advancements.get(q);
        if (s3 == null) {
            s3 = new AdvancementProgress();
            this.startProgress(q, s3);
        }
        return s3;
    }
    
    private void startProgress(final Advancement q, final AdvancementProgress s) {
        s.update(q.getCriteria(), q.getRequirements());
        this.advancements.put(q, s);
    }
    
    private void ensureVisibility(final Advancement q) {
        final boolean boolean3 = this.shouldBeVisible(q);
        final boolean boolean4 = this.visible.contains(q);
        if (boolean3 && !boolean4) {
            this.visible.add(q);
            this.visibilityChanged.add(q);
            if (this.advancements.containsKey(q)) {
                this.progressChanged.add(q);
            }
        }
        else if (!boolean3 && boolean4) {
            this.visible.remove(q);
            this.visibilityChanged.add(q);
        }
        if (boolean3 != boolean4 && q.getParent() != null) {
            this.ensureVisibility(q.getParent());
        }
        for (final Advancement q2 : q.getChildren()) {
            this.ensureVisibility(q2);
        }
    }
    
    private boolean shouldBeVisible(Advancement q) {
        for (int integer3 = 0; q != null && integer3 <= 2; q = q.getParent(), ++integer3) {
            if (integer3 == 0 && this.hasCompletedChildrenOrSelf(q)) {
                return true;
            }
            if (q.getDisplay() == null) {
                return false;
            }
            final AdvancementProgress s4 = this.getOrStartProgress(q);
            if (s4.isDone()) {
                return true;
            }
            if (q.getDisplay().isHidden()) {
                return false;
            }
        }
        return false;
    }
    
    private boolean hasCompletedChildrenOrSelf(final Advancement q) {
        final AdvancementProgress s3 = this.getOrStartProgress(q);
        if (s3.isDone()) {
            return true;
        }
        for (final Advancement q2 : q.getChildren()) {
            if (this.hasCompletedChildrenOrSelf(q2)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().registerTypeAdapter((Type)AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter((Type)ResourceLocation.class, new ResourceLocation.Serializer()).setPrettyPrinting().create();
        TYPE_TOKEN = new TypeToken<Map<ResourceLocation, AdvancementProgress>>() {};
    }
}
