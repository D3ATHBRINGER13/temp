package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.dimension.DimensionType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.world.Difficulty;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public class DifficultyCommand {
    private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder2 = Commands.literal("difficulty");
        for (final Difficulty ahg6 : Difficulty.values()) {
            literalArgumentBuilder2.then(Commands.literal(ahg6.getKey()).executes(commandContext -> setDifficulty((CommandSourceStack)commandContext.getSource(), ahg6)));
        }
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder2.requires(cd -> cd.hasPermission(2))).executes(commandContext -> {
            final Difficulty ahg2 = ((CommandSourceStack)commandContext.getSource()).getLevel().getDifficulty();
            ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.difficulty.query", new Object[] { ahg2.getDisplayName() }), false);
            return ahg2.getId();
        }));
    }
    
    public static int setDifficulty(final CommandSourceStack cd, final Difficulty ahg) throws CommandSyntaxException {
        final MinecraftServer minecraftServer3 = cd.getServer();
        if (minecraftServer3.getLevel(DimensionType.OVERWORLD).getDifficulty() == ahg) {
            throw DifficultyCommand.ERROR_ALREADY_DIFFICULT.create(ahg.getKey());
        }
        minecraftServer3.setDifficulty(ahg, true);
        cd.sendSuccess(new TranslatableComponent("commands.difficulty.success", new Object[] { ahg.getDisplayName() }), true);
        return 0;
    }
    
    static {
        ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.difficulty.failure", new Object[] { object }));
    }
}
