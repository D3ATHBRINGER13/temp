package net.minecraft.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;
import net.minecraft.commands.arguments.selector.EntitySelector;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.sounds.SoundSource;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class StopSoundCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        final RequiredArgumentBuilder<CommandSourceStack, EntitySelector> requiredArgumentBuilder2 = (RequiredArgumentBuilder<CommandSourceStack, EntitySelector>)((RequiredArgumentBuilder)Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.players()).executes(commandContext -> stopSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), null, null))).then(Commands.literal("*").then(Commands.argument("sound", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)SuggestionProviders.AVAILABLE_SOUNDS).executes(commandContext -> stopSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), null, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "sound")))));
        for (final SoundSource yq6 : SoundSource.values()) {
            requiredArgumentBuilder2.then(((LiteralArgumentBuilder)Commands.literal(yq6.getName()).executes(commandContext -> stopSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), yq6, null))).then(Commands.argument("sound", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)SuggestionProviders.AVAILABLE_SOUNDS).executes(commandContext -> stopSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), yq6, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "sound")))));
        }
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("stopsound").requires(cd -> cd.hasPermission(2))).then((ArgumentBuilder)requiredArgumentBuilder2));
    }
    
    private static int stopSound(final CommandSourceStack cd, final Collection<ServerPlayer> collection, @Nullable final SoundSource yq, @Nullable final ResourceLocation qv) {
        final ClientboundStopSoundPacket ni5 = new ClientboundStopSoundPacket(qv, yq);
        for (final ServerPlayer vl7 : collection) {
            vl7.connection.send(ni5);
        }
        if (yq != null) {
            if (qv != null) {
                cd.sendSuccess(new TranslatableComponent("commands.stopsound.success.source.sound", new Object[] { qv, yq.getName() }), true);
            }
            else {
                cd.sendSuccess(new TranslatableComponent("commands.stopsound.success.source.any", new Object[] { yq.getName() }), true);
            }
        }
        else if (qv != null) {
            cd.sendSuccess(new TranslatableComponent("commands.stopsound.success.sourceless.sound", new Object[] { qv }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.stopsound.success.sourceless.any", new Object[0]), true);
        }
        return collection.size();
    }
}
