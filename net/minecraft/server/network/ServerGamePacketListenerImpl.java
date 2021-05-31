package net.minecraft.server.network;

import net.minecraft.world.level.block.state.AbstractStateHolder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundContainerAckPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ClientboundContainerAckPacket;
import net.minecraft.world.inventory.Slot;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.CrashReportDetail;
import net.minecraft.CrashReport;
import net.minecraft.world.entity.player.ChatVisiblity;
import javax.annotation.Nullable;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import java.util.Iterator;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.chat.ChatType;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import java.util.Set;
import java.util.Collections;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.GameRules;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.SharedConstants;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MoverType;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Doubles;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.PacketListener;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.Connection;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerGamePacketListenerImpl implements ServerGamePacketListener {
    private static final Logger LOGGER;
    public final Connection connection;
    private final MinecraftServer server;
    public ServerPlayer player;
    private int tickCount;
    private long keepAliveTime;
    private boolean keepAlivePending;
    private long keepAliveChallenge;
    private int chatSpamTickCount;
    private int dropSpamTickCount;
    private final Int2ShortMap expectedAcks;
    private double firstGoodX;
    private double firstGoodY;
    private double firstGoodZ;
    private double lastGoodX;
    private double lastGoodY;
    private double lastGoodZ;
    private Entity lastVehicle;
    private double vehicleFirstGoodX;
    private double vehicleFirstGoodY;
    private double vehicleFirstGoodZ;
    private double vehicleLastGoodX;
    private double vehicleLastGoodY;
    private double vehicleLastGoodZ;
    private Vec3 awaitingPositionFromClient;
    private int awaitingTeleport;
    private int awaitingTeleportTime;
    private boolean clientIsFloating;
    private int aboveGroundTickCount;
    private boolean clientVehicleIsFloating;
    private int aboveGroundVehicleTickCount;
    private int receivedMovePacketCount;
    private int knownMovePacketCount;
    
    public ServerGamePacketListenerImpl(final MinecraftServer minecraftServer, final Connection jc, final ServerPlayer vl) {
        this.expectedAcks = (Int2ShortMap)new Int2ShortOpenHashMap();
        this.server = minecraftServer;
        (this.connection = jc).setListener(this);
        this.player = vl;
        vl.connection = this;
    }
    
    public void tick() {
        this.resetPosition();
        this.player.doTick();
        this.player.absMoveTo(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.yRot, this.player.xRot);
        ++this.tickCount;
        this.knownMovePacketCount = this.receivedMovePacketCount;
        if (this.clientIsFloating) {
            if (++this.aboveGroundTickCount > 80) {
                ServerGamePacketListenerImpl.LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
                this.disconnect(new TranslatableComponent("multiplayer.disconnect.flying", new Object[0]));
                return;
            }
        }
        else {
            this.clientIsFloating = false;
            this.aboveGroundTickCount = 0;
        }
        this.lastVehicle = this.player.getRootVehicle();
        if (this.lastVehicle == this.player || this.lastVehicle.getControllingPassenger() != this.player) {
            this.lastVehicle = null;
            this.clientVehicleIsFloating = false;
            this.aboveGroundVehicleTickCount = 0;
        }
        else {
            this.vehicleFirstGoodX = this.lastVehicle.x;
            this.vehicleFirstGoodY = this.lastVehicle.y;
            this.vehicleFirstGoodZ = this.lastVehicle.z;
            this.vehicleLastGoodX = this.lastVehicle.x;
            this.vehicleLastGoodY = this.lastVehicle.y;
            this.vehicleLastGoodZ = this.lastVehicle.z;
            if (this.clientVehicleIsFloating && this.player.getRootVehicle().getControllingPassenger() == this.player) {
                if (++this.aboveGroundVehicleTickCount > 80) {
                    ServerGamePacketListenerImpl.LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
                    this.disconnect(new TranslatableComponent("multiplayer.disconnect.flying", new Object[0]));
                    return;
                }
            }
            else {
                this.clientVehicleIsFloating = false;
                this.aboveGroundVehicleTickCount = 0;
            }
        }
        this.server.getProfiler().push("keepAlive");
        final long long2 = Util.getMillis();
        if (long2 - this.keepAliveTime >= 15000L) {
            if (this.keepAlivePending) {
                this.disconnect(new TranslatableComponent("disconnect.timeout", new Object[0]));
            }
            else {
                this.keepAlivePending = true;
                this.keepAliveTime = long2;
                this.keepAliveChallenge = long2;
                this.send(new ClientboundKeepAlivePacket(this.keepAliveChallenge));
            }
        }
        this.server.getProfiler().pop();
        if (this.chatSpamTickCount > 0) {
            --this.chatSpamTickCount;
        }
        if (this.dropSpamTickCount > 0) {
            --this.dropSpamTickCount;
        }
        if (this.player.getLastActionTime() > 0L && this.server.getPlayerIdleTimeout() > 0 && Util.getMillis() - this.player.getLastActionTime() > this.server.getPlayerIdleTimeout() * 1000 * 60) {
            this.disconnect(new TranslatableComponent("multiplayer.disconnect.idling", new Object[0]));
        }
    }
    
    public void resetPosition() {
        this.firstGoodX = this.player.x;
        this.firstGoodY = this.player.y;
        this.firstGoodZ = this.player.z;
        this.lastGoodX = this.player.x;
        this.lastGoodY = this.player.y;
        this.lastGoodZ = this.player.z;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    private boolean isSingleplayerOwner() {
        return this.server.isSingleplayerOwner(this.player.getGameProfile());
    }
    
    public void disconnect(final Component jo) {
        this.connection.send(new ClientboundDisconnectPacket(jo), (future -> this.connection.disconnect(jo)));
        this.connection.setReadOnly();
        this.server.executeBlocking(this.connection::handleDisconnection);
    }
    
    public void handlePlayerInput(final ServerboundPlayerInputPacket ou) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)ou, this, this.player.getLevel());
        this.player.setPlayerInput(ou.getXxa(), ou.getZza(), ou.isJumping(), ou.isSneaking());
    }
    
    private static boolean containsInvalidValues(final ServerboundMovePlayerPacket om) {
        return !Doubles.isFinite(om.getX(0.0)) || !Doubles.isFinite(om.getY(0.0)) || !Doubles.isFinite(om.getZ(0.0)) || !Floats.isFinite(om.getXRot(0.0f)) || !Floats.isFinite(om.getYRot(0.0f)) || (Math.abs(om.getX(0.0)) > 3.0E7 || Math.abs(om.getY(0.0)) > 3.0E7 || Math.abs(om.getZ(0.0)) > 3.0E7);
    }
    
    private static boolean containsInvalidValues(final ServerboundMoveVehiclePacket on) {
        return !Doubles.isFinite(on.getX()) || !Doubles.isFinite(on.getY()) || !Doubles.isFinite(on.getZ()) || !Floats.isFinite(on.getXRot()) || !Floats.isFinite(on.getYRot());
    }
    
    public void handleMoveVehicle(final ServerboundMoveVehiclePacket on) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)on, this, this.player.getLevel());
        if (containsInvalidValues(on)) {
            this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_vehicle_movement", new Object[0]));
            return;
        }
        final Entity aio3 = this.player.getRootVehicle();
        if (aio3 != this.player && aio3.getControllingPassenger() == this.player && aio3 == this.lastVehicle) {
            final ServerLevel vk4 = this.player.getLevel();
            final double double5 = aio3.x;
            final double double6 = aio3.y;
            final double double7 = aio3.z;
            final double double8 = on.getX();
            final double double9 = on.getY();
            final double double10 = on.getZ();
            final float float17 = on.getYRot();
            final float float18 = on.getXRot();
            double double11 = double8 - this.vehicleFirstGoodX;
            double double12 = double9 - this.vehicleFirstGoodY;
            double double13 = double10 - this.vehicleFirstGoodZ;
            final double double14 = aio3.getDeltaMovement().lengthSqr();
            double double15 = double11 * double11 + double12 * double12 + double13 * double13;
            if (double15 - double14 > 100.0 && !this.isSingleplayerOwner()) {
                ServerGamePacketListenerImpl.LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", aio3.getName().getString(), this.player.getName().getString(), double11, double12, double13);
                this.connection.send(new ClientboundMoveVehiclePacket(aio3));
                return;
            }
            final boolean boolean29 = vk4.noCollision(aio3, aio3.getBoundingBox().deflate(0.0625));
            double11 = double8 - this.vehicleLastGoodX;
            double12 = double9 - this.vehicleLastGoodY - 1.0E-6;
            double13 = double10 - this.vehicleLastGoodZ;
            aio3.move(MoverType.PLAYER, new Vec3(double11, double12, double13));
            final double double16 = double12;
            double11 = double8 - aio3.x;
            double12 = double9 - aio3.y;
            if (double12 > -0.5 || double12 < 0.5) {
                double12 = 0.0;
            }
            double13 = double10 - aio3.z;
            double15 = double11 * double11 + double12 * double12 + double13 * double13;
            boolean boolean30 = false;
            if (double15 > 0.0625) {
                boolean30 = true;
                ServerGamePacketListenerImpl.LOGGER.warn("{} moved wrongly!", aio3.getName().getString());
            }
            aio3.absMoveTo(double8, double9, double10, float17, float18);
            final boolean boolean31 = vk4.noCollision(aio3, aio3.getBoundingBox().deflate(0.0625));
            if (boolean29 && (boolean30 || !boolean31)) {
                aio3.absMoveTo(double5, double6, double7, float17, float18);
                this.connection.send(new ClientboundMoveVehiclePacket(aio3));
                return;
            }
            this.player.getLevel().getChunkSource().move(this.player);
            this.player.checkMovementStatistics(this.player.x - double5, this.player.y - double6, this.player.z - double7);
            this.clientVehicleIsFloating = (double16 >= -0.03125 && !this.server.isFlightAllowed() && !vk4.containsAnyBlocks(aio3.getBoundingBox().inflate(0.0625).expandTowards(0.0, -0.55, 0.0)));
            this.vehicleLastGoodX = aio3.x;
            this.vehicleLastGoodY = aio3.y;
            this.vehicleLastGoodZ = aio3.z;
        }
    }
    
    public void handleAcceptTeleportPacket(final ServerboundAcceptTeleportationPacket nv) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)nv, this, this.player.getLevel());
        if (nv.getId() == this.awaitingTeleport) {
            this.player.absMoveTo(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.yRot, this.player.xRot);
            this.lastGoodX = this.awaitingPositionFromClient.x;
            this.lastGoodY = this.awaitingPositionFromClient.y;
            this.lastGoodZ = this.awaitingPositionFromClient.z;
            if (this.player.isChangingDimension()) {
                this.player.hasChangedDimension();
            }
            this.awaitingPositionFromClient = null;
        }
    }
    
    public void handleRecipeBookUpdatePacket(final ServerboundRecipeBookUpdatePacket ov) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)ov, this, this.player.getLevel());
        if (ov.getPurpose() == ServerboundRecipeBookUpdatePacket.Purpose.SHOWN) {
            this.server.getRecipeManager().byKey(ov.getRecipe()).ifPresent(this.player.getRecipeBook()::removeHighlight);
        }
        else if (ov.getPurpose() == ServerboundRecipeBookUpdatePacket.Purpose.SETTINGS) {
            this.player.getRecipeBook().setGuiOpen(ov.isGuiOpen());
            this.player.getRecipeBook().setFilteringCraftable(ov.isFilteringCraftable());
            this.player.getRecipeBook().setFurnaceGuiOpen(ov.isFurnaceGuiOpen());
            this.player.getRecipeBook().setFurnaceFilteringCraftable(ov.isFurnaceFilteringCraftable());
            this.player.getRecipeBook().setBlastingFurnaceGuiOpen(ov.isBlastFurnaceGuiOpen());
            this.player.getRecipeBook().setBlastingFurnaceFilteringCraftable(ov.isBlastFurnaceFilteringCraftable());
            this.player.getRecipeBook().setSmokerGuiOpen(ov.isSmokerGuiOpen());
            this.player.getRecipeBook().setSmokerFilteringCraftable(ov.isSmokerFilteringCraftable());
        }
    }
    
    public void handleSeenAdvancements(final ServerboundSeenAdvancementsPacket oy) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)oy, this, this.player.getLevel());
        if (oy.getAction() == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
            final ResourceLocation qv3 = oy.getTab();
            final Advancement q4 = this.server.getAdvancements().getAdvancement(qv3);
            if (q4 != null) {
                this.player.getAdvancements().setSelectedTab(q4);
            }
        }
    }
    
    public void handleCustomCommandSuggestions(final ServerboundCommandSuggestionPacket ob) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)ob, this, this.player.getLevel());
        final StringReader stringReader3 = new StringReader(ob.getCommand());
        if (stringReader3.canRead() && stringReader3.peek() == '/') {
            stringReader3.skip();
        }
        final ParseResults<CommandSourceStack> parseResults4 = (ParseResults<CommandSourceStack>)this.server.getCommands().getDispatcher().parse(stringReader3, this.player.createCommandSourceStack());
        this.server.getCommands().getDispatcher().getCompletionSuggestions((ParseResults)parseResults4).thenAccept(suggestions -> this.connection.send(new ClientboundCommandSuggestionsPacket(ob.getId(), suggestions)));
    }
    
    public void handleSetCommandBlock(final ServerboundSetCommandBlockPacket pc) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)pc, this, this.player.getLevel());
        if (!this.server.isCommandBlockEnabled()) {
            this.player.sendMessage(new TranslatableComponent("advMode.notEnabled", new Object[0]));
            return;
        }
        if (!this.player.canUseGameMasterBlocks()) {
            this.player.sendMessage(new TranslatableComponent("advMode.notAllowed", new Object[0]));
            return;
        }
        BaseCommandBlock bgx3 = null;
        CommandBlockEntity bub4 = null;
        final BlockPos ew5 = pc.getPos();
        final BlockEntity btw6 = this.player.level.getBlockEntity(ew5);
        if (btw6 instanceof CommandBlockEntity) {
            bub4 = (CommandBlockEntity)btw6;
            bgx3 = bub4.getCommandBlock();
        }
        final String string7 = pc.getCommand();
        final boolean boolean8 = pc.isTrackOutput();
        if (bgx3 != null) {
            final Direction fb9 = this.player.level.getBlockState(ew5).<Direction>getValue((Property<Direction>)CommandBlock.FACING);
            switch (pc.getMode()) {
                case SEQUENCE: {
                    final BlockState bvt10 = Blocks.CHAIN_COMMAND_BLOCK.defaultBlockState();
                    this.player.level.setBlock(ew5, (((AbstractStateHolder<O, BlockState>)bvt10).setValue((Property<Comparable>)CommandBlock.FACING, fb9)).<Comparable, Boolean>setValue((Property<Comparable>)CommandBlock.CONDITIONAL, pc.isConditional()), 2);
                    break;
                }
                case AUTO: {
                    final BlockState bvt10 = Blocks.REPEATING_COMMAND_BLOCK.defaultBlockState();
                    this.player.level.setBlock(ew5, (((AbstractStateHolder<O, BlockState>)bvt10).setValue((Property<Comparable>)CommandBlock.FACING, fb9)).<Comparable, Boolean>setValue((Property<Comparable>)CommandBlock.CONDITIONAL, pc.isConditional()), 2);
                    break;
                }
                default: {
                    final BlockState bvt10 = Blocks.COMMAND_BLOCK.defaultBlockState();
                    this.player.level.setBlock(ew5, (((AbstractStateHolder<O, BlockState>)bvt10).setValue((Property<Comparable>)CommandBlock.FACING, fb9)).<Comparable, Boolean>setValue((Property<Comparable>)CommandBlock.CONDITIONAL, pc.isConditional()), 2);
                    break;
                }
            }
            btw6.clearRemoved();
            this.player.level.setBlockEntity(ew5, btw6);
            bgx3.setCommand(string7);
            bgx3.setTrackOutput(boolean8);
            if (!boolean8) {
                bgx3.setLastOutput(null);
            }
            bub4.setAutomatic(pc.isAutomatic());
            bgx3.onUpdated();
            if (!StringUtil.isNullOrEmpty(string7)) {
                this.player.sendMessage(new TranslatableComponent("advMode.setCommand.success", new Object[] { string7 }));
            }
        }
    }
    
    public void handleSetCommandMinecart(final ServerboundSetCommandMinecartPacket pd) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)pd, this, this.player.getLevel());
        if (!this.server.isCommandBlockEnabled()) {
            this.player.sendMessage(new TranslatableComponent("advMode.notEnabled", new Object[0]));
            return;
        }
        if (!this.player.canUseGameMasterBlocks()) {
            this.player.sendMessage(new TranslatableComponent("advMode.notAllowed", new Object[0]));
            return;
        }
        final BaseCommandBlock bgx3 = pd.getCommandBlock(this.player.level);
        if (bgx3 != null) {
            bgx3.setCommand(pd.getCommand());
            bgx3.setTrackOutput(pd.isTrackOutput());
            if (!pd.isTrackOutput()) {
                bgx3.setLastOutput(null);
            }
            bgx3.onUpdated();
            this.player.sendMessage(new TranslatableComponent("advMode.setCommand.success", new Object[] { pd.getCommand() }));
        }
    }
    
    public void handlePickItem(final ServerboundPickItemPacket op) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)op, this, this.player.getLevel());
        this.player.inventory.pickSlot(op.getSlot());
        this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, this.player.inventory.selected, this.player.inventory.getItem(this.player.inventory.selected)));
        this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, op.getSlot(), this.player.inventory.getItem(op.getSlot())));
        this.player.connection.send(new ClientboundSetCarriedItemPacket(this.player.inventory.selected));
    }
    
    public void handleRenameItem(final ServerboundRenameItemPacket ow) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)ow, this, this.player.getLevel());
        if (this.player.containerMenu instanceof AnvilMenu) {
            final AnvilMenu aym3 = (AnvilMenu)this.player.containerMenu;
            final String string4 = SharedConstants.filterText(ow.getName());
            if (string4.length() <= 35) {
                aym3.setItemName(string4);
            }
        }
    }
    
    public void handleSetBeaconPacket(final ServerboundSetBeaconPacket pa) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)pa, this, this.player.getLevel());
        if (this.player.containerMenu instanceof BeaconMenu) {
            ((BeaconMenu)this.player.containerMenu).updateEffects(pa.getPrimary(), pa.getSecondary());
        }
    }
    
    public void handleSetStructureBlock(final ServerboundSetStructureBlockPacket pg) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)pg, this, this.player.getLevel());
        if (!this.player.canUseGameMasterBlocks()) {
            return;
        }
        final BlockPos ew3 = pg.getPos();
        final BlockState bvt4 = this.player.level.getBlockState(ew3);
        final BlockEntity btw5 = this.player.level.getBlockEntity(ew3);
        if (btw5 instanceof StructureBlockEntity) {
            final StructureBlockEntity buw6 = (StructureBlockEntity)btw5;
            buw6.setMode(pg.getMode());
            buw6.setStructureName(pg.getName());
            buw6.setStructurePos(pg.getOffset());
            buw6.setStructureSize(pg.getSize());
            buw6.setMirror(pg.getMirror());
            buw6.setRotation(pg.getRotation());
            buw6.setMetaData(pg.getData());
            buw6.setIgnoreEntities(pg.isIgnoreEntities());
            buw6.setShowAir(pg.isShowAir());
            buw6.setShowBoundingBox(pg.isShowBoundingBox());
            buw6.setIntegrity(pg.getIntegrity());
            buw6.setSeed(pg.getSeed());
            if (buw6.hasStructureName()) {
                final String string7 = buw6.getStructureName();
                if (pg.getUpdateType() == StructureBlockEntity.UpdateType.SAVE_AREA) {
                    if (buw6.saveStructure()) {
                        this.player.displayClientMessage(new TranslatableComponent("structure_block.save_success", new Object[] { string7 }), false);
                    }
                    else {
                        this.player.displayClientMessage(new TranslatableComponent("structure_block.save_failure", new Object[] { string7 }), false);
                    }
                }
                else if (pg.getUpdateType() == StructureBlockEntity.UpdateType.LOAD_AREA) {
                    if (!buw6.isStructureLoadable()) {
                        this.player.displayClientMessage(new TranslatableComponent("structure_block.load_not_found", new Object[] { string7 }), false);
                    }
                    else if (buw6.loadStructure()) {
                        this.player.displayClientMessage(new TranslatableComponent("structure_block.load_success", new Object[] { string7 }), false);
                    }
                    else {
                        this.player.displayClientMessage(new TranslatableComponent("structure_block.load_prepare", new Object[] { string7 }), false);
                    }
                }
                else if (pg.getUpdateType() == StructureBlockEntity.UpdateType.SCAN_AREA) {
                    if (buw6.detectSize()) {
                        this.player.displayClientMessage(new TranslatableComponent("structure_block.size_success", new Object[] { string7 }), false);
                    }
                    else {
                        this.player.displayClientMessage(new TranslatableComponent("structure_block.size_failure", new Object[0]), false);
                    }
                }
            }
            else {
                this.player.displayClientMessage(new TranslatableComponent("structure_block.invalid_structure_name", new Object[] { pg.getName() }), false);
            }
            buw6.setChanged();
            this.player.level.sendBlockUpdated(ew3, bvt4, bvt4, 3);
        }
    }
    
    public void handleSetJigsawBlock(final ServerboundSetJigsawBlockPacket pf) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)pf, this, this.player.getLevel());
        if (!this.player.canUseGameMasterBlocks()) {
            return;
        }
        final BlockPos ew3 = pf.getPos();
        final BlockState bvt4 = this.player.level.getBlockState(ew3);
        final BlockEntity btw5 = this.player.level.getBlockEntity(ew3);
        if (btw5 instanceof JigsawBlockEntity) {
            final JigsawBlockEntity bum6 = (JigsawBlockEntity)btw5;
            bum6.setAttachementType(pf.getAttachementType());
            bum6.setTargetPool(pf.getTargetPool());
            bum6.setFinalState(pf.getFinalState());
            bum6.setChanged();
            this.player.level.sendBlockUpdated(ew3, bvt4, bvt4, 3);
        }
    }
    
    public void handleSelectTrade(final ServerboundSelectTradePacket oz) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)oz, this, this.player.getLevel());
        final int integer3 = oz.getItem();
        final AbstractContainerMenu ayk4 = this.player.containerMenu;
        if (ayk4 instanceof MerchantMenu) {
            final MerchantMenu azn5 = (MerchantMenu)ayk4;
            azn5.setSelectionHint(integer3);
            azn5.tryMoveItems(integer3);
        }
    }
    
    public void handleEditBook(final ServerboundEditBookPacket oh) {
        final ItemStack bcj3 = oh.getBook();
        if (bcj3.isEmpty()) {
            return;
        }
        if (!WritableBookItem.makeSureTagIsValid(bcj3.getTag())) {
            return;
        }
        final ItemStack bcj4 = this.player.getItemInHand(oh.getHand());
        if (bcj3.getItem() == Items.WRITABLE_BOOK && bcj4.getItem() == Items.WRITABLE_BOOK) {
            if (oh.isSigning()) {
                final ItemStack bcj5 = new ItemStack(Items.WRITTEN_BOOK);
                final CompoundTag id6 = bcj4.getTag();
                if (id6 != null) {
                    bcj5.setTag(id6.copy());
                }
                bcj5.addTagElement("author", (Tag)new StringTag(this.player.getName().getString()));
                bcj5.addTagElement("title", (Tag)new StringTag(bcj3.getTag().getString("title")));
                final ListTag ik7 = bcj3.getTag().getList("pages", 8);
                for (int integer8 = 0; integer8 < ik7.size(); ++integer8) {
                    String string9 = ik7.getString(integer8);
                    final Component jo10 = new TextComponent(string9);
                    string9 = Component.Serializer.toJson(jo10);
                    ik7.set(integer8, new StringTag(string9));
                }
                bcj5.addTagElement("pages", (Tag)ik7);
                this.player.setItemInHand(oh.getHand(), bcj5);
            }
            else {
                bcj4.addTagElement("pages", (Tag)bcj3.getTag().getList("pages", 8));
            }
        }
    }
    
    public void handleEntityTagQuery(final ServerboundEntityTagQuery oi) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)oi, this, this.player.getLevel());
        if (!this.player.hasPermissions(2)) {
            return;
        }
        final Entity aio3 = this.player.getLevel().getEntity(oi.getEntityId());
        if (aio3 != null) {
            final CompoundTag id4 = aio3.saveWithoutId(new CompoundTag());
            this.player.connection.send(new ClientboundTagQueryPacket(oi.getTransactionId(), id4));
        }
    }
    
    public void handleBlockEntityTagQuery(final ServerboundBlockEntityTagQuery nw) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)nw, this, this.player.getLevel());
        if (!this.player.hasPermissions(2)) {
            return;
        }
        final BlockEntity btw3 = this.player.getLevel().getBlockEntity(nw.getPos());
        final CompoundTag id4 = (btw3 != null) ? btw3.save(new CompoundTag()) : null;
        this.player.connection.send(new ClientboundTagQueryPacket(nw.getTransactionId(), id4));
    }
    
    public void handleMovePlayer(final ServerboundMovePlayerPacket om) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)om, this, this.player.getLevel());
        if (containsInvalidValues(om)) {
            this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_player_movement", new Object[0]));
            return;
        }
        final ServerLevel vk3 = this.server.getLevel(this.player.dimension);
        if (this.player.wonGame) {
            return;
        }
        if (this.tickCount == 0) {
            this.resetPosition();
        }
        if (this.awaitingPositionFromClient != null) {
            if (this.tickCount - this.awaitingTeleportTime > 20) {
                this.awaitingTeleportTime = this.tickCount;
                this.teleport(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.yRot, this.player.xRot);
            }
            return;
        }
        this.awaitingTeleportTime = this.tickCount;
        if (this.player.isPassenger()) {
            this.player.absMoveTo(this.player.x, this.player.y, this.player.z, om.getYRot(this.player.yRot), om.getXRot(this.player.xRot));
            this.player.getLevel().getChunkSource().move(this.player);
            return;
        }
        final double double4 = this.player.x;
        final double double5 = this.player.y;
        final double double6 = this.player.z;
        final double double7 = this.player.y;
        final double double8 = om.getX(this.player.x);
        final double double9 = om.getY(this.player.y);
        final double double10 = om.getZ(this.player.z);
        final float float18 = om.getYRot(this.player.yRot);
        final float float19 = om.getXRot(this.player.xRot);
        double double11 = double8 - this.firstGoodX;
        double double12 = double9 - this.firstGoodY;
        double double13 = double10 - this.firstGoodZ;
        final double double14 = this.player.getDeltaMovement().lengthSqr();
        double double15 = double11 * double11 + double12 * double12 + double13 * double13;
        if (this.player.isSleeping()) {
            if (double15 > 1.0) {
                this.teleport(this.player.x, this.player.y, this.player.z, om.getYRot(this.player.yRot), om.getXRot(this.player.xRot));
            }
            return;
        }
        ++this.receivedMovePacketCount;
        int integer30 = this.receivedMovePacketCount - this.knownMovePacketCount;
        if (integer30 > 5) {
            ServerGamePacketListenerImpl.LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), integer30);
            integer30 = 1;
        }
        if (!this.player.isChangingDimension()) {
            if (!this.player.getLevel().getGameRules().getBoolean(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isFallFlying()) {
                final float float20 = this.player.isFallFlying() ? 300.0f : 100.0f;
                if (double15 - double14 > float20 * integer30 && !this.isSingleplayerOwner()) {
                    ServerGamePacketListenerImpl.LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), double11, double12, double13);
                    this.teleport(this.player.x, this.player.y, this.player.z, this.player.yRot, this.player.xRot);
                    return;
                }
            }
        }
        final boolean boolean31 = this.isPlayerCollidingWithAnything(vk3);
        double11 = double8 - this.lastGoodX;
        double12 = double9 - this.lastGoodY;
        double13 = double10 - this.lastGoodZ;
        if (this.player.onGround && !om.isOnGround() && double12 > 0.0) {
            this.player.jumpFromGround();
        }
        this.player.move(MoverType.PLAYER, new Vec3(double11, double12, double13));
        this.player.onGround = om.isOnGround();
        final double double16 = double12;
        double11 = double8 - this.player.x;
        double12 = double9 - this.player.y;
        if (double12 > -0.5 || double12 < 0.5) {
            double12 = 0.0;
        }
        double13 = double10 - this.player.z;
        double15 = double11 * double11 + double12 * double12 + double13 * double13;
        boolean boolean32 = false;
        if (!this.player.isChangingDimension() && double15 > 0.0625 && !this.player.isSleeping() && !this.player.gameMode.isCreative() && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
            boolean32 = true;
            ServerGamePacketListenerImpl.LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
        }
        this.player.absMoveTo(double8, double9, double10, float18, float19);
        this.player.checkMovementStatistics(this.player.x - double4, this.player.y - double5, this.player.z - double6);
        if (!this.player.noPhysics && !this.player.isSleeping()) {
            final boolean boolean33 = this.isPlayerCollidingWithAnything(vk3);
            if (boolean31 && (boolean32 || !boolean33)) {
                this.teleport(double4, double5, double6, float18, float19);
                return;
            }
        }
        this.clientIsFloating = (double16 >= -0.03125 && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR && !this.server.isFlightAllowed() && !this.player.abilities.mayfly && !this.player.hasEffect(MobEffects.LEVITATION) && !this.player.isFallFlying() && !vk3.containsAnyBlocks(this.player.getBoundingBox().inflate(0.0625).expandTowards(0.0, -0.55, 0.0)));
        this.player.onGround = om.isOnGround();
        this.player.getLevel().getChunkSource().move(this.player);
        this.player.doCheckFallDamage(this.player.y - double7, om.isOnGround());
        this.lastGoodX = this.player.x;
        this.lastGoodY = this.player.y;
        this.lastGoodZ = this.player.z;
    }
    
    private boolean isPlayerCollidingWithAnything(final LevelReader bhu) {
        return bhu.noCollision(this.player, this.player.getBoundingBox().deflate(9.999999747378752E-6));
    }
    
    public void teleport(final double double1, final double double2, final double double3, final float float4, final float float5) {
        this.teleport(double1, double2, double3, float4, float5, (Set<ClientboundPlayerPositionPacket.RelativeArgument>)Collections.emptySet());
    }
    
    public void teleport(final double double1, final double double2, final double double3, final float float4, final float float5, final Set<ClientboundPlayerPositionPacket.RelativeArgument> set) {
        final double double4 = set.contains(ClientboundPlayerPositionPacket.RelativeArgument.X) ? this.player.x : 0.0;
        final double double5 = set.contains(ClientboundPlayerPositionPacket.RelativeArgument.Y) ? this.player.y : 0.0;
        final double double6 = set.contains(ClientboundPlayerPositionPacket.RelativeArgument.Z) ? this.player.z : 0.0;
        final float float6 = set.contains(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT) ? this.player.yRot : 0.0f;
        final float float7 = set.contains(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT) ? this.player.xRot : 0.0f;
        this.awaitingPositionFromClient = new Vec3(double1, double2, double3);
        if (++this.awaitingTeleport == Integer.MAX_VALUE) {
            this.awaitingTeleport = 0;
        }
        this.awaitingTeleportTime = this.tickCount;
        this.player.absMoveTo(double1, double2, double3, float4, float5);
        this.player.connection.send(new ClientboundPlayerPositionPacket(double1 - double4, double2 - double5, double3 - double6, float4 - float6, float5 - float7, set, this.awaitingTeleport));
    }
    
    public void handlePlayerAction(final ServerboundPlayerActionPacket os) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)os, this, this.player.getLevel());
        final BlockPos ew3 = os.getPos();
        this.player.resetLastActionTime();
        final ServerboundPlayerActionPacket.Action a4 = os.getAction();
        switch (a4) {
            case SWAP_HELD_ITEMS: {
                if (!this.player.isSpectator()) {
                    final ItemStack bcj5 = this.player.getItemInHand(InteractionHand.OFF_HAND);
                    this.player.setItemInHand(InteractionHand.OFF_HAND, this.player.getItemInHand(InteractionHand.MAIN_HAND));
                    this.player.setItemInHand(InteractionHand.MAIN_HAND, bcj5);
                }
            }
            case DROP_ITEM: {
                if (!this.player.isSpectator()) {
                    this.player.drop(false);
                }
            }
            case DROP_ALL_ITEMS: {
                if (!this.player.isSpectator()) {
                    this.player.drop(true);
                }
            }
            case RELEASE_USE_ITEM: {
                this.player.releaseUsingItem();
            }
            case START_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK: {
                this.player.gameMode.handleBlockBreakAction(ew3, a4, os.getDirection(), this.server.getMaxBuildHeight());
            }
            default: {
                throw new IllegalArgumentException("Invalid player action");
            }
        }
    }
    
    public void handleUseItemOn(final ServerboundUseItemOnPacket pk) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)pk, this, this.player.getLevel());
        final ServerLevel vk3 = this.server.getLevel(this.player.dimension);
        final InteractionHand ahi4 = pk.getHand();
        final ItemStack bcj5 = this.player.getItemInHand(ahi4);
        final BlockHitResult csd6 = pk.getHitResult();
        final BlockPos ew7 = csd6.getBlockPos();
        final Direction fb8 = csd6.getDirection();
        this.player.resetLastActionTime();
        if (ew7.getY() < this.server.getMaxBuildHeight() - 1 || (fb8 != Direction.UP && ew7.getY() < this.server.getMaxBuildHeight())) {
            if (this.awaitingPositionFromClient == null && this.player.distanceToSqr(ew7.getX() + 0.5, ew7.getY() + 0.5, ew7.getZ() + 0.5) < 64.0 && vk3.mayInteract(this.player, ew7)) {
                this.player.gameMode.useItemOn(this.player, vk3, bcj5, ahi4, csd6);
            }
        }
        else {
            final Component jo9 = new TranslatableComponent("build.tooHigh", new Object[] { this.server.getMaxBuildHeight() }).withStyle(ChatFormatting.RED);
            this.player.connection.send(new ClientboundChatPacket(jo9, ChatType.GAME_INFO));
        }
        this.player.connection.send(new ClientboundBlockUpdatePacket(vk3, ew7));
        this.player.connection.send(new ClientboundBlockUpdatePacket(vk3, ew7.relative(fb8)));
    }
    
    public void handleUseItem(final ServerboundUseItemPacket pl) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)pl, this, this.player.getLevel());
        final ServerLevel vk3 = this.server.getLevel(this.player.dimension);
        final InteractionHand ahi4 = pl.getHand();
        final ItemStack bcj5 = this.player.getItemInHand(ahi4);
        this.player.resetLastActionTime();
        if (bcj5.isEmpty()) {
            return;
        }
        this.player.gameMode.useItem(this.player, vk3, bcj5, ahi4);
    }
    
    public void handleTeleportToEntityPacket(final ServerboundTeleportToEntityPacket pj) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)pj, this, this.player.getLevel());
        if (this.player.isSpectator()) {
            for (final ServerLevel vk4 : this.server.getAllLevels()) {
                final Entity aio5 = pj.getEntity(vk4);
                if (aio5 != null) {
                    this.player.teleportTo(vk4, aio5.x, aio5.y, aio5.z, aio5.yRot, aio5.xRot);
                }
            }
        }
    }
    
    public void handleResourcePackResponse(final ServerboundResourcePackPacket ox) {
    }
    
    public void handlePaddleBoat(final ServerboundPaddleBoatPacket oo) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)oo, this, this.player.getLevel());
        final Entity aio3 = this.player.getVehicle();
        if (aio3 instanceof Boat) {
            ((Boat)aio3).setPaddleState(oo.getLeft(), oo.getRight());
        }
    }
    
    public void onDisconnect(final Component jo) {
        ServerGamePacketListenerImpl.LOGGER.info("{} lost connection: {}", this.player.getName().getString(), jo.getString());
        this.server.invalidateStatus();
        this.server.getPlayerList().broadcastMessage(new TranslatableComponent("multiplayer.player.left", new Object[] { this.player.getDisplayName() }).withStyle(ChatFormatting.YELLOW));
        this.player.disconnect();
        this.server.getPlayerList().remove(this.player);
        if (this.isSingleplayerOwner()) {
            ServerGamePacketListenerImpl.LOGGER.info("Stopping singleplayer server as player logged out");
            this.server.halt(false);
        }
    }
    
    public void send(final Packet<?> kc) {
        this.send(kc, null);
    }
    
    public void send(final Packet<?> kc, @Nullable final GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
        if (kc instanceof ClientboundChatPacket) {
            final ClientboundChatPacket kv4 = (ClientboundChatPacket)kc;
            final ChatVisiblity awe5 = this.player.getChatVisibility();
            if (awe5 == ChatVisiblity.HIDDEN && kv4.getType() != ChatType.GAME_INFO) {
                return;
            }
            if (awe5 == ChatVisiblity.SYSTEM && !kv4.isSystem()) {
                return;
            }
        }
        try {
            this.connection.send(kc, genericFutureListener);
        }
        catch (Throwable throwable4) {
            final CrashReport d5 = CrashReport.forThrowable(throwable4, "Sending packet");
            final CrashReportCategory e6 = d5.addCategory("Packet being sent");
            e6.setDetail("Packet class", (CrashReportDetail<String>)(() -> kc.getClass().getCanonicalName()));
            throw new ReportedException(d5);
        }
    }
    
    public void handleSetCarriedItem(final ServerboundSetCarriedItemPacket pb) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)pb, this, this.player.getLevel());
        if (pb.getSlot() < 0 || pb.getSlot() >= Inventory.getSelectionSize()) {
            ServerGamePacketListenerImpl.LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
            return;
        }
        this.player.inventory.selected = pb.getSlot();
        this.player.resetLastActionTime();
    }
    
    public void handleChat(final ServerboundChatPacket ny) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)ny, this, this.player.getLevel());
        if (this.player.getChatVisibility() == ChatVisiblity.HIDDEN) {
            this.send(new ClientboundChatPacket(new TranslatableComponent("chat.cannotSend", new Object[0]).withStyle(ChatFormatting.RED)));
            return;
        }
        this.player.resetLastActionTime();
        String string3 = ny.getMessage();
        string3 = StringUtils.normalizeSpace(string3);
        for (int integer4 = 0; integer4 < string3.length(); ++integer4) {
            if (!SharedConstants.isAllowedChatCharacter(string3.charAt(integer4))) {
                this.disconnect(new TranslatableComponent("multiplayer.disconnect.illegal_characters", new Object[0]));
                return;
            }
        }
        if (string3.startsWith("/")) {
            this.handleCommand(string3);
        }
        else {
            final Component jo4 = new TranslatableComponent("chat.type.text", new Object[] { this.player.getDisplayName(), string3 });
            this.server.getPlayerList().broadcastMessage(jo4, false);
        }
        this.chatSpamTickCount += 20;
        if (this.chatSpamTickCount > 200 && !this.server.getPlayerList().isOp(this.player.getGameProfile())) {
            this.disconnect(new TranslatableComponent("disconnect.spam", new Object[0]));
        }
    }
    
    private void handleCommand(final String string) {
        this.server.getCommands().performCommand(this.player.createCommandSourceStack(), string);
    }
    
    public void handleAnimate(final ServerboundSwingPacket pi) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)pi, this, this.player.getLevel());
        this.player.resetLastActionTime();
        this.player.swing(pi.getHand());
    }
    
    public void handlePlayerCommand(final ServerboundPlayerCommandPacket ot) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)ot, this, this.player.getLevel());
        this.player.resetLastActionTime();
        switch (ot.getAction()) {
            case START_SNEAKING: {
                this.player.setSneaking(true);
                break;
            }
            case STOP_SNEAKING: {
                this.player.setSneaking(false);
                break;
            }
            case START_SPRINTING: {
                this.player.setSprinting(true);
                break;
            }
            case STOP_SPRINTING: {
                this.player.setSprinting(false);
                break;
            }
            case STOP_SLEEPING: {
                if (this.player.isSleeping()) {
                    this.player.stopSleepInBed(false, true, true);
                    this.awaitingPositionFromClient = new Vec3(this.player.x, this.player.y, this.player.z);
                    break;
                }
                break;
            }
            case START_RIDING_JUMP: {
                if (this.player.getVehicle() instanceof PlayerRideableJumping) {
                    final PlayerRideableJumping ajg3 = (PlayerRideableJumping)this.player.getVehicle();
                    final int integer4 = ot.getData();
                    if (ajg3.canJump() && integer4 > 0) {
                        ajg3.handleStartJump(integer4);
                    }
                    break;
                }
                break;
            }
            case STOP_RIDING_JUMP: {
                if (this.player.getVehicle() instanceof PlayerRideableJumping) {
                    final PlayerRideableJumping ajg3 = (PlayerRideableJumping)this.player.getVehicle();
                    ajg3.handleStopJump();
                    break;
                }
                break;
            }
            case OPEN_INVENTORY: {
                if (this.player.getVehicle() instanceof AbstractHorse) {
                    ((AbstractHorse)this.player.getVehicle()).openInventory(this.player);
                    break;
                }
                break;
            }
            case START_FALL_FLYING: {
                if (!this.player.onGround && this.player.getDeltaMovement().y < 0.0 && !this.player.isFallFlying() && !this.player.isInWater()) {
                    final ItemStack bcj3 = this.player.getItemBySlot(EquipmentSlot.CHEST);
                    if (bcj3.getItem() == Items.ELYTRA && ElytraItem.isFlyEnabled(bcj3)) {
                        this.player.startFallFlying();
                    }
                    break;
                }
                this.player.stopFallFlying();
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid client command!");
            }
        }
    }
    
    public void handleInteract(final ServerboundInteractPacket oj) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)oj, this, this.player.getLevel());
        final ServerLevel vk3 = this.server.getLevel(this.player.dimension);
        final Entity aio4 = oj.getTarget(vk3);
        this.player.resetLastActionTime();
        if (aio4 != null) {
            final boolean boolean5 = this.player.canSee(aio4);
            double double6 = 36.0;
            if (!boolean5) {
                double6 = 9.0;
            }
            if (this.player.distanceToSqr(aio4) < double6) {
                if (oj.getAction() == ServerboundInteractPacket.Action.INTERACT) {
                    final InteractionHand ahi8 = oj.getHand();
                    this.player.interactOn(aio4, ahi8);
                }
                else if (oj.getAction() == ServerboundInteractPacket.Action.INTERACT_AT) {
                    final InteractionHand ahi8 = oj.getHand();
                    aio4.interactAt(this.player, oj.getLocation(), ahi8);
                }
                else if (oj.getAction() == ServerboundInteractPacket.Action.ATTACK) {
                    if (aio4 instanceof ItemEntity || aio4 instanceof ExperienceOrb || aio4 instanceof AbstractArrow || aio4 == this.player) {
                        this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_entity_attacked", new Object[0]));
                        this.server.warn("Player " + this.player.getName().getString() + " tried to attack an invalid entity");
                        return;
                    }
                    this.player.attack(aio4);
                }
            }
        }
    }
    
    public void handleClientCommand(final ServerboundClientCommandPacket nz) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)nz, this, this.player.getLevel());
        this.player.resetLastActionTime();
        final ServerboundClientCommandPacket.Action a3 = nz.getAction();
        switch (a3) {
            case PERFORM_RESPAWN: {
                if (this.player.wonGame) {
                    this.player.wonGame = false;
                    this.player = this.server.getPlayerList().respawn(this.player, DimensionType.OVERWORLD, true);
                    CriteriaTriggers.CHANGED_DIMENSION.trigger(this.player, DimensionType.THE_END, DimensionType.OVERWORLD);
                    break;
                }
                if (this.player.getHealth() > 0.0f) {
                    return;
                }
                this.player = this.server.getPlayerList().respawn(this.player, DimensionType.OVERWORLD, false);
                if (this.server.isHardcore()) {
                    this.player.setGameMode(GameType.SPECTATOR);
                    this.player.getLevel().getGameRules().<GameRules.BooleanValue>getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(false, this.server);
                    break;
                }
                break;
            }
            case REQUEST_STATS: {
                this.player.getStats().sendStats(this.player);
                break;
            }
        }
    }
    
    public void handleContainerClose(final ServerboundContainerClosePacket of) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)of, this, this.player.getLevel());
        this.player.doCloseContainer();
    }
    
    public void handleContainerClick(final ServerboundContainerClickPacket oe) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)oe, this, this.player.getLevel());
        this.player.resetLastActionTime();
        if (this.player.containerMenu.containerId == oe.getContainerId() && this.player.containerMenu.isSynched(this.player)) {
            if (this.player.isSpectator()) {
                final NonNullList<ItemStack> fk3 = NonNullList.<ItemStack>create();
                for (int integer4 = 0; integer4 < this.player.containerMenu.slots.size(); ++integer4) {
                    fk3.add(((Slot)this.player.containerMenu.slots.get(integer4)).getItem());
                }
                this.player.refreshContainer(this.player.containerMenu, fk3);
            }
            else {
                final ItemStack bcj3 = this.player.containerMenu.clicked(oe.getSlotNum(), oe.getButtonNum(), oe.getClickType(), this.player);
                if (ItemStack.matches(oe.getItem(), bcj3)) {
                    this.player.connection.send(new ClientboundContainerAckPacket(oe.getContainerId(), oe.getUid(), true));
                    this.player.ignoreSlotUpdateHack = true;
                    this.player.containerMenu.broadcastChanges();
                    this.player.broadcastCarriedItem();
                    this.player.ignoreSlotUpdateHack = false;
                }
                else {
                    this.expectedAcks.put(this.player.containerMenu.containerId, oe.getUid());
                    this.player.connection.send(new ClientboundContainerAckPacket(oe.getContainerId(), oe.getUid(), false));
                    this.player.containerMenu.setSynched(this.player, false);
                    final NonNullList<ItemStack> fk4 = NonNullList.<ItemStack>create();
                    for (int integer5 = 0; integer5 < this.player.containerMenu.slots.size(); ++integer5) {
                        final ItemStack bcj4 = ((Slot)this.player.containerMenu.slots.get(integer5)).getItem();
                        fk4.add((bcj4.isEmpty() ? ItemStack.EMPTY : bcj4));
                    }
                    this.player.refreshContainer(this.player.containerMenu, fk4);
                }
            }
        }
    }
    
    public void handlePlaceRecipe(final ServerboundPlaceRecipePacket oq) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)oq, this, this.player.getLevel());
        this.player.resetLastActionTime();
        if (this.player.isSpectator() || this.player.containerMenu.containerId != oq.getContainerId() || !this.player.containerMenu.isSynched(this.player) || !(this.player.containerMenu instanceof RecipeBookMenu)) {
            return;
        }
        this.server.getRecipeManager().byKey(oq.getRecipe()).ifPresent(ber -> ((RecipeBookMenu)this.player.containerMenu).handlePlacement(oq.isShiftDown(), ber, this.player));
    }
    
    public void handleContainerButtonClick(final ServerboundContainerButtonClickPacket od) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)od, this, this.player.getLevel());
        this.player.resetLastActionTime();
        if (this.player.containerMenu.containerId == od.getContainerId() && this.player.containerMenu.isSynched(this.player) && !this.player.isSpectator()) {
            this.player.containerMenu.clickMenuButton(this.player, od.getButtonId());
            this.player.containerMenu.broadcastChanges();
        }
    }
    
    public void handleSetCreativeModeSlot(final ServerboundSetCreativeModeSlotPacket pe) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)pe, this, this.player.getLevel());
        if (this.player.gameMode.isCreative()) {
            final boolean boolean3 = pe.getSlotNum() < 0;
            final ItemStack bcj4 = pe.getItem();
            final CompoundTag id5 = bcj4.getTagElement("BlockEntityTag");
            if (!bcj4.isEmpty() && id5 != null && id5.contains("x") && id5.contains("y") && id5.contains("z")) {
                final BlockPos ew6 = new BlockPos(id5.getInt("x"), id5.getInt("y"), id5.getInt("z"));
                final BlockEntity btw7 = this.player.level.getBlockEntity(ew6);
                if (btw7 != null) {
                    final CompoundTag id6 = btw7.save(new CompoundTag());
                    id6.remove("x");
                    id6.remove("y");
                    id6.remove("z");
                    bcj4.addTagElement("BlockEntityTag", (Tag)id6);
                }
            }
            final boolean boolean4 = pe.getSlotNum() >= 1 && pe.getSlotNum() <= 45;
            final boolean boolean5 = bcj4.isEmpty() || (bcj4.getDamageValue() >= 0 && bcj4.getCount() <= 64 && !bcj4.isEmpty());
            if (boolean4 && boolean5) {
                if (bcj4.isEmpty()) {
                    this.player.inventoryMenu.setItem(pe.getSlotNum(), ItemStack.EMPTY);
                }
                else {
                    this.player.inventoryMenu.setItem(pe.getSlotNum(), bcj4);
                }
                this.player.inventoryMenu.setSynched(this.player, true);
                this.player.inventoryMenu.broadcastChanges();
            }
            else if (boolean3 && boolean5 && this.dropSpamTickCount < 200) {
                this.dropSpamTickCount += 20;
                final ItemEntity atx8 = this.player.drop(bcj4, true);
                if (atx8 != null) {
                    atx8.setShortLifeTime();
                }
            }
        }
    }
    
    public void handleContainerAck(final ServerboundContainerAckPacket oc) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)oc, this, this.player.getLevel());
        final int integer3 = this.player.containerMenu.containerId;
        if (integer3 == oc.getContainerId() && this.expectedAcks.getOrDefault(integer3, (short)(oc.getUid() + 1)) == oc.getUid() && !this.player.containerMenu.isSynched(this.player) && !this.player.isSpectator()) {
            this.player.containerMenu.setSynched(this.player, true);
        }
    }
    
    public void handleSignUpdate(final ServerboundSignUpdatePacket ph) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)ph, this, this.player.getLevel());
        this.player.resetLastActionTime();
        final ServerLevel vk3 = this.server.getLevel(this.player.dimension);
        final BlockPos ew4 = ph.getPos();
        if (vk3.hasChunkAt(ew4)) {
            final BlockState bvt5 = vk3.getBlockState(ew4);
            final BlockEntity btw6 = vk3.getBlockEntity(ew4);
            if (!(btw6 instanceof SignBlockEntity)) {
                return;
            }
            final SignBlockEntity bus7 = (SignBlockEntity)btw6;
            if (!bus7.isEditable() || bus7.getPlayerWhoMayEdit() != this.player) {
                this.server.warn("Player " + this.player.getName().getString() + " just tried to change non-editable sign");
                return;
            }
            final String[] arr8 = ph.getLines();
            for (int integer9 = 0; integer9 < arr8.length; ++integer9) {
                bus7.setMessage(integer9, new TextComponent(ChatFormatting.stripFormatting(arr8[integer9])));
            }
            bus7.setChanged();
            vk3.sendBlockUpdated(ew4, bvt5, bvt5, 3);
        }
    }
    
    public void handleKeepAlive(final ServerboundKeepAlivePacket ok) {
        if (this.keepAlivePending && ok.getId() == this.keepAliveChallenge) {
            final int integer3 = (int)(Util.getMillis() - this.keepAliveTime);
            this.player.latency = (this.player.latency * 3 + integer3) / 4;
            this.keepAlivePending = false;
        }
        else if (!this.isSingleplayerOwner()) {
            this.disconnect(new TranslatableComponent("disconnect.timeout", new Object[0]));
        }
    }
    
    public void handlePlayerAbilities(final ServerboundPlayerAbilitiesPacket or) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)or, this, this.player.getLevel());
        this.player.abilities.flying = (or.isFlying() && this.player.abilities.mayfly);
    }
    
    public void handleClientInformation(final ServerboundClientInformationPacket oa) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)oa, this, this.player.getLevel());
        this.player.updateOptions(oa);
    }
    
    public void handleCustomPayload(final ServerboundCustomPayloadPacket og) {
    }
    
    public void handleChangeDifficulty(final ServerboundChangeDifficultyPacket nx) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)nx, this, this.player.getLevel());
        if (!this.player.hasPermissions(2) && !this.isSingleplayerOwner()) {
            return;
        }
        this.server.setDifficulty(nx.getDifficulty(), false);
    }
    
    public void handleLockDifficulty(final ServerboundLockDifficultyPacket ol) {
        PacketUtils.<ServerGamePacketListenerImpl>ensureRunningOnSameThread((Packet<ServerGamePacketListenerImpl>)ol, this, this.player.getLevel());
        if (!this.player.hasPermissions(2) && !this.isSingleplayerOwner()) {
            return;
        }
        this.server.setDifficultyLocked(ol.isLocked());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
