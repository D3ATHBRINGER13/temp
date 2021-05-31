package net.minecraft.client.tutorial;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.player.Input;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public class Tutorial {
    private final Minecraft minecraft;
    @Nullable
    private TutorialStepInstance instance;
    
    public Tutorial(final Minecraft cyc) {
        this.minecraft = cyc;
    }
    
    public void onInput(final Input dmn) {
        if (this.instance != null) {
            this.instance.onInput(dmn);
        }
    }
    
    public void onMouse(final double double1, final double double2) {
        if (this.instance != null) {
            this.instance.onMouse(double1, double2);
        }
    }
    
    public void onLookAt(@Nullable final MultiPlayerLevel dkf, @Nullable final HitResult csf) {
        if (this.instance != null && csf != null && dkf != null) {
            this.instance.onLookAt(dkf, csf);
        }
    }
    
    public void onDestroyBlock(final MultiPlayerLevel dkf, final BlockPos ew, final BlockState bvt, final float float4) {
        if (this.instance != null) {
            this.instance.onDestroyBlock(dkf, ew, bvt, float4);
        }
    }
    
    public void onOpenInventory() {
        if (this.instance != null) {
            this.instance.onOpenInventory();
        }
    }
    
    public void onGetItem(final ItemStack bcj) {
        if (this.instance != null) {
            this.instance.onGetItem(bcj);
        }
    }
    
    public void stop() {
        if (this.instance == null) {
            return;
        }
        this.instance.clear();
        this.instance = null;
    }
    
    public void start() {
        if (this.instance != null) {
            this.stop();
        }
        this.instance = this.minecraft.options.tutorialStep.create(this);
    }
    
    public void tick() {
        if (this.instance != null) {
            if (this.minecraft.level != null) {
                this.instance.tick();
            }
            else {
                this.stop();
            }
        }
        else if (this.minecraft.level != null) {
            this.start();
        }
    }
    
    public void setStep(final TutorialSteps ebb) {
        this.minecraft.options.tutorialStep = ebb;
        this.minecraft.options.save();
        if (this.instance != null) {
            this.instance.clear();
            this.instance = ebb.create(this);
        }
    }
    
    public Minecraft getMinecraft() {
        return this.minecraft;
    }
    
    public GameType getGameMode() {
        if (this.minecraft.gameMode == null) {
            return GameType.NOT_SET;
        }
        return this.minecraft.gameMode.getPlayerMode();
    }
    
    public static Component key(final String string) {
        return new KeybindComponent("key." + string).withStyle(ChatFormatting.BOLD);
    }
}
