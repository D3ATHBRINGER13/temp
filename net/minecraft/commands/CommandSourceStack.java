package net.minecraft.commands;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.Lists;
import java.util.Collection;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.chat.TextComponent;
import java.util.Iterator;
import net.minecraft.world.level.GameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.BinaryOperator;
import net.minecraft.world.phys.Vec2;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import com.mojang.brigadier.ResultConsumer;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CommandSourceStack implements SharedSuggestionProvider {
    public static final SimpleCommandExceptionType ERROR_NOT_PLAYER;
    public static final SimpleCommandExceptionType ERROR_NOT_ENTITY;
    private final CommandSource source;
    private final Vec3 worldPosition;
    private final ServerLevel level;
    private final int permissionLevel;
    private final String textName;
    private final Component displayName;
    private final MinecraftServer server;
    private final boolean silent;
    @Nullable
    private final Entity entity;
    private final ResultConsumer<CommandSourceStack> consumer;
    private final EntityAnchorArgument.Anchor anchor;
    private final Vec2 rotation;
    
    public CommandSourceStack(final CommandSource cc, final Vec3 csi, final Vec2 csh, final ServerLevel vk, final int integer, final String string, final Component jo, final MinecraftServer minecraftServer, @Nullable final Entity aio) {
        this(cc, csi, csh, vk, integer, string, jo, minecraftServer, aio, false, (ResultConsumer<CommandSourceStack>)((commandContext, boolean2, integer) -> {}), EntityAnchorArgument.Anchor.FEET);
    }
    
    protected CommandSourceStack(final CommandSource cc, final Vec3 csi, final Vec2 csh, final ServerLevel vk, final int integer, final String string, final Component jo, final MinecraftServer minecraftServer, @Nullable final Entity aio, final boolean boolean10, final ResultConsumer<CommandSourceStack> resultConsumer, final EntityAnchorArgument.Anchor a) {
        this.source = cc;
        this.worldPosition = csi;
        this.level = vk;
        this.silent = boolean10;
        this.entity = aio;
        this.permissionLevel = integer;
        this.textName = string;
        this.displayName = jo;
        this.server = minecraftServer;
        this.consumer = resultConsumer;
        this.anchor = a;
        this.rotation = csh;
    }
    
    public CommandSourceStack withEntity(final Entity aio) {
        if (this.entity == aio) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, aio.getName().getString(), aio.getDisplayName(), this.server, aio, this.silent, this.consumer, this.anchor);
    }
    
    public CommandSourceStack withPosition(final Vec3 csi) {
        if (this.worldPosition.equals(csi)) {
            return this;
        }
        return new CommandSourceStack(this.source, csi, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
    }
    
    public CommandSourceStack withRotation(final Vec2 csh) {
        if (this.rotation.equals(csh)) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, csh, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
    }
    
    public CommandSourceStack withCallback(final ResultConsumer<CommandSourceStack> resultConsumer) {
        if (this.consumer.equals(resultConsumer)) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, resultConsumer, this.anchor);
    }
    
    public CommandSourceStack withCallback(final ResultConsumer<CommandSourceStack> resultConsumer, final BinaryOperator<ResultConsumer<CommandSourceStack>> binaryOperator) {
        final ResultConsumer<CommandSourceStack> resultConsumer2 = (ResultConsumer<CommandSourceStack>)binaryOperator.apply(this.consumer, resultConsumer);
        return this.withCallback(resultConsumer2);
    }
    
    public CommandSourceStack withSuppressedOutput() {
        if (this.silent) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, true, this.consumer, this.anchor);
    }
    
    public CommandSourceStack withPermission(final int integer) {
        if (integer == this.permissionLevel) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, integer, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
    }
    
    public CommandSourceStack withMaximumPermission(final int integer) {
        if (integer <= this.permissionLevel) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, integer, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
    }
    
    public CommandSourceStack withAnchor(final EntityAnchorArgument.Anchor a) {
        if (a == this.anchor) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, a);
    }
    
    public CommandSourceStack withLevel(final ServerLevel vk) {
        if (vk == this.level) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, vk, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
    }
    
    public CommandSourceStack facing(final Entity aio, final EntityAnchorArgument.Anchor a) throws CommandSyntaxException {
        return this.facing(a.apply(aio));
    }
    
    public CommandSourceStack facing(final Vec3 csi) throws CommandSyntaxException {
        final Vec3 csi2 = this.anchor.apply(this);
        final double double4 = csi.x - csi2.x;
        final double double5 = csi.y - csi2.y;
        final double double6 = csi.z - csi2.z;
        final double double7 = Mth.sqrt(double4 * double4 + double6 * double6);
        final float float12 = Mth.wrapDegrees((float)(-(Mth.atan2(double5, double7) * 57.2957763671875)));
        final float float13 = Mth.wrapDegrees((float)(Mth.atan2(double6, double4) * 57.2957763671875) - 90.0f);
        return this.withRotation(new Vec2(float12, float13));
    }
    
    public Component getDisplayName() {
        return this.displayName;
    }
    
    public String getTextName() {
        return this.textName;
    }
    
    public boolean hasPermission(final int integer) {
        return this.permissionLevel >= integer;
    }
    
    public Vec3 getPosition() {
        return this.worldPosition;
    }
    
    public ServerLevel getLevel() {
        return this.level;
    }
    
    @Nullable
    public Entity getEntity() {
        return this.entity;
    }
    
    public Entity getEntityOrException() throws CommandSyntaxException {
        if (this.entity == null) {
            throw CommandSourceStack.ERROR_NOT_ENTITY.create();
        }
        return this.entity;
    }
    
    public ServerPlayer getPlayerOrException() throws CommandSyntaxException {
        if (!(this.entity instanceof ServerPlayer)) {
            throw CommandSourceStack.ERROR_NOT_PLAYER.create();
        }
        return (ServerPlayer)this.entity;
    }
    
    public Vec2 getRotation() {
        return this.rotation;
    }
    
    public MinecraftServer getServer() {
        return this.server;
    }
    
    public EntityAnchorArgument.Anchor getAnchor() {
        return this.anchor;
    }
    
    public void sendSuccess(final Component jo, final boolean boolean2) {
        if (this.source.acceptsSuccess() && !this.silent) {
            this.source.sendMessage(jo);
        }
        if (boolean2 && this.source.shouldInformAdmins() && !this.silent) {
            this.broadcastToAdmins(jo);
        }
    }
    
    private void broadcastToAdmins(final Component jo) {
        final Component jo2 = new TranslatableComponent("chat.type.admin", new Object[] { this.getDisplayName(), jo }).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        if (this.server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            for (final ServerPlayer vl5 : this.server.getPlayerList().getPlayers()) {
                if (vl5 != this.source && this.server.getPlayerList().isOp(vl5.getGameProfile())) {
                    vl5.sendMessage(jo2);
                }
            }
        }
        if (this.source != this.server && this.server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
            this.server.sendMessage(jo2);
        }
    }
    
    public void sendFailure(final Component jo) {
        if (this.source.acceptsFailure() && !this.silent) {
            this.source.sendMessage(new TextComponent("").append(jo).withStyle(ChatFormatting.RED));
        }
    }
    
    public void onCommandComplete(final CommandContext<CommandSourceStack> commandContext, final boolean boolean2, final int integer) {
        if (this.consumer != null) {
            this.consumer.onCommandComplete((CommandContext)commandContext, boolean2, integer);
        }
    }
    
    public Collection<String> getOnlinePlayerNames() {
        return (Collection<String>)Lists.newArrayList((Object[])this.server.getPlayerNames());
    }
    
    public Collection<String> getAllTeams() {
        return this.server.getScoreboard().getTeamNames();
    }
    
    public Collection<ResourceLocation> getAvailableSoundEvents() {
        return (Collection<ResourceLocation>)Registry.SOUND_EVENT.keySet();
    }
    
    public Stream<ResourceLocation> getRecipeNames() {
        return this.server.getRecipeManager().getRecipeIds();
    }
    
    public CompletableFuture<Suggestions> customSuggestion(final CommandContext<SharedSuggestionProvider> commandContext, final SuggestionsBuilder suggestionsBuilder) {
        return null;
    }
    
    static {
        ERROR_NOT_PLAYER = new SimpleCommandExceptionType((Message)new TranslatableComponent("permissions.requires.player", new Object[0]));
        ERROR_NOT_ENTITY = new SimpleCommandExceptionType((Message)new TranslatableComponent("permissions.requires.entity", new Object[0]));
    }
}
