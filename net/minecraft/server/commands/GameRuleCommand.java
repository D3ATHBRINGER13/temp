package net.minecraft.server.commands;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class GameRuleCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder2 = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("gamerule").requires(cd -> cd.hasPermission(2));
        GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            public <T extends GameRules.Value<T>> void visit(final GameRules.Key<T> d, final GameRules.Type<T> e) {
                literalArgumentBuilder2.then(((LiteralArgumentBuilder)Commands.literal(d.getId()).executes(commandContext -> GameRuleCommand.<GameRules.Value>queryRule((CommandSourceStack)commandContext.getSource(), (GameRules.Key<GameRules.Value>)d))).then(e.createArgument("value").executes(commandContext -> GameRuleCommand.<GameRules.Value>setRule((CommandContext<CommandSourceStack>)commandContext, (GameRules.Key<GameRules.Value>)d))));
            }
        });
        commandDispatcher.register((LiteralArgumentBuilder)literalArgumentBuilder2);
    }
    
    private static <T extends GameRules.Value<T>> int setRule(final CommandContext<CommandSourceStack> commandContext, final GameRules.Key<T> d) {
        final CommandSourceStack cd3 = (CommandSourceStack)commandContext.getSource();
        final T f4 = cd3.getServer().getGameRules().<T>getRule(d);
        f4.setFromArgument(commandContext, "value");
        cd3.sendSuccess(new TranslatableComponent("commands.gamerule.set", new Object[] { d.getId(), f4.toString() }), true);
        return f4.getCommandResult();
    }
    
    private static <T extends GameRules.Value<T>> int queryRule(final CommandSourceStack cd, final GameRules.Key<T> d) {
        final T f3 = cd.getServer().getGameRules().<T>getRule(d);
        cd.sendSuccess(new TranslatableComponent("commands.gamerule.query", new Object[] { d.getId(), f3.toString() }), false);
        return f3.getCommandResult();
    }
}
