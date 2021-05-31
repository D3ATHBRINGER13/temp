package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import net.minecraft.network.chat.TextComponent;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.Function;
import java.util.Map;
import net.minecraft.network.chat.ComponentUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.scores.Score;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.Iterator;
import net.minecraft.world.scores.Scoreboard;
import java.util.List;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.scores.Objective;
import com.google.common.collect.Lists;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.commands.arguments.OperationArgument;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ScoreboardCommand {
    private static final SimpleCommandExceptionType ERROR_OBJECTIVE_ALREADY_EXISTS;
    private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_EMPTY;
    private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_SET;
    private static final SimpleCommandExceptionType ERROR_TRIGGER_ALREADY_ENABLED;
    private static final SimpleCommandExceptionType ERROR_NOT_TRIGGER;
    private static final Dynamic2CommandExceptionType ERROR_NO_VALUE;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("scoreboard").requires(cd -> cd.hasPermission(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("objectives").then(Commands.literal("list").executes(commandContext -> listObjectives((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("add").then(Commands.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.word()).then(((RequiredArgumentBuilder)Commands.argument("criteria", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveCriteriaArgument.criteria()).executes(commandContext -> addObjective((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString(commandContext, "objective"), ObjectiveCriteriaArgument.getCriteria((CommandContext<CommandSourceStack>)commandContext, "criteria"), new TextComponent(StringArgumentType.getString(commandContext, "objective"))))).then(Commands.argument("displayName", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgument.textComponent()).executes(commandContext -> addObjective((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString(commandContext, "objective"), ObjectiveCriteriaArgument.getCriteria((CommandContext<CommandSourceStack>)commandContext, "criteria"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "displayName")))))))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)Commands.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).then(Commands.literal("displayname").then(Commands.argument("displayName", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgument.textComponent()).executes(commandContext -> setDisplayName((CommandSourceStack)commandContext.getSource(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "displayName")))))).then((ArgumentBuilder)createRenderTypeModify())))).then(Commands.literal("remove").then(Commands.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).executes(commandContext -> removeObjective((CommandSourceStack)commandContext.getSource(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective")))))).then(Commands.literal("setdisplay").then(((RequiredArgumentBuilder)Commands.argument("slot", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreboardSlotArgument.displaySlot()).executes(commandContext -> clearDisplaySlot((CommandSourceStack)commandContext.getSource(), ScoreboardSlotArgument.getDisplaySlot((CommandContext<CommandSourceStack>)commandContext, "slot")))).then(Commands.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).executes(commandContext -> setDisplaySlot((CommandSourceStack)commandContext.getSource(), ScoreboardSlotArgument.getDisplaySlot((CommandContext<CommandSourceStack>)commandContext, "slot"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective")))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("players").then(((LiteralArgumentBuilder)Commands.literal("list").executes(commandContext -> listTrackedPlayers((CommandSourceStack)commandContext.getSource()))).then(Commands.argument("target", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgument.scoreHolder()).suggests((SuggestionProvider)ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes(commandContext -> listTrackedPlayerScores((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getName((CommandContext<CommandSourceStack>)commandContext, "target")))))).then(Commands.literal("set").then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgument.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).then(Commands.argument("score", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer()).executes(commandContext -> setScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score")))))))).then(Commands.literal("get").then(Commands.argument("target", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgument.scoreHolder()).suggests((SuggestionProvider)ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).executes(commandContext -> getScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getName((CommandContext<CommandSourceStack>)commandContext, "target"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"))))))).then(Commands.literal("add").then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgument.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).then(Commands.argument("score", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> addScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score")))))))).then(Commands.literal("remove").then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgument.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).then(Commands.argument("score", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> removeScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score")))))))).then(Commands.literal("reset").then(((RequiredArgumentBuilder)Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgument.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes(commandContext -> resetScores((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets")))).then(Commands.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).executes(commandContext -> resetScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"))))))).then(Commands.literal("enable").then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgument.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).suggests((commandContext, suggestionsBuilder) -> suggestTriggers((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), suggestionsBuilder)).executes(commandContext -> enableTrigger((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"))))))).then(Commands.literal("operation").then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgument.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("targetObjective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).then(Commands.argument("operation", (com.mojang.brigadier.arguments.ArgumentType<Object>)OperationArgument.operation()).then(Commands.argument("source", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgument.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("sourceObjective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).executes(commandContext -> performOperation((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)commandContext, "targetObjective"), OperationArgument.getOperation((CommandContext<CommandSourceStack>)commandContext, "operation"), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "source"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "sourceObjective")))))))))));
    }
    
    private static LiteralArgumentBuilder<CommandSourceStack> createRenderTypeModify() {
        final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder1 = Commands.literal("rendertype");
        for (final ObjectiveCriteria.RenderType a5 : ObjectiveCriteria.RenderType.values()) {
            literalArgumentBuilder1.then(Commands.literal(a5.getId()).executes(commandContext -> setRenderType((CommandSourceStack)commandContext.getSource(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"), a5)));
        }
        return literalArgumentBuilder1;
    }
    
    private static CompletableFuture<Suggestions> suggestTriggers(final CommandSourceStack cd, final Collection<String> collection, final SuggestionsBuilder suggestionsBuilder) {
        final List<String> list4 = (List<String>)Lists.newArrayList();
        final Scoreboard cti5 = cd.getServer().getScoreboard();
        for (final Objective ctf7 : cti5.getObjectives()) {
            if (ctf7.getCriteria() == ObjectiveCriteria.TRIGGER) {
                boolean boolean8 = false;
                for (final String string10 : collection) {
                    if (!cti5.hasPlayerScore(string10, ctf7) || cti5.getOrCreatePlayerScore(string10, ctf7).isLocked()) {
                        boolean8 = true;
                        break;
                    }
                }
                if (!boolean8) {
                    continue;
                }
                list4.add(ctf7.getName());
            }
        }
        return SharedSuggestionProvider.suggest((Iterable<String>)list4, suggestionsBuilder);
    }
    
    private static int getScore(final CommandSourceStack cd, final String string, final Objective ctf) throws CommandSyntaxException {
        final Scoreboard cti4 = cd.getServer().getScoreboard();
        if (!cti4.hasPlayerScore(string, ctf)) {
            throw ScoreboardCommand.ERROR_NO_VALUE.create(ctf.getName(), string);
        }
        final Score cth5 = cti4.getOrCreatePlayerScore(string, ctf);
        cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.get.success", new Object[] { string, cth5.getScore(), ctf.getFormattedDisplayName() }), false);
        return cth5.getScore();
    }
    
    private static int performOperation(final CommandSourceStack cd, final Collection<String> collection2, final Objective ctf3, final OperationArgument.Operation a, final Collection<String> collection5, final Objective ctf6) throws CommandSyntaxException {
        final Scoreboard cti7 = cd.getServer().getScoreboard();
        int integer8 = 0;
        for (final String string10 : collection2) {
            final Score cth11 = cti7.getOrCreatePlayerScore(string10, ctf3);
            for (final String string11 : collection5) {
                final Score cth12 = cti7.getOrCreatePlayerScore(string11, ctf6);
                a.apply(cth11, cth12);
            }
            integer8 += cth11.getScore();
        }
        if (collection2.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.operation.success.single", new Object[] { ctf3.getFormattedDisplayName(), collection2.iterator().next(), integer8 }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.operation.success.multiple", new Object[] { ctf3.getFormattedDisplayName(), collection2.size() }), true);
        }
        return integer8;
    }
    
    private static int enableTrigger(final CommandSourceStack cd, final Collection<String> collection, final Objective ctf) throws CommandSyntaxException {
        if (ctf.getCriteria() != ObjectiveCriteria.TRIGGER) {
            throw ScoreboardCommand.ERROR_NOT_TRIGGER.create();
        }
        final Scoreboard cti4 = cd.getServer().getScoreboard();
        int integer5 = 0;
        for (final String string7 : collection) {
            final Score cth8 = cti4.getOrCreatePlayerScore(string7, ctf);
            if (cth8.isLocked()) {
                cth8.setLocked(false);
                ++integer5;
            }
        }
        if (integer5 == 0) {
            throw ScoreboardCommand.ERROR_TRIGGER_ALREADY_ENABLED.create();
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.enable.success.single", new Object[] { ctf.getFormattedDisplayName(), collection.iterator().next() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.enable.success.multiple", new Object[] { ctf.getFormattedDisplayName(), collection.size() }), true);
        }
        return integer5;
    }
    
    private static int resetScores(final CommandSourceStack cd, final Collection<String> collection) {
        final Scoreboard cti3 = cd.getServer().getScoreboard();
        for (final String string5 : collection) {
            cti3.resetPlayerScore(string5, null);
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.all.single", new Object[] { collection.iterator().next() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.all.multiple", new Object[] { collection.size() }), true);
        }
        return collection.size();
    }
    
    private static int resetScore(final CommandSourceStack cd, final Collection<String> collection, final Objective ctf) {
        final Scoreboard cti4 = cd.getServer().getScoreboard();
        for (final String string6 : collection) {
            cti4.resetPlayerScore(string6, ctf);
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.specific.single", new Object[] { ctf.getFormattedDisplayName(), collection.iterator().next() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.specific.multiple", new Object[] { ctf.getFormattedDisplayName(), collection.size() }), true);
        }
        return collection.size();
    }
    
    private static int setScore(final CommandSourceStack cd, final Collection<String> collection, final Objective ctf, final int integer) {
        final Scoreboard cti5 = cd.getServer().getScoreboard();
        for (final String string7 : collection) {
            final Score cth8 = cti5.getOrCreatePlayerScore(string7, ctf);
            cth8.setScore(integer);
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.set.success.single", new Object[] { ctf.getFormattedDisplayName(), collection.iterator().next(), integer }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.set.success.multiple", new Object[] { ctf.getFormattedDisplayName(), collection.size(), integer }), true);
        }
        return integer * collection.size();
    }
    
    private static int addScore(final CommandSourceStack cd, final Collection<String> collection, final Objective ctf, final int integer) {
        final Scoreboard cti5 = cd.getServer().getScoreboard();
        int integer2 = 0;
        for (final String string8 : collection) {
            final Score cth9 = cti5.getOrCreatePlayerScore(string8, ctf);
            cth9.setScore(cth9.getScore() + integer);
            integer2 += cth9.getScore();
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.add.success.single", new Object[] { integer, ctf.getFormattedDisplayName(), collection.iterator().next(), integer2 }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.add.success.multiple", new Object[] { integer, ctf.getFormattedDisplayName(), collection.size() }), true);
        }
        return integer2;
    }
    
    private static int removeScore(final CommandSourceStack cd, final Collection<String> collection, final Objective ctf, final int integer) {
        final Scoreboard cti5 = cd.getServer().getScoreboard();
        int integer2 = 0;
        for (final String string8 : collection) {
            final Score cth9 = cti5.getOrCreatePlayerScore(string8, ctf);
            cth9.setScore(cth9.getScore() - integer);
            integer2 += cth9.getScore();
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.remove.success.single", new Object[] { integer, ctf.getFormattedDisplayName(), collection.iterator().next(), integer2 }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.remove.success.multiple", new Object[] { integer, ctf.getFormattedDisplayName(), collection.size() }), true);
        }
        return integer2;
    }
    
    private static int listTrackedPlayers(final CommandSourceStack cd) {
        final Collection<String> collection2 = cd.getServer().getScoreboard().getTrackedPlayers();
        if (collection2.isEmpty()) {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.empty", new Object[0]), false);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.success", new Object[] { collection2.size(), ComponentUtils.formatList(collection2) }), false);
        }
        return collection2.size();
    }
    
    private static int listTrackedPlayerScores(final CommandSourceStack cd, final String string) {
        final Map<Objective, Score> map3 = cd.getServer().getScoreboard().getPlayerScores(string);
        if (map3.isEmpty()) {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.empty", new Object[] { string }), false);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.success", new Object[] { string, map3.size() }), false);
            for (final Map.Entry<Objective, Score> entry5 : map3.entrySet()) {
                cd.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.entry", new Object[] { ((Objective)entry5.getKey()).getFormattedDisplayName(), ((Score)entry5.getValue()).getScore() }), false);
            }
        }
        return map3.size();
    }
    
    private static int clearDisplaySlot(final CommandSourceStack cd, final int integer) throws CommandSyntaxException {
        final Scoreboard cti3 = cd.getServer().getScoreboard();
        if (cti3.getDisplayObjective(integer) == null) {
            throw ScoreboardCommand.ERROR_DISPLAY_SLOT_ALREADY_EMPTY.create();
        }
        cti3.setDisplayObjective(integer, null);
        cd.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.display.cleared", new Object[] { Scoreboard.getDisplaySlotNames()[integer] }), true);
        return 0;
    }
    
    private static int setDisplaySlot(final CommandSourceStack cd, final int integer, final Objective ctf) throws CommandSyntaxException {
        final Scoreboard cti4 = cd.getServer().getScoreboard();
        if (cti4.getDisplayObjective(integer) == ctf) {
            throw ScoreboardCommand.ERROR_DISPLAY_SLOT_ALREADY_SET.create();
        }
        cti4.setDisplayObjective(integer, ctf);
        cd.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.display.set", new Object[] { Scoreboard.getDisplaySlotNames()[integer], ctf.getDisplayName() }), true);
        return 0;
    }
    
    private static int setDisplayName(final CommandSourceStack cd, final Objective ctf, final Component jo) {
        if (!ctf.getDisplayName().equals(jo)) {
            ctf.setDisplayName(jo);
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.modify.displayname", new Object[] { ctf.getName(), ctf.getFormattedDisplayName() }), true);
        }
        return 0;
    }
    
    private static int setRenderType(final CommandSourceStack cd, final Objective ctf, final ObjectiveCriteria.RenderType a) {
        if (ctf.getRenderType() != a) {
            ctf.setRenderType(a);
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.modify.rendertype", new Object[] { ctf.getFormattedDisplayName() }), true);
        }
        return 0;
    }
    
    private static int removeObjective(final CommandSourceStack cd, final Objective ctf) {
        final Scoreboard cti3 = cd.getServer().getScoreboard();
        cti3.removeObjective(ctf);
        cd.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.remove.success", new Object[] { ctf.getFormattedDisplayName() }), true);
        return cti3.getObjectives().size();
    }
    
    private static int addObjective(final CommandSourceStack cd, final String string, final ObjectiveCriteria ctl, final Component jo) throws CommandSyntaxException {
        final Scoreboard cti5 = cd.getServer().getScoreboard();
        if (cti5.getObjective(string) != null) {
            throw ScoreboardCommand.ERROR_OBJECTIVE_ALREADY_EXISTS.create();
        }
        if (string.length() > 16) {
            throw ObjectiveArgument.ERROR_OBJECTIVE_NAME_TOO_LONG.create(16);
        }
        cti5.addObjective(string, ctl, jo, ctl.getDefaultRenderType());
        final Objective ctf6 = cti5.getObjective(string);
        cd.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.add.success", new Object[] { ctf6.getFormattedDisplayName() }), true);
        return cti5.getObjectives().size();
    }
    
    private static int listObjectives(final CommandSourceStack cd) {
        final Collection<Objective> collection2 = cd.getServer().getScoreboard().getObjectives();
        if (collection2.isEmpty()) {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.list.empty", new Object[0]), false);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.list.success", new Object[] { collection2.size(), ComponentUtils.<Objective>formatList(collection2, (java.util.function.Function<Objective, Component>)Objective::getFormattedDisplayName) }), false);
        }
        return collection2.size();
    }
    
    static {
        ERROR_OBJECTIVE_ALREADY_EXISTS = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.scoreboard.objectives.add.duplicate", new Object[0]));
        ERROR_DISPLAY_SLOT_ALREADY_EMPTY = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.scoreboard.objectives.display.alreadyEmpty", new Object[0]));
        ERROR_DISPLAY_SLOT_ALREADY_SET = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.scoreboard.objectives.display.alreadySet", new Object[0]));
        ERROR_TRIGGER_ALREADY_ENABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.scoreboard.players.enable.failed", new Object[0]));
        ERROR_NOT_TRIGGER = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.scoreboard.players.enable.invalid", new Object[0]));
        ERROR_NO_VALUE = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableComponent("commands.scoreboard.players.get.null", new Object[] { object1, object2 }));
    }
}
