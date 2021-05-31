package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.LootContext;

public class FillPlayerHead extends LootItemConditionalFunction {
    private final LootContext.EntityTarget entityTarget;
    
    public FillPlayerHead(final LootItemCondition[] arr, final LootContext.EntityTarget c) {
        super(arr);
        this.entityTarget = c;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)ImmutableSet.of(this.entityTarget.getParam());
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        if (bcj.getItem() == Items.PLAYER_HEAD) {
            final Entity aio4 = coy.<Entity>getParamOrNull(this.entityTarget.getParam());
            if (aio4 instanceof Player) {
                final GameProfile gameProfile5 = ((Player)aio4).getGameProfile();
                bcj.getOrCreateTag().put("SkullOwner", (Tag)NbtUtils.writeGameProfile(new CompoundTag(), gameProfile5));
            }
        }
        return bcj;
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<FillPlayerHead> {
        public Serializer() {
            super(new ResourceLocation("fill_player_head"), FillPlayerHead.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final FillPlayerHead cqd, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqd, jsonSerializationContext);
            jsonObject.add("entity", jsonSerializationContext.serialize(cqd.entityTarget));
        }
        
        @Override
        public FillPlayerHead deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final LootContext.EntityTarget c5 = GsonHelper.<LootContext.EntityTarget>getAsObject(jsonObject, "entity", jsonDeserializationContext, (java.lang.Class<? extends LootContext.EntityTarget>)LootContext.EntityTarget.class);
            return new FillPlayerHead(arr, c5);
        }
    }
}
