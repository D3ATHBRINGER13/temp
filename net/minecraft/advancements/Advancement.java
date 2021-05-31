package net.minecraft.advancements;

import org.apache.commons.lang3.ArrayUtils;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.Maps;
import net.minecraft.network.chat.HoverEvent;
import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import java.util.function.Consumer;
import net.minecraft.network.chat.TextComponent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import java.util.Set;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class Advancement {
    private final Advancement parent;
    private final DisplayInfo display;
    private final AdvancementRewards rewards;
    private final ResourceLocation id;
    private final Map<String, Criterion> criteria;
    private final String[][] requirements;
    private final Set<Advancement> children;
    private final Component chatComponent;
    
    public Advancement(final ResourceLocation qv, @Nullable final Advancement q, @Nullable final DisplayInfo z, final AdvancementRewards t, final Map<String, Criterion> map, final String[][] arr) {
        this.children = (Set<Advancement>)Sets.newLinkedHashSet();
        this.id = qv;
        this.display = z;
        this.criteria = (Map<String, Criterion>)ImmutableMap.copyOf((Map)map);
        this.parent = q;
        this.rewards = t;
        this.requirements = arr;
        if (q != null) {
            q.addChild(this);
        }
        if (z == null) {
            this.chatComponent = new TextComponent(qv.toString());
        }
        else {
            final Component jo8 = z.getTitle();
            final ChatFormatting c9 = z.getFrame().getChatColor();
            final Component jo9 = jo8.deepCopy().withStyle(c9).append("\n").append(z.getDescription());
            final Component jo10 = jo8.deepCopy().withStyle((Consumer<Style>)(jw -> jw.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, jo9))));
            this.chatComponent = new TextComponent("[").append(jo10).append("]").withStyle(c9);
        }
    }
    
    public Builder deconstruct() {
        return new Builder((this.parent == null) ? null : this.parent.getId(), this.display, this.rewards, (Map)this.criteria, this.requirements);
    }
    
    @Nullable
    public Advancement getParent() {
        return this.parent;
    }
    
    @Nullable
    public DisplayInfo getDisplay() {
        return this.display;
    }
    
    public AdvancementRewards getRewards() {
        return this.rewards;
    }
    
    public String toString() {
        return new StringBuilder().append("SimpleAdvancement{id=").append(this.getId()).append(", parent=").append((this.parent == null) ? "null" : this.parent.getId()).append(", display=").append(this.display).append(", rewards=").append(this.rewards).append(", criteria=").append(this.criteria).append(", requirements=").append(Arrays.deepToString((Object[])this.requirements)).append('}').toString();
    }
    
    public Iterable<Advancement> getChildren() {
        return (Iterable<Advancement>)this.children;
    }
    
    public Map<String, Criterion> getCriteria() {
        return this.criteria;
    }
    
    public int getMaxCriteraRequired() {
        return this.requirements.length;
    }
    
    public void addChild(final Advancement q) {
        this.children.add(q);
    }
    
    public ResourceLocation getId() {
        return this.id;
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Advancement)) {
            return false;
        }
        final Advancement q3 = (Advancement)object;
        return this.id.equals(q3.id);
    }
    
    public int hashCode() {
        return this.id.hashCode();
    }
    
    public String[][] getRequirements() {
        return this.requirements;
    }
    
    public Component getChatComponent() {
        return this.chatComponent;
    }
    
    public static class Builder {
        private ResourceLocation parentId;
        private Advancement parent;
        private DisplayInfo display;
        private AdvancementRewards rewards;
        private Map<String, Criterion> criteria;
        private String[][] requirements;
        private RequirementsStrategy requirementsStrategy;
        
        private Builder(@Nullable final ResourceLocation qv, @Nullable final DisplayInfo z, final AdvancementRewards t, final Map<String, Criterion> map, final String[][] arr) {
            this.rewards = AdvancementRewards.EMPTY;
            this.criteria = (Map<String, Criterion>)Maps.newLinkedHashMap();
            this.requirementsStrategy = RequirementsStrategy.AND;
            this.parentId = qv;
            this.display = z;
            this.rewards = t;
            this.criteria = map;
            this.requirements = arr;
        }
        
        private Builder() {
            this.rewards = AdvancementRewards.EMPTY;
            this.criteria = (Map<String, Criterion>)Maps.newLinkedHashMap();
            this.requirementsStrategy = RequirementsStrategy.AND;
        }
        
        public static Builder advancement() {
            return new Builder();
        }
        
        public Builder parent(final Advancement q) {
            this.parent = q;
            return this;
        }
        
        public Builder parent(final ResourceLocation qv) {
            this.parentId = qv;
            return this;
        }
        
        public Builder display(final ItemStack bcj, final Component jo2, final Component jo3, @Nullable final ResourceLocation qv, final FrameType aa, final boolean boolean6, final boolean boolean7, final boolean boolean8) {
            return this.display(new DisplayInfo(bcj, jo2, jo3, qv, aa, boolean6, boolean7, boolean8));
        }
        
        public Builder display(final ItemLike bhq, final Component jo2, final Component jo3, @Nullable final ResourceLocation qv, final FrameType aa, final boolean boolean6, final boolean boolean7, final boolean boolean8) {
            return this.display(new DisplayInfo(new ItemStack(bhq.asItem()), jo2, jo3, qv, aa, boolean6, boolean7, boolean8));
        }
        
        public Builder display(final DisplayInfo z) {
            this.display = z;
            return this;
        }
        
        public Builder rewards(final AdvancementRewards.Builder a) {
            return this.rewards(a.build());
        }
        
        public Builder rewards(final AdvancementRewards t) {
            this.rewards = t;
            return this;
        }
        
        public Builder addCriterion(final String string, final CriterionTriggerInstance y) {
            return this.addCriterion(string, new Criterion(y));
        }
        
        public Builder addCriterion(final String string, final Criterion v) {
            if (this.criteria.containsKey(string)) {
                throw new IllegalArgumentException("Duplicate criterion " + string);
            }
            this.criteria.put(string, v);
            return this;
        }
        
        public Builder requirements(final RequirementsStrategy ab) {
            this.requirementsStrategy = ab;
            return this;
        }
        
        public boolean canBuild(final Function<ResourceLocation, Advancement> function) {
            if (this.parentId == null) {
                return true;
            }
            if (this.parent == null) {
                this.parent = (Advancement)function.apply(this.parentId);
            }
            return this.parent != null;
        }
        
        public Advancement build(final ResourceLocation qv) {
            if (!this.canBuild((Function<ResourceLocation, Advancement>)(qv -> null))) {
                throw new IllegalStateException("Tried to build incomplete advancement!");
            }
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements((Collection<String>)this.criteria.keySet());
            }
            return new Advancement(qv, this.parent, this.display, this.rewards, this.criteria, this.requirements);
        }
        
        public Advancement save(final Consumer<Advancement> consumer, final String string) {
            final Advancement q4 = this.build(new ResourceLocation(string));
            consumer.accept(q4);
            return q4;
        }
        
        public JsonObject serializeToJson() {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements((Collection<String>)this.criteria.keySet());
            }
            final JsonObject jsonObject2 = new JsonObject();
            if (this.parent != null) {
                jsonObject2.addProperty("parent", this.parent.getId().toString());
            }
            else if (this.parentId != null) {
                jsonObject2.addProperty("parent", this.parentId.toString());
            }
            if (this.display != null) {
                jsonObject2.add("display", this.display.serializeToJson());
            }
            jsonObject2.add("rewards", this.rewards.serializeToJson());
            final JsonObject jsonObject3 = new JsonObject();
            for (final Map.Entry<String, Criterion> entry5 : this.criteria.entrySet()) {
                jsonObject3.add((String)entry5.getKey(), ((Criterion)entry5.getValue()).serializeToJson());
            }
            jsonObject2.add("criteria", (JsonElement)jsonObject3);
            final JsonArray jsonArray4 = new JsonArray();
            for (final String[] arr8 : this.requirements) {
                final JsonArray jsonArray5 = new JsonArray();
                for (final String string13 : arr8) {
                    jsonArray5.add(string13);
                }
                jsonArray4.add((JsonElement)jsonArray5);
            }
            jsonObject2.add("requirements", (JsonElement)jsonArray4);
            return jsonObject2;
        }
        
        public void serializeToNetwork(final FriendlyByteBuf je) {
            if (this.parentId == null) {
                je.writeBoolean(false);
            }
            else {
                je.writeBoolean(true);
                je.writeResourceLocation(this.parentId);
            }
            if (this.display == null) {
                je.writeBoolean(false);
            }
            else {
                je.writeBoolean(true);
                this.display.serializeToNetwork(je);
            }
            Criterion.serializeToNetwork(this.criteria, je);
            je.writeVarInt(this.requirements.length);
            for (final String[] arr6 : this.requirements) {
                je.writeVarInt(arr6.length);
                for (final String string10 : arr6) {
                    je.writeUtf(string10);
                }
            }
        }
        
        public String toString() {
            return new StringBuilder().append("Task Advancement{parentId=").append(this.parentId).append(", display=").append(this.display).append(", rewards=").append(this.rewards).append(", criteria=").append(this.criteria).append(", requirements=").append(Arrays.deepToString((Object[])this.requirements)).append('}').toString();
        }
        
        public static Builder fromJson(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final ResourceLocation qv3 = jsonObject.has("parent") ? new ResourceLocation(GsonHelper.getAsString(jsonObject, "parent")) : null;
            final DisplayInfo z4 = jsonObject.has("display") ? DisplayInfo.fromJson(GsonHelper.getAsJsonObject(jsonObject, "display"), jsonDeserializationContext) : null;
            final AdvancementRewards t5 = GsonHelper.<AdvancementRewards>getAsObject(jsonObject, "rewards", AdvancementRewards.EMPTY, jsonDeserializationContext, (java.lang.Class<? extends AdvancementRewards>)AdvancementRewards.class);
            final Map<String, Criterion> map6 = Criterion.criteriaFromJson(GsonHelper.getAsJsonObject(jsonObject, "criteria"), jsonDeserializationContext);
            if (map6.isEmpty()) {
                throw new JsonSyntaxException("Advancement criteria cannot be empty");
            }
            final JsonArray jsonArray7 = GsonHelper.getAsJsonArray(jsonObject, "requirements", new JsonArray());
            String[][] arr8 = new String[jsonArray7.size()][];
            for (int integer9 = 0; integer9 < jsonArray7.size(); ++integer9) {
                final JsonArray jsonArray8 = GsonHelper.convertToJsonArray(jsonArray7.get(integer9), new StringBuilder().append("requirements[").append(integer9).append("]").toString());
                arr8[integer9] = new String[jsonArray8.size()];
                for (int integer10 = 0; integer10 < jsonArray8.size(); ++integer10) {
                    arr8[integer9][integer10] = GsonHelper.convertToString(jsonArray8.get(integer10), new StringBuilder().append("requirements[").append(integer9).append("][").append(integer10).append("]").toString());
                }
            }
            if (arr8.length == 0) {
                arr8 = new String[map6.size()][];
                int integer9 = 0;
                for (final String string11 : map6.keySet()) {
                    arr8[integer9++] = new String[] { string11 };
                }
            }
            for (final String[] arr9 : arr8) {
                if (arr9.length == 0 && map6.isEmpty()) {
                    throw new JsonSyntaxException("Requirement entry cannot be empty");
                }
                for (final String string12 : arr9) {
                    if (!map6.containsKey(string12)) {
                        throw new JsonSyntaxException("Unknown required criterion '" + string12 + "'");
                    }
                }
            }
            for (final String string13 : map6.keySet()) {
                boolean boolean11 = false;
                for (final String[] arr10 : arr8) {
                    if (ArrayUtils.contains((Object[])arr10, string13)) {
                        boolean11 = true;
                        break;
                    }
                }
                if (!boolean11) {
                    throw new JsonSyntaxException("Criterion '" + string13 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
                }
            }
            return new Builder(qv3, z4, t5, map6, arr8);
        }
        
        public static Builder fromNetwork(final FriendlyByteBuf je) {
            final ResourceLocation qv2 = je.readBoolean() ? je.readResourceLocation() : null;
            final DisplayInfo z3 = je.readBoolean() ? DisplayInfo.fromNetwork(je) : null;
            final Map<String, Criterion> map4 = Criterion.criteriaFromNetwork(je);
            final String[][] arr5 = new String[je.readVarInt()][];
            for (int integer6 = 0; integer6 < arr5.length; ++integer6) {
                arr5[integer6] = new String[je.readVarInt()];
                for (int integer7 = 0; integer7 < arr5[integer6].length; ++integer7) {
                    arr5[integer6][integer7] = je.readUtf(32767);
                }
            }
            return new Builder(qv2, z3, AdvancementRewards.EMPTY, map4, arr5);
        }
        
        public Map<String, Criterion> getCriteria() {
            return this.criteria;
        }
    }
}
