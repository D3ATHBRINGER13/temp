package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import java.util.Collections;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.Function;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.PlayerTeam;
import java.util.Iterator;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.Collection;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.arguments.ColorArgument;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class TeamCommand {
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EXISTS;
    private static final DynamicCommandExceptionType ERROR_TEAM_NAME_TOO_LONG;
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EMPTY;
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_NAME;
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_COLOR;
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED;
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED;
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED;
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED;
    private static final SimpleCommandExceptionType ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED;
    private static final SimpleCommandExceptionType ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED;
    private static final SimpleCommandExceptionType ERROR_TEAM_COLLISION_UNCHANGED;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("team").requires(cd -> cd.hasPermission(2))).then(((LiteralArgumentBuilder)Commands.literal("list").executes(commandContext -> listTeams((CommandSourceStack)commandContext.getSource()))).then(Commands.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)TeamArgument.team()).executes(commandContext -> listMembers((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team")))))).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.word()).executes(commandContext -> createTeam((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString(commandContext, "team")))).then(Commands.argument("displayName", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgument.textComponent()).executes(commandContext -> createTeam((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString(commandContext, "team"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "displayName"))))))).then(Commands.literal("remove").then(Commands.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)TeamArgument.team()).executes(commandContext -> deleteTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team")))))).then(Commands.literal("empty").then(Commands.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)TeamArgument.team()).executes(commandContext -> emptyTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team")))))).then(Commands.literal("join").then(((RequiredArgumentBuilder)Commands.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)TeamArgument.team()).executes(commandContext -> joinTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), (Collection<String>)Collections.singleton(((CommandSourceStack)commandContext.getSource()).getEntityOrException().getScoreboardName())))).then(Commands.argument("members", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgument.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes(commandContext -> joinTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "members"))))))).then(Commands.literal("leave").then(Commands.argument("members", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgument.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes(commandContext -> leaveTeam((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "members")))))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("team", (com.mojang.brigadier.arguments.ArgumentType<Object>)TeamArgument.team()).then(Commands.literal("displayName").then(Commands.argument("displayName", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgument.textComponent()).executes(commandContext -> setDisplayName((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "displayName")))))).then(Commands.literal("color").then(Commands.argument("value", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColorArgument.color()).executes(commandContext -> setColor((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), ColorArgument.getColor((CommandContext<CommandSourceStack>)commandContext, "value")))))).then(Commands.literal("friendlyFire").then(Commands.argument("allowed", (com.mojang.brigadier.arguments.ArgumentType<Object>)BoolArgumentType.bool()).executes(commandContext -> setFriendlyFire((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed")))))).then(Commands.literal("seeFriendlyInvisibles").then(Commands.argument("allowed", (com.mojang.brigadier.arguments.ArgumentType<Object>)BoolArgumentType.bool()).executes(commandContext -> setFriendlySight((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("nametagVisibility").then(Commands.literal("never").executes(commandContext -> setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.NEVER)))).then(Commands.literal("hideForOtherTeams").executes(commandContext -> setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.HIDE_FOR_OTHER_TEAMS)))).then(Commands.literal("hideForOwnTeam").executes(commandContext -> setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.HIDE_FOR_OWN_TEAM)))).then(Commands.literal("always").executes(commandContext -> setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deathMessageVisibility").then(Commands.literal("never").executes(commandContext -> setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.NEVER)))).then(Commands.literal("hideForOtherTeams").executes(commandContext -> setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.HIDE_FOR_OTHER_TEAMS)))).then(Commands.literal("hideForOwnTeam").executes(commandContext -> setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.HIDE_FOR_OWN_TEAM)))).then(Commands.literal("always").executes(commandContext -> setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("collisionRule").then(Commands.literal("never").executes(commandContext -> setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.CollisionRule.NEVER)))).then(Commands.literal("pushOwnTeam").executes(commandContext -> setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.CollisionRule.PUSH_OWN_TEAM)))).then(Commands.literal("pushOtherTeams").executes(commandContext -> setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.CollisionRule.PUSH_OTHER_TEAMS)))).then(Commands.literal("always").executes(commandContext -> setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.CollisionRule.ALWAYS))))).then(Commands.literal("prefix").then(Commands.argument("prefix", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgument.textComponent()).executes(commandContext -> setPrefix((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "prefix")))))).then(Commands.literal("suffix").then(Commands.argument("suffix", (com.mojang.brigadier.arguments.ArgumentType<Object>)ComponentArgument.textComponent()).executes(commandContext -> setSuffix((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "suffix"))))))));
    }
    
    private static int leaveTeam(final CommandSourceStack cd, final Collection<String> collection) {
        final Scoreboard cti3 = cd.getServer().getScoreboard();
        for (final String string5 : collection) {
            cti3.removePlayerFromTeam(string5);
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.team.leave.success.single", new Object[] { collection.iterator().next() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.team.leave.success.multiple", new Object[] { collection.size() }), true);
        }
        return collection.size();
    }
    
    private static int joinTeam(final CommandSourceStack cd, final PlayerTeam ctg, final Collection<String> collection) {
        final Scoreboard cti4 = cd.getServer().getScoreboard();
        for (final String string6 : collection) {
            cti4.addPlayerToTeam(string6, ctg);
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.team.join.success.single", new Object[] { collection.iterator().next(), ctg.getFormattedDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.team.join.success.multiple", new Object[] { collection.size(), ctg.getFormattedDisplayName() }), true);
        }
        return collection.size();
    }
    
    private static int setNametagVisibility(final CommandSourceStack cd, final PlayerTeam ctg, final Team.Visibility b) throws CommandSyntaxException {
        if (ctg.getNameTagVisibility() == b) {
            throw TeamCommand.ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED.create();
        }
        ctg.setNameTagVisibility(b);
        cd.sendSuccess(new TranslatableComponent("commands.team.option.nametagVisibility.success", new Object[] { ctg.getFormattedDisplayName(), b.getDisplayName() }), true);
        return 0;
    }
    
    private static int setDeathMessageVisibility(final CommandSourceStack cd, final PlayerTeam ctg, final Team.Visibility b) throws CommandSyntaxException {
        if (ctg.getDeathMessageVisibility() == b) {
            throw TeamCommand.ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED.create();
        }
        ctg.setDeathMessageVisibility(b);
        cd.sendSuccess(new TranslatableComponent("commands.team.option.deathMessageVisibility.success", new Object[] { ctg.getFormattedDisplayName(), b.getDisplayName() }), true);
        return 0;
    }
    
    private static int setCollision(final CommandSourceStack cd, final PlayerTeam ctg, final Team.CollisionRule a) throws CommandSyntaxException {
        if (ctg.getCollisionRule() == a) {
            throw TeamCommand.ERROR_TEAM_COLLISION_UNCHANGED.create();
        }
        ctg.setCollisionRule(a);
        cd.sendSuccess(new TranslatableComponent("commands.team.option.collisionRule.success", new Object[] { ctg.getFormattedDisplayName(), a.getDisplayName() }), true);
        return 0;
    }
    
    private static int setFriendlySight(final CommandSourceStack cd, final PlayerTeam ctg, final boolean boolean3) throws CommandSyntaxException {
        if (ctg.canSeeFriendlyInvisibles() != boolean3) {
            ctg.setSeeFriendlyInvisibles(boolean3);
            cd.sendSuccess(new TranslatableComponent(new StringBuilder().append("commands.team.option.seeFriendlyInvisibles.").append(boolean3 ? "enabled" : "disabled").toString(), new Object[] { ctg.getFormattedDisplayName() }), true);
            return 0;
        }
        if (boolean3) {
            throw TeamCommand.ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED.create();
        }
        throw TeamCommand.ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED.create();
    }
    
    private static int setFriendlyFire(final CommandSourceStack cd, final PlayerTeam ctg, final boolean boolean3) throws CommandSyntaxException {
        if (ctg.isAllowFriendlyFire() != boolean3) {
            ctg.setAllowFriendlyFire(boolean3);
            cd.sendSuccess(new TranslatableComponent(new StringBuilder().append("commands.team.option.friendlyfire.").append(boolean3 ? "enabled" : "disabled").toString(), new Object[] { ctg.getFormattedDisplayName() }), true);
            return 0;
        }
        if (boolean3) {
            throw TeamCommand.ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED.create();
        }
        throw TeamCommand.ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED.create();
    }
    
    private static int setDisplayName(final CommandSourceStack cd, final PlayerTeam ctg, final Component jo) throws CommandSyntaxException {
        if (ctg.getDisplayName().equals(jo)) {
            throw TeamCommand.ERROR_TEAM_ALREADY_NAME.create();
        }
        ctg.setDisplayName(jo);
        cd.sendSuccess(new TranslatableComponent("commands.team.option.name.success", new Object[] { ctg.getFormattedDisplayName() }), true);
        return 0;
    }
    
    private static int setColor(final CommandSourceStack cd, final PlayerTeam ctg, final ChatFormatting c) throws CommandSyntaxException {
        if (ctg.getColor() == c) {
            throw TeamCommand.ERROR_TEAM_ALREADY_COLOR.create();
        }
        ctg.setColor(c);
        cd.sendSuccess(new TranslatableComponent("commands.team.option.color.success", new Object[] { ctg.getFormattedDisplayName(), c.getName() }), true);
        return 0;
    }
    
    private static int emptyTeam(final CommandSourceStack cd, final PlayerTeam ctg) throws CommandSyntaxException {
        final Scoreboard cti3 = cd.getServer().getScoreboard();
        final Collection<String> collection4 = (Collection<String>)Lists.newArrayList((Iterable)ctg.getPlayers());
        if (collection4.isEmpty()) {
            throw TeamCommand.ERROR_TEAM_ALREADY_EMPTY.create();
        }
        for (final String string6 : collection4) {
            cti3.removePlayerFromTeam(string6, ctg);
        }
        cd.sendSuccess(new TranslatableComponent("commands.team.empty.success", new Object[] { collection4.size(), ctg.getFormattedDisplayName() }), true);
        return collection4.size();
    }
    
    private static int deleteTeam(final CommandSourceStack cd, final PlayerTeam ctg) {
        final Scoreboard cti3 = cd.getServer().getScoreboard();
        cti3.removePlayerTeam(ctg);
        cd.sendSuccess(new TranslatableComponent("commands.team.remove.success", new Object[] { ctg.getFormattedDisplayName() }), true);
        return cti3.getPlayerTeams().size();
    }
    
    private static int createTeam(final CommandSourceStack cd, final String string) throws CommandSyntaxException {
        return createTeam(cd, string, new TextComponent(string));
    }
    
    private static int createTeam(final CommandSourceStack cd, final String string, final Component jo) throws CommandSyntaxException {
        final Scoreboard cti4 = cd.getServer().getScoreboard();
        if (cti4.getPlayerTeam(string) != null) {
            throw TeamCommand.ERROR_TEAM_ALREADY_EXISTS.create();
        }
        if (string.length() > 16) {
            throw TeamCommand.ERROR_TEAM_NAME_TOO_LONG.create(16);
        }
        final PlayerTeam ctg5 = cti4.addPlayerTeam(string);
        ctg5.setDisplayName(jo);
        cd.sendSuccess(new TranslatableComponent("commands.team.add.success", new Object[] { ctg5.getFormattedDisplayName() }), true);
        return cti4.getPlayerTeams().size();
    }
    
    private static int listMembers(final CommandSourceStack cd, final PlayerTeam ctg) {
        final Collection<String> collection3 = ctg.getPlayers();
        if (collection3.isEmpty()) {
            cd.sendSuccess(new TranslatableComponent("commands.team.list.members.empty", new Object[] { ctg.getFormattedDisplayName() }), false);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.team.list.members.success", new Object[] { ctg.getFormattedDisplayName(), collection3.size(), ComponentUtils.formatList(collection3) }), false);
        }
        return collection3.size();
    }
    
    private static int listTeams(final CommandSourceStack cd) {
        final Collection<PlayerTeam> collection2 = cd.getServer().getScoreboard().getPlayerTeams();
        if (collection2.isEmpty()) {
            cd.sendSuccess(new TranslatableComponent("commands.team.list.teams.empty", new Object[0]), false);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.team.list.teams.success", new Object[] { collection2.size(), ComponentUtils.<PlayerTeam>formatList(collection2, (java.util.function.Function<PlayerTeam, Component>)PlayerTeam::getFormattedDisplayName) }), false);
        }
        return collection2.size();
    }
    
    private static int setPrefix(final CommandSourceStack cd, final PlayerTeam ctg, final Component jo) {
        ctg.setPlayerPrefix(jo);
        cd.sendSuccess(new TranslatableComponent("commands.team.option.prefix.success", new Object[] { jo }), false);
        return 1;
    }
    
    private static int setSuffix(final CommandSourceStack cd, final PlayerTeam ctg, final Component jo) {
        ctg.setPlayerSuffix(jo);
        cd.sendSuccess(new TranslatableComponent("commands.team.option.suffix.success", new Object[] { jo }), false);
        return 1;
    }
    
    static {
        ERROR_TEAM_ALREADY_EXISTS = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.add.duplicate", new Object[0]));
        ERROR_TEAM_NAME_TOO_LONG = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.team.add.longName", new Object[] { object }));
        ERROR_TEAM_ALREADY_EMPTY = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.empty.unchanged", new Object[0]));
        ERROR_TEAM_ALREADY_NAME = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.name.unchanged", new Object[0]));
        ERROR_TEAM_ALREADY_COLOR = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.color.unchanged", new Object[0]));
        ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.friendlyfire.alreadyEnabled", new Object[0]));
        ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.friendlyfire.alreadyDisabled", new Object[0]));
        ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.seeFriendlyInvisibles.alreadyEnabled", new Object[0]));
        ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.seeFriendlyInvisibles.alreadyDisabled", new Object[0]));
        ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.nametagVisibility.unchanged", new Object[0]));
        ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.deathMessageVisibility.unchanged", new Object[0]));
        ERROR_TEAM_COLLISION_UNCHANGED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.collisionRule.unchanged", new Object[0]));
    }
}
