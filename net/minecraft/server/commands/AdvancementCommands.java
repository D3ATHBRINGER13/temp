package net.minecraft.server.commands;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import java.util.stream.Stream;
import net.minecraft.commands.SharedSuggestionProvider;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Iterator;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.EntityArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class AdvancementCommands {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_ADVANCEMENTS;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("advancement").requires(cd -> cd.hasPermission(2))).then(Commands.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)AdvancementCommands.SUGGEST_ADVANCEMENTS).executes(commandContext -> perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, (Collection<Advancement>)getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.ONLY)))).then(Commands.argument("criterion", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest((Iterable<String>)ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> performCriterion((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), StringArgumentType.getString(commandContext, "criterion"))))))).then(Commands.literal("from").then(Commands.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)AdvancementCommands.SUGGEST_ADVANCEMENTS).executes(commandContext -> perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, (Collection<Advancement>)getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.FROM)))))).then(Commands.literal("until").then(Commands.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)AdvancementCommands.SUGGEST_ADVANCEMENTS).executes(commandContext -> perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, (Collection<Advancement>)getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.UNTIL)))))).then(Commands.literal("through").then(Commands.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)AdvancementCommands.SUGGEST_ADVANCEMENTS).executes(commandContext -> perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, (Collection<Advancement>)getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.THROUGH)))))).then(Commands.literal("everything").executes(commandContext -> perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAllAdvancements())))))).then(Commands.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)AdvancementCommands.SUGGEST_ADVANCEMENTS).executes(commandContext -> perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, (Collection<Advancement>)getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.ONLY)))).then(Commands.argument("criterion", (com.mojang.brigadier.arguments.ArgumentType<Object>)StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest((Iterable<String>)ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> performCriterion((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), StringArgumentType.getString(commandContext, "criterion"))))))).then(Commands.literal("from").then(Commands.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)AdvancementCommands.SUGGEST_ADVANCEMENTS).executes(commandContext -> perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, (Collection<Advancement>)getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.FROM)))))).then(Commands.literal("until").then(Commands.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)AdvancementCommands.SUGGEST_ADVANCEMENTS).executes(commandContext -> perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, (Collection<Advancement>)getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.UNTIL)))))).then(Commands.literal("through").then(Commands.argument("advancement", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)AdvancementCommands.SUGGEST_ADVANCEMENTS).executes(commandContext -> perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, (Collection<Advancement>)getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.THROUGH)))))).then(Commands.literal("everything").executes(commandContext -> perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAllAdvancements()))))));
    }
    
    private static int perform(final CommandSourceStack cd, final Collection<ServerPlayer> collection2, final Action a, final Collection<Advancement> collection4) {
        int integer5 = 0;
        for (final ServerPlayer vl7 : collection2) {
            integer5 += a.perform(vl7, (Iterable<Advancement>)collection4);
        }
        if (integer5 != 0) {
            if (collection4.size() == 1) {
                if (collection2.size() == 1) {
                    cd.sendSuccess(new TranslatableComponent(a.getKey() + ".one.to.one.success", new Object[] { ((Advancement)collection4.iterator().next()).getChatComponent(), ((ServerPlayer)collection2.iterator().next()).getDisplayName() }), true);
                }
                else {
                    cd.sendSuccess(new TranslatableComponent(a.getKey() + ".one.to.many.success", new Object[] { ((Advancement)collection4.iterator().next()).getChatComponent(), collection2.size() }), true);
                }
            }
            else if (collection2.size() == 1) {
                cd.sendSuccess(new TranslatableComponent(a.getKey() + ".many.to.one.success", new Object[] { collection4.size(), ((ServerPlayer)collection2.iterator().next()).getDisplayName() }), true);
            }
            else {
                cd.sendSuccess(new TranslatableComponent(a.getKey() + ".many.to.many.success", new Object[] { collection4.size(), collection2.size() }), true);
            }
            return integer5;
        }
        if (collection4.size() == 1) {
            if (collection2.size() == 1) {
                throw new CommandRuntimeException(new TranslatableComponent(a.getKey() + ".one.to.one.failure", new Object[] { ((Advancement)collection4.iterator().next()).getChatComponent(), ((ServerPlayer)collection2.iterator().next()).getDisplayName() }));
            }
            throw new CommandRuntimeException(new TranslatableComponent(a.getKey() + ".one.to.many.failure", new Object[] { ((Advancement)collection4.iterator().next()).getChatComponent(), collection2.size() }));
        }
        else {
            if (collection2.size() == 1) {
                throw new CommandRuntimeException(new TranslatableComponent(a.getKey() + ".many.to.one.failure", new Object[] { collection4.size(), ((ServerPlayer)collection2.iterator().next()).getDisplayName() }));
            }
            throw new CommandRuntimeException(new TranslatableComponent(a.getKey() + ".many.to.many.failure", new Object[] { collection4.size(), collection2.size() }));
        }
    }
    
    private static int performCriterion(final CommandSourceStack cd, final Collection<ServerPlayer> collection, final Action a, final Advancement q, final String string) {
        int integer6 = 0;
        if (!q.getCriteria().containsKey(string)) {
            throw new CommandRuntimeException(new TranslatableComponent("commands.advancement.criterionNotFound", new Object[] { q.getChatComponent(), string }));
        }
        for (final ServerPlayer vl8 : collection) {
            if (a.performCriterion(vl8, q, string)) {
                ++integer6;
            }
        }
        if (integer6 != 0) {
            if (collection.size() == 1) {
                cd.sendSuccess(new TranslatableComponent(a.getKey() + ".criterion.to.one.success", new Object[] { string, q.getChatComponent(), ((ServerPlayer)collection.iterator().next()).getDisplayName() }), true);
            }
            else {
                cd.sendSuccess(new TranslatableComponent(a.getKey() + ".criterion.to.many.success", new Object[] { string, q.getChatComponent(), collection.size() }), true);
            }
            return integer6;
        }
        if (collection.size() == 1) {
            throw new CommandRuntimeException(new TranslatableComponent(a.getKey() + ".criterion.to.one.failure", new Object[] { string, q.getChatComponent(), ((ServerPlayer)collection.iterator().next()).getDisplayName() }));
        }
        throw new CommandRuntimeException(new TranslatableComponent(a.getKey() + ".criterion.to.many.failure", new Object[] { string, q.getChatComponent(), collection.size() }));
    }
    
    private static List<Advancement> getAdvancements(final Advancement q, final Mode b) {
        final List<Advancement> list3 = (List<Advancement>)Lists.newArrayList();
        if (b.parents) {
            for (Advancement q2 = q.getParent(); q2 != null; q2 = q2.getParent()) {
                list3.add(q2);
            }
        }
        list3.add(q);
        if (b.children) {
            addChildren(q, list3);
        }
        return list3;
    }
    
    private static void addChildren(final Advancement q, final List<Advancement> list) {
        for (final Advancement q2 : q.getChildren()) {
            list.add(q2);
            addChildren(q2, list);
        }
    }
    
    static {
        SUGGEST_ADVANCEMENTS = ((commandContext, suggestionsBuilder) -> {
            final Collection<Advancement> collection3 = ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAllAdvancements();
            return SharedSuggestionProvider.suggestResource((Stream<ResourceLocation>)collection3.stream().map(Advancement::getId), suggestionsBuilder);
        });
    }
    
    enum Action {
        GRANT("grant") {
            @Override
            protected boolean perform(final ServerPlayer vl, final Advancement q) {
                final AdvancementProgress s4 = vl.getAdvancements().getOrStartProgress(q);
                if (s4.isDone()) {
                    return false;
                }
                for (final String string6 : s4.getRemainingCriteria()) {
                    vl.getAdvancements().award(q, string6);
                }
                return true;
            }
            
            @Override
            protected boolean performCriterion(final ServerPlayer vl, final Advancement q, final String string) {
                return vl.getAdvancements().award(q, string);
            }
        }, 
        REVOKE("revoke") {
            @Override
            protected boolean perform(final ServerPlayer vl, final Advancement q) {
                final AdvancementProgress s4 = vl.getAdvancements().getOrStartProgress(q);
                if (!s4.hasProgress()) {
                    return false;
                }
                for (final String string6 : s4.getCompletedCriteria()) {
                    vl.getAdvancements().revoke(q, string6);
                }
                return true;
            }
            
            @Override
            protected boolean performCriterion(final ServerPlayer vl, final Advancement q, final String string) {
                return vl.getAdvancements().revoke(q, string);
            }
        };
        
        private final String key;
        
        private Action(final String string3) {
            this.key = "commands.advancement." + string3;
        }
        
        public int perform(final ServerPlayer vl, final Iterable<Advancement> iterable) {
            int integer4 = 0;
            for (final Advancement q6 : iterable) {
                if (this.perform(vl, q6)) {
                    ++integer4;
                }
            }
            return integer4;
        }
        
        protected abstract boolean perform(final ServerPlayer vl, final Advancement q);
        
        protected abstract boolean performCriterion(final ServerPlayer vl, final Advancement q, final String string);
        
        protected String getKey() {
            return this.key;
        }
    }
    
    enum Mode {
        ONLY(false, false), 
        THROUGH(true, true), 
        FROM(false, true), 
        UNTIL(true, false), 
        EVERYTHING(true, true);
        
        private final boolean parents;
        private final boolean children;
        
        private Mode(final boolean boolean3, final boolean boolean4) {
            this.parents = boolean3;
            this.children = boolean4;
        }
    }
}
