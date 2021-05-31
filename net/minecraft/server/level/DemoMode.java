package net.minecraft.server.level;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;

public class DemoMode extends ServerPlayerGameMode {
    private boolean displayedIntro;
    private boolean demoHasEnded;
    private int demoEndedReminder;
    private int gameModeTicks;
    
    public DemoMode(final ServerLevel vk) {
        super(vk);
    }
    
    @Override
    public void tick() {
        super.tick();
        ++this.gameModeTicks;
        final long long2 = this.level.getGameTime();
        final long long3 = long2 / 24000L + 1L;
        if (!this.displayedIntro && this.gameModeTicks > 20) {
            this.displayedIntro = true;
            this.player.connection.send(new ClientboundGameEventPacket(5, 0.0f));
        }
        this.demoHasEnded = (long2 > 120500L);
        if (this.demoHasEnded) {
            ++this.demoEndedReminder;
        }
        if (long2 % 24000L == 500L) {
            if (long3 <= 6L) {
                if (long3 == 6L) {
                    this.player.connection.send(new ClientboundGameEventPacket(5, 104.0f));
                }
                else {
                    this.player.sendMessage(new TranslatableComponent(new StringBuilder().append("demo.day.").append(long3).toString(), new Object[0]));
                }
            }
        }
        else if (long3 == 1L) {
            if (long2 == 100L) {
                this.player.connection.send(new ClientboundGameEventPacket(5, 101.0f));
            }
            else if (long2 == 175L) {
                this.player.connection.send(new ClientboundGameEventPacket(5, 102.0f));
            }
            else if (long2 == 250L) {
                this.player.connection.send(new ClientboundGameEventPacket(5, 103.0f));
            }
        }
        else if (long3 == 5L && long2 % 24000L == 22000L) {
            this.player.sendMessage(new TranslatableComponent("demo.day.warning", new Object[0]));
        }
    }
    
    private void outputDemoReminder() {
        if (this.demoEndedReminder > 100) {
            this.player.sendMessage(new TranslatableComponent("demo.reminder", new Object[0]));
            this.demoEndedReminder = 0;
        }
    }
    
    @Override
    public void handleBlockBreakAction(final BlockPos ew, final ServerboundPlayerActionPacket.Action a, final Direction fb, final int integer) {
        if (this.demoHasEnded) {
            this.outputDemoReminder();
            return;
        }
        super.handleBlockBreakAction(ew, a, fb, integer);
    }
    
    @Override
    public InteractionResult useItem(final Player awg, final Level bhr, final ItemStack bcj, final InteractionHand ahi) {
        if (this.demoHasEnded) {
            this.outputDemoReminder();
            return InteractionResult.PASS;
        }
        return super.useItem(awg, bhr, bcj, ahi);
    }
    
    @Override
    public InteractionResult useItemOn(final Player awg, final Level bhr, final ItemStack bcj, final InteractionHand ahi, final BlockHitResult csd) {
        if (this.demoHasEnded) {
            this.outputDemoReminder();
            return InteractionResult.PASS;
        }
        return super.useItemOn(awg, bhr, bcj, ahi, csd);
    }
}
