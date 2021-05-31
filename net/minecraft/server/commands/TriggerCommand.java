package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.scores.Score;
import java.util.Iterator;
import net.minecraft.world.scores.Scoreboard;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.Objective;
import com.google.common.collect.Lists;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.ObjectiveArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class TriggerCommand {
    private static final SimpleCommandExceptionType ERROR_NOT_PRIMED;
    private static final SimpleCommandExceptionType ERROR_INVALID_OBJECTIVE;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("trigger").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgument.objective()).suggests((commandContext, suggestionsBuilder) -> suggestObjectives((CommandSourceStack)commandContext.getSource(), suggestionsBuilder)).executes(commandContext -> simpleTrigger((CommandSourceStack)commandContext.getSource(), getScore(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"))))).then(Commands.literal("add").then(Commands.argument("value", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer()).executes(commandContext -> addValue((CommandSourceStack)commandContext.getSource(), getScore(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective")), IntegerArgumentType.getInteger(commandContext, "value")))))).then(Commands.literal("set").then(Commands.argument("value", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer()).executes(commandContext -> setValue((CommandSourceStack)commandContext.getSource(), getScore(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective")), IntegerArgumentType.getInteger(commandContext, "value")))))));
    }
    
    public static CompletableFuture<Suggestions> suggestObjectives(final CommandSourceStack cd, final SuggestionsBuilder suggestionsBuilder) {
        final Entity aio3 = cd.getEntity();
        final List<String> list4 = (List<String>)Lists.newArrayList();
        if (aio3 != null) {
            final Scoreboard cti5 = cd.getServer().getScoreboard();
            final String string6 = aio3.getScoreboardName();
            for (final Objective ctf8 : cti5.getObjectives()) {
                if (ctf8.getCriteria() == ObjectiveCriteria.TRIGGER && cti5.hasPlayerScore(string6, ctf8)) {
                    final Score cth9 = cti5.getOrCreatePlayerScore(string6, ctf8);
                    if (cth9.isLocked()) {
                        continue;
                    }
                    list4.add(ctf8.getName());
                }
            }
        }
        return SharedSuggestionProvider.suggest((Iterable<String>)list4, suggestionsBuilder);
    }
    
    private static int addValue(final CommandSourceStack cd, final Score cth, final int integer) {
        cth.add(integer);
        cd.sendSuccess(new TranslatableComponent("commands.trigger.add.success", new Object[] { cth.getObjective().getFormattedDisplayName(), integer }), true);
        return cth.getScore();
    }
    
    private static int setValue(final CommandSourceStack cd, final Score cth, final int integer) {
        cth.setScore(integer);
        cd.sendSuccess(new TranslatableComponent("commands.trigger.set.success", new Object[] { cth.getObjective().getFormattedDisplayName(), integer }), true);
        return integer;
    }
    
    private static int simpleTrigger(final CommandSourceStack cd, final Score cth) {
        cth.add(1);
        cd.sendSuccess(new TranslatableComponent("commands.trigger.simple.success", new Object[] { cth.getObjective().getFormattedDisplayName() }), true);
        return cth.getScore();
    }
    
    private static Score getScore(final ServerPlayer vl, final Objective ctf) throws CommandSyntaxException {
        if (ctf.getCriteria() != ObjectiveCriteria.TRIGGER) {
            throw TriggerCommand.ERROR_INVALID_OBJECTIVE.create();
        }
        final Scoreboard cti3 = vl.getScoreboard();
        final String string4 = vl.getScoreboardName();
        if (!cti3.hasPlayerScore(string4, ctf)) {
            throw TriggerCommand.ERROR_NOT_PRIMED.create();
        }
        final Score cth5 = cti3.getOrCreatePlayerScore(string4, ctf);
        if (cth5.isLocked()) {
            throw TriggerCommand.ERROR_NOT_PRIMED.create();
        }
        cth5.setLocked(true);
        return cth5;
    }
    
    static {
        ERROR_NOT_PRIMED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.trigger.failed.unprimed", new Object[0]));
        ERROR_INVALID_OBJECTIVE = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.trigger.failed.invalid", new Object[0]));
    }
}
