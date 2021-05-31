package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.ChatFormatting;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Style;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class LocateCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("locate").requires(cd -> cd.hasPermission(2))).then(Commands.literal("Pillager_Outpost").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Pillager_Outpost")))).then(Commands.literal("Mineshaft").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Mineshaft")))).then(Commands.literal("Mansion").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Mansion")))).then(Commands.literal("Igloo").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Igloo")))).then(Commands.literal("Desert_Pyramid").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Desert_Pyramid")))).then(Commands.literal("Jungle_Pyramid").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Jungle_Pyramid")))).then(Commands.literal("Swamp_Hut").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Swamp_Hut")))).then(Commands.literal("Stronghold").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Stronghold")))).then(Commands.literal("Monument").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Monument")))).then(Commands.literal("Fortress").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Fortress")))).then(Commands.literal("EndCity").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "EndCity")))).then(Commands.literal("Ocean_Ruin").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Ocean_Ruin")))).then(Commands.literal("Buried_Treasure").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Buried_Treasure")))).then(Commands.literal("Shipwreck").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Shipwreck")))).then(Commands.literal("Village").executes(commandContext -> locate((CommandSourceStack)commandContext.getSource(), "Village"))));
    }
    
    private static int locate(final CommandSourceStack cd, final String string) throws CommandSyntaxException {
        final BlockPos ew3 = new BlockPos(cd.getPosition());
        final BlockPos ew4 = cd.getLevel().findNearestMapFeature(string, ew3, 100, false);
        if (ew4 == null) {
            throw LocateCommand.ERROR_FAILED.create();
        }
        final int integer5 = Mth.floor(dist(ew3.getX(), ew3.getZ(), ew4.getX(), ew4.getZ()));
        final Component jo6 = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", new Object[] { ew4.getX(), "~", ew4.getZ() })).withStyle((Consumer<Style>)(jw -> jw.setColor(ChatFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, new StringBuilder().append("/tp @s ").append(ew4.getX()).append(" ~ ").append(ew4.getZ()).toString())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip", new Object[0])))));
        cd.sendSuccess(new TranslatableComponent("commands.locate.success", new Object[] { string, jo6, integer5 }), false);
        return integer5;
    }
    
    private static float dist(final int integer1, final int integer2, final int integer3, final int integer4) {
        final int integer5 = integer3 - integer1;
        final int integer6 = integer4 - integer2;
        return Mth.sqrt((float)(integer5 * integer5 + integer6 * integer6));
    }
    
    static {
        ERROR_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.locate.failed", new Object[0]));
    }
}
