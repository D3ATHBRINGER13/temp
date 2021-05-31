package net.minecraft.server.commands;

import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.commands.SharedSuggestionProvider;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.Message;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;
import java.util.Objects;
import java.util.Iterator;
import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.Container;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.arguments.SlotArgument;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class LootCommand {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_LOOT_TABLE;
    private static final DynamicCommandExceptionType ERROR_NO_HELD_ITEMS;
    private static final DynamicCommandExceptionType ERROR_NO_LOOT_TABLE;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)LootCommand.<LiteralArgumentBuilder>addTargets((LiteralArgumentBuilder)Commands.literal("loot").requires(cd -> cd.hasPermission(2)), (argumentBuilder, b) -> argumentBuilder.then(Commands.literal("fish").then(Commands.argument("loot_table", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)LootCommand.SUGGEST_LOOT_TABLE).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).executes(commandContext -> dropFishingLoot((CommandContext<CommandSourceStack>)commandContext, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), ItemStack.EMPTY, b))).then(Commands.argument("tool", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemArgument.item()).executes(commandContext -> dropFishingLoot((CommandContext<CommandSourceStack>)commandContext, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), ItemArgument.getItem((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "tool").createItemStack(1, false), b)))).then(Commands.literal("mainhand").executes(commandContext -> dropFishingLoot((CommandContext<CommandSourceStack>)commandContext, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.MAINHAND), b)))).then(Commands.literal("offhand").executes(commandContext -> dropFishingLoot((CommandContext<CommandSourceStack>)commandContext, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.OFFHAND), b)))))).then(Commands.literal("loot").then(Commands.argument("loot_table", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)LootCommand.SUGGEST_LOOT_TABLE).executes(commandContext -> dropChestLoot((CommandContext<CommandSourceStack>)commandContext, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "loot_table"), b)))).then(Commands.literal("kill").then(Commands.argument("target", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.entity()).executes(commandContext -> dropKillLoot((CommandContext<CommandSourceStack>)commandContext, EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), b)))).then(Commands.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).executes(commandContext -> dropBlockLoot((CommandContext<CommandSourceStack>)commandContext, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), ItemStack.EMPTY, b))).then(Commands.argument("tool", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemArgument.item()).executes(commandContext -> dropBlockLoot((CommandContext<CommandSourceStack>)commandContext, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), ItemArgument.getItem((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "tool").createItemStack(1, false), b)))).then(Commands.literal("mainhand").executes(commandContext -> dropBlockLoot((CommandContext<CommandSourceStack>)commandContext, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.MAINHAND), b)))).then(Commands.literal("offhand").executes(commandContext -> dropBlockLoot((CommandContext<CommandSourceStack>)commandContext, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.OFFHAND), b)))))));
    }
    
    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T addTargets(final T argumentBuilder, final TailProvider c) {
        return (T)argumentBuilder.then(((LiteralArgumentBuilder)Commands.literal("replace").then(Commands.literal("entity").then(Commands.argument("entities", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.entities()).then(c.construct(Commands.argument("slot", (com.mojang.brigadier.arguments.ArgumentType<Object>)SlotArgument.slot()), (commandContext, list, a) -> entityReplace(EntityArgument.getEntities(commandContext, "entities"), SlotArgument.getSlot(commandContext, "slot"), list.size(), list, a)).then((ArgumentBuilder)c.construct(Commands.argument("count", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)), (commandContext, list, a) -> entityReplace(EntityArgument.getEntities(commandContext, "entities"), SlotArgument.getSlot(commandContext, "slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, "count"), list, a))))))).then(Commands.literal("block").then(Commands.argument("targetPos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()).then(c.construct(Commands.argument("slot", (com.mojang.brigadier.arguments.ArgumentType<Object>)SlotArgument.slot()), (commandContext, list, a) -> blockReplace((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "targetPos"), SlotArgument.getSlot(commandContext, "slot"), list.size(), list, a)).then((ArgumentBuilder)c.construct(Commands.argument("count", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)), (commandContext, list, a) -> blockReplace((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "targetPos"), IntegerArgumentType.getInteger((CommandContext)commandContext, "slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, "count"), list, a))))))).then(Commands.literal("insert").then((ArgumentBuilder)c.construct(Commands.argument("targetPos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgument.blockPos()), (commandContext, list, a) -> blockDistribute((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "targetPos"), list, a)))).then(Commands.literal("give").then((ArgumentBuilder)c.construct(Commands.argument("players", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.players()), (commandContext, list, a) -> playerGive(EntityArgument.getPlayers(commandContext, "players"), list, a)))).then(Commands.literal("spawn").then((ArgumentBuilder)c.construct(Commands.argument("targetPos", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3Argument.vec3()), (commandContext, list, a) -> dropInWorld((CommandSourceStack)commandContext.getSource(), Vec3Argument.getVec3(commandContext, "targetPos"), list, a))));
    }
    
    private static Container getContainer(final CommandSourceStack cd, final BlockPos ew) throws CommandSyntaxException {
        final BlockEntity btw3 = cd.getLevel().getBlockEntity(ew);
        if (!(btw3 instanceof Container)) {
            throw ReplaceItemCommand.ERROR_NOT_A_CONTAINER.create();
        }
        return (Container)btw3;
    }
    
    private static int blockDistribute(final CommandSourceStack cd, final BlockPos ew, final List<ItemStack> list, final Callback a) throws CommandSyntaxException {
        final Container ahc5 = getContainer(cd, ew);
        final List<ItemStack> list2 = (List<ItemStack>)Lists.newArrayListWithCapacity(list.size());
        for (final ItemStack bcj8 : list) {
            if (distributeToContainer(ahc5, bcj8.copy())) {
                ahc5.setChanged();
                list2.add(bcj8);
            }
        }
        a.accept(list2);
        return list2.size();
    }
    
    private static boolean distributeToContainer(final Container ahc, final ItemStack bcj) {
        boolean boolean3 = false;
        for (int integer4 = 0; integer4 < ahc.getContainerSize() && !bcj.isEmpty(); ++integer4) {
            final ItemStack bcj2 = ahc.getItem(integer4);
            if (ahc.canPlaceItem(integer4, bcj)) {
                if (bcj2.isEmpty()) {
                    ahc.setItem(integer4, bcj);
                    boolean3 = true;
                    break;
                }
                if (canMergeItems(bcj2, bcj)) {
                    final int integer5 = bcj.getMaxStackSize() - bcj2.getCount();
                    final int integer6 = Math.min(bcj.getCount(), integer5);
                    bcj.shrink(integer6);
                    bcj2.grow(integer6);
                    boolean3 = true;
                }
            }
        }
        return boolean3;
    }
    
    private static int blockReplace(final CommandSourceStack cd, final BlockPos ew, final int integer3, final int integer4, final List<ItemStack> list, final Callback a) throws CommandSyntaxException {
        final Container ahc7 = getContainer(cd, ew);
        final int integer5 = ahc7.getContainerSize();
        if (integer3 < 0 || integer3 >= integer5) {
            throw ReplaceItemCommand.ERROR_INAPPLICABLE_SLOT.create(integer3);
        }
        final List<ItemStack> list2 = (List<ItemStack>)Lists.newArrayListWithCapacity(list.size());
        for (int integer6 = 0; integer6 < integer4; ++integer6) {
            final int integer7 = integer3 + integer6;
            final ItemStack bcj12 = (ItemStack)((integer6 < list.size()) ? list.get(integer6) : ItemStack.EMPTY);
            if (ahc7.canPlaceItem(integer7, bcj12)) {
                ahc7.setItem(integer7, bcj12);
                list2.add(bcj12);
            }
        }
        a.accept(list2);
        return list2.size();
    }
    
    private static boolean canMergeItems(final ItemStack bcj1, final ItemStack bcj2) {
        return bcj1.getItem() == bcj2.getItem() && bcj1.getDamageValue() == bcj2.getDamageValue() && bcj1.getCount() <= bcj1.getMaxStackSize() && Objects.equals(bcj1.getTag(), bcj2.getTag());
    }
    
    private static int playerGive(final Collection<ServerPlayer> collection, final List<ItemStack> list, final Callback a) throws CommandSyntaxException {
        final List<ItemStack> list2 = (List<ItemStack>)Lists.newArrayListWithCapacity(list.size());
        for (final ItemStack bcj6 : list) {
            for (final ServerPlayer vl8 : collection) {
                if (vl8.inventory.add(bcj6.copy())) {
                    list2.add(bcj6);
                }
            }
        }
        a.accept(list2);
        return list2.size();
    }
    
    private static void setSlots(final Entity aio, final List<ItemStack> list2, final int integer3, final int integer4, final List<ItemStack> list5) {
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            final ItemStack bcj7 = (ItemStack)((integer5 < list2.size()) ? list2.get(integer5) : ItemStack.EMPTY);
            if (aio.setSlot(integer3 + integer5, bcj7.copy())) {
                list5.add(bcj7);
            }
        }
    }
    
    private static int entityReplace(final Collection<? extends Entity> collection, final int integer2, final int integer3, final List<ItemStack> list, final Callback a) throws CommandSyntaxException {
        final List<ItemStack> list2 = (List<ItemStack>)Lists.newArrayListWithCapacity(list.size());
        for (final Entity aio8 : collection) {
            if (aio8 instanceof ServerPlayer) {
                final ServerPlayer vl9 = (ServerPlayer)aio8;
                vl9.inventoryMenu.broadcastChanges();
                setSlots(aio8, list, integer2, integer3, list2);
                vl9.inventoryMenu.broadcastChanges();
            }
            else {
                setSlots(aio8, list, integer2, integer3, list2);
            }
        }
        a.accept(list2);
        return list2.size();
    }
    
    private static int dropInWorld(final CommandSourceStack cd, final Vec3 csi, final List<ItemStack> list, final Callback a) throws CommandSyntaxException {
        final ServerLevel vk5 = cd.getLevel();
        list.forEach(bcj -> {
            final ItemEntity atx4 = new ItemEntity(vk5, csi.x, csi.y, csi.z, bcj.copy());
            atx4.setDefaultPickUpDelay();
            vk5.addFreshEntity(atx4);
        });
        a.accept(list);
        return list.size();
    }
    
    private static void callback(final CommandSourceStack cd, final List<ItemStack> list) {
        if (list.size() == 1) {
            final ItemStack bcj3 = (ItemStack)list.get(0);
            cd.sendSuccess(new TranslatableComponent("commands.drop.success.single", new Object[] { bcj3.getCount(), bcj3.getDisplayName() }), false);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.drop.success.multiple", new Object[] { list.size() }), false);
        }
    }
    
    private static void callback(final CommandSourceStack cd, final List<ItemStack> list, final ResourceLocation qv) {
        if (list.size() == 1) {
            final ItemStack bcj4 = (ItemStack)list.get(0);
            cd.sendSuccess(new TranslatableComponent("commands.drop.success.single_with_table", new Object[] { bcj4.getCount(), bcj4.getDisplayName(), qv }), false);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.drop.success.multiple_with_table", new Object[] { list.size(), qv }), false);
        }
    }
    
    private static ItemStack getSourceHandItem(final CommandSourceStack cd, final EquipmentSlot ait) throws CommandSyntaxException {
        final Entity aio3 = cd.getEntityOrException();
        if (aio3 instanceof LivingEntity) {
            return ((LivingEntity)aio3).getItemBySlot(ait);
        }
        throw LootCommand.ERROR_NO_HELD_ITEMS.create(aio3.getDisplayName());
    }
    
    private static int dropBlockLoot(final CommandContext<CommandSourceStack> commandContext, final BlockPos ew, final ItemStack bcj, final DropConsumer b) throws CommandSyntaxException {
        final CommandSourceStack cd5 = (CommandSourceStack)commandContext.getSource();
        final ServerLevel vk6 = cd5.getLevel();
        final BlockState bvt7 = vk6.getBlockState(ew);
        final BlockEntity btw8 = vk6.getBlockEntity(ew);
        final LootContext.Builder a9 = new LootContext.Builder(vk6).<BlockPos>withParameter(LootContextParams.BLOCK_POS, ew).<BlockState>withParameter(LootContextParams.BLOCK_STATE, bvt7).<BlockEntity>withOptionalParameter(LootContextParams.BLOCK_ENTITY, btw8).<Entity>withOptionalParameter(LootContextParams.THIS_ENTITY, cd5.getEntity()).<ItemStack>withParameter(LootContextParams.TOOL, bcj);
        final List<ItemStack> list2 = bvt7.getDrops(a9);
        return b.accept(commandContext, list2, list -> callback(cd5, list, bvt7.getBlock().getLootTable()));
    }
    
    private static int dropKillLoot(final CommandContext<CommandSourceStack> commandContext, final Entity aio, final DropConsumer b) throws CommandSyntaxException {
        if (!(aio instanceof LivingEntity)) {
            throw LootCommand.ERROR_NO_LOOT_TABLE.create(aio.getDisplayName());
        }
        final ResourceLocation qv4 = ((LivingEntity)aio).getLootTable();
        final CommandSourceStack cd5 = (CommandSourceStack)commandContext.getSource();
        final LootContext.Builder a6 = new LootContext.Builder(cd5.getLevel());
        final Entity aio2 = cd5.getEntity();
        if (aio2 instanceof Player) {
            a6.<Player>withParameter(LootContextParams.LAST_DAMAGE_PLAYER, (Player)aio2);
        }
        a6.<DamageSource>withParameter(LootContextParams.DAMAGE_SOURCE, DamageSource.MAGIC);
        a6.<Entity>withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, aio2);
        a6.<Entity>withOptionalParameter(LootContextParams.KILLER_ENTITY, aio2);
        a6.<Entity>withParameter(LootContextParams.THIS_ENTITY, aio);
        a6.<BlockPos>withParameter(LootContextParams.BLOCK_POS, new BlockPos(cd5.getPosition()));
        final LootTable cpb8 = cd5.getServer().getLootTables().get(qv4);
        final List<ItemStack> list2 = cpb8.getRandomItems(a6.create(LootContextParamSets.ENTITY));
        return b.accept(commandContext, list2, list -> callback(cd5, list, qv4));
    }
    
    private static int dropChestLoot(final CommandContext<CommandSourceStack> commandContext, final ResourceLocation qv, final DropConsumer b) throws CommandSyntaxException {
        final CommandSourceStack cd4 = (CommandSourceStack)commandContext.getSource();
        final LootContext.Builder a5 = new LootContext.Builder(cd4.getLevel()).<Entity>withOptionalParameter(LootContextParams.THIS_ENTITY, cd4.getEntity()).<BlockPos>withParameter(LootContextParams.BLOCK_POS, new BlockPos(cd4.getPosition()));
        return drop(commandContext, qv, a5.create(LootContextParamSets.CHEST), b);
    }
    
    private static int dropFishingLoot(final CommandContext<CommandSourceStack> commandContext, final ResourceLocation qv, final BlockPos ew, final ItemStack bcj, final DropConsumer b) throws CommandSyntaxException {
        final CommandSourceStack cd6 = (CommandSourceStack)commandContext.getSource();
        final LootContext coy7 = new LootContext.Builder(cd6.getLevel()).<BlockPos>withParameter(LootContextParams.BLOCK_POS, ew).<ItemStack>withParameter(LootContextParams.TOOL, bcj).create(LootContextParamSets.FISHING);
        return drop(commandContext, qv, coy7, b);
    }
    
    private static int drop(final CommandContext<CommandSourceStack> commandContext, final ResourceLocation qv, final LootContext coy, final DropConsumer b) throws CommandSyntaxException {
        final CommandSourceStack cd5 = (CommandSourceStack)commandContext.getSource();
        final LootTable cpb6 = cd5.getServer().getLootTables().get(qv);
        final List<ItemStack> list2 = cpb6.getRandomItems(coy);
        return b.accept(commandContext, list2, list -> callback(cd5, list));
    }
    
    static {
        SUGGEST_LOOT_TABLE = ((commandContext, suggestionsBuilder) -> {
            final LootTables cpd3 = ((CommandSourceStack)commandContext.getSource()).getServer().getLootTables();
            return SharedSuggestionProvider.suggestResource((Iterable<ResourceLocation>)cpd3.getIds(), suggestionsBuilder);
        });
        ERROR_NO_HELD_ITEMS = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.drop.no_held_items", new Object[] { object }));
        ERROR_NO_LOOT_TABLE = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.drop.no_loot_table", new Object[] { object }));
    }
    
    @FunctionalInterface
    interface TailProvider {
        ArgumentBuilder<CommandSourceStack, ?> construct(final ArgumentBuilder<CommandSourceStack, ?> argumentBuilder, final DropConsumer b);
    }
    
    @FunctionalInterface
    interface DropConsumer {
        int accept(final CommandContext<CommandSourceStack> commandContext, final List<ItemStack> list, final Callback a) throws CommandSyntaxException;
    }
    
    @FunctionalInterface
    interface Callback {
        void accept(final List<ItemStack> list) throws CommandSyntaxException;
    }
}
