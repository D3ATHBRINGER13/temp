package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.world.level.timers.FunctionCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.FunctionTagCallback;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.tags.Tag;
import net.minecraft.commands.CommandFunction;
import com.mojang.datafixers.util.Either;
import net.minecraft.commands.arguments.TimeArgument;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ScheduleCommand {
    private static final SimpleCommandExceptionType ERROR_SAME_TICK;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("schedule").requires(cd -> cd.hasPermission(2))).then(Commands.literal("function").then(Commands.argument("function", (com.mojang.brigadier.arguments.ArgumentType<Object>)FunctionArgument.functions()).suggests((SuggestionProvider)FunctionCommand.SUGGEST_FUNCTION).then(Commands.argument("time", (com.mojang.brigadier.arguments.ArgumentType<Object>)TimeArgument.time()).executes(commandContext -> schedule((CommandSourceStack)commandContext.getSource(), FunctionArgument.getFunctionOrTag((CommandContext<CommandSourceStack>)commandContext, "function"), IntegerArgumentType.getInteger(commandContext, "time")))))));
    }
    
    private static int schedule(final CommandSourceStack cd, final Either<CommandFunction, Tag<CommandFunction>> either, final int integer) throws CommandSyntaxException {
        if (integer == 0) {
            throw ScheduleCommand.ERROR_SAME_TICK.create();
        }
        final long long4 = cd.getLevel().getGameTime() + integer;
        either.ifLeft(ca -> {
            final ResourceLocation qv6 = ca.getId();
            cd.getLevel().getLevelData().getScheduledEvents().reschedule(qv6.toString(), long4, new FunctionCallback(qv6));
            cd.sendSuccess(new TranslatableComponent("commands.schedule.created.function", new Object[] { qv6, integer, long4 }), true);
        }).ifRight(zg -> {
            final ResourceLocation qv6 = zg.getId();
            cd.getLevel().getLevelData().getScheduledEvents().reschedule("#" + qv6.toString(), long4, new FunctionTagCallback(qv6));
            cd.sendSuccess(new TranslatableComponent("commands.schedule.created.tag", new Object[] { qv6, integer, long4 }), true);
        });
        return (int)Math.floorMod(long4, 2147483647L);
    }
    
    static {
        ERROR_SAME_TICK = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.schedule.same_tick", new Object[0]));
    }
}
