package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.sounds.SoundSource;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class PlaySoundCommand {
    private static final SimpleCommandExceptionType ERROR_TOO_FAR;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        final RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> requiredArgumentBuilder2 = (RequiredArgumentBuilder<CommandSourceStack, ResourceLocation>)Commands.argument("sound", (com.mojang.brigadier.arguments.ArgumentType<Object>)ResourceLocationArgument.id()).suggests((SuggestionProvider)SuggestionProviders.AVAILABLE_SOUNDS);
        for (final SoundSource yq6 : SoundSource.values()) {
            requiredArgumentBuilder2.then((ArgumentBuilder)source(yq6));
        }
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("playsound").requires(cd -> cd.hasPermission(2))).then((ArgumentBuilder)requiredArgumentBuilder2));
    }
    
    private static LiteralArgumentBuilder<CommandSourceStack> source(final SoundSource yq) {
        return (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal(yq.getName()).then(((RequiredArgumentBuilder)Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.players()).executes(commandContext -> playSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "sound"), yq, ((CommandSourceStack)commandContext.getSource()).getPosition(), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3Argument.vec3()).executes(commandContext -> playSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "sound"), yq, Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("volume", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(0.0f)).executes(commandContext -> playSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "sound"), yq, Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), (float)commandContext.getArgument("volume", (Class)Float.class), 1.0f, 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pitch", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(0.0f, 2.0f)).executes(commandContext -> playSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "sound"), yq, Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), (float)commandContext.getArgument("volume", (Class)Float.class), (float)commandContext.getArgument("pitch", (Class)Float.class), 0.0f))).then(Commands.argument("minVolume", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(0.0f, 1.0f)).executes(commandContext -> playSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "sound"), yq, Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), (float)commandContext.getArgument("volume", (Class)Float.class), (float)commandContext.getArgument("pitch", (Class)Float.class), (float)commandContext.getArgument("minVolume", (Class)Float.class))))))));
    }
    
    private static int playSound(final CommandSourceStack cd, final Collection<ServerPlayer> collection, final ResourceLocation qv, final SoundSource yq, final Vec3 csi, final float float6, final float float7, final float float8) throws CommandSyntaxException {
        final double double9 = Math.pow((float6 > 1.0f) ? ((double)(float6 * 16.0f)) : 16.0, 2.0);
        int integer11 = 0;
        for (final ServerPlayer vl13 : collection) {
            final double double10 = csi.x - vl13.x;
            final double double11 = csi.y - vl13.y;
            final double double12 = csi.z - vl13.z;
            final double double13 = double10 * double10 + double11 * double11 + double12 * double12;
            Vec3 csi2 = csi;
            float float9 = float6;
            if (double13 > double9) {
                if (float8 <= 0.0f) {
                    continue;
                }
                final double double14 = Mth.sqrt(double13);
                csi2 = new Vec3(vl13.x + double10 / double14 * 2.0, vl13.y + double11 / double14 * 2.0, vl13.z + double12 / double14 * 2.0);
                float9 = float8;
            }
            vl13.connection.send(new ClientboundCustomSoundPacket(qv, yq, csi2, float9, float7));
            ++integer11;
        }
        if (integer11 == 0) {
            throw PlaySoundCommand.ERROR_TOO_FAR.create();
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.playsound.success.single", new Object[] { qv, ((ServerPlayer)collection.iterator().next()).getDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.playsound.success.single", new Object[] { qv, ((ServerPlayer)collection.iterator().next()).getDisplayName() }), true);
        }
        return integer11;
    }
    
    static {
        ERROR_TOO_FAR = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.playsound.failed", new Object[0]));
    }
}
