package net.minecraft.server.commands;

import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import java.util.Collections;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.Set;
import java.util.EnumSet;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import java.util.Collection;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.EntityArgument;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;

public class TeleportCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        final LiteralCommandNode<CommandSourceStack> literalCommandNode2 = (LiteralCommandNode<CommandSourceStack>)commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("teleport").requires(cd -> cd.hasPermission(2))).then(((RequiredArgumentBuilder)Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("location", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3Argument.vec3()).executes(commandContext -> teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), null, null))).then(Commands.argument("rotation", (com.mojang.brigadier.arguments.ArgumentType<Object>)RotationArgument.rotation()).executes(commandContext -> teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), RotationArgument.getRotation((CommandContext<CommandSourceStack>)commandContext, "rotation"), null)))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("facingEntity", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.entity()).executes(commandContext -> teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), null, new LookAt(EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "facingEntity"), EntityAnchorArgument.Anchor.FEET)))).then(Commands.argument("facingAnchor", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityAnchorArgument.anchor()).executes(commandContext -> teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), null, new LookAt(EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "facingEntity"), EntityAnchorArgument.getAnchor((CommandContext<CommandSourceStack>)commandContext, "facingAnchor")))))))).then(Commands.argument("facingLocation", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3Argument.vec3()).executes(commandContext -> teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), null, new LookAt(Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "facingLocation")))))))).then(Commands.argument("destination", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.entity()).executes(commandContext -> teleportToEntity((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "destination")))))).then(Commands.argument("location", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3Argument.vec3()).executes(commandContext -> teleportToPos((CommandSourceStack)commandContext.getSource(), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getEntityOrException()), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), WorldCoordinates.current(), null)))).then(Commands.argument("destination", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.entity()).executes(commandContext -> teleportToEntity((CommandSourceStack)commandContext.getSource(), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getEntityOrException()), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "destination")))));
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tp").requires(cd -> cd.hasPermission(2))).redirect((CommandNode)literalCommandNode2));
    }
    
    private static int teleportToEntity(final CommandSourceStack cd, final Collection<? extends Entity> collection, final Entity aio) {
        for (final Entity aio2 : collection) {
            performTeleport(cd, aio2, (ServerLevel)aio.level, aio.x, aio.y, aio.z, (Set<ClientboundPlayerPositionPacket.RelativeArgument>)EnumSet.noneOf((Class)ClientboundPlayerPositionPacket.RelativeArgument.class), aio.yRot, aio.xRot, null);
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.teleport.success.entity.single", new Object[] { ((Entity)collection.iterator().next()).getDisplayName(), aio.getDisplayName() }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.teleport.success.entity.multiple", new Object[] { collection.size(), aio.getDisplayName() }), true);
        }
        return collection.size();
    }
    
    private static int teleportToPos(final CommandSourceStack cd, final Collection<? extends Entity> collection, final ServerLevel vk, final Coordinates dl4, @Nullable final Coordinates dl5, @Nullable final LookAt a) throws CommandSyntaxException {
        final Vec3 csi7 = dl4.getPosition(cd);
        final Vec2 csh8 = (dl5 == null) ? null : dl5.getRotation(cd);
        final Set<ClientboundPlayerPositionPacket.RelativeArgument> set9 = (Set<ClientboundPlayerPositionPacket.RelativeArgument>)EnumSet.noneOf((Class)ClientboundPlayerPositionPacket.RelativeArgument.class);
        if (dl4.isXRelative()) {
            set9.add(ClientboundPlayerPositionPacket.RelativeArgument.X);
        }
        if (dl4.isYRelative()) {
            set9.add(ClientboundPlayerPositionPacket.RelativeArgument.Y);
        }
        if (dl4.isZRelative()) {
            set9.add(ClientboundPlayerPositionPacket.RelativeArgument.Z);
        }
        if (dl5 == null) {
            set9.add(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
            set9.add(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
        }
        else {
            if (dl5.isXRelative()) {
                set9.add(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
            }
            if (dl5.isYRelative()) {
                set9.add(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
            }
        }
        for (final Entity aio11 : collection) {
            if (dl5 == null) {
                performTeleport(cd, aio11, vk, csi7.x, csi7.y, csi7.z, set9, aio11.yRot, aio11.xRot, a);
            }
            else {
                performTeleport(cd, aio11, vk, csi7.x, csi7.y, csi7.z, set9, csh8.y, csh8.x, a);
            }
        }
        if (collection.size() == 1) {
            cd.sendSuccess(new TranslatableComponent("commands.teleport.success.location.single", new Object[] { ((Entity)collection.iterator().next()).getDisplayName(), csi7.x, csi7.y, csi7.z }), true);
        }
        else {
            cd.sendSuccess(new TranslatableComponent("commands.teleport.success.location.multiple", new Object[] { collection.size(), csi7.x, csi7.y, csi7.z }), true);
        }
        return collection.size();
    }
    
    private static void performTeleport(final CommandSourceStack cd, Entity aio, final ServerLevel vk, final double double4, final double double5, final double double6, final Set<ClientboundPlayerPositionPacket.RelativeArgument> set, final float float8, final float float9, @Nullable final LookAt a) {
        if (aio instanceof ServerPlayer) {
            final ChunkPos bhd14 = new ChunkPos(new BlockPos(double4, double5, double6));
            vk.getChunkSource().<Integer>addRegionTicket(TicketType.POST_TELEPORT, bhd14, 1, aio.getId());
            aio.stopRiding();
            if (((ServerPlayer)aio).isSleeping()) {
                ((ServerPlayer)aio).stopSleepInBed(true, true, false);
            }
            if (vk == aio.level) {
                ((ServerPlayer)aio).connection.teleport(double4, double5, double6, float8, float9, set);
            }
            else {
                ((ServerPlayer)aio).teleportTo(vk, double4, double5, double6, float8, float9);
            }
            aio.setYHeadRot(float8);
        }
        else {
            final float float10 = Mth.wrapDegrees(float8);
            float float11 = Mth.wrapDegrees(float9);
            float11 = Mth.clamp(float11, -90.0f, 90.0f);
            if (vk == aio.level) {
                aio.moveTo(double4, double5, double6, float10, float11);
                aio.setYHeadRot(float10);
            }
            else {
                aio.unRide();
                aio.dimension = vk.dimension.getType();
                final Entity aio2 = aio;
                aio = (Entity)aio2.getType().create(vk);
                if (aio == null) {
                    return;
                }
                aio.restoreFrom(aio2);
                aio.moveTo(double4, double5, double6, float10, float11);
                aio.setYHeadRot(float10);
                vk.addFromAnotherDimension(aio);
                aio2.removed = true;
            }
        }
        if (a != null) {
            a.perform(cd, aio);
        }
        if (!(aio instanceof LivingEntity) || !((LivingEntity)aio).isFallFlying()) {
            aio.setDeltaMovement(aio.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            aio.onGround = true;
        }
    }
    
    static class LookAt {
        private final Vec3 position;
        private final Entity entity;
        private final EntityAnchorArgument.Anchor anchor;
        
        public LookAt(final Entity aio, final EntityAnchorArgument.Anchor a) {
            this.entity = aio;
            this.anchor = a;
            this.position = a.apply(aio);
        }
        
        public LookAt(final Vec3 csi) {
            this.entity = null;
            this.position = csi;
            this.anchor = null;
        }
        
        public void perform(final CommandSourceStack cd, final Entity aio) {
            if (this.entity != null) {
                if (aio instanceof ServerPlayer) {
                    ((ServerPlayer)aio).lookAt(cd.getAnchor(), this.entity, this.anchor);
                }
                else {
                    aio.lookAt(cd.getAnchor(), this.position);
                }
            }
            else {
                aio.lookAt(cd.getAnchor(), this.position);
            }
        }
    }
}
