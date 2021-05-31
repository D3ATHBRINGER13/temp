package net.minecraft.advancements.critereon;

import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonElement;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public class EntityEquipmentPredicate {
    public static final EntityEquipmentPredicate ANY;
    public static final EntityEquipmentPredicate CAPTAIN;
    private final ItemPredicate head;
    private final ItemPredicate chest;
    private final ItemPredicate legs;
    private final ItemPredicate feet;
    private final ItemPredicate mainhand;
    private final ItemPredicate offhand;
    
    public EntityEquipmentPredicate(final ItemPredicate bc1, final ItemPredicate bc2, final ItemPredicate bc3, final ItemPredicate bc4, final ItemPredicate bc5, final ItemPredicate bc6) {
        this.head = bc1;
        this.chest = bc2;
        this.legs = bc3;
        this.feet = bc4;
        this.mainhand = bc5;
        this.offhand = bc6;
    }
    
    public boolean matches(@Nullable final Entity aio) {
        if (this == EntityEquipmentPredicate.ANY) {
            return true;
        }
        if (!(aio instanceof LivingEntity)) {
            return false;
        }
        final LivingEntity aix3 = (LivingEntity)aio;
        return this.head.matches(aix3.getItemBySlot(EquipmentSlot.HEAD)) && this.chest.matches(aix3.getItemBySlot(EquipmentSlot.CHEST)) && this.legs.matches(aix3.getItemBySlot(EquipmentSlot.LEGS)) && this.feet.matches(aix3.getItemBySlot(EquipmentSlot.FEET)) && this.mainhand.matches(aix3.getItemBySlot(EquipmentSlot.MAINHAND)) && this.offhand.matches(aix3.getItemBySlot(EquipmentSlot.OFFHAND));
    }
    
    public static EntityEquipmentPredicate fromJson(@Nullable final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return EntityEquipmentPredicate.ANY;
        }
        final JsonObject jsonObject2 = GsonHelper.convertToJsonObject(jsonElement, "equipment");
        final ItemPredicate bc3 = ItemPredicate.fromJson(jsonObject2.get("head"));
        final ItemPredicate bc4 = ItemPredicate.fromJson(jsonObject2.get("chest"));
        final ItemPredicate bc5 = ItemPredicate.fromJson(jsonObject2.get("legs"));
        final ItemPredicate bc6 = ItemPredicate.fromJson(jsonObject2.get("feet"));
        final ItemPredicate bc7 = ItemPredicate.fromJson(jsonObject2.get("mainhand"));
        final ItemPredicate bc8 = ItemPredicate.fromJson(jsonObject2.get("offhand"));
        return new EntityEquipmentPredicate(bc3, bc4, bc5, bc6, bc7, bc8);
    }
    
    public JsonElement serializeToJson() {
        if (this == EntityEquipmentPredicate.ANY) {
            return (JsonElement)JsonNull.INSTANCE;
        }
        final JsonObject jsonObject2 = new JsonObject();
        jsonObject2.add("head", this.head.serializeToJson());
        jsonObject2.add("chest", this.chest.serializeToJson());
        jsonObject2.add("legs", this.legs.serializeToJson());
        jsonObject2.add("feet", this.feet.serializeToJson());
        jsonObject2.add("mainhand", this.mainhand.serializeToJson());
        jsonObject2.add("offhand", this.offhand.serializeToJson());
        return (JsonElement)jsonObject2;
    }
    
    static {
        ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
        CAPTAIN = new EntityEquipmentPredicate(ItemPredicate.Builder.item().of(Items.WHITE_BANNER).hasNbt(Raid.getLeaderBannerInstance().getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
    }
}
