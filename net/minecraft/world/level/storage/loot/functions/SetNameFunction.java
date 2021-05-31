package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.util.GsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import java.util.function.UnaryOperator;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import java.util.Set;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.Logger;

public class SetNameFunction extends LootItemConditionalFunction {
    private static final Logger LOGGER;
    private final Component name;
    @Nullable
    private final LootContext.EntityTarget resolutionContext;
    
    private SetNameFunction(final LootItemCondition[] arr, @Nullable final Component jo, @Nullable final LootContext.EntityTarget c) {
        super(arr);
        this.name = jo;
        this.resolutionContext = c;
    }
    
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set<LootContextParam<?>>)((this.resolutionContext != null) ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of());
    }
    
    public static UnaryOperator<Component> createResolver(final LootContext coy, @Nullable final LootContext.EntityTarget c) {
        if (c != null) {
            final Entity aio3 = coy.<Entity>getParamOrNull(c.getParam());
            if (aio3 != null) {
                final CommandSourceStack cd4 = aio3.createCommandSourceStack().withPermission(2);
                return (UnaryOperator<Component>)(jo -> {
                    try {
                        return ComponentUtils.updateForEntity(cd4, jo, aio3, 0);
                    }
                    catch (CommandSyntaxException commandSyntaxException4) {
                        SetNameFunction.LOGGER.warn("Failed to resolve text component", (Throwable)commandSyntaxException4);
                        return jo;
                    }
                });
            }
        }
        return (UnaryOperator<Component>)(jo -> jo);
    }
    
    public ItemStack run(final ItemStack bcj, final LootContext coy) {
        if (this.name != null) {
            bcj.setHoverName((Component)createResolver(coy, this.resolutionContext).apply(this.name));
        }
        return bcj;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Serializer extends LootItemConditionalFunction.Serializer<SetNameFunction> {
        public Serializer() {
            super(new ResourceLocation("set_name"), SetNameFunction.class);
        }
        
        @Override
        public void serialize(final JsonObject jsonObject, final SetNameFunction cqq, final JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, cqq, jsonSerializationContext);
            if (cqq.name != null) {
                jsonObject.add("name", Component.Serializer.toJsonTree(cqq.name));
            }
            if (cqq.resolutionContext != null) {
                jsonObject.add("entity", jsonSerializationContext.serialize(cqq.resolutionContext));
            }
        }
        
        @Override
        public SetNameFunction deserialize(final JsonObject jsonObject, final JsonDeserializationContext jsonDeserializationContext, final LootItemCondition[] arr) {
            final Component jo5 = Component.Serializer.fromJson(jsonObject.get("name"));
            final LootContext.EntityTarget c6 = GsonHelper.<LootContext.EntityTarget>getAsObject(jsonObject, "entity", (LootContext.EntityTarget)null, jsonDeserializationContext, (java.lang.Class<? extends LootContext.EntityTarget>)LootContext.EntityTarget.class);
            return new SetNameFunction(arr, jo5, c6, null);
        }
    }
}
