package net.minecraft.server.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.EntitySummonArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class SummonCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("summon").requires(cd -> cd.hasPermission(2))).then(((RequiredArgumentBuilder)Commands.argument("entity", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntitySummonArgument.id()).suggests((SuggestionProvider)SuggestionProviders.SUMMONABLE_ENTITIES).executes(commandContext -> spawnEntity((CommandSourceStack)commandContext.getSource(), EntitySummonArgument.getSummonableEntity((CommandContext<CommandSourceStack>)commandContext, "entity"), ((CommandSourceStack)commandContext.getSource()).getPosition(), new CompoundTag(), true))).then(((RequiredArgumentBuilder)Commands.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3Argument.vec3()).executes(commandContext -> spawnEntity((CommandSourceStack)commandContext.getSource(), EntitySummonArgument.getSummonableEntity((CommandContext<CommandSourceStack>)commandContext, "entity"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), new CompoundTag(), true))).then(Commands.argument("nbt", (com.mojang.brigadier.arguments.ArgumentType<Object>)CompoundTagArgument.compoundTag()).executes(commandContext -> spawnEntity((CommandSourceStack)commandContext.getSource(), EntitySummonArgument.getSummonableEntity((CommandContext<CommandSourceStack>)commandContext, "entity"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), CompoundTagArgument.getCompoundTag((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "nbt"), false))))));
    }
    
    private static int spawnEntity(final CommandSourceStack cd, final ResourceLocation qv, final Vec3 csi, final CompoundTag id, final boolean boolean5) throws CommandSyntaxException {
        final CompoundTag id2 = id.copy();
        id2.putString("id", qv.toString());
        if (EntityType.getKey(EntityType.LIGHTNING_BOLT).equals(qv)) {
            final LightningBolt atu7 = new LightningBolt(cd.getLevel(), csi.x, csi.y, csi.z, false);
            cd.getLevel().addGlobalEntity(atu7);
            cd.sendSuccess(new TranslatableComponent("commands.summon.success", new Object[] { atu7.getDisplayName() }), true);
            return 1;
        }
        final ServerLevel vk7 = cd.getLevel();
        final Entity aio8 = EntityType.loadEntityRecursive(id2, vk7, (Function<Entity, Entity>)(aio -> {
            aio.moveTo(csi.x, csi.y, csi.z, aio.yRot, aio.xRot);
            if (!vk7.addWithUUID(aio)) {
                return null;
            }
            return aio;
        }));
        if (aio8 == null) {
            throw SummonCommand.ERROR_FAILED.create();
        }
        if (boolean5 && aio8 instanceof Mob) {
            ((Mob)aio8).finalizeSpawn(cd.getLevel(), cd.getLevel().getCurrentDifficultyAt(new BlockPos(aio8)), MobSpawnType.COMMAND, null, null);
        }
        cd.sendSuccess(new TranslatableComponent("commands.summon.success", new Object[] { aio8.getDisplayName() }), true);
        return 1;
    }
    
    static {
        ERROR_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.summon.failed", new Object[0]));
    }
}
