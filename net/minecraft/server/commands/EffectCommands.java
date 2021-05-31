package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import java.util.Collection;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.arguments.MobEffectArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.EntityArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class EffectCommands {
    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED;
    private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED;
    private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("effect").requires(cd -> cd.hasPermission(2))).then(Commands.literal("clear").then(((RequiredArgumentBuilder)Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.entities()).executes(commandContext -> clearEffects((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets")))).then(Commands.argument("effect", (com.mojang.brigadier.arguments.ArgumentType<Object>)MobEffectArgument.effect()).executes(commandContext -> clearEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), MobEffectArgument.getEffect((CommandContext<CommandSourceStack>)commandContext, "effect"))))))).then(Commands.literal("give").then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("effect", (com.mojang.brigadier.arguments.ArgumentType<Object>)MobEffectArgument.effect()).executes(commandContext -> giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), MobEffectArgument.getEffect((CommandContext<CommandSourceStack>)commandContext, "effect"), null, 0, true))).then(((RequiredArgumentBuilder)Commands.argument("seconds", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(1, 1000000)).executes(commandContext -> giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), MobEffectArgument.getEffect((CommandContext<CommandSourceStack>)commandContext, "effect"), IntegerArgumentType.getInteger(commandContext, "seconds"), 0, true))).then(((RequiredArgumentBuilder)Commands.argument("amplifier", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0, 255)).executes(commandContext -> giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), MobEffectArgument.getEffect((CommandContext<CommandSourceStack>)commandContext, "effect"), IntegerArgumentType.getInteger(commandContext, "seconds"), IntegerArgumentType.getInteger(commandContext, "amplifier"), true))).then(Commands.argument("hideParticles", (com.mojang.brigadier.arguments.ArgumentType<Object>)BoolArgumentType.bool()).executes(commandContext -> giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), MobEffectArgument.getEffect((CommandContext<CommandSourceStack>)commandContext, "effect"), IntegerArgumentType.getInteger(commandContext, "seconds"), IntegerArgumentType.getInteger(commandContext, "amplifier"), !BoolArgumentType.getBool(commandContext, "hideParticles"))))))))));
    }
    
    private static int giveEffect(final CommandSourceStack cd, final Collection<? extends Entity> collection, final MobEffect aig, @Nullable final Integer integer, final int integer, final boolean boolean6) throws CommandSyntaxException {
        int integer2 = 0;
        int integer3;
        if (integer != null) {
            if (aig.isInstantenous()) {
                integer3 = integer;
            }
            else {
                integer3 = integer * 20;
            }
        }
        else if (aig.isInstantenous()) {
            integer3 = 1;
        }
        else {
            integer3 = 600;
        }
        for (final Entity aio10 : collection) {
            if (aio10 instanceof LivingEntity) {
                final MobEffectInstance aii11 = new MobEffectInstance(aig, integer3, integer, false, boolean6);
                if (!((LivingEntity)aio10).addEffect(aii11)) {
                    continue;
                }
                ++integer2;
            }
        }
        if (integer2 == 0) {
            throw EffectCommands.ERROR_GIVE_FAILED.create();
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.effect.give.success.single", new Object[] { aig.getDisplayName(), ((Entity)collection.iterator().next()).getDisplayName(), integer3 / 20 }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.effect.give.success.multiple", new Object[] { aig.getDisplayName(), collection.size(), integer3 / 20 }), true);
        }
        return integer2;
    }
    
    private static int clearEffects(final CommandSourceStack cd, final Collection<? extends Entity> collection) throws CommandSyntaxException {
        int integer3 = 0;
        for (final Entity aio5 : collection) {
            if (aio5 instanceof LivingEntity && ((LivingEntity)aio5).removeAllEffects()) {
                ++integer3;
            }
        }
        if (integer3 == 0) {
            throw EffectCommands.ERROR_CLEAR_EVERYTHING_FAILED.create();
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.effect.clear.everything.success.single", new Object[] { ((Entity)collection.iterator().next()).getDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.effect.clear.everything.success.multiple", new Object[] { collection.size() }), true);
        }
        return integer3;
    }
    
    private static int clearEffect(final CommandSourceStack cd, final Collection<? extends Entity> collection, final MobEffect aig) throws CommandSyntaxException {
        int integer4 = 0;
        for (final Entity aio6 : collection) {
            if (aio6 instanceof LivingEntity && ((LivingEntity)aio6).removeEffect(aig)) {
                ++integer4;
            }
        }
        if (integer4 == 0) {
            throw EffectCommands.ERROR_CLEAR_SPECIFIC_FAILED.create();
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.single", new Object[] { aig.getDisplayName(), ((Entity)collection.iterator().next()).getDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.multiple", new Object[] { aig.getDisplayName(), collection.size() }), true);
        }
        return integer4;
    }
    
    static {
        ERROR_GIVE_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.effect.give.failed", new Object[0]));
        ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.effect.clear.everything.failed", new Object[0]));
        ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.effect.clear.specific.failed", new Object[0]));
    }
}
