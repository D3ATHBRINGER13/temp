package net.minecraft.server.level;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.Level;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameType;
import org.apache.logging.log4j.Logger;

public class ServerPlayerGameMode {
    private static final Logger LOGGER;
    public ServerLevel level;
    public ServerPlayer player;
    private GameType gameModeForPlayer;
    private boolean isDestroyingBlock;
    private int destroyProgressStart;
    private BlockPos destroyPos;
    private int gameTicks;
    private boolean hasDelayedDestroy;
    private BlockPos delayedDestroyPos;
    private int delayedTickStart;
    private int lastSentState;
    
    public ServerPlayerGameMode(final ServerLevel vk) {
        this.gameModeForPlayer = GameType.NOT_SET;
        this.destroyPos = BlockPos.ZERO;
        this.delayedDestroyPos = BlockPos.ZERO;
        this.lastSentState = -1;
        this.level = vk;
    }
    
    public void setGameModeForPlayer(final GameType bho) {
        (this.gameModeForPlayer = bho).updatePlayerAbilities(this.player.abilities);
        this.player.onUpdateAbilities();
        this.player.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_GAME_MODE, new ServerPlayer[] { this.player }));
        this.level.updateSleepingPlayerList();
    }
    
    public GameType getGameModeForPlayer() {
        return this.gameModeForPlayer;
    }
    
    public boolean isSurvival() {
        return this.gameModeForPlayer.isSurvival();
    }
    
    public boolean isCreative() {
        return this.gameModeForPlayer.isCreative();
    }
    
    public void updateGameMode(final GameType bho) {
        if (this.gameModeForPlayer == GameType.NOT_SET) {
            this.gameModeForPlayer = bho;
        }
        this.setGameModeForPlayer(this.gameModeForPlayer);
    }
    
    public void tick() {
        ++this.gameTicks;
        if (this.hasDelayedDestroy) {
            final BlockState bvt2 = this.level.getBlockState(this.delayedDestroyPos);
            if (bvt2.isAir()) {
                this.hasDelayedDestroy = false;
            }
            else {
                final float float3 = this.incrementDestroyProgress(bvt2, this.delayedDestroyPos);
                if (float3 >= 1.0f) {
                    this.hasDelayedDestroy = false;
                    this.destroyBlock(this.delayedDestroyPos);
                }
            }
        }
        else if (this.isDestroyingBlock) {
            final BlockState bvt2 = this.level.getBlockState(this.destroyPos);
            if (bvt2.isAir()) {
                this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
                this.lastSentState = -1;
                this.isDestroyingBlock = false;
            }
            else {
                this.incrementDestroyProgress(bvt2, this.destroyPos);
            }
        }
    }
    
    private float incrementDestroyProgress(final BlockState bvt, final BlockPos ew) {
        final int integer4 = this.gameTicks - this.delayedTickStart;
        final float float5 = bvt.getDestroyProgress(this.player, this.player.level, ew) * (integer4 + 1);
        final int integer5 = (int)(float5 * 10.0f);
        if (integer5 != this.lastSentState) {
            this.level.destroyBlockProgress(this.player.getId(), ew, integer5);
            this.lastSentState = integer5;
        }
        return float5;
    }
    
    public void handleBlockBreakAction(final BlockPos ew, final ServerboundPlayerActionPacket.Action a, final Direction fb, final int integer) {
        final double double6 = this.player.x - (ew.getX() + 0.5);
        final double double7 = this.player.y - (ew.getY() + 0.5) + 1.5;
        final double double8 = this.player.z - (ew.getZ() + 0.5);
        final double double9 = double6 * double6 + double7 * double7 + double8 * double8;
        if (double9 > 36.0) {
            this.player.connection.send(new ClientboundBlockBreakAckPacket(ew, this.level.getBlockState(ew), a, false));
            return;
        }
        if (ew.getY() >= integer) {
            this.player.connection.send(new ClientboundBlockBreakAckPacket(ew, this.level.getBlockState(ew), a, false));
            return;
        }
        if (a == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            if (!this.level.mayInteract(this.player, ew)) {
                this.player.connection.send(new ClientboundBlockBreakAckPacket(ew, this.level.getBlockState(ew), a, false));
                return;
            }
            if (this.isCreative()) {
                if (!this.level.extinguishFire(null, ew, fb)) {
                    this.destroyAndAck(ew, a);
                }
                else {
                    this.player.connection.send(new ClientboundBlockBreakAckPacket(ew, this.level.getBlockState(ew), a, true));
                }
                return;
            }
            if (this.player.blockActionRestricted(this.level, ew, this.gameModeForPlayer)) {
                this.player.connection.send(new ClientboundBlockBreakAckPacket(ew, this.level.getBlockState(ew), a, false));
                return;
            }
            this.level.extinguishFire(null, ew, fb);
            this.destroyProgressStart = this.gameTicks;
            float float14 = 1.0f;
            final BlockState bvt15 = this.level.getBlockState(ew);
            if (!bvt15.isAir()) {
                bvt15.attack(this.level, ew, this.player);
                float14 = bvt15.getDestroyProgress(this.player, this.player.level, ew);
            }
            if (!bvt15.isAir() && float14 >= 1.0f) {
                this.destroyAndAck(ew, a);
            }
            else {
                this.isDestroyingBlock = true;
                this.destroyPos = ew;
                final int integer2 = (int)(float14 * 10.0f);
                this.level.destroyBlockProgress(this.player.getId(), ew, integer2);
                this.player.connection.send(new ClientboundBlockBreakAckPacket(ew, this.level.getBlockState(ew), a, true));
                this.lastSentState = integer2;
            }
        }
        else if (a == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
            if (ew.equals(this.destroyPos)) {
                final int integer3 = this.gameTicks - this.destroyProgressStart;
                final BlockState bvt15 = this.level.getBlockState(ew);
                if (!bvt15.isAir()) {
                    final float float15 = bvt15.getDestroyProgress(this.player, this.player.level, ew) * (integer3 + 1);
                    if (float15 >= 0.7f) {
                        this.isDestroyingBlock = false;
                        this.level.destroyBlockProgress(this.player.getId(), ew, -1);
                        this.destroyAndAck(ew, a);
                        return;
                    }
                    if (!this.hasDelayedDestroy) {
                        this.isDestroyingBlock = false;
                        this.hasDelayedDestroy = true;
                        this.delayedDestroyPos = ew;
                        this.delayedTickStart = this.destroyProgressStart;
                    }
                }
            }
            this.player.connection.send(new ClientboundBlockBreakAckPacket(ew, this.level.getBlockState(ew), a, true));
        }
        else if (a == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK) {
            this.isDestroyingBlock = false;
            this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
            this.player.connection.send(new ClientboundBlockBreakAckPacket(ew, this.level.getBlockState(ew), a, true));
        }
    }
    
    public void destroyAndAck(final BlockPos ew, final ServerboundPlayerActionPacket.Action a) {
        if (this.destroyBlock(ew)) {
            this.player.connection.send(new ClientboundBlockBreakAckPacket(ew, this.level.getBlockState(ew), a, true));
        }
        else {
            this.player.connection.send(new ClientboundBlockBreakAckPacket(ew, this.level.getBlockState(ew), a, false));
        }
    }
    
    public boolean destroyBlock(final BlockPos ew) {
        final BlockState bvt3 = this.level.getBlockState(ew);
        if (!this.player.getMainHandItem().getItem().canAttackBlock(bvt3, this.level, ew, this.player)) {
            return false;
        }
        final BlockEntity btw4 = this.level.getBlockEntity(ew);
        final Block bmv5 = bvt3.getBlock();
        if ((bmv5 instanceof CommandBlock || bmv5 instanceof StructureBlock || bmv5 instanceof JigsawBlock) && !this.player.canUseGameMasterBlocks()) {
            this.level.sendBlockUpdated(ew, bvt3, bvt3, 3);
            return false;
        }
        if (this.player.blockActionRestricted(this.level, ew, this.gameModeForPlayer)) {
            return false;
        }
        bmv5.playerWillDestroy(this.level, ew, bvt3, this.player);
        final boolean boolean6 = this.level.removeBlock(ew, false);
        if (boolean6) {
            bmv5.destroy(this.level, ew, bvt3);
        }
        if (this.isCreative()) {
            return true;
        }
        final ItemStack bcj7 = this.player.getMainHandItem();
        final boolean boolean7 = this.player.canDestroy(bvt3);
        bcj7.mineBlock(this.level, bvt3, ew, this.player);
        if (boolean6 && boolean7) {
            final ItemStack bcj8 = bcj7.isEmpty() ? ItemStack.EMPTY : bcj7.copy();
            bmv5.playerDestroy(this.level, this.player, ew, bvt3, btw4, bcj8);
        }
        return true;
    }
    
    public InteractionResult useItem(final Player awg, final Level bhr, final ItemStack bcj, final InteractionHand ahi) {
        if (this.gameModeForPlayer == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        if (awg.getCooldowns().isOnCooldown(bcj.getItem())) {
            return InteractionResult.PASS;
        }
        final int integer6 = bcj.getCount();
        final int integer7 = bcj.getDamageValue();
        final InteractionResultHolder<ItemStack> ahk8 = bcj.use(bhr, awg, ahi);
        final ItemStack bcj2 = ahk8.getObject();
        if (bcj2 == bcj && bcj2.getCount() == integer6 && bcj2.getUseDuration() <= 0 && bcj2.getDamageValue() == integer7) {
            return ahk8.getResult();
        }
        if (ahk8.getResult() == InteractionResult.FAIL && bcj2.getUseDuration() > 0 && !awg.isUsingItem()) {
            return ahk8.getResult();
        }
        awg.setItemInHand(ahi, bcj2);
        if (this.isCreative()) {
            bcj2.setCount(integer6);
            if (bcj2.isDamageableItem()) {
                bcj2.setDamageValue(integer7);
            }
        }
        if (bcj2.isEmpty()) {
            awg.setItemInHand(ahi, ItemStack.EMPTY);
        }
        if (!awg.isUsingItem()) {
            ((ServerPlayer)awg).refreshContainer(awg.inventoryMenu);
        }
        return ahk8.getResult();
    }
    
    public InteractionResult useItemOn(final Player awg, final Level bhr, final ItemStack bcj, final InteractionHand ahi, final BlockHitResult csd) {
        final BlockPos ew7 = csd.getBlockPos();
        final BlockState bvt8 = bhr.getBlockState(ew7);
        if (this.gameModeForPlayer == GameType.SPECTATOR) {
            final MenuProvider ahm9 = bvt8.getMenuProvider(bhr, ew7);
            if (ahm9 != null) {
                awg.openMenu(ahm9);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        else {
            final boolean boolean9 = !awg.getMainHandItem().isEmpty() || !awg.getOffhandItem().isEmpty();
            final boolean boolean10 = awg.isSneaking() && boolean9;
            if (!boolean10 && bvt8.use(bhr, awg, ahi, csd)) {
                return InteractionResult.SUCCESS;
            }
            if (bcj.isEmpty() || awg.getCooldowns().isOnCooldown(bcj.getItem())) {
                return InteractionResult.PASS;
            }
            final UseOnContext bdu11 = new UseOnContext(awg, ahi, csd);
            if (this.isCreative()) {
                final int integer12 = bcj.getCount();
                final InteractionResult ahj13 = bcj.useOn(bdu11);
                bcj.setCount(integer12);
                return ahj13;
            }
            return bcj.useOn(bdu11);
        }
    }
    
    public void setLevel(final ServerLevel vk) {
        this.level = vk;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
