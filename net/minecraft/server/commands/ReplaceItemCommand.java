package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import com.google.common.collect.Lists;
import net.minecraft.world.entity.Entity;
import java.util.Collection;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.arguments.EntityArgument;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.arguments.item.ItemArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.arguments.SlotArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ReplaceItemCommand {
    public static final SimpleCommandExceptionType ERROR_NOT_A_CONTAINER;
    public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_SLOT;
    public static final Dynamic2CommandExceptionType ERROR_ENTITY_SLOT;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replaceitem").requires(cd -> cd.hasPermission(2))).then(Commands.literal("block").then(Commands.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).then(Commands.argument("slot", (com.mojang.brigadier.arguments.ArgumentType<Object>)SlotArgument.slot()).then(((RequiredArgumentBuilder)Commands.argument("item", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemArgument.item()).executes(commandContext -> setBlockItem((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)commandContext, "slot"), ItemArgument.getItem((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "item").createItemStack(1, false)))).then(Commands.argument("count", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(1, 64)).executes(commandContext -> setBlockItem((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)commandContext, "slot"), ItemArgument.getItem((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "item").createItemStack(IntegerArgumentType.getInteger(commandContext, "count"), true))))))))).then(Commands.literal("entity").then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.entities()).then(Commands.argument("slot", (com.mojang.brigadier.arguments.ArgumentType<Object>)SlotArgument.slot()).then(((RequiredArgumentBuilder)Commands.argument("item", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemArgument.item()).executes(commandContext -> setEntityItem((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)commandContext, "slot"), ItemArgument.getItem((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "item").createItemStack(1, false)))).then(Commands.argument("count", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(1, 64)).executes(commandContext -> setEntityItem((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)commandContext, "slot"), ItemArgument.getItem((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "item").createItemStack(IntegerArgumentType.getInteger(commandContext, "count"), true)))))))));
    }
    
    private static int setBlockItem(final CommandSourceStack cd, final BlockPos ew, final int integer, final ItemStack bcj) throws CommandSyntaxException {
        final BlockEntity btw5 = cd.getLevel().getBlockEntity(ew);
        if (!(btw5 instanceof Container)) {
            throw ReplaceItemCommand.ERROR_NOT_A_CONTAINER.create();
        }
        final Container ahc6 = (Container)btw5;
        if (integer < 0 || integer >= ahc6.getContainerSize()) {
            throw ReplaceItemCommand.ERROR_INAPPLICABLE_SLOT.create(integer);
        }
        ahc6.setItem(integer, bcj);
        cd.sendSuccess(new TranslatableComponent("commands.replaceitem.block.success", new Object[] { ew.getX(), ew.getY(), ew.getZ(), bcj.getDisplayName() }), true);
        return 1;
    }
    
    private static int setEntityItem(final CommandSourceStack cd, final Collection<? extends Entity> collection, final int integer, final ItemStack bcj) throws CommandSyntaxException {
        final List<Entity> list5 = (List<Entity>)Lists.newArrayListWithCapacity(collection.size());
        for (final Entity aio7 : collection) {
            if (aio7 instanceof ServerPlayer) {
                ((ServerPlayer)aio7).inventoryMenu.broadcastChanges();
            }
            if (aio7.setSlot(integer, bcj.copy())) {
                list5.add(aio7);
                if (!(aio7 instanceof ServerPlayer)) {
                    continue;
                }
                ((ServerPlayer)aio7).inventoryMenu.broadcastChanges();
            }
        }
        if (list5.isEmpty()) {
            throw ReplaceItemCommand.ERROR_ENTITY_SLOT.create(bcj.getDisplayName(), integer);
        }
        if (list5.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.replaceitem.entity.success.single", new Object[] { ((Entity)list5.iterator().next()).getDisplayName(), bcj.getDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.replaceitem.entity.success.multiple", new Object[] { list5.size(), bcj.getDisplayName() }), true);
        }
        return list5.size();
    }
    
    static {
        ERROR_NOT_A_CONTAINER = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.replaceitem.block.failed", new Object[0]));
        ERROR_INAPPLICABLE_SLOT = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.replaceitem.slot.inapplicable", new Object[] { object }));
        ERROR_ENTITY_SLOT = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableComponent("commands.replaceitem.entity.failed", new Object[] { object1, object2 }));
    }
}
