package net.minecraft.advancements;

import net.minecraft.core.Registry;
import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.ItemLike;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

public class DisplayInfo {
    private final Component title;
    private final Component description;
    private final ItemStack icon;
    private final ResourceLocation background;
    private final FrameType frame;
    private final boolean showToast;
    private final boolean announceChat;
    private final boolean hidden;
    private float x;
    private float y;
    
    public DisplayInfo(final ItemStack bcj, final Component jo2, final Component jo3, @Nullable final ResourceLocation qv, final FrameType aa, final boolean boolean6, final boolean boolean7, final boolean boolean8) {
        this.title = jo2;
        this.description = jo3;
        this.icon = bcj;
        this.background = qv;
        this.frame = aa;
        this.showToast = boolean6;
        this.announceChat = boolean7;
        this.hidden = boolean8;
    }
    
    public void setLocation(final float float1, final float float2) {
        this.x = float1;
        this.y = float2;
    }
    
    public Component getTitle() {
        return this.title;
    }
    
    public Component getDescription() {
        return this.description;
    }
    
    public ItemStack getIcon() {
        return this.icon;
    }
    
    @Nullable
    public ResourceLocation getBackground() {
        return this.background;
    }
    
    public FrameType getFrame() {
        return this.frame;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public boolean shouldShowToast() {
        return this.showToast;
    }
    
    public boolean shouldAnnounceChat() {
        return this.announceChat;
    }
    
    public boolean isHidden() {
        return this.hidden;
    }
    
    public static DisplayInfo fromJson(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
        final Component jo3 = GsonHelper.<Component>getAsObject(jsonObject, "title", jsonDeserializationContext, (java.lang.Class<? extends Component>)Component.class);
        final Component jo4 = GsonHelper.<Component>getAsObject(jsonObject, "description", jsonDeserializationContext, (java.lang.Class<? extends Component>)Component.class);
        if (jo3 == null || jo4 == null) {
            throw new JsonSyntaxException("Both title and description must be set");
        }
        final ItemStack bcj5 = getIcon(GsonHelper.getAsJsonObject(jsonObject, "icon"));
        final ResourceLocation qv6 = jsonObject.has("background") ? new ResourceLocation(GsonHelper.getAsString(jsonObject, "background")) : null;
        final FrameType aa7 = jsonObject.has("frame") ? FrameType.byName(GsonHelper.getAsString(jsonObject, "frame")) : FrameType.TASK;
        final boolean boolean8 = GsonHelper.getAsBoolean(jsonObject, "show_toast", true);
        final boolean boolean9 = GsonHelper.getAsBoolean(jsonObject, "announce_to_chat", true);
        final boolean boolean10 = GsonHelper.getAsBoolean(jsonObject, "hidden", false);
        return new DisplayInfo(bcj5, jo3, jo4, qv6, aa7, boolean8, boolean9, boolean10);
    }
    
    private static ItemStack getIcon(final JsonObject jsonObject) {
        if (!jsonObject.has("item")) {
            throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
        }
        final Item bce2 = GsonHelper.getAsItem(jsonObject, "item");
        if (jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        final ItemStack bcj3 = new ItemStack(bce2);
        if (jsonObject.has("nbt")) {
            try {
                final CompoundTag id4 = TagParser.parseTag(GsonHelper.convertToString(jsonObject.get("nbt"), "nbt"));
                bcj3.setTag(id4);
            }
            catch (CommandSyntaxException commandSyntaxException4) {
                throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException4.getMessage());
            }
        }
        return bcj3;
    }
    
    public void serializeToNetwork(final FriendlyByteBuf je) {
        je.writeComponent(this.title);
        je.writeComponent(this.description);
        je.writeItem(this.icon);
        je.writeEnum(this.frame);
        int integer3 = 0;
        if (this.background != null) {
            integer3 |= 0x1;
        }
        if (this.showToast) {
            integer3 |= 0x2;
        }
        if (this.hidden) {
            integer3 |= 0x4;
        }
        je.writeInt(integer3);
        if (this.background != null) {
            je.writeResourceLocation(this.background);
        }
        je.writeFloat(this.x);
        je.writeFloat(this.y);
    }
    
    public static DisplayInfo fromNetwork(final FriendlyByteBuf je) {
        final Component jo2 = je.readComponent();
        final Component jo3 = je.readComponent();
        final ItemStack bcj4 = je.readItem();
        final FrameType aa5 = je.<FrameType>readEnum(FrameType.class);
        final int integer6 = je.readInt();
        final ResourceLocation qv7 = ((integer6 & 0x1) != 0x0) ? je.readResourceLocation() : null;
        final boolean boolean8 = (integer6 & 0x2) != 0x0;
        final boolean boolean9 = (integer6 & 0x4) != 0x0;
        final DisplayInfo z10 = new DisplayInfo(bcj4, jo2, jo3, qv7, aa5, boolean8, false, boolean9);
        z10.setLocation(je.readFloat(), je.readFloat());
        return z10;
    }
    
    public JsonElement serializeToJson() {
        final JsonObject jsonObject2 = new JsonObject();
        jsonObject2.add("icon", (JsonElement)this.serializeIcon());
        jsonObject2.add("title", Component.Serializer.toJsonTree(this.title));
        jsonObject2.add("description", Component.Serializer.toJsonTree(this.description));
        jsonObject2.addProperty("frame", this.frame.getName());
        jsonObject2.addProperty("show_toast", Boolean.valueOf(this.showToast));
        jsonObject2.addProperty("announce_to_chat", Boolean.valueOf(this.announceChat));
        jsonObject2.addProperty("hidden", Boolean.valueOf(this.hidden));
        if (this.background != null) {
            jsonObject2.addProperty("background", this.background.toString());
        }
        return (JsonElement)jsonObject2;
    }
    
    private JsonObject serializeIcon() {
        final JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("item", Registry.ITEM.getKey(this.icon.getItem()).toString());
        if (this.icon.hasTag()) {
            jsonObject2.addProperty("nbt", this.icon.getTag().toString());
        }
        return jsonObject2;
    }
}
