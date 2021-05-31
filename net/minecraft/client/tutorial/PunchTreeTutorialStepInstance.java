package net.minecraft.client.tutorial;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.GameType;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.network.chat.Component;

public class PunchTreeTutorialStepInstance implements TutorialStepInstance {
    private static final Component TITLE;
    private static final Component DESCRIPTION;
    private final Tutorial tutorial;
    private TutorialToast toast;
    private int timeWaiting;
    private int resetCount;
    
    public PunchTreeTutorialStepInstance(final Tutorial eaz) {
        this.tutorial = eaz;
    }
    
    public void tick() {
        ++this.timeWaiting;
        if (this.tutorial.getGameMode() != GameType.SURVIVAL) {
            this.tutorial.setStep(TutorialSteps.NONE);
            return;
        }
        if (this.timeWaiting == 1) {
            final LocalPlayer dmp2 = this.tutorial.getMinecraft().player;
            if (dmp2 != null) {
                if (dmp2.inventory.contains(ItemTags.LOGS)) {
                    this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                    return;
                }
                if (FindTreeTutorialStepInstance.hasPunchedTreesPreviously(dmp2)) {
                    this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                    return;
                }
            }
        }
        if ((this.timeWaiting >= 600 || this.resetCount > 3) && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, PunchTreeTutorialStepInstance.TITLE, PunchTreeTutorialStepInstance.DESCRIPTION, true);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
        }
    }
    
    public void clear() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }
    
    public void onDestroyBlock(final MultiPlayerLevel dkf, final BlockPos ew, final BlockState bvt, final float float4) {
        final boolean boolean6 = bvt.is(BlockTags.LOGS);
        if (boolean6 && float4 > 0.0f) {
            if (this.toast != null) {
                this.toast.updateProgress(float4);
            }
            if (float4 >= 1.0f) {
                this.tutorial.setStep(TutorialSteps.OPEN_INVENTORY);
            }
        }
        else if (this.toast != null) {
            this.toast.updateProgress(0.0f);
        }
        else if (boolean6) {
            ++this.resetCount;
        }
    }
    
    public void onGetItem(final ItemStack bcj) {
        if (ItemTags.LOGS.contains(bcj.getItem())) {
            this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
        }
    }
    
    static {
        TITLE = new TranslatableComponent("tutorial.punch_tree.title", new Object[0]);
        DESCRIPTION = new TranslatableComponent("tutorial.punch_tree.description", new Object[] { Tutorial.key("attack") });
    }
}
