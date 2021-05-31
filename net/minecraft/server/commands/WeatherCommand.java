package net.minecraft.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class WeatherCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("weather").requires(cd -> cd.hasPermission(2))).then(((LiteralArgumentBuilder)Commands.literal("clear").executes(commandContext -> setClear((CommandSourceStack)commandContext.getSource(), 6000))).then(Commands.argument("duration", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0, 1000000)).executes(commandContext -> setClear((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))))).then(((LiteralArgumentBuilder)Commands.literal("rain").executes(commandContext -> setRain((CommandSourceStack)commandContext.getSource(), 6000))).then(Commands.argument("duration", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0, 1000000)).executes(commandContext -> setRain((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))))).then(((LiteralArgumentBuilder)Commands.literal("thunder").executes(commandContext -> setThunder((CommandSourceStack)commandContext.getSource(), 6000))).then(Commands.argument("duration", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0, 1000000)).executes(commandContext -> setThunder((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20)))));
    }
    
    private static int setClear(final CommandSourceStack cd, final int integer) {
        cd.getLevel().getLevelData().setClearWeatherTime(integer);
        cd.getLevel().getLevelData().setRainTime(0);
        cd.getLevel().getLevelData().setThunderTime(0);
        cd.getLevel().getLevelData().setRaining(false);
        cd.getLevel().getLevelData().setThundering(false);
        cd.sendSuccess(new TranslatableComponent("commands.weather.set.clear", new Object[0]), true);
        return integer;
    }
    
    private static int setRain(final CommandSourceStack cd, final int integer) {
        cd.getLevel().getLevelData().setClearWeatherTime(0);
        cd.getLevel().getLevelData().setRainTime(integer);
        cd.getLevel().getLevelData().setThunderTime(integer);
        cd.getLevel().getLevelData().setRaining(true);
        cd.getLevel().getLevelData().setThundering(false);
        cd.sendSuccess(new TranslatableComponent("commands.weather.set.rain", new Object[0]), true);
        return integer;
    }
    
    private static int setThunder(final CommandSourceStack cd, final int integer) {
        cd.getLevel().getLevelData().setClearWeatherTime(0);
        cd.getLevel().getLevelData().setRainTime(integer);
        cd.getLevel().getLevelData().setThunderTime(integer);
        cd.getLevel().getLevelData().setRaining(true);
        cd.getLevel().getLevelData().setThundering(true);
        cd.sendSuccess(new TranslatableComponent("commands.weather.set.thunder", new Object[0]), true);
        return integer;
    }
}
