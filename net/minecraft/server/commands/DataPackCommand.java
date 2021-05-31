package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import java.util.stream.Stream;
import net.minecraft.commands.SharedSuggestionProvider;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.Function;
import net.minecraft.network.chat.ComponentUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.level.storage.LevelData;
import java.util.List;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.dimension.DimensionType;
import java.util.Collection;
import com.google.common.collect.Lists;
import net.minecraft.server.packs.repository.UnopenedPack;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public class DataPackCommand {
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_PACK;
    private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_ENABLED;
    private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_DISABLED;
    private static final SuggestionProvider<CommandSourceStack> SELECTED_PACKS;
    private static final SuggestionProvider<CommandSourceStack> AVAILABLE_PACKS;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("datapack").requires(cd -> cd.hasPermission(2))).then(Commands.literal("enable").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("name", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.string()).suggests((SuggestionProvider)DataPackCommand.AVAILABLE_PACKS).executes(commandContext -> enablePack((CommandSourceStack)commandContext.getSource(), getPack((CommandContext<CommandSourceStack>)commandContext, "name", true), (list, xa) -> xa.getDefaultPosition().<UnopenedPack, UnopenedPack>insert(list, xa, (java.util.function.Function<UnopenedPack, UnopenedPack>)(xa -> xa), false)))).then(Commands.literal("after").then(Commands.argument("existing", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.string()).suggests((SuggestionProvider)DataPackCommand.SELECTED_PACKS).executes(commandContext -> enablePack((CommandSourceStack)commandContext.getSource(), getPack((CommandContext<CommandSourceStack>)commandContext, "name", true), (list, xa) -> list.add(list.indexOf(getPack((CommandContext<CommandSourceStack>)commandContext, "existing", false)) + 1, xa)))))).then(Commands.literal("before").then(Commands.argument("existing", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.string()).suggests((SuggestionProvider)DataPackCommand.SELECTED_PACKS).executes(commandContext -> enablePack((CommandSourceStack)commandContext.getSource(), getPack((CommandContext<CommandSourceStack>)commandContext, "name", true), (list, xa) -> list.add(list.indexOf(getPack((CommandContext<CommandSourceStack>)commandContext, "existing", false)), xa)))))).then(Commands.literal("last").executes(commandContext -> enablePack((CommandSourceStack)commandContext.getSource(), getPack((CommandContext<CommandSourceStack>)commandContext, "name", true), List::add)))).then(Commands.literal("first").executes(commandContext -> enablePack((CommandSourceStack)commandContext.getSource(), getPack((CommandContext<CommandSourceStack>)commandContext, "name", true), (list, xa) -> list.add(0, xa))))))).then(Commands.literal("disable").then(Commands.argument("name", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.string()).suggests((SuggestionProvider)DataPackCommand.SELECTED_PACKS).executes(commandContext -> disablePack((CommandSourceStack)commandContext.getSource(), getPack((CommandContext<CommandSourceStack>)commandContext, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes(commandContext -> listPacks((CommandSourceStack)commandContext.getSource()))).then(Commands.literal("available").executes(commandContext -> listAvailablePacks((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("enabled").executes(commandContext -> listEnabledPacks((CommandSourceStack)commandContext.getSource())))));
    }
    
    private static int enablePack(final CommandSourceStack cd, final UnopenedPack xa, final Inserter a) throws CommandSyntaxException {
        final PackRepository<UnopenedPack> wx4 = cd.getServer().getPackRepository();
        final List<UnopenedPack> list5 = (List<UnopenedPack>)Lists.newArrayList((Iterable)wx4.getSelected());
        a.apply(list5, xa);
        wx4.setSelected((java.util.Collection<UnopenedPack>)list5);
        final LevelData com6 = cd.getServer().getLevel(DimensionType.OVERWORLD).getLevelData();
        com6.getEnabledDataPacks().clear();
        wx4.getSelected().forEach(xa -> com6.getEnabledDataPacks().add(xa.getId()));
        com6.getDisabledDataPacks().remove(xa.getId());
        cd.sendSuccess(new TranslatableComponent("commands.datapack.enable.success", new Object[] { xa.getChatLink(true) }), true);
        cd.getServer().reloadResources();
        return wx4.getSelected().size();
    }
    
    private static int disablePack(final CommandSourceStack cd, final UnopenedPack xa) {
        final PackRepository<UnopenedPack> wx3 = cd.getServer().getPackRepository();
        final List<UnopenedPack> list4 = (List<UnopenedPack>)Lists.newArrayList((Iterable)wx3.getSelected());
        list4.remove(xa);
        wx3.setSelected((java.util.Collection<UnopenedPack>)list4);
        final LevelData com5 = cd.getServer().getLevel(DimensionType.OVERWORLD).getLevelData();
        com5.getEnabledDataPacks().clear();
        wx3.getSelected().forEach(xa -> com5.getEnabledDataPacks().add(xa.getId()));
        com5.getDisabledDataPacks().add(xa.getId());
        cd.sendSuccess(new TranslatableComponent("commands.datapack.disable.success", new Object[] { xa.getChatLink(true) }), true);
        cd.getServer().reloadResources();
        return wx3.getSelected().size();
    }
    
    private static int listPacks(final CommandSourceStack cd) {
        return listEnabledPacks(cd) + listAvailablePacks(cd);
    }
    
    private static int listAvailablePacks(final CommandSourceStack cd) {
        final PackRepository<UnopenedPack> wx2 = cd.getServer().getPackRepository();
        if (wx2.getUnselected().isEmpty()) {
            cd.sendSuccess(new TranslatableComponent("commands.datapack.list.available.none", new Object[0]), false);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.datapack.list.available.success", new Object[] { wx2.getUnselected().size(), ComponentUtils.<UnopenedPack>formatList(wx2.getUnselected(), (java.util.function.Function<UnopenedPack, Component>)(xa -> xa.getChatLink(false))) }), false);
        }
        return wx2.getUnselected().size();
    }
    
    private static int listEnabledPacks(final CommandSourceStack cd) {
        final PackRepository<UnopenedPack> wx2 = cd.getServer().getPackRepository();
        if (wx2.getSelected().isEmpty()) {
            cd.sendSuccess(new TranslatableComponent("commands.datapack.list.enabled.none", new Object[0]), false);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.datapack.list.enabled.success", new Object[] { wx2.getSelected().size(), ComponentUtils.<UnopenedPack>formatList(wx2.getSelected(), (java.util.function.Function<UnopenedPack, Component>)(xa -> xa.getChatLink(true))) }), false);
        }
        return wx2.getSelected().size();
    }
    
    private static UnopenedPack getPack(final CommandContext<CommandSourceStack> commandContext, final String string, final boolean boolean3) throws CommandSyntaxException {
        final String string2 = StringArgumentType.getString((CommandContext)commandContext, string);
        final PackRepository<UnopenedPack> wx5 = ((CommandSourceStack)commandContext.getSource()).getServer().getPackRepository();
        final UnopenedPack xa6 = wx5.getPack(string2);
        if (xa6 == null) {
            throw DataPackCommand.ERROR_UNKNOWN_PACK.create(string2);
        }
        final boolean boolean4 = wx5.getSelected().contains(xa6);
        if (boolean3 && boolean4) {
            throw DataPackCommand.ERROR_PACK_ALREADY_ENABLED.create(string2);
        }
        if (!boolean3 && !boolean4) {
            throw DataPackCommand.ERROR_PACK_ALREADY_DISABLED.create(string2);
        }
        return xa6;
    }
    
    static {
        ERROR_UNKNOWN_PACK = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.datapack.unknown", new Object[] { object }));
        ERROR_PACK_ALREADY_ENABLED = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.datapack.enable.failed", new Object[] { object }));
        ERROR_PACK_ALREADY_DISABLED = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.datapack.disable.failed", new Object[] { object }));
        SELECTED_PACKS = ((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest((Stream<String>)((CommandSourceStack)commandContext.getSource()).getServer().getPackRepository().getSelected().stream().map(UnopenedPack::getId).map(StringArgumentType::escapeIfRequired), suggestionsBuilder));
        AVAILABLE_PACKS = ((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest((Stream<String>)((CommandSourceStack)commandContext.getSource()).getServer().getPackRepository().getUnselected().stream().map(UnopenedPack::getId).map(StringArgumentType::escapeIfRequired), suggestionsBuilder));
    }
    
    interface Inserter {
        void apply(final List<UnopenedPack> list, final UnopenedPack xa) throws CommandSyntaxException;
    }
}
