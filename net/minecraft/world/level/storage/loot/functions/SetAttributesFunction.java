package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import com.google.gson.JsonSyntaxException;
import com.google.common.collect.Lists;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.UUID;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.item.ItemStack;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import java.util.List;

public class SetAttributesFunction extends LootItemConditionalFunction {
    private final List<Modifier> modifiers;
    
    private SetAttributesFunction(final LootItemCondition[] arr, final List<Modifier> list) {
        super(arr);
        this.modifiers = (List<Modifier>)ImmutableList.copyOf((Collection)list);
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        final Random random4 = coy.getRandom();
        for (final Modifier b6 : this.modifiers) {
            UUID uUID7 = b6.id;
            if (uUID7 == null) {
                uUID7 = UUID.randomUUID();
            }
            final EquipmentSlot ait8 = b6.slots[random4.nextInt(b6.slots.length)];
            bcj.addAttributeModifier(b6.attribute, new AttributeModifier(uUID7, b6.name, b6.amount.getFloat(random4), b6.operation), ait8);
        }
        return bcj;
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<SetAttributesFunction> {
        public Serializer() {
            super(new ResourceLocation("set_attributes"), SetAttributesFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final SetAttributesFunction cqk, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqk, jsonSerializationContext);
            final JsonArray jsonArray5 = new JsonArray();
            for (final Modifier b7 : cqk.modifiers) {
                jsonArray5.add((JsonElement)b7.serialize(jsonSerializationContext));
            }
            jsonObject.add("modifiers", (JsonElement)jsonArray5);
        }
        
        @Override
        public SetAttributesFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final JsonArray jsonArray5 = GsonHelper.getAsJsonArray(jsonObject, "modifiers");
            final List<Modifier> list6 = (List<Modifier>)Lists.newArrayListWithExpectedSize(jsonArray5.size());
            for (final JsonElement jsonElement8 : jsonArray5) {
                list6.add(Modifier.deserialize(GsonHelper.convertToJsonObject(jsonElement8, "modifier"), jsonDeserializationContext));
            }
            if (list6.isEmpty()) {
                throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
            }
            return new SetAttributesFunction(arr, list6, null);
        }
    }
    
    static class Modifier {
        private final String name;
        private final String attribute;
        private final AttributeModifier.Operation operation;
        private final RandomValueBounds amount;
        @Nullable
        private final UUID id;
        private final EquipmentSlot[] slots;
        
        private Modifier(final String string1, final String string2, final AttributeModifier.Operation a, final RandomValueBounds cpg, final EquipmentSlot[] arr, @Nullable final UUID uUID) {
            this.name = string1;
            this.attribute = string2;
            this.operation = a;
            this.amount = cpg;
            this.id = uUID;
            this.slots = arr;
        }
        
        public JsonObject serialize(final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("name", this.name);
            jsonObject3.addProperty("attribute", this.attribute);
            jsonObject3.addProperty("operation", operationToString(this.operation));
            jsonObject3.add("amount", jsonSerializationContext.serialize(this.amount));
            if (this.id != null) {
                jsonObject3.addProperty("id", this.id.toString());
            }
            if (this.slots.length == 1) {
                jsonObject3.addProperty("slot", this.slots[0].getName());
            }
            else {
                final JsonArray jsonArray4 = new JsonArray();
                for (final EquipmentSlot ait8 : this.slots) {
                    jsonArray4.add((JsonElement)new JsonPrimitive(ait8.getName()));
                }
                jsonObject3.add("slot", (JsonElement)jsonArray4);
            }
            return jsonObject3;
        }
        
        public static Modifier deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext) {
            final String string3 = GsonHelper.getAsString(jsonObject, "name");
            final String string4 = GsonHelper.getAsString(jsonObject, "attribute");
            final AttributeModifier.Operation a5 = operationFromString(GsonHelper.getAsString(jsonObject, "operation"));
            final RandomValueBounds cpg6 = GsonHelper.<RandomValueBounds>getAsObject(jsonObject, "amount", jsonDeserializationContext, (java.lang.Class<? extends RandomValueBounds>)RandomValueBounds.class);
            UUID uUID8 = null;
            EquipmentSlot[] arr7;
            if (GsonHelper.isStringValue(jsonObject, "slot")) {
                arr7 = new EquipmentSlot[] { EquipmentSlot.byName(GsonHelper.getAsString(jsonObject, "slot")) };
            }
            else {
                if (!GsonHelper.isArrayNode(jsonObject, "slot")) {
                    throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
                }
                final JsonArray jsonArray9 = GsonHelper.getAsJsonArray(jsonObject, "slot");
                arr7 = new EquipmentSlot[jsonArray9.size()];
                int integer10 = 0;
                for (final JsonElement jsonElement12 : jsonArray9) {
                    arr7[integer10++] = EquipmentSlot.byName(GsonHelper.convertToString(jsonElement12, "slot"));
                }
                if (arr7.length == 0) {
                    throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
                }
            }
            if (jsonObject.has("id")) {
                final String string5 = GsonHelper.getAsString(jsonObject, "id");
                try {
                    uUID8 = UUID.fromString(string5);
                }
                catch (IllegalArgumentException illegalArgumentException10) {
                    throw new JsonSyntaxException("Invalid attribute modifier id '" + string5 + "' (must be UUID format, with dashes)");
                }
            }
            return new Modifier(string3, string4, a5, cpg6, arr7, uUID8);
        }
        
        private static String operationToString(final AttributeModifier.Operation a) {
            switch (a) {
                case ADDITION: {
                    return "addition";
                }
                case MULTIPLY_BASE: {
                    return "multiply_base";
                }
                case MULTIPLY_TOTAL: {
                    return "multiply_total";
                }
                default: {
                    throw new IllegalArgumentException(new StringBuilder().append("Unknown operation ").append(a).toString());
                }
            }
        }
        
        private static AttributeModifier.Operation operationFromString(final String string) {
            switch (string) {
                case "addition": {
                    return AttributeModifier.Operation.ADDITION;
                }
                case "multiply_base": {
                    return AttributeModifier.Operation.MULTIPLY_BASE;
                }
                case "multiply_total": {
                    return AttributeModifier.Operation.MULTIPLY_TOTAL;
                }
                default: {
                    throw new JsonSyntaxException("Unknown attribute modifier operation " + string);
                }
            }
        }
    }
}
