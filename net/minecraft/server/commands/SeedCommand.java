package net.minecraft.server.commands;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.ChatFormatting;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import java.util.function.Consumer;
import net.minecraft.network.chat.TextComponent;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class SeedCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("seed").requires(cd -> cd.getServer().isSingleplayer() || cd.hasPermission(2))).executes(commandContext -> {
            final long long2 = ((CommandSourceStack)commandContext.getSource()).getLevel().getSeed();
            final Component jo4 = ComponentUtils.wrapInSquareBrackets(new TextComponent(String.valueOf(long2)).withStyle((Consumer<Style>)(jw -> jw.setColor(ChatFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(long2))).setInsertion(String.valueOf(long2)))));
            ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.seed.success", new Object[] { jo4 }), false);
            return (int)long2;
        }));
    }
}
